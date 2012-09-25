/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 Geomatys
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
package org.geotoolkit.gui.swing.propertyedit.styleproperty.simple;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JPreview;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Fill;

/**
 * Fill control panel. This class displays a simple controller panel with a preview image and a button
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JFillControlPane extends StyleElementEditor<Fill> {

    private final JButton guiColorButton = new JButton();
    private final JPreview guiColorLabel = new JPreview();
    private final JFillPane paneFillChooser = new JFillPane();
    
    private MapLayer layer = null;   

    /** 
     * Creates new form JFillControlPanel 
     */
    public JFillControlPane() {
        super(Fill.class);
        setLayout(new BorderLayout(8,8));

        guiColorButton.setText(MessageBundle.getString("change"));
        guiColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                guiColorButtonActionPerformed(evt);
            }
        });
        add(guiColorButton, java.awt.BorderLayout.EAST);

        guiColorLabel.setPreferredSize(new Dimension(32, 32));
        guiColorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                guiColorLabelMouseClicked(evt);
            }
        });
        add(guiColorLabel, java.awt.BorderLayout.WEST);
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer(){
        return layer;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void parse(final Fill fill) {
        if (fill != null) {
            guiColorLabel.parse(fill);     
            paneFillChooser.parse(fill);           
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Fill create() {
        return paneFillChooser.create();
    }  
    
    public void setActive(boolean bool){       
        guiColorButton.setEnabled(bool);
        guiColorLabel.setVisible(bool);
    }

    /**
     * This function displays a message dialog in order to select how fill the form.
     * @param evt 
     */
    private void guiColorButtonActionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(null,paneFillChooser,"",JOptionPane.PLAIN_MESSAGE);
        parse(create());
        firePropertyChange(PROPERTY_TARGET, null, create());  
        
    }
    /**
     * This function displays a message dialog in order to select how fill the form.
     * @param evt 
     */
    private void guiColorLabelMouseClicked(MouseEvent evt) {
        JOptionPane.showMessageDialog(null,paneFillChooser,"",JOptionPane.PLAIN_MESSAGE);
        parse(create());
        firePropertyChange(PROPERTY_TARGET, null, create());  
    }
   
}
