/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.storage.multires;

import java.util.List;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.util.CodeList;

/**
 * Describes the encoding of tiles in the resource.
 * This is used to description the content of {@link Tile#getContentPath() }.
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileFormat {

    public static final TileFormat UNDEFINED = new TileFormat(null, null, Compression.UNDEFINED);

    protected String mimeType;
    protected String providerId;
    protected Compression compression;

    protected TileFormat() {
        this.compression = Compression.NONE;
    }

    public TileFormat(String mimeType, String providerId, Compression compression) {
        ArgumentChecks.ensureNonNull("compression", compression);
        this.mimeType = mimeType;
        this.providerId = providerId;
        this.compression = compression;
    }

    /**
     * @return tile encoding mime-type.
     *         may be null if the storage use varying formats (like png/jpeg/webp) for tiles.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return optional datastore provider identifier.
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * @return the compression used to further compress the tile content.
     */
    public Compression getCompression() {
        return compression;
    }

    public static class Compression extends CodeList<Compression> {

        public static final Compression UNDEFINED;
        public static final Compression NONE;
        public static final Compression ZIP;
        public static final Compression GZIP;
        public static final Compression LZ4;
        public static final Compression BROTLI;
        public static final Compression ZSTD;

    /**
     * All code list values created in the currently running <abbr>JVM</abbr>.
     */
    private static final List<Compression> VALUES = initialValues(
        // Inline assignments for getting compiler error if a field is missing or duplicated.
        UNDEFINED = new Compression("UNDEFINED"),
        NONE      = new Compression("NONE"),
        ZIP       = new Compression("ZIP"),
        GZIP      = new Compression("GZIP"),
        LZ4       = new Compression("LZ4"),
        BROTLI    = new Compression("BROTLI"),
        ZSTD      = new Compression("ZSTD"));

        private Compression(final String name) {
            super(name);
        }

        @Override
        public Compression[] family() {
        return VALUES.toArray(Compression[]::new);
        }

        public static Compression valueOf(String code) {
        return valueOf(VALUES, code, Compression::new);
        }
    }
}
