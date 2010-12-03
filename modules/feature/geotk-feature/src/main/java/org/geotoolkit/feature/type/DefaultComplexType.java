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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import static org.geotoolkit.util.StringUtilities.*;


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
     * Map to locate properties by name or string.
     */
    private final Map<Object, PropertyDescriptor> propertyMap;

    public DefaultComplexType(final Name name, final Collection<PropertyDescriptor> properties,
            final boolean identified, final boolean isAbstract, final List<Filter> restrictions,
            final AttributeType superType, final InternationalString description){
        super(name, Collection.class, identified, isAbstract, restrictions, superType, description);


        if (properties == null) {
            this.descriptors = new PropertyDescriptor[0];
            this.propertyMap = Collections.emptyMap();
        } else {
            this.descriptors = properties.toArray(new PropertyDescriptor[properties.size()]);

            //preserve order, in cas that several property has the same localpart
            //the first one must be returned by getDescriptor(String name)
            this.propertyMap = new HashMap<Object, PropertyDescriptor>();
            final PropertyDescriptor[] array = properties.toArray(new PropertyDescriptor[properties.size()]);
            for(int i=array.length-1 ;i>=0;i--){
                PropertyDescriptor pd = array[i];
                if (pd == null) {
                    // descriptor entry may be null if a request was made for a property that does not exist
                    throw new NullPointerException("PropertyDescriptor is null - did you request a property that does not exist?");
                }
                final Name pn = pd.getName();
                this.propertyMap.put(pn, pd);
                this.propertyMap.put(pn.getLocalPart(), pd);
                this.propertyMap.put(DefaultName.toExtendedForm(pn), pd);
                this.propertyMap.put(DefaultName.toJCRExtendedForm(pn), pd);
            }

        }
        this.descriptorsList = UnmodifiableArrayList.wrap(this.descriptors);
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
        return propertyMap.get(name);
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
            return Utilities.deepEquals(this.descriptors, that.descriptors);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 59 * super.hashCode() + Arrays.hashCode(descriptors);
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

        final Collection<PropertyDescriptor> descs = getDescriptors();
        for (PropertyDescriptor property : descs) {
            tablewriter.write(toString(property));
            tablewriter.write('\n');
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

    private static String toString(PropertyDescriptor property){
        final StringBuilder builder = new StringBuilder();

        builder.append(DefaultName.toJCRExtendedForm(property.getName()));
        builder.append("\t");
        builder.append(Integer.toString(property.getMinOccurs()));
        builder.append("\t");
        builder.append(Integer.toString(property.getMaxOccurs()));
        builder.append("\t");
        builder.append(Boolean.toString(property.isNillable()));
        builder.append("\t");
        final PropertyType pt = property.getType();
        if(pt instanceof ComplexType){
            builder.append("CX:").append( ((ComplexType)pt).getName().getLocalPart() );
        }else{
            builder.append(pt.getBinding().getSimpleName());
        }
        builder.append("\t");

        if(property instanceof GeometryDescriptor){
            final GeometryDescriptor desc = (GeometryDescriptor) property;
            final CoordinateReferenceSystem crs = desc.getCoordinateReferenceSystem();
            if(crs != null){
                try {
                    builder.append(String.valueOf(CRS.lookupIdentifier(crs, true)));
                } catch (FactoryException ex) {
                    builder.append("Error getting identifier");
                }
            }
        }else{
            builder.append("");
        }
        builder.append("\t");

        final Map<Object,Object> userDatas = property.getUserData();
        if(userDatas != null && !userDatas.isEmpty()){
            for(Map.Entry<Object,Object> param : userDatas.entrySet()){
                builder.append(param.getKey().toString());
                builder.append("=");
                builder.append(param.getValue().toString());
                builder.append("  ");
            }
        }

        if(property.getType() instanceof ComplexType){
            final ComplexType ct = (ComplexType) property.getType();
            final Collection<PropertyDescriptor> descs = ct.getDescriptors();

            if(!descs.isEmpty()){
                builder.append('\n');
            }

            builder.append(toStringTree(descs));
        }

        return builder.toString();
    }

    private static String toStringTree(Collection<PropertyDescriptor> objects){
        final StringBuilder sb = new StringBuilder();

        final int size = objects.size();

        final Iterator<PropertyDescriptor> ite = objects.iterator();
        int i=1;
        while(ite.hasNext()){
            String sub = toString(ite.next());

            if(i==size){
                sb.append(TREE_END);
                //move text to the right
                sub = sub.replaceAll("\n", "\n"+TREE_BLANK);
                sb.append(sub);
            }else{
                sb.append(TREE_CROSS);
                //move text to the right
                sub = sub.replaceAll("\n", "\n"+TREE_LINE);
                sb.append(sub);
                sb.append('\n');
            }
            i++;
        }
        return sb.toString();
    }

}
