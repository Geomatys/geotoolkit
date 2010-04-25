/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.gui.swing.coverage;

import java.util.Set;
import java.util.Arrays;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.sql.CoverageDatabase;
import org.geotoolkit.coverage.sql.FutureQuery;
import org.geotoolkit.coverage.sql.Layer;
import org.geotoolkit.gui.swing.ExceptionMonitor;
import org.geotoolkit.internal.swing.ArrayListModel;
import org.geotoolkit.resources.Vocabulary;


/**
 * A widget displaying the {@linkplain CoverageDatabase#getLayers() list of layers} available
 * in a coverage database. In the lower part of the panel, a description of the selected layer
 * is displayed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@SuppressWarnings("serial")
public class LayerList extends JSplitPane {
    /**
     * The database for which to display the list of available layers.
     */
    protected final CoverageDatabase database;

    /**
     * The list of layers.
     */
    private final ArrayListModel<String> layers;

    /**
     * The name of the currently selected layer.
     */
    private final JTextField name;

    /**
     * Creates a new {@code LayerList} instance for the given database.
     *
     * @param database The database for which to display the list of available layers.
     */
    public LayerList(final CoverageDatabase database) {
        super(VERTICAL_SPLIT);
        this.database = database;
        layers = new ArrayListModel<String>();
        final JList layerList = new JList(layers);
        layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setTopComponent(new JScrollPane(layerList));
        setLayerNames(database.getLayers());
        layerList.addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(final ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    setSelectedLayer((String) layerList.getSelectedValue());
                }
            }
        });
        /*
         * Creates the bottom component.
         */
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridy  = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        name = addField(resources, Vocabulary.Keys.NAME, panel, c);
        setBottomComponent(panel);
        setPreferredSize(new Dimension(400, 200));
    }

    /**
     * Adds a new label to the given panel.
     */
    private static JTextField addField(final Vocabulary resources, final int labelKey,
            final JPanel panel, final GridBagConstraints c)
    {
        final JLabel label = new JLabel(resources.getLabel(labelKey));
        final JTextField field = new JTextField();
        field.setEditable(false);
        label.setLabelFor(field);
        c.gridx=0; c.weightx=0; panel.add(label, c);
        c.gridx++; c.weightx=1; panel.add(field, c);
        c.gridy++;
        return field;
    }

    /**
     * Refreshes the list of layer names with the result of the given task.
     */
    private void setLayerNames(final FutureQuery<Set<String>> task) {
        task.invokeAfterCompletion(new Runnable() {
            /** The names of all layers. */
            private String[] names;

            /** Get the names of all layers, then pass them to the JList in the Swing thread. */
            @Override public synchronized void run() {
                if (names == null) try {
                    final Set<String> layerNames = task.result();
                    names = layerNames.toArray(new String[layerNames.size()]);
                    Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
                } catch (CoverageStoreException ex) {
                    ExceptionMonitor.show(LayerList.this, ex);
                    return;
                }
                if (!EventQueue.isDispatchThread()) {
                    EventQueue.invokeLater(this);
                } else {
                    layers.setElement(names);
                }
            }
        });
    }

    /**
     * Sets the layer to describe in the bottom panel.
     * This method does not change the selection in the list.
     *
     * @param layer The name of the layer to describe.
     */
    private void setSelectedLayer(final String layer) {
        name.setText(null);
        if (layer != null) {
            final FutureQuery<Layer> task = database.getLayer(layer);
            task.invokeAfterCompletion(new Runnable() {
                /** Info extracted from the Layer. */
                private String name;

                /** Get the info from the layer, then pass them in the Swing thread to the widgets. */
                @Override public synchronized void run() {
                    if (name == null) try {
                        final Layer layer = task.result();
                        name = layer.getName();
                    } catch (CoverageStoreException ex) {
                        ExceptionMonitor.show(LayerList.this, ex);
                        return;
                    }
                    if (!EventQueue.isDispatchThread()) {
                        EventQueue.invokeLater(this);
                    } else {
                        LayerList.this.name.setText(name);
                    }
                }
            });
        }
    }
}
