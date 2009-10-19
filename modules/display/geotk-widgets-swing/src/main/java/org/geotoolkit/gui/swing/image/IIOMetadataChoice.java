/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.util.Locale;
import java.awt.CardLayout;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.TreeSelectionListener;
import javax.imageio.metadata.IIOMetadataFormat;

import org.geotoolkit.image.io.metadata.MetadataTreeTable;
import org.geotoolkit.resources.Vocabulary;


/**
 * A choice in the "Format" combo box. An instance of {@code IIOMetadataChoice} primarily contains
 * the <cite>stream</cite> metadata, which are mandatory. It can optionnaly contains an arbitrary
 * amount of the <cite>image</cite> metadata.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 */
final class IIOMetadataChoice extends MetadataTreeTable {
    /**
     * The string representation of this instance to be returned by {@link #toString()}.
     * This is the label to be displayed in the combo box.
     */
    private final String label;

    /**
     * The metadata for each individual images. An element in this array may be
     * initially null and filled only when first needed.
     */
    private final MetadataTreeTable[] imageMetadata;

    /**
     * The table for the stream metadata represented by this format, or {@code null}
     * if not yet created.
     */
    private IIOMetadataTreeTable streamTable;

    /**
     * The table for the image metadata represented by this format. Elements in this
     * array are {@code null} if not yet created.
     */
    private final IIOMetadataTreeTable[] imageTables;

    /**
     * The name of the various metadata parts (stream metadata, image metadata, <i>etc/</i>).
     * The length of this array is always greater than 0, and the first element is always for
     * stream metadata.
     */
    private final String[] parts;

    /**
     * The index of the selected element in {@link #parts}.
     */
    private int selectedPart;

    /**
     * Creates a new instance for the given {@link IIOMetadataFormat}.
     *
     * @param locale The locale to use for formatting the node names.
     * @param stream The stream metadata format (mandatory).
     * @param image  The image metadata format, or {@code null} if none.
     */
    IIOMetadataChoice(final Locale locale, final IIOMetadataFormat stream, final IIOMetadataFormat image) {
        super(stream);
        setLocale(locale);
        final Vocabulary resources = Vocabulary.getResources(locale);
        label = stream.getRootName().replace('_', ' ').trim();
        imageMetadata = new MetadataTreeTable[(image != null) ? 1 : 0];
        parts = new String[1 + imageMetadata.length];
        if (image != null) {
            imageMetadata[0] = new MetadataTreeTable(image);
            parts[1] = resources.getString(Vocabulary.Keys.IMAGES);
        }
        imageTables = new IIOMetadataTreeTable[imageMetadata.length];
        parts[0] = resources.getString(Vocabulary.Keys.FILE);
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
     * The given {@code tables} component <strong>must</strong> use a {@link CardLayout}.
     * This component is updated if needed with new {@link IIOMetadataTreeTable} instances,
     * which are created only when first needed.
     *
     * @param  tables The component which contain the set of table.
     * @param  The tree selection listener to be registered to the {@code TreeTable} if a new one is
     *         created. Otherwise ignored.
     * @return The table which is now visible.
     * @throws IndexOutOfBoundsException If the given image index is positive but out of bounds.
     */
    final IIOMetadataTreeTable show(final JComponent tables, final TreeSelectionListener listener)
            throws IndexOutOfBoundsException
    {
        final int image = selectedPart - 1;
        IIOMetadataTreeTable table = (image >= 0) ? imageTables[image] : streamTable;
        if (table == null) {
            final MetadataTreeTable metadata = (image >= 0) ? imageMetadata[image] : this;
            String identifier = metadata.getMetadataFormat().getRootName();
            if (image >= 0) {
                identifier = identifier + ':' + image;
            }
            table = new IIOMetadataTreeTable(identifier, metadata.getRootNode());
            if (image >= 0) {
                imageTables[image] = table;
            } else {
                streamTable = table;
            }
            tables.add(new JScrollPane(table), identifier);
            table.getTreeSelectionModel().addTreeSelectionListener(listener);
        }
        ((CardLayout) tables.getLayout()).show(tables, table.identifier);
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
