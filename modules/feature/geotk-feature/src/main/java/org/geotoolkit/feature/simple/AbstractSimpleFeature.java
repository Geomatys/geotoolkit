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

package org.geotoolkit.feature.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureValidationUtilities;
import org.geotoolkit.feature.SimpleIllegalAttributeException;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.io.TableWriter;

import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;

/**
 * Abstract simple feature class, will delegate most Feature methods
 * to the simpleFeature interface methods.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractSimpleFeature extends AbstractFeature<List<Property>> implements SimpleFeature{

    protected abstract boolean isValidating();

    protected abstract Map<Object,Integer> getIndex();

    //simple feature------------------------------------------------------------

    protected AbstractSimpleFeature(final SimpleFeatureType type, final FeatureId id){
        this(new DefaultAttributeDescriptor( type, type.getName(), 1, 1, true, null),id);
    }

    protected AbstractSimpleFeature(final AttributeDescriptor desc, final FeatureId id){
        super(desc,id);
    }

    @Override
    public String getID() {
        return getIdentifier().getID();
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
            throw new SimpleIllegalAttributeException("Unknown attribute " + name);
        }
        setAttribute(idx, value);
    }

    @Override
    public void setAttribute(final Name name, final Object value) {
        final Integer idx = getIndex().get(name);
        if (idx == null) {
            throw new SimpleIllegalAttributeException("Unknown attribute " + name);
        }
        setAttribute(idx, value);
    }

    @Override
    public void setAttribute(final int index, final Object value) throws IndexOutOfBoundsException {
        final Property prop = getProperties().get(index);

        // if necessary, validation too
        if (isValidating()) {
            FeatureValidationUtilities.validate((AttributeDescriptor)prop.getDescriptor(), value);
        }

        //the type must match, we don't test, user must know what he is doing or must validate feature.
        prop.setValue(value);
    }

    @Override
    public List<Object> getAttributes() {
        final List<Object> values = new ArrayList<Object>();
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
    public Object getAttribute(final Name name) {
        final Integer idx = getIndex().get(name);
        if (idx != null) {
            return getAttribute(idx);
        } else {
            return null;
        }
    }

    @Override
    public Object getAttribute(final String name) {
        final Integer idx = getIndex().get(name);
        if (idx != null) {
            return getAttribute(idx);
        } else {
            return null;
        }
    }
    
    @Override
    public Object getAttribute(final int idx) throws IndexOutOfBoundsException {
        return getProperties().get(idx).getValue();
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
    public Collection<Property> getProperties(final Name name) {
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
    public Property getProperty(final Name name) {
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
    public void validate() throws IllegalAttributeException {
        final List<Property> properties = getProperties();
        final SimpleFeatureType type = getFeatureType();

        for (int i=0,n=type.getAttributeCount(); i<n; i++) {
            final AttributeDescriptor descriptor = type.getDescriptor(i);
            FeatureValidationUtilities.validate(descriptor, properties.get(i).getValue());
        }
    }

    @Override
    public AttributeDescriptor getDescriptor() {
        return null;
    }

    @Override
    public void setValue(final Object newValue) {
        setValue((Collection<Property>) newValue);
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public boolean isNillable() {
        return true;
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
        final TableWriter tablewriter = new TableWriter(writer);
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        tablewriter.write("@id\t"+getID()+"\n");

        for(Property prop : getProperties()){
            tablewriter.write(DefaultName.toJCRExtendedForm(prop.getName()));
            tablewriter.write("\t");
            tablewriter.write(String.valueOf(prop.getValue()));
            tablewriter.write("\n");
        }
        
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        
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
