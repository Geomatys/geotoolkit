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
package org.geotoolkit.wmts;

import java.util.Map;
import org.geotoolkit.client.Request;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public interface GetTileRequest extends Request {

    String getLayer();

    void setLayer(String layer);

    String getStyle();

    void setStyle(String styles);

    String getFormat();

    void setFormat(String format);

    String getTileMatrixSet();
    
    void setTileMatrixSet(String env);

    String getTileMatrix();

    void setTileMatrix(String ex);

    int getTileRow();

    void setTileRow(int tr);

    int getTileCol();

    void setTileCol(int tr);

    Map<String,String> dimensions();

}
