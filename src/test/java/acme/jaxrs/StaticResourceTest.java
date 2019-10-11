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
package acme.jaxrs;

import static javax.ws.rs.core.Response.Status.OK;
import static org.jboss.resteasy.mock.MockDispatcherFactory.createDispatcher;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import javax.servlet.ServletContext;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import acme.Log;

/**
 * Unit test for {@link StaticResource}.
 * 
 * @author Daniel Siviter
 * @since v1.0 [2 Dec 2018]
 */
public class StaticResourceTest {
	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	private Log log;
	@Mock
	private ServletContext servletCtx;

	@InjectMocks
	private StaticResource staticResource;

	private Dispatcher dispatcher;

	@Before
	public void before() {
		this.dispatcher = createDispatcher();
		ResteasyProviderFactory.pushContext(ServletContext.class, this.servletCtx);
		this.dispatcher.getProviderFactory().register(new PathParamConverterProvider());
		this.dispatcher.getRegistry().addSingletonResource(this.staticResource);
	}

	@Test
	public void get() throws URISyntaxException, UnsupportedEncodingException {
		final InputStream is = new ByteArrayInputStream("Hello world!".getBytes());
		when(this.servletCtx.getResourceAsStream(any())).thenReturn(is);
		when(this.servletCtx.getMimeType(any())).thenReturn("text/html");

		MockHttpRequest request = MockHttpRequest.get("/");
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		assertEquals(OK.getStatusCode(), response.getStatus());
		assertEquals("Hello world!", response.getContentAsString());

		verify(this.log).staticResource(Paths.get("index.html"));
		verify(this.servletCtx).getResourceAsStream("index.html");
		verify(this.servletCtx).getMimeType("index.html");
	}

	@Test
	public void get_path() throws URISyntaxException, UnsupportedEncodingException {
		final InputStream is = new ByteArrayInputStream("Hello world!".getBytes());
		when(this.servletCtx.getResourceAsStream(any())).thenReturn(is);
		when(this.servletCtx.getMimeType(any())).thenReturn("text/html");

		MockHttpRequest request = MockHttpRequest.get("foo.html");
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		assertEquals(OK.getStatusCode(), response.getStatus());
		assertEquals("Hello world!", response.getContentAsString());

		verify(this.log).staticResource(Paths.get("foo.html"));
		verify(this.servletCtx).getResourceAsStream("foo.html");
		verify(this.servletCtx).getMimeType("foo.html");
	}

	@After
	public void after() {
		verifyNoMoreInteractions(this.log, this.servletCtx);
	}
}
