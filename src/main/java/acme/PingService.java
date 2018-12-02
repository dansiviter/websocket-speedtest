/*
 * Copyright 2016-2018 Daniel Siviter
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
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.PongMessage;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import acme.api.Results;

/**
 * 
 * @author Daniel Siviter
 * @since v1.0 [8 Aug 2018]
 *
 */
@Dependent
public class PingService {
	private static final ByteBuffer EMPTY_BUF = ByteBuffer.allocate(0);

	@Inject
	private Logger log;
	@Resource
	private ManagedScheduledExecutorService executor;

	private Basic basic;

	private int warmUpCycles;
	private int cycles;
	private int pingsSent;

	private List<Integer> pingResults = new ArrayList<>();

	private ScheduledFuture<?> future;

	public void start(Session session, int warmUpCycles, int cycles) {
		this.basic = session.getBasicRemote();
		this.warmUpCycles = warmUpCycles;
		this.cycles = cycles;

		this.future = this.executor.scheduleAtFixedRate(this::sendPing, 0, 250, MILLISECONDS);
	}

	private void sendPing() {
		try {
			final ByteBuffer buf = pingsSent < warmUpCycles ? EMPTY_BUF : buf(nanoTime());
			this.basic.sendPing(buf);
			pingsSent++;

			if (pingsSent > warmUpCycles + cycles) {
				this.future.cancel(false);
			}
		} catch (IOException e) {
			this.log.warn("Aggggh!", e);
		}
	}

	private static ByteBuffer buf(Long l) {
		return (ByteBuffer) ByteBuffer.allocate(Long.BYTES).putLong(l).flip();
	}

	public void onPing(PongMessage msg, long nanos) {
		this.pingResults.add((int) (nanos - msg.getApplicationData().getLong()));

		if (this.pingResults.size() == this.cycles) {
			try {
				this.basic.sendObject(new Results(this.pingResults));
			} catch (IOException | EncodeException e) {
				this.log.warn("Aggggh!", e);
			}
		}
	}
}
