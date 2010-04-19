/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.setup;

import javax.swing.*;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.util.Locale;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.resources.Descriptions;


/**
 * The main control panel.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
public final class ControlPanel extends JPanel implements ActionListener {
    /**
     * The identifiers used with {@link CardLayout} for identifying the panels
     * showing connection parameters.
     */
    private static final String[] CONNECTION_PANELS = {
        "EPSG",
        "Coverages"
    };

    /**
     * Non-null if the panel should be disposed on close, without
     * call to {@link System#exit}. Otherwise the default is to exit.
     */
    private JInternalFrame disposeOnClose;

    /**
     * Creates the panel.
     */
    private ControlPanel(final Vocabulary resources) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        final DataPanel dataPanel = new DataPanel(resources);
        /*
         * Creates the panel for database connection parameters:
         *
         *    - Connection to the EPSG database (must be first)
         *    - Connection to the coverages database.
         *
         * All of them share the same "apply" button.
         */
        final JButton applyButton = new JButton(resources.getString(Vocabulary.Keys.APPLY));
        final EPSGPanel epsgPanel = new EPSGPanel(resources, dataPanel, applyButton);
        dataPanel.epsgPanel = epsgPanel;
        final JPanel connectionPanels = new JPanel(new CardLayout());
        final String[] databaseNames = new String[CONNECTION_PANELS.length];
        for (int i=0; i<CONNECTION_PANELS.length; i++) {
            databaseNames[i] = resources.getString(Vocabulary.Keys.DATA_BASE_$1, CONNECTION_PANELS[i]);
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
        final JPanel connections = new JPanel(new BorderLayout());
        final JComboBox databaseChoices = new JComboBox(databaseNames);
        databaseChoices.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        databaseChoices.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent event) {
                final int selected = databaseChoices.getSelectedIndex();
                if (selected >= 0) {
                    final CardLayout layout = (CardLayout) connectionPanels.getLayout();
                    layout.show(connectionPanels, CONNECTION_PANELS[selected]);
                }
            }
        });
        /*
         * Creates a warning message saying that the password is not encrypted,
         * and creates the "Apply" button.
         */
        final JPanel messagePanel = new JPanel(new BorderLayout());
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
        connections.add(databaseChoices,  BorderLayout.BEFORE_FIRST_LINE);
        connections.add(connectionPanels, BorderLayout.CENTER);
        connections.add(messagePanel,     BorderLayout.AFTER_LAST_LINE);
        connections.addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(final ComponentEvent e) {
                removeComponentListener(this);
                epsgPanel.getSettings(); // Force loading of the properties file.
            }
        });
        /*
         * Creates the other tabs (directories, data).
         */
        final JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(resources.getString(Vocabulary.Keys.DIRECTORIES), new DirectoryPanel(dataPanel));
        tabs.addTab(resources.getString(Vocabulary.Keys.CONNECTION_PARAMETERS), connections);
        tabs.addTab(resources.getString(Vocabulary.Keys.DATA), dataPanel);
        add(tabs, BorderLayout.CENTER);
        final JButton close = new JButton(resources.getString(Vocabulary.Keys.CLOSE));
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(close);
        box.add(Box.createHorizontalGlue());
        close.addActionListener(this);
        add(box, BorderLayout.SOUTH);
    }

    /**
     * Invoked when the user press the "close" button.
     * This method is public as an implementation side effect
     * and should not be invoked directly.
     *
     * @param event The event provided by the clicked button.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (disposeOnClose != null) {
            disposeOnClose.dispose();
        } else {
            System.exit(0);
        }
    }

    /**
     * Displays the control panel as a standalone frame.
     *
     * @param locale The locale.
     */
    public static void show(final Locale locale) {
        Installation.allowSystemPreferences = true;
        final Vocabulary resources = Vocabulary.getResources(locale);
        final JFrame frame = new JFrame(resources.getString(Vocabulary.Keys.INSTALLATION_$1, "Geotoolkit.org"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ControlPanel(resources));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Displays the control panel as an internal frame.
     *
     * @param desktop The desktop in which to show the control panel.
     *
     * @since 3.11
     */
    public static void show(final JDesktopPane desktop) {
        final Vocabulary resources = Vocabulary.getResources(desktop.getLocale());
        final ControlPanel panel = new ControlPanel(resources);
        final JInternalFrame frame = new JInternalFrame(resources.getString(
                Vocabulary.Keys.INSTALLATION_$1, "Geotoolkit.org"), true, true);
        desktop.add(frame);
        panel.disposeOnClose = frame;
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
