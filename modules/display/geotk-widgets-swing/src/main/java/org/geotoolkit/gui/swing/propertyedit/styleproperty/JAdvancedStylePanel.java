/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Johann Sorel
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
public class JAdvancedStylePanel<T extends Object> extends StyleElementEditor<T> implements PropertyPane {

    private final WeakHashMap<Class,StyleElementEditor> guiPanels = new WeakHashMap<Class, StyleElementEditor>();

    private MapLayer layer = null;
    private T style = null;
    private StyleElementEditor editor = null;
    private final TreeSelectionListener listener = new TreeSelectionListener() {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            final TreePath path = e.getNewLeadSelectionPath();

            //we validate the previous edition pane
            applyEditor(e.getOldLeadSelectionPath());

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
                Logger.getLogger(JAdvancedStylePanel.class.getName()).log(Level.WARNING, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(JAdvancedStylePanel.class.getName()).log(Level.WARNING, null, ex);
            }
            guiPanels.put(clazz, val);
        }
        val.setLayer(getLayer());
        return val;
    }

    private void applyEditor(TreePath oldPath){
        if(editor == null) return;

        //create implies a call to apply if a style element is present
        final Object obj = editor.create();
        
        if(obj instanceof Symbolizer){
            //in case of a symbolizer we must update it.
            if(oldPath != null && oldPath.getLastPathComponent() != null){
                final Symbolizer symbol = (Symbolizer) ((DefaultMutableTreeNode)oldPath.getLastPathComponent()).getUserObject();

                if(!symbol.equals(obj)){
                    //new symbol created is different, update in the rule
                    final MutableRule rule = (MutableRule) ((DefaultMutableTreeNode)oldPath.getParentPath().getLastPathComponent()).getUserObject();

                    final int index =rule.symbolizers().indexOf(symbol);
                    if(index >=0){
                        rule.symbolizers().remove(symbol);
                        rule.symbolizers().add(index,(Symbolizer) obj);
                    }
                }
            }
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new JSplitPane();
        jsp2 = new JScrollPane();
        tree = new JStyleTree();
        jScrollPane1 = new JScrollPane();
        pan_info = new JPanel();

        setLayout(new BorderLayout());

        jSplitPane1.setDividerLocation(220);
        jSplitPane1.setDividerSize(4);

        jsp2.setViewportView(tree);

        jSplitPane1.setLeftComponent(jsp2);

        pan_info.setLayout(new GridLayout(1, 1));
        jScrollPane1.setViewportView(pan_info);

        jSplitPane1.setRightComponent(jScrollPane1);

        add(jSplitPane1, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    @Override
    public void apply() {

        applyEditor(tree.getSelectionModel().getSelectionPath());

        style = (T) tree.getStyleElement();

        if (layer != null && style instanceof MutableStyle) {
            layer.setStyle((MutableStyle)style);
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
    public void parse(T style) {
        this.style = style;
        parse();
    }

    @Override
    public T create() {
        style = (T) tree.getStyleElement();
        apply();
        return style;
    }

    private void parse() {
        tree.setStyleElement(style);
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
            parse((T) this.layer.getStyle());
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
    JSplitPane jSplitPane1;
    JScrollPane jsp2;
    JPanel pan_info;
    JStyleTree tree;
    // End of variables declaration//GEN-END:variables

}
