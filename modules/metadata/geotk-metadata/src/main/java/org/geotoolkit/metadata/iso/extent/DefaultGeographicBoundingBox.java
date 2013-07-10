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
import java.text.FieldPosition;
import java.awt.geom.Rectangle2D;
import javax.xml.bind.annotation.XmlRootElement;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.measure.AngleFormat;


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
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@XmlRootElement(name = "EX_GeographicBoundingBox")
public class DefaultGeographicBoundingBox extends org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3278089380004172514L;

    /**
     * Constructs an initially {@linkplain #isEmpty() empty} geographic bounding box.
     * All longitude and latitude values are initialized to {@link Double#NaN}.
     */
    public DefaultGeographicBoundingBox() {
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
        super(box);
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
        super.setInclusion(Boolean.TRUE);
        super.setBounds(envelope);
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
        super.setInclusion(Boolean.TRUE);
        super.setBounds(new Envelope2D(crs, bounds));
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
        super(westBoundLongitude, eastBoundLongitude,
              southBoundLatitude, northBoundLatitude);
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
        setBounds(bounds.getMinX(), bounds.getMaxX(), bounds.getMinY(), bounds.getMaxY());
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
