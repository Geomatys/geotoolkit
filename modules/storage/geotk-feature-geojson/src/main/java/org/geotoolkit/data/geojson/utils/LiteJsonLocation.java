package org.geotoolkit.data.geojson.utils;

import com.fasterxml.jackson.core.JsonLocation;

/**
 * Lightweight pojo of {@link JsonLocation} without internal source object reference.
 * Because since 2.3.x+ of jackson byteOffset and charOffset values depend of underling
 * source type. (InputStream -> use byteOffset, BufferedReader -> use charOffset)
 *
 * @author Quentin Boileau (Geomatys)
 */
public class LiteJsonLocation {

    private final int lineNr;
    private final int columnNr;
    private final long offset;

    public LiteJsonLocation(JsonLocation location) {
        this.lineNr = location.getLineNr();
        this.columnNr = location.getColumnNr();
        this.offset = location.getByteOffset() > 0 ? location.getByteOffset() : location.getCharOffset();
    }

    public int getLineNr() {
        return lineNr;
    }

    public int getColumnNr() {
        return columnNr;
    }

    public long getOffset() {
        return offset;
    }

    public boolean equals(JsonLocation o) {
        if (o == null) return  false;
        LiteJsonLocation that = new LiteJsonLocation(o);

        if (lineNr != that.lineNr) return false;
        if (columnNr != that.columnNr) return false;
        return offset == that.offset;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiteJsonLocation that = (LiteJsonLocation) o;

        if (lineNr != that.lineNr) return false;
        if (columnNr != that.columnNr) return false;
        return offset == that.offset;
    }

    @Override
    public int hashCode() {
        int result = lineNr;
        result = 31 * result + columnNr;
        result = 31 * result + (int) (offset ^ (offset >>> 32));
        return result;
    }
}
