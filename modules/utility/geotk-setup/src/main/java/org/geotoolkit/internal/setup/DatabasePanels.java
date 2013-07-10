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
package org.geotoolkit.internal.setup;

import javax.swing.*;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Descriptions;


/**
 * The panel which will contain all {@link DatabasePanel} instances.
 *
 *    - Connection to the EPSG database (must be first)
 *    - Connection to the coverages database.
 *
 * All of them share the same "apply" button.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.11 (derived from 3.00)
 * @module
 */
@SuppressWarnings("serial")
final class DatabasePanels extends JComponent implements ActionListener {
    /**
     * The identifiers used with {@link CardLayout} for identifying the panels
     * showing connection parameters.
     */
    static final String[] CONNECTION_PANELS = {
        "EPSG",
        "Coverages"
    };

    /**
     * The panel which will contains the {@link DatabasePanel} instances.
     * This panel use a {@link CardLayout}.
     */
    private final JComponent connectionPanels;

    /**
     * The "Apply" button used in the panels for database connections.
     */
    private final JButton applyButton;

    /**
     * Creates the panels.
     */
    DatabasePanels(final Vocabulary resources, final DataPanel dataPanel) {
        setLayout(new BorderLayout());
        applyButton = new JButton(resources.getString(Vocabulary.Keys.APPLY));
        applyButton.setEnabled(false);
        final EPSGPanel epsgPanel = new EPSGPanel(resources, dataPanel, applyButton);
        dataPanel.epsgPanel = epsgPanel;
        connectionPanels = new JPanel(new CardLayout());
        connectionPanels.setOpaque(false);
        final String[] databaseNames = new String[CONNECTION_PANELS.length];
        for (int i=0; i<CONNECTION_PANELS.length; i++) {
            databaseNames[i] = resources.getString(Vocabulary.Keys.DATA_BASE_1, CONNECTION_PANELS[i]);
            final JComponent panel;
            switch (i) {
                case 0: panel = epsgPanel; break;
                case 1: panel = new CoveragePanel(resources, applyButton); break;
                default: throw new AssertionError(i);
            }
            connectionPanels.add(panel, CONNECTION_PANELS[i]);
        }
        /*
         * Creates the combo box for selecting which one of the above panels to show.
         */
        final JComboBox<String> databaseChoices = new JComboBox<>(databaseNames);
        databaseChoices.addActionListener(this);
        final Box choicePanel = Box.createHorizontalBox();
        choicePanel.add(databaseChoices);
        choicePanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        /*
         * Creates a warning message saying that the password is not encrypted,
         * and creates the "Apply" button.
         */
        final JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        final JLabel message = new JLabel(Descriptions.getResources(resources.getLocale())
                .getString(Descriptions.Keys.PASSWORD_NOT_ENCRYPTED));
        message.setForeground(Color.RED);
        messagePanel.add(applyButton, BorderLayout.LINE_END);
        messagePanel.add(message, BorderLayout.LINE_START);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 15));
        /*
         * Layouts the combo box, the connection parameters panels, and the (message, apply button
         * component. Note that the connection parameters panels defer the loading of the property
         * files until the component is view for the first time, but it doesn't seem to work with
         * CardLayout. So we add an other listener here for forcing the file loading of the EPSG
         * panel, which is expected to be the first panel visible on the top of the cards.
         */
        add(choicePanel,      BorderLayout.BEFORE_FIRST_LINE);
        add(connectionPanels, BorderLayout.CENTER);
        add(messagePanel,     BorderLayout.AFTER_LAST_LINE);
        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(final ComponentEvent e) {
                removeComponentListener(this);
                epsgPanel.getSettings(); // Force loading of the properties file.
            }
        });
    }

    /**
     * Invoked when a new database is selected. This method will make the selected
     * panel visible.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final int selected = ((JComboBox) event.getSource()).getSelectedIndex();
        if (selected >= 0) {
            final CardLayout layout = (CardLayout) connectionPanels.getLayout();
            layout.show(connectionPanels, CONNECTION_PANELS[selected]);
        }
    }

    /**
     * Refresh the state of the "Apply" button.
     */
    final void refresh() {
        boolean enabled = false;
        for (final Component c : connectionPanels.getComponents()) {
            if (((DatabasePanel) c).hasModifications()) {
                enabled = true;
                break;
            }
        }
        applyButton.setEnabled(enabled);
    }
}
