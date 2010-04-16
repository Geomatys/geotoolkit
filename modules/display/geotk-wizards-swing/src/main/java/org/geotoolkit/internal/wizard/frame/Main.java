/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.wizard.frame;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URI;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.netbeans.api.wizard.WizardDisplayer;

import org.geotoolkit.gui.swing.About;
import org.geotoolkit.gui.swing.ExceptionMonitor;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.internal.setup.ControlPanel;
import org.geotoolkit.internal.wizard.MosaicWizard;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Wizards;


/**
 * The main frame where available wizards are proposed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@SuppressWarnings("serial")
public final class Main extends JFrame implements ActionListener {
    /**
     * The button names, which will also be identifier for the action to launch.
     */
    private static final String SETUP="SETUP", MOSAIC="MOSAIC", HOME="HOME", ABOUT="ABOUT", QUIT="QUIT";

    /**
     * The desktop pane, which fill completly the frame.
     */
    private final JDesktopPane desktop;

    /**
     * Creates a new frame.
     */
    private Main() {
        super();
        add(desktop = new JDesktopPane());
        final Wizards resources = Wizards.getResources(getLocale());
        setTitle(resources.getString(Wizards.Keys.GEOTK_WIZARDS));
        setMenuBar(resources);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    /**
     * Sets the menu bar.
     */
    private void setMenuBar(final Wizards resources) {
        JMenu menu;
        JMenuItem item;
        final JMenuBar bar = new JMenuBar();
        final Vocabulary vocabulary = Vocabulary.getResources(getLocale());
        final int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        menu = new JMenu(vocabulary.getString(Vocabulary.Keys.FILE));
        item = new JMenuItem(vocabulary.getMenuLabel(Vocabulary.Keys.PREFERENCES), KeyEvent.VK_P);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, keyMask));
        item.setToolTipText(resources.getString(Wizards.Keys.SETUP_DESC));
        item.addActionListener(this);
        item.setName(SETUP);
        menu.add(item);

        item = new JMenuItem(vocabulary.getString(Vocabulary.Keys.QUIT), KeyEvent.VK_Q);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, keyMask));
        item.addActionListener(this);
        item.setName(QUIT);
        menu.add(item);
        bar.add(menu);

        menu = new JMenu(vocabulary.getString(Vocabulary.Keys.WIZARDS));
        item = new JMenuItem(resources.getMenuLabel(Wizards.Keys.MOSAIC_TITLE), KeyEvent.VK_M);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, keyMask));
        item.setToolTipText(resources.getString(Wizards.Keys.MOSAIC_DESC));
        item.addActionListener(this);
        item.setName(MOSAIC);
        menu.add(item);
        bar.add(menu);

        menu = new JMenu(vocabulary.getString(Vocabulary.Keys.HELP));
        item = new JMenuItem(vocabulary.getMenuLabel(Vocabulary.Keys.ABOUT), KeyEvent.VK_A);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, keyMask));
        item.addActionListener(this);
        item.setName(ABOUT);
        menu.add(item);

        if (Desktop.isDesktopSupported()) {
            item = new JMenuItem(resources.getString(Wizards.Keys.GEOTK_SITE));
            item.addActionListener(this);
            item.setName(HOME);
            menu.add(item);
        }
        bar.add(menu);

        setJMenuBar(bar);
    }

    /**
     * Invoked when a button has been pressed.
     *
     * @param event The button which has been pressed.
     *
     * @todo Use "switch in strings" when we will be allowed to compile for Java 7.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String action = ((Component) event.getSource()).getName();
        if (ABOUT.equals(action)) {
            final About about = new About();
            about.showDialog(Main.this);
        } else if (SETUP.equals(action)) {
            ControlPanel.show(desktop);
        } else if (QUIT.equals(action)) {
            System.exit(0);
        } else if (MOSAIC.equals(action)) {
            final MosaicWizard wizard = new MosaicWizard();
            WizardDisplayer.showWizard(wizard.createWizard());
        } else if (HOME.equals(action)) try {
            Desktop.getDesktop().browse(new URI("http://www.geotoolkit.org/modules/display/geotk-wizards-swing/index.html"));
        } catch (Exception ex) {
            ExceptionMonitor.show(desktop, ex);
        }
    }

    /**
     * Invoked from the command line for displaying the frame.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
        SwingUtilities.setLookAndFeel(Main.class, "<init>");
        final JFrame frame = new Main();
        // The line below should be after the Frame creation.
        // See the javadoc in 'setDefaultCodecPreferences()'.
        Setup.initialize(null);
        frame.setVisible(true);
    }
}
