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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 * Pattern concatenation operation.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PatternOperation extends AbstractOperation {

    private static final ParameterDescriptorGroup PARAMS =
            new DefaultParameterDescriptorGroup(Collections.singletonMap("name", "noargs"), 0, 1);
    private static final AttributeType RESULTTYPE = new DefaultAttributeType(Collections.singletonMap("name", "value"), String.class, 1, 1, null);

    private final GenericName[] refs;
    private final Pattern pattern;

    public PatternOperation(String identification, String regex, String ... referenceProperties) {
        this(Names.createLocalName(null, ":", identification),regex,toNameArray(referenceProperties));
    }

    public PatternOperation(GenericName identification, String regex, GenericName ... referneceProperties) {
        super(Collections.singletonMap("name", identification));
        this.pattern = Pattern.compile(regex);
        this.refs = referneceProperties;
    }

    private static GenericName[] toNameArray(String ... referenceProperties){
        final GenericName[] names = new GenericName[referenceProperties.length];
        for(int i=0;i<referenceProperties.length;i++){
            names[i] = Names.createLocalName(null, ":", referenceProperties[i]);
        }
        return names;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        return PARAMS;
    }

    @Override
    public IdentifiedType getResult() {
        return RESULTTYPE;
    }

    public GenericName[] getReferenceProperties() {
        return refs;
    }

    public Attribute invoke(Feature feature, ParameterValueGroup parameters){
        final Attribute att = RESULTTYPE.newInstance();
        final Object[] values = new Object[refs.length];
        for(int i=0;i<refs.length;i++) values[i] = feature.getPropertyValue(refs[i].toString());
        att.setValue(invoke(values));
        return att;
    }

    public String invoke(Object[] refPropertyValues){
        //TODO
        throw new UnsupportedOperationException("Waiting for operation interface to be finalized before implementation.");
    }

    public String[] reverse(String value){
        final String[] res = new String[refs.length];
        if(value==null || value.isEmpty()) return res;

        final Matcher m = pattern.matcher(value);
        if (m.matches()) {
            for(int i=0;i<refs.length;i++){
                res[i] = m.group(i+1);
            }
        }
        return res;
    }

    @Override
    public Property apply(Feature feature, ParameterValueGroup parameters) {
        throw new UnsupportedOperationException();
    }
}
