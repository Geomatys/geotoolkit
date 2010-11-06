/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.filter.accessor;


import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.Converters;
import org.jaxen.JaxenException;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;


/**
 * Creates property accessors for XPath expressions.
 * 
 * @author Johann Sorel, Geomatys
 * @module pending
 */
public final class XPathPropertyAccessorFactory implements PropertyAccessorFactory {

    private static final XPathPropertyAccessor ACCESSOR = new XPathPropertyAccessor();

    @Override
    public PropertyAccessor createPropertyAccessor(Class type, String xpath, Class target, Hints hints) {

        if(ACCESSOR.canHandle(type, xpath, target)){
            return ACCESSOR;
        }
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    private static class XPathPropertyAccessor implements PropertyAccessor{

        @Override
        public boolean canHandle(Class type, String xpath, Class target) {

            if(xpath == null || xpath.isEmpty()){
                return false;
            }

            if (!ComplexAttribute.class.isAssignableFrom(type)) {
                return false; // we only work with complex types.
            }

            //search anything that can be used to check if it's a valid xpath
            if(    xpath.indexOf('/') >= 0
                || xpath.indexOf('[') >0
                || xpath.indexOf('@') >0
                || xpath.indexOf('{') >0){
                //looks like an xpath, we accept it
                return true;
            }

            return false;
        }

        @Override
        public Object get(Object object, String path, Class target) throws IllegalArgumentException {

            try {
                final JaxenFeatureXPath xpath = JaxenFeatureXPath.create(path);
                Object v = xpath.evaluate(object);
                if(v instanceof Collection){
                    //several property for this path
                    final Collection properties = (Collection) v;
                    if(target != null && target.isInstance(properties)){
                        return properties;
                    }else{
                        final Iterator ite = properties.iterator();
                        if(ite.hasNext()){
                            v = ite.next();
                        }
                    }                    
                }
                
                if(v instanceof Property){
                    //extract value from property if necessary
                    final Property prop = (Property) v;
                    if(target != null && target.isInstance(prop)){
                        return prop;
                    }else{
                        v = prop.getValue();
                    }                    
                }
                
                if(target == null){
                    return v;
                }else{
                    return Converters.convert(v, target);
                }
                
            } catch (JaxenException ex) {
                Logger.getLogger(XPathPropertyAccessorFactory.class.getName()).log(Level.WARNING, null, ex);
            }
            return null;
        }

        @Override
        public void set(Object object, String xpath, Object value, Class target) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

}
