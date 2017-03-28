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

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.*;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.filter.visitor.DefaultFilterVisitor;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.Rescaler;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.shadedrelief.ShadedReliefDescriptor;
import org.geotoolkit.processing.image.bandselect.BandSelectDescriptor;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
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
import org.geotoolkit.image.BufferedImages;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsEqualTo;
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
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.palette.PaletteFactory;
import org.geotoolkit.internal.jdk8.JDK8;
import org.geotoolkit.metadata.ImageStatistics;
import org.geotoolkit.processing.coverage.statistics.StatisticOp;
import org.geotoolkit.processing.coverage.statistics.Statistics;
import org.geotoolkit.processing.image.dynamicrange.DynamicRangeStretchProcess;
import org.geotoolkit.style.MutableStyleFactory;
import static org.geotoolkit.style.StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
import static org.geotoolkit.style.StyleConstants.DEFAULT_FALLBACK;
import org.geotoolkit.style.function.DefaultInterpolate;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.metadata.content.CoverageDescription;

/**
 * Symbolizer renderer adapted for Raster.
 *
 * @author Johann Sorel    (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @author Marechal remi   (Geomatys)
 */
public class DefaultRasterSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedRasterSymbolizer>{



    /**
     * Style factory object use to generate in some case to interpret raster with no associated style.
     *
     * @see #applyColorMapStyle(CoverageReference, GridCoverage2D, RasterSymbolizer)
     */
    public static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
            new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));



    public DefaultRasterSymbolizerRenderer(final SymbolizerRendererService service, final CachedRasterSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException {

        try {
            GridCoverage2D dataCoverage = getObjectiveCoverage(projectedCoverage);
            GridCoverage2D elevationCoverage = getObjectiveElevationCoverage(projectedCoverage);
            final CoverageMapLayer coverageLayer = projectedCoverage.getLayer();
            final CoverageReference ref = coverageLayer.getCoverageReference();

            assert ref != null : "CoverageMapLayer.getCoverageReference() contract don't allow null pointeur.";

            if (dataCoverage == null) {
                //LOGGER.log(Level.WARNING, "RasterSymbolizer : Reprojected coverage is null.");
                return;
            }

            final RasterSymbolizer sourceSymbol = symbol.getSource();

            ////////////////////////////////////////////////////////////////////
            // 2 - Select bands to style / display                            //
            ////////////////////////////////////////////////////////////////////

            //band select ----------------------------------------------------------
            //works as a JAI operation
            final int nbDim = dataCoverage.getNumSampleDimensions();
            if (nbDim > 1) {
                //we can change sample dimension only if we have more then one available.
                final ChannelSelection selections = sourceSymbol.getChannelSelection();
                if (selections != null) {
                    final SelectedChannelType channel = selections.getGrayChannel();
                    if (channel != null) {
                        //single band selection
                        final int[] indices = new int[]{
                                getBandIndice(channel.getChannelName(), dataCoverage)
                        };
                        dataCoverage = selectBand(dataCoverage, indices);
                    } else {
                        final SelectedChannelType[] channels = selections.getRGBChannels();
                        final int[] selected = new int[]{
                                getBandIndice(channels[0].getChannelName(), dataCoverage),
                                getBandIndice(channels[1].getChannelName(), dataCoverage),
                                getBandIndice(channels[2].getChannelName(), dataCoverage)
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

            /*
             * If we haven't got any reprojection we delegate affine transformation to java2D
             * we must switch to objectiveCRS for grid coverage
             */
            renderingContext.switchToObjectiveCRS();

            ////////////////////////////////////////////////////////////////////
            // 4 - Apply style                                                //
            ////////////////////////////////////////////////////////////////////

//            RenderedImage dataImage = dataCoverage.getRenderedImage();
            RenderedImage dataImage = applyStyle(ref, dataCoverage, elevationCoverage, sourceSymbol);
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
                renderCoverage(projectedCoverage, dataImage, trs2D);

                //-- repetition of increasing and decreasing sides.
                for (int i = 0; i < nbDecRep; i++) {
                    g2d.setTransform(renderingContext.wraps.wrapDecObjToDisp[i]);
                    renderCoverage(projectedCoverage, dataImage, trs2D);
                }
                for (int i = 0; i < nbIncRep; i++) {
                    g2d.setTransform(renderingContext.wraps.wrapIncObjToDisp[i]);
                    renderCoverage(projectedCoverage, dataImage, trs2D);
                }
            }

            renderingContext.switchToDisplayCRS();
        } catch (DisjointCoverageDomainException e) {
            LOGGER.log(Level.FINE,"Disjoint exception: "+e.getMessage(),e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,"Portrayal exception: "+e.getMessage(),e);
        }
    }

    /**
     * Apply style on current coverage.<br><br>
     *
     * Style application follow way given by
     * <a href="http://portal.opengeospatial.org/files/?artifact_id=16700">OpenGIS_Symbology_Encoding_Implementation_Specification</a> sheet 32.
     *
     * @param ref needed to compute statistics from internal metadata in case where missing informations.
     * @param coverage current styled coverage.
     * @param elevationCoverage needed object to generate shaded relief, {àcode null} if none.
     * @param styleElement the {@link RasterSymbolizer} which contain styles properties.
     * @return styled coverage representation.
     * @throws ProcessException if problem during apply Color map or shaded relief styles.
     * @throws FactoryException if problem during apply shaded relief style.
     * @throws TransformException if problem during apply shaded relief style.
     * @throws PortrayalException if problem during apply contrast enhancement style.
     * @throws java.io.IOException if problem during style application
     * @see #applyColorMapStyle(CoverageReference, org.geotoolkit.coverage.grid.GridCoverage2D, org.opengis.style.RasterSymbolizer)
     * @see #applyShadedRelief(java.awt.image.RenderedImage, org.geotoolkit.coverage.grid.GridCoverage2D, org.geotoolkit.coverage.grid.GridCoverage2D, org.opengis.style.RasterSymbolizer)
     * @see #applyContrastEnhancement(java.awt.image.RenderedImage, org.opengis.style.RasterSymbolizer)
     */
    public static RenderedImage applyStyle(CoverageReference ref, GridCoverage2D coverage,
            GridCoverage2D elevationCoverage,
            final RasterSymbolizer styleElement)
            throws ProcessException, FactoryException, TransformException, PortrayalException, IOException
             {

        RenderedImage image = applyColorMapStyle(ref, coverage, styleElement);
        image = applyShadedRelief(image, coverage, elevationCoverage, styleElement);
        image = applyContrastEnhancement(image, styleElement);
        return image;
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
        ArgumentChecks.ensureNonNull("image", image);
        ArgumentChecks.ensureNonNull("styleElement", styleElement);
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
     * Apply shaded relief on the image parameter from coverage geographic properties and elevation coverage properties.
     *
     * @param colorMappedImage image result issue from {@link #applyColorMapStyle(CoverageReference, org.geotoolkit.coverage.grid.GridCoverage2D, org.opengis.style.RasterSymbolizer) }
     * @param coverage base coverage
     * @param elevationCoverage elevation coverage if exist, should be {@code null},
     * if {@code null} image is just transformed into {@link BufferedImage#TYPE_INT_ARGB}.
     * @param styleElement the {@link RasterSymbolizer} which contain shaded relief properties.
     * @return image with shadow.
     * @throws FactoryException if problem during DEM generation.
     * @throws TransformException if problem during DEM generation.
     * @see #getDEMCoverage(org.geotoolkit.coverage.grid.GridCoverage2D, org.geotoolkit.coverage.grid.GridCoverage2D)
     */
    private static RenderedImage applyShadedRelief(RenderedImage colorMappedImage, final GridCoverage2D coverage,
            final GridCoverage2D elevationCoverage, final RasterSymbolizer styleElement)
            throws FactoryException, TransformException, ProcessException {
        ArgumentChecks.ensureNonNull("colorMappedImage", colorMappedImage);
        ArgumentChecks.ensureNonNull("coverage", coverage);
        ArgumentChecks.ensureNonNull("styleElement", styleElement);

        //-- shaded relief---------------------------------------------------------
        final ShadedRelief shadedRel = styleElement.getShadedRelief();
        shadingCase:
        if (shadedRel != null && shadedRel.getReliefFactor() != null) {
            final double factor = shadedRel.getReliefFactor().evaluate(null, Double.class);
            if (factor== 0.0) break shadingCase;

            //BUG ? When using the grid coverage builder the color model is changed
            if (colorMappedImage.getColorModel() instanceof CompatibleColorModel) {
                final BufferedImage bi = new BufferedImage(colorMappedImage.getWidth(), colorMappedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                bi.createGraphics().drawRenderedImage(colorMappedImage, new AffineTransform());
                colorMappedImage = bi;
            }

            //-- ReliefShadow creating --------------------
            final GridCoverage2D mntCoverage;
            if (elevationCoverage != null) {
                mntCoverage = getDEMCoverage(coverage, elevationCoverage);
            } else {
                break shadingCase;
                //does not have a nice result, still better then nothing
                //but is really slow to calculate, disabled for now.
                //mntCoverage = getGeoideCoverage(coverage);
            }

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setGridGeometry(coverage.getGridGeometry());
            gcb.setRenderedImage(colorMappedImage);
            gcb.setName("tempimg");
            final GridCoverage2D ti = gcb.getGridCoverage2D();

            final MathTransform1D trs = (MathTransform1D) MathTransforms.linear(factor, 0);
            final org.geotoolkit.processing.coverage.shadedrelief.ShadedRelief proc = new org.geotoolkit.processing.coverage.shadedrelief.ShadedRelief(
                    ti, mntCoverage, trs);
            final ParameterValueGroup res = proc.call();
            final GridCoverage2D shaded = (GridCoverage2D) res.parameter(ShadedReliefDescriptor.OUT_COVERAGE_PARAM_NAME).getValue();
            colorMappedImage = shaded.getRenderedImage();
        }
        return colorMappedImage;
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
    private static RenderedImage applyColorMapStyle(final CoverageReference ref,
            GridCoverage2D coverage,final RasterSymbolizer styleElement) throws ProcessException, IOException {
        ArgumentChecks.ensureNonNull("CoverageReference", ref);
        ArgumentChecks.ensureNonNull("coverage", coverage);
        ArgumentChecks.ensureNonNull("styleElement", styleElement);

        RenderedImage resultImage;

        //Recolor coverage -----------------------------------------------------
        ColorMap recolor = styleElement.getColorMap();
        //cheat on the colormap if we have only one band and no colormap
        recolorCase:
        if ((recolor == null || recolor.getFunction() == null)) {

            //if there is no geophysic, the same coverage is returned

            coverage = hasQuantitativeCategory(coverage) ? coverage.view(ViewType.GEOPHYSICS) : coverage;

            final RenderedImage ri      = coverage.getRenderedImage();
            final SampleModel sampleMod = ri.getSampleModel();
            final ColorModel riColorModel = ri.getColorModel();

            /**
             * Break computing statistic if indexcolormodel is already adapted for java 2d interpretation
             * (which mean index color model with positive colormap array index -> DataBuffer.TYPE_BYTE || DataBuffer.TYPE_USHORT)
             * or if image has already 3 or 4 bands Byte typed.
             */
            if (!defaultStyleIsNeeded(sampleMod, riColorModel))
                break recolorCase;


            final int nbBands = sampleMod.getNumBands();

            final CoverageDescription covRefMetadata = ref.getMetadata();

            ImageStatistics analyse = null;

            if (covRefMetadata != null)
                analyse = ImageStatistics.transform(covRefMetadata);

            if (analyse == null)
                analyse = Statistics.analyse(ri, true);

            if (nbBands < 3) {

                LOGGER.log(Level.FINE, "applyColorMapStyle : fallBack way is choosen."
                    + "GrayScale interpretation of the first coverage image band.");

                final ImageStatistics.Band band0 = analyse.getBand(0);
                final double bmin        = band0.getMin();
                final double bmax        = band0.getMax();
                final Double mean = band0.getMean();
                final Double std  = band0.getStd();
                double palMin = bmin;
                double palMax = bmax;
                if (mean != null && std != null) {
                    palMin = StrictMath.max(bmin, mean - 2 * std);
                    palMax = StrictMath.min(bmax, mean + 2 * std);
                }
                assert JDK8.isFinite(palMin) : "Raster Style fallback : minimum value should be finite. min = "+palMin;
                assert JDK8.isFinite(palMax) : "Raster Style fallback : maximum value should be finite. max = "+palMax;
                assert palMin >= bmin;
                assert palMax <= bmax;

                final List<InterpolationPoint> values = new ArrayList<>();
                final double[] nodatas = band0.getNoData();
                if (nodatas != null)
                    for (double nodata : nodatas) {
                        values.add(SF.interpolationPoint(nodata, SF.literal(new Color(0, 0, 0, 0))));
                    }

                values.add(SF.interpolationPoint(Float.NaN, SF.literal(new Color(0, 0, 0, 0))));

                //-- Color palette
//                Color[] colorsPal = PaletteFactory.getDefault().getColors("rainbow-t");
                Color[] colorsPal = PaletteFactory.getDefault().getColors("grayscale");
                assert colorsPal.length >= 2;
                if (colorsPal.length < 4) {
                    final double percent_5 = (colorsPal.length == 3) ? 0.1 : 0.05;
                    final Color[] colorsPalTemp = colorsPal;
                    colorsPal = Arrays.copyOf(colorsPal, colorsPal.length + 2);
                    System.arraycopy(colorsPalTemp, 2, colorsPal, 2, colorsPalTemp.length - 2);
                    colorsPal[colorsPal.length - 1] = colorsPalTemp[colorsPalTemp.length - 1];
                    colorsPal[1] = DefaultInterpolate.interpolate(colorsPalTemp[0], colorsPalTemp[1], percent_5);
                    colorsPal[colorsPal.length - 2] = DefaultInterpolate.interpolate(colorsPalTemp[colorsPalTemp.length - 2], colorsPalTemp[colorsPalTemp.length - 1], 1 - percent_5);

                }

                //-- if difference between band minimum statistic and palette minimum,
                //-- define values between them as transparency
                values.add(SF.interpolationPoint(bmin, SF.literal(colorsPal[0])));

                assert colorsPal.length >= 4;

                final double step = (palMax - palMin) / (colorsPal.length - 3);//-- min and max transparency
                double currentVal = palMin;
                for (int c = 1; c <= colorsPal.length - 2; c++) {
                    values.add(SF.interpolationPoint(currentVal, SF.literal(colorsPal[c])));
                    currentVal += step;
                }
                assert StrictMath.abs(currentVal - step - palMax) < 1E-9;
                values.add(SF.interpolationPoint(bmax, SF.literal(colorsPal[colorsPal.length - 1])));

                final Function function = SF.interpolateFunction(DEFAULT_CATEGORIZE_LOOKUP, values, Method.COLOR, Mode.LINEAR, DEFAULT_FALLBACK);

                recolor = GO2Utilities.STYLE_FACTORY.colorMap(function);

            } else {

                LOGGER.log(Level.FINE, "RGBStyle : fallBack way is choosen."
                    + "RGB interpretation of the three first coverage image bands.");

                final int rgbNumBand = (riColorModel.hasAlpha()) ? 4 : 3;

                assert rgbNumBand <= nbBands;

                final int[] bands       = new int[]{-1, -1, -1, -1};
                final double[][] ranges = new double[][]{{-1, -1},
                                                         {-1, -1},
                                                         {-1, -1},
                                                         {-1, -1}};

                for (int b = 0; b < rgbNumBand; b++) {
                    final ImageStatistics.Band bandb = analyse.getBand(b);
                    double min = bandb.getMin();
                    double max = bandb.getMax();
                    final Double mean = bandb.getMean();
                    final Double std = bandb.getStd();
                    if (mean != null && std != null) {
                        min = StrictMath.max(min, mean - 2 * std);
                        max = StrictMath.min(max, mean + 2 * std);
                    }
                    assert JDK8.isFinite(min) : "Raster Style fallback : minimum value should be finite. min = "+min;
                    assert JDK8.isFinite(max) : "Raster Style fallback : maximum value should be finite. max = "+max;
                    bands[b] = b;
                    ranges[b][0] = min;
                    ranges[b][1] = max;
                }

                final DynamicRangeStretchProcess p = new DynamicRangeStretchProcess(ri, bands, ranges);
                final BufferedImage img            = p.executeNow();
                if (img instanceof WritableRenderedImage) GO2Utilities.removeBlackBorder((WritableRenderedImage)img);
                return img;
            }
        }

        //-- apply recolor function "sample to geophysic", sample interpretation.
        if (recolor != null
         && recolor.getFunction() != null) {

            //color map is applied on geophysics view
            //if there is no geophysic, the same coverage is returned
            coverage = hasQuantitativeCategory(coverage) ? coverage.view(ViewType.GEOPHYSICS) : coverage;
            resultImage = coverage.getRenderedImage();

            final Function fct = recolor.getFunction();
            resultImage        = recolor(resultImage, fct);
        } else {
            //no color map, used the default image rendered view
            // coverage = coverage.view(ViewType.RENDERED);
            if (coverage.getViewTypes().contains(ViewType.PHOTOGRAPHIC)) {
                resultImage = coverage.view(ViewType.PHOTOGRAPHIC).getRenderedImage();
            } else {
                resultImage = coverage.view(ViewType.PACKED).getRenderedImage();//-- same as rendered view into implementation
            }

//            //-- if RGB force ARGB to delete black border
//            final int[] componentSize = resultImage.getColorModel().getComponentSize();
//            if (componentSize.length == 3 && componentSize[0] == 8) {
//                resultImage = GO2Utilities.forceAlpha(resultImage);
//                if (resultImage instanceof WritableRenderedImage) GO2Utilities.removeBlackBorder((WritableRenderedImage)resultImage);
//            }
        }

        assert resultImage != null : "applyColorMapStyle : image can't be null.";
        return resultImage;
    }

    /**
     * Returns {@code true} if the given {@link GridCoverage2D} contain an interpretable geophysic {@link Category},
     * else {@code false}.
     *
     * @param coverage
     * @return true if coverage contain quantitative category.
     */
    private static boolean  hasQuantitativeCategory(final GridCoverage2D coverage) {
        ArgumentChecks.ensureNonNull("GridCoverage2D", coverage);
        for (GridSampleDimension gs : coverage.getSampleDimensions()) {
            final List<Category> categories = gs.getCategories();
            if (categories != null)
                for (Category cat : categories) {
                    if (cat.isQuantitative()) return true;
                }
        }
        return false;
    }

    /**
     * Returns {@code true} if a default style is needed to interpret current data
     * else {@code false} if java 2d will be able to interprete data.
     *
     * @param sampleModel
     * @param colorModel
     * @return {@code true} if a style creation is needed to show image datas else {@code false}.
     */
    private static boolean defaultStyleIsNeeded(final SampleModel sampleModel, final ColorModel colorModel) {
        ArgumentChecks.ensureNonNull("sampleModel", sampleModel);
        ArgumentChecks.ensureNonNull("colorModel",  colorModel);

        final int[] pixelSampleSize = colorModel.getComponentSize();

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

    private static int getBandIndice(final String name, final Coverage coverage) throws PortrayalException{
        try{
            return Integer.parseInt(name);
        }catch(NumberFormatException ex){
            //can be a name
            for(int i=0,n=coverage.getNumSampleDimensions();i<n;i++){
                final SampleDimension sampleDim = coverage.getSampleDimension(i);
                if (Objects.equals(String.valueOf(sampleDim.getDescription()), n)) {
                    return i;
                }
            }
        }

        throw new PortrayalException("Band for name/indice "+name+" not found");
    }

    private void renderCoverage(final ProjectedCoverage projectedCoverage, RenderedImage img, MathTransform2D trs2D) throws PortrayalException{
        if (trs2D instanceof AffineTransform) {
            g2d.setComposite(symbol.getJ2DComposite());
            try {
                g2d.drawRenderedImage(img, (AffineTransform)trs2D);
            } catch (Exception ex) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                LOGGER.log(Level.WARNING, sw.toString());//-- more explicite way to debug

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

        if (elevationModel == null) return null;

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

        final MathTransform demCRSToCov        = CRS.findOperation(demCRS, covCRS, null).getMathTransform(); // dem -> cov

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

        final MathTransform demCRSToCov = CRS.findOperation(demCRS, covCRS, null).getMathTransform(); // dem -> cov

        if (demCRSToCov.isIdentity())
            return dem;

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
        final BufferedImage destMNT = BufferedImages.createImage(covExtend.width, covExtend.height, demImage);
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
        final MathTransform dataToLongLat = CRS.findOperation(coverage.getCoordinateReferenceSystem2D(), CommonCRS.WGS84.normalizedGeographic(), null).getMathTransform();
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
     * {@inheritDoc }
     *
     * Prepare coverage for Raster rendering.
     */
    @Override
    protected GridCoverage2D prepareCoverageToResampling(final GridCoverage2D coverageSource, final CachedRasterSymbolizer symbolizer) {
        return getReadyToResampleCoverage(coverageSource, symbolizer.getSource());
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
            final int datatype = img.getSampleModel().getDataType();
            if (datatype != DataBuffer.TYPE_BYTE && datatype != DataBuffer.TYPE_USHORT) return source;
            RenderedImage imga = GO2Utilities.forceAlpha(img);

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
     * Returns a {@link GridCoverage2D} which contain band extracted from sourceCoverage
     * at band indices given by indice array parameter.<br><br>
     *
     * note : out coverage will have same band number than indice array length.
     *
     * @param sourceCoverage coverage which contain all needed band.
     * @param indices an array which contain band index of sourceCoverage to build another {@link GridCoverage2D}.
     * @return a new {@link GridCoverage2D} with extracted band from sourceCoverage at indice given by indice array.
     * @throws ProcessException if problem during process band selection.
     * @see BandSelectProcess
     */
    private static GridCoverage2D selectBand(final GridCoverage2D sourceCoverage, final int[] indices) throws ProcessException {
        if (sourceCoverage.getNumSampleDimensions() < indices.length) {
            //not enough bands in the image
            LOGGER.log(Level.WARNING, "Raster Style define more bands than the data");
            return sourceCoverage;
        } else {
            RenderedImage image = sourceCoverage.getRenderedImage();

            final ProcessDescriptor bandSelectDesc = BandSelectDescriptor.INSTANCE;
            final ParameterValueGroup param = bandSelectDesc.getInputDescriptor().createValue();
            ParametersExt.getOrCreateValue(param, BandSelectDescriptor.IN_IMAGE.getName().getCode()).setValue(image);
            ParametersExt.getOrCreateValue(param, BandSelectDescriptor.IN_BANDS.getName().getCode()).setValue(indices);
            final org.geotoolkit.process.Process process = bandSelectDesc.createProcess(param);

            final ParameterValueGroup output = process.call();
            image = (RenderedImage) ParametersExt.getOrCreateValue(output, BandSelectDescriptor.OUT_IMAGE.getName().getCode()).getValue();
            final GridCoverageBuilder builder = new GridCoverageBuilder();
            builder.setGridCoverage(sourceCoverage);
            builder.setRenderedImage(image);
            builder.setSampleDimensions();
            return builder.getGridCoverage2D();
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
                destination = BufferedImages.createImage(source.getWidth(), source.getHeight(), source);
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

}
