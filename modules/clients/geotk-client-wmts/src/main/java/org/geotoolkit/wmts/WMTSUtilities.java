/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wmts;

import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.wmts.xml.v100.TileMatrix;
import org.geotoolkit.wmts.xml.v100.TileMatrixSet;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Utility methods for WMTS strange scale calculation
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class WMTSUtilities {
    
    /**
     * Pixel size used in WMTS specification
     */
    public static final double STANDARD_PIXEL_SIZE = 0.00028d; 
    /**
     * Earth perimeter at equator
     */
    public static final double EARTH_PERIMETER = 40075016d;
    
    /**
     * Used for WMTS predefined scale : GlobalCRS84Scale
     */
    public static final double SCALE = 500e6;
    public static final double PIXEL_SIZE = 1.25764139776733;
    
    private WMTSUtilities(){}
    
    /**
     * 
     * @param set
     * @param setCrs
     * @param matrix
     * @return 
     */
    public static double unitsByPixel(final TileMatrixSet set, final CoordinateReferenceSystem setCrs, final TileMatrix matrix){
        
        //predefined scales
        if("GlobalCRS84Scale".equalsIgnoreCase(set.getIdentifier().getValue())){
            return getGlobalCRS84PixelScale(matrix.getScaleDenominator());
        }
        
        // Specification default calculation method :
        // pixelSpan = scaleDenominator * 0.28 10-3 / metersPerUnit(crs)
        final double meterByUnit =  metersPerUnit(setCrs);
        final double candidateUnitByPixel = matrix.getScaleDenominator() * STANDARD_PIXEL_SIZE / meterByUnit;
        return candidateUnitByPixel;
    }
    
    /**
     * 
     * @param crs CoordinateReferenceSystem
     * @param pixelSpan size of a pixel in crs unit
     * @return WMTS scale 
     */
    public static double toScaleDenominator(final CoordinateReferenceSystem crs, final double pixelSpan){
        
        // Specification default calculation method : (Reversed)
        // scaleDenominator = (pixelSpan * metersPerUnit(crs)) / 0.28 10-3 
        final double meterByUnit =  metersPerUnit(crs);
        final double scaleDenom = (pixelSpan * meterByUnit) / STANDARD_PIXEL_SIZE;
        return scaleDenom;
    }
    
    /**
     * 
     * @param crs CoordinateReferenceSystem
     * @return size in meter of one unit along CRS first axis
     */
    public static double metersPerUnit(final CoordinateReferenceSystem crs){
        final CoordinateSystem cs = crs.getCoordinateSystem();
        final Unit axi0Unit = cs.getAxis(0).getUnit();
        
        //in case axis in not in a meter compatible unit
        if(!SI.METRE.isCompatible(axi0Unit)){
            if(axi0Unit == NonSI.DEGREE_ANGLE){
                //axis is in degree, likely a geographic crs
                return EARTH_PERIMETER / 360d ;
            }else{
                throw new IllegalArgumentException("Unsupported unit : "+ axi0Unit);
            }
        }
        
        //convert 1 unit of the crs unit in meter
        return axi0Unit.getConverterTo(SI.METRE).convert(1);
    }
            
    /**
     * 
     * @param scaleDenominator
     * @return 
     */
    public static double getGlobalCRS84PixelScale(double scaleDenominator) {
        return (PIXEL_SIZE * 1.118164528) * scaleDenominator / SCALE ;
    }
    
    /**
     * Find more appropriate {@code CoordinateReferenceSystem} within list of {@code CoordinateReferenceSystem} from envelop.
     * <blockquote><font size=-1>
     * <strong>NOTE: Find another {@code CoordinateReferenceSystem} where envelop will suffer lesser deformation.</strong> 
     * </font></blockquote>
     * 
     * @param env
     * @param listCrs
     * @return more appropriate {@code CoordinateReferenceSystem}.
     * @throws FactoryException if impossible to find {@code MathTransform}.
     * @throws TransformException if impossible to derivative {@code MathTransform}.
     */
    public static CoordinateReferenceSystem getAppropriateCRS(final Envelope env, final List<CoordinateReferenceSystem> listCrs) throws FactoryException, TransformException {
        ArgumentChecks.ensureNonNull("env", env);
        ArgumentChecks.ensureNonNull("list CRS", listCrs);
        if (listCrs.isEmpty()) {
            throw new IllegalArgumentException("impossible to find appropriate CRS with empty list");
        }

        final CoordinateReferenceSystem crsBase = env.getCoordinateReferenceSystem();
        final double xMin = env.getMinimum(0);
        final double yMin = env.getMinimum(1);
        final double xMax = env.getMaximum(0);
        final double yMax = env.getMaximum(1);
        final double longRef = (xMin + xMax) / 2;
        final double latRef = (yMin + yMax) / 2;

        final DirectPosition2D dplong1 = new DirectPosition2D(xMin, latRef);
        final DirectPosition2D dplong2 = new DirectPosition2D(xMax, latRef);
        final DirectPosition2D dplat1 = new DirectPosition2D(longRef, yMin);
        final DirectPosition2D dplat2 = new DirectPosition2D(longRef, yMax);

        final GeneralMatrix mat1 = new GeneralMatrix();
        final GeneralMatrix mat2 = new GeneralMatrix();
        final GeneralMatrix mat3 = new GeneralMatrix();
        final GeneralMatrix mat4 = new GeneralMatrix();

        double valRef = -1;
        double eltTemp;

        CoordinateReferenceSystem crsTemp;
        MathTransform mt;

        int index = -1;
        double valTemp = 0;
        for (int n = 0, s = listCrs.size(); n < s; n++) {
            crsTemp = listCrs.get(n);
            valTemp = 0;
            mt = CRS.findMathTransform(crsBase, crsTemp);
            mat1.set(new GeneralMatrix(mt.derivative(dplong1)));
            mat2.set(new GeneralMatrix(mt.derivative(dplong2)));
            mat3.set(new GeneralMatrix(mt.derivative(dplat1)));
            mat4.set(new GeneralMatrix(mt.derivative(dplat2)));

            if (checkGMatrix(mat1) && checkGMatrix(mat2) && checkGMatrix(mat3) && checkGMatrix(mat4)) {

                mat2.invert();
                mat4.invert();
                mat1.multiply(mat2);
                mat3.multiply(mat4);

                for (int j = 0, nR = mat1.getNumRow(); j < nR; j++) {
                    for (int i = 0, nC = mat1.getNumCol(); i < nC; i++) {
                        if (i == j) {
                            eltTemp = mat1.getElement(i, j) - 1;
                        } else {
                            eltTemp = mat1.getElement(i, j);
                        }
                        valTemp += eltTemp * eltTemp;
                    }
                }

                for (int j = 0, nR = mat3.getNumRow(); j < nR; j++) {
                    for (int i = 0, nC = mat3.getNumCol(); i < nC; i++) {
                        if (i == j) {
                            eltTemp = mat3.getElement(i, j) - 1;
                        } else {
                            eltTemp = mat3.getElement(i, j);
                        }
                        valTemp += eltTemp * eltTemp;
                    }
                }
                if (valTemp < valRef || valRef == -1) {
                    valRef = valTemp;
                    index = n;
                }
            }
        }
        if (index == -1) {
            return env.getCoordinateReferenceSystem();
        }
        return listCrs.get(index);
    }

    /**
     * Verify that {@code GeneralMatrix gM} don't contains : 
     *                                      - {@code NAN} value.
     *                                      - {@code NEGATIVE_INFINITY}.
     *                                      - {@code POSITIVE_INFINITY}.
     * Moreover verify that {@code GeneralMatrix gM} not only contains 0 value.
     * 
     * @param gM
     * @return true if assertion is verified else false.
     */
    public static boolean checkGMatrix(final GeneralMatrix gM) {
        final int nR = gM.getNumRow();
        final int nC = gM.getNumCol();

        for (int j = 0; j < nR; j++) {
            for (int i = 0; i < nC; i++) {
                double gMij = gM.getElement(i, j);
                if (gMij == Double.NaN || gMij == Double.NEGATIVE_INFINITY || gMij == Double.POSITIVE_INFINITY) {
                    return false;
                }
            }
        }
        for (int j = 0; j < nR; j++) {
            for (int i = 0; i < nC; i++) {
                double gMij = gM.getElement(i, j);
                if (gMij != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
