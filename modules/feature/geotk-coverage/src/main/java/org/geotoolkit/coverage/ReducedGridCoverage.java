/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage;

import java.awt.image.RenderedImage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.internal.coverage.ConvertedGridCoverage;
import org.opengis.coverage.CannotEvaluateException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReducedGridCoverage extends GridCoverage {

    private final GridCoverage parent;
    private final int[] dimensions;

    public ReducedGridCoverage(GridCoverage parent, int ... dimensions) {
        super(parent.getGridGeometry().reduce(dimensions), parent.getSampleDimensions());
        this.parent = parent;
        this.dimensions = dimensions;
    }

    @Override
    public GridCoverage forConvertedValues(boolean converted) {
        if (converted) return ConvertedGridCoverage.createFromPacked(this);
        return this;
    }

    @Override
    public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
        GridExtent ext;
        GridExtent extent = parent.getGridGeometry().getExtent();
        if (sliceExtent == null) {
            ext = extent;
        } else {
            final long[] low = new long[extent.getDimension()];
            final long[] high = new long[low.length];
            int k = 0;
            for (int i=0;i<low.length;i++) {
                if (dimensions[k] == i) {
                    low[i] = sliceExtent.getLow(k);
                    high[i] = sliceExtent.getHigh(k);
                    k++;
                } else {
                    low[i] = extent.getLow(i);
                    high[i] = extent.getHigh(i);
                }
            }
            ext = new GridExtent(null, low, high, true);
        }

        return parent.render(ext);
    }

}
