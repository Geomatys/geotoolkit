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
package org.geotoolkit.wms.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
@XmlTransient
public abstract class AbstractLayer {
    
    /**
     * Get all Dimensions (TIME,ELEVATION,...) from a specific layer.
     * 
     */
    public abstract List<AbstractDimension> getAbstractDimension();

    /**
     * Gets the value of the keywordList property.
     *
     */
    public abstract AbstractKeywordList getKeywordList();

    /**
     * List of supported CRS.
     */
    public abstract List<String> getCRS();

}
