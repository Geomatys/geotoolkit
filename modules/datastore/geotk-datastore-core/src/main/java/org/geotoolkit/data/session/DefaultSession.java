/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data.session;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.visitor.DuplicatingFilterVisitor;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSession extends AbstractSession {

    protected static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    
    private final DefaultSessionDiff diff;
    private final boolean async;

    public DefaultSession(final DataStore store, final boolean async){
        super(store);
        
        this.diff = new DefaultSessionDiff();
        this.async = async;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isAsynchrone() {
        return async;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection getFeatureCollection(final Query query) {
        return QueryUtilities.evaluate("id", query,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator getFeatureIterator(Query original) throws DataStoreException {

        //quick check to bypass deltas
        if(!hasPendingChanges()){
            return store.getFeatureReader(original);
        }

        original = forceCRS(original,false);
        final List<Delta> deltas = diff.getDeltas();

        //we must store the modified queries to iterate on them in reverse order.
        final List<Query> modifieds = new ArrayList<Query>(deltas.size());
        Query modified = original;
        for(int i=0,n=deltas.size(); i<n; i++){
            final Delta delta = deltas.get(i);
            modifieds.add(modified); //store before modification
            modified = delta.modify(modified);
        }

        FeatureIterator reader = store.getFeatureReader(modified);

        for(int i=0,n=deltas.size(); i<n; i++){
            final Delta delta = deltas.get(i);
            reader = delta.modify(modifieds.get(i),reader);
        }

        //we must preserve the original filter after all thoses modifications
        Filter originalFilter = original.getFilter();
        originalFilter = forceCRS(originalFilter, original.getCoordinateSystemReproject(), true);
        reader = GenericFilterFeatureIterator.wrap(reader, originalFilter);

        return reader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addFeatures(final Name groupName, final Collection newFeatures) throws DataStoreException {
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(async){
            diff.add(new AddDelta(this, groupName, newFeatures));
            fireSessionChanged();
        }else{
            store.addFeatures(groupName, newFeatures);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends AttributeDescriptor,? extends Object> values) throws DataStoreException {
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(values == null || values.isEmpty()){
            //no modifications, no need to create a modify delta
            //todo should we raise an error ?
            return;
        }

        if(async){
            final Id modified;

            if(filter instanceof Id){
                modified = FF.id( ((Id)filter).getIdentifiers());
            }else{
                final Set<Identifier> identifiers = new HashSet<Identifier>();
                QueryBuilder qb = new QueryBuilder(groupName);
                qb.setFilter(filter);
                final FeatureIterator ite = getFeatureIterator(qb.buildQuery());
                try{
                    while(ite.hasNext()){
                        identifiers.add(ite.next().getIdentifier());
                    }
                }finally{
                    ite.close();
                }

                if(identifiers.isEmpty()){
                    //no feature match this filter, no need to create a modify delta
                    return;
                }else{
                    modified = FF.id(identifiers);
                }
            }

            diff.add(new ModifyDelta(this, groupName, modified, values));
            fireSessionChanged();
        }else{
            store.updateFeatures(groupName, filter, values);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(async){
            final Id removed;

            if(filter instanceof Id){
                removed = (Id)filter;
            }else{
                final Set<Identifier> identifiers = new HashSet<Identifier>();
                QueryBuilder qb = new QueryBuilder(groupName);
                qb.setFilter(filter);
                final FeatureIterator ite = getFeatureIterator(qb.buildQuery());
                try{
                    while(ite.hasNext()){
                        identifiers.add(ite.next().getIdentifier());
                    }
                }finally{
                    ite.close();
                }

                if(identifiers.isEmpty()){
                    //no feature match this filter, no need to create to remove delta
                    return;
                }else{
                    removed = FF.id(identifiers);
                }
            }

            diff.add(new RemoveDelta(this, groupName, removed));
            fireSessionChanged();
        }else{
            store.removeFeatures(groupName, filter);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasPendingChanges() {
        return !diff.getDeltas().isEmpty();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void commit() throws DataStoreException {
        diff.commit(store);
        fireSessionChanged();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void rollback() {
        diff.rollback();
        fireSessionChanged();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getCount(Query original) throws DataStoreException {
        original = forceCRS(original,false);
        final List<Delta> deltas = diff.getDeltas();

        //we must store the modified queries to iterate on them in reverse order.
        final List<Query> modifieds = new ArrayList<Query>(deltas.size());
        Query modified = original;
        for(int i=0,n=deltas.size(); i<n; i++){
            final Delta delta = deltas.get(i);
            modifieds.add(modified); //store before modification
            modified = delta.modify(modified);
        }

        long count = store.getCount(modified);

        for(int i=deltas.size()-1; i>=0; i--){
            final Delta delta = deltas.get(i);
            count = delta.modify(modifieds.get(i),count);
        }
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope(Query original) throws DataStoreException {
        original = forceCRS(original,false);
        final List<Delta> deltas = diff.getDeltas();

        //we must store the modified queries to iterate on them in reverse order.
        final List<Query> modifieds = new ArrayList<Query>(deltas.size());
        Query modified = original;
        for(int i=0,n=deltas.size(); i<n; i++){
            final Delta delta = deltas.get(i);
            modifieds.add(modified); //store before modification
            modified = delta.modify(modified);
        }

        Envelope env = store.getEnvelope(modified);

        for(int i=deltas.size()-1; i>=0; i--){
            final Delta delta = deltas.get(i);
            env = delta.modify(modifieds.get(i),env);
        }
        return env;
    }

    private Query forceCRS(final Query query, boolean replace) throws DataStoreException{
        final FeatureType ft = store.getFeatureType(query.getTypeName());
        final CoordinateReferenceSystem crs = ft.getCoordinateReferenceSystem();

        if(crs == null){
            return query;
        }

        final QueryBuilder qb = new QueryBuilder(query);
        qb.setFilter(forceCRS(qb.getFilter(), crs,replace));
        return qb.buildQuery();
    }

    private static Filter forceCRS(final Filter filter, final CoordinateReferenceSystem crs, final boolean replace){

        if(crs == null) return filter;

        final FilterVisitor visitor = new DuplicatingFilterVisitor(){

            @Override
            public Object visit(final Literal expression, final Object extraData) {

                final Object obj = expression.getValue();

                if(obj instanceof BoundingBox){
                    BoundingBox bb = (BoundingBox) obj;
                    if(bb.getCoordinateReferenceSystem() == null){
                        //force crs definition
                        bb = new DefaultBoundingBox(bb, crs);
                        return FF.literal(bb);
                    }else if(replace){
                        try {
                            //reproject bbox
                            final Envelope env = CRS.transform(bb, crs);
                            bb = new DefaultBoundingBox(env);
                            return FF.literal(bb);
                        } catch (TransformException ex) {
                            Logger.getLogger(DefaultSession.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return expression;
                }else if(obj instanceof Geometry){
                    Geometry geom = (Geometry) obj;
                    try {
                        CoordinateReferenceSystem cdtcrs = JTS.findCoordinateReferenceSystem(geom);
                        if(cdtcrs == null){
                            geom = (Geometry) geom.clone();
                            JTS.setCRS(geom, crs);
                            return FF.literal(geom);
                        }else if(replace){
                            //reproject geometry
                            final MathTransform trs = CRS.findMathTransform(cdtcrs, crs);
                            geom = JTS.transform(geom, trs);
                            JTS.setCRS(geom, crs);
                            return FF.literal(geom);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(DefaultSession.class.getName()).log(Level.WARNING, ex.getLocalizedMessage(), ex);
                        geom = (Geometry) geom.clone();
                        JTS.setCRS(geom, crs);
                        return FF.literal(geom);
                    }

                    return expression;
                }else{
                    return super.visit(expression, extraData);
                }

            }

        };

        return (Filter) filter.accept(visitor, null);
    }


}
