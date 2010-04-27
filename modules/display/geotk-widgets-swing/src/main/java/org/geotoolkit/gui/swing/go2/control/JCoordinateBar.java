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
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.gui.swing.BorderCutter;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.Map2D;
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

    private static final ImageIcon ICON_HINT = addHorizontalMargin(IconBundle.getInstance().getIcon("16_hint"),2);
    private static final ImageIcon ICON_TEMPORAL = addHorizontalMargin(IconBundle.getInstance().getIcon("16_temporal"),2);
    private static final ImageIcon ICON_ELEVATION = addHorizontalMargin(IconBundle.getInstance().getIcon("16_elevation"),2);

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private final myListener listener = new myListener();
    private Map2D map = null;
    private String error = MessageBundle.getString("map_control_coord_error");

    private final JButton guiHint = new JButton(ICON_HINT);
    private final JScaleCombo guiCombo = new JScaleCombo();
    private final JTextField guiCoord = new JTextField();
    private final JCRSButton guiCRS = new JCRSButton();
    private final JProgressBar guiPainting = new JProgressBar();
    private final JToggleButton guiElevation = new JToggleButton(ICON_ELEVATION);
    private final JToggleButton guiTemporal = new JToggleButton(ICON_TEMPORAL);
    
    private final JPanel paneTemp = new JPanel(new BorderLayout());
    private final JPanel paneElev = new JPanel(new BorderLayout());
    private final JMapElevationLine guiElevationLine = new JMapElevationLine();
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

        paneTemp.add(BorderLayout.CENTER,guiTimeLine);
        paneElev.add(BorderLayout.CENTER,guiElevationLine);

        //the hints menu -------------------------------------------------------
        final JCheckBoxMenuItem guiAxis = new JCheckBoxMenuItem(MessageBundle.getString("map_xy_ratio")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return map.getCanvas().getController().getAxisProportions() == 1;
            }
        };
        guiAxis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    double d = map.getCanvas().getController().getAxisProportions();
                    map.getCanvas().getController().setAxisProportions((d == 1) ? Double.NaN : 1);
                }
            }
        });

        final JCheckBoxMenuItem guiStatefull = new JCheckBoxMenuItem(MessageBundle.getString("map_statefull")){
            @Override
            public boolean isSelected() {
                if(map != null && map instanceof JMap2D){
                    return ((JMap2D)map).isStatefull();
                }else{
                    return false;
                }
            }
        };
        guiStatefull.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null && map instanceof JMap2D){
                    final JMap2D map2d = ((JMap2D)map);
                    map2d.setStatefull(!map2d.isStatefull());
                }
            }
        });

        final JCheckBoxMenuItem guiStyleOrder = new JCheckBoxMenuItem(MessageBundle.getString("map_style_order")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return GO2Hints.SYMBOL_RENDERING_PRIME.equals(map.getCanvas().getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER));
            }
        };
        guiStyleOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    final Object val = map.getCanvas().getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER);
                    map.getCanvas().setRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER, (GO2Hints.SYMBOL_RENDERING_PRIME.equals(val))?
                        GO2Hints.SYMBOL_RENDERING_SECOND : GO2Hints.SYMBOL_RENDERING_PRIME);
                }
            }
        });

        final JCheckBoxMenuItem guiAntiAliasing = new JCheckBoxMenuItem(MessageBundle.getString("antialiasing")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return RenderingHints.VALUE_ANTIALIAS_ON.equals(map.getCanvas().getRenderingHint(RenderingHints.KEY_ANTIALIASING));
            }
        };
        guiAntiAliasing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    final Object val = map.getCanvas().getRenderingHint(RenderingHints.KEY_ANTIALIASING);
                    map.getCanvas().setRenderingHint(RenderingHints.KEY_ANTIALIASING, (RenderingHints.VALUE_ANTIALIAS_ON.equals(val))?
                        RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON);
                }
            }
        });

        final JPopupMenu guiHintMenu = new JPopupMenu();
        guiHintMenu.add(guiAxis);
        guiHintMenu.add(guiStatefull);
        guiHintMenu.add(guiStyleOrder);
        guiHintMenu.add(new JSeparator());
        guiHintMenu.add(guiAntiAliasing);

        guiHint.setComponentPopupMenu(guiHintMenu);
        guiHint.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(event.getButton() == MouseEvent.BUTTON1){
                    guiHintMenu.setSize(guiHintMenu.getPreferredSize());
                    final Dimension dim = guiHintMenu.getSize();
                    guiHintMenu.show(guiHint.getParent(), guiHint.getX(), guiHint.getY()-dim.height);
                }
            }
            @Override
            public void mousePressed(MouseEvent arg0) {}
            @Override
            public void mouseReleased(MouseEvent arg0) {}
            @Override
            public void mouseEntered(MouseEvent arg0) {}
            @Override
            public void mouseExited(MouseEvent arg0) {}
        });


        guiCRS.setEnabled(false);
        guiAxis.setOpaque(false);

        guiCombo.setOpaque(false);

        guiCoord.setOpaque(false);
        guiCoord.setBorder(null);
        guiCoord.setEditable(false);
        guiCoord.setHorizontalAlignment(SwingConstants.CENTER);

        guiPainting.setPreferredSize(new Dimension(80,5));
        guiPainting.setString("");

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
        paneElev.setVisible(false);
        guiTimeLine.setPreferredSize(new Dimension(100, 100));
        guiElevationLine.setPreferredSize(new Dimension(100, 100));

        int x = 1;

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = 1;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;
        constraints.gridy = 0;

        constraints.gridx = x++;
        bottom.add(BorderCutter.cut(guiHint,false,false,false,true),constraints);
        constraints.gridx = x++;
        bottom.add(BorderCutter.cut(guiElevation,false,false,true,true),constraints);
        constraints.gridx = x++;
        bottom.add(BorderCutter.cut(guiTemporal,false,false,true,true),constraints);
        constraints.gridx = x++;
        constraints.gridx = x++;
        bottom.add(BorderCutter.cut(guiCRS,false,false,true,false),constraints);


        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridheight = GridBagConstraints.RELATIVE;
        constraints.weightx = 1.0;
        constraints.weighty = 0.9;
        constraints.gridx = x;
        bottom.add(guiCoord,constraints);
        constraints.weighty = 0.1;
        constraints.gridy = 1;
        bottom.add(guiPainting,constraints);
        constraints.gridy = 0;
        x++;


        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.5;
        constraints.gridx = x++;
        bottom.add(guiCombo,constraints);

        setMap(candidate);
    }

    public Map2D getMap() {
        return map;
    }

    public void setMap(Map2D map) {
        guiCombo.setMap(map);
        guiTimeLine.setMap(map);
        guiElevationLine.setMap(map);
        
        if(this.map != null){

            final Container container = map.getUIContainer();
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

            final Container container = map.getUIContainer();
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

    private static ImageIcon addHorizontalMargin(ImageIcon icon, int margin){
        final Image img = icon.getImage();
        BufferedImage buffer = new BufferedImage(img.getWidth(null)+2*margin,img.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        buffer.getGraphics().drawImage(img, margin, 0, null);
        return new ImageIcon(buffer);
    }

}
