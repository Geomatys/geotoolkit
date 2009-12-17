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

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;

import org.opengis.feature.Feature;
import org.opengis.filter.Id;

/**
 * MapLayer holding Features.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FeatureMapLayer extends MapLayer{

    /**
     * Get the feature source for this layer.
     *
     * @return The features for this layer, can not be null.
     */
    FeatureCollection<? extends Feature> getCollection();
    
    
    /**
     * Returns the definition query (filter) for this layer. If no definition
     * query has  been defined {@link Query#ALL} is returned.
     */
    Query getQuery();

    /**
     * Sets a definition query for the layer wich acts as a filter for the
     * features that the layer will draw.
     * 
     * <p>
     * A consumer must ensure that this query is used in  combination with the
     * bounding box filter generated on each map interaction to limit the
     * number of features returned to those that complains both the definition
     * query  and relies inside the area of interest.
     * </p>
     * <p>
     * IMPORTANT: only include attribute names in the query if you want them to
     * be ALWAYS returned. It is desirable to not include attributes at all
     * but let the layer user (a renderer?) to decide wich attributes are actually
     * needed to perform its requiered operation.
     * </p>
     *
     * @param query
     */
    void setQuery(Query query);

    /**
     * A separate filter for datas that are selected on this layer.
     * @return Filter, can be null or empty.
     */
    Id getSelectionFilter();

    /**
     * Set the selection fiter.
     * @param filter Id
     */
    void setSelectionFilter(Id filter);

}
