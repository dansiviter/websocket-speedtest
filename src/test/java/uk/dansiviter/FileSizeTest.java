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
package uk.dansiviter;

import static uk.dansiviter.FileSize.BYTE;
import static uk.dansiviter.FileSize.KILOBYTE;
import static uk.dansiviter.FileSize.MEGABYTE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link FileSize}.
 *
 * @author Daniel Siviter
 * @since v1.0 [13 Nov 2018]
 *
 */
public class FileSizeTest {
	@Test
	public void size() {
		assertEquals(1, BYTE.size());
		assertEquals(1_024, KILOBYTE.size());
		assertEquals(1_048_576, MEGABYTE.size());
	}

	@Test
	public void buf_capacity() {
		assertEquals(1, BYTE.buf().capacity());
		assertEquals(1_024, KILOBYTE.buf().capacity());
		assertEquals(1_048_576, MEGABYTE.buf().capacity());
	}
}
