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

import java.nio.ByteBuffer;
import java.util.Random;


/**
 * 
 * @author Daniel Siviter
 * @since v1.0 [7 Aug 2018]
 *
 */
public enum FileSize { 
	BYTE(1),
	KILOBYTE(1024),
	MEGABYTE(1024^2);

	public final ByteBuffer buf;

	private FileSize(int size) {
		final Random rand = new Random();
		final ByteBuffer buf = ByteBuffer.allocateDirect(size);
		rand.nextBytes(buf.array());
		this.buf = buf.asReadOnlyBuffer();
	}

	/**
	 * @return read only buffer.
	 */
	public ByteBuffer buf() {
		return this.buf;
	}

	/**
	 * @return the file size this file represents.
	 */
	public int size() {
		return buf().capacity();
	}
}
