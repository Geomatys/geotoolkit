/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.feature.simple;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.DefaultAttribute;
import org.geotoolkit.feature.DefaultGeometryAttribute;
import org.geotoolkit.filter.identity.DefaultFeatureId;

import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;

/**
 * An implementation of {@link SimpleFeature} geared towards speed and backed by an Object[].
 *
 * @author Justin
 * @author Andrea Aime
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DefaultSimpleFeature extends AbstractSimpleFeature {

    private String strID;

    private final Object[] valueArray;
    
    /**
     * The attribute name -> position index
     */
    private final Map<Object, Integer> index;
    /**
     * Whenever this feature is self validating or not
     */
    private final boolean validating;

    public DefaultSimpleFeature(final SimpleFeatureType featureType, final FeatureId id, final Object[] values, final boolean validating){
        super(featureType,id);

        // in the most common case reuse the map cached in the feature type
        if (type instanceof DefaultSimpleFeatureType) {
            index = ((DefaultSimpleFeatureType) type).index;
        } else {
            // if we're not lucky, rebuild the index completely...
            // TODO: create a separate cache for this case?
            this.index = DefaultSimpleFeatureType.buildIndex((SimpleFeatureType) type);
        }

        // if we're self validating, do validation right now
        this.validating = validating;
        if (validating) {
            validate();
        }
        
        this.valueArray = values;
    }
    
    public DefaultSimpleFeature(final AttributeDescriptor desc, final FeatureId id, final Object[] values, final boolean validating){
        super(desc,id);

        // in the most common case reuse the map cached in the feature type
        if (type instanceof DefaultSimpleFeatureType) {
            index = ((DefaultSimpleFeatureType) type).index;
        } else {
            // if we're not lucky, rebuild the index completely...
            // TODO: create a separate cache for this case?
            this.index = DefaultSimpleFeatureType.buildIndex((SimpleFeatureType) type);
        }

        // if we're self validating, do validation right now
        this.validating = validating;
        if (validating) {
            validate();
        }
        
        this.valueArray = values;
    }
    
    public DefaultSimpleFeature(final SimpleFeatureType type, final FeatureId id, final List<Property> properties, final boolean validating){
        super(type,id);

        // in the most common case reuse the map cached in the feature type
        if (type instanceof DefaultSimpleFeatureType) {
            index = ((DefaultSimpleFeatureType) type).index;
        } else {
            // if we're not lucky, rebuild the index completely...
            // TODO: create a separate cache for this case?
            this.index = DefaultSimpleFeatureType.buildIndex((SimpleFeatureType) type);
        }

        this.value = properties;
        this.validating = validating;
        this.valueArray = null;

        // if we're self validating, do validation right now
        if (validating) {
            validate();
        }
    }

    public DefaultSimpleFeature(final AttributeDescriptor desc, final FeatureId id, final List<Property> properties, final boolean validating){
        super(desc,id);

        // in the most common case reuse the map cached in the feature type
        if (desc.getType() instanceof DefaultSimpleFeatureType) {
            index = ((DefaultSimpleFeatureType) desc.getType()).index;
        } else {
            // if we're not lucky, rebuild the index completely...
            // TODO: create a separate cache for this case?
            this.index = DefaultSimpleFeatureType.buildIndex((SimpleFeatureType) desc.getType());
        }

        this.value = properties;
        this.validating = validating;
        this.valueArray = null;

        // if we're self validating, do validation right now
        if (validating) {
            validate();
        }
    }

    @Override
    public List<Property> getValue() {
        if(value == null){
            value = toProperties();
        }
        return value;
    }

    @Override
    public Object getAttribute(int idx) throws IndexOutOfBoundsException {
        if(valueArray == null){
            return super.getAttribute(idx);
        }
        return valueArray[idx];
    }

    @Override
    public Object getAttribute(String name) {
        return super.getAttribute(name);
    }
    
    @Override
    public void setAttribute(int idx, Object value) throws IndexOutOfBoundsException {
        if(valueArray == null){
            super.setAttribute(idx,value);
            return;
        }
        valueArray[idx] = value;
    }
    
    @Override
    protected boolean isValidating() {
        return validating;
    }

    @Override
    protected Map<Object, Integer> getIndex() {
        return index;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureId getIdentifier() {
        if(id == null){
            id = new DefaultFeatureId(strID);
        }
        return id;
    }

    public void setIdentifier(final FeatureId fid){
        this.id = fid;
        this.strID = fid.getID();
    }

    public void setId(final String id){
        this.strID = id;
        this.id = null;
    }

    @Override
    public String getID() {
        if(strID != null){
            return strID;
        }else{
            return id.getID();
        }
    }
    
    private List<Property> toProperties(){
        final SimpleFeatureType sft = (SimpleFeatureType) this.type;
        final int n = sft.getAttributeCount();
        final Property[] properties = new Property[n];
        for(int i=0; i<n; i++){
            final AttributeDescriptor desc = sft.getDescriptor(i);
            if(desc instanceof GeometryDescriptor){
                properties[i] = new SimpleGeometryAttribut(i,(GeometryDescriptor) desc);
            }else{
                properties[i] = new SimpleAttribut(i, desc);
            }
        }
        return UnmodifiableArrayList.wrap(properties);
    }
    

    /**
     * returns a unique code for this feature
     *
     * @return A unique int
     */
    @Override
    public int hashCode() {
        return getIdentifier().hashCode() * getType().hashCode();
    }

    /**
     * override of equals.  Returns if the passed in object is equal to this.
     *
     * @param obj the Object to test for equality.
     *
     * @return <code>true</code> if the object is equal, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultSimpleFeature)) {
            return false;
        }

        final DefaultSimpleFeature feat = (DefaultSimpleFeature) obj;

        // this check shouldn't exist, by contract,
        //all features should have an ID.
        if (getIdentifier() == null) {
            if (feat.getIdentifier() != null) {
                return false;
            }
        }

        if (!getIdentifier().equals(feat.getIdentifier())) {
            return false;
        }

        if (!feat.getFeatureType().equals(getFeatureType())) {
            return false;
        }

        final List<Property> properties = getProperties();
        for (int i=0, n=properties.size(); i<n; i++) {
            Object otherAtt = feat.getAttribute(i);

            if (getProperties().get(i).getValue() == null) {
                if (otherAtt != null) {
                    return false;
                }
            } else {
                if (!properties.get(i).getValue().equals(otherAtt)) {
                    if (properties.get(i).getValue() instanceof Geometry && otherAtt instanceof Geometry) {
                        // we need to special case Geometry
                        // as JTS is broken Geometry.equals( Object )
                        // and Geometry.equals( Geometry ) are different
                        // (We should fold this knowledge into AttributeType...)
                        if (!((Geometry) properties.get(i).getValue()).equalsExact(
                                (Geometry) otherAtt)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private class SimpleAttribut extends DefaultAttribute<Object,AttributeDescriptor,Identifier> {
        
        private final int index;
        
        private SimpleAttribut(final int index, final AttributeDescriptor desc){
            super(null, desc, null);
            this.index = index;
        }

        @Override
        public Object getValue() {
            return valueArray[index];
        }

        @Override
        public void setValue(Object newValue) throws IllegalArgumentException, IllegalStateException {
            valueArray[index] = newValue;
        }
    }
    
    private class SimpleGeometryAttribut extends DefaultGeometryAttribute{
        
         private final int index;
        
        private SimpleGeometryAttribut(final int index, final GeometryDescriptor desc){
            super(null, desc, null);
            this.index = index;
        }

        @Override
        public Object getValue() {
            return valueArray[index];
        }

        @Override
        public void setValue(Object newValue) throws IllegalArgumentException, IllegalStateException {
            valueArray[index] = newValue;
            bounds = null; //reset bounds
        }
        
    }
    
}
