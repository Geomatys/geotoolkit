/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.collection.CheckedArrayList;

import org.opengis.display.primitive.Graphic;

/**
 * Abstract implementation of the MapLayer.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractMapLayer extends AbstractMapItem implements MapLayer,StyleListener {
    
    private final List<GraphicBuilder> builders = new CheckedArrayList<GraphicBuilder>(GraphicBuilder.class);
    
    private final StyleListener.Weak styleListener = new StyleListener.Weak(null,this);
    
    protected MutableStyle style;

    protected MutableStyle selectionStyle;

    protected ElevationModel elevation = null;

    protected boolean selectable = false;

    private double opacity = 1d;

    /**
     * Constructor that can used by subclass only.
     */
    protected AbstractMapLayer(final MutableStyle style){
        if (style == null){
            throw new NullPointerException("Style can not be null");
        }
        setStyle(style);
        this.desc = StyleConstants.DEFAULT_DESCRIPTION;
        this.selectionStyle = null;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public final boolean isWellKnownedType() {
        return this instanceof FeatureMapLayer || this instanceof CoverageMapLayer;
    }

    @Override
    public List<MapItem> items() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Getter for property style.
     * 
     * @return Value of property style.
     */
    @Override
    public MutableStyle getStyle() {
        return this.style;
    }

    /**
     * Setter for property style.
     * 
     * @param style : New value of property style.
     */
    @Override
    public void setStyle(final MutableStyle style) {
        if (style == null) {
            throw new NullArgumentException("Style can not be null");
        }
        
        final MutableStyle oldStyle;
        synchronized (this) {
            oldStyle = this.style;
            if(style.equals(oldStyle)){
                return;
            }

            if(oldStyle != null){
                styleListener.unregisterSource(oldStyle);
            }
            this.style = style;
            styleListener.registerSource(style);
        }
        firePropertyChange(STYLE_PROPERTY, oldStyle, this.style);
    }

    @Override
    public MutableStyle getSelectionStyle(){
        return selectionStyle;
    }

    @Override
    public void setSelectionStyle(final MutableStyle style){

        final MutableStyle oldStyle;
        synchronized (this) {
            oldStyle = this.selectionStyle;
            if(oldStyle == style){
                return;
            }
            this.selectionStyle = style;
        }
        firePropertyChange(SELECTION_STYLE_PROPERTY, oldStyle, this.selectionStyle);
    }

    /**
     * {@inheritDoc }
     */
    @Override 
    public double getOpacity(){
        return opacity;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOpacity(final double opacity){
        final double oldOpacity;
        synchronized (this) {
            oldOpacity = this.opacity;
            if(oldOpacity == opacity){
                return;
            }
            this.opacity = opacity;
        }
        firePropertyChange(OPACITY_PROPERTY, oldOpacity, this.opacity);
    }

    /**
     * Getter for property selectable.
     *
     * @return Value of property selectable.
     */
    @Override
    public boolean isSelectable() {
        return this.selectable;
    }

    /**
     * Setter for property selectable.
     *
     * @param selectable : New value of property selectable.
     */
    @Override
    public void setSelectable(final boolean selectable) {
        final boolean oldSelectable;
        synchronized (this) {
            oldSelectable = this.selectable;
            if(oldSelectable == selectable){
                return;
            }
            this.selectable = selectable;
        }
        firePropertyChange(SELECTABLE_PROPERTY, oldSelectable, this.selectable);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ElevationModel getElevationModel(){
        return elevation;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setElevationModel(final ElevationModel model){
        final ElevationModel oldElevation;
        synchronized (this) {
            oldElevation = this.elevation;
            if(oldElevation != null && oldElevation.equals(model)){
                return;
            }
            this.elevation = model;
        }
        firePropertyChange(ELEVATION_PROPERTY, oldElevation, this.elevation);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<GraphicBuilder> graphicBuilders(){
        return builders;
    }
    
    /**
     * A layer may provide a graphic builder, this enable
     * special representations, like wind arrows for coverages.
     * A layer may have different builder for each kind of Graphic implementation.
     * This enable the possibility to have custom made graphic representation
     * and several builder, for 2D,3D or else...
     * 
     * @param type : the graphic type wanted
     * @return graphicBuilder<? extends type> or null
     */
    @Override
    public <T extends Graphic> GraphicBuilder<? extends T> getGraphicBuilder( final Class<T> type ){
        
        for(GraphicBuilder builder : builders){
            if(type.isAssignableFrom(builder.getGraphicType())){
                return builder;
            }
        }
        
        return null;
    }
        
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
        
    protected void fireStyleChange(final EventObject event){
        //TODO make fire property change thread safe, preserve fire order
        
        final LayerListener[] lists = listeners.getListeners(LayerListener.class);
        
        for(LayerListener listener : lists){
            listener.styleChange(this, event);
        }
    }

    //--------------------------------------------------------------------------
    // style listener-----------------------------------------------------------
    //--------------------------------------------------------------------------

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        fireStyleChange(event);
    }

    @Override
    public void featureTypeStyleChange(final CollectionChangeEvent<MutableFeatureTypeStyle> event) {
        fireStyleChange(event);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addLayerListener(final LayerListener listener){
        synchronized(listeners){
            listeners.add(LayerListener.class, listener);
            addItemListener(listener);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeLayerListener(final LayerListener listener){
        synchronized(listeners){
            listeners.remove(LayerListener.class, listener);
            removeItemListener(listener);
        }
    }
    
}
