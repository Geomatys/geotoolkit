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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
 * @version 3.16
 *
 * @since 3.11 (derived from 3.00)
 * @module
 */
@SuppressWarnings("serial")
abstract class DatabasePanel extends JComponent implements ActionListener {
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
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.11
     *
     * @since 3.11
     * @module
     */
    final class Field implements ActionListener, DocumentListener {
        final String     propertyKey;
        final JLabel     label;
        final JComponent component;
        final String     defaultValue;
        private String   originalValue;
        private boolean  isModified;

        /**
         * Creates a new {@code Field} instance with a label created from the given property key.
         */
        Field(final String propertyKey, final short resourceKey, final Vocabulary resources,
                final JComponent component, final String defaultValue)
        {
            this.propertyKey  = propertyKey;
            this.label        = new JLabel(resources.getLabel(resourceKey));
            this.component    = component;
            this.defaultValue = defaultValue;
            if (component instanceof JComboBox) {
                ((JComboBox) component).addActionListener(this);
            } else {
                ((JTextComponent) component).getDocument().addDocumentListener(this);
            }
            setOriginalValue(defaultValue);
        }

        /**
         * Sets the original value. This is used in order to detect if the
         * value edited by the user is different and needs to be saved.
         */
        final void setOriginalValue(String value) {
            if (value == null) {
                value = "";
            }
            originalValue = value;
            isModified = false;
        }

        /**
         * Returns the {@linkplain #component} text. The component can be a field or a combo box.
         */
        final Object getText() {
            final JComponent c = component;
            if (c instanceof JComboBox) {
                return ((JComboBox) c).getSelectedItem();
            }
            return ((JTextComponent) c).getText();
        }

        /**
         * Sets the {@linkplain #component} text. The component can be a field or a combo box.
         */
        final void setText(final String value) {
            final JComponent c = component;
            if (c instanceof JComboBox) {
                ((JComboBox) c).setSelectedItem(value);
            } else {
                ((JTextComponent) c).setText(value);
            }
        }

        /**
         * Invoked when the text changed. This method checks if the new user value is
         * different than the original value, and update the "Apply" button state concequently.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Object value = getText();
            if (value == null) {
                value = "";
            }
            boolean mod = !value.equals(originalValue);
            if (mod != isModified) {
                isModified = mod;
                if (mod) {
                    modificationCount++;
                } else {
                    modificationCount--;
                }
                refresh();
            }
        }

        @Override public void changedUpdate(DocumentEvent e) {actionPerformed(null);}
        @Override public void  insertUpdate(DocumentEvent e) {actionPerformed(null);}
        @Override public void  removeUpdate(DocumentEvent e) {actionPerformed(null);}
    }

    /**
     * The amount of fields having a value different than the default value.
     */
    int modificationCount;

    /**
     * {@code true} if the original file was defining the automatic mode.
     * This field is always {@code false} if there is no radio buttons for
     * the manual and automatic modes.
     *
     * @see #isAutomatic()
     */
    private boolean isOriginalAutomatic;

    /**
     * {@code true} if the combo box contains an URL for an explicit database.
     */
    private boolean hasExplicitURL;

    /**
     * The connection parameters as a map of properties, or {@code null} if they have not been
     * loaded yet. If the embedded database is used instead of an explicit one, then this map
     * is empty.
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
            final boolean hasAutoChoice, final JButton applyButton)
    {
        setLayout(new GridBagLayout());
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
            isAutomatic = new JRadioButton(descriptions.getString(Descriptions.Keys.USE_EOS_DATABASE_1, 0));
            isManual    = new JRadioButton(descriptions.getString(Descriptions.Keys.USE_EOS_DATABASE_1, 1));
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
        fields = getFields(resources);
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
     * Shall be invoked by subclasses for providing the database fields. This is invoked by
     * the constructor only. We can not let subclasses define their own static method because
     * the {@link Field} constructor needs access to {@code this}.
     */
    abstract Field[] getFields(Vocabulary resources);

    /**
     * Returns {@code true} if this form has some fields which need to be saved.
     */
    final boolean hasModifications() {
        if (settings == null) {
            return false;
        }
        final boolean isAutomatic = isAutomatic();
        if (isAutomatic != isOriginalAutomatic) {
            return true;
        }
        return !isAutomatic && modificationCount != 0;
    }

    /**
     * Returns {@code true} if the "automatic" button radio is selected. This method returns
     * always {@code false} if there is no automatic/manual button radio.
     */
    private boolean isAutomatic() {
        return isAutomatic != null && isAutomatic.isSelected();
    }

    /**
     * Invoked when the user change the "automatic" or "manual" mode. This method will enable
     * or disable the fields where are specified the connection parameters.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final boolean manual = !isAutomatic();
        for (final Field field : fields) {
            field.label.setEnabled(manual);
            field.component.setEnabled(manual);
            final String value;
            if (manual) {
                value = settings.getProperty(field.propertyKey);
            } else {
                value = field.defaultValue;
            }
            field.setText(value);
        }
        final JComboBox<?> url = (JComboBox<?>) fields[0].component;
        url.setSelectedIndex(manual || !hasExplicitURL ? 0 : 1);
        refresh();
    }

    /**
     * Refresh the state of the "Apply" button.
     */
    final void refresh() {
        ((DatabasePanels) getParent().getParent()).refresh();
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
     * If the properties file has not yet been read, reads it now.
     * Then, return its content (never {@code null}).
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
        Properties settings;
        try {
            settings = installation.getDataSource();
        } catch (IOException ex) {
            error(Errors.Keys.CANT_READ_FILE_1, ex);
            return;
        }
        final boolean manual = (settings != null);
        if (!manual) {
            settings = new Properties();
            for (final Field field : fields) {
                final String defaultValue = field.defaultValue;
                if (defaultValue != null) {
                    settings.setProperty(field.propertyKey, defaultValue);
                }
            }
        } else {
            /*
             * If there is an URL in the property file, add it at the beginning
             * of the URL combo box.
             */
            final Field urlField = fields[0];
            final String value = settings.getProperty(urlField.propertyKey);
            if (value != null) {
                @SuppressWarnings("unchecked")
                final JComboBox<String> url = (JComboBox<String>) urlField.component;
                final DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) url.getModel();
                if (!value.equals(model.getElementAt(0))) {
                    model.insertElementAt(value, 0);
                    hasExplicitURL = true;
                }
            }
            /*
             * Remember the original values. This will be used in order to detect
             * if a value has been edited, and consequently if we should enable or
             * disable the "Apply" button.
             */
            for (final Field field : fields) {
                field.setOriginalValue(settings.getProperty(field.propertyKey));
            }
        }
        this.settings = settings; // Set only on success.
        final JRadioButton mode = manual ? isManual : isAutomatic;
        if (mode != null) {
            mode.setSelected(true);
            isOriginalAutomatic = !manual;
        }
        actionPerformed(null); // Refresh the fields.
    }

    /**
     * Saves the values from the GUI fields to the {@value #CONFIGURATION_FILE} file,
     * or delete the file if the user selected the embedded database. This method is
     * invoked every time the "Apply" button has been pushed.
     */
    private void save() {
        if (!hasModifications()) {
            // Does not save the file if there is no modification, in order to preserve the
            // file date and time information. Also in order to preserve any manual edition
            // that the user could have done in the file.
            return;
        }
        final Properties settings = this.settings;
        if (isAutomatic()) {
            final File file = new File(installation.directory(true), Installation.DATASOURCE_FILE);
            file.delete();
            settings.clear();
            if (isAutomatic != null) {
                isOriginalAutomatic = true;
                // Shall stay 'false' if there is no automatic/manual radio buttons.
            }
        } else {
            for (final Field field : fields) {
                setProperty(settings, field.propertyKey, field.getText());
            }
            if (!settings.isEmpty()) {
                // Get the name to put in the property file comment line.
                String name = installation.name();
                for (final String id : DatabasePanels.CONNECTION_PANELS) {
                    if (id.equalsIgnoreCase(name)) {
                        name = id;
                        break;
                    }
                }
                try {
                    final File file = new File(installation.validDirectory(true), Installation.DATASOURCE_FILE);
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        settings.store(out, "Connection parameters to the " + name + " database");
                    }
                } catch (IOException ex) {
                    error(Errors.Keys.CANT_WRITE_FILE_1, ex);
                    return;
                }
            }
            isOriginalAutomatic = false;
        }
        // Remember the new field values only on success.
        for (final Field field : fields) {
            field.setOriginalValue(settings.getProperty(field.propertyKey));
        }
        modificationCount = 0;
        refresh();
    }

    /**
     * Adds the given (key, value) pair in the properties map,
     * providing that the value is not null and non-empty.
     */
    private static void setProperty(final Properties settings, final String key, final Object value) {
        if (value != null) {
            final String text = value.toString().trim();
            if (!text.isEmpty()) {
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
    private void error(final short key, final IOException ex) {
        JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(),
                Errors.format(key, Installation.DATASOURCE_FILE), JOptionPane.ERROR_MESSAGE);
    }
}
