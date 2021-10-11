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
 * Interpolation mode used by interpolate function.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/symbol">Symbology Encoding Implementation Specification 1.1.0</A>
 * @author Johann Sorel (Geomatys)
 */
@XmlElement("Mode")
public enum Mode {
    LINEAR,
    COSINE,
    CUBIC;

    public static Mode parse(String val){
        if("linear".equalsIgnoreCase(val)){
            return LINEAR;
        }else if("cosine".equalsIgnoreCase(val)){
            return COSINE;
        }else if("cubic".equalsIgnoreCase(val)){
            return CUBIC;
        }
        return null;
    }

}
