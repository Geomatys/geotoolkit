/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.wfs;

import java.util.Map;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface Update extends TransactionElement{

    Map<Name,Object> updates();

    String getHandle();

    void setHandle(String handle);

    Filter getFilter();

    void setFilter(Filter filter);
    
    CoordinateReferenceSystem getCoordinateReferenceSystem();
    
    void setCoordinateReferenceSystem(CoordinateReferenceSystem crs);
    
    String getInputFormat();
    
    void setInputFormat(String format);

    /**
     * @return QName : requested type name, can be null
     * if not yet configured.
     */
    String getTypeName();

    /**
     * @param type : requested type name, must not be null
     */
    void setTypeName(String type);

}
