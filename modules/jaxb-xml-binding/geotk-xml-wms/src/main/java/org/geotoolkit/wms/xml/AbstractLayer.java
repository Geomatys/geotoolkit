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

import java.net.URL;
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
public interface AbstractLayer {
    /**
     * Gets the abstract value for this layer.
     */
    String getAbstract();

    /**
     * Get all Dimensions (TIME,ELEVATION,...) from a specific layer.
     */
    List<AbstractDimension> getAbstractDimension();

    /**
     * Unmodifiable list of dimensions contained in this layer.
     */
    List<? extends AbstractDimension> getDimension();

    /**
     * Gets the value of the keywordList property.
     */
    AbstractKeywordList getKeywordList();

    /**
     * List of supported CRS.
     */
    List<String> getCRS();

    /**
     * Unmodifiable list of layers contained in this layer.
     */
    List<? extends AbstractLayer> getLayer();

    /**
     * Get the layer name.
     */
    String getName();

    /**
     * Set the layer name
     */
    void setName(String name);
    
    /**
     * Get the layer title
     */
    String getTitle();

    /**
     * Set the layer title
     */
    void setTitle(String title);

    /**
     * Get the layer envelope
     */
    Envelope getEnvelope();

    /**
     * Get layer styles
     */
    List<? extends Style> getStyle();

    /**
     * Get if the layer is queryable or not
     */
    boolean isQueryable();

    /**
     * Get the layer boundingboxes
     */
    List<? extends AbstractBoundingBox> getBoundingBox();
    
    /**
     * Get metadataURLs of the layer
     */
    List<? extends AbstractURL> getMetadataURL();
    
    /**
     * Get dataURLs of the layer
     */
    List<? extends AbstractURL> getDataURL();
    
    /**
     * Get minScaleDenominator of the layer
     */
    Double getMinScaleDenominator();
    
    /**
     * Get maxScaleDenominator of the layer
     */
    Double getMaxScaleDenominator();

}
