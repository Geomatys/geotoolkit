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
import org.opengis.geometry.Envelope;

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
     * Gets the abstract value for this layer.
     *
     * @return
     */
    public abstract String getAbstract();

    /**
     * Get all Dimensions (TIME,ELEVATION,...) from a specific layer.
     * 
     * @return
     */
    public abstract List<AbstractDimension> getAbstractDimension();

    /**
     * Unmodifiable list of dimensions contained in this layer.
     * @return
     */
    public abstract List<? extends AbstractDimension> getDimension();

    /**
     * Gets the value of the keywordList property.
     *
     * @return
     */
    public abstract AbstractKeywordList getKeywordList();

    /**
     * List of supported CRS.
     * @return
     */
    public abstract List<String> getCRS();

    /**
     * Unmodifiable list of layers contained in this layer.
     * @return
     */
    public abstract List<? extends AbstractLayer> getLayer();

    /**
     * Get the layer name.
     * @return
     */
    public abstract String getName();

    /**
     * Set the layer name
     * @param name
     */
    public abstract void setName(String name);
    
    /**
     * Get the layer title
     * @return
     */
    public abstract String getTitle();

    /**
     * Set the layer title
     * @param title
     */
    public abstract void setTitle(String title);

    /**
     * Get the layer envelope
     * @return
     */
    public abstract Envelope getEnvelope();

    /**
     * Get layer styles
     * @return
     */
    public abstract List<? extends Style> getStyle();

    /**
     * Get if the layer is queryable or not
     * @return
     */
    public abstract boolean isQueryable();

    /**
     * Get the layer boundingboxes
     * @return
     */
    public abstract List<? extends AbstractBoundingBox> getBoundingBox();

}
