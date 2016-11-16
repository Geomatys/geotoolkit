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
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JAdvancedStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationIntervalStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationSingleStylePanel;
import org.geotoolkit.gui.swing.style.JColorMapPane;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSLDImportExportPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSimpleStylePanel;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.symbolizer.JCellSymbolizerPane;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.DefaultMutableStyle;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;
import org.opengis.style.Style;

/**
 * Style type editor.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
        guiButton.addFocusListener(this);
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
    public boolean canHandle(PropertyType candidate) {
        return candidate instanceof AttributeType && Style.class.equals(((AttributeType)candidate).getValueClass());
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
        editors.addPropertyPanel(MessageBundle.format("analyze"),new JSimpleStylePanel());
        editors.addPropertyPanel(MessageBundle.format("analyze_vector"),new JClassificationSingleStylePanel());
        editors.addPropertyPanel(MessageBundle.format("analyze_vector"),new JClassificationIntervalStylePanel());
        editors.addPropertyPanel(MessageBundle.format("analyze_raster"),new JColorMapPane());
        editors.addPropertyPanel(MessageBundle.format("analyze_raster"),new JCellSymbolizerPane());
        editors.addPropertyPanel(MessageBundle.format("sld"),new JAdvancedStylePanel());
        editors.addPropertyPanel(MessageBundle.format("sld"),new JSLDImportExportPanel());
        editors.setTarget(layer);
        

        final int res = JOptionDialog.show((Component)(e.getSource()),editors, JOptionPane.OK_CANCEL_OPTION);
        
        if(JOptionPane.OK_OPTION == res){
            editors.apply();
            value = layer.getStyle();
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
