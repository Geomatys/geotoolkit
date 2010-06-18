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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
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
    	return getValue();
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
            if(DefaultName.match(prop.getName(),name)){
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
            if(DefaultName.match(prop.getName(),name)){
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

        toString(tablewriter, this, null, true);

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

    private static final String BLANCK = "\u00A0\u00A0\u00A0\u00A0";
    private static final String LINE =    "\u00A0\u00A0\u2502\u00A0";
    private static final String CROSS =  "\u00A0\u00A0\u251C\u2500";
    private static final String END =    "\u00A0\u00A0\u2514\u2500";

    /**
     *
     * @param tablewriter
     * @param property
     * @param index
     * @param path
     * @param last node of the tree
     */
    private static void toString(TableWriter tablewriter, Property property, Integer index, boolean last, String... path){

        //draw the path.
        for(String t : path){
            tablewriter.write(t);
        }

        //write property name
        tablewriter.write(DefaultName.toJCRExtendedForm(property.getName()));
        //write the index if one
        if(index != null){
            tablewriter.write('[');
            tablewriter.write(index.toString());
            tablewriter.write(']');
        }

        tablewriter.write('\t');

        final PropertyType pt = property.getType();
        if(pt instanceof ComplexType){
            //no value
            tablewriter.write('\n');
            
            final ComplexType ct = (ComplexType) pt;

            final Object value = property.getValue();
            final ComplexAttribute ca;
            if(value instanceof ComplexAttribute){
                //hack, when feature are not correctly build
                //todo fix all complexe feature instances, then remove
                ca = (ComplexAttribute)value;
            }else{
                ca = (ComplexAttribute)property;
            }

            int nbProperty = ca.getProperties().size();
            int nb=0;

            String[] subPath = last(path, LINE);

            for(PropertyDescriptor desc : ct.getDescriptors()){

                if(desc.getMaxOccurs()==1){
                    final Property sub = ca.getProperty(desc.getName());
                    if(sub != null){
                        nb++;
                        if(last){ subPath = last(path, BLANCK); }

                        toString(tablewriter, sub, null, nb==nbProperty, append(subPath, (nb==nbProperty)?END:CROSS));
                    }
                }else{
                    final Collection<? extends Property> properties = ca.getProperties(desc.getName());
                    int i = 0;
                    int n = properties.size()-1;
                    for(Property sub : properties){
                        nb++;
                        if(last){ subPath = last(path, BLANCK); }
                        
                        if(i==n){
                            toString(tablewriter, sub, i, nb==nbProperty, append(subPath, (nb==nbProperty)?END:CROSS));
                        }else if(i == 0){
                            toString(tablewriter, sub, i, nb==nbProperty, append(subPath, (nb==nbProperty)?END:CROSS));
                        }else{
                            toString(tablewriter, sub, i, nb==nbProperty, append(subPath, (nb==nbProperty)?END:CROSS));
                        }
                        i++;
                    }
                }
            }

        }else{
            //simple property
            final Object value = property.getValue();
            tablewriter.write((value == null)? "null" : value.toString());
            tablewriter.write('\n');
        }
    }

    private static String[] append(String[] array, String end){
        array = Arrays.copyOf(array, array.length+1);
        array[array.length-1] = end;
        return array;
    }

    private static String[] last(String[] array, String end){
        array = array.clone();
        if(array.length>0){
            array[array.length-1] = end;
        }
        return array;
    }
    
}
