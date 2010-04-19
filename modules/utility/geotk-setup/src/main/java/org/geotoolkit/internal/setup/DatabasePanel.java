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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.text.JTextComponent;

import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;

import static java.awt.GridBagConstraints.*;


/**
 * The panel displaying a configuration form for the connection parameters to a database.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11 (derived from 3.00)
 * @module
 */
@SuppressWarnings("serial")
abstract class DatabasePanel extends JPanel implements ActionListener {
    /**
     * The user configuration file.
     */
    private static final String CONFIGURATION_FILE = "DataSource.properties";

    /**
     * {@link Installation#EPSG} or {@link Installation#COVERAGES} depending on
     * the database we are configuring.
     */
    private final Installation installation;

    /**
     * Button specifying whatever the embedded database should be used ("automatic")
     * or an other database explicitly given by the user ("manual"). The "automatic"
     * and "manual" modes are exclusive.
     */
    private final JRadioButton isAutomatic, isManual;

    /**
     * The list of all components and their labels used for explicit configuration
     * of the database connection. They will be enabled or disabled depending on
     * which radio button is selected.
     * <p>
     * The first element shall be a {@link JComboBox} with a list of URLs.
     */
    private final Field[] fields;

    /**
     * Describes a field (URL, username, password, etc.).
     */
    static final class Field {
        final String     propertyKey;
        final JLabel     label;
        final JComponent component;
        final String     defaultValue;

        Field(final String propertyKey, final int resourceKey, final Vocabulary resources,
                final JComponent component, final String defaultValue)
        {
            this.propertyKey  = propertyKey;
            this.label        = new JLabel(resources.getLabel(resourceKey));
            this.component    = component;
            this.defaultValue = defaultValue;
        }
    }

    /**
     * {@code true} if the combo box contains an URL for an explicit database.
     */
    private boolean hasExplicitURL;

    /**
     * The connection parameters as a map of properties, or {@code null} if the embedded
     * database is used instead of an explicit one.
     * <p>
     * <strong>WARNING:</strong> contains the password. However it should not be a sensible
     * password since it will be written uncrypted in a clear text file.
     */
    private Properties settings;

    /**
     * Creates the panel.
     *
     * @param resources The {@code Vocabulary} instance for the locale to use.
     * @param installation {@link Installation#EPSG} or {@link Installation#COVERAGES}
     *        depending on the database we are configuring.
     * @param hasAutoChoice {@code true} for providing a choice between embedded and explicit database.
     * @param fields The list of fields to provide.
     */
    DatabasePanel(final Vocabulary resources, final Installation installation,
            final boolean hasAutoChoice, final Field[] fields, final JButton applyButton)
    {
        super(new GridBagLayout());
        this.installation = installation;
        setBorder(BorderFactory.createEmptyBorder(12, 12, 6, 12));
        /*
         * Layouts the radio buttons for choosing whatever the user want the
         * automatic JavaDB embedded database or an explicit one.
         */
        GridBagConstraints c = new GridBagConstraints();
        c.insets.left=3; c.insets.right=3; c.anchor=FIRST_LINE_START;
        c.gridx=0; c.gridwidth=REMAINDER;
        if (hasAutoChoice) {
            final Descriptions descriptions = Descriptions.getResources(resources.getLocale());
            isAutomatic = new JRadioButton(descriptions.getString(Descriptions.Keys.USE_EOS_DATABASE_$1, 0));
            isManual    = new JRadioButton(descriptions.getString(Descriptions.Keys.USE_EOS_DATABASE_$1, 1));
            isAutomatic.addActionListener(this);
            isManual   .addActionListener(this);
            final ButtonGroup group = new ButtonGroup();
            group.add(isAutomatic);
            group.add(isManual);
            c.gridy=0; add(isAutomatic, c);
            c.gridy++; add(isManual, c);
        } else {
            c.gridy = -1;
            isAutomatic = null;
            isManual    = null;
        }
        /*
         * Layouts the fields for configuring explicitly the connection parameters.
         */
        c.fill = BOTH;
        c.gridwidth = 1;
        this.fields = fields;
        for (final Field field : fields) {
            c.gridy++;
            c.gridx=1; c.weightx=0; c.insets.left=30; add(field.label, c);
            c.gridx=2; c.weightx=1; c.insets.left= 3; add(field.component, c);
        }
        applyButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                save();
            }
        });
        addComponentListener(new LoadWhenShown());
    }

    /**
     * Invoked when the user change the "automatic" or "manual" mode. This method will enable
     * or disable the fields where are specified the connection parameters.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final boolean manual = isManual == null || isManual.isSelected();
        for (final Field field : fields) {
            field.label.setEnabled(manual);
            final JComponent c = field.component;
            c.setEnabled(manual);
            final String value;
            if (manual && settings != null) {
                value = settings.getProperty(field.propertyKey);
            } else {
                value = field.defaultValue;
            }
            if (c instanceof JComboBox) {
                ((JComboBox) c).setSelectedItem(value);
            } else {
                ((JTextComponent) c).setText(value);
            }
        }
        final JComboBox url = (JComboBox) fields[0].component;
        url.setSelectedIndex(manual || !hasExplicitURL ? 0 : 1);
    }

    /**
     * Loads the data only when first needed, which may never happen. We will load those
     * data only once. One advantage of this deferred loading mechanism is to popup the
     * error dialog box (if they was an I/O error) only if the user actually wanted to
     * see this widget.
     */
    private class LoadWhenShown extends ComponentAdapter {
        @Override public void componentShown(final ComponentEvent e) {
            removeComponentListener(this);
            if (settings == null) {
                load();
            }
        }
    }

    /**
     * If the properies file has not yet been read, reads it now.
     * Then, return its content.
     */
    final Properties getSettings() {
        for (final ComponentListener listener : getComponentListeners()) {
            if (listener instanceof LoadWhenShown) {
                listener.componentShown(null);
                break;
            }
        }
        return settings;
    }

    /**
     * Loads the settings from the {@value #CONFIGURATION_FILE} property file.
     * The values will be stored in the GUI fields. This method is invoked only once.
     */
    private void load() {
        final File configFile = new File(installation.directory(true), CONFIGURATION_FILE);
        boolean manual = false;
        if (configFile.isFile()) {
            settings = new Properties();
            try {
                final InputStream in = new FileInputStream(configFile);
                settings.load(in);
                in.close();
                manual = true;
            } catch (IOException ex) {
                error(Errors.Keys.CANT_READ_$1, ex);
                settings = null;
                return;
            }
            /*
             * If there is an URL in the property file, add it at the begining
             * of the URL combo box.
             */
            final Field urlField = fields[0];
            final String value = settings.getProperty(urlField.propertyKey);
            if (value != null) {
                final JComboBox url = (JComboBox) urlField.component;
                final DefaultComboBoxModel model = (DefaultComboBoxModel) url.getModel();
                if (!value.equals(model.getElementAt(0))) {
                    model.insertElementAt(value, 0);
                    hasExplicitURL = true;
                }
            }
        }
        final JRadioButton mode = manual ? isManual : isAutomatic;
        if (mode != null) {
            mode.setSelected(true);
        }
        actionPerformed(null); // Refresh the fields.
    }

    /**
     * Saves the values from the GUI fields to the {@value #CONFIGURATION_FILE} file,
     * or delete the file if the user selected the embedded database. This method is
     * invoked every time the "Apply" button has been pushed.
     */
    private void save() {
        final boolean manual = isManual == null || isManual.isSelected();
        if (manual) {
            if (settings == null) {
                settings = new Properties();
            }
            for (final Field field : fields) {
                final Object value;
                final JComponent c = field.component;
                if (c instanceof JComboBox) {
                    value = ((JComboBox) c).getSelectedItem();
                } else {
                    value = ((JTextComponent) c).getText();
                }
                setProperty(field.propertyKey, value);
            }
            if (!settings.isEmpty()) {
                try {
                    final File file = new File(installation.validDirectory(true), CONFIGURATION_FILE);
                    final FileOutputStream out = new FileOutputStream(file);
                    settings.store(out, "Connection parameters to the " + installation.name() + " database");
                    out.close();
                } catch (IOException ex) {
                    error(Errors.Keys.CANT_WRITE_$1, ex);
                }
                return;
            }
        }
        // Automatic mode.
        settings = null;
        final File file = new File(installation.directory(true), CONFIGURATION_FILE);
        file.delete();
    }

    /**
     * Adds the given (key, value) pair in the properties map,
     * providing that the value is not null and non-empty.
     */
    private void setProperty(final String key, final Object value) {
        if (value != null) {
            final String text = value.toString().trim();
            if (text.length() != 0) {
                settings.setProperty(key, text);
                return;
            }
        }
        settings.remove(key);
    }

    /**
     * Displays an error message.
     *
     * @param key Whatever we are reading of writing the file, as a resource key.
     */
    private void error(final int key, final IOException ex) {
        JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(),
                Errors.format(key, CONFIGURATION_FILE), JOptionPane.ERROR_MESSAGE);
    }
}
