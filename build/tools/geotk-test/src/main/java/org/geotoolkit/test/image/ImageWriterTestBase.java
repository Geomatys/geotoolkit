/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import java.io.IOException;
import javax.imageio.ImageWriter;


/**
 * The base class for {@link ImageWriter} tests.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
public abstract class ImageWriterTestBase extends ImageTestBase {
    /**
     * Creates a new test suite for the given class.
     *
     * @param testing The class to be tested.
     */
    protected ImageWriterTestBase(final Class<? extends ImageWriter> testing) {
        super(testing);
    }

    /**
     * Creates the image writer.
     *
     * @return The writer to test.
     * @throws IOException If an error occurred while creating the writer.
     */
    protected abstract ImageWriter createImageWriter() throws IOException;
}
