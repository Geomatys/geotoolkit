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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.gui.swing.navigator.JNavigator;
import org.geotoolkit.gui.swing.navigator.JNavigatorBand;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.FeatureMapLayer.DimensionDef;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.LayerListener.Weak;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.RandomStyleBuilder;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.NumberRange;
import org.apache.sis.measure.Range;
import org.geotoolkit.util.SwingEventPassThrough;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Description;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JLayerBand extends JNavigatorBand implements LayerListener{

    private static final Logger LOGGER = Logging.getLogger(JLayerBand.class);

    private final MapLayer layer;
    private Color color = RandomStyleBuilder.randomColor();
    private final float width = 2f;
    private final float circleSize = 8f;

    private boolean analyzed = false;
    private List<Range<Double>> ranges = new ArrayList<Range<Double>>();
    private List<Double> ponctuals = new ArrayList<Double>();
    private final ActionMenu popupmenu = new ActionMenu();
    private final passthrought listener = new passthrought();

    //used by the popup menu
    private Point mousePosition = null;

    public JLayerBand(final MapLayer layer){
        ArgumentChecks.ensureNonNull("layer", layer);
        this.layer = layer;
        layer.addLayerListener(new Weak(this));
        setComponentPopupMenu(popupmenu);
        setMinimumSize(new Dimension(24, 24));
        setPreferredSize(new Dimension(24, 24));
    }

    @Override
    public JPopupMenu getComponentPopupMenu() {
        if(popupmenu.buildElements()){
            //return this popup menu if it contains some elements only
            //otherwise return the parent popup
            return popupmenu;
        }
        return null;
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
            final Envelope env = layer.getBounds();

            Double min = null;
            Double max = null;

            final CoordinateReferenceSystem dataCRS = env.getCoordinateReferenceSystem();
            final List<CoordinateReferenceSystem> sourceParts = ReferencingUtilities.decompose(dataCRS);

            for(CoordinateReferenceSystem sourcePart : sourceParts){

                try {
                    final MathTransform trs = CRS.findMathTransform(sourcePart, axis, true);
                    final CoordinateSystemAxis sourceAxi = sourcePart.getCoordinateSystem().getAxis(0);
                    if(sourceAxi instanceof DiscreteCoordinateSystemAxis){
                        final double[] incoord = new double[1];
                        final double[] outcoord = new double[1];
                        final DiscreteCoordinateSystemAxis dcsa = (DiscreteCoordinateSystemAxis) sourceAxi;
                        for(int k=0;k<dcsa.length();k++){
                            final Comparable c = dcsa.getOrdinateAt(k);
                            final double d;
                            if(c instanceof Number){
                                incoord[0] = ((Number)c).doubleValue();
                                trs.transform(incoord, 0, outcoord, 0, 1);
                                d = outcoord[0];
                            }else if(c instanceof Date){
                                d = ((Date)c).getTime();
                            }else{
                                //not supported
                                continue;
                            }
                            ponctuals.add(d);
                            if(min == null || min>d){
                                min = d;
                            }
                            if(max == null || max<d){
                                max = d;
                            }
                        }
                    }
                } catch (FactoryException ex) {
                    //no match, next one
                } catch (TransformException ex) {
                    //no match, next one
                }
            }

            if(min != null && max != null){
                ranges.add(NumberRange.create(min, max));
            }

        }else if(layer instanceof FeatureMapLayer){
            final FeatureMapLayer fml = (FeatureMapLayer) layer;

            Expression[] er = null;
            for (DimensionDef dimDef : fml.getExtraDimensions()) {
                try {
                    // Test if a math transform can be found.
                    CRS.findMathTransform(axis, dimDef.getCrs());
                    er = new Expression[]{dimDef.getLower(), dimDef.getUpper()};
                    break;
                } catch (FactoryException ex) {
                    // no math transform = nothing to do
                    continue;
                }
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
                }catch(final FeatureStoreRuntimeException ex){
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
        Double startPos = null;
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
                if(startPos == null || startPos>start){
                    startPos = start;
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
                if(startPos == null || pos < startPos){
                    startPos = pos;
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
        if(startPos != null){
            String name = getLayerName();
            if(startPos < 0){
                startPos = 0d;
                name = " <  " + name;
            }
            if(endPos > extent){
                name = name + "  > ";
            }

            final Font f = new Font("Monospaced", Font.PLAIN, 11);
            final FontMetrics fm = g2d.getFontMetrics(f);
            final double strWidth = fm.getStringBounds(name, g2d).getWidth();

            if(startPos+strWidth > extent){
                startPos = extent - strWidth;
            }

            //draw halo
            final GlyphVector glyph = f.createGlyphVector(g2d.getFontRenderContext(), name);
            final Shape shape = glyph.getOutline(startPos.floatValue(), centered-circleSize/2);
            g2d.setPaint(Color.WHITE);
            g2d.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2d.draw(shape);

            //draw text
            g2d.setColor(color);
            g2d.setFont(f);
            g2d.drawString(name, startPos.floatValue(), centered-circleSize/2);
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

    // Forward events to sub components ////////////////////////////////////////

    private class passthrought extends SwingEventPassThrough{

        private passthrought(){
            super(JLayerBand.this);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            JLayerBand.this.mousePosition = e.getPoint();
            super.mouseClicked(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            JLayerBand.this.mousePosition = e.getPoint();
            super.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            JLayerBand.this.mousePosition = e.getPoint();
            super.mouseReleased(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            JLayerBand.this.mousePosition = e.getPoint();
            super.mouseMoved(e);
        }

    }

    private class ActionMenu extends JPopupMenu{

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            removeAll();

            if(visible){
                buildElements();
            }

        }

        public boolean buildElements(){
            ActionMenu.this.removeAll();

            //check if we intersect some data at this position
            requestFocus();
            final Point pt = mousePosition;
            if(pt == null){
                return false;
            }

            final int orientation = getNavigator().getOrientation();
            final boolean horizontal = (orientation==SwingConstants.NORTH || orientation==SwingConstants.SOUTH);


            final float extent =  horizontal ? JLayerBand.this.getWidth() : JLayerBand.this.getHeight();
            final float centered = horizontal ? JLayerBand.this.getHeight()/2 : JLayerBand.this.getWidth()/2;

            final AffineTransform trs = new AffineTransform();
            if(!horizontal){
                //we apply a transform on everything
                trs.translate(JLayerBand.this.getWidth(), 0);
                trs.rotate(Math.toRadians(90));
            }

            for(final Double pc : ponctuals){
                double pos = getModel().getGraphicValueAt(pc);

                if(pos < 0 || pos > extent){
                    continue;
                }

                Shape circle = new java.awt.geom.Ellipse2D.Double(pos- circleSize/2, centered - circleSize/2, circleSize, circleSize);
                circle = trs.createTransformedShape(circle);
                Rectangle rect = circle.getBounds();
                //expend the rectengle for the line width, normaly width/2 should be used
                //but we want to be a bit more tolerant
                rect.x      -= width;
                rect.y      -= width;
                rect.height += width*2;
                rect.width  += width*2;

                if(rect.contains(pt)){
                    ActionMenu.this.add(new AbstractAction(MessageBundle.getString("movetoposition")) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JNavigator navi = JLayerBand.this.getNavigator();
                            if(navi instanceof JMapTimeLine){
                                ((JMapTimeLine)navi).moveTo(new Date(pc.longValue()));
                            }else if(navi instanceof JMapAxisLine){
                                ((JMapAxisLine)navi).moveTo(pc);
                            }
                        }
                    });
                    break;
                }
            }

            ActionMenu.this.revalidate();
            return getComponentCount() > 0;
        }

    }

}
