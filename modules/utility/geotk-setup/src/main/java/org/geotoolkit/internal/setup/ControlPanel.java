/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.resources.Vocabulary;


/**
 * The main control panel.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@SuppressWarnings("serial")
final class ControlPanel extends JPanel implements ActionListener {
    /**
     * Creates the panel.
     */
    ControlPanel(final Vocabulary resources) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        final DataPanel data = new DataPanel(resources);
        final JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(resources.getString(Vocabulary.Keys.DIRECTORIES), new DirectoryPanel(data));
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
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        System.exit(0);
    }
}
