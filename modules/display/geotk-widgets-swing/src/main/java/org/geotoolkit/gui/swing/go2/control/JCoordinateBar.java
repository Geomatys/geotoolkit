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
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import javax.swing.SwingConstants;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.gui.swing.go2.GoMap2D;
import org.geotoolkit.gui.swing.go2.J2DMapVolatile;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JCoordinateBar extends JToolBar {

    private static final ImageIcon ICON_XY = IconBundle.getInstance().getIcon("16_xy");
    private static final ImageIcon ICON_XY_DISABLE = IconBundle.getInstance().getIcon("16_xy_disable");
    private static final ImageIcon ICON_STATEFULL = IconBundle.getInstance().getIcon("16_statefull");
    private static final ImageIcon ICON_STATEFULL_DISABLE = IconBundle.getInstance().getIcon("16_statefull_disable");

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    private final myListener listener = new myListener();
    private GoMap2D map = null;
    private String error = MessageBundle.getString("map_control_coord_error");

    private final JCheckBox guiAxis = new JCheckBox();
    private final JTextField guiCoord = new JTextField();
    private final JCheckBox guiStatefull = new JCheckBox();
    private final JCRSButton gui_crsButton = new JCRSButton();

    public JCoordinateBar() {
        this(null);
    }

    public JCoordinateBar(GoMap2D candidate) {
        setLayout(new GridBagLayout());

        guiAxis.setSelected(true);
        guiAxis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null){
                    map.getCanvas().getController().setAxisProportions((!guiAxis.isSelected()) ? Double.NaN : 1);
                }
            }
        });

        guiStatefull.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if(map != null && map instanceof J2DMapVolatile){
                    ((J2DMapVolatile)map).setStatefull(guiStatefull.isSelected());
                }
            }
        });

        gui_crsButton.setEnabled(false);
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

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 0.0;

        add(guiStatefull,constraints);
        add(guiAxis,constraints);

        //a an empty component to fill space, like glue
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;
        JComponent glue = new JComponent() {};
        glue.setOpaque(false);
        add(glue,constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1.0;
        add(guiCoord,constraints);
        constraints.weightx = 0.0;
        add(gui_crsButton,constraints);

        setMap(candidate);
    }

    public GoMap2D getMap() {
        return map;
    }

    public void setMap(GoMap2D map) {
        
        if(this.map != null){
            this.map.getComponent().removeMouseMotionListener(listener);
        }
        
        this.map = map;
        gui_crsButton.setMap(this.map);
        
        if(this.map != null){
            this.map.getComponent().addMouseMotionListener(listener);
            map.getCanvas().addPropertyChangeListener(J2DCanvas.OBJECTIVE_CRS_PROPERTY, listener);

            CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();
            gui_crsButton.setText(crs.getName().toString());
        }
        
        gui_crsButton.setEnabled(this.map != null);
    }
           
    private class myListener extends MouseMotionAdapter implements PropertyChangeListener{

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
            gui_crsButton.setText(crs.getName().toString());
        }

    }
    
}
