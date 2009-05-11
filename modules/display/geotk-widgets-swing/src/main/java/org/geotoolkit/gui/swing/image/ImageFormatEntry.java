/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.image;

import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;


/**
 * An element to be displayed in a list of image formats. A list of {@code ImageFormatEntry}
 * will be a list of formats available for both reading and writing.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
final class ImageFormatEntry {
    /**
     * The image reader for this entry.
     */
    final ImageReaderSpi reader;

    /**
     * The image reader for this entry.
     */
    final ImageWriterSpi writer;

    /**
     * The description to be displayed in the combo box.
     */
    final String description;

    /**
     * Creates a new entry.
     */
    private ImageFormatEntry(final ImageReaderSpi reader, final ImageWriterSpi writer, final String description) {
        this.reader = reader;
        this.writer = writer;
        this.description = description;
    }

    /**
     * Creates a new list of entries.
     *
     * @param writers {@code true} for writers, or {@code false} for readers.
     */
    public static ImageFormatEntry[] list(final Locale locale) {
        final Set<String> formatsDone = new HashSet<String>();
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final List<ImageFormatEntry> entries = new ArrayList<ImageFormatEntry>();
skip:   for (final Iterator<ImageReaderSpi> it=registry.getServiceProviders(ImageReaderSpi.class, true); it.hasNext();) {
            final ImageReaderSpi reader = it.next();
            ImageWriterSpi writer = null;
            for (final String format : reader.getFormatNames()) {
                if (!formatsDone.add(format)) {
                    // Avoid declaring the same format twice (e.g. declaring
                    // both the JSE and JAI ImageReaders for the PNG format).
                    continue skip;
                }
            }
            // TODO: check image writer here.
            entries.add(new ImageFormatEntry(reader, null, reader.getDescription(locale)));
        }
        return entries.toArray(new ImageFormatEntry[entries.size()]);
    }

    /**
     * Returns the string to be displayed in the combo box.
     */
    @Override
    public String toString() {
        return description;
    }
}
