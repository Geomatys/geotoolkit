/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2014, Geomatys
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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.renderer.style.DynamicSymbolFactoryFinder;
import org.geotoolkit.renderer.style.MarkFactory;
import org.opengis.filter.Expression;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.ExternalMark;
import org.opengis.style.Mark;

/**
 * Cached Mark.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CachedMark extends Cache<Mark>{

    private static final MarkFactory[] MARK_FACTORIES;
    static {
        final List<MarkFactory> lst = new ArrayList<>();
        final Iterator<MarkFactory> ite = DynamicSymbolFactoryFinder.getMarkFactories();
        while(ite.hasNext()){
            lst.add(ite.next());
        }
        MARK_FACTORIES = lst.toArray(new MarkFactory[0]);
    }

    //IDS for cache map
    private Shape cachedWKN = null;

    private final CachedStrokeSimple cachedStroke;
    private final CachedFill cachedFill;


    private CachedMark(final Mark mark){
        super(mark);
        cachedStroke = (CachedStrokeSimple) CachedStroke.cache(mark.getStroke());
        cachedFill = CachedFill.cache(mark.getFill());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {
        if(!isNotEvaluated) return;

        this.isStatic = true;
        if(!evaluateMark()){
            //composite is completely translucent or paint is not visible
            //we cache nothing seens nothing can be render
            cachedWKN = null;
            requieredAttributs = EMPTY_ATTRIBUTS;
            isStatic = true;
        }

        if(!cachedStroke.isStatic()) isStatic = false;
        if(!cachedFill.isStatic()) isStatic = false;

        if(cachedFill.isStaticVisible() == VisibilityState.UNVISIBLE &&
           cachedStroke.isStaticVisible() == VisibilityState.UNVISIBLE &&
           styleElement.getExternalMark() == null ){
            //we have a Well Knowned mark but it's invisible
            //so nothing to cache
            cachedWKN = null;
            requieredAttributs = EMPTY_ATTRIBUTS;
            isStatic = true;
            isStaticVisible = VisibilityState.UNVISIBLE;
        }

        isNotEvaluated = false;
    }

    private boolean evaluateMark(){

        final Expression expWKN = styleElement.getWellKnownName();
        boolean isWKN = false;

        if(expWKN == null){
            isWKN = false;
        }else if(GO2Utilities.isStatic(expWKN)){

            Object markRef = expWKN.apply(null);

            for(int i=0; i<MARK_FACTORIES.length && cachedWKN==null; i++){
                try {
                    cachedWKN = MARK_FACTORIES[i].evaluateShape(null, markRef, 0);
                } catch (PortrayalException ex) {
                    LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
                }
            }

            //we return false, invalid marker
            if(cachedWKN == null){
                isStaticVisible = VisibilityState.UNVISIBLE;
                return false;
            }

            //this style is visible
            if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;

            isWKN = true;
        }else{
            //this style visibility is dynamic
            if(isStaticVisible == VisibilityState.NOT_DEFINED) isStaticVisible = VisibilityState.VISIBLE;
            isStatic = false;
            GO2Utilities.getRequieredAttributsName(expWKN,requieredAttributs);
            isWKN = true;
        }


        final ExternalMark external = styleElement.getExternalMark();

        if(!isWKN && external != null){
            //no well knowned mark but an external mark
            return true;
        }else if(!isWKN){
            // no well knowned mark and no external
            isStaticVisible = VisibilityState.UNVISIBLE;
            isStatic = true;
            return false;
        }

        return true;

    }

    public boolean isValid(){
        evaluate();
        return styleElement.getWellKnownName()!= null || styleElement.getExternalMark() != null ;
    }

    public Shape getShape(final Object candidate, final RenderingHints hints){
        evaluate();

        final Expression wkn = styleElement.getWellKnownName();
        final ExternalMark external = styleElement.getExternalMark();

        Shape candidateShape = cachedWKN;

        if(candidateShape == null){
            String format = null;
            Object markRef = null;
            int markIndex = 0;
            if(wkn!=null){
                markRef = wkn.apply(candidate);
            }else if(external!=null){
                format = external.getFormat();
                markRef = external.getOnlineResource().getLinkage().toString();
                markIndex = external.getMarkIndex();
            }

            for(int i=0; i<MARK_FACTORIES.length && candidateShape==null; i++){
                try {
                    candidateShape = MARK_FACTORIES[i].evaluateShape(format, markRef, markIndex);
                } catch (PortrayalException ex) {
                    LOGGER.log(Level.WARNING, ex.getLocalizedMessage(),ex);
                }
            }
        }

        return candidateShape;
    }

    public BufferedImage getImage(final Object candidate, final Float size, final RenderingHints hints){
        evaluate();

        final Expression wkn = styleElement.getWellKnownName();
        final ExternalMark external = styleElement.getExternalMark();

        int j2dSize = 16;
        float margin = 0;
        int maxWidth = 0;
        int center = 0;

        Shape candidateShape = getShape(candidate, hints);


        if(wkn != null || external != null){
            j2dSize = (size != null) ? size.intValue() : 16;
            if(j2dSize < 0) j2dSize = 0;
            if(j2dSize > 1000) j2dSize = 1000;
            margin = cachedStroke.getMargin(candidate,1);
            maxWidth = (int)(margin*2+0.5f)+ j2dSize ;
            center = maxWidth/2  ;
        }

        if(maxWidth < 1) maxWidth =1;

        if(candidateShape != null){

            BufferedImage buffer = new BufferedImage( maxWidth , maxWidth, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) buffer.getGraphics();
            if(hints != null) g2.setRenderingHints(hints);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(center,center);

            TransformedShape trs = new TransformedShape();
            trs.setOriginalShape(candidateShape);
            trs.scale(j2dSize,j2dSize);
            Shape shp = trs; //trs.createTransformedShape(marker);

            //test if we need to paint the fill
            if(cachedFill.isVisible(candidate)){
                g2.setPaint(cachedFill.getJ2DPaint(candidate,0, 0, 1,hints));
                g2.setComposite(cachedFill.getJ2DComposite(candidate));
                g2.fill(shp);
            }

            //test if we need to paint the stroke
            if(cachedStroke.isVisible(candidate)){
                g2.setStroke(cachedStroke.getJ2DStroke(candidate,1));
                g2.setPaint(cachedStroke.getJ2DPaint(candidate, 0, 0, 1, hints));
                g2.setComposite(cachedStroke.getJ2DComposite(candidate));
                g2.draw(shp);
            }
            g2.dispose();

            return buffer;

        }else if(external != null){
            return createImage(external, j2dSize, hints);
        }

        return null;
    }

    private BufferedImage createImage(final ExternalMark external, final int j2dSize, final RenderingHints hints){
        final String format = external.getFormat();
        final Icon icon = external.getInlineContent();
        final int index = external.getMarkIndex();
        final OnlineResource online = external.getOnlineResource();

        if(icon != null){
            int height = icon.getIconHeight();
            int width = icon.getIconWidth();

            BufferedImage buffer = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) buffer.getGraphics();
             if(hints != null) g2.setRenderingHints(hints);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            icon.paintIcon(null, g2, 0,0);
            g2.dispose();


            if(height != j2dSize){
                //Specification says size is for the hight and we must preserve width
                float aspect = (float)(height) / j2dSize ;
                float maxwidth = width / aspect;

                BufferedImage buffer2 = new BufferedImage( (int)(maxwidth+0.5f), (int)(j2dSize+0.5f), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) buffer2.getGraphics();
                 if(hints != null) g.setRenderingHints(hints);
                g.drawImage(buffer, 0, 0,buffer2.getWidth(), buffer2.getHeight(), 0, 0, width, height,null);
                g.dispose();

                return buffer2;
            }else{
                return buffer;
            }

        }else if(online != null){
            //TODO a faire
            return null;
        }
        return null;
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
            //a markis allways visible,
            //expect if while evaluating no valid graphic where found
            return true;
        }
    }

    public static CachedMark cache(final Mark mark){
        return new CachedMark(mark);
    }

}
