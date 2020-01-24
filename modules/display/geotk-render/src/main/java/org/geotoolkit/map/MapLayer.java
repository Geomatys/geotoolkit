/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import java.beans.PropertyChangeEvent;
import java.util.List;
import org.apache.sis.storage.Resource;
import org.geotoolkit.style.MutableStyle;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

/**
 * A layer to be rendered. A layer is an aggregation of both a
 * data source : {@link FeatureCollection} or {@ CoverageReference}
 * with a given {@link MutableStyle}.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface MapLayer extends MapItem {

    public static final String STYLE_PROPERTY = "style";
    public static final String OPACITY_PROPERTY = "opacity";
    public static final String SELECTABLE_PROPERTY = "selectable";
    public static final String QUERY_PROPERTY = "query";
    public static final String BOUNDS_PROPERTY = "bounds";
    public static final String SELECTION_FILTER_PROPERTY = "selection_filter";
    public static final String SELECTION_STYLE_PROPERTY = "selection_style";

    /**
     * Use this key in the User map properties and add a Boolean.TRUE
     * to indicate if features store their own Symbolizer.
     * Symbolizer should be stored in the user map of each feature with this key.
     *
     * TODO make a special feature and feature collection implementation to define this case.
     */
    public static final String USERKEY_STYLED_FEATURE = "styled_feature";

    /**
     * Get layer resource.
     *
     * @return Resource, can be null.
     */
    Resource getResource();

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
     *
     * @return double layer opacity between 0 and 1
     */
    double getOpacity();

    /**
     * Specify the global opacity of this layer.
     * 0 is fully translucent and 1 is opaque.
     * A {@link PropertyChangeEvent} is fired if the opacity changed.
     *
     * @param opacity : value between 0 and 1
     */
    void setOpacity(double opacity);

    /**
     * Determine whether this layer is selectable on a map pane or whether the
     * layer is static.
     *
     * @return <code>true</code> if the layer is selectable.
     */
    boolean isSelectable();

    /**
     * Specify whether this layer is selectable on a map pane.
     * A {@link PropertyChangeEvent} is fired if the selectable changed.
     *
     * @param selectable Show the layer if <code>true</code>.
     */
    void setSelectable(boolean selectable);

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
    Envelope getBounds();

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

    /**
     * Register a layer listener, this listener will be registered
     * also as an item listener.
     * @param listener Layer listener to register
     */
    void addLayerListener(LayerListener listener);

    /**
     * Unregister a layer listener, this listener will be unregistered
     * also as an item listener.
     * @param listener Layer listener to unregister
     */
    void removeLayerListener(LayerListener listener);

}
