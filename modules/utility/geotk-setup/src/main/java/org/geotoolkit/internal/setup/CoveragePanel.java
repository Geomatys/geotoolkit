/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.util.Arrays;
import java.util.TimeZone;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.internal.sql.table.ConfigurationKey.*;


/**
 * The panel displaying a configuration form for the connection parameters to the coverages
 * database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@SuppressWarnings("serial")
final class CoveragePanel extends DatabasePanel {
    /**
     * Creates the panel.
     */
    CoveragePanel(final Vocabulary resources, final JButton applyButton) {
        super(resources, Installation.COVERAGES, false, applyButton);
    }

    /**
     * Provides the database fields.
     */
    @Override
    Field[] getFields(final Vocabulary resources) {
        final JComboBox<String> url = new JComboBox<>(new String[] {
            "jdbc:postgresql://host/database",
            "jdbc:odbc:Coverages"
        });
        url.setEditable(true);
        final String[] tz = TimeZone.getAvailableIDs();
        Arrays.sort(tz);
        return new Field[] {
            new Field(URL.key,            Vocabulary.Keys.URL,                  resources, url, null),
            new Field(SCHEMA.key,         Vocabulary.Keys.SCHEMA,               resources, new JTextField(),     SCHEMA.defaultValue),
            new Field(USER.key,           Vocabulary.Keys.USER,                 resources, new JTextField(),     USER.defaultValue),
            new Field(PASSWORD.key,       Vocabulary.Keys.PASSWORD,             resources, new JPasswordField(), PASSWORD.defaultValue),
            new Field(TIMEZONE.key,       Vocabulary.Keys.TIME_ZONE,            resources, new JComboBox<>(tz),  TIMEZONE.defaultValue),
            new Field(ROOT_DIRECTORY.key, Vocabulary.Keys.IMAGE_ROOT_DIRECTORY, resources, new JTextField(),     ROOT_DIRECTORY.defaultValue),
        };
    }
}
