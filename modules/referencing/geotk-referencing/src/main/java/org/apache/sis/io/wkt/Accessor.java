package org.apache.sis.io.wkt;

import java.text.NumberFormat;


/**
 * Temporary accessor to Apache SIS package-private methods, to be removed after we completed migration to SIS.
 */
public final class Accessor {
    private Accessor() {
    }

    static {
        Convention.DEFAULT = Convention.WKT1;
        Convention.DEFAULT_SIMPLIFIED = Convention.WKT1;
    }

    public static void init() { // Dummy method for forcing class initialization by the caller.
    }

    public static NumberFormat createNumberFormat(Symbols symbols) {
        return symbols.createNumberFormat();
    }

    public static String formatWKT(FormattableObject object,
            Convention convention, byte indentation, boolean colorize, boolean strict)
    {
        return object.formatWKT(convention, indentation, colorize, strict);
    }
}
