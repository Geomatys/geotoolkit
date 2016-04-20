package org.geotoolkit.data.geojson.utils;

import com.fasterxml.jackson.core.JsonLocation;

import java.util.Objects;

/**
 * Lightweight pojo of {@link JsonLocation} without internal source object reference and offset.
 * Because since 2.3.x+ of jackson byteOffset and charOffset values depend of underling
 * source type. (InputStream -> use byteOffset, BufferedReader -> use charOffset)
 *
 * @author Quentin Boileau (Geomatys)
 */
public class LiteJsonLocation {

    private final int lineNr;
    private final int columnNr;

    public LiteJsonLocation(JsonLocation location) {
        this.lineNr = location.getLineNr();
        this.columnNr = location.getColumnNr();
    }

    public int getLineNr() {
        return lineNr;
    }

    public int getColumnNr() {
        return columnNr;
    }

    /**
     * Check if an JsonLocation position (line and column) is before
     * current LiteJsonLocation.
     * @param o JsonLocation
     * @return true if before and false if input JsonLocation is equals or after current LiteJsonLocation
     */
    public boolean isBefore(JsonLocation o) {
        if (o == null) return false;
        LiteJsonLocation that = new LiteJsonLocation(o);

        return lineNr < that.lineNr || (lineNr == that.lineNr  && columnNr < that.columnNr);
    }

    /**
     * Test equality with LiteJsonLocation and JsonLocation input objects
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        // not equals if o is null or not an instance of LiteJsonLocation or JsonLocation
        if (o == null ||
                (!LiteJsonLocation.class.isAssignableFrom(o.getClass()) &&
                        !JsonLocation.class.isAssignableFrom(o.getClass()))) return false;

        LiteJsonLocation that;
        if (JsonLocation.class.isAssignableFrom(o.getClass())) {
            that = new LiteJsonLocation((JsonLocation) o);
        } else {
            that = (LiteJsonLocation) o;
        }

        return lineNr == that.lineNr &&
                columnNr == that.columnNr;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNr, columnNr);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LiteJsonLocation{");
        sb.append("lineNr=").append(lineNr);
        sb.append(", columnNr=").append(columnNr);
        sb.append('}');
        return sb.toString();
    }
}
