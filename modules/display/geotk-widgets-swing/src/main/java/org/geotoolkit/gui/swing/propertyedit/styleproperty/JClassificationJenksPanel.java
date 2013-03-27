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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyleFactory;
import static org.geotoolkit.style.StyleConstants.*;
import org.geotoolkit.style.function.Jenks;
import org.geotoolkit.util.logging.Logging;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.Description;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Symbolizer;


/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class JClassificationJenksPanel extends JPanel implements PropertyPane{

    
    private static final Logger LOGGER = Logging.getLogger(JRasterColorMapStylePanel.class);
    
    private final PaletteFactory PF = PaletteFactory.getDefault();
    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    
    private MapLayer layer = null;
    private Integer classNumber = 5;
    private String paletteName = "rainbow";
    private List<String> geotkPalettes;
    
    /**
     * Creates new form JClassificationJenksPanel
     */
    public JClassificationJenksPanel() {
        initComponents();
        
        geotkPalettes = new ArrayList<String>();
        final Set<String> paletteNames = PF.getAvailableNames();
        
        for (String palName : paletteNames) {
            geotkPalettes.add(palName);
        }
        
        guiPalette.setModel(new ListComboBoxModel(geotkPalettes));
        guiPalette.setRenderer(new PaletteCellRenderer());
        guiPalette.setSelectedIndex(0);
        guiClasses.setValue(classNumber);
    }

    private void parse(){
                
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
        
        if(rs != null && rs.getColorMap() != null){
            final Function fct = rs.getColorMap().getFunction();
            if(fct instanceof Jenks){
                final Jenks jenks = (Jenks) fct;
                this.paletteName = jenks.getPalette().getValue().toString();
                this.classNumber = Integer.valueOf(jenks.getClassNumber().getValue().toString());
            }
        }
        if (geotkPalettes.contains(paletteName)) {
            guiPalette.setSelectedIndex(geotkPalettes.indexOf(paletteName));
        }
        guiClasses.setValue(classNumber);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        guiLblPalette = new javax.swing.JLabel();
        guiPalette = new javax.swing.JComboBox();
        guiLblClasses = new javax.swing.JLabel();
        guiClasses = new javax.swing.JSpinner();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(MessageBundle.getString("properties"))); // NOI18N

        guiLblPalette.setText(MessageBundle.getString("palette")); // NOI18N

        guiLblClasses.setText(MessageBundle.getString("classes")); // NOI18N

        guiClasses.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(5), Integer.valueOf(1), null, Integer.valueOf(1)));
        guiClasses.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                guiClassesStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(guiLblPalette)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiPalette, 0, 347, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(guiLblClasses)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiClasses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiPalette, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiLblPalette, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiLblClasses)
                    .addComponent(guiClasses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 189, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiClassesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_guiClassesStateChanged
        classNumber = Integer.valueOf(guiClasses.getValue().toString());
    }//GEN-LAST:event_guiClassesStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner guiClasses;
    private javax.swing.JLabel guiLblClasses;
    private javax.swing.JLabel guiLblPalette;
    private javax.swing.JComboBox guiPalette;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean canHandle(Object target) {
        return target instanceof MapLayer && !(target instanceof FeatureMapLayer);
    }

    @Override
    public void setTarget(Object layer) {
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
        paletteName = String.valueOf(guiPalette.getSelectedItem());
        layer.getStyle().featureTypeStyles().clear();
        
        final Literal fallback = DEFAULT_FALLBACK;
        final Jenks function = SF.jenksFunction(FF.literal(classNumber), FF.literal(paletteName), fallback);

        final ChannelSelection selection = null;
        final Expression opacity = LITERAL_ONE_FLOAT;
        final OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
        final ColorMap colorMap = SF.colorMap(function);
        final ContrastEnhancement enchance = SF.contrastEnhancement(LITERAL_ONE_FLOAT,ContrastMethod.NONE);
        final ShadedRelief relief = SF.shadedRelief(LITERAL_ONE_FLOAT);
        final Symbolizer outline = null;
        final Unit uom = NonSI.PIXEL;
        final String geom = DEFAULT_GEOM;
        final String name = "jenks";
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
        return MessageBundle.getString("property_style_jenks");
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
        return "Dynamic Jenks classifictation";
    }

    @Override
    public Component getComponent() {
        return this;
    }
    
}
