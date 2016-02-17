/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test.gui;

import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;

import org.junit.*;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Base class for tests on widgets. By default this test suite displays nothing;
 * it merely:
 * <p>
 * <ul>
 *   <li>Ensure that no exception is thrown while creating the widget</li>
 *   <li>Ensure that no exception is thrown while painting in a buffered image</li>
 * </ul>
 * <p>
 * However if the "{@code org.geotoolkit.showWidgetTests}" system property is
 * set to "{@code true}", then the widgets will be shown as an internal frame.
 *
 * @param <T> The type of the widget to be tested.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.3
 */
@SuppressWarnings("serial")
public abstract strictfp class SwingTestBase<T extends JComponent> {
    /**
     * The name of a system property for setting whatever the widget should be show.
     * If the value returned by the following is {@code true}, then the widgets will
     * be shown:
     *
     * {@preformat java
     *     Boolean.getBoolean(SHOW_PROPERTY_KEY);
     * }
     *
     * The value of this property key is {@value}.
     *
     * @see org.geotoolkit.test.TestBase#VERBOSE_KEY
     */
    public static final String SHOW_PROPERTY_KEY = "org.geotoolkit.test.gui.show";

    /**
     * The type of the widget being tested.
     */
    final Class<T> testing;

    /**
     * Number of invocation of {@link #create(int)} to perform.
     */
    final int numTests;

    /**
     * Creates a new instance of {@code SwingTestBase} which will invoke
     * {@link #create(int)} only once.
     *
     * @param testing The class being tested.
     */
    protected SwingTestBase(final Class<T> testing) {
        this(testing, 1);
    }

    /**
     * Creates a new instance of {@code SwingTestBase}.
     *
     * @param testing The class being tested.
     * @param numTests Number of invocation of {@link #create(int)} to perform.
     */
    protected SwingTestBase(final Class<T> testing, final int numTests) {
        assertTrue(testing.desiredAssertionStatus());
        assertTrue(JComponent.class.isAssignableFrom(testing));
        assertTrue(numTests >= 1);
        this.testing  = testing;
        this.numTests = numTests;
    }

    /**
     * If the widgets are to be show, prepares the desktop pane which will contain them.
     * This method is invoked by JUnit and should not be invoked directly.
     *
     * @throws HeadlessException If the current environment does not allow the display of widgets.
     */
    @BeforeClass
    public static synchronized void prepareDesktop() throws HeadlessException {
        if (isDisplayEnabled()) {
            DesktopPane.prepareDesktop();
        }
    }

    /**
     * Returns {@code true} if the display of widgets is enabled.
     *
     * @return {@code true} if the display of widgets is enabled.
     */
    public static boolean isDisplayEnabled() {
        return Boolean.getBoolean(SHOW_PROPERTY_KEY);
    }

    /**
     * Creates the widget. The widget is usually of type {@code T}, except if the
     * widget has been put in a scroll pane.
     * <p>
     * This method can return {@code null} if the widget can not be created for a
     * raison which is not a test failure, for example if the widget relies on some
     * resources which may not be available on the classpath.
     *
     * @param  index Index of the widget being created, from 0 inclusive to the value given
     *         at construction time, exclusive.
     * @return The created widget, or {@code null} if the widget can not be created for
     *         an acceptable raison.
     * @throws Exception If an exception occurred while creating the widget.
     */
    protected abstract JComponent create(int index) throws Exception;

    /**
     * {@linkplain #create() Creates} the widget. If the "{@code org.geotoolkit.showWidgetTests}"
     * system property is set to "{@code true}", then the widget will be show as an internal
     * frame in the desktop.
     *
     * @throws Exception If an exception occurred while creating the widget.
     */
    @Test
    public void display() throws Exception {
        final JComponent[] components = new JComponent[numTests];
        for (int i=0; i<components.length; i++) {
            assumeNotNull(components[i] = create(i));
        }
        if (!show(this, components)) {
            for (int i=0; i<components.length; i++) {
                final JComponent component = components[i];
                component.setSize(component.getPreferredSize());
                component.setVisible(true);
                final int width  = component.getWidth();
                final int height = component.getHeight();
                final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D gr = image.createGraphics();
                try {
                    component.print(gr);
                } finally {
                    gr.dispose();
                }
                /*
                 * Optionally save to a file in the current directory, for checking purpose.
                 * Actually the image is empty if we didn't overridden the Component.paint method,
                 * so the above check is useful only for widgets doing their own painting like
                 * ColorRamp.
                 */
                if (false) {
                    final File file = new File(DesktopPane.getTitle(component.getClass()) + '-' + i + ".png");
                    assertTrue(ImageIO.write(image, "png", file));
                    System.out.println("Image saved in " + file.getAbsolutePath());
                }
            }
        } else {
            animate(components);
        }
    }

    /**
     * Invoked in the JUnit thread if the widget have been shown. The default implementation
     * does nothing. Subclasses can override this method for testing an animation.
     *
     * @param  components The widgets that were created.
     * @throws Exception If an exception occurred while animating the widget.
     *
     * @since 3.07
     */
    protected void animate(final JComponent[] components) throws Exception {
    }

    /**
     * Shows the given components, if the test is allowed to display widgets and
     * the given component is not null.
     *
     * @param  testCase The test case for which the component is added.
     * @param  components The components to show, or {@code null} if none.
     * @return {@code true} if the component has been shown.
     */
    protected static boolean show(final SwingTestBase<?> testCase, final JComponent... components) {
        return isDisplayEnabled() && DesktopPane.show(testCase, components);
    }

    /**
     * If a frame has been created, wait for its disposal. This method is invoked by JUnit
     * and should not be invoked directly.
     *
     * @throws InterruptedException If the current thread has been interrupted while
     *         we were waiting for the frame disposal.
     */
    @AfterClass
    public static void waitForFrameDisposal() throws InterruptedException {
        if (isDisplayEnabled()) {
            DesktopPane.waitForFrameDisposal();
        }
    }
}
