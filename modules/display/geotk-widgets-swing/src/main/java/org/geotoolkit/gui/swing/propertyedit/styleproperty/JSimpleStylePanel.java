/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 - 2014 Geomatys
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JBankPanel;
import org.geotoolkit.gui.swing.style.JPreview;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.gui.swing.util.ActionCell;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.RuleListener;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.jdesktop.swingx.JXTable;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 * Frame of simple style editor
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JSimpleStylePanel extends StyleElementEditor implements PropertyPane, LayerListener{

    private static final ImageIcon ICO_UP = IconBuilder.createIcon(FontAwesomeIcons.ICON_CHEVRON_UP, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICO_DOWN = IconBuilder.createIcon(FontAwesomeIcons.ICON_CHEVRON_DOWN, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICO_ADD = IconBuilder.createIcon(FontAwesomeIcons.ICON_PLUS, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICO_DELETE = IconBuilder.createIcon(FontAwesomeIcons.ICON_TRASH_O, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private final SymbolizerModel model = new SymbolizerModel(getStyleFactory().rule());

    private MapLayer layer = null;
    private MutableStyle style = null;
    private JBankPanel bankController = new JBankPanel();
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(PROPERTY_UPDATED.equals(evt.getPropertyName())){
                    if (evt.getNewValue() instanceof Symbolizer) {
                        final Symbolizer s = (Symbolizer) evt.getNewValue();
                        guiOverviewLabel.parse(s);
                        final int selecteRow = guiTable.getSelectedRow();
                        if (selecteRow >= 0) {
                            model.rule.symbolizers().set(selecteRow,s);
                        }
                    }
                }
            }
        };
    
    private final LayerListener.Weak layerListener = new LayerListener.Weak(this);
    //current visible editor
    private StyleElementEditor currentEditor = null;

    //keep track of where the rule was to avoid rewriting the complete style
    private MutableRule parentRule = null;
    
    /**
     * Creates new form jStylePane
     */
    public JSimpleStylePanel() {
        super(Object.class);
        initComponents();

        // Set models
        guiTable.setTableHeader(null);
        guiTable.setModel(model);
        guiTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guiTable.setRowMargin(0);
        guiTable.setColumnMargin(0);

        guiTable.getColumn(0).setCellRenderer(new SymbolizerRenderer());

        guiTable.getColumn(1).setCellRenderer(new ActionCell.Renderer(ICO_UP));
        guiTable.getColumn(1).setCellEditor(new ActionCell.Editor(ICO_UP) {
            @Override
            public void actionPerformed(final ActionEvent e, Object value) {
                final Symbolizer symbol = (Symbolizer) value;
                model.moveUp(symbol);
            }
        });

        guiTable.getColumn(2).setCellRenderer(new ActionCell.Renderer(ICO_DOWN));
        guiTable.getColumn(2).setCellEditor(new ActionCell.Editor(ICO_DOWN) {
            @Override
            public void actionPerformed(final ActionEvent e, Object value) {
                final Symbolizer symbol = (Symbolizer) value;
                model.moveDown(symbol);
            }
        });

        guiTable.getColumn(3).setCellRenderer(new ActionCell.Renderer(ICO_DELETE));
        guiTable.getColumn(3).setCellEditor(new ActionCell.Editor(ICO_DELETE) {
            @Override
            public void actionPerformed(final ActionEvent e, Object value) {
                final Symbolizer symbol = (Symbolizer) value;
                model.rule.symbolizers().remove(symbol);
            }
        });

        final int width = 30;
        guiTable.getColumn(1).setMinWidth(width);
        guiTable.getColumn(1).setPreferredWidth(width);
        guiTable.getColumn(1).setMaxWidth(width);
        guiTable.getColumn(2).setMinWidth(width);
        guiTable.getColumn(2).setPreferredWidth(width);
        guiTable.getColumn(2).setMaxWidth(width);
        guiTable.getColumn(3).setMinWidth(width);
        guiTable.getColumn(3).setPreferredWidth(width);
        guiTable.getColumn(3).setMaxWidth(width);
        guiTable.setTableHeader(null);
        guiTable.setRowHeight(30);
        guiTable.setFillsViewportHeight(true);
        guiTable.setBackground(Color.WHITE);
        guiTable.setShowGrid(true);
        guiTable.setShowHorizontalLines(true);
        guiTable.setShowVerticalLines(false);

        guiTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if(evt.getValueIsAdjusting()) return;

                if(currentEditor != null){
                    currentEditor.removePropertyChangeListener(changeListener);
                }

                guiScrollInfo.setViewportView(null);
                guiOverviewLabel.parse(null);

                // Get all selected items
                final int selectetRow = guiTable.getSelectedRow();
                if (selectetRow >= 0 && model.rule.symbolizers().size() > selectetRow) {
                    final Object item = model.rule.symbolizers().get(selectetRow);
                    
                    if (item != null) {
                        currentEditor = StyleElementEditor.findEditor(item);

                        if (currentEditor != null) {
                            currentEditor.setLayer(layer);
                            currentEditor.parse(item);
                            currentEditor.addPropertyChangeListener(changeListener);
                            guiScrollInfo.setViewportView(currentEditor);
                        }

                        guiOverviewLabel.parse(item);
                    }
                }

                guiScrollInfo.revalidate();
                guiScrollInfo.repaint();
            }
        });

        guiOverviewLabel.setMir(true);
        jAddButton.setIcon(ICO_ADD);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new JPanel();
        jSplitPane1 = new JSplitPane();
        jPanel1 = new JPanel();
        jLabel5 = new JLabel();
        jLabel7 = new JLabel();
        guiOverviewLabel = new JPreview();
        jAddButton = new JButton();
        jScrollPane2 = new JScrollPane();
        guiTable = new JXTable();
        jPanel2 = new JPanel();
        jLabel6 = new JLabel();
        guiScrollInfo = new JScrollPane();

        setMinimumSize(new Dimension(820, 400));
        setLayout(new BorderLayout());

        jSplitPane1.setDividerLocation(240);

        jLabel5.setFont(new Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText(MessageBundle.format("symbolLayer")); // NOI18N

        jLabel7.setFont(new Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel7.setText(MessageBundle.format("symbolPreview")); // NOI18N

        GroupLayout guiOverviewLabelLayout = new GroupLayout(guiOverviewLabel);
        guiOverviewLabel.setLayout(guiOverviewLabelLayout);
        guiOverviewLabelLayout.setHorizontalGroup(
            guiOverviewLabelLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        guiOverviewLabelLayout.setVerticalGroup(
            guiOverviewLabelLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 109, Short.MAX_VALUE)
        );

        jAddButton.setText(MessageBundle.format("addSymbol")); // NOI18N
        jAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jAddButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(guiTable);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(jAddButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiOverviewLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, Alignment.LEADING)
                    .addGroup(Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7))
                        .addGap(0, 99, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jAddButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiOverviewLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jLabel6.setFont(new Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel6.setText(MessageBundle.format("editSymbol")); // NOI18N

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(478, Short.MAX_VALUE))
            .addComponent(guiScrollInfo, Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiScrollInfo, GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        add(jPanel3, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public boolean canHandle(Object target) {
        return target instanceof MapLayer;
    }

    @Override
    public void setTarget(Object target) {
        if(target instanceof MapLayer){
            setLayer((MapLayer)target);
            parse(target);
        }
    }

    @Override
    public void apply() {
         if(layer != null){
             if(currentEditor!=null){
                 currentEditor.apply();
             }

            if(parentRule==null){
                layer.setStyle(getStyleFactory().style(model.rule.symbolizers().toArray(new Symbolizer[0])));
            }
            
        }
    }

    @Override
    public void reset() {
        parse(parentRule);
    }

    @Override
    public String getTitle() {
      return "Simple";
    }

    @Override
    public ImageIcon getIcon() {
        return IconBundle.getIcon("16_simple_style");
    }

    @Override
    public Image getPreview() {
        return IconBundle.getIcon("preview_style_simple").getImage();
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
    public void setLayer(final MapLayer layer) {
        this.layer = layer;

        if(this.layer!=null){
            layerListener.unregisterSource(this.layer);
        }        
        this.layer = layer;
        if(this.layer!=null){
            layerListener.registerSource(this.layer);
        }
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    @Override
    public void parse(final Object obj) {
        model.setRule(getStyleFactory().rule());

        parentRule = null;        
        if(layer != null){
            for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                for(final Rule rule : fts.rules()){
                    parse((MutableRule)rule);
                    break; //we only retrieve the first rule.
                }
            }
        }

    }
    
    private void parse(final MutableRule rule) {

        //listen to rule change from other syle editors
        if(parentRule!=rule){
            parentRule = (MutableRule) rule;
            model.setRule(rule);
        }
    }

    @Override
    public Object create() {
        return style;
    }

    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{};
    }
    
    private void jAddButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jAddButtonActionPerformed

        final List<Class> clazz = new ArrayList<Class>();
        clazz.add(Symbolizer.class);
        clazz.add(PointSymbolizer.class);
        clazz.add(LineSymbolizer.class);
        clazz.add(PolygonSymbolizer.class);
        clazz.add(TextSymbolizer.class);
        clazz.add(RasterSymbolizer.class);

        bankController.setClazzList(clazz);

        final int result = JOptionDialog.show(this, bankController,JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            if (bankController.getSelectedSymbol() != null) {                
                model.rule.symbolizers().add( (Symbolizer)bankController.getSelectedSymbol());
                final int index = model.rule.symbolizers().size()-1;
                guiTable.getSelectionModel().setSelectionInterval(index, index);
            }
        }

    }//GEN-LAST:event_jAddButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPreview guiOverviewLabel;
    private JScrollPane guiScrollInfo;
    private JXTable guiTable;
    private JButton jAddButton;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane2;
    private JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

    //style events    
    @Override
    public void styleChange(MapLayer source, EventObject event) {
        //check if the first rule changed
        final MutableStyle ms = this.layer.getStyle();
        if(ms.featureTypeStyles().isEmpty()){
            parse(this.layer.getStyle());
            return;
        }
        final MutableFeatureTypeStyle fts = ms.featureTypeStyles().get(0);
        if(fts.rules().isEmpty()){
            parse(this.layer.getStyle());
            return;
        }
        final MutableRule r = fts.rules().get(0);
        if(parentRule!=r){
            parse(this.layer.getStyle());
            return;
        }
    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(MapLayer.STYLE_PROPERTY.equals(evt.getPropertyName()) && evt.getOldValue()!=evt.getNewValue()){
            parse(this.layer.getStyle());
        }
    }
    
    private static final class SymbolizerModel extends AbstractTableModel implements RuleListener{
        
        private MutableRule rule;
        private final Weak ruleListener = new Weak(this);

        public SymbolizerModel(MutableRule rule) {
            setRule(rule);
        }

        public void setRule(MutableRule rule) {
            ArgumentChecks.ensureNonNull("rule", rule);
            if(this.rule==rule) return;
                        
            if(this.rule!=null){
                this.ruleListener.unregisterSource(this.rule);
            }
            this.rule = rule;
            this.ruleListener.registerSource(rule);
            
            fireTableDataChanged();
        }

        public void moveUp(final Symbolizer s) {
            int index = rule.symbolizers().indexOf(s);
            if (index != 0) {
                rule.symbolizers().remove(s);
                rule.symbolizers().add(index - 1, s);
            }
        }
        
        public void moveDown(final Symbolizer s) {
            int index = rule.symbolizers().indexOf(s);
            if (index != rule.symbolizers().size() - 1) {
                rule.symbolizers().remove(s);
                rule.symbolizers().add(index + 1, s);
            }
        }
        
        @Override
        public int getRowCount() {
            return rule.symbolizers().size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rule.symbolizers().get(rowIndex);
        }

        @Override
        public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
            final int type = event.getType();
            if(type == CollectionChangeEvent.ITEM_ADDED){
                fireTableRowsInserted((int)event.getRange().getMinDouble(), (int)event.getRange().getMaxDouble());
            }else if(type == CollectionChangeEvent.ITEM_REMOVED){
                fireTableRowsDeleted((int)event.getRange().getMinDouble(), (int)event.getRange().getMaxDouble());
            }else if(type == CollectionChangeEvent.ITEM_CHANGED){
                fireTableRowsUpdated((int)event.getRange().getMinDouble(), (int)event.getRange().getMaxDouble());
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }
        
    }
    
    private static class SymbolizerRenderer extends DefaultTableCellRenderer {

        private final JPreview preview = new JPreview();

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column) {

            final JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof Symbolizer) {
                Symbolizer symbol = (Symbolizer) value;
                preview.parse(symbol);
                if (symbol.getName() != null && !symbol.getName().isEmpty()) {
                    lbl.setText(symbol.getName());
                } else if (symbol.getDescription() != null && symbol.getDescription().getTitle() != null && !symbol.getDescription().getTitle().toString().isEmpty()) {
                    lbl.setText(symbol.getDescription().getTitle().toString());
                } else {
                    lbl.setText("Unnamed");
                }
            }

            preview.setSize(lbl.getHeight(), lbl.getHeight());
            lbl.setIcon(new ImageIcon(preview.getImage()));
            return lbl;
        }
    }

}
