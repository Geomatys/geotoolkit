/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import org.opengis.referencing.operation.Matrix;
import org.opengis.test.referencing.AffineTransformTest;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.referencing.operation.MathTransformFactoryAdapter;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the {@link ProjectiveTransform} class. We use the {@link AffineTransform2D} class
 * as a reference, so we need to avoid NaN values. Note that {@link CopyTransformTest} will
 * use {@code ProjectiveTransform} as a reference, this time with NaN values.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@RunWith(JUnit4.class)
@DependsOn(AbstractMathTransformTest.class)
public final strictfp class ProjectiveTransformTest extends AffineTransformTest {
    /**
     * Creates a new test suite.
     */
    public ProjectiveTransformTest() {
        super(new MathTransformFactoryAdapter() {
            @Override
            public ProjectiveTransform createAffineTransform(final Matrix matrix) {
                if (matrix.getNumRow() == 3 && matrix.getNumCol() == 3) {
                    return new ProjectiveTransform2D(matrix);
                }
                return new ProjectiveTransform(matrix);
            }
        });
    }

    /*
     * Inherit all the tests from GeoAPI.
     */
}
