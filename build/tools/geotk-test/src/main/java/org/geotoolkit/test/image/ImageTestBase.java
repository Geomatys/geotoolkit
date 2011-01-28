/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.concurrent.CountDownLatch;

import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestBase;
import org.geotoolkit.test.gui.SwingTestBase;

import org.junit.After;
import org.junit.BeforeClass;
import static org.junit.Assert.*;


/**
 * Base class for tests applied on images. This base class provides a {@link #viewEnabled}
 * field initialized to {@code false}. If this field is set to {@code true}, then calls to
 * the {@link #view(String)} method will show the {@linkplain #image}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.16 (derived from 3.00)
 */
public abstract class ImageTestBase extends TestBase {
    /**
     * {@code true} if {@link #setDefaultCodecPreferences()} has been invoked.
     */
    private static boolean initialized;

    /**
     * Invokes {@link org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()}
     * in order to improve consistency between different execution of test suites.
     * This method is invoked automatically by JUnit and doesn't need to be invoked explicitely.
     */
    @BeforeClass
    public static synchronized void setDefaultCodecPreferences() {
        if (!initialized) try {
            initialized = true; // Initialize only once even in case of failure.
            Class.forName("org.geotoolkit.image.jai.Registry")
                 .getMethod("setDefaultCodecPreferences", (Class<?>[]) null)
                 .invoke(null, (Object[]) null);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * The image being tested.
     */
    protected RenderedImage image;

    /**
     * Set to {@code true} for enabling the display of test images.
     * The default value is determined by:
     *
     * {@preformat java
     *     Boolean.getBoolean(SHOW_PROPERTY_KEY);
     * }
     *
     * @see SwingTestBase#SHOW_PROPERTY_KEY
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
    protected ImageTestBase(final Class<?> testing) {
        assertTrue(testing.desiredAssertionStatus());
        viewEnabled = Boolean.getBoolean(SwingTestBase.SHOW_PROPERTY_KEY);
    }

    /**
     * Returns a copy of the current image.
     *
     * @return A copy of the current image.
     */
    protected final synchronized BufferedImage copyImage() {
        assertNotNull("No image currently defined.", image);
        final ColorModel cm = image.getColorModel();
        return new BufferedImage(cm, image.copyData(null), cm.isAlphaPremultiplied(), null);
    }

    /**
     * Asserts that the {@linkplain #image} checksum is equals to one of the specified values.
     *
     * @param name The name of the image being tested, or {@code null} if none.
     * @param expected The expected checksum value.
     */
    protected final synchronized void assertChecksumEquals(final String name, final long... expected) {
        final long c = Commons.checksum(image);
        for (final long e : expected) {
            if (e == c) return;
        }
        final StringBuilder buffer = new StringBuilder("Unexpected image checksum");
        if (name != null) {
            buffer.append(" for \"").append(name).append('"');
        }
        fail(buffer.append(": ").append(c).toString());
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
            String title = getClass().getSimpleName();
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
                            assertTrue(ImageIO.write(image, "png", file));
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(panel, e.toString(), "Error",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            });
            panel.add(save, BorderLayout.SOUTH);
            frame.add(panel);
            frame.setSize(image.getWidth() + 100, image.getHeight() + 150);
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
            // It is okay to continue. JUnit will close all windows.
        }
    }
}
