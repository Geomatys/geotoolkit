/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.internal.sql.table.DefaultEntry;


/**
 * A series of coverages sharing common characteristics in a {@linkplain LayerEntry layer entry}.
 * A layer often regroup all coverages in a single series, but in some cases a layer may contains
 * more than one series. For example a layer of <cite>Sea Surface Temperature</cite> (SST) from
 * Nasa <cite>Pathfinder</cite> can be subdivised in two series:
 * <p>
 * <ul>
 *   <li>Final release of historical data. Those data are often two years old.</li>
 *   <li>More recent but not yet definitive data.</li>
 * </ul>
 * <p>
 * In most cases it is sufficient to work with {@linkplain LayerEntry layer entry}
 * as a whole without the need to go down to the {@code SeriesEntry}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class SeriesEntry extends DefaultEntry {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7119677073947466143L;

    /**
     * The value to put in {@link #protocol} for files.
     */
    static final String FILE_PROTOCOL = "file";

    /**
     * The protocol in a URL, or {@code "file"} if the files should be read locally.
     * The protocol may be for example {@code "file"}, {@code "ftp"} or {@code "dods"}
     * (the later is for OpenDAP).
     */
    final String protocol;

    /**
     * The host in a URL, or {@code null} if the files should be read locally.
     */
    private final String host;

    /**
     * The directory which contains the data files for this series.
     * The path separator is Unix slash, never the Windows backslash.
     * May be an empty string but never {@code null}.
     */
    final String path;

    /**
     * The extension to add to filenames, not including the dot character.
     */
    private final String extension;

    /**
     * The format of all coverages in this series.
     */
    final FormatEntry format;

    /**
     * Creates a new series entry. The identifier must be an instance of {@link Integer} rather
     * than an arbitrary {@link Comparable} because some methods assume integer type. A search
     * on usage of {@link #getIdentifier()} will list them.
     *
     * @param identifier The identifier for this series.
     * @param root       The root directory or URL, or {@code null} if none.
     * @param pathname   The relative or absolute directory which contains the data files for this series.
     * @param extension  The extension to add to filenames, not including the dot character.
     * @param format     The format of all coverages in this series.
     * @param remarks    The remarks, or {@code null} if none.
     */
    protected SeriesEntry(final Integer identifier, final String root, final String pathname,
                          final String extension, final FormatEntry format, final String remarks)
    {
        super(identifier, remarks);
        this.extension = extension;
        this.format    = format;
        /*
         * Checks if the pathname contains a URL host. If it does, then the URL will have
         * precedence over the root parameter. In such case the root parameter is ignored.
         */
        int split = pathname.indexOf("://");
        if (split >= 0) {
            protocol = pathname.substring(0, split).trim();
            split += 3;
            if (protocol.equalsIgnoreCase(FILE_PROTOCOL)) {
                host = null;
                path = pathname.substring(split);
                // Path is likely to contains a leading '/' since the syntax is usualy "file:///".
            } else {
                final int base = split;
                split = pathname.indexOf('/', split);
                if (split < 0) {
                    // No path after the protocol (e.g. "dods://www.foo.org").
                    host = pathname.substring(base);
                    path = "";
                } else {
                    host = pathname.substring(base, split);
                    path = pathname.substring(++split);
                }
            }
            return;
        }
        /*
         * Below this point, we known that the pathname is not an URL.
         * but maybe the "root" parameter is an URL. Checks it now.
         */
        if (root == null) {
            protocol = FILE_PROTOCOL;
            host     = null;
            path     = pathname;
            return;
        }
        split = root.indexOf("://");
        if (split < 0) {
            protocol = FILE_PROTOCOL;
            host     = null;
            split    = 0; // Used for computing 'path' later.
        } else {
            protocol = root.substring(0, split).trim();
            split += 3;
            if (protocol.equalsIgnoreCase(FILE_PROTOCOL)) {
                host = null;
            } else {
                final int base = split;
                split = root.indexOf('/', split);
                if (split < 0) {
                    host = root.substring(base);
                    path = pathname;
                    return;
                }
                host = root.substring(base, split++);
            }
        }
        final boolean isAbsolute = pathname.startsWith("/");
        if (isAbsolute) {
            path = pathname;
        } else {
            final String directory = root.substring(split);
            final StringBuilder buffer = new StringBuilder(directory);
            if (!directory.endsWith("/")) {
                buffer.append('/');
            }
            path = buffer.append(pathname).toString();
        }
    }

    /**
     * Returns the identifier of this series.
     */
    @Override
    public Integer getIdentifier() {
        return (Integer) identifier;
    }

    /**
     * Returns the given filename as a {@link File} augmented with series-dependent
     * {@linkplain File#getParent parent} and extension. The returned file should be
     * {@linkplain File#isAbsolute absolute}. If it is not, then there is probably no
     * {@linkplain org.constellation.catalog.ConfigurationKey#ROOT_DIRECTORY root directory}
     * set and consequently the file is probably not accessible locally. In such case,
     * consider using {@link #uri(String)} instead.
     *
     * @param  filename The filename, not including the extension.
     * @return The file.
     */
    public File file(String filename) {
        if (extension != null && !extension.isEmpty()) {
            filename = filename + '.' + extension;
        }
        return new File(path, filename);
    }

    /**
     * Returns the given filename as a {@link URI} augmented with series-dependent
     * {@linkplain URI#getHost host}, parent and extension.
     *
     * @param  filename The filename, not including the extension.
     * @return The file.
     * @throws URISyntaxException if the URI can not be created from the informations
     *         provided in the database.
     */
    public URI uri(final String filename) throws URISyntaxException {
        if (host == null) {
            return file(filename).toURI();
        }
        final StringBuilder buffer = new StringBuilder(path.length() + 8);
        if (!path.startsWith("/")) {
            buffer.append('/');
        }
        buffer.append(path);
        if (!path.endsWith("/") && !filename.startsWith("/")) {
            buffer.append('/');
        }
        buffer.append(filename);
        if (extension != null && !extension.isEmpty()) {
            buffer.append('.').append(extension);
        }
        return new URI(protocol, host, buffer.toString(), null);
    }

    /**
     * Compares this series entry with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final SeriesEntry that = (SeriesEntry) object;
            return Utilities.equals(this.protocol,   that.protocol)  &&
                   Utilities.equals(this.host,       that.host)      &&
                   Utilities.equals(this.path,       that.path)      &&
                   Utilities.equals(this.extension,  that.extension) &&
                   Utilities.equals(this.format,     that.format);
        }
        return false;
    }
}
