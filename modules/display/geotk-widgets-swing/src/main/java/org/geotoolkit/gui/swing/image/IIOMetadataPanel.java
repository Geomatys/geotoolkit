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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

import org.jdesktop.swingx.JXTreeTable;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.image.io.metadata.MetadataTreeTable;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;


/**
 * A panel showing the content of an {@link IIOMetadata} instance. This panel contains three parts:
 *
 * <ul>
 *   <li><p>On the top, a few fields allow to select which metadata to display:
 *     <ul>
 *       <li>The categories of metadata: <cite>stream</cite> metadata which apply to a file as a
 *       whole (note that some file formats allow the storage of many images), and <cite>image</cite>
 *       metadata which apply to an individual image in the file.</li>
 *       <li>The metadata format, which are different views of the same information.
 *       The typical formats are
 *       {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME} and
 *       {@value javax.imageio.metadata.IIOMetadataFormatImpl#standardMetadataFormatName}</li>
 *     </ul></p></li>
 *
 *   <li><p>In the center, the metadata content is displayed as a {@linkplain JXTreeTable tree table}.
 *     The columns that appear in the table are the ones documented in {@link MetadataTreeTable}
 *     javadoc.</p></li>
 *
 *   <li><p>In the bottom, the properties of the currently selected metadata node:
 *     textual description, expected type, valid values, default value.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @see MetadataTreeTable
 *
 * @since 3.05
 * @module
 *
 * @todo The properties panel in the bottom is disabled for now.
 */
@SuppressWarnings("serial")
public class IIOMetadataPanel extends JPanel {
    /**
     * The choices of metadata format. Typical choices are
     * {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME} and
     * {@value javax.imageio.metadata.IIOMetadataFormatImpl#standardMetadataFormatName}.
     */
    private final MutableComboBoxModel formatChoices;

    /**
     * The properties of the currently selected metadata node.
     */
    private final JLabel description, type, validValues, defaultValue;

    /**
     * Creates a panel with no initial metadata. One of the {@code addXXXMetadata} or
     * {@code addXXXMetadataFormat} methods should be invoked in order to display a content.
     */
    public IIOMetadataPanel() {
        super(new BorderLayout());
        final JComponent tables = new JPanel(new CardLayout());
        add(tables, BorderLayout.CENTER);
        /*
         * Add the control button on top of the metadata table.
         */
        formatChoices = new DefaultComboBoxModel();
        final JComboBox formats = new JComboBox(formatChoices);
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        if (true) {
            final Box controls = Box.createHorizontalBox();
            controls.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            controls.add(label(resources, Vocabulary.Keys.FORMAT, formats));
            controls.add(formats);
            add(controls, BorderLayout.NORTH);
        }
        /*
         * Add the section for metadata properties.
         */
        if (true) {
            final JPanel properties = new JPanel(new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            c.weightx=1; c.anchor=GridBagConstraints.WEST;
            c.gridx=1; properties.add(description  = new JLabel(), c);
            c.gridy++; properties.add(type         = new JLabel(), c);
            c.gridy++; properties.add(validValues  = new JLabel(), c);
            c.gridy++; properties.add(defaultValue = new JLabel(), c);
            c.gridx=0; c.weightx=0; c.insets.right=9;
            c.gridy=0; properties.add(label(resources, Vocabulary.Keys.DESCRIPTION,   description),  c);
            c.gridy++; properties.add(label(resources, Vocabulary.Keys.TYPE,          type),         c);
            c.gridy++; properties.add(label(resources, Vocabulary.Keys.VALID_VALUES,  validValues),  c);
            c.gridy++; properties.add(label(resources, Vocabulary.Keys.DEFAULT_VALUE, defaultValue), c);
// TODO     add(properties, BorderLayout.SOUTH);
        }
        /*
         * Plug the listeners.
         */
        final Controller ctrl = new Controller(tables);
        formats.addActionListener(ctrl);
    }

    /**
     * Creates a new label for the given target component.
     */
    private static JLabel label(final Vocabulary resources, final int key, final JComponent target) {
        final JLabel label = new JLabel(resources.getLabel(key));
        label.setLabelFor(target);
        return label;
    }

    /**
     * Various interfaces that we need to implement. We do that in an internal class
     * in order to avoid exposing publicly the methods that we implement. Only one
     * instance of this class is created for an instance of {@code IIOMetdataPanel}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.05
     *
     * @since 3.05
     * @module
     */
    private static final class Controller implements ActionListener {
        /**
         * The component which hold every tables. This is the component that appear in the center of
         * this {@code IIOMetadataPanel}. Its layout manager must be an instance of {@link CardLayout}.
         */
        final JComponent tables;

        /**
         * The selected format, or {@code null} if none.
         */
        private IIOMetadataChoice selected;

        /**
         * Creates a new instance.
         */
        Controller(final JComponent tables) {
            this.tables = tables;
        }

        /**
         * Invoked when a new format has been selected in the "Formats" combo box.
         * When a format change is detected, the tree is immediately updated.
         */
        @Override
        public void actionPerformed(final ActionEvent event) {
            final JComboBox formatChoices = (JComboBox) event.getSource();
            final IIOMetadataChoice f = (IIOMetadataChoice) formatChoices.getSelectedItem();
            if (f == null) {
                /*
                 * 'f' may be null if the user selected the choice which was already selected,
                 * which have the effect of unselecting it. We want the current format to stay
                 * selected.
                 */
                formatChoices.setSelectedItem(selected);
            } else if (f != selected) {
                selected = f;
                f.show(tables, -1);
            }
        }
    }

    /**
     * Adds to this panel the description of
     * {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME} and
     * {@value javax.imageio.metadata.IIOMetadataFormatImpl#standardMetadataFormatName} formats.
     * The descriptions contain no metadata value, only the name of the nodes together with a few
     * additional information (type, valid values, <i>etc.</i>).
     */
    public void addDefaultMetadataFormats() {
        addStreamMetadataFormats(SpatialMetadataFormat.STREAM, IIOMetadataFormatImpl.getStandardFormatInstance());
    }

    /**
     * Adds to this panel the description of the given metadata formats. The descriptions contain
     * no metadata value, only the name of the nodes together with a few additional information
     * (type, valid values, <i>etc.</i>).
     *
     * @param formats The metadata format to display in this table.
     */
    public void addStreamMetadataFormats(final IIOMetadataFormat... formats) {
        final Locale locale = getLocale();
        for (final IIOMetadataFormat format : formats) {
            formatChoices.addElement(new IIOMetadataChoice(locale, format, null));
        }
    }
}
