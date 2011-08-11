/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit;

import java.util.EnumSet;
import java.util.Properties;

import org.opengis.util.Factory;
import org.opengis.util.NameFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.test.ImplementationDetails;
import org.opengis.test.ToleranceModifier;
import org.opengis.test.TestSuite;

import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;

import static org.opengis.test.CalculationType.*;
import static org.opengis.test.ToleranceModifiers.*;
import static org.geotoolkit.referencing.IdentifiedObjects.*;
import static org.geotoolkit.factory.AuthorityFactoryFinder.*;


/**
 * Runs the GeoAPI {@link TestSuite}. This class inherits all the tests defined in the
 * {@code geoapi-conformance} module. GeoAPI scans for all factories declared in the
 * {@code META-INF/services/*} files found on the classpath, excluding some of them
 * according the criterion defined in this class.
 * <p>
 * Note that there is a few other Java files named {@code GeoapiTest} in various sub-packages.
 * Those files extend directly one specific GeoAPI {@link org.opengis.test.TestCase} in order
 * to control better the test configuration, and for easier debugging.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see org.geotoolkit.naming.GeoapiTest
 * @see org.geotoolkit.referencing.factory.GeoapiTest
 * @see org.geotoolkit.referencing.factory.epsg.GeoapiTest
 * @see org.geotoolkit.referencing.operation.projection.integration.GeoapiTest
 * @see org.geotoolkit.GeoapiTest
 *
 * @since 3.19
 */
public final class GeoapiTest extends TestSuite implements ImplementationDetails {
    /**
     * Fix the authority factories to use for testing purpose. We do not rely on the
     * {@code META-INF/services/} discovery mechanism because the same implementation
     * is often used for many services.
     */
    static {
        setFactories(NameFactory.class,                         getNameFactory                        (        null));
        setFactories(CSFactory.class,                           getCSFactory                          (        null));
        setFactories(CRSFactory.class,                          getCRSFactory                         (        null));
        setFactories(DatumFactory.class,                        getDatumFactory                       (        null));
        setFactories(CoordinateOperationFactory.class,          getCoordinateOperationFactory         (        null));
        setFactories(CSAuthorityFactory.class,                  getCSAuthorityFactory                 ("EPSG", null));
        setFactories(CRSAuthorityFactory.class,                 getCRSAuthorityFactory                ("EPSG", null));
        setFactories(DatumAuthorityFactory.class,               getDatumAuthorityFactory              ("EPSG", null));
        setFactories(CoordinateOperationAuthorityFactory.class, getCoordinateOperationAuthorityFactory("EPSG", null));
    }

    /**
     * Returns unconditionally {@code true} since there is not factory to exclude, because we
     * declared in the static initializer all the factories we want to test.
     */
    @Override
    public <T extends Factory> boolean filter(final Class<T> category, final T factory) {
        return true;
    }

    /**
     * Unconditionally returns {@code null}, since we do not disable any test.
     */
    @Override
    public Properties configuration(final Factory... factories) {
        return null;
    }

    /**
     * Relaxes the tolerance threshold for some Geotk transforms.
     */
    @Override
    public ToleranceModifier needsRelaxedTolerance(final MathTransform transform) {
        if (transform instanceof AbstractMathTransform) {
            final IdentifiedObject id = ((AbstractMathTransform) transform).getParameterDescriptors();
            if (id != null) {
                if (nameMatches(id, "Lambert_Azimuthal_Equal_Area")) {
                    // Increase to 10 cm the tolerance factor in latitude for inverse projections.
                    return scale(EnumSet.of(INVERSE_TRANSFORM), 1, 10);
                }
                if (nameMatches(id, "Cassini_Soldner")) {
                    // Increase to 10 cm the tolerance factor in latitude for direct projections,
                    // and 50 cm the tolerance factor in latitude for inverse projections.
                    return maximum(scale(EnumSet.of(DIRECT_TRANSFORM),  1,  10),
                                   scale(EnumSet.of(INVERSE_TRANSFORM), 2, 200));
                }
            }
        }
        return null;
    }
}
