/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.internal.referencing.Identifiers;


/**
 * The provider for "<cite>Geocentric translation</cite>" (EPSG:9603). This is a special
 * case of "{@linkplain PositionVector7Param Position Vector 7-param. transformation"}
 * where only the translation terms can be set to a non-null value.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public class GeocentricTranslation extends PositionVector7Param {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -7160250630666911608L;

    /**
     * The parameters group. This is the same group than {@link PositionVector7Param#PARAMETERS}
     * minus the {@linkplain #EX ex}, {@linkplain #EY ey}, {@linkplain #EZ ez} and
     * {@linkplain #PPM ppm} parameters.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "Geocentric translations"),
            new IdentifierCode (Citations.EPSG,  9603)
        }, new ParameterDescriptor<?>[] {
            DX, DY, DZ,
            SRC_SEMI_MAJOR, SRC_SEMI_MINOR,
            TGT_SEMI_MAJOR, TGT_SEMI_MINOR,
            SRC_DIM, TGT_DIM
        });

    /**
     * Constructs the provider.
     */
    public GeocentricTranslation() {
        super(PARAMETERS);
    }

    /**
     * Fills the given Bursa-Wolf parameters according the specified values.
     * Only the translation terms are extracted from the given parameter values.
     */
    @Override
    void fill(final BursaWolfParameters parameters, final ParameterValueGroup values) {
        parameters.dx  = Parameters.doubleValue(DX, values);
        parameters.dy  = Parameters.doubleValue(DY, values);
        parameters.dz  = Parameters.doubleValue(DZ, values);
    }
}
