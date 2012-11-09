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
import org.opengis.style.GraphicalSymbol;

/**
 * Graphical symbol controller editor.
 * 
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JGraphicSymbolControlPane extends StyleElementEditor<GraphicalSymbol> {

    private final JButton guiMarkButton = new JButton();
    private final JPreview guiMarkLabel = new JPreview();
    
    private MapLayer layer = null;    
    private final JGraphicalSymbolPane paneGraphicalSymbolChooser = new JGraphicalSymbolPane();

    /** 
     * Creates new form JGraphicSymbolControlPane
     */
    public JGraphicSymbolControlPane() {
        super(GraphicalSymbol.class);
        setLayout(new BorderLayout(8,8));

        guiMarkButton.setText(MessageBundle.getString("change"));
        guiMarkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                guiMarkButtonActionPerformed(evt);
            }
        });
        add(guiMarkButton,BorderLayout.EAST);

        guiMarkLabel.setPreferredSize(new Dimension(32, 32));
        guiMarkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                guiMarkLabelMouseClicked(evt);
            }
        });
        add(guiMarkLabel, BorderLayout.WEST);
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;     
        paneGraphicalSymbolChooser.setLayer(layer);
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
    public void parse(final GraphicalSymbol graphicalSymbol) {
        
        if (graphicalSymbol != null) {
            guiMarkLabel.parse(graphicalSymbol);            
            paneGraphicalSymbolChooser.parse(graphicalSymbol);            
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GraphicalSymbol create() {
        return paneGraphicalSymbolChooser.create();
    }

    private void guiMarkButtonActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(null,paneGraphicalSymbolChooser,"",JOptionPane.PLAIN_MESSAGE);
        parse(create());
        firePropertyChange(PROPERTY_TARGET, null, create());
    }

    private void guiMarkLabelMouseClicked(java.awt.event.MouseEvent evt) {
        JOptionPane.showMessageDialog(null,paneGraphicalSymbolChooser,"",JOptionPane.PLAIN_MESSAGE);
        parse(create());
        firePropertyChange(PROPERTY_TARGET, null, create());
    }
   
}
