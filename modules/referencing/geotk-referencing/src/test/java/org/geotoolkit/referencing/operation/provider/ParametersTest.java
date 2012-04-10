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

import java.util.Map;
import java.util.HashMap;
import java.util.IdentityHashMap;

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
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
        Affine                            .PARAMETERS,
        AlbersEqualArea                   .PARAMETERS,
        CassiniSoldner                    .PARAMETERS,
        CoordinateFrameRotation           .PARAMETERS,
        EllipsoidToGeocentric             .PARAMETERS,
        EquidistantCylindrical            .PARAMETERS,
        Exponential                       .PARAMETERS,
        GeocentricToEllipsoid             .PARAMETERS,
        GeocentricTranslation             .PARAMETERS,
        HotineObliqueMercator             .PARAMETERS,
        HotineObliqueMercator.TwoPoint    .PARAMETERS,
        Krovak                            .PARAMETERS,
        LambertAzimuthalEqualArea         .PARAMETERS,
        LambertConformal1SP               .PARAMETERS,
        LambertConformal2SP               .PARAMETERS,
        LambertConformal2SP.Belgium       .PARAMETERS,
        Logarithmic                       .PARAMETERS,
        LongitudeRotation                 .PARAMETERS,
        Mercator1SP                       .PARAMETERS,
        Mercator2SP                       .PARAMETERS,
        MillerCylindrical                 .PARAMETERS,
        Molodensky                        .PARAMETERS,
        NADCON                            .PARAMETERS,
        NTv2                              .PARAMETERS,
        NewZealandMapGrid                 .PARAMETERS,
        ObliqueMercator                   .PARAMETERS,
        ObliqueMercator.TwoPoint          .PARAMETERS,
        ObliqueStereographic              .PARAMETERS,
        Orthographic                      .PARAMETERS,
        PlateCarree                       .PARAMETERS,
        PolarStereographic                .PARAMETERS,
        PolarStereographic.North          .PARAMETERS,
        PolarStereographic.South          .PARAMETERS,
        PolarStereographic.VariantB       .PARAMETERS,
        Polyconic                         .PARAMETERS,
        PositionVector7Param              .PARAMETERS,
        PseudoMercator                    .PARAMETERS,
        RGF93                             .PARAMETERS,
        Stereographic                     .PARAMETERS,
        TransverseMercator                .PARAMETERS,
        TransverseMercator.SouthOrientated.PARAMETERS
    };

    /**
     * Ensures that every instance is unique. Actually this is not really a requirement.
     * This is only for sharing existing resources by avoiding unnecessary objects duplication.
     */
    @Test
    public void ensureUniqueness() {
        final Map<GeneralParameterDescriptor, String> groupNames = new IdentityHashMap<>();
        final Map<GeneralParameterDescriptor, GeneralParameterDescriptor> existings = new HashMap<>();
        for (final ParameterDescriptorGroup group : parameters) {
            final String name = group.getName().getCode();
            for (final GeneralParameterDescriptor param : group.descriptors()) {
                assertFalse("Parameter declared twice in the same group.",
                        name.equals(groupNames.put(param, name)));
                final GeneralParameterDescriptor existing = existings.put(param, param);
                if (existing != null && existing != param) {
                    fail("Parameter \"" + param.getName().getCode() + "\" defined in \"" + name +
                            "\" was already defined in \"" + groupNames.get(existing) +
                            "\". The same instance could be shared.");
                }
            }
        }
    }
}
