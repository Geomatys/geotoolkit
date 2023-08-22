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

package org.geotoolkit.display2d.ext.isoline.symbolizer;

import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.map.ExceptionPresentation;
import org.apache.sis.map.Presentation;
import org.apache.sis.storage.CoverageQuery;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.display2d.style.renderer.*;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.process.*;
import org.geotoolkit.processing.coverage.isoline.IsolineDescriptor;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.function.Jenks;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.style.ColorMap;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.TextSymbolizer;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class IsolineSymbolizerRenderer  extends AbstractCoverageSymbolizerRenderer<CachedIsolineSymbolizer> {

    public IsolineSymbolizerRenderer(final SymbolizerRendererService service, CachedIsolineSymbolizer cache, RenderingContext2D context) {
        super(service, cache, context);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Resource resource) {

        if (!(resource instanceof GridCoverageResource)) {
            return Stream.empty();
        }

        final GridCoverageResource coverageReference = (GridCoverageResource) resource;

        final IsolineSymbolizer isolineSymbolizer = symbol.getSource();

        try {
            Stream<Presentation> stream = Stream.empty();
            ////////////////////
            // 1 - Render raster
            ////////////////////
            final CachedRasterSymbolizer cachedRasterSymbolizer = symbol.getCachedRasterSymbolizer();
            if (!isolineSymbolizer.getIsolineOnly() || isJenksFunction(cachedRasterSymbolizer)) {

                final MapLayer sublayer = MapBuilder.createLayer(coverageReference);
                sublayer.setStyle(GO2Utilities.STYLE_FACTORY.style(cachedRasterSymbolizer.getSource()));

                stream = Stream.concat(stream, DefaultPortrayalService.present(sublayer, coverageReference, renderingContext));
            }

            final LineSymbolizer lineSymbolizer = isolineSymbolizer.getLineSymbolizer();
            final TextSymbolizer textSymbolizer = isolineSymbolizer.getTextSymbolizer();

            /////////////////////
            // 2 - Isolines
            ////////////////////
            if (lineSymbolizer != null) {

                double[] intervales = symbol.getSteps();

                final CoverageQuery query = new CoverageQuery();
                query.setSelection(renderingContext.getGridGeometry());
                query.setSourceDomainExpansion(2);
                final GridCoverageResource res = coverageReference.subset(query);

                final GridCoverage inCoverage = res.read(null).forConvertedValues(true);
                final GridCoverageResource resampledCovRef = new InMemoryGridCoverageResource(coverageReference.getIdentifier().orElse(null), inCoverage);

                /////////////////////
                // 2.2 - Compute isolines
                ////////////////////
                FeatureSet isolines = null;
                final ProcessDescriptor isolineDesc = symbol.getIsolineDesc();
                if (isolineDesc != null) {
                    final Parameters inputs = Parameters.castOrWrap(isolineDesc.getInputDescriptor().createValue());
                    inputs.getOrCreate(IsolineDescriptor.COVERAGE_REF).setValue(resampledCovRef);
                    inputs.getOrCreate(IsolineDescriptor.INTERVALS).setValue(intervales);
                    final org.geotoolkit.process.Process process = isolineDesc.createProcess(inputs);
                    final ParameterValueGroup result = process.call();
                    isolines = (FeatureSet) result.parameter(IsolineDescriptor.FCOLL.getName().getCode()).getValue();
                }

                /////////////////////
                // 2.3 - Render isolines
                ////////////////////
                if (isolines != null) {
                    MutableStyle featureStyle = null;
                    if (textSymbolizer != null) {
                        featureStyle = GO2Utilities.STYLE_FACTORY.style(lineSymbolizer, textSymbolizer);
                    } else {
                        featureStyle = GO2Utilities.STYLE_FACTORY.style(lineSymbolizer);
                    }

                    MapLayer fml = MapBuilder.createLayer(isolines);
                    fml.setStyle(featureStyle);

                    stream = Stream.concat(stream, DefaultPortrayalService.present(fml, isolines, renderingContext));
                }
            }

            return stream;

        } catch (DataStoreException | ProcessException ex) {
            ExceptionPresentation ep = new ExceptionPresentation(ex);
            ep.setLayer(layer);
            ep.setResource(resource);
            return Stream.of(ep);
        }
    }

    private boolean isJenksFunction(CachedRasterSymbolizer cachedRasterSymbolizer) {
        if (cachedRasterSymbolizer != null) {
            RasterSymbolizer source = cachedRasterSymbolizer.getSource();
            if (source != null && source.getColorMap() != null) {
                ColorMap colorMap = source.getColorMap();
                return (source.getColorMap().getFunction() instanceof Jenks);
            }
        }
        return false;
    }

}
