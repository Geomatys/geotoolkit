/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.gui.test;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.AbstractAction;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameAdapter;
import java.util.concurrent.CountDownLatch;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.prefs.Preferences;


/**
 * The desktop pane when to put the widgets to be tested.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
@SuppressWarnings("serial")
final class DesktopPane extends JDesktopPane {
    /**
     * The key for screenshot directory in the user preferences.
     */
    private static final String SCREENSHOT_DIRECTORY_PREFS = "Screenshots";

    /**
     * A lock used for waiting that the {@linkplain #desktop} has been closed.
     */
    final CountDownLatch lock;

    /**
     * The last active component.
     */
    private JComponent active;

    /**
     * Creates the desktop.
     */
    DesktopPane() {
        lock = new CountDownLatch(1);
    }

    /**
     * Creates the frame for this desktop.
     *
     * @return The frame in which the desktop is shown.
     */
    final JFrame createFrame() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu menu = new JMenu("Tools");
        menu.add(new AbstractAction("Screenshot") {
            @Override public void actionPerformed(final ActionEvent event) {
                screenshot();
            }
        });
        menu.add(new AbstractAction("Preferences") {
            @Override public void actionPerformed(final ActionEvent event) {
                preferences();
            }
        });
        menuBar.add(menu);
        final JFrame frame = new JFrame("Geotoolkit.org widget tests");
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(final WindowEvent event) {
                frame.removeWindowListener(this);
                lock.countDown();
                frame.dispose();
            }
        });
        frame.setJMenuBar(menuBar);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(this);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // Put at screen center.
        return frame;
    }

    /**
     * Returns the title to use for a widget of the given type.
     */
    private static String getTitle(Class<?> type) {
        while (type.isAnonymousClass()) {
            type = type.getSuperclass();
        }
        return type.getSimpleName();
    }

    /**
     * Shows the given component in a frame.
     *
     * @param  component The component to show.
     */
    final void show(final JComponent component) {
        final String title = getTitle(component.getClass());
        final JInternalFrame frame = new JInternalFrame(title, true, true, true, true);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameActivated(final InternalFrameEvent event) {
                active = component;
            }
        });
        frame.add(component);
        frame.pack();
        frame.setVisible(true);
        add(frame);
        if (active == null) {
            active = component;
        }
        System.out.println("Showing " + title);
    }

    /**
     * Popup a dialog box for setting the preferences.
     */
    private void preferences() {
        final String directory = JOptionPane.showInternalInputDialog(this, "Screenshot directory");
        if (directory != null) {
            Preferences.userNodeForPackage(DesktopPane.class).put(SCREENSHOT_DIRECTORY_PREFS, directory);
        }
    }

    /**
     * Takes a screenshot of the currently active component.
     */
    private void screenshot() {
        final JComponent active = this.active;
        if (active != null && active.isValid()) {
            final BufferedImage image = new BufferedImage(active.getWidth(), active.getHeight(), BufferedImage.TYPE_INT_RGB);
            final Graphics2D handler = image.createGraphics();
            active.print(handler);
            handler.dispose();
            File file = new File(Preferences.userNodeForPackage(DesktopPane.class).get(SCREENSHOT_DIRECTORY_PREFS, "."));
            file = new File(file, getTitle(active.getClass()) + ".png");
            try {
                ImageIO.write(image, "png", file);
                file = file.getParentFile();
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                JOptionPane.showInternalMessageDialog(active, e.getLocalizedMessage(),
                        e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
