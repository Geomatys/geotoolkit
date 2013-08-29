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
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
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
import org.geotoolkit.display2d.style.j2d.DefaultPathWalker;
import org.geotoolkit.display2d.style.j2d.PathWalker;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Context.GeoType;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52SVGIcon;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.dai.PatternDefinition;
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
import org.geotoolkit.s52.render.PatternSymbolStyle;
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
                        new URL("file:/media/jsorel/terra/TRAVAIL/1_Specification/IHO/S-52/S-52_CD/PresLib_e3.4_2008/Digital_Files/Digital_PresLib/pslb03_4.dai")
                        );

//                s52context.load(
//                        new URL("file:/media/jsorel/terra/TRAVAIL/1_Specification/IHO/S-52/S-52_CD/PresLib_e3.4_2008/Digital_Files/Digital_PresLib/pslb03_4.dai"),
//                        S52Context.ICONS,
//                        S52Context.LK_AREA_BOUNDARY,
//                        S52Context.LK_LINE,
//                        S52Context.LK_POINT_SIMPLIFIED
//                        );
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
                    final PatternSymbolStyle ss = (PatternSymbolStyle) context.getSyle(pf.patternName);
                    final PatternDefinition pd = (PatternDefinition) ss.definition;

                    //TODO handle size/distance correction
                    final int maxdist = pd.PAMA;
                    final int mindist = pd.PAMI; //used in constant scaling
                    final String scaling = pd.PASP;
                    final String placement = pd.PATP;

                    final float spacing = SymbolStyle.SCALE*mindist;
                    final Rectangle2D rect = ss.getBounds();
                    final float px = ss.definition.getPivotX()*SymbolStyle.SCALE;
                    final float py = ss.definition.getPivotY()*SymbolStyle.SCALE;

                    final TexturePaint paint;
                    if("LIN".equals(placement)){
                        final Coordinate center = new Coordinate(px-rect.getX(), py-rect.getY());
                        final BufferedImage img = new BufferedImage((int)(rect.getWidth()+spacing),
                                                                    (int)(rect.getHeight()+spacing),
                                                                    BufferedImage.TYPE_INT_ARGB);
                        ss.render(img.createGraphics(), context, colorTable, center, 0f);
                        paint = new TexturePaint(img, new Rectangle2D.Double(0, 0,
                                rect.getWidth()+spacing, rect.getHeight()+spacing));
                    }else if("STG".equals(placement)){
                        final Coordinate center = new Coordinate(px-rect.getX(), py-rect.getY());
                        final BufferedImage img = new BufferedImage((int)(rect.getWidth()*2+spacing*2),
                                                                    (int)(rect.getHeight()*2+spacing*2),
                                                                    BufferedImage.TYPE_INT_ARGB);
                        //first symbol
                        ss.render(img.createGraphics(), context, colorTable, center, 0f);
                        //second symbol with displacement
                        center.x += rect.getWidth() + spacing;
                        center.y += rect.getHeight()+ spacing;
                        ss.render(img.createGraphics(), context, colorTable, center, 0f);

                        paint = new TexturePaint(img, new Rectangle2D.Double(0, 0,
                                rect.getWidth()*2+spacing*2, rect.getHeight()*2+spacing*2));
                    }else{
                        throw new PortrayalException("Unexpected placement : "+placement);
                    }

                    g2d.setPaint(paint);
                    g2d.fill(graphic.getGeometry(null).getDisplayShape());

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
                    final SymbolStyle ss = context.getSyle(cl.LINNAME);

                    final PathIterator ite = graphic.getGeometry(null).getDisplayShape().getPathIterator(null);
                    final PathWalker walker = new DefaultPathWalker(ite);
                    final Point2D pt = new Point2D.Double();
                    while(!walker.isFinished()){
                        //TODO not correct
                        walker.walk(1);
                        walker.getPosition(pt);
                        final float rotation = walker.getRotation();
                        ss.render(g2d, context, colorTable, new Coordinate(pt.getX(),pt.getY()), rotation);
                        walker.walk(9);
                    }

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
                    ss.render(g2d, context, colorTable, center, rotation);


                }else if(inst instanceof ConditionalSymbolProcedure){
                    final ConditionalSymbolProcedure con = (ConditionalSymbolProcedure) inst;
                    //con.procedureName
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
