package org.geotoolkit.filter.capability;

import java.util.Collection;
import org.opengis.filter.capability.GeometryOperand;

public abstract class SpatialCapabilities {
    public abstract SpatialOperators getSpatialOperators();

    public abstract Collection<GeometryOperand> getGeometryOperands();
}
