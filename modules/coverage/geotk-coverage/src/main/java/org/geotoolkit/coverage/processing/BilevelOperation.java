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
package org.geotoolkit.coverage.processing;

import java.util.Arrays;
import javax.media.jai.operator.BinarizeDescriptor;

import org.opengis.coverage.processing.OperationNotFoundException;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;


/**
 * Wraps any JAI operation producing a bilevel image. An example of such operation is
 * {@link BinarizeDescriptor Binarize}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class BilevelOperation extends OperationJAI {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8975871552152978976L;

    /**
     * The sample dimension for the resulting image.
     */
    private static final GridSampleDimension SAMPLE_DIMENSION =
            new GridSampleDimension("Bilevel", new Category[] {
                Category.FALSE,
                Category.TRUE
            }, null);

    /**
     * Constructs a bilevel operation with an OGC's name identical to the JAI name.
     *
     * @param name The JAI operation name.
     * @throws OperationNotFoundException if no JAI descriptor was found for the given name.
     */
    public BilevelOperation(final String name) throws OperationNotFoundException {
        super(name);
    }

    /**
     * Derives the {@link GridSampleDimension}s for the destination image.
     *
     * @param  bandLists Sample dimensions for each band in each source coverages.
     * @param  parameters The user-supplied parameters.
     * @return The sample dimensions for each band in the destination image.
     */
    @Override
    protected GridSampleDimension[] deriveSampleDimension(
            final GridSampleDimension[][] bandLists, final Parameters parameters)
    {
        final GridSampleDimension[] bands = new GridSampleDimension[bandLists[0].length];
        Arrays.fill(bands, SAMPLE_DIMENSION);
        return bands;
    }
}
