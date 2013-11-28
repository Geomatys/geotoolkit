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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.j2d.PathWalker;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.instruction.AlphanumericText;
import org.geotoolkit.s52.lookuptable.instruction.NumericText;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.geotoolkit.s52.render.SymbolStyle;
import org.geotoolkit.s52.symbolizer.S52Graphic;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * S-52 Annex A Part I p.160 (12.2.7)
 *
 * @author Johann Sorel (Geomatys)
 */
public class LIGHTS05 extends Procedure{

    private static final SimpleLine BLACK_ONE = new SimpleLine(SimpleLine.PStyle.DASH, 1, "CHBLK");
    private static final SimpleLine OUTLW_FOUR = new SimpleLine(SimpleLine.PStyle.SOLD, 4, "OUTLW");

    public LIGHTS05() {
        super("LIGHTS05");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {
        final Graphics2D g2d = ctx.getGraphics();

        Number valnmr = (Number) graphic.feature.getProperty("VALNMR").getValue();
        String[] catlit = (String[]) graphic.feature.getProperty("CATLIT").getValue();
        Number orient = (Number) graphic.feature.getProperty("ORIENT").getValue();

        final Coordinate displayCenter;
        final Coordinate objectiveCenter;
        try {
            displayCenter = graphic.graphic.getGeometry(null).getDisplayGeometryJTS().getCoordinate();
            objectiveCenter = graphic.graphic.getGeometry(null).getObjectiveGeometryJTS().getCentroid().getCoordinate();
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        if(valnmr == null){
            valnmr = 9;
        }

        if(catlit != null){

            if(S52Utilities.containsAny(catlit, "8","11")){
                final SymbolStyle ss = context.getSyle("LIGHTS82");
                ss.render(g2d, context, colorTable, displayCenter, 0f);
                return; //finished
            }else if(S52Utilities.containsAny(catlit, "9")){
                final SymbolStyle ss = context.getSyle("LIGHTS81");
                ss.render(g2d, context, colorTable, displayCenter, 0f);
                return; //finished
            }else if(S52Utilities.containsAny(catlit, "1","16")){
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
            }

        }

        //continuation A
        String[] colour = (String[]) graphic.feature.getProperty("COLOUR").getValue();
        if(colour == null || colour.length==0){
            colour = new String[]{"12"};
        }
        final List<String> colors = Arrays.asList(colour);

        //sector values are in 0/360°
        Number sector1 = (Number) graphic.feature.getProperty("SECTR1").getValue();
        Number sector2 = (Number) graphic.feature.getProperty("SECTR2").getValue();
        if(sector1 == null || sector2 == null){
            //TODO find other lights at same position
            final boolean lightAtSamePosition = false;

            boolean flareAt45Degree = false;
            if(lightAtSamePosition){
                flareAt45Degree = false;
                if(S52Utilities.containsAny(colour, "1","6","11")){
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

            if(S52Utilities.containsAny(catlit, "1","16")){
                if(orient != null){
                    double rota = lightAngleRadian(orient.doubleValue());
                    ss.render(g2d, context, colorTable, displayCenter, (float)rota);

                    final NumericText text = new NumericText();
                    try {
                        text.read("TE('%03.0lf deg','ORIENT',3,3,3,'15110',3,1,CHBLK,23)");
                    } catch (IOException ex) {
                        throw new PortrayalException(ex);
                    }
                    text.render(ctx, context, colorTable, all, graphic);

                }else{
                    final SymbolStyle rs = context.getSyle("QUESMRK1");
                    rs.render(g2d, context, colorTable, displayCenter, 0f);
                }

            }else{
                if(flareAt45Degree){
                    ss.render(g2d, context, colorTable, displayCenter, (float)Math.toRadians(45.0));
                }else{
                    ss.render(g2d, context, colorTable, displayCenter, (float)Math.toRadians(135.0));
                }
            }

            if(context.isLightDescription()){

                final String litchr = (String) graphic.feature.getProperty("LITCHR").getValue();
                final String siggrp = (String) graphic.feature.getProperty("SIGGRP").getValue();
                final Number sigper = (Number) graphic.feature.getProperty("SIGPER").getValue();
                final Number height = (Number) graphic.feature.getProperty("HEIGHT").getValue();
                final String[] status = (String[]) graphic.feature.getProperty("STATUS").getValue();

                final LITDSN01 litdsn01 = (LITDSN01) context.getProcedure("LITDSN01");
                final String litdsn = litdsn01.render(ctx, context, colorTable, all, graphic,
                        catlit, litchr, siggrp,colour,sigper,height,valnmr,status);

                final AlphanumericText text = new AlphanumericText();
                if(flareAt45Degree){
                    try {
                        text.read("TX('"+litdsn+"',3,1,3,'15110',2,-1,CHBLK,23)");
                    } catch (IOException ex) {
                        throw new PortrayalException(ex);
                    }
                    text.render(ctx, context, colorTable, all, graphic);
                }else{
                    try {
                        text.read("TX('"+litdsn+"',3,2,3,'15110',2,0,CHBLK,23)");
                    } catch (IOException ex) {
                        throw new PortrayalException(ex);
                    }
                }
                text.render(ctx, context, colorTable, all, graphic);
            }
            return; //finished
        }

        // Continuation B
        if(sector1 == null){
            //assume all around light
            sector1 = 0d;
            sector2 = 0d;
        }

        //sector legs;
        Line2D.Double leg1 = null;
        Line2D.Double leg2 = null;
        final double mapRotationRadians = XAffineTransform.getRotation(ctx.getObjectiveToDisplay());
        final double mapRotationDegrees = Math.toDegrees(mapRotationRadians);

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
            ss.render(g2d, context, colorTable, displayCenter, (float)Math.toRadians(135.0));

            if(context.isLightDescription()){
                final String litchr = (String) graphic.feature.getProperty("LITCHR").getValue();
                final String siggrp = (String) graphic.feature.getProperty("SIGGRP").getValue();
                final Number sigper = (Number) graphic.feature.getProperty("SIGPER").getValue();
                final Number height = (Number) graphic.feature.getProperty("HEIGHT").getValue();
                final String[] status = (String[]) graphic.feature.getProperty("STATUS").getValue();

                final LITDSN01 litdsn01 = new LITDSN01();
                final String litdsn = litdsn01.render(ctx, context, colorTable, all, graphic,
                        catlit, litchr, siggrp,colour,sigper,height,valnmr,status);

                final AlphanumericText text = new AlphanumericText();
                try {
                    text.read("TX('"+litdsn+"',3,2,3,'15110',2,0,CHBLK,23)");
                } catch (IOException ex) {
                    throw new PortrayalException(ex);
                }
                text.render(ctx, context, colorTable, all, graphic);
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
                final double legLength;
                if(context.isFullSectors()){
                    //real leg, this is the real geometry,
                    //if we use it directly the angles will not be right because of the projection
                    //Since we must preserve the visual angle of the sector, we only keep the length
                    //of this real leg
                    final Line2D rleg = calculateDisplayLine(ctx,objectiveCenter.x, objectiveCenter.y, sector1, valnmr);
                    legLength = PathWalker.distance((float)rleg.getX1(),(float)rleg.getY1(),(float)rleg.getX2(),(float)rleg.getY2());

                }else{
                    legLength = S52Utilities.mmToPixel(25);
                }

                final double radsector1 = lightAngleRadian(sector1.doubleValue()+90d) - mapRotationRadians;
                final double radsector2 = lightAngleRadian(sector2.doubleValue()+90d) - mapRotationRadians;

                final double end1X = displayCenter.x + legLength* Math.cos(radsector1);
                final double end1Y = displayCenter.y + legLength* Math.sin(radsector1);
                final double end2X = displayCenter.x + legLength* Math.cos(radsector2);
                final double end2Y = displayCenter.y + legLength* Math.sin(radsector2);

                leg1 = new Line2D.Double(displayCenter.x, displayCenter.y, end1X, end1Y);
                leg2 = new Line2D.Double(displayCenter.x, displayCenter.y, end2X, end2Y);
                g2d.draw(leg1);
                g2d.draw(leg2);
            }catch(TransformException ex){
                throw new PortrayalException(ex);
            }

        }

        //second part of continuation B
        boolean extendedArcRadius = false;
        final boolean otherlightAtPosition = false;
        if(otherlightAtPosition){
            //TODO need to know other lights overlapping
        }

        final double radsector1 = sector1.doubleValue();
        final double radsector2 = sector2.doubleValue();

        final String[] litvis = (String[]) graphic.feature.getProperty("LITVIS").getValue();
        if(S52Utilities.containsAny(litvis, "7","8","3")){
            final double radius;
            if(extendedArcRadius){
                radius = S52Utilities.mmToPixel(25);
            }else{
                radius = S52Utilities.mmToPixel(20);
            }

            final Shape arc = arc(objectiveCenter.x, objectiveCenter.y, radsector1, radsector2, mapRotationDegrees, radius);
            if(arc==null) return;
            drawArc(g2d, colorTable, arc, BLACK_ONE);

        }else{
            final String color;
            if(colors.contains("1") && colors.contains("3")){
                color = "LITRD";
            }else if(colors.contains("3")){
                color = "LITRD";
            }else if(colors.contains("1") && colors.contains("4")){
                color = "LITGN";
            }else if(colors.contains("4")){
                color = "LITGN";
            }else if(colors.contains("11")){
                color = "LITYW";
            }else if(colors.contains("6")){
                color = "LITYW";
            }else if(colors.contains("1")){
                color = "LITYW";
            }else{
                color = "CHMGD";
            }
            final SimpleLine sl = new SimpleLine(SimpleLine.PStyle.SOLD, 2, color);

            final double radius;
            if(extendedArcRadius){
                radius = S52Utilities.mmToPixel(25);
            }else{
                radius = S52Utilities.mmToPixel(20);
            }

            final Shape arc = arc(displayCenter.x, displayCenter.y, radsector1, radsector2, mapRotationDegrees, radius);
            if(arc==null) return;
            drawArc(g2d, colorTable, arc, OUTLW_FOUR);
            drawArc(g2d, colorTable, arc, sl);
        }

    }

    private void drawArc(Graphics2D g2d, S52Palette colorTable, Shape arc, SimpleLine sl){
        final Stroke stroke = sl.getStroke();
        final Color color = sl.getColor(colorTable);
        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
        g2d.setColor(color);
        g2d.setStroke(stroke);
        g2d.draw(arc);
    }

    private static double lightAngleRadian(double value){
        return Math.toRadians(lightAngleDegree(value));
    }

    private static double lightAngleDegree(double value){
        return (value % 360d);
    }

    private static Shape arc(double startX, double startY, double sector1, double sector2, double mapRotation, double distance){

        //sector values are from sea side
        //      |
        //      |
        // -----+-----
        //      |
        //    0 | 360
        //we need to convert it to java2d arc cs
        //      |
        //      |   0
        // -----+-----
        //      |  360
        //      |
        sector1 = (360-sector1) - 90;
        sector2 = (360-sector2) - 90;

        //concatenate map rotation
        sector1 = (sector1+mapRotation);
        sector2 = (sector2+mapRotation);
        
        // JVM crash for arc2d with sector values : 160, 90
        // to fix it flatten the geometry
        // TODO find a beter solution ...
        double extent = sector1 - sector2;
        final Arc2D arc = new Arc2D.Double(startX-distance, startY-distance,
                         distance*2,
                         distance*2,
                         sector2, extent,
                         Arc2D.OPEN);
        final Path2D path = new GeneralPath();
        path.append(arc.getPathIterator(null,1f), false);
        return path;
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
            double startX, double startY, Number orient, Number valnmr) throws TransformException{
        final GeodeticCalculator gc = new GeodeticCalculator(objCrs);
        gc.setStartingPosition(new DirectPosition2D(objCrs,startX, startY));
        double distance = S52Utilities.NAUTIC_MILES_TO_METERS.convert(valnmr.doubleValue());
        final Unit ellipsoidUnit = gc.getEllipsoid().getAxisUnit();
        distance = SI.METRE.getConverterTo(ellipsoidUnit).convert(distance);
        gc.setDirection(lightAngleDegree(orient.doubleValue())-180d, distance);
        final DirectPosition endpt = gc.getDestinationPosition();
        return new Point2D.Double(endpt.getOrdinate(0), endpt.getOrdinate(1));
    }

    private static Line2D.Double calculateDisplayLine(RenderingContext2D ctx,
            double startX, double startY, Number orient, Number valnmr)
            throws MismatchedDimensionException, TransformException{

        final Point2D endpt = calculateEnd(ctx.getObjectiveCRS2D(),
                startX, startY, orient, valnmr);

        //build line
        final Point2D pt1 = ctx.getObjectiveToDisplay().transform(new Point2D.Double(startX, startY), null);
        final Point2D pt2 = ctx.getObjectiveToDisplay().transform(endpt, null);
        return new Line2D.Double(pt1,pt2);
    }

}
