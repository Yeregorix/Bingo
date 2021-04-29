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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
	public static final long SEED = 0;
	public static final int RUNS = 10_000;

	public static void main(String[] args) {
		printResult(20);
		System.out.println("Warm up ...");
		warmupPerformanceRange(1, 1_000);
		System.out.println("Measuring ...");
		outputPerformanceRange(1, 3_000);
		System.out.println("Done.");
	}

	public static void warmupPerformanceRange(int from, int to) {
		try {
			for (int i = from; i <= to; i++) {
				testPerformance(null, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputPerformanceRange(int from, int to) {
		try (BufferedWriter w = Files.newBufferedWriter(Paths.get(
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".csv"))) {
			w.write("size,linearTime,binaryTime\n");
			for (int i = from; i <= to; i++) {
				testPerformance(w, i);
				w.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void testPerformance(Writer w, int size) throws IOException {
		if (w != null)
			w.write(size + ",");
		testPerformance(w, new LinearSearch<>(createElements(size)), ',');
		testPerformance(w, new BinarySearch<>(createElements(size)), '\n');
	}

	private static void testPerformance(Writer w, WeightedList<AtomicInteger> list, char sep) throws IOException {
		Random random = new Random(SEED);

		long t = System.nanoTime();
		for (int i = 0; i < RUNS; i++)
			list.get(random);
		t = System.nanoTime() - t;

		if (w != null) {
			w.write(Long.toString(t));
			w.write(sep);
		}
	}

	public static void printResult(int size) {
		System.out.print("Linear: ");
		printResult(new LinearSearch<>(createElements(size)));
		System.out.print("Binary: ");
		printResult(new BinarySearch<>(createElements(size)));
	}

	private static void printResult(WeightedList<AtomicInteger> list) {
		Random random = new Random(SEED);

		for (int i = 0; i < RUNS; i++)
			list.get(random).value.incrementAndGet();

		list.forEach(e -> System.out.print(e.value.get() + " "));
		System.out.println();
	}

	public static WeightedElement<AtomicInteger>[] createElements(int size) {
		WeightedElement<AtomicInteger>[] elements = new WeightedElement[size];
		for (int i = 0; i < size; i++)
			elements[i] = new WeightedElement<>(new AtomicInteger(), size - i);
		return elements;
	}
}
