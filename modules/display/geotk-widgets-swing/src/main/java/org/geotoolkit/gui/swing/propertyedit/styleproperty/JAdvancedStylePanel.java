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
package org.geotoolkit.gui.swing.propertyedit.styleproperty;

import java.awt.Component;
import java.awt.GridLayout;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.geotoolkit.gui.swing.resource.MessageBundle;
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
 * @module pending
 */
public class JAdvancedStylePanel extends StyleElementEditor<MutableStyle> implements PropertyPane {

    private final WeakHashMap<Class,StyleElementEditor> guiPanels = new WeakHashMap<Class, StyleElementEditor>();

    private MapLayer layer = null;
    private MutableStyle style = null;
    private StyleElementEditor editor = null;
    private final TreeSelectionListener listener = new TreeSelectionListener() {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            final TreePath path = e.getNewLeadSelectionPath();

            //we validate the previous edition pane
            if (editor != null) {
                Object obj = editor.create();
                if(obj instanceof Symbolizer){
                    final TreePath oldPath = e.getOldLeadSelectionPath();
                    if(oldPath != null && oldPath.getLastPathComponent() != null){
                        final Symbolizer symbol = (Symbolizer) ((DefaultMutableTreeNode)oldPath.getLastPathComponent()).getUserObject();
                        final MutableRule rule = (MutableRule) ((DefaultMutableTreeNode)oldPath.getParentPath().getLastPathComponent()).getUserObject();

                        final int index =rule.symbolizers().indexOf(symbol);
                        if(index >=0){
                            rule.symbolizers().remove(symbol);
                            rule.symbolizers().add(index,(Symbolizer) obj);
                        }
                    }

                }else{
                    editor.apply();
                }

            }
           
            if (path != null) {
                final Object val = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                pan_info.removeAll();

                if (val instanceof MutableStyle) {
                    editor = getPanel(JStylePane.class);
                } else if (val instanceof MutableFeatureTypeStyle) {
                    editor = getPanel(JFeatureTypeStylePane.class);
                } else if (val instanceof MutableRule) {
                    editor = getPanel(JRulePane.class);
                } else if (val instanceof RasterSymbolizer) {
                    editor = getPanel(JRasterSymbolizerPane.class);
                } else if (val instanceof PolygonSymbolizer) {
                    editor = getPanel(JPolygonSymbolizerPane.class);
                } else if (val instanceof LineSymbolizer) {
                    editor = getPanel(JLineSymbolizerPane.class);
                } else if (val instanceof PointSymbolizer) {
                    editor = getPanel(JPointSymbolizerPane.class);
                } else if (val instanceof TextSymbolizer) {
                    editor = getPanel(JTextSymbolizerPane.class);
                } else {
                    editor = null;
                }

                if(editor != null){
                    editor.parse(val);
                    pan_info.add(editor);
                }

                pan_info.revalidate();
                pan_info.repaint();
            }
        }
    };

    /** Creates new form JAdvancedStylePanel */
    public JAdvancedStylePanel() {
        initComponents();
        tree.addTreeSelectionListener(listener);
    }

    private StyleElementEditor getPanel(Class clazz){
        StyleElementEditor val = guiPanels.get(clazz);
        if(val == null){
            try {
                val = (StyleElementEditor) clazz.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(JAdvancedStylePanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(JAdvancedStylePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            guiPanels.put(clazz, val);
        }
        val.setLayer(getLayer());
        return val;
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
            final Object obj = editor.create();
            if(obj instanceof Symbolizer){
                final TreePath oldPath = tree.getSelectionModel().getSelectionPath();
                if(oldPath != null){
                    final Symbolizer symbol = (Symbolizer) ((DefaultMutableTreeNode)oldPath.getLastPathComponent()).getUserObject();
                    final MutableRule rule = (MutableRule) ((DefaultMutableTreeNode)oldPath.getParentPath().getLastPathComponent()).getUserObject();

                    final int index =rule.symbolizers().indexOf(symbol);
                    if(index >=0){
                        rule.symbolizers().remove(symbol);
                        rule.symbolizers().add(index,(Symbolizer) obj);
                    }
                }

            }else{
                editor.apply();
            }
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
        return MessageBundle.getString("advanced");
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
