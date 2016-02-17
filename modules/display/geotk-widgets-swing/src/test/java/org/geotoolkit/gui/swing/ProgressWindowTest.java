/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import java.awt.HeadlessException;
import org.geotoolkit.process.ProgressController;

import org.junit.*;


/**
 * Tests {@link ProgressWindow}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.0
 */
public final strictfp class ProgressWindowTest extends org.geotoolkit.test.TestBase {
    /**
     * Set to {@code true} if windows should be visible.
     */
    private static final boolean displayEnabled = false;

    /**
     * Creates the widget. If {@link #displayEnabled} is {@code true}, then the widget is shown.
     * The progress listener is tested with a progress ranging from 0 to 100%.
     *
     * @throws InterruptedException Should never happen.
     */
    @Test
    public void display() throws InterruptedException {
        if (displayEnabled) try {
            final ProgressController progress = new ProgressWindow(null);
            progress.started();
            for (int i=0; i<=100; i++) {
                progress.setProgress(i);
                Thread.sleep(100);
                switch (i) {
                    case 20: {
                        progress.setTask("Description of the task.");
                        break;
                    }
                    case 40:
                    case 80: {
                        progress.warningOccurred("Some source", "(" + i + ")", "A dummy warning.");
                        break;
                    }
                }
            }
            progress.completed();
        } catch (HeadlessException e) {
            // do nothing
        }
    }
}
