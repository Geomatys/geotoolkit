/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import static org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer.checkResolution;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.extractQuery;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.fixEnvelopeWithQuery;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.fixResolutionWithCRS;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.opengis.coverage.Coverage;
import org.geotoolkit.feature.GeometryAttribute;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.coverage.resample.ResampleDescriptor;
import org.geotoolkit.process.coverage.resample.ResampleProcess;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Symbolizer;
import org.opengis.util.FactoryException;

/**
 * Abstract renderer for symbolizer which only apply on coverages data.
 * This class will take care to implement the coverage hit method.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractCoverageSymbolizerRenderer<C extends CachedSymbolizer<? extends Symbolizer>> extends AbstractSymbolizerRenderer<C>{


    public AbstractCoverageSymbolizerRenderer(final SymbolizerRendererService service, final C symbol, final RenderingContext2D context){
        super(service, symbol,context);
    }

    @Override
    public void portray(final ProjectedObject graphic) throws PortrayalException {
        if(graphic instanceof ProjectedFeature){
            final ProjectedFeature pf = (ProjectedFeature) graphic;
            final String geomName = symbol.getSource().getGeometryPropertyName();
            final Object obj;
            if(geomName == null || geomName.isEmpty()){
                final GeometryAttribute att = pf.getCandidate().getDefaultGeometryProperty();
                obj = (att!=null) ? att.getValue() : null;
            }else{
                obj = GO2Utilities.evaluate(GO2Utilities.FILTER_FACTORY.property(geomName), pf.getCandidate(), null, null);
            }
            if(obj instanceof GridCoverage2D){
                final CoverageMapLayer ml = MapBuilder.createCoverageLayer((GridCoverage2D)obj, GO2Utilities.STYLE_FACTORY.style(), "");
                final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(),ml);
                params.update(renderingContext);
                final ProjectedCoverage pc = new ProjectedCoverage(params, ml);
                portray(pc);
            }
        }
    }

    @Override
    public boolean hit(final ProjectedObject graphic, final SearchAreaJ2D mask, final VisitFilter filter) {
        if(graphic instanceof ProjectedFeature){
            final ProjectedFeature pf = (ProjectedFeature) graphic;
            final Object obj = GO2Utilities.evaluate(GO2Utilities.FILTER_FACTORY.property(
                    symbol.getSource().getGeometryPropertyName()), pf.getCandidate(), null, null);
            if(obj instanceof GridCoverage2D){
                final CoverageMapLayer ml = MapBuilder.createCoverageLayer((GridCoverage2D)obj, GO2Utilities.STYLE_FACTORY.style(), "");
                final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(),ml);
                params.update(renderingContext);
                final ProjectedCoverage pc = new ProjectedCoverage(params, ml);
                return hit(pc,mask,filter);
            }
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedCoverage projectedCoverage, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();
        final Shape[] shapes;
        try {
            shapes = projectedCoverage.getEnvelopeGeometry().getDisplayShape();
        } catch (TransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
            return false;
        }

        for(Shape shape : shapes){
            final Area area = new Area(mask);
            switch(filter){
                case INTERSECTS :
                    area.intersect(new Area(shape));
                    if(!area.isEmpty()) return true;
                    break;
                case WITHIN :
                    Area start = new Area(area);
                    area.add(new Area(shape));
                    if(start.equals(area)) return true;
                    break;
            }
        }
        return false;
    }
    
    /**
     * Effectuate some operations on source {@link GridCoverage2D} in relation with its internally symbolizer type.
     * 
     * @param coverageSource source coverage which will be adapted to resampling.
     * @param symbolizer 
     * @return coverage prepared to resampling.
     * @see DefaultRasterSymbolizerRenderer#prepareCoverageToResampling(org.geotoolkit.coverage.grid.GridCoverage2D, org.geotoolkit.display2d.style.CachedSymbolizer) 
     */
    protected abstract GridCoverage2D prepareCoverageToResampling(final GridCoverage2D coverageSource, C symbolizer);

     /**
     * Returns expected {@link GridCoverage2D} from given {@link ProjectedCoverage}, 
     * adapted to asked {@linkplain #renderingContext internally rendering context} situation.
     * 
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @return an expected slice 2D of given {@link ProjectedCoverage}.
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ResampleDescriptor
     * @see ResampleProcess
     * @see ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam) 
     */
    protected final GridCoverage2D getObjectiveCoverage(final ProjectedCoverage projectedCoverage/*, final CanvasType displayOrObjective*/) 
            throws CoverageStoreException, TransformException, FactoryException, ProcessException {
        return getObjectiveCoverage(projectedCoverage, false);
    }
    
    /**
     * Returns expected {@linkplain GridCoverage2D elevation coverage} from given {@link ProjectedCoverage}, 
     * adapted to asked {@linkplain #renderingContext internally rendering context} situation.
     * 
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @return an expected slice 2D of given {@link ProjectedCoverage}.
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ResampleDescriptor
     * @see ResampleProcess
     * @see ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam) 
     */
    protected final GridCoverage2D getObjectiveElevationCoverage(final ProjectedCoverage projectedCoverage/*, final CanvasType displayOrObjective*/) 
            throws CoverageStoreException, TransformException, FactoryException, ProcessException {
        return getObjectiveCoverage(projectedCoverage, true);
    }
    
    /**
     * Returns expected {@linkplain GridCoverage2D elevation coverage} or {@linkplain GridCoverage2D coverage}
     * from given {@link ProjectedCoverage}, adapted to asked {@linkplain #renderingContext internally rendering context} situation.
     * 
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @param isElevation {@code true} if we want elevation coverage, else ({@code false}) for read coverage.
     * @return expected {@linkplain GridCoverage2D elevation coverage} or {@linkplain GridCoverage2D coverage}
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam) 
     * @see ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam) 
     */
    private GridCoverage2D getObjectiveCoverage(final ProjectedCoverage projectedCoverage, final boolean isElevation/*, final CanvasType displayOrObjective*/) 
            throws CoverageStoreException, TransformException, FactoryException, ProcessException {
        ArgumentChecks.ensureNonNull("projectedCoverage", projectedCoverage);
        //-- ArgumentChecks.ensureNonNull("CanvasType", displayOrObjective);
        
        ////////////////////////////////////////////////////////////////////
        // 1 - Get data                       
        ////////////////////////////////////////////////////////////////////

        //-- resolution of horizontal Part of CRS
        double[] resolution = renderingContext.getResolution();
        assert resolution.length == 2 : "DefaultRasterSymboliser : resolution from renderingContext should only defined in 2D.";

        Envelope renderingBound = renderingContext.getCanvasObjectiveBounds();
        
        resolution = checkResolution(resolution, renderingBound);

        final CoverageMapLayer coverageLayer = projectedCoverage.getLayer();
        final CoverageReference ref          = coverageLayer.getCoverageReference();
        final Envelope layerBounds           = coverageLayer.getBounds();
        final CoordinateReferenceSystem coverageMapLayerCRS = layerBounds.getCoordinateReferenceSystem();

        final Map<String, Double> queryValues = extractQuery(projectedCoverage.getLayer());
        if (queryValues != null && !queryValues.isEmpty()) {
            renderingBound = fixEnvelopeWithQuery(queryValues, renderingBound, coverageMapLayerCRS);
            resolution     = fixResolutionWithCRS(resolution, coverageMapLayerCRS);
        }

        final GridCoverageReader reader = ref.acquireReader();
        final Envelope dataBBox         = reader.getGridGeometry(ref.getImageIndex()).getEnvelope();
        ref.recycle(reader);
        
        /*
        * Study rendering context envelope and internal coverage envelope.
        * We try to define if the two geographic part from the two respectively 
        * coverage and rendering envelope intersect.
        */
        final CoordinateReferenceSystem renderingContextObjectiveCRS2D = renderingContext.getObjectiveCRS2D();
        final GeneralEnvelope renderingBound2D                         = GeneralEnvelope.castOrCopy(Envelopes.transform(renderingBound, renderingContextObjectiveCRS2D));
        final GeneralEnvelope coverageIntoRender2DCRS                  = GeneralEnvelope.castOrCopy(Envelopes.transform(dataBBox, renderingContextObjectiveCRS2D));
        
        
        if (!org.geotoolkit.geometry.Envelopes.containNAN(renderingBound2D)
         && !org.geotoolkit.geometry.Envelopes.containNAN(coverageIntoRender2DCRS)
         && !coverageIntoRender2DCRS.intersects(renderingBound2D, true)) {
            //-- in future jdk8 version return an Optional<Coverage> 
            final StringBuilder strB = new StringBuilder(isElevation ? "getObjectiveElevationCoverage()" : "getObjectiveCoverage()");
            strB.append(" : the 2D geographic part of rendering context does not intersect the 2D geographic part of coverage : ");
            strB.append("\n rendering context 2D CRS :  ");
            strB.append(renderingContextObjectiveCRS2D);
            strB.append("\n rendering context boundary : ");
            strB.append(renderingBound2D);
            strB.append("\n 2D coverage geographic part into rendering context CRS : ");
            strB.append(coverageIntoRender2DCRS);
            LOGGER.log(Level.FINE, strB.toString());
            return null;
        }
        //-- else  
        //-- Note : in the case of NAN values we try later to clip requested envelope with coverage boundary.
        
        /*
         * Study rendering context envelope and internal coverage envelope.
         * For example if we store data with a third dimension or more, with the 2 dimensional renderer
         * it is possible to miss some internal stored data.
         * To avoid this comportment we can "complete"(fill) render envelope with missing dimensions.
         */
        final GeneralEnvelope paramEnvelope = org.geotoolkit.referencing.ReferencingUtilities.intersectEnvelopes(dataBBox, renderingBound);
        assert paramEnvelope.getCoordinateReferenceSystem() != null : "DefaultRasterSymbolizerRenderer : CRS from param envelope cannot be null.";

        //-- Check if projected coverage has NAN values on other dimension than geographic 2D part 
        if (org.geotoolkit.geometry.Envelopes.containNAN(paramEnvelope)
        && !org.geotoolkit.geometry.Envelopes.containNANInto2DGeographicPart(paramEnvelope)) 
            throw new CoverageStoreException("ParamEnvelope build : unexpected comportment."
                    + "\n has some NAN values on other dimension than geographic part."+paramEnvelope);

        //-- We know we don't have NAN values on other dimension than geographic 
        //-- We clip envelope with coverage boundary
        clipAndReplaceNANEnvelope(paramEnvelope, dataBBox, paramEnvelope);
        
        assert !org.geotoolkit.geometry.Envelopes.containNAN(paramEnvelope) : "paramEnvelope can't contain NAN values";
        
        //-- convert resolution adapted to coverage CRS (resolution from rendering context --> coverage resolution)
        final double[] paramRes = ReferencingUtilities.convertResolution(renderingBound, resolution, paramEnvelope.getCoordinateReferenceSystem());

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(paramEnvelope);
        param.setResolution(paramRes);

        GridCoverage2D dataCoverage = (isElevation) ? projectedCoverage.getElevationCoverage(param) : projectedCoverage.getCoverage(param);

        if (dataCoverage == null) {
            //-- in future jdk8 version return an Optional<Coverage> 
            final StringBuilder strB = new StringBuilder(isElevation ? "getObjectiveElevationCoverage()" : "getObjectiveCoverage()");
            strB.append(" : \n impossible to read coverage ");
            strB.append("with internally projected coverage boundary : ");
            strB.append(dataBBox);
            strB.append("\nwith the following renderer requested Envelope.");
            strB.append(paramEnvelope);
            LOGGER.log(Level.FINE, strB.toString());
            return null;
        }

        ////////////////////////////////////////////////////////////////////
        // 3 - Reproject data                                             //
        ////////////////////////////////////////////////////////////////////

        final CoordinateReferenceSystem coverageCRS   = dataCoverage.getCoordinateReferenceSystem();
        assert CRS.equalsIgnoreMetadata(dataBBox.getCoordinateReferenceSystem(), coverageCRS);
        
        final CoordinateReferenceSystem coverageCRS2D = CRSUtilities.getCRS2D(coverageCRS);
        
        /* It appears EqualsIgnoreMetadata can return false sometimes, even if the two CRS are equivalent.
         * But mathematics don't lie, so if they really describe the same transformation, conversion from
         * one to another will give us an identity matrix.
         */
        final MathTransform coverageToObjective = CRS.findMathTransform(coverageCRS2D, renderingContextObjectiveCRS2D);

        if (coverageToObjective.isIdentity())
            return dataCoverage;

        //-- before try to read coverage in relation with rendering view boundary
        assert !renderingBound2D.isEmpty() : "2D rendering boundary should not be empty.";
        final GeneralEnvelope coverageEnv2D = new GeneralEnvelope(dataCoverage.getEnvelope2D());
        assert !coverageEnv2D.isEmpty() : "2D coverage boundary should not be empty.";
        
        /*
        * In case where coverage2D envelope into rendering CRS is not empty,
        * try to reproject a coverage which have already been clipped with the objective rendering context boundary.
        */
        GeneralEnvelope outputRenderingCoverageEnv2D = GeneralEnvelope.castOrCopy(Envelopes.transform(coverageToObjective, coverageEnv2D));
        outputRenderingCoverageEnv2D.setCoordinateReferenceSystem(renderingContextObjectiveCRS2D);
        if (!outputRenderingCoverageEnv2D.isEmpty()) {
            outputRenderingCoverageEnv2D.intersect(renderingBound2D);
        } else {
            outputRenderingCoverageEnv2D = renderingBound2D;
        }
        
        //----------------------------- DISPLAY -------------------------------//
        //-- compute output grid Envelope into rendering context display
        //-- get destination image size
        final AffineTransform2D trs   = renderingContext.getObjectiveToDisplay();
        final GeneralEnvelope dispEnv = CRS.transform(trs, outputRenderingCoverageEnv2D);
        final int width               = (int) Math.ceil(dispEnv.getSpan(0));
        final int height              = (int) Math.ceil(dispEnv.getSpan(1));

        if (width <= 0 || height <= 0) {
            LOGGER.log(Level.FINE, "Coverage is out of rendering window.");
            return null;
        }
        //-----------------------------------------------------------------------
        
        //-- Use into DefaultRasterSymbolizerRenderer
        //-- force alpha if image do not get any "invalid data" rule (Ex : No-data in image or color map).
        dataCoverage = prepareCoverageToResampling(dataCoverage, symbol);

        /*
         * NODATA 
         * 
         * 1 : Normally all NODATA for all gridSampleDimension for a same coverage are equals.
         * 2 : Normally all NODATA for each coverage internally samples are equals.
         */
        final double[] nodata = dataCoverage.getSampleDimension(0).getNoDataValues();

        /*
         * If nodata is not know.
         * 1 : find a nodata value out of internal gridSampleDimension categories.
         * 2 : if category already contain all sample Datatype possible values, 
         * transform image into a sample type with more bitspersample to define 
         * an appropriate NODATA values out of categories borders.
         */
        if (nodata == null) {
            //-- TODO
        }

        final GridGeometry2D gg = new GridGeometry2D(new GridEnvelope2D(0, 0, width, height), outputRenderingCoverageEnv2D);

        final ProcessDescriptor desc = ResampleDescriptor.INSTANCE;
        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
        ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_COVERAGE.getName().getCode()).setValue(dataCoverage);
        ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_BACKGROUND.getName().getCode()).setValue(nodata);
        ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_COORDINATE_REFERENCE_SYSTEM.getName().getCode()).setValue(renderingContextObjectiveCRS2D);
        ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_GRID_GEOMETRY.getName().getCode()).setValue(gg);

        final org.geotoolkit.process.Process process = desc.createProcess(params);
        final ParameterValueGroup result = process.call();
        dataCoverage = (GridCoverage2D) result.parameter("result").getValue();
        
        return dataCoverage;
    }
    
    /**
     * Clip requested envelope with internally {@link ProjectedCoverage} boundary.
     * 
     * <strong>
     * In some case when the rendering boundary is reprojected into coverage space 
     * some {@linkplain Double#NaN NAN} values can be computed, which is an expected comportment.
     * To avoid normally exception during coverage reading this method replace NAN values by coverage boundary values.
     * </strong>
     * 
     * @param requestedEnvelope envelope which will be clipped.
     * @param coverageEnvelope reference coverage envelope.
     * @param result set result of clipping into this {@link GeneralEnvelope}, 
     * a new result envelope is built if it is {@code null}, you should pass the same Envelope as requestedEnvelope.
     * Moreover the result envelope is defined into same CRS than requestedEnvelope.
     * @return requested clipped envelope result.
     * @throws NullArgumentException if requestedEnvelope or coverageEnvelope are {@code null}.
     * @throws IllegalArgumentException if CRS from requestedEnvelope and coverageEnvelope are different.
     */
    private GeneralEnvelope clipAndReplaceNANEnvelope(final Envelope requestedEnvelope, final Envelope coverageEnvelope, GeneralEnvelope result) {
        ArgumentChecks.ensureNonNull("requestedEnvelope", requestedEnvelope);
        ArgumentChecks.ensureNonNull("coverageEnvelope",  coverageEnvelope);
        
        final CoordinateReferenceSystem requestCRS = requestedEnvelope.getCoordinateReferenceSystem();
        if (!CRS.equalsIgnoreMetadata(requestCRS, coverageEnvelope.getCoordinateReferenceSystem()))
            throw new IllegalArgumentException("requestedEnvelope and coverage envelope will be able to have same CRS : "
                    + "\n Expected CRS : "+requestCRS
                    + "\n Found : "+coverageEnvelope.getCoordinateReferenceSystem());
        
        if (result == null) result = new GeneralEnvelope(requestCRS);
        
        for (int d = 0, dim = requestedEnvelope.getDimension(); d < dim; d++) {
            
            final double reqMin = requestedEnvelope.getMinimum(d);
            final double reqMax = requestedEnvelope.getMaximum(d);
            
            final double min = (Double.isNaN(reqMin) || Double.isInfinite(reqMin) 
                    ? coverageEnvelope.getMinimum(d) 
                    : StrictMath.max(reqMin, coverageEnvelope.getMinimum(d)));
            
            final double max = (Double.isNaN(reqMax) || Double.isInfinite(reqMax) 
                    ? coverageEnvelope.getMaximum(d) 
                    : StrictMath.min(reqMax, coverageEnvelope.getMaximum(d)));
            
            result.setRange(d, min, max);
        }
        return result;
    }
}
