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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.geotoolkit.gui.swing.referencing.AuthorityCodesComboBox;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.feature.type.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class CRSEditor extends PropertyValueEditor implements PropertyChangeListener {

    private final AuthorityCodesComboBox component = new AuthorityCodesComboBox();

    public CRSEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, component);
        component.addPropertyChangeListener(this);
        component.addFocusListener(this);
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return CoordinateReferenceSystem.class.equals(candidate.getBinding());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        if (value instanceof CoordinateReferenceSystem) {
            String code = IdentifiedObjects.getIdentifier(((CoordinateReferenceSystem) value));
            component.setSelectedCode(code);
            try {
                if (component.getSelectedItem() == null) {
                    //strip the 'EPSG:'
                    final int index = code.indexOf(':');
                    if (index >= 0) {
                        code = code.substring(index + 1);
                        component.setSelectedCode(code);
                    }
                }
            } catch (FactoryException ex) {
                //no need to log
            }
        }else{
            component.setSelectedCode(null);
        }
    }

    @Override
    public Object getValue() {
        try {
            return component.getSelectedItem();
        } catch (FactoryException ex) {
            //no need to log
            return null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AuthorityCodesComboBox.SELECTED_CODE_PROPERTY)) {
            valueChanged();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        //avoid memory leaks by removing listener.
        component.removePropertyChangeListener(this);
    }

    @Override
    public void setEnabled(boolean enabled) {
        component.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return component.isEnabled();
    }
}
