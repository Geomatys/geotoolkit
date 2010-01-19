/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.navigator.DoubleNavigatorModel;
import org.geotoolkit.gui.swing.navigator.DoubleRenderer;
import org.geotoolkit.gui.swing.navigator.JNavigator;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

import org.opengis.display.canvas.CanvasEvent;
import org.opengis.display.canvas.CanvasListener;
import org.opengis.display.canvas.RenderingState;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JCoordinateBar extends JToolBar {

    private static final ImageIcon ICON_XY = IconBundle.getInstance().getIcon("16_xy");
    private static final ImageIcon ICON_XY_DISABLE = IconBundle.getInstance().getIcon("16_xy_disable");
    private static final ImageIcon ICON_STATEFULL = IconBundle.getInstance().getIcon("16_statefull");
    private static final ImageIcon ICON_STATEFULL_DISABLE = IconBundle.getInstance().getIcon("16_statefull_disable");
    private static final ImageIcon ICON_TEMPORAL = IconBundle.getInstance().getIcon("16_temporal");
    private static final ImageIcon ICON_ELEVATION = IconBundle.getInstance().getIcon("16_elevation");

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private final myListener listener = new myListener();
    private Map2D map = null;
    private String error = MessageBundle.getString("map_control_coord_error");

    private final JCheckBox guiAxis = new JCheckBox();
    private final JScaleCombo guiCombo = new JScaleCombo();
    private final JTextField guiCoord = new JTextField();
    private final JCheckBox guiStatefull = new JCheckBox();
    private final JCRSButton guiCRS = new JCRSButton();
    private final JProgressBar guiPainting = new JProgressBar();
    private final JToggleButton guiElevation = new JToggleButton(ICON_ELEVATION);
    private final JToggleButton guiTemporal = new JToggleButton(ICON_TEMPORAL);
    
    private final JPanel paneTemp = new JPanel(new BorderLayout());
    private final JPanel paneElev = new JPanel(new BorderLayout());
    private final JMapAnimatingPane guiAnimatingPane = new JMapAnimatingPane();
    private final JNavigator<Double> panenav = new JNavigator<Double>(new DoubleNavigatorModel());
    private final JMapTimeLine guiTimeLine = new JMapTimeLine();

    public JCoordinateBar() {
        this(null);
    }

    public JCoordinateBar(Map2D candidate) {
        setLayout(new BorderLayout(0,1));
        final JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setOpaque(false);
        add(BorderLayout.SOUTH,bottom);
        add(BorderLayout.CENTER,paneTemp);

        paneTemp.add(BorderLayout.WEST,guiAnimatingPane);
        paneTemp.add(BorderLayout.CENTER,guiTimeLine);

        panenav.setModelRenderer(new DoubleRenderer());
        paneElev.add(BorderLayout.CENTER,panenav);


        guiAxis.setSelected(true);
        guiAxis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    map.getCanvas().getController().setAxisProportions((!guiAxis.isSelected()) ? Double.NaN : 1);
                }
            }
        });
        guiAxis.setToolTipText(MessageBundle.getString("map_xy_ratio"));

        guiStatefull.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null && map instanceof JMap2D){
                    ((JMap2D)map).setStatefull(guiStatefull.isSelected());
                }
            }
        });
        guiStatefull.setToolTipText(MessageBundle.getString("map_statefull"));

        guiCRS.setEnabled(false);
        guiAxis.setPressedIcon(ICON_XY);
        guiAxis.setSelectedIcon(ICON_XY);
        guiAxis.setRolloverSelectedIcon(ICON_XY);
        guiAxis.setIcon(ICON_XY_DISABLE);
        guiAxis.setRolloverIcon(ICON_XY_DISABLE);
        guiAxis.setOpaque(false);
        guiStatefull.setPressedIcon(ICON_STATEFULL);
        guiStatefull.setSelectedIcon(ICON_STATEFULL);
        guiStatefull.setRolloverSelectedIcon(ICON_STATEFULL);
        guiStatefull.setIcon(ICON_STATEFULL_DISABLE);
        guiStatefull.setRolloverIcon(ICON_STATEFULL_DISABLE);
        guiStatefull.setOpaque(false);

        guiCoord.setOpaque(false);
        guiCoord.setBorder(null);
        guiCoord.setEditable(false);
        guiCoord.setHorizontalAlignment(SwingConstants.CENTER);

        guiPainting.setPreferredSize(new Dimension(80,guiPainting.getPreferredSize().height));
        guiPainting.setString(MessageBundle.getString("map_painting"));

        guiTemporal.setToolTipText(MessageBundle.getString("map_temporal_slider"));
        guiTemporal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paneTemp.setVisible(guiTemporal.isSelected());
            }
        });

        guiElevation.setToolTipText(MessageBundle.getString("map_elevation_slider"));
        guiElevation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paneElev.setVisible(guiElevation.isSelected());
            }
        });


        paneTemp.setVisible(false);
        guiTimeLine.setPreferredSize(new Dimension(100, 100));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;

        bottom.add(guiElevation);
        bottom.add(guiTemporal);
        bottom.add(guiPainting);
        bottom.add(guiStatefull,constraints);
        bottom.add(guiAxis,constraints);


        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1.0;
        bottom.add(guiCoord,constraints);

        constraints.weightx = 0.5;
        bottom.add(guiCombo,constraints);

        constraints.weightx = 0.0;
        bottom.add(guiCRS,constraints);

        setMap(candidate);
    }

    public Map2D getMap() {
        return map;
    }

    public void setMap(Map2D map) {
        guiCombo.setMap(map);
        guiTimeLine.setMap(map);
        guiAnimatingPane.setMap(map);
        
        if(this.map != null){

            Container container = (Container) map.getComponent();
            container.remove(paneElev);

            this.map.getComponent().removeMouseMotionListener(listener);
            this.map.getCanvas().removeCanvasListener(listener);
        }
        
        this.map = map;
        guiCRS.setMap(this.map);
        
        if(this.map != null){
            this.map.getComponent().addMouseMotionListener(listener);
            this.map.getCanvas().addCanvasListener(listener);
            map.getCanvas().addPropertyChangeListener(J2DCanvas.OBJECTIVE_CRS_PROPERTY, listener);

            Container container = (Container) map.getComponent();
            container.add(BorderLayout.WEST, paneElev);
            container.repaint();

            CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();
            guiCRS.setText(crs.getName().toString());

        }
        
        guiCRS.setEnabled(this.map != null);
    }

    public void setScales(List<Number> scales){
        guiCombo.setScales(scales);
    }

    public List<Number> getScales(){
        return guiCombo.getScales();
    }

    public void setStepSize(Number step){
        guiCombo.setStepSize(step);
    }

    public Number getStepSize(){
        return guiCombo.getStepSize();
    }

    private class myListener extends MouseMotionAdapter implements PropertyChangeListener,CanvasListener{

        @Override
        public void mouseMoved(MouseEvent e) {
            update(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            update(e);
        }

        private void update(MouseEvent event){
            
            Point2D coord = new DirectPosition2D();
            try {
                coord = map.getCanvas().getController().getTransform().inverseTransform(event.getPoint(), coord);
            } catch (NoninvertibleTransformException ex) {
                guiCoord.setText(error);
                return;
            }

            CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();
            
            StringBuilder sb = new StringBuilder("  ");
            sb.append(crs.getCoordinateSystem().getAxis(0).getAbbreviation());
            sb.append(" : ");
            sb.append(NUMBER_FORMAT.format(coord.getX()));
            sb.append("   ");
            sb.append(crs.getCoordinateSystem().getAxis(1).getAbbreviation());
            sb.append(" : ");
            sb.append(NUMBER_FORMAT.format(coord.getY()));
            guiCoord.setText(sb.toString());
        }

        @Override
        public void propertyChange(PropertyChangeEvent arg0) {
            CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();
            guiCRS.setText(crs.getName().toString());
        }

        @Override
        public void canvasChanged(CanvasEvent event) {

            if(RenderingState.ON_HOLD.equals(event.getNewRenderingstate())){
                guiPainting.setStringPainted(false);
                guiPainting.setIndeterminate(false);
            }else if(RenderingState.RENDERING.equals(event.getNewRenderingstate())){
                guiPainting.setStringPainted(true);
                guiPainting.setIndeterminate(true);
            }else{
                guiPainting.setStringPainted(false);
                guiPainting.setIndeterminate(false);
            }
        }

    }
    
}
