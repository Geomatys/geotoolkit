/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2010-2011, Geomatys
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
package org.geotoolkit.gui.swing.go2.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingConstants;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.gui.swing.navigator.JNavigatorBand;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.LayerListener.Weak;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.random.RandomStyleBuilder;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.Range;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.style.Description;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JLayerBand extends JNavigatorBand implements LayerListener{

    private static final Logger LOGGER = Logging.getLogger(JLayerBand.class);
    
    private final MapLayer layer;
    private Color color = new RandomStyleBuilder().randomColor();
    private final float width = 2f;
    private final float circleSize = 8f;
    
    private boolean analyzed = false;
    private List<Range<Double>> ranges = new ArrayList<Range<Double>>();
    private List<Double> ponctuals = new ArrayList<Double>();
    
    public JLayerBand(final MapLayer layer){
        ArgumentChecks.ensureNonNull("layer", layer);
        this.layer = layer;
        layer.addLayerListener(new Weak(this));
    }

    public MapLayer getLayer() {
        return layer;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }
    
    private String getLayerName(){
        final Description desc = layer.getDescription();
        if(desc != null){
            final InternationalString title = desc.getTitle();
            if(title != null){
                return title.toString();
            }            
        }
        
        final String name = layer.getName();
        return (name == null)? "" : name;
    }
    
    private void analyze(){
        if(analyzed) return;
        ranges.clear();
        ponctuals.clear();
        
        final CoordinateReferenceSystem axis = getModel().getCRS();
        
        if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final Envelope env = layer.getBounds();                        
            
            final CoordinateSystem cs = env.getCoordinateReferenceSystem().getCoordinateSystem();
            for(int i=0;i<cs.getDimension();i++){
                CoordinateSystemAxis csa = cs.getAxis(i);
                final AxisDirection direction = csa.getDirection();
                
                if(axis instanceof TemporalCRS){
                    if(!(AxisDirection.FUTURE.equals(direction) || AxisDirection.PAST.equals(direction))){
                        csa = null;
                    }
                }else if(axis instanceof VerticalCRS){
                    if(!(AxisDirection.UP.equals(direction) || AxisDirection.DOWN.equals(direction))){
                        csa = null;
                    }
                }
                
                if(csa instanceof DiscreteCoordinateSystemAxis){
                    final DiscreteCoordinateSystemAxis dcsa = (DiscreteCoordinateSystemAxis) csa;
                    for(int k=0;k<dcsa.length();k++){
                        final Comparable c = dcsa.getOrdinateAt(k);
                        final Double d = toValue(c);
                        if(d != null){
                            ponctuals.add(d);
                        }
                    }
                }
            }
                        
        }else if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;
            
            Expression[] er = null;
            if(axis instanceof TemporalCRS){
                er = fml.getTemporalRange().clone();
            }else if(axis instanceof VerticalCRS){
                er = fml.getElevationRange().clone();
            }
            
            //iterate on collection and find values
            if(er != null && (er[0] != null || er[1] != null) ){
                if(er[0] == null){
                    er[0] = er[1];
                }
                if(er[1] == null){
                    er[1] = er[0];
                }
                
                FeatureCollection col = fml.getCollection();
                final QueryBuilder qb = new QueryBuilder(col.getFeatureType().getName());
                qb.setProperties(new String[]{ 
                    ((PropertyName)er[0]).getPropertyName(), 
                    ((PropertyName)er[1]).getPropertyName() });

                FeatureIterator ite = null;
                try{
                    col = col.subCollection(qb.buildQuery());
                    ite = col.iterator();
                    while(ite.hasNext()){
                        final Feature f = ite.next();
                        final Double d1 = toValue(er[0].evaluate(f));
                        final Double d2 = toValue(er[1].evaluate(f));

                        if(d1 != null && d2 == null){
                            ponctuals.add(d1);
                        }else if(d2 != null && d1 == null){
                            ponctuals.add(d2);
                        }else if(d1 != null && d2 != null){
                            if(d1.doubleValue() != d2.doubleValue()){
                                ranges.add(NumberRange.create(d1, d2));
                            }else{
                                ponctuals.add(d1);
                            }
                        }

                    }
                }catch(final DataStoreException ex){
                    LOGGER.log(Level.FINE,ex.getMessage(),ex);
                }catch(final DataStoreRuntimeException ex){
                    LOGGER.log(Level.FINE,ex.getMessage(),ex);
                }finally{
                    if(ite != null){
                        ite.close();
                    }
                }
            }
            
        }
                
        analyzed = true;
    }
    
    private static Double toValue(Object candidate){
        if(candidate instanceof Date){
            return (double)((Date)candidate).getTime();
        }else if(candidate instanceof Number){
            return ((Number)candidate).doubleValue();
        }
        return Converters.convert(candidate, Double.class);
        
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        analyze();        
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        final int orientation = getNavigator().getOrientation();
        final boolean horizontal = (orientation==SwingConstants.NORTH || orientation==SwingConstants.SOUTH);
        
        
        final float extent =  horizontal ? getWidth() : getHeight();
        final float centered = horizontal ? getHeight()/2 : getWidth()/2;
        Double StartPos = null;
        Double endPos = null;
        
        if(!horizontal){
            //we apply a transform on eveyrthing we paint
            g2d.translate(getWidth(), 0);
            g2d.rotate(Math.toRadians(90));
        }
        
        
        //draw range as a line
        if(ranges != null){
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for(Range<Double> range : ranges){
                double start = getModel().getGraphicValueAt(range.getMinValue());
                double end = getModel().getGraphicValueAt(range.getMaxValue());
                if(StartPos == null || StartPos>start){
                    StartPos = start;
                }
                if(endPos==null || endPos<end){
                    endPos = end;
                }
                
                final Shape shape = new java.awt.geom.Line2D.Double(start, centered, end, centered);
                g2d.draw(shape);
            }
        }
        
        
        //draw ponctual values as dots
        if(ponctuals != null){
            g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            for(final Double d : ponctuals){
                double pos = getModel().getGraphicValueAt(d);
                if(StartPos == null || pos < StartPos){
                    StartPos = pos;
                }
                if(endPos == null || pos > endPos){
                    endPos = pos;
                }
                
                final Shape circle = new java.awt.geom.Ellipse2D.Double(pos- circleSize/2, centered - circleSize/2, circleSize, circleSize);
                
                g2d.setColor(Color.WHITE);
                g2d.fill(circle);
                g2d.setColor(color);
                g2d.draw(circle);
            }
        }
                
        //name
        if(StartPos != null){
            String name = getLayerName();
            if(StartPos-20 < 0){
                StartPos = 20d;
                name = " ❮❮ " + name;
            }
            if(endPos > extent){
                endPos = 0d;
                name = name + " ❯❯ ";
            }
                        
            final Font f = new Font("Dialog", Font.BOLD, 12);
            final FontMetrics fm = g2d.getFontMetrics();
            final double strWidth = fm.getStringBounds(name, g2d).getWidth() + 20; //20 to keep it far from border
            
            if(StartPos+strWidth > extent){
                StartPos = extent - strWidth;
            }
            
            //draw halo
            final GlyphVector glyph = f.createGlyphVector(g2d.getFontRenderContext(), name);
            final Shape shape = glyph.getOutline(StartPos.floatValue(), centered-circleSize/2);
            g2d.setPaint(Color.WHITE);
            g2d.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2d.draw(shape);
            
            //draw text
            g2d.setColor(color);
            g2d.setFont(f);
            g2d.drawString(name, StartPos.floatValue(), centered-circleSize/2);
        }
        
    }

    
    // listen to later changes /////////////////////////////////////////////////
    @Override
    public void styleChange(MapLayer source, EventObject event) {}

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        analyzed = false;
    }
    
}
