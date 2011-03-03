/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import java.util.Collection;

import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.MutableStyle;

import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.geometry.Envelope;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Default implementation of a collection MapLayer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class DefaultCollectionMapLayer extends AbstractMapLayer implements CollectionMapLayer {

    protected Id selectionFilter = null;

    private final Collection<?> collection;


    /**
     * Creates a new instance of DefaultCollectionMapLayer
     * 
     * @param collection : the data source for this layer
     * @param style : the style used to represent this layer
     */
    DefaultCollectionMapLayer(final Collection<?> collection, final MutableStyle style) {
        super(style);
        ensureNonNull("collection", collection);
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
    public Collection<?> getCollection() {
        return this.collection;
    }
    
    /**
     * Can not calculate bounds on a collection layer.
     * @return a full world envelope.
     */
    @Override
    public Envelope getBounds() {
        return new Envelope2D(DefaultGeographicCRS.WGS84,-180,-90,360,180);
    }
   
}
