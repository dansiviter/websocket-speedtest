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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

/**
 * 
 * @author Daniel Siviter
 * @since v1.0 [6 Aug 2018]
 *
 */
public class LogProducer {
	@Produces @Dependent
	public static Logger log(InjectionPoint ip) {
		final Bean<?> bean = ip.getBean();
		return logger(bean != null ? bean.getBeanClass() : ip.getMember().getDeclaringClass());
	}

	/**
	 * @param cls the class to create the logger for.
	 * @return the logger instance.
	 */
	public static Logger logger(Class<?> cls) {
		return Logger.getLogger(cls);
	}
}
