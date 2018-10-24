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
package acme.ws;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.EndpointConfig;

/**
 * Base class for JSONB stuff.
 * 
 * @author Daniel Siviter
 * @since v1.0 [24 Oct 2018]
 */
public abstract class AbstractJsonbEncoder {
	protected Jsonb jsonb;

	public void init(EndpointConfig config) {
		this.jsonb = JsonbBuilder.create();
	}

	public void destroy() {
		// nothing to see here!
	}
}
