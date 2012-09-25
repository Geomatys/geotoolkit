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
package org.geotoolkit.gui.swing.style;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.swing.JComponent;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.PointPlacement;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.TextSymbolizer;

/**
 * This component ables to display preview of SLD norm object (PointSymbolizer,
 * Fill, Stroke...)
 *
 * @author Fabien Rétif (Geomatys)
 */
public class JPreview extends JComponent implements ComponentListener {

    private BufferedImage image = null;
    private final double[] disp = new double[] {0d,0d};
    /**
     * Display or not mir
     */
    private boolean mir = false;
    private Object targetObj = null;

    public JPreview() {
        this.addComponentListener(this);
    }

    public void setMir(boolean mir) {
        this.mir = mir;
    }

    public boolean isMir() {
        return mir;
    }

    public Object getTarget() {
        return targetObj;
    }   
    

    /**
     * This methods parses settled object and tries to create a preview of it
     *
     * @param obj Parsing object
     */
    public void parse(final Object obj) {
        if(this.targetObj != null && this.targetObj.equals(obj)){
            //same object
            return;
        }else if(this.targetObj == null && obj == null){
            //both null
            return;
        }
        
        this.targetObj = obj;
        image = null;
        repaint();
    }

    private void updateImage() {
        
        //There is already an image or there is no object to draw => exit
        if (image != null || this.targetObj == null) {
            return;
        }

        int glyphSize = Math.min(getWidth(), getHeight());       
        glyphSize = Math.max(24, glyphSize);
        final Dimension dim = new Dimension(glyphSize, glyphSize);  

        image = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Rectangle rect = new Rectangle(dim);
        final Graphics2D g = image.createGraphics();
        
        this.disp[0] = 0d;
        this.disp[1] = 0d;

        try {           
            // Main type
            if (this.targetObj instanceof PointSymbolizer) {
                DefaultGlyphService.render((PointSymbolizer) targetObj, rect, g, null);   
                this.disp[0] = ((PointSymbolizer) targetObj).getGraphic().getDisplacement().getDisplacementX().evaluate(null, Double.class);
                this.disp[1] = ((PointSymbolizer) targetObj).getGraphic().getDisplacement().getDisplacementY().evaluate(null, Double.class);

            } else if (targetObj instanceof LineSymbolizer) {
                DefaultGlyphService.render((LineSymbolizer) targetObj, rect, g, null);
                this.disp[0] = ((LineSymbolizer) targetObj).getPerpendicularOffset().evaluate(null, Double.class);               

            } else if (targetObj instanceof PolygonSymbolizer) {
                DefaultGlyphService.render((PolygonSymbolizer) targetObj, rect, g, null);
                this.disp[0] = ((PolygonSymbolizer) targetObj).getDisplacement().getDisplacementX().evaluate(null, Double.class);
                this.disp[1] = ((PolygonSymbolizer) targetObj).getDisplacement().getDisplacementY().evaluate(null, Double.class);

            } else if (targetObj instanceof TextSymbolizer) {
                DefaultGlyphService.render((TextSymbolizer) targetObj, rect, g, null);
                
                if(((TextSymbolizer) targetObj).getLabelPlacement() instanceof  PointPlacement) {
                    PointPlacement pp = (PointPlacement) ((TextSymbolizer) targetObj).getLabelPlacement();
                    this.disp[0] = pp.getDisplacement().getDisplacementX().evaluate(null, Double.class);
                    this.disp[1] = pp.getDisplacement().getDisplacementY().evaluate(null, Double.class);
                }

            } else if (targetObj instanceof RasterSymbolizer) {
                DefaultGlyphService.render((RasterSymbolizer) targetObj, rect, g, null); 
            } 
            // Sub-type
            else if (targetObj instanceof GraphicalSymbol) {
                final String name = "mySymbol";
                final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
                final String geometry = null; //use the default geometry of the feature
                final Unit unit = NonSI.PIXEL;
                final Expression offset = StyleConstants.LITERAL_ONE_FLOAT;

                //the visual element
                final Expression size = GO2Utilities.FILTER_FACTORY.literal(12);
                final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
                final Expression rotation = StyleConstants.LITERAL_ONE_FLOAT;
                final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
                final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;
                
                final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();

                symbols.add((GraphicalSymbol) targetObj);
                final Graphic graphic = GO2Utilities.STYLE_FACTORY.graphic(symbols, opacity, size, rotation, anchor, disp);

                final PointSymbolizer symbolizer = GO2Utilities.STYLE_FACTORY.pointSymbolizer(name, geometry, desc, unit, graphic);
                DefaultGlyphService.render(symbolizer, rect, g, null);               
                
               
            } else if (targetObj instanceof Stroke) {
                final String name = "mySymbol";
                final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
                final String geometry = null; //use the default geometry of the feature
                final Unit unit = NonSI.PIXEL;
                final Expression offset = StyleConstants.LITERAL_ONE_FLOAT;

                final LineSymbolizer symbolizer = GO2Utilities.STYLE_FACTORY.lineSymbolizer(name, geometry, desc, unit, (Stroke) targetObj, offset);

                DefaultGlyphService.render(symbolizer, rect, g, null);
                
            } else if (targetObj instanceof Fill) {
                final String name = "mySymbol";
                final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
                final String geometry = null; //use the default geometry of the feature
                final Unit unit = NonSI.PIXEL;
                final Expression offset = StyleConstants.LITERAL_ONE_FLOAT;

                //the visual element
                final Expression size = GO2Utilities.FILTER_FACTORY.literal(32);
                final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
                final Expression rotation = StyleConstants.LITERAL_ONE_FLOAT;
                final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
                final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;

                //the visual element
                final Expression color = GO2Utilities.STYLE_FACTORY.literal(Color.BLACK);
                final Expression width = GO2Utilities.FILTER_FACTORY.literal(2);
                final Expression linecap = StyleConstants.STROKE_CAP_BUTT;
                final Expression linejoin = StyleConstants.STROKE_JOIN_ROUND;
                final Expression dashOffset = StyleConstants.LITERAL_ZERO_FLOAT;
                final Stroke stroke = GO2Utilities.STYLE_FACTORY.stroke(color, opacity, width, linejoin, linecap, new float[]{1.f, 1.f, 1.f}, dashOffset);

                final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
                final Mark mark = GO2Utilities.STYLE_FACTORY.mark(StyleConstants.MARK_SQUARE, (Fill) targetObj, stroke);
                symbols.add(mark);
                final Graphic graphic = GO2Utilities.STYLE_FACTORY.graphic(symbols, opacity, size, rotation, anchor, disp);

                final PointSymbolizer symbolizer = GO2Utilities.STYLE_FACTORY.pointSymbolizer(name, geometry, desc, unit, graphic);

                DefaultGlyphService.render(symbolizer, rect, g, null);
               
            } else if (targetObj instanceof ExternalGraphic) {
                final String name = "mySymbol";
                final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
                final String geometry = null; //use the default geometry of the feature
                final Unit unit = NonSI.PIXEL;
                final Expression offset = StyleConstants.LITERAL_ONE_FLOAT;

                //the visual element
                final Expression size = GO2Utilities.FILTER_FACTORY.literal(32);
                final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
                final Expression rotation = StyleConstants.LITERAL_ONE_FLOAT;
                final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
                final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;

                final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
                symbols.add((ExternalGraphic) targetObj);
                final Graphic graphic = GO2Utilities.STYLE_FACTORY.graphic(symbols, opacity, size, rotation, anchor, disp);

                final PointSymbolizer symbolizer = GO2Utilities.STYLE_FACTORY.pointSymbolizer(name, geometry, desc, unit, graphic);

                DefaultGlyphService.render(symbolizer, rect, g, null);               
                
                
            }  
            
            setPreferredSize(dim);
            revalidate();

        } catch (NullPointerException ex) {           
            //Do nothing because we probably try to draw a symbolizer to size 0
        }
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        final Graphics2D g = (Graphics2D) grphcs;

        if (this.mir) {
            g.setColor(Color.LIGHT_GRAY);

            g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
            g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

        }

        updateImage();

        if (this.image != null) {
            
            final AffineTransform trs = new AffineTransform();
            trs.translate(disp[0] + (getWidth() - image.getWidth()) / 2, disp[1] + (getHeight() - image.getHeight()) / 2);
            
            g.drawImage(image, trs, null);
        }

    }

    /**
     * When component is resized, we repaint the image
     * @param ce : ComponentEvent
     */
    public void componentResized(ComponentEvent ce) {
        image = null;
        updateImage();
    }

    public void componentMoved(ComponentEvent ce) {
    }

    public void componentShown(ComponentEvent ce) {
    }

    public void componentHidden(ComponentEvent ce) {
    }
}
