/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2015, Geomatys
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.Resource;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.filter.visitor.DefaultFilterVisitor;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Rescaler;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.metadata.MetadataUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.geotoolkit.processing.coverage.statistics.StatisticOp;
import org.geotoolkit.processing.coverage.statistics.Statistics;
import org.geotoolkit.storage.coverage.ImageStatistics;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import static org.geotoolkit.style.StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
import static org.geotoolkit.style.StyleConstants.DEFAULT_FALLBACK;
import org.geotoolkit.style.function.CompatibleColorModel;
import org.geotoolkit.style.function.DefaultInterpolationPoint;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.locationtech.jts.geom.Geometry;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.SelectedChannelType;
import org.opengis.util.FactoryException;
import org.opengis.util.LocalName;

/**
 * Symbolizer renderer adapted for Raster.
 *
 * @author Johann Sorel    (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @author Marechal remi   (Geomatys)
 */
public class RasterSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedRasterSymbolizer>{

    /**
     * Style factory object use to generate in some case to interpret raster with no associated style.
     *
     * @see #applyColorMapStyle(GridCoverageResource, GridCoverage, RasterSymbolizer)
     */
    public static final MutableStyleFactory SF = GO2Utilities.STYLE_FACTORY;
    public static final LocalName ALPHA_SAMPLE_DIM = Names.createLocalName("GO2", ":", "alpha");


    public RasterSymbolizerRenderer(final SymbolizerRendererService service, final CachedRasterSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean portray(final ProjectedCoverage projectedCoverage) throws PortrayalException {
        if (monitor.stopRequested()) return false;

        boolean dataRendered = false;
        try {
            final MapLayer coverageLayer = projectedCoverage.getLayer();
            final Resource resource = coverageLayer.getResource();
            if (!(resource instanceof GridCoverageResource)) {
                LOGGER.log(Level.WARNING, () -> String.format(
                        "Unsupported case: given layer [%s] has no compatible resource.%nExpected: %s%nBut got: %s",
                        coverageLayer.getName(), GridCoverageResource.class, resource == null? "null" : resource.getClass()
                ));
                return false;
            }

            final GridCoverageResource ref = (GridCoverageResource) resource;
            if (!isInView(ref)) return false;
            if (monitor.stopRequested()) return false;

            final RasterSymbolizer sourceSymbol = symbol.getSource();

            ////////////////////////////////////////////////////////////////////
            // 2 - Select bands to style / display                            //
            ////////////////////////////////////////////////////////////////////

            //band select ----------------------------------------------------------
            //works as a JAI operation
            final ChannelSelection selections = sourceSymbol.getChannelSelection();
            final List<SampleDimension> sampleDimensions = ref.getSampleDimensions();
            final int[] channelSelection;
            //we can change sample dimension only if we have more then one available.
            if (selections != null && (sampleDimensions == null || sampleDimensions.size() > 1)) {
                final SelectedChannelType channel = selections.getGrayChannel();

                final SelectedChannelType[] channels = channel != null?
                        new SelectedChannelType[]{channel} : selections.getRGBChannels();
                if (channels != null && channels.length > 0) {
                    channelSelection = new int[channels.length];
                    for (int i = 0 ; i < channels.length ; i++) {
                        channelSelection[i] = getBandIndice(channels[i].getChannelName(), sampleDimensions);
                    }
                } else channelSelection = null;
            } else {
                channelSelection = null;
            }

            if (monitor.stopRequested()) return false;
            final GridCoverage dataCoverage = getObjectiveCoverage(projectedCoverage, renderingContext.getGridGeometry(), false, channelSelection);
            if (dataCoverage == null) {
                //LOGGER.log(Level.WARNING, "RasterSymbolizer : Reprojected coverage is null.");
                return false;
            }
            if (monitor.stopRequested()) return false;

            /*
             * If we haven't got any reprojection we delegate affine transformation to java2D
             * we must switch to objectiveCRS for grid coverage
             */
            renderingContext.switchToObjectiveCRS();

            ////////////////////////////////////////////////////////////////////
            // 4 - Apply style                                                //
            ////////////////////////////////////////////////////////////////////

            final GridCoverage2D dataImage = applyStyle(ref, dataCoverage, sourceSymbol);
            final MathTransform trs2D = dataCoverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
            if (monitor.stopRequested()) return false;

            ////////////////////////////////////////////////////////////////////
            // 5 - Correct cross meridian problems / render                   //
            ////////////////////////////////////////////////////////////////////

            if (renderingContext.wraps == null) {
                //single rendering
                dataRendered |= renderCoverage(projectedCoverage, dataImage, trs2D);

            } else {
                //check if the geometry overlaps the meridian
                int nbIncRep = renderingContext.wraps.wrapIncNb;
                int nbDecRep = renderingContext.wraps.wrapDecNb;
                final Geometry objBounds = JTS.toGeometry(dataCoverage.getGridGeometry().getEnvelope());

                // geometry cross the far east meridian, geometry is like :
                // POLYGON(-179,10,  181,10,  181,-10,  179,-10)
                if (objBounds.intersects(renderingContext.wraps.wrapIncLine)) {
                    //duplicate geometry on the other warp line
                    nbDecRep++;
                }
                // geometry cross the far west meridian, geometry is like :
                // POLYGON(-179,10, -181,10, -181,-10,  -179,-10)
                else if (objBounds.intersects(renderingContext.wraps.wrapDecLine)) {
                    //duplicate geometry on the other warp line
                    nbIncRep++;
                }
                dataRendered |= renderCoverage(projectedCoverage, dataImage, trs2D);

                //-- repetition of increasing and decreasing sides.
                for (int i = 0; i < nbDecRep; i++) {
                    g2d.setTransform(renderingContext.wraps.wrapDecObjToDisp[i]);
                    dataRendered |= renderCoverage(projectedCoverage, dataImage, trs2D);
                }
                for (int i = 0; i < nbIncRep; i++) {
                    g2d.setTransform(renderingContext.wraps.wrapIncObjToDisp[i]);
                    dataRendered |= renderCoverage(projectedCoverage, dataImage, trs2D);
                }
            }

            renderingContext.switchToDisplayCRS();
        } catch (NoSuchDataException e) {
            LOGGER.log(Level.FINE,"Disjoint exception: "+e.getMessage(),e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,"Portrayal exception: "+e.getMessage(),e);
        }
        return dataRendered;
    }

    /**
     * Analyse input coverage to know if we need to add an alpha channel. Alpha channel is required in photographic
     * coverage case, in order for the resample to deliver a ready to style image.
     *
     * @param source The coverage to analyse.
     * @param symbolizer contain color Map.
     * @return The same coverage as input if style do not require an ARGB data to properly render, or a new ARGB coverage
     * computed from source data.
     */
    @Override
    protected final GridCoverage prepareCoverageToResampling(final GridCoverage source, final CachedRasterSymbolizer symbolizer) {
        final ColorMap cMap = symbolizer.getSource().getColorMap();
        if (cMap != null && cMap.getFunction() != null) {
            return source; // Coloration is handled externally
        }

        final List<SampleDimension> sds = source.getSampleDimensions();
        if (sds == null || sds.size() != 3) {
            return source;
        }

        if (source != source.forConvertedValues(false)) {
            return source;
        }

        for (SampleDimension sd : sds) {
            if (sd.getTransferFunction().isPresent())
                return source;
        }

        // At this point, no geophysic information has been found. We will consider input coverage as a colorized image.
        return new ForcedAlpha(source);
    }

    /**
     * Apply style on current coverage.<br><br>
     *
     * Style application follow way given by
     * <a href="http://portal.opengeospatial.org/files/?artifact_id=16700">OpenGIS_Symbology_Encoding_Implementation_Specification</a> sheet 32.
     *
     * @param ref needed to compute statistics from internal metadata in case where missing informations.
     * @param coverage current styled coverage.
     * @param styleElement the {@link RasterSymbolizer} which contain styles properties.
     * @return styled coverage representation.
     * @throws ProcessException if problem during apply Color map or shaded relief styles.
     * @throws FactoryException if problem during apply shaded relief style.
     * @throws TransformException if problem during apply shaded relief style.
     * @throws PortrayalException if problem during apply contrast enhancement style.
     * @throws java.io.IOException if problem during style application
     * @see #applyColorMapStyle(CoverageReference, org.geotoolkit.coverage.grid.GridCoverage2D, org.opengis.style.RasterSymbolizer)
     * @see #applyContrastEnhancement(java.awt.image.RenderedImage, org.opengis.style.RasterSymbolizer)
     */
    public static GridCoverage2D applyStyle(GridCoverageResource ref, GridCoverage coverage,
            final RasterSymbolizer styleElement)
            throws ProcessException, FactoryException, TransformException, PortrayalException, IOException
             {

        RenderedImage image = applyColorMapStyle(ref, coverage, styleElement);
        image = applyContrastEnhancement(image, styleElement);

        //generate a new coverage with colored sample dimensions
        final int numBands = image.getSampleModel().getNumBands();
        final List<SampleDimension> sampleDimensions = new ArrayList<>(numBands);
        for (int i=0;i<numBands;i++) sampleDimensions.add(new SampleDimension.Builder().setName(i).build());
        return new GridCoverage2D(coverage.getGridGeometry(), sampleDimensions, image);
    }

    /**
     * Returns contrast enhancement modified image.
     *
     * @param image worked image.
     * @param styleElement the {@link RasterSymbolizer} which contain contrast enhancement properties.
     * @return contrast enhancement modified image.
     * @throws PortrayalException if problem during gamma value application
     * @see #brigthen(java.awt.image.RenderedImage, int)
     */
    private static RenderedImage applyContrastEnhancement(RenderedImage image, final RasterSymbolizer styleElement)
            throws PortrayalException {
        ensureNonNull("image", image);
        ensureNonNull("styleElement", styleElement);
         //-- contrast enhancement -------------------
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
     * Apply {@linkplain RasterSymbolizer#getColorMap() color map style properties} on current coverage if need.<br><br>
     *
     * In case where no {@linkplain ColorMap#getFunction() sample to geophysic}
     * transformation function is available and coverage is define as {@link ViewType#GEOPHYSICS}
     * a way is find to avoid empty result, like follow : <br>
     * The first band from {@linkplain GridCoverage2D#getRenderedImage() coverage image} is selected
     * and a grayscale color model is apply from {@linkplain ImageStatistics computed image statistic}.
     *
     * @param ref needed to compute statistics from internal metadata in case where missing informations.
     * @param coverage color map style apply on this object.
     * @param styleElement the {@link RasterSymbolizer} which contain color map properties.
     * @return image which is the coverage exprimate into {@link ViewType#PHOTOGRAPHIC}.
     * @throws ProcessException if problem during statistic problem.
     */
    private static RenderedImage applyColorMapStyle(final GridCoverageResource ref,
            GridCoverage coverage,final RasterSymbolizer styleElement) throws ProcessException, IOException, PortrayalException {
        ensureNonNull("CoverageReference", ref);
        ensureNonNull("coverage", coverage);
        ensureNonNull("styleElement", styleElement);

        RenderedImage resultImage;

        //Recolor coverage -----------------------------------------------------
        final ColorMap recolor = styleElement.getColorMap();
        recolorCase:
        if (recolor == null || recolor.getFunction() == null) {

            resultImage = coverage.forConvertedValues(false).render(null);
            final SampleModel sampleMod = resultImage.getSampleModel();
            final ColorModel riColorModel = resultImage.getColorModel();

            /**
             * Break computing statistic if indexcolormodel is already adapted for java 2d interpretation
             * (which mean index color model with positive colormap array index -> DataBuffer.TYPE_BYTE || DataBuffer.TYPE_USHORT)
             * or if image has already 3 or 4 bands Byte typed.
             */
            if (!defaultStyleIsNeeded(sampleMod, riColorModel)) {
                break recolorCase;
            }

            //if there is no geophysic, the same coverage is returned
            coverage = coverage.forConvertedValues(true);
            CoverageDescription covRefMetadata = null;

            if (covRefMetadata == null) {
                final Metadata metadata;
                try {
                    metadata = ref.getMetadata();
                } catch (DataStoreException ex) {
                    throw new IOException("Cannot fetch metadata from input resource.", ex);
                }

                covRefMetadata = MetadataUtilities.extractCoverageDescription(metadata)
                    .findFirst()
                    .orElse(null);
            }

            ImageStatistics analyse = null;
            if (covRefMetadata != null) {
                analyse = ImageStatistics.transform(covRefMetadata);
                if (analyse != null) {
                    // Ensure band statistics are valid.
                    for (ImageStatistics.Band b : analyse.getBands()) {
                        if (b.getMax() == null || b.getMin() == null || b.getMax() - b.getMin() < 1e-11) {
                            analyse = null;
                            break;
                        }
                    }
                }
            }

            // TODO : we should analyze a subset of the entire image instead, to
            // ensure consistency over tiled rendering (cf. OpenLayer/WMS).
            if (analyse == null)
                analyse = Statistics.analyse(coverage.render(null), true);

            final Optional<MutableStyle> styleFromStats = GO2Utilities.inferStyle(analyse, riColorModel.hasAlpha());
            if (styleFromStats.isPresent()) {
                final MapContext subCtx = MapBuilder.createContext();
                subCtx.items().add(MapBuilder.createCoverageLayer(coverage, styleFromStats.get()));
                final Dimension renderingDimension = new Dimension(resultImage.getWidth(), resultImage.getHeight());
                resultImage = DefaultPortrayalService.portray(subCtx, coverage.getGridGeometry().getEnvelope(), renderingDimension, false);
            }

        } else {
            //color map is applied on geophysics view
            //if there is no geophysic, the same coverage is returned
            coverage = coverage.forConvertedValues(true);
            resultImage = recolor.getFunction().evaluate(coverage.render(null), RenderedImage.class);
        }

        assert resultImage != null : "applyColorMapStyle : image can't be null.";
        return resultImage;
    }

    /**
     * Returns {@code true} if a default style is needed to interpret current data
     * else {@code false} if java 2d will be able to interprete data.
     *
     * @return {@code true} if a style creation is needed to show image datas else {@code false}.
     */
    private static boolean defaultStyleIsNeeded(final SampleModel sampleModel, final ColorModel colorModel) {
        ensureNonNull("sampleModel", sampleModel);
        ensureNonNull("colorModel",  colorModel);

        final int[] pixelSampleSize = colorModel.getComponentSize();
        if (pixelSampleSize == null) return true;

        assert pixelSampleSize != null;
        final int sampleSize = pixelSampleSize[0];

        if (pixelSampleSize.length > 1) {
            for (int s = 1; s < pixelSampleSize.length; s++) {
                if (pixelSampleSize[s] != sampleSize) return false; //-- special case different samplesize.
            }
        }

        if (pixelSampleSize.length == 2) return true; //-- special case where we select first band.

        final int dataBufferType = sampleModel.getDataType();

        //-- one band
        if (pixelSampleSize.length == 1) {
            if (!(colorModel instanceof IndexColorModel)) return true;
            //-- ! IndexColorModel + Byte or UShort case
            return (!(sampleSize == 8 || (sampleSize == 16 && dataBufferType == DataBuffer.TYPE_USHORT)));
        }

        assert pixelSampleSize.length > 2;

        //-- is RGB or ARGB Byte
        return sampleSize != 8;
    }

    private static int getBandIndice(final String name, final List<SampleDimension> dims) throws PortrayalException{
        try{
            return Integer.parseInt(name);
        }catch(NumberFormatException ex){
            //can be a name
            if (dims != null) {
                for (int i = 0, n = dims.size(); i < n; i++) {
                    final SampleDimension sampleDim = dims.get(i);
                    if (Objects.equals(String.valueOf(sampleDim.getName()), n)) {
                        return i;
                    }
                }
            }
        }

        throw new PortrayalException("Band for name/indice "+name+" not found");
    }

    private boolean renderCoverage(final ProjectedCoverage projectedCoverage, org.apache.sis.coverage.grid.GridCoverage2D coverage, MathTransform trs2D) throws PortrayalException{
        if (monitor.stopRequested()) return false;
        boolean dataRendered = false;

        RenderedImage img = coverage.render(null);

        /*
         * Try to prefetch image before rendering
         * resampled image or mosaic have deferred tiles
         * java2d render tiles one by one which can be slow when working with
         * computed coverages or distant services like WMTS or TMS
         */
        if ( (img.getWidth() * img.getHeight()) < 5000*5000) {
            ImageProcessor processor = new ImageProcessor();
            processor.setExecutionMode(ImageProcessor.Mode.PARALLEL);
            img = processor.prefetch(img);
        }

        if (monitor.stopRequested()) return false;

        final InterpolationCase interpolationCase = (InterpolationCase) hints.get(GO2Hints.KEY_INTERPOLATION);
        if (interpolationCase != null) {
            switch (interpolationCase) {
                case NEIGHBOR :
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    break;
                case BILINEAR :
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    break;
                case BICUBIC :
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    break;
                default :
                    //resample image ourself
                    try {
                        GridCoverage cov = new ResampleProcess(coverage, renderingContext.getGridGeometry().getCoordinateReferenceSystem(), renderingContext.getGridGeometry2D(), interpolationCase, null).executeNow();
                        trs2D = cov.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
                        if (monitor.stopRequested()) return false;
                        img = cov.render(null);
                    } catch (ProcessException ex) {
                        throw new PortrayalException(ex);
                    }
                    break;
            }
        }

        if (trs2D instanceof AffineTransform) {
            g2d.setComposite(symbol.getJ2DComposite());
            try {
                g2d.drawRenderedImage(img, (AffineTransform)trs2D);
                dataRendered = true;
            } catch (Exception ex) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                LOGGER.log(Level.WARNING, sw.toString());//-- more explicite way to debug

                if(ex instanceof ArrayIndexOutOfBoundsException){

                    //we can recover when it's an inapropriate componentcolormodel
                    final StackTraceElement[] eles = ex.getStackTrace();
                    if(eles.length > 0 && ComponentColorModel.class.getName().equalsIgnoreCase(eles[0].getClassName())){

                        final Resource resource = projectedCoverage.getLayer().getResource();
                        if (resource instanceof GridCoverageResource) {
                            try {
                                final GridCoverageResource reader = (GridCoverageResource) resource;
                                final Map<String,Object> analyze = StatisticOp.analyze(reader.read(null).render(null));
                                final double[] minArray = (double[])analyze.get(StatisticOp.MINIMUM);
                                final double[] maxArray = (double[])analyze.get(StatisticOp.MAXIMUM);
                                final double min = findExtremum(minArray, true);
                                final double max = findExtremum(maxArray, false);

                                final List<InterpolationPoint> values = new ArrayList<>();
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
                                dataRendered = true;
                            } catch(Exception e) {
                                //plenty of errors can happen when painting an image
                                monitor.exceptionOccured(e, Level.WARNING);

                                //raise the original error
                                monitor.exceptionOccured(ex, Level.WARNING);
                            }
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
            if (monitor.stopRequested()) return false;
            dataRendered |= GO2Utilities.portray(projectedCoverage, outline, renderingContext);
        }
        return dataRendered;
    }

    /**
     * Fix portrayal resolutions on CoverageMapLayer bounds CRS horizontal part dimensions.
     *
     * @param resolution default resolution
     * @param coverageCRS CoverageMapLayer CRS
     * @return fixed resolutions or input resolution if coverageCRS is null.
     */
    public static  double[] fixResolutionWithCRS(final double[] resolution, final CoordinateReferenceSystem coverageCRS) {
        assert resolution.length == 2; //-- resolution from rendering context (2D space)
        if (coverageCRS == null) return resolution;
        final int minOrdi      = CRSUtilities.firstHorizontalAxis(coverageCRS);
        final double[] tempRes = new double[coverageCRS.getCoordinateSystem().getDimension()];
        Arrays.fill(tempRes, 1);
        tempRes[minOrdi]     = resolution[0];
        tempRes[minOrdi + 1] = resolution[1];
        return tempRes;
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
            for (int i = 0; i < coverageCRS.getCoordinateSystem().getDimension(); i++) {
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
    public static Map<String, Double> extractQuery(final MapLayer coverageMapLayer) {

        Map<String,Double> values = null;
        if (coverageMapLayer instanceof CoverageMapLayer) {
            final CoverageMapLayer covMapLayer = (CoverageMapLayer) coverageMapLayer;
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
                values = (Map<String,Double>) filter.accept(fv, new HashMap<>());
            }
        }
        return values;
    }

    ////////////////////////////////////////////////////////////////////////////
    // RenderedImage JAI image operations ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

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
                Interpolation.create(new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(source), InterpolationCase.NEIGHBOR, 2), 0, 255).getMinMaxValue(null);

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
                destination = BufferedImages.createImage(source.getWidth(), source.getHeight(), source);
            }

            final PixelIterator pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(source);
            final WritablePixelIterator wIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).createWritable(destination);

            while (pxIt.next()) {
                wIt.next();
                for (int band = 0; band < numBands; band++) {
                    wIt.setSample(band, pxIt.getSampleDouble(band) * scale[band] + translation[band]);
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

    //-- indice de confiance 3/10 (cf:johann)
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

    private static ColorMap create(Color[] palette, final double[] noDataValues, final double minValue, final double maxValue) {
        final List<InterpolationPoint> values = new ArrayList<>();
        if (noDataValues != null)
            for (double nodata : noDataValues) {
                values.add(SF.interpolationPoint(nodata, SF.literal(new Color(0, 0, 0, 0))));
            }

        values.add(SF.interpolationPoint(Float.NaN, SF.literal(new Color(0, 0, 0, 0))));

        //-- Color palette
        assert palette.length >= 2;
        values.add(SF.interpolationPoint(minValue, SF.literal(palette[0])));

        if (palette.length > 2) {
            final int numSteps = palette.length-2;
            final double step = maxValue - minValue / (numSteps);
            double currentValue = minValue;
            for (int i = 1 ; i <= numSteps ; i++) {
                currentValue += step;
                values.add(SF.interpolationPoint(currentValue, SF.literal(palette[i])));
            }
        }

        values.add(SF.interpolationPoint(maxValue, SF.literal(palette[palette.length - 1])));

        final Function function = SF.interpolateFunction(DEFAULT_CATEGORIZE_LOOKUP, values, Method.COLOR, Mode.LINEAR, DEFAULT_FALLBACK);
        return GO2Utilities.STYLE_FACTORY.colorMap(function);
    }


    private static List<SampleDimension> addAlphaDimension(final List<SampleDimension> source) {
        if (source == null || source.isEmpty()) {
            throw new IllegalArgumentException("Source sample dimension list must not be empty.");
        }
        final ArrayList<SampleDimension> newSamples = new ArrayList<>(source);
        newSamples.add(new SampleDimension(ALPHA_SAMPLE_DIM, 0, Collections.EMPTY_SET));
        return newSamples;
    }


    private boolean isInView(final GridCoverageResource candidate) throws DataStoreException {
        final GridGeometry gridGeometry = candidate.getGridGeometry();
        if (gridGeometry.isDefined(GridGeometry.ENVELOPE)) {
            try {
                Envelope bounds = gridGeometry.getEnvelope();
                GeneralEnvelope boundary = GeneralEnvelope.castOrCopy(
                        Envelopes.transform(bounds, renderingContext.getObjectiveCRS2D()));
                if (boundary.isEmpty()) {
                    //we may have NaN values with envelopes which cross poles
                    //normalizing envelope before transform often solve this issue
                    bounds = new GeneralEnvelope(bounds);
                    ((GeneralEnvelope) bounds).normalize();
                    boundary = GeneralEnvelope.castOrCopy(
                        Envelopes.transform(bounds, renderingContext.getObjectiveCRS2D()));
                }

                return boundary.intersects(renderingContext.getCanvasObjectiveBounds2D());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Cannot compare layer bbox with rendering context", e);
            }
        }

        // Cannot determine intersection. Display object.
        return true;
    }

    private static class ForcedAlpha extends GridCoverage {

        private final GridCoverage source;
        private BufferedImage entireAlpha;

        protected ForcedAlpha(final GridCoverage source) {
            super(source.getGridGeometry(), addAlphaDimension(source.getSampleDimensions()));
            this.source = source;
        }

        @Override
        public GridCoverage forConvertedValues(boolean converted) {
            return this; // Only support non-geophysic coverages
        }

        @Override
        public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
            synchronized (this) {
                if (entireAlpha != null) {
                    if (sliceExtent == null) {
                        return entireAlpha;
                    }

                    return CoverageUtilities.subgrid(entireAlpha, sliceExtent);
                }

                if (sliceExtent == null) {
                    entireAlpha = addAlpha(source.render(null));
                    return entireAlpha;
                }
            }

            return addAlpha(source.render(sliceExtent));
        }

        private BufferedImage addAlpha(final RenderedImage img) {
            // TODO: find a more optimized way.
            final BufferedImage buffer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            buffer.createGraphics().drawRenderedImage(img, new AffineTransform());
            return buffer;
        }
    }
}
