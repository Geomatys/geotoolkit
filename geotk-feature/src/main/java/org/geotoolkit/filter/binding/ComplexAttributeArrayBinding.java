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

import java.util.Iterator;
import org.opengis.feature.Feature;
import org.opengis.feature.PropertyType;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ComplexAttributeArrayBinding extends AbstractBinding<Feature>{

    public ComplexAttributeArrayBinding() {
        super(Feature.class, 21);
    }

    private int toIndex(final String xpath){
        String num = xpath.substring(2, xpath.length()-1);

        if(num.startsWith("position()=")){
            num = num.substring(11);
        }

        return Integer.valueOf(num);
    }

    @Override
    public boolean support(String xpath) {
        return xpath.startsWith("*[") && xpath.endsWith("]");
    }

    @Override
    public <T> T get(Feature candidate, String xpath, Class<T> target) throws IllegalArgumentException {
        if(candidate==null) return null;
        final int index = toIndex(xpath);

        final Iterator<? extends PropertyType> properties = candidate.getType().getProperties(true).iterator();
        int i=1;
        while(properties.hasNext()){
            final PropertyType prop = properties.next();
            if(i==index){
                return (T) candidate.getPropertyValue(prop.getName().toString());
            }
            i++;
        }
        return null;
    }

    @Override
    public void set(Feature candidate, String xpath, Object value) throws IllegalArgumentException {
        final int index = toIndex(xpath);

        final Iterator<? extends PropertyType> properties = candidate.getType().getProperties(true).iterator();
        int i=1;
        while(properties.hasNext()){
            final PropertyType prop = properties.next();
            if(i==index){
                candidate.setPropertyValue(prop.getName().toString(),value);
                break;
            }
            i++;
        }
    }

}
