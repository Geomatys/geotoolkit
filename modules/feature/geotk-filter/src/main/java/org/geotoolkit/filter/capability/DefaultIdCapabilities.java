

package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.IdCapabilities;

public class DefaultIdCapabilities implements IdCapabilities{

    private final boolean eid;
    private final boolean fid;

    public DefaultIdCapabilities(boolean eid, boolean fid) {
        this.eid = eid;
        this.fid = fid;
    }

    @Override
    public boolean hasEID() {
        return eid;
    }

    @Override
    public boolean hasFID() {
        return fid;
    }

}
