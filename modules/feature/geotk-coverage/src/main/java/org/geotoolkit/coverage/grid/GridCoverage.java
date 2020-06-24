/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.coverage.grid;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.io.StringWriter;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import javax.media.jai.ImageFunction;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;
import javax.media.jai.operator.ImageFunctionDescriptor;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.internal.coverage.j2d.ColorModelFactory;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.util.Classes;
import org.apache.sis.util.Localized;
import org.apache.sis.util.iso.Types;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.io.LineWriter;
import org.geotoolkit.lang.Debug;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.geotoolkit.resources.Errors;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.util.InternationalString;


/**
 * Temporary class while moving classes to Apache SIS.
 *
 * Represent the basic implementation which provides access to grid coverage data.
 * A {@code GridCoverage} implementation may provide the ability to update grid values.
 *
 * <div class="warning"><b>Warning — this class will change</b><br>
 * Current API is derived from OGC <a href="http://www.opengis.org/docs/01-004.pdf">Grid Coverages Implementation specification 1.0</a>.
 * We plan to replace it by new interfaces derived from ISO 19123 (<cite>Schema for coverage geometry
 * and functions</cite>). Current interfaces should be considered as legacy and are included in this
 * distribution only because they were part of GeoAPI 1.0 release. We will try to preserve as much
 * compatibility as possible, but no migration plan has been determined yet.
 * </div>
 * *
 * Base class of all coverage type. The essential property of coverage is to be able
 * to generate a value for any point within its domain.  How coverage is represented
 * internally is not a concern. For example consider the following different internal
 * representations of coverage:
 * <p>
 * <ul>
 *   <li>A coverage may be represented by a set of polygons which exhaustively tile a
 *       plane (that is each point on the plane falls in precisely one polygon). The
 *       value returned by the coverage for a point is the value of an attribute of
 *       the polygon that contains the point.</li>
 *   <li>A coverage may be represented by a grid of values. The value returned by the
 *       coverage for a point is that of the grid value whose location is nearest the
 *       point.</li>
 *   <li>Coverage may be represented by a mathematical function. The value returned
 *       by the coverage for a point is just the return value of the function when
 *       supplied the coordinates of the point as arguments.</li>
 *   <li>Coverage may be represented by combination of these. For example, coverage
 *       may be represented by a combination of mathematical functions valid over a
 *       set of polynomials.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 */
public abstract class GridCoverage extends org.apache.sis.coverage.grid.GridCoverage implements Localized {

    /**
     * The logger for grid coverage operations.
     */
    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.coverage.grid");

    /**
     * Sources grid coverage, or {@code null} if none. This information is lost during
     * serialization, in order to avoid sending a too large amount of data over the network.
     */
    private final transient List<org.apache.sis.coverage.grid.GridCoverage> sources;

    /**
     * The sample dimension to make visible by {@link #getRenderableImage}.
     *
     * @see org.geotoolkit.coverage.io.ImageReaderAdapter#VISIBLE_BAND
     */
    private static final int VISIBLE_BAND = 0;

    /**
     * The coverage name, or {@code null} if none.
     */
    private final InternationalString name;

    /**
     * Constructs a coverage using the specified coordinate reference system. If the coordinate
     * reference system is {@code null}, then the subclasses must override {@link #getDimension()}.
     *
     * @param name
     *          The coverage name, or {@code null} if none.
     * @param grid   the grid extent, CRS and conversion from cell indices to CRS.
     * @param bands  sample dimensions for each image band.
     */
    protected GridCoverage(final CharSequence name,
                           final GridGeometry grid,
                           final Collection<? extends SampleDimension> bands)
    {
        super(grid, bands);
        this.name = Types.toInternationalString(name);
        this.sources = null;
    }

    /**
     * Constructs a new coverage with the same parameters than the specified coverage.
     *
     * @param name
     *          The name for this coverage, or {@code null} for the same than {@code coverage}.
     * @param coverage
     *          The source coverage.
     */
    protected GridCoverage(final CharSequence name, final GridCoverage coverage) {
        super(coverage.getGridGeometry(), coverage.getSampleDimensions());
        final InternationalString n = Types.toInternationalString(name);
        this.name = (n != null) ? n : coverage.name;
        this.sources = Collections.singletonList(coverage);
    }

    /**
     * Constructs a grid coverage with sources. Arguments are the same than for the
     * {@linkplain #AbstractGridCoverage(CharSequence,CoordinateReferenceSystem,Map)
     * previous constructor}, with an additional {@code sources} argument.
     *
     * @param name
     *          The grid coverage name.
     * @param grid   the grid extent, CRS and conversion from cell indices to CRS.
     * @param bands  sample dimensions for each image band.
     * @param sources
     *          The {@linkplain #getSources sources} for a grid coverage, or {@code null} if none.
     */
    protected GridCoverage(final CharSequence name,
                           final GridGeometry grid,
                           final Collection<? extends SampleDimension> bands,
                           final GridCoverage[] sources)
    {
        super(grid, bands);
        this.name = Types.toInternationalString(name);
        if (sources != null) {
            switch (sources.length) {
                case 0:  this.sources = null; break;
                case 1:  this.sources = Collections.singletonList(sources[0]); break;
                default: this.sources = UnmodifiableArrayList.wrap(sources.clone()); break;
            }
        } else {
            this.sources = null;
        }
    }

    /**
     * Returns the coverage name, or {@code null} if none. The default
     * implementation returns the name specified at construction time.
     *
     * @return The coverage name, or {@code null}.
     */
    public InternationalString getName() {
        return name;
    }

    /**
     * Returns the bounding box for the coverage domain in
     * {@linkplain #getCoordinateReferenceSystem coordinate reference system} coordinates. May
     * be {@code null} if this coverage has no associated coordinate reference system. For grid
     * coverages, the grid cells are centered on each grid coordinate. The envelope for a 2-D
     * grid coverage includes the following corner positions.
     *
     * {@preformat text
     *     (Minimum row - 0.5, Minimum column - 0.5) for the minimum coordinates
     *     (Maximum row - 0.5, Maximum column - 0.5) for the maximum coordinates
     * }
     *
     * The default implementation returns the domain of validity of the CRS, if there is one.
     *
     * @return The bounding box for the coverage domain in coordinate system coordinates.
     */
    public Envelope getEnvelope() {
        return getGridGeometry().getEnvelope();
    }

    @Override
    public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
        final int[] indices = sliceExtent.getSubspaceDimensions(2);
        final int xAxis = indices[0];
        final int yAxis = indices[1];
        final RenderableImage ri = getRenderableImage(xAxis, yAxis);
        final RenderedImage img = ri.createDefaultRendering();
        final GridExtent gridExtent = getGridGeometry().getExtent();
        final long dx = Math.subtractExact(sliceExtent.getLow(xAxis), gridExtent.getLow(xAxis));
        final long dy = Math.subtractExact(sliceExtent.getLow(yAxis), gridExtent.getLow(yAxis));
        final int width = Math.toIntExact(sliceExtent.getSize(xAxis));
        final int height = Math.toIntExact(sliceExtent.getSize(yAxis));

        if (img.getWidth() == width && img.getHeight() == height && dx == 0 && dy == 0) {
            return img;
        }

        final int x = Math.toIntExact(img.getMinX() + dx);
        final int y = Math.toIntExact(img.getMinY() + dy);

        if (img instanceof BufferedImage) {
            final BufferedImage bimg = (BufferedImage) img;
            return bimg.getSubimage(x, y, width, height);
        } else {
            final BufferedImage bimg = BufferedImages.createImage(width, height, img);
            bimg.setData(img.getData(new Rectangle(x, y, width, height)));
            return bimg;
        }
    }

    /**
     * Returns 2D view of this grid coverage as a renderable image. This method
     * allows inter-operability with Java2D.
     *
     * @param xAxis Dimension to use for the <var>x</var> display axis.
     * @param yAxis Dimension to use for the <var>y</var> display axis.
     * @return A 2D view of this grid coverage as a renderable image.
     */
    public RenderableImage getRenderableImage(final int xAxis, final int yAxis) {
        return new Renderable(xAxis, yAxis);
    }

    /////////////////////////////////////////////////////////////////////////
    ////////////////                                         ////////////////
    ////////////////     RenderableImage / ImageFunction     ////////////////
    ////////////////                                         ////////////////
    /////////////////////////////////////////////////////////////////////////

    /**
     * A view of a {@linkplain AbstractCoverage coverage} as a renderable image. Renderable images
     * allow inter-operability with <A HREF="http://java.sun.com/products/java-media/2D/">Java2D</A>
     * for a two-dimensional slice of a coverage (which may or may not be a
     * {@linkplain org.geotoolkit.coverage.grid.GridCoverage2D grid coverage}).
     *
     * @author Martin Desruisseaux (IRD)
     */
    protected class Renderable implements RenderableImage, ImageFunction {
        /**
         * The two dimensional view of the coverage's envelope.
         */
        private final Rectangle2D bounds;

        /**
         * Dimension to use for <var>x</var> axis.
         */
        protected final int xAxis;

        /**
         * Dimension to use for <var>y</var> axis.
         */
        protected final int yAxis;

        /**
         * A coordinate point where to evaluate the function. The point dimension is equals to the
         * {@linkplain AbstractCoverage#getDimension coverage's dimension}. The {@linkplain #xAxis
         * x} and {@link #yAxis y} coordinates will be ignored, since they will vary for each pixel
         * to be evaluated. Other coordinates, if any, should be set to a fixed value. For example a
         * coverage may be three-dimensional, where the third dimension is the time axis. In such
         * case, {@code coordinate.ord[2]} should be set to the point in time where to evaluate the
         * coverage. By default, all coordinates are initialized to 0. Subclasses should set the
         * desired values in their constructor if needed.
         */
        protected final GeneralDirectPosition coordinate = new GeneralDirectPosition(getCoordinateReferenceSystem().getCoordinateSystem().getDimension());

        /**
         * Constructs a renderable image.
         *
         * @param xAxis Dimension to use for <var>x</var> axis.
         * @param yAxis Dimension to use for <var>y</var> axis.
         */
        public Renderable(final int xAxis, final int yAxis) {
            this.xAxis = xAxis;
            this.yAxis = yAxis;
            final Envelope envelope = getEnvelope();
            bounds = new Rectangle2D.Double(envelope.getMinimum(xAxis), envelope.getMinimum(yAxis),
                                            envelope.getSpan   (xAxis), envelope.getSpan   (yAxis));
        }

        /**
         * Returns {@code null} to indicate that no source information is available.
         */
        @Override
        public Vector<RenderableImage> getSources() {
            return null;
        }

        /**
         * Returns {@code true} if successive renderings with the same arguments
         * may produce different results. The default implementation returns {@code false}.
         *
         * @see org.geotoolkit.coverage.grid.GridCoverage2D#isDataEditable
         */
        @Override
        public boolean isDynamic() {
            return false;
        }

        /**
         * Returns {@code false} since values are not complex.
         *
         * @return Always {@code false} in default implementation.
         */
        @Override
        public boolean isComplex() {
            return false;
        }

        /**
         * Gets the width in coverage coordinate space.
         *
         * @see AbstractCoverage#getEnvelope
         * @see AbstractCoverage#getCoordinateReferenceSystem
         */
        @Override
        public float getWidth() {
            return (float) bounds.getWidth();
        }

        /**
         * Gets the height in coverage coordinate space.
         *
         * @see AbstractCoverage#getEnvelope
         * @see AbstractCoverage#getCoordinateReferenceSystem
         */
        @Override
        public float getHeight() {
            return (float) bounds.getHeight();
        }

        /**
         * Gets the minimum <var>X</var> coordinate of the rendering-independent image
         * data. This is the {@linkplain AbstractCoverage#getEnvelope coverage's envelope}
         * minimal value for the {@linkplain #xAxis x axis}.
         *
         * @see AbstractCoverage#getEnvelope
         * @see AbstractCoverage#getCoordinateReferenceSystem
         */
        @Override
        public float getMinX() {
            return (float) bounds.getX();
        }

        /**
         * Gets the minimum <var>Y</var> coordinate of the rendering-independent image
         * data. This is the {@linkplain AbstractCoverage#getEnvelope coverage's envelope}
         * minimal value for the {@linkplain #yAxis y axis}.
         *
         * @see AbstractCoverage#getEnvelope
         * @see AbstractCoverage#getCoordinateReferenceSystem
         */
        @Override
        public float getMinY() {
            return (float) bounds.getY();
        }

        /**
         * Returns a rendered image with a default width and height in pixels.
         *
         * @return A rendered image containing the rendered data
         */
        @Override
        public RenderedImage createDefaultRendering() {
            return createScaledRendering(512, 0, null);
        }

        /**
         * Creates a rendered image with width {@code width} and height {@code height} in pixels.
         * If {@code width} is 0, it will be computed automatically from {@code height}. Conversely,
         * if {@code height} is 0, il will be computed automatically from {@code width}.
         * <p>
         * The default implementation creates a render context with {@link #createRenderContext}
         * and invokes {@link #createRendering(RenderContext)}.
         *
         * @param width  The width of rendered image in pixels, or 0.
         * @param height The height of rendered image in pixels, or 0.
         * @param hints  Rendering hints, or {@code null}.
         * @return A rendered image containing the rendered data
         */
        @Override
        public RenderedImage createScaledRendering(int width, int height, final RenderingHints hints) {
            final double boundsWidth  = bounds.getWidth();
            final double boundsHeight = bounds.getHeight();
            if (!(width > 0)) { // Use '!' in order to catch NaN
                if (!(height > 0)) {
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.UnspecifiedImageSize));
                }
                width = (int) Math.round(height * (boundsWidth / boundsHeight));
            } else if (!(height > 0)) {
                height = (int) Math.round(width * (boundsHeight / boundsWidth));
            }
            return createRendering(createRenderContext(new Rectangle(0, 0, width, height), hints));
        }

        /**
         * Creates a rendered image using a given render context. This method will uses an
         * "{@link ImageFunctionDescriptor ImageFunction}" operation if possible (i.e. if
         * the area of interect is rectangular and the affine transform contains only
         * translation and scale coefficients).
         *
         * @param context The render context to use to produce the rendering.
         * @return A rendered image containing the rendered data
         */
        @Override
        public RenderedImage createRendering(final RenderContext context) {
            final AffineTransform crsToGrid = context.getTransform();
            final Shape area = context.getAreaOfInterest();
            final Rectangle gridBounds;
            if (true) {
                /*
                 * Computes the grid bounds for the coverage bounds (or the area of interest).
                 * The default implementation of Rectangle uses Math.floor and Math.ceil for
                 * computing a box which contains fully the Rectangle2D. But in our particular
                 * case, we really want to round toward the nearest integer.
                 */
                final Rectangle2D bounds = AffineTransforms2D.transform(crsToGrid,
                        (area != null) ? area.getBounds2D() : this.bounds, null);
                final int xmin = (int) Math.round(bounds.getMinX());
                final int ymin = (int) Math.round(bounds.getMinY());
                final int xmax = (int) Math.round(bounds.getMaxX());
                final int ymax = (int) Math.round(bounds.getMaxY());
                gridBounds = new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);
            }
            /*
             * Computes some properties of the image to be created.
             */
            final Dimension tileSize = org.apache.sis.internal.coverage.j2d.ImageLayout.DEFAULT.suggestTileSize(
                    gridBounds.width, gridBounds.height, true);
            SampleDimension band = getSampleDimensions().get(VISIBLE_BAND);
            if (band == null)
                throw new IllegalStateException("Sample dimensions are undetermined.");
            final int nbBand = getSampleDimensions().size();
            ColorModel colorModel = SampleDimensionUtils.getColorModel(band, VISIBLE_BAND, nbBand);
            final SampleModel sampleModel;
            if (colorModel != null) {
                sampleModel = colorModel.createCompatibleSampleModel(tileSize.width, tileSize.height);
            } else {
                sampleModel = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, tileSize.width, tileSize.height, nbBand);
                colorModel = ColorModelFactory.createGrayScale(DataBuffer.TYPE_DOUBLE, nbBand, 0, 0, 1);
            }

            /*
             * If the image can be created using the ImageFunction operation, do it.
             * It allow JAI to defer the computation until a tile is really requested.
             */
            final PlanarImage image;
            if ((area == null || area instanceof Rectangle2D) &&
                    crsToGrid.getShearX() == 0 && crsToGrid.getShearY() == 0)
            {
                image = ImageFunctionDescriptor.create(this, // The functional description
                        gridBounds.width,                    // The image width
                        gridBounds.height,                   // The image height
                        (float) (1/crsToGrid.getScaleX()),   // The X scale factor
                        (float) (1/crsToGrid.getScaleY()),   // The Y scale factor
                        (float) crsToGrid.getTranslateX(),   // The X translation
                        (float) crsToGrid.getTranslateY(),   // The Y translation
                        new RenderingHints(JAI.KEY_IMAGE_LAYOUT, new ImageLayout()
                                .setMinX       (gridBounds.x)
                                .setMinY       (gridBounds.y)
                                .setTileWidth  (tileSize.width)
                                .setTileHeight (tileSize.height)
                                .setSampleModel(sampleModel)
                                .setColorModel (colorModel)));
            } else {
                /*
                 * Creates immediately a rendered image using a given render context. This block
                 * is run when the image can't be created with JAI's ImageFunction operator, for
                 * example because the affine transform swap axis or because there is an area of
                 * interest.
                 */
                // Clones the coordinate point in order to allow multi-thread
                // invocation.
                final GeneralDirectPosition coordinate = new GeneralDirectPosition(this.coordinate);
                final TiledImage tiled = new TiledImage(gridBounds.x, gridBounds.y,
                        gridBounds.width, gridBounds.height, 0, 0, sampleModel, colorModel);
                final Point2D.Double point2D = new Point2D.Double();
                final int numBands = tiled.getNumBands();
                final double[] samples = new double[numBands];
                final double[] padNaNs = new double[numBands];
                Arrays.fill(padNaNs, Double.NaN);
                final WritableRectIter iterator = RectIterFactory.createWritable(tiled, gridBounds);
                if (!iterator.finishedLines()) try {
                    int y = gridBounds.y;
                    do {
                        iterator.startPixels();
                        if (!iterator.finishedPixels()) {
                            int x = gridBounds.x;
                            do {
                                point2D.x = x;
                                point2D.y = y;
                                crsToGrid.inverseTransform(point2D, point2D);
                                if (area == null || area.contains(point2D)) {
                                    coordinate.coordinates[xAxis] = point2D.x;
                                    coordinate.coordinates[yAxis] = point2D.y;
                                    iterator.setPixel(evaluate(coordinate, samples));
                                } else {
                                    iterator.setPixel(padNaNs);
                                }
                                x++;
                            } while (!iterator.nextPixelDone());
                            assert (x == gridBounds.x + gridBounds.width);
                            y++;
                        }
                    } while (!iterator.nextLineDone());
                    assert (y == gridBounds.y + gridBounds.height);
                } catch (NoninvertibleTransformException exception) {
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.IllegalArgument_1, "context"), exception);
                }
                image = tiled;
            }
            /*
             * Adds a 'gridToCRS' property to the image. This is an important
             * information for constructing a GridCoverage from this image later.
             */
            try {
                image.setProperty("gridToCRS", crsToGrid.createInverse());
            } catch (NoninvertibleTransformException exception) {
                // Can't add the property. Too bad, the image has been created
                // anyway. Maybe the user know what he is doing...
                Logging.unexpectedException(null, Renderable.class, "createRendering", exception);
            }
            return image;
        }

        /**
         * Initializes a render context with an affine transform that maps the coverage envelope
         * to the specified destination rectangle. The affine transform mays swap axis in order to
         * normalize their order (i.e. make them appear in the (<var>x</var>,<var>y</var>) order),
         * so that the image appears properly oriented when rendered.
         *
         * @param gridBounds The two-dimensional destination rectangle.
         * @param hints      The rendering hints, or {@code null} if none.
         * @return A render context initialized with an affine transform from the coverage
         *         to the grid coordinate system. This transform is the inverse of
         *         {@link org.geotoolkit.coverage.grid.GridGeometry2D#getGridToCRS2D}.
         *
         * @see org.geotoolkit.coverage.grid.GridGeometry2D#getGridToCRS2D
         */
        protected RenderContext createRenderContext(final Rectangle2D gridBounds,
                                                    final RenderingHints hints)
        {
            final MatrixSIS matrix;
            final GeneralEnvelope srcEnvelope = new GeneralEnvelope(
                    new double[] {bounds.getMinX(), bounds.getMinY()},
                    new double[] {bounds.getMaxX(), bounds.getMaxY()});
            final GeneralEnvelope dstEnvelope = new GeneralEnvelope(
                    new double[] {gridBounds.getMinX(), gridBounds.getMinY()},
                    new double[] {gridBounds.getMaxX(), gridBounds.getMaxY()});
            final CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
            if (crs != null) {
                final CoordinateSystem cs = crs.getCoordinateSystem();
                final AxisDirection[] axis = new AxisDirection[] {
                        cs.getAxis(xAxis).getDirection(),
                        cs.getAxis(yAxis).getDirection()
                };
                final AxisDirection[] normalized = axis.clone();
                if (false) {
                    // Normalize axis: Is it really a good idea?
                    // We should provide a rendering hint for configuring that.
                    Arrays.sort(normalized);
                    for (int i = normalized.length; --i >= 0;) {
                        normalized[i] = AxisDirections.absolute(normalized[i]);
                    }
                }
                normalized[1] = AxisDirections.opposite(normalized[1]); // Image's Y axis is downward.
                matrix = Matrices.createTransform(srcEnvelope, axis, dstEnvelope, normalized);
            } else {
                matrix = Matrices.createTransform(srcEnvelope, dstEnvelope);
            }
            return new RenderContext(AffineTransforms2D.castOrCopy(matrix), hints);
        }

        /**
         * Returns the number of elements per value at each position. This is
         * the maximum value plus 1 allowed in {@code getElements(...)} methods
         * invocation. The default implementation returns the number of sample
         * dimensions in the coverage.
         *
         * @return The number of sample dimensions.
         */
        @Override
        public int getNumElements() {
            return getSampleDimensions().size();
        }

        /**
         * Returns all values of a given element for a specified set of coordinates.
         * This method is automatically invoked at rendering time for populating an
         * image tile, providing that the rendered image is created using the
         * "{@link ImageFunctionDescriptor ImageFunction}" operator and the image
         * type is not {@code double}. The default implementation invokes
         * {@link AbstractCoverage#evaluate(DirectPosition,float[])} recursively.
         *
         * @param startX The X coordinate of the upper left location to evaluate.
         * @param startY The Y coordinate of the upper left location to evaluate.
         * @param deltaX The horizontal increment.
         * @param deltaY The vertical increment.
         * @param countX The number of points in the horizontal direction.
         * @param countY The number of points in the vertical direction.
         * @param dim    The sample dimension to evaluate.
         * @param real   Where the real parts of all elements will be returned.
         * @param imag   Where the imaginary parts of all elements will be returned, or {@code null}.
         */
        @Override
        public void getElements(final float startX, final float startY,
                                final float deltaX, final float deltaY,
                                final int   countX, final int   countY, final int dim,
                                final float[] real, final float[] imag)
        {
            final double[] tmp = new double[real.length];
            getElements(startX, startY, deltaX, deltaY, countX, countY, dim, tmp, null);
            for (int i=0; i<real.length; i++) real[i] = (float) tmp[i];
        }

        /**
         * Returns all values of a given element for a specified set of coordinates.
         * This method is automatically invoked at rendering time for populating an
         * image tile, providing that the rendered image is created using the
         * "{@link ImageFunctionDescriptor ImageFunction}" operator and the image
         * type is {@code double}. The default implementation invokes
         * {@link AbstractCoverage#evaluate(DirectPosition,double[])} recursively.
         *
         * @param startX The X coordinate of the upper left location to evaluate.
         * @param startY The Y coordinate of the upper left location to evaluate.
         * @param deltaX The horizontal increment.
         * @param deltaY The vertical increment.
         * @param countX The number of points in the horizontal direction.
         * @param countY The number of points in the vertical direction.
         * @param dim    The sample dimension to evaluate.
         * @param real   Where the real parts of all elements will be returned.
         * @param imag   Where the imaginary parts of all elements will be returned, or {@code null}.
         */
        @Override
        public void getElements(final double startX, final double startY,
                                final double deltaX, final double deltaY,
                                final int    countX, final int    countY, final int dim,
                                final double[] real, final double[] imag)
        {
            int index = 0;
            double[] buffer = null;
            // Clones the coordinate point in order to allow multi-thread invocation.
            final GeneralDirectPosition coordinate = new GeneralDirectPosition(this.coordinate);
            coordinate.coordinates[1] = startY;
            for (int j=0; j<countY; j++) {
                coordinate.coordinates[0] = startX;
                for (int i=0; i<countX; i++) {
                    buffer = evaluate(coordinate, buffer);
                    real[index++] = buffer[dim];
                    coordinate.coordinates[0] += deltaX;
                }
                coordinate.coordinates[1] += deltaY;
            }
        }

        @Override
        public Object getProperty(String name) {
            return java.awt.Image.UndefinedProperty;
        }

        @Override
        public String[] getPropertyNames() {
            return new String[0];
        }
    }

    /**
     * Display this coverage in a windows. This convenience method is used for debugging purpose.
     * The exact appareance of the windows and the tools provided may changes in future versions.
     *
     * @param  title The window title, or {@code null} for default value.
     * @param  xAxis Dimension to use for the <var>x</var> display axis.
     * @param  yAxis Dimension to use for the <var>y</var> display axis.
     *
     * @since 2.3
     */
    @Debug
    public void show(String title, final int xAxis, final int yAxis) {
        if (title == null || (title = title.trim()).isEmpty()) {
            title = String.valueOf(getName());
        }
        // In the following line, the constructor display immediately the viewer.
        new Viewer(title, getRenderableImage(xAxis, yAxis).createDefaultRendering());
    }

    /**
     * A trivial viewer implementation to be used by {@link AbstractCoverage#show(String,int,int)}
     * method.
     * <p>
     * <strong>Implementation note:</strong>
     * We use AWT Frame, not Swing JFrame, because {@link ScrollingImagePane} is an AWT
     * component. Swing is an overhead in this context without clear benefic. Note also
     * that {@code ScrollingImagePanel} includes the scroll bar, so there is no need to
     * put this component in an other {@code JScrollPane}.
     */
    private static final class Viewer extends WindowAdapter implements Runnable {
        /**
         * The frame to dispose once closed.
         */
        private final Frame frame;

        /**
         * Displays the specified image in a window with the specified title.
         */
        @SuppressWarnings("deprecation")
        public Viewer(final String title, final RenderedImage image) {
            final int width  = Math.max(Math.min(image.getWidth(),  800), 24);
            final int height = Math.max(Math.min(image.getHeight(), 600), 24);
            frame = new Frame(title);
            frame.add(new javax.media.jai.widget.ScrollingImagePanel(image, width, height));
            frame.addWindowListener(this);
            EventQueue.invokeLater(this);
        }

        /**
         * Display the window in the event queue.
         * Required because 'pack()' is invoked before 'setVisible(true)'.
         */
        @Override
        public void run() {
            frame.pack();
            frame.setLocationByPlatform(true);
            frame.setVisible(true);
        }

        /**
         * Invoked when the user closes the window.
         */
        @Override
        public void windowClosing(WindowEvent e) {
            frame.removeWindowListener(this);
            frame.dispose();
        }
    }

    /**
     * Display this coverage in a windows. This convenience method is used for
     * debugging purpose. The exact appareance of the windows and the tools
     * provided may changes in future versions.
     *
     * @param  title The window title, or {@code null} for default value.
     *
     * @since 2.3
     */
    @Debug
    public void show(final String title) {
        show(title, 0, 1);
    }

    /**
     * Displays this coverage in a windows. This convenience method is used for debugging purpose.
     * The exact appareance of the windows and the tools provided may changes in future versions.
     */
    @Debug
    public void show() {
        show(null);
    }

    /**
     * Returns the default locale for logging, error messages, <i>etc</i>.
     *
     * @return The default locale for logging and error message.
     */
    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Returns a string representation of this coverage. This string is for
     * debugging purpose only and may change in future version.
     */
    @Override
    public String toString() {
        final StringWriter out = new StringWriter();
        out.write(Classes.getShortClassName(this));
        out.write("[\"");
        out.write(String.valueOf(getName()));
        out.write('"');
        final Envelope envelope = getEnvelope();
        if (envelope != null) {
            out.write(", ");
            out.write(envelope.toString());
        }
        final CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        if (crs != null) {
            out.write(", ");
            out.write(Classes.getShortClassName(crs));
            out.write("[\"");
            out.write(crs.getName().getCode());
            out.write("\"]");
        }
        out.write(']');
        final String lineSeparator = System.lineSeparator();
        final LineWriter filter = new LineWriter(out, lineSeparator + "\u2502   ");
        final List<SampleDimension> dims = getSampleDimensions();
        final int n = dims.size();
        try {
            filter.write(lineSeparator);
            for (int i=0; i<n; i++) {
                filter.write(dims.get(i).toString());
            }
            filter.flush();
        } catch (IOException exception) {
            // Should not happen
            throw new AssertionError(exception);
        }
        final StringBuffer buffer = out.getBuffer();
        buffer.setLength(buffer.lastIndexOf(lineSeparator) + lineSeparator.length());
        return buffer.toString();
    }

    /**
     * Provides a hint that a coverage will no longer be accessed from a reference in user space.
     * This can be used as a hint in situations where waiting for garbage collection would be
     * overly conservative. The results of referencing a coverage after a call to {@code dispose}
     * are undefined, except if this method returned {@code false}.
     * <p>
     * This method can work in a <cite>conservative</cite> mode or a <cite>forced</cite> mode,
     * determined by the {@code force} argument:
     *
     * <ul>
     *   <li><p>If {@code force} is {@code false} (the recommended value), this method may process
     *   only under some conditions. For example a grid coverage may dispose its planar image only
     *   if it has no {@linkplain PlanarImage#getSinks sinks}. This method returns {@code true} if
     *   it disposed all resources, or {@code false} if this method vetoed against the disposal.
     *   In the later case, this coverage can still be used.</p></li>
     *
     *   <li><p>If {@code force} is {@code true}, then this method processes inconditionnally and
     *   returns always {@code true}. This is a more risky behavior.</p></li>
     * </ul>
     * <p>
     * The conservative mode ({@code force = false}) performs its safety checks on a
     * <cite>best-effort</cite> basis, with no guarantees. Therefore, it would be wrong to write
     * a program that depended on the safety checks for its correctness. In case of doubt about
     * whatever this coverage still in use or not, it is safer to rely on the garbage collector.
     *
     * @param  force {@code true} for forcing an inconditionnal disposal, or {@code false} for
     *         performing a conservative disposal. The recommended value is {@code false}.
     * @return {@code true} if this method disposed at least some resources, or {@code false}
     *         if this method vetoed against the disposal.
     *
     * @see PlanarImage#dispose
     *
     * @since 2.4
     */
    public boolean dispose(boolean force) {
        return true;
    }

    static {
        SampleTranscoder.register(JAI.getDefaultInstance());
    }


    /**
     * Returns the source data for a grid coverage. If the {@code GridCoverage} was produced from
     * an underlying dataset, the returned list is an empty list. If the {@code GridCoverage} was
     * produced using {@code GridCoverageProcessor}, then it should
     * return the source grid coverage of the one used as input to {@code GridCoverageProcessor}.
     * In general the {@code getSources()} method is intended to return the original
     * {@code GridCoverage} on which it depends. This is intended to allow applications
     * to establish what {@code GridCoverage}s will be affected when others are updated,
     * as well as to trace back to the "raw data".
     */
    public List<org.apache.sis.coverage.grid.GridCoverage> getSources() {
        // Reminder: 'sources' is always null after deserialization.
        if (sources != null) {
            return sources;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Constructs an error message for a point that can not be evaluated.
     * This is used for formatting error messages.
     *
     * @param  point The coordinate point to format.
     * @param  outside {@code true} if the evaluation failed because the given point is outside
     *         the coverage, or {@code false} if it failed for an other (unknown) reason.
     * @return An error message.
     *
     * @since 2.5
     */
    protected String formatEvaluateError(final Point2D point, final boolean outside) {
        return formatEvaluateError((DirectPosition) new DirectPosition2D(point.getX(), point.getY()), outside);
    }

    /**
     * Constructs an error message for a position that can not be evaluated.
     * This is used for formatting error messages.
     *
     * @param  point The coordinate point to format.
     * @param  outside {@code true} if the evaluation failed because the given point is outside
     *         the coverage, or {@code false} if it failed for an other (unknown) reason.
     * @return An error message.
     *
     * @since 2.5
     */
    protected String formatEvaluateError(final DirectPosition point, final boolean outside) {
        final Locale locale = getLocale();
        return Errors.getResources(locale).getString(outside ?
                Errors.Keys.PointOutsideCoverage_1 : Errors.Keys.CantEvaluateForCoordinate_1, toString(point, locale));
    }

    /**
     * Constructs a string for the specified point.
     * This is used for formatting error messages.
     *
     * @param  point The coordinate point to format.
     * @param  locale The locale for formatting numbers.
     * @return The coordinate point as a string, without '(' or ')' characters.
     */
    static String toString(final Point2D point, final Locale locale) {
        return toString((DirectPosition) new DirectPosition2D(point.getX(), point.getY()), locale);
    }

    /**
     * Constructs a string for the specified point.
     * This is used for formatting error messages.
     *
     * @param  point The coordinate point to format.
     * @param  locale The locale for formatting numbers.
     * @return The coordinate point as a string, without '(' or ')' characters.
     */
    static String toString(final DirectPosition point, final Locale locale) {
        final StringBuffer buffer = new StringBuffer();
        final FieldPosition dummy = new FieldPosition(0);
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        final int       dimension = point.getDimension();
        for (int i=0; i<dimension; i++) {
            if (i != 0) {
                buffer.append(", ");
            }
            format.format(point.getOrdinate(i), buffer, dummy);
        }
        return buffer.toString();
    }
}
