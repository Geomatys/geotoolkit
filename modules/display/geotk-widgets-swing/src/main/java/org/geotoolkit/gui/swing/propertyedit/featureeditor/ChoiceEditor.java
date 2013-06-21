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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.geotoolkit.filter.visitor.DefaultFilterVisitor;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ChoiceEditor extends PropertyValueEditor implements ActionListener{

    private final JComboBox guiCombo = new JComboBox();
    private final JLabel guiLabel = new JLabel();

    public ChoiceEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, guiCombo);
        guiCombo.addActionListener(this);
        guiLabel.setOpaque(false);
        setOpaque(false);
    }

    @Override
    public void setValue(PropertyType type, Object value) {

        removeAll();
        guiLabel.setText("");

        final List<Object> values = extractChoices(type);
        guiCombo.setModel(new ListComboBoxModel(values));
        guiCombo.setSelectedItem(value);

        if(values.size() == 1){
            guiLabel.setText(String.valueOf(values.get(0)));
            add(BorderLayout.CENTER,guiLabel);
        }else{
            add(BorderLayout.CENTER,guiCombo);
        }

    }

    @Override
    public Object getValue() {
        return guiCombo.getSelectedItem();
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return extractChoices(candidate) != null;
    }

    /**
     * Search for a 'In' restriction filter.
     * return list of possible values if restriction exist. null otherwise
     */
    private static List<Object> extractChoices(PropertyType candidate){
        Class clazz = candidate.getBinding();
        final List choices = new ArrayList<Object>();
        final List<Filter> restrictions = candidate.getRestrictions();
        for(Filter f : restrictions){
            f.accept(new DefaultFilterVisitor() {
                @Override
                public Object visit(Function expression, Object data) {
                    if(expression.getName().equalsIgnoreCase("in")){
                        final List<Expression> values = expression.getParameters();
                        for(int i=1,n=values.size();i<n;i++){
                            //we expect values to be literals
                            choices.add(values.get(i).evaluate(null));
                        }
                    }
                    return data;
                }

            }, choices);
        }
        
        if(choices.isEmpty()){
            return null;
        }else{
            if (Comparable.class.isAssignableFrom(clazz)) {
                Collections.sort(choices);
            }
            return choices;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        valueChanged();
    }

    @Override
    public void setEnabled(boolean enabled) {
        guiCombo.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return guiCombo.isEnabled();
    }
}
