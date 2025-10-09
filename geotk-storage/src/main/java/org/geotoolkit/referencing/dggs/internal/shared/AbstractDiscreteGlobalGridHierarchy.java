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

import java.util.AbstractList;
import java.util.List;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridHierarchy;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractDiscreteGlobalGridHierarchy<T extends DiscreteGlobalGridReferenceSystem> extends AbstractList<DiscreteGlobalGrid> implements DiscreteGlobalGridHierarchy {

    public final T dggrs;

    protected AbstractDiscreteGlobalGridHierarchy(T dggrs) {
        ArgumentChecks.ensureNonNull("dggrs", dggrs);
        this.dggrs = dggrs;
    }

    @Override
    public final List<DiscreteGlobalGrid> getGrids() {
        return this;
    }

}
