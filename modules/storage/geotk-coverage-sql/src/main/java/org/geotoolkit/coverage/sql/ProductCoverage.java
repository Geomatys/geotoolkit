/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.awt.image.RenderedImage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;


/**
 * Data of a product.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
final class ProductCoverage extends GridCoverage {
    /**
     * For keeping trace of temporary hacks.
     */
    static final boolean HACK = true;

    /**
     * The components of this grid coverage.
     */
    private final List<GridCoverageEntry> entries;

    /**
     * Creates a coverage for the given product.
     */
    ProductCoverage(final GridGeometry geometry, final List<GridCoverageEntry> entries) {
        // TODO: arbitrarily take the first entry as a template for sample dimensions.
        super(geometry, entries.get(0).getSampleDimensions());
        this.entries = entries;
    }

    @Override
    public RenderedImage render(GridExtent sliceExtent) {
        try {
            return entries.get(0).coverage(null, null).render(sliceExtent);
        } catch (DataStoreException e) {
            throw new BackingStoreException(e);
        }
    }
}
