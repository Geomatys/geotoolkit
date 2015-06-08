/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.feature.type;

import org.opengis.util.GenericName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.feature.simple.DefaultSimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.util.InternationalString;

/**
 * SimpleFeatureType with modifiable properties.
 * This can be used when creating recursive types.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ModifiableSimpleFeaturetype extends DefaultSimpleFeatureType implements ModifiableType {

    private boolean lock = false;
    private AttributeType parent;

    public ModifiableSimpleFeaturetype(final GenericName name, final List<AttributeDescriptor> schema,
            final GeometryDescriptor defaultGeometry, final boolean isAbstract,
            final List<Filter> restrictions, final AttributeType superType, final InternationalString description) {
        super(name, schema, defaultGeometry, isAbstract, restrictions, superType, description);
        this.descriptorsList = new ArrayList(this.descriptorsList){

            @Override
            public boolean add(Object e) {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                return super.add(e);
            }

            @Override
            public void add(int index, Object element) {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                super.add(index, element);
            }

            @Override
            public boolean addAll(Collection c) {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                return super.addAll(c);
            }

            @Override
            public boolean addAll(int index, Collection c) {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                return super.addAll(index, c);
            }

            @Override
            public boolean remove(Object o) {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                return super.remove(o);
            }

            @Override
            public Object remove(int index) {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                return super.remove(index);
            }

            @Override
            public boolean removeAll(Collection c) {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                return super.removeAll(c);
            }

            @Override
            public void clear() {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                super.clear();
            }

            @Override
            public Object set(int index, Object element) {
                if(lock) throw new IllegalArgumentException("Modifiable type has been locked");
                return super.set(index, element);
            }

        };
    }

    @Override
    public void lock() {
        this.lock = true;
    }

    @Override
    public boolean isLock() {
        return lock;
    }

    @Override
    public void changeProperty(final int index, PropertyDescriptor desc) {
        if(desc==null){
            this.descriptorsList.remove(index);
        }else{
            this.descriptorsList.set(index,desc);
        }
        rebuildPropertyMap();
    }

    @Override
    public List<PropertyDescriptor> getDescriptors() {
        return descriptorsList;
    }

    @Override
    public void changeParent(AttributeType parent) {
        this.parent = parent;
    }

    @Override
    public AttributeType getSuper() {
        if(this.parent != null){
            return parent;
        }else{
            return super.getSuper();
        }
    }

    @Override
    public void rebuildPropertyMap(){
        this.descriptors = descriptorsList.toArray(new PropertyDescriptor[0]);
        //for faster access
        types = new AttributeType[descriptors.length];
        for(int i=0; i<descriptors.length;i++){
            types[i] = (AttributeType) descriptors[i].getType();
        }
        typesList = UnmodifiableArrayList.wrap(types);
        super.rebuildPropertyMap();
    }
}
