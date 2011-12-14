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

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Dimension;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.measure.unit.Unit;
import javax.media.jai.PlanarImage;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.util.FactoryException;
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
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.ImmutableEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.referencing.operation.transform.LinearTransform1D;
import org.geotoolkit.referencing.operation.transform.LogarithmicTransform1D;
import org.geotoolkit.resources.Errors;


/**
 * Helper class for the creation of {@link GridCoverage2D} instances. This builder can creates the
 * parameters to be given to {@linkplain GridCoverage2D#GridCoverage2D(CharSequence, PlanarImage,
 * GridGeometry2D, GridSampleDimension[], GridCoverage[], Map, Hints) grid coverage constructor}
 * from simpler parameters given to this builder.
 * <p>
 * The builder supports the following properties:
 * <p>
 * <table border="1" cellspacing="0" cellpadding="4">
 *   <tr bgcolor="lightblue">
 *     <th>Properties</th>
 *     <th>Can be set from</th>
 *   </tr><tr>
 *     <td>&nbsp;{@link #crs}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setCoordinateReferenceSystem(CoordinateReferenceSystem) CRS instance} or
 *               {@linkplain #setCoordinateReferenceSystem(String) authority code}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #envelope}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setEnvelope(Envelope) Envelope instance} or
 *               {@linkplain #setEnvelope(double[]) ordinate values}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #extent}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setExtent(GridEnvelope) Grid envelope instance} or
 *               {@linkplain #setExtent(int[]) spans} (typically image width and height)&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #pixelAnchor}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setPixelAnchor(PixelInCell) Code list value}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #gridToCRS}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setGridToCRS(MathTransform) transform instance} or
 *               {@linkplain #setGridToCRS(double, double, double, double, double, double) affine transform coefficients}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #gridGeometry}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setGridGeometry(GridGeometry) Grid geometry instance}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link #sampleRanges}&nbsp;</td>
 *     <td>&nbsp;{@linkplain #setSampleRange(int, NumberRange) range instance} or
 *               {@linkplain #setSampleRange(int, int, int) lower and upper values}&nbsp;</td>
 *   </tr>
 * </table>
 *
 * {@section Usage example}
 * {@preformat java
 *     GridCoverageBuilder builder = new GridCoverageBuilder();
 *     builder.setCoordinateReferenceSystem("EPSG:4326");
 *     builder.setEnvelope(-60, 40, -50, 50);
 *
 *     // Will use sample value in the range 0 inclusive to 20000 exclusive.
 *     builder.setSampleRange(0, 20000);
 *
 *     // Defines elevation (m) = sample / 10
 *     GridCoverageBuilder.Variable elevation = builder.newVariable("Elevation", SI.METRE);
 *     elevation.setLinearTransform(0.1, 0);
 *     elevation.addNodataValue("No data", 32767);
 *
 *     // Gets a 600×400 pixels image, then draw something on it.
 *     builder.setExtent(600, 400);
 *     BufferedImage image = builder.getBufferedImage();
 *     Graphics2D gr = image.createGraphics();
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
     * The default {@linkplain #sampleRange}.
     */
    private static final NumberRange<Integer> DEFAULT_RANGE = NumberRange.create(0, true, 256, false);

    /**
     * The initial length of arrays in this class. Those arrays are created only when first needed.
     * A value of 4 is a good match for ARGB images - few images need more bands.
     *
     * @since 3.20
     */
    private static final int INITIAL_ARRAY_LENGTH = 4;

    /**
     * The coordinate reference system, or {@code null} if unspecified. This field is non-null only
     * if the CRS has been {@linkplain #setCoordinateReferenceSystem(CoordinateReferenceSystem)
     * explicitely specified} by the user. The values inferred from other attributes are not stored
     * in the field.
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
     * specified} by the user. The values inferred from other attributes are not stored in the field.
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
     * inferred from other attributes are not stored in the field.
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
     * by the user. The values inferred from other attributes are not stored in the field.
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
     * by the user. The values inferred from other attributes are not stored in the field.
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
     * user. The values inferred from other attributes are not stored in the field.
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
     * Number of sample dimensions (bands), or 0 if unspecified. This field is non-zero only if
     * the number of sample dimensions has been {@linkplain #setNumSampleDimensions(int) explicitely
     * specified} by the user. The values inferred from other attributes are not stored in the field.
     *
     * @see #getNumSampleDimensions()
     * @see #setNumSampleDimensions(int)
     */
    protected int numSampleDimensions;

    /**
     * The range of sample values, or {@code null} if unspecified. This array can have any length,
     * but only the {@link #numSampleDimensions} first elements are valid. Each element can be non-null
     * only if the range has been {@linkplain #setSampleRange(int, NumberRange) explicitely specified}
     * by the user. The values inferred from other attributes are not stored in the field.
     *
     * @see #getSampleRange(int)
     * @see #setSampleRange(int, NumberRange)
     * @see #setSampleRange(int, int, int)
     *
     * @since 3.20 (derived from 2.5)
     */
    protected NumberRange<?>[] sampleRanges;

    /**
     * The list of variables created by the user. Each variable will be mapped to a
     * {@linkplain GridSampleDimension sample dimension}. The list is created when first needed.
     *
     * @see #newVariable(CharSequence, Unit)
     */
    private List<Variable> variables;

    /**
     * The image. Will be created only when first needed.
     */
    private BufferedImage image;

    /**
     * The grid coverage. Will be created only when first needed.
     */
    private GridCoverage2D coverage;

    /**
     * Optional hints, or {@code null} if none.
     *
     * @since 3.20
     */
    protected Hints hints;

    /**
     * Creates a uninitialized builder. All fields values are {@code null}.
     */
    public GridCoverageBuilder() {
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
     * Returns {@link #gridGeometry} only if it is an instance of {@link GeneralGridGeometry}
     * and the property indicated by the given flag is defined, or {@code null} otherwise.
     *
     * @param flag One of {@link GeneralGridGeometry} constants.
     */
    private GeneralGridGeometry gridGeometry(final int flag) {
        if (gridGeometry instanceof GeneralGridGeometry) {
            final GeneralGridGeometry gg = (GeneralGridGeometry) gridGeometry;
            if (gg.isDefined(flag)) {
                return gg;
            }
        }
        return null;
    }

    /**
     * Returns the current coordinate reference system. If no CRS has been
     * {@linkplain #setCoordinateReferenceSystem(CoordinateReferenceSystem) explicitly defined},
     * then this method returns the {@linkplain #envelope} CRS or the {@linkplain #gridGeometry
     * grid geometry} CRS.
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
            final Envelope env = getEnvelope();
            if (env != null) {
                return env.getCoordinateReferenceSystem();
            }
            final GeneralGridGeometry gridGeometry = gridGeometry(GeneralGridGeometry.CRS);
            if (gridGeometry != null) {
                return gridGeometry.getCoordinateReferenceSystem();
            }
        }
        return crs;
    }

    /**
     * Sets the coordinate reference system to the specified value. If an
     * {@linkplain #setEnvelope(Envelope) envelope was previously defined},
     * then it will be reprojected to the new CRS.
     *
     * @param  crs The new CRS to use, or {@code null}.
     * @throws IllegalArgumentException if the CRS is illegal for the
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
     * Sets the coordinate reference system to the specified authority code. This convenience
     * method gives a preference to axis in (<var>longitude</var>, <var>latitude</var>) order.
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
     * Returns the current envelope. If no envelope has been {@linkplain #setEnvelope(Envelope)
     * explicitly defined}, then this method returns the {@link #gridGeometry grid geometry}
     * envelope.
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
            final GeneralGridGeometry gridGeometry = gridGeometry(GeneralGridGeometry.ENVELOPE);
            if (gridGeometry != null) {
                return gridGeometry.getEnvelope();
            }
        }
        return envelope;
    }

    /**
     * Sets the envelope to the specified value. If a CRS has been
     * {@linkplain #setCoordinateReferenceSystem(CoordinateReferenceSystem) explicitely defined},
     * then the given envelope will be reprojected to that CRS.
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
     * <p>
     * <b>Example:</b>
     * (<var>x</var><sub>min</sub>, <var>y</var><sub>min</sub>, <var>z</var><sub>min</sub>,
     *  <var>x</var><sub>max</sub>, <var>y</var><sub>max</sub>, <var>z</var><sub>max</sub>)
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
     * Returns the current grid extent. If no extent has been {@linkplain #setExtent(GridEnvelope)
     * explicitly defined}, then this method returns the {@link #gridGeometry grid geometry} extent.
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
            final GridGeometry g = gridGeometry;
            if (g != null) {
                if (g instanceof GeneralGridGeometry) {
                    if (!((GeneralGridGeometry) g).isDefined(GeneralGridGeometry.EXTENT)) {
                        return null;
                    }
                }
                return g.getExtent();
            }
        }
        return extent;
    }

    /**
     * Sets the grid extent to the specified value.
     *
     * @param  extent The new grid extent to use, or {@code null}.
     * @throws IllegalArgumentException if the extent is illegal.
     *
     * @since 3.20
     */
    public void setExtent(final GridEnvelope extent) throws IllegalArgumentException {
        GridEnvelope newValue = extent;
        if (extent != null) {
            final MathTransform gridToCRS = this.gridToCRS;
            if (gridToCRS != null) {
                ensureDimensionMatch("extent", extent.getDimension(), gridToCRS.getSourceDimensions());
            }
        }
        this.extent = newValue;
        gridGeometryChanged();
    }

    /**
     * Sets the grid extent to a grid envelope having the given span.
     * The {@link GridEnvelope#getLow() low ordinate values} are set to 0.
     * <p>
     * This method is typically invoked for defining image dimension as below:
     *
     * {@preformat java
     *     setExtent(width, height);
     * }
     *
     * @param  span The span values for all dimensions.
     * @throws IllegalArgumentException if the extent is illegal.
     *
     * @since 3.20
     */
    public void setExtent(final int... span) throws IllegalArgumentException {
        setExtent(new GeneralGridEnvelope(new int[span.length], span, false));
    }

    /**
     * Returns the current <cite>grid to CRS</cite> transform. If no transform has been
     * {@linkplain #setGridToCRS(MathTransform) explicitly defined}, then this method
     * returns the {@link #gridGeometry grid geometry} transform. Whatever the returned
     * transform maps pixel centers or pixel corners depends on the {@link #pixelAnchor} value.
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
            final GridGeometry g = gridGeometry;
            if (g != null) {
                final PixelInCell p = pixelAnchor;
                if (p == null) {
                    return g.getGridToCRS();
                } else if (g instanceof GeneralGridGeometry) {
                    return ((GeneralGridGeometry) g).getGridToCRS(p);
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
     * The number of {@linkplain MathTransform#getSourceDimensions() source dimensions} shall
     * match the grid {@linkplain #extent} dimensions (if any), and the number of
     * {@linkplain MathTransform#getTargetDimensions() target dimensions} shall matches the
     * {@linkplain #crs} and {@linkplain #envelope} dimensions (if any).
     *
     * @param  gridToCRS The new <cite>grid to CRS</cite> transform, or {@code null}.
     * @throws IllegalArgumentException If the given transform is invalid, for example if the
     *         dimensions don't match.
     *
     * @since 3.20
     */
    public void setGridToCRS(final MathTransform gridToCRS) throws IllegalArgumentException {
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
     * Sets the <cite>grid to CRS</cite> transform from the given affine transform coefficients.
     * Whatever the transform maps pixel centers or pixel corners depends on the {@link #pixelAnchor}
     * value.
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
     * @since 3.20
     */
    public void setGridToCRS(double m00, double m10, double m01, double m11, double m02, double m12) {
        setGridToCRS(new AffineTransform2D(m00, m10, m01, m11, m02, m12));
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
     * Sets the whatever the {@linkplain #gridToCRS grid to CRS} transform maps pixel center
     * or pixel corner.
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
     * method builds a default instance from the values returned <u>one</u> of the
     * following set of getter methods:
     * <p>
     * <table><tr><td><ul>
     *   <li>{@link #getExtent()}</li>
     *   <li>{@link #getPixelAnchor()}</li>
     *   <li>{@link #getGridToCRS()}</li>
     *   <li>{@link #getCoordinateReferenceSystem()}</li>
     * </ul>
     * </td><td><b>or</b></td><td>
     * <ul>
     *   <li>{@link #getExtent()}</li>
     *   <li>{@link #getEnvelope()}</li>
     * </ul></td></tr></table>
     * <p>
     * Note that the grid geometry build from an envelope uses heuristic rules documented in
     * {@linkplain GeneralGridGeometry#GeneralGridGeometry(GridEnvelope,Envelope) here}.
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
                    geom = new GridGeometry2D(extent, getEnvelope());
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
        image              = null;
        coverage           = null;
        cachedGridGeometry = null;
    }

    /**
     * Invoked when a property used for computing the sample dimensions has been changed.
     *
     * @since 3.20
     */
    private void sampleDimensionsChanged() {
        coverage = null;
    }

    /**
     * Returns the number of sample dimensions (bands). If this number has not been
     * {@linkplain #setNumSampleDimensions(int) explicitly defined}, then this method
     * returns the number of bands in the {@link #image}, if any. If there is no image
     * neither, then the default value is 1.
     *
     * @return The number of sample dimensions (bands).
     *
     * @see #numSampleDimensions
     *
     * @since 3.20
     */
    public int getNumSampleDimensions() {
        int n = numSampleDimensions;
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
     * Sets the number of sample dimensions (bands).
     *
     * @param n Number of sample dimensions, or 0 to unspecify.
     *
     * @since 3.20
     */
    public void setNumSampleDimensions(final int n) {
        ArgumentChecks.ensurePositive("n", n);
        numSampleDimensions = n;
        sampleDimensionsChanged();
    }

    /**
     * Returns the last non-null element at the given index or in a previous index
     * in the given array.
     *
     * @param  <E>   The type of array elements.
     * @param  array The array from which to get an element, or {@code null}.
     * @param  band  The index of the sample dimension for which to get the element.
     * @return The array element for the given sample dimension, or {@code null}.
     * @throws IndexOutOfBoundsException If the given {@code band} index is out of bounds.
     *
     * @since 3.20
     */
    private <E> E getArrayElement(final E[] array, int band) throws IndexOutOfBoundsException {
        ArgumentChecks.ensureValidIndex(getNumSampleDimensions(), band);
        if (band >= array.length) {
            band = array.length - 1;
        }
        for (; band >= 0; band--) {
            final E element = array[band];
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    /**
     * Sets the value at the given index in the given array. If the given index is equals or
     * greater than the {@linkplain #getNumSampleDimensions() number of sample dimensions},
     * then the later will be increased as needed.
     *
     * @param  <E>   The type of array elements.
     * @param  array The array in which to set an element.
     * @param  band  The index of the sample dimension for which to set the element.
     * @param  value The new element value, or {@code null}.
     * @return The array, or a copy of the array if we needed to increase its size.
     * @throws IndexOutOfBoundsException If the given {@code band} index is negative.
     *
     * @since 3.20
     */
    private <E> E[] setArrayElement(E[] array, final int band, final E value) throws IndexOutOfBoundsException {
        if (band >= numSampleDimensions) {
            numSampleDimensions = band+1;
        }
        ArgumentChecks.ensureValidIndex(getNumSampleDimensions(), band);
        final boolean expand = (band >= array.length);
        if (value != null || !expand) {
            if (expand) {
                array = Arrays.copyOf(array, Math.max(array.length*2, band+1));
            }
            array[band] = value;
        }
        return array;
    }

    /**
     * Returns the range of sample values for the given sample dimension. If no range has been
     * {@linkplain #setSampleRange(int, NumberRange) explicitly defined}, then this method
     * searches backward in previous sample dimensions until a range is found. If no range
     * is found, then the default is a range from 0 inclusive to 256 exclusive.
     *
     * @param  band The index of the sample dimension for which to get the range of sample values.
     * @return The current range of sample values for the given index.
     * @throws IndexOutOfBoundsException If the given {@code band} index is out of bounds.
     *
     * @since 3.20 (derived from 2.5)
     */
    public NumberRange<?> getSampleRange(final int band) throws IndexOutOfBoundsException {
        final NumberRange<?> sampleRange = getArrayElement(sampleRanges, band);
        return (sampleRange != null) ? sampleRange : DEFAULT_RANGE;
    }

    /**
     * Sets the range of sample values for the given sample dimension. If the given index is equals
     * or greater than the {@linkplain #getNumSampleDimensions() number of sample dimensions}, then
     * the later will be increased as needed.
     *
     * @param  band  The index of the sample dimension for which to set the range.
     * @param  range The new range of sample values, or {@code null}.
     * @throws IndexOutOfBoundsException If the given {@code band} index is negative.
     *
     * @since 3.20 (derived from 2.5)
     */
    public void setSampleRange(final int band, final NumberRange<?> range) throws IndexOutOfBoundsException {
        if (sampleRanges == null) {
            sampleRanges = new NumberRange<?>[INITIAL_ARRAY_LENGTH];
        }
        sampleRanges = setArrayElement(sampleRanges, band, range);
        sampleDimensionsChanged();
    }

    /**
     * Sets the range of sample values for the given sample dimension. If the given index is equals
     * or greater than the {@linkplain #getNumSampleDimensions() number of sample dimensions}, then
     * the later will be increased as needed.
     *
     * @param  band  The index of the sample dimension for which to set the range.
     * @param  lower The lower sample value (inclusive), typically 0.
     * @param  upper The upper sample value (exclusive), typically 256.
     * @throws IndexOutOfBoundsException If the given {@code band} index is negative.
     *
     * @since 3.20 (derived from 2.5)
     */
    public void setSampleRange(final int band, final int lower, final int upper) throws IndexOutOfBoundsException {
        setSampleRange(band, NumberRange.create(lower, true, upper, false));
    }

    /**
     * @deprecated Replaced by {@link #getSampleRange(int)}.
     */
    @Deprecated
    public NumberRange<?> getSampleRange() {
        return getSampleRange(0);
    }

    /**
     * @deprecated Replaced by {@link #getSampleRange(int)}.
     */
    @Deprecated
    public void setSampleRange(final NumberRange<?> range) {
        setSampleRange(0, range);
    }

    /**
     * @deprecated Replaced by {@link #getSampleRange(int, int, int)}.
     */
    @Deprecated
    public void setSampleRange(final int lower, final int upper) {
        setSampleRange(0, lower, upper);
    }

    /**
     * Returns the image size.
     *
     * @return The current image size.
     *
     * @deprecated Replaced by {@link #getExtent()}.
     */
    @Deprecated
    public Dimension getImageSize() {
        if (extent != null) {
            return new Dimension(extent.getSpan(0), extent.getSpan(1));
        }
        return null;
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
     * Returns the variables array, creating it if needed.
     *
     * @since 3.20
     */
    private List<Variable> variables() {
        if (variables == null) {
            variables = new ArrayList<Variable>();
        }
        return variables;
    }

    /**
     * Creates a new variable, which will be mapped to a {@linkplain GridSampleDimension sample
     * dimension}. Additional information like scale, offset and nodata values can be provided
     * by invoking setters on the returned variable.
     *
     * @param  name  The variable name, or {@code null} for a default name.
     * @param  units The variable units, or {@code null} if unknown.
     * @return A new variable.
     */
    public Variable newVariable(final CharSequence name, final Unit<?> units) {
        final Variable variable = new Variable(name, units);
        variables().add(variable);
        return variable;
    }

    /**
     * Returns the buffered image to be wrapped by {@link GridCoverage2D}. If no image has been
     * {@linkplain #setBufferedImage explicitly defined}, a new one is created the first time
     * this method is invoked. Users can write in this image before to create the grid coverage.
     *
     * @return The buffered image to be wrapped by {@code GridCoverage2D}.
     */
    public BufferedImage getBufferedImage() {
        if (image == null) {
            final Dimension size = getImageSize();
            if (XCollections.isNullOrEmpty(variables)) {
                image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_BYTE_GRAY);
            } else {
                final List<Variable> variables = variables();
                final int numBands = variables.size();
                final GridSampleDimension sd = variables.get(0).build();
                final ColorModel cm;
                if (numBands == 1) {
                    cm = sd.getColorModel();
                } else {
                    cm = sd.getColorModel(0, numBands);
                }
                final WritableRaster raster = cm.createCompatibleWritableRaster(size.width, size.height);
                image = new BufferedImage(cm, raster, false, null);
            }
        }
        return image;
    }

    /**
     * Sets the buffered image. Invoking this method overwrite the
     * {@linkplain #getImageSize() image size} with the given image size.
     *
     * @param image The buffered image to be wrapped by {@code GridCoverage2D}.
     */
    public void setBufferedImage(final BufferedImage image) {
        setImageSize(image.getWidth(), image.getHeight());
        this.image = image; // Stores only if the above line succeed.
        coverage = null;
    }

    /**
     * Sets the buffered image by reading it from the given file. Invoking this method
     * overwrite the {@linkplain #getImageSize() image size} with the given image size.
     *
     * @param file The file of the image to be wrapped by {@code GridCoverage2D}.
     * @throws IOException if the image can't be read.
     */
    public void setBufferedImage(final File file) throws IOException {
        setBufferedImage(ImageIO.read(file));
    }

    /**
     * Sets the buffered image to a raster filled with random value using the specified random
     * number generator. This method can be used for testing purpose, or for adding noise to a
     * coverage.
     *
     * @param random The random number generator to use for generating pixel values.
     */
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
     * Returns the grid coverage.
     *
     * @return The grid coverage.
     */
    public GridCoverage2D getGridCoverage2D() {
        if (coverage == null) {
            final BufferedImage image = getBufferedImage();
            final GridSampleDimension[] bands;
            if (XCollections.isNullOrEmpty(variables)) {
                bands = null;
            } else {
                final List<Variable> variables = variables();
                bands = new GridSampleDimension[variables.size()];
                for (int i=0; i<bands.length; i++) {
                    bands[i] = variables.get(i).build();
                }
            }
            coverage = new GridCoverage2D(
                    null,
                    PlanarImage.wrapRenderedImage(image),
                    GridGeometry2D.castOrCopy(getGridGeometry()),
                    bands,
                    null,
                    null,
                    hints);
        }
        return coverage;
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
     * A variable to be mapped to a {@linkplain GridSampleDimension sample dimension}.
     * Variables are created by {@link GridCoverageBuilder#newVariable(CharSequence, Unit)}.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.20
     *
     * @since 2.5
     * @module
     */
    public class Variable extends Builder<GridSampleDimension> {
        /**
         * The variable name, or {@code null} for a default name.
         */
        private final CharSequence name;

        /**
         * The variable units, or {@code null} for a default units.
         */
        private final Unit<?> units;

        /**
         * The "nodata" values.
         */
        private final Map<Integer,CharSequence> nodata;

        /**
         * The "<cite>sample to geophysics</cite>" transform.
         */
        private MathTransform1D transform;

        /**
         * The sample dimension. Will be created when first needed. May be reset to {@code null}
         * after creation if a new sample dimension need to be computed.
         */
        private GridSampleDimension sampleDimension;

        /**
         * Creates a new variable of the given name and units.
         *
         * @param name  The variable name, or {@code null} for a default name.
         * @param units The variable units, or {@code null} if unknown.
         *
         * @see GridCoverageBuilder#newVariable
         */
        protected Variable(final CharSequence name, final Unit<?> units) {
            this.name   = name;
            this.units  = units;
            this.nodata = new TreeMap<Integer,CharSequence>();
        }

        /**
         * Returns the "<cite>sample to units</cite>" transform, or {@code null} if none.
         *
         * @return The "<cite>sample to units</cite>" transform, or {@code null}.
         */
        public MathTransform1D getTransform() {
            return transform;
        }

        /**
         * Sets the "<cite>sample to units</cite>" transform.
         *
         * @param transform The new "<cite>sample to units</cite>" transform.
         */
        public void setTransform(final MathTransform1D transform) {
            this.transform = transform;
            sampleDimension = null;
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
            final Integer key = value;
            final CharSequence old = nodata.put(key, name);
            if (old != null) {
                nodata.put(key, old);
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_$2, "value", key));
            }
            sampleDimension = null;
        }

        /**
         * Returns a sample dimension for the current
         * {@linkplain GridCoverageBuilder#getSampleRange range of sample values}.
         *
         * @return The sample dimension for the current range of sample values.
         */
        @Override
        public GridSampleDimension build() {
            if (sampleDimension == null) {
                NumberRange<?> range = getSampleRange();
                int lower = (int) Math.floor(range.getMinimum(true));
                int upper = (int) Math.ceil (range.getMaximum(false));
                final Category[] categories = new Category[nodata.size() + 1];
                int i = 0;
                for (final Map.Entry<Integer,CharSequence> entry : nodata.entrySet()) {
                    final int sample = entry.getKey();
                    if (sample >= lower && sample < upper) {
                        if (sample - lower <= upper - sample) {
                            lower = sample + 1;
                        } else {
                            upper = sample;
                        }
                    }
                    categories[i++] = new Category(entry.getValue(), null, sample);
                }
                range = NumberRange.create(lower, true, upper, false);
                categories[i] = new Category(name, null, range, (transform != null) ?
                        transform : LinearTransform1D.IDENTITY);
                sampleDimension = new GridSampleDimension(name, categories, units);
            }
            return sampleDimension;
        }

        /**
         * @deprecated Renamed {@link #build()}.
         */
        @Deprecated
        public GridSampleDimension getSampleDimension() {
            return build();
        }

        /**
         * Returns a string representation of this variable.
         */
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder(getClass().getSimpleName());
            buffer.append('[');
            if (name != null) {
                buffer.append('"').append(name).append('"');
                if (units != null) {
                    buffer.append(' ');
                }
            }
            if (units != null) {
                buffer.append('(').append(units).append(')');
            }
            return buffer.append(']').toString();
        }
    }
}
