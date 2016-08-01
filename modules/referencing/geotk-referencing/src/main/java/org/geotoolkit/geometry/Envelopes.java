/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.lang.Static;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.apache.sis.geometry.Shapes2D;


/**
 * Utility methods for envelopes. This utility class is made up of static functions working
 * with arbitrary implementations of GeoAPI interfaces.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @author Andrea Aime (TOPP)
 * @author Johann Sorel (Geomatys)
 * @see CRS
 * @module
 */
public final class Envelopes extends Static {
    static {
        org.geotoolkit.internal.io.JNDI.install();
    }

    /**
     * Do not allow instantiation of this class.
     */
    private Envelopes() {
    }

    /**
     * Returns the domain of validity for the specified coordinate reference system,
     * or {@code null} if unknown. The returned envelope is expressed in terms of the
     * specified CRS.
     * <p>
     * This method performs the work documented in the
     * {@link CRS#getEnvelope(CoordinateReferenceSystem)} method.
     * It is defined in this class for convenience.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The envelope in terms of the specified CRS, or {@code null} if none.
     *
     * @see CRS#getEnvelope(CoordinateReferenceSystem)
     * @see org.apache.sis.geometry.GeneralEnvelope#reduceToDomain(boolean)
     */
    public static Envelope getDomainOfValidity(final CoordinateReferenceSystem crs) {
        return CRS.getEnvelope(crs);
    }

    /**
     * Transforms a rectangular envelope using the given {@linkplain MathTransform math transform}.
     * The transformation is only approximative: the returned envelope may be bigger than
     * necessary, or smaller than required if the bounding box contains a pole.
     * <p>
     * Invoking this method is equivalent to invoking the following:
     *
     * {@preformat java
     *   transform(transform, new GeneralEnvelope(envelope)).toRectangle2D()
     * }
     *
     * Note that this method can not handle the case where the rectangle contains the North or South
     * pole, or when it cross the &plusmn;180&deg; longitude, because {@linkplain MathTransform
     * math transforms} do not carry sufficient informations. For a more robust rectangle
     * transformation, use {@link #transform(CoordinateOperation, Rectangle2D, Rectangle2D)}
     * instead.
     *
     * @param  transform   The transform to use. Source and target dimension must be 2.
     * @param  envelope    The rectangle to transform (may be {@code null}).
     * @param  destination The destination rectangle (may be {@code envelope}).
     *         If {@code null}, a new rectangle will be created and returned.
     * @return {@code destination}, or a new rectangle if {@code destination} was non-null
     *         and {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(CoordinateOperation, Rectangle2D, Rectangle2D)
     * @see org.geotoolkit.referencing.operation.matrix.XAffineTransform#transform(AffineTransform, Rectangle2D, Rectangle2D)
     *
     * @deprecated Moved to Apache SIS in {@link Shapes2D} class.
     */
    @Deprecated
    public static Rectangle2D transform(final MathTransform2D transform,
                                        final Rectangle2D     envelope,
                                              Rectangle2D     destination)
            throws TransformException
    {
        return Shapes2D.transform(transform, envelope, destination);
    }

    /**
     * Transforms a rectangular envelope using the given {@linkplain CoordinateOperation coordinate
     * operation}. The transformation is only approximative: the returned envelope may be bigger
     * than the smallest possible bounding box, but should not be smaller in most cases.
     * <p>
     * Invoking this method is equivalent to invoking the following:
     *
     * {@preformat java
     *     transform(operation, new GeneralEnvelope(envelope)).toRectangle2D()
     * }
     *
     * This method can handle the case where the rectangle contains the North or South pole,
     * or when it cross the &plusmn;180&deg; longitude.
     *
     * @param  operation The operation to use. Source and target dimension must be 2.
     * @param  envelope The rectangle to transform (may be {@code null}).
     * @param  destination The destination rectangle (may be {@code envelope}).
     *         If {@code null}, a new rectangle will be created and returned.
     * @return {@code destination}, or a new rectangle if {@code destination} was non-null
     *         and {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(MathTransform2D, Rectangle2D, Rectangle2D)
     * @see org.geotoolkit.referencing.operation.matrix.XAffineTransform#transform(AffineTransform, Rectangle2D, Rectangle2D)
     *
     * @deprecated Moved to Apache SIS in {@link Shapes2D} class.
     */
    @Deprecated
    public static Rectangle2D transform(final CoordinateOperation operation,
                                        final Rectangle2D         envelope,
                                              Rectangle2D         destination)
            throws TransformException
    {
        return Shapes2D.transform(operation, envelope, destination);
    }

    /**
     * Returns {@code true} if {@link Envelope} contain at least one
     * {@link Double#NaN} value, else {@code false}.
     *
     * @param envelope the envelope which will be verify.
     * @return {@code true} if {@link Envelope} contain at least one {@link Double#NaN} value, else {@code false}.
     * @see #containNAN(org.opengis.geometry.Envelope, int, int)
     */
    public static boolean containNAN(final Envelope envelope) {
        return containNAN(envelope, 0, envelope.getDimension() - 1);
    }

    /**
     * Returns {@code true} if {@link Envelope} contain at least one
     * {@link Double#NaN} value into its horizontal geographic part, else {@code false}.
     *
     * @param envelope the envelope which will be verify.
     * @return {@code true} if {@link Envelope} contain at least one {@link Double#NaN} value, else {@code false}.
     * @see CRSUtilities#firstHorizontalAxis(org.opengis.referencing.crs.CoordinateReferenceSystem)
     * @see #containNAN(org.opengis.geometry.Envelope, int, int)
     */
    public static boolean containNANInto2DGeographicPart(final Envelope envelope) {
        ArgumentChecks.ensureNonNull("Envelopes.containNANInto2DGeographicPart()", envelope);
        final int minOrdiGeo = CRSUtilities.firstHorizontalAxis(envelope.getCoordinateReferenceSystem());
        return containNAN(envelope, minOrdiGeo, minOrdiGeo + 1);
    }

    /**
     * Returns {@code true} if {@link Envelope} contain at least one
     * {@link Double#NaN} value on each inclusive dimension stipulate by
     * firstIndex and lastIndex, else {@code false}.
     *
     * @param envelope the envelope which will be verify.
     * @param firstIndex first inclusive dimension index.
     * @param lastIndex last <strong>INCLUSIVE</strong> dimension.
     * @return {@code true} if {@link Envelope} contain at least one {@link Double#NaN} value, else {@code false}.
     */
    public static boolean containNAN(final Envelope envelope, final int firstIndex, final int lastIndex) {
        ArgumentChecks.ensureNonNull("Envelopes.containNAN()", envelope);
        ArgumentChecks.ensurePositive("firstIndex", firstIndex);
        ArgumentChecks.ensurePositive("lastIndex", lastIndex);
        if (lastIndex >= envelope.getDimension())
            throw new IllegalArgumentException("LastIndex must be strictly lower than "
                    + "envelope dimension number. Expected maximum valid index = "+(envelope.getDimension() - 1)+". Found : "+lastIndex);
        ArgumentChecks.ensureValidIndex(lastIndex + 1, firstIndex);
        for (int d = firstIndex; d <= lastIndex; d++) {
            if (Double.isNaN(envelope.getMinimum(d))
             || Double.isNaN(envelope.getMaximum(d))) return true;
        }
        return false;
    }
}
