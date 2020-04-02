/*
 * Copyright 2018-2020 Daniel Siviter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package acme;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.PongMessage;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import acme.api.ControlMessage;
import acme.api.Results;

/**
 *
 * @author Daniel Siviter
 * @since v1.0 [8 Aug 2018]
 *
 */
@Dependent
public class PingService {
	@Inject
	private Log log;
	@Inject
	private ScheduledExecutorService executor;

	private final List<Integer> pingResults = new ArrayList<>();

	private ControlMessage controlMsg;
	private Session session;
	private Basic basic;
	private int pingsSent;

	/**
	 *
	 * @param session
	 * @param warmUpCycles
	 * @param cycles
	 */
	public void start(Session session, ControlMessage controlMsg) {
		log.infof("Starting ping... [sessionId=%s,warmupCycles=%d,cycles=%d,delay=%d]",
			session.getId(),
			Prop.WARM_UP_CYCLES.get(controlMsg).intValue(),
			Prop.CYCLES.get(controlMsg).intValue(),
			Prop.DELAY.get(controlMsg).intValue());

		this.controlMsg = controlMsg;

		this.session = session;
		this.basic = session.getBasicRemote();

		sendPing();
	}

	/**
	 *
	 */
	private void sendPing() {
		log.debugf("Sending ping... [sessionId=%s,sent=%d]", this.session.getId(), this.pingsSent);
		try {
			this.basic.sendPing(buf(nanoTime()));
			pingsSent++;
		} catch (IOException e) {
			this.log.warn("Aggggh!", e);
		}
	}

	/**
	 *
	 * @param msg
	 * @param nanos
	 */
	public void on(PongMessage msg, long nanos) {
		this.log.debugf("Recieved pong... [sessionId=%s]", this.session.getId());

		if (!isWarmUp(this.controlMsg, pingsSent)) {
			final long startNanos = msg.getApplicationData().getLong();
			this.pingResults.add((int) (nanos - startNanos));
		}

		if (isDone(this.controlMsg, this.pingsSent)) {
			this.log.debugf("Completed. [sessionId=%s]", this.session.getId());
			try {
				this.basic.sendObject(new Results(this.pingResults));
			} catch (IOException | EncodeException e) {
				this.log.warn("Aggggh!", e);
			}
		} else {
			this.executor.schedule(this::sendPing, Prop.DELAY.getInt(this.controlMsg), MILLISECONDS);
		}
	}

	/**
	 *
	 * @param l
	 * @return
	 */
	private static ByteBuffer buf(Long l) {
		return ByteBuffer.allocate(Long.BYTES).putLong(l).rewind();
	}

	/**
	 *
	 * @param session
	 * @param pingsSent
	 * @return
	 */
	private static boolean isWarmUp(ControlMessage msg, int pingsSent) {
		final int warmUpCycles = Prop.WARM_UP_CYCLES.getInt(msg);
		return pingsSent <= warmUpCycles;
	}

	/**
	 *
	 * @param session
	 * @param pingsSent
	 * @return
	 */
	private static boolean isDone(ControlMessage msg, int pingsSent) {
		final int warmUpCycles = Prop.WARM_UP_CYCLES.getInt(msg);
		final int cycles = Prop.CYCLES.getInt(msg);
		return pingsSent >= warmUpCycles + cycles;
	}

	/**
	 *
	 */
	private enum Prop {
		WARM_UP_CYCLES("warmUp"),
		CYCLES("cycles"),
		DELAY("delay");

		private final String param;

		Prop(String param) {
			this.param = param;
		}

		Number get(ControlMessage msg) {
			return msg.numParam(this.param);
		}

		int getInt(ControlMessage msg) {
			return get(msg).intValue();
		}
	}
}
