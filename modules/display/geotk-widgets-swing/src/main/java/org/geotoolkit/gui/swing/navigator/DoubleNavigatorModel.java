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

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DoubleNavigatorModel implements NavigatorModel{

    /**
     * Listeners to be notified about any changes in this object properties.
     */
    protected final PropertyChangeSupport propertyListeners;
    private final AffineTransform dimToGraphic = new AffineTransform();

    public DoubleNavigatorModel() {
        this.propertyListeners = new PropertyChangeSupport(this);
    }

    @Override
    public double getGraphicValueAt(double d) {
        final Point2D pt = dimToGraphic.transform(new Point2D.Double(d, 0), null);
        return pt.getX();
    }

    @Override
    public double getDimensionValueAt(double candidate) {
        Point2D pt = null;
        try {
            pt = dimToGraphic.inverseTransform(new Point2D.Double(candidate, 0), null);
        } catch (NoninvertibleTransformException ex) {
            //shoult not happen
            Logger.getLogger(DoubleNavigatorModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pt.getX();
    }

    @Override
    public double getScale() {
        return dimToGraphic.getScaleX();
    }

    @Override
    public void scale(double factor, double position) {
        position = getDimensionValueAt(position);
        final AffineTransform newtrs = new AffineTransform(dimToGraphic);

        newtrs.translate(+position, 0);
        newtrs.scale(factor, 1);
        newtrs.translate(-position, 0);
        setTransform(newtrs);
    }

    @Override
    public void translate(double tr) {
        final AffineTransform newtrs = new AffineTransform(dimToGraphic);
        newtrs.translate(tr, 0);
        setTransform(newtrs);
    }

    protected void setTransform(AffineTransform trs){
        if(!dimToGraphic.equals(trs)){
            AffineTransform old = new AffineTransform(dimToGraphic);
            dimToGraphic.setTransform(trs);
            fireTransformChange(old,trs);
        }
    }

    protected void fireTransformChange(AffineTransform oldtrs, AffineTransform newtrs){
        final PropertyChangeEvent event = new PropertyChangeEvent(this, TRANSFORM_PROPERTY, oldtrs, newtrs);
        for(final PropertyChangeListener lst : propertyListeners.getPropertyChangeListeners()){
            lst.propertyChange(event);
        }
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
