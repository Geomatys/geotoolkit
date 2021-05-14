package org.geotoolkit.benchmarks;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.UnconvertibleObjectException;
import org.openjdk.jmh.annotations.*;

/**
 * Checks performance of conversion of a sequence of text-encoded double values into an array of primitives.
 * It's interesting to run it against various JDK versions to profile evolution of different algorithms.
 */
@Fork(value = 2, jvmArgs = {"-server", "-Xmx256m"} )
@Threads(8)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(value = TimeUnit.MILLISECONDS)
public class StringToDoubleArray {

    private static final Pattern SEPARATOR_PATTERN = Pattern.compile("[,;\\s]+");

    /**
     * Generates a list of double values separated with a comma. Used as input for benchmarks.
     */
    @State(Scope.Benchmark)
    public static class InputSequence {

        @Param({"100", "1000", "5000", "10000"})
        public int nbValues;

        public String sequence;

        public InputSequence() {}

        @Setup(Level.Trial)
        public void createSequence() {
            final Random rand = new Random();
            sequence = IntStream.range(0, nbValues)
                    .mapToDouble(idx -> rand.nextDouble())
                    .mapToObj(Double::toString)
                    .collect(Collectors.joining(","));
        }
    }

    @Benchmark
    public void sisWay(InputSequence state) {
        double[] result = CharSequences.parseDoubles(state.sequence, ',');
        if (state.nbValues != result.length) throw new AssertionError("Bad decoding");
    }

    @Benchmark
    public void wpsNoSplit(InputSequence state) {
        double[] result = wpsNoSplit(state.sequence);
        if (state.nbValues != result.length) throw new AssertionError("Bad decoding");
    }

    //@Benchmark
    public void wpsWithSplit(InputSequence state) {
        double[] result = wpsWithSplit(state.sequence);
        if (state.nbValues != result.length) throw new AssertionError("Bad decoding");
    }

    @Benchmark
    public void regexStreaming(InputSequence state) {
        double[] result = SEPARATOR_PATTERN
                .splitAsStream(state.sequence)
                .mapToDouble(StringToDoubleArray::toDoubleOrNaN)
                .toArray();
        if (state.nbValues != result.length) throw new AssertionError("Bad decoding");
    }

    private static double toDoubleOrNaN(String token) {
        return token == null || token.isEmpty() ? Double.NaN : Double.parseDouble(token);
    }

    public static double[] wpsNoSplit(String source) throws UnconvertibleObjectException {

        if (source != null) {
            source = source.trim();
            if (!source.isEmpty()) {

                final List<Number> values = new ArrayList<>();

                final int length = source.length();
                final ParsePosition pp = new ParsePosition(0);
                int idx;
                final NumberFormat format = DecimalFormat.getInstance(Locale.ENGLISH);
                format.setParseIntegerOnly(false);
                for(;;) {
                    Number number = format.parse(source, pp);
                    if (number == null) {
                        throw new UnconvertibleObjectException("Invalid source String : "+source);
                    }
                    values.add(number);

                    idx = pp.getIndex();
                    while (idx != length) {
                        char c = source.charAt(idx);
                        if (c == ',' || c == ' ' || c == '\t' || c == '\n') {
                            idx++;
                        } else {
                            break;
                        }
                    }
                    if (idx == length) break;
                    pp.setIndex(idx);
                }

                if (!values.isEmpty()) {
                    final double[] outArray = new double[values.size()];
                    for (int i = 0; i < outArray.length; i++) {
                        outArray[i] = values.get(i).doubleValue();
                    }
                    return outArray;
                } else {
                    throw new UnconvertibleObjectException("Invalid source String : "+source);
                }
            }
        }

        return new double[0];
    }

    public double[] wpsWithSplit(final String source) throws UnconvertibleObjectException {

        if (source != null && !source.trim().isEmpty()) {

            final List<Double> doubleList = new LinkedList<Double>();
            if (source.contains(",")) {
                final String[] sourceSplit = source.split(",");

                for (final String str : sourceSplit) {
                    try {
                        final Double dbl = Double.valueOf(str.trim());
                        if (dbl != null) {
                            doubleList.add(dbl);
                        }
                    } catch (NumberFormatException ex) {
                        throw new UnconvertibleObjectException(ex.getMessage(), ex);
                    }
                }
            } else {
                try {
                    final Double dbl = Double.valueOf(source.trim());
                    if (dbl != null) {
                        doubleList.add(dbl);
                    }
                } catch (NumberFormatException ex) {
                    throw new UnconvertibleObjectException(ex.getMessage(), ex);
                }
            }

            if (!doubleList.isEmpty()) {
                final double[] outArray = new double[doubleList.size()];
                for (int i = 0; i < doubleList.size(); i++) {
                    outArray[i] = doubleList.get(i);
                }
                return outArray;
            } else {
                throw new UnconvertibleObjectException("Invalid source String : "+source);
            }
        }

        return new double[0];
    }

    public static void main(String... args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}


