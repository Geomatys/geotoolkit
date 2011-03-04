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
package org.geotoolkit.display2d.style;

import org.geotoolkit.display2d.GO2Utilities;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import org.opengis.filter.expression.Expression;
import org.opengis.style.GraphicFill;
import org.opengis.style.Stroke;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * The cached simple stroke work for strokes that have
 * only a paint or a color defined.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedStrokeSimple extends CachedStroke{

    //cached values
    private float[] cachedDashes = null;
    private float cachedDashOffset = Float.NaN;
    private int cachedCap = Integer.MAX_VALUE;
    private int cachedJoin = Integer.MAX_VALUE;
    private float cachedWidth = Float.NaN;
    private CachedGraphic cachedGraphic = null;
    private AlphaComposite cachedComposite = null;
    private java.awt.Stroke cachedStroke = null;
    private Paint cachedPaint = null;

    CachedStrokeSimple(final Stroke stroke){
        super(stroke);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void evaluate(){
        if(!isNotEvaluated) return;

        this.isStatic = true;

        if(!evaluateComposite() || !evaluatePaint() || !evaluateStroke()){
            //composite is completely translucent or paint is not visible
            //we cache nothing seens nothing can be render
            cachedDashes = null;
            cachedDashOffset = Float.NaN;
            cachedCap = Integer.MAX_VALUE;
            cachedJoin = Integer.MAX_VALUE;
            cachedWidth = Float.NaN;
            cachedGraphic = null;
            cachedComposite = null;
            cachedPaint = null;
            requieredAttributs = EMPTY_ATTRIBUTS;
            isStatic = true;
        }

        isNotEvaluated = false;
    }

    private boolean evaluateComposite(){
        final Expression opacity = styleElement.getOpacity();

        if(GO2Utilities.isStatic(opacity)){
            float j2dOpacity = GO2Utilities.evaluate(opacity, null, Float.class, 1f);

            //we return false, opacity is 0 no need to cache or draw anything
            if(j2dOpacity == 0){
                isStaticVisible = VisibilityState.UNVISIBLE;
                return false;
            }

            //this style is visible
            if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;

            //we cache the composite
            cachedComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, j2dOpacity);
        }else{
            //this style visibility is dynamic
            if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
            isStatic = false;
            GO2Utilities.getRequieredAttributsName(opacity,requieredAttributs);
        }

        return true;
    }

    private boolean evaluatePaint(){

        final GraphicFill graphicFill = styleElement.getGraphicFill();

        if(graphicFill != null){
            cachedGraphic = CachedGraphic.cache(graphicFill);

            if(cachedGraphic.isStaticVisible() == VisibilityState.UNVISIBLE){
                //graphic is not visible even if some value are dynamic
                //this fill is not visible neither
                isStaticVisible = VisibilityState.UNVISIBLE;
                return false;
            }else if(cachedGraphic.isStaticVisible() == VisibilityState.DYNAMIC){
                //graphic visibility is dynamic, so this fill too
                if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
            }else{
                //this graphic is visible
                if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;
            }

            cachedGraphic.getRequieredAttributsName(requieredAttributs);

        }else{
            final Expression expColor = styleElement.getColor();

            if(GO2Utilities.isStatic(expColor)){
                Color j2dColor = GO2Utilities.evaluate(expColor, null, Color.class, Color.BLACK);

                //we return false, opacity is 0 no need to cache or draw anything
                if( j2dColor.getAlpha() == 0 ){
                    isStaticVisible = VisibilityState.UNVISIBLE;
                    return false;
                }

                //this style is visible even if something else is dynamic
                //evaluatePaint may change this value
                if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;

                //we cache the paint
                cachedPaint = j2dColor;
            }else{
                //this style visibility is dynamic
                if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
                isStatic = false;
                GO2Utilities.getRequieredAttributsName(expColor,requieredAttributs);
            }
        }

        //TODO missing Graphic Stroke


        return true;
    }

    private boolean evaluateStroke(){
        final float[] dashArray = styleElement.getDashArray();
        final Expression expOffset = styleElement.getDashOffset();
        final Expression expLineCap = styleElement.getLineCap();
        final Expression expLineJoin = styleElement.getLineJoin();
        final Expression expWidth = styleElement.getWidth();
        boolean strokeStatic = true;

        float[] candidateDashes = null;
        float candidateOffset = Float.NaN;
        int candidateCap = -1;
        int candidateJoin = -1;
        float candidateWidth = Float.NaN;


        candidateDashes = GO2Utilities.validDashes(dashArray);

        // offset ----------------------------------------------
        if(GO2Utilities.isStatic(expOffset)){
            candidateOffset = GO2Utilities.evaluate(expOffset, null, Float.class, 1f);
        }else{
            strokeStatic = false;
            GO2Utilities.getRequieredAttributsName(expOffset,requieredAttributs);
        }


        // line cap ---------------------------------------------
        if(GO2Utilities.isStatic(expLineCap)){
            final String cap = GO2Utilities.evaluate(expLineCap, null, String.class, STROKE_CAP_BUTT_STRING);
            if (STROKE_CAP_BUTT_STRING.equalsIgnoreCase(cap))        candidateCap = BasicStroke.CAP_BUTT;
            else if (STROKE_CAP_SQUARE_STRING.equalsIgnoreCase(cap)) candidateCap = BasicStroke.CAP_SQUARE;
            else if (STROKE_CAP_ROUND_STRING.equalsIgnoreCase(cap))  candidateCap = BasicStroke.CAP_ROUND;
            else                                                     candidateCap = BasicStroke.CAP_BUTT;
        }else{
            strokeStatic = false;
            GO2Utilities.getRequieredAttributsName(expLineCap,requieredAttributs);
        }


        // line join --------------------------------------------
        if(GO2Utilities.isStatic(expLineJoin)){
            final String join = GO2Utilities.evaluate(expLineJoin, null, String.class, STROKE_JOIN_BEVEL_STRING);
            if (STROKE_JOIN_BEVEL_STRING.equalsIgnoreCase(join))       candidateJoin = BasicStroke.JOIN_BEVEL;
            else if (STROKE_JOIN_MITRE_STRING.equalsIgnoreCase(join))  candidateJoin = BasicStroke.JOIN_MITER;
            else if (STROKE_JOIN_ROUND_STRING.equalsIgnoreCase(join)) candidateJoin = BasicStroke.JOIN_ROUND;
            else                                                      candidateJoin = BasicStroke.JOIN_BEVEL;
        }else{
            strokeStatic = false;
            GO2Utilities.getRequieredAttributsName(expLineJoin,requieredAttributs);
        }


        // line width ------------------------------------------
        if(GO2Utilities.isStatic(expWidth)){
            candidateWidth = GO2Utilities.evaluate(expWidth, null, Float.class, 1f);

            //we return false, width is 0 no need to cache or draw anything
            if(candidateWidth == 0){
                isStaticVisible = VisibilityState.UNVISIBLE;
                return false;
            }

            //this style is visible
            if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;

        }else{
            //this style visibility is dynamic
            if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
            strokeStatic = false;
            GO2Utilities.getRequieredAttributsName(expWidth,requieredAttributs);
        }

        // we cache each possible expression ------------------------------
        this.cachedDashes = candidateDashes;
        if(!Float.isNaN(candidateOffset)) cachedDashOffset = (candidateOffset>0) ? candidateOffset : 0;
        if(candidateCap != -1) cachedCap = candidateCap;
        if(candidateJoin != -1) cachedJoin = candidateJoin;
        if(!Float.isNaN(candidateWidth)) cachedWidth = candidateWidth;

        //if static we can can cache the stroke directly----------------------
        if(strokeStatic){
            //we can never cache the java2d stroke seens it's size depend on the symbolizer unit of mesure
            if (cachedDashes != null) {
                cachedStroke = new BasicStroke(candidateWidth, candidateCap, candidateJoin, 10f, cachedDashes, cachedDashOffset);
            } else {
                cachedStroke = new BasicStroke(candidateWidth, candidateCap, candidateJoin, 10f);
            }
        }else{
            this.isStatic = false;
        }

        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(final Object candidate, final float coeff){
        evaluate();
        
        if(Float.isNaN(cachedWidth)){
            final Expression expWidth = styleElement.getWidth();
            if(candidate == null){
                //can not evaluate
                return Float.NaN;
            }else{
                return GO2Utilities.evaluate(expWidth, candidate, Float.class, 1f);
            }
        }

        return cachedWidth * coeff;
    }

    /**
     * Get the java2D Composite for the given feature.
     * 
     * @param candidate : evaluate paint with the given feature
     * @return Java2D Composite
     */
    public AlphaComposite getJ2DComposite(final Object candidate){
        evaluate();

        if(cachedComposite == null){
            //if composite is null it means it is dynamic
            final Expression opacity = styleElement.getOpacity();
            Float j2dOpacity = GO2Utilities.evaluate(opacity, candidate, Float.class, 1f);
            return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, j2dOpacity.floatValue());
        }

        return cachedComposite;
    }

    public boolean isMosaicPaint(){
        evaluate();
        return cachedGraphic != null;
    }

    /**
     * Get the java2D Paint for the given feature.
     * 
     * @param candidate : evaluate paint with the given feature
     * @param x : start X position of the fill area
     * @param y : start Y position of the fill area
     * @return Java2D Paint
     */
    public Paint getJ2DPaint(final Object candidate, final int x, final int y, final float coeff, final RenderingHints hints){
        evaluate();


        if(cachedPaint == null){
            //if paint is null it means it is dynamic
            final Expression expColor = styleElement.getColor();

            if (cachedGraphic != null) {
                //we have a graphic inside
                BufferedImage mosaique = cachedGraphic.getImage(candidate, coeff, hints);

                if (mosaique != null) {
                    return new TexturePaint(mosaique, new Rectangle(x, y, mosaique.getWidth(), mosaique.getHeight()));
                } else {
                    return Color.BLACK;
                }
            } else {
                //or it's a normal plain inside
                return GO2Utilities.evaluate(expColor, null, Color.class, Color.BLACK);
            }
        }

        return cachedPaint;
    }

    public float getStrokeWidth(final Object candidate){
        float candidateWidth = cachedWidth;
        if(Float.isNaN(candidateWidth)){
            final Expression expWidth = styleElement.getWidth();
            candidateWidth = GO2Utilities.evaluate(expWidth, candidate, Float.class, 1f);
        }

        return candidateWidth;
    }

    /**
     * Get the java2D Stroke for the given feature.
     * 
     * @param candidate : evaluate stroke with the given feature
     * @param coeff : use to adjust stroke size, if in display unit value equals 1
     * @return Java2D Stroke
     */
    public java.awt.Stroke getJ2DStroke(final Object candidate, float coeff){
        evaluate();

        coeff = Math.abs(coeff);

        java.awt.Stroke j2dStroke = cachedStroke;
        //if stroke is null it means something is dynamic
        if(j2dStroke == null || coeff != 1){

            float[] candidateDashes = cachedDashes;
            float candidateOffset = cachedDashOffset;
            int candidateCap = cachedCap;
            int candidateJoin = cachedJoin;
            float candidateWidth = cachedWidth;

            if(Float.isNaN(candidateOffset)){
                final Expression expOffset = styleElement.getDashOffset();
                candidateOffset = GO2Utilities.evaluate(expOffset, candidate, Float.class, 1f);
            }

            if(candidateCap == Integer.MAX_VALUE){
                final Expression expCap = styleElement.getLineCap();
                final String cap = GO2Utilities.evaluate(expCap, null, String.class, STROKE_CAP_BUTT_STRING);
                if (STROKE_CAP_BUTT_STRING.equalsIgnoreCase(cap))        candidateCap = BasicStroke.CAP_BUTT;
                else if (STROKE_CAP_SQUARE_STRING.equalsIgnoreCase(cap)) candidateCap = BasicStroke.CAP_SQUARE;
                else if (STROKE_CAP_ROUND_STRING.equalsIgnoreCase(cap))  candidateCap = BasicStroke.CAP_ROUND;
                else                                                     candidateCap = BasicStroke.CAP_BUTT;
            }

            if(candidateJoin == Integer.MAX_VALUE){
                final Expression expJoin = styleElement.getLineJoin();
                final String join = GO2Utilities.evaluate(expJoin, null, String.class, STROKE_JOIN_BEVEL_STRING);
                if (STROKE_JOIN_BEVEL_STRING.equalsIgnoreCase(join))       candidateJoin = BasicStroke.JOIN_BEVEL;
                else if (STROKE_JOIN_MITRE_STRING.equalsIgnoreCase(join))  candidateJoin = BasicStroke.JOIN_MITER;
                else if (STROKE_JOIN_ROUND_STRING.equalsIgnoreCase(join)) candidateJoin = BasicStroke.JOIN_ROUND;
                else                                                      candidateJoin = BasicStroke.JOIN_BEVEL;
            }

            if(Float.isNaN(candidateWidth)){
                final Expression expWidth = styleElement.getWidth();
                candidateWidth = GO2Utilities.evaluate(expWidth, candidate, Float.class, 1f);
            }

            if (candidateDashes != null){
                float[] s = candidateDashes.clone();
                for(int i=0 ;i<s.length; i++){
                    s[i] = s[i]*coeff;
                }
                j2dStroke = new BasicStroke(candidateWidth*coeff, candidateCap, candidateJoin, 1f, s, candidateOffset);
            }else{
                j2dStroke = new BasicStroke(candidateWidth*coeff, candidateCap, candidateJoin, 10f);
            }
        }

        return j2dStroke;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible(final Object candidate) {
        evaluate();

        if(isStaticVisible == VisibilityState.VISIBLE){
            //visible whatever feature we have
            return true;
        }else if(isStaticVisible == VisibilityState.UNVISIBLE){
            //unvisible whatever feature we have
            return false;
        }else{
            //dynamic visibility

            //test dynamic composite
            if(cachedComposite == null){
                final Expression opacity = styleElement.getOpacity();
                Float j2dOpacity = GO2Utilities.evaluate(opacity, candidate, Float.class, 1f);
                if(j2dOpacity == 0) return false;
            }

            //test dynamic paint
            if(cachedPaint == null){
                final Expression expColor = styleElement.getColor();

                if (cachedGraphic != null) {
                    boolean visible = cachedGraphic.isVisible(candidate);
                    if(!visible) return false;
                } else {
                    //or it's a normal plain inside
                    Color color = GO2Utilities.evaluate(expColor, null, Color.class, Color.BLACK);
                    if(color.getAlpha() == 0) return false;
                }
            }

            //test dynamic width
            if(Float.isNaN(cachedWidth)){
                final Expression expWidth = styleElement.getWidth();
                Float j2dWidth = GO2Utilities.evaluate(expWidth, candidate, Float.class, 1f);
                if(j2dWidth == 0) return false;
            }


            return true;
        }
    }

}
