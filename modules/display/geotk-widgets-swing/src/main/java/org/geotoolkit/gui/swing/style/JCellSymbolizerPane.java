/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.style;

import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display2d.ext.cellular.CellSymbolizer;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import static org.geotoolkit.gui.swing.style.StyleElementEditor.getStyleFactory;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.RandomStyleBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.openide.util.Exceptions;

/**
 * Cell Symbolizer editor.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JCellSymbolizerPane extends StyleElementEditor<CellSymbolizer> implements PropertyPane {
    
    private MapLayer layer = null;
    private StyleElementEditor editor = null;
    private MapLayer cellMimicLayer = null;
    private final TreeSelectionListener listener = new TreeSelectionListener() {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            final TreePath path = e.getNewLeadSelectionPath();

            //we validate the previous edition pane
            applyEditor(e.getOldLeadSelectionPath());

            pan_info.removeAll();
            
            if (path != null) {
                final Object val = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                editor = StyleElementEditor.findEditor(val);
                if(editor != null){
                    editor.setLayer(cellMimicLayer);
                    editor.parse(val);
                    pan_info.add(editor);
                }                
            }
            
            pan_info.revalidate();
            pan_info.repaint();
        }
    };
    
    /**
     * Creates new form JCellSymbolizerPane
     */
    public JCellSymbolizerPane() {
        super(CellSymbolizer.class);
        initComponents();
        guiTree.addTreeSelectionListener(listener);
    }

    @Override
    public boolean canHandle(Object target) {
        return target instanceof CoverageMapLayer || target instanceof CellSymbolizer;
    }
    
    @Override
    public ImageIcon getIcon() {
        return IconBundle.getIcon("16_classification_cell");
    }
    
    @Override
    public Image getPreview() {
        return null;
    }

    @Override
    public String getTitle() {
        return MessageBundle.getString("style.cellsymbolizer.tooltip");
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
    public void setTarget(Object candidate) {
        cellMimicLayer = null;
        parse(null);
        if (candidate instanceof CoverageMapLayer) {
            setLayer((CoverageMapLayer) candidate);
            try {
                final SimpleFeatureType sft = CellSymbolizer.buildCellType((CoverageMapLayer)this.layer);
                cellMimicLayer = MapBuilder.createFeatureLayer(FeatureStoreUtilities.collection("", sft), getStyleFactory().style());
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(),ex);
            }
            //search for a cell symbolizer
            for(FeatureTypeStyle fts : this.layer.getStyle().featureTypeStyles()){
                for(Rule r : fts.rules()){
                    for(Symbolizer s : r.symbolizers()){
                        if(s instanceof CellSymbolizer){
                            parse((CellSymbolizer)s);
                            return;
                        }
                    }
                }
            }
        }else if(candidate instanceof CellSymbolizer){
            parse((CellSymbolizer)candidate);
        }
    }

    @Override
    public void reset() {
        parse(null);
    }

    @Override
    public String getToolTip() {
        return "";
    }

    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    public void apply() {
        applyEditor(guiTree.getSelectionModel().getSelectionPath());

        if(layer!=null){
            final CellSymbolizer cs = create();
            final MutableStyle style = layer.getStyle();
            style.featureTypeStyles().clear();
            style.featureTypeStyles().add(getStyleFactory().featureTypeStyle(cs));
        }
    }
    
    @Override
    public void parse(CellSymbolizer target) {
        guiCellSize.setValue(20);
        guiTree.setStyleElement(getStyleFactory().featureTypeStyle(RandomStyleBuilder.createRandomPointSymbolizer()));
        if(target!=null){
            guiCellSize.setValue(target.getCellSize());
            final MutableFeatureTypeStyle fts = getStyleFactory().featureTypeStyle();
            fts.rules().addAll((List)target.getRules());
            guiTree.setStyleElement(fts);
        }
    }

    @Override
    public CellSymbolizer create() {
        final List<Rule> rules = new ArrayList<Rule>();
        final FeatureTypeStyle fts = (FeatureTypeStyle) guiTree.getStyleElement();
        if(fts!=null){
            rules.addAll(fts.rules());
        }
        final CellSymbolizer cs = new CellSymbolizer((Integer)guiCellSize.getValue(), rules);
        return cs;
    }
    
    private void applyEditor(final TreePath oldPath){
        if(editor == null) return;

        //create implies a call to apply if a style element is present
        final Object obj = editor.create();
        editor.parse(obj);
        
        if(obj instanceof Symbolizer){
            //in case of a symbolizer we must update it.
            if(oldPath != null && oldPath.getLastPathComponent() != null){
                final Symbolizer symbol = (Symbolizer) ((DefaultMutableTreeNode)oldPath.getLastPathComponent()).getUserObject();

                if(!symbol.equals(obj)){
                    //new symbol created is different, update in the rule
                    final MutableRule rule = (MutableRule) ((DefaultMutableTreeNode)oldPath.getParentPath().getLastPathComponent()).getUserObject();

                    final int index = rule.symbolizers().indexOf(symbol);
                    if(index >= 0){
                        rule.symbolizers().set(index, (Symbolizer) obj);
                    }
                }
            }
        }
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        guiCellSize = new javax.swing.JSpinner();
        guiTreePane = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jsp2 = new javax.swing.JScrollPane();
        guiTree = new org.geotoolkit.gui.swing.style.JStyleTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        pan_info = new javax.swing.JPanel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, MessageBundle.getString("style.cellsymbolizer.cellsize")); // NOI18N

        guiCellSize.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(3), Integer.valueOf(3), null, Integer.valueOf(1)));

        guiTreePane.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(220);
        jSplitPane1.setDividerSize(4);

        jsp2.setViewportView(guiTree);

        jSplitPane1.setLeftComponent(jsp2);

        pan_info.setLayout(new java.awt.GridLayout(1, 1));
        jScrollPane1.setViewportView(pan_info);

        jSplitPane1.setRightComponent(jScrollPane1);

        guiTreePane.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiCellSize, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(241, Short.MAX_VALUE))
            .addComponent(guiTreePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(guiCellSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiTreePane, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner guiCellSize;
    private org.geotoolkit.gui.swing.style.JStyleTree guiTree;
    private javax.swing.JPanel guiTreePane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane jsp2;
    private javax.swing.JPanel pan_info;
    // End of variables declaration//GEN-END:variables

    
    
}
