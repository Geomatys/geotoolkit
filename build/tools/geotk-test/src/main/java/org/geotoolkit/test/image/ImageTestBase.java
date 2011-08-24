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
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.concurrent.CountDownLatch;

import org.opengis.coverage.Coverage;

import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestBase;
import org.geotoolkit.test.gui.SwingTestBase;

import org.junit.After;
import static org.junit.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Base class for tests applied on images. This base class provides a {@link #viewEnabled}
 * field initialized to {@code false}. If this field is set to {@code true}, then calls to
 * the {@link #view(String)} method will show the {@linkplain #image}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.16 (derived from 3.00)
 */
public abstract strictfp class ImageTestBase extends TestBase {
    /**
     * Small value for comparison of sample values. Since most grid coverage implementations in
     * Geotk 2 store geophysics values as {@code float} numbers, this {@code SAMPLE_TOLERANCE}
     * value must be of the order of {@code float} relative precision, not {@code double}.
     */
    public static final float SAMPLE_TOLERANCE = 1E-5f;

    /**
     * Invokes {@link org.geotoolkit.image.jai.Registry#setDefaultCodecPreferences()}
     * in order to improve consistency between different execution of test suites.
     */
    static {
        try {
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
     * Asserts that the {@linkplain #image} checksum is equals to one of the specified values.
     *
     * @param name The name of the image being tested, or {@code null} if none.
     * @param expected The expected checksum value.
     */
    protected final synchronized void assertCurrentChecksumEquals(final String name, final long... expected) {
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
     * Returns a copy of the current image.
     *
     * @return A copy of the current image.
     */
    protected final synchronized BufferedImage copyCurrentImage() {
        assertNotNull("No image currently defined.", image);
        final ColorModel cm = image.getColorModel();
        return new BufferedImage(cm, image.copyData(null), cm.isAlphaPremultiplied(), null);
    }

    /**
     * Saves the current image as a PNG image in the given file. This is sometime useful for visual
     * check purpose, and is used only as a helper tools for tuning the test suites. Floating-point
     * images are converted to grayscale before to be saved.
     *
     * @param  filename The name (optionally with its path) of the file to create.
     * @throws ImagingOpException If an error occurred while writing the file.
     *
     * @since 3.19
     */
    protected final synchronized void saveCurrentImage(final String filename) throws ImagingOpException {
        final RenderedImage image = this.image;
        assertNotNull("An image must be specified", image);
        final File file = new File(filename);
        try {
            if (!ImageIO.write(image, "png", file)) {
                savePNG(image.getData(), file);
            }
        } catch (IOException e) {
            throw new ImagingOpException(e.toString());
        }
    }

    /**
     * Saves the first band of the given raster as a PNG image in the given file.
     * This is sometime useful for visual check purpose, and is used only as a helper
     * tools for tuning the test suites. The image is converted to grayscale before to
     * be saved.
     *
     * @param  raster The raster to write in PNG format.
     * @param  file The file to create.
     * @throws IOException If an error occurred while writing the file.
     */
    private static void savePNG(final Raster raster, final File file) throws IOException {
        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;
        final int xmin   = raster.getMinX();
        final int ymin   = raster.getMinY();
        final int width  = raster.getWidth();
        final int height = raster.getHeight();
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                final float value = raster.getSampleFloat(x + xmin, y + ymin, 0);
                if (value < min) min = value;
                if (value > min) max = value;
            }
        }
        final float scale = 255 / (max - min);
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        final WritableRaster dest = image.getRaster();
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                final double value = raster.getSampleDouble(x + xmin, y + ymin, 0);
                dest.setSample(x, y, 0, round((value - min) * scale));
            }
        }
        assertTrue("No suitable PNG writer found.", ImageIO.write(image, "png", file));
    }

    /**
     * Display the {@linkplain #image} if {@link #viewEnabled} is set to {@code true},
     * otherwise does nothing. This is mostly for debugging purpose.
     *
     * @param method The name of the test method invoking {@code view}.
     *        This is used only for the frame title.
     */
    @SuppressWarnings("deprecation")
    protected final synchronized void showCurrentImage(final String method) {
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
                        final boolean done;
                        try {
                            done = ImageIO.write(image, "png", file);
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(panel, e.toString(),
                                    "Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        if (!done) {
                            JOptionPane.showMessageDialog(panel, "No appropriate writer found",
                                    "Error", JOptionPane.WARNING_MESSAGE);
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
     * Shows the default rendering of the specified coverage.
     * This is used for debugging only.
     *
     * @param coverage The coverage to display.
     */
    protected final void show(final Coverage coverage) {
        if (!viewEnabled) {
            return;
        }
        final RenderedImage image = coverage.getRenderableImage(0,1).createDefaultRendering();
        try {
            Class.forName("org.geotoolkit.gui.swing.OperationTreeBrowser")
                 .getMethod("show", new Class<?>[] {RenderedImage.class})
                 .invoke(null, new Object[]{image});
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            /*
             * The OperationTreeBrowser is not part of Geotk's core. It is optional and this
             * class should not fails if it is not presents. This is only a helper for debugging.
             */
            System.err.println(e);
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
