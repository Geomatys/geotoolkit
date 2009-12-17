/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.referencing.datum.BursaWolfParameters;


/**
 * The provider for "<cite>Coordinate Frame Rotation</cite>" (EPSG:9607). This is the same
 * transformation than "{@linkplain PositionVector7Param Position Vector 7-param."} except
 * that the rotation angles have the opposite sign.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.07
 *
 * @since 2.2
 * @module
 */
@Immutable
public class CoordinateFrameRotation extends PositionVector7Param {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 5513675854809530038L;

    /**
     * The parameters group. This is the same group than
     * {@link PositionVector7Param#PARAMETERS} except for the name.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(9607,
            "Coordinate Frame Rotation (geog2D domain)", "Coordinate Frame Rotation");

    /**
     * Constructs the provider.
     */
    public CoordinateFrameRotation() {
        super(PARAMETERS);
    }

    /**
     * Fills the given Bursa-Wolf parameters according the specified values.
     */
    @Override
    void fill(final BursaWolfParameters parameters, final ParameterValueGroup values) {
        super.fill(parameters, values);
        parameters.ex = -parameters.ex;
        parameters.ey = -parameters.ey;
        parameters.ez = -parameters.ez;
    }
}
