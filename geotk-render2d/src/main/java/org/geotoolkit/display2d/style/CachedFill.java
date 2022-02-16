/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.filter.Expression;
import org.opengis.style.Fill;
import org.opengis.style.GraphicFill;

/**
 * Cached Fill.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedFill extends Cache<Fill> {

    //Cached values
    private AlphaComposite cachedComposite = null;
    private Paint cachedPaint = null;
    private CachedGraphic cachedGraphic = null;

    private CachedFill(final Fill fill){
        super(fill);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate(){
        if(!isNotEvaluated) return;

        this.isStatic = true;
        if(!evaluateComposite() || !evaluatePaint() ){
            //composite is completely translucent or paint is not visible
            //we cache nothing seens nothing can be render
            cachedComposite = null;
            cachedPaint = null;
            cachedGraphic = null;
            requieredAttributs = EMPTY_ATTRIBUTS;
            isStatic = true;
        }

        isNotEvaluated = false;
    }

    /**
     * @return false if it's not even necessary to paint anything.
     */
    private boolean evaluateComposite(){
        final Expression opacity = styleElement.getOpacity();

        if(GO2Utilities.isStatic(opacity)){
            float j2dOpacity = GO2Utilities.evaluate(opacity, null, 1f,0f,1f);

            //we return false, opacity is 0 no need to cache or draw anything
            if(j2dOpacity <= 0){
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

    /**
     * @return false if it's not even necessary to paint anything.
     */
    private boolean evaluatePaint(){

        final GraphicFill graphicFill = styleElement.getGraphicFill();

        if (graphicFill != null) {
            cachedGraphic = CachedGraphic.cache(graphicFill);

            switch(cachedGraphic.isStaticVisible()){
                //graphic is not visible even if some value are dynamic, this fill is not visible neither
                case UNVISIBLE :
                    isStaticVisible = VisibilityState.UNVISIBLE;
                    return false;
                //graphic visibility is dynamic, so this fill too
                case DYNAMIC :
                    if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
                    break;
                //this graphic is visible
                default :
                    if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;
            }

            cachedGraphic.getRequieredAttributsName(requieredAttributs);

        } else {
            final Expression expColor = styleElement.getColor();

            if(GO2Utilities.isStatic(expColor)){
                Color j2dColor = GO2Utilities.evaluate(expColor, null, Color.class, Color.BLACK);

                //we return false, opacity is 0 no need to cache or draw anything
                if( j2dColor.getAlpha() <= 0 ){
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

        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible(final Object candidate){
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
                Float j2dOpacity = GO2Utilities.evaluate(opacity, candidate, 1f,0f,1f);
                if(j2dOpacity <= 0) return false;
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
                    if(color.getAlpha() <= 0) return false;
                }
            }

            return true;
        }

    }

    /**
     * @return Java2D composite for this feature
     */
    public AlphaComposite getJ2DComposite(final Object candidate){
        evaluate();

        if(cachedComposite == null){
            //if composite is null it means it is dynamic
            final Expression opacity = styleElement.getOpacity();
            Float j2dOpacity = GO2Utilities.evaluate(opacity, candidate, 1f,0f,1f);
            return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, j2dOpacity.floatValue());
        }

        return cachedComposite;
    }

    /**
     *
     * @return true if the paint is a texture, in this case X/Y value must be correct
     * when calling getJ2DPaint method.
     */
    public boolean isMosaic(){
        return cachedPaint != null && cachedGraphic != null;
    }

    /**
     *
     * @return Java2D paint for this feature
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
                return GO2Utilities.evaluate(expColor, candidate, Color.class, Color.BLACK);
            }
        }

        return cachedPaint;
    }

    public static CachedFill cache(final Fill fill){
        return new CachedFill(fill);
    }

}
