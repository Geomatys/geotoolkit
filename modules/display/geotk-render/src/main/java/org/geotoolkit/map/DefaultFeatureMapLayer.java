/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.map;

import java.util.logging.Level;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.style.MutableStyle;

import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default implementation of the MapLayer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class DefaultFeatureMapLayer extends AbstractMapLayer implements FeatureMapLayer {

    protected Query query = null;
    protected Id selectionFilter = null;

    private final FeatureCollection<? extends Feature> collection;

    private Expression height;
    private Expression[] elevationRange;
    private Expression[] temporalRange;

    /**
     * Creates a new instance of DefaultFeatureMapLayer
     * 
     * @param collection : the data source for this layer
     * @param style : the style used to represent this layer
     */
    DefaultFeatureMapLayer(final FeatureCollection<? extends Feature> collection, final MutableStyle style) {
        super(style);
        if (collection == null) {
            throw new NullPointerException("FeatureSource and Style can not be null");
        }
        this.collection = collection;
    }

    @Override
    public Id getSelectionFilter(){
        return selectionFilter;
    }

    @Override
    public void setSelectionFilter(final Id filter){

        final Filter oldfilter;
        synchronized (this) {
            oldfilter = this.selectionFilter;
            if(oldfilter == filter){
                return;
            }
            this.selectionFilter = filter;
        }
        firePropertyChange(SELECTION_FILTER_PROPERTY, oldfilter, this.selectionFilter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query getQuery() {
        if(query == null){
            query = QueryBuilder.all(getCollection().getFeatureType().getName());
        }

        return query;
    }

    /**
     * Sets a definition query for this layer.
     *
     * <p>
     * If present (other than <code>Query.ALL</code>, a renderer or consumer
     * must use it to limit the number of returned features based on the filter
     * it holds and the value of the maxFeatures attributes, and also can use it
     * as a performance hto limit the number of requested attributes
     * </p>
     *
     * @param query the full filter for this layer. can not be null.
     */
    @Override
    public void setQuery(final Query query) {
        if (query == null) {
            throw new NullPointerException( "must provide a Query. Do you mean Query.ALL?");
        }

        final Query oldQuery;
        synchronized (this) {
            oldQuery = getQuery();
            if(oldQuery.equals(query)){
                return;
            }
            this.query = query;
        }
        firePropertyChange(QUERY_PROPERTY, oldQuery, this.query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<? extends Feature> getCollection() {
        return this.collection;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {

        final CoordinateReferenceSystem sourceCrs = collection.getFeatureType().getCoordinateReferenceSystem();
        Envelope env = null;
        try {
            env = collection.getEnvelope();
        } catch (DataStoreException e) {
            LOGGER.log(Level.WARNING, "Could not create referecenced envelope.",e);
        }

        if(env == null){
            Envelope crsEnv = CRS.getEnvelope(sourceCrs);
            if(crsEnv != null){
                //we couldn't estime the features envelope, return the crs envelope if possible
                //we assume the features are not out of the crs valide envelope
                env = new GeneralEnvelope(crsEnv);
            }else{
                //never return a null envelope, we better return an infinite envelope
                env = new Envelope2D(sourceCrs,XRectangle2D.INFINITY);
            }
        }

        return env;
    }

    @Override
    public Expression getHeight() {
        return height;
    }

    @Override
    public void setHeight(final Expression height) {
        this.elevationRange = null;
        this.height = height;
    }

    @Override
    public Expression[] getElevationRange() {
        if(elevationRange == null){
            return new Expression[2];
        }else{
            return elevationRange.clone();
        }
    }

    @Override
    public void setElevationRange(final Expression from, final Expression to) {
        height = null;
        final Expression[] old = elevationRange;
        elevationRange = new Expression[]{from,to};
        firePropertyChange(MapLayer.ELEVATION_PROPERTY, old, elevationRange);
    }

    @Override
    public Expression[] getTemporalRange() {
        if(temporalRange == null){
            return new Expression[2];
        }else{
            return temporalRange.clone();
        }
    }

    @Override
    public void setTemporalRange(final Expression from, final Expression to) {
        temporalRange = new Expression[]{from,to};
    }
    

}
