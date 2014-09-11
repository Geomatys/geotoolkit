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
package org.geotoolkit.display2d.ext.pie;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Arc2D;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.DefaultProjectedObject;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedPolygonSymbolizer;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Fill;
import org.opengis.style.PolygonSymbolizer;

import org.opengis.style.Stroke;

/**
 * Pie symbolizer renderer.
 *
 * @author Johann Sorel (Geomays)
 * @author Cédric Briançon (Geomatys)
 */
public class PieSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedPieSymbolizer> {

    static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(null);
    static final GeometryFactory GF = new GeometryFactory();

    public PieSymbolizerRenderer(final SymbolizerRendererService service, CachedPieSymbolizer cache, RenderingContext2D context){
        super(service, cache, context);
    }

    @Override
    public void portray(ProjectedObject graphic) throws PortrayalException {
    }

    @Override
    public void portray(Iterator<? extends ProjectedObject> ite) throws PortrayalException {

        final int pieSize = 80;

        //create cells
        final CoordinateReferenceSystem gridCRS = renderingContext.getObjectiveCRS2D();
        final Envelope gridEnv = renderingContext.getCanvasObjectiveBounds2D();

        final double step = (pieSize+100) * renderingContext.getDisplayToObjective().getScaleX();

        final double startX = (int)((gridEnv.getMinimum(0) / step)-1) * step  ;
        final double startY = (int)((gridEnv.getMinimum(1) / step)-1) * step;

        final List<PieSymbolizer.Group> groups = symbol.getSource().getGroups();


        final Map<Polygon,Map<PieSymbolizer.Group,Integer>> cells = new LinkedHashMap<>();

        for(double x=startX;x<gridEnv.getMaximum(0);x+=step){
            for(double y=startY;y<gridEnv.getMaximum(1);y+=step){

                final Coordinate[] coords = new Coordinate[]{
                        new Coordinate(x, y),
                        new Coordinate(x, y+step),
                        new Coordinate(x+step, y+step),
                        new Coordinate(x+step, y),
                        new Coordinate(x, y),
                };
                final LinearRing ring = GF.createLinearRing(coords);
                final Polygon poly = GF.createPolygon(ring, new LinearRing[0]);
                JTS.setCRS(poly, gridCRS);


                final Map<PieSymbolizer.Group,Integer> grps = new LinkedHashMap<>();
                for(PieSymbolizer.Group grp : groups){
                    grps.put(grp, 0);
                }

                cells.put(poly, grps);
            }
        }

        while(ite.hasNext()){
            try {
                final Object next = ite.next();
                final Feature f;
                if(next instanceof ProjectedFeature){
                    f = ((ProjectedFeature)next).getCandidate();
                }else{
                    continue;
                }
                ProjectedFeature pf = (ProjectedFeature) next;
                final Geometry[] geoms = pf.getGeometry(null).getObjectiveGeometryJTS();

entries:        for(Entry<Polygon,Map<PieSymbolizer.Group,Integer>> entry : cells.entrySet()){
                    for (final Geometry geom : geoms) {
                        if (entry.getKey().intersects(geom)) {
                            Map<PieSymbolizer.Group, Integer> grps = entry.getValue();
                            for (Entry<PieSymbolizer.Group, Integer> group : grps.entrySet()) {
                                if (group.getKey().getFilter().evaluate(f)) {
                                    group.setValue(group.getValue() + 1);
                                }
                            }

                            break entries; //cells are mutually exclusive
                        }
                    }
                }
            } catch (TransformException ex) {
                throw new PortrayalException(ex);
            }
        }

        final MathTransform objtoDisp = renderingContext.getObjectiveToDisplay();
        final Font font = new Font("Monospaced", Font.BOLD, 12);
        g2d.setFont(font);
        final FontMetrics fm = g2d.getFontMetrics(font);
        final int fabove = fm.getAscent();

        try{
            for(Map.Entry<Polygon,Map<PieSymbolizer.Group,Integer>> entry : cells.entrySet()){
                final Map<PieSymbolizer.Group,Integer> grps = entry.getValue();

                Point pt = entry.getKey().getCentroid();
                pt = (Point) JTS.transform(pt, objtoDisp);
                final int centerX = (int)pt.getX();
                final int centerY = (int)pt.getY();

                int count = 0;
                for(Integer i : grps.values()){
                    count += i;
                }

                if(count>0){

                    final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                    ftb.setName("arc");
                    ftb.add("geom", Polygon.class,renderingContext.getDisplayCRS());
                    final FeatureType ft = ftb.buildFeatureType();

                    //draw area ------------------------------------------------
//                    final Polygon geo = (Polygon) JTS.transform(entry.getKey(), objtoDisp);
//                    Shape shp = new JTSGeometryJ2D(geo);
//                    g2d.setColor(Color.LIGHT_GRAY);
//                    g2d.setStroke(new BasicStroke(1));
//                    g2d.draw(shp);
//                    g2d.drawLine((int)geo.getCoordinates()[0].x, (int)geo.getCoordinates()[0].y,
//                                (int)geo.getCoordinates()[2].x, (int)geo.getCoordinates()[2].y);
//                    g2d.drawLine((int)geo.getCoordinates()[1].x, (int)geo.getCoordinates()[1].y,
//                                (int)geo.getCoordinates()[3].x, (int)geo.getCoordinates()[3].y);



                    // draw pie ------------------------------------------------
                    final int nbGroup = grps.size();
                    final double degrees = -360/nbGroup;

                    double startDegree = 90;
                    for(Entry<PieSymbolizer.Group,Integer> group : grps.entrySet()){
                        final PieSymbolizer.Group symbol = group.getKey();


                        final Arc2D arc = new Arc2D.Double(centerX-pieSize/2, centerY-pieSize/2, pieSize, pieSize, startDegree, degrees, Arc2D.PIE);
                        final Geometry arcGeo = JTS.shapeToGeometry(arc, GF);
                        final double arcCenterX = arcGeo.getCentroid().getX();
                        final double arcCenterY = arcGeo.getCentroid().getY();

                        final Feature arcFeature = FeatureUtilities.defaultFeature(ft, "0");
                        arcFeature.setPropertyValue("geom", arcGeo);

                        final StatelessContextParams param = new StatelessContextParams(renderingContext.getCanvas(), null);
                        param.update(renderingContext);
                        final DefaultProjectedObject<ProjectedFeature> pf = new DefaultProjectedObject(param, arcFeature);

                        final Fill fill = symbol.getFill();
                        final Stroke stroke = symbol.getStroke();
                        final PolygonSymbolizer symbolizer = SF.polygonSymbolizer(stroke, fill, null);
                        final CachedPolygonSymbolizer cachedFill = (CachedPolygonSymbolizer) GO2Utilities.getCached(symbolizer, ft);
                        GO2Utilities.portray(pf.getCandidate(), cachedFill, renderingContext);

                        final float[] disp = cachedFill.getDisplacement(pf);

                        final Expression textfill = group.getKey().getText();
                        final Color color = textfill.evaluate(null, Color.class);

                        final String text = String.valueOf(group.getValue());
                        final int textSize = fm.stringWidth(text);
                        g2d.setColor(color);
                        g2d.drawString(text, (int)(arcCenterX-textSize/2 + disp[0]), (int)(arcCenterY+fabove/2 - disp[1]) );

                        startDegree -= degrees;
                    }
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void portray(ProjectedCoverage graphic) throws PortrayalException {

    }

    @Override
    public boolean hit(ProjectedObject graphic, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    @Override
    public boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

}
