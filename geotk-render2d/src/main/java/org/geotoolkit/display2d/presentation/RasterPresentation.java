/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.presentation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.ComponentColorModel;
import java.awt.image.RenderedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.ImageLayout;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.map.coverage.RenderingWorkaround;
import org.apache.sis.map.MapLayer;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.geotoolkit.processing.coverage.statistics.StatisticOp;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.CompatibleColorModel;
import org.geotoolkit.style.function.DefaultInterpolationPoint;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RasterPresentation extends Grid2DPresentation {

    public AlphaComposite composite = GO2Utilities.ALPHA_COMPOSITE_1F;
    public GridCoverage coverage;

    public RasterPresentation(MapLayer layer, Resource resource, GridCoverage coverage) {
        super(layer, resource, null);
        this.coverage = coverage;
    }

    @Override
    public boolean paint(RenderingContext2D renderingContext) throws PortrayalException {
        final CanvasMonitor monitor = renderingContext.getMonitor();
        final Graphics2D g2d = renderingContext.getGraphics();

        boolean dataRendered = false;
        final GridCoverage dataImage = coverage;

        g2d.setComposite(composite);

        /*
         * If we haven't got any reprojection we delegate affine transformation to java2D
         * we must switch to objectiveCRS for grid coverage
         */
        renderingContext.switchToObjectiveCRS();

        MathTransform trs2D = renderingTransform(dataImage.getGridGeometry());

        if (monitor.stopRequested()) return dataRendered;

        ////////////////////////////////////////////////////////////////////
        // 5 - Correct cross meridian problems / render                   //
        ////////////////////////////////////////////////////////////////////

        if (renderingContext.wraps == null) {
            //single rendering
            dataRendered |= renderCoverage(renderingContext, dataImage, trs2D);

        } else {
            //check if the geometry overlaps the meridian
            int nbIncRep = renderingContext.wraps.wrapIncNb;
            int nbDecRep = renderingContext.wraps.wrapDecNb;
            final Geometry objBounds = JTS.toGeometry(dataImage.getGridGeometry().getEnvelope());

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
            dataRendered |= renderCoverage(renderingContext, dataImage, trs2D);

            //-- repetition of increasing and decreasing sides.
            for (int i = 0; i < nbDecRep; i++) {
                g2d.setTransform(renderingContext.wraps.wrapDecObjToDisp[i]);
                dataRendered |= renderCoverage(renderingContext, dataImage, trs2D);
            }
            for (int i = 0; i < nbIncRep; i++) {
                g2d.setTransform(renderingContext.wraps.wrapIncObjToDisp[i]);
                dataRendered |= renderCoverage(renderingContext, dataImage, trs2D);
            }
        }

        renderingContext.switchToDisplayCRS();
        return dataRendered;
    }

    @Override
    public boolean hit(RenderingContext2D renderingContext, SearchAreaJ2D search) {
        final Envelope envelope = coverage.getGridGeometry().getEnvelope();
        final Polygon polygon = JTS.toGeometry(envelope);
        polygon.setUserData(envelope.getCoordinateReferenceSystem());

        Geometry coverageShape;
        try {
            coverageShape = org.apache.sis.geometry.wrapper.jts.JTS.transform(polygon, renderingContext.getDisplayCRS());
            final Geometry area = search.getDisplayGeometryJTS();

            if (coverageShape.intersects(area)) {
                return true;
            }

            //test with wrap around split
            coverageShape = GeometricUtilities.toJTSGeometry(envelope, GeometricUtilities.WrapResolution.SPLIT);
            coverageShape = org.apache.sis.geometry.wrapper.jts.JTS.transform(polygon, renderingContext.getDisplayCRS());
            if (coverageShape.intersects(area)) {
                return true;
            }

        } catch (MismatchedDimensionException | TransformException | FactoryException ex) {
            Logger.getLogger("org.geotoolkit.display2d").log(Level.INFO, ex.getMessage(), ex);
        }
        return false;
    }

    private boolean renderCoverage(RenderingContext2D renderingContext,
            org.apache.sis.coverage.grid.GridCoverage coverage,
            MathTransform trs2D) throws PortrayalException {
        final Graphics2D g2d = renderingContext.getGraphics();
        final CanvasMonitor monitor = renderingContext.getMonitor();
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
            img = processor.prefetch(img, null);
        }


        final InterpolationCase interpolationCase = (InterpolationCase) renderingContext.getRenderingHints().get(GO2Hints.KEY_INTERPOLATION);
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
                        trs2D = renderingTransform(cov.getGridGeometry());
                        img = cov.render(null);
                    } catch (ProcessException ex) {
                        throw new PortrayalException(ex);
                    }
                    break;
            }
        }

        if (trs2D instanceof AffineTransform) {
            try {
                g2d.drawRenderedImage(RenderingWorkaround.wrap(img), (AffineTransform)trs2D);
                dataRendered = true;
            } catch (Exception ex) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                //LOGGER.log(Level.WARNING, sw.toString());//-- more explicite way to debug

                if(ex instanceof ArrayIndexOutOfBoundsException){

                    //we can recover when it's an inapropriate componentcolormodel
                    final StackTraceElement[] eles = ex.getStackTrace();
                    if(eles.length > 0 && ComponentColorModel.class.getName().equalsIgnoreCase(eles[0].getClassName())){

                        try {
                            final Map<String,Object> analyze = StatisticOp.analyze(img);
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
                            final Expression function = GO2Utilities.STYLE_FACTORY.interpolateFunction(
                                    lookup, values, Method.COLOR, Mode.LINEAR, fallback);
                            final CompatibleColorModel model = new CompatibleColorModel(img.getColorModel().getPixelSize(), function);
                            final ImageLayout layout = new ImageLayout().setColorModel(model);
                            img = new NullOpImage(img, layout, null, OpImage.OP_COMPUTE_BOUND);
                            g2d.drawRenderedImage(RenderingWorkaround.wrap(img), (AffineTransform)trs2D);
                            dataRendered = true;
                        } catch(Exception e) {
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
        } else if (trs2D instanceof LinearTransform) {
            final LinearTransform lt = (LinearTransform) trs2D;
            final int col = lt.getMatrix().getNumCol();
            final int row = lt.getMatrix().getNumRow();
            //TODO using only the first parameters of the linear transform
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass());
        } else {
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass() );
        }

        return dataRendered;
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

    private static MathTransform renderingTransform(GridGeometry gridGeometry) {
        final MathTransform trs2D = gridGeometry.getGridToCRS(PixelInCell.CELL_CORNER);
        final long[] sourceCorner = gridGeometry.getExtent().getLow().getCoordinateValues();
        final MathTransform cornerOffset = MathTransforms.translation(sourceCorner[0], sourceCorner[1]);
        return MathTransforms.concatenate(cornerOffset, trs2D);
    }

}
