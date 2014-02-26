/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2012 - 2014, Geomatys
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
package org.geotoolkit.gui.swing.render2d.control;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.Unit;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.navigator.DateRenderer;
import org.geotoolkit.gui.swing.navigator.DoubleRenderer;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.util.BufferLayout;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.util.JTabHeader;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.FeatureMapLayer.DimensionDef;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

/**
 * Component that allows to browse data on an additional axis.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JAdditionalAxisNavigator extends JPanel {

    private static final ImageIcon ICON_ADD = IconBuilder.createIcon(FontAwesomeIcons.ICON_PLUS_SIGN, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_SEPARATE = IconBuilder.createIcon(FontAwesomeIcons.ICON_LIST_OL, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_VERT = IconBuilder.createIcon(FontAwesomeIcons.ICON_LEVEL_UP, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_DELETE = IconBuilder.createIcon(FontAwesomeIcons.ICON_REMOVE, 14, FontAwesomeIcons.DEFAULT_COLOR);
    
    private class AxisDef{
        private final CoordinateReferenceSystem axis;
        private final ActionListener closeAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAxis(AxisDef.this);
            }
        };
        private JMapAxisLine nav;

        public AxisDef(final CoordinateReferenceSystem axis){
            this.axis = axis;
            nav = new JMapAxisLine(axis);
            if(axis instanceof TemporalCRS){
                nav.setModelRenderer(new DateRenderer());
                long now = System.currentTimeMillis();
                nav.getModel().translate(-now);
                nav.getModel().scale(0.0000001d, 0);
            }else{
                nav.setModelRenderer(new DoubleRenderer());
            }
        }
        
        private String getAxisShortName(){
            return axis.getName().getCode();
        }
        
        private JPanel buildButton(boolean vertical){
            final JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(false);
            
            final JButton guiDel = new JButton("",ICON_DELETE);
            guiDel.setMargin(new Insets(0, 0, 0, 0));
            guiDel.setVerticalTextPosition(SwingConstants.BOTTOM);
            guiDel.addActionListener(closeAction);
            
            final JLabel lbl = new JLabel();
            
            String text = getAxisShortName();
            if(vertical){
                final StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                for(int i=0;i<text.length();i++){
                    sb.append(text.charAt(i));
                    sb.append("<br/>");
                }
                sb.append("</html>");
                text = sb.toString();
            }
            lbl.setText(text);
            
            if(vertical){
                panel.add(BorderLayout.CENTER,lbl);
                panel.add(BorderLayout.NORTH,guiDel);
            }else{
                panel.add(BorderLayout.CENTER,lbl);
                panel.add(BorderLayout.WEST,guiDel);
            }
            
            return panel;
        }
                
    }

    private final List<AxisDef> axis = new ArrayList<>();
    private final JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
    private final JPanel content = new JPanel();
    private JTabbedPane tabPane = new JTabbedPane(JTabbedPane.RIGHT);
    private final JList guiAxis = new JList();
    
    private volatile JMap2D map = null;

    private final Action addButton = new AbstractAction("", ICON_ADD) {
        @Override
        public void actionPerformed(ActionEvent e) {
            showSelectAxisPane();
        }
    };
    private final JToggleButton separateAction = new JToggleButton(new AbstractAction("", ICON_SEPARATE) {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateLayout();
        }
    });
    private final JToggleButton verticalAction = new JToggleButton(new AbstractAction("", ICON_VERT) {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateLayout();
        }
    });

    
    public JAdditionalAxisNavigator(){
        super(new BorderLayout());
        toolbar.setFloatable(false);
        toolbar.add(addButton);
        toolbar.add(separateAction);
        toolbar.add(verticalAction);
        
        guiAxis.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                final Object tmpVal = value;

                if (value instanceof CoordinateReferenceSystem) {
                    value = "";
                }

                final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if(tmpVal instanceof CoordinateReferenceSystem){
                    final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) tmpVal;
                    final StringBuilder sb = new StringBuilder();
                    final CoordinateSystemAxis axi = crs.getCoordinateSystem().getAxis(0);
                    if(axi != null){
                        if(axi.getDirection() != null && axi.getDirection().identifier() != null){
                            sb.append(axi.getDirection().identifier().toUpperCase());
                        }

                        final Unit unit = axi.getUnit();
                        if(unit != null){
                            sb.append(" (");
                            sb.append(unit);
                            sb.append(") ");
                        }
                        sb.append(crs.getName().getCode());
                    }
                    lbl.setText(sb.toString());
                }

                if(tmpVal instanceof CoordinateSystemAxis){
                    final CoordinateSystemAxis axis = (CoordinateSystemAxis) tmpVal;
                    lbl.setText(axis.getName().getCode());
                }
                return lbl;
            }

        });

        add(BorderLayout.WEST,toolbar);
        add(BorderLayout.CENTER,content);
    }

    public JToolBar getToolbar() {
        return toolbar;
    }

    private void updateModel() {
        final List<CoordinateReferenceSystem> values = new ArrayList<>();
        values.add(DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
        values.add(DefaultTemporalCRS.JAVA);
        if (map != null) {
            getDimensions(map.getContainer().getContext(), values);
        }
        guiAxis.setModel(new ListComboBoxModel(values));
    }

    private void updateLayout(){
        content.removeAll();
        
        if(separateAction.isSelected()){
            if(verticalAction.isSelected()){
                content.setLayout(new GridLayout(1,0));
                for (AxisDef def : axis) {
                    def.nav.setOrientation(verticalAction.isSelected() ? SwingConstants.WEST : SwingConstants.SOUTH);
                    //change model scale if needed
                    final double scale = def.nav.getModel().getScale();
                    if(scale>0){
                        //scale is going up, must be negative
                        def.nav.getModel().scale(-1, 0);
                    }

                    final JPanel over = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    over.setOpaque(false);
                    over.add(def.buildButton(verticalAction.isSelected()));
                    
                    final JPanel stack = new JPanel(){
                        @Override
                        public boolean isOptimizedDrawingEnabled() {
                            return false;
                        }
                    };
                    stack.setLayout(new OverlayLayout(stack));
                    stack.add(over);
                    stack.add(def.nav);
                    content.add(stack);
                }
                
            }else{
                content.setLayout(new GridLayout(0,1));
                for (AxisDef def : axis) {
                    def.nav.setOrientation(verticalAction.isSelected() ? SwingConstants.WEST : SwingConstants.SOUTH);
                    //change model scale if needed
                    final double scale = def.nav.getModel().getScale();
                    if(scale<0){
                        //scale is going down, must be negative
                        def.nav.getModel().scale(-1, 0);
                    }

                    final JPanel over = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    over.setOpaque(false);
                    over.add(def.buildButton(verticalAction.isSelected()));
                    
                    final JPanel stack = new JPanel(){
                        @Override
                        public boolean isOptimizedDrawingEnabled() {
                            return false;
                        }
                    };
                    stack.setLayout(new OverlayLayout(stack));
                    stack.add(over);
                    stack.add(def.nav);
                    content.add(stack);
                }
                
            }
            
        }else{
            content.setLayout(new BorderLayout());
            //create a tab view
            if(tabPane != null){
                tabPane.removeAll();
            }
            
            if(verticalAction.isSelected()){
                tabPane = new JTabbedPane(JTabbedPane.TOP);
            }else{
                tabPane = new JTabbedPane(JTabbedPane.LEFT);
            }
            content.add(BorderLayout.CENTER,tabPane);
            
            for(int i=0;i<axis.size();i++) {
                final AxisDef def = axis.get(i);
                def.nav.setOrientation(verticalAction.isSelected() ? SwingConstants.WEST : SwingConstants.SOUTH);
                final double scale = def.nav.getModel().getScale();
                    if((verticalAction.isSelected() && scale>0) || (!verticalAction.isSelected() && scale<0)){
                        //scale must be inverted
                        def.nav.getModel().scale(-1, 0);
                    }
                
                tabPane.addTab(def.getAxisShortName(), def.nav);
                tabPane.setTabComponentAt(i, new JTabHeader(tabPane,def.closeAction));
            }
        }
        
        content.revalidate();
        content.repaint();
    }
    
    private static void getDimensions(final MapItem source, final List<CoordinateReferenceSystem> toFill) {
        if (source == null) {
            return;
        }
        if (source instanceof MapLayer) {
            final MapLayer layer = (MapLayer) source;
            if (layer.isVisible() || layer.isSelectable()) {
                final Envelope bounds = layer.getBounds();
                if (bounds != null && bounds.getCoordinateReferenceSystem() != null) {
                    final CoordinateReferenceSystem layerCRS = bounds.getCoordinateReferenceSystem();

                    final List<CoordinateReferenceSystem> parts = ReferencingUtilities.decompose(layerCRS);
browseCRS:          for (CoordinateReferenceSystem part : parts) {
                        if (part.getCoordinateSystem().getDimension() == 1
                                && part.getCoordinateSystem().getAxis(0).getDirection() != AxisDirection.FUTURE) {
                            // Check we haven't already got it
                            for (CoordinateReferenceSystem inserted : toFill) {
                                if (CRS.equalsApproximatively(inserted, part)) {
                                    continue browseCRS;
                                }
                            }
                            toFill.add(part);
                        }
                    }
                }
                // Handle extra dimensions of feature map layer
                if (layer instanceof FeatureMapLayer) {
                    final FeatureMapLayer fml = (FeatureMapLayer) layer;
                    for (final DimensionDef extraDim : fml.getExtraDimensions()) {
                        if (extraDim.getCrs() != null) {
                            toFill.add(extraDim.getCrs());
                        }
                    }
                }
            }

        } else {
            for (MapItem item : source.items()) {
                getDimensions(item, toFill);
            }
        }
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

    private void showSelectAxisPane(){
        updateModel();
        
        final int result = JOptionDialog.show(this, new JScrollPane(guiAxis), JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION){
            addAxis();
        }
        
    }
    
    private void addAxis(){
        final Object obj = guiAxis.getSelectedValue();
        final CoordinateReferenceSystem axi = (CoordinateReferenceSystem) obj;
        if(axi == null) return;
        
        //check the axis is not already in the list
        for (AxisDef tmp : axis) {
            if (CRS.equalsIgnoreMetadata(guiAxis.getSelectedValue(), tmp.nav.getCrs())) {
                return;
            }
        }
        
        final AxisDef def = new AxisDef(axi);
        def.nav.setMap(map);
        axis.add(def);
        
        updateLayout();
    }

    private void removeAxis(AxisDef def){

        if(map != null){
            try {
                map.getCanvas().setAxisRange(null, null, def.nav.getAxisIndexFinder(), def.nav.getCrs());
            } catch (TransformException ex) {
                ex.printStackTrace();
            }
        }

        def.nav.setMap(null);
        axis.remove(def);
                
        updateLayout();
    }

}
