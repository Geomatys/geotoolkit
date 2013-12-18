/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2012 - 2013, Geomatys
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
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.Unit;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.FeatureMapLayer.DimensionDef;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

/**
 * Component that allows to browse data on an additional axis.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JAdditionalAxisNavigator extends JPanel implements ContextListener{

    private class AxisDef{
        private final JPanel pane = new JPanel(new BorderLayout());
        private JButton guiDel = new JButton("-");
        private JMapAxisLine nav;

        public AxisDef(final CoordinateReferenceSystem axis){
            nav = new JMapAxisLine(axis);

            pane.add(BorderLayout.NORTH,guiDel);
            pane.add(BorderLayout.CENTER,nav);

            guiDel.setIcon(IconBuilder.createIcon(FontAwesomeIcons.ICON_TRASH, 16, Color.BLACK));
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

    private final JButton addButton = new JButton(IconBuilder.createIcon(FontAwesomeIcons.ICON_PLUS_SIGN, 16, Color.BLACK));

    public JAdditionalAxisNavigator(){
        super(new BorderLayout());

        addButton.setMargin(new Insets(0, 0, 0, 0));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAxis();
                addButton.setEnabled(false);
            }
        });

        guiAxis.setEditable(false);
        guiAxis.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {

                for (AxisDef tmp : axis) {
                    if (CRS.equalsIgnoreMetadata(guiAxis.getSelectedItem(), tmp.nav.getCrs())) {
                        addButton.setEnabled(false);
                        return;
                    }
                }
                addButton.setEnabled(true);
            }
        });

        updateModel();

        guiAxis.setRenderer(new DefaultListCellRenderer(){

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

        final JPanel addPan = new JPanel(new BorderLayout());
        addPan.add(BorderLayout.CENTER,guiAxis);
        addPan.add(BorderLayout.EAST,addButton);
        add(BorderLayout.NORTH,addPan);
        add(BorderLayout.CENTER,grid);
    }

    private void updateModel() {
        final List<CoordinateReferenceSystem> values = new ArrayList<CoordinateReferenceSystem>();
        values.add(DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
        if (map != null) {
            getDimensions(map.getContainer().getContext(), values);
        }
        guiAxis.setModel(new ListComboBoxModel(values));
    }

    private void getDimensions(final MapItem source, final List<CoordinateReferenceSystem> toFill) {
        if (source == null) {
            return;
        }
        if (source instanceof MapLayer) {
            final MapLayer layer = (MapLayer) source;
            final Envelope bounds = layer.getBounds();
            if (bounds != null && bounds.getCoordinateReferenceSystem() != null) {
                final CoordinateReferenceSystem layerCRS = bounds.getCoordinateReferenceSystem();

                final List<CoordinateReferenceSystem> parts = ReferencingUtilities.decompose(layerCRS);
browseCRS:      for (CoordinateReferenceSystem part : parts) {
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
        if (map != null) {
            final MapContext ctx = map.getContainer().getContext();
            if(ctx != null){
                ctx.addContextListener(new  Weak(this));
            }
        }

        updateModel();
        repaint();
    }

    private void addAxis(){
        final Object obj = guiAxis.getSelectedItem();
        final CoordinateReferenceSystem axi = (CoordinateReferenceSystem) obj;

        final AxisDef def = new AxisDef(axi);
        def.nav.setMap(map);
        def.guiDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (def.nav.getCrs().equals(guiAxis.getSelectedItem())) {
                    addButton.setEnabled(true);
                }
            }
        });

        axis.add(def);
        grid.add(def.pane);
        grid.revalidate();
        grid.repaint();
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
        grid.remove(def.pane);
        grid.revalidate();
        grid.repaint();
    }

    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> event) {
        updateModel();
    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
        updateModel();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

}
