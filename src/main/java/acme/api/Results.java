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
package acme.api;

import static java.lang.Math.abs;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Collections.unmodifiableList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.List;

/**
 * Represents the results calculated on the server.
 *
 * @author Daniel Siviter
 * @since v1.0 [8 Aug 2018]
 */
public class Results {
	private static final MathContext MC = new MathContext(2, HALF_UP);

	private final List<Integer> pings;
	private final BigDecimal avgRtt;
	private final BigDecimal jitter;

	public Results(List<Integer> pings) {
		this.pings = unmodifiableList(pings);
		this.avgRtt = mean(pings);
		this.jitter = calcJitter(pings);
	}

	/**
	 * @return ping Round Trip Times (RTT) in nanos.
	 */
	public List<Integer> getPings() {
		return pings;
	}

	/**
	 * @return the average Round Trip Time (RTT) in nanos.
	 */
	public BigDecimal getAvgRtt() {
		return avgRtt;
	}

	/**
	 * @return the calculated network jitter in nanos.
	 */
	public BigDecimal getJitter() {
		return jitter;
	}


	// --- Static Methods ---

	/**
	 *
	 * @param values
	 * @return
	 */
	private static BigDecimal mean(Collection<Integer> values) {
		return BigDecimal.valueOf(values.stream().mapToInt(Integer::intValue).average().getAsDouble()).round(MC);
	}

	/**
	 * Calculate the variation in the latency on a ping/pong flow.
	 *
	 * @param values
	 * @return
	 */
	private static BigDecimal calcJitter(List<Integer> values) {
		if (values.size() <= 1) {
			return BigDecimal.ZERO;
		}
		double jitter = 0;
		for (int i = 1; i < values.size(); i++) {
			jitter = abs(values.get(i - 1) - values.get(i));
		}
		return valueOf(jitter).divide(valueOf(values.size() - 1), MC);
	}
}
