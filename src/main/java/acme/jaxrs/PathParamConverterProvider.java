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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

/**
 * Converter for {@link Path}.
 * 
 * @author Daniel Siviter
 * @since v1.0 [22 Oct 2018]
 */
@Provider
public class PathParamConverterProvider implements ParamConverterProvider {

	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if (rawType.equals(Path.class)) {
			return new ParamConverter<T>() {

				@Override
				public T fromString(String value) {
					return (T) Paths.get(value).normalize();
				}

				@Override
				public String toString(T value) {
					if (value == null) {
						return null;
					}
					return value.toString();
				}
			};
		}
		return null;
	}

}
