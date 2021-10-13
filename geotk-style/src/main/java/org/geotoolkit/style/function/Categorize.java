/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.style.function;

import java.util.Map;
import org.opengis.annotation.XmlElement;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;


/**
 * Categorization: The transformation of continuous values to distinct values. This is
 * for example needed to generate choropleth maps from continuous attributes.
 * Another example would be the stepwise selection of different text heights or line
 * widths in dependence from such an attribute.
 *
 * In case the Categorize (or Interpolate) function is used inside a RasterSymbolizer as a
 * ColorMap, the LookupValue is set to the fixed value “Rasterdata”.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/symbol">Symbology Encoding Implementation Specification 1.1.0</A>
 * @author Johann Sorel (Geomatys)
 */
@XmlElement("Categorize")
public interface Categorize extends Expression<Object,Object> {
    /**
     * Get lookup value.
     *
     * @return Expression
     */
    @XmlElement("LookupValue")
    Expression getLookupValue();

    /**
     * The Thresholds have to be specified in ascending order and (like the LookupValue)
     * have to be of a uniform and orderable type. The value of the function is determined by
     * looking up into which interval between two thresholds the LookupValue falls. The first
     * interval ranges from -Infinity to the first given threshold and the last one accordingly
     * from the last threshold to +Infinity.
     *
     */
    Map<Expression,Expression> getThresholds();

    /**
     * Get the function direction.
     * Default is SUCCEEDING.
     *
     * @return SUCCEEDING or PRECEDING
     */
    @XmlElement("ThreshholdsBelongTo")
    ThreshholdsBelongTo getBelongTo();

    Literal getFallbackValue();
}
