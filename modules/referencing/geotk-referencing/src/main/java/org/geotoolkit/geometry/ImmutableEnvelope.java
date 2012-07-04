/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry;

import java.io.Serializable;
import net.jcip.annotations.Immutable;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.metadata.extent.GeographicBoundingBox;

import static org.geotoolkit.geometry.AbstractDirectPosition.checkCoordinateReferenceSystemDimension;


/**
 * Immutable representation of an {@linkplain Envelope envelope}. This class is final in order
 * to ensure that the immutability contract can not be broken (assuming not using <cite>Java
 * Native Interface</cite> or reflections).
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
@Immutable
public final class ImmutableEnvelope extends ArrayEnvelope implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5593936512712449234L;

    /**
     * Creates an immutable envelope with the values of the given envelope.
     * This envelope can be used when the envelope is known to not be an instance of
     * {@code ImmutableEnvelope}. In case of doubt, consider using {@link #castOrCopy(Envelope)}
     * instead.
     *
     * @param envelope The envelope to copy.
     *
     * @see #castOrCopy(Envelope)
     */
    public ImmutableEnvelope(final Envelope envelope) {
        super(envelope);
    }

    /**
     * Constructs a new envelope with the same data than the specified
     * geographic bounding box. The coordinate reference system is set
     * to {@linkplain org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84 WGS84}.
     *
     * @param box The bounding box to copy.
     *
     * @since 3.20
     */
    public ImmutableEnvelope(final GeographicBoundingBox box) {
        super(box);
    }

    /**
     * Creates an immutable envelope with the ordinate values of the given envelope but
     * a different CRS. This method does <strong>not</strong> reproject the given envelope.
     * It just assign the given CRS to this envelope without any check, except for the CRS
     * dimension.
     * <p>
     * The main purpose of this method is to assign a non-null CRS when the envelope to
     * copy has a null CRS.
     *
     * @param crs      The CRS to assign to this envelope, or {@code null}.
     * @param envelope The envelope from which to copy ordinate values.
     * @throws MismatchedDimensionException If the dimension of the given CRS is not equals
     *         to the dimension of the given envelope.
     *
     * @since 3.20
     */
    public ImmutableEnvelope(final CoordinateReferenceSystem crs, final Envelope envelope)
            throws MismatchedDimensionException
    {
        super(envelope);
        this.crs = crs;
        checkCoordinateReferenceSystemDimension(crs, getDimension());
    }

    /**
     * Builds a two-dimensional envelope with the specified bounds.
     *
     * @param crs  The coordinate reference system, or {@code null} if none.
     * @param xmin The lower value for the first ordinate.
     * @param xmax The upper value for the first ordinate.
     * @param ymin The lower value for the second ordinate.
     * @param ymax The upper value for the second ordinate.
     * @throws MismatchedDimensionException If the dimension of the given CRS is not equals to 2.
     */
    public ImmutableEnvelope(final CoordinateReferenceSystem crs, final double xmin,
                             final double xmax, final double ymin, final double ymax)
            throws MismatchedDimensionException
    {
        super(xmin, xmax, ymin, ymax);
        this.crs = crs;
        checkCoordinateReferenceSystemDimension(crs, 2);
    }

    /**
     * Constructs a new envelope initialized to the values parsed from the given string in
     * <cite>Well Known Text</cite> (WKT) format. The given string is typically a {@code BOX}
     * element like below:
     *
     * {@preformat wkt
     *     BOX(-180 -90, 180 90)
     * }
     *
     * However this constructor is lenient to other geometry types like {@code POLYGON}.
     * See the javadoc of the {@link GeneralEnvelope#GeneralEnvelope(String) GeneralEnvelope}
     * constructor for more information.
     *
     * @param crs  The coordinate reference system, or {@code null} if none.
     * @param  wkt The {@code BOX}, {@code POLYGON} or other kind of element to parse.
     * @throws NumberFormatException If a number can not be parsed.
     * @throws IllegalArgumentException If the parenthesis are not balanced.
     * @throws MismatchedDimensionException If the dimension of the given CRS is not equals
     *         to the dimension of the parsed envelope.
     *
     * @since 3.20
     */
    public ImmutableEnvelope(final CoordinateReferenceSystem crs, final String wkt)
            throws NumberFormatException, IllegalArgumentException, MismatchedDimensionException
    {
        super(wkt);
        this.crs = crs;
        checkCoordinateReferenceSystemDimension(crs, getDimension());
    }

    /**
     * Returns the given envelope as an {@code ImmutableEnvelope} instance. If the given envelope
     * is already an instance of {@code ImmutableEnvelope}, then it is returned unchanged.
     * Otherwise the coordinate values and the CRS of the given envelope are copied in a
     * new envelope.
     *
     * @param  envelope The envelope to cast, or {@code null}.
     * @return The values of the given envelope as an {@code ImmutableEnvelope} instance.
     *
     * @see GeneralEnvelope#castOrCopy(Envelope)
     *
     * @since 3.20
     */
    public static ImmutableEnvelope castOrCopy(final Envelope envelope) {
        if (envelope == null || envelope instanceof ImmutableEnvelope) {
            return (ImmutableEnvelope) envelope;
        }
        return new ImmutableEnvelope(envelope);
    }
}
