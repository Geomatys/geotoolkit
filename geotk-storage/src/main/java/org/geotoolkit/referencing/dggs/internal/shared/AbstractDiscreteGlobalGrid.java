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
package org.geotoolkit.referencing.dggs.internal.shared;

import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractDiscreteGlobalGrid implements DiscreteGlobalGrid {

    protected final AbstractDiscreteGlobalGridHierarchy hierarchy;
    protected final int level;

    public AbstractDiscreteGlobalGrid(AbstractDiscreteGlobalGridHierarchy hierarchy, int level) {
        this.hierarchy = hierarchy;
        this.level = level;
    }

    @Override
    public final int getRefinementLevel() {
        return level;
    }

}
