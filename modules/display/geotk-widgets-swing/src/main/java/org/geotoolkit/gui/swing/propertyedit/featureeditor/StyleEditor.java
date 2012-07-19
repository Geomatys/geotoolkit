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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JAdvancedStylePanel;
import org.geotoolkit.style.DefaultMutableStyle;
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

        final JDialog dialog = new JDialog();
        final JAdvancedStylePanel stylePane = new JAdvancedStylePanel();

        if(value == null){
            value = new DefaultMutableStyle();
        }

        stylePane.parse(value);

        dialog.setContentPane(stylePane);
        dialog.pack();
        dialog.setSize(640, 480);

        final Point p = ((JComponent)e.getSource()).getLocationOnScreen();
        p.x -= dialog.getWidth()/2;
        p.y -= dialog.getHeight()/2;
        if(p.x < 0) p.x=0;
        if(p.y < 0) p.y=0;

        dialog.setLocation(p);
        dialog.setModal(true);
        dialog.setVisible(true);

        value = stylePane.create();

        updateText();
    }

}
