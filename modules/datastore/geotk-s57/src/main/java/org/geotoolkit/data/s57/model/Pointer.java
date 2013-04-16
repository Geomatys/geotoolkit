/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57.model;

import org.geotoolkit.data.s57.S57Constants;

/**
 * Common parent for VectorRecord.RecordPointer and FeatureRecord.SpatialPointer
 * @author Johann Sorel
 */
public class Pointer extends S57ModelObject {
    
    //reference id
    public S57Constants.RecordType type;
    public long refid;

    public Pointer() {
    }

    public Pointer(S57Constants.RecordType type, long refid) {
        this.type = type;
        this.refid = refid;
    }

    @Override
    public String toString() {
        return "P:"+type+","+refid;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (this.refid ^ (this.refid >>> 32));
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Pointer)) {
            return false;
        }
        final Pointer other = (Pointer) obj;
        if (this.refid != other.refid) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }
    
}
