package org.apache.sis.io.wkt;

import java.text.NumberFormat;


/**
 * Temporary accessor to Apache SIS package-private methods, to be removed after we completed migration to SIS.
 */
public final class Accessor {
    private Accessor() {
    }

    public static NumberFormat createNumberFormat(Symbols symbols) {
        return symbols.createNumberFormat();
    }
}
