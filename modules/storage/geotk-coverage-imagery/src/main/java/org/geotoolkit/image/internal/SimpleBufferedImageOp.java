/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.internal;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.Serializable;
import javax.media.jai.PlanarImage;


/**
 * An implementation of {@link BufferedImageOp} where the result is expected to be an image of
 * the same size than the input. This class is not public because we rely on JAI when we can.
 * It is used only when an operation can not be easily described in a single JAI parameter block.
 *
 * {@section Serialization}
 * This class must be serializable because the operation may be sent over the network
 * for execution on a remote server.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
public abstract class SimpleBufferedImageOp implements BufferedImageOp, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 6973234963094057672L;

    /**
     * Creates a new operation.
     */
    protected SimpleBufferedImageOp() {
    }

    /**
     * Applies the operation on the given image.
     *
     * @param  src The image to be filtered.
     * @return The result of the filtering operation.
     */
    public abstract RenderedImage filter(final BufferedImage src);

    /**
     * Applies the operation on the given image. The default implementation invokes
     * {@link #filter(BufferedImage)} and convert the result to a {@link BufferedImage}.
     *
     * @param  src The image to be filtered.
     * @param  dest Where to store the result, or {@code null}.
     * @return The result of the filtering operation.
     */
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dest) {
        final RenderedImage result = filter(src);
        if (dest != result) {
            if (dest == null) {
                if (result instanceof BufferedImage) {
                    return (BufferedImage) result;
                }
                if (result instanceof PlanarImage) {
                    return ((PlanarImage) result).getAsBufferedImage();
                }
                dest = createCompatibleDestImage(src, null);
            } else {
                final Rectangle2D bounds = getBounds2D(src);
                if (dest.getWidth() != bounds.getWidth() || dest.getHeight() != bounds.getHeight()) {
                    throw new IllegalArgumentException();
                }
            }
            ImageUtilities.fill(dest, 0);
            final Graphics2D gr = dest.createGraphics();
            gr.drawRenderedImage(result, new AffineTransform());
            gr.dispose();
        }
        return dest;
    }

    /**
     * Creates destination image with the correct size and number of bands.
     */
    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage src, ColorModel destCM) {
        if (destCM == null) {
            destCM = src.getColorModel();
        }
        final WritableRaster raster = destCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight());
        return new BufferedImage(destCM, raster, false, null);
    }

    /**
     * Returns the bounding box of the filtered destination image.
     * The default implementation returns the same bounds than the source image.
     */
    @Override
    public Rectangle2D getBounds2D(final BufferedImage src) {
        return ImageUtilities.getBounds(src);
    }

    /**
     * Returns the location of the corresponding destination point given a point in the source image.
     * The default implementation returns the same ordinates than the source point.
     */
    @Override
    public Point2D getPoint2D(final Point2D srcPt, final Point2D dstPt) {
        if (dstPt == null) {
            return (Point2D) srcPt.clone();
        }
        dstPt.setLocation(srcPt);
        return dstPt;
    }

    /**
     * Returns the rendering hints for this operation.
     * The default implementation returns an empty map.
     */
    @Override
    public RenderingHints getRenderingHints() {
        return new RenderingHints(null);
    }
}
