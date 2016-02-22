/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2015, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2015, Geomatys
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
package org.geotoolkit.image.io.mosaic;

import org.apache.sis.internal.storage.IOUtilities;
import org.apache.sis.util.ArraysExt;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

/**
 * PathMatcher implementation of previous {@link ImageFileFilter} using NIO api.
 * This PathMatcher is designed to be used with {@link org.geotoolkit.nio.PathFilterVisitor} that
 * return all matched Path visited.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ImagePathMatcher implements PathMatcher {

    /**
     * Some special filenames to exclude when they are followed by the {@code ".txt"}
     * suffix or no suffix.
     */
    private static final String[] EXCLUDES = {"readme", "credits", "license"};

    /**
     * The suffixes of image files to accept.
     */
    private final String[] suffixes;

    /**
     * Creates a new filter.
     *
     * @param spi The provider for which to accept images, or {@code null} for all providers.
     */
    public ImagePathMatcher(final ImageReaderSpi spi) {
        if (spi != null) {
            suffixes = spi.getFileSuffixes();
        } else {
            suffixes = ImageIO.getReaderFileSuffixes();
        }
    }

    /**
     * Tests whether or not the specified filename should be included in a file list.
     *
     * @param path Path to test
     * @return {@code true} if the filename should be included.
     */
    @Override
    public boolean matches(Path path) {
        String filename = IOUtilities.filename(path);
        final int s = filename.lastIndexOf('.');
        final String suffix = (s >= 0) ? filename.substring(s + 1) : "";
        if (ArraysExt.containsIgnoreCase(suffixes, suffix)) {
            // Found a file having the expected suffix. However we still have a
            // few special cases to exclude, namely the readme and license files.
            if (suffix.isEmpty() || suffix.equalsIgnoreCase("txt")) {
                if (s >= 0) {
                    filename = filename.substring(0, s);
                }
                return !ArraysExt.containsIgnoreCase(EXCLUDES, filename);
            }
            return true;
        }
        return false;
    }
}
