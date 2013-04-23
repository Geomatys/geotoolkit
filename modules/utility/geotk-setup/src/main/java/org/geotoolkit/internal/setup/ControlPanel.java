/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.io.Installation;


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
        final JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(resources.getString(Vocabulary.Keys.DIRECTORIES), new DirectoryPanel(dataPanel));
        tabs.addTab(resources.getString(Vocabulary.Keys.CONNECTION_PARAMETERS), new DatabasePanels(resources, dataPanel));
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
        final JFrame frame = new JFrame(resources.getString(Vocabulary.Keys.INSTALLATION_1, "Geotoolkit.org"));
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
                Vocabulary.Keys.INSTALLATION_1, "Geotoolkit.org"), true, true);
        desktop.add(frame);
        panel.disposeOnClose = frame;
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
