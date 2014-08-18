/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

package org.geotoolkit.process.coverage.mathcalc;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.coverage.ProcessedRenderedImage;
import org.geotoolkit.process.coverage.mathcalc.FillCoverage.SampleEvaluator;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MathCalcImageEvaluator implements ProcessedRenderedImage.Evaluator {

    private final MathTransform gridToCrs;
    private final SampleEvaluator coverageEvaluator;
    private final double[] gridCoord;
    private final double[] crsCoord;
    private final GeneralDirectPosition geoPos;

    /**
     * 
     * @param baseGridCoord
     * @param gridToCrs CORNER grid to crs transform
     * @param coverageEvaluator 
     */
    public MathCalcImageEvaluator(double[] baseGridCoord, MathTransform gridToCrs, SampleEvaluator coverageEvaluator) {
        this.gridToCrs = gridToCrs;
        this.coverageEvaluator = coverageEvaluator;
        this.gridCoord = baseGridCoord;
        this.crsCoord = new double[gridToCrs.getTargetDimensions()];
        this.geoPos = new GeneralDirectPosition(crsCoord.length);
    }
    
    @Override
    public void evaluate(int x, int y, double[] sampleBuffer) {
        gridCoord[0] = x;
        gridCoord[1] = y;
        try {
            gridToCrs.transform(gridCoord, 0, crsCoord, 0, 1);
        } catch (Exception ex) {
            Logger.getLogger(MathCalcProcess.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
            //we should use NoData value
            Arrays.fill(sampleBuffer, Double.NaN);
        }
        geoPos.setCoordinate(crsCoord);
        coverageEvaluator.evaluate(geoPos, sampleBuffer);
    }
    
}
