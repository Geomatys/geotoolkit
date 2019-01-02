/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BandedSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.media.jai.ImageFunction;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertySource;
import javax.media.jai.PropertySourceImpl;
import javax.media.jai.TiledImage;
import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;
import javax.media.jai.operator.ImageFunctionDescriptor;

import org.opengis.coverage.*; // Numerous import, including many for javadoc.
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.temporal.Period;
import org.opengis.metadata.extent.Extent;
import org.opengis.util.InternationalString;
import org.opengis.util.Record;
import org.opengis.util.RecordType;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.geotoolkit.io.LineWriter;
import org.apache.sis.util.Localized;
import org.apache.sis.util.Classes;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Debug;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.util.iso.Types;
import org.apache.sis.internal.raster.ScaledColorSpace;
import org.geotoolkit.image.internal.ImageUtilities;


/**
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
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public abstract class AbstractCoverage extends PropertySourceImpl implements Coverage, Localized {
    /**
     * For compatibility during cross-version serialization.
     */
    private static final long serialVersionUID = -2989320942499746295L;

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
     * The coordinate reference system, or {@code null} if there is none.
     */
    protected final CoordinateReferenceSystem crs;

    /**
     * Constructs a coverage using the specified coordinate reference system. If the coordinate
     * reference system is {@code null}, then the subclasses must override {@link #getDimension()}.
     *
     * @param name
     *          The coverage name, or {@code null} if none.
     * @param crs
     *          The coordinate reference system. This specifies the CRS used when accessing
     *          a coverage or grid coverage with the {@code evaluate(...)} methods.
     * @param propertySource
     *          The source for this coverage, or {@code null} if none. Source may be (but is not
     *          limited to) a {@link PlanarImage} or an other {@code AbstractCoverage} object.
     * @param properties
     *          The set of properties for this coverage, or {@code null} if there is none.
     *          Keys are {@link String} objects ({@link javax.media.jai.util.CaselessStringKey}
     *          are accepted as well), while values may be any {@link Object}.
     */
    protected AbstractCoverage(final CharSequence             name,
                               final CoordinateReferenceSystem crs,
                               final PropertySource propertySource,
                               final Map<?,?>           properties)
    {
        super(properties, propertySource);
        this.name = Types.toInternationalString(name);
        this.crs  = crs;
    }

    /**
     * Constructs a new coverage with the same parameters than the specified coverage.
     *
     * {@note This constructor keeps a strong reference to the source
     *        coverage through the <code>PropertySourceImpl</code> super class}.
     *
     * @param name
     *          The name for this coverage, or {@code null} for the same than {@code coverage}.
     * @param coverage
     *          The source coverage.
     */
    protected AbstractCoverage(final CharSequence name, final Coverage coverage) {
        super(null, (coverage instanceof PropertySource) ? (PropertySource) coverage : null);
        final InternationalString n = Types.toInternationalString(name);
        if (coverage instanceof AbstractCoverage) {
            final AbstractCoverage source = (AbstractCoverage) coverage;
            this.name = (n != null) ? n : source.name;
            this.crs  = source.crs;
        } else {
            this.name = (n != null) ? n : new SimpleInternationalString(coverage.toString());
            this.crs  = coverage.getCoordinateReferenceSystem();
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
     * Returns the dimension of this coverage. This is a shortcut for
     * <code>{@linkplain #crs}.getCoordinateSystem().getDimension()</code>.
     *
     * @return The dimension of this coverage.
     */
    public final int getDimension() {
        return crs.getCoordinateSystem().getDimension();
    }

    /**
     * Returns the coordinate reference system to which the objects in its domain are
     * referenced. This is the CRS used when accessing a coverage or grid coverage with
     * the {@code evaluate(...)} methods. This coordinate reference system is usually
     * different than coordinate system of the grid. It is the target coordinate reference
     * system of the {@link org.geotoolkit.coverage.grid.GridGeometry#getGridToCRS gridToCRS}
     * math transform.
     * <p>
     * Grid coverage can be accessed (re-projected) with new coordinate reference system
     * with the {@link org.opengis.coverage.processing.GridCoverageProcessor} component.
     * In this case, a new instance of a grid coverage is created.
     *
     * @return The coordinate reference system used when accessing a coverage or
     *         grid coverage with the {@code evaluate(...)} methods.
     *
     * @see org.geotoolkit.coverage.grid.GeneralGridGeometry#getGridToCRS
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
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
    @Override
    public Envelope getEnvelope() {
        return CRS.getDomainOfValidity(crs);
    }

    /**
     * Returns the extent of the domain of the coverage. Extents may be specified in space,
     * time or space-time. The collection must contains at least one element.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     *
     * @todo Proposed default implementation: invokes {@link #getEnvelope}, extract
     *       the spatial and temporal parts and put them in a {@link Extent} object.
     */
    @Override
    public Set<Extent> getDomainExtents() {
        throw unsupported();
    }

    /**
     * Returns the set of domain objects in the domain.
     * The collection must contains at least one element.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     *
     * @todo Proposed default implementation: invokes {@link #getEnvelope}, extract the
     *       spatial and temporal parts, get the grid geometry and create on-the-fly a
     *       {@link DomainObject} for each cell.
     */
    @Override
    public Set<? extends DomainObject<?>> getDomainElements() {
        throw unsupported();
    }

    /**
     * Returns the set of attribute values in the range. The range of a coverage shall be a
     * homogeneous collection of records. That is, the range shall have a constant dimension
     * over the entire domain, and each field of the record shall provide a value of the same
     * attribute type over the entire domain.
     * <p>
     * In the case of a {@linkplain DiscreteCoverage discrete coverage}, the size of the range
     * collection equals that of the {@linkplain #getDomainElements domains} collection. In other
     * words, there is one instance of {@link AttributeValues} for each instance of
     * {@link DomainObject}. Usually, these are stored values that are accessed by the
     * {@link #evaluate(DirectPosition,Collection) evaluate} operation.
     * <p>
     * In the case of a {@linkplain ContinuousCoverage continuous coverage}, there is a transfinite
     * number of instances of {@link AttributeValues} for each {@link DomainObject}. A few instances
     * may be stored as input for the {@link #evaluate(DirectPosition,Collection) evaluate}
     * operation, but most are generated as needed by that operation.
     * <p>
     * <B>NOTE:</B> ISO 19123 does not specify how the {@linkplain #getDomainElements domain}
     * and {@linkplain #getRangeElements range} associations are to be implemented. The relevant
     * data may be generated in real time, it may be held in persistent local storage, or it may
     * be electronically accessible from remote locations.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     */
    @Override
    public Set<AttributeValues> getRangeElements() {
        throw unsupported();
    }

    /**
     * Describes the range of the coverage. It consists of a list of attribute name/data type pairs.
     * A simple list is the most common form of range type, but {@code RecordType} can be used
     * recursively to describe more complex structures. The range type for a specific coverage
     * shall be specified in an application schema.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     */
    @Override
    public RecordType getRangeType() {
        throw unsupported();
    }

    /**
     * Identifies the procedure to be used for evaluating the coverage at a position that falls
     * either on a boundary between geometric objects or within the boundaries of two or more
     * overlapping geometric objects. The geometric objects are either {@linkplain DomainObject
     * domain objects} or {@linkplain ValueObject value objects}.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     */
    @Override
    public CommonPointRule getCommonPointRule() {
        throw unsupported();
    }

    /**
     * Returns the dictionary of <var>geometry</var>-<var>value</var> pairs that contain the
     * {@linkplain DomainObject objects} in the domain of the coverage each paired with its
     * record of feature attribute values. In the case of an analytical coverage, the operation
     * shall return the empty set.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     */
    @Override
    public Set<? extends GeometryValuePair> list() {
        throw unsupported();
    }

    /**
     * Returns the set of <var>geometry</var>-<var>value</var> pairs that contain
     * {@linkplain DomainObject domain objects} that lie within the specified geometry and period.
     * If {@code s} is null, the operation shall return all <var>geometry</var>-<var>value</var>
     * pairs that contain {@linkplain DomainObject domain objects} within {@code t}. If the value
     * of {@code t} is null, the operation shall return all <var>geometry</var>-<var>value</var>
     * pair that contain {@linkplain DomainObject domain objects} within {@code s}. In the case
     * of an analytical coverage, the operation shall return the empty set.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     */
    @Override
    public Set<? extends GeometryValuePair> select(Geometry s, Period t) {
        throw unsupported();
    }

    /**
     * Returns the sequence of <var>geometry</var>-<var>value</var> pairs that include the
     * {@linkplain DomainObject domain objects} nearest to the direct position and their
     * distances from the direction position. The sequence shall be ordered by distance from
     * the direct position, beginning with the record containing the {@linkplain DomainObject
     * domain object} nearest to the direct position. The length of the sequence (the number
     * of <var>geometry</var>-<var>value</var> pairs returned) shall be no greater than the
     * number specified by the parameter {@code limit}. The default shall be to return a single
     * <var>geometry</var>-<var>value</var> pair. The operation shall return a warning if the
     * last {@linkplain DomainObject domain object} in the sequence is at a distance from the
     * direct position equal to the distance of other {@linkplain DomainObject domain objects}
     * that are not included in the sequence. In the case of an analytical coverage, the operation
     * shall return the empty set.
     * <p>
     * <B>NOTE:</B> This operation is useful when the domain of a coverage does not exhaustively
     * partition the extent of the coverage. Even in that case, the first element of the sequence
     * returned may be the <var>geometry</var>-<var>value</var> pair that contains the input direct
     * position.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     */
    @Override
    public List<? extends GeometryValuePair> find(DirectPosition p, int limit) {
        throw unsupported();
    }

    /**
     * Returns the nearest <var>geometry</var>-<var>value</var> pair
     * from the specified direct position. This is a shortcut for
     * <code>{@linkplain #find(DirectPosition,int) find}(p,1)</code>.
     *
     * @since 2.3
     */
    @Override
    public GeometryValuePair find(final DirectPosition p) {
        final List<? extends GeometryValuePair> pairs = find(p, 1);
        return pairs.isEmpty() ? null : (GeometryValuePair) pairs.get(0);
    }

    /**
     * Invoked when an unsupported operation is invoked.
     */
    private static UnsupportedOperationException unsupported() {
        throw new UnsupportedOperationException(
                "This method is currently not implemented. " +
                "It may be implemented by next version of coverage module.");
    }

    /**
     * Returns a localized error message for the specified array.
     */
    private static String formatErrorMessage(final Object array) {
        Class<?> type = null;
        if (array != null) {
            type = array.getClass();
            if (type.isArray()) {
                type = type.getComponentType();
            }
        }
        return Errors.format(Errors.Keys.CantConvertFromType_1, type);
    }

    /**
     * Returns a set of records of feature attribute values for the specified direct position. The
     * parameter {@code list} is a sequence of feature attribute names each of which identifies a
     * field of the range type. If {@code list} is null, the operation shall return a value for
     * every field of the range type. Otherwise, it shall return a value for each field included in
     * {@code list}. If the direct position passed is not in the domain of the coverage, then an
     * exception is thrown. If the input direct position falls within two or more geometric objects
     * within the domain, the operation shall return records of feature attribute values computed
     * according to the {@linkplain #getCommonPointRule common point rule}.
     * <P>
     * <B>NOTE:</B> Normally, the operation will return a single record of feature attribute values.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     */
    @Override
    public Set<Record> evaluate(final DirectPosition p, final Collection<String> list) {
        throw unsupported();
    }

    /**
     * Returns a sequence of boolean values for a given point in the coverage. A value for each
     * {@linkplain SampleDimension sample dimension} is included in the sequence. The default
     * interpolation type used when accessing grid values for points which fall between grid cells
     * is {@linkplain javax.media.jai.InterpolationNearest nearest neighbor}, but it can be changed
     * by some {@linkplain org.geotoolkit.coverage.grid.Interpolator2D subclasses}. The CRS of the
     * point is the same as the grid coverage {@linkplain #getCoordinateReferenceSystem coordinate
     * reference system}.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if the evaluation failed because the input point
     *         has invalid coordinates.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate
     *         for an other reason. It may be thrown if the coverage data type can't be converted
     *         to {@code boolean} by an identity or widening conversion. Subclasses may relax this
     *         constraint if appropriate.
     */
    @Override
    public boolean[] evaluate(final DirectPosition coord, boolean[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        final Object array = evaluate(coord);
        try {
            final int length = Array.getLength(array);
            if (dest == null) {
                dest = new boolean[length];
            }
            for (int i=0; i<length; i++) {
                dest[i] = Array.getBoolean(array, i);
            }
        } catch (IllegalArgumentException exception) {
            throw new CannotEvaluateException(formatErrorMessage(array), exception);
        }
        return dest;
    }

    /**
     * Returns a sequence of byte values for a given point in the coverage. A value for each
     * {@linkplain SampleDimension sample dimension} is included in the sequence. The default
     * interpolation type used when accessing grid values for points which fall between grid cells
     * is {@linkplain javax.media.jai.InterpolationNearest nearest neighbor}, but it can be changed
     * by some {@linkplain org.geotoolkit.coverage.grid.Interpolator2D subclasses}. The CRS of the
     * point is the same as the grid coverage {@linkplain #getCoordinateReferenceSystem coordinate
     * reference system}.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if the evaluation failed because the input point
     *         has invalid coordinates.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate
     *         for an other reason. It may be thrown if the coverage data type can't be converted
     *         to {@code byte} by an identity or widening conversion. Subclasses may relax this
     *         constraint if appropriate.
     */
    @Override
    public byte[] evaluate(final DirectPosition coord, byte[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        final Object array = evaluate(coord);
        try {
            final int length = Array.getLength(array);
            if (dest == null) {
                dest = new byte[length];
            }
            for (int i=0; i<length; i++) {
                dest[i] = Array.getByte(array, i);
            }
        } catch (IllegalArgumentException exception) {
            throw new CannotEvaluateException(formatErrorMessage(array), exception);
        }
        return dest;
    }

    /**
     * Returns a sequence of integer values for a given point in the coverage. A value for each
     * {@linkplain SampleDimension sample dimension} is included in the sequence. The default
     * interpolation type used when accessing grid values for points which fall between grid cells
     * is {@linkplain javax.media.jai.InterpolationNearest nearest neighbor}, but it can be changed
     * by some {@linkplain org.geotoolkit.coverage.grid.Interpolator2D subclasses}. The CRS of the
     * point is the same as the grid coverage {@linkplain #getCoordinateReferenceSystem coordinate
     * reference system}.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if the evaluation failed because the input point
     *         has invalid coordinates.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate
     *         for an other reason. It may be thrown if the coverage data type can't be converted
     *         to {@code int} by an identity or widening conversion. Subclasses may relax this
     *         constraint if appropriate.
     */
    @Override
    public int[] evaluate(final DirectPosition coord, int[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        final Object array = evaluate(coord);
        try {
            final int length = Array.getLength(array);
            if (dest == null) {
                dest = new int[length];
            }
            for (int i=0; i<length; i++) {
                dest[i] = Array.getInt(array, i);
            }
        } catch (IllegalArgumentException exception) {
            throw new CannotEvaluateException(formatErrorMessage(array), exception);
        }
        return dest;
    }

    /**
     * Returns a sequence of float values for a given point in the coverage. A value for each
     * {@linkplain SampleDimension sample dimension} is included in the sequence. The default
     * interpolation type used when accessing grid values for points which fall between grid cells
     * is {@linkplain javax.media.jai.InterpolationNearest nearest neighbor}, but it can be changed
     * by some {@linkplain org.geotoolkit.coverage.grid.Interpolator2D subclasses}. The CRS of the
     * point is the same as the grid coverage {@linkplain #getCoordinateReferenceSystem coordinate
     * reference system}.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if the evaluation failed because the input point
     *         has invalid coordinates.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate
     *         for an other reason. It may be thrown if the coverage data type can't be converted
     *         to {@code float} by an identity or widening conversion. Subclasses may relax this
     *         constraint if appropriate.
     */
    @Override
    public float[] evaluate(final DirectPosition coord, float[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        final Object array = evaluate(coord);
        try {
            final int length = Array.getLength(array);
            if (dest == null) {
                dest = new float[length];
            }
            for (int i=0; i<length; i++) {
                dest[i] = Array.getFloat(array, i);
            }
        } catch (IllegalArgumentException exception) {
            throw new CannotEvaluateException(formatErrorMessage(array), exception);
        }
        return dest;
    }

    /**
     * Returns a sequence of double values for a given point in the coverage. A value for each
     * {@linkplain SampleDimension sample dimension} is included in the sequence. The default
     * interpolation type used when accessing grid values for points which fall between grid cells
     * is {@linkplain javax.media.jai.InterpolationNearest nearest neighbor}, but it can be changed
     * by some {@linkplain org.geotoolkit.coverage.grid.Interpolator2D subclasses}. The CRS of the
     * point is the same as the grid coverage {@linkplain #getCoordinateReferenceSystem coordinate
     * reference system}.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if the evaluation failed because the input point
     *         has invalid coordinates.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate
     *         for an other reason. It may be thrown if the coverage data type can't be converted
     *         to {@code double} by an identity or widening conversion. Subclasses may relax this
     *         constraint if appropriate.
     */
    @Override
    public double[] evaluate(final DirectPosition coord, double[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        final Object array = evaluate(coord);
        try {
            final int length = Array.getLength(array);
            if (dest == null) {
                dest = new double[length];
            }
            for (int i=0; i<length; i++) {
                dest[i] = Array.getDouble(array, i);
            }
        } catch (IllegalArgumentException exception) {
            throw new CannotEvaluateException(formatErrorMessage(array), exception);
        }
        return dest;
    }

    /**
     * Returns a set of {@linkplain DomainObject domain objects} for the specified record of feature
     * attribute values. Normally, this method returns the set of {@linkplain DomainObject objects}
     * in the domain that are associated with values equal to those in the input record. However,
     * the operation may return other {@linkplain DomainObject objects} derived from those in the
     * domain, as specified by the application schema.
     * <p>
     * <B>Example:</B> The {@code evaluateInverse} operation could return a set
     * of contours derived from the feature attribute values associated with the
     * {@linkplain org.opengis.coverage.grid.GridPoint grid points} of a grid coverage.
     * <p>
     * <strong>This method is not yet implemented.</strong>
     *
     * @since 2.3
     */
    @Override
    public Set<? extends DomainObject<?>> evaluateInverse(Record v) {
        throw unsupported();
    }

    /**
     * Returns 2D view of this grid coverage as a renderable image. This method
     * allows inter-operability with Java2D.
     *
     * @param xAxis Dimension to use for the <var>x</var> display axis.
     * @param yAxis Dimension to use for the <var>y</var> display axis.
     * @return A 2D view of this grid coverage as a renderable image.
     */
    @Override
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
     * @version 3.00
     *
     * @see AbstractCoverage#getRenderableImage
     *
     * @since 2.0
     * @module
     */
    protected class Renderable extends PropertySourceImpl implements RenderableImage, ImageFunction {
        /**
         * For compatibility during cross-version serialization.
         */
        private static final long serialVersionUID = -6661389795161502552L;

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
         * x} and {@link #yAxis y} ordinates will be ignored, since they will vary for each pixel
         * to be evaluated. Other ordinates, if any, should be set to a fixed value. For example a
         * coverage may be three-dimensional, where the third dimension is the time axis. In such
         * case, {@code coordinate.ord[2]} should be set to the point in time where to evaluate the
         * coverage. By default, all ordinates are initialized to 0. Subclasses should set the
         * desired values in their constructor if needed.
         */
        protected final GeneralDirectPosition coordinate = new GeneralDirectPosition(getDimension());

        /**
         * Constructs a renderable image.
         *
         * @param xAxis Dimension to use for <var>x</var> axis.
         * @param yAxis Dimension to use for <var>y</var> axis.
         */
        public Renderable(final int xAxis, final int yAxis) {
            super(null, AbstractCoverage.this);
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
            final Dimension       tileSize = ImageUtilities.toTileSize(gridBounds.getSize());
            SampleDimension sampleDimension = getSampleDimension(VISIBLE_BAND);
            if (sampleDimension == null)
                throw new IllegalStateException("Sample dimensions are undetermined.");
            final GridSampleDimension band = GridSampleDimension.castOrCopy(sampleDimension);
            final int nbBand = getNumSampleDimensions();
            ColorModel colorModel = band.getColorModel(VISIBLE_BAND, nbBand);
            final SampleModel sampleModel;
            if (colorModel != null) {
                sampleModel = colorModel.createCompatibleSampleModel(tileSize.width, tileSize.height);
            } else {
                sampleModel = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, tileSize.width, tileSize.height, nbBand);
                final ColorSpace colors = new ScaledColorSpace(nbBand, 0, 0, 1);
                colorModel = new ComponentColorModel(colors, false, false, Transparency.OPAQUE, DataBuffer.TYPE_DOUBLE);
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
                                    coordinate.ordinates[xAxis] = point2D.x;
                                    coordinate.ordinates[yAxis] = point2D.y;
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
            final GeneralMatrix matrix;
            final GeneralEnvelope srcEnvelope = new GeneralEnvelope(
                    new double[] {bounds.getMinX(), bounds.getMinY()},
                    new double[] {bounds.getMaxX(), bounds.getMaxY()});
            final GeneralEnvelope dstEnvelope = new GeneralEnvelope(
                    new double[] {gridBounds.getMinX(), gridBounds.getMinY()},
                    new double[] {gridBounds.getMaxX(), gridBounds.getMaxY()});
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
                matrix = new GeneralMatrix(srcEnvelope, axis, dstEnvelope, normalized);
            } else {
                matrix = new GeneralMatrix(srcEnvelope, dstEnvelope);
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
            return getNumSampleDimensions();
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
            int index = 0;
            float[] buffer = null;
            // Clones the coordinate point in order to allow multi-thread invocation.
            final GeneralDirectPosition coordinate = new GeneralDirectPosition(this.coordinate);
            coordinate.ordinates[1] = startY;
            for (int j=0; j<countY; j++) {
                coordinate.ordinates[0] = startX;
                for (int i=0; i<countX; i++) {
                    buffer = evaluate(coordinate, buffer);
                    real[index++] = buffer[dim];
                    coordinate.ordinates[0] += deltaX;
                }
                coordinate.ordinates[1] += deltaY;
            }
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
            coordinate.ordinates[1] = startY;
            for (int j=0; j<countY; j++) {
                coordinate.ordinates[0] = startX;
                for (int i=0; i<countX; i++) {
                    buffer = evaluate(coordinate, buffer);
                    real[index++] = buffer[dim];
                    coordinate.ordinates[0] += deltaX;
                }
                coordinate.ordinates[1] += deltaY;
            }
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
     * Returns the source data for a coverage. The default implementation returns an empty list.
     */
    @Override
    public List<? extends Coverage> getSources() {
        return Collections.emptyList();
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
        final int n = getNumSampleDimensions();
        try {
            filter.write(lineSeparator);
            for (int i=0; i<n; i++) {
                filter.write(getSampleDimension(i).toString());
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
}
