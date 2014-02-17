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
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JPreview;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import static org.geotoolkit.gui.swing.style.StyleElementEditor.PROPERTY_TARGET;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.GraphicalSymbol;

/**
 * Graphical symbol controller editor.
 * 
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JGraphicSymbolControlPane extends StyleElementEditor<GraphicalSymbol> {

    private final Action symbolChange = new AbstractAction(MessageBundle.getString("change")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null,paneGraphicalSymbolChooser,"",JOptionPane.PLAIN_MESSAGE);
            final GraphicalSymbol created = create();
            parse(created);
            JGraphicSymbolControlPane.this.firePropertyChange(PROPERTY_TARGET, null, created);
        }
    };
    
    private final JButton guiMarkButton = new JButton(symbolChange);
    private final JPreview guiMarkLabel = new JPreview();
    
    private MapLayer layer = null;    
    private final JGraphicalSymbolPane paneGraphicalSymbolChooser = new JGraphicalSymbolPane();

    /** 
     * Creates new form JGraphicSymbolControlPane
     */
    public JGraphicSymbolControlPane() {
        super(new BorderLayout(8,8),GraphicalSymbol.class);

        guiMarkLabel.setPreferredSize(new Dimension(32, 32));
        guiMarkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                symbolChange.actionPerformed(new ActionEvent(JGraphicSymbolControlPane.this, evt.getID(), PROPERTY_TARGET));
            }
        });
        
        add(guiMarkLabel, BorderLayout.WEST);
        add(guiMarkButton,BorderLayout.EAST);
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
   
}
