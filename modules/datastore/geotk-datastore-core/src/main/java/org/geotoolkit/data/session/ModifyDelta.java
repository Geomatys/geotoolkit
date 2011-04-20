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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.GenericModifyFeatureIterator;
import org.geotoolkit.data.memory.GenericTransformFeatureIterator;
import org.geotoolkit.data.memory.WrapFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.NullArgumentException;

import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Delta which modify a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 * @todo make this concurrent
 */
class ModifyDelta extends AbstractDelta{

    private final Name type;
    private final Id filter;
    private final Map<AttributeDescriptor,Object> values = new HashMap<AttributeDescriptor, Object>();

    ModifyDelta(final Session session, final Name typeName, final Id filter, final Map<? extends AttributeDescriptor,? extends Object> values){
        super(session);
        ensureNonNull("type name", typeName);
        if(filter == null){
            throw new NullArgumentException("Filter can not be null. Did you mean Filter.INCLUDE ?");
        }
        if(values == null || values.isEmpty()){
            throw new IllegalArgumentException("Modified values can not be null or empty. A modify delta is useless in this case.");
        }

        this.type = typeName;
        this.filter = filter;
        this.values.putAll(values);
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
                            final FeatureType original = session.getDataStore().getFeatureType(feature.getType().getName());
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
    public void commit(final DataStore store) throws DataStoreException {
        store.updateFeatures(type, filter, values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

}
