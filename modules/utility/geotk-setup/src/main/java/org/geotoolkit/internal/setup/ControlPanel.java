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
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
public final class ControlPanel extends JPanel implements ActionListener {
    /**
     * Creates the panel.
     */
    private ControlPanel(final Vocabulary resources) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        final DataPanel data = new DataPanel(resources);
        final EPSGPanel epsg = new EPSGPanel(resources, data);
        data.epsgPanel = epsg;
        final JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(resources.getString(Vocabulary.Keys.DIRECTORIES), new DirectoryPanel(data));
        tabs.addTab(resources.getString(Vocabulary.Keys.DATA_BASE_$1, "EPSG"), epsg);
        tabs.addTab(resources.getString(Vocabulary.Keys.DATA), data);
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
        System.exit(0);
    }

    /**
     * Displays the control panel.
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
}
