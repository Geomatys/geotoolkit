/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2012, Geomatys
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
package org.geotoolkit.gui.swing.go2.control;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.measure.unit.Unit;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import org.geotoolkit.display.canvas.DefaultCanvasController2D;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.referencing.crs.AbstractCRS;
import org.geotoolkit.referencing.crs.AbstractSingleCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.cs.AbstractCS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.datum.AbstractDatum;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JAdditionalAxisNavigator extends JPanel{
    
    private class AxisDef{
        private final JPanel pane = new JPanel(new BorderLayout());
        private JButton guiDel = new JButton("-");
        private JMapAxisLine nav;
        
        public AxisDef(final CoordinateReferenceSystem axis){
            nav = new JMapAxisLine(axis);
            
            pane.add(BorderLayout.NORTH,guiDel);
            pane.add(BorderLayout.CENTER,nav);
            
            guiDel.setIcon(IconBundle.getIcon("16_remove"));
            guiDel.setText(axis.getName().getCode());
            guiDel.setMargin(new Insets(0, 0, 0, 0));
            guiDel.setVerticalTextPosition(SwingConstants.BOTTOM);
            
            guiDel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeAxis(AxisDef.this);
                }
            });
        }
    }
    
    private final List<AxisDef> axis = new ArrayList<AxisDef>();
    private final JPanel grid = new JPanel(new GridLayout(1, 0));
    private final JComboBox guiAxis = new JComboBox();
    private volatile JMap2D map = null;
        
    public JAdditionalAxisNavigator(){
        super(new BorderLayout());
        
        final JButton but = new JButton(IconBundle.getIcon("16_add"));
        but.setMargin(new Insets(0, 0, 0, 0));
        but.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAxis();
            }
        });
        
        guiAxis.setEditable(true);
        
        final List values = new ArrayList();
        values.add( DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
        guiAxis.setModel(new ListComboBoxModel(values));
        guiAxis.setEditor(new BasicComboBoxEditor(){

            private Object anObject;
            
            @Override
            public void setItem(Object anObject) {
                this.anObject = anObject;
                if(anObject instanceof CoordinateReferenceSystem ){
                    super.setItem(((CoordinateReferenceSystem)anObject).getName().getCode());
                }else{
                    super.setItem(anObject);
                }                
            }

            @Override
            public Object getItem() {
                
                String name = (String)super.getItem();
                if(anObject instanceof CoordinateReferenceSystem 
                        && ((CoordinateReferenceSystem)anObject).getName().getCode().equals(name) ){
                    return anObject;
                }
                
                //create it
                final AbstractDatum frequencyDatum = new AbstractDatum(Collections.singletonMap("name", name));
                final AbstractCS customCs = new AbstractCS(Collections.singletonMap("name", name), new DefaultCoordinateSystemAxis(name, "n", AxisDirection.OTHER, Unit.ONE));
                final AbstractCRS customCRS = new AbstractSingleCRS(Collections.singletonMap("name", name), frequencyDatum, customCs);
                return customCRS;
            }
            
        });
        
        guiAxis.setRenderer(new DefaultListCellRenderer(){

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if(value instanceof CoordinateReferenceSystem){
                    final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) value;
                    lbl.setText(crs.getName().getCode());
                }
                
                return lbl;
            }
            
        });
        
        final JPanel addPan = new JPanel(new BorderLayout());
        addPan.add(BorderLayout.CENTER,guiAxis);
        addPan.add(BorderLayout.EAST,but);
        add(BorderLayout.NORTH,addPan);
        add(BorderLayout.CENTER,grid);
    }
    
    public JMap2D getMap() {
        return map;
    }

    public void setMap(final JMap2D map) {
        this.map = map;
        for(AxisDef def : axis){
            def.nav.setMap(map);
        }
        repaint();
    }
    
    
    private void addAxis(){
        final Object obj = guiAxis.getSelectedItem();
        final CoordinateReferenceSystem axi = (CoordinateReferenceSystem) obj;
        
        final AxisDef def = new AxisDef(axi);
        def.nav.setMap(map);
        axis.add(def);
        grid.add(def.pane);
        grid.revalidate();
        grid.repaint();
    }
    
    private void removeAxis(AxisDef def){
        
        if(map != null){
            final DefaultCanvasController2D control = (DefaultCanvasController2D) map.getCanvas().getController();
            try {
                control.setAxisRange(null, null, def.nav.getAxisIndexFinder(), def.nav.getCrs());
            } catch (TransformException ex) {
                ex.printStackTrace();
            }
        }
        
        def.nav.setMap(null);
        axis.remove(def);
        grid.remove(def.pane);
        grid.revalidate();
        grid.repaint();
    }
    
    
}
