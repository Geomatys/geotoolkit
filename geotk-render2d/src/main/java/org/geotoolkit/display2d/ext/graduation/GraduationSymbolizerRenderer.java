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

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import org.apache.sis.map.ExceptionPresentation;
import org.apache.sis.map.Presentation;
import org.apache.sis.measure.Units;
import org.apache.sis.map.MapLayer;
import org.apache.sis.referencing.datum.DatumOrEnsemble;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.presentation.TextPresentation2;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.j2d.GeodeticPathWalker;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.LineSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.geometry.jts.awt.JTSGeometryJ2D;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Graduation symbolizer renderer.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GraduationSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedGraduationSymbolizer>{

    private Feature feature;
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
    public Stream<Presentation> presentations(MapLayer layer, Feature feature) {

        final ProjectedGeometry projGeom = new ProjectedGeometry(renderingContext);
        projGeom.setDataGeometry(GO2Utilities.getGeometry(feature, symbol.getSource().getGeometry()), null);

        final CoordinateReferenceSystem displayCrs = renderingContext.getDisplayCRS();
        final List<CachedGraduationSymbolizer.CachedGraduation> grads = symbol.getCachedGraduations();
        if(grads.isEmpty()) return Stream.empty();

        //precalculate values
        final List<GradInfo> forwardCandidates = new ArrayList<>();
        final List<GradInfo> backwardCandidates = new ArrayList<>();
        for (CachedGraduationSymbolizer.CachedGraduation cg : grads) {
            final GraduationSymbolizer.Graduation grad = cg.getGraduation();
            final GradInfo info = new GradInfo();
            info.grad = cg;
            info.stepReal = ((Number) grad.getStep().apply(feature)).floatValue();
            info.size = ((Number) grad.getSize().apply(feature)).doubleValue();
            info.format = new DecimalFormat(grad.getFormat().apply(feature).toString());
            info.distanceTextOffset = ((Number) grad.getStart().apply(feature)).floatValue();
            info.distanceTextOffset = ((Number) grad.getStart().apply(feature)).floatValue();
            String side = grad.getSide().apply(feature).toString();
            if (GraduationSymbolizer.SIDE_BOTH.getValue().toString().equalsIgnoreCase(side)) {
                info.side = GraduationSymbolizer.SIDE_BOTH;
            } else if(GraduationSymbolizer.SIDE_LEFT.getValue().toString().equalsIgnoreCase(side)) {
                info.side = GraduationSymbolizer.SIDE_LEFT;
            } else {
                info.side = GraduationSymbolizer.SIDE_RIGHT;
            }

            //get unit
            final Expression unitExp = grad.getUnit();
            final String unitStr = (unitExp==null) ? null : unitExp.apply(feature).toString();
            final Unit unit = (unitStr==null) ? Units.METRE : Units.valueOf(unitStr);
            //adjust unit to ellipsoid unit, for path walker
            final Ellipsoid ellipsoid = DatumOrEnsemble.getEllipsoid(displayCrs).orElseThrow();
            final UnitConverter converter = unit.getConverterTo(ellipsoid.getAxisUnit());
            info.stepGeo = (float)converter.convert(info.stepReal);

            //avoid 0 and very small values
            if (info.stepGeo>=0.0000001) {
                if (Boolean.FALSE.equals(grad.getReverse().apply(feature))) {
                    forwardCandidates.add(info);
                } else {
                    backwardCandidates.add(info);
                }
            }
        }
        if (forwardCandidates.isEmpty() && backwardCandidates.isEmpty()) return Stream.empty();

        final List<Presentation> presentations = new ArrayList<>();
        renderingContext.switchToDisplayCRS();
        try {
            final Geometry geom = projGeom.getDataGeometryJTS();
            final Geometry displayGeom = org.apache.sis.geometry.wrapper.jts.JTS.transform(geom,projGeom.getDataToDisplay());

            if (!forwardCandidates.isEmpty()) {
                final Shape dispShape = new JTSGeometryJ2D(displayGeom);
                final GradInfo[] gradInfos = forwardCandidates.toArray(new GradInfo[forwardCandidates.size()]);
                final GeodeticPathWalker walker = new GeodeticPathWalker(dispShape.getPathIterator(null), displayCrs);
                portray(layer, walker, gradInfos, presentations);
            }
            if (!backwardCandidates.isEmpty()) {
                final Shape dispShape = new JTSGeometryJ2D(displayGeom.reverse());
                final GradInfo[] gradInfos = backwardCandidates.toArray(new GradInfo[backwardCandidates.size()]);
                final GeodeticPathWalker walker = new GeodeticPathWalker(dispShape.getPathIterator(null), displayCrs);
                portray(layer, walker, gradInfos, presentations);
            }
        } catch (TransformException | FactoryException ex) {
            ExceptionPresentation ep = new ExceptionPresentation(ex);
            ep.setLayer(layer);
            ep.setResource(layer.getData());
            ep.setCandidate(feature);
            presentations.add(ep);
        } catch(IllegalArgumentException ex) {
            //may happen with geodetic calculator when geometry goes outside the valid envelope
        }
        return presentations.stream();
    }

    private void portray(MapLayer layer, GeodeticPathWalker walker, GradInfo[] gradInfos, List<Presentation> presentations) throws TransformException{

        //store current distance for each graduation
        final float[] distances = new float[gradInfos.length];

        float currentDistance = 0;

        //render the first tick at 0
        renderTick(layer, walker, gradInfos[0], distances[0], presentations);

        //walk over the path rendering closest tick each time
        while (!walker.isFinished()) {
            nextNearest.clear();
            //find the next nearest graduations
            nextNearest.add(0);

            float minDistance = distances[0] + gradInfos[0].stepGeo;
            for (int i=1;i<gradInfos.length;i++) {
                final float candidateDist = distances[i] + gradInfos[i].stepGeo;
                if (candidateDist < minDistance) {
                    nextNearest.clear();
                    nextNearest.add(i);
                    minDistance = candidateDist;
                } else if(candidateDist == minDistance) {
                    nextNearest.add(i);
                }
            }

            walker.walk(minDistance - currentDistance);
            currentDistance = minDistance;
            if (walker.isFinished()) break;

            for (Integer index : nextNearest) {
                distances[index] = currentDistance;
            }

            renderTick(layer, walker, gradInfos[nextNearest.get(0)], currentDistance, presentations);
        }
    }

    private void renderTick(MapLayer layer, GeodeticPathWalker walker, GradInfo info, double distance, List<Presentation> presentations){
        walker.getPosition(start);

        //ensure the point is in the visible area
        if (!renderingContext.getCanvasDisplayBounds().contains(start)) return;

        double angle = walker.getRotation();

        if (info.side==GraduationSymbolizer.SIDE_LEFT || info.side==GraduationSymbolizer.SIDE_BOTH) {
            renderTick(layer, info, distance, angle - Math.PI/2, presentations);
        }
        if (info.side==GraduationSymbolizer.SIDE_RIGHT || info.side==GraduationSymbolizer.SIDE_BOTH) {
            renderTick(layer, info, distance, angle + Math.PI/2, presentations);
        }

    }

    private void renderTick(MapLayer layer, GradInfo info, double distance, double angle, List<Presentation> presentations){
        final CachedGraduationSymbolizer.CachedGraduation cgrad = info.grad;
        final CachedStroke cs = cgrad.getCachedStroke();

        //render tick
        end.setLocation(
                start.getX() + Math.cos(angle)*info.size,
                start.getY() + Math.sin(angle)*info.size );
        final Line2D tick = new Line2D.Double(start, end);
        List<Presentation> pres = LineSymbolizerRenderer.portray(layer, symbol, tick, cs, feature, coeff, hints, renderingContext);


        //render text
        final String text = info.format.format(distance + info.distanceTextOffset);
        final Font font = cgrad.getCachedFont().getJ2dFont(feature, coeff);

        final Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
        final float height = (float)bounds.getMaxY();

        //ensure text is always upside down
        boolean flip = false;
        angle = (angle+Math.PI*2) % (Math.PI*2);
        if (angle >= Math.PI/2 && angle <= Math.PI*3/2) {
            flip = true;
            end.setLocation(
                    end.getX()+Math.cos(angle)*bounds.getWidth(),
                    end.getY()+Math.sin(angle)*bounds.getWidth());
            angle -= Math.PI;
        }

        AffineTransform trs = new AffineTransform();
        trs.rotate(angle, end.getX(), end.getY());

        final TextPresentation2 tp = new TextPresentation2(layer, null, feature);
        tp.forGrid(renderingContext);
        tp.text = new AttributedString(text);
        tp.font = font;
        tp.x = (float)end.getX()+(flip?-2:+2);
        tp.y = (float)end.getY()+height/2;
        tp.displayTransform = trs;

        presentations.addAll(pres);
        presentations.add(tp);
    }

}
