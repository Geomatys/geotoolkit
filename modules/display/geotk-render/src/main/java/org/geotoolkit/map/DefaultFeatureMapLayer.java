/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import java.util.logging.Level;

import org.geotools.data.FeatureSource;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.style.MutableStyle;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default implementation of the MapLayer.
 * 
 * @author Johann Sorel (Geomatys)
 */
final class DefaultFeatureMapLayer extends AbstractMapLayer implements FeatureMapLayer {

    private final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;

    /**
     * Creates a new instance of DefaultFeatureMapLayer
     * 
     * @param featureSource : the data source for this layer
     * @param style : the style used to represent this layer
     */
    DefaultFeatureMapLayer(FeatureSource<SimpleFeatureType, SimpleFeature> featureSource, MutableStyle style) {
        super(style);
        if (featureSource == null) {
            throw new NullPointerException("FeatureSource and Style can not be null");
        }
        this.featureSource = featureSource;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureSource<SimpleFeatureType, SimpleFeature> getFeatureSource() {
        return this.featureSource;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {

        final CoordinateReferenceSystem sourceCrs = featureSource.getSchema().getCoordinateReferenceSystem();
        Envelope env = null;
        try {
            env = featureSource.getBounds();
        } catch (MismatchedDimensionException e) {
            LOGGER.log(Level.SEVERE, "Could not create referecenced envelope.",e);
        } catch (IOException e) {
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
    

}
