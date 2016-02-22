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
package org.geotoolkit.internal.wizard;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.sql.Connection;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import javax.swing.JLabel;
import javax.swing.JComponent;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.JDBCLoginService;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;

import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.internal.swing.ExceptionMonitor;
import org.geotoolkit.internal.sql.table.ConfigurationKey;
import org.geotoolkit.internal.sql.CoverageDatabaseInstaller;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Wizards;
import org.apache.sis.util.Classes;
import org.opengis.util.FactoryException;


/**
 * The object that create a coverage database {@link CoverageDatabaseWizard}
 * finished to collect all information.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
final class CoverageDatabaseCreator extends DeferredWizardResult implements Runnable {
    /**
     * The wizard which created this object.
     * Used in order to fetch user parameters.
     */
    private final CoverageDatabaseWizard wizard;

    /**
     * The JDBC loging pane, used in order to get connection to use for the installation.
     * It shall use a {@link JDBCLoginService}.
     */
    private final JXLoginPane login;

    /**
     * The schema currently in process of being created.
     */
    private transient String currentSchema;

    /**
     * The pane which list the schemas to install.
     */
    private transient Container schemaPane;

    /**
     * Creates a new {@code CoverageDatabaseCreator} which will use the given connection.
     * That connection will be closed by this object.
     *
     * @param wizard The wizard which created this object.
     * @param login  The JDBC loging pane, which shall use a {@link JDBCLoginService}.
     */
    CoverageDatabaseCreator(final CoverageDatabaseWizard wizard, final JXLoginPane login) {
        this.wizard = wizard;
        this.login  = login;
    }

    /**
     * Returns the logging service.
     */
    private JDBCLoginService getLoggingService() {
        return (JDBCLoginService) login.getLoginService();
    }

    /**
     * Invoked in the Swing thread for reporting the schema in process of being created.
     * This method is public as an implementation side-effect; do not invoke.
     */
    @Override
    public synchronized void run() {
        final String[] schemas = wizard.schemas;
        final Component[] components = schemaPane.getComponents();
        for (int i=Math.min(components.length, schemas.length); --i>=0;) {
            final JComponent component = (JComponent) components[i];
            if (schemas[i].equals(currentSchema)) {
                component.setFont(component.getFont().deriveFont(Font.BOLD));
                component.setForeground(Color.WHITE);
                component.setBackground(Color.BLUE);
                component.setOpaque(true);
            } else {
                component.setFont(null);
                component.setForeground(null);
                component.setBackground(null);
                component.setOpaque(false);
            }
        }
    }

    /**
     * Performs the creation of the coverage database.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void start(final Map settings, final ResultProgressHandle progress) {
        CoverageDatabaseInstaller installer = null;
        try {
            try (Connection connection = getLoggingService().getConnection()) {
                installer = new CoverageDatabaseInstaller(connection) {
                    @Override protected void progress(int percent, String schema) {
                        progress.setProgress(percent, 100);
                        synchronized (CoverageDatabaseCreator.this) {
                            currentSchema = schema;
                            schemaPane = (Container) settings.get(CoverageDatabaseWizard.CONFIRM);
                        }
                        EventQueue.invokeLater(CoverageDatabaseCreator.this);
                    }
                };
                installer.postgisDir  = wizard.postgis.getSelectedFile();
                installer.createRoles = wizard.createRoles.isSelected();
                installer.createEPSG  = wizard.createEPSG.isSelected();
                installer.schema      = wizard.schema.getText();
                installer.admin       = wizard.admin.getText();
                installer.user        = wizard.user.getText();
                installer.install();
                installer.close(true);
            }
            if (wizard.setAsDefaultEPSG.isSelected() && wizard.createEPSG.isSelected()) {
                saveSettings(true);
            }
            if (wizard.setAsDefault.isSelected()) {
                saveSettings(false);
            }
        } catch (IOException | SQLException | FactoryException exception) {
            String message = exception.getLocalizedMessage();
            if (message == null) {
                message = Classes.getShortClassName(exception);
            }
            if (installer != null) {
                message = Vocabulary.format(Vocabulary.Keys.Error_1,
                        installer.getCurrentPosition()) + ": " + message;
            }
            progress.failed(message, false);
            ExceptionMonitor.show((Component) settings.get(CoverageDatabaseWizard.CONFIRM), exception, message);
            return;
        }
        Object result = null;
        result = Summary.create(new JLabel(Wizards.format(Wizards.Keys.CoverageDatabaseCreated)), result);
        progress.finished(result);
    }

    /**
     * Saves the configuration for the connection to the EPSG database or Coverages database.
     *
     * @param epsg {@code true} for saving the configuration about the EPSG database, or
     *        {@code false} for the configuration about the coverage database.
     * @throws IOException If the configuration can not be saved.
     */
    private void saveSettings(final boolean epsg) throws IOException {
        final Properties properties = new Properties();
        setProperty(properties, ConfigurationKey.URL,      getLoggingService().getUrl());
        setProperty(properties, ConfigurationKey.USER,     login.getUserName());
        setProperty(properties, ConfigurationKey.PASSWORD, new String(login.getPassword()));
        setProperty(properties, ConfigurationKey.SCHEMA,   epsg ? "EPSG" : wizard.schema.getText());
        Path file = (epsg ? Installation.EPSG : Installation.COVERAGES).validDirectory(true);
        file = file.resolve(Installation.DATASOURCE_FILE);
        try (OutputStream out = Files.newOutputStream(file)) {
            properties.store(out, "Connection parameters from the installer.");
        }
    }

    /**
     * Sets a value in the given properties map, provided that the value is non-null and not empty.
     */
    private static void setProperty(final Properties properties, final ConfigurationKey key, String value) {
        if (value != null) {
            value = value.trim();
            if (!value.isEmpty()) {
                properties.setProperty(key.key, value);
            }
        }
    }
}
