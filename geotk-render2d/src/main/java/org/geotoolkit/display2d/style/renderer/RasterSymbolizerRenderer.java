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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.RenderedOp;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.image.privy.TiledImage;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.map.ExceptionPresentation;
import org.apache.sis.map.Presentation;
import org.apache.sis.map.coverage.RenderingWorkaround;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.Resource;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.presentation.RasterPresentation;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.filter.coverage.CoverageFilterFactory;
import org.geotoolkit.filter.coverage.FilteredCoverageQuery;
import org.geotoolkit.filter.visitor.DefaultFilterVisitor;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.GeometricUtilities.WrapResolution;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Rescaler;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.metadata.MetadataUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.statistics.Statistics;
import org.geotoolkit.storage.coverage.BandedCoverageResource;
import org.geotoolkit.storage.coverage.ImageStatistics;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.Literal;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
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
     * @return styled coverage representation, can be null.
     * @throws ProcessException if problem during apply Color map or shaded relief styles.
     * @throws FactoryException if problem during apply shaded relief style.
     * @throws TransformException if problem during apply shaded relief style.
     * @throws PortrayalException if problem during apply contrast enhancement style.
     * @throws java.io.IOException if problem during style application
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
        GridCoverage2D result = new GridCoverage2D(coverage.getGridGeometry(), sampleDimensions, image);

        //Apply geometry mask
        Expression<Feature, ?> expression = styleElement.getGeometry();
        if (expression != null) {
            //create a boundary feature
            Envelope env = coverage.getEnvelope().get();
            final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(env.getCoordinateReferenceSystem());
            env = Envelopes.transform(env, crs2d);
            final GeometryFactory gf = new GeometryFactory(new PrecisionModel(result.getGridGeometry().getResolution(true)[0]));
            final Geometry polygon = GeometricUtilities.toJTSGeometry(env, WrapResolution.NONE, gf);
            polygon.setUserData(crs2d);

            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("boundary");
            ftb.addAttribute(Geometry.class).setName(AttributeConvention.GEOMETRY_PROPERTY).setCRS(crs2d).addRole(AttributeRole.DEFAULT_GEOMETRY);
            final FeatureType featureType = ftb.build();
            Feature feature = featureType.newInstance();
            feature.setPropertyValue(AttributeConvention.GEOMETRY, polygon);

            Object geomValue = expression.apply(feature);

            if (geomValue instanceof Geometry) {
                final Geometry geom = (Geometry) geomValue;
                if (geom.isEmpty()) {
                    return null;
                }
                final FilteredCoverageQuery fcq = new FilteredCoverageQuery();
                final CoverageFilterFactory cff = CoverageFilterFactory.DEFAULT;
                fcq.filter(cff.intersects(cff.property("*"), cff.literal(geom)));
                try {
                    final GridCoverageResource gcrr = fcq.execute(new InMemoryGridCoverageResource(result));
                    result = (GridCoverage2D) gcrr.read(null);
                } catch (DataStoreException ex) {
                    throw new PortrayalException(ex.getMessage(), ex);
                }
            } else {
                //use 'else' for backward compatibility, like field names on database raster attributes
                //no intersection
                LOGGER.log(Level.FINE, "Encounter value {0} as raster symbolizer geometry expression, value is not a Geometry, it will be ignored for backward compatibility.", geomValue);
            }
        }

        return result;
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
            final Double gamma = doubleValue(ce.getGammaValue());
            if (gamma != null && gamma != 1) {
                //Specification : page 35
                // A “GammaValue” tells how much to brighten (values greater than 1.0) or dim (values less than 1.0) an image.
                image = brigthen(image, (int) ((gamma - 1) * 255f));
            }
        }
        return image;
    }

    private static Double doubleValue(final Expression e) {
        final Number v = (Number) e.apply(null);
        return (v != null) ? v.doubleValue() : null;
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
            if (riColorModel != null && !defaultStyleIsNeeded(sampleMod, riColorModel)) {
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
            if (analyse == null) {
                analyse = Statistics.analyse(coverage.render(null), true);
            }

            final Optional<MutableStyle> styleFromStats = GO2Utilities.inferStyle(analyse, (riColorModel== null) ? true : riColorModel.hasAlpha());
            if (styleFromStats.isPresent()) {
                /* WARNING: That's neither optimal nor stable. However, do not know any other way to override style on
                 * the fly.
                 *
                 * !!! IMPORTANT !!!
                 * The canvas here is created with the geometry of input coverage, because otherwise, we would apply
                 * two times the affine transform to display system.
                 */
                final MapLayers subCtx = MapBuilder.createContext();
                subCtx.getComponents().add(MapBuilder.createCoverageLayer(coverage, styleFromStats.get()));
                resultImage = DefaultPortrayalService.portray(
                        new CanvasDef(coverage.getGridGeometry()),
                        new SceneDef(subCtx)
                );
            }

        } else {
            //color map is applied on geophysics view
            //if there is no geophysic, the same coverage is returned
            coverage = coverage.forConvertedValues(true);
            resultImage = (RenderedImage) recolor.getFunction().apply(coverage.render(null));
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
     * @param coverageMapLayer MapLayer
     * @return a Map</String,Double> with query parameters or null
     */
    public static Map<String, Double> extractQuery(final MapLayer coverageMapLayer) {

        Map<String,Double> values = null;
        final Query query = coverageMapLayer.getQuery();
        if (query instanceof FeatureQuery) {
            FeatureQuery sq = (FeatureQuery) query;
            // visit the filter to extract all values
            final DefaultFilterVisitor fv = new DefaultFilterVisitor() {
                {
                    setFilterHandler(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO, (f, data) -> {
                        final BinaryComparisonOperator<Object> filter = (BinaryComparisonOperator) f;
                        final Map<String,Double> map = (Map<String,Double>) data;
                        final String expr1 = ((ValueReference)filter.getOperand1()).getXPath();
                        final Double expr2 = Double.valueOf(((Literal)filter.getOperand2()).getValue().toString());
                        map.put(expr1, expr2);
                    });
                }
            };
            final Filter filter = sq.getSelection();
            values = new HashMap<>();
            fv.visit(filter, values);
        }
        return values;
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Resource rs) {

        if (rs instanceof BandedCoverageResource) {
            BandedCoverageResource bcr = (BandedCoverageResource) rs;
            try {
                GridCoverage coverage = BandedCoverageResource.sample(bcr, renderingContext.getGridGeometry2D());
                rs = new InMemoryGridCoverageResource(rs.getIdentifier().orElse(null), coverage);
            } catch (DataStoreException ex) {
                ExceptionPresentation ep = new ExceptionPresentation(ex);
                ep.setLayer(layer);
                ep.setResource(rs);
                return Stream.of(ep);
            }
        }

        if (rs instanceof GridCoverageResource) {
            GridCoverageResource ref = (GridCoverageResource) rs;

            try {
                final RasterSymbolizer sourceSymbol = symbol.getSource();
                final int[] channelSelection = channelSelection(sourceSymbol, ref);

                final GridCoverage dataCoverage = getObjectiveCoverage(ref, renderingContext.getGridGeometry(), false, channelSelection);
                if (dataCoverage == null) {
                    return Stream.empty();
                }

                final GridCoverage2D dataImage = applyStyle(ref, dataCoverage, sourceSymbol);
                if (dataImage == null) return Stream.empty();
                final RasterPresentation rasterPresentation = new RasterPresentation(layer, layer.getData(), dataImage);
                rasterPresentation.forGrid(renderingContext);

                return Stream.concat(Stream.of(rasterPresentation), outline(layer, dataImage.getGridGeometry()));

            } catch (NoSuchDataException | DisjointExtentException e) {
                LOGGER.log(Level.FINE,"Disjoint exception: "+e.getMessage(),e);
            } catch (Exception e) {
                ExceptionPresentation ep = new ExceptionPresentation(e);
                ep.setLayer(layer);
                ep.setResource(rs);
                return Stream.of(ep);
            }
        } else {
            return super.presentations(layer, rs);
        }

        return Stream.empty();
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Feature feature) {

        final RasterSymbolizer sourceSymbol = symbol.getSource();
        final Object candidate = GO2Utilities.evaluate(sourceSymbol.getGeometry(), feature, null, null);

        GridCoverageResource ref = null;
        if (candidate instanceof GridCoverageResource) {
            ref = (GridCoverageResource) candidate;
        } else if (candidate instanceof GridCoverage) {
            ref = new InMemoryGridCoverageResource((GridCoverage) candidate);
        } else {
            return Stream.empty();
        }

        try {
            final int[] channelSelection = channelSelection(sourceSymbol, ref);

            final GridCoverage dataCoverage = getObjectiveCoverage(ref, renderingContext.getGridGeometry(), false, channelSelection);
            if (dataCoverage == null) {
                return Stream.empty();
            }

            final GridCoverage2D dataImage = applyStyle(ref, dataCoverage, sourceSymbol);
            if (dataImage == null) return Stream.empty();
            final RasterPresentation rasterPresentation = new RasterPresentation(layer, ref, dataImage);
            rasterPresentation.forGrid(renderingContext);
            rasterPresentation.setCandidate(feature);

            return Stream.concat(Stream.of(rasterPresentation), outline(layer, dataImage.getGridGeometry()));
        } catch (NoSuchDataException | DisjointExtentException e) {
            LOGGER.log(Level.FINE,"Disjoint exception: "+e.getMessage(),e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,"Portrayal exception: "+e.getMessage(),e);
        }
        return Stream.empty();
    }

    private int[] channelSelection(RasterSymbolizer sourceSymbol, GridCoverageResource ref) throws PortrayalException, DataStoreException {
        final ChannelSelection selections = sourceSymbol.getChannelSelection();
        final int[] channelSelection;
        //we can change sample dimension only if we have more then one available.
        if (selections != null) {
            //delay sample dimension reading until we really need it, it may be expensive
            final List<SampleDimension> sampleDimensions = ref.getSampleDimensions();
            if (sampleDimensions == null || sampleDimensions.size() > 1) {
                final SelectedChannelType channel = selections.getGrayChannel();

                final SelectedChannelType[] channels = channel != null?
                        new SelectedChannelType[]{channel} : selections.getRGBChannels();
                if (channels != null && channels.length > 0) {
                    channelSelection = new int[channels.length];
                    for (int i = 0 ; i < channels.length ; i++) {
                        channelSelection[i] = getBandIndice(channels[i].getChannelName(), sampleDimensions);
                    }
                } else {
                    channelSelection = null;
                }
            } else {
                channelSelection = null;
            }
        } else {
            channelSelection = null;
        }
        return channelSelection;
    }

    private Stream<Presentation> outline(MapLayer layer, GridGeometry gridGeom) throws PortrayalException {
        if (symbol.getOutLine() != null) {
            final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(gridGeom.getCoordinateReferenceSystem());
            final Geometry geom = GeometricUtilities.toJTSGeometry(gridGeom.getEnvelope(), GeometricUtilities.WrapResolution.NONE);
            geom.setUserData(crs2d);

            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("border");
            ftb.addAttribute(Geometry.class).setName("geom").setCRS(crs2d).addRole(AttributeRole.DEFAULT_GEOMETRY);
            ftb.addAttribute(Integer.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
            final FeatureType type = ftb.build();
            final Feature feature = type.newInstance();
            feature.setPropertyValue("id", 0);
            feature.setPropertyValue("geom", geom);

            final SymbolizerRendererService service = GO2Utilities.findRenderer(symbol.getOutLine());
            final SymbolizerRenderer renderer = service.createRenderer(symbol, renderingContext);
            return renderer.presentations(layer, feature);
        }
        return Stream.empty();
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
                GeneralEnvelope bounds = new GeneralEnvelope(gridGeometry.getEnvelope());
                GeneralEnvelope boundary = GeneralEnvelope.castOrCopy(
                        Envelopes.transform(bounds, renderingContext.getObjectiveCRS2D()));
                if (boundary.isEmpty()) {
                    //we may have NaN values with envelopes which cross poles
                    //normalizing envelope before transform often solve this issue
                    bounds.normalize();
                    boundary = GeneralEnvelope.castOrCopy(
                        Envelopes.transform(bounds, renderingContext.getObjectiveCRS2D()));
                }
                // Ensure both envelopes are expressed in the same convention, because CRS transforms do not take care of it.
                boundary.normalize();
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
            return addAlpha(source.render(sliceExtent));
        }

        private RenderedImage addAlpha(final RenderedImage img) {
            // TODO: find a more optimized way.
            final BufferedImage buffer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = buffer.createGraphics();
            g.drawRenderedImage(RenderingWorkaround.wrap(img), new AffineTransform(1,0,0,1,-img.getMinX(), -img.getMinY()));
            g.dispose();
            final WritableRaster r = buffer.getRaster().createWritableTranslatedChild(img.getMinX(), img.getMinY());
            return new TiledImage(null, buffer.getColorModel(), img.getWidth(), img.getHeight(), img.getMinX(), img.getMinY(), r);
        }
    }
}
