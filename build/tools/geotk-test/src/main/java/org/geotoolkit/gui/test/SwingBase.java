/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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

import javax.swing.JComponent;
import java.awt.HeadlessException;
import java.beans.PropertyVetoException;

import org.junit.*;

import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Base class for tests on widgets. By default this test suite displays nothing;
 * it merely checks that no exception is thrown during widget construction.
 * However if the "{@code org.geotoolkit.showWidgetTests}" system property is
 * set to "{@code true}", then the widgets will be shown as an internal frame.
 *
 * @param <T> The type of the widget to be tested.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.05
 *
 * @since 2.3
 */
@SuppressWarnings("serial")
public abstract class SwingBase<T extends JComponent> {
    /**
     * The desktop which contain the internal frame for each widget. Will be created only if
     * the "{@code org.geotoolkit.showWidgetTests}" system property is set to {@code true}.
     */
    private static DesktopPane desktop;

    /**
     * The type of the widget being tested.
     */
    final Class<T> testing;

    /**
     * Creates a new instance of {@code SwingBase}.
     *
     * @param testing The class being tested.
     */
    protected SwingBase(final Class<T> testing) {
        assertTrue(testing.desiredAssertionStatus());
        this.testing = testing;
    }

    /**
     * If the widgets are to be show, prepares the desktop pane which will contain them.
     * This method is invoked by JUnit and should not be invoked directly.
     *
     * @throws HeadlessException If the current environment does not allow the display of widgets.
     */
    @BeforeClass
    public static synchronized void prepareDesktop() throws HeadlessException {
        desktop = null; // Safety in case of failures in previous tests.
        if (Boolean.getBoolean("org.geotoolkit.showWidgetTests")) {
            desktop = new DesktopPane();
            desktop.createFrame().setVisible(true);
        }
    }

    /**
     * Returns {@code true} if the display of widgets is enabled.
     *
     * @return {@code true} if the display of widgets is enabled.
     */
    public static synchronized boolean isDisplayEnabled() {
        return desktop != null;
    }

    /**
     * Creates the widget. The widget is usually of type {@code T}, except if the
     * widget has been put in a scroll pane.
     * <p>
     * This method can return {@code null} if the widget can not be created for a
     * raison which is not a test failure, for example if the widget relies on some
     * resources which may not be available on the classpath.
     *
     * @return The created widget, or {@code null} if the widget can not be created for
     *         an acceptable raison.
     * @throws Exception If an exception occured while creating the widget.
     */
    protected abstract JComponent create() throws Exception;

    /**
     * {@linkplain #create() Creates} the widget. If the "{@code org.geotoolkit.showWidgetTests}"
     * system property is set to "{@code true}", then the widget will be show as an internal
     * frame in the desktop.
     *
     * @throws Exception If an exception occured while creating the widget.
     */
    @Test
    public void display() throws Exception {
        final JComponent component = create();
        assumeNotNull(component);
        show(this, component);
    }

    /**
     * Show the given component, if the test is allowed to display widgets and
     * the given component is not null.
     *
     * @param  testCase The test case for which the component is added.
     * @param  component The component to show, or {@code null} if none.
     * @throws PropertyVetoException Should not happen.
     */
    static synchronized void show(final SwingBase<?> testCase, final JComponent component)
            throws PropertyVetoException
    {
        if (desktop != null) {
            desktop.addTestCase(testCase);
            if (component != null) {
                desktop.show(component);
            }
        }
    }

    /**
     * If a frame has been created, wait for its disposal. This method is invoked by JUnit
     * and should not be invoked directly.
     *
     * @throws InterruptedException If the current thread has been interrupted while
     *         we were waiting for the frame disposal.
     */
    @AfterClass
    public static synchronized void waitForFrameDisposal() throws InterruptedException {
        if (desktop != null) {
            desktop.lock.await();
            desktop = null;
        }
    }
}
