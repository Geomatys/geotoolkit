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
 * A Single Member of a relation.
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

    /**
     * @return id of the referenced member.
     */
    public long getReference() {
        return ref;
    }

    /**
     * A Member has a defined role in the relation, expressed by the string
     * for exemple a way can have the role "Border" while a Node
     * can have the role "Entrance" for a relation defining a parc.
     * @return String
     */
    public String getRole() {
        return role;
    }

    /**
     * Narrow the type of the relation member, which can be a node, way or relation.
     * @return MemberType
     */
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
