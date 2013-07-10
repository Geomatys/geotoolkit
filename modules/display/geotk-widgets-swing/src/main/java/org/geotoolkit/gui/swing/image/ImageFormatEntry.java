/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.swing.JComboBox;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import org.apache.sis.util.ArraysExt;


/**
 * An element to be displayed in a list of image formats. A list of such elements is
 * created by the {@link #list} method. Those elements are designed for use in a Swing
 * combo box.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class ImageFormatEntry implements Comparable<ImageFormatEntry> {
    /**
     * The image reader for this entry.
     */
    private ImageReaderSpi reader;

    /**
     * The image reader for this entry.
     */
    private ImageWriterSpi writer;

    /**
     * The file format. Also used as the description to be displayed in the combo box
     * in current version.
     */
    private String format;

    /**
     * Creates a new entry.
     */
    private ImageFormatEntry() {
    }

    /**
     * Creates a new list of entries in a combo box. This method returns elements
     * that describes formats available both for reading and for writing.
     *
     * @param defaultFormat The format to select by default (can not be {@code null}).
     */
    static JComboBox<ImageFormatEntry> comboBox(final String defaultFormat) {
        final Set<ImageFormatEntry> preferred = new LinkedHashSet<>();
        final JComboBox<ImageFormatEntry> formatChoices = new JComboBox<>(list(defaultFormat, preferred));
        final Iterator<ImageFormatEntry> it = preferred.iterator();
        if (it.hasNext()) {
            formatChoices.setSelectedItem(it.next());
            // Select only one, ignore the other ones.
        }
        return formatChoices;
    }

    /**
     * Creates a new list of entries. This method returns elements that describes formats
     * available both for reading and for writing.
     *
     * @param defaultFormat The format to select by default (can not be {@code null}).
     * @param preferred Where to store the entry for the preferred format.
     */
    private static ImageFormatEntry[] list(final String defaultFormat, final Set<ImageFormatEntry> preferred) {
        final Map<String,ImageFormatEntry> formatsDone = new HashMap<>();
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        /*
         * Get the list of writers first, because they are typically less numerous
         * than readers (e.g. they were no GIF writers before the patent get expired).
         */
skip:   for (final Iterator<ImageWriterSpi> it=registry.getServiceProviders(ImageWriterSpi.class, true); it.hasNext();) {
            final ImageWriterSpi spi = it.next();
            final ImageFormatEntry entry = new ImageFormatEntry();
            for (final String format : spi.getFormatNames()) {
                final ImageFormatEntry old = formatsDone.put(format, entry);
                if (old != null) {
                    // Avoid declaring the same format twice (e.g. declaring
                    // both the JSE and JAI ImageReaders for the PNG format).
                    formatsDone.put(format, old);
                    continue skip;
                }
                entry.addFormat(format);
                if (defaultFormat.equalsIgnoreCase(format)) {
                    preferred.add(entry);
                }
            }
            entry.writer = spi;
        }
        /*
         * Associate a reader to the entries built in the previous step.
         */
skip:   for (final Iterator<ImageReaderSpi> it=registry.getServiceProviders(ImageReaderSpi.class, true); it.hasNext();) {
            final ImageReaderSpi spi = it.next();
            for (final String format : spi.getFormatNames()) {
                final ImageFormatEntry entry = formatsDone.get(format);
                if (entry != null) {
                    if (entry.reader == null) {
                        entry.reader = spi;
                    } else if (entry.reader != spi) {
                        // Avoid declaring the same format twice (e.g. declaring
                        // both the JSE and JAI ImageReaders for the PNG format).
                        continue skip;
                    }
                    entry.addFormat(format);
                }
            }
        }
        /*
         * Gets the array of entries, removing the one without readers.
         * We wraps in a HashSet in order to remove duplicated values.
         */
        final Collection<ImageFormatEntry> entries = new HashSet<>(formatsDone.values());
        ImageFormatEntry[] array = entries.toArray(new ImageFormatEntry[entries.size()]);
        int count = 0;
        for (int i=0; i<array.length; i++) {
            final ImageFormatEntry entry = array[i];
            if (entry.reader != null) {
                array[count++] = entry;
            }
        }
        array = ArraysExt.resize(array, count);
        preferred.retainAll(Arrays.asList(array));
        Arrays.sort(array);
        return array;
    }

    /**
     * If the given string is longer than the current {@linkplain #format},
     * replaces the current description by that format.
     */
    private void addFormat(final String format) {
        this.format = longest(this.format, format);
    }

    /**
     * Selects the longest format string. If two of them
     * have the same length, favor the one in upper case.
     *
     * @param current    The previous longest format string, or {@code null} if none.
     * @param candidate  The format string which may be longuer than the previous one.
     * @return The format string which is the longest one up to date.
     */
    static String longest(final String current, final String candidate) {
        if (current != null) {
            final int dl = candidate.length() - current.length();
            if (dl < 0 || (dl == 0 && candidate.compareTo(current) >= 0)) {
                return current;
            }
        }
        return candidate;
    }

    /**
     * Returns the image format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the image reader provider.
     */
    public ImageReaderSpi getReader() {
        return reader;
    }

    /**
     * Returns the image writer provider.
     */
    public ImageWriterSpi getWriter() {
        return writer;
    }

    /**
     * Compares this entry with the given one for order. This is used for sorting
     * the entries to be displayed in a combo box.
     */
    @Override
    public int compareTo(final ImageFormatEntry other) {
        return format.compareTo(other.format);
    }

    /**
     * Returns the string to be displayed in the combo box.
     */
    @Override
    public String toString() {
        return format;
    }
}
