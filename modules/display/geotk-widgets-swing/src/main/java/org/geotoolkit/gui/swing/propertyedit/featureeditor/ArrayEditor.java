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
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.iso.ResourceInternationalString;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.PropertyType;

/**
 * Array Type editor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ArrayEditor extends PropertyValueEditor implements ActionListener{

    private static final String BUNDLE_PATH = "org/geotoolkit/gui/swing/resource/Bundle";
    private static final String UNDEFINED_KEY = "undefined";
    private final List<PropertyValueEditor> editors = new ArrayList<PropertyValueEditor>();
    private final JButton guiButton = new JButton("...");
    private final JLabel guiLabel = new JLabel();
    private PropertyType type = null;
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
            guiLabel.setText(new ResourceInternationalString(BUNDLE_PATH, UNDEFINED_KEY).toString());
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
    public boolean canHandle(PropertyType type) {
        Class clazz = type.getBinding();
        if(!clazz.isArray()) return false;

        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        atb.setName("");
        atb.setBinding(clazz.getComponentType());
        final PropertyType subType = atb.buildType();

        for(PropertyValueEditor editor : editors){
            if(editor.canHandle(subType)){
                return true;
            }
        }

        return false;
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        this.type = type;
        this.value = value;
        updateText();
    }

    @Override
    public Object getValue() {
        //create empty array to avoid return null.
        if (value == null) {
            final Class subClass = this.type.getBinding().getComponentType();
            value = Array.newInstance(subClass, 0);
        }
        
        return value;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final JFeatureOutLine outline = new JFeatureOutLine();

        final Class subClass = this.type.getBinding().getComponentType();
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("array");
        final AttributeDescriptor elementDesc = ftb.add("element", subClass, 0, Integer.MAX_VALUE,false,null);
        final ComplexType subType = ftb.buildType();
        final ComplexAttribute ca = FeatureUtilities.defaultProperty(subType);


        final int size = (value != null) ? Array.getLength(value) : 0;
        for(int i=0; i<size;i++){
            final Attribute att = (Attribute) FeatureUtilities.defaultProperty(elementDesc);
            att.setValue(Array.get(value, i));
            ca.getProperties().add(att);
        }

        outline.setEdited(ca);

        final int res = JOptionDialog.show((Component)e.getSource(), new JScrollPane(outline), JOptionPane.OK_OPTION);
        
        if(JOptionPane.OK_OPTION == res){
            final Collection<Property> properties = ca.getProperties();
            final int newSize = properties.size();
            value = Array.newInstance(subClass, newSize);
            int i=0;
            for(Property prop : properties){
                final Object val = prop.getValue();
                if(subClass.isPrimitive()){
                    //we can only set if the value is not null
                    if(val != null){
                        Array.set(value,i,val);
                    }

                }else{
                    Array.set(value,i,val);
                }
                i++;
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
