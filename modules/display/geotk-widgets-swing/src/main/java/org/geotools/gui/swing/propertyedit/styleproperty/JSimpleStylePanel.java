/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gui.swing.propertyedit.styleproperty;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.geotools.gui.swing.resource.IconBundle;
import org.geotools.gui.swing.propertyedit.PropertyPane;
import org.geotools.gui.swing.style.JLineSymbolizerPane;
import org.geotools.gui.swing.style.JPointSymbolizerPane;
import org.geotools.gui.swing.style.JPolygonSymbolizerPane;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import org.geotools.gui.swing.resource.MessageBundle;
import org.geotools.gui.swing.style.JRasterSymbolizerPane;
import org.geotools.gui.swing.style.JTextSymbolizerPane;
import org.geotools.gui.swing.style.StyleElementEditor;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 * Simple style panel
 * 
 * @author  Johann Sorel
 */
public class JSimpleStylePanel extends JTabbedPane implements PropertyPane {

    private final JPanel stylePane = new JPanel(new BorderLayout());
    private final JPanel labelPane = new JPanel(new BorderLayout());
    private final JToolBar topLabelBar = new JToolBar();
    private final JCheckBox check = new JCheckBox();
    private final JTextSymbolizerPane textSymbolPane = new JTextSymbolizerPane();
    private MapLayer layer;
    private StyleElementEditor<? extends Symbolizer> detail = null;

    /** Creates new form XMLStylePanel */
    public JSimpleStylePanel() {
        check.setSelected(false);
        check.setText(MessageBundle.getString("property_style_label_enable"));
        check.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(check.isSelected()){
                    labelPane.add(BorderLayout.CENTER,new JScrollPane(textSymbolPane));
                }else{
                    labelPane.removeAll();
                    labelPane.add(BorderLayout.NORTH,topLabelBar);
                }
                labelPane.revalidate();
                labelPane.repaint();
    }
        });

        topLabelBar.setFloatable(false);
        topLabelBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        topLabelBar.add(check);

        labelPane.add(BorderLayout.NORTH,topLabelBar);

        add(MessageBundle.getString("property_style_style"), stylePane);
        add(MessageBundle.getString("property_style_label"), labelPane);
        setSelectedComponent(stylePane);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void apply() {
        if(detail != null && layer != null){
            MutableStyleFactory SF = new DefaultStyleFactory();

            if(check.isSelected()){
                Symbolizer symbol = detail.create();
                Symbolizer label = textSymbolPane.create();                
                layer.setStyle(SF.style(new Symbolizer[]{symbol,label}));
            }else{
                layer.setStyle(SF.style(detail.create()));
            }
            
        }
    }

    @Override
    public ImageIcon getIcon() {
        return IconBundle.getInstance().getIcon("16_simple_style");
    }

    @Override
    public String getTitle() {
        return MessageBundle.getString("property_style_simple");
    }

    @Override
    public void setTarget(Object layer) {
        
        if(layer instanceof MapLayer){
            this.layer = (MapLayer) layer;
            parse();
            }
    }

    private void parse() {
        stylePane.removeAll();

        if (layer != null) {
                
            if(layer instanceof FeatureMapLayer){
                final FeatureMapLayer featureLayer = (FeatureMapLayer) layer;
                final Class val = featureLayer.getFeatureSource().getSchema().getGeometryDescriptor().getType().getBinding();

                if (val.equals(Polygon.class) || val.equals(MultiPolygon.class)) {
                    detail = new JPolygonSymbolizerPane();
                    detail.setLayer(layer);

                    loop:
                    for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                        for(final Rule rule : fts.rules()){
                            for(final Symbolizer symbol : rule.symbolizers()){
                                if(symbol instanceof PolygonSymbolizer){
                                    ((StyleElementEditor<PolygonSymbolizer>)detail).parse((PolygonSymbolizer) symbol);
                                    break loop;
                                }
                            }
                        }
                    }

                    JScrollPane jsp = new JScrollPane(detail);
                    jsp.setBorder(null);
                    jsp.setViewportBorder(null);
                    stylePane.add(BorderLayout.CENTER, jsp );
                } else if (val.equals(MultiLineString.class) || val.equals(LineString.class)) {
                    detail = new JLineSymbolizerPane();
                    detail.setLayer(layer);

                    loop:
                    for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                        for(final Rule rule : fts.rules()){
                            for(final Symbolizer symbol : rule.symbolizers()){
                                if(symbol instanceof LineSymbolizer){
                                    ((StyleElementEditor<LineSymbolizer>)detail).parse((LineSymbolizer) symbol);
                                    break loop;
                                }
                            }
                        }
                    }

                    JScrollPane jsp = new JScrollPane(detail);
                    jsp.setBorder(null);
                    jsp.setViewportBorder(null);
                    stylePane.add(BorderLayout.CENTER, jsp );
                } else if (val.equals(Point.class) || val.equals(MultiPoint.class)) {
                    detail = new JPointSymbolizerPane();
                    detail.setLayer(layer);

                    loop:
                    for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                        for(final Rule rule : fts.rules()){
                            for(final Symbolizer symbol : rule.symbolizers()){
                                if(symbol instanceof PointSymbolizer){
                                    ((StyleElementEditor<PointSymbolizer>)detail).parse((PointSymbolizer) symbol);
                                    break loop;
                                }
                            }
                        }
                    }

                    JScrollPane jsp = new JScrollPane(detail);
                    jsp.setBorder(null);
                    jsp.setViewportBorder(null);
                    stylePane.add(BorderLayout.CENTER, jsp );
                } else {        
                    detail = null;
                    stylePane.add(BorderLayout.CENTER,new JLabel("<b>" + MessageBundle.getString("property_style_unknown_simplestyle") + "</b>"));
                }
                
                
            }else if(layer instanceof CoverageMapLayer){
                detail = new JRasterSymbolizerPane();
                    detail.setLayer(layer);

                loop:
                for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                    for(final Rule rule : fts.rules()){
                        for(final Symbolizer symbol : rule.symbolizers()){
                            if(symbol instanceof RasterSymbolizer){
                                ((StyleElementEditor<RasterSymbolizer>)detail).parse((RasterSymbolizer) symbol);
                                break loop;
                            }
                        }
                    }
                }

                JScrollPane jsp = new JScrollPane(detail);
                    jsp.setBorder(null);
                    jsp.setViewportBorder(null);
                stylePane.add(BorderLayout.CENTER, jsp );
            }
            
        }

        loop:
        for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
            for(final Rule rule : fts.rules()){
                for(final Symbolizer symbol : rule.symbolizers()){
                    if(symbol instanceof TextSymbolizer){
                        textSymbolPane.parse((TextSymbolizer) symbol);
                        check.setSelected(true);
                        break loop;
                    }
                }
            }
        }


        stylePane.revalidate();
        stylePane.repaint();
    }

    @Override
    public void reset() {
        parse();
    }

    @Override
    public String getToolTip() {
        return "";
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
