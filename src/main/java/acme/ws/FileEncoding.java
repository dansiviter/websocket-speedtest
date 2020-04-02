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

import java.nio.ByteBuffer;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import acme.FileSize;

/**
 *
 * @author Daniel Siviter
 * @since v1.0 [7 Aug 2018]
 *
 */
public class FileEncoding implements Encoder.Binary<FileSize>, Decoder.Binary<FileSize> {

	@Override
	public void init(EndpointConfig config) {
		// nothing to see here!
	}

	@Override
	public ByteBuffer encode(FileSize object) throws EncodeException {
		return object.buf().asReadOnlyBuffer();
	}

	@Override
	public FileSize decode(ByteBuffer bytes) throws DecodeException {
		for (FileSize fileSize : FileSize.values()) {
			if (bytes.capacity() == fileSize.size()) {
				return fileSize;
			}
		}
		throw new DecodeException(bytes, "Unknown file size!");
	}

	@Override
	public boolean willDecode(ByteBuffer bytes) {
		for (FileSize fileSize : FileSize.values()) {
			if (bytes.capacity() == fileSize.size()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		// nothing to see here!
	}
}
