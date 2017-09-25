/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.display2d.ext.graduation;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.measure.UnitConverter;
import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.geometry.jts.awt.JTSGeometryJ2D;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.j2d.GeodeticPathWalker;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.DefaultLineSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.TransformException;

/**
 * Graduation symbolizer renderer.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GraduationSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedGraduationSymbolizer>{

    private Object candidate;
    private static final class GradInfo{
        private CachedGraduationSymbolizer.CachedGraduation grad;
        private float stepReal;
        private float stepGeo;
        private double size;
        private float distanceTextOffset;
        /** Exact instance of GraduationSymbolizer.SIDE_X */
        private Literal side;
        private NumberFormat format;
    }


    //reused variables
    private final Point2D start = new Point2D.Double();
    private final Point2D end = new Point2D.Double();
    private final List<Integer> nextNearest = new ArrayList<>();

    public GraduationSymbolizerRenderer(SymbolizerRendererService service, CachedGraduationSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    @Override
    public void portray(ProjectedObject graphic) throws PortrayalException {
        final ProjectedGeometry projGeom = graphic.getGeometry(null);
        if(projGeom==null) return;

        final CoordinateReferenceSystem displayCrs = renderingContext.getDisplayCRS();
        final List<CachedGraduationSymbolizer.CachedGraduation> grads = symbol.getCachedGraduations();
        if(grads.isEmpty()) return;

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //precalculate values
        candidate = graphic.getCandidate();
        final List<GradInfo> forwardCandidates = new ArrayList<>();
        final List<GradInfo> backwardCandidates = new ArrayList<>();
        for(CachedGraduationSymbolizer.CachedGraduation cg : grads){
            final GraduationSymbolizer.Graduation grad = cg.getGraduation();
            final GradInfo info = new GradInfo();
            info.grad = cg;
            info.stepReal = grad.getStep().evaluate(candidate, Number.class).floatValue();
            info.size = grad.getSize().evaluate(candidate, Number.class).doubleValue();
            info.format = new DecimalFormat(grad.getFormat().evaluate(candidate, String.class));
            info.distanceTextOffset = grad.getStart().evaluate(candidate, Number.class).floatValue();
            info.distanceTextOffset = grad.getStart().evaluate(candidate, Number.class).floatValue();
            String side = grad.getSide().evaluate(candidate, String.class);
            if(GraduationSymbolizer.SIDE_BOTH.getValue().toString().equalsIgnoreCase(side)){
                info.side = GraduationSymbolizer.SIDE_BOTH;
            }else if(GraduationSymbolizer.SIDE_LEFT.getValue().toString().equalsIgnoreCase(side)){
                info.side = GraduationSymbolizer.SIDE_LEFT;
            }else{
                info.side = GraduationSymbolizer.SIDE_RIGHT;
            }

            //get unit
            final Expression unitExp = grad.getUnit();
            final String unitStr = (unitExp==null) ? null : unitExp.evaluate(candidate, String.class);
            final Unit unit = (unitStr==null) ? Units.METRE : Units.valueOf(unitStr);
            //adjust unit to ellipsoid unit, for path walker
            final Ellipsoid ellipsoid = CRS.getEllipsoid(displayCrs);
            final UnitConverter converter = unit.getConverterTo(ellipsoid.getAxisUnit());
            info.stepGeo = (float)converter.convert(info.stepReal);

            //avoid 0 and very small values
            if(info.stepGeo>=0.0000001){
                if(Boolean.FALSE.equals(grad.getReverse().evaluate(candidate, Boolean.class))){
                    forwardCandidates.add(info);
                }else{
                    backwardCandidates.add(info);
                }
            }
        }
        if(forwardCandidates.isEmpty() && backwardCandidates.isEmpty())return;


        renderingContext.switchToDisplayCRS();
        try {
            final Geometry geom = projGeom.getDataGeometryJTS();
            final Geometry displayGeom = JTS.transform(geom,projGeom.getDataToDisplay());

            if(!forwardCandidates.isEmpty()){
                final Shape dispShape = new JTSGeometryJ2D(displayGeom);
                final GradInfo[] gradInfos = forwardCandidates.toArray(new GradInfo[forwardCandidates.size()]);
                final GeodeticPathWalker walker = new GeodeticPathWalker(dispShape.getPathIterator(null), displayCrs);
                portray(walker,gradInfos);
            }
            if(!backwardCandidates.isEmpty()){
                final Shape dispShape = new JTSGeometryJ2D(displayGeom.reverse());
                final GradInfo[] gradInfos = backwardCandidates.toArray(new GradInfo[backwardCandidates.size()]);
                final GeodeticPathWalker walker = new GeodeticPathWalker(dispShape.getPathIterator(null), displayCrs);
                portray(walker,gradInfos);
            }

        } catch (TransformException ex) {
            throw new PortrayalException(ex.getMessage(), ex);
        }catch(IllegalArgumentException ex){
            //may happen with geodetic calculator when geometry goes outside the valid envelope
        }

    }

    private void portray(GeodeticPathWalker walker, GradInfo[] gradInfos) throws TransformException{

        //store current distance for each graduation
        final float[] distances = new float[gradInfos.length];

        float currentDistance = 0;

        //render the first tick at 0
        renderTick(walker, gradInfos[0], distances[0]);

        //walk over the path rendering closest tick each time
        while(!walker.isFinished()){
            nextNearest.clear();
            //find the next nearest graduations
            nextNearest.add(0);

            float minDistance = distances[0] + gradInfos[0].stepGeo;
            for(int i=1;i<gradInfos.length;i++){
                final float candidateDist = distances[i] + gradInfos[i].stepGeo;
                if(candidateDist < minDistance){
                    nextNearest.clear();
                    nextNearest.add(i);
                    minDistance = candidateDist;
                }else if(candidateDist == minDistance){
                    nextNearest.add(i);
                }
            }

            walker.walk(minDistance - currentDistance);
            currentDistance = minDistance;
            if(walker.isFinished()) break;

            for(Integer index : nextNearest){
                distances[index] = currentDistance;
            }

            renderTick(walker, gradInfos[nextNearest.get(0)], currentDistance);
        }

    }

    private void renderTick(GeodeticPathWalker walker, GradInfo info, double distance){
        walker.getPosition(start);

        //ensure the point is in the visible area
        if(!renderingContext.getCanvasDisplayBounds().contains(start)) return;

        double angle = walker.getRotation();

        if(info.side==GraduationSymbolizer.SIDE_LEFT || info.side==GraduationSymbolizer.SIDE_BOTH){
            renderTick(info, distance, angle - Math.PI/2);
        }
        if(info.side==GraduationSymbolizer.SIDE_RIGHT || info.side==GraduationSymbolizer.SIDE_BOTH){
            renderTick(info, distance, angle + Math.PI/2);
        }

    }

    private void renderTick(GradInfo info, double distance, double angle){
        final CachedGraduationSymbolizer.CachedGraduation cgrad = info.grad;
        final CachedStroke cs = cgrad.getCachedStroke();

        //render tick
        end.setLocation(
                start.getX() + Math.cos(angle)*info.size,
                start.getY() + Math.sin(angle)*info.size );
        final Line2D tick = new Line2D.Double(start, end);
        DefaultLineSymbolizerRenderer.portray(symbol, g2d, tick, cs, candidate, coeff, hints);

        //render text
        final String text = info.format.format(distance + info.distanceTextOffset);
        final Font font = cgrad.getCachedFont().getJ2dFont(candidate, coeff);

        final Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);

        //ensure text is always upside down
        boolean flip = false;
        angle = (angle+Math.PI*2) % (Math.PI*2);
        if(angle>=Math.PI/2 && angle<=Math.PI*3/2){
            flip = true;
            end.setLocation(
                    end.getX()+Math.cos(angle)*bounds.getWidth(),
                    end.getY()+Math.sin(angle)*bounds.getWidth());
            angle -= Math.PI;
        }


        g2d.rotate(angle, end.getX(), end.getY());
        g2d.setFont(font);
        final float height = (float)bounds.getMaxY();
        g2d.drawString(text, (float)end.getX()+(flip?-2:+2), (float)end.getY()+height/2);
        g2d.rotate(-angle, end.getX(), end.getY());
    }


    /**
     * Picking not supported.
     *
     * @param graphic
     * @param mask
     * @param filter
     * @return
     */
    @Override
    public boolean hit(ProjectedObject graphic, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    /**
     * Picking not supported.
     *
     * @param graphic
     * @param mask
     * @param filter
     * @return
     */
    @Override
    public boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    /**
     * Coverage no supported.
     *
     * @param graphic
     * @throws PortrayalException
     */
    @Override
    public void portray(ProjectedCoverage graphic) throws PortrayalException {
    }
}
