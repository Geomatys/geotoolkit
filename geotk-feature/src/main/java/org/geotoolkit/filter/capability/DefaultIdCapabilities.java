/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.util.Collection;
import org.opengis.filter.capability.IdCapabilities;
import org.opengis.util.LocalName;

/**
 * Immutable id capabilities.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class DefaultIdCapabilities implements IdCapabilities {

    private final boolean eid;
    private final boolean fid;

    public DefaultIdCapabilities(final boolean eid, final boolean fid) {
        this.eid = eid;
        this.fid = fid;
    }

    public boolean hasEID() {
        return eid;
    }

    public boolean hasFID() {
        return fid;
    }

    @Override
    public Collection<? extends LocalName> getResourceIdentifiers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultIdCapabilities other = (DefaultIdCapabilities) obj;
        if (this.eid != other.eid) {
            return false;
        }
        if (this.fid != other.fid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.eid ? 1 : 0);
        hash = 67 * hash + (this.fid ? 1 : 0);
        return hash;
    }
}
