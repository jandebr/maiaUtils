package org.maia.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Randomizer {

	private Random randomNumberGenerator;

	public Randomizer() {
		this(new Random());
	}

	public Randomizer(Object seed) {
		this(seed.hashCode());
	}

	public Randomizer(long seed) {
		this(new Random(seed));
	}

	private Randomizer(Random randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}

	public boolean drawBoolean() {
		return getRandomNumberGenerator().nextBoolean();
	}

	public int drawIntegerNumber(int minInclusive, int maxInclusive) {
		float r = getRandomNumberGenerator().nextFloat(); // 0 <= r < 1
		return minInclusive + (int) Math.floor(r * (maxInclusive - minInclusive + 1));
	}

	public float drawFloatUnitNumber() {
		float r = getRandomNumberGenerator().nextFloat();
		if (drawBoolean()) {
			return r;
		} else {
			return 1f - r;
		}
	}

	public double drawDoubleUnitNumber() {
		double r = getRandomNumberGenerator().nextDouble();
		if (drawBoolean()) {
			return r;
		} else {
			return 1.0 - r;
		}
	}

	public void shuffle(List<?> list) {
		Collections.shuffle(list, getRandomNumberGenerator());
	}

	private Random getRandomNumberGenerator() {
		return randomNumberGenerator;
	}

}