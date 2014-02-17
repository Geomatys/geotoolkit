/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.data.wfs;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractNative implements Native{

    protected String vendorId = null;
    protected boolean safe = true;

    @Override
    public String getVendorId() {
        return vendorId;
    }

    @Override
    public void setVendorId(final String vendorId) {
        this.vendorId = vendorId;
    }

    @Override
    public boolean isSafeToIgnore() {
        return safe;
    }

    @Override
    public void setSafeToIgnore(final boolean safe) {
        this.safe = safe;
    }

}
