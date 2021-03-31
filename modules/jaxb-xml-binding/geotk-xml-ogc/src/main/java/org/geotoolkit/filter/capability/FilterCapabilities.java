package org.geotoolkit.filter.capability;

public abstract class FilterCapabilities {

    public abstract SpatialCapabilities getSpatialCapabilities();

    public abstract ScalarCapabilities getScalarCapabilities();

    public abstract TemporalCapabilities getTemporalCapabilities();

    public abstract DefaultIdCapabilities getIdCapabilities();

    public abstract String getVersion();
}
