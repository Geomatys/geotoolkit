/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
import java.util.List;

import org.opengis.util.Factory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.test.ImplementationDetails;
import org.opengis.test.ToleranceModifier;
import org.opengis.test.FactoryFilter;
import org.opengis.test.Configuration;
import org.opengis.test.TestSuite;

import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryNotFoundException;
import org.geotoolkit.factory.Hints;

import org.apache.sis.referencing.operation.transform.MathTransforms;
import static org.opengis.test.CalculationType.*;
import static org.opengis.test.ToleranceModifiers.*;
import static org.apache.sis.referencing.IdentifiedObjects.*;


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
 * @see org.apache.sis.util.iso.GeoapiTest
 * @see org.geotoolkit.referencing.factory.GeoapiTest
 * @see org.geotoolkit.referencing.factory.epsg.GeoapiTest
 * @see org.geotoolkit.referencing.operation.transform.GeoapiTest
 * @see org.geotoolkit.referencing.operation.projection.GeoapiTest
 * @see org.geotoolkit.GeoapiTest
 *
 * @since 3.19
 *
 * @todo For a mysterious reason, some GIGS tests are skipped by this test suite (see the XML files
 *       in the {@code target/surefire-report} directory). We need more investigation about what is
 *       going on. Even when no tests is skipped, some tests are incomplete. For example GIGS 2008
 *       should fail when "gravity-related depth" is not on the list of allowed axis names - this
 *       is not currently the case.
 */
public final strictfp class GeoapiTest implements ImplementationDetails, FactoryFilter {
    /**
     * Whether the EPSG factory is available, or {@code null} if not yet tested.
     */
    private static Boolean isEpsgFactoryAvailable;

    /**
     * Returns {@code false} if the given factory is the Geotk EPSG factory but this factory
     * is declared unavailable. We also exclude every non-Geotk implementations as a safety.
     * <p>
     * Note that this method is not strictly necessary since the above static initialization
     * should have defined the appropriate factories. Nevertheless we do those checks as a
     * paranoiac safety.
     */
    @Override
    public <T extends Factory> boolean filter(final Class<T> category, final T factory) {
        if (!(factory.getClass().getName().startsWith("org.geotoolkit."))) {
            return false;
        }
        if (!AuthorityFactory.class.isAssignableFrom(category)) {
            return true;
        }
        /*
         * This check is similar to Commons.isEpsgFactoryAvailable(), but we have to perform
         * our own check because we can not depend on a class in the 'test' directory.  This
         * is because this class will be copied into the main JAR for allowing execution from
         * the geoapi-conformance widget.
         */
        if (isEpsgFactoryAvailable == null) {
            final Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, ThreadedEpsgFactory.class);
            try {
                AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
                isEpsgFactoryAvailable = Boolean.TRUE;
            } catch (FactoryNotFoundException e) {
                isEpsgFactoryAvailable = Boolean.FALSE;
            }
        }
        return isEpsgFactoryAvailable;
    }

    /**
     * Returns the configuration map, which lists the tests to disable.
     */
    @Override
    public Configuration configuration(final Factory... factories) {
        return null;
    }

    /**
     * Relaxes the tolerance threshold for some Geotk transforms.
     */
    @Override
    public ToleranceModifier tolerance(final MathTransform transform) {
        if (true) {
            return null;  // TODO
        }
        final List<MathTransform> steps = MathTransforms.getSteps(transform);
        final boolean isAlone = steps.size() == 1;
        for (final MathTransform step : steps) {
            while (step instanceof AbstractMathTransform) {
                final IdentifiedObject id = ((AbstractMathTransform) step).getParameterDescriptors();
                if (id != null) {
                    if (isHeuristicMatchForName(id, "Abridged_Molodenski")) {
                        // Increase to 2 cm the tolerance factor for datum shift.
                        return scale(EnumSet.of(DIRECT_TRANSFORM, INVERSE_TRANSFORM), 2, 2, 2);
                    }
                    if (isHeuristicMatchForName(id, "Lambert_Azimuthal_Equal_Area")) {
                        // Increase to 5 cm the tolerance factor in latitude for inverse projections.
                        return scale(EnumSet.of(INVERSE_TRANSFORM), isAlone ? 1 : 5, 5);
                    }
                    if (isHeuristicMatchForName(id, "Cassini_Soldner")) {
                        // Increase to 5 cm the tolerance factor in latitude for direct projections,
                        // and to 1 metres the tolerance factor in latitude for inverse projections.
                        if (isAlone) {
                            return maximum(scale(EnumSet.of(DIRECT_TRANSFORM),  1,  5),
                                           scale(EnumSet.of(INVERSE_TRANSFORM), 2, 100));
                        } else {
                            return scale(EnumSet.of(DIRECT_TRANSFORM, INVERSE_TRANSFORM), 250, 250);
                        }
                    }
                }
            }
        }
        return null;
    }
}
