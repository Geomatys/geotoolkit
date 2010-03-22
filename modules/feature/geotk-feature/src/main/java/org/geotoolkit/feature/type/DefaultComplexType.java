/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature.type;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.referencing.CRS;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.util.InternationalString;


/**
 * Base class for complex types.
 *
 * @author gabriel
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultComplexType extends DefaultAttributeType<AttributeType> implements ComplexType {

    /**
     * Immutable copy of the properties list with which we were constructed.
     */
    protected final PropertyDescriptor[] descriptors;
    protected final List<PropertyDescriptor> descriptorsList;

    /**
     * Map to locate properties by name.
     */
    private final Map<Name, PropertyDescriptor> propertyMap;

    public DefaultComplexType(final Name name, final Collection<PropertyDescriptor> properties,
            final boolean identified, final boolean isAbstract, final List<Filter> restrictions,
            final AttributeType superType, final InternationalString description){
        super(name, Collection.class, identified, isAbstract, restrictions, superType, description);

        final Map<Name, PropertyDescriptor> localPropertyMap;

        if (properties == null) {
            this.descriptors = new PropertyDescriptor[0];
            localPropertyMap = Collections.emptyMap();
        } else {
            this.descriptors = properties.toArray(new PropertyDescriptor[properties.size()]);

            localPropertyMap = new HashMap<Name, PropertyDescriptor>();
            for (PropertyDescriptor pd : properties) {
                if (pd == null) {
                    // descriptor entry may be null if a request was made for a property that does not exist
                    throw new NullPointerException("PropertyDescriptor is null - did you request a property that does not exist?");
                }
                localPropertyMap.put(pd.getName(), pd);
            }

        }
        this.descriptorsList = UnmodifiableArrayList.wrap(this.descriptors);
        this.propertyMap = Collections.unmodifiableMap(localPropertyMap);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<Collection<Property>> getBinding() {
        return (Class<Collection<Property>>) super.getBinding();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<PropertyDescriptor> getDescriptors() {
        return descriptorsList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyDescriptor getDescriptor(final Name name) {
        if(name.getNamespaceURI() == null){
            return getDescriptor(name.getLocalPart());
        }else{
            return propertyMap.get(name);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyDescriptor getDescriptor(final String name) {
        // this method should be deprecated
        for (Name propertyName: propertyMap.keySet()) {
            if (propertyName.getLocalPart().equals(name)) {
                return propertyMap.get(propertyName);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isInline() {
        //JD: at this point "inlining" is unused... we might want to kill it
        // from the interface
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DefaultComplexType && super.equals(o)) {
            DefaultComplexType that = (DefaultComplexType) o;
            return Utilities.equals(this.descriptorsList, that.descriptorsList);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 59 * super.hashCode() + descriptors.hashCode();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append(" ");
        sb.append(getName());
        if (isAbstract()) {
            sb.append(" abstract");
        }
        if (isIdentified()) {
            sb.append(" identified");
        }
        if (superType != null) {
            sb.append(" extends ");
            sb.append(superType.getName().getLocalPart());
        }
        sb.append('\n');

        boolean first = true;

        //make a nice table to display
        final StringWriter writer = new StringWriter();
        final TableWriter tablewriter = new TableWriter(writer);
        tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        tablewriter.write("name\t min\t max\t nillable\t type\t CRS\t UserData\n");
        tablewriter.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);

        for (PropertyDescriptor property : getDescriptors()) {
            toString(tablewriter, property, 0, 0);
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

        if (description != null) {
            sb.append("\n\tdescription=");
            sb.append(description);
        }
        if (restrictions != null && !restrictions.isEmpty()) {
            sb.append("\nrestrictions=");
            first = true;
            for (Filter filter : restrictions) {
                if (first) {
                    first = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append(filter);
            }
        }
        return sb.toString();
    }

    private static void toString(TableWriter tablewriter, PropertyDescriptor property, int depth, int pos){

        if(depth != 0){
            for(int i=0; i<depth-1;i++){
                tablewriter.write("\u00A0");
            }
            if(pos == 0){
                tablewriter.write("\u251C\u2500\u2500");
            }else if(pos == 1){
                tablewriter.write("\u2514\u2500\u2500");
            }else{
                tablewriter.write("\u251C\u2500\u2500");
            }
        }

        tablewriter.write(DefaultName.toJCRExtendedForm(property.getName()));
        tablewriter.write("\t");
        tablewriter.write(Integer.toString(property.getMinOccurs()));
        tablewriter.write("\t");
        tablewriter.write(Integer.toString(property.getMaxOccurs()));
        tablewriter.write("\t");
        tablewriter.write(Boolean.toString(property.isNillable()));
        tablewriter.write("\t");
        tablewriter.write(property.getType().getBinding().getSimpleName());
        tablewriter.write("\t");

        if(property instanceof GeometryDescriptor){
            final GeometryDescriptor desc = (GeometryDescriptor) property;
            try {
                tablewriter.write(String.valueOf(CRS.lookupIdentifier(desc.getCoordinateReferenceSystem(), true)));
            } catch (FactoryException ex) {
                tablewriter.write("Error getting identifier");
            }
        }else{
            tablewriter.write("");
        }
        tablewriter.write("\t");

        final Map<Object,Object> userDatas = property.getUserData();
        if(userDatas != null && !userDatas.isEmpty()){
            for(Map.Entry<Object,Object> param : userDatas.entrySet()){
                tablewriter.write(param.getKey().toString());
                tablewriter.write("=");
                tablewriter.write(param.getValue().toString());
                tablewriter.write("  ");
            }
        }

        tablewriter.write("\n");

        System.out.println(">>> "+property);
        if(property.getType() instanceof ComplexType){
            final ComplexType ct = (ComplexType) property.getType();
            final Collection<PropertyDescriptor> descs = ct.getDescriptors();
            int i=0;
            int n=descs.size()-1;
            for(PropertyDescriptor desc : descs){
                if(i==n){
                    toString(tablewriter, desc, depth+1, 1);
                }else if(i == 0){
                    toString(tablewriter, desc, depth+1, 0);
                }else{
                    toString(tablewriter, desc, depth+1, -1);
                }
                i++;
            }
        }

    }

}
