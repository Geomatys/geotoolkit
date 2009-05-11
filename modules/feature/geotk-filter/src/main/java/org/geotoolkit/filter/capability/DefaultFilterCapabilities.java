

package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.capability.IdCapabilities;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.capability.SpatialCapabilities;

public class DefaultFilterCapabilities implements FilterCapabilities {

    private final String version;
    private final IdCapabilities id;
    private final SpatialCapabilities spatial;
    private final ScalarCapabilities scalar;

    public DefaultFilterCapabilities(String version, IdCapabilities id, SpatialCapabilities spatial, ScalarCapabilities scalar) {
        this.version = version;
        this.id = id;
        this.spatial = spatial;
        this.scalar = scalar;
    }

    @Override
    public ScalarCapabilities getScalarCapabilities() {
        return scalar;
    }

    @Override
    public SpatialCapabilities getSpatialCapabilities() {
        return spatial;
    }

    @Override
    public IdCapabilities getIdCapabilities() {
        return id;
    }

    @Override
    public String getVersion() {
        return version;
    }

}
