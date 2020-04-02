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
package acme.ws;

import java.io.IOException;
import java.io.Writer;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;

import acme.api.Results;

/**
 *
 * @author Daniel Siviter
 * @since v1.0 [8 Aug 2018]
 *
 */
public class ResultsEncoder extends AbstractJsonbEncoder implements Encoder.TextStream<Results> {
	@Override
	public void encode(Results object, Writer writer) throws EncodeException, IOException {
		this.jsonb.toJson(object, writer);
	}
}
