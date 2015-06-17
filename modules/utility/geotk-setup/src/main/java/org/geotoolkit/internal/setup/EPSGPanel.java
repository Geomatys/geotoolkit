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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.opengis.util.FactoryException;

import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.referencing.factory.epsg.EpsgInstaller;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.referencing.factory.epsg.EpsgInstaller.DEFAULT_SCHEMA;

/**
 * The panel displaying a configuration form for the connection parameters to the current
 * EPSG database. Geotk can use either the embedded JavaDB database or an explicit one
 * selected by this form.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
final class EPSGPanel extends DatabasePanel {
    /**
     * Creates the panel.
     */
    EPSGPanel(final Vocabulary resources, final DataPanel dataPanel, final JButton applyButton) {
        super(resources, Installation.EPSG, true, applyButton);
        final class Refresh implements ActionListener, Runnable {
            /**
             * Will refresh the state of the "Available data" panel later. We need to defer
             * the refresh because the other ActionListeners must be executed first, because
             * some of them write the property files that the DataPanel will need to read.
             */
            @Override public void actionPerformed(final ActionEvent event) {
                EventQueue.invokeLater(this);
            }

            @Override public void run() {
                dataPanel.refresh(DataPanel.EPSG);
            }
        }
        applyButton.addActionListener(new Refresh());
    }

    /**
     * Provides the database fields.
     */
    @Override
    Field[] getFields(final Vocabulary resources) {
        final JComboBox<String> url = new JComboBox<>(new String[] {
            ThreadedEpsgFactory.getDefaultURL(),
            "jdbc:derby:" + System.getProperty("user.home", "").replace(File.separatorChar, '/') + "/Referencing",
            "jdbc:postgresql://host/database",
            "jdbc:odbc:EPSG"
        });
        url.setEditable(true);
        return new Field[] {
            new Field("URL",      Vocabulary.Keys.Url,      resources, url, null),
            new Field("schema",   Vocabulary.Keys.Schema,   resources, new JTextField(DEFAULT_SCHEMA), DEFAULT_SCHEMA),
            new Field("user",     Vocabulary.Keys.User,     resources, new JTextField(), null),
            new Field("password", Vocabulary.Keys.Password, resources, new JPasswordField(), null)
        };
    }

    /**
     * Returns an installer for the EPSG database. This method must be invoked from the
     * Swing thread. However the installer can (and should) be used from a background thread.
     *
     * @since 3.05
     */
    final EpsgInstaller installer() throws FactoryException {
        final Properties settings = getSettings();
        final EpsgInstaller install = new EpsgInstaller();
        if (!settings.isEmpty()) {
            install.setDatabase(settings.getProperty("URL"),
                                settings.getProperty("user"),
                                settings.getProperty("password"));
            install.setSchema(  settings.getProperty("schema"));
        }
        return install;
    }
}
