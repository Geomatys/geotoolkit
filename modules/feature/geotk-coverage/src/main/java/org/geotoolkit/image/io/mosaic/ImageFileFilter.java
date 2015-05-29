/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;

import org.apache.sis.util.ArraysExt;


/**
 * Filter the content of a directory according the file names. The filtering is performed using
 * the file suffix. This filter contains an arbitrary list of files to exclude; it is not quite
 * appropriate to put that in a public API for this reason.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
final class ImageFileFilter implements FileFilter, FilenameFilter {
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
    public ImageFileFilter(final ImageReaderSpi spi) {
        if (spi != null) {
            suffixes = spi.getFileSuffixes();
        } else {
            suffixes = ImageIO.getReaderFileSuffixes();
        }
    }

    /**
     * Tests whether or not the specified pathname should be included in a file list.
     * If the given pathname is a directory, then this method returns {@code true} in
     * order to allow recursive invocation.
     *
     * @param  pathname The abstract pathname to be tested.
     * @return {@code true} if the pathname should be included.
     */
    @Override
    public boolean accept(final File pathname) {
        return pathname.isDirectory() || accept(null, pathname.getName());
    }

    /**
     * Tests whether or not the specified filename should be included in a file list.
     * This method does not allow recursive invocation of sub-directories.
     *
     * @param directory Ignored (can be {@code null}).
     * @param filename The filename to be tested.
     * @return {@code true} if the filename should be included.
     */
    @Override
    public boolean accept(final File directory, String filename) {
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
