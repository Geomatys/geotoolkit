/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.image;

import org.geotoolkit.gui.test.SwingBase;

import org.junit.*;
import static org.junit.Assume.*;


/**
 * Tests the {@link ImageFileChooser}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.05
 *
 * @since 3.00
 */
public final class ImageFileChooserTest extends SwingBase<ImageFileChooser> {
    /**
     * Constructs the test case.
     */
    public ImageFileChooserTest() {
        super(ImageFileChooser.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected ImageFileChooser create() {
        final ImageFileChooser test = new ImageFileChooser("png");
        test.setDialogType(ImageFileChooser.OPEN_DIALOG);
        test.setApproveButtonText("Noop");
        return test;
    }

    /**
     * Tests the usage of the {@link ImageFileChooser#showOpenDialog} method.
     */
    @Test
    public void testOpenDialog() {
        assumeTrue(isDisplayEnabled());
        final ImageFileChooser test = new ImageFileChooser("png", true);
        test.showOpenDialog(null);
    }
}
