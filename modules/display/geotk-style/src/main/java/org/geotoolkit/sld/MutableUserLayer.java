/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.sld;

import java.util.List;
import org.geotoolkit.style.MutableStyle;
import org.opengis.sld.Source;
import org.opengis.sld.UserLayer;

/**
 * @author Johann Sorel (Geomatys)
 */
public interface MutableUserLayer extends MutableLayer, UserLayer{

    public static final String SOURCE_PROPERTY = "source";
    public static final String CONSTRAINTS_PROPERTY = "constraints";
    
    void setSource(Source source);

    MutableConstraints getConstraints();
    
    void setConstraints(MutableConstraints constraints);

    List<MutableStyle> styles();
    
}
