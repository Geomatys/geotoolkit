/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.model;

import java.io.Serializable;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Member implements Serializable {

    private final long ref;
    private final MemberType type;
    private final String role;

    public Member(long ref, MemberType type, String role) {
        if(type == null){
            throw new NullPointerException("Member type can not be null.");
        }

        this.ref = ref;
        this.type = type;
        this.role = role;
    }

    public long getReference() {
        return ref;
    }

    public String getRole() {
        return role;
    }

    public MemberType getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(type).append(':').append(ref);
        if(role != null){
            sb.append(" / ");
            sb.append("Role:").append(role);
        }
        return sb.toString();
    }



}
