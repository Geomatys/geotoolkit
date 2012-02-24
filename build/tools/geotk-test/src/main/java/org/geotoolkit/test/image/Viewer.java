/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.test.image;

import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.util.concurrent.CountDownLatch;

import static java.lang.StrictMath.*;


/**
 * Provides a Swing viewer for images.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
final strictfp class Viewer extends WindowAdapter {
    /**
     * A lock used for waiting that at least one frame has been closed.
     */
    private final CountDownLatch lock;

    /**
     * The frame showing the images.
     */
    private final JFrame frame;

    /**
     * The desktop pane where to show each images.
     */
    private final JDesktopPane desktop;

    /**
     * The location of the next internal frame to create.
     */
    private int location;

    /**
     * Creates a new viewer and show it immediately.
     */
    Viewer(final String title) {
        lock = new CountDownLatch(1);
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);
        desktop = new JDesktopPane();
        desktop.setSize(800, 600);
        final JMenuBar menuBar = new JMenuBar();
        if (true) {
            final JMenu menu = new JMenu("File");
            if (true) {
                final JMenuItem item = new JMenuItem("Save as PNG");
                item.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        savePNG();
                    }
                });
                menu.add(item);
            }
            menuBar.add(menu);
        }
        frame.setJMenuBar(menuBar);
        frame.add(desktop);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates and shows a new internal frame for the given image.
     */
    final void addImage(final RenderedImage image, final String title) {
        final JInternalFrame internal = new JInternalFrame(title, true, true);
        internal.add(new ImagePanel(image));
        internal.pack();
        internal.show();
        desktop.add(internal);
        if (location > min(desktop.getWidth()  - internal.getWidth(),
                           desktop.getHeight() - internal.getHeight()))
        {
            location = 0;
        }
        internal.setLocation(location, location);
        location += 30;
        internal.toFront();
    }

    /**
     * Returns the image of the currently selected frame.
     */
    private RenderedImage getSelectedImage() {
        final JInternalFrame frame = desktop.getSelectedFrame();
        if (frame != null) {
            return ((ImagePanel) frame.getContentPane().getComponent(0)).image;
        }
        return null;
    }

    /**
     * Saves the image of the currently selected frame.
     */
    final void savePNG() {
        final RenderedImage image = getSelectedImage();
        if (image != null) {
            final File file = new File(System.getProperty("user.home"), "ImageTest.png");
            final String title, message;
            final int type;
            if (file.exists()) {
                type    = JOptionPane.WARNING_MESSAGE;
                title   = "Confirm overwrite";
                message = "File " + file + " exists. Overwrite?";
            } else {
                type    = JOptionPane.QUESTION_MESSAGE;
                title   = "Confirm write";
                message = "Save in " + file + '?';
            }
            if (JOptionPane.showInternalConfirmDialog(desktop, message, title,
                    JOptionPane.YES_NO_OPTION, type) == JOptionPane.OK_OPTION)
            {
                try {
                    ImageTestBase.savePNG(image, file);
                } catch (IOException e) {
                    JOptionPane.showInternalMessageDialog(desktop, e.toString(),
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    /**
     * Invoked when the window is closed. Tells the test suite that
     * it can stop waiting.
     */
    @Override
    public void windowClosed(final WindowEvent event) {
        frame.removeWindowListener(this);
        lock.countDown();
        frame.dispose();
    }

    /**
     * Waits for the frame disposal.
     */
    final void waitForFrameDisposal() {
        try {
            lock.await();
        } catch (InterruptedException e) {
            // It is okay to continue. JUnit will close all windows.
        }
    }
}
