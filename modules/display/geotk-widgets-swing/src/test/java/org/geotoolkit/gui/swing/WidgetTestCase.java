/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.gui.swing;

import javax.swing.JFrame;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.concurrent.CountDownLatch;

import org.junit.*;
import org.geotoolkit.util.converter.Classes;

import static org.junit.Assert.*;


/**
 * Base class for tests on widgets. Widgets will be displayed only if {@link #displayEnabled}
 * is set to {@code true}. Otherwise the test suite merely checks that no exception are thrown
 * during widget construction.
 *
 * @param <T> The type of the widget to be tested.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.01
 *
 * @since 2.3
 */
public abstract class WidgetTestCase<T extends Component> {
    /**
     * Set to {@code true} if windows should be visible.
     */
    protected boolean displayEnabled;

    /**
     * The widget being tested.
     */
    protected Component component;

    /**
     * A lock used for waiting that at least one frame has been closed.
     */
    private transient CountDownLatch lock;

    /**
     * Creates a new instance of {@code WidgetTestCase}.
     *
     * @param testing The class being tested.
     */
    protected WidgetTestCase(final Class<T> testing) {
        assertTrue(testing.desiredAssertionStatus());
    }

    /**
     * Creates the widget. The widget is usually of type {@code T}, except if the
     * widget has been put in a scroll pane.
     *
     * @return The created widget.
     * @throws Exception If an exception occured while creating the widget.
     */
    protected abstract Component create() throws Exception;

    /**
     * {@linkplain #create() Creates} the widget and stores the reference in {@linkplain #component}.
     * If {@link #displayEnabled} is {@code true}, then the widget is displayed in a frame.
     *
     * @throws Exception If an exception occured while creating the widget.
     */
    @Test
    public final void display() throws Exception {
        component = create();
        show(Classes.getShortClassName(component));
    }

    /**
     * Shows the {@linkplain #component} in a frame if {@link #displayEnabled} is {@code true}.
     *
     * @param title The window title.
     */
    protected synchronized final void show(final String title) {
        if (displayEnabled) try {
            if (lock == null) {
                lock = new CountDownLatch(1);
            }
            final CountDownLatch lock = this.lock;
            final JFrame frame = new JFrame(title);
            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowClosed(final WindowEvent event) {
                    frame.removeWindowListener(this);
                    lock.countDown();
                    frame.dispose();
                }
            });
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(component);
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        } catch (HeadlessException exception) {
            // The test is running on a machine without display. Ignore.
        }
    }

    /**
     * If a frame has been created by {@link #show}, wait for its disposal
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
