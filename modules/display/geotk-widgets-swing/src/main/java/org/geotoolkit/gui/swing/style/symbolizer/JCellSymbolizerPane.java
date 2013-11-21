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
package org.geotoolkit.gui.swing.style.symbolizer;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display2d.ext.cellular.CellSymbolizer;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.filter.JCQLEditor;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 * Cell Symbolizer editor.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JCellSymbolizerPane extends StyleElementEditor<CellSymbolizer> implements PropertyPane {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));

    private MapLayer layer = null;
    private StyleElementEditor editor = null;
    private Filter filter = null;
    private MapLayer cellMimicLayer = null;
    private final ItemListener listener = new ItemListener() {

        @Override
        public void itemStateChanged(ItemEvent e) {
            final String typeName = String.valueOf(e.getItem());

            pan_info.removeAll();

            final Symbolizer symbol;
            if("Text".equalsIgnoreCase(typeName)){
                symbol =  getStyleFactory().textSymbolizer();
            }else{
                symbol =  getStyleFactory().pointSymbolizer();
            }

            setEditedSymbolizer(symbol);
        }
    };

    //keep track of where the symbolizer was to avoid rewriting the complete style
    private MutableRule parentRule = null;
    private int parentIndex = 0;

    /**
     * Creates new form JCellSymbolizerPane
     */
    public JCellSymbolizerPane() {
        super(CellSymbolizer.class);
        initComponents();
        guiTypeList.addItemListener(listener);
    }

    private void setEditedSymbolizer(Symbolizer symbol){

        pan_info.removeAll();

        if(symbol instanceof TextSymbolizer){
            guiTypeList.setSelectedIndex(1);
        }else{
            guiTypeList.setSelectedIndex(0);
        }

        editor = StyleElementEditor.findEditor(symbol);
        if(editor != null){
            editor.setLayer(cellMimicLayer);
            editor.parse(symbol);
            pan_info.add(editor);
        }


        pan_info.revalidate();
        pan_info.repaint();
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
        if (candidate instanceof CoverageMapLayer) {
            setLayer((CoverageMapLayer) candidate);
            try {
                final SimpleFeatureType sft = CellSymbolizer.buildCellType((CoverageMapLayer)this.layer);
                cellMimicLayer = MapBuilder.createFeatureLayer(FeatureStoreUtilities.collection("", sft), getStyleFactory().style());
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(),ex);
            }
            //search for a cell symbolizer
            parentRule = null;
            parentIndex = 0;
            for(MutableFeatureTypeStyle fts : this.layer.getStyle().featureTypeStyles()){
                for(MutableRule r : fts.rules()){
                    for(int i=0,n=r.symbolizers().size();i<n;i++){
                        Symbolizer s = r.symbolizers().get(i);
                        if(s instanceof CellSymbolizer){
                            parse((CellSymbolizer)s);
                            parentRule = r;
                            parentIndex = i;
                            return;
                        }
                    }
                }
            }
            parse(null);
        }else if(candidate instanceof CellSymbolizer){
            parse((CellSymbolizer)candidate);
        }else{
            parse(null);
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
        if(layer!=null){
            final CellSymbolizer symbol = create();

            if(parentRule!=null){
                parentRule.symbolizers().remove(parentIndex);
                parentRule.symbolizers().add(parentIndex,symbol);
            }else{
                //style did not exist, add a new feature type style for it
                final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
                final MutableRule rule = SF.rule(symbol);
                fts.rules().add(rule);
                fts.setDescription(SF.description("raster cell", "raster cell"));
                rule.setDescription(SF.description("raster cell", "raster cell"));
                layer.getStyle().featureTypeStyles().add(fts);
                parentRule = rule;
                parentIndex = 0;
            }
        }
    }

    @Override
    public void parse(CellSymbolizer target) {
        guiCellSize.setValue(20);
        pan_info.removeAll();
        pan_info.revalidate();
        pan_info.repaint();
        editor = null;
        filter = Filter.INCLUDE;
        guiTypeList.setSelectedIndex(0);
        final Symbolizer symbol =  getStyleFactory().pointSymbolizer();
        setEditedSymbolizer(symbol);

        if(target!=null){
            guiCellSize.setValue(target.getCellSize());
            final PointSymbolizer ps = target.getPointSymbolizer();
            final TextSymbolizer ts = target.getTextSymbolizer();
            filter = target.getFilter();

            if(ps!=null){
                guiTypeList.setSelectedIndex(0);
                setEditedSymbolizer(ps);
            }else if(ts!=null){
                guiTypeList.setSelectedIndex(1);
                setEditedSymbolizer(ts);
            }

        }

        guiCQL.setFilter(filter);
    }

    @Override
    public CellSymbolizer create() {
        try {
            filter = guiCQL.getFilter();
        } catch (CQLException ex) {
            LOGGER.log(Level.INFO, ex.getMessage(), ex);
        }
        PointSymbolizer ps = null;
        TextSymbolizer ts = null;

        if(editor!=null){
            Object c = editor.create();
            if(c instanceof PointSymbolizer){
                ps = (PointSymbolizer) c;
            }else{
                ts = (TextSymbolizer) c;
            }
        }

        return new CellSymbolizer((Integer)guiCellSize.getValue(), filter, ps, ts);
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
        jLabel2 = new javax.swing.JLabel();
        guiTypeList = new javax.swing.JComboBox();
        guiCQL = new org.geotoolkit.cql.JCQLTextPane();
        but_edit = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        pan_info = new javax.swing.JPanel();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, MessageBundle.getString("style.cellsymbolizer.cellsize")); // NOI18N

        guiCellSize.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(3), Integer.valueOf(3), null, Integer.valueOf(1)));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, MessageBundle.getString("style.cellsymbolizer.type")); // NOI18N

        guiTypeList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Point", "Text" }));

        org.openide.awt.Mnemonics.setLocalizedText(but_edit, MessageBundle.getString("style.cellsymbolizer.edit")); // NOI18N
        but_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                but_editActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, MessageBundle.getString("style.cellsymbolizer.filter")); // NOI18N

        pan_info.setLayout(new java.awt.BorderLayout());
        jScrollPane1.setViewportView(pan_info);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiCellSize, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(but_edit)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiCQL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {but_edit, jLabel1, jLabel3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(guiCellSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(guiTypeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(but_edit))
                    .addComponent(guiCQL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void but_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_but_editActionPerformed

        try {
            filter = JCQLEditor.showDialog(this, cellMimicLayer, filter);
            guiCQL.setFilter(filter);
        } catch (CQLException ex) {
            LOGGER.log(Level.INFO, ex.getMessage(), ex);
        }

    }//GEN-LAST:event_but_editActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton but_edit;
    private org.geotoolkit.cql.JCQLTextPane guiCQL;
    private javax.swing.JSpinner guiCellSize;
    private javax.swing.JComboBox guiTypeList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel pan_info;
    // End of variables declaration//GEN-END:variables



}
