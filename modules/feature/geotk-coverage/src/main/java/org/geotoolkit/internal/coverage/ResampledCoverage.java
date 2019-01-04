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
package org.geotoolkit.internal.coverage;

import java.util.Map;
import org.geotoolkit.coverage.grid.AbstractGridCoverage;
import org.geotoolkit.coverage.grid.GridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.coverage.SampleDimension;
import org.geotoolkit.coverage.grid.GridGeometry;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResampledCoverage extends AbstractGridCoverage {

    private final Coverage base;
    private final GridGeometry gridGeometry;

    public static GridCoverage create(CharSequence name, Coverage base, GridGeometry gridGeom, Map<?,?> properties) {
        return new ResampledCoverage(name, base, gridGeom, properties);
    }

    private ResampledCoverage(CharSequence name, Coverage base, GridGeometry gridGeom, Map<?,?> properties) {
        super(name, base.getCoordinateReferenceSystem(), null, properties);
        this.base = base;
        this.gridGeometry = gridGeom;
    }

    @Override
    public Object evaluate(DirectPosition point) throws PointOutsideCoverageException, CannotEvaluateException {
        return base.evaluate(point);
    }

    @Override
    public double[] evaluate(DirectPosition point, double[] dest) throws PointOutsideCoverageException, CannotEvaluateException {
        return base.evaluate(point, dest);
    }

    @Override
    public float[] evaluate(DirectPosition point, float[] dest) throws PointOutsideCoverageException, CannotEvaluateException {
        return base.evaluate(point, dest);
    }

    @Override
    public int[] evaluate(DirectPosition point, int[] dest) throws PointOutsideCoverageException, CannotEvaluateException {
        return base.evaluate(point, dest);
    }

    @Override
    public byte[] evaluate(DirectPosition point, byte[] dest) throws PointOutsideCoverageException, CannotEvaluateException {
        return base.evaluate(point, dest);
    }

    @Override
    public boolean[] evaluate(DirectPosition point, boolean[] dest) throws PointOutsideCoverageException, CannotEvaluateException {
        return base.evaluate(point, dest);
    }

    @Override
    public int getNumSampleDimensions() {
        return base.getNumSampleDimensions();
    }

    @Override
    public SampleDimension getSampleDimension(int index) throws IndexOutOfBoundsException {
        return base.getSampleDimension(index);
    }

    @Override
    public Envelope getEnvelope() {
        if (gridGeometry instanceof GridGeometry) {
            return ((GridGeometry2D)gridGeometry).getEnvelope();
        } else {
            return new GridGeometry(gridGeometry).getEnvelope();
        }
    }

    @Override
    public GridGeometry getGridGeometry() {
        return gridGeometry;
    }

}
