/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 Geomatys
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.measure.Unit;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.simple.JFillControlPane;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.simple.JStrokeControlPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JNumberExpressionPane;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import static org.geotoolkit.gui.swing.style.StyleElementEditor.PROPERTY_UPDATED;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.Description;
import org.opengis.style.PolygonSymbolizer;
import org.apache.sis.measure.Units;

/**
 * PolygonSymbolizer editor.
 *
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JPolygonSymbolizerSimple extends StyleElementEditor<PolygonSymbolizer> implements PropertyChangeListener {

    private MapLayer layer = null;
    private PolygonSymbolizer oldSymbolizer = null;

    /**
     * Creates new form JPolygonSymbolizerPane and sets range of number component
     */
    public JPolygonSymbolizerSimple() {
        super(PolygonSymbolizer.class);
        initComponents();
        guiOffset.setModel(0d, 0d, Double.POSITIVE_INFINITY, 1d);
        guiDisplacementX.setModel(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1d);
        guiDisplacementY.setModel(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1d);
        guiDisplacementX.setExpressionVisible(false);
        guiDisplacementY.setExpressionVisible(false);
        guiOffset.setExpressionVisible(false);

        guiFillControlPane.addPropertyChangeListener(this);
        guiStrokeControlPane.addPropertyChangeListener(this);
        guiOffset.addPropertyChangeListener(this);
        guiDisplacementX.addPropertyChangeListener(this);
        guiDisplacementY.addPropertyChangeListener(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer){
        this.layer = layer;

        guiFillControlPane.setLayer(layer);
        guiDisplacementY.setLayer(layer);
        guiDisplacementX.setLayer(layer);
        guiStrokeControlPane.setLayer(layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer(){
        return layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void parse(final PolygonSymbolizer symbol) {
        oldSymbolizer = symbol;
        if (symbol != null) {
            guiStrokeControlPane.parse(symbol.getStroke());
            guiFillControlPane.parse(symbol.getFill());
            guiDisplacementX.parse(symbol.getDisplacement().getDisplacementX());
            guiDisplacementY.parse(symbol.getDisplacement().getDisplacementY());
            guiOffset.parse(symbol.getPerpendicularOffset());
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PolygonSymbolizer create(){
        String name = "polygonSymbolizer";
        String geomName = null;
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;
        Unit unit = Units.POINT;
        if(oldSymbolizer!=null){
            name = oldSymbolizer.getName();
            geomName = oldSymbolizer.getGeometryPropertyName();
            desc = oldSymbolizer.getDescription();
            unit = oldSymbolizer.getUnitOfMeasure();
        }

        return getStyleFactory().polygonSymbolizer(
                    name,
                    geomName,
                    desc,
                    unit,
                    guiStrokeControlPane.create(),
                    guiFillControlPane.create(),
                    getStyleFactory().displacement(guiDisplacementX.create(),guiDisplacementY.create()),
                    guiOffset.create());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROPERTY_UPDATED.equalsIgnoreCase(evt.getPropertyName())) {
            firePropertyChange(PROPERTY_UPDATED, null, create());
        }
    }

    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{};
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel10 = new JLabel();
        guiDisplacementX = new JNumberExpressionPane();
        guiDisplacementY = new JNumberExpressionPane();
        guiOffset = new JNumberExpressionPane();
        guiOffsetLabel = new JLabel();
        guiStrokeControlPane = new JStrokeControlPane();
        guiFillControlPane = new JFillControlPane();

        jLabel2.setText(MessageBundle.format("shapeFill")); // NOI18N

        jLabel5.setText(MessageBundle.format("displacementX")); // NOI18N

        jLabel6.setText(MessageBundle.format("displacementY")); // NOI18N

        jLabel10.setText(MessageBundle.format("shapeBorder")); // NOI18N

        guiOffsetLabel.setText(MessageBundle.format("offset")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiStrokeControlPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiFillControlPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(guiOffsetLabel))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(guiOffset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(guiDisplacementX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(guiDisplacementY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiDisplacementX, guiDisplacementY, guiOffset});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiOffsetLabel, jLabel10, jLabel2, jLabel5, jLabel6});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(guiFillControlPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(guiStrokeControlPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiOffsetLabel)
                    .addComponent(guiOffset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(guiDisplacementX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiDisplacementY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiOffset, guiOffsetLabel});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiDisplacementX, jLabel5});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiDisplacementY, jLabel6});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiFillControlPane, jLabel2});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiStrokeControlPane, jLabel10});

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JNumberExpressionPane guiDisplacementX;
    private JNumberExpressionPane guiDisplacementY;
    private JFillControlPane guiFillControlPane;
    private JNumberExpressionPane guiOffset;
    private JLabel guiOffsetLabel;
    private JStrokeControlPane guiStrokeControlPane;
    private JLabel jLabel10;
    private JLabel jLabel2;
    private JLabel jLabel5;
    private JLabel jLabel6;
    // End of variables declaration//GEN-END:variables

}
