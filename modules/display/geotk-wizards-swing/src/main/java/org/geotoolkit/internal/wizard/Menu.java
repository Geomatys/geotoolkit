/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.wizard;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.jdesktop.swingx.JXLabel;
import org.netbeans.api.wizard.WizardDisplayer;

import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.internal.setup.ControlPanel;


/**
 * The main menu, which propose to setup or start a wizard.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.01
 * @module
 *
 * @todo Needs localization.
 */
@SuppressWarnings("serial")
final class Menu extends JFrame implements ActionListener {
    /**
     * The button names, which will also be identifier for the action to launch.
     */
    private static final String SETUP = "SETUP", MOSAIC = "MOSAIC";

    /**
     * Creates a new instance of the main panel and display it immediately.
     */
    private Menu() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Geotoolkit.org wizards");
        setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridy=0;
        addButton(MOSAIC, "Mosaic generator",
                "Read a potentially big image (which may be splitted in many tiles " +
                "at the same resolution) and write a set of smaller tiles of given " +
                "size and using different subsamplings.", c);
        c.gridy++;
        addButton(SETUP,  "Geotoolkit Setup",
                "Select directories and install the NADCON and EPSG data. " +
                "This setup is optional. If executed, the setting will be " +
                "remembered for all subsequent Geotoolkit.org usage.", c);
        setSize(740, 200);
        setLocationRelativeTo(null);
    }

    /**
     * Add a button to this frame.
     *
     * @param name  The button name, to be used as an identifier for the action to start.
     * @param title The text to display in the button.
     * @param c The constraint. The <var>gridy</var> field should be set to the appropriate value.
     *        Other fields may be overwritten.
     */
    private void addButton(final String name, final String title, final String description,
            final GridBagConstraints c)
    {
        final JButton button = new JButton(title);
        button.setName(name);
        button.addActionListener(this);
        //button.setPreferredSize(new Dimension(100, 20));
        c.fill = GridBagConstraints.BOTH;
        c.insets.top = c.insets.bottom = 15;
        c.insets.left = c.insets.right = 30;
        c.weightx = c.weighty = 0;
        c.gridx=0;
        add(button, c);

        final JXLabel label = new JXLabel(description);
        label.setLineWrap(true);
        c.weightx = c.weighty = 1;
        c.insets.left = 0;
        c.gridx++;
        add(label, c);
    }

    /**
     * Invoked when a button has been pressed.
     *
     * @param event The button which has been pressed.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String action = ((Component) event.getSource()).getName();
        dispose();
        if (SETUP.equals(action)) {
            ControlPanel.show(getLocale());
        } else if (MOSAIC.equals(action)) {
            final MosaicWizard wizard = new MosaicWizard();
            WizardDisplayer.showWizard(wizard.createWizard());
            System.exit(0);
        }
    }

    /**
     * Invoked from the command line for displaying this frame.
     */
    public static void run() {
        SwingUtilities.setLookAndFeel(Main.class, "run");
        final JFrame frame = new Menu();
        // The line below should be after the Frame creation.
        // See the javadoc in 'setDefaultCodecPreferences()'.
        Registry.setDefaultCodecPreferences();
        frame.setVisible(true);
    }
}
