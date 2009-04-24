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
package org.geotoolkit.gui.swing.propertyedit.styleproperty;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.style.JFeatureTypeStylePane;
import org.geotoolkit.gui.swing.style.JLineSymbolizerPane;
import org.geotoolkit.gui.swing.style.JPointSymbolizerPane;
import org.geotoolkit.gui.swing.style.JPolygonSymbolizerPane;
import org.geotoolkit.gui.swing.style.JRasterSymbolizerPane;
import org.geotoolkit.gui.swing.style.JRulePane;
import org.geotoolkit.gui.swing.style.JStylePane;
import org.geotoolkit.gui.swing.style.JStyleTree;
import org.geotoolkit.gui.swing.style.JTextSymbolizerPane;
import org.geotoolkit.gui.swing.style.StyleElementEditor;

import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;

import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JAdvancedStylePanel extends StyleElementEditor<MutableStyle> implements PropertyPane {

    private MapLayer layer = null;
    private MutableStyle style = null;
    private StyleElementEditor editor = null;
    private final TreeSelectionListener listener = new TreeSelectionListener() {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath path = tree.getSelectionModel().getSelectionPath();

            //we validate the previous edition pane
            if (editor != null) {
                editor.apply();
            }

           
            if (path != null) {
                Object val = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

                pan_info.removeAll();

                if (val instanceof MutableStyle) {
                    MutableStyle style = (MutableStyle) val;
                    JStylePane pan = new JStylePane();
                    pan.parse(style);
                    editor = pan;
                    pan_info.add(pan);
                } else if (val instanceof MutableFeatureTypeStyle) {
                    MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) val;
                    JFeatureTypeStylePane pan = new JFeatureTypeStylePane();
                    pan.parse(fts);
                    editor = pan;
                    pan_info.add(pan);
                } else if (val instanceof MutableRule) {
                    MutableRule rule = (MutableRule) val;
                    JRulePane pan = new JRulePane();
                    pan.parse(rule);
                    editor = pan;
                    pan_info.add(pan);
                } else if (val instanceof Symbolizer) {
                    Symbolizer symb = (Symbolizer) val;

                    if (symb instanceof RasterSymbolizer) {
                        JRasterSymbolizerPane p = new JRasterSymbolizerPane();
                        p.setLayer(layer);
                        p.parse((RasterSymbolizer) symb);
                        editor = p;
                    } else if (symb instanceof PolygonSymbolizer) {
                        JPolygonSymbolizerPane p = new JPolygonSymbolizerPane();
                        p.setLayer(layer);
                        p.parse((PolygonSymbolizer) symb);
                        editor = p;
                    } else if (symb instanceof LineSymbolizer) {
                        JLineSymbolizerPane p = new JLineSymbolizerPane();
                        p.setLayer(layer);
                        p.parse((LineSymbolizer) symb);
                        editor = p;
                    } else if (symb instanceof PointSymbolizer) {
                        JPointSymbolizerPane p = new JPointSymbolizerPane();
                        p.setLayer(layer);
                        p.parse((PointSymbolizer) symb);
                        editor = p;
                    } else if (symb instanceof TextSymbolizer) {
                        JTextSymbolizerPane p = new JTextSymbolizerPane();
                        p.setLayer(layer);
                        p.parse((TextSymbolizer) symb);
                        editor = p;
                    } else {
                        editor = null;
                    }

                    if(editor != null){
                        pan_info.add(editor);
                    }
                }

                pan_info.revalidate();

            }
        }
    };

    /** Creates new form JAdvancedStylePanel */
    public JAdvancedStylePanel() {
        initComponents();
        init();
    }
    
    private void init(){
        tree.addTreeSelectionListener(listener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        pan_info = new JPanel();
        jsp2 = new JScrollPane();
        tree = new JStyleTree();

        pan_info.setLayout(new GridLayout(1, 1));
        jScrollPane1.setViewportView(pan_info);

        jsp2.setViewportView(tree);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jsp2, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
            .addComponent(jsp2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    @Override
    public void apply() {
        if (editor != null) {
            editor.apply();
        }

        style = tree.getStyle();

        if (layer != null) {
            layer.setStyle(style);
        }
    }

    @Override
    public void setLayer(MapLayer layer) {
        this.layer = layer;
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    @Override
    public void parse(MutableStyle style) {
        this.style = style;
        parse();
    }

    @Override
    public MutableStyle create() {
        style = tree.getStyle();
        apply();
        return style;
    }

    private void parse() {
        tree.setStyle(style);
    }

    @Override
    public ImageIcon getIcon() {
        return IconBundle.getInstance().getIcon("16_advanced_style");
    }

    @Override
    public String getTitle() {
        return "advanced";
    }

    @Override
    public void setTarget(Object layer) {

        if (layer instanceof MapLayer) {
            setLayer((MapLayer) layer);
            parse(this.layer.getStyle());
        }
    }

    @Override
    public void reset() {
        parse();
    }

    @Override
    public String getToolTip() {
        return "";
    }

    @Override
    public Component getComponent() {
        return this;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    JScrollPane jScrollPane1;
    JScrollPane jsp2;
    JPanel pan_info;
    JStyleTree tree;
    // End of variables declaration//GEN-END:variables

}
