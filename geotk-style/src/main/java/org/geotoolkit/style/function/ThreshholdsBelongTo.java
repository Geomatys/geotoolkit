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

import org.opengis.annotation.XmlElement;


/**
 * Used by Categorize function.<br>
 * Whether the Threshold values themselves belong to the preceding or the succeeding
 * interval can be controlled by the attribute thresholdsBelongTo= with the possible values
 * "preceding" and "succeeding" the latter being the default.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/symbol">Symbology Encoding Implementation Specification 1.1.0</A>
 * @author Johann Sorel (Geomatys)
 */
@XmlElement("ThreshholdsBelongToType")
public enum ThreshholdsBelongTo {
        SUCCEEDING,
        PRECEDING;

    public static ThreshholdsBelongTo parse(String val){
        if("succeeding".equalsIgnoreCase(val)){
            return SUCCEEDING;
        }else if("preceding".equalsIgnoreCase(val)){
            return PRECEDING;
        }
        return null;
    }

}
