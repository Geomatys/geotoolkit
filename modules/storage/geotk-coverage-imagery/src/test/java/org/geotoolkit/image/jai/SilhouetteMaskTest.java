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
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.registry.RenderedRegistryMode;

import org.geotoolkit.image.SampleImage;
import org.geotoolkit.image.SampleImageTestBase;
import org.geotoolkit.internal.image.jai.SilhouetteMaskDescriptor;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link SilhouetteMask}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class SilhouetteMaskTest extends SampleImageTestBase {
    /**
     * Creates a new test case.
     */
    public SilhouetteMaskTest() {
        super(SilhouetteMask.class);
    }

    /**
     * Ensures that the JAI registration has been done.
     */
    @Test
    public void testRegistration() {
        final RegistryElementDescriptor descriptor = JAI.getDefaultInstance().getOperationRegistry()
                .getDescriptor(RenderedRegistryMode.MODE_NAME, SilhouetteMask.OPERATION_NAME);
        assertNotNull("Descriptor not found.", descriptor);
        assertTrue(descriptor instanceof SilhouetteMaskDescriptor);
    }

    /**
     * Applies the operation and tests the result with the expected one, using checksum.
     */
    @Test
    public void testOnRGB() {
        loadSampleImage(SampleImage.RGB_ROTATED);
        final ParameterBlockJAI parameters = new ParameterBlockJAI(SilhouetteMask.OPERATION_NAME);
        applyUnary(parameters, 2197236510L);
        showCurrentImage("testOnRGB");
    }

    /**
     * Applies the operation and tests the result with the expected one, using checksum.
     */
    @Test
    public void testOnIndexed() {
        loadSampleImage(SampleImage.INDEXED);
        final ParameterBlockJAI parameters = new ParameterBlockJAI(SilhouetteMask.OPERATION_NAME);
        parameters.setParameter("background", new double[][] {{255}});
        applyUnary(parameters, 3206331653L);
        showCurrentImage("testOnIndexed");
    }
}
