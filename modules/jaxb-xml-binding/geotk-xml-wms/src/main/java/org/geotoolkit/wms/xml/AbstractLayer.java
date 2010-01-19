/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
 * @author Cédric Briançon
 *
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
     * Unmodifiable list of dimensions contained in this layer.
     */
    public abstract List<? extends AbstractDimension> getDimension();

    /**
     * Gets the value of the keywordList property.
     *
     */
    public abstract AbstractKeywordList getKeywordList();

    /**
     * List of supported CRS.
     */
    public abstract List<String> getCRS();

    /**
     * Unmodifiable list of layers contained in this layer.
     */
    public abstract List<? extends AbstractLayer> getLayer();

    /**
     * Get the layer name.
     */
    public abstract String getName();
}
