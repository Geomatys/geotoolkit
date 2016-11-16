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

import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.sis.feature.FeatureExt;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.GenericModifyFeatureIterator;
import org.geotoolkit.data.memory.WrapFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.NullArgumentException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.apache.sis.internal.feature.AttributeConvention;

import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


/**
 * Delta which modify a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 * @todo make this concurrent
 */
public class ModifyDelta extends AbstractDelta{

    protected final Map<String,Object> values = new HashMap<>();
    protected Id filter;

    public ModifyDelta(final Session session, final String typeName, final Id filter, final Map<String,?> values){
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
                        final FeatureType original = session.getFeatureStore().getFeatureType(feature.getType().getName().toString());
                        for(String desc : values.keySet()){
                            if (AttributeConvention.isGeometryAttribute(feature.getType().getProperty(desc))) {
                                final CoordinateReferenceSystem originalCRS = FeatureExt.getCRS(original.getProperty(desc));
                                if(!Utilities.equalsIgnoreMetadata(originalCRS,crs)){
                                    MathTransform trs = CRS.findOperation(originalCRS, crs, null).getMathTransform();
                                    Object geom = feature.getPropertyValue(desc);
                                    if (geom instanceof Geometry) {
                                        try {
                                            geom = JTS.transform((Geometry) geom, trs);
                                        } catch (MismatchedDimensionException | TransformException ex) {
                                            throw new FeatureStoreRuntimeException(ex);
                                        }
                                        JTS.setCRS((Geometry) geom, crs);
                                        feature.setPropertyValue(desc, geom);
                                    }
                                }
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
