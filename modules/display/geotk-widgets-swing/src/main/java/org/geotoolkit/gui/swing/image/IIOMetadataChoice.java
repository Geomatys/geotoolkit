/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.util.List;
import java.util.Locale;
import java.awt.CardLayout;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.TreeSelectionListener;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadata;

import org.geotoolkit.image.io.metadata.MetadataTreeTable;
import org.geotoolkit.resources.Vocabulary;


/**
 * A choice in the "Format" combo box. An instance of {@code IIOMetadataChoice} contains the
 * <cite>stream</cite> metadata with an arbitrary amount of the <cite>image</cite> metadata.
 * At least one of stream metadata and image metadata shall be non-null.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 */
@SuppressWarnings("serial")
final class IIOMetadataChoice implements Serializable {
    /**
     * The string representation of this instance to be returned by {@link #toString()}.
     * This is the label to be displayed in the combo box.
     */
    private final String label;

    /**
     * {@code true} if this object contains a stream metadata in addition
     * of image metadata.
     */
    private final boolean hasStreamMetadata;

    /**
     * The trees for the stream metadata (if any) and each image metadata.
     * All elements in this array are non-null.
     */
    private final MetadataTreeTable[] metadata;

    /**
     * The tables for the stream metadata (if any) and each image metadata.
     * Elements in this array are {@code null} if not yet created.
     */
    private final IIOMetadataTreeTable[] tables;

    /**
     * The name of the various metadata parts (stream metadata, image metadata, <i>etc/</i>).
     */
    private final String[] parts;

    /**
     * The index of the selected element in {@link #parts}.
     */
    private int selectedPart;

    /**
     * Creates a new instance for the given {@link IIOMetadataFormat}.
     * At least one of {@code stream} and {@code image} shall be non-null.
     *
     * @param locale The locale to use for formatting the node names.
     * @param stream The stream metadata format, or {@code null} if none.
     * @param image  The image metadata format, or {@code null} if none.
     */
    IIOMetadataChoice(final Locale locale, final IIOMetadataFormat stream, final IIOMetadataFormat image) {
        hasStreamMetadata = (stream != null);
        int count = hasStreamMetadata ? 1 : 0;
        if (image != null) {
            count++;
        }
        metadata = new MetadataTreeTable   [count];
        tables   = new IIOMetadataTreeTable[count];
        parts    = new String              [count];
        final Vocabulary resources = Vocabulary.getResources(locale);
        count = 0;
        if (hasStreamMetadata) {
            final MetadataTreeTable tree = new MetadataTreeTable(stream);
            tree.setLocale(locale);
            metadata[count] = tree;
            parts[count++] = resources.getString(Vocabulary.Keys.FILE);
        }
        if (image != null) {
            final MetadataTreeTable tree = new MetadataTreeTable(image);
            tree.setLocale(locale);
            metadata[count] = tree;
            parts[count++] = resources.getString(Vocabulary.Keys.IMAGES);
        }
        label = label(metadata);
    }

    /**
     * Creates a new instance for the given {@link IIOMetadata}. The metadata list can not
     * be empty. If the list contains the stream metadata, then it must be first in the list.
     *
     * @param locale The locale to use for formatting the node names.
     * @param format The format name.
     * @param stream The stream metadata, or {@code null} if none.
     * @param all    All metadata, <strong>including the stream one</strong>.
     */
    IIOMetadataChoice(final Locale locale, final String format,
            final IIOMetadata stream, final List<IIOMetadata> all)
    {
        final int count = all.size();
        hasStreamMetadata = (all.get(0) == stream);
        metadata = new MetadataTreeTable   [count];
        tables   = new IIOMetadataTreeTable[count];
        parts    = new String              [count];
        final Vocabulary resources = Vocabulary.getResources(locale);
        for (int i=0; i<count; i++) {
            final IIOMetadata im = all.get(i);
            final MetadataTreeTable tree = new MetadataTreeTable(im.getMetadataFormat(format));
            tree.setMetadata(im);
            tree.setLocale(locale);
            tree.setSimplificationAllowed(true);
            metadata[i] = tree;
            final String name;
            if (im == stream) {
                name = resources.getString(Vocabulary.Keys.FILE);
            } else {
                int n = i;
                if (!hasStreamMetadata) {
                    n++; // Number images starting with 1.
                }
                name = resources.getString(Vocabulary.Keys.IMAGE_$1, n);
            }
            parts[i] = name;
        }
        label = label(metadata);
    }

    /**
     * Returns the label to use in the Swing widget,
     * which is inferred from the metadata format.
     */
    private static String label(final MetadataTreeTable[] metadata) {
        return metadata[0].getMetadataFormat().getRootName().replace('_', ' ').trim();
    }

    /**
     * Returns the name of the currently selected metadata part (stream, image, <i>etc.</i>).
     */
    final String getSelectedPart() {
        return parts[selectedPart];
    }

    /**
     * Fills the given combo box model with the list of metadata parts allowed for this format choice.
     * This method is invoked when the {@code IIOMetadataChoice} to show in {@link IIOMetadataPanel}
     * changed, but not when only the metadata part to show changed.
     *
     * @param  partChoices The combox box model in which to put the list of available metadata parts.
     */
    final void fillPartChoices(final DefaultComboBoxModel partChoices) {
        partChoices.removeAllElements();
        for (final String part : parts) {
            partChoices.addElement(part);
        }
        partChoices.setSelectedItem(getSelectedPart());
    }

    /**
     * Sets the selected part to the given value. This method should be invoked before
     * {@link #show} in order to make a new metadata part visible.
     */
    final void setSelectedPart(final String part) {
        for (int i=0; i<parts.length; i++) {
            if (part.equals(parts[i])) {
                selectedPart = i;
                return;
            }
        }
        throw new IllegalArgumentException(part); // Should never happen.
    }

    /**
     * Shows the {@code TreeTable} associated with the stream or image metadata.
     * The given {@code panel} component <strong>must</strong> use a {@link CardLayout}.
     * This component is updated if needed with new {@link IIOMetadataTreeTable} instances,
     * which are created only when first needed.
     *
     * @param  panel The component which contain the collection of tables.
     * @param  The tree selection listener to be registered to the {@code TreeTable}
     *         if a new one is created. Otherwise ignored.
     * @param  The table which was visible before this method call, or {@code null} if unknown.
     * @return The table which is now visible.
     * @throws IndexOutOfBoundsException If the given image index is positive but out of bounds.
     */
    final IIOMetadataTreeTable show(final JComponent panel, final TreeSelectionListener listener,
            final IIOMetadataTreeTable visibleTable) throws IndexOutOfBoundsException
    {
        final int index = selectedPart;
        IIOMetadataTreeTable table = tables[index];
        if (table == null) {
            /*
             * A new table needs to be created. Constructs an identifier to be used
             * for locating the JTable in the java.awt.Container with CardLayout.
             */
            final MetadataTreeTable metadata = this.metadata[index];
            String identifier = metadata.getMetadataFormat().getRootName();
            final int offset = hasStreamMetadata ? 1 : 0;
            if (index >= offset) {
                identifier = identifier + ':' + (index - offset + 1);
            }
            /*
             * Create the table and register the listener, which is for displaying
             * the properties of the selected node in the widget bottom.
             */
            table = new IIOMetadataTreeTable(identifier, metadata.getRootNode(), visibleTable);
            panel.add(new JScrollPane(table), identifier);
            table.getTreeSelectionModel().addTreeSelectionListener(listener);
            tables[index] = table;
        }
        ((CardLayout) panel.getLayout()).show(panel, table.identifier);
        return table;
    }

    /**
     * Returns the label to be displayed in the combo box.
     */
    @Override
    public String toString() {
        return label;
    }
}
