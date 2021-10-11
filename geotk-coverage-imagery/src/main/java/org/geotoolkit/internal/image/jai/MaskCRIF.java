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
package org.geotoolkit.internal.image.jai;

import javax.media.jai.JAI;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.RenderingHints;

import org.geotoolkit.image.jai.Mask;


/**
 * The factory for the {@link Mask} operation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class MaskCRIF extends CRIFImpl {
    /**
     * Constructs a default factory.
     */
    public MaskCRIF() {
    }

    /**
     * Creates a {@link RenderedImage} representing the results of an imaging
     * operation for a given {@link ParameterBlock} and {@link RenderingHints}.
     *
     * @param param The parameter to be given to the image operation.
     * @param hints An optional set of hints, or {@code null}.
     */
    @Override
    public RenderedImage create(final ParameterBlock param, final RenderingHints hints) {
        final RenderedImage image = (RenderedImage) param.getSource(0);
        final RenderedImage  mask = (RenderedImage) param.getSource(1);
        final ImageLayout  layout = (ImageLayout) hints.get(JAI.KEY_IMAGE_LAYOUT);
        final double[]  newValues = (double[]) param.getObjectParameter(0);
        return new Mask(image, mask, layout, hints, newValues);
    }
}
