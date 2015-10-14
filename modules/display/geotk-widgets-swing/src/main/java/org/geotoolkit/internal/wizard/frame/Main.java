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
package org.geotoolkit.internal.wizard.frame;

import java.util.Locale;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.netbeans.api.wizard.WizardDisplayer;

import org.geotoolkit.gui.swing.About;
import org.geotoolkit.gui.swing.coverage.LayerList;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.internal.GraphicsUtilities;
import org.geotoolkit.internal.setup.ControlPanel;
import org.geotoolkit.internal.swing.ExceptionMonitor;
import org.geotoolkit.internal.wizard.CoverageDatabaseWizard;
import org.geotoolkit.internal.wizard.MosaicWizard;
import org.geotoolkit.coverage.sql.CoverageDatabase;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Wizards;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.logging.Logging;


/**
 * The main frame where available wizards are proposed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.11
 * @module
 */
@SuppressWarnings("serial")
public final class Main extends JFrame implements ActionListener {
    /**
     * The button names, which will also be identifier for the action to launch.
     */
    private static final String COVERAGES="COVERAGES", COVERAGES_SCHEMA="COVERAGES_SCHEMA",
            MOSAIC="MOSAIC", SETUP="SETUP", HOME="HOME", ABOUT="ABOUT", QUIT="QUIT";

    /**
     * The desktop pane, which fill completely the frame.
     */
    private final JDesktopPane desktop;

    /**
     * Creates a new frame.
     */
    private Main() {
        super();
        add(desktop = new JDesktopPane());
        final Wizards resources = Wizards.getResources(getLocale());
        setTitle(resources.getString(Wizards.Keys.GeotkWizards));
        setMenuBar(resources);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

        menu = new JMenu(vocabulary.getString(Vocabulary.Keys.File));
        item = new JMenuItem(vocabulary.getMenuLabel(Vocabulary.Keys.Preferences), KeyEvent.VK_P);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, keyMask));
        item.setToolTipText(resources.getString(Wizards.Keys.SetupDesc));
        item.addActionListener(this);
        item.setActionCommand(SETUP);
        menu.add(item);

        item = new JMenuItem(vocabulary.getString(Vocabulary.Keys.Quit), KeyEvent.VK_Q);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, keyMask));
        item.addActionListener(this);
        item.setActionCommand(QUIT);
        menu.add(item);
        bar.add(menu);

        menu = new JMenu(vocabulary.getString(Vocabulary.Keys.Navigate));
        item = new JMenuItem(vocabulary.getMenuLabel(Vocabulary.Keys.GriddedData), KeyEvent.VK_G);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, keyMask));
        item.addActionListener(this);
        item.setActionCommand(COVERAGES);
        menu.add(item);
        bar.add(menu);

        menu = new JMenu(vocabulary.getString(Vocabulary.Keys.Wizards));
        item = new JMenuItem(resources.getMenuLabel(Wizards.Keys.CoverageDatabaseTitle));
        item.setToolTipText(resources.getString(Wizards.Keys.CoverageDatabaseDesc));
        item.addActionListener(this);
        item.setActionCommand(COVERAGES_SCHEMA);
        menu.add(item);

        item = new JMenuItem(resources.getMenuLabel(Wizards.Keys.MosaicTitle), KeyEvent.VK_M);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, keyMask));
        item.setToolTipText(resources.getString(Wizards.Keys.MosaicDesc));
        item.addActionListener(this);
        item.setActionCommand(MOSAIC);
        menu.add(item);
        bar.add(menu);

        menu = new JMenu(vocabulary.getString(Vocabulary.Keys.Help));
        item = new JMenuItem(vocabulary.getMenuLabel(Vocabulary.Keys.About), KeyEvent.VK_A);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, keyMask));
        item.addActionListener(this);
        item.setActionCommand(ABOUT);
        menu.add(item);

        if (Desktop.isDesktopSupported()) {
            item = new JMenuItem(resources.getString(Wizards.Keys.GeotkSite));
            item.addActionListener(this);
            item.setActionCommand(HOME);
            menu.add(item);
        }
        bar.add(menu);

        setJMenuBar(bar);
    }

    /**
     * Invoked when a button has been pressed.
     *
     * @param event The button which has been pressed.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String action = event.getActionCommand();
        switch (action) {
            case ABOUT: {
                final About about = new About();
                about.showDialog(Main.this);
                break;
            }
            case SETUP: {
                ControlPanel.show(desktop);
                break;
            }
            case QUIT: {
                System.exit(0);
                break;
            }
            case COVERAGES: {
                final CoverageDatabase database = getCoverageDatabase();
                if (database != null) {
                    show(Vocabulary.Keys.GriddedData, new LayerList(database));
                }
                break;
            }
            case COVERAGES_SCHEMA: {
                final CoverageDatabaseWizard wizard = new CoverageDatabaseWizard();
                WizardDisplayer.showWizard(wizard.createWizard());
                break;
            }
            case MOSAIC: {
                final MosaicWizard wizard = new MosaicWizard();
                WizardDisplayer.showWizard(wizard.createWizard());
                break;
            }
            case HOME: {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.geotoolkit.org/modules/display/geotk-wizards-swing/index.html"));
                } catch (URISyntaxException | IOException ex) {
                    ExceptionMonitor.show(desktop, ex);
                }
                break;
            }
        }
    }

    /**
     * Shows the given component in an internal frame.
     */
    private void show(final short titleKey, final JComponent component) {
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        final JInternalFrame frame = new JInternalFrame(resources.getString(titleKey), true, true, true, true);
        frame.add(component);
        frame.pack();
        final int x = (desktop.getWidth()  - frame.getWidth())  / 2;
        final int y = (desktop.getHeight() - frame.getHeight()) / 2;
        if (x >= 0 && y >= 0) {
            frame.setLocation(x, y);
        }
        desktop.add(frame);
        frame.setVisible(true);
    }

    /**
     * Returns the {@link CoverageDatabase}, or {@code null} if none.
     */
    private CoverageDatabase getCoverageDatabase() {
        CoverageDatabase database = null;
        try {
            database = CoverageDatabase.getDefaultInstance();
            if (database == null) {
                final Locale locale = getLocale();
                JOptionPane.showInternalMessageDialog(desktop,
                        Wizards.getResources(locale).getString(Wizards.Keys.UnspecifiedCoveragesDatabase),
                        Errors.getResources(locale).getString(Errors.Keys.NoDataSource), JOptionPane.WARNING_MESSAGE);
            }
        } catch (CoverageStoreException e) {
            ExceptionMonitor.show(desktop, e);
        }
        return database;
    }

    /**
     * Invoked from the command line for displaying the frame.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
        if (ArraysExt.containsIgnoreCase(args, "--nimbus")) try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            Logging.recoverableException(null, Main.class, "<init>", e);
        } else {
            GraphicsUtilities.setLookAndFeel(Main.class, "<init>");
        }
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                final JFrame frame = new Main();
                // The line below should be after the Frame creation.
                // See the javadoc in 'setDefaultCodecPreferences()'.
                Setup.initialize(null);
                frame.setVisible(true);
            }
        });
    }
}
