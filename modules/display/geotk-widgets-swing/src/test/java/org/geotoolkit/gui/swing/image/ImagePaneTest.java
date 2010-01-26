/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.gui.swing.image;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.geotoolkit.test.TestData;
import org.geotoolkit.test.gui.SwingBase;
import org.geotoolkit.internal.SwingUtilities;


/**
 * Tests the {@link ImagePane}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
public final class ImagePaneTest extends SwingBase<ImagePane> {
    /**
     * Constructs the test case.
     */
    public ImagePaneTest() {
        super(ImagePane.class);
    }

    /**
     * Creates the widget. This method loads {@code "QL95209.png"} if it is accessible as a URL.
     * This is the case when testing from an IDE like NetBeans, but not during Maven test phase
     * because the {@code "QL95209.png"} file is stored in a different module (geotk-coverage).
     *
     * @throws IOException If an error occured while reading the test file.
     */
    @Override
    protected ImagePane create() throws IOException {
        final ImagePane test = new ImagePane();
        try {
            test.setImage(ImageIO.read(TestData.url(org.geotoolkit.image.ImageInspector.class, "QL95209.png")));
        } catch (IOException e) {
            test.setError(e);
        }
        return test;
    }

    /**
     * Shows the progress bar.
     *
     * @throws InterruptedException If the test has been interrupted.
     */
    @Override
    protected void animate(final JComponent component) throws InterruptedException {
        final ImagePane pane = (ImagePane) component;
        for (int i=0; i<=100; i++) {
            final int percentageDone = i;
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override public void run() {
                    if (percentageDone == 0) {
                        pane.setProgressVisible(true);
                    }
                    pane.setProgress(percentageDone);
                    if (percentageDone == 100) {
                        pane.setProgressVisible(false);
                    }
                }
            });
            Thread.sleep(100);
        }
        System.out.println("Done.");
    }
}
