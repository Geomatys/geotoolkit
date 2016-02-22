/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test.image;

import java.awt.EventQueue;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.imageio.IIOException;

import org.opengis.coverage.Coverage;

import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestBase;
import org.geotoolkit.test.gui.SwingTestBase;

import org.junit.AfterClass;
import static org.junit.Assume.*;
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
        } catch (ReflectiveOperationException e) {
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
     * The image viewer, which can be created only if {@link #viewEnabled} is {@code true}.
     */
    private static volatile Viewer viewer;

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
     * Returns the file of the given name in the {@code "Geotoolkit.org/Tests"} directory.
     * This directory contains data too big for inclusion in the source code repository.
     * The file is tested for existence using:
     *
     * {@code java
     *     assumeTrue(file.canRead());
     * }
     *
     * Consequently if the file can not be read (typically because the users did not installed
     * those data on its local directory), then the tests after the call to this method are
     * completely skipped.
     *
     * @param  filename The name of the file to get, or {@code null}.
     * @return The name of directory of the given name in the {@code "Geotoolkit.org/Tests"}
     *         directory (never {@code null}).
     *
     * @since 3.19
     */
    public static File getLocallyInstalledFile(final String filename) {
        Path file;
        try {
            final Class<?> c = Class.forName("org.geotoolkit.internal.io.Installation");
            file = (Path) c.getMethod("directory", Boolean.TYPE).invoke(c.getField("TESTS").get(null), Boolean.TRUE);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
        if (filename != null) {
            file = file.resolve(filename);
        }
        assumeTrue(Files.isReadable(file));
        return file.toFile();
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
        try {
            savePNG(image, new File(filename));
        } catch (IOException e) {
            throw new ImagingOpException(e.toString());
        }
    }

    /**
     * Implementation of {@link #saveCurrentImage(String)}, to be shared by the widget
     * shown by {@link #showCurrentImage(String)}.
     */
    static void savePNG(final RenderedImage image, final File file) throws IOException {
        assertNotNull("An image must be set.", image);
        if (!ImageIO.write(image, "png", file)) {
            savePNG(image.getData(), file);
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
        if (!ImageIO.write(image, "png", file)) {
            throw new IIOException("No suitable PNG writer found.");
        }
    }

    /**
     * Displays the {@linkplain #image} if {@link #viewEnabled} is set to {@code true},
     * otherwise does nothing. This method is mostly for debugging purpose.
     *
     * @param title The window title.
     */
    @SuppressWarnings("deprecation")
    protected final synchronized void showCurrentImage(final String title) {
        final RenderedImage image = this.image;
        assertNotNull("An image must be set.", image);
        if (viewEnabled) {
            final String classname = getClass().getSimpleName();
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    @Override public void run() {
                        Viewer v = viewer;
                        if (v == null) {
                            viewer = v = new Viewer(classname);
                        }
                        v.addImage(image, String.valueOf(title));
                    }
                });
            } catch (InterruptedException | InvocationTargetException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * Shows the default rendering of the specified coverage.
     * This is used for debugging only.
     *
     * @param coverage The coverage to display.
     */
    protected final synchronized void show(final Coverage coverage) {
        if (!viewEnabled) {
            return;
        }
        final RenderedImage image = coverage.getRenderableImage(0,1).createDefaultRendering();
        try {
            Class.forName("org.geotoolkit.gui.swing.image.OperationTreeBrowser")
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
    @AfterClass
    public static void waitForFrameDisposal() {
        final Viewer v = viewer;
        if (v != null) {
            v.waitForFrameDisposal();
        }
    }
}
