/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.media.jai.Histogram;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.BandSelectDescriptor;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageUtilities;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.*;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.processing.CoverageProcessingException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.raster.ShadedReliefOp;
import org.geotoolkit.filter.visitor.DefaultFilterVisitor;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.Rescaler;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.image.jai.FloodFill;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.coverage.statistics.ImageStatistics;
import org.geotoolkit.process.coverage.statistics.StatisticOp;
import org.geotoolkit.process.coverage.resample.ResampleDescriptor;
import org.geotoolkit.process.coverage.shadedrelief.ShadedReliefDescriptor;
import org.geotoolkit.process.coverage.statistics.Statistics;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.referencing.operation.transform.EarthGravitationalModel;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.CompatibleColorModel;
import org.geotoolkit.style.function.DefaultInterpolationPoint;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Jenks;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.util.BufferedImageUtilities;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.ShadedRelief;
import org.opengis.util.FactoryException;
import org.apache.sis.referencing.CommonCRS;

/**
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class DefaultRasterSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedRasterSymbolizer>{

    /**
     * A tolerence value for black color. Used in {@linkplain #removeBlackBorder(java.awt.image.WritableRenderedImage)}
     * to define an applet of black colors to replace with alpha data.
     */
    private static final int COLOR_TOLERANCE = 13;

    public DefaultRasterSymbolizerRenderer(final SymbolizerRendererService service, final CachedRasterSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException {

        try {
            ////////////////////////////////////////////////////////////////////
            // 1 - Get data and elevation coverage                            //
            ////////////////////////////////////////////////////////////////////
            double[] resolution = renderingContext.getResolution();
            Envelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
            resolution = checkResolution(resolution,bounds);
            final CoverageMapLayer coverageLayer = projectedCoverage.getLayer();
            final Envelope layerBounds = coverageLayer.getBounds();
            final CoordinateReferenceSystem coverageMapLayerCRS = layerBounds.getCoordinateReferenceSystem();

            final Map<String,Double> queryValues = extractQuery(projectedCoverage.getLayer());
            if (queryValues != null && !queryValues.isEmpty()) {
                bounds = fixEnvelopeWithQuery(queryValues, bounds, coverageMapLayerCRS);
                resolution = fixResolutionWithCRS(resolution, coverageMapLayerCRS);
            }

            final GridCoverageReadParam param = new GridCoverageReadParam();
            param.setEnvelope(bounds);
            param.setResolution(resolution);

            GridCoverage2D dataCoverage;
            GridCoverage2D elevationCoverage;
            try {
                dataCoverage = projectedCoverage.getCoverage(param);
                elevationCoverage = projectedCoverage.getElevationCoverage(param);
            } catch (DisjointCoverageDomainException ex) {
                //LOGGER.log(Level.INFO, ex.getMessage());

                //since the visible envelope can be much larger, we may obtain NaN when transforming the envelope
                //which causes the disjoint domain exception
                final GeneralEnvelope objCovEnv = new GeneralEnvelope(CRS.transform(layerBounds, bounds.getCoordinateReferenceSystem()));
                objCovEnv.intersect(bounds);
                if(objCovEnv.isEmpty()){
                    return; //the coverage envelope does not intersect the canvas envelope.
                }
                param.setEnvelope(objCovEnv);
                try {
                    dataCoverage = projectedCoverage.getCoverage(param);
                    elevationCoverage = projectedCoverage.getElevationCoverage(param);
                } catch (DisjointCoverageDomainException exd) {
                    //we tried
                    return;
                }
            } catch (CoverageStoreException ex) {
                throw new PortrayalException(ex);
            }

            if(dataCoverage == null){
                //LOGGER.log(Level.WARNING, "Requested an area where no coverage where found.");
                return;
            }


            ////////////////////////////////////////////////////////////////////
            // 2 - Select bands to style / display                            //
            ////////////////////////////////////////////////////////////////////

            //band select ----------------------------------------------------------
            //works as a JAI operation
            final int nbDim = dataCoverage.getNumSampleDimensions();
            final RasterSymbolizer sourceSymbol = symbol.getSource();
            if (nbDim > 1) {
                //we can change sample dimension only if we have more then one available.
                final ChannelSelection selections = sourceSymbol.getChannelSelection();
                if (selections != null) {
                    final SelectedChannelType channel = selections.getGrayChannel();
                    if (channel != null) {
                        //single band selection
                        final int[] indices = new int[]{
                                Integer.valueOf(channel.getChannelName())
                        };
                        dataCoverage = selectBand(dataCoverage, indices);
                    } else {
                        final SelectedChannelType[] channels = selections.getRGBChannels();
                        final int[] selected = new int[]{
                                Integer.valueOf(channels[0].getChannelName()),
                                Integer.valueOf(channels[1].getChannelName()),
                                Integer.valueOf(channels[2].getChannelName())
                        };
                        //@Workaround(library="JAI",version="1.0.x")
                        //TODO when JAI has been rewritten, this test might not be necessary anymore
                        //check if selection actually does something
                        if (!(selected[0] == 0 && selected[1] == 1 && selected[2] == 2) || nbDim != 3) {
                            dataCoverage = selectBand(dataCoverage, selected);
                        }
                    }
                }
            }

            ////////////////////////////////////////////////////////////////////
            // 3 - Reproject data                                             //
            ////////////////////////////////////////////////////////////////////

            boolean isReprojected;
            final MathTransform coverageToObjective;
            final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
            try{
                final CoordinateReferenceSystem targetCRS = renderingContext.getObjectiveCRS2D();
                final CoordinateReferenceSystem candidate2D = CRSUtilities.getCRS2D(coverageCRS);
                /* It appears EqualsIgnoreMetadata can return false sometimes, even if the two CRS are equivalent.
                 * But mathematics don't lie, so if they really describe the same transformation, conversion from
                 * one to another will give us an identity matrix.
                 */
                coverageToObjective = CRS.findMathTransform(candidate2D, targetCRS);
                isReprojected = !coverageToObjective.isIdentity();

                if (isReprojected) {
                    //calculate best intersection area
                    final GeneralEnvelope tmp = new GeneralEnvelope(renderingContext.getPaintingObjectiveBounds2D());
                    final int xAxis = Math.max(0, CoverageUtilities.getMinOrdinate(coverageCRS));

//                    tmp.intersect(CRS.transform(coverageToObjective, new GeneralEnvelope(
//                            dataCoverage.getEnvelope()).subEnvelope(xAxis, xAxis + 2)));
                    final GeneralEnvelope coverageEnv2D =new GeneralEnvelope(dataCoverage.getEnvelope2D());
                    final Envelope transformed = CRS.transform(coverageEnv2D, renderingContext.getObjectiveCRS2D());
                    tmp.intersect(transformed);

                    if (tmp.isEmpty()) {
                        dataCoverage = null;

                    } else {
                        //calculate gridgeometry
                        final AffineTransform2D trs = renderingContext.getObjectiveToDisplay();
                        final GeneralEnvelope dispEnv = CRS.transform(trs, tmp);
                        final int width = (int)Math.ceil(dispEnv.getSpan(0));
                        final int height = (int)Math.ceil(dispEnv.getSpan(1));

                        if (width <= 0 || height <= 0) {
                            dataCoverage = null;
                        } else {
                            //force alpha if image do not get any "invalid data" rule (Ex : No-data in image or color map).
                            dataCoverage = getReadyToResampleCoverage(dataCoverage, sourceSymbol);

                            final GridGeometry2D gg = new GridGeometry2D(new GridEnvelope2D(0, 0, width, height), tmp);

                            final ProcessDescriptor desc = ResampleDescriptor.INSTANCE;
                            final ParameterValueGroup params = desc.getInputDescriptor().createValue();
                            ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_COVERAGE.getName().getCode()).setValue(dataCoverage);
                            ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_COORDINATE_REFERENCE_SYSTEM.getName().getCode()).setValue(targetCRS);
                            ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_GRID_GEOMETRY.getName().getCode()).setValue(gg);

                            final org.geotoolkit.process.Process process = desc.createProcess(params);
                            final ParameterValueGroup result = process.call();
                            dataCoverage = (GridCoverage2D) result.parameter("result").getValue();
                        }
                    }
                }
            } catch (CoverageProcessingException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            } catch(Exception ex) {
                //several kind of errors can happen here, we catch anything to avoid blocking the map component.
                monitor.exceptionOccured(
                    new IllegalStateException("Coverage is not in the requested CRS, found : \n" +
                    coverageCRS +
                    "\n Was expecting : \n" +
                    renderingContext.getObjectiveCRS() +
                    "\nOriginal Cause:"+ ex.getMessage(), ex), Level.WARNING);
                return;
            }

            if (dataCoverage == null) {
                //LOGGER.log(Level.WARNING, "RasterSymbolizer : Reprojected coverage is null.");
                return;
            }

            //we must switch to objectiveCRS for grid coverage
            renderingContext.switchToObjectiveCRS();

            ////////////////////////////////////////////////////////////////////
            // 4 - Apply style                                                //
            ////////////////////////////////////////////////////////////////////

            RenderedImage dataImage = applyStyle(dataCoverage, elevationCoverage, coverageLayer.getElevationModel(), sourceSymbol, hints, isReprojected);
            final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);

            ////////////////////////////////////////////////////////////////////
            // 5 - Correct cross meridian problems / render                   //
            ////////////////////////////////////////////////////////////////////

            if (renderingContext.wraps == null) {
                //single rendering
                renderCoverage(projectedCoverage, dataImage, trs2D);

            } else {
                //check if the geometry overlaps the meridian
                int nbIncRep = renderingContext.wraps.wrapIncNb;
                int nbDecRep = renderingContext.wraps.wrapDecNb;
                final Geometry objBounds = JTS.toGeometry(dataCoverage.getEnvelope());

                // geometry cross the far east meridian, geometry is like :
                // POLYGON(-179,10,  181,10,  181,-10,  179,-10)
                if(objBounds.intersects(renderingContext.wraps.wrapIncLine)){
                    //duplicate geometry on the other warp line
                    nbDecRep++;
                }
                // geometry cross the far west meridian, geometry is like :
                // POLYGON(-179,10, -181,10, -181,-10,  -179,-10)
                else if(objBounds.intersects(renderingContext.wraps.wrapDecLine)){
                    //duplicate geometry on the other warp line
                    nbIncRep++;
                }

                renderCoverage(projectedCoverage, dataImage, trs2D);

                //repetition of increasing and decreasing sides.
                for(int i=0;i<nbDecRep;i++){
                    g2d.setTransform(renderingContext.wraps.wrapDecObjToDisp[i]);
                    renderCoverage(projectedCoverage, dataImage, trs2D);
                }
                for(int i=0;i<nbIncRep;i++){
                    g2d.setTransform(renderingContext.wraps.wrapIncObjToDisp[i]);
                    renderCoverage(projectedCoverage, dataImage, trs2D);
                }
            }

            renderingContext.switchToDisplayCRS();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,"Portrayal exception: "+e.getMessage(),e);
        }
    }

    private static double[] nextDataType(int type){
        switch(type){
            case DataBuffer.TYPE_BYTE   : return new double[]{DataBuffer.TYPE_SHORT,Short.MIN_VALUE};
            case DataBuffer.TYPE_USHORT : return new double[]{DataBuffer.TYPE_INT,Integer.MIN_VALUE};
            case DataBuffer.TYPE_SHORT  : return new double[]{DataBuffer.TYPE_INT,Integer.MIN_VALUE};
            case DataBuffer.TYPE_INT    : return new double[]{DataBuffer.TYPE_INT,Integer.MIN_VALUE};
            case DataBuffer.TYPE_FLOAT  : return new double[]{DataBuffer.TYPE_FLOAT,Float.NaN};
            case DataBuffer.TYPE_DOUBLE : return new double[]{DataBuffer.TYPE_DOUBLE,Double.NaN};
            default : throw new IllegalArgumentException("");

        }
    }

    private void renderCoverage(final ProjectedCoverage projectedCoverage, RenderedImage img, MathTransform2D trs2D) throws PortrayalException{
        if (trs2D instanceof AffineTransform) {
            g2d.setComposite(symbol.getJ2DComposite());
            try {
                g2d.drawRenderedImage(img, (AffineTransform)trs2D);
            } catch (Exception ex) {

                if(ex instanceof ArrayIndexOutOfBoundsException){
                    //we can recover when it's an inapropriate componentcolormodel
                    final StackTraceElement[] eles = ex.getStackTrace();
                    if(eles.length > 0 && ComponentColorModel.class.getName().equalsIgnoreCase(eles[0].getClassName())){

                        try{
                            final CoverageReference ref = projectedCoverage.getLayer().getCoverageReference();
                            final GridCoverageReader reader = ref.acquireReader();
                            final Map<String,Object> analyze = StatisticOp.analyze(reader,ref.getImageIndex());
                            ref.recycle(reader);
                            final double[] minArray = (double[])analyze.get(StatisticOp.MINIMUM);
                            final double[] maxArray = (double[])analyze.get(StatisticOp.MAXIMUM);
                            final double min = findExtremum(minArray, true);
                            final double max = findExtremum(maxArray, false);

                            final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();
                            values.add(new DefaultInterpolationPoint(Double.NaN, GO2Utilities.STYLE_FACTORY.literal(new Color(0, 0, 0, 0))));
                            values.add(new DefaultInterpolationPoint(min, GO2Utilities.STYLE_FACTORY.literal(Color.BLACK)));
                            values.add(new DefaultInterpolationPoint(max, GO2Utilities.STYLE_FACTORY.literal(Color.WHITE)));
                            final Literal lookup = StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
                            final Literal fallback = StyleConstants.DEFAULT_FALLBACK;
                            final Function function = GO2Utilities.STYLE_FACTORY.interpolateFunction(
                                    lookup, values, Method.COLOR, Mode.LINEAR, fallback);
                            final CompatibleColorModel model = new CompatibleColorModel(img.getColorModel().getPixelSize(), function);
                            final ImageLayout layout = new ImageLayout().setColorModel(model);
                            img = new NullOpImage(img, layout, null, OpImage.OP_COMPUTE_BOUND);
                            g2d.drawRenderedImage(img, (AffineTransform)trs2D);
                        }catch(Exception e){
                            //plenty of errors can happen when painting an image
                            monitor.exceptionOccured(e, Level.WARNING);

                            //raise the original error
                            monitor.exceptionOccured(ex, Level.WARNING);
                        }
                    } else {
                        //plenty of errors can happen when painting an image
                        monitor.exceptionOccured(ex, Level.WARNING);
                    }
                } else {
                    //plenty of errors can happen when painting an image
                    monitor.exceptionOccured(ex, Level.WARNING);
                }
            }
        }else if (trs2D instanceof LinearTransform) {
            final LinearTransform lt = (LinearTransform) trs2D;
            final int col = lt.getMatrix().getNumCol();
            final int row = lt.getMatrix().getNumRow();
            //TODO using only the first parameters of the linear transform
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass());
        }else{
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass() );
        }

        //draw the border if there is one---------------------------------------
        CachedSymbolizer outline = symbol.getOutLine();
        if(outline != null){
            GO2Utilities.portray(projectedCoverage, outline, renderingContext);
        }

    }

    /**
     * Fix portrayal resolutions from CoverageMapLayer bounds CRS dimensions.
     *
     * @param resolution default resolution
     * @param coverageCRS CoverageMapLayer CRS
     * @return fixed resolutions or input resolution if coverageCRS is null.
     */
    public static  double[] fixResolutionWithCRS(double[] resolution, CoordinateReferenceSystem coverageCRS) {
        if (coverageCRS != null) {
            final double[] tempRes = new double[coverageCRS.getCoordinateSystem().getDimension()];
            for (int i=0; i < tempRes.length; i++) {
                if (i < resolution.length) {
                    tempRes[i] = resolution[i];
                } else {
                    tempRes[i] = Math.nextUp(0);
                }
            }
            return tempRes;
        }
        return resolution;
    }

    /**
     * Set envelope ranges using values map extracted from Query.
     * This method use coverage CRS axis names to link Query parameters.
     *
     * @param values Map<String, Double> extracted from CoverageMapLayer Query
     * @param bounds Envelope to fix.
     * @param coverageCRS complete ND CRS
     * @return fixed Envelope or input bounds parameter if values are null or empty.
     */
    public static Envelope fixEnvelopeWithQuery(final Map<String, Double> values, final Envelope bounds,
                                                final CoordinateReferenceSystem coverageCRS) {
        if (values != null && !values.isEmpty()) {
            final GeneralEnvelope env = new GeneralEnvelope(coverageCRS);

            // Set ranges from the map
            for (int j=0; j < bounds.getDimension(); j++) {
                env.setRange(j, bounds.getMinimum(j), bounds.getMaximum(j));
            }

            // Set ranges from the filter
            for (int i=0; i < coverageCRS.getCoordinateSystem().getDimension(); i++) {
                final CoordinateSystemAxis axis = coverageCRS.getCoordinateSystem().getAxis(i);
                final String axisName = axis.getName().getCode();
                if (values.containsKey(axisName)) {
                    final Double val = values.get(axisName);
                    env.setRange(i, val, val);
                }
            }

            return env;
        }

        return bounds;
    }

    /**
     * Extract query parameters from CoverageMapLayer if his an instance of DefaultCoverageMapLayer.
     *
     * @param coverageMapLayer CoverageMapLayer
     * @return a Map</String,Double> with query parameters or null
     */
    public static Map<String, Double> extractQuery(final CoverageMapLayer coverageMapLayer) {

        Map<String,Double> values = null;
        if (coverageMapLayer instanceof DefaultCoverageMapLayer) {
            final DefaultCoverageMapLayer covMapLayer = (DefaultCoverageMapLayer) coverageMapLayer;
            final Query query = covMapLayer.getQuery();
            if (query != null) {
                // visit the filter to extract all values
                final FilterVisitor fv = new DefaultFilterVisitor() {

                    @Override
                    public Object visit(PropertyIsEqualTo filter, Object data) {
                        final Map<String,Double> values = (Map<String,Double>) data;
                        final String expr1 = ((PropertyName)filter.getExpression1()).getPropertyName();
                        final Double expr2 = Double.valueOf(((Literal)filter.getExpression2()).getValue().toString());
                        values.put(expr1, expr2);
                        return values;
                    }

                };

                final Filter filter = query.getFilter();
                values = (Map<String,Double>) filter.accept(fv, new HashMap<String, Double>());
            }
        }
        return values;
    }

    /**
     * Return a Digital Elevation Model from source {@link ElevationModel} parameter in function of coverage parameter properties.
     *
     * @param coverage
     * @param elevationModel
     * @return a Digital Elevation Model from source {@link ElevationModel} parameter in function of coverage parameter properties.
     * @throws FactoryException
     * @throws TransformException
     */
    public static GridCoverage2D getDEMCoverage(final GridCoverage2D coverage, final ElevationModel elevationModel) throws FactoryException, TransformException, CoverageStoreException {

        if(elevationModel==null) return null;

        // coverage attributs
        final GridGeometry2D covGridGeom       = coverage.getGridGeometry();
        final GridEnvelope2D covExtend         = covGridGeom.getExtent2D();
        final CoordinateReferenceSystem covCRS = coverage.getCoordinateReferenceSystem2D();
        final Envelope2D covEnv2d              = coverage.getGridGeometry().getEnvelope2D();
        final double[] covResolution           = coverage.getGridGeometry().getResolution();

        final GridCoverageReader elevationReader = elevationModel.getCoverageReader();
        final GeneralGridGeometry elevGridGeom   = elevationReader.getGridGeometry(0);
        if (!(elevGridGeom instanceof GridGeometry2D)) {
            throw new IllegalArgumentException("the Digital Elevation Model should be instance of gridcoverage2D."+elevGridGeom);
        }
        final GridGeometry2D elevGridGeom2D    = (GridGeometry2D) elevGridGeom;

        final CoordinateReferenceSystem demCRS = elevGridGeom2D.getCoordinateReferenceSystem2D();

        final MathTransform demCRSToCov        = CRS.findMathTransform(demCRS, covCRS); // dem -> cov

        if (elevGridGeom2D.getEnvelope2D().equals(coverage.getGridGeometry().getEnvelope2D())
         && covExtend.equals(elevGridGeom2D.getExtent2D())) return (GridCoverage2D) elevationReader.read(0, null);

        final GeneralEnvelope readParamEnv = Envelopes.transform(demCRSToCov.inverse(), covEnv2d);

        final GridCoverageReadParam gcrp = new GridCoverageReadParam();
        gcrp.setCoordinateReferenceSystem(demCRS);
        gcrp.setEnvelope(readParamEnv);

        final GridCoverage2D dem = (GridCoverage2D) elevationReader.read(0, gcrp);
        return getDEMCoverage(coverage, dem);

    }

    /**
     * Return a Digital Elevation Model from source DEM parameter in function of coverage parameter properties.
     *
     * @param coverage
     * @param dem
     * @return a Digital Elevation Model from source DEM parameter in function of coverage parameter properties.
     * @throws FactoryException
     * @throws TransformException
     */
    public static GridCoverage2D getDEMCoverage(final GridCoverage2D coverage, final GridCoverage2D dem) throws FactoryException, TransformException {

        // coverage attributs
        final GridGeometry2D covGridGeom       = coverage.getGridGeometry();
        final GridEnvelope2D covExtend         = covGridGeom.getExtent2D();
        final GridGeometry2D demGridGeom       = dem.getGridGeometry();

        //CRS
        final CoordinateReferenceSystem covCRS = coverage.getCoordinateReferenceSystem2D();
        final CoordinateReferenceSystem demCRS = demGridGeom.getCoordinateReferenceSystem2D();

        final MathTransform demCRSToCov = CRS.findMathTransform(demCRS, covCRS); // dem -> cov

        final GeneralEnvelope demDestEnv = Envelopes.transform(demCRSToCov, demGridGeom.getEnvelope2D());
        // coverage envelope
        final Envelope2D covEnv = covGridGeom.getEnvelope2D();

        /**
         * if the 2 coverage don't represent the same area we can't compute shadow on coverage.
         */
        if (!demDestEnv.intersects(covEnv, true)) {
            return null;
        }
        // get intersection to affect relief on shared area.
        GeneralEnvelope intersec = new GeneralEnvelope(demDestEnv);
        intersec.intersect(covEnv);

        final RenderedImage demImage = dem.getRenderedImage();

        // output mnt creation
        final BufferedImage destMNT = BufferedImageUtilities.createImage(covExtend.width, covExtend.height, demImage);
        intersec = Envelopes.transform(covGridGeom.getGridToCRS(PixelInCell.CELL_CORNER).inverse(), intersec);

        final Rectangle areaIterate = new Rectangle((int) intersec.getMinimum(0), (int) intersec.getMinimum(1), (int) Math.ceil(intersec.getSpan(0)), (int) Math.ceil(intersec.getSpan(1)));

        // dem source to dem dest
        final MathTransform sourcetodest = MathTransforms.concatenate(dem.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER),
                                                                      demCRSToCov,
                                                                      covGridGeom.getGridToCRS(PixelInCell.CELL_CENTER).inverse());


        final PixelIterator srcPix   = PixelIteratorFactory.createRowMajorIterator(demImage);
        final Interpolation interpol = Interpolation.create(srcPix, InterpolationCase.BICUBIC, 2);
        final Resample resampl       = new Resample(sourcetodest.inverse(), destMNT, areaIterate, interpol, new double[interpol.getNumBands()]);
        resampl.fillImage();

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setCoordinateReferenceSystem(covCRS);
        gcb.setRenderedImage(destMNT);
        gcb.setEnvelope(covEnv);
        return gcb.getGridCoverage2D();
    }

    /**
     * Create a geoide coverage to mimic an elevation model.
     * @param coverage
     * @return
     * @throws IllegalArgumentException
     * @throws FactoryException
     * @throws TransformException
     */
    public static GridCoverage2D getGeoideCoverage(final GridCoverage2D coverage) throws IllegalArgumentException, FactoryException, TransformException{

        final RenderedImage base = coverage.getRenderedImage();
        final float[][] matrix = new float[base.getHeight()][base.getWidth()];

        final EarthGravitationalModel trs = EarthGravitationalModel.create(CommonCRS.WGS84.datum(), 180);
        final MathTransform dataToLongLat = CRS.findMathTransform(coverage.getCoordinateReferenceSystem2D(), CommonCRS.WGS84.normalizedGeographic());
        final MathTransform2D gridToCRS = coverage.getGridGeometry().getGridToCRS2D();
        final MathTransform gridToLonLat = MathTransforms.concatenate(gridToCRS, dataToLongLat);

        final float[] buffer = new float[6];

        for(int y=0;y<matrix.length;y++){
            for(int x=0;x<matrix[0].length;x++){
                buffer[0]=x;buffer[1]=y;buffer[2]=0;
                gridToLonLat.transform(buffer, 0, buffer, 0, 1);
                trs.transform(buffer, 0, buffer, 0, 1);
                matrix[y][x] = buffer[2];
            }
        }

        GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("geoide");
        gcb.setRenderedImage(matrix);
        gcb.setCoordinateReferenceSystem(coverage.getCoordinateReferenceSystem2D());
        gcb.setGridToCRS(gridToCRS);
        return gcb.getGridCoverage2D();
    }


    ////////////////////////////////////////////////////////////////////////////
    // RenderedImage JAI image operations ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     *
     * @param coverage
     * @param styleElement
     * @param hints
     * @param isReprojected : if the coverage was rerprojected, this implies some
     *       black borders might have been added on the image
     * @return
     * @throws PortrayalException
     */
    public static RenderedImage applyStyle(GridCoverage2D coverage, GridCoverage2D elevationCoverage, final ElevationModel elevationModel, final RasterSymbolizer styleElement,
                final RenderingHints hints, boolean isReprojected) throws PortrayalException, ProcessException, FactoryException, TransformException, IOException {

        //band select ----------------------------------------------------------
        //works as a JAI operation
        final int nbDim = coverage.getNumSampleDimensions();

        RenderedImage image;

        //Recolor coverage -----------------------------------------------------
        ColorMap recolor = styleElement.getColorMap();
        //cheat on the colormap if we have only one band and no colormap
        recolorCase:
        if((recolor==null || recolor.getFunction()==null) && nbDim==1){
            RenderedImage ri = coverage.getRenderedImage();
            if( (ri.getColorModel() instanceof IndexColorModel) && (recolor==null || recolor.getFunction()==null)){
                //image has it's own color model
                break recolorCase;
            }
            ri = coverage.view(ViewType.GEOPHYSICS).getRenderedImage();
            final ImageStatistics analyse = Statistics.analyse(ri, true);
            final ImageStatistics.Band band0 = analyse.getBand(0);
            final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();
            values.add( GO2Utilities.STYLE_FACTORY.interpolationPoint(Float.NaN, GO2Utilities.STYLE_FACTORY.literal(new Color(0,0,0,0))));
            values.add( GO2Utilities.STYLE_FACTORY.interpolationPoint(band0.getMin(), GO2Utilities.STYLE_FACTORY.literal(Color.BLACK)));
            values.add( GO2Utilities.STYLE_FACTORY.interpolationPoint(band0.getMax(), GO2Utilities.STYLE_FACTORY.literal(Color.WHITE)));
            final Expression lookup = StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
            final Literal fallback = StyleConstants.DEFAULT_FALLBACK;
            final Function function = GO2Utilities.STYLE_FACTORY.interpolateFunction(
                    lookup, values, Method.COLOR, Mode.LINEAR, fallback);

            recolor = GO2Utilities.STYLE_FACTORY.colorMap(function);
        }
        if (recolor != null && recolor.getFunction() != null) {
            //color map is applied on geophysics view
            coverage = coverage.view(ViewType.GEOPHYSICS);
            image = coverage.getRenderedImage();

            final Function fct = recolor.getFunction();
            image = recolor(image, fct);
        } else {
            //no color map, used the default image rendered view
            // coverage = coverage.view(ViewType.RENDERED);
            if (coverage.getViewTypes().contains(ViewType.PHOTOGRAPHIC)) {
                image = coverage.view(ViewType.PHOTOGRAPHIC).getRenderedImage();
                image = forceAlpha(image);
                if (image instanceof WritableRenderedImage) {
                    removeBlackBorder((WritableRenderedImage)image);
                }
            } else {
                image = coverage.view(ViewType.RENDERED).getRenderedImage();
            }
        }

        //shaded relief---------------------------------------------------------
        final ShadedRelief shadedRel = styleElement.getShadedRelief();
        shadingCase:
        if(shadedRel!=null && shadedRel.getReliefFactor()!= null) {
            final double factor = shadedRel.getReliefFactor().evaluate(null, Double.class);
            if(factor== 0.0) break shadingCase;

            //BUG ? When using the grid coverage builder the color model is changed
            if(image.getColorModel() instanceof CompatibleColorModel){
                final BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                bi.createGraphics().drawRenderedImage(image, new AffineTransform());
                image = bi;
            }

            //ReliefShadow creating
            final GridCoverage2D mntCoverage;
            if(elevationCoverage!=null){
                mntCoverage = getDEMCoverage(coverage, elevationCoverage);
            }else{
                break shadingCase;
                //does not have a nice result, still better then nothing
                //but is really slow to calculate, disabled for now.
                //mntCoverage = getGeoideCoverage(coverage);
            }

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setGridGeometry(coverage.getGridGeometry());
            gcb.setRenderedImage(image);
            gcb.setName("tempimg");
            final GridCoverage2D ti = gcb.getGridCoverage2D();

            final MathTransform1D trs = (MathTransform1D) MathTransforms.linear(factor, 0);
            final org.geotoolkit.process.coverage.shadedrelief.ShadedRelief proc = new org.geotoolkit.process.coverage.shadedrelief.ShadedRelief(
                    ti, mntCoverage, trs);
            final ParameterValueGroup res = proc.call();
            final GridCoverage2D shaded = (GridCoverage2D) res.parameter(ShadedReliefDescriptor.OUT_COVERAGE_PARAM_NAME).getValue();
            image = shaded.getRenderedImage();
        }

        final ContrastEnhancement ce = styleElement.getContrastEnhancement();
        if(ce != null && image.getColorModel() instanceof ComponentColorModel){

            // histogram/normalize adjustment ----------------------------------
            final ContrastMethod method = ce.getMethod();
            if (ContrastMethod.HISTOGRAM.equals(method)) {
                image = equalize(image);
            } else if(ContrastMethod.NORMALIZE.equals(method)) {
                image = normalize(image);
            }

            // gamma correction ------------------------------------------------
            final Double gamma = ce.getGammaValue().evaluate(null, Double.class);
            if (gamma != null && gamma != 1) {
                //Specification : page 35
                // A “GammaValue” tells how much to brighten (values greater than 1.0) or dim (values less than 1.0) an image.
                image = brigthen(image, (int) ((gamma - 1) * 255f));
            }
        }
        return image;
    }

    /**
     * Analyse input coverage to know if we need to add an alpha channel. Alpha channel is required in photographic
     * coverage case, in order for the resample to deliver a ready to style image.
     *
     * @param source The coverage to analyse.
     * @param style Style to apply on coverage data.
     * @return The same coverage as input if style do not require an ARGB data to properly render, or a new ARGB coverage
     * computed from source data.
     */
    private static GridCoverage2D getReadyToResampleCoverage(final GridCoverage2D source, final RasterSymbolizer style) {
        final GridSampleDimension[] dims = source.getSampleDimensions();
        final ColorMap cMap = style.getColorMap();
        if ((cMap != null && cMap.getFunction() != null) ||
            (dims != null && dims.length != 0 && dims[0].getNoDataValues() != null) ||
            !source.getViewTypes().contains(ViewType.PHOTOGRAPHIC)) {
            return source;

        } else {
            final GridCoverage2D photoCvg = source.view(ViewType.PHOTOGRAPHIC);
            RenderedImage img = photoCvg.getRenderedImage();
            RenderedImage imga = forceAlpha(img);

            if (imga != img) {
                final GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setName("temp");
                gcb.setGridGeometry(source.getGridGeometry());
                gcb.setRenderedImage(imga);
                return gcb.getGridCoverage2D();
            } else {
                return source;
            }
        }
    }

    /**
     * Add an alpha band to the image and remove any black border if asked.
     *
     * TODO, this could be done more efficiently by adding an ImageLayout hints
     * when doing the coverage reprojection. but hints can not be passed currently.
     */
    private static RenderedImage forceAlpha(RenderedImage img){
        if (!img.getColorModel().hasAlpha()) {
            //Add alpha channel
            final BufferedImage buffer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            buffer.createGraphics().drawRenderedImage(img, new AffineTransform());
            img = buffer;
        }
        return img;
    }

    /**
     * Remove black border of an ARGB image to replace them with transparent pixels.
     * @param toFilter Image to ermove black border from.
     */
    private static void removeBlackBorder(final WritableRenderedImage toFilter) {
        final ArrayList<double[]> blackColors = new ArrayList<>();
        fillColorToleranceTable(0, 2, blackColors, new double[]{0, 0, 0, 255}, COLOR_TOLERANCE);
        final double[][] oldColors = blackColors.toArray(new double[0][]);
                FloodFill.fill(toFilter, oldColors, new double[]{0d, 0d, 0d, 0d},
                new java.awt.Point(0, 0),
                new java.awt.Point(toFilter.getWidth() - 1, 0),
                new java.awt.Point(toFilter.getWidth() - 1, toFilter.getHeight() - 1),
                new java.awt.Point(0, toFilter.getHeight() - 1)
        );
    }

    private static void fillColorToleranceTable(int index, int maxIndex, List<double[]> container, double[] baseColor, int tolerance) {
        for (int j = 0 ; j < tolerance; j++) {
            final double[] color = new double[baseColor.length];
            System.arraycopy(baseColor, 0, color, 0, baseColor.length);
            color[index] += j;
            if (index >= maxIndex) {
                container.add(color);
            } else {
                for (int i = index +1 ; i <= maxIndex ; i++) {
                    fillColorToleranceTable(i, maxIndex, container, color, tolerance);
                }
            }
        }
    }

    private static RenderedImage shadowed(final RenderedImage img){
        final ParameterBlock pb = new ParameterBlock();
        pb.setSource(img, 0);
//        return JAI.create("ShadedRelief", pb, null);

        return new ShadedReliefOp(img, null, null, null);
    }

    private static GridCoverage2D selectBand(GridCoverage2D coverage, final int[] indices){
        if(coverage.getNumSampleDimensions() < indices.length){
            //not enough bands in the image
            LOGGER.log(Level.WARNING, "Raster Style define more bands than the data");
            return coverage;
        }else{
            RenderedImage image = coverage.getRenderedImage();
            image = BandSelectDescriptor.create(image, indices, null);
            final GridCoverageBuilder builder = new GridCoverageBuilder();
            builder.setGridCoverage(coverage);
            builder.setRenderedImage(image);
            builder.setSampleDimensions();
            coverage = builder.getGridCoverage2D();
            return coverage;
        }
    }

    private static RenderedImage recolor(final RenderedImage image, final Function function){

        RenderedImage recolorImage = image;
        if (function instanceof Categorize) {
            final Categorize categorize = (Categorize) function;
            recolorImage = (RenderedImage) categorize.evaluate(image);

        } else if(function instanceof Interpolate) {
            final Interpolate interpolate = (Interpolate) function;
            recolorImage = (RenderedImage) interpolate.evaluate(image);

        } else if(function instanceof Jenks) {
            final Jenks jenks = (Jenks) function;
            recolorImage = (RenderedImage) jenks.evaluate(image);
        }

        return recolorImage;

    }

    /**
     * Rescale image colors between datatype bounds (0, 255 for bytes or 0 655535 for short data). This solution will work
     * well only for byte or (u)short data models.
     * TODO : change for a less brut-force solution.
     * @param source The image to process.
     * @return The rescaled image.
     */
    private static RenderedImage equalize(final RenderedImage source) {
        final ColorModel srcModel = source.getColorModel();

        // Store min and max value for each band, along with the pixel where we've found the position.
        final double[] minMax = new Rescaler(
                Interpolation.create(PixelIteratorFactory.createRowMajorIterator(source), InterpolationCase.NEIGHBOR, 2), 0, 255).getMinMaxValue(null);

        // Compute transformation to apply on pixel values. We want to scale the pixel range in [0..255]
        final int numBands = srcModel.getNumComponents();
        double[] translation = new double[numBands];
        final double[] scale = new double[numBands];
        // Java 2D manage color scaling until ushort size. After that, current approach is totally wrong.
        for (int i = 0, j = 0; i < numBands; i++, j += 6) {
            final double min = 0, max;
            if (srcModel.getComponentSize(i) > 8) {
                max = 65532;
            } else {
                max = 255;
            }
            scale[i] = max / (minMax[j + 3] - minMax[j]);
            translation[i] = (min - minMax[j]) * scale[i];
        }

        /**
         * If just one of the bands is already scaled right, it might means that the entire image is right scaled, we
         * won't perform any operation.
         */
        boolean noOperationNeeded = false;
        final double scaleEpsilon = 0.05; // 5 % tolerence.
        final double translationTolerence = 1.0;
        for (int i = 0; i < scale.length; i++) {
            if ((scale[i] < 1.00 + scaleEpsilon && scale[i] > 1.00 - scaleEpsilon)
                    && (translation[i] < translationTolerence && translation[i] > -translationTolerence)) {
                noOperationNeeded = true;
                break;
            }
        }

        if (noOperationNeeded) {
            return source;
        } else {

            final WritableRenderedImage destination;
            if (source instanceof WritableRenderedImage) {
                destination = (WritableRenderedImage) source;
            } else {
                destination = BufferedImageUtilities.createImage(source.getWidth(), source.getHeight(), source);
            }

            final PixelIterator pxIt = PixelIteratorFactory.createRowMajorWriteableIterator(source, destination);
            int band = 0;
            while (pxIt.next()) {
                pxIt.setSampleDouble(pxIt.getSampleDouble() * scale[band] + translation[band]);
                if (++band >= numBands) {
                    band = 0;
                }
            }
            return destination;
        }
    }

    private static RenderedImage normalize(final RenderedImage source) {

        double[] mean = new double[] { 128.0,128.0,128.0 };
        double[] stDev = new double[] { 34.0,34.0,34.0 };
        float[][] CDFnorm = new float[3][];
        CDFnorm[0] = new float[256];
        CDFnorm[1] = new float[256];
        CDFnorm[2] = new float[256];

        double mu0 = mean[0];
        double mu1 = mean[1];
        double mu2 = mean[2];

        double twoSigmaSquared0 = 2.0*stDev[0]*stDev[0];
        double twoSigmaSquared1 = 2.0*stDev[1]*stDev[1];
        double twoSigmaSquared2 = 2.0*stDev[2]*stDev[2];

        CDFnorm[0][0] = (float)Math.exp(-mu0*mu0/twoSigmaSquared0);
        CDFnorm[1][0] = (float)Math.exp(-mu1*mu1/twoSigmaSquared1);
        CDFnorm[2][0] = (float)Math.exp(-mu2*mu2/twoSigmaSquared2);

        for ( int i = 1; i < 256; i++ ) {
            double deviation0 = i - mu0;
            double deviation1 = i - mu1;
            double deviation2 = i - mu2;
            CDFnorm[0][i] = CDFnorm[0][i-1] + (float)Math.exp(-deviation0*deviation0/twoSigmaSquared0);
            CDFnorm[1][i] = CDFnorm[1][i-1] + (float)Math.exp(-deviation1*deviation1/twoSigmaSquared1);
            CDFnorm[2][i] = CDFnorm[2][i-1] + (float)Math.exp(-deviation2*deviation2/twoSigmaSquared2);
        }

        for ( int i = 0; i < 256; i++ ) {
            CDFnorm[0][i] /= CDFnorm[0][255];
            CDFnorm[1][i] /= CDFnorm[1][255];
            CDFnorm[2][i] /= CDFnorm[2][255];
        }

        int[] bins = { 256 };
        double[] low = { 0.0D };
        double[] high = { 256.0D };

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(null);
        pb.add(1);
        pb.add(1);
        pb.add(bins);
        pb.add(low);
        pb.add(high);

        RenderedOp fmt = JAI.create("histogram", pb, null);

        return JAI.create("matchcdf", fmt, CDFnorm);
    }

    private static int[] getHistogram(final RenderedImage image) {
        // set up the histogram
        final int[] bins = { 256 };
        final double[] low = { 0.0D };
        final double[] high = { 256.0D };

        final ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(null);
        pb.add(1);
        pb.add(1);
        pb.add(bins);
        pb.add(low);
        pb.add(high);

        final RenderedOp op = JAI.create("histogram", pb, null);
        final Histogram histogram = (Histogram) op.getProperty("histogram");

        // get histogram contents
        final int[] local_array = new int[histogram.getNumBins(0)];
        for ( int i = 0; i < histogram.getNumBins(0); i++ ) {
            local_array[i] = histogram.getBinSize(0, i);
        }

        return local_array;
    }

    private static RenderedImage brigthen(final RenderedImage image,final int brightness) throws PortrayalException{
        final ColorModel model = image.getColorModel();

        if(model instanceof IndexColorModel){
            //no contrast enhance for indexed colormap
            return image;
        }else if(model instanceof ComponentColorModel){

            byte[][] lut = new byte[3][256];
            byte[][] newlut = new byte[3][256];

            // initialize lookup table
            for ( int i = 0; i < 256; i++ ) {
               lut[0][i] = (byte) i;
               lut[1][i] = (byte) i;
               lut[2][i] = (byte) i;
            }

            for (int i = 0; i < 256; i++ ) {
                int red   = (int)lut[0][i]&0xFF;
                int green = (int)lut[1][i]&0xFF;
                int blue  = (int)lut[2][i]&0xFF;
                newlut[0][i] = clamp(red   + brightness);
                newlut[1][i] = clamp(green + brightness);
                newlut[2][i] = clamp(blue  + brightness);
            }

            return colorize(image,newlut);

        }else{
            throw new PortrayalException("Unsupported image color model, found :" + model.getClass());
        }

    }

    private static byte clamp(final int v) {
        if ( v > 255 ) {
            return (byte)255;
        } else if ( v < 0 ) {
            return (byte)0;
        } else {
            return (byte)v;
        }
    }

    private static RenderedImage colorize(final RenderedImage image, final byte[][] lt) {
        LookupTableJAI lookup = new LookupTableJAI(lt);
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(lookup);
        return JAI.create("lookup", pb, null);
    }

    /**
     * Find the min or max values in an array of double
     * @param data double array
     * @param min search min values or max values
     * @return min or max value.
     */
    private static double findExtremum(final double[] data, final boolean min) {
        if (data.length > 0) {
            double extremum = data[0];
            if (min) {
                for (int i = 0; i < data.length; i++) {
                    extremum = Math.min(extremum, data[i]);
                }
            } else {
                for (int i = 0; i < data.length; i++) {
                    extremum = Math.max(extremum, data[i]);
                }
            }
            return extremum;
        }
        throw new IllegalArgumentException("Array of " + (min ? "min" : "max") + " values is empty.");
    }

}
