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
import org.opengis.geometry.Envelope;

/**
 *
 * @author Guilhem Legal
 * @author Cédric Briançon
 *
 * @module
 */
public interface AbstractLayer {
    /**
     * Gets the abstract value for this layer.
     */
    String getAbstract();

    void setAbstract(final String abstrac);

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

    void setKeywordList(final List<String> kewords);

    /**
     * List of supported CRS.
     */
    List<String> getCRS();

    void setCrs(final List<String> crs);

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

    void updateStyle(final List<Style> styles);


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

    void setMetadataURL(final String format, final String href, final String type);

    /**
     * Get dataURLs of the layer
     */
    List<? extends AbstractURL> getDataURL();

    void setDataURL(final String format, final String href);

    void setAuthorityURL(final String format, final String href);

    /**
     * Get minScaleDenominator of the layer
     */
    Double getMinScaleDenominator();

    /**
     * Get maxScaleDenominator of the layer
     */
    Double getMaxScaleDenominator();

    void setIdentifier(final String authority, final String value);

    void setOpaque(final Integer opaque);

    void setAttribution(final String title, final String href, final AbstractLogoURL logo);

}
