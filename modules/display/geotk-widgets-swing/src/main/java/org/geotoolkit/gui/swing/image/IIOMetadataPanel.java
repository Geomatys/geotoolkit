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
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.IdentityHashMap;
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
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.Classes;
import org.geotoolkit.image.io.metadata.MetadataTreeNode;
import org.geotoolkit.image.io.metadata.MetadataTreeTable;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.internal.swing.ComboBoxRenderer;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * A panel showing the content of an {@link IIOMetadata} instance. This panel contains three parts:
 * <p>
 * <ul>
 *  <li>At the top, a field allowing to select which metadata to display:
 *   <ul>
 *    <li>The metadata format, typically
 *        {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME} or
 *        {@value javax.imageio.metadata.IIOMetadataFormatImpl#standardMetadataFormatName}.
 *    </li>
 *    <li>The metadata part to display: <cite>stream</cite> metadata or one of the
 *        <cite>image</cite> metadata.</li>
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
 * <table cellspacing="24" cellpadding="12" align="center"><tr valign="top">
 * <td width="500" bgcolor="lightblue">
 * {@section Demo}
 * To try this component in your browser, see the
 * <a href="http://www.geotoolkit.org/demos/geotk-simples/applet/IIOMetadataPanel.html">demonstration applet</a>.
 * </td></tr></table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @see MetadataTreeTable
 *
 * @since 3.05
 * @module
 */
@SuppressWarnings("serial")
public class IIOMetadataPanel extends JComponent {
    /**
     * The choices of metadata format. Typical choices are
     * {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME} and
     * {@value javax.imageio.metadata.IIOMetadataFormatImpl#standardMetadataFormatName}.
     */
    private final DefaultComboBoxModel<Object> formatChoices;

    /**
     * The properties of the currently selected metadata node.
     */
    private final JLabel description, validValues;

    /**
     * The unique instance of the set of listeners which is associated to this panel.
     */
    private final Controller controller;

    /**
     * The name of images for each index. This is an undocumented feature for now.
     */
    transient List<String> imageNames;

    /**
     * Creates a panel with no initial metadata. One of the {@code addXXXMetadata} or
     * {@code addXXXMetadataFormat} methods should be invoked in order to display a content.
     */
    public IIOMetadataPanel() {
        setLayout(new BorderLayout());
        // If the preferred width is modified, consider updating the
        // preferred column width in IIOMetadataTreeTable constructor.
        setPreferredSize(new Dimension(500, 400));
        final JComponent tables = new JPanel(new CardLayout());
        add(tables, BorderLayout.CENTER);
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        /*
         * Add the control button on top of the metadata table.
         */
        formatChoices = new DefaultComboBoxModel<>();
        final JComboBox<Object> formats = new JComboBox<>(formatChoices);
        ComboBoxRenderer.install(formats);
        formats.setName("Formats");
        if (true) {
            final JPanel controls = new JPanel(new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            final Insets ci = c.insets;
            ci.left = 12;
            c.gridy=0; ci.top=6; ci.bottom=0;
            c.gridx=0; c.weightx=0; c.anchor=GridBagConstraints.WEST;
            controls.add(label(resources, Vocabulary.Keys.Format, formats), c);
            c.insets.right = 12; c.insets.left = 0;
            c.gridx=1; c.weightx=1; c.fill=GridBagConstraints.BOTH;
            controls.add(formats, c);
            add(controls, BorderLayout.NORTH);
            controls.setOpaque(false);
        }
        /*
         * Add the section for metadata properties.
         */
        if (true) {
            final JPanel properties = new JPanel(new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            final Insets ci = c.insets;
            c.gridx=1; c.weightx=1; c.anchor=GridBagConstraints.WEST;
            c.gridy=0; ci.top=3; ci.bottom=0; properties.add(description = new JLabel(), c);
            c.gridy++; ci.top=0; ci.bottom=3; properties.add(validValues = new JLabel(), c);
            ci.left = 6;
            c.gridx=0; c.weightx=0; ci.right=9;
            c.gridy=0; ci.top=3; ci.bottom=0; properties.add(label(resources, Vocabulary.Keys.Description,  description), c);
            c.gridy++; ci.top=0; ci.bottom=3; properties.add(label(resources, Vocabulary.Keys.ValidValues, validValues), c);
            add(properties, BorderLayout.SOUTH);
            properties.setOpaque(false);
        }
        /*
         * Plug the listeners.
         */
        controller = new Controller(tables);
        formats.addActionListener(controller);
    }

    /**
     * Creates a new label for the given target component.
     */
    private static JLabel label(final Vocabulary resources, final short key, final JComponent target) {
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
     * @version 3.08
     *
     * @since 3.05
     * @module
     */
    private final class Controller implements ActionListener, TreeSelectionListener {
        /**
         * The component which hold every tables. This is the component that appear in the center of
         * this {@code IIOMetadataPanel}. Its layout manager must be an instance of {@link CardLayout}.
         */
        private final JComponent tables;

        /**
         * The selected format, or {@code null} if none.
         */
        private IIOMetadataChoice selectedFormat;

        /**
         * The table which is currently visible, or {@code null} if none.
         */
        private IIOMetadataTreeTable visibleTable;

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
            // Do not set visibleTable to null, because we want to copy its layout
            // when a new table will be created even if the previous table was for
            // an other image.
            selectedFormat = null;
            tables.removeAll();
        }

        /**
         * Invoked when a new format has been selected in the combo box.
         * When a change is detected, the tree is immediately updated.
         */
        @Override
        public void actionPerformed(final ActionEvent event) {
            final JComboBox<?> choices = (JComboBox<?>) event.getSource();
            final IIOMetadataChoice oldFormat = selectedFormat;
            final Object selected = choices.getSelectedItem();
            if (!(selected instanceof IIOMetadataChoice)) {
                /*
                 * This happen if the user selected the separator.
                 */
                choices.setSelectedItem(oldFormat);
                return;
            }
            final IIOMetadataChoice newFormat = (IIOMetadataChoice) selected;
            if (newFormat == null) {
                /*
                 * May be null if the user selected the choice which was already selected,
                 * which have the effect of unselecting it. We want the current format to
                 * stay selected.
                 */
                choices.setSelectedItem(oldFormat);
                return;
            }
            if (newFormat == oldFormat) {
                return;
            }
            show(newFormat);
        }

        /**
         * Shows the {@code TreeTable} associated with the given choice.
         * It is the caller's responsibility to ensure that the given
         * format is the one selected in the combo box.
         */
        final void show(final IIOMetadataChoice newFormat) {
            selectedFormat = newFormat;
            visibleTable = newFormat.show(tables, this, visibleTable);
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
                    final NumberRange<Integer> occurrences = node.getOccurrences();
                    if (occurrences != null) {
                        final String s = occurrences.toString();
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
        controller   .reset();
    }

    /**
     * Clears the previous metadata content and adds the values of the given <em>stream</em> and
     * <em>image</em> metadata. Invoking this method is equivalent to invoking {@link #clear()}
     * followed by {@link #addMetadata(IIOMetadata, IIOMetadata[]) addMetadata(...)}, except that
     * the metadata initially show will be for the same format than the one currently selected,
     * if this format exists in the new metadata.
     *
     * @param stream The stream metadata, or {@code null} if none.
     * @param image  The image metadata for each image in a file.
     *
     * @since 3.09
     */
    public void setMetadata(final IIOMetadata stream, final IIOMetadata... image) {
        final Object selected = formatChoices.getSelectedItem();
        clear();
        addMetadata(stream, image);
        final int index = formatChoices.getIndexOf(selected);
        if (index > 0) { // Intentionnaly skip the first choice, since it is already selected.
            final Object newFormat = formatChoices.getElementAt(index);
            if (newFormat instanceof IIOMetadataChoice) {
                formatChoices.setSelectedItem(newFormat);
                controller.show((IIOMetadataChoice) newFormat);
            }
        }
    }

    /**
     * Adds to this panel the values of the given <em>stream</em> and <em>image</em> metadata.
     * Note that this method is typically invoked alone; there is no need to invoke
     * {@link #addMetadataFormat addMetadataFormat} prior this method.
     *
     * @param stream The stream metadata, or {@code null} if none.
     * @param image  The image metadata for each image in a file.
     */
    public void addMetadata(final IIOMetadata stream, final IIOMetadata... image) {
        final Map<IIOMetadata,Integer> imageIndex = new IdentityHashMap<>();
        final Map<String, List<IIOMetadata>> metadataForNames = new LinkedHashMap<>();
        addFormatNames(stream, metadataForNames);
        imageIndex.put(stream, -1);
        if (image != null) {
            for (int i=0; i<image.length; i++) {
                final IIOMetadata metadata = image[i];
                addFormatNames(metadata, metadataForNames);
                imageIndex.put(metadata, i);
            }
        }
        /*
         * At this point, we grouped every metadata by format name and we remember the
         * image index for each metadata. Now process to the addition to the combo box.
         * If the format is already present in the combo box, insert right after the
         * existing format.
         */
        final Locale locale = getLocale();
        for (final Map.Entry<String, List<IIOMetadata>> entry : metadataForNames.entrySet()) {
            int insertAt = -1; // Where to insert, or -1 for adding to the end of the list.
            final String formatName = entry.getKey();
            for (int i=formatChoices.getSize(); --i>=0;) {
                final Object existing = formatChoices.getElementAt(i);
                if (existing instanceof IIOMetadataChoice) {
                    if (formatName.equals(((IIOMetadataChoice) existing).getFormatName())) {
                        insertAt = i;
                        break;
                    }
                }
            }
            if (insertAt < 0 && formatChoices.getSize() != 0) {
                formatChoices.addElement(ComboBoxRenderer.SEPARATOR);
            }
            final List<String> names = imageNames;
            final int namesCount = (names != null) ? names.size() : 0;
            for (final IIOMetadata metadata : entry.getValue()) {
                final int index = imageIndex.get(metadata); // Should never be null.
                final String name = (index >= 0 && index < namesCount) ? names.get(index) : null;
                final IIOMetadataChoice choice = new IIOMetadataChoice(locale, formatName, metadata, index, name);
                if (insertAt >= 0) {
                    formatChoices.insertElementAt(choice, ++insertAt);
                } else {
                    formatChoices.addElement(choice);
                }
            }
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
            final String[] formatNames = metadata.getMetadataFormatNames();
            moveAtEnd(formatNames, IIOMetadataFormatImpl.standardMetadataFormatName);
            moveAtEnd(formatNames, metadata.getNativeMetadataFormatName());
            for (final String formatName : formatNames) {
                List<IIOMetadata> list = metadataForNames.get(formatName);
                if (list == null) {
                    list = new ArrayList<>();
                    metadataForNames.put(formatName, list);
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
     * @param stream The stream metadata format, or {@code null} if none.
     * @param image  The image metadata format, or {@code null} if none.
     */
    public void addMetadataFormat(final IIOMetadataFormat stream, final IIOMetadataFormat image) {
        final Locale locale = getLocale();
        if (stream != null) {
            formatChoices.addElement(new IIOMetadataChoice(locale, stream, true));
        }
        if (image != null) {
            formatChoices.addElement(new IIOMetadataChoice(locale, image, false));
        }
    }

    /**
     * Adds to this panel the description of
     * {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME} and
     * {@value javax.imageio.metadata.IIOMetadataFormatImpl#standardMetadataFormatName} formats.
     * The descriptions contain no metadata value, only the name of the nodes together with a few
     * additional information (type, valid values, <i>etc.</i>).
     */
    public void addDefaultMetadataFormats() {
        addMetadataFormat(SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME),
                          SpatialMetadataFormat.getImageInstance (GEOTK_FORMAT_NAME));
        addMetadataFormat(null, IIOMetadataFormatImpl.getStandardFormatInstance());
    }
}
