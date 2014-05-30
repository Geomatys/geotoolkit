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

import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.PropertyDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ComplexTypeArrayBinding extends AbstractBinding<ComplexType>{

    public ComplexTypeArrayBinding() {
        super(ComplexType.class, 11);
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
    public <T> T get(ComplexType candidate, String xpath, Class<T> target) throws IllegalArgumentException {
        if(candidate==null) return null;
        int index = toIndex(xpath);
        int i = 1;
        for(PropertyDescriptor prop : candidate.getDescriptors()){
            if(i == index){
                return (T) prop;
            }
            i++;
        }
        return null;
    }

    @Override
    public void set(ComplexType candidate, String xpath, Object value) throws IllegalArgumentException {
        throw new IllegalArgumentException("Types are immutable");
    }
    
}
