/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.apache.sis.feature;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.sis.internal.feature.ArrayFeature;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.Operation;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyType;

/**
 * TODO : move ArrayFeature in SIS
 *
 * @author Johann Sorel (Geomatys)
 */
final class DefaultArrayFeature extends AbstractFeature implements ArrayFeature {

    private final Map<String,Integer> index = new HashMap<>();
    private final Object[] values;
    private final Property[] properties;

    /**
     * Creates a new feature of the given type.
     *
     * @param type Information about the feature (name, characteristics, <i>etc.</i>).
     */
    public DefaultArrayFeature(final DefaultFeatureType type) {
        super(type);

        //build index
        final Collection<PropertyType> props = type.getProperties(true);
        values = new Object[props.size()];
        properties = new Property[values.length];
        
        final Iterator<PropertyType> ite = type.getProperties(true).iterator();
        int i=0;
        while(ite.hasNext()){
            final PropertyType pt = ite.next();
            index.put(pt.getName().toString(),i);
            if(pt instanceof Operation){
                values[i] = pt;
            }
            i++;
        }
    }

    @Override
    public Property getProperty(String name) throws IllegalArgumentException {
        final int idx = getIndex(name);
        if(properties[idx]==null){
            final PropertyType pt = getType().getProperty(name);
            if(pt instanceof Operation){
                final Operation op = (Operation) pt;
                return op.apply(this, op.getParameters().createValue());
            }else if(pt instanceof AttributeType){
                properties[idx] = ((AttributeType)pt).newInstance();
                ((Attribute)properties[idx]).setValue(values[idx]);
            }else if(pt instanceof FeatureAssociationRole){
                properties[idx] = ((FeatureAssociationRole)pt).newInstance();
                ((FeatureAssociation)properties[idx]).setValue((Feature)values[idx]);
            }
        }
        return properties[idx];
    }

    @Override
    public void setProperty(Property property) throws IllegalArgumentException {
        final int idx = getIndex(property.getName().toString());
        if(values[idx] instanceof Operation){
            //todo
        }else{
            properties[idx] = property;
            values[idx] = null; //remove old reference, could be a source of memory leak
        }
    }

    @Override
    public Object getPropertyValue(String name) throws IllegalArgumentException {
        return getPropertyValue(getIndex(name));
    }

    @Override
    public Object getPropertyValue(int index) {
        if(properties[index]!=null){
            return properties[index].getValue();
        }else if(values[index] instanceof Operation){
            final Operation op = (Operation) values[index];
            return op.apply(this, op.getParameters().createValue()).getValue();
        }else{
            return values[index];
        }
    }

    @Override
    public void setPropertyValue(int index, Object value) {
        if(values[index] instanceof Operation){
            final Operation op = (Operation) values[index];
            setOperationValue(op.getName().toString(), value);
        }else if(properties[index] instanceof Attribute){
            ((Attribute)properties[index]).setValue(value);
        }else if(properties[index] instanceof FeatureAssociation){
            ((FeatureAssociation)properties[index]).setValue((Feature) value);
        }else{
            values[index] = value;
        }
    }

    @Override
    public void setPropertyValue(String name, Object value) throws IllegalArgumentException {
        setPropertyValue(getIndex(name),value);
    }

    private int getIndex(String name){
        final Integer idx = index.get(name);
        if(idx==null) throw new IllegalArgumentException("Not property for name "+name);
        return idx;
    }

}
