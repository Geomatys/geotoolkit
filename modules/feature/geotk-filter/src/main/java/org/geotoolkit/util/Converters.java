

package org.geotoolkit.util;

import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;
import org.geotoolkit.util.logging.Logging;

public final class Converters {

    protected static final ConverterRegistry CONVERTERS = ConverterRegistry.system();

    private Converters(){}

    public static <T> T convert(Object candidate, Class<T> target) {
        if(candidate == null) return null;
        return (T) convert(candidate, (Class) candidate.getClass(), target);
    }

    private static <S,T> T convert(S candidate, Class<S> source, Class<T> target) {

        // handle case of source being an instance of target up front
        if (target.isAssignableFrom(source) ) {
            return (T) candidate;
        }

        final ObjectConverter<S,T> converter;
        try {
            converter = CONVERTERS.converter(source, target);
            return converter.convert(candidate);
        } catch (NonconvertibleObjectException ex) {
            Logging.recoverableException(Converters.class, "convert", ex);
            return null;
        }
    }

}
