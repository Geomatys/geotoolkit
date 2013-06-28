/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Johann Sorel
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
package org.geotoolkit.gui.swing.propertyedit.featureeditor;

import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JPanel;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel
 */
public abstract class PropertyValueEditor extends JPanel implements FocusListener {

    public static final String PROP_VALUE = "value";

    public PropertyValueEditor() {
    }

    public PropertyValueEditor(LayoutManager layout) {
        super(layout);
    }

    public abstract boolean canHandle(PropertyType candidate);

    public abstract void setValue(final PropertyType type, Object value);

    public abstract Object getValue();
    
    @Override
    public abstract void setEnabled(boolean enabled);
    
    @Override
    public abstract boolean isEnabled();
    
    /**
     * Fire property changed event with new property value.
     */
    public void valueChanged() {
        firePropertyChange(PROP_VALUE, null, getValue());
    }

    /**
     * Forward focus gained event.
     * @param e 
     */
    @Override
    public void focusGained(FocusEvent e) {
        for (FocusListener listeners : getFocusListeners()) {
            listeners.focusGained(e);
        }
    }

    /**
     * Forward focus lost event.
     * @param e 
     */
    @Override
    public void focusLost(FocusEvent e) {
        for (FocusListener listeners : getFocusListeners()) {
            listeners.focusLost(e);
        }
    }
    
    /**
     * Create a copy of this editor.
     * @return PropertyValueEditor
     */
    public PropertyValueEditor copy(){
        try {
            return getClass().newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
}
