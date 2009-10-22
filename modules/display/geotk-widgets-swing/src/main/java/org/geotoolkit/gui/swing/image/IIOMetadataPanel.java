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

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

import org.jdesktop.swingx.JXTreeTable;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.image.io.metadata.MetadataTreeNode;
import org.geotoolkit.image.io.metadata.MetadataTreeTable;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;


/**
 * A panel showing the content of an {@link IIOMetadata} instance. This panel contains three parts:
 * <p>
 * <ul>
 *  <li>At the top, a few fields allow to select which metadata to display:
 *   <ul>
 *    <li>The metadata format, typically one of:
 *     <ul>
 *      <li>{@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME}</li>
 *      <li>{@value javax.imageio.metadata.IIOMetadataFormatImpl#standardMetadataFormatName}</li>
 *     </ul>
 *    </li>
 *    <li>The metadata part to display:
 *     <ul>
 *      <li><cite>Stream</cite> metadata which apply to a file as a whole.</li>
 *      <li>At least one <cite>image</cite> metadata which apply to an individual image in the
 *          file. More than one image metadata may be present if the file contains many images.</li>
 *     </ul>
 *    </li>
 *   </ul>
 *  </li>
 *  <li>At the center, the metadata as a {@linkplain JXTreeTable tree table}.
 *      The columns are documented in {@link MetadataTreeTable} javadoc.</li>
 *  <li>At the bottom, a description of the currently selected metadata node.</li>
 * </ul>
 * <p>
 * Most columns are hiden by default. The initial view shows only (<var>name</var>, <var>value</var>) pairs in
 * the {@link IIOMetadata} case, or (<var>name</var>, <var>type</var>) pairs in the {@link IIOMetadataFormat}
 * case. Users can make additional columns visible by clicking on the icon in the upper-right corner.
 * <p>
 * This class can be used in two ways (choose only one):
 * <p>
 * <ul>
 *   <li>For displaying the structure of {@link IIOMetadataFormat} instances without data,
 *     invoke {@link #addMetadataFormat addMetadataFormat(...)}.</li>
 *
 *   <li>For displaying the actual content of {@link IIOMetadata} instances, invoke
 *     {@link #addMetadata addMetadata(...)}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @see MetadataTreeTable
 *
 * @since 3.05
 * @module
 */
@SuppressWarnings("serial")
public class IIOMetadataPanel extends JPanel {
    /**
     * The choices of metadata format. Typical choices are
     * {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#FORMAT_NAME} and
     * {@value javax.imageio.metadata.IIOMetadataFormatImpl#standardMetadataFormatName}.
     */
    private final DefaultComboBoxModel formatChoices;

    /**
     * The choices of metadata parts. There is typically a <cite>stream</cite> metadata
     * and at least one <cite>image</cite> metadata. More image metadata may be present
     * if a stream contains many images.
     */
    private final DefaultComboBoxModel partChoices;

    /**
     * The properties of the currently selected metadata node.
     */
    private final JLabel description, validValues;

    /**
     * The unique instance of {@link Controller} which is associated to this panel.
     */
    private final Controller controller;

    /**
     * Creates a panel with no initial metadata. One of the {@code addXXXMetadata} or
     * {@code addXXXMetadataFormat} methods should be invoked in order to display a content.
     */
    public IIOMetadataPanel() {
        super(new BorderLayout());
        // If the preferred width is modified, consider updating the
        // preferred column width in IIOMetadataTreeTable constructor.
        setPreferredSize(new Dimension(500, 400));
        final JComponent tables = new JPanel(new CardLayout());
        add(tables, BorderLayout.CENTER);
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        /*
         * Add the control button on top of the metadata table.
         */
        formatChoices = new DefaultComboBoxModel();
        partChoices   = new DefaultComboBoxModel();
        final JComboBox formats = new JComboBox(formatChoices);
        final JComboBox parts   = new JComboBox(partChoices);
        formats.setName("Formats"); // This name is expected by Controller.
        parts  .setName("Parts");
        if (true) {
            final JPanel controls = new JPanel(new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            c.gridx=0; c.weightx=0; c.anchor=GridBagConstraints.WEST;
            c.gridy=0; controls.add(label(resources, Vocabulary.Keys.FORMAT, formats), c);
            c.gridy++; controls.add(label(resources, Vocabulary.Keys.PART,   parts),   c);
            c.gridx=1; c.weightx=1; c.fill=GridBagConstraints.BOTH;
            c.gridy=0; controls.add(formats, c);
            c.gridy++; controls.add(parts,   c);
            controls.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            add(controls, BorderLayout.NORTH);
        }
        /*
         * Add the section for metadata properties.
         */
        if (true) {
            final JPanel properties = new JPanel(new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            c.gridx=1; c.weightx=1; c.anchor=GridBagConstraints.WEST;
            c.gridy=0; properties.add(description = new JLabel(), c);
            c.gridy++; properties.add(validValues = new JLabel(), c);
            c.gridx=0; c.weightx=0; c.insets.right=9;
            c.gridy=0; properties.add(label(resources, Vocabulary.Keys.DESCRIPTION,  description), c);
            c.gridy++; properties.add(label(resources, Vocabulary.Keys.VALID_VALUES, validValues), c);
            add(properties, BorderLayout.SOUTH);
        }
        /*
         * Plug the listeners.
         */
        controller = new Controller(tables);
        formats.addActionListener(controller);
        parts  .addActionListener(controller);
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
    private final class Controller implements ActionListener, TreeSelectionListener {
        /**
         * The component which hold every tables. This is the component that appear in the center of
         * this {@code IIOMetadataPanel}. Its layout manager must be an instance of {@link CardLayout}.
         */
        final JComponent tables;

        /**
         * The selected format, or {@code null} if none.
         */
        private IIOMetadataChoice selectedFormat;

        /**
         * The table which is currently visible, or {@code null} if none.
         */
        private IIOMetadataTreeTable visibleTable;

        /**
         * {@code true} if the metadata selection is in process of being adjusted.
         * This is used for ignoring redundant events and process only when all
         * fields have been assigned appropriate values for the new selection.
         */
        private transient boolean isAdjusting;

        /**
         * Creates a new instance.
         */
        Controller(final JComponent tables) {
            this.tables = tables;
        }

        /**
         * Resets this controller in the same state than after construction.
         */
        final void reset() {
            selectedFormat = null;
            visibleTable   = null;
        }

        /**
         * Invoked when a new format or a new part has been selected in a combo box.
         * When a change is detected, the tree is immediately updated.
         */
        @Override
        public void actionPerformed(final ActionEvent event) {
            if (isAdjusting) {
                return;
            }
            final JComboBox choices = (JComboBox) event.getSource();
            final IIOMetadataChoice oldFormat = selectedFormat;
            /*
             * The case when a new format is selected.
             */
            if ("Formats".equals(choices.getName())) {
                final IIOMetadataChoice newFormat = (IIOMetadataChoice) choices.getSelectedItem();
                if (newFormat == null) {
                    /*
                     * 'f' may be null if the user selected the choice which was already selected,
                     * which have the effect of unselecting it. We want the current format to stay
                     * selected.
                     */
                    choices.setSelectedItem(oldFormat);
                    return;
                }
                if (newFormat == oldFormat) {
                    return;
                }
                selectedFormat = newFormat;
                isAdjusting = true;
                try {
                    newFormat.fillPartChoices(partChoices);
                } finally {
                    isAdjusting = false;
                }
            } else {
                /*
                 * The case when the format still the same and a new part is selected.
                 * Note that 'oldFormat' can not be null since this block can be run
                 * only after some items have been made available by the block above.
                 */
                final String oldPart = oldFormat.getSelectedPart();
                final String newPart = (String) choices.getSelectedItem();
                if (newPart == null) {
                    choices.setSelectedItem(oldPart);
                    return;
                }
                if (newPart == oldPart) {
                    return;
                }
                oldFormat.setSelectedPart(newPart);
            }
            /*
             * Make visible the new format or the new part.
             */
            visibleTable = selectedFormat.show(tables, this, visibleTable);
            showProperties(visibleTable.selectedNode);
        }

        /**
         * Invoked when a node has been selected.
         */
        @Override
        public void valueChanged(final TreeSelectionEvent event) {
            final TreePath path = event.getNewLeadSelectionPath();
            if (path != null) {
                final MetadataTreeNode node = (MetadataTreeNode) path.getLastPathComponent();
                visibleTable.selectedNode = node;
                showProperties(node);
            }
        }
    }

    /**
     * Fills the "properties" section in the bottom of this {@code IIOMetadataPanel}
     * using the information provided by the given node.
     *
     * @param node The selected node, for which to display the information in the bottom
     *        of this panel. Can be null.
     */
    final void showProperties(final MetadataTreeNode node) {
        if (node == null) {
            description.setText(null);
            validValues.setText(null);
        } else {
            /*
             * Get the description of the given node. If no description is found for that node,
             * search for the parent until a description is found. We do that way mostly because
             * when an element contains only one attribute, some format don't provide a description
             * for that attribute since it is redundant with the element description.
             */
            MetadataTreeNode parent = node;
            String text;
            do text = parent.getDescription();
            while (text == null && (parent = parent.getParent()) != null);
            if (text == null) {
                text = node.getLabel();
            }
            description.setText(text);
            /*
             * Now get the description of valid values. If there is none, we will build
             * one from the data type.
             */
            Object restriction = node.getValueRestriction();
            if (restriction == null) {
                Class<?> type = node.getValueType();
                if (type != null) {
                    if (type.isArray()) {
                        type = type.getComponentType();
                    }
                    StringBuilder buffer = new StringBuilder(Classes.getShortName(type));
                    final NumberRange<Integer> occurences = node.getOccurences();
                    if (occurences != null) {
                        final String s = occurences.toString();
                        if (s.startsWith("[")) {
                            buffer.append(s);
                        } else {
                            buffer.append('[').append(s).append(']');
                        }
                    }
                    restriction = buffer;
                }
            }
            validValues.setText(restriction != null ? restriction.toString() : null);
        }
    }

    /**
     * Removes all metadata from this widget. After the invocation of this method,
     * this panel will be in the same state than after construction.
     */
    public void clear() {
        formatChoices.removeAllElements();
        partChoices  .removeAllElements();
        controller   .reset();
    }

    /**
     * Adds to this panel the values of the given <em>stream</em> and <em>image</em> metadata.
     * Note that this method is typically invoked alone; there is no need to invoke
     * {@link #addMetadataFormat addMetadataFormat} prior this method.
     *
     * @param stream The stream metadata (mandatory).
     * @param image  The image metadata for each image in a file.
     */
    public void addMetadata(final IIOMetadata stream, final IIOMetadata... image) {
        final Map<String, List<IIOMetadata>> metadataForNames =
                new LinkedHashMap<String, List<IIOMetadata>>();
        addFormatNames(stream, metadataForNames);
        if (image != null) {
            for (final IIOMetadata metadata : image) {
                addFormatNames(metadata, metadataForNames);
            }
        }
        final Locale locale = getLocale();
        for (final Map.Entry<String, List<IIOMetadata>> entry : metadataForNames.entrySet()) {
            formatChoices.addElement(new IIOMetadataChoice(locale, entry.getKey(), stream, entry.getValue()));
        }
    }

    /**
     * Adds the metadata format names to the keys of the given map, and the metadata
     * to the values. The given metadata can be {@code null} (as authorized by the
     * {@link #addMetadata} method contract), in which case it is ignored.
     */
    private static void addFormatNames(final IIOMetadata metadata,
            final Map<String, List<IIOMetadata>> metadataForNames)
    {
        if (metadata != null) {
            final String[] formats = metadata.getMetadataFormatNames();
            moveAtEnd(formats, IIOMetadataFormatImpl.standardMetadataFormatName);
            moveAtEnd(formats, metadata.getNativeMetadataFormatName());
            for (final String format : formats) {
                List<IIOMetadata> list = metadataForNames.get(format);
                if (list == null) {
                    list = new ArrayList<IIOMetadata>();
                    metadataForNames.put(format, list);
                }
                list.add(metadata);
            }
        }
    }

    /**
     * If the {@code toMove} name is found in the given array, move it at the end of the array.
     */
    private static void moveAtEnd(final String[] names, final String toMove) {
        if (toMove != null) {
            for (int i=0; i<names.length; i++) {
                final String name = names[i];
                if (toMove.equals(name)) {
                    System.arraycopy(names, i+1, names, i, names.length - (i+1));
                    names[names.length - 1] = name;
                    break;
                }
            }
        }
    }

    /**
     * Adds to this panel the description of the given <em>stream</em> and <em>image</em>
     * metadata formats. The descriptions contain no metadata value, only the name of the
     * nodes together with a few additional information (type, valid values, <i>etc.</i>).
     *
     * @param stream The stream metadata format (mandatory).
     * @param image  The image metadata format, or {@code null} if none.
     */
    public void addMetadataFormat(final IIOMetadataFormat stream, final IIOMetadataFormat image) {
        if (stream != null || image != null) {
            formatChoices.addElement(new IIOMetadataChoice(getLocale(), stream, image));
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
        addMetadataFormat(SpatialMetadataFormat.STREAM, SpatialMetadataFormat.IMAGE);
        addMetadataFormat(null, IIOMetadataFormatImpl.getStandardFormatInstance());
    }
}
