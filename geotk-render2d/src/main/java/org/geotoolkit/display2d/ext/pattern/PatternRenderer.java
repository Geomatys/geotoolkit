/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.display2d.ext.pattern;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.map.ExceptionPresentation;
import org.apache.sis.map.Presentation;
import org.apache.sis.map.MapLayer;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.geotoolkit.style.MutableStyle;
import org.opengis.referencing.operation.TransformException;

/**
 * Renderer for Pattern symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class PatternRenderer extends AbstractCoverageSymbolizerRenderer<CachedPatternSymbolizer>{

    public PatternRenderer(final SymbolizerRendererService service,final CachedPatternSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Resource resource) {

        if (!(resource instanceof GridCoverageResource)) {
            return Stream.empty();
        }

        final GridCoverageResource gcr = (GridCoverageResource) resource;
        GridCoverage dataCoverage;
        try {
            dataCoverage = gcr.read(renderingContext.getGridGeometry());
        } catch (NoSuchDataException ex) {
            return Stream.empty();
        } catch (DataStoreException ex) {
            ExceptionPresentation ep = new ExceptionPresentation(ex);
            ep.setLayer(layer);
            ep.setResource(resource);
            return Stream.of(ep);
        }

        if (!CRS.equivalent(CRS.getHorizontalComponent(dataCoverage.getCoordinateReferenceSystem()), renderingContext.getObjectiveCRS())) {
            //coverage is not in objective crs, resample it
            try {
                //we resample the native view of the coverage only, the style will be applied later.

                dataCoverage = new ResampleProcess(dataCoverage, renderingContext.getObjectiveCRS(), null, InterpolationCase.NEIGHBOR, null).executeNow();
            } catch(Exception ex) {
                LOGGER.log(Level.WARNING, "ERROR resample in raster symbolizer renderer",ex);
            }
        }

        final Map.Entry<FeatureSet, MutableStyle> entry;
        try {
            entry = symbol.getMasks(dataCoverage);
        } catch (IOException | TransformException ex) {
            ExceptionPresentation ep = new ExceptionPresentation(ex);
            ep.setLayer(layer);
            ep.setResource(resource);
            return Stream.of(ep);
        }

        final MapLayer subLayer = MapBuilder.createLayer(entry.getKey());
        subLayer.setStyle(entry.getValue());

        return DefaultPortrayalService.present(subLayer, entry.getKey(), renderingContext);
    }
}
