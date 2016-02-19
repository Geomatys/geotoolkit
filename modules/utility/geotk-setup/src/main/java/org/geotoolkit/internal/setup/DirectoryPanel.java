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

import java.io.File;
import java.io.Serializable;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.io.Installation;

import static java.awt.GridBagConstraints.*;


/**
 * The panel for setting directories. Values are read from and stored in preferences.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
final class DirectoryPanel extends JComponent {
    /**
     * An item in the list of directories that can be setup.
     */
    @SuppressWarnings("serial")
    private final class Item implements ActionListener, FocusListener, Serializable {
        /**
         * The mode index.
         */
        private static final int AUTO=0, USER=1, SYSTEM=2;

        /**
         * Specifies if the directory should be determined automatically,
         * specified for current user or specified for all users.
         */
        private final JComboBox<String> mode;

        /**
         * The value specified by the user. This field is disabled if the currently
         * selected {@linkplain #mode} is "automatic".
         */
        private final JTextField directory;

        /**
         * The button for displaying the "Open" dialog box, for browsing to a local directory.
         */
        private final JButton open;

        /**
         * The {@linkplain #directory} value when the {@linkplain #mode}
         * is "automatic" or "Current/All users".
         */
        private String automatic, supplied;

        /**
         * The currently selected {@linkplain #mode}.
         */
        private int currentMode;

        /**
         * The configuration for which this item is created.
         */
        private final Installation config;

        /**
         * Creates a new item with the given label.
         */
        Item(final GridBagConstraints c, final String text, final Installation config) {
            this.config = config;
            final Vocabulary resources = data.resources;
            final String[] modes = new String[3];
            modes[AUTO]   = resources.getString(Vocabulary.Keys.Automatic);
            modes[USER]   = resources.getString(Vocabulary.Keys.CurrentUser);
            modes[SYSTEM] = resources.getString(Vocabulary.Keys.AllUsers);
            mode = new JComboBox<>(modes);
            directory = new JTextField();
            directory.addFocusListener(this);
            open = new JButton(openIcon);
            open.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent event) {
                    final JFileChooser chooser = new JFileChooser(new File(directory.getText()));
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (chooser.showOpenDialog(DirectoryPanel.this) == JFileChooser.APPROVE_OPTION) {
                        directory.setText(chooser.getSelectedFile().getPath());
                        Item.this.actionPerformed(null);
                    }
                }
            });
            final JLabel label = new JLabel(text);
            c.gridx=0; c.ipadx= 0; c.weightx=0; add(label,     c);
            c.gridx=1; c.ipadx=80; c.weightx=1; add(directory, c);
            c.gridx=2; c.ipadx= 0; c.weightx=0; add(open,      c);
            c.gridx=3; add(mode, c);
            c.gridy++;
            reload();
        }

        /**
         * Reloads the settings from the preferences. This is invoked at construction
         * time, and when the preferences changed.
         */
        final void reload() {
            automatic = config.directory(false).toString();
            final String user   = config.get(true);
            final String system = config.get(false);
            if (user != null) {
                supplied = user;
                currentMode = USER;
            } else if (system != null) {
                supplied = system;
                currentMode = SYSTEM;
            } else {
                supplied = this.automatic;
                currentMode = AUTO;
            }
            directory.removeActionListener(this);
            directory.setText(supplied);
            directory.setEnabled(currentMode != 0);
            directory.addActionListener(this);
            mode.removeActionListener(this);
            mode.setSelectedIndex(currentMode);
            mode.addActionListener(this);
            open.setEnabled(currentMode != 0);
        }

        /**
         * Invoked when the field gain its focus. There is nothing to do.
         */
        @Override
        public void focusGained(FocusEvent event) {
        }

        /**
         * Invoked when the field lost its focus. This method does the same thing
         * than when Enter is pressed in the field.
         */
        @Override
        public void focusLost(FocusEvent event) {
            actionPerformed(null);
        }

        /**
         * Invoked when the mode changed or when "Enter" has been press in a field.
         * This method save immediately the setting in the preferences, then refresh
         * the views.
         */
        @Override
        public void actionPerformed(final ActionEvent event) {
            String text = directory.getText();
            if (event != null && event.getSource() == mode) {
                if (currentMode != 0) {
                    supplied = text;
                }
                currentMode = mode.getSelectedIndex();
                final boolean manual = (currentMode != 0);
                text = manual ? supplied : automatic;
                directory.setText(text);
                directory.setEnabled(manual);
            }
            boolean user = false;
            switch (currentMode) {
                case AUTO: text = null; break;
                case USER: user = true; break;
                case SYSTEM: break;
            }
            config.set(user, text);
            reloadAll();
        }
    }

    /**
     * The data to refresh when the directories changed.
     */
    final DataPanel data;

    /**
     * The icon for the "open" action.
     */
    final Icon openIcon;

    /**
     * The items that can be set.
     */
    private final Item[] items;

    /**
     * Creates a new panel.
     */
    DirectoryPanel(final DataPanel data) {
        this.data = data;
        final Vocabulary resources = data.resources;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        openIcon = new ImageIcon(DirectoryPanel.class.getResource("/toolbarButtonGraphics/general/Open16.gif"));
        final GridBagConstraints c = new GridBagConstraints();
        c.insets.left=3; c.insets.right=3; c.fill=HORIZONTAL; c.gridy=0;
        items = new Item[] {
            new Item(c, resources.getLabel (Vocabulary.Keys.RootDirectory),          Installation.ROOT_DIRECTORY),
            new Item(c, resources.getString(Vocabulary.Keys.Data_1, "EPSG") + ':',   Installation.EPSG),
//          new Item(c, resources.getString(Vocabulary.Keys.Data_1, "NADCON") + ':', Installation.NADCON),
            new Item(c, resources.getLabel (Vocabulary.Keys.GriddedData),            Installation.COVERAGES)
        };
    }

    /**
     * Reloads all settings from the preferences. This is invoked when a preferences
     * changed because the new value may impact the default values of other items.
     */
    final void reloadAll() {
        for (final Item item : items) {
            item.reload();
        }
        data.refresh();
    }
}
