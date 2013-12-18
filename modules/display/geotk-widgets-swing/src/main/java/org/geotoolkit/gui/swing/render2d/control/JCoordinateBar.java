/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2009 - 2013, Geomatys
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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.jdesktop.swingx.JXBusyLabel;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JCoordinateBar extends AbstractMapControlBar {

    private static final ImageIcon ICON_HINT = IconBuilder.createIcon(FontAwesomeIcons.ICON_LIGHTBULB, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_TEMPORAL = IconBuilder.createIcon(FontAwesomeIcons.ICON_TIME, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_ELEVATION = IconBuilder.createIcon(FontAwesomeIcons.ICON_BAR_CHART, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private final myListener listener = new myListener();
    private String error = MessageBundle.getString("map_control_coord_error");

    private final JButton guiHint = new JButton(ICON_HINT);
    private final JScaleCombo guiCombo = new JScaleCombo();
    private final JTextField guiCoord = new JTextField();
    private final JCRSButton guiCRS = new JCRSButton();
    private final JToggleButton guiElevation = new JToggleButton(ICON_ELEVATION);
    private final JToggleButton guiTemporal = new JToggleButton(ICON_TEMPORAL);

    private final JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private final JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final JPanel paneTemp = new JPanel(new BorderLayout());
    private final JPanel paneElev = new JPanel(new BorderLayout());
    private final JAdditionalAxisNavigator guiAdditional = new JAdditionalAxisNavigator();
    private final JMapTimeLine guiTimeLine = new JMapTimeLine();

    public JCoordinateBar() {
        this(null);
    }

    public JCoordinateBar(final JMap2D candidate) {

        setLayout(new BorderLayout(0,1));
        final JToolBar bottom = new JToolBar();
        bottom.setFloatable(false);
        bottom.setLayout(new GridBagLayout());
        add(BorderLayout.SOUTH,bottom);

        paneTemp.add(BorderLayout.CENTER,guiTimeLine);
        paneElev.add(BorderLayout.CENTER,guiAdditional);
        paneTemp.setPreferredSize(new Dimension(120, 120));

        //the hints menu -------------------------------------------------------
        final JCheckBoxMenuItem guiAxis = new JCheckBoxMenuItem(MessageBundle.getString("map_xy_ratio")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return map.getCanvas().getAxisProportions() == 1;
            }
        };
        guiAxis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    double d = map.getCanvas().getAxisProportions();
                    map.getCanvas().setAxisProportions((d == 1) ? Double.NaN : 1);
                }
            }
        });

        final JCheckBoxMenuItem guiStyleOrder = new JCheckBoxMenuItem(MessageBundle.getString("map_style_order")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return GO2Hints.SYMBOL_RENDERING_PRIME.equals(
                        map.getCanvas().getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER));
            }
        };
        guiStyleOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    final Object val = map.getCanvas().getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER);
                    map.getCanvas().setRenderingHint(
                        GO2Hints.KEY_SYMBOL_RENDERING_ORDER, (GO2Hints.SYMBOL_RENDERING_PRIME.equals(val))?
                        GO2Hints.SYMBOL_RENDERING_SECOND : GO2Hints.SYMBOL_RENDERING_PRIME);
                }
            }
        });

        final JCheckBoxMenuItem guiAntiAliasing = new JCheckBoxMenuItem(MessageBundle.getString("antialiasing")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return RenderingHints.VALUE_ANTIALIAS_ON.equals(
                        map.getCanvas().getRenderingHint(RenderingHints.KEY_ANTIALIASING));
            }
        };
        guiAntiAliasing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    final Object val = map.getCanvas().getRenderingHint(RenderingHints.KEY_ANTIALIASING);
                    map.getCanvas().setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, (RenderingHints.VALUE_ANTIALIAS_ON.equals(val))?
                        RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON);
                }
            }
        });

        final ButtonGroup group = new ButtonGroup();
        final JRadioButtonMenuItem guiNone = new JRadioButtonMenuItem(MessageBundle.getString("interpolation_none")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.equals(
                        map.getCanvas().getRenderingHint(RenderingHints.KEY_INTERPOLATION));
            }
        };
        final JRadioButtonMenuItem guiLinear = new JRadioButtonMenuItem(MessageBundle.getString("interpolation_linear")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return RenderingHints.VALUE_INTERPOLATION_BILINEAR.equals(
                        map.getCanvas().getRenderingHint(RenderingHints.KEY_INTERPOLATION));
            }
        };
        final JRadioButtonMenuItem guiBicubic = new JRadioButtonMenuItem(MessageBundle.getString("interpolation_bicubic")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return RenderingHints.VALUE_INTERPOLATION_BICUBIC.equals(
                        map.getCanvas().getRenderingHint(RenderingHints.KEY_INTERPOLATION));
            }
        };
        group.add(guiNone);
        group.add(guiLinear);
        group.add(guiBicubic);
        final ActionListener interListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(map != null){
                    final Object source = e.getSource();
                    if(source == guiNone){
                        map.getCanvas().setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    }else if(source == guiLinear){
                        map.getCanvas().setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    }else if(source == guiBicubic){
                        map.getCanvas().setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    }
                }
            }
        };
        guiNone.addActionListener(interListener);
        guiLinear.addActionListener(interListener);
        guiBicubic.addActionListener(interListener);

        final JCheckBoxMenuItem guiMultiThread = new JCheckBoxMenuItem(MessageBundle.getString("multithread")){
            @Override
            public boolean isSelected() {
                if(map == null) return false;
                return GO2Hints.MULTI_THREAD_ON.equals(map.getCanvas().getRenderingHint(GO2Hints.KEY_MULTI_THREAD));
            }
        };
        guiMultiThread.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    final Object val = map.getCanvas().getRenderingHint(GO2Hints.KEY_MULTI_THREAD);
                    map.getCanvas().setRenderingHint(GO2Hints.KEY_MULTI_THREAD, (GO2Hints.MULTI_THREAD_ON.equals(val))?
                        GO2Hints.MULTI_THREAD_OFF : GO2Hints.MULTI_THREAD_ON);
                }
            }
        });


        final JPopupMenu guiHintMenu = new JPopupMenu();
        guiHintMenu.add(guiAxis);
        guiHintMenu.add(guiMultiThread);
        guiHintMenu.add(guiStyleOrder);
        guiHintMenu.add(guiAntiAliasing);
        guiHintMenu.add(new JSeparator());
        guiHintMenu.add(new JMenuItem(MessageBundle.getString("interpolation")));
        guiHintMenu.add(guiNone);
        guiHintMenu.add(guiLinear);
        guiHintMenu.add(guiBicubic);

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

        final int defaultInsetTop = guiElevation.getMargin().top;
        final int defaultInsetBottom = guiElevation.getMargin().bottom;

        guiHint.setMargin(new Insets(defaultInsetTop, 0, defaultInsetBottom, 0));

        guiTemporal.setMargin(new Insets(defaultInsetTop, 0, defaultInsetBottom, 0));
        guiTemporal.setToolTipText(MessageBundle.getString("map_temporal_slider"));
        guiTemporal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paneTemp.setVisible(guiTemporal.isSelected());
                verticalSplit.setDividerLocation(baseMapContainer.getHeight()-paneTemp.getPreferredSize().height);
            }
        });

        guiElevation.setMargin(new Insets(defaultInsetTop, 0, defaultInsetBottom, 0));
        guiElevation.setToolTipText(MessageBundle.getString("map_elevation_slider"));
        guiElevation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paneElev.setVisible(guiElevation.isSelected());
                horizontalSplit.setDividerLocation(paneElev.getPreferredSize().width);
            }
        });


        guiTimeLine.setPreferredSize(new Dimension(100, 100));

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
        bottom.add(guiHint,constraints);
        constraints.gridx = x++;
        bottom.add(guiElevation,constraints);
        constraints.gridx = x++;
        bottom.add(guiTemporal,constraints);


        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.gridx = x++;
        bottom.add(guiCoord,constraints);

        constraints.weightx = 0;
        constraints.gridx = x++;
        bottom.add(guiCombo,constraints);

        constraints.weightx = 0;
        constraints.gridx = x++;
        bottom.add(guiCRS,constraints);

        setMap(candidate);

        paneElev.setVisible(false);
        paneTemp.setVisible(false);
        horizontalSplit.setDividerSize(2);
        verticalSplit.setDividerSize(2);
        horizontalSplit.setLeftComponent(paneElev);
        verticalSplit.setTopComponent(horizontalSplit);
        verticalSplit.setBottomComponent(paneTemp);
    }

    private Container baseMapContainer;
    private Component baseMapComponent;

    @Override
     public void setMap(final JMap2D map) {
         super.setMap(map);
         guiCombo.setMap(map);
         guiTimeLine.setMap(map);
         guiAdditional.setMap(map);

        if(baseMapContainer != null){
            horizontalSplit.remove(baseMapComponent);
            baseMapContainer.remove(verticalSplit);
            baseMapContainer.add(BorderLayout.CENTER, baseMapComponent);
            baseMapComponent.removeMouseMotionListener(listener);
            this.map.getCanvas().removePropertyChangeListener(listener);
        }

         this.map = map;
         guiCRS.setMap(this.map);

         if(this.map != null){
            baseMapContainer = map.getUIContainer();
            baseMapComponent = map.getComponent(0);
            baseMapComponent.addMouseMotionListener(listener);
            this.map.getCanvas().addPropertyChangeListener(listener);
            map.getCanvas().addPropertyChangeListener(listener);

            baseMapContainer.remove(baseMapComponent);


            horizontalSplit.setRightComponent(baseMapComponent);
            //multiSplitPane.setDividerSize(2);
            baseMapContainer.add(BorderLayout.CENTER, verticalSplit);
            baseMapContainer.repaint();

            final CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();
            guiCRS.setText(crs.getName().toString());
        }

        guiCRS.setEnabled(this.map != null);
     }

    public void setScales(final List<Number> scales){
        guiCombo.setScales(scales);
    }

    public List<Number> getScales(){
        return guiCombo.getScales();
    }

    public void setStepSize(final Number step){
        guiCombo.setStepSize(step);
    }

    public Number getStepSize(){
        return guiCombo.getStepSize();
    }

    private class myListener extends MouseMotionAdapter implements PropertyChangeListener{

        @Override
        public void mouseMoved(final MouseEvent e) {
            update(e);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            update(e);
        }

        private void update(final MouseEvent event){

            Point2D coord = new DirectPosition2D();
            try {
                coord = map.getCanvas().getObjectiveToDisplay().inverseTransform(event.getPoint(), coord);
            } catch (NoninvertibleTransformException ex) {
                guiCoord.setText(error);
                return;
            }

            final CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();

            final StringBuilder sb = new StringBuilder("  ");
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
        public void propertyChange(final PropertyChangeEvent arg0) {
            if(J2DCanvas.OBJECTIVE_CRS_KEY.equals(arg0.getPropertyName())){
                CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();
                guiCRS.setText(crs.getName().toString());
            }else if(AbstractCanvas.RENDERSTATE_KEY.equals(arg0.getPropertyName())){
                final Object state = arg0.getNewValue();
                if(AbstractCanvas.ON_HOLD.equals(state)){
                    map.getInformationDecoration().setPaintingIconVisible(false);
                }else if(AbstractCanvas.RENDERING.equals(state)){
                    map.getInformationDecoration().setPaintingIconVisible(true);
                }else{
                    map.getInformationDecoration().setPaintingIconVisible(false);
                }
            }
        }

    }

    private static ImageIcon addHorizontalMargin(final ImageIcon icon, final int margin){
        final Image img = icon.getImage();
        BufferedImage buffer = new BufferedImage(img.getWidth(null)+2*margin,img.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        buffer.getGraphics().drawImage(img, margin, 0, null);
        return new ImageIcon(buffer);
    }

}
