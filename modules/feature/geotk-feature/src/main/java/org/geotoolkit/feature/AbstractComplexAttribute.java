/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.feature;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.identity.Identifier;

/**
 * Default implementation of a complexeAttribut.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractComplexAttribute<V extends Collection<Property>,I extends Identifier> extends DefaultAttribute<V,AttributeDescriptor,I>
        implements ComplexAttribute {

    protected AbstractComplexAttribute(AttributeDescriptor descriptor, I id) {
        super( null , descriptor, id );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComplexType getType() {
        return (ComplexType)descriptor.getType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public V getProperties() {
    	return value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Property> getProperties(Name name) {
        if(name.getNamespaceURI() == null){
            return getProperties(name.getLocalPart());
        }

        //we size it to 1, in most of the cases there is always a single property for a name.
        final List<Property> matches = new ArrayList<Property>(1);
        for(Property prop : getProperties()){
            if(prop.getName().equals(name)){
                matches.add(prop);
            }
        }
        return matches;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Property> getProperties(String name) {
        //we size it to 1, in most of the cases there is always a single property for a name.
        final List<Property> matches = new ArrayList<Property>(1);
        for(Property prop : getProperties()){
            if(prop.getName().getLocalPart().equals(name)){
                matches.add(prop);
            }
        }
        return matches;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Property getProperty(Name name) {
        if(name.getNamespaceURI() == null){
            return getProperty(name.getLocalPart());
        }
        //TODO find a faster way, hashmap ?
        for(Property prop : getProperties()){
            if(prop.getName().equals(name)){
                return prop;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Property getProperty(String name) {
        //TODO find a faster way, hashmap ?
        for(Property prop : getProperties()){
            if(prop.getName().getLocalPart().equals(name)){
                return prop;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(Object newValue) throws IllegalArgumentException,
            IllegalStateException {
        setValue((Collection<Property>)newValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(Collection<Property> newValues) {
        final Collection<Property> props = getProperties();
        if(props.size() != newValues.size()){
            throw new IllegalArgumentException("Expected size of the collection is " 
                    + this.getProperties().size() +" but the provided size is " +newValues.size());
        }
        props.clear();
        props.addAll(newValues);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append(" ");
        sb.append(getName());
        sb.append(" type=");
        sb.append(getType().getName());

        sb.append('\n');

        //make a nice table to display
        final StringWriter writer = new StringWriter();
        final TableWriter tablewriter = new TableWriter(writer);
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        tablewriter.write("name\t value\n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);

        final Collection<PropertyDescriptor> descs = getType().getDescriptors();
        final int last = descs.size()-1;
        int i=0;
        for (PropertyDescriptor property : descs) {
            if(i==last){
                toString(tablewriter, property, this, 1, 1, 1);
            }else{
                toString(tablewriter, property, this, 1, 0, 1);
            }
            i++;
        }
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        try {
            tablewriter.flush();
            writer.flush();
        } catch (IOException ex) {
            //will never happen is this case
            ex.printStackTrace();
        }
        sb.append(writer.getBuffer().toString());

        return sb.toString();
    }

    private void toString(TableWriter tablewriter, PropertyDescriptor desc, Object att, int depth, int pos, int startdepth){

        final Collection<Property> values;
        if(att instanceof ComplexAttribute){
            values = ((ComplexAttribute)att).getProperties(desc.getName());
        }else{
            values = Collections.singleton((Property)att);
        }

        if(values.isEmpty())return;

        if(depth > 1){
            for(int i=1; i<depth-1;i++){
                if(i < startdepth){
                    tablewriter.write("\u00A0\u00A0\u00A0");
                }else{
                    tablewriter.write("\u2502\u00A0\u00A0");
                }
            }
            if(pos == 0){
                tablewriter.write("\u251C\u2500\u2500");
            }else if(pos == 1){
                tablewriter.write("\u2514\u2500\u2500");
            }else{
                tablewriter.write("\u251C\u2500\u2500");
            }
        }

        for(Property p : values){
            final Object value = p.getValue();

            tablewriter.write(DefaultName.toJCRExtendedForm(desc.getName()));
            tablewriter.write("\t");

            if(desc.getType() instanceof ComplexType){
                tablewriter.write("\n");

                final ComplexAttribute ca = (ComplexAttribute) value;
//                PropertyDescriptor td = new DefaultPropertyDescriptor(ca.getType(), ca.getName(), 0, 1, true);
//                toString(tablewriter,td,ca,depth+1,pos,startdepth);
//
//                final Collection<PropertyDescriptor> descs = ca.getType().getDescriptors();
//                for (PropertyDescriptor property : descs) {
//                    toString(tablewriter, property, ca, depth+1,pos,startdepth);
//                }


                final ComplexType ct = (ComplexType) ca.getType();
                final Collection<PropertyDescriptor> descs = ct.getDescriptors();
                int i=0;
                int n=descs.size()-1;
                for(PropertyDescriptor sub : descs){
                    if(i==n){
                        toString(tablewriter, sub, value, depth+1, 1, startdepth +((pos == 1)? 1 : 0));
                    }else if(i == 0){
                        toString(tablewriter, sub, value, depth+1, 0, startdepth);
                    }else{
                        toString(tablewriter, sub, value, depth+1, -1, startdepth);
                    }
                    i++;
                }
            }else{
                tablewriter.write((value == null)? "null" : value.toString());
                tablewriter.write("\n");
            }

        }

    }


}
