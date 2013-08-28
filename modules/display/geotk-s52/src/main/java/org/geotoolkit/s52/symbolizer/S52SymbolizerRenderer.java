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
package org.geotoolkit.s52.symbolizer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Context.GeoType;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52SVGIcon;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.dai.SymbolVector;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.lookuptable.instruction.ColorFill;
import org.geotoolkit.s52.lookuptable.instruction.Instruction;
import org.geotoolkit.s52.lookuptable.instruction.ComplexLine;
import org.geotoolkit.s52.lookuptable.instruction.ConditionalSymbolProcedure;
import org.geotoolkit.s52.lookuptable.instruction.PatternFill;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.opengis.feature.Feature;
import org.geotoolkit.s52.lookuptable.instruction.Text;
import org.geotoolkit.s52.render.SymbolStyle;
import org.geotoolkit.util.Converters;
import org.opengis.feature.Property;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52SymbolizerRenderer extends AbstractSymbolizerRenderer<S52CachedSymbolizer>{

    public static final RenderingHints.Key KEY = new GO2Hints.NamedKey(S52Context.class,"context");

    private S52Context s52context = null;

    public S52SymbolizerRenderer(SymbolizerRendererService service, S52CachedSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    private S52Context getS52Context() throws PortrayalException{
        if(s52context != null) return s52context;
        s52context = (S52Context) renderingContext.getCanvas().getRenderingHint(KEY);
        if(s52context==null){
            s52context = new S52Context();
            try {
                s52context.load(
                        new URL("file:/media/jsorel/terra/TRAVAIL/1_Specification/IHO/S-52/S-52_CD/PresLib_e3.4_2008/Digital_Files/Digital_PresLib/pslb03_4.dai"),
                        S52Context.ICONS,
                        S52Context.LK_AREA_BOUNDARY,
                        S52Context.LK_LINE,
                        S52Context.LK_POINT_SIMPLIFIED
                        );
            } catch (IOException ex) {
                throw new PortrayalException(ex);
            }
            renderingContext.getCanvas().setRenderingHint(KEY, s52context);
        }
        return s52context;
    }

    @Override
    public void portray(ProjectedObject graphic) throws PortrayalException {
        final Feature feature = (Feature) graphic.getCandidate();

        //Follow schema Annex A Part I p.68
        String objClassCode = feature.getType().getName().getLocalPart();
        final int sep = objClassCode.indexOf('_');
        if(sep >= 0){
            objClassCode = objClassCode.substring(sep+1);
        }
        Geometry geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
        final S52Context context = getS52Context();

        //find geometry category
        final GeoType type = getType(geom);
        if(type==null){
            //no geometry
            return;
        }
        //get the appropriate informations
        final S52Palette colorTable = context.getPalette();
        final LookupTable lookup = context.getLookupTable(type);
        final List<LookupRecord> records = lookup.getRecords(objClassCode);

        if(records.isEmpty()){
            //TODO show the QUESTMRK SYMBOL for appropriate geometry type
            System.out.println("No S-52 symbol for type : " +objClassCode);
            return;
        }

        //ensure we paint in display mode.
        renderingContext.switchToDisplayCRS();

        final LookupRecord record = getActiveRecord(records,feature);
        final Instruction[] instructions = record.getInstruction();

        try{
            for(Instruction inst : instructions){

                if(inst instanceof Text){

                    if(s52context.isNoText()) continue;

                    //this includ alphanumeric and numeric texts
                    final Text text = (Text) inst;

                    //get font and text metas
                    final Font font = text.getFont();
                    g2d.setFont(font);
                    final Expression expStr = text.getText();
                    final String str = expStr.evaluate(feature, null);
                    FontMetrics fm = null;
                    Integer fontHeight = null;

                    //find and adjust pivot point
                    final Coordinate pivotPoint = getPivotPoint(graphic.getGeometry(null).getDisplayGeometryJTS());
                    if(text.xOffset != 0){
                        if(fm==null) fm = g2d.getFontMetrics(font);
                        if(fontHeight==null) fontHeight = fm.getAscent();
                        pivotPoint.x += text.xOffset * fontHeight;
                    }
                    if(text.yOffset != 0){
                        if(fm==null) fm = g2d.getFontMetrics(font);
                        if(fontHeight==null) fontHeight = fm.getAscent();
                        pivotPoint.y += text.yOffset * fontHeight;
                    }

                    //set color
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    final Color color = colorTable.getColor(text.color);
                    g2d.setColor(color);


                    if(text.horizontalAdjust != 3 || text.verticalAdjust != 1){
                        //calculate horizontal and vertical adjustement
                        if(fm==null) fm = g2d.getFontMetrics(font);

                        if(text.horizontalAdjust==1){
                            final int width = fm.stringWidth(str);
                            pivotPoint.x -= width/2;
                        }else if(text.horizontalAdjust==2){
                            final int width = fm.stringWidth(str);
                            pivotPoint.x -= width;
                        }

                        if(text.verticalAdjust==2){
                        if(fontHeight==null) fontHeight = fm.getAscent();
                            pivotPoint.y += fontHeight/2;
                        }else if(text.verticalAdjust==3){
                        if(fontHeight==null) fontHeight = fm.getAscent();
                            pivotPoint.y += fontHeight;
                        }
                    }

                    //TODO handle SPACE parameter

                    g2d.drawString(str, (float)pivotPoint.x, (float)pivotPoint.y);

                }else if(inst instanceof ColorFill){
                    final ColorFill cf = (ColorFill) inst;
                    final Color color = colorTable.getColor(cf.color);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cf.getAlpha()));
                    g2d.setColor(color);
                    g2d.fill(graphic.getGeometry(null).getDisplayShape());

                }else if(inst instanceof PatternFill){
                    final PatternFill pf = (PatternFill) inst;
                    //LOGGER.log(Level.INFO, "TODO support instruction : {0}", inst.getCode());

                }else if(inst instanceof SimpleLine){
                    final SimpleLine sl = (SimpleLine) inst;
                    final Stroke stroke = sl.getStroke();
                    final Color color = colorTable.getColor(sl.color);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    g2d.setColor(color);
                    g2d.setStroke(stroke);
                    g2d.draw(graphic.getGeometry(null).getDisplayShape());

                }else if(inst instanceof ComplexLine){
                    final ComplexLine cl = (ComplexLine) inst;
                    //LOGGER.log(Level.INFO, "TODO support instruction : {0}", inst.getCode());

                }else if(inst instanceof Symbol){
                    final Symbol symbol = (Symbol) inst;
                    final Coordinate center = getPivotPoint(graphic.getGeometry(null).getDisplayGeometryJTS());
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                    //find rotation
                    float rotation = 0f;
                    if(symbol.rotation == null || symbol.rotation.isEmpty()){
                        rotation = 0f;
                    }else{
                        try{
                            rotation = (float)Math.toRadians(Integer.valueOf(symbol.rotation));
                        }catch(NumberFormatException ex){
                            //it's a field
                            final Property prop = feature.getProperty(symbol.rotation);
                            if(prop!=null){
                                Float val = Converters.convert(prop.getValue(),Float.class);
                                if(val!=null){
                                    //combine with map rotation
                                    rotation = -(float)XAffineTransform.getRotation(renderingContext.getObjectiveToDisplay());
                                    rotation += Math.toRadians(val);
                                }
                            }
                        }

                    }


                    final SymbolStyle ss = context.getSyle(symbol.symbolName);
                    if(ss.definition.SYDF.equals("R")){
                        System.out.println(">>>>>>>>>>>>>>> RASTER");
                    }else if(ss.definition.SYDF.equals("V")){
                        final float boxwidth  = ss.definition.SBXC;
                        final float boxheight = ss.definition.SBXR;
                        final float pivotX    = ss.definition.SYCL;
                        final float pivotY    = ss.definition.SYRW;
                        //one unit = 0.01mm
                        //adjust values back to pixel units
                        final float scale = S52Utilities.mmToPixel(1) * 0.01f;

                        final AffineTransform old = g2d.getTransform();
                        final AffineTransform trs = new AffineTransform();
                        trs.translate(center.x, center.y);
                        trs.scale(scale, scale);
                        trs.rotate(rotation);
                        trs.translate(-pivotX, -pivotY);
                        g2d.setTransform(trs);

                        float alpha = 1f;
                        float tx = 0f;
                        float ty = 0f;

                        boolean polygonMode = false;
                        Path2D path = null;

                        for(SymbolVector sv : ss.vectors){
                            final String[] parts = sv.VECD.split(";");
                            for(String part : parts){
                                //S52 Annex A Part I p.34 (5)
                                final String action = part.substring(0, 2);
                                if("SP".equals(action)){
                                    //color
                                    final String colorCode = ""+part.charAt(2);
                                    final Color color;
                                    if(colorCode.equals("@")){
                                        color = new Color(0, 0, 0, 0);
                                    }else{
                                        final String colorName = ss.colors.colors.get(colorCode);
                                        color = colorTable.getColor(colorName);
                                    }
                                    g2d.setColor(color);

                                }else if("ST".equals(action)){
                                    //transparency
                                    final char trans = part.charAt(2);
                                    switch(trans){
                                        case '0' : alpha = 1f; break;
                                        case '1' : alpha = 0.25f; break;
                                        case '2' : alpha = 0.50f; break;
                                        case '3' : alpha = 0.75f; break;
                                        default : alpha = 1f;
                                    }

                                }else if("SW".equals(action)){
                                    //pen size
                                    float size = Integer.valueOf(part.substring(2));
                                    //one unit = 0.3mm
                                    size = S52Utilities.mmToPixel(size*0.3f) * (1f/scale);
                                    g2d.setStroke(new BasicStroke(size));

                                }else if("PU".equals(action)){
                                    //move pen , no draw
                                    part = part.substring(2);
                                    final int index = part.indexOf(',');
                                    tx = Integer.valueOf(part.substring(0, index));
                                    ty = Integer.valueOf(part.substring(index+1));
                                    if(polygonMode){
                                        path.moveTo(tx,ty);
                                    }

                                }else if("PD".equals(action)){
                                    part = part.substring(2);
                                    final String[] pts = part.split(",");
                                    final Path2D line = (polygonMode) ? path : new Path2D.Float();
                                    line.moveTo(tx, ty);
                                    for(int k=0;k<pts.length;k+=2){
                                        final float ex = Integer.valueOf(pts[k]);
                                        final float ey = Integer.valueOf(pts[k+1]);
                                        line.lineTo(ex,ey);
                                        tx = ex;
                                        ty = ey;
                                    }
                                    if(!polygonMode){
                                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                                        g2d.draw(line);
                                    }

                                }else if("CI".equals(action)){
                                    //circle
                                    float size = Integer.valueOf(part.substring(2));
                                    final Shape shp = new Ellipse2D.Float(tx-size, ty-size, size*2, size*2);
                                    if(polygonMode){
                                        path.append(shp, true);
                                    }else{
                                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                                        g2d.draw(shp);
                                    }

                                }else if("AA".equals(action)){
                                    throw new IOException("Action not implemented yet : "+part);
                                }else if("PM".equals(action)){
                                    //polygon operations
                                    final char trans = part.charAt(2);
                                    switch(trans){
                                        case '0' :
                                            path = new Path2D.Float();
                                            polygonMode = true;
                                            break;
                                        case '1' :
                                            path.closePath();
                                            break;
                                        case '2' :
                                            polygonMode = false;
                                            break;
                                        default : throw new IOException("unexpected action : "+part);
                                    }

                                }else if("EP".equals(action)){
                                    //outline polygon
                                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                                    g2d.draw(path);

                                }else if("FP".equals(action)){
                                    //fill polygon
                                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                                    g2d.draw(path);

                                }else if("SC".equals(action)){
                                    final String name = part.substring(2, 9);
                                    part = part.substring(9);
                                    System.out.println("SC Action not implemented yet.");
                                }else{
                                    throw new IOException("unexpected action : "+part);
                                }
                            }
                        }

                        g2d.setTransform(old);
                    }

                    ////////////////////////////////////////////////////////////
//                    final S52SVGIcon icon = context.getIcon(symbol.symbolName).derivate(colorTable);
//                    icon.paint(g2d, new Point2D.Double(pivotPoint.x, pivotPoint.y), rotation);
                    //for debugging, see the center
//                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//                    g2d.setColor(Color.red);
//                    g2d.fillRect((int)center.x-1, (int)center.y-1, 2, 2);
                    ////////////////////////////////////////////////////////////

                }else if(inst instanceof ConditionalSymbolProcedure){
                    final ConditionalSymbolProcedure con = (ConditionalSymbolProcedure) inst;
                    //LOGGER.log(Level.INFO, "TODO support instruction : {0}", inst.getCode());

                } else{
                    throw new PortrayalException("Unexpected instruction : " + inst.getCode());
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    /**
     * Find the lookup record which apply to this feature.
     * @param records
     * @param feature
     * @return
     */
    private LookupRecord getActiveRecord(List<LookupRecord> records, Feature feature){
        final int size = records.size();
        // 0 is the fail safe record (p.66 8.3.3.3)
        LookupRecord validRec = records.get(0);
        if(size == 1){
            // Annex A part I p.65 8.3.3.2
            // If only a single line is found, field 2 of that line shall be empty
            // and the object is always shown with the same symbology.
            return records.get(0);
        }

        for(int i=1;i<size;i++){
            //filter on fields
            final LookupRecord rec = records.get(i);
            if(rec.getFilter().evaluate(feature)){
                return rec;
            }
        }

        return validRec;
    }

    private Coordinate getPivotPoint(Geometry geom){
        try{
            if(geom instanceof Point || geom instanceof MultiPoint){
                return (Coordinate)geom.getCoordinate().clone();

            }else if(geom instanceof LineString || geom instanceof MultiLineString){
                //S-52 Annex A Part I p.47 7.1.1
                // The pivot-point for text for a line is the centre of a single segment line.
                // For a multi-segment-line the pivot-point is the mid-point of the run-length of the line.
                return geom.getInteriorPoint().getCoordinate();

            }else if(geom instanceof Polygon || geom instanceof MultiPolygon){
                // The pivot-point for text for an area object is the centre of the area
                return geom.getInteriorPoint().getCoordinate();

            }else{
                //some other kind of geometry, normaly not happening but might be possible
                //if S-52 style is used on something else then S-57 datas.
                return geom.getInteriorPoint().getCoordinate();
            }
        }catch(TopologyException ex){
            //renderingContext.getMonitor().exceptionOccured(ex, Level.INFO);
            //JTS is sometimes unstable
            //falback on centroid if we have problems.
            return geom.getCentroid().getCoordinate();
        }
    }

    @Override
    public boolean hit(ProjectedObject graphic, SearchAreaJ2D mask, VisitFilter filter) {
        //TODO
        return false;
    }

    @Override
    public void portray(ProjectedCoverage graphic) throws PortrayalException {
        // no S-52 symbology for coverage
    }

    @Override
    public boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter) {
        // no S-52 symbology for coverage
        return false;
    }

    private static GeoType getType(Geometry geom){
        if(geom instanceof Point || geom instanceof MultiPoint){
            return GeoType.POINT;
        }else if(geom instanceof LineString || geom instanceof MultiLineString){
            return GeoType.LINE;
        }else if(geom instanceof Polygon || geom instanceof MultiPolygon){
            return GeoType.AREA;
        }
        return null;
    }

}
