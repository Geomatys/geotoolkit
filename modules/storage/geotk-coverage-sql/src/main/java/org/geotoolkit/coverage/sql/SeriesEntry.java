/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * A series of coverages sharing common characteristics in a {@link ProductEntry}.
 * A product often regroup all coverages in a single series, but in some cases a product may contain
 * more than one series. For example a <cite>Sea Surface Temperature</cite> (SST) product from
 * Nasa <cite>Pathfinder</cite> can be subdivised in two series:
 *
 * <ul>
 *   <li>Final release of historical data. Those data are often two years old.</li>
 *   <li>More recent but not yet definitive data.</li>
 * </ul>
 *
 * In most cases it is sufficient to work with {@link ProductEntry} as a whole without
 * the need to go down to the {@code SeriesEntry}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class SeriesEntry extends Entry {
    /**
     * Identifier of this series.
     */
    final int identifier;

    /**
     * Identifier of the product to which this series belong.
     */
    final String product;

    /**
     * The directory which contains the data files for this series.
     */
    private final Path directory;

    /**
     * The extension to add to filenames, not including the dot character.
     */
    private final String extension;

    /**
     * The format of all coverages in this series.
     */
    final FormatEntry format;

    /**
     * Creates a new series entry.
     *
     * @param root       the root directory or URL, or {@code null} if none.
     * @param directory  the relative or absolute directory which contains the data files for this series.
     * @param extension  the extension to add to filenames, not including the dot character.
     * @param format     the format of all coverages in this series.
     */
    SeriesEntry(final int identifier, final String product, final Path root, final URI directory, String extension, final FormatEntry format) {
        this.identifier = identifier;
        this.product    = product;
        this.extension  = (extension != null && !(extension = extension.trim()).isEmpty()) ? extension : null;
        this.format     = format;
        this.directory  = directory.isAbsolute() ? Paths.get(directory) : root.resolve(directory.toString());
    }

    /**
     * Returns the given filename as a {@link Path} in the directory of this series.
     *
     * @param  filename  the filename, not including the extension.
     * @return path to the file.
     */
    Path path(String filename) {
        if (extension != null) {
            filename = filename + '.' + extension;
        }
        return directory.resolve(filename);
    }
}
