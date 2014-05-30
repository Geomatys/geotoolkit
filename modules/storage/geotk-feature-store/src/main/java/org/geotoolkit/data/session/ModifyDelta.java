/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.GenericModifyFeatureIterator;
import org.geotoolkit.data.memory.GenericTransformFeatureIterator;
import org.geotoolkit.data.memory.WrapFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.Name;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Delta which modify a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 * @todo make this concurrent
 */
public class ModifyDelta extends AbstractDelta{

    protected final Map<AttributeDescriptor,Object> values = new HashMap<AttributeDescriptor, Object>();
    protected Id filter;

    public ModifyDelta(final Session session, final Name typeName, final Id filter, final Map<? extends AttributeDescriptor,? extends Object> values){
        super(session,typeName);
        ensureNonNull("type name", typeName);
        if(filter == null){
            throw new NullArgumentException("Filter can not be null. Did you mean Filter.INCLUDE ?");
        }
        if(values == null || values.isEmpty()){
            throw new IllegalArgumentException("Modified values can not be null or empty. A modify delta is useless in this case.");
        }

        this.filter = filter;
        this.values.putAll(values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(Map<String, String> idUpdates) {
        if(idUpdates == null || idUpdates.isEmpty())return;

        final Set<Identifier> ids = filter.getIdentifiers();
        final Set<Identifier> newIds = new HashSet<Identifier>();

        for(final Identifier id : ids){
            String newId = idUpdates.get(id.getID().toString());
            if(newId != null){
                //id has change
                newIds.add(FF.featureId(newId));
            }else{
                //this id did not change
                newIds.add(id);
            }
        }

        filter = FF.id(newIds);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query modify(final Query query) {
        if(!query.getTypeName().equals(type)) return query;

        //we always include the modified features
        //they will be filtered at return time in the other modified methods
        //todo we should modify this query for count and envelope
        final QueryBuilder builder = new QueryBuilder(query);
        builder.setFilter(FF.or(builder.getFilter(),filter));

        return builder.buildQuery();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator modify(final Query query, final FeatureIterator reader) throws DataStoreException {

        final FeatureIterator wrap = new WrapFeatureIterator(reader) {

            @Override
            protected Feature modify(Feature feature) {

                if(!filter.evaluate(feature)){
                    return feature;
                }

                //modify the feature
                feature = GenericModifyFeatureIterator.apply(feature, values);
                try {
                    final CoordinateReferenceSystem crs = query.getCoordinateSystemReproject();

                    //wrap reprojection ----------------------------------------------------
                    if(crs != null){
                        //check we have a geometry modification
                        boolean hasgeoModified = false;
                        for(AttributeDescriptor desc : values.keySet()){
                            if(desc instanceof GeometryDescriptor){
                                hasgeoModified = true;
                                break;
                            }
                        }
                        
                        if(hasgeoModified){
                            final FeatureType original = session.getFeatureStore().getFeatureType(feature.getType().getName());
                            final CoordinateReferenceSystem originalCRS = original.getCoordinateReferenceSystem();
                            if(!CRS.equalsIgnoreMetadata(originalCRS,crs)){
                                final CoordinateSequenceMathTransformer trs =
                                        new CoordinateSequenceMathTransformer(CRS.findMathTransform(originalCRS, crs, true));
                                GeometryCSTransformer transformer = new GeometryCSTransformer(trs);
                                transformer.setCoordinateReferenceSystem(crs);
                                feature = GenericTransformFeatureIterator.apply(feature, transformer);
                            }
                        }
                    }

                } catch (DataStoreException ex) {
                    getLogger().log(Level.WARNING, null, ex);
                    feature = null;
                } catch (FactoryException ex) {
                    getLogger().log(Level.WARNING, null, ex);
                    feature = null;
                }

                return feature;
            }

        };

        return wrap;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long modify(final Query query, final long count) throws DataStoreException{
        //todo must find a correct wayto alterate the count
        //the send request should be modified
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope modify(final Query query, final Envelope env) throws DataStoreException {
        //todo must find a correct wayto alterate the envelope
        //the send request should be modified
        return env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String,String> commit(final FeatureStore store) throws DataStoreException {
        store.updateFeatures(type, filter, values);
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

}
