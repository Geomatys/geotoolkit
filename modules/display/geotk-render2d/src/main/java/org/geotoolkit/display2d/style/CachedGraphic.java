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
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.filter.Expression;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;

/**
 * Cached graphic.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedGraphic<C extends Graphic> extends Cache<C>{

    //cached values
    private float cachedOpacity = Float.NaN;
    private float cachedRotation = Float.NaN;
    private float cachedSize = Float.NaN;
    private final CachedAnchorPoint cachedAnchor;
    private final CachedDisplacement cachedDisplacement;
    private CachedMark cachedMark = null;
    private CachedExternal cachedExternal = null;

    //we cant use buffer for images seens they can be symbolizer UOM relative
//    protected static final short ID_SUBBUFFER = 6;
//    protected static final short ID_BUFFER = 7;



    protected CachedGraphic(final C graphic){
        super(graphic);
        this.cachedAnchor = CachedAnchorPoint.cache(graphic.getAnchorPoint());
        this.cachedDisplacement = CachedDisplacement.cache(graphic.getDisplacement());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate(){
        if(!isNotEvaluated) return;

        if(!evaluateGraphic()){
            //composite is completely translucent or paint is not visible
            //we cache nothing seens nothing can be render
            cachedOpacity = Float.NaN;
            cachedRotation = Float.NaN;
            cachedSize = Float.NaN;
            cachedMark = null;
            cachedExternal = null;
            requieredAttributs = EMPTY_ATTRIBUTS;
            isStatic = true;
        }else{
            //we can try to cache other parameters
            cachedAnchor.getRequieredAttributsName(requieredAttributs);
            cachedDisplacement.getRequieredAttributsName(requieredAttributs);

            if(cachedMark != null){
                isStatic = (
                        cachedMark.isStatic() &&
                        !Float.isNaN(cachedOpacity) &&
                        !Float.isNaN(cachedRotation) &&
                        !Float.isNaN(cachedSize) &&
                        cachedDisplacement.isStatic()
                        );
            }else if(cachedExternal != null){
                isStatic = (
                        cachedExternal.isStatic() &&
                        !Float.isNaN(cachedOpacity) &&
                        !Float.isNaN(cachedRotation) &&
                        !Float.isNaN(cachedSize) &&
                        cachedDisplacement.isStatic()
                        );
            }else{
                throw new IllegalStateException("Inconsistent symbology graphic cache.");
            }

        }

        isNotEvaluated = false;
    }

    private boolean evaluateGraphic(){

        final List<GraphicalSymbol> symbols = styleElement.graphicalSymbols();
        final Expression expOpacity = styleElement.getOpacity();
        final Expression expRotation = styleElement.getRotation();
        final Expression expSize = styleElement.getSize();

        // Opacity -------------------------------------
        if(GO2Utilities.isStatic(expOpacity)){
            cachedOpacity = GO2Utilities.evaluate(expOpacity, null,1f,0f,1f);
            //we return false, opacity is 0 no need to cache or draw anything
            if(cachedOpacity <= 0){
                isStaticVisible = VisibilityState.UNVISIBLE;
                return false;
            }
            //this style is visible
            if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;

        }else{
            //this style visibility is dynamic
            if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
            isStatic = false;
            GO2Utilities.getRequieredAttributsName(expOpacity,requieredAttributs);
        }


        // Rotation ------------------------------------
        if(GO2Utilities.isStatic(expRotation)){
            cachedRotation = (float) Math.toRadians(GO2Utilities.evaluate(expRotation, null, Number.class, 0d).doubleValue());
        }else{
            isStatic = false;
            GO2Utilities.getRequieredAttributsName(expRotation,requieredAttributs);
        }


        // Size ----------------------------------------
        if(GO2Utilities.isStatic(expSize)){
            cachedSize = GO2Utilities.evaluate(expSize, null, Number.class, Float.NaN).floatValue();
            //we return false, size is 0 no need to cache or draw anything
            if(cachedSize <= 0){
                isStaticVisible = VisibilityState.UNVISIBLE;
                return false;
            }
            //this style is visible
            if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;
        }else{
            //this style visibility is dynamic
            if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
            isStatic = false;
            GO2Utilities.getRequieredAttributsName(expSize,requieredAttributs);
        }


        //grab the first available symbol-------------------
        boolean found = false;

        graphicLoop:
        for(GraphicalSymbol symbol : symbols){

            if(symbol instanceof Mark){
                CachedMark candidateMark = CachedMark.cache((Mark)symbol);

                //test if the mark is valid, could be false if an URL or anything is broken
                if(candidateMark.isValid()){

                    //if the mark is invisible this graphic is invisible too
                    //so there is nothing to cache
                    VisibilityState markStaticVisibility = candidateMark.isStaticVisible();
                    if(markStaticVisibility == VisibilityState.UNVISIBLE){
                        isStaticVisible = VisibilityState.UNVISIBLE;
                        return false;
                    }else if(markStaticVisibility == VisibilityState.VISIBLE){
                        if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;
                        if(!candidateMark.isStatic()) isStatic = false;
                        this.cachedMark = candidateMark;
                    }else{
                        if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
                        if(!candidateMark.isStatic()) isStatic = false;
                        this.cachedMark = candidateMark;
                    }

                    candidateMark.getRequieredAttributsName(requieredAttributs);
                    found = true;
                    break graphicLoop;
                }
            }else if(symbol instanceof ExternalGraphic){
                CachedExternal candidateExternal = CachedExternal.cache((ExternalGraphic)symbol);

                if(candidateExternal.isValid()){

                    //if the external is invisible this graphic is invisible too
                    //so there is nothing to cache
                    if(candidateExternal.isStaticVisible() == VisibilityState.UNVISIBLE){
                        isStaticVisible = VisibilityState.UNVISIBLE;
                        return false;
                    }

//                    //if size is static and external static visible, we can cache the symbol graphic
//                    if(!Float.isNaN(cachedSize) && cachedExternal.isStatic() && cachedExternal.isStaticVisible() == VisibilityState.VISIBLE){
//                        BufferedImage buffer = cachedExternal.getImage(cachedSize);
//                        cachedValues.put(ID_SUBBUFFER, buffer);
//                    }else{
                        if(isStaticVisible != VisibilityState.UNVISIBLE) isStaticVisible = VisibilityState.DYNAMIC;
                        isStatic = false;
                        this.cachedExternal = candidateExternal;
//                    }

                    candidateExternal.getRequieredAttributsName(requieredAttributs);
                    found = true;
                    break graphicLoop;
                }

            }
        }

        //create the default square symbol is no symbol found
        if(!found){
            Mark mark = GO2Utilities.STYLE_FACTORY.mark();
            cachedMark = CachedMark.cache(mark);

            if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;

//            //if size is static, we can cache the symbol graphic
//            if(!Float.isNaN(cachedSize)){
//                BufferedImage buffer = cachedMark.getImage(null, cachedSize);
//                cachedValues.put(ID_SUBBUFFER, buffer);
//            }else{
//            }
        }


//        //if static we can cache the stroke directly
//        if(isStatic){
//
//            //no operation to append to image, erase all cache and cache the subbuffer as the main buffer
//            if(cachedRotation ==0 && cachedOpacity==1){
//                BufferedImage buffer = (BufferedImage) cachedValues.get(ID_SUBBUFFER);
//                cachedValues.clear();
//                cachedValues.put(ID_BUFFER, buffer);
//            }
//            //or we have to apply a new opacity and rotation to the subbuffer
//            else{
//                final BufferedImage buffer = (BufferedImage) cachedValues.get(ID_SUBBUFFER);
//                final Float j2dRotation = new Float(Math.toRadians(cachedRotation));
//                final Composite j2dComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cachedOpacity);
//                final int maxSizeX;
//                final int maxSizeY;
//                if(j2dRotation == 0){
//                    maxSizeX = buffer.getWidth();
//                    maxSizeY = buffer.getHeight();
//                }else{
//                    Rectangle rect = new Rectangle(buffer.getWidth(), buffer.getHeight());
//                    TransformedShape trs = new TransformedShape();
//                    trs.shape = rect;
//                    trs.rotate(j2dRotation);
//                    maxSizeX = (int) trs.getBounds2D().getWidth();
//                    maxSizeY = (int) trs.getBounds2D().getHeight();
//                }
//
//                BufferedImage img = new BufferedImage( maxSizeX , maxSizeY, BufferedImage.TYPE_INT_ARGB);
//                Graphics2D g2 = (Graphics2D) img.getGraphics();
//
//                g2.setComposite(j2dComposite);
//                g2.rotate(j2dRotation, maxSizeX/2f, maxSizeY/2f);
//                final int translateX = (int)((maxSizeX-buffer.getWidth())/2 );
//                final int translateY = (int)((maxSizeY-buffer.getHeight())/2 );
//                g2.drawImage(buffer, translateX, translateY, null);
//                g2.dispose();
//            }
//
//        }

        return true;
    }

    /**
     *
     * @return BufferedImage for a feature
     */
    public BufferedImage getImage(final Object candidate, final float coeff, final RenderingHints hints) {
        return getImage(candidate, null, coeff, hints);
    }

    /**
     *
     * @return BufferedImage for a feature
     */
    public BufferedImage getImage(final Object candidate, final Float forcedSize, final float coeff, final RenderingHints hints) {
        return getImage(candidate, forcedSize, coeff, true, hints);
    }

    /**
     *
     * @return BufferedImage for a feature
     */
    public BufferedImage getImage(final Object candidate, final Float forcedSize, final float coeff, boolean withRotation, final RenderingHints hints) {
        evaluate();


//        //we have a cached buffer ---------------------------------------------------------------
//        if(cachedValues.containsKey(ID_BUFFER)){
//            return (BufferedImage) cachedValues.get(ID_BUFFER);
//        }

        //-------- grab the cached parameters ----------------------------------------------------
        float candidateOpacity = cachedOpacity;
        float candidateRotation = cachedRotation;
        Float candidateSize = (forcedSize!=null) ? forcedSize : cachedSize;

        if(Float.isNaN(candidateOpacity)){
            final Expression expOpacity = styleElement.getOpacity();
            candidateOpacity = GO2Utilities.evaluate(expOpacity, candidate, 1f,0f,1);
        }

        if(Float.isNaN(candidateRotation)){
            final Expression expRotation = styleElement.getRotation();
            final Number rot = GO2Utilities.evaluate(expRotation, candidate, Number.class, 0f);
            candidateRotation = (float) Math.toRadians(rot.doubleValue());
        }

        if(candidateSize.isNaN()){
            final Expression expSize = styleElement.getSize();
            candidateSize = GO2Utilities.evaluate(expSize, candidate, Number.class, Float.NaN).floatValue();
        }

        //the subbuffer image
        BufferedImage subBuffer = null;

//        //we have a cached subbuffer ------------------------------------------------------------
//        if(cachedValues.containsKey(ID_SUBBUFFER)){
//            subBuffer = (BufferedImage) cachedValues.get(ID_SUBBUFFER);
//        }

        //we have a cached mark ------------------------------------------------------------------
        if(cachedMark != null){
            if(candidateSize.isNaN()){
                subBuffer = cachedMark.getImage(candidate, 16*coeff,hints);
            }else{
                subBuffer = cachedMark.getImage(candidate, candidateSize*coeff,hints);
            }
        }


        //we have a cached external --------------------------------------------------------------
        if(cachedExternal != null){
            subBuffer = cachedExternal.getImage(candidateSize,coeff,hints);
        }

        if(subBuffer==null){
            //may happen if image is too small
            return null;
        }

        final boolean skipRotation = candidateRotation == 0 || !withRotation;
        //no operation to append to image, return the buffer directly ----------------------------
        if( skipRotation && candidateOpacity == 1 ) return subBuffer;

        // we must change opacity or rotation ----------------------------------------------------
        final int maxSizeX;
        final int maxSizeY;
        if(skipRotation) {
            maxSizeX = subBuffer.getWidth();
            maxSizeY = subBuffer.getHeight();
        }else{
            Rectangle rect = new Rectangle(subBuffer.getWidth(), subBuffer.getHeight());
            TransformedShape trs = new TransformedShape();
            trs.setOriginalShape(rect);
            trs.rotate(candidateRotation);
            final Rectangle2D rotatedRect = trs.getBounds2D();
            maxSizeX = (int) rotatedRect.getWidth();
            maxSizeY = (int) rotatedRect.getHeight();
        }

        if(maxSizeX<=0 || maxSizeY<=0){
            return null;
        }

        final BufferedImage buffer = new BufferedImage( maxSizeX , maxSizeY, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        if(hints != null){
            g2.setRenderingHints(hints);
        }

        final Composite j2dComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, candidateOpacity);

        g2.setComposite(j2dComposite);
        if (!skipRotation)
            g2.rotate(candidateRotation, maxSizeX/2f, maxSizeY/2f);
        final int translateX = (maxSizeX-subBuffer.getWidth())/2;
        final int translateY = (maxSizeY-subBuffer.getHeight())/2;
        g2.drawImage(subBuffer, translateX, translateY, null);
        g2.dispose();

        return buffer;
    }

    /**
     * return an Array of 2 floats always in display unit.
     */
    public float[] getDisplacement(final Object candidate, final float[] buffer){
        return cachedDisplacement.getValues(candidate, buffer);
    }

    /**
     * return an Array of 2 floats.
     */
    public float[] getAnchor(final Object candidate, final float[] buffer){
        return cachedAnchor.getValues(candidate, buffer);
    }

    public float getRotation(final Object candidate){
        float candidateRotation = cachedRotation;
        if(Float.isNaN(candidateRotation)){
            final Expression expRotation = styleElement.getRotation();
            final Number rot = GO2Utilities.evaluate(expRotation, candidate, Number.class, 0f);
            candidateRotation = (float) Math.toRadians(rot.doubleValue());
        }
        return candidateRotation;
    }

    /**
     * @return margin of this style for the given feature
     */
    public float getMargin(final Object candidate,final float coeff) {
        evaluate();

        final boolean noFeature = (candidate == null);

//        //we have a cachedImage, we return it's bigest attribut
//        BufferedImage img = (BufferedImage)cachedValues.get(ID_BUFFER);
//        if(img != null){
//            return (img.getHeight()*coeff > img.getWidth()*coeff) ? img.getHeight()*coeff : img.getWidth()*coeff;
//        }

        //get the displacement margin
        final float maxDisplacement = cachedDisplacement.getMargin(candidate,coeff);
        if(Float.isNaN(maxDisplacement)) return Float.NaN;
        //get anchor margin
        final float anchorRatio = cachedAnchor.getMarginRatio(candidate,coeff);
        if(Float.isNaN(anchorRatio)) return Float.NaN;


        float candidateOpacity = cachedOpacity;
        float candidateRotation = cachedRotation;
        float candidateSize = cachedSize;

        if(Float.isNaN(candidateOpacity)){
            final Expression expOpacity = styleElement.getOpacity();
            if(noFeature){
                //can not evaluate
                return Float.NaN;
            }else{
                candidateOpacity = GO2Utilities.evaluate(expOpacity, candidate, 1f,0f,1f);
            }
        }

        if(candidateOpacity <= 0) return 0;

        if(Float.isNaN(candidateRotation)){
            final Expression expRotation = styleElement.getRotation();
            final Number rot = GO2Utilities.evaluate(expRotation, candidate, Number.class, 0f);
            candidateRotation = (float) Math.toRadians(rot.doubleValue());
        }

        if(Float.isNaN(candidateSize)){
            final Expression expSize = styleElement.getSize();
            if(noFeature){
                //can not evaluate
                return Float.NaN;
            }else{
                candidateSize = GO2Utilities.evaluate(expSize, candidate, Number.class, 16f).floatValue();
            }
        }

        if(candidateSize <= 0) return 0;

        //the subbuffer image
        BufferedImage subBuffer = null;

//        //we have a cached subbuffer ------------------------------------------------------------
//        if(cachedValues.containsKey(ID_SUBBUFFER)){
//            subBuffer = (BufferedImage) cachedValues.get(ID_SUBBUFFER);
//        }

        //we have a cached mark ------------------------------------------------------------------
        if(cachedMark != null){
            if(noFeature){
                if(cachedMark.isStatic()){
                    subBuffer = cachedMark.getImage(candidate, candidateSize*coeff,null);
                }
            }else{
                subBuffer = cachedMark.getImage(candidate, candidateSize*coeff,null);
            }
        }

        //we have a cached external --------------------------------------------------------------
        if(cachedExternal != null){
            if(noFeature){
                if(cachedExternal.isStatic()){
                    subBuffer = cachedExternal.getImage(candidateSize,coeff,null);
                }
            }else{
                subBuffer = cachedExternal.getImage(candidateSize,coeff,null);
            }
        }

        if(subBuffer == null) return 0;

        // we must change size according to rotation ---------------------------------------------
        final int maxSizeX;
        final int maxSizeY;
        if(candidateRotation == 0){
            maxSizeX = subBuffer.getWidth();
            maxSizeY = subBuffer.getHeight();
        }else{
            Rectangle rect = new Rectangle(subBuffer.getWidth(), subBuffer.getHeight());
            TransformedShape trs = new TransformedShape();
            trs.setOriginalShape(rect);
            trs.rotate(candidateRotation);
            maxSizeX = (int) trs.getBounds2D().getWidth();
            maxSizeY = (int) trs.getBounds2D().getHeight();
        }

        //consider the anchor value and displacement
        //max margin is : iconSize/2 + iconSize-0.5 + maxDisplacement
        final float iconSize = Math.max(maxSizeX, maxSizeY);
        return (iconSize/2f + (Math.abs(anchorRatio-0.5f)*iconSize) + maxDisplacement) * coeff;

    }

    /**
     * {@inheritDoc }
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

//            if(cachedValues.get(ID_BUFFER) != null){
//                //should normaly not happen, if we have a buffer
//                // Visibility should always be VISIBLE
//                return true;
//            }

            //test dynamic opacity
            if(Float.isNaN(cachedOpacity)){
                final Expression expopacity = styleElement.getOpacity();
                float j2dOpacity = GO2Utilities.evaluate(expopacity, candidate, 1f,0f,1f);
                if(j2dOpacity <= 0) return false;
            }

            //test dynamic size
            if(Float.isNaN(cachedSize)){
                final Expression expSize = styleElement.getSize();
                float j2dSize = GO2Utilities.evaluate(expSize, candidate, Number.class, 16f).floatValue();
                if(j2dSize <= 0) return false;
            }

//            if(cachedValues.get(ID_SUBBUFFER) == null){
            if(true){


                //test dynamic mark
                if(cachedMark != null){
                    boolean visible = cachedMark.isVisible(candidate);
                    if(!visible) return false;
                }

                //test dynamic external
                if(cachedExternal != null){
                    boolean visible = cachedExternal.isVisible(candidate);
                    if(!visible) return false;
                }
            }

            return true;
        }
    }

    public static CachedGraphic cache(final Graphic graphic){
        return new CachedGraphic(graphic);
    }

}
