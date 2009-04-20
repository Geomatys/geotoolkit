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

import java.util.List;

import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotoolkit.style.MutableStyle;

import org.opengis.display.primitive.Graphic;
import org.opengis.filter.Filter;
import org.opengis.style.Description;

/**
 * A layer to be rendered. A layer is an aggregation of both a
 * {@link FeatureSource}, a {@link MutableStyle} and, optionally, a {@link Query}
 *
 * @author Johann Sorel (Geomatys)
 */
public interface MapLayer {
    
    public static final String NAME_PROPERTY = "name";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String STYLE_PROPERTY = "style";
    public static final String VISIBILITY_PROPERTY = "visibility";
    public static final String SELECTABLE_PROPERTY = "selectable";
    public static final String QUERY_PROPERTY = "query";
    public static final String BOUNDS_PROPERTY = "bounds";
    public static final String ELEVATION_PROPERTY = "elevation";
    public static final String SELECTION_FILTER_PROPERTY = "selection_filter";
    public static final String SELECTION_STYLE_PROPERTY = "selection_style";
    
    /**
     * Set the layer name, this should be used as an
     * identifier. Use getdescription for UI needs.
     */
    void setName(String name);

    /**
     * Get the layer name. Use getdescription for UI needs.
     */
    String getName();
    
    /**
     * Set the layer description. this holds a title and an abstract summary
     * used for user interfaces.
     */
    void setDescription(Description desc);

    /**
     * Returns the description of the layer.this holds a title and an abstract summary
     * used for user interfaces.
     */
    Description getDescription();

    /**
     * Get the style for this layer.  If style has not been set, then null is
     * returned.
     *
     * @return The style.
     */
    MutableStyle getStyle();

    /**
     * Sets the style for this layer. If a style has not been defined a default
     * one shall be used.
     *
     * @param style The new style
     */
    void setStyle(MutableStyle style);

    /**
     * Determine whether this layer is visible on a map pane or whether the
     * layer is hidden.
     *
     * @return <code>true</code> if the layer is visible, or <code>false</code>
     *         if the layer is hidden.
     */
    boolean isVisible();

    /**
     * Specify whether this layer is visible on a map pane or whether the layer
     * is hidden. A {@link LayerEvent} is fired if the visibility changed.
     *
     * @param visible Show the layer if <code>true</code>, or hide the layer if
     *        <code>false</code>
     */
    void setVisible(boolean visible);

    /**
     * Determine whether this layer is selectable on a map pane or whether the
     * layer is static.
     *
     * @return <code>true</code> if the layer is selectable.
     */
    boolean isSelectable();

    /**
     * Specify whether this layer is selectable on a map pane.
     * A {@link LayerEvent} is fired if the selectable changed.
     *
     * @param selectable Show the layer if <code>true</code>.
     */
    void setSelectable(boolean selectable);

    /**
     * Returns the definition query (filter) for this layer. If no definition
     * query has  been defined {@link Query.ALL} is returned.
     *
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
     * @return Filter, can not be null but can be Filter.None
     */
    Filter getSelectionFilter();

    /**
     * Set the selection fiter.
     *
     * @param filter, can not be null
     */
    void setSelectionFilter(Filter filter);

    /**
     *
     * @return Style associated for the selected datas
     */
    MutableStyle getSelectionStyle();

    /**
     * Set the style associated to selected datas.
     *
     * @param style : can be null, the default selection style should used by
     * rendering engine if this vaue is null.
     */
    void setSelectionStyle(MutableStyle style);

    /**
     * find out the bounds of the layer
     * This method should never return null,
     * if the features envelope could not calculated then the crs valide envelope
     * if possible and in last case an infinitee envelope.
     * @return - the layer's bounds
     */
    ReferencedEnvelope getBounds();

    /**
     * Returns the Elevation model to use for this layer.
     * If no elevation model is set, then we should use the data elevation values
     * if they exists.
     *
     * @return ElevationModel or null if none
     */
    ElevationModel getElevationModel();

    /**
     * set the elevation model of this layer.
     * @param model , can be null
     */
    void setElevationModel(ElevationModel model);

    /**
     * Returns the living list of all graphic builders linked to this
     * map layer.
     *
     * @return living list of graphic builders
     */
    List<GraphicBuilder> graphicBuilders();

    /**
     * A layer may provide a graphic builder, this enable
     * special representations, like wind arrows for coverages.
     * A layer may have different builder for each kind of Graphic implementation.
     * This enable the possibility to have custom made graphic representation
     * and several builder, for 2D, 3D or else...
     *
     * @param type : the graphic type wanted
     * @return graphicBuilder<? extends type> or null
     */
    <T extends Graphic> GraphicBuilder<? extends T> getGraphicBuilder(Class<T> type);
    
    void addLayerListener(LayerListener listener);

    void removeLayerListener(LayerListener listener);

    /**
     * Store a value for this maplayer in a hashmap using the given key.
     */
    void setUserPropertie(String key,Object value);
    
    /**
     * Get a stored value knowing the key.
     */
    Object getUserPropertie(String key);
    
    
}
