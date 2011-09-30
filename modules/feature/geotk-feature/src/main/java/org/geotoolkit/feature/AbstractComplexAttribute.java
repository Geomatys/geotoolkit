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
package org.geotoolkit.feature;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Association;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;

/**
 * Default implementation of a complexeAttribut.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractComplexAttribute<V extends Collection<Property>,I extends Identifier> extends DefaultAttribute<V,AttributeDescriptor,I>
        implements ComplexAttribute {
    
    protected AbstractComplexAttribute(final AttributeDescriptor descriptor, final I id) {
        super( descriptor, id );
    }

    protected AbstractComplexAttribute(final ComplexType type, final I id) {
        super( type, id );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComplexType getType() {
        return (ComplexType)super.getType();
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
    public Collection<Property> getProperties(final Name name) {
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
    public Collection<Property> getProperties(final String name) {
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
    public Property getProperty(final Name name) {
        if(name.getNamespaceURI() == null){
            return getProperty(name.getLocalPart());
        }
        //TODO find a faster way, hashmap ?
        for(Property prop : getProperties()){
            if(name.equals(prop.getName())){
                return prop;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Property getProperty(final String name) {
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
    public void setValue(final Object newValue) throws IllegalArgumentException,
            IllegalStateException {
        setValue((Collection<Property>)newValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(final Collection<Property> newValues) {
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
        return toString(3,5);
    }
    
    public String toString(int depth, int maxarray) {
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
        tablewriter.write("name\tid\tvalue\n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);

        final Set<FeatureId> visited = new HashSet<FeatureId>();        
        toString(tablewriter, this, null, true, depth, maxarray, visited);

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
    private static final String LINE =   "\u00A0\u00A0\u2502\u00A0";
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
    private static void toString(final TableWriter tablewriter, final Property property, 
            final Integer index, final boolean last, final int depth, final int maxArray,
            final Set<FeatureId> visited,final String... path){

        //draw the path.
        for(String t : path){
            tablewriter.write(t);
        }

        //write property name
        Name name = property.getName();
        if(name == null){
            //use the type name
            name = property.getType().getName();
        }
        
        if(name != null){
            tablewriter.write(DefaultName.toJCRExtendedForm(name));
        }
                
        //write the index if one
        if(index != null){
            tablewriter.write('[');
            tablewriter.write(index.toString());
            tablewriter.write(']');
        }
        
        if(property instanceof Association){
            tablewriter.write("  <ASSO> ");
            //check if we already visited this element
            //with complex type and associations this can happen
            final Object value = property.getValue();
            if(value instanceof Feature){
                final Feature f = (Feature) value;
                if(visited.contains(f.getIdentifier())){
                    tablewriter.write(" <CYCLIC> \t");
                    tablewriter.write(f.getIdentifier().getID());
                    tablewriter.write("\n");
                    return;
                }
            }
        } else if(property instanceof Feature){
            //check if we already visited this element
            //with complex type and associations this can happen
            final Feature f = (Feature) property;
            if(visited.contains(f.getIdentifier())){
                tablewriter.write(" <CYCLIC> \t\t \n");
                return;
            }
            visited.add(f.getIdentifier());
        }
        
        //check if we reached depth limit
        if( (depth <= 1 && (property.getType() instanceof ComplexType || property.getType() instanceof AssociationType))
            || depth == 0 ){
            tablewriter.write(" ⋅⋅⋅ \t\t \n");
            return;
        }
        
        
        

        tablewriter.write('\t');

        final PropertyType pt = property.getType();
        if(pt instanceof ComplexType){
            //show id in value column if a feature
            if(property instanceof Feature){
                tablewriter.write(((Feature)property).getIdentifier().getID());
            }
            
            tablewriter.write('\n');
            
            final ComplexType ct = (ComplexType) pt;
            final ComplexAttribute ca = (ComplexAttribute)property;

            int nbProperty = ca.getProperties().size();
            int nb=0;

            String[] subPath = last(path, LINE);

            for(PropertyDescriptor desc : ct.getDescriptors()){

                if(desc.getMaxOccurs()==1){
                    final Property sub = ca.getProperty(desc.getName());
                    if(sub != null){
                        nb++;
                        if(last){ subPath = last(path, BLANCK); }

                        toString(tablewriter, sub, null, nb==nbProperty, depth-1, 
                                maxArray, visited, append(subPath, (nb==nbProperty)?END:CROSS));
                        
                    }
                }else{
                    final Collection<? extends Property> properties = ca.getProperties(desc.getName());
                    int i = 0;
                    int n = properties.size()-1;
                    final int k = nb;
                    for(Property sub : properties){
                        nb++;
                        if(last){ subPath = last(path, BLANCK); }
                        
                        if(i==maxArray){
                            //do not display to much values if there are plenty
                            final String[] ep = append(subPath, (k+n+1==nbProperty)?END:CROSS);
                            for(String t : ep) tablewriter.write(t);
                            tablewriter.write("... ");
                            tablewriter.write(Integer.toString(n));
                            tablewriter.write(" elements... \n");
                            nb += n-i;
                            break;
                            
                        }else if(i==n){
                            toString(tablewriter, sub, i, nb==nbProperty, depth-1, 
                                    maxArray, visited, append(subPath, (nb==nbProperty)?END:CROSS));
                        }else if(i == 0){
                            toString(tablewriter, sub, i, nb==nbProperty, depth-1, 
                                    maxArray, visited, append(subPath, (nb==nbProperty)?END:CROSS));
                        }else{
                            toString(tablewriter, sub, i, nb==nbProperty, depth-1, 
                                    maxArray, visited, append(subPath, (nb==nbProperty)?END:CROSS));
                        }
                        i++;
                    }
                }
            }

        }else if(pt instanceof AssociationType){
            //no value
            tablewriter.write('\n');
            
            //encode association value
            final Property ca = (Property) property.getValue();
            final String[] subPath = last(path, (last)?BLANCK:LINE);            
            toString(tablewriter, ca, null, true, depth-1, 
                    maxArray, visited, append(subPath, END));
            
        }else{
            //simple property
            String strValue = String.valueOf(property.getValue());
            if(strValue.length() > 100){
                //clip the string, to avoid a to big table
                strValue = strValue.substring(0, 100) +" ...";
            }
            
            tablewriter.write("\t");
            tablewriter.write(strValue);
            tablewriter.write('\n');
        }
    }

    private static String[] append(String[] array, final String end){
        array = Arrays.copyOf(array, array.length+1);
        array[array.length-1] = end;
        return array;
    }

    private static String[] last(String[] array, final String end){
        array = array.clone();
        if(array.length>0){
            array[array.length-1] = end;
        }
        return array;
    }
    
}
