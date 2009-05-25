/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.referencing;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.opengis.referencing.IdentifiedObject;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.io.wkt.UnformattableObjectException;


/**
 * Display informations about a CRS object. Current implementation only display the
 * <cite>Well Known Text</cite> (WKT). We may provide more informations in a future
 * version.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class PropertiesSheet extends JComponent {
    /**
     * Provides different view of the CRS object (properties, WKT, etc.).
     */
    private final JTabbedPane tabs;

    /**
     * The <cite>Well Known Text</cite> area.
     */
    private final JTextArea wktArea;

    /**
     * Creates a new, initially empty, property sheet.
     */
    public PropertiesSheet() {
        tabs    = new JTabbedPane();
        wktArea = new JTextArea();
        wktArea.setEditable(false);
        tabs.addTab("WKT", new JScrollPane(wktArea));
        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    /**
     * Sets the object to display in this property sheet.
     *
     * @param item The object to display info about.
     */
    public void setIdentifiedObject(final IdentifiedObject item) {
        String text;
        try {
            text = item.toWKT();
        } catch (UnsupportedOperationException e) {
            text = e.getLocalizedMessage();
            if (text == null) {
                text = Classes.getShortClassName(e);
            }
            final String lineSeparator = System.getProperty("line.separator", "\n");
            if (e instanceof UnformattableObjectException) {
                text = Vocabulary.format(Vocabulary.Keys.WARNING) + ": " + text +
                        lineSeparator + lineSeparator + item + lineSeparator;
            } else {
                text = Vocabulary.format(Vocabulary.Keys.ERROR) + ": " + text + lineSeparator;
            }
        }
        wktArea.setText(text);
    }

    /**
     * Sets an error message to display instead of the current identified object.
     *
     * @param message The error message.
     */
    public void setErrorMessage(final String message) {
        wktArea.setText(Vocabulary.format(Vocabulary.Keys.ERROR_$1, message));
    }
}
