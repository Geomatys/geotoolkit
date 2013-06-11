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

import org.opengis.feature.Attribute;
import org.opengis.feature.Property;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AttributeBinding extends AbstractBinding<Object>{

    public AttributeBinding() {
        super(Object.class, 25);
    }
    
    @Override
    public boolean support(String xpath) {
        return ".".equalsIgnoreCase(xpath);
    }

    @Override
    public <T> T get(Object candidate, String xpath, Class<T> target) throws IllegalArgumentException {
        if(candidate==null)return null;
        
        if(candidate instanceof Attribute){
            if(Property.class.isAssignableFrom(target)){
                return (T) candidate;
            }else{
                return (T) ((Attribute)candidate).getValue();
            }
        }
        
        return (T) candidate;
        
    }

    @Override
    public void set(Object candidate, String xpath, Object value) throws IllegalArgumentException {
        if(candidate instanceof Attribute){
            ((Attribute)candidate).setValue(value);
        }
    }
    
    /**
     * We strip off namespace prefix, we need new feature model to do this
     * property
     * <ul>
     * <li>BEFORE: foo:bar
     * <li>AFTER: bar
     * </ul>
     * 
     * @param xpath
     * @return xpath with any XML prefixes removed
     */
    static String stripPrefix(String xpath) {
        while(xpath.charAt(0) == '/'){
            xpath = xpath.substring(1);
        }
        return xpath;
    }
    
}
