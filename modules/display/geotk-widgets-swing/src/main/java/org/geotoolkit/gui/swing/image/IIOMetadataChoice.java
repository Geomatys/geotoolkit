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

import java.util.Locale;
import java.awt.CardLayout;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

import org.geotoolkit.image.io.metadata.MetadataTreeTable;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.resources.Vocabulary;


/**
 * A choice in the "Format" combo box.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
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
     * The image index of the metadata, or -1 for stream metadata.
     */
    private final int imageIndex;

    /**
     * The trees for the metadata.
     */
    private final MetadataTreeTable metadataTable;

    /**
     * The table for the metadata. Created when first needed.
     */
    private transient IIOMetadataTreeTable metadataPanel;

    /**
     * Creates a new instance for the given {@link IIOMetadataFormat}.
     *
     * @param locale   The locale to use for formatting the node names.
     * @param metadata The metadata format.
     * @param isStream {@code true} for stream metadata, or {@code false} for image metadata.
     */
    IIOMetadataChoice(final Locale locale, final IIOMetadataFormat metadata, final boolean isStream) {
        metadataTable = new MetadataTreeTable(metadata);
        metadataTable.setLocale(locale);
        imageIndex = isStream ? -1 : 0;
        label = label(locale, true);
    }

    /**
     * Creates a new instance for the given {@link IIOMetadata}.
     *
     * @param locale     The locale to use for formatting the node names.
     * @param format     The format name.
     * @param metadata   The metadata.
     * @param imageIndex The image index of the metadata, or -1 for stream metadata.
     */
    IIOMetadataChoice(final Locale locale, final String format,
            final IIOMetadata metadata, final int imageIndex)
    {
        metadataTable = new MetadataTreeTable(metadata.getMetadataFormat(format));
        metadataTable.setMetadata(metadata);
        metadataTable.setLocale(locale);
        metadataTable.setSimplificationAllowed(true);
        this.imageIndex = imageIndex;
        label = label(locale, false);
    }

    /**
     * Returns the label to use in the Swing widget,
     * which is inferred from the metadata format.
     */
    private String label(final Locale locale, final boolean isFormat) {
        final Vocabulary resources = Vocabulary.getResources(locale);
        final String part;
        if (imageIndex < 0) {
            part = resources.getString(Vocabulary.Keys.FILE);
        } else if (isFormat) {
            part = resources.getString(Vocabulary.Keys.IMAGES);
        } else {
            part = resources.getString(Vocabulary.Keys.IMAGE_$1, imageIndex + 1);
        }
        String rootName = metadataTable.getMetadataFormat().getRootName();
        if (rootName.equals(SpatialMetadataFormat.FORMAT_NAME)) {
            rootName = resources.getString(Vocabulary.Keys.GEOSPATIAL);
        } else if (rootName.equals(IIOMetadataFormatImpl.standardMetadataFormatName)) {
            rootName = resources.getString(Vocabulary.Keys.STANDARD);
        } else {
            rootName = rootName.replace('_', ' ').trim();
        }
        return "<html><b>" + rootName + "</b> \u00A0\u2014\u00A0 " + part + "</html>";
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
        if (metadataPanel == null) {
            /*
             * A new table needs to be created. Constructs an identifier to be used
             * for locating the JTable in the java.awt.Container with CardLayout.
             */
            String identifier = metadataTable.getMetadataFormat().getRootName();
            if (imageIndex >= 0) {
                identifier = identifier + ':' + imageIndex;
            }
            /*
             * Create the table and register the listener, which is for displaying
             * the properties of the selected node in the widget bottom.
             */
            metadataPanel = new IIOMetadataTreeTable(identifier, metadataTable.getRootNode(), visibleTable);
            panel.add(new JScrollPane(metadataPanel), identifier);
            metadataPanel.getTreeSelectionModel().addTreeSelectionListener(listener);
        }
        ((CardLayout) panel.getLayout()).show(panel, metadataPanel.identifier);
        return metadataPanel;
    }

    /**
     * Returns the label to be displayed in the combo box.
     */
    @Override
    public String toString() {
        return label;
    }
}
