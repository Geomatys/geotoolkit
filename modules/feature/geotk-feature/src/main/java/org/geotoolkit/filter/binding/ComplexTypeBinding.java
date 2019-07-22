/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.filter.binding;

import java.util.Collections;
import java.util.regex.Pattern;
import org.apache.sis.feature.DefaultAssociationRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.util.Classes;
import org.apache.sis.util.resources.Errors;
import static org.geotoolkit.filter.binding.AttributeBinding.stripPrefix;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ComplexTypeBinding extends AbstractBinding<FeatureType>{
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(\\w+:)?(.+)");

    public ComplexTypeBinding() {
        super(FeatureType.class, 10);
    }

    @Override
    public boolean support(String xpath) {
        return !xpath.startsWith("/") && PROPERTY_PATTERN.matcher(xpath).matches();
    }

    @Override
    public <T> T get(FeatureType type, String xpath, Class<T> target) throws IllegalArgumentException {
        if(type==null) return null;
        xpath = stripPrefix(xpath);

        if (!xpath.isEmpty() && xpath.charAt(0) == '{') {
            xpath = NamesExt.valueOf(xpath).toString();
        }


        String name = xpath;
        PropertyType propertyType;
        try {
            propertyType = type.getProperty(name);         // May throw IllegalArgumentException.
            final GenericName baseName = propertyType.getName();
            while (propertyType instanceof Operation) {
                final IdentifiedType it = ((Operation) propertyType).getResult();
                if (it instanceof PropertyType) {
                    propertyType = (PropertyType) it;
                } else if (it instanceof FeatureType) {
                    return (T) new DefaultAssociationRole(Collections.singletonMap(DefaultAssociationRole.NAME_KEY, baseName), type, 1, 1);
                } else {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalPropertyValueClass_3,
                                name, PropertyType.class, Classes.getStandardType(Classes.getClass(it))));
                }
            }

            //preserve original name
            if (!baseName.equals(propertyType.getName())) {
                if (propertyType instanceof AttributeType) {
                    propertyType = new FeatureTypeBuilder().addAttribute((AttributeType) propertyType).setName(baseName).build();
                }
            }
        } catch(PropertyNotFoundException ex) {
            return null;
        }

        return (T) propertyType;
    }

    @Override
    public void set(FeatureType candidate, String xpath, Object value) throws IllegalArgumentException {
        throw new IllegalArgumentException("Types are immutable");
    }

}
