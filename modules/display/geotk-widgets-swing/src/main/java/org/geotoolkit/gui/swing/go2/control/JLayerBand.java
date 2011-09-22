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
import java.awt.Shape;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
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
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.random.RandomStyleBuilder;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.MeasurementRange;
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
import org.opengis.referencing.operation.TransformException;
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
            
            try {
                final GridCoverageReader reader = cml.getCoverageReader();
                final List<String> names = reader.getCoverageNames();           
                
            } catch (CoverageStoreException ex) {
                LOGGER.log(Level.FINE,ex.getMessage(),ex);
            } catch (CancellationException ex) {
                LOGGER.log(Level.FINE,ex.getMessage(),ex);
            }
            Envelope env = layer.getBounds();
            
            try {
                env = CRS.transform(env, axis);
            } catch (TransformException ex) {
                LOGGER.log(Level.FINE,ex.getMessage(),ex);
            }
            
            if(env != null){
                ranges.add(NumberRange.create(env.getMinimum(0), env.getMaximum(0)));
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
        
        final float midHeight = getHeight()/2;
        Double startX = null;
        Double endX = null;
        
        //draw range as a line
        if(ranges != null){
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for(Range<Double> range : ranges){
                startX = getModel().getGraphicValueAt(range.getMinValue());
                endX = getModel().getGraphicValueAt(range.getMaxValue());
                final Shape shape = new java.awt.geom.Line2D.Double(startX, midHeight, endX, midHeight);
                g2d.draw(shape);
            }
        }
        
        
        //draw ponctual values as dots
        if(ponctuals != null){
            g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            for(final Double d : ponctuals){
                double pos = getModel().getGraphicValueAt(d);
                if(startX == null || pos < startX){
                    startX = pos;
                }
                
                final Shape circle = new java.awt.geom.Ellipse2D.Double(pos- circleSize/2, midHeight - circleSize/2, circleSize, circleSize);
                g2d.setColor(Color.WHITE);
                g2d.fill(circle);
                g2d.setColor(color);
                g2d.draw(circle);
            }
        }
        
        
        //name
        if(startX != null){
            if(startX < 0){
                startX = 0d;
            }
            
            g2d.setColor(color);
            final String name = getLayerName();
            final Font f = new Font("Arial", Font.PLAIN, 12);
            final FontMetrics fm = g2d.getFontMetrics(f);
            g2d.setFont(f);
            g2d.drawString(name, startX.floatValue(), midHeight-circleSize/2);
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
