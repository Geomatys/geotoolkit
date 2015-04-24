/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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
package org.geotoolkit.data.bean;

import com.vividsolutions.jts.geom.Geometry;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.AbstractFeature;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.InvalidPropertyValueException;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BeanFeature extends AbstractFeature{

    public static final String KEY_BEAN = "bean";

    private final Object bean;
    private final Mapping mapping;

    public BeanFeature(Object bean, Mapping mapping){
        super(mapping.featureType);
        this.bean = bean;
        this.mapping = mapping;
    }
    
    public Object getBean(){
        return bean;
    }

    @Override
    public Property getProperty(String string) throws PropertyNotFoundException {
        final PropertyType pt = mapping.featureType.getProperty(string);
        return new BeanAttributeProperty((AttributeType) pt);
    }

    @Override
    public void setProperty(Property prprt) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Object getPropertyValue(String string) throws PropertyNotFoundException {
        return getProperty(string).getValue();
    }

    @Override
    public void setPropertyValue(String string, Object o) throws IllegalArgumentException {
        Property property = getProperty(string);
        if(property instanceof Attribute){
            ((Attribute)property).setValue(o);
        }else{
            throw new IllegalArgumentException("Property "+string+" can not be set.");
        }
        
    }

    public static class Mapping {
        public final FeatureType featureType;
        public final String idField;
        public final Map<String,java.beans.PropertyDescriptor> accessors = new HashMap<>();
        public java.beans.PropertyDescriptor idAccessor;

        public Mapping(Class clazz, String namespace, CoordinateReferenceSystem crs, String idField) {
            this(clazz,namespace,crs,idField,null, new Predicate<java.beans.PropertyDescriptor>() {
                @Override
                public boolean test(java.beans.PropertyDescriptor t) {
                    return true;
                }
            });
        }

        public Mapping(Class clazz, String namespace, CoordinateReferenceSystem crs, String idField, String defaultGeom, Predicate<java.beans.PropertyDescriptor> filter) {
            this.idField = idField;
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(namespace,clazz.getSimpleName());
            try {
                for (java.beans.PropertyDescriptor pd : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {

                    final String propName = pd.getName();
                    if(propName.equals(idField)){
                        //ignore the id field, it will be used as featureId
                        idAccessor = pd;
                        continue;
                    }

                    final Method readMethod = pd.getReadMethod();
                    if(readMethod==null) continue;

                    if(!filter.test(pd)) continue;

                    final Class propClazz = readMethod.getReturnType();
                    if(Geometry.class.isAssignableFrom(propClazz)){
                        final AttributeTypeBuilder atb = ftb.addAttribute(propClazz).setName(propName).setCRS(crs);
                        if(defaultGeom==null || defaultGeom.equals(atb.getName().tip().toString())){
                            defaultGeom = atb.getName().tip().toString();
                            atb.addRole(AttributeRole.DEFAULT_GEOMETRY);
                        }
                    }else{
                        ftb.addAttribute(propClazz).setName(propName);
                    }
                    accessors.put(propName, pd);
                }
            } catch (IntrospectionException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            featureType = ftb.build();
        }

        public FeatureId buildId(Object bean){
            try {
                return new DefaultFeatureId(String.valueOf(idAccessor.getReadMethod().invoke(bean)));
            } catch (ReflectiveOperationException | IllegalArgumentException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
        }

    }

    private class BeanAttributeProperty implements Attribute{

        private final AttributeType desc;

        public BeanAttributeProperty(AttributeType desc){
            this.desc = desc;
        }

        @Override
        public Object getValue() {
            try {
                final Method m = mapping.accessors.get(getName().tip().toString()).getReadMethod();
                if(m==null) return null;
                return m.invoke(bean);
            } catch (ReflectiveOperationException | IllegalArgumentException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
        }

        @Override
        public void setValue(Object newValue) {
            try {
                final Method m = mapping.accessors.get(getName().tip().toString()).getWriteMethod();
                if(m==null) return;
                m.invoke(bean,newValue);
            } catch (ReflectiveOperationException | IllegalArgumentException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
        }

        @Override
        public GenericName getName() {
            return getType().getName();
        }
        
        @Override
        public AttributeType getType() {
            return desc;
        }

        @Override
        public Collection<Object> getValues() {
            return Collections.singleton(getValue());
        }

        @Override
        public void setValues(Collection clctn) throws InvalidPropertyValueException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map characteristics() {
            return Collections.EMPTY_MAP;
        }

    }

}
