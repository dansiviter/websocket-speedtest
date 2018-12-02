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

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Collections.unmodifiableList;

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
	private final long avgRtt;
	private final long jitter;

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
	public long getAvgRtt() {
		return avgRtt;
	}

	/**
	 * @return the calculated network jitter in nanos.
	 */
	public long getJitter() {
		return jitter;
	}


	// --- Static Methods ---

	/**
	 * 
	 * @param values
	 * @return
	 */
	private static long mean(Collection<Integer> values) {
		return round(values.stream().mapToInt(Integer::intValue).average().getAsDouble());
	}

	/**
	 * Calculate the variation in the latency on a ping/pong flow.
	 * 
	 * @param values
	 * @return
	 */
	private static long calcJitter(List<Integer> values) {
		if (values.size() <= 1) {
			return 0L;
		}
		double jitter = 0;
		for (int i = 1; i < values.size(); i++) {
			jitter = abs(values.get(i - 1) - values.get(i));
		}
		return valueOf(jitter).divide(valueOf(values.size() - 1), MC).longValue();
	}
}
