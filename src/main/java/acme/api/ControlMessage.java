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
package acme.api;

import static java.lang.Integer.parseInt;
import static java.util.Collections.unmodifiableMap;

import java.util.Map;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * 
 * @author Daniel Siviter
 * @since v1.0 [7 Aug 2018]
 *
 */
public class ControlMessage {
	private final Type type;

	private final Map<String, String> params;

	@JsonbCreator
	public ControlMessage(@JsonbProperty("type") Type type, @JsonbProperty("params") Map<String, String> params) {
		this.type = type;
		this.params = unmodifiableMap(params);
	}

	public Type type() {
		return this.type;
	}

	public Map<String, String> params() {
		return params;
	}

	public String param(String name) {
		return params().get(name);
	}

	public int intParam(String name) {
		return parseInt(param(name));
	}


	// --- Inner Classes ---

	/**
	 * 
	 * @author Daniel Siviter
	 * @since v1.0 [7 Aug 2018]
	 *
	 */
	public enum Type {
		START,
		PING_COMPLETE,
		DOWNLOAD_COMPLETE,
		UPLOAD_COMPLETE,
		FINISH
	}
}
