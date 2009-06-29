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

import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.util.collection.CheckedArrayList;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;

import org.opengis.display.primitive.Graphic;
import org.opengis.style.Description;

/**
 * Abstract implementation of the MapLayer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractMapLayer implements MapLayer {

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.map");
    
    private final List<GraphicBuilder> builders = new CheckedArrayList<GraphicBuilder>(GraphicBuilder.class);
    
    private final EventListenerList listeners = new EventListenerList();

    private final StyleListener styleListener = new StyleListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            fireStyleChange(event);
        }

        @Override
        public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
            fireStyleChange(event);
        }
        
    };
    
    private final Map<String,Object> parameters = new HashMap<String,Object>();

    protected MutableStyle style;

    protected MutableStyle selectionStyle;

    protected ElevationModel elevation = null;

    protected String name = null;

    protected Description desc = null;

    protected boolean visible = true;

    protected boolean selectable = false;

    /**
     * Constructor that can used by subclass only.
     */
    protected AbstractMapLayer(MutableStyle style){
        if (style == null){
            throw new NullPointerException("Style can not be null");
        }
        setStyle(style);
        this.desc = StyleConstants.DEFAULT_DESCRIPTION;
        this.selectionStyle = null;
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setName(String name) {
        final String oldName;
        synchronized (this) {
            oldName = this.name;
            if (Utilities.equals(oldName, name)) {
                return;
            }
            this.name = name;
        }
        firePropertyChange(NAME_PROPERTY, oldName, this.name);
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public Description getDescription() {
        return desc;
    }

    /**
     * {@inheritDoc }
     * @param desc : Description can't be null
     */
    @Override
    public void setDescription(Description desc){
        if (desc == null) {
            throw new NullPointerException("description can't be null");
        }
        
        final Description oldDesc;
        synchronized (this) {
            oldDesc = this.desc;
            if(oldDesc.equals(desc)){
                return;
            }
            this.desc = desc;
        }
        firePropertyChange(DESCRIPTION_PROPERTY, oldDesc, this.desc);
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
    public void setStyle(MutableStyle style) {
        if (style == null) {
            throw new NullPointerException("Style can't be null");
        }
        
        final MutableStyle oldStyle;
        synchronized (this) {
            oldStyle = this.style;
            if(style.equals(oldStyle)){
                return;
            }

            synchronized(listeners){
                if(listeners.getListenerCount() > 0){

                    if(oldStyle != null){
                        oldStyle.removeListener(styleListener);
                    }
                    this.style = style;
                    this.style.addListener(styleListener);
                }else{
                    this.style = style;
                }
            }

        }
        firePropertyChange(STYLE_PROPERTY, oldStyle, this.style);
    }

    @Override
    public MutableStyle getSelectionStyle(){
        return selectionStyle;
    }

    @Override
    public void setSelectionStyle(MutableStyle style){

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
     * Getter for property visible.
     * 
     * @return Value of property visible.
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Setter for property visible.
     * 
     * @param visible : New value of property visible.
     */
    @Override
    public void setVisible(boolean visible) {
        final boolean oldVisible;
        synchronized (this) {
            oldVisible = this.visible;
            if(oldVisible == visible){
                return;
            }
            this.visible = visible;
        }
        firePropertyChange(VISIBILITY_PROPERTY, oldVisible, this.visible);
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
     * @param visible : New value of property selectable.
     */
    @Override
    public void setSelectable(boolean selectable) {
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
    public void setElevationModel(ElevationModel model){
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
    public <T extends Graphic> GraphicBuilder<? extends T> getGraphicBuilder( Class<T> type ){
        
        for(GraphicBuilder builder : builders){
            if(type.isAssignableFrom(builder.getGraphicType())){
                return builder;
            }
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setUserPropertie(String key,Object value){
        parameters.put(key, value);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Object getUserPropertie(String key){
        return parameters.get(key);
    }
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        //TODO make fire property change thread safe, preserve fire order
        
        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final LayerListener[] lists = listeners.getListeners(LayerListener.class);
        
        for(LayerListener listener : lists){
            listener.propertyChange(event);
        }
    }
    
    protected void fireStyleChange(EventObject event){
        //TODO make fire property change thread safe, preserve fire order
        
        final LayerListener[] lists = listeners.getListeners(LayerListener.class);
        
        for(LayerListener listener : lists){
            listener.styleChange(this, event);
        }
    }
    
    @Override
    public void addLayerListener(LayerListener listener){

        synchronized(listeners){
            if(listeners.getListenerCount() == 0){
                style.addListener(styleListener);
            }
            listeners.add(LayerListener.class, listener);
        }
    }

    @Override
    public void removeLayerListener(LayerListener listener){

        synchronized(listeners){
            listeners.remove(LayerListener.class, listener);
            if(listeners.getListenerCount() == 0){
                style.removeListener(styleListener);
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("AbstractMapLayer[ ");
        buf.append(desc);
        if (visible) {
            buf.append(", VISIBLE");
        } else {
            buf.append(", HIDDEN");
        }
        buf.append(", style=");
        buf.append(style);
        buf.append("]");
        return buf.toString();
    }

}
