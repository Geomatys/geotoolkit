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
 * @module pending
 */
public interface GetLegendRequest extends Request {

    String getLayer();

    void setLayer(String layer);

    Dimension getDimension();

    void setDimension(Dimension dim);

    String getFormat();

    void setFormat(String format);

    String getExceptions();

    void setExceptions(String ex);

    String[] getStyles();

    void setStyles(String ... styles);

    String getSld();

    void setSld(String sld);

    String getSldBody();

    void setSldBody(String sldBody);

}
