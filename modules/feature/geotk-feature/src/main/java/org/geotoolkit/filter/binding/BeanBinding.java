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

import com.vividsolutions.jts.geom.Geometry;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.ObjectConverters;

/**
 * Binding for Beans.
 * Convention : if xpath is null or empty and target class is Geometry.
 * The searched property must have the name 'Geometry'
 *
 * @author Johann Sorel (Geomatys)
 */
public final class BeanBinding extends AbstractBinding<Object> implements Serializable {

    public BeanBinding() {
        super(Object.class, -1000);
    }

    @Override
    public boolean support(String xpath) {
        return true;
    }

    @Override
    public <T> T get(Object candidate, String xpath, Class<T> target) throws IllegalArgumentException {
        if((xpath==null || xpath.isEmpty()) &&Geometry.class.isAssignableFrom(target)){
            xpath = "geometry";
        }

        if(candidate==null) return null;

        try {
            final BeanInfo info = Introspector.getBeanInfo(candidate.getClass());
            final PropertyDescriptor[] descs = info.getPropertyDescriptors();
            if(descs==null)return null;
            for(PropertyDescriptor d : descs){
                if(d.getName().equalsIgnoreCase(xpath)) {
                    final Method readMethod = d.getReadMethod();
                    if (readMethod == null) return null;
                    
                    Object o = readMethod.invoke(candidate);
                    if (o == null || target == null || target.isInstance(o)) {
                        return (T) o;
                    } else {
                        return ObjectConverters.convert(o, target);
                    }
                }
            }
            return null;
        } catch (IntrospectionException | ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void set(Object candidate, String xpath, Object value) throws IllegalArgumentException {
        try {
            final BeanInfo info = Introspector.getBeanInfo(candidate.getClass());
            final PropertyDescriptor[] descs = info.getPropertyDescriptors();
            if (descs == null)
                return;
            for (PropertyDescriptor d : descs) {
                if (d.getName().equalsIgnoreCase(xpath)) {

                    final Method writeMethod = d.getWriteMethod();
                    if (writeMethod == null)
                        return;

                    Class<?> propertyType = d.getPropertyType();
                    propertyType = Numbers.primitiveToWrapper(propertyType);
                    if (value == null || propertyType.isInstance(value)) {
                        writeMethod.invoke(candidate, value);
                    } else {
                        writeMethod.invoke(candidate,
                                ObjectConverters.convert(value, propertyType));
                    }
                }
            }
        } catch (IntrospectionException | ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
