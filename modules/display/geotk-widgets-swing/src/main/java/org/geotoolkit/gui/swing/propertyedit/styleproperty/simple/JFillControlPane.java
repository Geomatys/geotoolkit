/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JPreview;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import static org.geotoolkit.gui.swing.style.StyleElementEditor.PROPERTY_TARGET;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Fill;

/**
 * Fill control panel. This class displays a simple controller panel with a preview image and a button
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JFillControlPane extends StyleElementEditor<Fill> {

    private final JButton guiColorButton = new JButton(new AbstractAction(MessageBundle.getString("change")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null,paneFillChooser,"",JOptionPane.PLAIN_MESSAGE);
            parse(create());
            firePropertyChange(PROPERTY_TARGET, null, create());  
        }
    });
    private final JPreview guiColorLabel = new JPreview();
    private final JFillPane paneFillChooser = new JFillPane();
    
    private MapLayer layer = null;   

    /** 
     * Creates new form JFillControlPanel 
     */
    public JFillControlPane() {
        super(Fill.class);
        setLayout(new BorderLayout(8,8));

        guiColorLabel.setPreferredSize(new Dimension(32, 32));
        guiColorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JOptionPane.showMessageDialog(null,paneFillChooser,"",JOptionPane.PLAIN_MESSAGE);
                parse(create());
                firePropertyChange(PROPERTY_TARGET, null, create());  
            }
        });
        
        add(guiColorButton, java.awt.BorderLayout.EAST);
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
   
}
