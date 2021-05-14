package org.geotoolkit.wps.converters.inputs.literal;

import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.util.converter.SimpleConverter;

public abstract class StringToNumberSequenceConverter<T> extends SimpleConverter<String, T> {

    static final Pattern SEPARATOR_REGEX = Pattern.compile("\\s*[,;\\s]\\s*");

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public T apply(String source) throws UnconvertibleObjectException {
        try {
            return convertSequence(source == null ? Stream.empty() : SEPARATOR_REGEX.splitAsStream(source));
        } catch (NullPointerException | NumberFormatException e) {
            throw new UnconvertibleObjectException("Cannot convert input text to a sequence of numbers: "+truncate(source, 50), e);
        }
    }

    protected abstract T convertSequence(Stream<String> values);

    private static String truncate(String source, int limit) {
        if (source == null) return "null";
        else if (source.trim().isEmpty()) return "[empty string]";
        else if (limit > 0 && source.length() > limit) return source.substring(0, limit)+"...";
        else return source;
    }
}
