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

import java.util.Set;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.util.Classes;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.io.TableAppender;
import org.geotoolkit.feature.ComplexAttribute;

import org.geotoolkit.feature.Property;
import org.opengis.filter.Filter;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import static org.geotoolkit.util.StringUtilities.*;
import org.opengis.util.GenericName;


/**
 * Base class for complex types.
 *
 * @author gabriel
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 * @author Johann Sorel (Geomatys)
 * @module pending
 *
 * @deprecated To be replaced by subtypes of Apache SIS {@link org.apache.sis.feature.AbstractIdentifiedType}.
 */
@Deprecated
public class DefaultComplexType extends DefaultAttributeType<AttributeType> implements ComplexType {

    /**
     * Immutable copy of the properties list with which we were constructed.
     */
    protected PropertyDescriptor[] descriptors;
    protected List<PropertyDescriptor> descriptorsList;

    /**
     * Map to locate properties by name or string.
     */
    private final Map<Object, PropertyDescriptor> propertyMap;

    public DefaultComplexType(final GenericName name, final Collection<PropertyDescriptor> properties,
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
                final GenericName pn = pd.getName();
                this.propertyMap.put(pn, pd);
                this.propertyMap.put(pn.tip().toString(), pd);
                this.propertyMap.put(NamesExt.toExtendedForm(pn), pd);
                this.propertyMap.put(NamesExt.toExpandedString(pn), pd);
            }

        }
        this.descriptorsList = UnmodifiableArrayList.wrap(this.descriptors);
    }

    protected void rebuildPropertyMap(){
        propertyMap.clear();
        for(int i=descriptors.length-1 ;i>=0;i--){
            PropertyDescriptor pd = descriptors[i];
            if (pd == null) {
                // descriptor entry may be null if a request was made for a property that does not exist
                throw new NullPointerException("PropertyDescriptor is null - did you request a property that does not exist?");
            }
            final GenericName pn = pd.getName();
            this.propertyMap.put(pn, pd);
            this.propertyMap.put(pn.tip().toString(), pd);
            this.propertyMap.put(NamesExt.toExtendedForm(pn), pd);
            this.propertyMap.put(NamesExt.toExpandedString(pn), pd);
        }
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
    public PropertyDescriptor getDescriptor(final GenericName name) {
        final String ns = NamesExt.getNamespace(name);
        if(ns==null || ns.isEmpty()){
            return getDescriptor(name.toString());
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

    @Override
    public ComplexAttribute newInstance() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ComplexType && super.equals(o)) {
            final ComplexType that = (ComplexType) o;
            return Objects.deepEquals(this.getDescriptors(), that.getDescriptors());
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 59 * super.hashCode();
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
            sb.append(superType.getName().tip().toString());
        }
        sb.append('\n');

        boolean first = true;

        //make a nice table to display
        final StringWriter writer = new StringWriter();
        final TableAppender tablewriter = new TableAppender(writer);
        tablewriter.appendHorizontalSeparator();
        tablewriter.append("name\t min\t max\t nillable\t type\t CRS\t UserData\n");
        tablewriter.appendHorizontalSeparator();

        final Collection<PropertyDescriptor> descs = getDescriptors();
        final Set<GenericName> loops = new HashSet<GenericName>();
        loops.add(this.getName());
        for (PropertyDescriptor property : descs) {
            tablewriter.append(toString(property,loops));
            tablewriter.append('\n');
        }
        tablewriter.appendHorizontalSeparator();
        try {
            tablewriter.flush();
            writer.flush();
        } catch (IOException ex) {
            //will never happen is this case
            ex.printStackTrace();
        }
        sb.append(writer.getBuffer().toString());

        if (super.getDescription() != null) {
            sb.append("\n\tdescription=");
            sb.append(super.getDescription());
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

    private static String toString(final PropertyDescriptor property, final Set<GenericName> visited){
        final StringBuilder builder = new StringBuilder();

        //check if we are in a cycle
        final PropertyType type = property.getType();
        final boolean inCycle = visited.contains(type.getName());

        builder.append(NamesExt.toExpandedString(property.getName()));
        if(inCycle){
            builder.append(" <...CYCLIC...>");
        }

        builder.append("\t");
        builder.append(Integer.toString(property.getMinOccurs()));
        builder.append("\t");
        builder.append(Integer.toString(property.getMaxOccurs()));
        builder.append("\t");
        builder.append(Boolean.toString(property.isNillable()));
        builder.append("\t");
        final PropertyType pt = property.getType();
        if(pt instanceof ComplexType){
            builder.append("CX:").append( ((ComplexType)pt).getName().tip().toString() );
        }if(pt instanceof OperationType){
            builder.append("OP:").append( ((OperationType)pt).getClass().getSimpleName().replaceAll("Operation", "") );
        }else if(pt instanceof AssociationType){
            builder.append("AS:").append( ((AssociationType)pt).getRelatedType().getName().tip().toString() );
        }else{
            builder.append(pt.getBinding().getSimpleName());
        }
        builder.append("\t");

        if(property instanceof GeometryDescriptor){
            final GeometryDescriptor desc = (GeometryDescriptor) property;
            final CoordinateReferenceSystem crs = desc.getCoordinateReferenceSystem();
            if(crs != null){
                try {
                    builder.append(String.valueOf(IdentifiedObjects.lookupIdentifier(crs, true)));
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
                builder.append(param.getValue());
                builder.append("  ");
            }
        }

        if(!inCycle && type instanceof ComplexType){
            final ComplexType ct = (ComplexType) type;
            final Collection<PropertyDescriptor> descs = ct.getDescriptors();

            if(!descs.isEmpty()){
                builder.append('\n');
            }

            visited.add(ct.getName());
            builder.append(toStringTree(descs,visited));
            visited.remove(ct);
        }else if(!inCycle && type instanceof AssociationType){
            final AssociationType ct = (AssociationType) type;
            final AttributeType at = ct.getRelatedType();
            visited.add(ct.getName());
            if(at instanceof ComplexType){
                visited.add(at.getName());
                final Collection<PropertyDescriptor> descs = ((ComplexType)at).getDescriptors();
                if(!descs.isEmpty()){
                    builder.append('\n');
                }

                builder.append(toStringTree(descs,visited));
            }
        }

        return builder.toString();
    }

    /**
     *
     * @param objects : collection of properties to display as tree
     * @param cycles : descriptors already visited, to avoid infinite cycles
     * @return tree string form
     */
    private static String toStringTree(final Collection<PropertyDescriptor> objects, final Set<GenericName> cycles){
        final StringBuilder sb = new StringBuilder();

        final int size = objects.size();

        final Iterator<PropertyDescriptor> ite = objects.iterator();
        int i=1;
        while(ite.hasNext()){
            String sub = toString(ite.next(),cycles);

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
