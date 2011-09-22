/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011 Geomatys
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


import org.opengis.filter.expression.Function;
import org.geotoolkit.style.function.Interpolate;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.interval.DefaultRandomPalette;
import org.geotoolkit.style.interval.RandomPalette;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.util.ColorCellEditor;
import org.geotoolkit.util.ColorCellRenderer;
import org.geotoolkit.util.Converters;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.Description;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.style.Symbolizer;

import static org.geotoolkit.style.StyleConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JRasterColorMapStylePanel extends JPanel implements PropertyPane{

    private static final List<RandomPalette> PALETTES;

    static{
        PALETTES = new ArrayList<RandomPalette>();
        PALETTES.add(new DefaultRandomPalette());
    }

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private final ColorMapModel model = new ColorMapModel();
    private CoverageMapLayer layer = null;

    public JRasterColorMapStylePanel() {
        initComponents();
        guiTable.setModel(model);
        
        guiPalette.setModel(new ListComboBoxModel(PALETTES));
        guiPalette.setRenderer(new PaletteRenderer());
        guiPalette.setSelectedIndex(0);

        guiTable.getColumnModel().getColumn(1).setCellRenderer(new ColorCellRenderer());
        guiTable.getColumnModel().getColumn(1).setCellEditor(new ColorCellEditor());
        guiTable.getColumnModel().getColumn(2).setCellRenderer(new DeleteRenderer());
        guiTable.getColumnModel().getColumn(2).setCellEditor(new DeleteEditor());

        guiTable.setShowGrid(false, false);
        //guiTable.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping(Color.WHITE, HighlighterFactory.QUICKSILVER, 1)});
        
        guiTable.getColumnExt(2).setMaxWidth(20);
    }

    private void parse(){
        guiTable.revalidate();
        guiTable.repaint();
        guiOther.setSelected(true);
                
        RasterSymbolizer rs = null;
        search:
        if(layer != null){
            for(final MutableFeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                for(MutableRule r : fts.rules()){
                    for(Symbolizer s : r.symbolizers()){
                        if(s instanceof RasterSymbolizer){
                            rs = (RasterSymbolizer) s;
                            break search;
                        }
                    }
                }
            }
        }
        
        List<InterpolationPoint> points = null;
        if(rs != null && rs.getColorMap() != null){
            final Function fct = rs.getColorMap().getFunction();
            if(fct instanceof Interpolate){
                points = ((Interpolate)fct).getInterpolationPoints();
            }
        }
        
        model.points.clear();
        if(points != null){
            model.points.addAll(points);
        }
        
        model.fireTableDataChanged();
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
        guiOther = new JCheckBox();
        guiLblPalette = new JLabel();
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

        guiOther.setSelected(true);
        guiOther.setText(MessageBundle.getString("nanValue")); // NOI18N

        guiLblPalette.setText(MessageBundle.getString("palette")); // NOI18N

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
                        .addComponent(guiLblPalette)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiPalette, 0, 379, Short.MAX_VALUE))
                    .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(guiOther)
                        .addPreferredGap(ComponentPlacement.RELATED, 277, Short.MAX_VALUE)
                        .addComponent(guiGenerate)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiPalette, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblPalette, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiOther)
                    .addComponent(guiGenerate)))
        );

        jScrollPane1.setViewportView(guiTable);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiAddOne)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiRemoveAll)
                .addContainerGap())
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiAddOne)
                    .addComponent(guiRemoveAll)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiAddOneActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiAddOneActionPerformed
        final InterpolationPoint pt = SF.interpolationPoint(Float.NaN, SF.literal(Color.BLACK));
        model.points.add(pt);
        model.fireTableDataChanged();
    }//GEN-LAST:event_guiAddOneActionPerformed

    private void guiRemoveAllActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiRemoveAllActionPerformed
        model.points.clear();
        model.fireTableDataChanged();
    }//GEN-LAST:event_guiRemoveAllActionPerformed

    private void guiGenerateActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiGenerateActionPerformed


    }//GEN-LAST:event_guiGenerateActionPerformed

    @Override
    public boolean canHandle(Object target) {
        return target instanceof CoverageMapLayer;
    }
    
    @Override
    public void setTarget(final Object layer) {
        if(layer instanceof CoverageMapLayer){
            this.layer = (CoverageMapLayer) layer;
            parse();
        }else{
            this.layer = null;
        }
    }

    @Override
    public void apply() {
        if(layer == null) return;

        layer.getStyle().featureTypeStyles().clear();

        final Expression lookup = DEFAULT_CATEGORIZE_LOOKUP;
        final Literal fallback = DEFAULT_FALLBACK;
        final Interpolate function = SF.interpolateFunction(
                lookup, new ArrayList<InterpolationPoint>(model.points), Method.COLOR, Mode.LINEAR, fallback);

        final ChannelSelection selection = DEFAULT_RASTER_CHANNEL_RGB;

        final Expression opacity = LITERAL_ONE_FLOAT;
        final OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
        final ColorMap colorMap = SF.colorMap(function);
        final ContrastEnhancement enchance = SF.contrastEnhancement(LITERAL_ONE_FLOAT,ContrastMethod.NONE);
        final ShadedRelief relief = SF.shadedRelief(LITERAL_ONE_FLOAT);
        final Symbolizer outline = null;
        final Unit uom = NonSI.PIXEL;
        final String geom = DEFAULT_GEOM;
        final String name = "interpolate";
        final Description desc = DEFAULT_DESCRIPTION;

        final RasterSymbolizer symbol = SF.rasterSymbolizer(
                name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);
        
        final MutableFeatureTypeStyle fts = SF.featureTypeStyle(symbol);
        fts.setDescription(SF.description("analyze", "analyze"));
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
        return MessageBundle.getString("property_style_colormap");
    }

    @Override
    public ImageIcon getIcon() {
        return IconBundle.getIcon("16_classification_single");
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
    private JLabel guiLblPalette;
    private JCheckBox guiOther;
    private JComboBox guiPalette;
    private JButton guiRemoveAll;
    private JXTable guiTable;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables


    private class PaletteRenderer extends DefaultListCellRenderer{

        private RandomPalette palette = null;

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            PaletteRenderer.this.setText(" Random ");

            if(value instanceof RandomPalette){
                palette = (RandomPalette)value;
            }
            return PaletteRenderer.this;
        }

        @Override
        protected void paintComponent(final Graphics g) {
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
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            DeleteRenderer.this.setIcon(IconBundle.getIcon("16_delete"));
            return DeleteRenderer.this;
        }
        
    }

    private class DeleteEditor extends AbstractCellEditor implements TableCellEditor{

        private final JButton button = new JButton();
        private int row;

        public DeleteEditor() {
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setIcon(IconBundle.getIcon("16_delete"));

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    model.points.remove(row);
                    fireEditingCanceled();
                    model.fireTableDataChanged();
                }
            });
            
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
            this.row = row;
            return button;
        }

    }

    private class ColorMapModel extends AbstractTableModel{

        private final List<InterpolationPoint> points = new ArrayList<InterpolationPoint>();

        @Override
        public int getRowCount() {
            return points.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final InterpolationPoint pt = points.get(rowIndex);
            switch(columnIndex){
                case 0: 
                    return pt.getData();
                case 1: 
                    final Color c = pt.getValue().evaluate(null, Color.class);
                    return c;
            }
            return "";
        }

        @Override
        public String getColumnName(final int columnIndex) {
            switch(columnIndex){
                case 0: return "value";
                case 1: return "color";
            }
            return "";
        }

        @Override
        public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
            InterpolationPoint pt = points.get(rowIndex);
            switch(columnIndex){
                case 0: 
                    Number n = Converters.convert(aValue, Number.class);
                    if(n == null){
                        n = Float.NaN;
                    }
                    
                    pt = SF.interpolationPoint(n, pt.getValue());
                    break;
                case 1: 
                    Color c = (Color) aValue;
                    if(c == null){
                        c = new Color(0, 0, 0, 0);
                    }
                    pt = SF.interpolationPoint(pt.getData(),SF.literal(c));
                    break;
            }
            
            points.set(rowIndex, pt);
        }

    }

}
