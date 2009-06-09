/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.map;

import org.geotoolkit.coverage.io.CoverageReader;
import org.opengis.feature.type.Name;

/**
 * MapLayer handeling coverages.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface CoverageMapLayer extends MapLayer{

    Name getCoverageName();
    
    /**
     * CoverageReader used to obtain GridCoverages.
     */
    CoverageReader getCoverageReader();
        
}
