/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggs.h3;

import java.util.AbstractList;
import java.util.List;
import org.geotoolkit.storage.dggs.DiscreteGlobalGrid;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridHierarchy;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class H3Dggh extends AbstractList<DiscreteGlobalGrid> implements DiscreteGlobalGridHierarchy {

    @Override
    public List<DiscreteGlobalGrid> getGrids() {
        return this;
    }

    @Override
    public DiscreteGlobalGrid get(int level) {
        return new H3Dgg(level);
    }

    @Override
    public int size() {
        return 15;
    }

}
