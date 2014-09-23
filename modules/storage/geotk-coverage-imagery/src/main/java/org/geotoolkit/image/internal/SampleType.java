/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.image.internal;

import java.awt.image.ColorModel;

/**
 * Define internaly {@link ColorModel} data type.
 * 
 * @author Remi Marechal (Geomatys).
 */
public enum SampleType {
    /**
     * Data type {@link Byte}.
     */
    Byte, 
    
    /**
     * Data type {@link Short}.
     */
    Short, 
    
    /**
     * Data type {@link Integer}.
     */
    Integer, 
    
    /**
     * Data type {@link Float}.
     */
    Float, 
    
    /**
     * Data type {@link Double}.
     */
    Double
}
