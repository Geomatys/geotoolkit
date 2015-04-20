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

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.provider.PositionVector7Param;
import org.geotoolkit.referencing.operation.provider.GeocentricTranslation;
import org.geotoolkit.referencing.operation.provider.CoordinateFrameRotation;
import org.apache.sis.referencing.operation.transform.CoordinateDomain;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.test.DependsOn;
import org.junit.*;


/**
 * Tests the {@link GeocentricAffineTransform} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 */
//@DependsOn({ProjectiveTransformTest.class, GeocentricTransformTest.class, ConcatenatedTransformTest.class})
public final strictfp class GeocentricAffineTransformTest extends TransformTestBase {
    /**
     * Creates a new test suite.
     */
    public GeocentricAffineTransformTest() {
        super(GeocentricAffineTransform.class, null);
    }

    /**
     * Test case using example from EPSG Guidance Note number 7 part 2 (May 2005),
     * section 2.4.3.1.
     *
     * @throws FactoryException   Should never occur.
     * @throws TransformException Should never occur.
     */
    @Test
    public void testTranslation() throws FactoryException, TransformException {
        final String classification = "Geocentric translations";
        final ParameterValueGroup param = mtFactory.getDefaultParameters(classification);

        param.parameter("dx").setValue( 84.87);
        param.parameter("dy").setValue( 96.49);
        param.parameter("dz").setValue(116.95);

        transform = mtFactory.createParameterizedTransform(param);
        assertParameterEquals(GeocentricTranslation.PARAMETERS, param);
        validate();

        tolerance = 0.005;
        verifyTransform(new double[] {3771793.97, 140253.34, 5124304.35},
                        new double[] {3771878.84, 140349.83, 5124421.30});

        // Do not stress this transform. Stressing testSevenParam() should be sufficient.
    }

    /**
     * Test case using example from EPSG Guidance Note number 7 part 2 (May 2005),
     * section 2.4.3.2.1.
     *
     * @throws FactoryException   Should never occur.
     * @throws TransformException Should never occur.
     */
    @Test
    public void testSevenParam() throws FactoryException, TransformException {
        final String classification = "Position Vector 7-param. transformation";
        final ParameterValueGroup param = mtFactory.getDefaultParameters(classification);

        param.parameter("dx") .setValue(0.000);
        param.parameter("dy") .setValue(0.000);
        param.parameter("dz") .setValue(4.5);
        param.parameter("ex") .setValue(0.000);
        param.parameter("ey") .setValue(0.000);
        param.parameter("ez") .setValue(0.554);
        param.parameter("ppm").setValue(0.219);

        tolerance = 1E-10;
        transform = mtFactory.createParameterizedTransform(param);
        assertParameterEquals(PositionVector7Param.PARAMETERS, param);
        validate();

        tolerance = 0.01;
        verifyTransform(new double[] {3657660.66, 255768.55, 5201382.11},
                        new double[] {3657660.78, 255778.43, 5201387.75});

        isDerivativeSupported = false;  // TODO
        verifyInDomain(CoordinateDomain.GEOCENTRIC, 943559739);
    }

    /**
     * Test case using example from EPSG Guidance Note number 7 part 2 (May 2005),
     * section 2.4.3.2.2.
     *
     * @throws FactoryException   Should never occur.
     * @throws TransformException Should never occur.
     */
    @Test
    public void testFrameRotation() throws FactoryException, TransformException {
        final String classification = "Coordinate Frame rotation";
        final ParameterValueGroup param = mtFactory.getDefaultParameters(classification);

        param.parameter("dx") .setValue( 0.000);
        param.parameter("dy") .setValue( 0.000);
        param.parameter("dz") .setValue( 4.5);
        param.parameter("ex") .setValue( 0.000);
        param.parameter("ey") .setValue( 0.000);
        param.parameter("ez") .setValue(-0.554);
        param.parameter("ppm").setValue( 0.219);

        tolerance = 1E-10;
        transform = mtFactory.createParameterizedTransform(param);
        assertParameterEquals(CoordinateFrameRotation.PARAMETERS, param);
        validate();

        tolerance = 0.01;
        verifyTransform(new double[] {3657660.66, 255768.55, 5201382.11},
                        new double[] {3657660.78, 255778.43, 5201387.75});

        // Do not stress this transform, since it is identical to testSevenParam().
    }

    /**
     * Tests the creation with geocentric transforms.
     *
     * {@note The expected values here are approximatives since we didn't
     *        used an external source of test points for this test.}
     *
     * @throws FactoryException   Should never occur.
     * @throws TransformException Should never occur.
     */
    @Test
    public void testGeotoolkitExtensions() throws FactoryException, TransformException {
        final String classification = "Coordinate Frame rotation";
        final ParameterValueGroup param = mtFactory.getDefaultParameters(classification);
        final Ellipsoid sourceEllipsoid = CommonCRS.ED50.ellipsoid();
        final Ellipsoid targetEllipsoid = CommonCRS.WGS84.ellipsoid();

        param.parameter("dx") .setValue( 0.000);
        param.parameter("dy") .setValue( 0.000);
        param.parameter("dz") .setValue( 4.5);
        param.parameter("ex") .setValue( 0.000);
        param.parameter("ey") .setValue( 0.000);
        param.parameter("ez") .setValue(-0.554);
        param.parameter("ppm").setValue( 0.219);

        param.parameter("src_dim").setValue(3);
        param.parameter("tgt_dim").setValue(3);
        param.parameter("src_semi_major").setValue(sourceEllipsoid.getSemiMajorAxis());
        param.parameter("src_semi_minor").setValue(sourceEllipsoid.getSemiMinorAxis());
        param.parameter("tgt_semi_major").setValue(targetEllipsoid.getSemiMajorAxis());
        param.parameter("tgt_semi_minor").setValue(targetEllipsoid.getSemiMinorAxis());

        transform = mtFactory.createParameterizedTransform(param);
        validate();

        tolerance = 0.015;
        verifyTransform(new double[] {4.00, 55.00, -191.61},  // (longitude, latitude, height)
                        new double[] {4.00, 55.00,    3.23});

        isDerivativeSupported = false;  // TODO
        verifyInDomain(CoordinateDomain.GEOGRAPHIC, 699525038);
    }
}
