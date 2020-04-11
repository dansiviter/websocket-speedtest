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
package uk.dansiviter.ws;

import static uk.dansiviter.api.ControlMessage.Type.START;
import static javax.websocket.CloseReason.CloseCodes.CANNOT_ACCEPT;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;
import javax.websocket.CloseReason;
import javax.websocket.PongMessage;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.dansiviter.Log;
import uk.dansiviter.PingService;
import uk.dansiviter.api.ControlMessage;

/**
 * Unit tests for {@link Endpoint}.
 *
 * @author Daniel Siviter
 * @since v1.0 [1 Dec 2018]
 */
@ExtendWith(MockitoExtension.class)
public class EndpointTest {
	@Mock
	private Log log;
	@Mock
	private Provider<PingService> pingServiceProvider;
	@Mock
	private Session session;
	@Mock
	private PingService pingService;

	@InjectMocks
	private Endpoint endpoint;

	@Test
	public void typeAnnotations() {
		final ServerEndpoint serverEndpoint = Endpoint.class.getAnnotation(ServerEndpoint.class);
		assertEquals("/v1", serverEndpoint.value());
		assertArrayEquals(new String[] { "speed-test" }, serverEndpoint.subprotocols());
		assertArrayEquals(new Class[] { ControlMessageEncoding.class, FileEncoding.class }, serverEndpoint.decoders());
		assertArrayEquals(new Class[] { ControlMessageEncoding.class, FileEncoding.class, ResultsEncoder.class }, serverEndpoint.encoders());
	}

	@Test
	public void onOpen() {
		when(this.session.getId()).thenReturn("ABC123");

		this.endpoint.onOpen(this.session);

		verify(this.log).infof("Connection opened. [sessionId=%s]", "ABC123");
		verify(this.session).getId();
	}

	@Test
	public void onControl() {
		when(this.session.getId()).thenReturn("ABC123");
		final Map<String, Object> params = new HashMap<>();
		params.put("warmUp", 1);
		params.put("cycles", 2);
		final ControlMessage msg = new ControlMessage(START, params);

		when(session.getUserProperties()).thenReturn(new HashMap<String, Object>());
		when(this.pingServiceProvider.get()).thenReturn(this.pingService);

		this.endpoint.on(session, msg);

		verify(this.session).getId();
		verify(this.log).infof("Control received. [sessionId=%s,type=%s]", "ABC123", START);
		verify(session, times(2)).getUserProperties();
		verify(this.pingServiceProvider).get();
		verify(this.pingService).start(this.session, msg);
	}

	@Test
	public void onBinary() {
		when(this.session.getId()).thenReturn("ABC123");
		final byte[] payload = new byte[0];
		this.endpoint.on(session, payload, true);

		verify(this.session).getId();
		verify(this.log).infof("Binary received. [sessionId=%s,msg=%s,last=%s]", "ABC123", payload, true);
	}

	@Test
	public void onPing() {
		when(this.session.getId()).thenReturn("ABC123");
		final PongMessage msg = mock(PongMessage.class);
		final ByteBuffer buf = ByteBuffer.wrap(new byte[0]);
		when(msg.getApplicationData()).thenReturn(buf);
		final Map<String, Object> map = new HashMap<>();
		map.put("service", this.pingService);
		when(session.getUserProperties()).thenReturn(map);

		this.endpoint.on(this.session, msg);

		verify(this.session).getId();
		verify(this.log).debugf("Pong received. [sessionId=%s,data=%s]", "ABC123", buf);
		verify(msg).getApplicationData();
		verify(this.session).getUserProperties();
		verify(this.pingService).on(eq(msg), anyLong());
		verifyNoMoreInteractions(msg);
	}

	@Test
	public void onError() throws IOException {
		when(this.session.getId()).thenReturn("ABC123");
		final PongMessage msg = mock(PongMessage.class);
		final Throwable t = new IllegalStateException("Agggghhhh!");
		final Basic basic = mock(Basic.class);
		when(this.session.isOpen()).thenReturn(true);
		when(this.session.getBasicRemote()).thenReturn(basic);

		this.endpoint.onError(session, t);

		verify(this.session).getId();
		verify(this.log).warnf(t, "Error! [sessionId=%s,msg=%s]", "ABC123", t.getMessage());
		verify(this.session).isOpen();
		verify(this.session).getBasicRemote();
		verify(basic).sendText("ERROR: " + t.getMessage());
		verifyNoMoreInteractions(msg, basic);
	}

	@Test
	public void onClose() {
		when(this.session.getId()).thenReturn("ABC123");

		this.endpoint.onClose(this.session, new CloseReason(CANNOT_ACCEPT, null));

		verify(this.session).getId();
		verify(this.log).infof("Connection closed. [sessionId=%s,reasonCode=%s]", "ABC123", CANNOT_ACCEPT);
	}

	@AfterEach
	public void after() {
		verifyNoMoreInteractions(this.log, this.pingServiceProvider, this.session, this.pingService);
	}
}
