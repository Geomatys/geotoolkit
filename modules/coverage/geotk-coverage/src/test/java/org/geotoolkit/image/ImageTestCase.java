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
package org.geotoolkit.image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.concurrent.CountDownLatch;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;

import org.geotoolkit.test.Commons;
import org.geotoolkit.util.converter.Classes;

import org.junit.After;
import static org.junit.Assert.*;


/**
 * Base class for tests applied on images.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public abstract class ImageTestCase {
    /**
     * The image being tested.
     */
    protected RenderedImage image;

    /**
     * Set to {@code true} for enabling the display of test images.
     */
    protected boolean viewEnabled;

    /**
     * A lock used for waiting that at least one frame has been closed.
     */
    private transient CountDownLatch lock;

    /**
     * Creates a new test suite for the given class.
     *
     * @param testing The class to be tested.
     */
    protected ImageTestCase(final Class<?> testing) {
        assertTrue(testing.desiredAssertionStatus());
    }

    /**
     * Loads the given sample image. The result is stored in the {@link #image} field.
     *
     * @param  s The enum for the sample image to load.
     */
    protected final synchronized void loadSampleImage(final SampleImage s) {
        try {
            image = s.load();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Asserts that the {@linkplain #image} checksum is equals to the specified value.
     *
     * @param expected The expected checksum value.
     */
    protected final synchronized void assertChecksumEquals(final long expected) {
        assertEquals(expected, Commons.checksum(image));
    }

    /**
     * Applies a unary operation on the current image using the given parameters.
     *
     * @param parameters The parameters, without any source. The current {@linkplain #image}
     *        will be added directly as the source in the given parameter block.
     * @param checksum The checksum of the expected result, or 0 for ignoring it.
     */
    protected final synchronized void applyUnary(final ParameterBlockJAI parameters, final long checksum) {
        final String operation = parameters.getOperationDescriptor().getName();
        image = JAI.create(operation, parameters.addSource(image));
        if (checksum != 0) {
            String message = "Checksum failed for operation \"" + operation + "\".";
            assertEquals(message, checksum, Commons.checksum(image));
        }
    }

    /**
     * Display the {@linkplain #image} if {@link #viewEnabled} is set to {@code true},
     * otherwise does nothing. This is mostly for debugging purpose.
     *
     * @param method The name of the test method invoking {@code view}.
     *        This is used only for the frame title.
     */
    @SuppressWarnings("deprecation")
    protected final synchronized void view(final String method) {
        assertNotNull("An image must be set.", image);
        final RenderedImage image = this.image;
        /*
         * It was necessary to copy the image field in a local variable because the code below
         * contains inner class, and we want those inner classes to work on the image that was
         * active at the time this method was invoked.
         */
        if (viewEnabled) {
            if (lock == null) {
                lock = new CountDownLatch(1);
            }
            final CountDownLatch lock = this.lock;
            String title = Classes.getShortClassName(this);
            if (method != null) {
                title = title + '.' + method;
            }
            final JFrame frame = new JFrame(title);
            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(final WindowEvent event) {
                    frame.removeWindowListener(this);
                    lock.countDown();
                    frame.dispose();
                }
            });
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            final JPanel panel = new JPanel(new BorderLayout());
            panel.add(new javax.media.jai.widget.ScrollingImagePanel(image, 500, 500), BorderLayout.CENTER);
            final JButton save = new JButton("Save as PNG");
            save.addActionListener(new ActionListener() {
                @Override public void actionPerformed(final ActionEvent event) {
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
                    if (JOptionPane.showConfirmDialog(panel, message, title,
                            JOptionPane.YES_NO_OPTION, type) == JOptionPane.OK_OPTION)
                    {
                        try {
                            ImageIO.write(image, "png", file);
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(panel, e.toString(), "Error",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            });
            panel.add(save, BorderLayout.SOUTH);
            frame.add(panel);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        }
    }

    /**
     * If a frame has been created by {@link #view}, wait for its disposal
     * before to move to the next test.
     */
    @After
    public final void waitForFrameDisposal() {
        final CountDownLatch lock = this.lock;
        if (lock != null) try{
            lock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            // It is okay to continue. JUnit will close all windows.
        }
    }
}
