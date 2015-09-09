/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.referencing.operation.provider;

import java.util.List;

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.apache.sis.referencing.IdentifiedObjects;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Verifications on the {@code PARAMETERS} constant declared in each provider.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class ParametersTest {
    /**
     * The parameter groups to be tested.
     */
    private final ParameterDescriptorGroup[] parameters = {
        AbridgedMolodensky                .PARAMETERS,
        AlbersEqualArea                   .PARAMETERS,
        CassiniSoldner                    .PARAMETERS,
        CoordinateFrameRotation           .PARAMETERS,
        EllipsoidToGeocentric             .PARAMETERS,
        Exponential                       .PARAMETERS,
        GeocentricToEllipsoid             .PARAMETERS,
        GeocentricTranslation             .PARAMETERS,
        HotineObliqueMercator             .PARAMETERS,
        HotineObliqueMercator.TwoPoint    .PARAMETERS,
        Krovak                            .PARAMETERS,
        LambertAzimuthalEqualArea         .PARAMETERS,
        Logarithmic                       .PARAMETERS,
        Molodensky                        .PARAMETERS,
        NADCON                            .PARAMETERS,
        NTv2                              .PARAMETERS,
        NewZealandMapGrid                 .PARAMETERS,
        ObliqueMercator                   .PARAMETERS,
        ObliqueMercator.TwoPoint          .PARAMETERS,
        ObliqueStereographic              .PARAMETERS,
        Orthographic                      .PARAMETERS,
        Polyconic                         .PARAMETERS,
        PositionVector7Param              .PARAMETERS,
        RGF93                             .PARAMETERS,
        Stereographic                     .PARAMETERS
    };

    /**
     * Verify that the type of descriptor groups is consistent with the parameter that it contains.
     */
    @Test
    public void verifyParameterDescriptorGroupClass() {
        for (final ParameterDescriptorGroup group : parameters) {
            final String name = group.getName().getCode();
            final List<GeneralParameterDescriptor> descriptors = group.descriptors();
            final boolean hasAxes = UniversalParameters.SEMI_MAJOR.find(descriptors) != null;
            /*
             * Parameters shall have both semi-major and semi-minor axes, or none of them.
             */
            assertEquals(name, hasAxes, UniversalParameters.SEMI_MINOR.find(descriptors) != null);
            /*
             * Parameters having ellipsoid axes shall be instance of MapProjectionDescriptor.
             * Note that the "projection" name is a bit abusive since some other transforms
             * (e.g. Molodensky) fall also in this category.
             */
            assertEquals(name, hasAxes, group instanceof MapProjectionDescriptor);
            if (hasAxes) {
                final int supplement = ((MapProjectionDescriptor) group).supplement;
                assertTrue(name, (supplement & MapProjectionDescriptor.ADD_EARTH_RADIUS) != 0);
                final boolean hasStandardParallel1 = UniversalParameters.STANDARD_PARALLEL_1.find(descriptors) != null;
                final boolean hasStandardParallel2 = UniversalParameters.STANDARD_PARALLEL_2.find(descriptors) != null;
                boolean hasStandardParallel = false;
                for (final GeneralParameterDescriptor param : descriptors) {
                    if (IdentifiedObjects.isHeuristicMatchForName(group, MapProjectionDescriptor.STANDARD_PARALLEL)) {
                        assertFalse(name, hasStandardParallel);
                        hasStandardParallel = true;
                    }
                }
                /*
                 * The "standard_parallel_2" parameter can exist if, and only if, the "standard_parallel_1"
                 * parameter also exist (the converse is not necessary true). Parameter "standard_parallel"
                 * (without "1") may exist as an alias of "standard_parallel_1", but can not be a dynamic
                 * parameter and the "standard_parrallel_2" parameter must not exist (otherwise the numbering
                 * would be necessary).
                 */
                if ( hasStandardParallel2) assertTrue (name, hasStandardParallel1);
                if (!hasStandardParallel1) assertFalse(name, hasStandardParallel2);
                if (hasStandardParallel) {
                    assertTrue  (name, hasStandardParallel1);
                    assertFalse (name, hasStandardParallel2);
                    assertEquals(name, 0, supplement & MapProjectionDescriptor.ADD_STANDARD_PARALLEL);
                } else {
                    /*
                     * If there is no explicit "standard_parallel" parameter, then a dynamic parameter
                     * shall exist if and only if the map projection define 2 standard parallels.
                     */
//                  assertEquals(name, hasStandardParallel2, (supplement & MapProjectionDescriptor.ADD_STANDARD_PARALLEL) != 0);
                }
            }
        }
    }
}
