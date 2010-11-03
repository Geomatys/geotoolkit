/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.wms;

import java.awt.Dimension;
import java.util.Map;
import org.geotoolkit.client.Request;
import org.opengis.geometry.Envelope;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface GetMapRequest extends Request {
    /**
     * Returns the layer(s) concerned, never {@code null}. Should not be empty.
     */
    String[] getLayers();

    /**
     * Sets the layer(s) concerned. Must be called.
     */
    void setLayers(String ... layers);

    /**
     * Returns the data envelope to request, never {@code null}.
     */
    Envelope getEnvelope();

    /**
     * Sets the envelope to request. Must be called.
     */
    void setEnvelope(Envelope env);

    /**
     * Returns the output dimension to request, never {@code null}.
     */
    Dimension getDimension();

    /**
     * Sets the output dimension to request. Must be called.
     */
    void setDimension(Dimension dim);

    /**
     * Returns the chosen format, never {@code null}.
     */
    String getFormat();

    /**
     * Sets the format to use. Must be called.
     */
    void setFormat(String format);

    /**
     * Returns the mime-type for exception, or {@code null} if none chosen.
     */
    String getExceptions();

    /**
     * Sets the exception mime-type.
     */
    void setExceptions(String ex);

    /**
     * Returns the style(s) to apply. The array may be empty.
     */
    String[] getStyles();

    /**
     * Sets the style(s) to apply. Must be called.
     */
    void setStyles(String ... styles);

    /**
     * Returns the sld to apply, or {@code null} if none was given.
     */
    String getSld();

    /**
     * Sets the sld to apply. If the {@link #setSldVersion(java.lang.String)} method has been
     * called, this method should be called too.
     */
    void setSld(String sld);

    /**
     * Returns the sld version, or {@code null} if none was given.
     */
    String getSldVersion();

    /**
     * Sets the sld version for the given sld. If the {@link #setSld(java.lang.String)}
     * method has been called, this method should be called too.
     */
    void setSldVersion(String sldVersion);

    /**
     * Returns the sld body to apply, or {@code null} if none was given.
     */
    String getSldBody();

    /**
     * Sets a sld body to apply.
     */
    void setSldBody(String sldBody);

    /**
     * Returns {@code true} if the layer is transparent, {@code false} otherwise.
     */
    boolean isTransparent();

    /**
     * Sets if a layer is transparent or not.
     */
    void setTransparent(Boolean transparent);

    /**
     * Returns additional dimensions.
     */
    Map<String,String> dimensions();

}
