/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011 Geomatys
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
package org.geotoolkit.gui.swing.propertyedit.styleproperty.simple;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.measure.unit.Unit;
import javax.swing.JLabel;
import org.geotoolkit.gui.swing.style.JNumberExpressionPane;
import org.geotoolkit.gui.swing.style.JNumberSliderExpressionPane;
import org.geotoolkit.gui.swing.style.JUOMPane;
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
 * @author Fabien Rétif
 */
public class JPointSymbolizerPane extends StyleElementEditor<PointSymbolizer> {

    private MapLayer layer = null;

    public JPointSymbolizerPane() {
        super(PointSymbolizer.class);
        initComponents();
        init();
    }
    
    /**
     * Sets range for number component (size, rotation, opacity, displacement)
     */
     private void init() {        
        guiRotation.setModel(1, 0, 360, 1);
        guiSize.setModel(1, 0, 200, 1);
        guiOpacity.setModel(99, 0, 100, 1);
        guiDisplacementY.setModel(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1d);
        guiDisplacementX.setModel(0d, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1d);
        guiDisplacementY.setExpressionUnvisible();
        guiDisplacementX.setExpressionUnvisible();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        guiDisplacementY.setLayer(layer);
        guiDisplacementX.setLayer(layer);
        guiFillControlPane.setLayer(layer);
        guiRotation.setLayer(layer);
        guiSize.setLayer(layer);
        guiStrokeControlPane.setLayer(layer);
        guiUOM.setLayer(layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer() {
        return layer;
    }

    /**
     * This method parses a PointSymbolizer object. This object can contains many Graphical Symbols but here we considered that only one is used.
     * {@inheritDoc }
     */
    @Override
    public void parse(final PointSymbolizer symbol) {
        if (symbol instanceof PointSymbolizer) {
            
            guiSize.parse(symbol.getGraphic().getSize());
            guiRotation.parse((symbol.getGraphic().getRotation()));            

            guiDisplacementY.parse(symbol.getGraphic().getDisplacement().getDisplacementY());
            guiDisplacementX.parse(symbol.getGraphic().getDisplacement().getDisplacementX());
//            guiGeom.setGeom(symbol.getGeometryPropertyName());
            guiUOM.parse(symbol.getUnitOfMeasure());
            
            //Parsing the first graphic symbol
            Iterator<GraphicalSymbol> iterGraphic = symbol.getGraphic().graphicalSymbols().iterator();
                        
            if(iterGraphic.hasNext())
            {
                GraphicalSymbol gs = iterGraphic.next();              
                
                guiGraphicalSymbolControlPane.parse(gs);           
                             
                if (gs instanceof Mark)
                {
                    guiFillControlPane.setActive(true);
                    guiStrokeControlPane.setActive(true);
                    
                    Mark mark = (Mark) gs;                   
                    guiFillControlPane.parse(mark.getFill());
                    guiStrokeControlPane.parse(mark.getStroke());                    
                }                
                else if (gs instanceof ExternalGraphic)
                {
                    guiFillControlPane.setActive(false);
                    guiStrokeControlPane.setActive(false);       
                    
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
        final Unit unit = guiUOM.create();
        final Expression offset = StyleConstants.LITERAL_ONE_FLOAT;                 
        
        //the visual element
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        final Stroke stroke = guiStrokeControlPane.create();
        final Fill fill = guiFillControlPane.create();
        final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        final Displacement disp = getStyleFactory().displacement(guiDisplacementX.create(), guiDisplacementY.create());
        final Double alpha = guiOpacity.create().evaluate(null, Double.class);
        final Expression opacity = getFilterFactory().literal(alpha/100.d);
        
        final GraphicalSymbol graphicalSymbol = guiGraphicalSymbolControlPane.create();
        GraphicalSymbol finalGraphicalSymbol = null;
        
        if (graphicalSymbol instanceof Mark)
        {
            finalGraphicalSymbol = getStyleFactory().mark(((Mark)graphicalSymbol).getWellKnownName(), stroke, fill);            
        }
        else
        {
            finalGraphicalSymbol = graphicalSymbol;
        }
                
        symbols.add(finalGraphicalSymbol);
        final Graphic graphic = getStyleFactory().graphic(symbols, opacity, guiSize.create(), guiRotation.create(), anchor, disp);           

        return getStyleFactory().pointSymbolizer(name, geometry, desc, unit, graphic);      
        
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
        jLabel6 = new JLabel();
        guiDisplacementX = new JNumberExpressionPane();
        jLabel8 = new JLabel();
        guiUOM = new JUOMPane();

        setBackground(new Color(204, 204, 204));

        jLabel1.setText("Type de forme :");
        add(jLabel1);

        jLabel2.setText("Remplissage de la forme :");
        add(jLabel2);

        jLabel10.setText("Bordure de la forme :");
        add(jLabel10);

        jLabel4.setText("Taille :");
        add(jLabel4);

        guiSize.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerPane.this.propertyChange(evt);
            }
        });
        add(guiSize);

        jLabel7.setText("Angle :");
        add(jLabel7);

        guiRotation.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerPane.this.propertyChange(evt);
            }
        });
        add(guiRotation);

        jLabel3.setText("Opacité :");
        add(jLabel3);

        guiOpacity.setPreferredSize(new Dimension(100, 30));
        guiOpacity.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerPane.this.propertyChange(evt);
            }
        });
        add(guiOpacity);

        jLabel5.setText("Décallage X :");
        add(jLabel5);

        guiDisplacementY.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerPane.this.propertyChange(evt);
            }
        });
        add(guiDisplacementY);

        jLabel6.setText("Unité :");
        add(jLabel6);

        guiDisplacementX.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JPointSymbolizerPane.this.propertyChange(evt);
            }
        });
        add(guiDisplacementX);

        jLabel8.setText("Décallage Y :");
        add(jLabel8);
        add(guiUOM);
    }// </editor-fold>//GEN-END:initComponents

    private void propertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_propertyChange
        // TODO add your handling code here:
        if (PROPERTY_TARGET.equalsIgnoreCase(evt.getPropertyName())) {            
            firePropertyChange(PROPERTY_TARGET, null, create());
            parse(create());
        }
    }//GEN-LAST:event_propertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JNumberExpressionPane guiDisplacementX;
    private JNumberExpressionPane guiDisplacementY;
    private JNumberSliderExpressionPane guiOpacity;
    private JNumberSliderExpressionPane guiRotation;
    private JNumberSliderExpressionPane guiSize;
    private JUOMPane guiUOM;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    // End of variables declaration//GEN-END:variables

    public void componentHidden(ComponentEvent e) {
        System.out.println(e.getComponent().getClass().getName() + " --- Hidden");
    }

}
