/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2016, Geomatys
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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import static org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer.LOGGER;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.resample.ResampleDescriptor;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Symbolizer;
import org.opengis.util.FactoryException;


/**
 * Abstract renderer for symbolizer which only apply on coverages data.
 * This class will take care to implement the coverage hit method.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Remi Marechal (Geomatys)
 * @module
 */
public abstract class AbstractCoverageSymbolizerRenderer<C extends CachedSymbolizer<? extends Symbolizer>> extends AbstractSymbolizerRenderer<C>{


    public AbstractCoverageSymbolizerRenderer(final SymbolizerRendererService service, final C symbol, final RenderingContext2D context){
        super(service, symbol,context);
    }

    @Override
    public boolean portray(final ProjectedObject graphic) throws PortrayalException {
        if(graphic instanceof ProjectedFeature){
            final ProjectedFeature pf = (ProjectedFeature) graphic;
            final String geomName = symbol.getSource().getGeometryPropertyName();
            Object obj;
            if(geomName == null || geomName.isEmpty()){
                try{
                    obj = pf.getCandidate().getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());
                }catch(PropertyNotFoundException ex){
                    obj = null;
                }
            }else{
                obj = GO2Utilities.evaluate(GO2Utilities.FILTER_FACTORY.property(geomName), pf.getCandidate(), null, null);
            }
            if(obj instanceof GridCoverage2D){
                final GridCoverage2D cov = (GridCoverage2D) obj;
                CharSequence name = cov.getName();
                if (name==null) name = "unnamed";
                final MapLayer ml = MapBuilder.createCoverageLayer(cov, GO2Utilities.STYLE_FACTORY.style(), name.toString());
                final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(),ml);
                params.update(renderingContext);
                final ProjectedCoverage pc = new ProjectedCoverage(params, ml);
                return portray(pc);
            }else  if(obj instanceof GridCoverageResource){
                final MapLayer ml = MapBuilder.createCoverageLayer((GridCoverageResource)obj);
                final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(),ml);
                params.update(renderingContext);
                final ProjectedCoverage pc = new ProjectedCoverage(params, ml);
                return portray(pc);
            }else  if(obj instanceof GridCoverageReader){
                final MapLayer ml = MapBuilder.createCoverageLayer((GridCoverageReader)obj);
                final StatelessContextParams params = new StatelessContextParams(renderingContext.getCanvas(),ml);
                params.update(renderingContext);
                final ProjectedCoverage pc = new ProjectedCoverage(params, ml);
                return portray(pc);
            }
        }
        return false;
    }

    @Override
    public boolean hit(final ProjectedObject graphic, final SearchAreaJ2D mask, final VisitFilter filter) {
        if(graphic instanceof ProjectedFeature){
            final ProjectedFeature pf = (ProjectedFeature) graphic;
            final Object obj = GO2Utilities.evaluate(GO2Utilities.FILTER_FACTORY.property(
                    symbol.getSource().getGeometryPropertyName()), pf.getCandidate(), null, null);
            if(obj instanceof GridCoverage2D){
                final MapLayer ml = MapBuilder.createCoverageLayer((GridCoverage2D)obj, GO2Utilities.STYLE_FACTORY.style(), "");
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

        final Geometry geom = search.getObjectiveGeometryJTS();
        final JTSEnvelope2D searchEnv = JTS.toEnvelope(geom);

        GridGeometry searchGrid = renderingContext.getGridGeometry().reduce(0,1);
        searchGrid = searchGrid.derive().subgrid(searchEnv).build();
        try {
            GridCoverage2D coverage = getObjectiveCoverage(projectedCoverage, searchGrid, false);
            return coverage != null;
        } catch (DataStoreException | TransformException | FactoryException | ProcessException ex) {
            return false;
        }
    }

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
    protected final GridCoverage2D getObjectiveCoverage(final ProjectedCoverage projectedCoverage)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        return getObjectiveCoverage(projectedCoverage, renderingContext.getGridGeometry(), false);
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
    protected final GridCoverage2D getObjectiveElevationCoverage(final ProjectedCoverage projectedCoverage)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        return getObjectiveCoverage(projectedCoverage, renderingContext.getGridGeometry(), true);
    }

    /**
     * Returns expected {@linkplain GridCoverage2D elevation coverage} or {@linkplain GridCoverage2D coverage}
     * from given {@link ProjectedCoverage}.
     *
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @param canvasGrid Rendering canvas grid geometry
     * @param isElevation {@code true} if we want elevation coverage, else ({@code false}) for features coverage.
     * @return expected {@linkplain GridCoverage2D elevation coverage} or {@linkplain GridCoverage2D coverage}
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     * @see ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     */
    protected GridCoverage2D getObjectiveCoverage(final ProjectedCoverage projectedCoverage,
            GridGeometry canvasGrid, final boolean isElevation)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        return getObjectiveCoverage(projectedCoverage, canvasGrid, isElevation, null);
    }

    /**
     * Returns expected {@linkplain GridCoverage2D elevation coverage} or {@linkplain GridCoverage2D coverage}
     * from given {@link ProjectedCoverage}.
     *
     * @param projectedCoverage Convenient representation of a {@link Coverage} for rendering.
     * @param canvasGrid Rendering canvas grid geometry
     * @param isElevation {@code true} if we want elevation coverage, else ({@code false}) for features coverage.
     * @param sourceBands coverage source bands to features
     * @return expected {@linkplain GridCoverage2D elevation coverage} or {@linkplain GridCoverage2D coverage}
     * @throws org.geotoolkit.coverage.io.CoverageStoreException if problem during coverage reading.
     * @throws org.opengis.referencing.operation.TransformException if problem during {@link Envelope} transformation.
     * @throws org.opengis.util.FactoryException if problem during {@link Envelope} study.
     * @throws org.geotoolkit.process.ProcessException if problem during resampling processing.
     * @see ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     * @see ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam)
     */
    protected GridCoverage2D getObjectiveCoverage(final ProjectedCoverage projectedCoverage,
            GridGeometry canvasGrid, final boolean isElevation, int[] sourceBands)
            throws DataStoreException, TransformException, FactoryException, ProcessException {
        ArgumentChecks.ensureNonNull("projectedCoverage", projectedCoverage);

        final MapLayer coverageLayer = projectedCoverage.getLayer();
        final GridCoverageResource ref = (GridCoverageResource) coverageLayer.getResource();

        final GridGeometry slice = extractSlice(ref.getGridGeometry(), canvasGrid);

        final GridCoverage gc = ref.read(slice, sourceBands);
        GridCoverage2D coverage = org.geotoolkit.internal.coverage.CoverageUtilities.toGeotk(gc);

        //at this point, we want a single slice in 2D
        //we remove all other dimension to simplify any following operation
        if (coverage.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() > 2) {
            final GridGeometry gridGeometry2d = coverage.getGridGeometry().reduce(0,1);
            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setGridGeometry(gridGeometry2d);
            gcb.setRenderedImage(coverage.getRenderedImage());
            gcb.setSampleDimensions(coverage.getSampleDimensions());
            gcb.setName(coverage.getName());
            coverage = gcb.getGridCoverage2D();
        }

        final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(canvasGrid.getCoordinateReferenceSystem());
        if (Utilities.equalsIgnoreMetadata(crs2d, coverage.getCoordinateReferenceSystem2D())) {
            return coverage;
        } else {
            //resample
            final double[] fill = new double[coverage.getSampleDimensions().size()];
            Arrays.fill(fill, Double.NaN);

            /////// HACK FOR 0/360 /////////////////////////////////////////
            GeneralEnvelope ge = new GeneralEnvelope(coverage.getEnvelope2D());
            try {
                GeneralEnvelope cdt = GeneralEnvelope.castOrCopy(Envelopes.transform(coverage.getEnvelope2D(), CommonCRS.WGS84.normalizedGeographic()));
                cdt.normalize();
                if (!cdt.isEmpty()) {
                    ge = cdt;
                }
            } catch (ProjectionException ex) {
                LOGGER.log(Level.INFO, ex.getMessage(), ex);
            }
            GridGeometry resampleGrid = canvasGrid;
            try {
                resampleGrid = resampleGrid.derive()
                    .rounding(GridRoundingMode.ENCLOSING)
                    .subgrid(ge)
                    .build()
                    .reduce(0,1);
            } catch (IllegalGridGeometryException ex) {
                LOGGER.log(Level.INFO, ex.getMessage(), ex);
            }
            resampleGrid = CoverageUtilities.forceLowerToZero(resampleGrid);
            /////// HACK FOR 0/360 /////////////////////////////////////////

            if (coverage.getSampleDimensions() != null && !coverage.getSampleDimensions().isEmpty()) {
                //interpolate in geophysic
                coverage = coverage.view(ViewType.GEOPHYSICS);
            }
            return new ResampleProcess(coverage, crs2d, resampleGrid, InterpolationCase.BILINEAR, fill).executeNow();
        }

    }

    private GridGeometry extractSlice(GridGeometry fullArea, GridGeometry areaOfInterest)
            throws CoverageStoreException, TransformException, FactoryException, ProcessException {

        // on displayed area
        Envelope canvasEnv = areaOfInterest.getEnvelope();
        double[] resolution = areaOfInterest.getResolution(true);
        /////// HACK FOR 0/360 /////////////////////////////////////////////
        try {
            Map.Entry<Envelope, double[]> entry = solveWrapAround(fullArea, canvasEnv, resolution);
            if (entry != null) {
                canvasEnv = entry.getKey();
                resolution = entry.getValue();
            }
        } catch (ProjectionException ex) {
            //mays happen when displaying an area partialy outside
            //computation area of coverage crs
            LOGGER.log(Level.INFO, ex.getMessage(), ex);
        }
        /////// HACK FOR 0/360 /////////////////////////////////////////////
        GridGeometry slice = fullArea;
        try {
            slice = fullArea.derive()
                    .rounding(GridRoundingMode.ENCLOSING)
                    .subgrid(canvasEnv, resolution)
                    .build();
        } catch (IllegalGridGeometryException ex) {
            LOGGER.log(Level.INFO, ex.getMessage(), ex);
        }

        // latest data slice
        final GridExtent extent = slice.getExtent();
        final MathTransform gridToCrs = slice.getGridToCRS(PixelInCell.CELL_CENTER);
        final long[] low = new long[extent.getDimension()];
        final long[] high = new long[extent.getDimension()];
        low[0] = extent.getLow(0);
        low[1] = extent.getLow(1);
        high[0] = extent.getHigh(0);
        high[1] = extent.getHigh(1);
        for (int i=2,n=low.length;i<n;i++) {
            low[i] = extent.getHigh(i);
            high[i] = extent.getHigh(i);
        }
        //add 3 cell padding for interpolations
        for (int i=0;i<2;i++) {
            low[i] = extent.getLow(i) - 3;
            high[i] = extent.getHigh(i) + 3;
        }
        final GridExtent sliceExt = new GridExtent(null, low, high, true);
        slice = new GridGeometry(sliceExt, PixelInCell.CELL_CENTER, gridToCrs, slice.getCoordinateReferenceSystem());

        return slice;
    }

    /**
     * Pragmatic approach trying to solve intersection of areas with
     * different meridian origin such as -180/+180 to +0/+360.
     *
     * @param areaOfInterest
     * @param resolution, may be changed by this method.
     * @return update area of interest envelope, CRS may have changed and new resolution
     *         or null if unchanged.
     */
    private Map.Entry<Envelope, double[]> solveWrapAround(final GridGeometry grid, Envelope areaOfInterest, double[] resolution) throws TransformException, FactoryException {

        // unchanged
        if (areaOfInterest == null) return null;

        final CoordinateReferenceSystem gridCrs = grid.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem areaCrs = areaOfInterest.getCoordinateReferenceSystem();

        // unchanged
        if (Utilities.equalsIgnoreMetadata(gridCrs, areaCrs)) return null;

        // find area horizontal crs and it's index.
        List<SingleCRS> areaCrsComponents = CRS.getSingleComponents(areaCrs);
        int areaHorizontalIndex = 0;
        int areaHorizontalOffset = 0;
        SingleCRS areaHorizontalCrs = null;
        for (int n=areaCrsComponents.size(); areaHorizontalIndex < n; areaHorizontalIndex++) {
            SingleCRS areaCmpCrs = areaCrsComponents.get(areaHorizontalIndex);
            if (CRS.isHorizontalCRS(areaCmpCrs)) {
                areaHorizontalCrs = areaCmpCrs;
                break;
            }
            areaHorizontalOffset += areaCmpCrs.getCoordinateSystem().getDimension();
        }

        // if no horizontal part found, return area unchanged
        if (areaHorizontalCrs == null) return null;

        // find counterpart in grid crs
        final List<SingleCRS> gridCrsComponents = CRS.getSingleComponents(gridCrs);
        int offsetGrid = 0;
        SingleCRS gridHorizontalCrs = null;
        for (SingleCRS gridCmpCrs : gridCrsComponents) {
            if (CRS.isHorizontalCRS(areaHorizontalCrs)) {
                gridHorizontalCrs = gridCmpCrs;
                break;
            }
            offsetGrid += gridCmpCrs.getCoordinateSystem().getDimension();
        }

        // no horizontal counter part found, return area unchanged
        if (gridHorizontalCrs == null) return null;
        // unchanged
        if (Utilities.equalsIgnoreMetadata(areaHorizontalCrs, gridHorizontalCrs)) return null;


        // Extract Horizontal envelopes
        final Envelope gridEnvelope = grid.getEnvelope();
        GeneralEnvelope areaEnv = new GeneralEnvelope(areaHorizontalCrs);
        areaEnv.setRange(areaHorizontalOffset, areaOfInterest.getMinimum(areaHorizontalOffset), areaOfInterest.getMaximum(areaHorizontalOffset));
        areaEnv.setRange(areaHorizontalOffset+1, areaOfInterest.getMinimum(areaHorizontalOffset+1), areaOfInterest.getMaximum(areaHorizontalOffset+1));
        GeneralEnvelope gridEnv = new GeneralEnvelope(gridHorizontalCrs);
        gridEnv.setRange(offsetGrid, gridEnvelope.getMinimum(offsetGrid), gridEnvelope.getMaximum(offsetGrid));
        gridEnv.setRange(offsetGrid+1, gridEnvelope.getMinimum(offsetGrid+1), gridEnvelope.getMaximum(offsetGrid+1));

        // Convert envelopes to geographic
        GeneralEnvelope areaHorizontalEnv = areaEnv;
        SingleCRS areaGeographicCrs = areaHorizontalCrs;
        if (areaHorizontalCrs instanceof ProjectedCRS) {
            areaGeographicCrs = ((ProjectedCRS) areaHorizontalCrs).getBaseCRS();
            areaEnv = GeneralEnvelope.castOrCopy(Envelopes.transform(areaEnv, areaGeographicCrs));
        }
        SingleCRS gridGeographicCrs = gridHorizontalCrs;
        if (gridHorizontalCrs instanceof ProjectedCRS) {
            gridGeographicCrs = ((ProjectedCRS) gridHorizontalCrs).getBaseCRS();
            gridEnv = GeneralEnvelope.castOrCopy(Envelopes.transform(gridEnv, gridGeographicCrs));
        }

        // intersections are correctly handle in geographic CRS where WrapAround axis are defined.
        CoordinateOperation operation = CRS.findOperation(areaGeographicCrs, gridGeographicCrs, null);
        gridEnv.intersect(Envelopes.transform(operation, areaEnv));

        // Create new compound CRS for area of interest
        areaCrsComponents = new ArrayList(areaCrsComponents); //make list modifiable
        areaCrsComponents.set(areaHorizontalIndex, gridGeographicCrs);
        final CoordinateReferenceSystem newAreaCrs = CRS.compound(areaCrsComponents.toArray(new CoordinateReferenceSystem[0]));

        // Rebuild area of interest in new CRS
        final GeneralEnvelope env = new GeneralEnvelope(newAreaCrs);
        for (int k=0,kn=env.getDimension(); k<kn; k++) {
            env.setRange(k, areaOfInterest.getMinimum(k), areaOfInterest.getMaximum(k));
        }
        env.setRange(areaHorizontalOffset, gridEnv.getMinimum(0), gridEnv.getMaximum(0));
        env.setRange(areaHorizontalOffset+1, gridEnv.getMinimum(1), gridEnv.getMaximum(1));

        if (env.isEmpty()) {
            //the solvewrap arround method is not 100% reliable with special projection
            //in some cases envelopes becomes empty
            return null;
        }

        //compute new resolution
        if (resolution != null && resolution.length != 0) {
            operation = CRS.findOperation(areaHorizontalCrs, gridGeographicCrs, null);

            double[] horizontalResolution = new double[]{
                resolution[areaHorizontalOffset],
                resolution[areaHorizontalOffset+1]};
            final Matrix m = operation.getMathTransform().derivative(areaHorizontalEnv.getMedian());
            horizontalResolution = MatrixSIS.castOrCopy(m).multiply(horizontalResolution);

            resolution = resolution.clone(); //do not modify user parameter
            resolution[areaHorizontalOffset] = Math.abs(horizontalResolution[0]);
            resolution[areaHorizontalOffset+1] = Math.abs(horizontalResolution[1]);
        }

        return new AbstractMap.SimpleImmutableEntry<>(env, resolution);
    }

    /**
     * {@inheritDoc }
     *
     * Prepare coverage for Raster rendering.
     */
    protected GridCoverage2D prepareCoverageToResampling(final GridCoverage2D coverageSource, final C symbolizer) {
        return coverageSource;
    }

    /**
     * Returns features Coverage from {@link ProjectedCoverage} and given initialized parameters.
     *
     * @param projectedCoverage Coverage where {@link GridCoverage2D} is features.
     * @param isElevation define if internaly method {@link ProjectedCoverage#getElevationCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam) }
     * or {@link ProjectedCoverage#getCoverage(org.geotoolkit.coverage.io.GridCoverageReadParam) } will be call.
     * @param paramEnvelope Requested envelope to features GridCoverage.
     * @param paramResolution Requested features resolution.
     * @param sourceBands Requested Read source bands. May be {@code null}.
     * @param inputCoverageEnvelope Envelope only use if problem during reading for log message.
     * @return features Coverage.
     * @throws CoverageStoreException if problem during reading action.
     */
    private static GridCoverage2D readCoverage(final ProjectedCoverage projectedCoverage, final boolean isElevation,
                                               final Envelope paramEnvelope, double[] paramResolution, final int[] sourceBands,
                                               final Envelope inputCoverageEnvelope)
            throws CoverageStoreException, TransformException {

        //check if the envelope is not reduced to an empty area
        if (paramEnvelope != null) {
            GeneralEnvelope genv = GeneralEnvelope.castOrCopy(paramEnvelope);
            if (genv.getDimension() != 2) {
                //extract 2d component, we do not want to test other dimensions
                //time or height may have the same value for min/max which
                //result in a slice which would be 'empty'.
                final SingleCRS crs2d = CRS.getHorizontalComponent(genv.getCoordinateReferenceSystem());
                genv = GeneralEnvelope.castOrCopy(Envelopes.transform(genv, crs2d));
            }

            if (genv.isEmpty()) return null;
        }
        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(paramEnvelope);
        if (sourceBands!=null) param.setSourceBands(sourceBands);

        //ensure dimension match, resolution is often in 2D while data is actualy N-Dimension
        if (paramEnvelope != null && paramEnvelope.getDimension() != paramResolution.length) {
            final int dim = paramEnvelope.getDimension();
            final double[] cp = Arrays.copyOf(paramResolution, dim);
            for (int i=paramResolution.length; i<cp.length; i++) cp[i] = 1;
            paramResolution = cp;
        }
        param.setResolution(paramResolution);

        GridCoverage2D dataCoverage = (isElevation) ? projectedCoverage.getElevationCoverage(param) : projectedCoverage.getCoverage(param);

        if (dataCoverage == null) {
            //-- in future jdk8 version return an Optional<Coverage>
            final StringBuilder strB = new StringBuilder(isElevation ? "getObjectiveElevationCoverage()" : "getObjectiveCoverage()");
            strB.append(" : \n impossible to read");
            strB.append((isElevation) ? " Elevation ":" Image ");
            strB.append("Coverage with internally projected coverage boundary : ");
            strB.append(inputCoverageEnvelope);
            strB.append("\nwith the following renderer requested Envelope.");
            strB.append(paramEnvelope);
            LOGGER.log(Level.FINE, strB.toString());
        }

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
        if (!Utilities.equalsIgnoreMetadata(requestCRS, coverageEnvelope.getCoordinateReferenceSystem()))
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

    /**
     * Detect the most appropriate interpolation type based on coverage sample dimensions.
     * Interpolation is possible only when data do not contain qualitative informations.
     */
    private static InterpolationCase findInterpolationCase(List<SampleDimension> sampleDimensions) throws CoverageStoreException{

        if (sampleDimensions != null) {
            for (SampleDimension sd : sampleDimensions) {
                final List<Category> categories = sd.getCategories();
                if (categories != null) {
                    for (Category cat : categories) {
                        if (!cat.isQuantitative() && !cat.getName().toString(Locale.ENGLISH).equals("No data")) {
                            return InterpolationCase.NEIGHBOR;
                        }
                    }
                }
            }
        }

        //no information on the data or datas are not qualitative, assume it can be interpolated
        //TODO : search geotk history for code made by Desruisseaux in old Resample operator,
        //       it contained such verifications.
        return InterpolationCase.BILINEAR;
    }

    /**
     * Returns {@code true} if {@link Envelope} contain at least one
     * {@link Double#NaN} value, else {@code false}.
     *
     * @param envelope the envelope which will be verify.
     * @return {@code true} if {@link Envelope} contain at least one {@link Double#NaN} value, else {@code false}.
     * @see #containNAN(org.opengis.geometry.Envelope, int, int)
     */
    private static boolean containNAN(final Envelope envelope) {
        return containNAN(envelope, 0, envelope.getDimension() - 1);
    }

    /**
     * Returns {@code true} if {@link Envelope} contain at least one
     * {@link Double#NaN} value into its horizontal geographic part, else {@code false}.
     *
     * @param envelope the envelope which will be verify.
     * @return {@code true} if {@link Envelope} contain at least one {@link Double#NaN} value, else {@code false}.
     * @see CRSUtilities#firstHorizontalAxis(org.opengis.referencing.crs.CoordinateReferenceSystem)
     * @see #containNAN(org.opengis.geometry.Envelope, int, int)
     */
    private static boolean containNANInto2DGeographicPart(final Envelope envelope) {
        ArgumentChecks.ensureNonNull("Envelopes.containNANInto2DGeographicPart()", envelope);
        final int minOrdiGeo = CRSUtilities.firstHorizontalAxis(envelope.getCoordinateReferenceSystem());
        return containNAN(envelope, minOrdiGeo, minOrdiGeo + 1);
    }

    /**
     * Returns {@code true} if {@link Envelope} contain at least one
     * {@link Double#NaN} value on each inclusive dimension stipulate by
     * firstIndex and lastIndex, else {@code false}.
     *
     * @param envelope the envelope which will be verify.
     * @param firstIndex first inclusive dimension index.
     * @param lastIndex last <strong>INCLUSIVE</strong> dimension.
     * @return {@code true} if {@link Envelope} contain at least one {@link Double#NaN} value, else {@code false}.
     */
    private static boolean containNAN(final Envelope envelope, final int firstIndex, final int lastIndex) {
        ArgumentChecks.ensureNonNull("Envelopes.containNAN()", envelope);
        ArgumentChecks.ensurePositive("firstIndex", firstIndex);
        ArgumentChecks.ensurePositive("lastIndex", lastIndex);
        if (lastIndex >= envelope.getDimension())
            throw new IllegalArgumentException("LastIndex must be strictly lower than "
                    + "envelope dimension number. Expected maximum valid index = "+(envelope.getDimension() - 1)+". Found : "+lastIndex);
        ArgumentChecks.ensureValidIndex(lastIndex + 1, firstIndex);
        for (int d = firstIndex; d <= lastIndex; d++) {
            if (Double.isNaN(envelope.getMinimum(d))
             || Double.isNaN(envelope.getMaximum(d))) return true;
        }
        return false;
    }

}
