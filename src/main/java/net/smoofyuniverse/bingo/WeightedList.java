/*
 * Copyright (c) 2021 Hugo Dupanloup (Yeregorix)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.smoofyuniverse.bingo;

import java.util.*;
import java.util.function.Consumer;

public abstract class WeightedList<T> {
	protected final WeightedElement<T>[] elements;
	protected final double[] cumulativeWeights;

	WeightedList(WeightedElement<T>[] elements) {
		this.elements = elements;
		this.cumulativeWeights = new double[elements.length];

		double sum = 0;
		for (int i = 0; i < elements.length; i++) {
			sum += elements[i].weight;
			this.cumulativeWeights[i] = sum;
		}
	}

	public int size() {
		return this.elements.length;
	}

	public void forEach(Consumer<WeightedElement<T>> consumer) {
		for (WeightedElement<T> e : this.elements)
			consumer.accept(e);
	}

	public abstract WeightedElement<T> get(Random random);

	public static <T> WeightedList<T> of(Map<T, Double> map) {
		if (map.isEmpty())
			throw new IllegalArgumentException("empty map");

		List<WeightedElement<T>> list = new ArrayList<>();
		for (Map.Entry<T, Double> e : map.entrySet())
			list.add(new WeightedElement<>(e.getKey(), e.getValue()));

		return of(list);
	}

	public static <T> WeightedList<T> of(Collection<WeightedElement<T>> col) {
		if (col.isEmpty())
			throw new IllegalArgumentException("empty collection");

		WeightedElement<T>[] elements = col.toArray(new WeightedElement[0]);
		Arrays.sort(elements);

		if (elements.length > 300)
			return new BinarySearch<>(elements);

		return new LinearSearch<>(elements);
	}

	public static class Builder<T> {
		private final List<WeightedElement<T>> list = new ArrayList<>();

		private Builder() {}

		public Builder<T> reset() {
			this.list.clear();
			return this;
		}

		public Builder<T> add(T value, double weight) {
			this.list.add(new WeightedElement<>(value, weight));
			return this;
		}

		public Builder<T> add(WeightedElement<T> element) {
			this.list.add(element);
			return this;
		}

		public WeightedList<T> build() {
			return of(this.list);
		}
	}
}
