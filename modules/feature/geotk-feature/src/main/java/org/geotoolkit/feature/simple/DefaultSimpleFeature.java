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
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.io.TableAppender;
import org.apache.sis.util.Utilities;
import org.geotoolkit.feature.AbstractFeature;

import org.geotoolkit.feature.DefaultAttribute;
import org.geotoolkit.feature.DefaultGeometryAttribute;
import org.geotoolkit.feature.FeatureValidationUtilities;
import org.geotoolkit.feature.GeometryAttribute;
import org.geotoolkit.filter.identity.DefaultFeatureId;

import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.NamesExt;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.util.GenericName;

/**
 * An implementation of {@link SimpleFeature} geared towards speed and backed by an Object[].
 *
 * @author Justin
 * @author Andrea Aime
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class DefaultSimpleFeature extends AbstractFeature<List<Property>> implements SimpleFeature {

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
    public SimpleFeatureType getFeatureType() {
        return (SimpleFeatureType) getType();
    }

    @Override
    public SimpleFeatureType getType() {
        return (SimpleFeatureType) super.getType();
    }

    @Override
    public List<Property> getValue() {
        if(value == null || value.isEmpty()){
            value = toProperties();
        }
        return value;
    }

    @Override
    public Object getAttribute(int idx) throws IndexOutOfBoundsException {
        if(valueArray == null){
            return getProperties().get(idx).getValue();
        }
        return valueArray[idx];
    }

    @Override
    public Object getAttribute(final GenericName name) {
        final Integer idx = getIndex().get(name);
        if (idx != null) {
            return getAttribute(idx);
        } else {
            return null;
        }
    }

    @Override
    public Object getAttribute(String name) {
        final Integer idx = getIndex().get(name);
        if (idx != null) {
            return getAttribute(idx);
        } else {
            return null;
        }
    }
    
    @Override
    public List<Object> getAttributes() {
        final List<Object> values = new ArrayList<>();
        for(final Property prop : getProperties()){
            values.add(prop.getValue());
        }
        return values;
    }

    @Override
    public int getAttributeCount() {
        return getProperties().size();
    }

    @Override
    public void setAttribute(int idx, Object value) throws IndexOutOfBoundsException {
        if(valueArray == null){
            final Property prop = getProperties().get(idx);

            // if necessary, validation too
            if (isValidating()) {
                FeatureValidationUtilities.validate((AttributeDescriptor)prop.getDescriptor(), value);
            }

            //the type must match, we don't test, user must know what he is doing or must validate feature.
            prop.setValue(value);
            return;
        }
        valueArray[idx] = value;

        //clear the geometry cache if necessary
//        if(this.value != null){
//            Object prop = this.value.get(idx);
//            if(prop instanceof DefaultGeometryAttribute){
//                ((SimpleGeometryAttribut)prop).clearCache();
//            }
//        }

    }

    @Override
    public void setAttributes(final List<Object> values) {
        for (int i=0,n=values.size(); i<n; i++) {
            setAttribute(i, values.get(i));
        }
    }

    @Override
    public void setAttributes(final Object[] values) {

        final List<Property> properties = getProperties();
        final boolean validating = isValidating();

        for (int index = 0; index < values.length; index++) {
            final Property prop = properties.get(index);
            final Object val = values[index];

            // if necessary, validation too
            if (validating) {
                FeatureValidationUtilities.validate((AttributeDescriptor)prop.getDescriptor(), val);
            }

            //the type must match, we don't test, user must know what he is doing or must validate feature.
            prop.setValue(val);
        }
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        final Integer idx = getIndex().get(name);
        if (idx == null) {
            throw new PropertyNotFoundException("Unknown attribute " + name);
        }
        setAttribute(idx, value);
    }

    @Override
    public void setAttribute(final GenericName name, final Object value) {
        final Integer idx = getIndex().get(name);
        if (idx == null) {
            throw new PropertyNotFoundException("Unknown attribute " + name);
        }
        setAttribute(idx, value);
    }

    @Override
    public Object getDefaultGeometry() {
        final Map<Object,Integer> index = getIndex();

        // should be specified in the index as the default key (null)
        final Integer indexGeom = index.get(null);

        if(indexGeom != null){
            return getAttribute(indexGeom);
        }else{
            final GeometryDescriptor geometryDescriptor = getFeatureType().getGeometryDescriptor();
            if (geometryDescriptor != null) {
                final Integer defaultGeomIndex = index.get(geometryDescriptor.getName());
                index.put(null, defaultGeomIndex);
                return getAttribute(defaultGeomIndex.intValue());
            }
        }

        return null;
    }

    @Override
    public void setDefaultGeometry(final Object geometry) {
        final Integer geometryIndex = getIndex().get(null);
        if (geometryIndex != null) {
            setAttribute(geometryIndex, geometry);
        }
    }

    protected boolean isValidating() {
        return validating;
    }

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

    @Override
    public void setIdentifier(final FeatureId fid){
        super.setIdentifier(fid);
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
            if (valueArray != null) {
                return valueArray[index];
            }
            return null;
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
            if (valueArray != null) {
                return valueArray[index];
            }
            return null;
        }

        @Override
        public void setValue(Object newValue) throws IllegalArgumentException, IllegalStateException {
            valueArray[index] = newValue;
        }

    }

    //feature methods ----------------------------------------------------------

    @Override
    public GeometryAttribute getDefaultGeometryProperty() {
        final Map<Object,Integer> index = getIndex();

        // should be specified in the index as the default key (null)
        final Integer indexGeom = index.get(null);

        if(indexGeom != null){
            return (GeometryAttribute) getValue().get(indexGeom);
        }else{
            final GeometryDescriptor geometryDescriptor = getFeatureType().getGeometryDescriptor();
            if (geometryDescriptor != null) {
                final Integer defaultGeomIndex = index.get(geometryDescriptor.getName());
                index.put(null, defaultGeomIndex);
                return (GeometryAttribute) getValue().get(defaultGeomIndex.intValue());
            }
        }

        return null;
    }

    @Override
    public void setDefaultGeometryProperty(final GeometryAttribute geometryAttribute) {
        if (geometryAttribute != null) {
            setDefaultGeometry(geometryAttribute.getValue());
        } else {
            setDefaultGeometry(null);
        }
    }

    @Override
    public Collection<Property> getProperties(final GenericName name) {
        final Integer idx = getIndex().get(name);
        if (idx != null) {
            final Property prop = getProperties().get(idx);
            // cast temporarily to a plain collection to avoid type problems with generics
            final Collection c = Collections.singleton(prop);
            return c;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Property getProperty(final GenericName name) {
        final Integer idx = getIndex().get(name);
        if (idx == null) {
            return null;
        } else {
            return getProperties().get(idx);
        }
    }

    @Override
    public Collection<Property> getProperties(final String name) {
        final Integer idx = getIndex().get(name);
        if (idx != null) {
            final Property prop = getProperties().get(idx);
            // cast temporarily to a plain collection to avoid type problems with generics
            final Collection c = Collections.singleton(prop);
            return c;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Property getProperty(final String name) {
        final Integer idx = getIndex().get(name);
        if (idx == null) {
            return null;
        } else {
            return getProperties().get(idx);
        }
    }

    @Override
    public void validate() throws IllegalArgumentException {
        final List<Property> properties = getProperties();
        final SimpleFeatureType type = getFeatureType();

        for (int i=0,n=type.getAttributeCount(); i<n; i++) {
            final AttributeDescriptor descriptor = type.getDescriptor(i);
            //check for attribute identifier
//            if(properties.get(i) instanceof Attribute){
//                Attribute toTest = (Attribute)properties.get(i);
//                for(int j = i+1 ; j < n ; j++){
//                    if(properties.get(j) instanceof Attribute){
//                        if(toTest.getIdentifier().equals(((Attribute)properties.get(j)).getIdentifier()))
//                            throw new IllegalAttributeException(descriptor, toTest, "We can't have two attributes with the same identifier");
//                    }
//                }
//            }
            FeatureValidationUtilities.validate(descriptor, properties.get(i).getValue());
        }
    }

    @Override
    public void setValue(final Object newValue) {
        setValue((Collection<Property>) newValue);
    }

    @Override
    public String toString() {

        final StringWriter writer = new StringWriter();
        writer.append(this.getClass().getName());
        writer.append('\n');

        final FeatureType featureType = getFeatureType();
        if (featureType != null) {
            writer.append("featureType:").append(featureType.getName().toString()).append('\n');
        }

        //make a nice table to display
        final TableAppender tablewriter = new TableAppender(writer);
        tablewriter.appendHorizontalSeparator();
        tablewriter.append("@id\t"+getID()+"\n");

        for(Property prop : getProperties()){
            tablewriter.append(NamesExt.toExpandedString(prop.getName()));
            tablewriter.append("\t");
            Object value = prop.getValue();
            if(value != null && value.getClass().isArray()){
                value = Utilities.deepToString(value);
            }else{
                value = String.valueOf(value);
            }

            tablewriter.append((String)value);
            tablewriter.append("\n");
        }

        tablewriter.appendHorizontalSeparator();

        try {
            tablewriter.flush();
            writer.flush();
        } catch (IOException ex) {
            //will never happen is this case
            ex.printStackTrace();
        }

        return writer.toString();
    }

}
