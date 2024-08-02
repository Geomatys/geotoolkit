package org.geotoolkit.feature.util.converter;

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.temporal.object.TemporalUtilities;

import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Converter from String to Date
 *
 * @author Quentin Bialota (Geomatys)
 * @module
 */
public class StringToLocalDateConverter extends SimpleConverter<String, LocalDate>{
    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<LocalDate> getTargetClass() {
        return LocalDate.class;
    }

    @Override
    public LocalDate apply(final String s) throws UnconvertibleObjectException {
        return TemporalUtilities.parseDateSafe(s,false).toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
    }
}