/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
  * @author Guilhem Legal (Geomatys)
 */
public interface AbstractGeometry extends AbstractGML {

    String getSrsName();

    void setSrsName(final String srsName);

    void setId(final String id);

    int getCoordinateDimension();

    /**
     * Direct binding with "srsDimension" GML attribute.
     * @return Null if no "srsDimension" attribute is defined on the geometry, or
     * a positive integer otherwise.
     */
    Integer getSrsDimension();

    CoordinateReferenceSystem getCoordinateReferenceSystem(final boolean longitudeFirst);
}
