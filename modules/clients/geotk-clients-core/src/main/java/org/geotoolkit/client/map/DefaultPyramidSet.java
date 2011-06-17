/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.client.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Default PyramidSet.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPyramidSet implements PyramidSet{

    private final List<Pyramid> pyramids = new ArrayList<Pyramid>();
    
    @Override
    public Collection<Pyramid> getPyramids() {
        return pyramids;
    }
    
}
