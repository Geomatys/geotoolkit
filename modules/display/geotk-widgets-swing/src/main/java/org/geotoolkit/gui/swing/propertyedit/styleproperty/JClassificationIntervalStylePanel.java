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


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.propertyedit.JPropertyDialog;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.Analyze.METHOD;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JClassificationIntervalStylePanel extends JPanel implements PropertyPane{

    private static NumberFormat FORMAT = NumberFormat.getNumberInstance();

    private final Dimension GLYPH_DIMENSION = new Dimension(30, 20);

    private static final List<IntervalPalette> PALETTES;

    static{
        PALETTES = new ArrayList<IntervalPalette>();
        PALETTES.add(new DefaultIntervalPalette(new Color[]{Color.BLUE,Color.RED}));
        PALETTES.add(new DefaultIntervalPalette(new Color[]{Color.GREEN,Color.WHITE,Color.BLUE}));
        PALETTES.add(new DefaultIntervalPalette(new Color[]{Color.GREEN,Color.WHITE}));
        PALETTES.add(new DefaultIntervalPalette(new Color[]{Color.RED,Color.YELLOW,Color.WHITE}));
        PALETTES.add(new DefaultIntervalPalette(new Color[]{Color.BLACK,Color.CYAN}));
    }

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private final RuleModel model = new RuleModel();
    private final Analyze analyze = new Analyze();
    private FeatureMapLayer layer = null;

    public JClassificationIntervalStylePanel() {
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

        guiMethod.setModel(new EnumComboBoxModel(Analyze.METHOD.class));
        guiMethod.setRenderer(new MethodRenderer());

    }

    private void parse(){
        model.rules.clear();

        if(layer != null){
            analyze.setLayer(layer);
            if(analyze.isIntervalStyle(layer.getStyle())){
                model.rules.addAll(layer.getStyle().featureTypeStyles().get(0).rules());
            }
        }

        List<PropertyName> props = analyze.getProperties();
        guiProperty.setModel(new ListComboBoxModel(new ArrayList<PropertyName>(props)));

        if(!props.isEmpty()){
            guiProperty.setSelectedIndex(0);
        }

        updateNormalizeList();
        updateModelGlyph();

        guiMethod.setSelectedItem(analyze.getMethod());
        guiClasses.setValue(analyze.getNbClasses());
        

        guiTable.revalidate();
        guiTable.repaint();
    }

    private void updateNormalizeList(){

        Object oldSelected = guiNormalize.getSelectedItem();

        List<PropertyName> lstnormalize = new ArrayList<PropertyName>();
        lstnormalize.add(Analyze.NO_VALUE);
        lstnormalize.addAll(analyze.getProperties());
        lstnormalize.remove(guiProperty.getSelectedItem());
        guiNormalize.setModel(new ListComboBoxModel(lstnormalize));

        guiNormalize.setSelectedItem(oldSelected);

    }

    private void updateModelGlyph(){
        Symbolizer template = analyze.getTemplate();
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {





        guiRemoveAll = new JButton();
        jPanel1 = new JPanel();
        guiLblProperty = new JLabel();
        guiProperty = new JComboBox();
        guiLblPalette = new JLabel();
        guiLblModel = new JLabel();
        guiModel = new JButton();
        guiGenerate = new JButton();
        guiPalette = new JComboBox();
        guiLblNormalize = new JLabel();
        guiNormalize = new JComboBox();
        guiClassify = new JButton();
        guiLblMethod = new JLabel();
        guiMethod = new JComboBox();
        guiLblClasses = new JLabel();
        guiClasses = new JSpinner();
        jScrollPane1 = new JScrollPane();
        guiTable = new JXTable();
        guiInvert = new JButton();

        guiRemoveAll.setText(MessageBundle.getString("remove_all_values")); // NOI18N
        guiRemoveAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiRemoveAllActionPerformed(evt);
            }
        });

        jPanel1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getString("properties"))); // NOI18N
        guiLblProperty.setText(MessageBundle.getString("property")); // NOI18N
        guiProperty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiPropertyActionPerformed(evt);
            }
        });

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

        ResourceBundle bundle = ResourceBundle.getBundle("org/geotoolkit/gui/swing/resource/Bundle"); // NOI18N
        guiLblNormalize.setText(bundle.getString("normalize")); // NOI18N



        guiNormalize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiNormalizeActionPerformed(evt);
            }
        });

        guiClassify.setText(MessageBundle.getString("classify")); // NOI18N
        guiClassify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiClassifyActionPerformed(evt);
            }
        });

        guiLblMethod.setText(MessageBundle.getString("method")); // NOI18N
        guiMethod.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiMethodActionPerformed(evt);
            }
        });

        guiLblClasses.setText(MessageBundle.getString("classes")); // NOI18N
        guiClasses.setModel(new SpinnerNumberModel(Integer.valueOf(5), Integer.valueOf(1), null, Integer.valueOf(1)));
        guiClasses.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                guiClassesStateChanged(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(guiLblProperty)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiProperty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiLblNormalize)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiNormalize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(guiLblMethod)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiMethod, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiLblClasses)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiClasses, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiClassify)
                            .addContainerGap(48, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(guiLblPalette)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiPalette, 0, 268, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiLblModel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(guiModel)
                            .addContainerGap()))
                    .addComponent(guiGenerate, Alignment.TRAILING)))
        );

        jPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiLblMethod, guiLblPalette, guiLblProperty});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiLblProperty)
                    .addComponent(guiProperty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblNormalize)
                    .addComponent(guiNormalize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiLblMethod)
                    .addComponent(guiMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblClasses)
                    .addComponent(guiClasses, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiClassify))
                .addPreferredGap(ComponentPlacement.RELATED)
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

        guiInvert.setText(bundle.getString("invert_palette")); // NOI18N
        guiInvert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiInvertActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiInvert)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiRemoveAll)
                .addContainerGap(237, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiInvert)
                    .addComponent(guiRemoveAll)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiRemoveAllActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiRemoveAllActionPerformed
        model.rules.clear();
        guiTable.revalidate();
        guiTable.repaint();
    }//GEN-LAST:event_guiRemoveAllActionPerformed

    private void guiGenerateActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiGenerateActionPerformed
        model.rules.clear();
        model.rules.addAll(analyze.generateRules((IntervalPalette) guiPalette.getSelectedItem()));

        guiTable.revalidate();
        guiTable.repaint();
    }//GEN-LAST:event_guiGenerateActionPerformed

    private void guiModelActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiModelActionPerformed
        analyze.setTemplate(JPropertyDialog.showSymbolizerDialog(analyze.getTemplate(), layer));
        updateModelGlyph();
    }//GEN-LAST:event_guiModelActionPerformed

    private void guiPropertyActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiPropertyActionPerformed
        analyze.setClassification((PropertyName) guiProperty.getSelectedItem());
        updateNormalizeList();
    }//GEN-LAST:event_guiPropertyActionPerformed

    private void guiNormalizeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiNormalizeActionPerformed
        PropertyName prop = (PropertyName) guiNormalize.getSelectedItem();
        analyze.setNormalize(prop);
    }//GEN-LAST:event_guiNormalizeActionPerformed

    private void guiClassesStateChanged(ChangeEvent evt) {//GEN-FIRST:event_guiClassesStateChanged
        analyze.setNbClasses((Integer)guiClasses.getModel().getValue());
    }//GEN-LAST:event_guiClassesStateChanged

    private void guiInvertActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiInvertActionPerformed

        Symbolizer[] symbols = new Symbolizer[model.rules.size()];

        for(int i=0;i<model.rules.size();i++){
            symbols[model.rules.size()-1-i] = model.rules.get(i).symbolizers().get(0);
        }

        for(int i=0;i<model.rules.size();i++){
            model.rules.get(i).symbolizers().clear();
            ((MutableRule)model.rules.get(i)).symbolizers().add(symbols[i]);
        }

        guiTable.revalidate();
        guiTable.repaint();

    }//GEN-LAST:event_guiInvertActionPerformed

    private void guiMethodActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiMethodActionPerformed
        analyze.setMethod((METHOD) guiMethod.getSelectedItem());
    }//GEN-LAST:event_guiMethodActionPerformed

    private void guiClassifyActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiClassifyActionPerformed

        JAnalizePanel panel = new JAnalizePanel(analyze);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setAlwaysOnTop(true);
        dialog.setContentPane(panel);

        dialog.pack();
        dialog.setLocationRelativeTo(guiClassify);
        dialog.setVisible(true);

        guiMethod.setSelectedItem(analyze.getMethod());
        guiClasses.setValue(analyze.getNbClasses());

    }//GEN-LAST:event_guiClassifyActionPerformed

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
        return MessageBundle.getString("property_style_classification_interval");
    }

    @Override
    public ImageIcon getIcon() {
        return IconBundle.getInstance().getIcon("16_classification_interval");
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
    private JSpinner guiClasses;
    private JButton guiClassify;
    private JButton guiGenerate;
    private JButton guiInvert;
    private JLabel guiLblClasses;
    private JLabel guiLblMethod;
    private JLabel guiLblModel;
    private JLabel guiLblNormalize;
    private JLabel guiLblPalette;
    private JLabel guiLblProperty;
    private JComboBox guiMethod;
    private JButton guiModel;
    private JComboBox guiNormalize;
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

    private class MethodRenderer extends DefaultListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            MethodRenderer.this.setText(" ");

            if(value instanceof Analyze.METHOD){
                MethodRenderer.this.setText(((Analyze.METHOD)value).getTitle());
            }
            return MethodRenderer.this;
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

                if(f != null && f instanceof And){
                    And prop = (And) f;

                    BinaryComparisonOperator under = (BinaryComparisonOperator) prop.getChildren().get(0);
                    BinaryComparisonOperator above = (BinaryComparisonOperator) prop.getChildren().get(1);

                    Number nUnder = under.getExpression2().evaluate(null, Number.class);
                    Number nAbove = above.getExpression2().evaluate(null, Number.class);

                    this.setText( FORMAT.format(nUnder.doubleValue()) + " -> " + FORMAT.format(nAbove.doubleValue()) );
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
            return columnIndex != 1;
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
