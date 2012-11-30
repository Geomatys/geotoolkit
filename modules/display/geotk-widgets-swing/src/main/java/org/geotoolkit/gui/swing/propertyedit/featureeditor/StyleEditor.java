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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.geotoolkit.gui.swing.misc.JOptionDialog;
import org.geotoolkit.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JAdvancedStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationIntervalStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationJenksPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationSingleStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JRasterColorMapStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSimpleStylePanel;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.DefaultMutableStyle;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.type.PropertyType;
import org.opengis.style.Style;

/**
 * Style type editor.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StyleEditor extends PropertyValueEditor implements ActionListener{

    private final JButton guiButton = new JButton("...");
    private final JLabel guiLabel = new JLabel();
    private PropertyType type = null;
    private Object value = null;

    public StyleEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, guiLabel);
        add(BorderLayout.EAST, guiButton);
        guiButton.addActionListener(this);
        guiButton.setMargin(new Insets(0, 0, 0, 0));
    }

    private void updateText(){
        if(value != null){
            guiLabel.setText(((Style)value).getName());
        }else{
            guiLabel.setText("");
        }
    }

    @Override
    public boolean canHandle(PropertyType type) {
        return Style.class.isAssignableFrom(type.getBinding());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        this.type = type;
        this.value = value;
        updateText();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(value == null){
            value = new DefaultMutableStyle();
        }
        
        final MapLayer layer = MapBuilder.createEmptyMapLayer();
        layer.setStyle((MutableStyle)value);

        final LayerStylePropertyPanel editors = new LayerStylePropertyPanel();
        editors.addPropertyPanel(new JSimpleStylePanel());
        editors.addPropertyPanel(new JClassificationSingleStylePanel());
        editors.addPropertyPanel(new JClassificationIntervalStylePanel());
        editors.addPropertyPanel(new JClassificationJenksPanel());
        editors.addPropertyPanel(new JRasterColorMapStylePanel());
        editors.addPropertyPanel(new JAdvancedStylePanel());
        editors.setTarget(layer);
        

        final int res = JOptionDialog.show((Component)(e.getSource()),editors, JOptionPane.OK_CANCEL_OPTION);
        
        if(JOptionPane.OK_OPTION == res){
            editors.apply();
            value = layer.getStyle();
        }

        updateText();
    }

}
