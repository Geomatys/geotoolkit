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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Context.GeoType;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.geotoolkit.s52.lookuptable.instruction.Instruction;
import org.opengis.feature.Feature;

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
        final String objClassCode = S52Utilities.getObjClass(feature);
        Geometry geom = (Geometry) feature.getDefaultGeometryProperty().getValue();
        final S52Context context = getS52Context();

        //find geometry category
        final GeoType geoType = getType(geom);
        if(geoType==null){
            //no geometry
            return;
        }
        //get the appropriate informations
        final S52Palette colorTable = context.getPalette();
        final LookupTable lookup = context.getLookupTable(geoType);
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
                inst.render(renderingContext, context, colorTable, graphic, geoType);
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
