/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import org.geotoolkit.gui.test.SwingBase;


/**
 * Tests the {@link ImageFileProperties}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
public class ImageFilePropertiesTest extends SwingBase<ImageFileProperties> {
    /**
     * Constructs the test case.
     */
    public ImageFilePropertiesTest() {
        super(ImageFileProperties.class);
    }

    /**
     * Creates the widget.
     *
     * @throws IOException If an error occured while reading the test file.
     */
    @Override
    protected ImageFileProperties create() throws IOException {
        final ImageFileProperties test = new ImageFileProperties();
        // TODO: set a file here.
        return test;
    }
}
