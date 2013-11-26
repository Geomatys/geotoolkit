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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.DefaultProjectedObject;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Context.GeoType;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.IMODisplayCategory;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.geotoolkit.s52.lookuptable.instruction.Instruction;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52SymbolizerRenderer extends AbstractSymbolizerRenderer<S52CachedSymbolizer>{

    private S52Context s52context = null;

    private final List<S52Graphic> elements = new ArrayList<>();

    public S52SymbolizerRenderer(SymbolizerRendererService service, S52CachedSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    private S52Context getS52Context() throws PortrayalException{
        if(s52context != null) return s52context;
        s52context = S52Utilities.getS52Context(renderingContext.getCanvas());
        return s52context;
    }

    @Override
    public void portray(ProjectedObject graphic) throws PortrayalException {
        portray(Collections.singleton(graphic).iterator());
    }

    @Override
    public void portray(Iterator<? extends ProjectedObject> graphics) throws PortrayalException {
        elements.clear();

        //used by filters
        final double currentScale = renderingContext.getGeographicScale();

        final S52Context context = getS52Context();
        final S52Palette colorTable = context.getPalette();

        //cache all objects to render, we need to sort them before rendering
        while(graphics.hasNext()){
            if(monitor.stopRequested()) return;

            //create the cache for future rendering
            final S52Graphic element = new S52Graphic();
            element.graphic = graphics.next();
            element.feature = (Feature) element.graphic.getCandidate();

            //check min/max scale on the record
            if(context.isScaleFilter()){
                final Number minScale = (Number)S52Utilities.getValue(element, "SCAMIN");
                final Number maxScale = (Number)S52Utilities.getValue(element, "SCAMAX");
                if(minScale!=null && minScale.doubleValue() < currentScale){
                    continue;
                }
                if(maxScale!=null && maxScale.doubleValue() > currentScale){
                    continue;
                }
            }

            //copy and cache the graphic
            final ProjectedFeature dpf = new ProjectedFeature(
                    ((DefaultProjectedObject)element.graphic).getParameters(),element.feature);
            element.graphic = dpf;

            //Follow schema Annex A Part I p.68
            final String objClassCode = S52Utilities.getObjClass(element.feature);

            //filter rendering, exclude classes which are marked as hidden
            if(context.getHiddenClasses().contains(objClassCode)){
                continue;
            }

            final Geometry geom = (Geometry) element.feature.getDefaultGeometryProperty().getValue();

            //find geometry category
            element.geoType = getType(geom);
            if(element.geoType==null){
                //no geometry
                return;
            }
            //get the appropriate informations
            final LookupTable lookup = context.getLookupTable(element.geoType);
            final List<LookupRecord> records = lookup.getRecords(objClassCode);

            if(records.isEmpty()){
                //TODO show the QUESTMRK SYMBOL for appropriate geometry type
                System.out.println("No S-52 symbol for type : " +objClassCode);
                continue;
            }

            element.record = LookupTable.getActiveRecord(records,element.feature);

            //filter rendering, exclude elemens which are not in the current context category
            final IMODisplayCategory category = element.record.getDisplayCategory();
            if(   category.equals(IMODisplayCategory.DISPLAYBASE)
               || category.equals(IMODisplayCategory.STANDARD)
               || category.equals(IMODisplayCategory.OTHER)
               || category.equals(IMODisplayCategory.NULL)){
                if(category.getPriority() > s52context.getDisplayChartCategory().getPriority()){
                    continue;
                }
            }else if(category.equals(IMODisplayCategory.MARINERS_DISPLAYBASE)
               || category.equals(IMODisplayCategory.MARINERS_STANDARD)
               || category.equals(IMODisplayCategory.MARINERS_OTHER)){
                if(category.getPriority() > s52context.getDisplayMarinerCategory().getPriority()){
                    continue;
                }
            }

            element.priority = element.record.getPriority();
            elements.add(element);
        }

        //sort elements by priority
        Collections.sort(elements);


        //render elements
        for(S52Graphic element : elements){
            if(monitor.stopRequested()) return;
            final Instruction[] instructions = element.record.getInstruction();
            for(Instruction inst : instructions){
                    inst.render(renderingContext, context, colorTable, elements, element);
            }
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
