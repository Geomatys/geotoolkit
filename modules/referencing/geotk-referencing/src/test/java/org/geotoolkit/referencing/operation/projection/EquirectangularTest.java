/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.projection;

import java.awt.geom.AffineTransform;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;

import org.junit.*;
import org.apache.sis.test.DependsOn;
import org.geotoolkit.referencing.operation.transform.CoordinateDomain;

import static org.opengis.test.Assert.*;
import static org.geotoolkit.referencing.operation.provider.EquidistantCylindrical.PARAMETERS;


/**
 * Tests the {@link Equirectangular} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class EquirectangularTest extends ProjectionTestBase {
    /**
     * Tolerance level for comparing floating point numbers.
     */
    private static final double TOLERANCE = 1E-12;

    /**
     * Creates a default test suite.
     */
    public EquirectangularTest() {
        super(Equirectangular.class, null);
    }

    /**
     * Returns a new instance of {@link Equirectangular}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    static Equirectangular create(final boolean ellipse) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        return new Equirectangular(parameters);
    }

    /**
     * Tests the unitary projection on an ellipse.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testUnitaryOnEllipse() throws TransformException {
        transform = create(true);
        tolerance = TOLERANCE;
        validate();
        stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 366685805);
    }

    /**
     * Tests longitude rolling.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testLongitudeRolling() throws TransformException {
        tolerance = TOLERANCE;
        for (int centralMeridian=-180; centralMeridian<=180; centralMeridian+=45) {
            final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, false);
            parameters.centralMeridian = centralMeridian;
            transform = new Equirectangular(parameters);
            validate();
            stressLongitudeRolling(CoordinateDomain.GEOGRAPHIC);
        }
    }

    /**
     * Tests the capability of the factory to simplify a equirectangular projection to an
     * affine transform when there is no longitude rolling to apply.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testSimplification() throws FactoryException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Equirectangular");
        parameters.parameter("semi-major axis").setValue(6378245.0);
        parameters.parameter("semi-minor axis").setValue(6378245.0 * (1 - 1/298.3));
        parameters.parameter("Longitude of natural origin").setValue(50.0);
        transform = mtFactory.createParameterizedTransform(parameters);
//        assertInstanceOf("Expected the full transform.", ConcatenatedTransform.class, transform);

        parameters.parameter("Longitude of natural origin").setValue(0.0);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertInstanceOf("Expected simplification.", AffineTransform.class, transform);
    }
}
