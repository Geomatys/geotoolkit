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
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

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
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.propertyedit.JPropertyDialog;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;

import org.geotools.data.DefaultQuery;
import org.geotools.feature.FeatureIterator;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JClassificationSingleStylePanel extends JPanel implements PropertyPane{

    private final Dimension GLYPH_DIMENSION = new Dimension(30, 20);

    private static final List<RandomPalette> PALETTES;

    static{
        PALETTES = new ArrayList<RandomPalette>();
        PALETTES.add(new DefaultRandomPalette());
    }

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private final RuleModel model = new RuleModel();
    private final List<PropertyName> properties = new ArrayList<PropertyName>();
    private FeatureMapLayer layer = null;
    private Symbolizer template = null;
    private Class<? extends Symbolizer> expectedType = null;

    public JClassificationSingleStylePanel() {
        initComponents();
        guiTable.setModel(model);
        
        guiProperty.setRenderer(new PropertyRenderer());

        guiPalette.setModel(new ListComboBoxModel(PALETTES));
        guiPalette.setRenderer(new PaletteRenderer());
        guiPalette.setSelectedIndex(0);

        guiTable.getColumnModel().getColumn(0).setCellRenderer(new RuleStyleRenderer());
        guiTable.getColumnModel().getColumn(0).setCellEditor(new RuleStyleEditor());
        guiTable.getColumnModel().getColumn(1).setCellRenderer(new RulePropertyRenderer());
        guiTable.getColumnModel().getColumn(1).setCellEditor(new RulePropertyEditor());
        guiTable.getColumnModel().getColumn(2).setCellRenderer(new RuleNameRenderer());
        guiTable.getColumnModel().getColumn(2).setCellEditor(new RuleNameEditor());
        guiTable.getColumnModel().getColumn(3).setCellRenderer(new DeleteRenderer());
        guiTable.getColumnModel().getColumn(3).setCellEditor(new DeleteEditor());

        guiTable.setShowGrid(false, false);
        guiTable.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping(Color.WHITE, HighlighterFactory.QUICKSILVER, 1)});

        guiTable.getColumnExt(0).setMaxWidth(30);
        guiTable.getColumnExt(3).setMaxWidth(20);
    }

    private void parse(){

        model.rules.clear();
        guiTable.revalidate();
        guiTable.repaint();
        guiOther.setSelected(false);

        properties.clear();
        if(layer != null){
            SimpleFeatureType schema = layer.getFeatureSource().getSchema();

            for(PropertyDescriptor desc : schema.getDescriptors()){
                Class<?> type = desc.getType().getBinding();

                if(!Geometry.class.isAssignableFrom(type)){
                    properties.add(FF.property(desc.getName().getLocalPart()));
                }
            }
            
            //find the geometry class for template
            GeometryDescriptor geo = schema.getGeometryDescriptor();
            Class<?> geoClass = geo.getType().getBinding();

            if(Polygon.class.isAssignableFrom(geoClass) || MultiPolygon.class.isAssignableFrom(geoClass)){
                Stroke stroke = SF.stroke(Color.BLACK, 1);
                Fill fill = SF.fill(Color.BLUE);
                template = SF.polygonSymbolizer(stroke,fill,null);
                expectedType = PolygonSymbolizer.class;
            }else if(LineString.class.isAssignableFrom(geoClass) || MultiLineString.class.isAssignableFrom(geoClass)){
                Stroke stroke = SF.stroke(Color.BLUE, 2);
                template = SF.lineSymbolizer(stroke,null);
                expectedType = LineSymbolizer.class;
            }else{
                Stroke stroke = SF.stroke(Color.BLACK, 1);
                Fill fill = SF.fill(Color.BLUE);
                List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
                symbols.add(SF.mark(StyleConstants.MARK_CIRCLE, fill, stroke));
                Graphic gra = SF.graphic(symbols, FF.literal(1), FF.literal(12), FF.literal(0), SF.anchorPoint(), SF.displacement());
                template = SF.pointSymbolizer(gra, null);
                expectedType = PointSymbolizer.class;
            }


            guiProperty.setModel(new ListComboBoxModel(properties));

            //try to rebuild the previous analyze if it was one
            List<MutableFeatureTypeStyle> ftss = layer.getStyle().featureTypeStyles();

            if(ftss.size() == 1){
                MutableFeatureTypeStyle fts = ftss.get(0);

                List<MutableRule> rules = fts.rules();

                for(Rule r : rules){
                    List<? extends Symbolizer> symbols = r.symbolizers();

                    if(symbols.size() != 1) break;

                    Symbolizer symbol = symbols.get(0);
                    if(expectedType.isInstance(symbol)){

                        if(r.isElseFilter()){
                            //it looks like it's a valid classification "other" rule
                            model.rules.add(r);
                            template = symbol;
                            guiOther.setSelected(true);
                        }else{
                            Filter f = r.getFilter();
                            if(f != null && f instanceof PropertyIsEqualTo){
                                PropertyIsEqualTo equal = (PropertyIsEqualTo) f;
                                Expression exp1 = equal.getExpression1();
                                Expression exp2 = equal.getExpression2();

                                if(exp1 instanceof PropertyName && exp2 instanceof Literal){
                                    if(properties.contains(exp1)){
                                        //it looks like it's a valid classification property rule
                                        model.rules.add(r);
                                        template = symbol;
                                        guiProperty.setSelectedItem(exp1);
                                    }else{
                                        //property is not in the schema
                                        break;
                                    }
                                }else if(exp2 instanceof PropertyName && exp1 instanceof Literal){
                                    if(properties.contains(exp2)){
                                        //it looks like it's a valid classification property rule
                                        model.rules.add(r);
                                        template = symbol;
                                        guiProperty.setSelectedItem(exp2);
                                    }else{
                                        //property is not in the schema
                                        break;
                                    }
                                }else{
                                    //mismatch analyze structure
                                    break;
                                }
                            }
                        }

                    }else{
                        break;
                    }

                }

            }
        }else{
            guiProperty.setModel(new ListComboBoxModel(properties));
        }

        updateModelGlyph();
        guiTable.revalidate();
        guiTable.repaint();
    }

    private Symbolizer createSymbolizer(){
        return derivateSymbolizer(template, ((RandomPalette)guiPalette.getSelectedItem()).next());
    }

    /**
     * Derivate a symbolizer with a new color.
     */
    private Symbolizer derivateSymbolizer(Symbolizer symbol, Color color){

        if(symbol instanceof PolygonSymbolizer){
            PolygonSymbolizer ps = (PolygonSymbolizer)symbol;
            Fill fill = SF.fill(SF.literal(color),ps.getFill().getOpacity());
            return SF.polygonSymbolizer(ps.getName(), ps.getGeometryPropertyName(),
                    ps.getDescription(), ps.getUnitOfMeasure(),
                    ps.getStroke(),fill,ps.getDisplacement(),ps.getPerpendicularOffset());
        }else if(symbol instanceof LineSymbolizer){
            LineSymbolizer ls = (LineSymbolizer) symbol;
            Stroke oldStroke = ls.getStroke();
            Stroke stroke = SF.stroke(SF.literal(color),oldStroke.getOpacity(),oldStroke.getWidth(),
                    oldStroke.getLineJoin(),oldStroke.getLineCap(),oldStroke.getDashArray(),oldStroke.getDashOffset());
            return SF.lineSymbolizer(ls.getName(), ls.getGeometryPropertyName(),
                    ls.getDescription(), ls.getUnitOfMeasure(), stroke, ls.getPerpendicularOffset());
        }else if(symbol instanceof PointSymbolizer){
            PointSymbolizer ps = (PointSymbolizer) symbol;
            Graphic oldGraphic = ps.getGraphic();
            Mark oldMark = (Mark) oldGraphic.graphicalSymbols().get(0);
            Fill fill = SF.fill(SF.literal(color),oldMark.getFill().getOpacity());
            List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
            symbols.add(SF.mark(oldMark.getWellKnownName(), fill, oldMark.getStroke()));
            Graphic graphic = SF.graphic(symbols, oldGraphic.getOpacity(),oldGraphic.getSize(),
                    oldGraphic.getRotation(),oldGraphic.getAnchorPoint(),oldGraphic.getDisplacement());
            return SF.pointSymbolizer(graphic,ps.getGeometryPropertyName());
        }else{
            throw new IllegalArgumentException("unexpected symbolizer type : " + symbol);
        }

    }

    private void updateModelGlyph(){
        if(template == null){
            guiModel.setIcon(null);
            guiModel.setText(" ");
        }else{
            BufferedImage img = new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB);
            DefaultGlyphService.render(template, new Rectangle(GLYPH_DIMENSION), img.createGraphics());
            guiModel.setIcon(new ImageIcon(img));
            guiModel.setText("");
        }
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
        guiModel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiModelActionPerformed(evt);
            }
        });

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
                    .addComponent(guiLblPalette, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiPalette, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblModel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiModel))
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guiGenerate))
        );

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiLblModel, guiLblPalette, guiModel});

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
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
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

    private void guiModelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiModelActionPerformed
        template = JPropertyDialog.showSymbolizerDialog(template, layer);
        updateModelGlyph();
    }//GEN-LAST:event_guiModelActionPerformed

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
        return IconBundle.getInstance().getIcon("16_classification_single");
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

        private RandomPalette palette = null;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            PaletteRenderer.this.setText(" Random ");

            if(value instanceof RandomPalette){
                palette = (RandomPalette)value;
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

    private class DeleteEditor extends AbstractCellEditor implements TableCellEditor{

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

    private class RulePropertyEditor extends AbstractCellEditor implements TableCellEditor{

        private final JTextField field = new JTextField();
        private MutableRule value;

        public RulePropertyEditor() {
        }

        @Override
        public Object getCellEditorValue() {
            return FF.literal(field.getText());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(value instanceof MutableRule){
                this.value = (MutableRule) value;

                Filter f = this.value.getFilter();

                if(f != null && f instanceof PropertyIsEqualTo){
                    PropertyIsEqualTo prop = (PropertyIsEqualTo) f;

                    if(prop.getExpression1() instanceof Literal){
                        field.setText(prop.getExpression1().toString());
                    }else if(prop.getExpression2() instanceof Literal){
                        field.setText(prop.getExpression2().toString());
                    }

                }

            }else{
                this.value = null;
            }
            return field;
        }


    }

    private class RuleNameRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            RuleNameRenderer.this.setText("");

            if(value instanceof Rule){
                Rule r = (Rule) value;
                RuleNameRenderer.this.setText(r.getDescription().getTitle().toString());
            }

            return RuleNameRenderer.this;
        }

    }

    private class RuleNameEditor extends AbstractCellEditor implements TableCellEditor{

        private final JTextField field = new JTextField();
        private MutableRule value;

        public RuleNameEditor() {
        }

        @Override
        public Object getCellEditorValue() {
            return field.getText();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(value instanceof MutableRule){
                this.value = (MutableRule) value;

                field.setText(this.value.getDescription().getTitle().toString());

            }else{
                field.setText("");
                this.value = null;
            }
            return field;
        }


    }

    private class RuleStyleRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            RuleStyleRenderer.this.setText("");

            if(value instanceof Rule){
                BufferedImage img = new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render((Rule)value, new Rectangle(GLYPH_DIMENSION), img.createGraphics());
                RuleStyleRenderer.this.setIcon(new ImageIcon(img));
            }

            return RuleStyleRenderer.this;
        }

    }

    private class RuleStyleEditor extends AbstractCellEditor implements TableCellEditor{

        private final JButton button = new JButton();
        private MutableRule value;

        public RuleStyleEditor() {
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(value != null){
                        Symbolizer symbol = JPropertyDialog.showSymbolizerDialog(value.symbolizers().get(0), layer);
                        value.symbolizers().clear();
                        value.symbolizers().add(symbol);
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
            if(value instanceof MutableRule){
                this.value = (MutableRule) value;
                BufferedImage img = new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(this.value, new Rectangle(GLYPH_DIMENSION), img.createGraphics());
                button.setIcon(new ImageIcon(img));
            }else{
                this.value = null;
            }
            return button;
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
            return rules.get(rowIndex);
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

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

            MutableRule rule = (MutableRule) rules.get(rowIndex);

            if(aValue instanceof Literal){
                //the property value field has changed
                Filter f = rule.getFilter();

                if(f != null){
                    PropertyIsEqualTo prop = (PropertyIsEqualTo) f;
                    if(prop.getExpression1() instanceof Literal){
                       rule.setFilter(FF.equals(prop.getExpression2(),(Expression) aValue));
                    }else if(prop.getExpression2() instanceof Literal){
                       rule.setFilter(FF.equals(prop.getExpression1(),(Expression) aValue));
                    }
                }


            }else if(aValue instanceof String){
                //the rule title changed
                rule.setDescription(SF.description((String)aValue, (String)aValue));
            }

            super.setValueAt(aValue, rowIndex, columnIndex);

        }




    }

}
