/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image;

import java.io.IOException;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;

import org.geotoolkit.test.Commons;
import org.geotoolkit.test.image.ImageTestBase;

import static org.junit.Assert.*;


/**
 * Base class for tests applied on images enumerated in the {@link SampleImage} enum.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.00
 */
public abstract strictfp class SampleImageTestBase extends ImageTestBase {
    /**
     * Creates a new test suite for the given class.
     *
     * @param testing The class to be tested.
     */
    protected SampleImageTestBase(final Class<?> testing) {
        super(testing);
    }

    /**
     * Loads the given sample image. The result is stored in the {@link #image} field.
     * Note that the returned image may be a cached instance, so it should not be modified.
     * For an image that can be modified, use {@link #copyImage()}.
     *
     * @param  s The enum for the sample image to load.
     */
    protected final synchronized void loadSampleImage(final SampleImage s) {
        try {
            image = s.load();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Applies a unary operation on the current image using the given parameters.
     *
     * @param parameters The parameters, without any source. The current {@linkplain #image}
     *        will be added directly as the source in the given parameter block.
     * @param checksum The checksum of the expected result, or 0 for ignoring it.
     */
    protected final synchronized void applyUnary(final ParameterBlockJAI parameters, final long checksum) {
        final String operation = parameters.getOperationDescriptor().getName();
        image = JAI.create(operation, parameters.addSource(image));
        if (checksum != 0) {
            String message = "Checksum failed for operation \"" + operation + "\".";
            assertEquals(message, checksum, Commons.checksum(image));
        }
    }
}
