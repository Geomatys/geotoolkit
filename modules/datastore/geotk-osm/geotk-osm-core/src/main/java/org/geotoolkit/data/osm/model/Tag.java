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

import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.feature.AbstractComplexAttribute;

import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.identity.Identifier;

/**
 * OSM Tag. Every OSM element might have tags.
 * A Tag is combinaison of a Key and a Value, both are String values.
 * An element can only have one tag with the given key.
 * If several values has to be added for the same tag, then they must separated with a ; .
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Tag extends AbstractComplexAttribute<Collection<Property>,Identifier>{

    private final String k;
    private final String v;

    public Tag(String key, String value) {
        super(OSMModelConstants.DESC_TAG,new SimpleId(key));
        this.k = key;
        this.v = value;
    }

    /**
     * @return Tag key
     */
    public String getK() {
        return k;
    }

    /**
     * @return tag value
     */
    public String getV() {
        return v;
    }

    // feature/attribut model --------------------------------------------------

    @Override
    public Collection<Property> getProperties() {
        final Collection<Property> props = new ArrayList<Property>();
        props.add(IdentifiedElement.FF.createAttribute(k, (AttributeDescriptor) getType().getDescriptor("k"),null));
        props.add(IdentifiedElement.FF.createAttribute(k, (AttributeDescriptor) getType().getDescriptor("v"),null));
        return props;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Tag[");
        sb.append(k).append(',').append(v).append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tag other = (Tag) obj;
        if ((this.k == null) ? (other.k != null) : !this.k.equals(other.k)) {
            return false;
        }
        if ((this.v == null) ? (other.v != null) : !this.v.equals(other.v)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.k != null ? this.k.hashCode() : 0);
        hash = 97 * hash + (this.v != null ? this.v.hashCode() : 0);
        return hash;
    }

}
