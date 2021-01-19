/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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

import java.util.Objects;
import java.util.logging.Level;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.feature.FeatureExt;
import static org.geotoolkit.map.MapLayer.SELECTION_FILTER_PROPERTY;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.style.MutableStyle;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * MapLayer holding a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 * @deprecated use MapLayer interface instead
 */
@Deprecated
public final class FeatureMapLayer extends MapLayer {

    protected Id selectionFilter = null;

    /**
     * Creates a new instance of DefaultFeatureMapLayer
     *
     * @param collection : the data source for this layer
     * @param style : the style used to represent this layer
     */
    FeatureMapLayer(final FeatureSet collection, final MutableStyle style) {
        super(collection);
        ArgumentChecks.ensureNonNull("FeatureSet", collection);
        setStyle(style);
        trySetName(collection);
    }

    /**
     * A separate filter for datas that are selected on this layer.
     * @return Filter, can be null or empty.
     */
    public Id getSelectionFilter(){
        return selectionFilter;
    }

    /**
     * Set the selection filter.
     * @param filter Id
     */
    public void setSelectionFilter(final Id filter){

        final Filter oldfilter;
        synchronized (this) {
            oldfilter = this.selectionFilter;
            if(Objects.equals(oldfilter, filter)){
                return;
            }
            this.selectionFilter = filter;
        }
        firePropertyChange(SELECTION_FILTER_PROPERTY, oldfilter, this.selectionFilter);
    }

    /**
     * The feature collection of this layer.
     *
     * @return The features for this layer, can not be null.
     */
    @Override
    public FeatureSet getResource() {
        return (FeatureSet) resource;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        final FeatureSet featureSet = getResource();
        CoordinateReferenceSystem sourceCrs = null;
        Envelope env = null;
        try {
            sourceCrs = FeatureExt.getCRS(featureSet.getType());
            env = FeatureStoreUtilities.getEnvelope(featureSet);
        } catch (DataStoreException e) {
            LOGGER.log(Level.WARNING, "Could not create referecenced envelope.",e);
        }

        if(env == null){
            //no data
            //never return a null envelope, we better return an infinite envelope
            env = new Envelope2D(sourceCrs,Double.NaN,Double.NaN,Double.NaN,Double.NaN);

//            Envelope crsEnv = CRS.getEnvelope(sourceCrs);
//            if(crsEnv != null){
//                //we couldn't estime the features envelope, return the crs envelope if possible
//                //we assume the features are not out of the crs valide envelope
//                env = new GeneralEnvelope(crsEnv);
//            }else{
//                //never return a null envelope, we better return an infinite envelope
//                env = new Envelope2D(sourceCrs,Double.NaN,Double.NaN,Double.NaN,Double.NaN);
//            }
        }

        return env;
    }

}
