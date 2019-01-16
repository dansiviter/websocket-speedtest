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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledFuture;

import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.websocket.EncodeException;
import javax.websocket.PongMessage;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

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
	private Logger log;
	@Mock
	private ManagedScheduledExecutorService executor;
	@Mock
	private Session session;
	@Mock
	private Basic basic;

	@InjectMocks
	private PingService pingService;

	@Test
	public void start() throws IllegalArgumentException, IOException {
		when(this.session.getBasicRemote()).thenReturn(this.basic);
		when(this.executor.scheduleAtFixedRate(any(), anyLong(), anyLong(), any())).thenAnswer(new Answer<ScheduledFuture<?>>() {
			@Override
			public ScheduledFuture<?> answer(InvocationOnMock invoc) throws Throwable {
				final Runnable run = invoc.getArgument(0);
				run.run();
				return null;
			}
		});

		this.pingService.start(session, 1, 2);

		verify(this.session).getBasicRemote();
		verify(this.executor).scheduleAtFixedRate(any(), eq(0L), eq(250L), eq(MILLISECONDS));
		verify(this.basic).sendPing(any());
	}

	@Test
	public void onPing() {
		final PongMessage msg = mock(PongMessage.class);
		when(msg.getApplicationData()).thenReturn((ByteBuffer) ByteBuffer.allocate(8).putLong(1L).flip());

		this.pingService.onPing(msg, 2L);

		verify(msg).getApplicationData();
		verifyNoMoreInteractions(msg);
	}

	@Test
	public void onPing_complete() throws IOException, EncodeException {
		set(this.pingService, "cycles", 1);
		final PongMessage msg = mock(PongMessage.class);
		when(msg.getApplicationData()).thenReturn((ByteBuffer) ByteBuffer.allocate(8).putLong(1L).flip());

		this.pingService.onPing(msg, 2L);

		verify(msg).getApplicationData();
		verify(this.basic).sendObject(any());
		verifyNoMoreInteractions(msg);
	}

	@After
	public void after() {
		verifyNoMoreInteractions(this.log, this.executor, this.session, this.basic);
	}
}