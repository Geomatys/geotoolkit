/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display2d.ext.rastermask;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageReadParam;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.statefull.StatefullContextParams;
import org.geotoolkit.display2d.container.statefull.StatefullProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RasterMaskRenderer implements SymbolizerRenderer<RasterMaskSymbolizer,CachedRasterMaskSymbolizer>{

    private static final Logger LOGGER = Logging.getLogger(RasterMaskRenderer.class);

    @Override
    public Class<RasterMaskSymbolizer> getSymbolizerClass() {
        return RasterMaskSymbolizer.class;
    }

    @Override
    public Class<CachedRasterMaskSymbolizer> getCachedSymbolizerClass() {
        return CachedRasterMaskSymbolizer.class;
    }

    @Override
    public CachedRasterMaskSymbolizer createCachedSymbolizer(RasterMaskSymbolizer symbol) {
        return new CachedRasterMaskSymbolizer(symbol);
    }

    @Override
    public void portray(ProjectedFeature graphic, CachedRasterMaskSymbolizer symbol, RenderingContext2D context)
            throws PortrayalException {
        //nothing to portray;
    }

    @Override
    public void portray(ProjectedCoverage projectedCoverage, CachedRasterMaskSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException {

        double[] resolution = context.getResolution();
        resolution[0] *= 3;
        resolution[1] *= 3;

        final CoordinateReferenceSystem gridCRS = projectedCoverage.getCoverageLayer().getBounds().getCoordinateReferenceSystem();

        Envelope bounds = new GeneralEnvelope(context.getCanvasObjectiveBounds());
        //bounds.setCoordinateReferenceSystem(context.getObjectiveCRS());

        if(!gridCRS.equals(bounds.getCoordinateReferenceSystem())){
            try {
                bounds = CRS.transform(bounds, gridCRS);
            } catch (TransformException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

            DirectPosition2D pos = new DirectPosition2D(context.getObjectiveCRS());
            pos.x = resolution[0];
            pos.y = resolution[1];

            try{
                MathTransform trs = context.getMathTransform(context.getObjectiveCRS(), gridCRS);
                DirectPosition pos2 = trs.transform(pos, null);
                resolution[0] = pos2.getOrdinate(0);
                resolution[1] = pos2.getOrdinate(1);
            }catch(Exception ex){
                throw new PortrayalException(ex);
            }

        }

        final CoverageReadParam param = new CoverageReadParam(bounds, resolution);

        GridCoverage2D dataCoverage;
        try {
            dataCoverage = projectedCoverage.getCoverage(param);
        } catch (FactoryException ex) {
            throw new PortrayalException(ex);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        if(!CRS.equalsIgnoreMetadata(dataCoverage.getCoordinateReferenceSystem2D(), context.getObjectiveCRS())){
            //coverage is not in objective crs, resample it
            try{
                //we resample the native view of the coverage only, the style will be applied later.
                dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(
                        dataCoverage.view(ViewType.NATIVE),
                        context.getObjectiveCRS());
            }catch(Exception ex){
                System.out.println("ERROR resample in raster symbolizer renderer: " + ex.getMessage());
            }
        }


        final Map<SimpleFeature, List<CachedSymbolizer>> features;
        try {
            features = symbol.getMasks(dataCoverage);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }


        //paint all dynamicly generated features -------------------------------
        final ReferencedCanvas2D canvas = context.getCanvas();
        final CoordinateReferenceSystem dataCRS = dataCoverage.getCoordinateReferenceSystem();
        final StatefullContextParams params = new StatefullContextParams(canvas,null);
        final CoordinateReferenceSystem objectiveCRS = context.getObjectiveCRS();

        try {
            params.dataToObjective = context.getMathTransform(dataCRS, objectiveCRS);
            params.dataToObjectiveTransformer.setMathTransform(params.dataToObjective);
        } catch (FactoryException ex) {
            ex.printStackTrace();
        }

        final AffineTransform objtoDisp = context.getObjectiveToDisplay();
        params.objectiveToDisplay.setTransform(objtoDisp);
        params.updateGeneralizationFactor(context, dataCRS);
        try {
            params.dataToDisplayTransformer.setMathTransform(context.getMathTransform(dataCRS, context.getDisplayCRS()));
        } catch (FactoryException ex) {
            ex.printStackTrace();
        }

        for(final Map.Entry<SimpleFeature,List<CachedSymbolizer>> entry : features.entrySet()){
            final StatefullProjectedFeature projectedFeature = new StatefullProjectedFeature(params);
            projectedFeature.setFeature(entry.getKey());

            for(final CachedSymbolizer cached : entry.getValue()){
                GO2Utilities.portray(projectedFeature, cached, context);
            }
            
        }

        context.switchToDisplayCRS();
    }

    @Override
    public boolean hit(ProjectedFeature graphic, CachedRasterMaskSymbolizer symbol, RenderingContext2D context,
            SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    @Override
    public boolean hit(final ProjectedCoverage projectedCoverage, final CachedRasterMaskSymbolizer symbol,
            final RenderingContext2D context, final SearchAreaJ2D search, final VisitFilter filter) {


        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();
        final Shape shape;
        try {
            shape = projectedCoverage.getEnvelopeGeometry().getDisplayShape();
        } catch (TransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }

        final Area area = new Area(mask);

        switch(filter){
            case INTERSECTS :
                area.intersect(new Area(shape));
                return !area.isEmpty();
            case WITHIN :
                Area start = new Area(area);
                area.add(new Area(shape));
                return start.equals(area);
        }

        return false;
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedRasterMaskSymbolizer symbol) {
        return null;
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedRasterMaskSymbolizer symbol) {
        //no glyph
    }

}
