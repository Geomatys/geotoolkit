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

import java.io.File;
import java.io.IOException;
import org.geotoolkit.test.TestData;
import org.geotoolkit.test.gui.SwingTestBase;


/**
 * Tests the {@link ImageFileProperties}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
public class ImageFilePropertiesTest extends SwingTestBase<ImageFileProperties> {
    /**
     * Constructs the test case.
     */
    public ImageFilePropertiesTest() {
        super(ImageFileProperties.class);
    }

    /**
     * Creates the widget. This method loads {@code "QL95209.png"} if it is accessible as a file
     * (not as a URL to an entry in a JAR file). This is the case when testing from an IDE like
     * NetBeans, but not during Maven test phase because the {@code "QL95209.png"} file is stored
     * in a different module (geotk-coverage).
     *
     * @throws IOException If an error occurred while reading the test file.
     */
    @Override
    protected ImageFileProperties create(final int index) throws IOException {
        final ImageFileProperties test = new ImageFileProperties();
        final File file;
        try {
            file = TestData.file(org.geotoolkit.image.ImageInspector.class, "QL95209.png");
        } catch (IOException e) {
            return test;
        }
        test.setImage(file);
        return test;
    }
}
