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
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.apache.sis.feature.SingleAttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.util.Utilities;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

/**
 * Array Type editor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ArrayEditor extends PropertyValueEditor implements ActionListener{

    private final List<PropertyValueEditor> editors = new ArrayList<>();
    private final JButton guiButton = new JButton("...");
    private final JLabel guiLabel = new JLabel();
    private AttributeType type = null;
    private Object value = null;

    public ArrayEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, guiLabel);
        add(BorderLayout.EAST, guiButton);
        guiButton.addActionListener(this);
        guiButton.addFocusListener(this);
        guiButton.setMargin(new Insets(0, 0, 0, 0));
    }

    private void updateText(){
        if (value != null) {
            guiLabel.setText(Utilities.deepToString(value));
        } else {
            guiLabel.setText(MessageBundle.format("undefined"));
        }
    }

    /**
     * Must be called before the canHandle method.
     * @param editors
     */
    public void setEditors(Collection<? extends PropertyValueEditor> editors){
        this.editors.clear();
        this.editors.addAll(editors);
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        if(!(candidate instanceof AttributeType)){
            return false;
        }
        final Class clazz = ((AttributeType)candidate).getValueClass();
        if(!clazz.isArray()) return false;

        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
        atb.setName("");
        atb.setValueClass(clazz.getComponentType());
        final PropertyType subType = atb.build();

        for(PropertyValueEditor editor : editors){
            if(editor.canHandle(subType)){
                return true;
            }
        }

        return false;
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        this.type = (AttributeType) type;
        this.value = value;
        updateText();
    }

    @Override
    public Object getValue() {
        //create empty array to avoid return null.
        if (value == null) {
            final Class subClass = this.type.getValueClass().getComponentType();
            value = Array.newInstance(subClass, 0);
        }
        
        return value;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final JFeatureOutLine outline = new JFeatureOutLine();

        final Class subClass = this.type.getValueClass().getComponentType();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("array");
        ftb.addAttribute(subClass).setName("element").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        final FeatureType subType = ftb.build();
        final Feature ca = subType.newInstance();

        ca.setPropertyValue("element", Arrays.asList(value));

        outline.setEdited(ca);

        final int res = JOptionDialog.show((Component)e.getSource(), new JScrollPane(outline), JOptionPane.OK_OPTION);
        
        if(JOptionPane.OK_OPTION == res){
            value = ca.getPropertyValue("element");
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
