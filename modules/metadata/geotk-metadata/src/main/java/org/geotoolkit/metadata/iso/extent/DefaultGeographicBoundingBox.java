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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.extent;

import java.util.Locale;
import java.util.Objects;
import java.text.FieldPosition;
import java.awt.geom.Rectangle2D;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.measure.AngleFormat;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.referencing.ProxyForMetadata;

import static org.geotoolkit.metadata.iso.extent.DefaultExtent.ensureNonNull;


/**
 * Geographic position of the dataset. This is only an approximate so specifying the coordinate
 * reference system is unnecessary. The CRS shall be geographic with Greenwich prime meridian,
 * but the datum doesn't need to be WGS84.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.20
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "EX_GeographicBoundingBox_Type", propOrder={
    "westBoundLongitude",
    "eastBoundLongitude",
    "southBoundLatitude",
    "northBoundLatitude"
})
@XmlRootElement(name = "EX_GeographicBoundingBox")
public class DefaultGeographicBoundingBox extends AbstractGeographicExtent
        implements GeographicBoundingBox
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3278089380004172514L;

    /**
     * A bounding box ranging from 180°W to 180°E and 90°S to 90°N.
     *
     * @since 2.2
     */
    public static final GeographicBoundingBox WORLD;
    static {
        final DefaultGeographicBoundingBox world = new DefaultGeographicBoundingBox(-180, 180, -90, 90);
        world.freeze();
        WORLD = world;
    }

    /**
     * The western-most coordinate of the limit of the dataset extent.
     * The value is expressed in longitude in decimal degrees (positive east).
     */
    private double westBoundLongitude;

    /**
     * The eastern-most coordinate of the limit of the dataset extent.
     * The value is expressed in longitude in decimal degrees (positive east).
     */
    private double eastBoundLongitude;

    /**
     * The southern-most coordinate of the limit of the dataset extent.
     * The value is expressed in latitude in decimal degrees (positive north).
     */
    private double southBoundLatitude;

    /**
     * The northern-most, coordinate of the limit of the dataset extent.
     * The value is expressed in latitude in decimal degrees (positive north).
     */
    private double northBoundLatitude;

    /**
     * Constructs an initially {@linkplain #isEmpty() empty} geographic bounding box.
     * All longitude and latitude values are initialized to {@link Double#NaN}.
     */
    public DefaultGeographicBoundingBox() {
        westBoundLongitude = Double.NaN;
        eastBoundLongitude = Double.NaN;
        southBoundLatitude = Double.NaN;
        northBoundLatitude = Double.NaN;
    }

    /**
     * Constructs a geographic bounding box initialized to the same values than the specified one.
     *
     * @param box The existing box to use for initializing this geographic bounding box.
     *
     * @see #setBounds(GeographicBoundingBox)
     *
     * @since 2.2
     */
    public DefaultGeographicBoundingBox(final GeographicBoundingBox box) {
        /*
         * We could invokes super(box), but we will perform the assignations explicitly here
         * for performance reason. Warning: it may be a problem if the user creates a subclass
         * and relies on the default MetadataEntity(Object) behavior. Rather than bothering
         * the user with a javadoc warning, I would prefer to find some trick to avoid this
         * issue (todo).
         */
        super();
        setBounds(box);
    }

    /**
     * Constructs a geographic bounding box from the specified envelope. If the envelope contains
     * a CRS, then the bounding box may be projected to a geographic CRS. Otherwise, the envelope
     * is assumed already in appropriate CRS.
     * <p>
     * When coordinate transformation is required, the target geographic CRS is not necessarily
     * {@linkplain org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84 WGS84}. This method
     * preserves the same {@linkplain org.opengis.referencing.datum.Ellipsoid ellipsoid} than
     * in the envelope CRS when possible. This is because geographic bounding box are only
     * approximative and the ISO specification do not mandates a particular CRS, so we avoid
     * transformations that are not strictly necessary.
     *
     * {@note This constructor is available only if the referencing module is on the classpath.}
     *
     * @param  envelope The envelope to use for initializing this geographic bounding box.
     * @throws UnsupportedOperationException if the referencing module is not on the classpath.
     * @throws TransformException if the envelope can't be transformed.
     *
     * @see DefaultExtent#DefaultExtent(Envelope)
     * @see DefaultVerticalExtent#DefaultVerticalExtent(Envelope)
     * @see DefaultTemporalExtent#DefaultTemporalExtent(Envelope)
     *
     * @since 2.2
     */
    public DefaultGeographicBoundingBox(final Envelope envelope) throws TransformException {
        super(true);
        ProxyForMetadata.getInstance().copy(envelope, this);
    }

    /**
     * Constructs a geographic bounding box from the specified rectangle and CRS. If the given
     * CRS is not null, then the rectangle may be projected to a geographic CRS. Otherwise, the
     * rectangle is assumed already in appropriate CRS.
     * <p>
     * When coordinate transformation is required, the target geographic CRS is not necessarily
     * {@linkplain org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84 WGS84}. This method
     * preserves the same {@linkplain org.opengis.referencing.datum.Ellipsoid ellipsoid} than
     * in the given CRS when possible. This is because geographic bounding box are only
     * approximative and the ISO specification do not mandates a particular CRS, so we avoid
     * transformations that are not strictly necessary.
     *
     * {@note This constructor is available only if the referencing module is on the classpath.}
     *
     * @param  bounds The rectangle to use for initializing this geographic bounding box.
     * @param  crs The rectangle CRS, or {@code null}.
     * @throws UnsupportedOperationException if the referencing module is not on the classpath.
     * @throws TransformException if the envelope can't be transformed.
     *
     * @since 3.00
     */
    public DefaultGeographicBoundingBox(final Rectangle2D bounds, final CoordinateReferenceSystem crs)
            throws TransformException
    {
        super(true);
        ProxyForMetadata.getInstance().copy(bounds, crs, this);
    }

    /**
     * Constructs a geographic bounding box from the specified rectangle. The rectangle is assumed
     * in {@linkplain org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84 WGS84} CRS.
     *
     * @param bounds The rectangle to use for initializing this geographic bounding box.
     *
     * @see #setBounds(Rectangle2D)
     */
    public DefaultGeographicBoundingBox(final Rectangle2D bounds) {
        this(bounds.getMinX(), bounds.getMaxX(),
             bounds.getMinY(), bounds.getMaxY());
    }

    /**
     * Creates a geographic bounding box initialized to the specified values.
     * <p>
     * <strong>Caution:</strong> Arguments are expected in the same order than they appear in the
     * ISO 19115 specification. This is different than the order commonly found in Java world,
     * which is rather (<var>x</var><sub>min</sub>, <var>y</var><sub>min</sub>,
     * <var>x</var><sub>max</sub>, <var>y</var><sub>max</sub>).
     *
     * @param westBoundLongitude The minimal <var>x</var> value.
     * @param eastBoundLongitude The maximal <var>x</var> value.
     * @param southBoundLatitude The minimal <var>y</var> value.
     * @param northBoundLatitude The maximal <var>y</var> value.
     *
     * @throws IllegalArgumentException If (<var>west bound</var> &gt; <var>east bound</var>)
     *         or (<var>south bound</var> &gt; <var>north bound</var>). Note that
     *         {@linkplain Double#NaN NaN} values are allowed.
     *
     * @see #setBounds(double, double, double, double)
     */
    public DefaultGeographicBoundingBox(final double westBoundLongitude,
                                        final double eastBoundLongitude,
                                        final double southBoundLatitude,
                                        final double northBoundLatitude)
            throws IllegalArgumentException
    {
        super(true);
        setBounds(westBoundLongitude, eastBoundLongitude,
                  southBoundLatitude, northBoundLatitude);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultGeographicBoundingBox castOrCopy(final GeographicBoundingBox object) {
        return (object == null) || (object instanceof DefaultGeographicBoundingBox)
                ? (DefaultGeographicBoundingBox) object : new DefaultGeographicBoundingBox(object);
    }

    /**
     * Returns the western-most coordinate of the limit of the
     * dataset extent. The value is expressed in longitude in
     * decimal degrees (positive east).
     *
     * @return The western-most longitude between -180 and +180°,
     *         or {@linkplain Double#NaN NaN} if undefined.
     */
    @Override
    @ValueRange(minimum=-180, maximum=180)
    @XmlElement(name = "westBoundLongitude", required = true)
    public synchronized double getWestBoundLongitude() {
        return westBoundLongitude;
    }

    /**
     * Sets the western-most coordinate of the limit of the
     * dataset extent. The value is expressed in longitude in
     * decimal degrees (positive east).
     *
     * @param newValue The western-most longitude between -180 and +180°,
     *        or {@linkplain Double#NaN NaN} to undefine.
     */
    public synchronized void setWestBoundLongitude(final double newValue) {
        checkWritePermission();
        westBoundLongitude = newValue;
    }

    /**
     * Returns the eastern-most coordinate of the limit of the
     * dataset extent. The value is expressed in longitude in
     * decimal degrees (positive east).
     *
     * @return The eastern-most longitude between -180 and +180°,
     *         or {@linkplain Double#NaN NaN} if undefined.
     */
    @Override
    @ValueRange(minimum=-180, maximum=180)
    @XmlElement(name = "eastBoundLongitude", required = true)
    public synchronized double getEastBoundLongitude() {
        return eastBoundLongitude;
    }

    /**
     * Sets the eastern-most coordinate of the limit of the
     * dataset extent. The value is expressed in longitude in
     * decimal degrees (positive east).
     *
     * @param newValue The eastern-most longitude between -180 and +180°,
     *        or {@linkplain Double#NaN NaN} to undefine.
     */
    public synchronized void setEastBoundLongitude(final double newValue) {
        checkWritePermission();
        eastBoundLongitude = newValue;
    }

    /**
     * Returns the southern-most coordinate of the limit of the
     * dataset extent. The value is expressed in latitude in
     * decimal degrees (positive north).
     *
     * @return The southern-most latitude between -90 and +90°,
     *         or {@linkplain Double#NaN NaN} if undefined.
     */
    @Override
    @ValueRange(minimum=-90, maximum=90)
    @XmlElement(name = "southBoundLatitude", required = true)
    public synchronized double getSouthBoundLatitude()  {
        return southBoundLatitude;
    }

    /**
     * Sets the southern-most coordinate of the limit of the
     * dataset extent. The value is expressed in latitude in
     * decimal degrees (positive north).
     *
     * @param newValue The southern-most latitude between -90 and +90°,
     *        or {@linkplain Double#NaN NaN} to undefine.
     */
    public synchronized void setSouthBoundLatitude(final double newValue) {
        checkWritePermission();
        southBoundLatitude = newValue;
    }

    /**
     * Returns the northern-most, coordinate of the limit of the
     * dataset extent. The value is expressed in latitude in
     * decimal degrees (positive north).
     *
     * @return The northern-most latitude between -90 and +90°,
     *         or {@linkplain Double#NaN NaN} if undefined.
     */
    @Override
    @ValueRange(minimum=-90, maximum=90)
    @XmlElement(name = "northBoundLatitude", required = true)
    public synchronized double getNorthBoundLatitude()   {
        return northBoundLatitude;
    }

    /**
     * Sets the northern-most, coordinate of the limit of the
     * dataset extent. The value is expressed in latitude in
     * decimal degrees (positive north).
     *
     * @param newValue The northern-most latitude between -90 and +90°,
     *        or {@linkplain Double#NaN NaN} to undefine.
     */
    public synchronized void setNorthBoundLatitude(final double newValue) {
        checkWritePermission();
        northBoundLatitude = newValue;
    }

    /**
     * Sets the bounding box to the specified values.
     * <p>
     * <strong>Caution:</strong> Arguments are expected in the same order than they appear in the
     * ISO 19115 specification. This is different than the order commonly found in Java world,
     * which is rather (<var>x</var><sub>min</sub>, <var>y</var><sub>min</sub>,
     * <var>x</var><sub>max</sub>, <var>y</var><sub>max</sub>).
     *
     * @param westBoundLongitude The minimal <var>x</var> value.
     * @param eastBoundLongitude The maximal <var>x</var> value.
     * @param southBoundLatitude The minimal <var>y</var> value.
     * @param northBoundLatitude The maximal <var>y</var> value.
     *
     * @throws IllegalArgumentException If (<var>west bound</var> &gt; <var>east bound</var>)
     *         or (<var>south bound</var> &gt; <var>north bound</var>). Note that
     *         {@linkplain Double#NaN NaN} values are allowed.
     *
     * @since 2.5
     */
    public synchronized void setBounds(final double westBoundLongitude,
                                       final double eastBoundLongitude,
                                       final double southBoundLatitude,
                                       final double northBoundLatitude)
            throws IllegalArgumentException
    {
        checkWritePermission();
        final int propertyKey;
        final double min, max;
        if (westBoundLongitude > eastBoundLongitude) {
            min = westBoundLongitude;
            max = eastBoundLongitude;
            propertyKey = Vocabulary.Keys.LONGITUDE;
            // Exception will be thrown below.
        } else if (southBoundLatitude > northBoundLatitude) {
            min = southBoundLatitude;
            max = northBoundLatitude;
            propertyKey = Vocabulary.Keys.LATITUDE;
            // Exception will be thrown below.
        } else {
            this.westBoundLongitude = westBoundLongitude;
            this.eastBoundLongitude = eastBoundLongitude;
            this.southBoundLatitude = southBoundLatitude;
            this.northBoundLatitude = northBoundLatitude;
            return;
        }
        String message = Vocabulary.format(propertyKey);
        message = Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_1, message);
        message = message + ' ' + Errors.format(Errors.Keys.ILLEGAL_RANGE_2, min, max);
        throw new IllegalArgumentException(message);
    }

    /**
     * Sets the bounding box to the specified rectangle. The rectangle is assumed in
     * {@linkplain org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84 WGS84} CRS.
     *
     * @param bounds The rectangle to use for setting the values of this box.
     *
     * @since 3.18
     */
    public void setBounds(final Rectangle2D bounds) {
        ensureNonNull("bounds", bounds);
        setBounds(bounds.getMinX(), bounds.getMaxX(), bounds.getMinY(), bounds.getMaxY());
    }

    /**
     * Sets the bounding box to the same values than the specified box.
     *
     * @param box The geographic bounding box to use for setting the values of this box.
     *
     * @since 2.5
     */
    public void setBounds(final GeographicBoundingBox box) {
        ensureNonNull("box", box);
        setInclusion(box.getInclusion());
        setBounds(box.getWestBoundLongitude(), box.getEastBoundLongitude(),
                  box.getSouthBoundLatitude(), box.getNorthBoundLatitude());
    }

    /**
     * Adds a geographic bounding box to this box. If the {@linkplain #getInclusion inclusion}
     * status is the same for this box and the box to be added, then the resulting bounding box
     * is the union of the two boxes. If the {@linkplain #getInclusion inclusion} status are
     * opposite (<cite>exclusion</cite>), then this method attempt to exclude some area of
     * specified box from this box. The resulting bounding box is smaller if the exclusion can
     * be performed without ambiguity.
     *
     * @param box The geographic bounding box to add to this box.
     *
     * @since 2.2
     */
    public synchronized void add(final GeographicBoundingBox box) {
        checkWritePermission();
        final double xmin = box.getWestBoundLongitude();
        final double xmax = box.getEastBoundLongitude();
        final double ymin = box.getSouthBoundLatitude();
        final double ymax = box.getNorthBoundLatitude();
        /*
         * Reminder: 'inclusion' is a mandatory attribute, so it should never be null for a
         * valid metadata object.  If the metadata object is invalid, it is better to get a
         * an exception than having a code doing silently some inappropriate work.
         */
        final Boolean inc1 =     getInclusion(); ensureNonNull("inclusion", inc1);
        final Boolean inc2 = box.getInclusion(); ensureNonNull("inclusion", inc2);
        if (inc1.booleanValue() == inc2.booleanValue()) {
            if (xmin < westBoundLongitude) westBoundLongitude = xmin;
            if (xmax > eastBoundLongitude) eastBoundLongitude = xmax;
            if (ymin < southBoundLatitude) southBoundLatitude = ymin;
            if (ymax > northBoundLatitude) northBoundLatitude = ymax;
        } else {
            if (ymin <= southBoundLatitude && ymax >= northBoundLatitude) {
                if (xmin > westBoundLongitude) westBoundLongitude = xmin;
                if (xmax < eastBoundLongitude) eastBoundLongitude = xmax;
            }
            if (xmin <= westBoundLongitude && xmax >= eastBoundLongitude) {
                if (ymin > southBoundLatitude) southBoundLatitude = ymin;
                if (ymax < northBoundLatitude) northBoundLatitude = ymax;
            }
        }
    }

    /**
     * Sets this bounding box to the intersection of this box with the specified one.
     * The {@linkplain #getInclusion inclusion} status must be the same for both boxes.
     *
     * @param box The geographic bounding box to intersect with this box.
     *
     * @since 2.5
     */
    public synchronized void intersect(final GeographicBoundingBox box) {
        checkWritePermission();
        final Boolean inc1 =     getInclusion(); ensureNonNull("inclusion", inc1);
        final Boolean inc2 = box.getInclusion(); ensureNonNull("inclusion", inc2);
        if (inc1.booleanValue() != inc2.booleanValue()) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_1, "box"));
        }
        final double xmin = box.getWestBoundLongitude();
        final double xmax = box.getEastBoundLongitude();
        final double ymin = box.getSouthBoundLatitude();
        final double ymax = box.getNorthBoundLatitude();
        if (xmin > westBoundLongitude) westBoundLongitude = xmin;
        if (xmax < eastBoundLongitude) eastBoundLongitude = xmax;
        if (ymin > southBoundLatitude) southBoundLatitude = ymin;
        if (ymax < northBoundLatitude) northBoundLatitude = ymax;
        if (westBoundLongitude > eastBoundLongitude) {
            westBoundLongitude = eastBoundLongitude = 0.5 * (westBoundLongitude + eastBoundLongitude);
        }
        if (southBoundLatitude > northBoundLatitude) {
            southBoundLatitude = northBoundLatitude = 0.5 * (southBoundLatitude + northBoundLatitude);
        }
    }

    /**
     * Returns {@code true} if this metadata is empty. This metadata is considered empty if
     * every bound values are {@linkplain Double#NaN NaN}. Note that this is different than
     * the <cite>Java2D</cite> or <cite>envelope</cite> definition of "emptiness", since we
     * don't test if the area is greater than zero - this method is a metadata test, not a
     * geometric test.
     *
     * @return {@code true} if this metadata does not define any bound value.
     *
     * @since 2.5
     *
     * @see org.geotoolkit.geometry.AbstractEnvelope#isEmpty()
     * @see java.awt.geom.Rectangle2D#isEmpty()
     */
    @Override
    public synchronized boolean isEmpty() {
        return Double.isNaN(eastBoundLongitude) &&
               Double.isNaN(westBoundLongitude) &&
               Double.isNaN(northBoundLatitude) &&
               Double.isNaN(southBoundLatitude);
    }

    /**
     * Compares this geographic bounding box with the specified object for equality.
     *
     * @param object The object to compare for equality.
     * @return {@code true} if the given object is equal to this box.
     */
    @Override
    public synchronized boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        // Above code really requires DefaultGeographicBoundingBox.class, not getClass().
        // This code is used only for performance raison. The super-class implementation
        // is generic enough for all other cases.
        if (object != null && object.getClass() == DefaultGeographicBoundingBox.class) { // NOSONAR
            final DefaultGeographicBoundingBox that = (DefaultGeographicBoundingBox) object;
            return Objects  .equals(this.getInclusion(),     that.getInclusion())     &&
                   Utilities.equals(this.southBoundLatitude, that.southBoundLatitude) &&
                   Utilities.equals(this.northBoundLatitude, that.northBoundLatitude) &&
                   Utilities.equals(this.eastBoundLongitude, that.eastBoundLongitude) &&
                   Utilities.equals(this.westBoundLongitude, that.westBoundLongitude);
        }
        return super.equals(object, mode);
    }

    /**
     * Returns a string representation of this extent using a default angle pattern.
     */
    @Override
    public synchronized String toString() {
        return toString(this, "DD°MM′SS.s″", null);
    }

    /**
     * Returns a string representation of the specified extent using the specified angle pattern
     * and locale. See {@link AngleFormat} for a description of angle patterns.
     *
     * @param box     The bounding box to format.
     * @param pattern The angle pattern (e.g. {@code DD°MM'SS.s"}.
     * @param locale  The locale, or {@code null} for the default one.
     * @return A string representation of the given box in the given locale.
     *
     * @since 2.2
     */
    public static String toString(final GeographicBoundingBox box,
                                  final String pattern, final Locale locale)
    {
        final AngleFormat format;
        format = (locale != null) ? new AngleFormat(pattern, locale) : new AngleFormat(pattern);
        final FieldPosition pos = new FieldPosition(0);
        final StringBuffer buffer = new StringBuffer();
        format.format(new  Latitude(box.getNorthBoundLatitude()), buffer, pos).append(", ");
        format.format(new Longitude(box.getWestBoundLongitude()), buffer, pos).append(" - ");
        format.format(new  Latitude(box.getSouthBoundLatitude()), buffer, pos).append(", ");
        format.format(new Longitude(box.getEastBoundLongitude()), buffer, pos);
        return buffer.toString();
    }
}
