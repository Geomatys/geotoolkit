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

import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.style.MutableStyle;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default implementation of the MapLayer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class DefaultFeatureMapLayer extends AbstractFeatureMapLayer {

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
    DefaultFeatureMapLayer(FeatureCollection<? extends Feature> collection, MutableStyle style) {
        super(style);
        if (collection == null) {
            throw new NullPointerException("FeatureSource and Style can not be null");
        }
        this.collection = collection;
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
            LOGGER.log(Level.SEVERE, "Could not create referecenced envelope.",e);
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
    public void setHeight(Expression height) {
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
    public void setElevationRange(Expression from, Expression to) {
        height = null;
        elevationRange = new Expression[]{from,to};
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
    public void setTemporalRange(Expression from, Expression to) {
        temporalRange = new Expression[]{from,to};
    }
    

}
