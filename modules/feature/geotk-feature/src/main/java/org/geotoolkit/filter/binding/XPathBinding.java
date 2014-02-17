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
package org.geotoolkit.filter.binding;


import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.util.Converters;

import org.jaxen.JaxenException;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;


/**
 * Creates property accessors for XPath expressions.
 * 
 * @author Johann Sorel, Geomatys
 * @module pending
 */
public class XPathBinding<C> extends AbstractBinding<C> {

    public static class CAXPath extends XPathBinding<ComplexAttribute>{
        public CAXPath() {
            super(ComplexAttribute.class, 19);
        }        
    }
    
    public static class CTXPath extends XPathBinding<ComplexType>{
        public CTXPath() {
            super(ComplexType.class, 9);
        }        
    }
    
    public XPathBinding(Class<C> bindedClass, int priority) {
        super(bindedClass, priority);
    }

    @Override
    public boolean support(String xpath) {
        return true;
    }
    
//    @Override
//    public boolean canHandle(final Class type, final String xpath, final Class target) {
//
//        if(xpath == null || xpath.isEmpty()){
//            return false;
//        }
//
//        if (!ComplexAttribute.class.isAssignableFrom(type) 
//                && !PropertyType.class.isAssignableFrom(type)
//                && !PropertyDescriptor.class.isAssignableFrom(type)) {
//            return false; // we only work with complex types.
//        }
//
//        return true;
//    }

    @Override
    public <T> T get(C candidate, String path, Class<T> target) throws IllegalArgumentException {
        if(candidate==null) return null;

        try {
            final JaxenFeatureXPath xpath = JaxenFeatureXPath.create(path);
            Object v = xpath.evaluate(candidate);
            if(v instanceof Collection){
                //several property for this path
                final Collection properties = (Collection) v;
                if(target != null && target.isInstance(properties)){
                    return (T) properties;
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
                    return (T) prop;
                }else{
                    v = prop.getValue();
                }                    
            }

            if(target == null){
                return (T) v;
            }else{
                return Converters.convert(v, target);
            }

        } catch (JaxenException ex) {
            Logger.getLogger(XPathBinding.class.getName()).log(Level.WARNING, null, ex);
        }
        return null;
    }

    @Override
    public void set(final C candidate, final String xpath, final Object value) throws IllegalArgumentException {
        final Object obj = get(candidate,xpath,Property.class);

        if(obj instanceof Property){
            final Property prop = (Property)obj;
            prop.setValue(value);
        }else{
            throw new IllegalArgumentException("Can not set value for xpath : " + xpath);
        }
    }

}
