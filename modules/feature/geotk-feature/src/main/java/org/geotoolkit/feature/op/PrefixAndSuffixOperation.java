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
package org.geotoolkit.feature.op;

import java.util.Collections;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.util.iso.Names;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Property;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Simple operation which concatenant another property with a prefix and a suffix.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PrefixAndSuffixOperation extends AbstractOperation {

    private static final DefaultParameterDescriptorGroup PARAMS =
            new DefaultParameterDescriptorGroup(Collections.singletonMap("name", "noargs"), 0, 1);
    private static final AttributeType RESULTTYPE = new DefaultAttributeType(Collections.singletonMap("name", "value"), String.class, 1, 1, null);

    private final String prefix;
    private final String suffix;
    private final GenericName ref;

    public PrefixAndSuffixOperation(String identification, String referenceProperty, String prefix, String suffix) {
        this(Names.createLocalName(null, ":", identification), Names.createLocalName(null, ":", referenceProperty),prefix,suffix);
    }

    public PrefixAndSuffixOperation(GenericName identification, GenericName referenceProperty, String prefix, String suffix) {
        super(Collections.singletonMap("name", identification));
        this.prefix = prefix;
        this.suffix = suffix;
        this.ref = referenceProperty;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        return PARAMS;
    }

    @Override
    public IdentifiedType getResult() {
        return RESULTTYPE;
    }

    public GenericName getReferenceProperty() {
        return ref;
    }

    public Attribute invoke(Feature feature, ParameterValueGroup parameters){
        final Attribute att = RESULTTYPE.newInstance();
        att.setValue(invoke(feature.getPropertyValue(ref.toString())));
        return att;
    }

    public String invoke(Object refPropertyValue){
        if(prefix==null){
            return new StringBuilder(String.valueOf(refPropertyValue)).append(suffix).toString();
        }else if(suffix==null){
            return new StringBuilder(prefix).append(refPropertyValue).toString();
        }else{
            return new StringBuilder(prefix).append(refPropertyValue).append(suffix).toString();
        }
    }

    public String reverse(String value){
        if(value==null || value.isEmpty()) return value;

        if(prefix!=null && value.startsWith(prefix)){
            value = value.substring(prefix.length());
        }

        if(suffix!=null && value.endsWith(suffix)){
            value = value.substring(0, value.length()-suffix.length());
        }

        return value;
    }

    @Override
    public Property apply(Feature feature, ParameterValueGroup parameters) {
        throw new UnsupportedOperationException();
    }
}
