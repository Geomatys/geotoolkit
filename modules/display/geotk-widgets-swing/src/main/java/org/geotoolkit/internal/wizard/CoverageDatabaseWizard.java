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

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.Map;
import java.util.Locale;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.swing.text.NumberFormatter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.JDBCLoginService;
import org.netbeans.spi.wizard.WizardController;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Widgets;
import org.geotoolkit.resources.Wizards;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Descriptions;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.io.Host;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.internal.sql.PostgisInstaller;
import org.geotoolkit.coverage.sql.CoverageDatabase;
import org.geotoolkit.internal.sql.CoverageDatabaseInstaller;
import org.geotoolkit.internal.sql.Dialect;
import org.geotoolkit.internal.swing.DocumentChangeListener;
import org.geotoolkit.referencing.factory.epsg.EpsgInstaller;


/**
 * Guides the user through the steps of creating a coverage database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
public final class CoverageDatabaseWizard extends AbstractWizard {
    /**
     * The preference key for the PostGIS directory.
     */
    private static final String POSTGIS_PREFS = "PostGIS directory";

    /**
     * The default size for content panes.
     */
    private static final Dimension SIZE = new Dimension(600, 400);

    /**
     * The ID of the chooser for entering all required informations.
     */
    static final String CONNECTION="CONNECTION", POSTGIS="POSTGIS", CONFIGURE="CONFIGURE", CONFIRM="CONFIRM";

    /**
     * The field for selecting the database engine.
     */
    private JComboBox<String> engine;

    /**
     * Fields to be provided by users.
     */
    JTextField server, database, schema, admin, user;

    /**
     * Fields to be provided by users.
     */
    private JFormattedTextField port;

    /**
     * The directory which contains PostGIS files.
     */
    JFileChooser postgis;

    /**
     * Choices to be provided by users.
     */
    JCheckBox createRoles, createEPSG, setAsDefault, setAsDefaultEPSG;

    /**
     * The schemas to create. They are listed in the summary panel before to start
     * the creation of the database.
     */
    String[] schemas;

    /**
     * Creates a new wizard.
     */
    public CoverageDatabaseWizard() {
        super(Wizards.format(Wizards.Keys.COVERAGE_DATABASE_TITLE), new String[] {
            CONNECTION,
            POSTGIS,
            CONFIGURE,
            CONFIRM
        }, new String[] {
            Vocabulary.format(Vocabulary.Keys.CONNECTION_PARAMETERS),
            Vocabulary.format(Vocabulary.Keys.SPATIAL_OBJECTS),
            Vocabulary.format(Vocabulary.Keys.CONFIGURE),
            Vocabulary.format(Vocabulary.Keys.CONFIRM)
        });
    }

    /**
     * Creates a panel that represents a named step in the wizard.
     *
     * @param controller The object which controls whether the Next/Finish buttons in the wizard are enabled.
     * @param id         The name of the step, one of the array of steps passed in the constructor.
     * @param settings   A Map containing settings from earlier steps in the wizard.
     * @return The component that should be displayed in the center of the wizard.
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected JComponent createPanel(final WizardController controller, final String id, final Map settings) {
        final Locale     locale    = Locale.getDefault(Locale.Category.DISPLAY);
        final Vocabulary resources = Vocabulary.getResources(locale);
        final Wizards    wizardRes = Wizards.getResources(locale);
        final JComponent panel;
        switch (id) {
            // -------------------------------------------------------------------
            //     Panel 1:  Connection parameters
            // -------------------------------------------------------------------
            case CONNECTION: {
                panel = new JPanel(new GridBagLayout());
                final GridBagConstraints c = new GridBagConstraints();
                c.gridy  = 0;
                c.anchor = GridBagConstraints.WEST;
                engine   = new JComboBox<>(new String[] {"PostgreSQL"});
                server   = new JTextField();
                port     = new JFormattedTextField(new NumberFormatter());
                database = new JTextField();
                schema   = new JTextField(CoverageDatabaseInstaller.SCHEMA);
                admin    = new JTextField(CoverageDatabaseInstaller.ADMINISTRATOR);
                user     = new JTextField(CoverageDatabaseInstaller.USER);
                add(panel, resources, Vocabulary.Keys.DATABASE_ENGINE, engine,   c);
                add(panel, resources, Vocabulary.Keys.SERVER,          server,   c);
                add(panel, resources, Vocabulary.Keys.PORT,            port,     c);
                add(panel, resources, Vocabulary.Keys.DATABASE,        database, c);
                add(panel, resources, Vocabulary.Keys.SCHEMA,          schema,   c);
                add(panel, resources, Vocabulary.Keys.ADMINISTRATOR,   admin ,   c);
                add(panel, resources, Vocabulary.Keys.USER,            user,     c);
                final String problem = wizardRes.getString(Wizards.Keys.DATABASE_REQUIRED);
                final DocumentChangeListener listener = new DocumentChangeListener() {
                    /** The set of documents having a non-blank text value. */
                    private final Set<Document> hasText = Collections.newSetFromMap(new IdentityHashMap<Document,Boolean>());

                    @Override protected void textChanged(final Document document, final String text) {
                        if (!text.trim().isEmpty() ? hasText.add(document) : hasText.remove(document)) {
                            final int size = 2 - hasText.size(); // 2 is the amount of fields having this listener.
                            assert size >= 0 : size;
                            controller.setProblem(size == 0 ? null : problem);
                        }
                    }
                };
                server  .getDocument().addDocumentListener(listener);
                database.getDocument().addDocumentListener(listener);
                controller.setProblem(problem);
                try {
                    // Initial values, which must be set after the listeners.
                    final Host host = new Host(Installation.COVERAGES.getDataSource(), null);
                    if (host.host != null) server.setText(host.host);
                    if (host.port != null) port.setValue(host.port);
                } catch (IOException e) {
                    Logging.recoverableException(Logging.getLogger(CoverageDatabase.class),
                            CoverageDatabaseWizard.class, "createPanel", e);
                }
                addSetting(settings, CONNECTION, panel);
                break;
            }
            // -------------------------------------------------------------------
            //     Panel 2:  Spatial objects
            // -------------------------------------------------------------------
            case POSTGIS: {
                final JXLabel desc = new JXLabel(wizardRes.getString(Wizards.Keys.POSTGIS_DIRECTORY));
                desc.setLineWrap(true);
                panel = new JPanel(new BorderLayout());
                panel.add(desc, BorderLayout.BEFORE_FIRST_LINE);
                postgis = new JFileChooser();
                postgis.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                postgis.setControlButtonsAreShown(false);
                postgis.setMultiSelectionEnabled(false);
                postgis.setAcceptAllFileFilterUsed(false);
                postgis.addChoosableFileFilter(new FileNameExtensionFilter(resources.getString(Vocabulary.Keys.FILES_1, "SQL"), "sql"));
                postgis.addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, new PropertyChangeListener() {
                    @Override public void propertyChange(final PropertyChangeEvent event) {
                        String missing = PostgisInstaller.INSTALL;
                        final File directory = (File) event.getNewValue();
                        if (directory != null) {
                            if (new File(directory, missing).isFile() ||
                                new File(directory, PostgisInstaller.LEGACY).isFile())
                            {
                                missing = PostgisInstaller.REF_SYS;
                                if (new File(directory, missing).isFile()) {
                                    missing = null;
                                }
                            }
                        }
                        if (missing != null) {
                            missing = Errors.format(Errors.Keys.FILE_DOES_NOT_EXIST_1, missing);
                        }
                        controller.setProblem(missing);
                    }
                });
                controller.setProblem(Widgets.format(Widgets.Keys.SELECT_DIRECTORY));
                final File directory = new File(preferences().get(POSTGIS_PREFS, System.getProperty("user.dir", ".")));
                postgis.setCurrentDirectory(directory.getParentFile());
                panel.add(postgis, BorderLayout.CENTER);
                addSetting(settings, POSTGIS, panel);
                break;
            }
            // -------------------------------------------------------------------
            //     Panel 3:  Configure whatever we want roles, EPSG database, etc.
            // -------------------------------------------------------------------
            case CONFIGURE: {
                createRoles      = new JCheckBox(wizardRes.getString(Wizards.Keys.CREATE_ROLES_2, admin.getText(), user.getText()), true);
                createEPSG       = new JCheckBox(wizardRes.getString(Wizards.Keys.CREATE_EPSG), true);
                setAsDefaultEPSG = new JCheckBox(wizardRes.getString(Wizards.Keys.SET_AS_DEFAULT_1, "EPSG"));
                setAsDefault     = new JCheckBox(wizardRes.getString(Wizards.Keys.SET_AS_DEFAULT_1, "Coverages"));
                createEPSG.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(final ActionEvent event) {
                        setAsDefaultEPSG.setEnabled(createEPSG.isSelected());
                    }
                });
                // The warning will be made visible or hidden using the 'setForeground' method.
                // We do not use the 'setVisible(boolean)' method because we want to keep the
                // layout unchanged.
                final JLabel passwordWarning = new JLabel(Descriptions.getResources(locale)
                        .getString(Descriptions.Keys.PASSWORD_NOT_ENCRYPTED));
                passwordWarning.setForeground(passwordWarning.getBackground());
                final ActionListener listener = new ActionListener() {
                    @Override public void actionPerformed(final ActionEvent event) {
                        final boolean enabled = setAsDefault.isSelected() ||
                                (createEPSG.isSelected() && setAsDefaultEPSG.isSelected());
                        passwordWarning.setForeground(enabled ? Color.RED : passwordWarning.getBackground());
                    }
                };
                createEPSG      .addActionListener(listener);
                setAsDefault    .addActionListener(listener);
                setAsDefaultEPSG.addActionListener(listener);
                final JPanel choices = new JPanel(new GridBagLayout());
                final GridBagConstraints c = new GridBagConstraints();
                c.anchor=GridBagConstraints.WEST;
                c.gridy=0;
                c.gridx=0;
                choices.add(createRoles, c);
                c.gridy++;
                choices.add(createEPSG, c);
                c.gridy++;
                c.insets.left=30;
                choices.add(setAsDefaultEPSG, c);
                c.gridy++;
                c.insets.left= 0;
                choices.add(setAsDefault, c);
                c.gridy++;
                c.insets.top =15;
                choices.add(passwordWarning, c);
                panel = new JPanel(new BorderLayout());
                panel.add(choices, BorderLayout.CENTER);
                panel.add(new JLabel(wizardRes.getString(Wizards.Keys.COVERAGE_DATABASE_NOTES_1,
                        server.getText())), BorderLayout.PAGE_END);
                break;
            }
            // -------------------------------------------------------------------
            //     Panel 4:  Confirm
            // -------------------------------------------------------------------
            case CONFIRM: {
                String schema = this.schema.getText();
                if (schema == null || ((schema = schema.trim()).length()) == 0) {
                    schema = CoverageDatabaseInstaller.SCHEMA;
                }
                schemas = new String[] {
                    PostgisInstaller.DEFAULT_SCHEMA,
                    EpsgInstaller.DEFAULT_SCHEMA,
                    CoverageDatabaseInstaller.METADATA_SCHEMA,
                    schema
                };
                // NOTE: CoverageDatabaseCreator expect the label to be added directly
                // to the pane. Shall not be a pane included in an other pane.
                panel = new JPanel(new GridBagLayout());
                final GridBagConstraints c = new GridBagConstraints();
                c.gridx=0;
                c.gridy=0;
                c.anchor=GridBagConstraints.LINE_START;
                c.insets.top = c.insets.bottom = 6;
                for (int i=0; i<schemas.length; i++) {
                    panel.add(new JLabel(wizardRes.getString(Wizards.Keys.CREATING_SCHEMA_1, schemas[i])), c);
                    c.gridy++;
                }
                addSetting(settings, CONFIRM, panel);
                break;
            }
            default: {
                throw new IllegalArgumentException(id); // Should never happen.
            }
        }
        panel.setPreferredSize(SIZE);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 15, 9, 15));
        return panel;
    }

    /**
     * Adds a field and its label to the given panel.
     */
    private static void add(JComponent panel, JLabel label, JComponent field, GridBagConstraints c) {
        label.setLabelFor(field);
        c.gridx=0; c.weightx=0; c.fill=GridBagConstraints.NONE; panel.add(label, c);
        c.gridx=1; c.weightx=1; c.fill=GridBagConstraints.BOTH; panel.add(field, c);
        c.gridy++;
    }

    /**
     * Adds a field and its label to the given panel.
     */
    private static void add(JComponent panel, Vocabulary resources, short key, JComponent field, GridBagConstraints c) {
        add(panel, new JLabel(resources.getLabel(key)), field, c);
    }

    /**
     * Invoked when the user finished to go through wizard steps.
     *
     * @param  settings The settings provided by the user.
     * @return The object which will create the database.
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected Object finish(final Map settings) {
        /*
         * Fetch now the information that we want to save in user preferences.
         * This way, if we fail later in this method, the user setting will be remembered.
         */
        preferences().put(POSTGIS_PREFS, postgis.getSelectedFile().toString());
        /*
         * Construct the JDBC URL, with the optional port number.
         */
        final StringBuilder buffer = new StringBuilder("jdbc:postgresql://").append(server.getText());
        final Number port = (Number) this.port.getValue();
        if (port != null) {
            buffer.append(':').append(port);
        }
        final String url = buffer.append('/').append(database.getText()).toString();
        /*
         * Show the logging panel now.
         */
        final JXLoginPane login = new JXLoginPane(new JDBCLoginService(Dialect.POSTGRESQL.driverClass, url));
        login.setUserName(admin.getText());
        login.setPreferredSize(new Dimension(400, 300));
        switch (JXLoginPane.showLoginDialog((JComponent) settings.get(CONFIRM), login)) {
            case SUCCEEDED: {
                return new CoverageDatabaseCreator(this, login);
            }
        }
        return null; // User cancelled the operation.
    }
}
