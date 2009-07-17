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


import org.geotoolkit.data.DefaultQuery;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.style.MutableStyle;

import org.opengis.filter.Filter;
import org.opengis.filter.Id;

/**
 * Abstract implementation of the MapLayer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractFeatureMapLayer extends AbstractMapLayer implements FeatureMapLayer {

    protected Query query = Query.ALL;
    protected Id selectionFilter = null;

    protected AbstractFeatureMapLayer(MutableStyle style){
        super(style);
    }

    @Override
    public Id getSelectionFilter(){
        return selectionFilter;
    }

    @Override
    public void setSelectionFilter(Id filter){

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
     * Returns the definition query established for this layer.
     *
     * @return the definition query established for this layer. If not set, just
     *         returns {@link Query.ALL}, if set, returns a copy of the actual
     *         query object to avoid external modification
     *
     * @see org.geotools.map.MapLayer#getQuery()
     */
    @Override
    public Query getQuery() {
        return (query == Query.ALL) ? query : new DefaultQuery(query);
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
            oldQuery = this.query;
            if(oldQuery.equals(query)){
                return;
            }
            this.query = new DefaultQuery(query);
        }
        firePropertyChange(QUERY_PROPERTY, oldQuery, this.query);
    }

}
