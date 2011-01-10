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
package org.geotoolkit.filter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.filter.accessor.Accessors;
import org.geotoolkit.filter.accessor.PropertyAccessor;

import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

/**
 * Immutable id filter.
 * This implementation assume Object ids have a 1=1 relation with Identifier and
 * that both are unique within this filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultId implements Id,Serializable{

    private static final String XPATH_ID = "@id";

    private final DualKeyMap keys = new DualKeyMap();

    public DefaultId( final Set<? extends Identifier> ids ) {
        for(Identifier id : ids){
            keys.put(id.getID(), id);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Object> getIDs() {
        return keys.keySet();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Identifier> getIdentifiers() {
        return new HashSet<Identifier>(keys.values());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(final Object object) {
        if (object == null) {
            return false;
        }

        final PropertyAccessor accessor = Accessors.getAccessor(object.getClass(), XPATH_ID, null);

        if (accessor == null) {
            return false;
        }
        return keys.containsKey(accessor.get(object, XPATH_ID, null));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultId other = (DefaultId) obj;
        if (this.keys != other.keys && (this.keys == null || !this.keys.equals(other.keys))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.keys != null ? this.keys.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Filter ID :");

        for(final Object key : keys.keySet()){
            sb.append(key.toString()).append(", ");
        }

        return sb.toString();
    }




    /**
     * Take advantage of the fact that both ObjectId and Identifier are unique and
     * ObjectId is an attribut of Identifier.
     * This special map act like a double key map and so benefit all the performance
     * of hashmap.
     */
    private static class DualKeyMap extends HashMap<Object,Identifier>{

        @Override
        public boolean containsValue(final Object value) {
            if(value instanceof Identifier){
                Identifier ident = (Identifier) value;
                return containsKey(ident.getID());
            }else{
                return false;
            }
        }
    }
}
