/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.procedure;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.instruction.AlphanumericText;
import org.geotoolkit.s52.lookuptable.instruction.NumericText;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.geotoolkit.s52.render.PointSymbolStyle;
import org.geotoolkit.s52.render.SymbolStyle;
import org.opengis.feature.Feature;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A Part I p.160 (12.2.7)
 *
 * @author Johann Sorel (Geomatys)
 */
public class LIGHTS05 extends Procedure{

    public LIGHTS05() {
        super("LIGHTS05");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, ProjectedObject graphic, S52Context.GeoType geotype) throws PortrayalException {
        final Graphics2D g2d = ctx.getGraphics();
        final Feature feature = (Feature) graphic.getCandidate();

        Number valnmr = (Number) feature.getProperty("VALNMR").getValue();
        String catlit = (String) feature.getProperty("CATLIT").getValue();
        Number orient = (Number) feature.getProperty("ORIENT").getValue();

        final Coordinate displayCenter;
        final Coordinate objectiveCenter;
        try {
            displayCenter = graphic.getGeometry(null).getDisplayGeometryJTS().getCoordinate();
            objectiveCenter = graphic.getGeometry(null).getObjectiveGeometryJTS().getCentroid().getCoordinate();
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        if(valnmr == null){
            valnmr = 9;
        }

        if(catlit != null){

            if("8".equals(catlit) || "11".equals(catlit)){
                final SymbolStyle ss = context.getSyle("LIGHTS82");
                ss.render(g2d, context, colorTable, displayCenter, 0f);
                return; //finished
            }else if("9".equals(catlit)){
                final SymbolStyle ss = context.getSyle("LIGHTS81");
                ss.render(g2d, context, colorTable, displayCenter, 0f);
                return; //finished
            }else if("1".equals(catlit) || "16".equals(catlit)){
                if(orient != null){
                    //draw a line using orient value with the length VALNMR
                    try{
                        //calculate end point
                        final Shape shp = calculateDisplayLine(ctx,
                                objectiveCenter.x, objectiveCenter.y, orient, valnmr);

                        //draw line
                        final Stroke stroke = SimpleLine.PStyle.DASH.createStroke(1);
                        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
                        g2d.setColor(colorTable.getColor("CHBLK"));
                        g2d.setStroke(stroke);
                        g2d.draw(shp);

                    } catch (TransformException ex) {
                        throw new PortrayalException(ex);
                    }
                }
                final SymbolStyle ss = context.getSyle("LIGHTS82");
                ss.render(g2d, context, colorTable, displayCenter, 0f);
                return;
            }

        }

        //continuation A
        String colour = (String) feature.getProperty("COLOUR").getValue();
        if(colour == null){
            colour = "13";
        }
        final List<String> colors = Arrays.asList(colour.split(","));

        Number sector1 = (Number) feature.getProperty("SECTR1").getValue();
        Number sector2 = (Number) feature.getProperty("SECTR2").getValue();
        if(sector1 == null || sector2 == null){
            //TODO find other lights at same position
            final boolean lightAtSamePosition = false;

            boolean flareAt45Degree = false;
            if(lightAtSamePosition){
                flareAt45Degree = false;
                if(colors.contains("1") || colors.contains("6") || colors.contains("11")){
                    flareAt45Degree = true;
                }
            }

            final SymbolStyle ss;
            if(colors.contains("1") && colors.contains("3")){
                ss = context.getSyle("LIGHTS11");
            }else if(colors.contains("3")){
                ss = context.getSyle("LIGHTS11");
            }else if(colors.contains("1") && colors.contains("4")){
                ss = context.getSyle("LIGHTS12");
            }else if(colors.contains("4")){
                ss = context.getSyle("LIGHTS12");
            }else if(colors.contains("11")){
                ss = context.getSyle("LIGHTS13");
            }else if(colors.contains("6")){
                ss = context.getSyle("LIGHTS13");
            }else if(colors.contains("1")){
                ss = context.getSyle("LIGHTS13");
            }else{
                ss = context.getSyle("LIGHTS11");
            }

            if("1".equals(catlit) || "16".equals(catlit)){
                if(orient != null){
                    double rota = Math.toRadians( (orient.doubleValue()+180) %360d );
                    ss.render(g2d, context, colorTable, displayCenter, (float)rota);

                    final NumericText text = new NumericText();
                    try {
                        text.read("TE('%03.0lf deg','ORIENT',3,3,3,'15110',3,1,CHBLK,23)");
                    } catch (IOException ex) {
                        throw new PortrayalException(ex);
                    }
                    text.render(ctx, context, colorTable, graphic, geotype);

                }else{
                    final SymbolStyle rs = context.getSyle("QUESMRK1");
                    rs.render(g2d, context, colorTable, displayCenter, 0f);
                }

            }else{
                if(flareAt45Degree){
                    ss.render(g2d, context, colorTable, displayCenter, 45f);
                }else{
                    ss.render(g2d, context, colorTable, displayCenter, 135f);
                }
            }

            if(context.isLightDescription()){

                final String litchr = (String) feature.getProperty("LITCHR").getValue();
                final String siggrp = (String) feature.getProperty("SIGGRP").getValue();
                final Number sigper = (Number) feature.getProperty("SIGPER").getValue();
                final Number height = (Number) feature.getProperty("HEIGHT").getValue();
                final String status = (String) feature.getProperty("STATUS").getValue();

                final LITDSN01 litdsn01 = new LITDSN01();
                final String litdsn = litdsn01.render(ctx, context, colorTable, graphic, geotype,
                        catlit, litchr, siggrp,colour,sigper,height,valnmr,status);

                final AlphanumericText text = new AlphanumericText();
                if(flareAt45Degree){
                    try {
                        text.read("TX('"+litdsn+"',3,1,3,'15110',2,-1,CHBLK,23)");
                    } catch (IOException ex) {
                        throw new PortrayalException(ex);
                    }
                    text.render(ctx, context, colorTable, graphic, geotype);
                }else{
                    try {
                        text.read("TX('"+litdsn+"',3,2,3,'15110',2,0,CHBLK,23)");
                    } catch (IOException ex) {
                        throw new PortrayalException(ex);
                    }
                }
                text.render(ctx, context, colorTable, graphic, geotype);
            }
            return; //finished
        }

        // Continuation B
        if(sector1 == null){
            //assume all around light
            sector1 = 0d;
            sector2 = 0d;
        }

        if(sector1.doubleValue()-sector2.doubleValue() == 0){

            final SymbolStyle ss;
            if(colors.contains("1") && colors.contains("3")){
                ss = context.getSyle("LIGHTS11");
            }else if(colors.contains("3")){
                ss = context.getSyle("LIGHTS11");
            }else if(colors.contains("1") && colors.contains("4")){
                ss = context.getSyle("LIGHTS12");
            }else if(colors.contains("4")){
                ss = context.getSyle("LIGHTS12");
            }else if(colors.contains("11")){
                ss = context.getSyle("LIGHTS13");
            }else if(colors.contains("6")){
                ss = context.getSyle("LIGHTS13");
            }else if(colors.contains("1")){
                ss = context.getSyle("LIGHTS13");
            }else{
                ss = context.getSyle("LIGHTS11");
            }
            ss.render(g2d, context, colorTable, displayCenter, 135f);

            if(context.isLightDescription()){
                final String litchr = (String) feature.getProperty("LITCHR").getValue();
                final String siggrp = (String) feature.getProperty("SIGGRP").getValue();
                final Number sigper = (Number) feature.getProperty("SIGPER").getValue();
                final Number height = (Number) feature.getProperty("HEIGHT").getValue();
                final String status = (String) feature.getProperty("STATUS").getValue();

                final LITDSN01 litdsn01 = new LITDSN01();
                final String litdsn = litdsn01.render(ctx, context, colorTable, graphic, geotype,
                        catlit, litchr, siggrp,colour,sigper,height,valnmr,status);

                final AlphanumericText text = new AlphanumericText();
                try {
                    text.read("TX('"+litdsn+"',3,2,3,'15110',2,0,CHBLK,23)");
                } catch (IOException ex) {
                    throw new PortrayalException(ex);
                }
                text.render(ctx, context, colorTable, graphic, geotype);
            }
            return; //finished
        }else{
            if(sector2.doubleValue() < sector1.doubleValue()){
                sector2 = sector2.doubleValue() + 360;
            }

            //set the stroke
            ctx.switchToDisplayCRS();
            final Stroke stroke = SimpleLine.PStyle.DASH.createStroke(1);
            g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
            g2d.setColor(colorTable.getColor("CHBLK"));
            g2d.setStroke(stroke);

            try{
                if(context.isFullSectors()){
                    //calculate first leg
                    Shape leg = calculateDisplayLine(ctx,objectiveCenter.x, objectiveCenter.y, sector1, valnmr);
                    g2d.draw(leg);
                    leg = calculateDisplayLine(ctx,objectiveCenter.x, objectiveCenter.y, sector2, valnmr);
                    g2d.draw(leg);

                }else{
                    final double mapRotation = -XAffineTransform.getRotation(ctx.getObjectiveToDisplay());
                    final double legLength = S52Utilities.mmToPixel(25);
                    final double angle1 = mapRotation + Math.toRadians( 90 - (sector1.doubleValue()+180) %360d );
                    final double angle2 = mapRotation + Math.toRadians( 90 - (sector2.doubleValue()+180) %360d );
                    final double end1X = displayCenter.x + legLength* Math.cos(angle1);
                    final double end1Y = displayCenter.y + legLength* Math.sin(angle1);
                    final double end2X = displayCenter.x + legLength* Math.cos(angle2);
                    final double end2Y = displayCenter.y + legLength* Math.sin(angle2);

                    final Shape leg1 = new Line2D.Double(displayCenter.x, displayCenter.y, end1X, end1Y);
                    final Shape leg2 = new Line2D.Double(displayCenter.x, displayCenter.y, end2X, end2Y);
                    g2d.draw(leg1);
                    g2d.draw(leg2);
                }
            }catch(TransformException ex){
                throw new PortrayalException(ex);
            }

        }

        //TODO second part of continuation B
        //need to know other lights overlapping
    }

    /**
     *
     * @param startX : in objective crs
     * @param startY : in objective crs
     * @param orientation : in degrees relative to true north from seaward
     * @param distance : distance in nautical miles
     * @return end point
     */
    private static Point2D calculateEnd(CoordinateReferenceSystem objCrs,
            double startX, double startY, Number orient, Number valnmr){
        final GeodeticCalculator gc = new GeodeticCalculator(objCrs);
        gc.setStartingGeographicPoint(new DirectPosition2D(startX, startY));
        double distance = S52Utilities.NAUTIC_MILES_TO_METERS.convert(valnmr.doubleValue());
        final Unit ellipsoidUnit = gc.getEllipsoid().getAxisUnit();
        distance = SI.METRE.getConverterTo(ellipsoidUnit).convert(distance);
        gc.setDirection( ((orient.doubleValue()+180) %360d)-180, distance);
        final Point2D endpt = gc.getDestinationGeographicPoint();
        return endpt;
    }

    private static Shape calculateDisplayLine(RenderingContext2D ctx,
            double startX, double startY, Number orient, Number valnmr)
            throws MismatchedDimensionException, TransformException{

        final Point2D endpt = calculateEnd(ctx.getObjectiveCRS2D(),
                startX, startY, orient, valnmr);

        //build line
        Geometry line = new GeometryFactory().createLineString(
                new Coordinate[]{
                    new Coordinate(startX, startY),
                    new Coordinate(endpt.getX(), endpt.getY())
                });
        line = JTS.transform(line, ctx.getObjectiveToDisplay());
        final Shape shp = new JTSGeometryJ2D(line);
        return shp;
    }


}
