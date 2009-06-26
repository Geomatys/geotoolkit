/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 Geomatys
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

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotools.data.DefaultQuery;
import org.geotools.feature.FeatureIterator;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.Fill;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JCategoryStylePanel extends JPanel implements PropertyPane{

    private static final List<Palette> PALETTES;

    static{
        PALETTES = new ArrayList<Palette>();
        PALETTES.add(new RandomPalette());
    }

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private final RuleModel model = new RuleModel();
    private final List<PropertyName> properties = new ArrayList<PropertyName>();
    private FeatureMapLayer layer = null;

    public JCategoryStylePanel() {
        initComponents();
        guiTable.setModel(model);
        
        guiProperty.setRenderer(new PropertyRenderer());

        guiPalette.setModel(new ListComboBoxModel(PALETTES));
        guiPalette.setRenderer(new PaletteRenderer());
        guiPalette.setSelectedIndex(0);

        guiTable.getColumnModel().getColumn(0).setCellRenderer(new RuleStyleRenderer());
        guiTable.getColumnModel().getColumn(1).setCellRenderer(new RulePropertyRenderer());
        guiTable.getColumnModel().getColumn(3).setCellEditor(new DeleteEditor());
        guiTable.getColumnModel().getColumn(3).setCellRenderer(new DeleteRenderer());

        guiTable.setShowGrid(false, false);
        guiTable.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping(Color.white, HighlighterFactory.QUICKSILVER, 1)});

        guiTable.getColumnExt(0).setMaxWidth(30);
        guiTable.getColumnExt(3).setMaxWidth(20);
    }

    private void parse(){
        properties.clear();
        if(layer == null){
            model.rules.clear();
        }else{
            for(PropertyDescriptor desc : layer.getFeatureSource().getSchema().getDescriptors()){
                Class<?> type = desc.getType().getBinding();

                if(!Geometry.class.isAssignableFrom(type)){
                    properties.add(FF.property(desc.getName().getLocalPart()));
                }

            }
        }

        guiProperty.setModel(new ListComboBoxModel(properties));
    }

    private Symbolizer createSymbolizer(){
        Stroke stroke = SF.stroke(Color.BLACK, 1);
        Fill fill = SF.fill( ((Palette)guiPalette.getSelectedItem()).next() );
        return SF.polygonSymbolizer(stroke,fill,null);
    }

    private Rule createRule(PropertyName property, Object obj){
        MutableRule r = SF.rule(createSymbolizer());
        r.setFilter(FF.equals(property, FF.literal(obj)));
        r.setDescription(SF.description(obj.toString(), obj.toString()));
        return r;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {






        guiAddOne = new JButton();
        guiRemoveAll = new JButton();
        jPanel1 = new JPanel();
        guiLblProperty = new JLabel();
        guiOther = new JCheckBox();
        guiProperty = new JComboBox();
        guiLblPalette = new JLabel();
        guiLblModel = new JLabel();
        guiModel = new JButton();
        guiGenerate = new JButton();
        guiPalette = new JComboBox();
        jScrollPane1 = new JScrollPane();
        guiTable = new JXTable();

        guiAddOne.setText(MessageBundle.getString("add_value")); // NOI18N
        guiAddOne.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiAddOneActionPerformed(evt);
            }
        });

        guiRemoveAll.setText(MessageBundle.getString("remove_all_values")); // NOI18N
        guiRemoveAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiRemoveAllActionPerformed(evt);
            }
        });

        jPanel1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getString("properties"))); // NOI18N
        guiLblProperty.setText(MessageBundle.getString("property")); // NOI18N
        guiOther.setText(MessageBundle.getString("otherRule")); // NOI18N
        guiLblPalette.setText(MessageBundle.getString("palette")); // NOI18N
        guiLblModel.setText(MessageBundle.getString("model")); // NOI18N
        guiModel.setText(" ");

        guiGenerate.setText(MessageBundle.getString("generate")); // NOI18N
        guiGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiGenerateActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(guiLblPalette)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiPalette, 0, 277, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiLblModel)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiModel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(guiLblProperty)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiProperty, 0, 266, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiOther)))
                        .addContainerGap())
                    .addComponent(guiGenerate, Alignment.TRAILING)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiLblProperty)
                    .addComponent(guiProperty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiOther))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiLblPalette)
                    .addComponent(guiPalette, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblModel)
                    .addComponent(guiModel))
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guiGenerate))
        );

        jScrollPane1.setViewportView(guiTable);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiAddOne)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiRemoveAll)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiAddOne)
                    .addComponent(guiRemoveAll)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiAddOneActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiAddOneActionPerformed
        String val = JOptionPane.showInputDialog(MessageBundle.getString("value")+" :");
        Rule r = createRule((PropertyName) guiProperty.getSelectedItem(), val);

        model.rules.add(r);
        guiTable.revalidate();
        guiTable.repaint();
    }//GEN-LAST:event_guiAddOneActionPerformed

    private void guiRemoveAllActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiRemoveAllActionPerformed
        model.rules.clear();
        guiTable.revalidate();
        guiTable.repaint();
    }//GEN-LAST:event_guiRemoveAllActionPerformed

    private void guiGenerateActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiGenerateActionPerformed

        //search the different values
        final Set<Object> differentValues = new HashSet<Object>();
        final PropertyName property = (PropertyName)guiProperty.getSelectedItem();
        final DefaultQuery query = new DefaultQuery();
        query.setPropertyNames(new String[]{property.getPropertyName()});
        
        FeatureIterator<SimpleFeature> features = null;
        try{
            features = layer.getFeatureSource().getFeatures(query).features();
            while(features.hasNext()){
                SimpleFeature sf = features.next();
                differentValues.add(property.evaluate(sf));
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }finally{
            if(features != null){
                features.close();
            }
        }

        //generate the different rules
        List<Rule> rules = new ArrayList<Rule>();

        for(Object obj : differentValues){
            rules.add(createRule(property, obj));
        }

        //generate the other rule if asked
        if(guiOther.isSelected()){
            MutableRule r = SF.rule(createSymbolizer());
            r.setElseFilter(true);
            r.setDescription(SF.description("other", "other"));
            rules.add(r);
        }

        model.rules.clear();
        model.rules.addAll(rules);

        guiTable.revalidate();
        guiTable.repaint();

    }//GEN-LAST:event_guiGenerateActionPerformed

    @Override
    public void setTarget(Object layer) {
        if(layer instanceof FeatureMapLayer){
            this.layer = (FeatureMapLayer) layer;
            parse();
        }else{
            this.layer = null;
        }
    }

    @Override
    public void apply() {
        if(layer == null) return;

        layer.getStyle().featureTypeStyles().clear();

        MutableFeatureTypeStyle fts = SF.featureTypeStyle(
                "", SF.description("analyze", "analyze"), null, null, null, model.rules);
        layer.getStyle().featureTypeStyles().add(fts);
    }

    @Override
    public void reset() {
        if(layer != null){
            parse();
        }
    }

    @Override
    public String getTitle() {
        return MessageBundle.getString("property_style_classification_unique");
    }

    @Override
    public ImageIcon getIcon() {
        return null;
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
    private JButton guiAddOne;
    private JButton guiGenerate;
    private JLabel guiLblModel;
    private JLabel guiLblPalette;
    private JLabel guiLblProperty;
    private JButton guiModel;
    private JCheckBox guiOther;
    private JComboBox guiPalette;
    private JComboBox guiProperty;
    private JButton guiRemoveAll;
    private JXTable guiTable;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables


    private class PropertyRenderer extends DefaultListCellRenderer{
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof PropertyName){
                setText(((PropertyName)value).getPropertyName());
            }
            return PropertyRenderer.this;
        }
    }

    private class PaletteRenderer extends DefaultListCellRenderer{

        private Palette palette = null;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            PaletteRenderer.this.setText(" Random ");

            if(value instanceof Palette){
                palette = (Palette)value;
            }
            return PaletteRenderer.this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(palette != null){
                Dimension d = PaletteRenderer.this.getSize();
                Rectangle rect = new Rectangle(d);
                rect.grow(-2, -2);
                palette.render((Graphics2D) g, rect);
            }

        }

    }


    private class DeleteRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            DeleteRenderer.this.setIcon(IconBundle.getInstance().getIcon("16_delete"));
            return DeleteRenderer.this;
        }
        
    }

    private class DeleteEditor extends AbstractCellEditor implements TableCellEditor,TableCellRenderer{

        private final JButton button = new JButton();
        private Object value;

        public DeleteEditor() {
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setIcon(IconBundle.getInstance().getIcon("16_delete"));

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(value != null && value instanceof Rule){
                        model.rules.remove(value);
                        guiTable.revalidate();
                        guiTable.repaint();
                    }
                }
            });
            
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.value = value;
            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.value = value;
            return button;
        }

    }

    private class RulePropertyRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            RulePropertyRenderer.this.setText("");

            if(value instanceof Rule){
                Rule r = (Rule) value;
                Filter f = r.getFilter();

                if(f != null && f instanceof PropertyIsEqualTo){
                    PropertyIsEqualTo prop = (PropertyIsEqualTo) f;

                    if(prop.getExpression1() instanceof Literal){
                        RulePropertyRenderer.this.setText(prop.getExpression1().toString());
                    }else if(prop.getExpression2() instanceof Literal){
                        RulePropertyRenderer.this.setText(prop.getExpression2().toString());
                    }

                }

            }

            return RulePropertyRenderer.this;
        }

    }

    private class RuleStyleRenderer extends DefaultTableCellRenderer{

        private final Dimension dim = new Dimension(30, 20);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            RuleStyleRenderer.this.setText("");

            if(value instanceof Rule){
                BufferedImage img = new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render((Rule)value, new Rectangle(dim), img.createGraphics());
                RuleStyleRenderer.this.setIcon(new ImageIcon(img));
            }

            return RuleStyleRenderer.this;
        }

    }

    private class RuleModel extends AbstractTableModel{

        private final List<Rule> rules = new ArrayList<Rule>();

        @Override
        public int getRowCount() {
            return rules.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch(columnIndex){
                case 0: return rules.get(rowIndex);
                case 1: return rules.get(rowIndex);
                case 2: return rules.get(rowIndex).getDescription().getTitle().toString();
                case 3: return rules.get(rowIndex);
            }

            return null;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch(columnIndex){
                case 0: return "";
                case 1: return MessageBundle.getString("value");
                case 2: return MessageBundle.getString("title");
                case 3: return "";
            }

            return "";
        }



    }

}
