/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.internal.setup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.referencing.factory.epsg.EpsgInstaller;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;

import static java.awt.GridBagConstraints.*;


/**
 * The panel displaying a configuration form for the connection parameters to the current
 * EPSG database. Geotoolkit can use either the embedded JavaDB database or an explicit
 * one selected by this form.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
final class EPSGPanel extends JPanel implements ActionListener {
    /**
     * The user configuration file.
     */
    private static final String CONFIGURATION_FILE = "DataSource.properties";

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
     */
    private final JComponent[] manualConfig;

    /**
     * The URL to the database. The combo box contains some proposed defaults, with
     * the first proposal being the value read from the {@value #CONFIGURATION_FILE}
     * file if it exists.
     */
    private final JComboBox url;

    /**
     * {@code true} if the combo box contains an URL for an explicit database.
     */
    private boolean hasExplicitURL;

    /**
     * The schema, or a null text if there is none.
     */
    private final JTextField schema;

    /**
     * The user, or a null text if there is none.
     */
    private final JTextField user;

    /**
     * The password, or a null text if there is none.
     */
    private final JPasswordField password;

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
     */
    EPSGPanel(final Vocabulary resources) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        /*
         * Creates the widget components.
         */
        isAutomatic = new JRadioButton(Descriptions.format(Descriptions.Keys.USE_EPSG_DATABASE_$1, 0));
        isManual    = new JRadioButton(Descriptions.format(Descriptions.Keys.USE_EPSG_DATABASE_$1, 1));
        final ButtonGroup group = new ButtonGroup();
        group.add(isAutomatic);
        group.add(isManual);

        url = new JComboBox(new String[] {
            ThreadedEpsgFactory.getDefaultURL(),
            "jdbc:derby:" + System.getProperty("user.home", "").replace(File.separatorChar, '/') + "/Referencing",
            "jdbc:postgresql://localhost:5432/Referencing",
            "jdbc:odbc:EPSG"
        });
        url.setEditable(true);
        schema   = new JTextField();
        user     = new JTextField();
        password = new JPasswordField();
        /*
         * Layout the radio buttons for choosing whatever the user want the
         * automatic JavaDB embedded database or an explicit one.
         */
        GridBagConstraints c = new GridBagConstraints();
        c.insets.left=3; c.insets.right=3; c.anchor=FIRST_LINE_START;
        c.gridx=0; c.gridwidth=REMAINDER;
        c.gridy=0; add(isAutomatic, c);
        c.gridy++; add(isManual, c);
        /*
         * Layout the fields for configuring explicitly the connection parameters.
         */
        c.fill=BOTH;
        c.gridwidth=1;
        manualConfig = new JComponent[8]; // Must be twice the upper limit of i below.
        for (int i=0,j=0; i<4; i++) {
            final int key;
            final JComponent field;
            switch (i) {
                case 0: key = Vocabulary.Keys.URL;      field = url;      break;
                case 1: key = Vocabulary.Keys.SCHEMA;   field = schema;   break;
                case 2: key = Vocabulary.Keys.USER;     field = user;     break;
                case 3: key = Vocabulary.Keys.PASSWORD; field = password; break;
                default: throw new AssertionError(i);
            }
            final JLabel label = new JLabel(resources.getLabel(key));
            c.gridy++;
            c.gridx=1; c.weightx=0; c.insets.left=30; add(label, c);
            c.gridx=2; c.weightx=1; c.insets.left= 3; add(field, c);
            manualConfig[j++] = label;
            manualConfig[j++] = field;
        }
        /*
         * Adds the "Apply" button.
         */
        final JButton apply = new JButton(resources.getString(Vocabulary.Keys.APPLY));
        c.gridx=2; c.gridy++;
        c.fill=NONE; c.anchor=EAST;
        add(apply,c);
        apply.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                save();
            }
        });
        isAutomatic.addActionListener(this);
        isManual   .addActionListener(this);
        /*
         * Loads the data only when first needed, which may never happen. We will load those
         * data only once. One advantage of this deferred loading mechanism is to popup the
         * error dialog box (if they was an I/O error) only if the user actually wanted to
         * see this widget.
         */
        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(final ComponentEvent e) {
                removeComponentListener(this);
                load();
            }
        });
    }

    /**
     * Invoked when the user change the "automatic" or "manual" mode. This method will enable
     * or disable the fields where are specified the connection parameters.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final boolean manual = isManual.isSelected();
        for (final JComponent c : manualConfig) {
            c.setEnabled(manual);
        }
        if (manual && settings != null) {
            url     .setSelectedIndex(0);
            schema  .setText(settings.getProperty("schema"));
            user    .setText(settings.getProperty("user"));
            password.setText(settings.getProperty("password"));
        } else {
            url     .setSelectedIndex(hasExplicitURL ? 1 : 0);
            schema  .setText(EpsgInstaller.SCHEMA);
            user    .setText(null);
            password.setText(null);
        }
    }

    /**
     * Loads the settings from the {@value #CONFIGURATION_FILE} property file.
     * The values will be stored in the GUI fields. This method is invoked only once.
     */
    private void load() {
        final File configFile = new File(Installation.EPSG.directory(true), CONFIGURATION_FILE);
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
            final String value = settings.getProperty("URL");
            if (value != null) {
                final DefaultComboBoxModel model = (DefaultComboBoxModel) url.getModel();
                if (!value.equals(model.getElementAt(0))) {
                    model.insertElementAt(value, 0);
                    hasExplicitURL = true;
                }
            }
        }
        (manual ? isManual : isAutomatic).setSelected(true);
        actionPerformed(null); // Refresh the fields.
    }

    /**
     * Saves the values from the GUI fields to the {@value #CONFIGURATION_FILE} file,
     * or delete the file if the user selected the embedded database. This method is
     * invoked every time the "Apply" button has been pushed.
     */
    private void save() {
        final boolean manual = isManual.isSelected();
        if (manual) {
            if (settings == null) {
                settings = new Properties();
            }
            setProperty("URL",      url.getSelectedItem());
            setProperty("user",     user.getText());
            setProperty("password", String.valueOf(password.getPassword()));
            setProperty("schema",   schema.getText());
            try {
                final File file = new File(Installation.EPSG.validDirectory(true), CONFIGURATION_FILE);
                final FileOutputStream out = new FileOutputStream(file);
                settings.store(out, "Connection parameters to the EPSG database");
                out.close();
            } catch (IOException ex) {
                error(Errors.Keys.CANT_WRITE_$1, ex);
            }
        } else {
            settings = null;
            final File file = new File(Installation.EPSG.directory(true), CONFIGURATION_FILE);
            file.delete();
        }
    }

    /**
     * Adds the given (key, value) pair in the properties map,
     * providing that the value is not null.
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
