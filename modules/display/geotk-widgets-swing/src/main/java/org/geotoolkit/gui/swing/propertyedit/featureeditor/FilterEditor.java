/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.gui.swing.filter.JCQLEditor;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;

/**
 * Filter/Expression type editor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FilterEditor extends PropertyValueEditor implements ActionListener{

    private final JButton guiButton = new JButton("...");
    private final JLabel guiLabel = new JLabel();
    private AttributeType type = null;
    private Object value = null;

    public FilterEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, guiLabel);
        add(BorderLayout.EAST, guiButton);
        guiButton.addActionListener(this);
        guiButton.addFocusListener(this);
        guiButton.setMargin(new Insets(0, 0, 0, 0));
    }

    private void updateText(){
        if(value instanceof Filter){
            guiLabel.setText(CQL.write((Filter)value));
        }else if(value instanceof Expression){
            guiLabel.setText(CQL.write((Expression)value));
        }else{
            guiLabel.setText("");
        }
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        Class valueClass = ((AttributeType)candidate).getValueClass();
        return Expression.class.isAssignableFrom(valueClass)
            || Filter.class.isAssignableFrom(valueClass);
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        this.type = (AttributeType) type;
        this.value = value;
        updateText();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(Expression.class.isAssignableFrom(type.getValueClass())){
            try {
                value = JCQLEditor.showDialog(this, null, (Expression)value);
            } catch (CQLException ex) {
                Logging.getLogger("org.geotoolkit.gui.swing.propertyedit.featureeditor").log(Level.INFO, ex.getMessage(), ex);
            }
        }else{
            try {
                value = JCQLEditor.showDialog(this, null, (Filter)value);
            } catch (CQLException ex) {
                Logging.getLogger("org.geotoolkit.gui.swing.propertyedit.featureeditor").log(Level.INFO, ex.getMessage(), ex);
            }
        }

        updateText();
        valueChanged();
    }

    @Override
    public void setEnabled(boolean enabled) {
        guiButton.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return guiButton.isEnabled();
    }
}
