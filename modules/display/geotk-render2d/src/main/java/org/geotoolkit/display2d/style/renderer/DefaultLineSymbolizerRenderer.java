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
package org.geotoolkit.display2d.style.renderer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.logging.Level;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.display2d.style.CachedGraphicStroke;
import org.geotoolkit.display2d.style.CachedLineSymbolizer;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.CachedStrokeGraphic;
import org.geotoolkit.display2d.style.CachedStrokeSimple;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.j2d.PathWalker;
import org.geotoolkit.geometry.jts.LineStringTranslator;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultLineSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedLineSymbolizer>{

    private final CachedStroke cachedStroke;

    public DefaultLineSymbolizerRenderer(final SymbolizerRendererService service,final CachedLineSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
        cachedStroke = symbol.getCachedStroke();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException{
        //portray the border of the coverage
        final ProjectedGeometry projectedGeometry = projectedCoverage.getEnvelopeGeometry();

        //could not find the border geometry
        if(projectedGeometry == null) return;

        portray(projectedGeometry, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedObject projectedFeature) throws PortrayalException{
        final Object candidate = projectedFeature.getCandidate();
        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return;

        //test if the symbol is visible on this feature
        if(symbol.isVisible(candidate)){
            portray(projectedGeometry, candidate);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final Iterator<? extends ProjectedObject> graphics) throws PortrayalException {

        if(dispGeom){
            renderingContext.switchToDisplayCRS();
        }else{
            renderingContext.switchToObjectiveCRS();
        }

        while(graphics.hasNext()){

            if(monitor.stopRequested()){
                break;
            }

            final ProjectedObject projectedFeature = graphics.next();
            final Object feature = projectedFeature.getCandidate();
            final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) continue;

            //test if the symbol is visible on this feature
            if(!symbol.isVisible(feature)){
                continue;
            }

            final Shape[] j2dShapes = getShapes(projectedGeometry, feature);

            // Do not try to draw this shape if null
            if (j2dShapes == null) {
                continue;
            }

            for(Shape j2dShape : j2dShapes){
                portray(symbol, g2d, j2dShape, cachedStroke, feature, coeff, hints);
            }
        }
    }

    private void portray(final ProjectedGeometry projectedGeometry, final Object feature) throws PortrayalException{

        final Shape[] j2dShapes;

        if(dispGeom){
            renderingContext.switchToDisplayCRS();
        }else{
            renderingContext.switchToObjectiveCRS();
        }
        
        j2dShapes = getShapes(projectedGeometry, feature);

        if(j2dShapes == null){
            return;
        }

        for(Shape j2dShape : j2dShapes){
            portray(symbol, g2d, j2dShape, cachedStroke, feature, coeff, hints);
        }
    }
    
    private Shape[] getShapes(ProjectedGeometry projectedGeometry, Object feature) throws PortrayalException{
        final float offset = symbol.getOffset(feature, coeff);
        final Shape[] j2dShapes;
        if(offset==0){
            try{
                j2dShapes = (dispGeom)? projectedGeometry.getDisplayShape()
                                     : projectedGeometry.getObjectiveShape();
            } catch (TransformException ex) {
                throw new PortrayalException("Could not calculate objective projected geometry",ex);
            }
        }else{
            try{
                final Geometry[] geoms = (dispGeom)? projectedGeometry.getDisplayGeometryJTS()
                                         : projectedGeometry.getObjectiveGeometryJTS();
                j2dShapes = new Shape[geoms.length];
                for(int i=0;i<geoms.length;i++){
                    Geometry g = geoms[i];
                    if(g instanceof LineString){
                        g = LineStringTranslator.translateLineString((LineString)g, offset);
                    }else if(g instanceof MultiLineString){
                        g = LineStringTranslator.translateLineString((MultiLineString)g, offset);
                    }
                    j2dShapes[i] = new JTSGeometryJ2D(g);
                    //TODO : clip geometry
                }
                
            } catch (TransformException ex) {
                throw new PortrayalException("Could not calculate objective projected geometry",ex);
            }
        }
        return j2dShapes;
    }
    
    public static void portray(CachedSymbolizer symbol, Graphics2D g2d, Shape j2dShape, 
            CachedStroke cachedStroke, Object feature, float coeff, RenderingHints hints){

        if(cachedStroke instanceof CachedStrokeSimple){
            final CachedStrokeSimple cs = (CachedStrokeSimple)cachedStroke;
            g2d.setComposite(cs.getJ2DComposite(feature));

            if(cs.isMosaicPaint()){
                //we need to find the top left bounds of the geometry
                final float margin = symbol.getMargin(feature, coeff) /2f;
                final Rectangle2D bounds = j2dShape.getBounds2D();
                final int x = (int) (bounds.getMinX() - margin);
                final int y = (int) (bounds.getMinY() - margin);
                g2d.setPaint(cs.getJ2DPaint(feature, x, y, coeff, hints));
            }else{
                g2d.setPaint(cs.getJ2DPaint(feature, 0, 0, coeff, hints));
            }
            g2d.setStroke(cs.getJ2DStroke(feature,coeff));
            g2d.draw(j2dShape);
        }else if(cachedStroke instanceof CachedStrokeGraphic){
            final CachedStrokeGraphic gc = (CachedStrokeGraphic)cachedStroke;
            g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
            final float initGap = gc.getInitialGap(feature);
            final Point2D pt = new Point2D.Double();
            final CachedGraphicStroke cgs = gc.getCachedGraphic();
            final Image img = cgs.getImage(feature, 1, hints);
            final float imgWidth = img.getWidth(null);
            final float imgHeight = img.getHeight(null);
            final float gap = gc.getGap(feature)+ imgWidth;
            final AffineTransform trs = new AffineTransform();

            final PathIterator ite = j2dShape.getPathIterator(null);
            final PathWalker walker = new PathWalker(ite);
            walker.walk(initGap);
            while(!walker.isFinished()){
                //paint the motif --------------------------------------------------
                walker.getPosition(pt);
                final float angle = walker.getRotation();
                trs.setToTranslation(pt.getX(), pt.getY());
                trs.rotate(angle);
                final float[] anchor = cgs.getAnchor(feature, null);
                final float[] disp = cgs.getDisplacement(feature, null);
                trs.translate(-imgWidth*anchor[0], -imgHeight*anchor[1]);
                trs.translate(disp[0], -disp[1]);

                g2d.drawImage(img, trs, null);

                //walk over the gap ------------------------------------------------
                walker.walk(gap);
            }
        }

    }
    

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedObject projectedFeature, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Geometry mask = search.getDisplayGeometryJTS();

        final Object feature = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(!(symbol.isVisible(feature))) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        //Test composites ------------------------------------------------------
        if(cachedStroke instanceof CachedStrokeSimple){
            final CachedStrokeSimple cs = (CachedStrokeSimple)cachedStroke;
            final float strokeAlpha = cs.getJ2DComposite(feature).getAlpha();
            if(strokeAlpha < GO2Utilities.SELECTION_LOWER_ALPHA){
                //feature graphic is translucide, not selectable
                return false;
            }
        }

        if(dispGeom){

            final Geometry[] j2dShapes;

            try {
                j2dShapes = projectedGeometry.getDisplayGeometryJTS();
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, "Error while accesing geometry.",ex);
                return false;
            }

            final int bufferWidth = (int) symbol.getMargin(feature,1);

            //test envelopes first
//            Geometry CRSShape = mask.getEnvelope();
//            CRSShape = mask.buffer(bufferWidth,1);
//            boolean hit = testHit(filter,CRSShape,j2dShape.getEnvelope());

//            if(!hit) return false;

            //test real shape
            Geometry CRSShape = mask;
            try{
                CRSShape = mask.buffer(bufferWidth);
            }catch(IllegalArgumentException ex){
                //can happen if the geometry has too few points, like a ring of 3points
                LOGGER.log(Level.FINE, ex.getLocalizedMessage(), ex);
            }
            
            for(Geometry j2dShape : j2dShapes){
                if(GO2Utilities.testHit(filter,CRSShape,j2dShape)) return true;
            }
            return false;

        }else{
            final Shape[] j2dShapes;
            final Shape[] CRSShapes;
            try {
                j2dShapes = projectedGeometry.getObjectiveShape();
                CRSShapes = new Shape[j2dShapes.length];
                for(int i=0;i<j2dShapes.length;i++){
                    CRSShapes[i] = new TransformedShape();
                    ((TransformedShape)CRSShapes[i]).setTransform(
                            renderingContext.getAffineTransform(renderingContext.getDisplayCRS(), renderingContext.getObjectiveCRS()));
                    ((TransformedShape)CRSShapes[i]).setOriginalShape(search.getDisplayShape());
                }
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, "Error while accesing geometry.",ex);
                return false;
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, "Error while accesing geometry.",ex);
                return false;
            }

            for(int i=0;i<j2dShapes.length;i++){
                Shape j2dShape = j2dShapes[i];
                Shape CRSShape = CRSShapes[i];
                if(cachedStroke instanceof CachedStrokeSimple){
                    final CachedStrokeSimple cs = (CachedStrokeSimple)cachedStroke;
                    final java.awt.Stroke stroke = cs.getJ2DStroke(feature,coeff);
                    final Area area = new Area(stroke.createStrokedShape(j2dShape));

                    switch(filter){
                        case INTERSECTS :
                            area.intersect(new Area(CRSShape));
                            return !area.isEmpty();
                        case WITHIN :
                            Area start = new Area(area);
                            area.add(new Area(CRSShape));
                            return start.equals(area);
                    }
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedCoverage graphic, final SearchAreaJ2D mask, final VisitFilter filter) {
        return false;
    }

}
