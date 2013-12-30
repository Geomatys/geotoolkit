package org.apache.sis.io.wkt;

import java.text.NumberFormat;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.cs.CartesianCS;
import javax.measure.quantity.Angle;
import javax.measure.unit.Unit;

import static org.geotoolkit.referencing.cs.DefaultCartesianCS.GEOCENTRIC;


/**
 * Temporary accessor to Apache SIS package-private methods, to be removed after we completed migration to SIS.
 */
public final class Accessor {
    private Accessor() {
    }

    static {
        Legacy.STANDARD = GEOCENTRIC;
    }

    public static void init() { // Dummy method for forcing class initialization by the caller.
    }

    public static Formatter newFormatter(Symbols symbols, NumberFormat numberFormat) {
        return new Formatter(symbols, numberFormat);
    }

    public static void setConvention(Formatter formatter, Convention convention, Citation authority) {
        formatter.setConvention(convention, authority);
    }

    public static Unit<Angle> forcedAngularUnit(Convention convention) {
        return convention.forcedAngularUnit;
    }

    public static int indentation(Formatter formatter) {
        return formatter.indentation;
    }

    public static void indentation(Formatter formatter, byte indent) {
        formatter.indentation = indent;
    }

    public static Colors colors(Formatter formatter) {
        return formatter.colors;
    }

    public static void colors(Formatter formatter, Colors colors) {
        formatter.colors = colors;
    }

    public static NumberFormat createNumberFormat(Symbols symbols) {
        return symbols.createNumberFormat();
    }

    public static void buffer(Formatter formatter, StringBuffer buffer) {
        formatter.buffer = buffer;
    }

    public static void bufferBase(Formatter formatter, int bufferBase) {
        formatter.bufferBase = bufferBase;
    }

    public static String getErrorMessage(Formatter formatter) {
        return formatter.getErrorMessage();
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
