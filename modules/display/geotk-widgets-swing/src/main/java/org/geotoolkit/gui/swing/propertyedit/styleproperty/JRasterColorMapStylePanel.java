/*//GEN-FIRST:event_guiRemoveAllActionPerformed
 *    Geotoolkit - An Open Source Java GIS Toolkit//GEN-LAST:event_guiRemoveAllActionPerformed
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


import java.util.Map;
import java.util.Map.Entry;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.geotoolkit.coverage.CoverageReference;

import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.process.coverage.copy.StatisticOp;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.interval.DefaultRandomPalette;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.interval.DefaultIntervalPalette;
import org.geotoolkit.style.interval.Palette;
import org.geotoolkit.util.ColorCellEditor;
import org.geotoolkit.util.ColorCellRenderer;
import org.geotoolkit.util.Converters;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

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
import org.opengis.filter.expression.Function;

import static org.geotoolkit.style.StyleConstants.*;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.opengis.style.SelectedChannelType;
import org.opengis.util.GenericName;

/**
 * Style editor which handle Raster colormap edition.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JRasterColorMapStylePanel extends JPanel implements PropertyPane{

    private static final Logger LOGGER = Logging.getLogger(JRasterColorMapStylePanel.class);

    private static final PaletteFactory PF = PaletteFactory.getDefault();
    private static final List<Object> PALETTES;

    static{
        PALETTES = new ArrayList<>();
        PALETTES.add(new DefaultRandomPalette());
        final Set<String> paletteNames = PF.getAvailableNames();

        for (String palName : paletteNames) {
            PALETTES.add(palName);
        }

        double[] fractions = new double[]{
            -3000,
            -1500,
            -0.1,
            +0,
            556,
            1100,
            1600,
            2200,
            3000};
        Color[] colors = new Color[]{
            new Color(9, 9, 145, 255),
            new Color(31, 131, 224, 255),
            new Color(182, 240, 240, 255),
            new Color(5, 90, 5, 255),
            new Color(150, 200, 150, 255),
            new Color(190, 150, 20, 255),
            new Color(100, 100, 50, 255),
            new Color(200, 210, 220, 255),
            new Color(255, 255, 255, 255),
            };
        PALETTES.add(new DefaultIntervalPalette(fractions,colors));

    }

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private ColorMapModel model = new InterpolateColorModel(Collections.EMPTY_LIST);
    private MapLayer layer = null;
    //keep track of where the symbolizer was to avoid rewriting the complete style
    private MutableRule parentRule = null;
    private int parentIndex = 0;

    public JRasterColorMapStylePanel() {
        initComponents();
        guiPalette.setModel(new ListComboBoxModel(PALETTES));
        guiPalette.setRenderer(new PaletteCellRenderer());
        guiPalette.setSelectedIndex(0);
        guiTable.setShowGrid(false, false);
        parse(null);
    }

    private void parse(){
        guiTable.revalidate();
        guiTable.repaint();
        guiNaN.setSelected(true);

        RasterSymbolizer rs = null;
        parentRule = null;
        parentIndex = 0;
        search:
        if(layer != null){
            for(final MutableFeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
                for(MutableRule r : fts.rules()){
                    for(int i=0,n=r.symbolizers().size();i<n;i++){
                        Symbolizer s = r.symbolizers().get(i);
                        if(s instanceof RasterSymbolizer){
                            rs = (RasterSymbolizer) s;
                            parentRule = r;
                            parentIndex = i;
                            break search;
                        }
                    }
                }
            }
        }

        parse(rs);
    }

    private void parse(RasterSymbolizer rs){
        //find channel
        if(rs!=null){
            final ChannelSelection selection = rs.getChannelSelection();
            guiBand.setValue(0);
            if(selection!=null){
                final SelectedChannelType sct = selection.getGrayChannel();
                if(sct!=null){
                    try{
                        guiBand.setValue(Integer.valueOf(sct.getChannelName()));
                    }catch(Exception ex){ //nullpointer or numberformat exception
                        LOGGER.log(Level.INFO, "chanel name is not a number : {0}", sct.getChannelName());
                    }
                }
            }
        }

        model = null;

        if(rs != null){
            guiName.setText(rs.getName());
            guiDesc.setText(rs.getDescription()== null ? "" : ((rs.getDescription().getTitle()==null) ? "" : rs.getDescription().getTitle().toString()));

            if(rs.getColorMap()!=null && rs.getColorMap().getFunction()!=null){
                final Function fct = rs.getColorMap().getFunction();
                if(fct instanceof Interpolate){
                    final List<InterpolationPoint> points = ((Interpolate)fct).getInterpolationPoints();
                    model = new InterpolateColorModel(points);
                }else if(fct instanceof Categorize){
                    final Map<Expression,Expression> th = ((Categorize)fct).getThresholds();
                    model = new CategorizeColorModel(th);
                }else{
                    throw new IllegalArgumentException("Unknowned colormap function : "+fct);
                }
            }else{
                //create an empty interpolate colormodel
                model = new InterpolateColorModel(Collections.EMPTY_LIST);
            }
        }else{
            //create an empty interpolate colormodel
            model = new InterpolateColorModel(Collections.EMPTY_LIST);
        }

        postParse();
    }

    private void postParse(){
        guiTable.setModel(model);
        if(model instanceof InterpolateColorModel){
            guiInterpolate.setSelected(true);
            guiTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer());
            guiTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
            guiTable.getColumnModel().getColumn(1).setCellRenderer(new ColorCellRenderer());
            guiTable.getColumnModel().getColumn(1).setCellEditor(new ColorCellEditor());
            guiTable.getColumnModel().getColumn(2).setCellRenderer(new DeleteRenderer());
            guiTable.getColumnModel().getColumn(2).setCellEditor(new DeleteEditor());
            guiTable.getColumnExt(2).setMaxWidth(20);
        }else if(model instanceof CategorizeColorModel){
            guiInterpolate.setSelected(false);
            guiTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer());
            guiTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));
            guiTable.getColumnModel().getColumn(1).setCellRenderer(new ColorCellRenderer());
            guiTable.getColumnModel().getColumn(1).setCellEditor(new ColorCellEditor());
            guiTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer());
            guiTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));
            guiTable.getColumnModel().getColumn(3).setCellRenderer(new DeleteRenderer());
            guiTable.getColumnModel().getColumn(3).setCellEditor(new DeleteEditor());
            guiTable.getColumnExt(3).setMaxWidth(20);
        }


        if(layer instanceof CoverageMapLayer){
            guiLblPalette.setVisible(true);
            guiPalette.setVisible(true);
            guiBand.setVisible(true);
            guiLblBand.setVisible(true);
            guiNaN.setVisible(true);
            guiInvert.setVisible(true);
            guiGenerate.setVisible(true);

        }else{
            guiLblPalette.setVisible(false);
            guiPalette.setVisible(false);
            guiBand.setVisible(false);
            guiLblBand.setVisible(false);
            guiNaN.setVisible(false);
            guiInvert.setVisible(false);
            guiGenerate.setVisible(false);
        }

        revalidate();
        repaint();
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
        guiNaN = new JCheckBox();
        guiLblPalette = new JLabel();
        guiGenerate = new JButton();
        guiPalette = new JComboBox();
        guiInvert = new JCheckBox();
        guiLblBand = new JLabel();
        guiBand = new JSpinner();
        guiInterpolate = new JCheckBox();
        jLabel2 = new JLabel();
        guiName = new JTextField();
        jLabel3 = new JLabel();
        guiDesc = new JTextField();
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

        jPanel1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));

        guiNaN.setSelected(true);
        guiNaN.setText(MessageBundle.getString("style.rastercolormappane.nan")); // NOI18N

        guiLblPalette.setText(MessageBundle.getString("style.rastercolormappane.palette")); // NOI18N

        guiGenerate.setText(MessageBundle.getString("generate")); // NOI18N
        guiGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiGenerateActionPerformed(evt);
            }
        });

        guiInvert.setText(MessageBundle.getString("style.rastercolormappane.invert")); // NOI18N

        guiLblBand.setText(MessageBundle.getString("style.rastercolormappane.band")); // NOI18N

        guiBand.setModel(new SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        guiInterpolate.setText(MessageBundle.getString("style.rastercolormappane.interpolate")); // NOI18N
        guiInterpolate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiInterpolateActionPerformed(evt);
            }
        });

        jLabel2.setText(MessageBundle.getString("name")); // NOI18N

        jLabel3.setText(MessageBundle.getString("description")); // NOI18N

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
                                .addComponent(guiNaN)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiInvert)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiInterpolate)
                                .addGap(0, 146, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(guiLblPalette)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiPalette, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiLblBand)))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING, false)
                            .addComponent(guiBand)
                            .addComponent(guiGenerate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiName, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiDesc)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(guiName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(guiDesc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiPalette, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblPalette, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblBand)
                    .addComponent(guiBand, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiNaN)
                    .addComponent(guiGenerate)
                    .addComponent(guiInvert)
                    .addComponent(guiInterpolate)))
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
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiAddOne)
                    .addComponent(guiRemoveAll)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiInterpolateActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiInterpolateActionPerformed

        if(guiInterpolate.isSelected()){
            if(model instanceof InterpolateColorModel){
                //nothing to do
            }else if(model == null){
                model = new InterpolateColorModel(Collections.EMPTY_LIST);
                postParse();
            }else{
                //we need to convert from categorize thredholds to interpolation points.
                final List<InterpolationPoint> points = new ArrayList<>();
                final List<Entry<Expression, Expression>> ths = ((CategorizeColorModel)model).ths;
                for(int i=1,n=ths.size();i<n;i++){
                    final Entry<Expression, Expression> entry = ths.get(i);
                    points.add(SF.interpolationPoint(entry.getKey().evaluate(n, Number.class), entry.getValue()));
                }
                model = new InterpolateColorModel(points);
                postParse();
            }
        }else{
            if(model instanceof CategorizeColorModel){
                //nothing to do
            }else if(model == null){
                final Map<Expression, Expression> values = new HashMap<>();
                values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, SF.literal(new Color(0f,0f,0f,0f)));
                values.put(new DefaultLiteral<Number>(0), SF.literal(new Color(0f,0f,0f,0f)));
                model = new CategorizeColorModel(values);
                postParse();
            }else{
                //we need to convert from interpolate to categorize
                final Map<Expression, Expression> values = new HashMap<>();
                values.put( StyleConstants.CATEGORIZE_LESS_INFINITY, SF.literal(new Color(0f,0f,0f,0f)));
                final List<InterpolationPoint> points = ((InterpolateColorModel)model).points;
                for(InterpolationPoint pt : points){
                    values.put(new DefaultLiteral(pt.getData()), pt.getValue());
                }
                model = new CategorizeColorModel(values);
                postParse();
            }
        }

    }//GEN-LAST:event_guiInterpolateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton guiAddOne;
    private JSpinner guiBand;
    private JTextField guiDesc;
    private JButton guiGenerate;
    private JCheckBox guiInterpolate;
    private JCheckBox guiInvert;
    private JLabel guiLblBand;
    private JLabel guiLblPalette;
    private JCheckBox guiNaN;
    private JTextField guiName;
    private JComboBox guiPalette;
    private JButton guiRemoveAll;
    private JXTable guiTable;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    private void guiAddOneActionPerformed(final ActionEvent evt) {
        model.addValue(Float.NaN, Color.BLACK);
        model.fireTableDataChanged();
    }

    private void guiRemoveAllActionPerformed(final ActionEvent evt) {
        model.removeAll();
        model.fireTableDataChanged();
    }

    private void guiGenerateActionPerformed(final ActionEvent evt) {

        if(!(layer instanceof CoverageMapLayer)){
            return;
        }

        model.removeAll();

        //add the NaN if specified
        if(guiNaN.isSelected() && guiInterpolate.isSelected()){
            model.addValue(Float.NaN, new Color(0, 0, 0, 0));
        }

        boolean mustInterpolation = true;
        final Object paletteValue = (Object) guiPalette.getSelectedItem();
        List<Entry<Double, Color>> steps = new ArrayList<>();

        if (paletteValue instanceof Palette) {
            final Palette palette = (Palette) paletteValue;
            steps = palette.getSteps();
        } else if (paletteValue instanceof String) {
            try {
                final Color[] paletteColors = PF.getColors(String.valueOf(paletteValue));
                final double stepValue = 1.0f/(paletteColors.length-1);
                for (int i = 0; i < paletteColors.length; i++) {
                    final double fragment = i * stepValue;
                    steps.add(new AbstractMap.SimpleEntry<>(fragment, paletteColors[i]));
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        for(int i=0,n=steps.size();i<n;i++){
            final double k = steps.get(i).getKey();
            if(k < -0.01 || k > 1.01){
                mustInterpolation = false;
            }
        }

        if(guiInvert.isSelected()){
            final List<Entry<Double, Color>> inverted = new ArrayList<>();
            for(int i=0,n=steps.size();i<n;i++){
                final double k = steps.get(i).getKey();
                inverted.add(new SimpleImmutableEntry<>(
                        k, steps.get(n-1-i).getValue()));
            }
            steps = inverted;
        }
        if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer)layer;
            try {
                final GridCoverageReader reader = cml.getCoverageReference().acquireReader();
                final List<? extends GenericName> names = reader.getCoverageNames();
                int imageIndex = 0;
                final CoverageReference ref = cml.getCoverageReference();
                if(ref!=null){
                    imageIndex = ref.getImageIndex();
                }

                if(mustInterpolation){
                    final List<MeasurementRange<?>> ranges = reader.getSampleValueRanges(imageIndex);
                    if(ranges != null && !ranges.isEmpty()){
                        final Integer index = (Integer) guiBand.getValue();
                        if(index<ranges.size()){
                            final MeasurementRange r = ranges.get(index);
                            final double min = r.getMinDouble();
                            final double max = r.getMaxDouble();
                            if (Double.isInfinite(min) || Double.isInfinite(max)) {
                                getInterpolationPoints(reader, cml, steps);
                            } else {
                                for(int s=0,l=steps.size();s<l;s++){
                                    final Entry<Double, Color> step = steps.get(s);
                                    model.addValue(min + (step.getKey()*(max-min)), step.getValue());
                                }
                            }
                        }
                    }else{
                        getInterpolationPoints(reader, cml, steps);
                    }
                }else{
                    for(int s=0,l=steps.size();s<l;s++){
                        final Entry<Double, Color> step = steps.get(s);
                        model.addValue(step.getKey(), step.getValue());
                    }
                }

            } catch (CoverageStoreException ex) {
                LOGGER.log(Level.INFO, ex.getMessage(),ex);
            }
        }

        model.fireTableDataChanged();

    }

    /**
     * Find the min or max values in an array of double
     * @param data double array
     * @param min search min values or max values
     * @return min or max value.
     */
    private double findExtremum(final double[] data, final boolean min) {
        if (data.length > 0) {
            double extremum = data[0];
            if (min) {
                for (int i = 0; i < data.length; i++) {
                    extremum = Math.min(extremum, data[i]);
                }
            } else {
                for (int i = 0; i < data.length; i++) {
                    extremum = Math.max(extremum, data[i]);
                }
            }
            return extremum;
        }
        throw new IllegalArgumentException("Array of " + (min ? "min" : "max") + " values is empty.");
    }

    @Override
    public boolean canHandle(Object target) {
        return target instanceof MapLayer && !(target instanceof FeatureMapLayer);
    }

    @Override
    public void setTarget(final Object layer) {
        if(layer instanceof MapLayer){
            this.layer = (MapLayer)layer;
            parse();
        }else{
            this.layer = null;
        }
    }

    @Override
    public void apply() {
        if(layer == null) return;

        final Expression lookup = DEFAULT_CATEGORIZE_LOOKUP;
        final Literal fallback = DEFAULT_FALLBACK;

        final Function function = ((ColorMapModel)guiTable.getModel()).createFunction();

        final ChannelSelection selection = SF.channelSelection(
                SF.selectedChannelType(String.valueOf(guiBand.getValue()),DEFAULT_CONTRAST_ENHANCEMENT));

        final Expression opacity = LITERAL_ONE_FLOAT;
        final OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
        final ColorMap colorMap = SF.colorMap(function);
        final ContrastEnhancement enchance = SF.contrastEnhancement(LITERAL_ONE_FLOAT,ContrastMethod.NONE);
        final ShadedRelief relief = SF.shadedRelief(LITERAL_ONE_FLOAT);
        final Symbolizer outline = null;
        final Unit uom = NonSI.PIXEL;
        final String geom = DEFAULT_GEOM;
        final String name = guiName.getText();
        final Description desc = SF.description(guiDesc.getText(), guiDesc.getText());

        final RasterSymbolizer symbol = SF.rasterSymbolizer(
                name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);

        if(parentRule!=null){
            parentRule.symbolizers().remove(parentIndex);
            parentRule.symbolizers().add(parentIndex,symbol);
        }else{
            //style did not exist, add a new feature type style for it
            final MutableFeatureTypeStyle fts = SF.featureTypeStyle(symbol);
            fts.setDescription(SF.description("analyze", "analyze"));
            layer.getStyle().featureTypeStyles().add(fts);
        }
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
    public Image getPreview() {
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


    private void getInterpolationPoints(final GridCoverageReader reader, final CoverageMapLayer cml, List<Entry<Double, Color>> steps) throws CoverageStoreException {
        //we explore the image and try to find the min and max
        Map<String,Object> an = StatisticOp.analyze(reader,cml.getCoverageReference().getImageIndex());
        final double[] minArray = (double[])an.get(StatisticOp.MINIMUM);
        final double[] maxArray = (double[])an.get(StatisticOp.MAXIMUM);
        final double min = findExtremum(minArray, true);
        final double max = findExtremum(maxArray, false);

        for(int s=0,l=steps.size();s<l;s++){
            final Entry<Double, Color> step = steps.get(s);
            model.addValue(min + (step.getKey()*(max-min)), step.getValue());
        }
    }


    private class DeleteRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            DeleteRenderer.this.setIcon(IconBuilder.createIcon(FontAwesomeIcons.ICON_TRASH, 16, Color.BLACK));
            return DeleteRenderer.this;
        }

    }

    private class DeleteEditor extends AbstractCellEditor implements TableCellEditor{

        private final JButton button = new JButton();
        private int row;

        public DeleteEditor() {
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setIcon(IconBuilder.createIcon(FontAwesomeIcons.ICON_TRASH, 16, Color.BLACK));

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    model.remove(row);
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

    private abstract class ColorMapModel extends AbstractTableModel{

        public abstract void addValue(Number value, Color color);

        public abstract void remove(int row);

        public abstract void removeAll();

        public abstract Function createFunction();

    }

    private class InterpolateColorModel extends ColorMapModel{

        final Comparator<InterpolationPoint> COMP = new Comparator<InterpolationPoint>() {
            @Override
            public int compare(InterpolationPoint o1, InterpolationPoint o2) {
                return (int)Math.signum( (o1.getData().doubleValue() - o2.getData().doubleValue()));
            }
        };

        private final List<InterpolationPoint> points = new ArrayList<>();

        public InterpolateColorModel(List<InterpolationPoint> points) {
            this.points.addAll(points);
            Collections.sort(this.points,COMP);
        }

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
                case 0: return MessageBundle.getString("style.rastersymbolizer.value");
                case 1: return MessageBundle.getString("style.rastersymbolizer.color");
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
            Collections.sort(points,COMP);
            fireTableDataChanged();
        }

        @Override
        public void addValue(Number value, Color color) {
            final InterpolationPoint pt = SF.interpolationPoint(Float.NaN, SF.literal(Color.BLACK));
            points.add(pt);
            Collections.sort(points,COMP);
            fireTableDataChanged();
        }

        @Override
        public void removeAll() {
            points.clear();
            fireTableDataChanged();
        }

        @Override
        public void remove(int row) {
            points.remove(row);
            fireTableDataChanged();
        }

        @Override
        public Function createFunction() {
            final Expression lookup = DEFAULT_CATEGORIZE_LOOKUP;
            final Literal fallback = DEFAULT_FALLBACK;

            return SF.interpolateFunction(lookup, new ArrayList<>(points),
                    Method.COLOR, Mode.LINEAR, fallback);
        }

    }

    private class CategorizeColorModel extends ColorMapModel{

        final Comparator<Entry<Expression,Expression>> COMP = new Comparator<Entry<Expression, Expression>>() {

            @Override
            public int compare(Entry<Expression, Expression> o1, Entry<Expression, Expression> o2) {
                final Double d0 = o1.getKey().evaluate(null, Double.class);
                final Double d1 = o2.getKey().evaluate(null, Double.class);
                if(d0==null) return -1;
                if(d1==null) return +1;
                return d0.compareTo(d1);
            }
        };

        private final List<Entry<Expression,Expression>> ths = new ArrayList<>();

        public CategorizeColorModel(List<Entry<Expression,Expression>> map) {
            ths.addAll(map);
            Collections.sort(ths,COMP);
        }

        public CategorizeColorModel(Map<Expression,Expression> map) {
            ths.addAll(map.entrySet());
            Collections.sort(ths,COMP);
        }

        @Override
        public int getRowCount() {
            return ths.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            if(columnIndex==3){
                //first and last (infinite) rows can not be removed
                return rowIndex > 0 && rowIndex < ths.size()-1;
            }else if(columnIndex==2){
                //a copy of the next threholds value, can not be removed
                return false;
            }else if(columnIndex==1){
                //color column
                return true;
            }else{
                //first column
                return rowIndex>0;
            }
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if(columnIndex==2){
                //return next column value if any
                if(rowIndex<ths.size()-1){
                    return getValueAt(rowIndex+1, 0);
                }else{
                    //last line +infinity
                    return Double.POSITIVE_INFINITY;
                }
            }else if(columnIndex==3){
                //delete column
                return "";
            }else if(columnIndex==1){
                //color column
                final Entry<Expression, Expression> entry = ths.get(rowIndex);
                return entry.getValue().evaluate(null, Color.class);
            }else{
                //thresdhold value
                final Entry<Expression, Expression> entry = ths.get(rowIndex);
                Number n = entry.getKey().evaluate(null, Number.class);
                if(n==null) n = Double.NEGATIVE_INFINITY;
                return n;
            }
        }

        @Override
        public String getColumnName(final int columnIndex) {
            switch(columnIndex){
                case 0: return MessageBundle.getString("style.rastersymbolizer.lower");
                case 1: return MessageBundle.getString("style.rastersymbolizer.color");
                case 2: return MessageBundle.getString("style.rastersymbolizer.upper");
            }
            return "";
        }

        @Override
        public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
            Entry<Expression,Expression> th = ths.get(rowIndex);
            switch(columnIndex){
                case 0:
                    Number n = Converters.convert(aValue, Number.class);
                    if(n == null){
                        n = Float.NaN;
                    }
                    th = new AbstractMap.SimpleEntry<>((Expression)FF.literal(n),th.getValue());
                    break;
                case 1:
                    Color c = (Color) aValue;
                    if(c == null){
                        c = new Color(0, 0, 0, 0);
                    }
                    th = new AbstractMap.SimpleEntry<>(th.getKey(),(Expression)SF.literal(c));
                    break;
            }

            ths.set(rowIndex, th);
            Collections.sort(ths,COMP);
            fireTableDataChanged();
        }

        @Override
        public void addValue(Number value, Color color) {
            Entry<Expression,Expression> th = new AbstractMap.SimpleEntry<>(
                    (Expression)FF.literal(value),(Expression)SF.literal(color));
            ths.add(th);
            Collections.sort(ths,COMP);
            fireTableDataChanged();
        }

        @Override
        public void removeAll() {
            ths.clear();
            Entry<Expression,Expression> th1 = new AbstractMap.SimpleEntry<>(
                    (Expression)StyleConstants.CATEGORIZE_LESS_INFINITY,(Expression)SF.literal(new Color(0f,0f,0f,0f)));
            Entry<Expression,Expression> th2 = new AbstractMap.SimpleEntry<>(
                    (Expression)FF.literal(0d),(Expression)SF.literal(new Color(0f,0f,0f,0f)));
            ths.add(th1);
            ths.add(th2);
            fireTableDataChanged();
        }

        @Override
        public void remove(int row) {
            ths.remove(row);
            fireTableDataChanged();
        }

        @Override
        public Function createFunction() {
            final Expression lookup = DEFAULT_CATEGORIZE_LOOKUP;
            final Literal fallback = DEFAULT_FALLBACK;

            final Map<Expression,Expression> map = new HashMap<>();
            for(Entry<Expression,Expression> exp : ths){
                map.put(exp.getKey(), exp.getValue());
            }

            return SF.categorizeFunction(lookup, map, ThreshholdsBelongTo.PRECEDING, fallback);
        }

    }


}
