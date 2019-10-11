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

import static acme.ReflectionUtil.set;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.websocket.EncodeException;
import javax.websocket.PongMessage;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.google.common.collect.ImmutableMap;

import acme.api.ControlMessage;
import acme.api.ControlMessage.Type;

/**
 * Unit test for {@link PingService}.
 * 
 * @author Daniel Siviter
 * @since v1.0 [2 Dec 2018]
 */
public class PingServiceTest {
	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	private Log log;
	@Mock
	private ManagedScheduledExecutorService executor;
	@Mock
	private Session session;
	@Mock
	private Basic basic;

	@InjectMocks
	private PingService pingService;

	@Before
	public void before() {
		when(this.session.getId()).thenReturn("ABC123");
	}

	@Test
	public void start() throws IllegalArgumentException, IOException {
		when(this.session.getId()).thenReturn("ABC123");
		when(this.session.getBasicRemote()).thenReturn(this.basic);

		ControlMessage controlMsg = new ControlMessage(
				Type.START,
				ImmutableMap.of("delay", 250, "warmUp", 10, "cycles", 10));

		this.pingService.start(session, controlMsg);

		verify(this.session, times(2)).getId();
		verify(this.log).infof("Starting ping... [sessionId=%s,warmupCycles=%d,cycles=%d,delay=%d]", "ABC123", 10, 10, 250);
		verify(this.session).getBasicRemote();
		verify(this.log).debugf("Sending ping... [sessionId=%s,sent=%d]", "ABC123", 0);
		verify(this.basic).sendPing(any());
	}

	@Test
	public void on_pong() {
		final ControlMessage controlMsg = new ControlMessage(
				Type.START,
				ImmutableMap.of("delay", 250, "warmUp", 10, "cycles", 10));
		set(this.pingService, "controlMsg", controlMsg);
		final PongMessage msg = mock(PongMessage.class);

		this.pingService.on(msg, 2L);

		verify(this.session).getId();
		verify(this.log).debugf("Recieved pong... [sessionId=%s]", "ABC123");
		verify(this.executor).schedule(any(Runnable.class), eq(250L), eq(MILLISECONDS));
		verifyNoMoreInteractions(msg);
	}

	@Test
	public void on_pong_complete() throws IOException, EncodeException {
		final ControlMessage controlMsg = new ControlMessage(
				Type.START,
				ImmutableMap.of("delay", 250, "warmUp", 10, "cycles", 10));
		set(this.pingService, "controlMsg", controlMsg);
		set(this.pingService, "pingsSent", 20);
		final PongMessage msg = mock(PongMessage.class);
		when(msg.getApplicationData()).thenReturn((ByteBuffer) ByteBuffer.allocate(8).putLong(1L).flip());

		this.pingService.on(msg, 2L);

		verify(this.session, times(2)).getId();
		verify(this.log).debugf("Recieved pong... [sessionId=%s]", "ABC123");
		verify(msg).getApplicationData();
		verify(this.log).debugf("Completed. [sessionId=%s]", "ABC123");
		verify(this.basic).sendObject(any());
		verifyNoMoreInteractions(msg);
	}

	@After
	public void after() {
		verifyNoMoreInteractions(this.log, this.executor, this.session, this.basic);
	}
}
