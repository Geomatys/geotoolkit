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
import javax.imageio.metadata.IIOMetadataFormat;

import org.geotoolkit.image.io.metadata.MetadataTreeTable;


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
     * Creates a new instance for the given {@link IIOMetadataFormat}.
     *
     * @param locale The locale to use for formatting the node names.
     * @param stream The stream metadata format (mandatory).
     * @param image  The image metadata format, or {@code null} if none.
     */
    IIOMetadataChoice(final Locale locale, final IIOMetadataFormat stream, final IIOMetadataFormat image) {
        super(stream);
        setLocale(locale);
        label = stream.getRootName().replace('_', ' ').trim();
        imageMetadata = new MetadataTreeTable[(image != null) ? 1 : 0];
        if (image != null) {
            imageMetadata[0] = new MetadataTreeTable(image);
        }
        imageTables = new IIOMetadataTreeTable[imageMetadata.length];
    }

    /**
     * Shows the {@code TreeTable} associated with the stream or image metadata.
     * The given {@code tables} component <strong>must</strong> use a {@link CardLayout}.
     * This component is updated if needed with new {@link IIOMetadataTreeTable} instances,
     * which are created only when first needed.
     *
     * @param  tables The component which contain the set of table.
     * @param  image The index of the image for which metadata are wanted, or -1 for stream metadata.
     * @throws IndexOutOfBoundsException If the given image index is positive but out of bounds.
     */
    final void show(final JComponent tables, final int image) throws IndexOutOfBoundsException {
        IIOMetadataTreeTable table = (image >= 0) ? imageTables[image] : streamTable;
        if (table == null) {
            final MetadataTreeTable metadata = (image >= 0) ? imageMetadata[image] : this;
            String identifier = metadata.getMetadataFormat().getRootName();
            if (image >= 0) {
                identifier = identifier + ':' + image;
            }
            table = new IIOMetadataTreeTable(identifier, getRootNode());
            if (image >= 0) {
                imageTables[image] = table;
            } else {
                streamTable = table;
            }
            tables.add(new JScrollPane(table), identifier);
        }
        ((CardLayout) tables.getLayout()).show(tables, table.identifier);
    }

    /**
     * Returns the label to be displayed in the combo box.
     */
    @Override
    public String toString() {
        return label;
    }
}
