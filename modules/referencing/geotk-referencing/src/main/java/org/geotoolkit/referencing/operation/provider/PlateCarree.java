/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Plate Carrée</cite>" projection. This is a special case of
 * {@linkplain EquidistantCylindrical Equidistant Cylindrical} with the latitude of
 * natural origin at equator.
 *
 * @author John Grange
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public class PlateCarree extends EquidistantCylindrical {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 8535645757318203345L;

    /**
     * The parameters group. A "<cite>Plate Carrée</cite>" alias is declared for the EPSG
     * authority, but EPSG do not specifically defines this name. However EPSG mentions it
     * in the comment attached to the Equidistant Cylindrical case.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(new NamedIdentifier[] {
            new NamedIdentifier(Citations.OGC,  "Equirectangular"),
            new NamedIdentifier(Citations.ESRI, "Plate_Carree"),
            new NamedIdentifier(Citations.EPSG, "Plate Carrée")
        }, new ParameterDescriptor[] {
            SEMI_MAJOR,       SEMI_MINOR,
            ROLL_LONGITUDE,   CENTRAL_MERIDIAN,
            FALSE_EASTING,    FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public PlateCarree() {
        super(PARAMETERS);
    }
}
