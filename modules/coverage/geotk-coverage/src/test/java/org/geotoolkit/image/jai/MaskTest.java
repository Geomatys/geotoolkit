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

import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.registry.RenderedRegistryMode;

import org.geotoolkit.test.Depend;
import org.geotoolkit.image.SampleImage;
import org.geotoolkit.image.SampleImageTestBase;
import org.geotoolkit.internal.image.jai.MaskDescriptor;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link Mask}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 */
@Depend(SilhouetteMaskTest.class)
public final strictfp class MaskTest extends SampleImageTestBase {
    /**
     * Creates a new test case.
     */
    public MaskTest() {
        super(Mask.class);
    }

    /**
     * Ensures that the JAI registration has been done.
     */
    @Test
    @Ignore("Fails randomly, need investigation.")
    public void testRegistration() {
        final RegistryElementDescriptor descriptor = JAI.getDefaultInstance().getOperationRegistry()
                .getDescriptor(RenderedRegistryMode.MODE_NAME, Mask.OPERATION_NAME);
        assertNotNull("Descriptor not found.", descriptor);
        assertTrue(descriptor instanceof MaskDescriptor);
    }

    /**
     * Applies the operation and tests the result with the expected one, using checksum.
     */
    @Test
    @Ignore("Fails randomly, need investigation.")
    public void testOnRGB() {
        loadSampleImage(SampleImage.RGB_ROTATED);
        ParameterBlockJAI parameters = new ParameterBlockJAI(SilhouetteMask.OPERATION_NAME);
        RenderedImage mask = JAI.create(SilhouetteMask.OPERATION_NAME, parameters.addSource(image));

        final double[] blueColor = new double[] {64, 64, 255};
        parameters = new ParameterBlockJAI(Mask.OPERATION_NAME);
        image = JAI.create(Mask.OPERATION_NAME, parameters.addSource(image).addSource(mask).set(blueColor, 0));
        assertCurrentChecksumEquals("testOnRGB", 2300860193L, 3418587197L);
        showCurrentImage("testOnRGB");
    }

    /**
     * Applies the operation and tests the result with the expected one, using checksum.
     */
    @Test
    @Ignore("Fails randomly, need investigation.")
    public void testOnIndexed() {
        loadSampleImage(SampleImage.INDEXED);
        ParameterBlockJAI parameters = new ParameterBlockJAI(SilhouetteMask.OPERATION_NAME);
        parameters.setParameter("background", new double[][] {{255}});
        RenderedImage mask = JAI.create(SilhouetteMask.OPERATION_NAME, parameters.addSource(image));

        final double[] newValues = new double[] {0};
        parameters = new ParameterBlockJAI(Mask.OPERATION_NAME);
        image = JAI.create(Mask.OPERATION_NAME, parameters.addSource(image).addSource(mask).set(newValues, 0));
        assertCurrentChecksumEquals("testOnIndexed", 3577749049L);
        showCurrentImage("testOnIndexed");
    }
}
