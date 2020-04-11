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

import static java.util.Collections.emptySet;

import java.util.List;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.glassfish.tyrus.ext.extension.deflate.PerMessageDeflateExtension;

import io.helidon.microprofile.server.RoutingPath;

/**
 *
 * @author Daniel Siviter
 * @since v1.0 [3 Apr 2020]
 */
@RoutingPath("api/v1")
public class ApplicationConfig implements ServerApplicationConfig {
	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
		return Set.of(
			ServerEndpointConfig.Builder.create(WsEndpoint.class, "/ws")
					.subprotocols(List.of("speed-test"))
					.decoders(List.of(ControlMessageEncoding.class, FileEncoding.class))
					.encoders(List.of(ControlMessageEncoding.class, FileEncoding.class, ResultsEncoder.class))
					.extensions(List.of(new PerMessageDeflateExtension()))
					.build()
		);
	}

	@Override
	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
		return emptySet();
	}
}
