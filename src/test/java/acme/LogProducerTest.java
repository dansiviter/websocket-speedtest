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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Member;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit test for {@link LogProducer}.
 * 
 * @author Daniel Siviter
 * @since v1.0 [1 Dec 2018]
 */
public class LogProducerTest {
	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	private InjectionPoint ip;
	@Mock
	@SuppressWarnings("rawtypes")
	private Bean bean;
	@Mock
	private Member member;

	@Test
	public void logger() {
		final Log log = LogProducer.log(LogProducerTest.class);
		assertNotNull(log);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void log() {
		when(this.ip.getBean()).thenReturn(this.bean);
		when(this.bean.getBeanClass()).thenReturn(LogProducerTest.class);

		final Log log = LogProducer.log(this.ip);
		assertNotNull(log);

		verify(this.ip).getBean();
		verify(this.bean).getBeanClass();
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void log_member() {
		when(this.ip.getMember()).thenReturn(this.member);
		when(this.member.getDeclaringClass()).thenReturn((Class) LogProducerTest.class);

		final Log log = LogProducer.log(this.ip);
		assertNotNull(log);

		verify(this.ip).getBean();
		verify(this.ip).getMember();
		verify(this.member).getDeclaringClass();
	}

	@After
	public void after() {
		verifyNoMoreInteractions(this.ip, this.bean, this.member);
	}
}
