/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.IdCapabilities;

/**
 * Immutable id capabilities.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultIdCapabilities implements IdCapabilities{

    private final boolean eid;
    private final boolean fid;

    public DefaultIdCapabilities(boolean eid, boolean fid) {
        this.eid = eid;
        this.fid = fid;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasEID() {
        return eid;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasFID() {
        return fid;
    }

}
