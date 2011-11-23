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
package org.geotoolkit.geometry;

import java.io.Serializable;
import net.jcip.annotations.Immutable;

import org.opengis.geometry.Envelope;
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
     *
     * @param envelope The envelope to copy.
     */
    public ImmutableEnvelope(final Envelope envelope) {
        super(envelope);
    }

    /**
     * Constructs a new envelope with the same data than the specified
     * geographic bounding box. The coordinate reference system is set
     * to {@linkplain DefaultGeographicCRS#WGS84 WGS84}.
     *
     * @param box The bounding box to copy.
     *
     * @since 3.20
     */
    public ImmutableEnvelope(final GeographicBoundingBox box) {
        super(box);
    }

    /**
     * Builds a two-dimensional envelope with the specified bounds.
     *
     * @param crs  The coordinate reference system, or {@code null} if none.
     * @param xmin The minimal value for the first ordinate.
     * @param xmax The maximal value for the first ordinate.
     * @param ymin The minimal value for the second ordinate.
     * @param ymax The maximal value for the second ordinate.
     */
    public ImmutableEnvelope(final CoordinateReferenceSystem crs, final double xmin,
                             final double xmax, final double ymin, final double ymax)
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
     *
     * @since 3.20
     */
    public ImmutableEnvelope(final CoordinateReferenceSystem crs, final String wkt)
            throws NumberFormatException, IllegalArgumentException
    {
        super(wkt);
        this.crs = crs;
        checkCoordinateReferenceSystemDimension(crs, getDimension());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return super.getCoordinateReferenceSystem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDimension() {
        return super.getDimension();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMinimum(final int dimension) throws IndexOutOfBoundsException {
        return super.getMinimum(dimension);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaximum(final int dimension) throws IndexOutOfBoundsException {
        return super.getMaximum(dimension);
    }
}
