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
package org.geotoolkit.data.s57;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.internal.LazySet;
import org.geotoolkit.lang.Static;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TypeBanks extends Static {

    /**
     * The service loader. This loader and its iterator are not synchronized;
     * when doing an iteration, the iterator must be used inside synchronized blocks.
     */
    private static final ServiceLoader<TypeBank> loader = ServiceLoader.load(TypeBank.class);

    private static Map<Integer,String> ALL_TYPES;
    private static Map<Integer,PropertyDescriptor> ALL_PROPERTIES;

    private TypeBanks(){}

    /**
     * Returns the set of all type banks, optionally filtered by type and availability.
     * This method ensures also that the iterator backing the set is properly synchronized.
     * <p>
     * Note that the iterator doesn't need to be thread-safe; this is the accesses to the
     * underlying {@linkplain #loader}, directly or indirectly through its iterator, which
     * need to be thread-safe.
     *
     * @param  <T>  The type of bank to be returned.
     * @param  type The type of bank to be returned, or {@code null} for all kind of bank.
     * @return The set of banks for the given conditions.
     */
    private static synchronized <T extends TypeBank> Set<T> getBanks(final Class<T> type) {
        final Iterator<TypeBank> factories = loader.iterator();
        return new LazySet<T>(new Iterator<T>() {
            /**
             * The next bank to be returned by the {@link #next()} method, or {@code null}
             * if not yet computed. This field is set by the {@link #hasNext()} method.
             */
            private T next;

            /**
             * Returns {@code true} if there is more banks to return.
             * This implementation fetches immediately the next bank.
             */
            @Override
            @SuppressWarnings("unchecked")
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }
                synchronized (TypeBanks.class) {
                    while (factories.hasNext()) {
                        final TypeBank candidate = factories.next();
                        if (type == null || type.isInstance(candidate)) {
                            next = (T) candidate;
                            return true;
                        }
                    }
                }
                return false;
            }

            /**
             * Returns the next element in the iteration.
             */
            @Override
            public T next() {
                if (hasNext()) {
                    final T n = next;
                    next = null; // Tells to hasNext() that it will need to fetch a new element.
                    return n;
                }
                throw new NoSuchElementException("No more elements.");
            }

            /**
             * Unsupported operation, since this iterator is read-only.
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Can not remove elements from this iterator.");
            }
        });
    }

    private static synchronized Set<TypeBank> getBanks(){
        return getBanks(null);
    }

    /**
     * Find in which specification this type is defined.
     * @param name
     * @return
     * @throws DataStoreException
     */
    public static String getFeatureTypeSpecification(final String name) throws DataStoreException{
        String spec = null;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && spec == null){
            final TypeBank bank = ite.next();
            try{
                bank.getFeatureTypeCode(name);
                spec = bank.getSpecification();
            }catch(DataStoreException ex){
                //normal
            }
        }
        return spec;
    }

    public static int getFeatureTypeCode(final String name) throws DataStoreException{
        int code = -1;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && code == -1){
            final TypeBank bank = ite.next();
            try{
                code = bank.getFeatureTypeCode(name);
            }catch(DataStoreException ex){
                //normal
            }
        }
        if(code==-1){
            throw new DataStoreException("Name : "+name+" do not exist");
        }
        return code;
    }

    public static String getFeatureTypeName(final int code) throws DataStoreException{
        String name = null;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && name == null){
            final TypeBank bank = ite.next();
            try{
                name = bank.getFeatureTypeName(code);
            }catch(DataStoreException ex){
                //normal
            }
        }
        if(name==null){
            throw new DataStoreException("Code : "+name+" do not exist");
        }
        return name;
    }

    public static int getPropertyTypeCode(final String name) throws DataStoreException{
        int code = -1;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && code == -1){
            final TypeBank bank = ite.next();
            try{
                code = bank.getPropertyTypeCode(name);
            }catch(DataStoreException ex){
                //normal
            }
        }
        if(code==-1){
            throw new DataStoreException("Name : "+name+" do not exist");
        }
        return code;
    }

    public static String getPropertyTypeName(final int code) throws DataStoreException{
        String name = null;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && name == null){
            final TypeBank bank = ite.next();
            try{
                name = bank.getPropertyTypeName(code);
            }catch(DataStoreException ex){
                //normal
            }
        }
        if(name==null){
            throw new DataStoreException("Code : "+name+" do not exist");
        }
        return name;
    }

    public static FeatureType getFeatureType(final String name, CoordinateReferenceSystem crs) throws DataStoreException{
        FeatureType type = null;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && type == null){
            final TypeBank bank = ite.next();
            try{
                type = bank.getFeatureType(name,crs);
            }catch(DataStoreException ex){
                //normal
            }
        }
        if(type==null){
            throw new DataStoreException("Name : "+name+" do not exist");
        }
        return type;
    }

    public static FeatureType getFeatureType(final int code, CoordinateReferenceSystem crs) throws DataStoreException{
        FeatureType type = null;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && type == null){
            final TypeBank bank = ite.next();
            try{
                type = bank.getFeatureType(code,crs);
                if(type!=null) break;
            }catch(DataStoreException ex){
                //normal
            }
        }
        if(type==null){
            throw new DataStoreException("Code : "+code+" do not exist");
        }
        return type;
    }

    public static AttributeDescriptor getAttributeDescriptor(final String name) throws DataStoreException{
        AttributeDescriptor type = null;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && type == null){
            final TypeBank bank = ite.next();
            try{
                type = bank.getAttributeDescriptor(name);
            }catch(DataStoreException ex){
                //normal
            }
        }
        if(type==null){
            throw new DataStoreException("Name : "+name+" do not exist");
        }
        return type;
    }

    public static AttributeDescriptor getAttributeDescriptor(final int code) throws DataStoreException{
        AttributeDescriptor type = null;
        final Iterator<TypeBank> ite = getBanks().iterator();
        while(ite.hasNext() && type == null){
            final TypeBank bank = ite.next();
            try{
                type = bank.getAttributeDescriptor(code);
            }catch(DataStoreException ex){
                //normal
            }
        }
        if(type==null){
            throw new DataStoreException("Code : "+code+" do not exist");
        }
        return type;
    }

    public static synchronized Map<Integer,String> getAllFeatureTypes() throws DataStoreException {
        if(ALL_TYPES==null){
            final Map<Integer,String> temp = new HashMap<>();
            for(TypeBank bank : getBanks()){
                for(String name : bank.getFeatureTypeNames()){
                    final int code = bank.getFeatureTypeCode(name);
                    temp.put(code, name);
                }
            }
            ALL_TYPES = Collections.unmodifiableMap(temp);
        }

        return ALL_TYPES;
    }

    public static synchronized Map<Integer,PropertyDescriptor> getAllProperties() throws DataStoreException{
        if(ALL_PROPERTIES==null){
            final Map<Integer,PropertyDescriptor> temp = new HashMap<>();
            for(TypeBank bank : getBanks()){
                for(String name : bank.getPropertyTypeNames()){
                    final AttributeDescriptor desc = bank.getAttributeDescriptor(name);
                    final Integer code = (Integer) desc.getUserData().get(S57FeatureStore.S57TYPECODE);
                    if(code == null){
                        throw new DataStoreException("Property S-57 code has not been set on attribute : "+desc);
                    }
                    temp.put(code, desc);
                }
            }
            ALL_PROPERTIES = Collections.unmodifiableMap(temp);
        }

        return ALL_PROPERTIES;
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is needed because the
     * application class path can theoretically change, or additional plug-ins may become available.
     * Rather than re-scanning the classpath on every invocation of the API, the class path is scanned
     * automatically only on the first invocation. Clients can call this method to prompt a re-scan.
     * Thus this method need only be invoked by sophisticated applications which dynamically make
     * new plug-ins available at runtime.
     */
    public static synchronized void scanForPlugins() {
        loader.reload();
    }


}
