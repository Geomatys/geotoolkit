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
import org.apache.sis.coverage.grid.GridGeometry;
import org.opengis.coverage.CannotEvaluateException;

/**
 * @author Johann Sorel (Geomatys)
 */
public class ReducedGridCoverage extends GridCoverage {

    private final GridCoverage parent;
    private final int[] dimensions;
    private final GridExtent baseExtent;

    public ReducedGridCoverage(GridCoverage parent, int ... dimensions) {
        this(parent, parent.getGridGeometry(), dimensions);
    }

    public ReducedGridCoverage(GridCoverage parent, GridGeometry base, int... dimensions) {
        super(base.selectDimensions(dimensions), parent.getSampleDimensions());
        this.parent = parent;
        this.dimensions = dimensions;
        this.baseExtent = base.getExtent();
    }

    @Override
    public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
        GridExtent ext;
        if (sliceExtent == null) {
            ext = this.baseExtent;
        } else {
            final long[] low = new long[this.baseExtent.getDimension()];
            final long[] high = new long[low.length];
            int k = 0;
            for (int i=0;i<low.length;i++) {
                if (k < dimensions.length && dimensions[k] == i) {
                    low[i] = sliceExtent.getLow(k);
                    high[i] = sliceExtent.getHigh(k);
                    k++;
                } else {
                    low[i] = this.baseExtent.getLow(i);
                    high[i] = this.baseExtent.getHigh(i);
                }
            }
            ext = new GridExtent(null, low, high, true);
        }

        return parent.render(ext);
    }

}
