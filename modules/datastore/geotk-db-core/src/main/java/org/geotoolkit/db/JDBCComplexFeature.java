/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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
package org.geotoolkit.db;

import com.vividsolutions.jts.geom.Geometry;
import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.GenericAssociationIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.db.reverse.RelationMetaModel;
import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Property;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.identity.FeatureId;

/**
 * JDBC feature, handle both simple and complexe types.
 * Complex properties are retrieve throut foreign key definitions.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JDBCComplexFeature extends AbstractFeature<Collection<Property>> {
        
    private final DefaultJDBCFeatureStore store;
    private final Map<Name,Object> progressiveMap = new HashMap<Name, Object>();
    private final FeatureType type;
    
    public JDBCComplexFeature(final DefaultJDBCFeatureStore store, final ResultSet rs,
            final FeatureType type, final FeatureId id) throws SQLException, DataStoreException{
        super(type, id);
        this.type = type;
        this.store = store;
        
        int k=0;
        for(final PropertyDescriptor desc : type.getDescriptors()){
            final Name n = desc.getName();
            final PropertyType ptype = desc.getType();
            
            final Object prop;
            if(ptype instanceof AssociationType){
                final AssociationDescriptor assDesc = (AssociationDescriptor) desc;
                final RelationMetaModel template = (RelationMetaModel) desc.getUserData().get(JDBCFeatureStore.JDBC_PROPERTY_RELATION);
                
                if(template != null){
                    //create a dynamic collection
                    final Object key = rs.getObject(template.getCurrentColumn());
                    
                    final QueryBuilder qb = new QueryBuilder();
                    qb.setTypeName(new DefaultName(
                            store.getDefaultNamespace(), 
                            template.getForeignTable()));
                    qb.setFilter(template.toFilter(key));

                    prop = GenericAssociationIterator.wrap(
                            store.createSession(false).getFeatureCollection(qb.buildQuery()),
                            assDesc,key);
                }else{
                    prop = Collections.emptyList();
                }
                k++;
            }else if(ptype instanceof ComplexType){
                final RelationMetaModel template = (RelationMetaModel) desc.getUserData().get(JDBCFeatureStore.JDBC_PROPERTY_RELATION);
                
                if(template != null){
                    //create a dynamic collection
                    final Object key = rs.getObject(template.getCurrentColumn());
                    
                    //create the filter, excluding relation and id fields                    
                    final QueryBuilder qb = new QueryBuilder();
                    qb.setTypeName(new DefaultName(
                            store.getDefaultNamespace(), 
                            template.getForeignTable()));
                    qb.setFilter(template.toFilter(key));
                    qb.setProperties(template.getSubTypeFields(store.getDatabaseModel()));

                    prop = new ComplexAttCollection(qb.buildQuery(), desc);
                }else{
                    prop = Collections.emptyList();
                }
            }else{
                //single value attribut
                prop = FeatureUtilities.defaultProperty(desc);
                final Object value = readSimpleValue(store.getDialect(), rs, k+1, desc);
                ((Property)prop).setValue(value);
                k++;
            }
            
            progressiveMap.put(n, prop);
        }
        
        //used by getValue and getProperties()
        //this collection falls back on the property map.
        value = new CollectionMap();
    }

    @Override
    public Collection<Property> getProperties(Name name) {
        final Object obj = progressiveMap.get(name);
        if(obj == null){
            return Collections.emptyList();
        }else if(obj instanceof Collection){
            return (Collection)obj;
        }else if(obj instanceof Property){
            return Collections.singleton((Property)obj);
        }else{
            throw new IllegalStateException("Map contain a value which is not a property or a Collection, it should not happen.");
        }
    }

    @Override
    public Collection<Property> getProperties(final String name) {
        return getProperties(new DefaultName(null, name));
    }

    @Override
    public Property getProperty(final Name name) {
        Object obj;
        if(name.getNamespaceURI() == null){
            obj = null;
            for(Entry<Name,Object> entry : progressiveMap.entrySet()){
                if(DefaultName.match(name, entry.getKey())){
                    obj = entry.getValue();
                    break;
                }
            }
        }else{
            obj = progressiveMap.get(name);
        }
        
        if(obj == null){
            return null;
        }else if(obj instanceof Collection){
            final Collection col = (Collection) obj;
            Iterator ite = null;
            try{
                ite = col.iterator();
                if(ite.hasNext()){
                    return (Property) ite.next();
                }
            }finally{
                if(ite instanceof Closeable){
                    try {
                        ((Closeable)ite).close();
                    } catch (IOException ex) {
                        store.getLogger().log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
            
            return null;
        }else if(obj instanceof Property){
            return (Property)obj;
        }else{
            throw new IllegalStateException("Map contain a value which is not a property or a Collection, it should not happen.");
        }
    }
    
    @Override
    public Property getProperty(final String name) {
        return getProperty(new DefaultName(null, name));
    }

    public void updateResultSet(final ResultSet rs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private class CollectionMap extends AbstractCollection<Property> {
    
        public CollectionMap() {
        }
        
        @Override
        public CollectionMapIterator iterator() {
            return new CollectionMapIterator();
        }

        @Override
        public int size() {
            int size = 0;
            for(Object value : progressiveMap.values()){
                if(value instanceof Collection){
                    size += ((Collection)value).size();
                }else{
                    size++;
                }
            }
            return size;
        }
        
    }
    
    private class CollectionMapIterator implements CloseableIterator<Property>{

        private final Iterator<Object> mainIte;
        private Iterator sub = null;
        private Property next = null;

        private CollectionMapIterator() {
            mainIte = progressiveMap.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            findNext();
            return next != null;
        }

        @Override
        public Property next() {
            findNext();
            if(next == null){
                throw new NoSuchElementException("No more elements.");
            }
            final Property candidate = next;
            next = null;
            return candidate;
        }
        
        private void findNext(){
            if(next != null) return;
            
            while(next == null){
                
                //search if we have remaining values in the sub
                if(sub != null){
                    if(sub.hasNext()){
                        next = (Property) sub.next();
                        continue;
                    }else{
                        //close it
                        close(sub);
                        sub = null;
                    }
                }
                
                //search the main iterator
                if(mainIte.hasNext()){
                    final Object obj = mainIte.next();
                    if(obj instanceof Collection){
                        sub = ((Collection)obj).iterator();
                    }else if(obj instanceof Property){
                        next = (Property) obj;
                    }else{
                        throw new IllegalStateException("Map contain a value which is not a property or a Collection, it should not happen.");
                    }
                }else{
                    //nothing left
                    break;
                }
            }
        }
       
        @Override
        public void close() {
            if(sub != null){
                close(sub);
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
        
        private void close(Iterator ite){
            if(ite instanceof Closeable){
                try {
                    ((Closeable)ite).close();
                } catch (IOException ex) {
                    Logger.getLogger(JDBCComplexFeature.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            //ensure the sub iterator is closed
            close();
        }
        
    }
    
    public static Object readSimpleValue(final SQLDialect dialect, final ResultSet rs, int index, PropertyDescriptor desc) throws SQLException{
        if(desc instanceof GeometryDescriptor){
            final GeometryDescriptor gatt = (GeometryDescriptor) desc;
            //read the geometry
            final Geometry geom;
            try {
                geom = dialect.decodeGeometryValue(gatt, rs, index);
            } catch (IOException e) {
                throw new SQLException(e);
            }

            if(geom != null && geom.getUserData() == null){ 
                //set crs is not set
                JTS.setCRS(geom, gatt.getCoordinateReferenceSystem());
            }
            return geom;
        }else{
            return dialect.decodeAttributeValue((AttributeDescriptor)desc, rs, index);
        }
    }
    
    /**
     * Load properties later
     */
    private class ComplexAttCollection implements Collection<Property>{

        private final Query query;
        private final PropertyDescriptor desc;
        private List<Property> loaded = null;

        public ComplexAttCollection(Query query, PropertyDescriptor desc) {
            this.query = query;
            this.desc = desc;
        }
        
        private synchronized void load(){
            if(loaded!=null) return;
            
            loaded = new ArrayList<Property>();
            final FeatureCollection col = store.createSession(false).getFeatureCollection(query);
            final FeatureIterator ite = col.iterator();
            try{
                while(ite.hasNext()){
                    Property p = ite.next();
                    Property cp = FeatureUtilities.defaultProperty(desc);
                    FeatureUtilities.copy(p, cp, false);
                    //Property p = FeatureUtilities.wrapProperty(ite.next(), desc);
                    loaded.add(cp);
                }
            }finally{
                ite.close();
            }
        }
        
        @Override
        public int size() {
            load();
            return loaded.size();
        }

        @Override
        public boolean isEmpty() {
            load();
            return loaded.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            load();
            return loaded.contains(o);
        }

        @Override
        public Iterator iterator() {
            load();
            return loaded.iterator();
        }

        @Override
        public Object[] toArray() {
            load();
            return loaded.toArray();
        }

        @Override
        public Object[] toArray(Object[] a) {
            load();
            return loaded.toArray(a);
        }

        @Override
        public boolean add(Property e) {
            load();
            return loaded.add(e);
        }

        @Override
        public boolean remove(Object o) {
            load();
            return loaded.remove(o);
        }

        @Override
        public boolean containsAll(Collection c) {
            load();
            return loaded.containsAll(c);
        }

        @Override
        public boolean addAll(Collection c) {
            load();
            return loaded.addAll(c);
        }

        @Override
        public boolean removeAll(Collection c) {
            load();
            return loaded.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection c) {
            load();
            return loaded.removeAll(c);
        }

        @Override
        public void clear() {
            load();
            loaded.clear();
        }
        
    }
    
}
