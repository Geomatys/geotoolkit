package org.apache.sis.io.wkt;

import java.text.NumberFormat;
import org.opengis.referencing.cs.CartesianCS;


/**
 * Temporary accessor to Apache SIS package-private methods, to be removed after we completed migration to SIS.
 */
public final class Accessor {
    private Accessor() {
    }

    static {
        Convention.DEFAULT = Convention.WKT1;
    }

    public static void init() { // Dummy method for forcing class initialization by the caller.
    }

    public static Formatter newFormatter(Symbols symbols, NumberFormat numberFormat) {
        return new Formatter(symbols, numberFormat);
    }

    public static NumberFormat createNumberFormat(Symbols symbols) {
        return symbols.createNumberFormat();
    }

    public static String formatWKT(FormattableObject object,
            Convention convention, byte indentation, boolean colorize, boolean strict)
    {
        return object.formatWKT(convention, indentation, colorize, strict);
    }

    public static CartesianCS replace(final CartesianCS cs, final boolean toLegacy) {
        return Legacy.replace(cs, toLegacy);
    }
}
