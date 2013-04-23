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

import java.io.Closeable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.memory.GenericAssociationIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.db.reverse.ForeignKey;
import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Property;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
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
    
    public JDBCComplexFeature(final DefaultJDBCFeatureStore store, final ResultSet rs,
            final FeatureType type, final FeatureId id) throws SQLException{
        super(type, id);
        this.store = store;
        
        for(final PropertyDescriptor desc : type.getDescriptors()){
            final Name n = desc.getName();
            
            Object prop = null;
            if(desc.getType() instanceof AssociationType){
                final AssociationDescriptor assDesc = (AssociationDescriptor) desc;
                ForeignKey template = (ForeignKey) desc.getUserData().get(ForeignKey.RELATION);
                
                if(template != null){
                    //create a dynamic collection
                    final Object key = rs.getObject(template.getRelationModel().getCurrentColumn());
                    
                    final QueryBuilder qb = new QueryBuilder();
                    qb.setTypeName(new DefaultName(
                            store.getDefaultNamespace(), 
                            template.getRelationModel().getForeignTable()));
                    qb.setFilter(template.toFilter(key));

                    prop = GenericAssociationIterator.wrap(
                            store.createSession(false).getFeatureCollection(qb.buildQuery()),
                            assDesc,key);
                }else{
                    prop = Collections.emptyList();
                }
            }else{
                //single value attribut
                prop = FeatureUtilities.defaultProperty(desc);
                ((Property)prop).setValue(rs.getObject(n.getLocalPart()));
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
    
}
