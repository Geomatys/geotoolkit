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

import java.util.regex.Pattern;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyNotFoundException;
import static org.geotoolkit.filter.binding.AttributeBinding.stripPrefix;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ComplexAttributeBinding extends AbstractBinding<Feature>{
    private static final Pattern ID_PATTERN       = Pattern.compile("@(\\w+:)?id");
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(\\w+:)?(.+)");

    public ComplexAttributeBinding() {
        super(Feature.class, 20);
    }

    @Override
    public boolean support(String xpath) {
        return !xpath.startsWith("/") &&
               !xpath.startsWith("*") &&
               (PROPERTY_PATTERN.matcher(xpath).matches() || ID_PATTERN.matcher(xpath).matches());
    }

    @Override
    public <T> T get(Feature candidate, String xpath, Class<T> target) throws IllegalArgumentException {
        if(candidate==null) return null;
        xpath = stripPrefix(xpath);
        if(ID_PATTERN.matcher(xpath).matches()){
            return (T) FeatureExt.getId(candidate).getID();
        }

        if(target != null){
            if(Property.class.isAssignableFrom(target)){
                return (T) candidate.getPropertyValue(xpath);
            }
        }

        if (!xpath.isEmpty() && xpath.charAt(0) == '{') {
            xpath = NamesExt.valueOf(xpath).toString();
        }

        try{
            return (T) candidate.getPropertyValue(xpath);
        }catch(PropertyNotFoundException ex){
            return null;
        }

    }

    @Override
    public void set(Feature candidate, String xpath, Object value) throws IllegalArgumentException {
        xpath = stripPrefix(xpath);
        candidate.setPropertyValue(xpath,value);
    }

}
