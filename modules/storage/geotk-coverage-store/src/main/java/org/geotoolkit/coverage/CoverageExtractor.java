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

package org.geotoolkit.coverage;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.lang.Static;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.*;

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
        private List<GridSampleDimension> sampleDimensions = new ArrayList<GridSampleDimension>();
        private Map<DirectPosition, double[]> values = new TreeMap<>(new SliceComparator());

        public List<GridSampleDimension> getSampleDimensions() {
            return sampleDimensions;
        }

        public Map<DirectPosition, double[]> getValues() {
            return values;
        }
    }

    /**
     * Call {@link org.opengis.coverage.grid.GridCoverage#evaluate(org.opengis.geometry.DirectPosition, double[])} on all
     * {@link org.geotoolkit.coverage.grid.GridCoverage2D} slices returned by reader.
     *
     * @param point position given to evaluate function.
     * @param reader GridCoverageReader not disposed or released
     * @param imageIndex index given to read.
     * @param param GridCoverageReadParam. Deferred parameter will be forced at true.
     * @return Ray bean object.
     * @throws CoverageStoreException
     * @throws TransformException
     */
    public static Ray rayExtraction(GeneralDirectPosition point, GridCoverageReader reader, int imageIndex,
                                                              GridCoverageReadParam param) throws CoverageStoreException, TransformException {
        param.setDeferred(true); //force deferred
        final GridCoverage coverage = reader.read(imageIndex, param);
        Ray result = new Ray();
        result.getSampleDimensions().addAll(reader.getSampleDimensions(imageIndex));
        evaluateAllSlices(point, coverage, result);
        return result;
    }

    /**
     * Call {@link org.opengis.coverage.grid.GridCoverage#evaluate(org.opengis.geometry.DirectPosition, double[])} on all
     * {@link org.geotoolkit.coverage.grid.GridCoverage2D slices}.
     *
     * @param directPos position given to evaluate function.
     * @param coverage GridCoverage2D or GridCoverageStack coverage.
     * @param result recursively filled with extracted values
     * @throws CoverageStoreException
     * @throws TransformException
     */
    private static void evaluateAllSlices(GeneralDirectPosition directPos, GridCoverage coverage, Ray result)
            throws CoverageStoreException, TransformException {

        if (coverage instanceof GridCoverage2D) {
            final GridCoverage2D coverage2D = (GridCoverage2D) coverage;
            double[] values = new double[coverage2D.getSampleDimensions().length];

            //place directPos to a pixel center
            GridGeometry2D gg2D = coverage2D.getGridGeometry();
            MathTransform gridToCRS = gg2D.getGridToCRS2D();
            gridToCRS.inverse().transform(directPos, directPos);

            for (int i = 0; i < directPos.getDimension(); i++) {
                directPos.setOrdinate(i, Math.round(directPos.getOrdinate(i)));
            }

            gridToCRS.transform(directPos, directPos);

            coverage2D.evaluate(directPos, values);

            final CoordinateReferenceSystem crs = gg2D.getCoordinateReferenceSystem();
            int dimension = crs.getCoordinateSystem().getDimension();

            double[] point = new double[dimension];
            final MathTransform cov2DGridToCRS = gg2D.getGridToCRS();
            cov2DGridToCRS.transform(point, 0, point, 0, 1);

            final GeneralDirectPosition position = new GeneralDirectPosition(point);
            position.setCoordinateReferenceSystem(crs);
            position.setOrdinate(0, directPos.getOrdinate(0));
            position.setOrdinate(1, directPos.getOrdinate(1));
            result.getValues().put(position, values);

        } else if (coverage instanceof GridCoverageStack) {
            final GridCoverageStack coverageStack = (GridCoverageStack) coverage;
            int length = coverageStack.getStackSize();
            for (int i = 0; i < length; i++) {
                evaluateAllSlices(directPos, (GridCoverage) coverageStack.coverageAtIndex(i), result);
            }
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
