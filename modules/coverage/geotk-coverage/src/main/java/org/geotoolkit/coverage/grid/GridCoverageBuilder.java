/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Arrays;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.IndexColorModel;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.measure.unit.Unit;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.media.jai.RenderedImageAdapter;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.util.FactoryException;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.lang.Builder;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.measure.Units;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.ImmutableEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.referencing.operation.transform.LinearTransform1D;
import org.geotoolkit.referencing.operation.transform.LogarithmicTransform1D;
import org.geotoolkit.internal.image.ImageUtilities;
import org.geotoolkit.resources.Errors;


/**
 * Helper class for the creation of {@link GridCoverage2D} instances. This builder can creates the
 * parameters to be given to {@linkplain GridCoverage2D#GridCoverage2D(CharSequence, PlanarImage,
 * GridGeometry2D, GridSampleDimension[], GridCoverage[], Map, Hints) grid coverage constructor}
 * from simpler parameters given to this builder.
 * <p>
 * The builder supports the following properties:
 * <p>
 * <table border="1" cellspacing="0" cellpadding="1">
 *   <tr bgcolor="lightblue">
 *     <th>Properties</th>
 *     <th>Can be set from</th>
 *     <th>Default value</th>
 *   </tr><tr>
 *     <td>&nbsp;{@link #crs}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setCoordinateReferenceSystem(CoordinateReferenceSystem) CRS instance} or
 *               {@linkplain #setCoordinateReferenceSystem(String) authority code}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #envelope}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setEnvelope(Envelope) Envelope instance} or
 *               {@linkplain #setEnvelope(double[]) ordinate values}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #extent}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setExtent(GridEnvelope) Grid envelope instance} or
 *               {@linkplain #setExtent(int[]) spans}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #image Image} width and height&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #pixelAnchor}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setPixelAnchor(PixelInCell) Code list value}&nbsp;</td>
 *     <td>&nbsp;&nbsp;{@link PixelInCell#CELL_CENTER}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #gridToCRS}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setGridToCRS(MathTransform) Transform instance} or
 *               {@linkplain #setGridToCRS(double, double, double, double, double, double) affine transform coefficients}&nbsp;</td>
 *     <td>&nbsp;{@linkplain org.geotoolkit.referencing.operation.builder.GridToEnvelopeMapper Computed} from the above&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #gridGeometry}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setGridGeometry(GridGeometry) Grid geometry instance}&nbsp;</td>
 *     <td>&nbsp;{@linkplain GridGeometry2D#GridGeometry2D(GridEnvelope, PixelInCell, MathTransform, CoordinateReferenceSystem, Hints) Computed} from the above&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #numBands}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setNumBands(int) Positive integer}&nbsp;</td>
 *     <td>&nbsp;1&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link Variable#name Variable.name}&nbsp;</td>
 *     <td>&nbsp;{@linkplain Variable#setName(CharSequence) Character sequence}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link Variable#unit Variable.unit}&nbsp;</td>
 *     <td>&nbsp;{@linkplain Variable#setUnit(Unit) Unit instance} or
 *               {@linkplain Variable#setUnit(String) unit symbol}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link Variable#sampleRange Variable.sampleRange}&nbsp;</td>
 *     <td>&nbsp;{@linkplain Variable#setSampleRange(NumberRange) Range instance} or
 *               {@linkplain Variable#setSampleRange(int, int) lower and upper values}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link Variable#transform Variable.transform}&nbsp;</td>
 *     <td>&nbsp;{@linkplain Variable#setTransform(MathTransform1D) Transform instance} or
 *               {@linkplain Variable#setLinearTransform(double, double) coefficients}&nbsp;</td>
 *     <td>&nbsp;{@link LinearTransform1D#IDENTITY}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link Variable#sampleDimension Variable.sampleDimension}&nbsp;</td>
 *     <td>&nbsp;{@linkplain Variable#setSampleDimension(SampleDimension) Sample dimension instance} or
 *               {@linkplain #setSampleDimensions(SampleDimension[]) array}&nbsp;</td>
 *     <td>&nbsp;Computed from the above&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;Tile layout&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setTileSize(Dimension) Tile size} and/or
 *               {@linkplain #setTileGridOffset(Point) tile grid offset}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #image}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setRenderedImage(RenderedImage) Rendered image instance}&nbsp;</td>
 *     <td>&nbsp;Empty image&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #sources}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setSources(GridCoverage[]) Array of grid coverages}&nbsp;</td>
 *     <td>&nbsp;Empty array&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #properties}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setProperties(Map) Map of properties}&nbsp;</td>
 *     <td>&nbsp;Empty map&nbsp;</td>
 *   </tr>
 * </table>
 *
 * {@section Envelope vs <cite>grid to CRS</cite> transform}
 * The preferred way to define the geographic location of a grid coverage is to
 * {@linkplain #setGridToCRS(MathTransform) specify the grid to CRS transform} or the
 * {@linkplain #setGridGeometry(GridGeometry) grid geometry}. However as a convenience,
 * this builder also {@linkplain #setEnvelope(Envelope) accepts envelopes}. In such case,
 * this builder assumes that axis order in the supplied image matches exactly axis order in
 * the supplied envelope. In other words, in the usual case where axis order in the image is
 * (<var>column</var>, <var>row</var>), then the envelope should probably have a
 * (<var>longitude</var>, <var>latitude</var>) or (<var>easting</var>, <var>northing</var>)
 * axis order.
 * <p>
 * An exception to the above rule applies for CRS using exactly the following axis order:
 * ({@link AxisDirection#NORTH NORTH}|{@link AxisDirection#SOUTH SOUTH},
 * {@link AxisDirection#EAST EAST}|{@link AxisDirection#WEST WEST}). An example of such CRS is
 * {@code EPSG:4326}. This builder will interchange automatically the (<var>y</var>,<var>x</var>)
 * axes for those CRS.
 * <p>
 * See {@link org.geotoolkit.referencing.operation.builder.GridToEnvelopeMapper} for more information
 * about the heuristic rules. If more control on axis order and direction reversal is wanted, specify
 * explicitely the <cite>grid to CRS</cite> transform or the <cite>grid geometry</cite> instead than
 * an envelope.
 *
 * {@section Examples}
 * Creates a grid coverage from the specified {@linkplain RenderedImage image} and
 * {@linkplain Envelope envelope}. An {@linkplain AffineTransform affine transform} will
 * be computed automatically from the specified envelope using heuristic rules described
 * in the above javadoc.
 *
 * {@preformat java
 *     GridCoverageBuilder builder = new GridCoverageBuilder();
 *     builder.setName("My coverage"); // Optional
 *     builder.setEnvelope(envelope);
 *     builder.setRenderedImage(image);
 *     GridCoverage2D coverage = builder.getGridCoverage2D();
 * }
 *
 * Creates a grid coverage from the specified {@linkplain RenderedImage image} and
 * {@linkplain GridGeometry2D#getGridToCRS() grid to CRS} transform. In this example,
 * the pixels size is 1000&times;1000 metres and the CRS is <cite>Mercator</cite> ("EPSG:3395").
 * The {@linkplain Envelope envelope} will be inferred from the grid geometry.
 *
 * {@preformat java
 *     GridCoverageBuilder builder = new GridCoverageBuilder();
 *     builder.setName("My coverage"); // Optional
 *     builder.setCoordinateReferenceSystem("EPSG:3395");
 *     builder.setGridToCRS(AffineTransform.getScaleInstance(1000, -1000));
 *     builder.setSampleDimensions(myFirstBand, mySecondBand); // Optional
 *     builder.setRenderedImage(image);
 *     GridCoverage2D coverage = builder.getGridCoverage2D();
 * }
 *
 * Creates a 600&times;400 pixels image from -40°S to 40°N and -60°W to 60°E.
 * Uses the 0 pixel value for "<cite>no data</cite>", and pixel values in the
 * [1&hellip;255] range for elevation values in metres. Then draw something on
 * the image using Java2D API:
 *
 * {@preformat java
 *     GridCoverageBuilder builder = new GridCoverageBuilder();
 *     builder.setCoordinateReferenceSystem("CRS:84");
 *     builder.setEnvelope(-60, -40, 60, 40);
 *     builder.setExtent(600, 400);
 *
 *     // Use sample values in the range 1 inclusive to 255 exclusive
 *     // and define elevation in metres as (sample value) / 10.
 *     builder.variable(0).setName("Elevation");
 *     builder.variable(0).setUnit(SI.METRE);
 *     builder.variable(0).setSampleRange(1, 256);
 *     builder.variable(0).setLinearTransform(0.1, 0);
 *     builder.variable(0).addNodataValue("No data", 0);
 *
 *     // Gets a 600×400 pixels (the extent) image, then draw something on it.
 *     Graphics2D gr = (Graphics2D) builder.createGraphics();
 *     gr.draw(...);
 *     gr.dispose();
 *
 *     // Gets the coverage.
 *     GridCoverage2D coverage = builder.getGridCoverage2D();
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @since 2.5
 *
 * @version 3.20
 * @module
 */
public class GridCoverageBuilder extends Builder<GridCoverage> {
    /**
     * The coverage name, or {@code null} if unspecified. This field is non-null only if the name
     * has been {@linkplain #setName(CharSequence) explicitely specified} by the user. The values
     * inferred from other attributes are not stored in this field.
     *
     * @see #getName()
     * @see #setName(CharSequence)
     *
     * @since 3.20
     */
    protected CharSequence name;

    /**
     * The coordinate reference system, or {@code null} if unspecified. This field is non-null only
     * if the CRS has been {@linkplain #setCoordinateReferenceSystem(CoordinateReferenceSystem)
     * explicitely specified} by the user. The values inferred from other attributes are not stored
     * in this field.
     *
     * @see #getCoordinateReferenceSystem()
     * @see #setCoordinateReferenceSystem(CoordinateReferenceSystem)
     * @see #setCoordinateReferenceSystem(String)
     *
     * @since 3.20
     */
    protected CoordinateReferenceSystem crs;

    /**
     * The envelope, including coordinate reference system, or {@code null} if unspecified. This
     * field is non-null only if the envelope has been {@linkplain #setEnvelope(Envelope) explicitely
     * specified} by the user. The values inferred from other attributes are not stored in this field.
     *
     * @see #getEnvelope()
     * @see #setEnvelope(Envelope)
     * @see #setEnvelope(double[])
     *
     * @since 3.20 (derived from 2.5)
     */
    protected Envelope envelope;

    /**
     * The grid extent, or {@code null} if unspecified. This field is non-null only if the extent
     * has been {@linkplain #setExtent(GridEnvelope) explicitely specified} by the user. The values
     * inferred from other attributes are not stored in this field.
     *
     * @see #getExtent()
     * @see #setExtent(GridEnvelope)
     * @see #setExtent(int[])
     *
     * @since 3.20
     */
    protected GridEnvelope extent;

    /**
     * The <cite>grid to CRS</cite> transform, or {@code null} if unspecified. This field is non-null
     * only if the transform has been {@linkplain #setGridToCRS(MathTransform) explicitely specified}
     * by the user. The values inferred from other attributes are not stored in this field.
     *
     * @see #getGridToCRS()
     * @see #setGridToCRS(MathTransform)
     * @see #setGridToCRS(double, double, double, double, double, double)
     *
     * @since 3.20
     */
    protected MathTransform gridToCRS;

    /**
     * Whatever the {@link #gridToCRS} transform maps pixel center or pixel corner. This field is
     * non-null only if it has been {@linkplain #setPixelAnchor(PixelInCell) explicitely specified}
     * by the user. The values inferred from other attributes are not stored in this field.
     *
     * @see #getPixelAnchor()
     * @see #setPixelAnchor(PixelInCell)
     *
     * @since 3.20
     */
    protected PixelInCell pixelAnchor;

    /**
     * The grid geometry, or {@code null} if unspecified. This field is non-null only if the grid
     * geometry has been {@linkplain #setGridGeometry(GridGeometry) explicitely specified} by the
     * user. The values inferred from other attributes are not stored in this field.
     *
     * @see #getGridGeometry()
     * @see #setGridGeometry(GridGeometry)
     *
     * @since 3.20
     */
    protected GridGeometry gridGeometry;

    /**
     * The grid geometry calculated from other properties, or {@code null} if none.
     *
     * @since 3.20
     */
    private transient GridGeometry cachedGridGeometry;

    /**
     * Number of bands (sample dimensions), or 0 if unspecified. This field is non-zero only if
     * the number of bands has been {@linkplain #setNumBands(int) explicitely specified} by the
     * user, or {@linkplain #variable(int) variables were created}. The values inferred from other
     * attributes are not stored in this field.
     *
     * @see #getNumBands()
     * @see #setNumBands(int)
     *
     * @since 3.20
     */
    protected int numBands;

    /**
     * The list of variables created by the user, or {@code null} if unspecified. If non-null,
     * each element in this list will determine a {@linkplain GridSampleDimension grid sample
     * dimension} in the coverage to create.
     *
     * @see #variable(int)
     *
     * @since 3.20 (derived from 2.5)
     */
    private Variable[] variables;

    /**
     * The sample dimensions created from the {@link #variables} array, or {@code null}
     * if none or not yet computed. If every sample dimensions are actually instances of
     * {@link GridSampleDimension}, then the array type is {@code GridSampleDimension[]}.
     *
     * @see #getSampleDimensions()
     *
     * @since 3.20
     */
    private transient SampleDimension[] sampleDimensions;

    /**
     * The {@linkplain #image} tile size and grid offset, or {@code null} if the image is untiled.
     * This field is non-null only if the tile size or grid offset has been explicitely specified
     * by the user. The values inferred from other attributes are not stored in this field.
     * <p>
     * The rectangle can be understood as the bounds of tile at index (0,0). Note that this tile
     * doesn't need to exist.
     *
     * @see #getTileSize()
     * @see #setTileSize(Dimension)
     * @see #getTileGridOffset()
     * @see #setTileGridOffset(Point)
     *
     * @since 3.20
     */
    private TileLayout tileLayout;

    /**
     * The aggregation of tile size and tile grid offset. This can be understood as the bounds
     * of the tile at tile index (0,0). Note that this tile doesn't need to exist.
     *
     * @since 3.20
     */
    @SuppressWarnings("serial")
    private static final class TileLayout extends Rectangle {
        /**
         * {@code true} if the grid offset is defined. If {@code false}, the
         * {@linkplain #x x} and {@linkplain #y y} ordinate values shall be ignored.
         */
        private boolean hasOffset;

        /**
         * Creates a new tile layout initialized to the given grid tile offset.
         */
        TileLayout(final Point location) {
            super(location);
            hasOffset = true;
        }

        /**
         * Creates a new tile layout initialized to the given tile size.
         */
        TileLayout(final Dimension size) {
            super(size);
        }

        /**
         * Resets this tile layout to an "empty" state (i.e. all attributes are marked
         * as unspecified).
         */
        public void reset() {
            x         = 0;
            y         = 0;
            width     = 0;
            height    = 0;
            hasOffset = false;
        }

        /**
         * Returns the tile grid offset only if defined, or {@code null} otherwise.
         */
        @Override
        public Point getLocation() {
            return hasOffset ? super.getLocation() : null;
        }

        /**
         * Sets the tile grid offset to the given location. If the given argument is null,
         * unset the grid offset.
         */
        @Override
        public void setLocation(final Point location) {
            if (hasOffset = (location != null)) {
                super.setLocation(location);
            }
        }

        /**
         * Returns the tile size only if non-empty, or {@code null} otherwise.
         */
        @Override
        public Dimension getSize() {
            return (width > 0 && height > 0) ? super.getSize() : null;
        }

        /**
         * Sets the size to the given dimension. If the given argument is null,
         * then unset the tile size.
         */
        @Override
        public void setSize(final Dimension size) {
            if (size != null) {
                ArgumentChecks.ensureStrictlyPositive("width",  size.width);
                ArgumentChecks.ensureStrictlyPositive("height", size.height);
                super.setSize(size);
            } else {
                width  = 0;
                height = 0;
            }
        }
    }

    /**
     * The rendered image, or {@code null} if not yet computed. This image can either be
     * {@linkplain #setRenderedImage(RenderedImage) specified explicitely} by the user or
     * created from other properties.
     * <p>
     * The preferred implementation class is {@link BufferedImage}. However in some cases this
     * builder may instantiate other kind of images, like {@link javax.media.jai.TiledImage}.
     *
     * @see #getRenderedImage()
     * @see #setRenderedImage(RenderedImage)
     *
     * @since 3.20 (derived from 2.5)
     */
    protected RenderedImage image;

    /**
     * The grid coverage. Will be created only when first needed.
     */
    private GridCoverage2D coverage;

    /**
     * An optional array of sources to be associated with the grid coverage,
     * or {@code null} if none. Those sources will be given to the
     * {@linkplain GridCoverage2D#GridCoverage2D(CharSequence, PlanarImage, GridGeometry2D,
     * GridSampleDimension[], GridCoverage[], Map, Hints) grid coverage constructor} without
     * any processing by this class.
     *
     * @see #getSources()
     * @see #setSources(GridCoverage[])
     *
     * @since 3.20
     */
    protected GridCoverage[] sources;

    /**
     * An optional map of properties to be associated with the grid coverage,
     * or {@code null} if none. Those properties will be given to the
     * {@linkplain GridCoverage2D#GridCoverage2D(CharSequence, PlanarImage, GridGeometry2D,
     * GridSampleDimension[], GridCoverage[], Map, Hints) grid coverage constructor} without
     * any processing by this builder class.
     *
     * @see #getProperties()
     * @see #setProperties(Map)
     *
     * @since 3.20
     */
    protected Map<?,?> properties;

    /**
     * Optional hints for fetching factories, or {@code null} if none. Those hints can be
     * specified at {@linkplain #GridCoverageBuilder(Hints) builder construction time}.
     *
     * @since 3.20
     */
    protected final Hints hints;

    /**
     * Creates a uninitialized builder. All fields values are {@code null}.
     */
    public GridCoverageBuilder() {
        this.hints = null;
    }

    /**
     * Creates a uninitialized builder using the given hints
     *
     * @param hints Optional hints for fetching factories, or {@code null} if none.
     *
     * @since 3.20
     */
    public GridCoverageBuilder(final Hints hints) {
        if (XCollections.isNullOrEmpty(hints)) {
            this.hints = null;
        } else {
            this.hints = new Hints(hints);
        }
    }

    /**
     * Returns the coverage name.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The value defined by the last call to {@link #setName(CharSequence)}.</li>
     *   <li>In an iteration over all {@linkplain #variable(int) variables} (if any), the
     *       first non-null value returned by {@link Variable#getName() Variable.getName()}.</li>
     * </ul>
     * <p>
     * As a consequence of the above, the {@linkplain GridCoverage2D#getName() coverage name} will
     * be the name of the first {@linkplain #getSampleDimensions() sample dimension}, unless a
     * coverage name is explicitely given.
     *
     * @return The coverage name, or {@code null}.
     *
     * @see GridCoverage2D#getName()
     *
     * @since 3.20
     */
    public CharSequence getName() {
        CharSequence value = name;
        if (value == null) {
            final Variable[] variables = this.variables;
            for (int i=0; i<numBands; i++) {
                final Variable var = variables[i];
                if (var != null) {
                    value = var.getName();
                    if (value != null) {
                        break;
                    }
                }
            }
        }
        return value;
    }

    /**
     * Sets the coverage name. The given name is typically (but not restricted to) a
     * {@link String} or {@link org.opengis.util.InternationalString} instance.
     *
     * @param name The new name, or {@code null}.
     *
     * @since 3.20
     */
    public void setName(final CharSequence name) {
        this.name = name;
        coverage = null;
    }

    /**
     * Convenience method for checking object dimension validity.
     * This method is usually invoked for argument checking.
     *
     * @param  name The name of the argument to check.
     * @param  dimension The object dimension.
     * @param  expectedDimension The Expected dimension for the object.
     * @throws MismatchedDimensionException if the object doesn't have the expected dimension.
     */
    private static void ensureDimensionMatch(final String name, final int dimension,
            final int expectedDimension) throws MismatchedDimensionException
    {
        if (dimension != expectedDimension) {
            throw new MismatchedDimensionException(Errors.format(Errors.Keys.MISMATCHED_DIMENSION_$3,
                        name, dimension, expectedDimension));
        }
    }

    /**
     * Returns {@code true} if the property identified by the given flag is defined in the
     * grid geometry. If the grid geometry is not an instance of {@link GeneralGridGeometry},
     * then only the {@code EXTENT} and {@code GRID_TO_CRS} properties are assumed defined.
     *
     * @param  gridGeometry The grid geometry to test, or {@code null}.
     * @param  flag One of the {@link GeneralGridGeometry} constants.
     * @return {@code true} if the given property is defined.
     */
    private boolean isDefined(final int flag) {
        final GridGeometry gridGeometry = this.gridGeometry;
        if (gridGeometry == null) {
            return false;
        }
        if (gridGeometry instanceof GeneralGridGeometry) {
            return ((GeneralGridGeometry) gridGeometry).isDefined(flag);
        } else {
            return (flag & (GeneralGridGeometry.EXTENT | GeneralGridGeometry.GRID_TO_CRS)) == flag;
        }
    }

    /**
     * Returns the current coordinate reference system.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The value defined by the last call to {@link #setCoordinateReferenceSystem(CoordinateReferenceSystem)}.</li>
     *   <li>The {@linkplain #envelope} CRS.</li>
     *   <li>The {@linkplain #gridGeometry grid geometry} CRS.</li>
     * </ul>
     *
     * @return The current CRS, or {@code null} if unspecified and can not be inferred.
     *
     * @see #crs
     * @see Envelope#getCoordinateReferenceSystem()
     * @see GridGeometry2D#getCoordinateReferenceSystem()
     * @see GridCoverage2D#getCoordinateReferenceSystem()
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        final CoordinateReferenceSystem crs = this.crs;
        if (crs == null) {
            // We do not need to check for non-null CRS because the setter methods in
            // this builder have automatically set the envelope CRS when possible.
            final Envelope envelope = this.envelope;
            if (envelope != null) {
                return envelope.getCoordinateReferenceSystem();
            }
            if (isDefined(GeneralGridGeometry.CRS)) {
                return ((GeneralGridGeometry) gridGeometry).getCoordinateReferenceSystem();
            }
        }
        return crs;
    }

    /**
     * Sets the coordinate reference system to the specified value. If an envelope
     * has been {@linkplain #setEnvelope(Envelope) explicitely defined},
     * it will be reprojected to the new CRS.
     *
     * {@section Precedence}
     * If a grid geometry has been {@linkplain #setGridGeometry(GridGeometry) explicitely set}
     * and {@linkplain GeneralGridGeometry#getCoordinateReferenceSystem() defines a CRS}, then
     * that later CRS will have precedence for the creation of {@link GridCoverage2D} instances.
     *
     * @param  crs The new CRS to use, or {@code null}.
     * @throws IllegalArgumentException if the given CRS is illegal for the
     *         {@linkplain #getEnvelope() current envelope}.
     *
     * @see #crs
     * @see Envelopes#transform(Envelope, CoordinateReferenceSystem)
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs)
            throws IllegalArgumentException
    {
        if (crs != null) {
            final int dimension = crs.getCoordinateSystem().getDimension();
            final MathTransform gridToCRS = this.gridToCRS;
            if (gridToCRS != null) {
                ensureDimensionMatch("crs", dimension, gridToCRS.getSourceDimensions());
            }
            final Envelope oldEnvelope = envelope;
            if (oldEnvelope != null) {
                ensureDimensionMatch("crs", dimension, oldEnvelope.getDimension());
                try {
                    envelope = Envelopes.transform(oldEnvelope, crs);
                } catch (TransformException exception) {
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), exception);
                }
                if (envelope.getCoordinateReferenceSystem() != crs) {
                    envelope = new ImmutableEnvelope(crs, envelope);
                }
            }
        }
        this.crs = crs; // Assign only after success.
        gridGeometryChanged();
    }

    /**
     * Sets the coordinate reference system to the specified authority code. This method gives a
     * preference to axes in (<var>longitude</var>, <var>latitude</var>) order. This convenience
     * method is equivalent to the following code (omitting exception handling):
     *
     * {@preformat java
     *     setCoordinateReferenceSystem(CRS.decode(code, true));
     * }
     *
     * See {@link #setCoordinateReferenceSystem(CoordinateReferenceSystem)} for information about
     * precedence.
     *
     * @param  code The authority code of the CRS to use.
     * @throws IllegalArgumentException if the given authority code is illegal.
     *
     * @see #crs
     * @see CRS#decode(String, boolean)
     */
    public void setCoordinateReferenceSystem(final String code) throws IllegalArgumentException {
        final CoordinateReferenceSystem crs;
        try {
            crs = CRS.decode(code, true);
        } catch (FactoryException exception) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), exception);
        }
        setCoordinateReferenceSystem(crs);
    }

    /**
     * Returns the current envelope.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The value defined by the last call to {@link #setEnvelope(Envelope)}.</li>
     *   <li>The {@linkplain #gridGeometry grid geometry} envelope.</li>
     * </ul>
     *
     * @return A copy of the current envelope, or {@code null} if unspecified and can not be inferred.
     *
     * @see #envelope
     * @see GridGeometry2D#getEnvelope()
     * @see GridCoverage2D#getEnvelope()
     */
    public Envelope getEnvelope() {
        final Envelope envelope = this.envelope;
        if (envelope != null) {
            if (envelope instanceof Cloneable) {
                return (Envelope) ((Cloneable) envelope).clone();
            }
        } else {
            if (isDefined(GeneralGridGeometry.ENVELOPE)) {
                return ((GeneralGridGeometry) gridGeometry).getEnvelope();
            }
        }
        return envelope;
    }

    /**
     * Sets the envelope to the specified value. If a CRS has been
     * {@linkplain #setCoordinateReferenceSystem(CoordinateReferenceSystem) explicitely defined},
     * then the given envelope will be reprojected to that CRS.
     * <p>
     * <strong>This method is not recommended</cite>, since the creation of a grid coverage from
     * an envelope implies some arbitrary choices. Those arbitrary choices are implemented as
     * heuristic rules documented in this <a href="#overview">class javadoc</a>. The recommended
     * usage is to {@linkplain #setGridToCRS(MathTransform) specify the grid to CRS transform} or
     * the {@linkplain #setGridGeometry(GridGeometry) grid geometry} instead, and specify an
     * envelope only when the other information are not available.
     *
     * {@section Precedence}
     * If a grid geometry has been {@linkplain #setGridGeometry(GridGeometry) explicitely set}
     * and {@linkplain GeneralGridGeometry#getEnvelope() defines an envelope}, then that later
     * envelope will have precedence for the creation of {@link GridCoverage2D} instances.
     *
     * @param  envelope The new envelope to use, or {@code null}.
     * @throws IllegalArgumentException if the envelope is illegal for the CRS.
     *
     * @see #envelope
     * @see Envelopes#transform(Envelope, CoordinateReferenceSystem)
     */
    public void setEnvelope(final Envelope envelope) throws IllegalArgumentException {
        Envelope newValue = envelope;
        if (newValue != null) {
            final int dimension = newValue.getDimension();
            final MathTransform gridToCRS = this.gridToCRS;
            if (gridToCRS != null) {
                ensureDimensionMatch("envelope", dimension, gridToCRS.getTargetDimensions());
            }
            final CoordinateReferenceSystem crs = this.crs;
            if (crs != null) {
                ensureDimensionMatch("envelope", dimension, crs.getCoordinateSystem().getDimension());
                try {
                    newValue = Envelopes.transform(newValue, crs);
                } catch (TransformException exception) {
                    throw new IllegalArgumentException(Errors.format(
                            Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), exception);
                }
                if (newValue.getCoordinateReferenceSystem() != crs) {
                    newValue = new ImmutableEnvelope(crs, newValue);
                }
            }
            if (newValue == envelope) {
                newValue = ImmutableEnvelope.castOrCopy(newValue);
            }
        }
        this.envelope = newValue;
        gridGeometryChanged();
    }

    /**
     * Sets the envelope to the specified values, which must be the lower corner coordinates
     * followed by upper corner coordinates. The number of arguments provided shall be twice
     * the envelope dimension, and minimum shall not be greater than maximum.
     * <blockquote>
     * <b>Example:</b>
     * (<var>x</var><sub>min</sub>, <var>y</var><sub>min</sub>, <var>z</var><sub>min</sub>,
     *  <var>x</var><sub>max</sub>, <var>y</var><sub>max</sub>, <var>z</var><sub>max</sub>)
     * </blockquote>
     * See {@link #setEnvelope(Envelope)} for information about recommended practices and
     * precedence.
     *
     * @param  ordinates The ordinates of the new envelope to use.
     * @throws IllegalArgumentException if the envelope is illegal.
     */
    public void setEnvelope(final double... ordinates) throws IllegalArgumentException {
        final CoordinateReferenceSystem crs = this.crs;
        final GeneralEnvelope env;
        if (crs != null) {
            env = new GeneralEnvelope(crs);
        } else {
            env = new GeneralEnvelope(ordinates.length / 2);
        }
        env.setEnvelope(ordinates);
        setEnvelope(env);
    }

    /**
     * Returns the current grid extent.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The value defined by the last call to {@link #setExtent(GridEnvelope)}.</li>
     *   <li>The {@linkplain #gridGeometry grid geometry} extent.</li>
     *   <li>The {@linkplain #image} bounds (including {@linkplain RenderedImage#getMinX() minX}
     *       and {@linkplain RenderedImage#getMinY() minY} values).</li>
     * </ul>
     *
     * @return The current grid extent, or {@code null} if unspecified and can not be inferred.
     *
     * @see #extent
     * @see GridGeometry2D#getExtent()
     * @see org.opengis.coverage.grid.Grid#getExtent()
     *
     * @since 3.20
     */
    public GridEnvelope getExtent() {
        final GridEnvelope extent = this.extent;
        if (extent != null) {
            if (extent instanceof Cloneable) {
                return (GridEnvelope) ((Cloneable) extent).clone();
            }
        } else {
            if (isDefined(GeneralGridGeometry.EXTENT)) {
                return gridGeometry.getExtent();
            }
            final RenderedImage image = this.image;
            if (image != null) {
                return new GeneralGridEnvelope(image, getGridDimension(2));
            }
        }
        return extent;
    }

    /**
     * Sets the grid extent to the specified value.
     *
     * {@section Precedence}
     * If a grid geometry has been {@linkplain #setGridGeometry(GridGeometry) explicitely set}
     * and {@linkplain GeneralGridGeometry#getExtent() defines an extent}, then that later extent
     * will have precedence for the creation of {@link GridCoverage2D} instances.
     *
     * @param  extent The new grid extent to use, or {@code null}.
     * @throws MismatchedDimensionException If the extent dimension is not equal to the
     *         <cite>grid to CRS</cite> source dimensions.
     *
     * @since 3.20
     */
    public void setExtent(final GridEnvelope extent) throws MismatchedDimensionException {
        GridEnvelope newValue = extent;
        if (extent != null) {
            final int dim = getGridDimension(-1);
            if (dim >= 0) {
                ensureDimensionMatch("extent", extent.getDimension(), dim);
            }
        }
        this.extent = newValue;
        gridGeometryChanged();
    }

    /**
     * Sets the grid extent to a grid envelope having the given span.
     * The {@linkplain GridEnvelope#getLow() low ordinate values} are set to 0.
     * <p>
     * This method is typically invoked for defining image dimension as below:
     *
     * {@preformat java
     *     setExtent(width, height);
     * }
     *
     * See {@link #setExtent(GridEnvelope)} for information about precedence.
     *
     * @param  span The span values for all dimensions.
     * @throws MismatchedDimensionException If the arguments contain negative span values, or the
     *         number of values is not equal to the <cite>grid to CRS</cite> source dimensions.
     *
     * @since 3.20
     */
    public void setExtent(final int... span) throws MismatchedDimensionException {
        setExtent(new GeneralGridEnvelope(new int[span.length], span, false));
    }

    /**
     * Returns the grid dimension, which is inferred from the {@link #gridGeometry} if possible,
     * or the {@link #gridToCRS} otherwise. The {@linkplain #extent} is not used because this
     * method is invoked by the {@code get/setExtent(...)} methods.
     */
    private int getGridDimension(final int defaultValue) {
        MathTransform tr = null;
        final GridGeometry gridGeometry = this.gridGeometry;
        if (gridGeometry != null) {
            if (gridGeometry instanceof GeneralGridGeometry) {
                return ((GeneralGridGeometry) gridGeometry).getDimension();
            } else {
                tr = gridGeometry.getGridToCRS();
            }
        }
        if (tr == null) {
            if ((tr = gridToCRS) == null) {
                return defaultValue;
            }
        }
        return tr.getSourceDimensions();
    }

    /**
     * Returns the current <cite>grid to CRS</cite> transform.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The value defined by the last call to {@link #setGridToCRS(MathTransform)}.</li>
     *   <li>The {@linkplain #gridGeometry grid geometry} transform.</li>
     * </ul>
     * <p>
     * Whatever the returned transform maps pixel centers or pixel corners depends on the
     * {@link #pixelAnchor} value.
     *
     * @return The <cite>grid to CRS</cite> transform, or {@code null} if unspecified and can not
     *         be inferred.
     *
     * @see #gridToCRS
     * @see GridGeometry2D#getGridToCRS()
     *
     * @since 3.20
     */
    public MathTransform getGridToCRS() {
        final MathTransform gridToCRS = this.gridToCRS;
        if (gridToCRS == null) {
            final GridGeometry gridGeometry = this.gridGeometry;
            if (gridGeometry != null) {
                final PixelInCell pixelAnchor = this.pixelAnchor;
                if (pixelAnchor == null) {
                    return gridGeometry.getGridToCRS();
                } else if (gridGeometry instanceof GeneralGridGeometry) {
                    return ((GeneralGridGeometry) gridGeometry).getGridToCRS(pixelAnchor);
                }
            }
        }
        return gridToCRS;
    }

    /**
     * Sets the <cite>grid to CRS</cite> transform. Whatever the transform maps pixel centers
     * or pixel corners depends on the {@link #pixelAnchor} value.
     *
     * {@section Restrictions}
     * <ul>
     *   <li>The number of {@linkplain MathTransform#getSourceDimensions() source dimensions}
     *       shall matches the grid {@linkplain #extent} dimensions (if any)</li>
     *   <li>The number of {@linkplain MathTransform#getTargetDimensions() target dimensions}
     *       shall matches the {@linkplain #crs} and {@linkplain #envelope} dimensions (if any).</li>
     * </ul>
     *
     * {@section Precedence}
     * If a grid geometry has been {@linkplain #setGridGeometry(GridGeometry) explicitely set}
     * and {@linkplain GeneralGridGeometry#getGridToCRS() defines a transform}, then that later
     * transform will have precedence for the creation of {@link GridCoverage2D} instances.
     *
     * @param  gridToCRS The new <cite>grid to CRS</cite> transform, or {@code null}.
     * @throws MismatchedDimensionException If the given transform is invalid, for example if the
     *         dimensions don't match.
     *
     * @since 3.20
     */
    public void setGridToCRS(final MathTransform gridToCRS) throws MismatchedDimensionException {
        if (gridToCRS != null) {
            final CoordinateReferenceSystem crs = this.crs;
            final GridEnvelope extent = this.extent;
            if (extent != null) {
                ensureDimensionMatch("gridToCRS", gridToCRS.getSourceDimensions(), extent.getDimension());
            }
            final Envelope envelope = this.envelope;
            if (crs != null || envelope != null) {
                ensureDimensionMatch("gridToCRS", gridToCRS.getTargetDimensions(),
                        (crs != null) ? crs.getCoordinateSystem().getDimension() : envelope.getDimension());
            }
        }
        this.gridToCRS = gridToCRS;
        gridGeometryChanged();
    }

    /**
     * Sets the <cite>grid to CRS</cite> transform. Whatever the transform maps pixel centers
     * or pixel corners depends on the {@link #pixelAnchor} value.
     * <p>
     * See {@link #setGridToCRS(MathTransform)} for information about restrictions and precedence.
     *
     * @param  gridToCRS The new <cite>grid to CRS</cite> transform, or {@code null}.
     * @throws MismatchedDimensionException If the current {@linkplain #extent} and
     *         {@linkplain #envelope} (if any) are not two-dimensional.
     *
     * @since 3.20
     */
    public void setGridToCRS(final AffineTransform gridToCRS) throws MismatchedDimensionException {
        setGridToCRS(gridToCRS != null ? MathTransforms.linear(gridToCRS) : null);
    }

    /**
     * Sets the <cite>grid to CRS</cite> transform from the given affine transform coefficients.
     * Whatever the transform maps pixel centers or pixel corners depends on the {@link #pixelAnchor}
     * value.
     * <p>
     * See {@link #setGridToCRS(MathTransform)} for information about restrictions and precedence.
     *
     * @param m00 the X coordinate scaling.
     * @param m10 the Y coordinate shearing.
     * @param m01 the X coordinate shearing.
     * @param m11 the Y coordinate scaling.
     * @param m02 the X coordinate translation.
     * @param m12 the Y coordinate translation.
     *
     * @see AffineTransform2D
     *
     * @throws MismatchedDimensionException If the current {@linkplain #extent} and
     *         {@linkplain #envelope} (if any) are not two-dimensional.
     *
     * @since 3.20
     */
    public void setGridToCRS(double m00, double m10, double m01, double m11, double m02, double m12)
            throws MismatchedDimensionException
    {
        setGridToCRS((MathTransform) new AffineTransform2D(m00, m10, m01, m11, m02, m12));
    }

    /**
     * Returns whatever the {@linkplain #gridToCRS grid to CRS} transform maps pixel center or
     * pixel corner. The OGC 01-004 specification mandates pixel center, but some computation
     * are more convenient when mapping pixel corners.
     *
     * @return The "pixel in cell" policy. If unspecified, default value is
     *         {@link PixelInCell#CELL_CENTER}.
     *
     * @see #pixelAnchor
     * @see GeneralGridGeometry#GeneralGridGeometry(GridEnvelope, PixelInCell, MathTransform, CoordinateReferenceSystem)
     *
     * @since 3.20
     */
    public PixelInCell getPixelAnchor() {
        final PixelInCell pixelAnchor = this.pixelAnchor;
        return (pixelAnchor != null) ? pixelAnchor : PixelInCell.CELL_CENTER;
    }

    /**
     * Sets the whatever the {@linkplain #gridToCRS grid to CRS} transform maps pixel center or
     * pixel corner. Note that this attribute has no effect if the {@link #gridToCRS} attribute
     * is not used (for example because the {@link #gridGeometry} attribute has precedence).
     *
     * @param anchor The new "pixel in cell" policy, or {@code null}.
     *
     * @since 3.20
     */
    public void setPixelAnchor(final PixelInCell anchor) {
        this.pixelAnchor = anchor;
        gridGeometryChanged();
    }

    /**
     * Returns the current grid geometry. If no grid geometry has been
     * {@linkplain #setGridGeometry(GridGeometry) explicitly defined}, then this
     * method builds a default instance from the values returned by <u>one</u> of
     * the following set of getter methods:
     * <p>
     * <table>
     * <tr><th>Recommended</th><th>&nbsp;</th><th>Alternative</th></tr>
     * <tr><td valign="top"><ul>
     *   <li>{@link #getExtent()}</li>
     *   <li>{@link #getPixelAnchor()}</li>
     *   <li>{@link #getGridToCRS()}</li>
     *   <li>{@link #getCoordinateReferenceSystem()}</li>
     * </ul>
     * </td><td><b>or</b></td><td valign="top">
     * <ul>
     *   <li>{@link #getExtent()}</li>
     *   <li>{@link #getEnvelope()}</li>
     * </ul></td></tr></table>
     * <p>
     * Note that creation of grid geometries from the parameters listed in the right column use heuristic
     * rules documented {@linkplain GeneralGridGeometry#GeneralGridGeometry(GridEnvelope,Envelope) here}.
     * In order to keep grid geometry creations more determinist, we recommend to specify the parameters
     * listed in the left column instead.
     *
     * @return The grid geometry, or {@code null} if unspecified and can not be inferred.
     *
     * @see #gridGeometry
     * @see GridCoverage2D#getGridGeometry()
     *
     * @since 3.20
     */
    public GridGeometry getGridGeometry() {
        GridGeometry geom = gridGeometry;
        if (geom == null) {
            geom = cachedGridGeometry;
            if (geom == null) {
                final GridEnvelope  extent    = getExtent();
                final MathTransform gridToCRS = getGridToCRS();
                if (gridToCRS != null) {
                    geom = new GridGeometry2D(extent, getPixelAnchor(),
                            gridToCRS, getCoordinateReferenceSystem(), hints);
                } else {
                    final Envelope envelope = getEnvelope();
                    if (extent != null || envelope != null) { // Its okay to have 1 null value.
                        geom = new GridGeometry2D(extent, envelope);
                    }
                }
                cachedGridGeometry = geom;
            }
        }
        return geom;
    }

    /**
     * Sets the grid geometry to the given value. If non-null, this value will have precedence over
     * the {@linkplain #crs}, {@linkplain #envelope}, {@linkplain #extent}, {@linkplain #gridToCRS
     * grid to CRS} and {@linkplain #pixelAnchor pixel anchor} attributes.
     *
     * @param geom The new grid geometry, or {@code null}.
     *
     * @since 3.20
     */
    public void setGridGeometry(final GridGeometry geom) {
        gridGeometry = geom;
        gridGeometryChanged();
    }

    /**
     * Invoked when a property used for computing the grid geometry has been changed.
     *
     * @since 3.20
     */
    private void gridGeometryChanged() {
        cachedGridGeometry = null;
        coverage           = null;
    }

    /**
     * Invoked when a property used for computing the sample dimensions has been changed.
     *
     * @since 3.20
     */
    final void sampleDimensionsChanged() {
        sampleDimensions = null;
        coverage         = null;
    }

    /**
     * Returns the number of sample dimensions (bands).
     * This method returns the first defined value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The value defined by the last call to {@link #setNumBands(int)}.</li>
     *   <li>The highest <var>n</var>+1 value given to {@link #variable(int)}.</li>
     *   <li>The number of bands in the {@linkplain #image}.</li>
     *   <li>The default value 1.</li>
     * </ul>
     *
     * @return The number of sample dimensions (bands).
     *
     * @see #numBands
     * @see SampleModel#getNumBands()
     *
     * @since 3.20
     */
    public int getNumBands() {
        int n = numBands;
        if (n == 0) {
            final RenderedImage image = this.image;
            if (image != null) {
                final SampleModel sampleModel = image.getSampleModel();
                if (sampleModel != null) {
                    return sampleModel.getNumBands();
                }
            }
            n = 1;
        }
        return n;
    }

    /**
     * Sets the number of sample dimensions (bands). If any {@linkplain #variable(int) variable}
     * existed at the index <var>n</var> or greater, they will be discarded.
     *
     * @param n Number of sample dimensions, or 0 to unspecify.
     * @throws IllegalArgumentException If the given value is negative.
     *
     * @since 3.20
     */
    public void setNumBands(final int n) throws IllegalArgumentException {
        ArgumentChecks.ensurePositive("n", n);
        final Variable[] variables = this.variables;
        if (variables != null && n < variables.length) {
            Arrays.fill(variables, numBands, variables.length, null);
        }
        numBands = n;
        sampleDimensionsChanged();
    }

    /**
     * Returns the {@linkplain GridSampleDimension sample dimension} builder for the given
     * band index. If a {@code Variable} instance exists at the given index, it will be
     * returned. Otherwise a new instance will be created.
     * <p>
     * If the given band index is equals or greater than the {@linkplain #getNumBands() number
     * of sample dimensions}, then the later will be increased as needed.
     *
     * @param  band The index of the sample dimension for which to get the variable.
     * @return The builder for the given sample dimension.
     * @throws IllegalArgumentException If the given band index is negative.
     *
     * @since 3.20
     */
    public Variable variable(final int band) throws IllegalArgumentException {
        ArgumentChecks.ensurePositive("band", band);
        Variable[] v = this.variables;
        if (v == null) {
            // A length of 4 is a good match for ARGB images - few images need more bands.
            variables = v = new Variable[Math.max(4, band+1)];
        } else if (band >= v.length) {
            variables = v = Arrays.copyOf(v, Math.max(v.length*2, band+1));
        }
        Variable var = v[band];
        if (var == null) {
            v[band] = var = newVariable(band);
        }
        if (band >= numBands) {
            numBands = band+1;
        }
        return var;
    }

    /**
     * Invoked by the {@link #variable(int)} method when a new variable need to be created.
     * This method is a hook for subclasses that wish to instantiate their own
     * {@link Variable Variable} subclasses. The default implementation is:
     *
     * {@preformat java
     *     return new Variable(band);
     * }
     *
     * @param  band The index of the sample dimension for which to get the new variable.
     * @return The new variable.
     *
     * @since 3.20
     */
    protected Variable newVariable(final int band) {
        return new Variable(band);
    }

    /**
     * @deprecated Replaced by {@link Variable#getSampleRange()}.
     *
     * @return The current range of sample values.
     */
    @Deprecated
    public NumberRange<?> getSampleRange() {
        return variable(0).getSampleRange();
    }

    /**
     * @deprecated Replaced by {@link Variable#setSampleRange(NumberRange)}.
     *
     * @param  range The new range of sample values, or {@code null}.
     */
    @Deprecated
    public void setSampleRange(final NumberRange<?> range) {
        variable(0).setSampleRange(range);
    }

    /**
     * @deprecated Replaced by {@link Variable#setSampleRange(int, int)}.
     *
     * @param  lower The lower sample value (inclusive), typically 0.
     * @param  upper The upper sample value (exclusive), typically 256.
     */
    @Deprecated
    public void setSampleRange(final int lower, final int upper) {
        variable(0).setSampleRange(lower, upper);
    }

    /**
     * Gets the given image property as an array of double values, or {@code null} if none.
     */
    private double[] getArrayProperty(final String name) {
        final RenderedImage image = this.image;
        if (image != null) {
            final Object property = image.getProperty(name);
            if (property instanceof double[]) {
                return (double[]) property;
            }
        }
        return null;
    }

    /**
     * Creates defaults sample dimensions. This method should be invoked only when no sample
     * dimensions can be built from the {@linkplain #variables}.
     *
     * @param raster The raster for which to build sample dimensions, or {@code null} for
     *               using the {@linkplain #image} instead.
     *
     * @since 3.20
     */
    private SampleDimension[] getDefaultSampleDimensions(final Raster raster) {
        double[]     minimum = null;
        double[]     maximum = null;
        Color[][]     colors = null;
        Unit<?>[]      units = null;
        CharSequence[] names = null;
        final int   numBands = this.numBands;
        for (int i=0; i<numBands; i++) {
            final Variable variable = variables[i];
            if (variable != null) {
                final NumberRange<?> range = variable.getSampleRange();
                if (range != null) {
                    if (minimum == null) {
                        minimum = new double[numBands];
                        maximum = new double[numBands];
                        Arrays.fill(minimum, Double.NEGATIVE_INFINITY);
                        Arrays.fill(maximum, Double.POSITIVE_INFINITY);
                    }
                    minimum[i] = range.getMinimum(true);
                    maximum[i] = range.getMaximum(true);
                }
                final Unit<?> unit = variable.getUnit();
                if (unit != null) {
                    if (units == null) {
                        units = new Unit<?>[numBands];
                    }
                    units[i] = unit;
                }
                final Color[] c = variable.getColors();
                if (c != null) {
                    if (colors == null) {
                        colors = new Color[numBands][];
                    }
                    colors[i] = c;
                }
                final CharSequence name = variable.getName();
                if (name != null) {
                    if (names == null) {
                        names = new CharSequence[numBands];
                    }
                    names[i] = name;
                }
            }
        }
        // If we can't find the minimum or maximum from sample values, queries JAI properties.
        if (minimum == null) minimum = getArrayProperty("minimum");
        if (maximum == null) maximum = getArrayProperty("maximum");
        if (raster != null) {
            return RenderedSampleDimension.create(names, raster, minimum, maximum, units, colors, hints);
        } else {
            return RenderedSampleDimension.create(names, image, minimum, maximum, units, colors, hints);
        }
    }

    /**
     * Returns the sample dimensions, or {@code null} if none. If all sample dimensions
     * are actually instances of {@link GridSampleDimension}, then the array type is
     * {@code GridSampleDimension[]}.
     *
     * @return The sample dimensions, or {@code null} if none.
     *
     * @since 3.20
     */
    public SampleDimension[] getSampleDimensions() {
        SampleDimension[] bands = sampleDimensions;
        if (bands == null) {
            // Builds the sample dimension from the variables when first needed.
            final int numBands = this.numBands;
            for (int i=numBands; --i>=0;) {
                final SampleDimension band = variable(i).getSampleDimension();
                if (band != null) {
                    if (band instanceof GridSampleDimension) {
                        if (bands == null) {
                            bands = new GridSampleDimension[numBands];
                        }
                    } else {
                        if (bands == null) {
                            bands = new SampleDimension[numBands];
                        } else if (bands instanceof GridSampleDimension[]) {
                            final SampleDimension[] old = bands;
                            bands = new SampleDimension[numBands];
                            System.arraycopy(old, 0, bands, 0, numBands);
                        }
                    }
                    bands[i] = band;
                }
            }
            // If the were no variables, build default sample dimensions.
            if (bands == null && image != null) {
                bands = getDefaultSampleDimensions(null);
            }
            sampleDimensions = bands;
        }
        return (bands != null) ? bands.clone() : null;
    }

    /**
     * Sets all sample dimensions. This convenience method {@linkplain #setNumBands(int)
     * sets the number of bands} to the number of given arguments, then invokes
     * {@link Variable#setSampleDimension(SampleDimension) setSampleDimension(...)}
     * for each element.
     *
     * @param bands The new sample dimensions, or {@code null}.
     *
     * @since 3.20
     */
    public void setSampleDimensions(final SampleDimension... bands) {
        setNumBands(bands != null ? bands.length : 0);
        if (bands != null) {
            for (int i=0; i<bands.length; i++) {
                variable(i).setSampleDimension(bands[i]);
            }
        }
    }

    /**
     * Returns or computes the {@linkplain #image} color model.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>A color model created by one of the {@link GridSampleDimension#getColorModel(int, int)
     *       getColorModel(...)} methods invoked on the first {@link GridSampleDimension} instance
     *       returned by {@linkplain #getSampleDimensions()}.</li>
     *   <li>The {@linkplain #image} color model.</li>
     * </ul>
     *
     * @return The color model, or {@code null} if none.
     *
     * @since 3.20
     */
    public ColorModel getColorModel() {
        ColorModel cm = getColorModel(getSampleDimensions(), DataBuffer.TYPE_UNDEFINED);
        if (cm == null) {
            final RenderedImage image = this.image;
            if (image != null) {
                cm = image.getColorModel();
            }
        }
        return cm;
    }

    /**
     * Creates a color model from the bands for the given data type.
     *
     * @param dataType One of the {@link java.awt.image.DataBuffer} constants.
     */
    private static ColorModel getColorModel(final SampleDimension[] bands, final int dataType) {
        if (bands != null) {
            for (int i=0; i<bands.length; i++) {
                final SampleDimension band = bands[i];
                if (band instanceof GridSampleDimension) {
                    if (dataType != DataBuffer.TYPE_UNDEFINED) {
                        return ((GridSampleDimension) band).getColorModel(i, bands.length, dataType);
                    } else {
                        return ((GridSampleDimension) band).getColorModel(i, bands.length);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Creates a graphics which can be used to draw into the {@linkplain #getRenderedImage()
     * rendered image}. It is caller responsibility to invoke {@link Graphics#dispose()} after
     * the drawing has been completed.
     * <p>
     * The returned value can usually be casted to {@link java.awt.Graphics2D}.
     *
     * @return A graphics (usually a {@link java.awt.Graphics2D}) for drawing into the image.
     * @throws UnsupportedOperationException If the rendered image does not support drawing.
     *
     * @since 3.20
     */
    public Graphics createGraphics() throws UnsupportedOperationException {
        RenderedImage image = getRenderedImage();
        while (image instanceof RenderedImageAdapter) {
            image = ((RenderedImageAdapter) image).getWrappedImage();
        }
        if (image instanceof Image) {
            return ((Image) image).getGraphics();
        }
        Throwable cause = null;
        if (image instanceof PlanarImage) try {
            return ((PlanarImage) image).getGraphics();
        } catch (IllegalAccessError e) {
            cause = e;
        }
        throw new UnsupportedOperationException(Errors.format(Errors.Keys.UNSUPPORTED_IMAGE_TYPE), cause);
    }

    /**
     * Returns the image bounds.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The {@linkplain #getGridGeometry() grid geometry} extent.</li>
     *   <li>The {@linkplain #image} bounds.</li>
     * </ul>
     * <p>
     * Note that the ({@linkplain Rectangle#x x},{@linkplain Rectangle#y y}) origin must be
     * (0,0) for building a {@link BufferedImage}, but can be different for other kinds of
     * {@link RenderedImage}.
     *
     * @return The current image bounds. Never {@code null} since no image can be built
     *         without this information.
     * @throws InvalidGridGeometryException if there is no {@linkplain #getGridGeometry()
     *         grid geometry} or no extent associated to that grid geometry.
     *
     * @see GridGeometry2D#getExtent2D()
     *
     * @since 3.20
     */
    public Rectangle getImageBounds() throws InvalidGridGeometryException {
        final GridGeometry2D gridGeometry = GridGeometry2D.castOrCopy(getGridGeometry());
        if (gridGeometry != null) {
            return gridGeometry.getExtent2D();
        }
        final RenderedImage image = this.image;
        if (image != null) {
            return ImageUtilities.getBounds(image);
        }
        throw new InvalidGridGeometryException(Errors.Keys.UNSPECIFIED_IMAGE_SIZE);
    }

    /**
     * Returns the image size.
     *
     * @return The current image size.
     *
     * @deprecated Replaced by {@link #getImageBounds()}.
     */
    @Deprecated
    public Dimension getImageSize() {
        try {
            return getImageBounds().getSize();
        } catch (InvalidGridGeometryException e) {
            return null; // To preserve the contract of previous implementation.
        }
    }

    /**
     * Sets the image size.
     *
     * @param size The new image size.
     *
     * @deprecated Replaced by {@link #setExtent(int[])}.
     */
    @Deprecated
    public void setImageSize(final Dimension size) {
        setExtent(size.width, size.height);
    }

    /**
     * Sets the image size.
     *
     * @param width The new image width.
     * @param height The new image height.
     *
     * @deprecated Replaced by {@link #setExtent(int[])}.
     */
    @Deprecated
    public void setImageSize(final int width, final int height) {
        setImageSize(new Dimension(width, height));
    }

    /**
     * Creates a new variable, which will be mapped to a {@linkplain GridSampleDimension sample
     * dimension}. Additional information like scale, offset and nodata values can be provided
     * by invoking setters on the returned variable.
     *
     * @param  name  The variable name, or {@code null} for a default name.
     * @param  units The variable units, or {@code null} if unknown.
     * @return A new variable.
     *
     * @deprecated Replaced by {@link #variable(int)}.
     */
    @Deprecated
    public Variable newVariable(final CharSequence name, final Unit<?> units) {
        final Variable variable = variable(numBands);
        variable.setName(name);
        variable.setUnit(units);
        return variable;
    }

    /**
     * Returns the tile size, or {@code null} if the image is untiled.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The value defined by the last call to {@link #setTileSize(Dimension)}.</li>
     *   <li>The {@linkplain #image} tile size.</li>
     * </ul>
     *
     * @return The tile size, or {@code null} if the image is untiled.
     *
     * @see RenderedImage#getTileWidth()
     * @see RenderedImage#getTileHeight()
     *
     * @since 3.20
     */
    public Dimension getTileSize() {
        final TileLayout tileLayout = this.tileLayout;
        if (tileLayout != null) {
            final Dimension size = tileLayout.getSize();
            if (size != null) {
                return size;
            }
        }
        final RenderedImage image = this.image;
        if (image != null) {
            final int width  = image.getTileWidth();
            final int height = image.getTileHeight();
            if (width < image.getWidth() || height < image.getHeight()) {
                return new Dimension(width, height);
            }
        }
        return null;
    }

    /**
     * Sets the tile size.
     *
     * {@section Precedence}
     * If an image has been {@linkplain #setRenderedImage(RenderedImage) explicitely set},
     * then its tile setting will have precedence over this attribute for the creation of
     * {@link GridCoverage2D} instances (i.e. this method does not retile existing images).
     *
     * @param size The new tile size, or {@code null} for untiled image.
     *
     * @since 3.20
     */
    public void setTileSize(final Dimension size) {
        final TileLayout layout = tileLayout;
        if (layout != null) {
            layout.setSize(size);
        } else if (size != null) {
            tileLayout = new TileLayout(size);
        }
    }

    /**
     * Returns the tile grid offset, or {@code null} if the image is untiled.
     * This method returns the first non-null value in the above choices, in preference order:
     * <p>
     * <ul>
     *   <li>The value defined by the last call to {@link #setTileGridOffset(Point)}.</li>
     *   <li>The {@linkplain #image} tile grid offset.</li>
     * </ul>
     *
     * @return The tile grid offset, or {@code null} if the image is untiled.
     *
     * @see RenderedImage#getTileGridXOffset()
     * @see RenderedImage#getTileGridYOffset()
     *
     * @since 3.20
     */
    public Point getTileGridOffset() {
        final TileLayout layout = tileLayout;
        if (layout != null) {
            return layout.getLocation();
        }
        final RenderedImage image = this.image;
        if (image != null && ImageUtilities.isTiled(image)) {
            return new Point(image.getTileGridXOffset(),
                             image.getTileGridYOffset());
        }
        return null;
    }

    /**
     * Sets the tile offset.
     *
     * {@section Precedence}
     * If an image has been {@linkplain #setRenderedImage(RenderedImage) explicitely set},
     * then its tile setting will have precedence over this attribute for the creation of
     * {@link GridCoverage2D} instances (i.e. this method does not retile existing images).
     *
     * @param offset The new tile offset, or {@code null} if none.
     *
     * @since 3.20
     */
    public void setTileGridOffset(final Point offset) {
        final TileLayout layout = tileLayout;
        if (layout != null) {
            layout.setLocation(offset);
        } else if (offset != null) {
            tileLayout = new TileLayout(offset);
        }
    }

    /**
     * Returns the rendered image to be wrapped by {@link GridCoverage2D}. If no image has been
     * {@linkplain #setRenderedImage(RenderedImage) explicitly defined}, a new one is created the
     * first time this method is invoked. Users can modify the pixel values in this image before
     * to create the grid coverage.
     * <p>
     * In the common case of untiled image having a {@linkplain GridEnvelope#getLow() lower corner}
     * located at (0,0), this method returns an instance of {@link BufferedImage}. If the builder
     * settings do not allow the creation of a {@code BufferedImage} instance, then the default
     * implementation fallbacks on a {@link TiledImage} instance.
     *
     * @return The rendered image to be wrapped by {@code GridCoverage2D}.
     *
     * @since 3.20 (derived from 2.5)
     */
    public RenderedImage getRenderedImage() {
        if (image == null) {
            final Rectangle bounds = getImageBounds(); // Can not be null.
            Dimension tileSize = getTileSize();
            if (tileSize        != null &&
                tileSize.width  >= bounds.width &&
                tileSize.height >= bounds.height)
            {
                tileSize = null; // Tile size not smaller than image size: untiled image.
            }
            Point offset = getTileGridOffset();
            if (offset != null &&
                offset.x == bounds.x &&
                offset.y == bounds.y)
            {
                offset = null; // Grid offset == image origin: no tile offset.
            }
            ColorModel cm = getColorModel();
            if (tileSize == null && offset == null && bounds.x == 0 && bounds.y == 0) {
                if (cm == null) {
                    image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_BYTE_GRAY);
                } else {
                    image = new BufferedImage(cm,
                            cm.createCompatibleWritableRaster(bounds.width, bounds.height), false, null);
                }
            } else {
                /*
                 * The code in the above block created a untiled image, which is the most common case.
                 * The code in this block is a fallback used only if the user want to create a tiled
                 * image, or if the origin is not (0,0). We have to use the more generic JAI image
                 * rather than the JDK one.
                 */
                if (cm == null) {
                    cm = PlanarImage.getDefaultColorModel(DataBuffer.TYPE_BYTE, getNumBands());
                }
                image = new TiledImage(bounds.x, bounds.y, bounds.width, bounds.height,
                        (offset != null) ? offset.x : bounds.x,
                        (offset != null) ? offset.y : bounds.y,
                        cm.createCompatibleSampleModel(
                                (tileSize != null) ? tileSize.width  : bounds.width,
                                (tileSize != null) ? tileSize.height : bounds.height), cm);
            }
        }
        return image;
    }

    /**
     * Sets the rendered image.
     *
     * @param image The rendered image to be wrapped by {@code GridCoverage2D}.
     *
     * @since 3.20 (derived from 2.5)
     */
    public void setRenderedImage(final RenderedImage image) {
        this.image = image;
        coverage = null;
    }

    /**
     * Creates a rendered image from the given raster. This methods create a color model
     * for the given raster, then creates a {@link BufferedImage} using that color model
     * and the raster, and finally invokes {@link #setRenderedImage(RenderedImage)} with
     * the result in argument.
     *
     * @param raster The raster to be wrapped by {@code GridCoverage2D}.
     *
     * @since 3.20
     */
    public void setRenderedImage(final WritableRaster raster) {
        ColorModel cm = getColorModel();
        if (cm == null) {
            final int dataType = raster.getDataBuffer().getDataType();
            final SampleDimension[] bands = getDefaultSampleDimensions(raster);
            if (bands == null) {
                cm = PlanarImage.getDefaultColorModel(dataType, raster.getNumBands());
            } else {
                cm = getColorModel(bands, dataType);
                if (sampleDimensions == null) {
                    sampleDimensions = bands;
                }
            }
        }
        setRenderedImage(new BufferedImage(cm, raster, false, null));
    }

    /**
     * @deprecated Replaced by {@link #getRenderedImage()}.
     *
     * @return The buffered image to be wrapped by {@code GridCoverage2D}.
     */
    @Deprecated
    public BufferedImage getBufferedImage() {
        return (BufferedImage) getRenderedImage();
    }

    /**
     * @deprecated Replaced by {@link #setRenderedImage(RenderedImage)}.
     *
     * @param image The buffered image to be wrapped by {@code GridCoverage2D}.
     */
    @Deprecated
    public void setBufferedImage(final BufferedImage image) {
        setRenderedImage(image);
    }

    /**
     * Sets the buffered image by reading it from the given file. Invoking this method
     * overwrite the {@linkplain #getImageSize() image size} with the given image size.
     *
     * @param file The file of the image to be wrapped by {@code GridCoverage2D}.
     * @throws IOException if the image can't be read.
     *
     * @deprecated Use {@link org.geotoolkit.coverage.io.GridCoverageReader} instead.
     */
    @Deprecated
    public void setBufferedImage(final File file) throws IOException {
        setBufferedImage(ImageIO.read(file));
    }

    /**
     * Sets the buffered image to a raster filled with random value using the specified random
     * number generator. This method can be used for testing purpose, or for adding noise to a
     * coverage.
     *
     * @param random The random number generator to use for generating pixel values.
     *
     * @deprecated No replacement.
     */
    @Deprecated
    public void setBufferedImage(final Random random) {
        image = null; // Will forces the creation of a new BufferedImage.
        final BufferedImage image = getBufferedImage();
        final WritableRaster raster = image.getRaster();
        final ColorModel model = image.getColorModel();
        final int size;
        if (model instanceof IndexColorModel) {
            size = ((IndexColorModel) model).getMapSize();
        } else {
            size = 1 << Short.SIZE;
        }
        for (int i=raster.getWidth(); --i>=0;) {
            for (int j=raster.getHeight(); --j>=0;) {
                raster.setSample(i,j,0, random.nextInt(size));
            }
        }
    }

    /**
     * Returns the grid coverage. The default implementation builds a coverage like below
     * (omitting the conversions of some argument types):
     *
     * <blockquote><pre>return new {@linkplain GridCoverage2D#GridCoverage2D(CharSequence,
     * PlanarImage, GridGeometry2D, GridSampleDimension[], GridCoverage[], Map, Hints) GridCoverage2D}(
     *         {@linkplain #getName()},
     *         {@linkplain #getRenderedImage()},
     *         {@linkplain #getGridGeometry()},
     *         {@linkplain #getSampleDimensions()},
     *         {@linkplain #getSources()},
     *         {@linkplain #getProperties()},
     *         {@linkplain #hints})</pre></blockquote>
     *
     * @return The grid coverage.
     */
    public GridCoverage2D getGridCoverage2D() {
        if (coverage == null) {
            final SampleDimension[] sd = getSampleDimensions();
            final GridSampleDimension[] bands;
            if (sd == null || sd instanceof GridSampleDimension[]) {
                bands = (GridSampleDimension[]) sd;
            } else {
                bands = new GridSampleDimension[sd.length];
                for (int i=0; i<bands.length; i++) {
                    bands[i] = GridSampleDimension.castOrCopy(sd[i]);
                }
            }
            coverage = new GridCoverage2D(
                    getName(),
                    PlanarImage.wrapRenderedImage(getRenderedImage()),
                    GridGeometry2D.castOrCopy(getGridGeometry()),
                    bands,
                    getSources(),
                    getProperties(),
                    hints);
        }
        return coverage;
    }

    /**
     * Returns the optional sources to be associated with the coverage.
     * If there is no sources, then this method returns {@code null}.
     *
     * {@section Implementation note}
     * This method returns a direct reference to the {@linkplain #sources} field.
     * The array is not cloned because it is not used for any calculation in this
     * class. {@code GridCoverageBuilder} users are responsible for the content of
     * this array.
     *
     * @return Optional grid coverage sources, or {@code null} if none.
     *
     * @see #sources
     * @see GridCoverage2D#getSources()
     *
     * @since 3.20
     */
    public GridCoverage[] getSources() {
        return sources;
    }

    /**
     * Sets the optional sources to be associated with the coverage.
     *
     * {@section Implementation note}
     * The given array is not cloned at this method invocation time. The array will be cloned by
     * the {@linkplain GridCoverage2D#GridCoverage2D(CharSequence, PlanarImage, GridGeometry2D,
     * GridSampleDimension[], GridCoverage[], Map, Hints) grid coverage constructor}. This builder
     * does nothing but passing the array to that constructor.
     *
     * @param sources Optional grid coverage sources, or {@code null} or an empty array if none.
     *
     * @since 3.20
     */
    public void setSources(GridCoverage... sources) {
        if (sources != null && sources.length == 0) {
            sources = null;
        }
        this.sources = sources; // NOSONAR
    }

    /**
     * Returns optional properties to be given to the coverage, or {@code null} if none.
     *
     * {@section Implementation note}
     * This method returns a direct reference to the {@linkplain #properties} field.
     * The map is not cloned because it is not used for any calculation in this class.
     * {@code GridCoverageBuilder} users are responsible for the content of this map.
     *
     * @return Optional map of coverage properties, or {@code null}.
     *
     * @see #properties
     * @see GridCoverage2D#getProperties()
     * @see GridCoverage2D#getPropertyNames()
     *
     * @since 3.20
     */
    public Map<?,?> getProperties() {
        return properties;
    }

    /**
     * Sets the optional hints to be given to the coverage.
     *
     * {@section Reference to the given map}
     * The given map is not cloned at this method invocation time. The map will be cloned by
     * the {@linkplain GridCoverage2D#GridCoverage2D(CharSequence, PlanarImage, GridGeometry2D,
     * GridSampleDimension[], GridCoverage[], Map, Hints) grid coverage constructor}. This builder
     * does nothing but passing the map to that constructor.
     *
     * @param properties Optional map of coverage properties, or {@code null}.
     *
     * @since 3.20
     */
    public void setProperties(final Map<?,?> properties) {
        this.properties = properties; // NOSONAR
    }

    /**
     * Creates the grid coverage. Current implementation delegates to {@link #getGridCoverage2D()},
     * but future implementations may instantiate different coverage types.
     *
     * @since 3.20
     */
    @Override
    public GridCoverage build() {
        return getGridCoverage2D();
    }

    /**
     * Resets this builder to its initial state. This method can be invoked in order to
     * reuse this builder for creating new {@link GridCoverage2D} instances.
     *
     * @since 3.20
     */
    public void reset() {
        name               = null;
        crs                = null;
        envelope           = null;
        extent             = null;
        gridToCRS          = null;
        pixelAnchor        = null;
        gridGeometry       = null;
        cachedGridGeometry = null;
        sampleDimensions   = null;
        image              = null;
        coverage           = null;
        sources            = null;
        properties         = null;
        numBands           = 0;
        if (variables != null) {
            Arrays.fill(variables, null);
        }
        if (tileLayout != null) {
            tileLayout.reset();
        }
    }


    /**
     * Helper class for the creation of {@link SampleDimension} instances.
     * A variable to be mapped to a {@linkplain GridSampleDimension sample dimension}.
     *
     * {@note This class is named <cite>variable</cite> because it is usually not needed for
     * Red/Green/Blue bands. <code>Variable</code> is typically used for describing the
     * measurement of a single phenomenon, like temperature (°C) or elevation (m). The
     * <cite>variable</cite> name is used for this purpose in NetCDF files for instance.}
     *
     * Variables are obtained by calls to {@link GridCoverageBuilder#variable(int)}.
     * See {@linkplain GridCoverageBuilder outer class javadoc} for usage examples.
     *
     * {@section Subclassing}
     * Implementors who wish to create their own {@code Variable} subclass will probably
     * need to override the {@link GridCoverageBuilder#newVariable(int)} method as well.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.20
     *
     * @see GridCoverageBuilder#variable(int)
     * @see ucar.nc2.Variable
     *
     * @since 2.5
     * @module
     */
    public class Variable {
        /**
         * The band index for this variable. This is the {@code band} argument given
         * to the {@link GridCoverageBuilder#variable(int)} method.
         *
         * @since 3.20
         */
        protected final int band;

        /**
         * The variable name, or {@code null} if unspecified. This field is non-null only if the
         * name has been {@linkplain #setName(CharSequence) explicitely specified} by the user.
         * The values inferred from other attributes are not stored in this field.
         *
         * @see #getName()
         * @see #setName(CharSequence)
         *
         * @since 3.20 (derived from 2.5)
         */
        protected CharSequence name;

        /**
         * The units of measurement, or {@code null} if unspecified. This field is non-null only
         * if the unit has been {@linkplain #setUnit(Unit) explicitely specified} by the user.
         * The values inferred from other attributes are not stored in this field.
         *
         * @see #getUnit()
         * @see #setUnit(Unit)
         *
         * @since 3.20 (derived from 2.5)
         */
        protected Unit<?> unit;

        /**
         * The range of sample values, or {@code null} if unspecified. This field is non-null only
         * if the range has been {@linkplain #setSampleRange(NumberRange) explicitely specified}
         * by the user. The values inferred from other attributes are not stored in this field.
         *
         * @see #getSampleRange()
         * @see #setSampleRange(NumberRange)
         * @see #setSampleRange(int, int)
         *
         * @since 3.20 (derived from 2.5)
         */
        protected NumberRange<?> sampleRange;

        /**
         * The "nodata" values, or {@code null} if none. This map is created when first needed.
         *
         * @see #addNodataValue(CharSequence, int)
         */
        private Map<Integer,CharSequence> nodata;

        /**
         * The "<cite>sample to geophysics</cite>" transform, or {@code null} if unspecified. This
         * field is non-null only if the transform has been {@linkplain #setTransform(MathTransform1D)
         * explicitely specified} by the user (potentially as transform coefficients). The values
         * inferred from other attributes are not stored in this field.
         *
         * @see #getTransform()
         * @see #setTransform(MathTransform1D)
         * @see #setLinearTransform(double, double)
         * @see #setLogarithmicTransform(double, double)
         *
         * @since 3.20 (derived from 2.5)
         */
        protected MathTransform1D transform;

        /**
         * The colors to associate to values in the {@linkplain #sampleRange sample range}, or
         * {@code null} if unspecified.
         *
         * @since 3.20
         */
        protected Color[] colors;

        /**
         * The sample dimension, or {@code null} if unspecified. This field is non-null only if the
         * sample dimension has been {@linkplain #setSampleDimension(SampleDimension) explicitely
         * specified} by the user. The values inferred from other attributes are not stored in this
         * field.
         *
         * @see #getSampleDimension()
         * @see #setSampleDimension(SampleDimension)
         *
         * @since 3.20 (derived from 2.5)
         */
        protected SampleDimension sampleDimension;

        /**
         * The sample dimension calculated from other attributes. Will be created when first
         * needed and cleared when attributes change.
         *
         * @since 3.20
         */
        private transient SampleDimension cached;

        /**
         * Creates an initially empty variable.
         *
         * @param band The band index for this variable. This is the {@code band} argument given
         *        to the {@link GridCoverageBuilder#variable(int)} method.
         *
         * @see GridCoverageBuilder#variable(int)
         * @see GridCoverageBuilder#newVariable(int)
         *
         * @since 3.20
         */
        protected Variable(final int band) {
            this.band = band;
        }

        /**
         * Invoked when this sample dimension changed. Note: we keep the plural form in the
         * method name (despite modifying only this single dimension) for making sure that
         * we don't invoke by accident the method from the outer class instead than this one.
         */
        private void sampleDimensionsChanged() {
            cached = null;
            GridCoverageBuilder.this.sampleDimensionsChanged();
        }

        /**
         * Returns the name of this variable (or bands or sample dimension). If no name has been
         * {@linkplain #setName(CharSequence) explicitly defined}, then this method returns the
         * {@linkplain #sampleDimension sample dimension} description. If this description is not
         * defined neither, then this method returns the {@linkplain GridCoverageBuilder#name
         * coverge name}.
         *
         * @return The variable name, or {@code null}.
         *
         * @see Category#getName()
         * @see SampleDimension#getDescription()
         *
         * @since 3.20
         */
        public CharSequence getName() {
            CharSequence value = name;
            if (value == null) {
                final SampleDimension sampleDimension = this.sampleDimension;
                if (sampleDimension != null) {
                    value = sampleDimension.getDescription();
                    if (value == null) {
                        value = GridCoverageBuilder.this.name;
                    }
                }
            }
            return value;
        }

        /**
         * Sets the name of this variable (or bands or sample dimension). This name will be
         * given to the geophysics category (if any) and to the sample dimension as a whole.
         *
         * @param name The new name, or {@code null}.
         *
         * @since 3.20
         */
        public void setName(final CharSequence name) {
            this.name = name;
            sampleDimensionsChanged();
        }

        /**
         * Returns the units of measurement, or {@code null} if none. If no unit has been
         * {@linkplain #setUnit(Unit) explicitly defined}, then this method returns the
         * {@linkplain #sampleDimension sample dimension} unit.
         *
         * @return The units of measurement of geophysics values, or {@code null}.
         *
         * @see SampleDimension#getUnits()
         *
         * @since 3.20
         */
        public Unit<?> getUnit() {
            Unit<?> value = unit;
            if (value == null) {
                final SampleDimension sampleDimension = this.sampleDimension;
                if (sampleDimension != null) {
                    value = sampleDimension.getUnits();
                }
            }
            return value;
        }

        /**
         * Sets the units of measurement, or {@code null} if none. This is the unit of geophysics
         * values <em>after</em> the {@linkplain #getTransform() sample to unit transform}.
         *
         * @param unit The new units of measurement of geophysics values, or {@code null}.
         *
         * @since 3.20
         */
        public void setUnit(final Unit<?> unit) {
            this.unit = unit;
            sampleDimensionsChanged();
        }

        /**
         * Sets the units of measurement from the given symbol. The default implementation parses
         * the given symbol using the {@link Units#valueOf(String)} method, then passes the result
         * to {@link #setUnit(Unit)}.
         *
         * @param symbol The new units symbol, or {@code null}.
         *
         * @since 3.20
         */
        public void setUnit(final String symbol) {
            setUnit(Units.valueOf(symbol));
        }

        /**
         * Returns the range of sample values. If no range has been
         * {@linkplain #setSampleRange(NumberRange) explicitly defined}, then this method
         * returns the {@linkplain #sampleDimension sample dimension} range.
         *
         * @return The range of sample values, or {@code null}.
         *
         * @since 3.20 (derived from 2.5)
         */
        public NumberRange<?> getSampleRange() {
            NumberRange<?> range = sampleRange;
            if (range == null) {
                final SampleDimension sampleDimension = this.sampleDimension;
                if (sampleDimension instanceof GridSampleDimension) {
                    range = ((GridSampleDimension) sampleDimension).getRange();
                }
            }
            return range;
        }

        /**
         * Sets the range of sample values. We allow only one range of values per {@code Variable}
         * instance, because sample dimensions with multi-ranges of values are very rare. If such
         * case happen, a {@link GridSampleDimension} instances will need to be created from outside
         * this helper class.
         *
         * @param range The new range of sample values, or {@code null}.
         *
         * @since 3.20 (derived from 2.5)
         */
        public void setSampleRange(final NumberRange<?> range) {
            sampleRange = range;
            sampleDimensionsChanged();
        }

        /**
         * Sets the range of sample values from the given lower and upper values.
         * This convenience methods creates a {@link NumberRange} objects from the
         * given values and delegates to {@link #setSampleRange(NumberRange)}.
         *
         * @param  lower The lower sample value (inclusive), typically 0.
         * @param  upper The upper sample value (exclusive), typically 256.
         *
         * @since 3.20 (derived from 2.5)
         */
        public void setSampleRange(final int lower, final int upper) {
            setSampleRange(NumberRange.create(lower, true, upper, false));
        }

        /**
         * Returns the "<cite>sample to units</cite>" transform. If no transform has been
         * {@linkplain #setTransform(MathTransform1D) explicitly defined}, then this method
         * returns the {@linkplain #sampleDimension sample dimension} transform. If no such
         * transform is defined neither, then the default is {@link LinearTransform1D#IDENTITY}.
         *
         * @return The "<cite>sample to units</cite>" transform.
         */
        public MathTransform1D getTransform() {
            MathTransform1D value = transform;
            if (value == null) {
                final SampleDimension sampleDimension = this.sampleDimension;
                if (sampleDimension != null) {
                    value = sampleDimension.getSampleToGeophysics();
                }
            }
            return (value != null) ? value : LinearTransform1D.IDENTITY;
        }

        /**
         * Sets the "<cite>sample to units</cite>" transform.
         *
         * @param transform The new "<cite>sample to units</cite>" transform, or {@code null}.
         */
        public void setTransform(final MathTransform1D transform) {
            this.transform = transform;
            sampleDimensionsChanged();
        }

        /**
         * Sets the "<cite>sample to units</cite>" transform from a scale and an offset.
         * The transformation formula will be:
         *
         * <blockquote>
         * <var>geophysics</var> = {@code scale} &times; <var>sample</var> + {@code offset}
         * </blockquote>
         *
         * @param scale  The {@code scale}  term in the linear equation.
         * @param offset The {@code offset} term in the linear equation.
         */
        public void setLinearTransform(final double scale, final double offset) {
            setTransform(LinearTransform1D.create(scale, offset));
        }

        /**
         * Sets the "<cite>sample to units</cite>" logarithmic transform
         * from a scale and an offset. The transformation formula will be:
         *
         * <blockquote>
         * <var>geophysics</var> = log<sub>{@code base}</sub>(<var>sample</var>) + {@code offset}
         * </blockquote>
         *
         * @param base   The base of the logarithm (typically 10).
         * @param offset The offset to add to the logarithm.
         */
        public void setLogarithmicTransform(final double base, final double offset) {
            setTransform(LogarithmicTransform1D.create(base, offset));
        }

        /**
         * Adds a "nodata" value.
         *
         * @param  name  The name for the "nodata" value.
         * @param  value The pixel value to assign to "nodata".
         * @throws IllegalArgumentException if the given pixel value is already assigned.
         */
        public void addNodataValue(final CharSequence name, final int value)
                throws IllegalArgumentException
        {
            if (nodata == null) {
                nodata = new TreeMap<>();
            }
            final Integer key = value;
            final CharSequence old = nodata.put(key, name);
            if (old != null) {
                nodata.put(key, old);
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_$2, "value", key));
            }
            sampleDimensionsChanged();
        }

        /**
         * Returns the colors associated to the values in the {@linkplain #getSampleRange()
         * sample range}, or {@code null} if none.
         *
         * @return The colors ramp, or {@code null} if none.
         *
         * @since 3.20
         */
        public Color[] getColors() {
            final Color[] colors = this.colors;
            return (colors != null) ? colors.clone() : null;
        }

        /**
         * Sets the colors associated to the values in the {@linkplain #getSampleRange() sample range}.
         *
         * @param colors The new colors ramp, or {@code null} if none.
         *
         * @since 3.20
         */
        public void setColors(final Color[] colors) {
            this.colors = (colors != null) ? colors.clone() : null;
        }

        /**
         * Returns the sample dimension. If no dimension has been
         * {@linkplain #setSampleDimension(SampleDimension) explicitly defined}, then this method
         * builds a new dimension from the other attributes defined in this class.
         *
         * @return The sample dimension for this {@code Variable} object.
         */
        public SampleDimension getSampleDimension() {
            SampleDimension sd = sampleDimension;
            if (sd == null) {
                sd = cached;
                if (sd == null) {
                    NumberRange<?> range = getSampleRange();
                    if (range != null) {
                        int lower = (int) Math.floor(range.getMinimum(true));
                        int upper = (int) Math.ceil (range.getMaximum(false));
                        final Map<Integer,CharSequence> nodata = this.nodata;
                        final Category[] categories;
                        int count = 0;
                        if (nodata == null) {
                            categories = new Category[1];
                        } else {
                            categories = new Category[nodata.size() + 1];
                            for (final Map.Entry<Integer,CharSequence> entry : nodata.entrySet()) {
                                final int sample = entry.getKey();
                                if (sample >= lower && sample < upper) {
                                    if (sample - lower <= upper - sample) {
                                        lower = sample + 1;
                                    } else {
                                        upper = sample;
                                    }
                                }
                                categories[count++] = new Category(entry.getValue(), null, sample);
                            }
                        }
                        final CharSequence name = getName();
                        range = NumberRange.create(lower, true, upper, false);
                        categories[count] = new Category(name, getColors(), range, getTransform());
                        cached = sd = new GridSampleDimension(name, categories, getUnit());
                    }
                }
            }
            return sd;
        }

        /**
         * Sets the sample dimension. If non-null, the given value will have precedence over
         * all other attributes specified in this class.
         *
         * @param dim The new sample dimension, or {@code null}.
         *
         * @since 3.20
         */
        public void setSampleDimension(final SampleDimension dim) {
            sampleDimension = dim;
            sampleDimensionsChanged();
        }

        /**
         * Returns a string representation of this variable.
         * This string is for debugging purpose only and may change in any future version.
         */
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder(getClass().getSimpleName());
            buffer.append('[');
            if (name != null) {
                buffer.append('"').append(name).append('"');
                if (unit != null) {
                    buffer.append(' ');
                }
            }
            if (unit != null) {
                buffer.append('(').append(unit).append(')');
            }
            return buffer.append(']').toString();
        }
    }
}
