/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.List;
import java.util.Vector;
import javax.media.jai.CRIFImpl;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.RenderingHints;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.image.jai.Combine;

import static org.geotoolkit.image.jai.Combine.Transform;


/**
 * The image factory for the {@link Combine} operation.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public final class CombineCRIF extends CRIFImpl {
    /**
     * Constructs a default factory.
     */
    public CombineCRIF() {
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
        final Vector<RenderedImage> sources = cast(param.getSources());
        final double[][] matrix = (double[][]) param.getObjectParameter(0);
        final Transform transform = (Transform) param.getObjectParameter(1);
        return transform == null && isDyadic(sources, matrix) ?
               new Combine.Dyadic(sources, matrix, hints)   :
               new Combine       (sources, matrix, transform, hints);
    }

    /**
     * Returns {@code true} if the combine operation could be done through
     * the optimized {@code Combine.Dyadic} class.
     */
    private static boolean isDyadic(final List<?> sources, final double[][] matrix) {
        if (sources.size() != 2) {
            return false;
        }
        final RenderedImage src0 = (RenderedImage) sources.get(0);
        final RenderedImage src1 = (RenderedImage) sources.get(1);
        final int numBands0 = src0.getSampleModel().getNumBands();
        final int numBands1 = src1.getSampleModel().getNumBands();
        final int numBands  = matrix.length;
        if (numBands!=numBands0 || numBands!=numBands1) {
            return false;
        }
        for (int i=0; i<numBands; i++) {
            final double[] row = matrix[i];
            for (int j=numBands0+numBands1; --j>=0;) {
                if (j!=i && j!=i+numBands0 && row[j]!=0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Casts the given vector of object to a vector of rendered image. This method is not public
     * because while the vector is correct right after this method, there is nothing preventing
     * the callers to add invalid objects in the original vector after the call.
     */
    @SuppressWarnings("unchecked")
    static Vector<RenderedImage> cast(final Vector<?> sources) {
        for (final Object element : sources) {
            if (!(element instanceof RenderedImage)) {
                throw new ClassCastException(Errors.format(Errors.Keys.IllegalClass_2,
                        element.getClass(), RenderedImage.class));
            }
        }
        return (Vector<RenderedImage>) sources;
    }
}
