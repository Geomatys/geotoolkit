/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.storage.coverage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.geotoolkit.lang.Static;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * A set of utility method to extract values, coverages or cube from a GridCoverage.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class CoverageExtractor extends Static {

    /**
     * Simple Pojo that keep rayExtraction values.
     */
    public static class Ray {
        private List<SampleDimension> sampleDimensions = new ArrayList<>();
        private Map<DirectPosition, double[]> values = new TreeMap<>(new SliceComparator());

        public List<SampleDimension> getSampleDimensions() {
            return sampleDimensions;
        }

        public Map<DirectPosition, double[]> getValues() {
            return values;
        }
    }

    /**
     * Call {@link org.geotoolkit.coverage.grid.GridCoverage#evaluate(org.opengis.geometry.DirectPosition, double[])} on all
     * {@link org.geotoolkit.coverage.grid.GridCoverage2D} slices returned by reader.
     *
     * TODO: Find a way to simulate a deferred mechanism, to allow loading of only required data.
     *
     * @param point position given to evaluate function.
     * @param source GridCoverage containing data to evaluate.
     * @return Ray bean object.
     * @throws DataStoreException
     * @throws TransformException
     */
    public static Ray rayExtraction(GeneralDirectPosition point, GridCoverage source) throws DataStoreException, TransformException {
        Ray result = new Ray();
        result.getSampleDimensions().addAll(source.getSampleDimensions());
        evaluateAllSlices(point, source, result);
        return result;
    }

    /**
     * Call {@link org.geotoolkit.coverage.grid.GridCoverage#evaluate(org.opengis.geometry.DirectPosition, double[])} on all
     * {@link org.geotoolkit.coverage.grid.GridCoverage2D slices}.
     *
     * @param directPos position given to evaluate function.
     * @param coverage GridCoverage2D or GridCoverageStack coverage.
     * @param result recursively filled with extracted values
     * @throws DataStoreException
     * @throws TransformException
     */
    private static void evaluateAllSlices(GeneralDirectPosition directPos, GridCoverage coverage, Ray result)
            throws DataStoreException, TransformException {

        if (coverage instanceof GridCoverageStack) {
            final GridCoverageStack coverageStack = (GridCoverageStack) coverage;
            int length = coverageStack.getStackSize();
            for (int i = 0; i < length; i++) {
                evaluateAllSlices(directPos, (GridCoverage) coverageStack.coverageAtIndex(i), result);
            }
        } else if (coverage instanceof GridCoverage) {
            final GridCoverage coverage2D = coverage;
            double[] values;

            //place directPos to a pixel center
            GridGeometry gg2D = coverage2D.getGridGeometry();
            MathTransform gridToCRS = gg2D.getGridToCRS(PixelInCell.CELL_CENTER);
            gridToCRS.inverse().transform(directPos, directPos);

            for (int i = 0; i < directPos.getDimension(); i++) {
                directPos.setOrdinate(i, Math.round(directPos.getOrdinate(i)));
            }

            gridToCRS.transform(directPos, directPos);

            values = coverage2D.evaluator().apply(directPos);

            final CoordinateReferenceSystem crs = gg2D.getCoordinateReferenceSystem();
            int dimension = crs.getCoordinateSystem().getDimension();

            double[] point = new double[dimension];
            final MathTransform cov2DGridToCRS = gg2D.getGridToCRS(PixelInCell.CELL_CENTER);
            cov2DGridToCRS.transform(point, 0, point, 0, 1);

            final GeneralDirectPosition position = new GeneralDirectPosition(point);
            position.setCoordinateReferenceSystem(crs);
            position.setOrdinate(0, directPos.getOrdinate(0));
            position.setOrdinate(1, directPos.getOrdinate(1));
            result.getValues().put(position, values);

        } else {
            throw new DataStoreException("Unexpected coverage type " + coverage.getClass().getName());
        }
    }

    /**
     * Comparator of DirectPosition to sort slice by dimensions.
     */
    private static class SliceComparator implements Comparator<DirectPosition> {
        @Override
        public int compare(DirectPosition o1, DirectPosition o2) {
            int dimension = o1.getDimension();

            if (dimension > 2) {
                int currDim = 2;
                int[] dimCompar = new int[dimension-2];
                while (currDim <= dimension-1) {
                    dimCompar[currDim-2] = Double.compare(o1.getOrdinate(currDim), o2.getOrdinate(currDim));
                    currDim++;
                }

                for (int i = 0; i < dimCompar.length; i++) {
                    if (dimCompar[i] != 0) {
                        return dimCompar[i];
                    }
                }
            }
            return 0;
        }
    }

}
