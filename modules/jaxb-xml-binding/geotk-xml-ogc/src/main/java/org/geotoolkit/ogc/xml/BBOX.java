/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.ogc.xml;

import org.geotoolkit.gml.xml.Envelope;

/**
 *
 * @author guilhem
 */
public interface BBOX extends SpatialOperator {

    Envelope getEnvelope();

    String getPropertyName();

    String getSRS();

    /**
     * Assuming getExpression2() is a literal bounding box access
     * the minimum value for the first coordinate.
     *
     * @deprecated please use getExpression2(), to check for a literal Envelope.getMinimum(0)
     */
    double getMinX();

    /**
     * Assuming getExpression2() is a literal bounding box access
     * the minimum value for the second ordinate.
     * @deprecated please use getExpression2(), to check for a literal Envelope.getMinimum(1)
     */
    double getMinY();

    /**
     * Assuming getExpression2() is a literal bounding box access
     * the maximum value for the first ordinate.
     *
     * @deprecated please use getExpression2(), to check for a literal Envelope.getMaximum(0)
     */
    double getMaxX();

    /**
     * Assuming getExpression2() is a literal bounding box access
     * the maximum value for the second coordinate.
     * @deprecated please use getExpression2(), to check for a literal Envelope.getMaximum(1)
     */
    double getMaxY();
}
