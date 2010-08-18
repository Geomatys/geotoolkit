/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.style.j2d;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * Text Stroke special for OGC Symbology encoding.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TextStroke implements Stroke {

    private static final byte STATE_INITIAL_GAP = 0;
    private static final byte STATE_GAP = 1;
    private static final byte STATE_LABEL = 2;

    private final String text;
    private final Font font;
    private final boolean repeat;
    private final AffineTransform t = new AffineTransform();
    private final float offset;
    private final float initialGap;
    private final float gap;
    private final Rectangle clip;

    public TextStroke(String text, Font font, boolean repeat, float offset,
            float initialgap, float gap, Rectangle clip) {
        this.text = text;
        this.font = font;
        this.repeat = repeat;
        this.offset = offset;
        this.initialGap = initialgap;
        this.gap = gap;
        this.clip = clip;
    }

    @Override
    public Shape createStrokedShape(Shape shape) {
        final FontRenderContext frc = new FontRenderContext(null, true, true);
        final GlyphVector glyphVector = font.createGlyphVector(frc, text);
        final GeneralPath result = new GeneralPath();
        final float[] points = new float[6];
        final int labelLength = glyphVector.getNumGlyphs();

        //no label to paint
        if (labelLength == 0) return result;
        
        final float totalLabelLenght = (float) glyphVector.getVisualBounds().getWidth();
        float remainingPathLength = measurePathLength(shape);
                
        //path is to short to paint label
        if( (initialGap+totalLabelLenght) > remainingPathLength){
            return result;
        }

        final PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), 1);
        
        float moveX = 0, moveY = 0;
        float lastX = 0, lastY = 0;
        float thisX = 0, thisY = 0;
        int segmentType = 0;
        float next = 0;
        float remainingSegmentsize = 0;
        int currentChar = 0;

        //initialise values
        byte state = STATE_INITIAL_GAP;
        float initialGapToConsume = initialGap;
        float gapToConsume = gap;

        next = glyphVector.getGlyphMetrics(currentChar).getAdvance();
        
        mainLoop :
        while(!it.isDone()){
            //while we havent reach the end of the path iterator
            segmentType = it.currentSegment(points);

            switch(segmentType){
                case PathIterator.SEG_MOVETO:
                    moveX = lastX = points[0];
                    moveY = lastY = points[1];
                    result.moveTo(moveX, moveY);
                    break;

                case PathIterator.SEG_CLOSE:
                    points[0] = moveX;
                    points[1] = moveY;
                    // Fall into....

                case PathIterator.SEG_LINETO:
                    thisX = points[0];
                    thisY = points[1];
                    final float dx = thisX - lastX;
                    final float dy = thisY - lastY;
                    final float distance = (float) Math.sqrt(dx * dx + dy * dy);
                    float segmentToConsume = distance + remainingSegmentsize;
                    remainingSegmentsize = 0;
                    remainingPathLength -= distance;

                    segmentLoop :
                    while(segmentToConsume>0){
                        //while the segment is not consume
                        switch(state){
                            case STATE_INITIAL_GAP :
                                //PASS THE INITIAL GAP--------------------------
                                if(segmentToConsume<initialGapToConsume){
                                    //segment is completly consume by the initial gap
                                    initialGapToConsume -= segmentToConsume;
                                    segmentToConsume = 0;
                                }else if(segmentToConsume==initialGapToConsume){
                                    //segment is completly consume by the initial gap
                                    //and finish the initial gap
                                    initialGapToConsume = 0;
                                    segmentToConsume = 0;
                                    state = STATE_LABEL;
                                }else{
                                    //segment is bigger than the initial gap
                                    segmentToConsume -= initialGapToConsume;
                                    state = STATE_LABEL;
                                }
                                break;
                            case STATE_GAP :
                                //PASS A GAP------------------------------------
                                if(segmentToConsume<gapToConsume){
                                    //segment is completly consume by the gap
                                    gapToConsume -= segmentToConsume;
                                    segmentToConsume = 0;
                                }else if(segmentToConsume==gapToConsume){
                                    //segment is completly consume by the gap
                                    //and finish the gap
                                    segmentToConsume = 0;
                                    state = STATE_LABEL;
                                     //restore gap value for next gap
                                    gapToConsume = gap;
                                }else{
                                    //segment is bigger than the gap
                                    segmentToConsume -= gapToConsume;
                                    state = STATE_LABEL;
                                    //restore gap value for next gap
                                    gapToConsume = gap;
                                }
                                break;
                            case STATE_LABEL :
                                //DRAW THE LABEL--------------------------------
                                final float angle = (float) Math.atan2(dy, dx);

                                labelLoop :
                                while(segmentToConsume >= next){
                                    //check if we are in the clip area
                                    final float x = lastX + ((distance-segmentToConsume)/distance) * dx ;
                                    final float y = lastY + ((distance-segmentToConsume)/distance) * dy ;
                                    if(clip.contains(x, y)){
                                        //we are in the clip area
                                        final Shape charGlyph = glyphVector.getGlyphOutline(currentChar);
                                        final Point2D p = glyphVector.getGlyphPosition(currentChar);
                                        final float px = (float) p.getX();
                                        final float py = (float) p.getY();
                                        t.setToTranslation(x+0, y);
                                        t.rotate(angle);
                                        t.translate(-px, -py);
                                        t.translate(0, -offset);
                                        result.append(t.createTransformedShape(charGlyph), false);
                                    }                                    

                                    segmentToConsume -= next;
                                    currentChar++;

                                    if(currentChar>=labelLength){
                                        //we reach the end of the String
                                        if(repeat && remainingPathLength > (gap+totalLabelLenght)){
                                            //get back to string start
                                            //if there is enough space to draw another label
                                            currentChar = 0;
                                            //prepare to paint the gap
                                            state = STATE_GAP;
                                            continue segmentLoop;
                                        }else{
                                            //we have finish, only one label painted
                                            break mainLoop;
                                        }
                                    }else{
                                        //some more caracteres to paint
                                        next = glyphVector.getGlyphMetrics(currentChar).getAdvance();
                                    }
                                }

                                //store the remaining segment size for next caractere
                                //this is to avoid incoherent caractere spacing when several
                                //to small segments are chained
                                remainingSegmentsize = segmentToConsume;
                                segmentToConsume = 0;

                                break;
                        }
                    }

                    lastX = thisX;
                    lastY = thisY;
                    break;
            }

            it.next();
        }

        return result;
    }

    
    public float measurePathLength(Shape shape) {
        final PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), 1);
        final float[] points = new float[6];
        float moveX = 0, moveY = 0;
        float lastX = 0, lastY = 0;
        float thisX = 0, thisY = 0;
        int type = 0;
        float total = 0;

        while (!it.isDone()) {
            type = it.currentSegment(points);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    moveX = lastX = points[0];
                    moveY = lastY = points[1];
                    break;

                case PathIterator.SEG_CLOSE:
                    points[0] = moveX;
                    points[1] = moveY;
                    // Fall into....

                case PathIterator.SEG_LINETO:
                    thisX = points[0];
                    thisY = points[1];
                    final float dx = thisX - lastX;
                    final float dy = thisY - lastY;
                    total += (float) Math.sqrt(dx * dx + dy * dy);
                    lastX = thisX;
                    lastY = thisY;
                    break;
            }
            it.next();
        }

        return total;
    }
    

}
