/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.gui.swing.go2.control.edition;


import com.vividsolutions.jts.geom.Geometry;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;

import org.opengis.feature.Feature;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.JPanelMapDecoration;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;
import org.geotoolkit.gui.swing.go2.decoration.MapDecorationStack;


/**
 * Geometry clipboard edition tool delegate.
 * 
 * @author Johann Sorel
 * @module pending
 */
public class GeometryFromClipboardDelegate extends AbstractFeatureEditionDelegate {

    private final DialogDecoration dialogDecoration = new DialogDecoration();
    
    private Feature feature = null;


    public GeometryFromClipboardDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        feature = null;
        decoration.setGeometries(null);
        dialogDecoration.clipboardPanel.setGeometry(null);
    }
    
    @Override
    public MapDecoration getDecoration() {
        final MapDecoration parentDeco = super.getDecoration();
        return MapDecorationStack.wrap(dialogDecoration,parentDeco);
    }
    
    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        if(feature != null){            
            final Geometry geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
            decoration.setGeometries(Collections.singleton(helper.toObjectiveCRS(geom)));
            dialogDecoration.clipboardPanel.setGeometry(geom);
            dialogDecoration.clipboardPanel.setCrs(feature.getDefaultGeometryProperty().getType().getCoordinateReferenceSystem());
        }else{
            dialogDecoration.clipboardPanel.setGeometry(null);
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            if(feature == null){
                setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            }
        }else if(button == MouseEvent.BUTTON3 && feature != null){
            final Geometry oldgeom = (Geometry) feature.getDefaultGeometryProperty().getValue();
            final Geometry newGeom = dialogDecoration.clipboardPanel.getGeometry();
            if(!oldgeom.equals(newGeom)){
                helper.sourceModifyFeature(feature, newGeom, false);
            }
            reset();
        }
    }

    private class DialogDecoration extends JPanelMapDecoration implements PropertyChangeListener{

        private final JLayeredPane desktop;
        private final JInternalFrame frame;
        private final JClipboardPanel clipboardPanel;
        
        public DialogDecoration() {
            setLayout(new BorderLayout());
            
            clipboardPanel = new JClipboardPanel();
            clipboardPanel.addPropertyChangeListener(DialogDecoration.this);
            
            frame = new JInternalFrame("Clipboard");
            frame.setContentPane(clipboardPanel);      
            frame.setResizable(true);
            frame.setClosable(false);
            frame.setIconifiable(false);
            frame.pack();
            frame.setVisible(true);
            
            desktop = new JLayeredPane();
            desktop.setOpaque(false);
            desktop.add(frame);
            
            add(BorderLayout.CENTER,desktop);            
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            
            if(JClipboardPanel.GEOMETRY_PROPERTY.equals(evt.getPropertyName())){
                final Geometry geom = dialogDecoration.clipboardPanel.getGeometry();
                if(geom == null){
                    decoration.setGeometries((Collection)Collections.emptyList());
                }else{
                    decoration.setGeometries(Collections.singleton(helper.toObjectiveCRS(geom)));
                }
            }
            
        }
        
    }
}
