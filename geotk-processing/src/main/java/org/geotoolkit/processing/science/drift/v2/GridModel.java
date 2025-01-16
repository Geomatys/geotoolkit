/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.NoninvertibleTransformException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class GridModel {

    final GridGeometry model;
    final MathTransform2D crs2grid;

    final int dimension;
    final int width;
    final int height;

    GridModel(final GridGeometry model) throws NoninvertibleTransformException {
        ArgumentChecks.ensureNonNull("Grid model", model);
        this.model = model;

        dimension = model.getDimension();
        if (dimension != 2) {
            throw new UnsupportedOperationException("Only 2D grid are allowed for now, but provided " + dimension + "D.");
        }

        final long tmpWidth = model.getExtent().getSize(0);
        final long tmpHeight = model.getExtent().getSize(1);

        if (tmpWidth * tmpHeight > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("Specified grid is too large. Only data < 2 giga-byte supported for now.");
        }

        width = (int) tmpWidth;
        height = (int) tmpHeight;
        final MathTransform gridToCRS = model.getGridToCRS(PixelInCell.CELL_CENTER);
        if (!(gridToCRS instanceof MathTransform2D)) {
            throw new IllegalStateException("For a 2D grid, we expect a 2D tranform, but got: "+ gridToCRS.getClass());
        }

        crs2grid = ((MathTransform2D)gridToCRS).inverse();
    }
}
