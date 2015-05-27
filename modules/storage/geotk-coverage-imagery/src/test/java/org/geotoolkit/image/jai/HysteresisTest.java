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
package org.geotoolkit.image.jai;

import javax.media.jai.JAI;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.registry.RenderedRegistryMode;

import org.geotoolkit.image.SampleImageTestBase;
import org.geotoolkit.internal.image.jai.HysteresisDescriptor;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link Hysteresis}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class HysteresisTest extends SampleImageTestBase {
    /**
     * Creates a new test case.
     */
    public HysteresisTest() {
        super(Hysteresis.class);
    }

    /**
     * Ensures that the JAI registration has been done.
     */
    @Test
    public void testRegistration() {
        final RegistryElementDescriptor descriptor = JAI.getDefaultInstance().getOperationRegistry()
                .getDescriptor(RenderedRegistryMode.MODE_NAME, Hysteresis.OPERATION_NAME);
        assertNotNull("Descriptor not found.", descriptor);
        assertTrue(descriptor instanceof HysteresisDescriptor);
    }
}
