/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.gui.swing.navigator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingConstants;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractNavigatorModel<T extends Comparable> implements NavigatorModel<T>{

    /**
     * Listeners to be notified about any changes in this object properties.
     */
    protected final PropertyChangeSupport propertyListeners;

    private int orientation = SwingConstants.HORIZONTAL;
    private double scale = 1;
    private double translate = 0;

    public AbstractNavigatorModel() {
        this.propertyListeners = new PropertyChangeSupport(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setScale(double scale){
        if(this.scale != scale){
            double old = this.scale;
            this.scale = scale;
            propertyListeners.firePropertyChange(SCALE_PROPERTY, old, this.scale);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getScale(){
        return scale;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTranslation(double tr){
        if(this.translate != tr){
            double old = this.translate;
            this.translate = tr;
            propertyListeners.firePropertyChange(TRANSLATE_PROPERTY, old, this.translate);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getTranslation(){
        return translate;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOrientation(int orientation) {
        if(orientation != SwingConstants.NORTH && orientation != SwingConstants.SOUTH &&
                orientation != SwingConstants.EAST && orientation != SwingConstants.WEST){
            throw new IllegalArgumentException("Orientation must be SwingConstants : HORIZONTAL or VERTICAL.");
        }

        int old = this.orientation;
        this.orientation = orientation;
        
        if(this.orientation != orientation){
            propertyListeners.firePropertyChange(ORIENTATION_PROPERTY, old, orientation);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getOrientation() {
        return orientation;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyListeners.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyListeners.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyListeners.removePropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyListeners.removePropertyChangeListener(propertyName, listener);
    }

}
