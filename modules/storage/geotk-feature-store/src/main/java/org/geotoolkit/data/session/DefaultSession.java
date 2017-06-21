/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.visitor.DuplicatingFilterVisitor;
import org.geotoolkit.filter.visitor.SimplifyingFilterVisitor;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.jts.JTS;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.version.Version;
import org.opengis.util.GenericName;
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
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureStreams;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultSession extends AbstractSession {

    protected static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    private final DefaultSessionDiff diff;
    private final boolean async;
    private final Version version;

    public DefaultSession(final FeatureStore store, final boolean async){
        this(store,async,null);
    }

    public DefaultSession(final FeatureStore store, final boolean async, final Version version){
        super(store);
        this.diff = createDiff();
        this.async = async;
        this.version = version;
    }

    protected DefaultSessionDiff createDiff(){
        return new DefaultSessionDiff();
    }

    protected AddDelta createAddDelta(Session session, String typeName, Collection<? extends Feature> features){
        return new AddDelta(this, typeName, features);
    }

    protected ModifyDelta createModifyDelta(Session session, String typeName,
            Id filter , final Map<String,?> values){
        return new ModifyDelta(this, typeName, filter, values);
    }

    protected RemoveDelta createRemoveDelta(Session session, String typeName, Id filter){
        return new RemoveDelta(session, typeName, filter);
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
    public Version getVersion() {
        return version;
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
        final List<Query> modifieds = new ArrayList<>(deltas.size());
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
        reader = FeatureStreams.filter(reader, originalFilter);

        return reader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addFeatures(final String groupName, final Collection newFeatures) throws DataStoreException {
        checkVersion();
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(async){
            diff.add(createAddDelta(this, groupName, newFeatures));
            fireSessionChanged();
        }else{
            store.addFeatures(groupName.toString(), newFeatures);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final String groupName, Filter filter, final Map<String,?> values) throws DataStoreException {
        checkVersion();
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(values == null || values.isEmpty()){
            //no modifications, no need to create a modify delta
            //todo should we raise an error ?
            return;
        }

        if(async){
            final Id modified;

            final SimplifyingFilterVisitor simplifier = new SimplifyingFilterVisitor();
            filter = (Filter) filter.accept(simplifier, null);

            if(filter instanceof Id){
                modified = FF.id( ((Id)filter).getIdentifiers());
            }else{
                final Set<Identifier> identifiers = new HashSet<Identifier>();
                QueryBuilder qb = new QueryBuilder(groupName);
                qb.setFilter(filter);
                final FeatureIterator ite = getFeatureIterator(qb.buildQuery());
                try{
                    while(ite.hasNext()){
                        identifiers.add(FeatureExt.getId(ite.next()));
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

            diff.add(createModifyDelta(this, groupName, modified, values));
            fireSessionChanged();
        }else{
            store.updateFeatures(groupName, filter, values);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        checkVersion();
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
                        identifiers.add(FeatureExt.getId(ite.next()));
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

            diff.add(createRemoveDelta(this, groupName, removed));
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
    public long getCount(final Query original) throws DataStoreException {
        if(hasPendingChanges()){
            return FeatureStoreUtilities.calculateCount(getFeatureIterator(original));
        }else{
            return store.getCount(original);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope(Query original) throws DataStoreException {
        if(hasPendingChanges()){
            return FeatureStoreUtilities.calculateEnvelope(getFeatureIterator(original));
        }else{
            return store.getEnvelope(original);
        }
    }

    protected DefaultSessionDiff getDiff() {
        return diff;
    }

    /**
     * Test if a version is set, raise an error if it's the case.
     * Version must not be set when doing writing operations
     */
    protected void checkVersion() throws DataStoreException {
        if(version!=null){
            throw new DataStoreException("Session is opened on version : "+version+". "
                    + "Writing operations are not allowed, open a session without version to support writing.");
        }
    }

    private Query forceCRS(final Query query, boolean replace) throws DataStoreException{
        final FeatureType ft = store.getFeatureType(query.getTypeName());
        final CoordinateReferenceSystem crs = FeatureExt.getCRS(ft);

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
                            final Envelope env = Envelopes.transform(bb, crs);
                            bb = new DefaultBoundingBox(env);
                            return FF.literal(bb);
                        } catch (TransformException ex) {
                            Logging.getLogger("org.geotoolkit.data.session").log(Level.SEVERE, null, ex);
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
                            final MathTransform trs = CRS.findOperation(cdtcrs, crs, null).getMathTransform();
                            geom = JTS.transform(geom, trs);
                            JTS.setCRS(geom, crs);
                            return FF.literal(geom);
                        }
                    } catch (Exception ex) {
                        Logging.getLogger("org.geotoolkit.data.session").log(Level.WARNING, ex.getLocalizedMessage(), ex);
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
