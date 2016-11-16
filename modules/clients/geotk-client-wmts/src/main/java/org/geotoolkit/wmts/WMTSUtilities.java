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
import java.util.Map;
import javax.measure.Unit;

import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.measure.Units;

import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.OutOfDomainOfValidityException;
import org.geotoolkit.wmts.xml.v100.TileMatrix;
import org.geotoolkit.wmts.xml.v100.TileMatrixSet;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Utility methods for WMTS strange scale calculation
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
        if(!Units.METRE.isCompatible(axi0Unit)){
            if(Units.DEGREE.equals(axi0Unit)){
                //axis is in degree, likely a geographic crs
                return EARTH_PERIMETER / 360d ;
            }else{
                throw new IllegalArgumentException("Unsupported unit : "+ axi0Unit);
            }
        }

        //convert 1 unit of the crs unit in meter
        return axi0Unit.getConverterTo(Units.METRE).convert(1);
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

        double valRef = -1;
        double eltTemp;
        //in case we fail to find a crs more appropriate then another, we return the first one
        int index = 0;

        for (int n = 0, s = listCrs.size(); n < s; n++) {
            final CoordinateReferenceSystem crsTemp = listCrs.get(n);
            double valTemp = 0;
            final MathTransform mt = CRS.findOperation(crsBase, crsTemp, null).getMathTransform();
            MatrixSIS mat1 = MatrixSIS.castOrCopy(mt.derivative(dplong1));
            MatrixSIS mat2 = MatrixSIS.castOrCopy(mt.derivative(dplong2));
            MatrixSIS mat3 = MatrixSIS.castOrCopy(mt.derivative(dplat1));
            MatrixSIS mat4 = MatrixSIS.castOrCopy(mt.derivative(dplat2));

            if (checkGMatrix(mat1) && checkGMatrix(mat2) && checkGMatrix(mat3) && checkGMatrix(mat4)) {

                mat2 = mat2.inverse();
                mat4 = mat4.inverse();
                mat1 = mat1.multiply(mat2);
                mat3 = mat3.multiply(mat4);

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
    public static boolean checkGMatrix(final Matrix gM) {
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

    /**
     * Adapt input envelope to fit urn:ogc:def:wkss:OGC:1.0:GoogleCRS84Quad. Also give well known scales into the interval
     * given in parameter.
     *
     * As specified by WMTS standard v1.0.0 :
     * <p>
     *     [GoogleCRS84Quad] well-known scale set has been defined to allow quadtree pyramids in CRS84. Level
     * 0 allows representing the whole world in a single 256x256 pixels (where the first 64 and
     * last 64 lines of the tile are left blank). The next level represents the whole world in 2x2
     * tiles of 256x256 pixels and so on in powers of 2. Scale denominator is only accurate near
     * the equator.
     * </p>
     *
     * /!\ The well-known scales computed here have been designed for CRS:84 and Mercator projected CRS. Using it for
     * other coordinate reference systems can result in strange results.
     *
     * Note : only horizontal part of input envelope is analysed, so returned envelope will have same values as input one
     * for all additional dimension.
     *
     * @param envelope An envelope to adapt to well known scale quad-tree.
     * @param scaleLimit Minimum and maximum authorized scales. Edge inclusive. Unit must be input envelope horizontal
     *                    axis unit.
     * @return An entry with adapted envelope and its well known scales.
     */
    public static Map.Entry<Envelope, double[]> toWellKnownScale(final Envelope envelope, final NumberRange<Double> scaleLimit)
            throws TransformException, OutOfDomainOfValidityException {
        return CoverageUtilities.toWellKnownScale(envelope, scaleLimit);
    }
}
