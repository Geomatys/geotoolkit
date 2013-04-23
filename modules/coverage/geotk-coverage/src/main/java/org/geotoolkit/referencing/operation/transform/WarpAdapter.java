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
package org.geotoolkit.referencing.operation.transform;

import java.awt.geom.Point2D;
import java.awt.image.RasterFormatException;
import javax.media.jai.Warp;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.operation.MathTransforms;


/**
 * Wraps an arbitrary {@link MathTransform2D} into an image warp operation. This warp
 * operation is used by {@link org.geotoolkit.coverage.processing.operation.Resample}
 * when no standard warp operation has been found applicable.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Immutable
final class WarpAdapter extends Warp {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8679060848877065181L;

    /**
     * The transform to apply before the transform given at construction time.
     */
    private static final AffineTransform2D PRE_TRANSFORM = new AffineTransform2D(1, 0, 0, 1, 0.5, 0.5);

    /**
     * The transform to apply after the transform given at construction time.
     */
    private static final AffineTransform2D POST_TRANSFORM = new AffineTransform2D(1, 0, 0, 1, -0.5, -0.5);

    /**
     * The coverage name. Used for formatting error message.
     */
    private final CharSequence name;

    /**
     * The <strong>inverse</strong> of the transform to apply for projecting an image.
     * This transform maps destination pixels to source pixels.
     */
    private final MathTransform2D inverse;

    /**
     * Constructs a new {@code WarpAdapter} using the given transform.
     *
     * @param name    The coverage name. Used for formatting error message.
     * @param inverse The <strong>inverse</strong> of the transformation to apply for projecting
     *                an image. This inverse transform maps destination pixels to source pixels.
     */
    public WarpAdapter(final CharSequence name, final MathTransform2D inverse) {
        this.name    = name;
        this.inverse = MathTransforms.concatenate(PRE_TRANSFORM, inverse, POST_TRANSFORM);
    }

    /**
     * Returns the transform from image destination pixels to source pixels.
     */
    public MathTransform2D getTransform() {
        return inverse;
    }

    /**
     * Computes the source pixel positions for a given rectangular
     * destination region, subsampled with an integral period.
     */
    @Override
    public float[] warpSparseRect(final int xmin,    final int ymin,
                                  final int width,   final int height,
                                  final int periodX, final int periodY, float[] destRect)
    {
        if (periodX < 1) throw new IllegalArgumentException(String.valueOf(periodX));
        if (periodY < 1) throw new IllegalArgumentException(String.valueOf(periodY));

        final int xmax  = xmin + width;
        final int ymax  = ymin + height;
        final int count = ((width  + (periodX - 1)) / periodX) *
                          ((height + (periodY - 1)) / periodY);
        if (destRect == null) {
            destRect = new float[2*count];
        }
        int index = 0;
        for (int y=ymin; y<ymax; y+=periodY) {
            for (int x=xmin; x<xmax; x+=periodX) {
                destRect[index++] = x;
                destRect[index++] = y;
            }
        }
        try {
            inverse.transform(destRect, 0, destRect, 0, count);
        } catch (TransformException exception) {
            // At least one transformation failed. In Geotk MapProjection
            // implementation, unprojected coordinates are set to (NaN,NaN).
            RasterFormatException e = new RasterFormatException(
                    Errors.format(Errors.Keys.CANT_REPROJECT_COVERAGE_1, name));
            e.initCause(exception);
            throw e;
        }
        return destRect;
    }

    /**
     * Computes the source point corresponding to the supplied point.
     *
     * @param destPt The position in destination image coordinates
     *               to map to source image coordinates.
     */
    @Override
    public Point2D mapDestPoint(final Point2D destPt) {
        try {
            return inverse.transform(destPt, null);
        } catch (TransformException exception) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "destPt", destPt), exception);
        }
    }

    /**
     * Computes the destination point corresponding to the supplied point.
     *
     * @param sourcePt The position in source image coordinates
     *                 to map to destination image coordinates.
     */
    @Override
    public Point2D mapSourcePoint(final Point2D sourcePt) {
        try {
            return inverse.inverse().transform(sourcePt, null);
        } catch (TransformException exception) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "sourcePt", sourcePt), exception);
        }
    }
}
