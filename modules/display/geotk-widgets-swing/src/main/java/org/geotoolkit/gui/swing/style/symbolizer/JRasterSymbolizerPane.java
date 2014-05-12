/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JColorMapPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JChannelSelectionPane;
import org.geotoolkit.gui.swing.style.JContrastEnhancement;
import org.geotoolkit.gui.swing.style.JGeomPane;
import org.geotoolkit.gui.swing.style.JNumberExpressionPane;
import org.geotoolkit.gui.swing.style.JShadedReliefPane;
import org.geotoolkit.gui.swing.style.JTextExpressionPane;
import org.geotoolkit.gui.swing.style.JUOMPane;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import static org.geotoolkit.style.StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Symbolizer;

/**
 * Raster symbolizer editor.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JRasterSymbolizerPane extends StyleElementEditor<RasterSymbolizer> {

    private MapLayer layer = null;
    private RasterSymbolizer oldSymbolizer;

    private final JChannelSelectionPane guiChannelPane = new JChannelSelectionPane();
    private final JColorMapPane guiColorMapPane = new JColorMapPane();
    private final JLineSymbolizerPane guiLinePane = new JLineSymbolizerPane();
    private final JPolygonSymbolizerPane guiPolygonPane = new JPolygonSymbolizerPane();
    
    private final String cmRGB = MessageBundle.getString("style.rastersymbolizer.cm_rgb");
    private final String cmColorMap = MessageBundle.getString("style.rastersymbolizer.cm_colormap");
    
    public JRasterSymbolizerPane() {
        super(RasterSymbolizer.class);
        initComponents();
        
        guiColorCombo.setModel(new ListComboBoxModel(Arrays.asList("-",cmRGB,cmColorMap)));
        guiColorCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                guiColorimetryPane.removeAll();
                if(cmRGB.equals(guiColorCombo.getSelectedItem())){
                    guiColorimetryPane.add(guiChannelPane, BorderLayout.CENTER);
                }else if(cmColorMap.equals(guiColorCombo.getSelectedItem())){
                    guiColorimetryPane.add(guiColorMapPane, BorderLayout.CENTER);
                }
                guiColorimetryPane.revalidate();
                guiColorimetryPane.repaint();
            }
        });
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        guiOpacity.setLayer(layer);
        guiGeom.setLayer(layer);
        guiOverLap.setLayer(layer);
        guiContrast.setLayer(layer);
        guiRelief.setLayer(layer);
        guiChannelPane.setLayer(layer);
        guiColorMapPane.setLayer(layer);
        guiLinePane.setLayer(layer);
        guiPolygonPane.setLayer(layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer() {
        return layer;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void parse(final RasterSymbolizer symbol) {
        this.oldSymbolizer = symbol;
        
        if (symbol != null) {
            guiGeom.setGeom(symbol.getGeometryPropertyName());
            guiUOM.parse(symbol.getUnitOfMeasure());
            guiOpacity.parse(symbol.getOpacity());
            //guiOverLap.parse(symbol.getOverlapBehavior());
            guiContrast.parse(symbol.getContrastEnhancement());
            guiRelief.parse(symbol.getShadedRelief());
            guiChannelPane.parse(symbol.getChannelSelection());
            guiColorMapPane.parse(symbol.getColorMap());
            
            final Symbolizer outLine = symbol.getImageOutline();
            if(outLine instanceof LineSymbolizer){
                guiLine.setSelected(true);
                guiLinePane.parse((LineSymbolizer)outLine);
            }else if(outLine instanceof PolygonSymbolizer){
                guiPolygon.setSelected(true);
                guiPolygonPane.parse((PolygonSymbolizer)outLine);
            }else{
                guinone.setSelected(true);
            }
            
            if(symbol.getColorMap()!=null && symbol.getColorMap().getFunction()!=null){
                guiColorCombo.getModel().setSelectedItem(cmColorMap);
            }else{
                guiColorCombo.getModel().setSelectedItem(cmRGB);
            }
            
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public RasterSymbolizer create() {
        Symbolizer outline = null;
        if(guiLine.isSelected()){
            outline = guiLinePane.create();
        }else if(guiPolygon.isSelected()){
            outline = guiPolygonPane.create();
        }
        
        final ChannelSelection chanSelect;
        final ColorMap colorMap;
        if(cmRGB.equals(guiColorCombo.getSelectedItem())){
            chanSelect = guiChannelPane.create();
            colorMap = null;
        }else if(cmColorMap.equals(guiColorCombo.getSelectedItem())){
            chanSelect = getStyleFactory().channelSelection(getStyleFactory()
                    .selectedChannelType(guiColorMapPane.getSelectedBand(),DEFAULT_CONTRAST_ENHANCEMENT));
            colorMap = guiColorMapPane.create();
        }else{
            chanSelect = null;
            colorMap = null;
        }
        
        return getStyleFactory().rasterSymbolizer(
                (oldSymbolizer!=null) ? oldSymbolizer.getName(): "RasterSymbolizer",
                guiGeom.getGeom(),
                (oldSymbolizer!=null) ? oldSymbolizer.getDescription() : StyleConstants.DEFAULT_DESCRIPTION,
                guiUOM.create(),
                guiOpacity.create(),
                chanSelect,
                (oldSymbolizer!=null) ? oldSymbolizer.getOverlapBehavior() : OverlapBehavior.AVERAGE, 
                colorMap, 
                guiContrast.create(), 
                guiRelief.create(), 
                outline);
    
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpOutline = new ButtonGroup();
        jPanel1 = new JPanel();
        guiOverLap = new JTextExpressionPane();
        jLabel2 = new JLabel();
        jLabel1 = new JLabel();
        guiOpacity = new JNumberExpressionPane();
        guiUOM = new JUOMPane();
        guiGeom = new JGeomPane();
        jTabbedPane1 = new JTabbedPane();
        jPanel3 = new JPanel();
        jLabel4 = new JLabel();
        guiColorCombo = new JComboBox();
        guiColorimetryPane = new JPanel();
        jPanel2 = new JPanel();
        guinone = new JRadioButton();
        guiLine = new JRadioButton();
        guiPolygon = new JRadioButton();
        guiOutlinePane = new JPanel();
        guiContrast = new JContrastEnhancement();
        guiRelief = new JShadedReliefPane();

        setOpaque(false);

        jPanel1.setBorder(BorderFactory.createTitledBorder(MessageBundle.getString("style.rastersymbolizer.general"))); // NOI18N
        jPanel1.setOpaque(false);

        guiOverLap.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JRasterSymbolizerPane.this.propertyChange(evt);
            }
        });

        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText(MessageBundle.getString("style.rastersymbolizer.overlap")); // NOI18N

        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText(MessageBundle.getString("style.rastersymbolizer.opacity")); // NOI18N

        guiOpacity.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JRasterSymbolizerPane.this.propertyChange(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiOpacity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiOverLap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(guiGeom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(guiUOM, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiUOM, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiGeom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(guiOpacity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiOverLap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, Alignment.TRAILING))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiOpacity, guiOverLap, jLabel1, jLabel2});

        jTabbedPane1.setTabPlacement(JTabbedPane.LEFT);

        jLabel4.setText(MessageBundle.getString("style.rastersymbolizer.colormodel")); // NOI18N

        guiColorimetryPane.setLayout(new BorderLayout());

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiColorimetryPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiColorCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 212, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(guiColorCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiColorimetryPane, GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(MessageBundle.getString("colors"), jPanel3); // NOI18N

        jPanel2.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel2.setOpaque(false);

        grpOutline.add(guinone);
        guinone.setSelected(true);
        guinone.setText(MessageBundle.getString("style.rastersymbolizer.none")); // NOI18N
        guinone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guinoneActionPerformed(evt);
            }
        });

        grpOutline.add(guiLine);
        guiLine.setText(MessageBundle.getString("style.rastersymbolizer.line")); // NOI18N
        guiLine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guinoneActionPerformed(evt);
            }
        });

        grpOutline.add(guiPolygon);
        guiPolygon.setText(MessageBundle.getString("style.rastersymbolizer.polygon")); // NOI18N
        guiPolygon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guinoneActionPerformed(evt);
            }
        });

        guiOutlinePane.setLayout(new BorderLayout());

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guinone)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiLine)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiPolygon)
                .addContainerGap(126, Short.MAX_VALUE))
            .addComponent(guiOutlinePane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiLine, guiPolygon, guinone});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guinone)
                    .addComponent(guiLine)
                    .addComponent(guiPolygon))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(guiOutlinePane, GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(MessageBundle.getString("style.rastersymbolizer.outline"), jPanel2); // NOI18N

        guiContrast.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        guiContrast.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JRasterSymbolizerPane.this.propertyChange(evt);
            }
        });
        jTabbedPane1.addTab(MessageBundle.getString("contrast"), guiContrast); // NOI18N

        guiRelief.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        guiRelief.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JRasterSymbolizerPane.this.propertyChange(evt);
            }
        });
        jTabbedPane1.addTab(MessageBundle.getString("relief"), guiRelief); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guinoneActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guinoneActionPerformed
        guiOutlinePane.removeAll();
        if(guiLine.isSelected()){
            guiOutlinePane.add(guiLinePane, BorderLayout.CENTER);
        }else if(guiPolygon.isSelected()){
            guiOutlinePane.add(guiPolygonPane, BorderLayout.CENTER);
        }
        guiOutlinePane.revalidate();
        guiOutlinePane.repaint();
        
    }//GEN-LAST:event_guinoneActionPerformed

    private void propertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_propertyChange
        if (PROPERTY_TARGET.equalsIgnoreCase(evt.getPropertyName())) {            
            firePropertyChange(PROPERTY_TARGET, null, create());
            parse(create());
        }
    }//GEN-LAST:event_propertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ButtonGroup grpOutline;
    private JComboBox guiColorCombo;
    private JPanel guiColorimetryPane;
    private JContrastEnhancement guiContrast;
    private JGeomPane guiGeom;
    private JRadioButton guiLine;
    private JNumberExpressionPane guiOpacity;
    private JPanel guiOutlinePane;
    private JTextExpressionPane guiOverLap;
    private JRadioButton guiPolygon;
    private JShadedReliefPane guiRelief;
    private JUOMPane guiUOM;
    private JRadioButton guinone;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel4;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
    
}
