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

import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.memory.MemoryCoverageStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.display2d.style.renderer.*;
import org.opengis.util.GenericName;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.process.*;
import org.geotoolkit.processing.coverage.isoline2.IsolineDescriptor2;
import org.geotoolkit.processing.coverage.resample.ResampleDescriptor;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.function.Jenks;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.style.ColorMap;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.TextSymbolizer;

import java.util.Map;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import static org.geotoolkit.processing.coverage.resample.ResampleDescriptor.*;
import org.geotoolkit.utility.parameter.ParametersExt;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class IsolineSymbolizerRenderer  extends AbstractCoverageSymbolizerRenderer<CachedIsolineSymbolizer> {

    public IsolineSymbolizerRenderer(final SymbolizerRendererService service, CachedIsolineSymbolizer cache, RenderingContext2D context) {
        super(service, cache, context);
    }

    @Override
    public void portray(ProjectedCoverage graphic) throws PortrayalException {

        IsolineSymbolizer isolineSymbolizer = symbol.getSource();

        try {
            ////////////////////
            // 1 - Render raster
            ////////////////////
            final CachedRasterSymbolizer cachedRasterSymbolizer = symbol.getCachedRasterSymbolizer();
            if (!isolineSymbolizer.getIsolineOnly() || isJenksFunction(cachedRasterSymbolizer)) {
                GO2Utilities.portray(graphic, cachedRasterSymbolizer, renderingContext);
            }

//            final MutableStyle rasterStyle = GO2Utilities.STYLE_FACTORY.style(cachedRasterSymbolizer.getSource());
//            final CoverageMapLayer covMapLayer = MapBuilder.createCoverageLayer(resampledCoverage, rasterStyle, name.getLocalPart());
//            final StatelessCoverageLayerJ2D statelessCoverageLayer = new StatelessCoverageLayerJ2D(renderingContext.getCanvas(), covMapLayer);
//            statelessCoverageLayer.paintLayer(renderingContext);

            final LineSymbolizer lineSymbolizer = isolineSymbolizer.getLineSymbolizer();
            final TextSymbolizer textSymbolizer = isolineSymbolizer.getTextSymbolizer();

            /////////////////////
            // 2 - Isolines
            ////////////////////
            if (lineSymbolizer != null) {

                double[] intervales = symbol.getSteps();
                ////////////////////
                // 2.1 - Resample input coverage
                ////////////////////
                final CoverageMapLayer coverageLayer = graphic.getLayer();
                final CoordinateReferenceSystem coverageMapLayerCRS = coverageLayer.getBounds().getCoordinateReferenceSystem();
                final CoverageReference coverageReference = coverageLayer.getCoverageReference();

                double[] resolution = renderingContext.getResolution();
                Envelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
                resolution = checkResolution(resolution, bounds);
                if(resolution.length!=bounds.getDimension()){
                    double[] res = new double[bounds.getDimension()];
                    res[0] = resolution[0];
                    res[1] = resolution[1];
                    for(int i=2;i<res.length;i++){
                        res[i] = bounds.getSpan(i);
                    }
                    resolution = res;
                }

                final Map<String, Double> queryValues = DefaultRasterSymbolizerRenderer.extractQuery(coverageLayer);
                if (queryValues != null && !queryValues.isEmpty()) {
                    bounds = DefaultRasterSymbolizerRenderer.fixEnvelopeWithQuery(queryValues, bounds, coverageMapLayerCRS);
                    resolution = DefaultRasterSymbolizerRenderer.fixResolutionWithCRS(resolution, coverageMapLayerCRS);
                }

                final GridCoverageReadParam param = new GridCoverageReadParam();
                param.setEnvelope(bounds);
                param.setResolution(resolution);

                final GridCoverageReader reader = coverageReference.acquireReader();
                GridCoverage2D inCoverage = (GridCoverage2D) reader.read(coverageReference.getImageIndex(), param);
                inCoverage = inCoverage.view(ViewType.GEOPHYSICS);
                coverageReference.recycle(reader);

                final GridEnvelope gridEnv = new GeneralGridEnvelope(renderingContext.getPaintingDisplayBounds(), 2);
                final CoordinateReferenceSystem crs = renderingContext.getObjectiveCRS2D();
                final MathTransform gridToCRS = renderingContext.getDisplayToObjective();
                final GridGeometry inGridGeom = new GeneralGridGeometry(gridEnv, gridToCRS, crs);

                final ParameterValueGroup resampleParams = ResampleDescriptor.INPUT_DESC.createValue();
                ParametersExt.getOrCreateValue(resampleParams, IN_COVERAGE.getName().getCode()).setValue(inCoverage);
                ParametersExt.getOrCreateValue(resampleParams, IN_COORDINATE_REFERENCE_SYSTEM.getName().getCode()).setValue(crs);
                ParametersExt.getOrCreateValue(resampleParams, IN_GRID_GEOMETRY.getName().getCode()).setValue(inGridGeom);
                ParametersExt.getOrCreateValue(resampleParams, IN_INTERPOLATION_TYPE.getName().getCode()).setValue(InterpolationCase.BILINEAR);
                ParametersExt.getOrCreateValue(resampleParams, IN_BORDER_COMPORTEMENT_TYPE.getName().getCode()).setValue(ResampleBorderComportement.FILL_VALUE);
                final ResampleProcess resampleProcess = new ResampleProcess(resampleParams);
                final ParameterValueGroup output = resampleProcess.call();

                final GridCoverage2D resampledCoverage = (GridCoverage2D) output.parameter(ResampleDescriptor.OUT_COVERAGE.getName().getCode()).getValue();
                final MemoryCoverageStore memoryCoverageStore = new MemoryCoverageStore(resampledCoverage, coverageReference.getName().tip().toString());

                final GenericName name = memoryCoverageStore.getNames().iterator().next();
                final CoverageReference resampledCovRef = memoryCoverageStore.getCoverageReference(name);

                /////////////////////
                // 2.2 - Compute isolines
                ////////////////////
                FeatureCollection isolines = null;
                ProcessDescriptor isolineDesc = symbol.getIsolineDesc();
                if (isolineDesc != null) {
                    ParameterValueGroup inputs = isolineDesc.getInputDescriptor().createValue();
                    inputs.parameter(IsolineDescriptor2.COVERAGE_REF.getName().getCode()).setValue(resampledCovRef);
                    inputs.parameter(IsolineDescriptor2.READ_PARAM.getName().getCode()).setValue(param);
                    inputs.parameter(IsolineDescriptor2.INTERVALS.getName().getCode()).setValue(intervales);
                    org.geotoolkit.process.Process process = isolineDesc.createProcess(inputs);
                    ParameterValueGroup result = process.call();
                    isolines = (FeatureCollection) result.parameter(IsolineDescriptor2.FCOLL.getName().getCode()).getValue();
                }

                /////////////////////
                // 2.3 - Render isolines
                ////////////////////
                if (isolines != null && !isolines.isEmpty()) {
                    MutableStyle featureStyle = null;
                    if (textSymbolizer != null) {
                        featureStyle = GO2Utilities.STYLE_FACTORY.style(lineSymbolizer, textSymbolizer);
                    } else {
                        featureStyle = GO2Utilities.STYLE_FACTORY.style(lineSymbolizer);
                    }

                    FeatureMapLayer fml = MapBuilder.createFeatureLayer(isolines, featureStyle);

                    StatelessFeatureLayerJ2D statelessFeatureLayerJ2D = new StatelessFeatureLayerJ2D(renderingContext.getCanvas(), fml);
                    statelessFeatureLayerJ2D.paintLayer(renderingContext);
                }
            }

        } catch (DataStoreException ex) {
            throw new PortrayalException(ex.getMessage(), ex);
        } catch (ProcessException e) {
            throw new PortrayalException(e.getMessage(), e);
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
