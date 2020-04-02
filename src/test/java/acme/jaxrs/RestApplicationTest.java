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
package acme.jaxrs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.ws.rs.ApplicationPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link RestApplication}.
 *
 * @author Daniel Siviter
 * @since v1.0 [13 Nov 2018]
 */
public class RestApplicationTest {
	private RestApplication restApplication;

	@BeforeEach
	public void before() {
		this.restApplication = new RestApplication();
	}

	@Test
	public void applicationPath() {
		final ApplicationPath applicationPath = RestApplication.class.getAnnotation(ApplicationPath.class);
		assertEquals("", applicationPath.value());
	}

	@Test
	public void getSingletons() {
		assertTrue(this.restApplication.getSingletons().isEmpty());
	}

	@Test
	public void getClasses() {
		assertTrue(this.restApplication.getClasses().isEmpty());
	}

	@Test
	public void getProperties() {
		assertTrue(this.restApplication.getProperties().isEmpty());
	}
}
