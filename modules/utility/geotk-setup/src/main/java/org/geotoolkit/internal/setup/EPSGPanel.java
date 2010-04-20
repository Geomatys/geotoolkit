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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.opengis.referencing.FactoryException;

import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.referencing.factory.epsg.EpsgInstaller;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.resources.Vocabulary;


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
        applyButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                dataPanel.refresh(DataPanel.EPSG);
            }
        });
    }

    /**
     * Provides the database fields.
     */
    @Override
    Field[] getFields(final Vocabulary resources) {
        final JComboBox url = new JComboBox(new String[] {
            ThreadedEpsgFactory.getDefaultURL(),
            "jdbc:derby:" + System.getProperty("user.home", "").replace(File.separatorChar, '/') + "/Referencing",
            "jdbc:postgresql://host/database",
            "jdbc:odbc:EPSG"
        });
        url.setEditable(true);
        return new Field[] {
            new Field("URL",      Vocabulary.Keys.URL,      resources, url, null),
            new Field("schema",   Vocabulary.Keys.SCHEMA,   resources, new JTextField(), EpsgInstaller.DEFAULT_SCHEMA),
            new Field("user",     Vocabulary.Keys.USER,     resources, new JTextField(), null),
            new Field("password", Vocabulary.Keys.PASSWORD, resources, new JPasswordField(), null)
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
