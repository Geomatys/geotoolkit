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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.simple.JFillControlPane;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.simple.JGraphicSymbolControlPane;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.simple.JStrokeControlPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JNumberExpressionPane;
import org.geotoolkit.gui.swing.style.JNumberSliderExpressionPane;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.Stroke;

/**
 * This class ables to display PointSymbolizer tool pane
 *
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JPointSymbolizerSimple extends StyleElementEditor<PointSymbolizer> {

    private MapLayer layer = null;

    public JPointSymbolizerSimple() {
        super(PointSymbolizer.class);
        initComponents();
        init();
    }

    /**
     * Sets range for number component (size, rotation, opacity, displacement)
     */
    private void init() {
        guiRotation.setModel(1, 0, 360, 1);
        guiSize.setModel(1, 0, 100, 1);
        guiOpacity.setModel(99, 0, 100, 1);
        guiDisplacementY.setModel(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1d);
        guiDisplacementX.setModel(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1d);
        guiDisplacementY.setExpressionVisible(false);
        guiDisplacementX.setExpressionVisible(false);
        guiSize.setExpressionVisible(false);
        guiRotation.setExpressionVisible(false);
        guiOpacity.setExpressionVisible(false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        guiDisplacementY.setLayer(layer);
        guiDisplacementX.setLayer(layer);
        guiFill.setLayer(layer);
        guiRotation.setLayer(layer);
        guiSize.setLayer(layer);
        guiStroke.setLayer(layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer() {
        return layer;
    }

    /**
     * This method parses a PointSymbolizer object. This object can contains
     * many Graphical Symbols but here we considered that only one is used.
     * {@inheritDoc }
     */
    @Override
    public void parse(final PointSymbolizer symbol) {
        if (symbol instanceof PointSymbolizer) {

            guiSize.parse(symbol.getGraphic().getSize());
            guiRotation.parse((symbol.getGraphic().getRotation()));

            guiDisplacementY.parse(symbol.getGraphic().getDisplacement().getDisplacementY());
            guiDisplacementX.parse(symbol.getGraphic().getDisplacement().getDisplacementX());

            //Parsing the first graphic symbol
            Iterator<GraphicalSymbol> iterGraphic = symbol.getGraphic().graphicalSymbols().iterator();

            if (iterGraphic.hasNext()) {
                GraphicalSymbol gs = iterGraphic.next();

                guiGraphicSymbol.parse(gs);

                if (gs instanceof Mark) {
                    guiFill.setActive(true);
                    guiStroke.setActive(true);

                    Mark mark = (Mark) gs;
                    guiFill.parse(mark.getFill());
                    guiStroke.parse(mark.getStroke());
                } else if (gs instanceof ExternalGraphic) {
                    guiFill.setActive(false);
                    guiStroke.setActive(false);

                }
            }

        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PointSymbolizer create() {        
        final String name = "mySymbol";
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = NonSI.PIXEL;
        final Expression offset = StyleConstants.LITERAL_ONE_FLOAT;

        //the visual element
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        final Displacement disp = getStyleFactory().displacement(guiDisplacementX.create(), guiDisplacementY.create());
        final Double alpha = guiOpacity.create().evaluate(null, Double.class);
        final Expression opacity = getFilterFactory().literal(alpha / 100.d);

        final GraphicalSymbol graphicalSymbol = guiGraphicSymbol.create();
        
        final GraphicalSymbol finalGraphicalSymbol;
        if (graphicalSymbol instanceof Mark) {
            final Stroke stroke = guiStroke.create();
            final Fill fill = guiFill.create();
            finalGraphicalSymbol = getStyleFactory().mark(((Mark) graphicalSymbol).getWellKnownName(), stroke, fill);
        } else {
            finalGraphicalSymbol = graphicalSymbol;
        }

        symbols.add(finalGraphicalSymbol);
        final Graphic graphic = getStyleFactory().graphic(symbols, opacity, guiSize.create(), guiRotation.create(), anchor, disp);

        return getStyleFactory().pointSymbolizer(name, geometry, desc, unit, graphic);
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

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel10 = new JLabel();
        jLabel4 = new JLabel();
        guiSize = new JNumberSliderExpressionPane();
        jLabel7 = new JLabel();
        guiRotation = new JNumberSliderExpressionPane();
        jLabel3 = new JLabel();
        guiOpacity = new JNumberSliderExpressionPane();
        jLabel5 = new JLabel();
        guiDisplacementY = new JNumberExpressionPane();
        guiDisplacementX = new JNumberExpressionPane();
        jLabel8 = new JLabel();
        guiGraphicSymbol = new JGraphicSymbolControlPane();
        guiStroke = new JStrokeControlPane();
        guiFill = new JFillControlPane();

        jLabel1.setText(MessageBundle.getString("type")); // NOI18N

        jLabel2.setText(MessageBundle.getString("fill")); // NOI18N

        jLabel10.setText(MessageBundle.getString("border")); // NOI18N

        jLabel4.setText(MessageBundle.getString("size")); // NOI18N

        guiSize.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerSimple.this.propertyChange(evt);
            }
        });

        jLabel7.setText(MessageBundle.getString("rotation")); // NOI18N

        guiRotation.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerSimple.this.propertyChange(evt);
            }
        });

        jLabel3.setText(MessageBundle.getString("opacity")); // NOI18N

        guiOpacity.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerSimple.this.propertyChange(evt);
            }
        });

        jLabel5.setText(MessageBundle.getString("displacementX")); // NOI18N

        guiDisplacementY.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerSimple.this.propertyChange(evt);
            }
        });

        guiDisplacementX.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerSimple.this.propertyChange(evt);
            }
        });

        jLabel8.setText(MessageBundle.getString("displacementY")); // NOI18N

        guiGraphicSymbol.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerSimple.this.propertyChange(evt);
            }
        });

        guiStroke.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerSimple.this.propertyChange(evt);
            }
        });

        guiFill.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerSimple.this.propertyChange(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiGraphicSymbol, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiFill, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiStroke, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiOpacity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiDisplacementX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(guiDisplacementY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel3, jLabel4, jLabel5, jLabel7, jLabel8});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel1, jLabel10, jLabel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(guiGraphicSymbol, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(guiFill, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(guiStroke, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(jLabel4, Alignment.TRAILING)
                    .addComponent(guiSize, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(guiRotation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(guiOpacity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(guiDisplacementX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(guiDisplacementY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiGraphicSymbol, jLabel1});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiFill, jLabel2});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiStroke, jLabel10});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiSize, jLabel4});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiRotation, jLabel7});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiOpacity, jLabel3});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiDisplacementX, jLabel5});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiDisplacementY, jLabel8});

    }// </editor-fold>//GEN-END:initComponents

    private void propertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_propertyChange
        if (PROPERTY_TARGET.equalsIgnoreCase(evt.getPropertyName())) {
            firePropertyChange(PROPERTY_TARGET, null, create());
            parse(create());
        }
    }//GEN-LAST:event_propertyChange
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JNumberExpressionPane guiDisplacementX;
    private JNumberExpressionPane guiDisplacementY;
    private JFillControlPane guiFill;
    private JGraphicSymbolControlPane guiGraphicSymbol;
    private JNumberSliderExpressionPane guiOpacity;
    private JNumberSliderExpressionPane guiRotation;
    private JNumberSliderExpressionPane guiSize;
    private JStrokeControlPane guiStroke;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel7;
    private JLabel jLabel8;
    // End of variables declaration//GEN-END:variables

}
