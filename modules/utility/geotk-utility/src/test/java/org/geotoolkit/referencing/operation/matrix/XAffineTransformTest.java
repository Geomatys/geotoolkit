/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.matrix;

import java.awt.geom.AffineTransform;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link XAffineTransform} static methods.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.14
 *
 * @since 2.3
 */
public final strictfp class XAffineTransformTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests the {@link XAffineTransform#roundIfAlmostInteger} method.
     */
    @Test
    public void testRoundIfAlmostInteger() {
        final AffineTransform test = new AffineTransform(4, 0, 0, 4, -400, -1186);
        final AffineTransform copy = new AffineTransform(test);
        XAffineTransform.roundIfAlmostInteger(test, 1E-6);
        assertEquals("Translation terms were already integers, so the " +
                "transform should not have been modified.", copy, test);

        test.translate(1E-8, 2E-8);
        XAffineTransform.roundIfAlmostInteger(test, 1E-9);
        assertFalse("Treshold was smaller than the translation, so the " +
                "transform should not have been modified.", copy.equals(test));

        XAffineTransform.roundIfAlmostInteger(test, 1E-6);
        assertEquals("Translation terms should have been rounded.", copy, test);
    }
}
