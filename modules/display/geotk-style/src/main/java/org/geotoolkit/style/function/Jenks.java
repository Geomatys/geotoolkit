/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.style.function;

import java.awt.Color;
import java.util.Map;
import org.opengis.annotation.XmlElement;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
@XmlElement("Jenks")
public interface Jenks extends Function {


    /**
     * Get number of class.
     *
     * @return Expression
     */
    @XmlElement("ClassNumber")
    Literal getClassNumber();

    /**
     * Get palette name.
     *
     * @return Expression
     */
    @XmlElement("Palette")
    Literal getPalette();

    /**
     * Get no-data values
     * @return
     */
    @XmlElement("noData")
    double[] getNoData();

    /**
    * Generated color map.
    * @return map of value and associated color.
    */
    Map<Double, Color> getColorMap();
}
