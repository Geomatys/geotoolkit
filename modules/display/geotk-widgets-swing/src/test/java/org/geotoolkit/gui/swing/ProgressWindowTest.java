/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.util.ProgressListener;

import org.junit.*;


/**
 * Tests {@link ProgressWindow}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.0
 */
public class ProgressWindowTest extends WidgetTestCase {
    /**
     * Constructs the test case.
     */
    public ProgressWindowTest() {
        super(ProgressWindow.class);
    }

    /**
     * Test the progress listener with a progress ranging from 0 to 100%.
     *
     * @throws InterruptedException Should never happen.
     */
    @Test
    public void testProgress() throws InterruptedException {
        if (displayEnabled) try {
            final ProgressListener progress = new ProgressWindow(null);
            progress.started();
            for (int i=0; i<=100; i++) {
                progress.progress(i);
                Thread.sleep(100);
                if (i==40 || i==80) {
                    progress.warningOccurred("Some source", "(" + i + ")", "A dummy warning.");
                }
            }
            progress.complete();
        } catch (HeadlessException e) {
            // do nothing
        }
    }
}
