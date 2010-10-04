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
import org.geotoolkit.client.Request;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface GetLegendRequest extends Request {
    /**
     * Returns the layer concerned, never {@code null}.
     */
    String getLayer();

    /**
     * Sets the layer concerned. Must be called.
     */
    void setLayer(String layer);

    /**
     * Returns the dimension specified, or {@code null} if none.
     */
    Dimension getDimension();

    /**
     * Sets the dimension for the legend output.
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
     * Returns the rule to apply in the sld file, or {@code null} if none chosen.
     */
    String getRule();

    /**
     * Sets a rule name to choose in the sld file.
     */
    void setRule(String rule);

    /**
     * Returns the style to apply, or {@code null} if none was given.
     */
    String getStyle();

    /**
     * Sets the style to apply.
     */
    void setStyle(String style);

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

}
