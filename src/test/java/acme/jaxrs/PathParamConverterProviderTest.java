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

import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Annotation;
import java.nio.file.Path;

import javax.ws.rs.ext.ParamConverter;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit test for {@link PathParamConverterProvider};
 * 
 * @author Daniel Siviter
 * @since v1.0 [24 Oct 2018]
 */
public class PathParamConverterProviderTest {
	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();

	@InjectMocks
	private PathParamConverterProvider provider;

	@Test
	public void getConverter() {
		final ParamConverter<Path> converter = provider.getConverter(Path.class, Path.class, new Annotation[0]);
		assertNotNull(converter);
	}
}
