/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2018, Geomatys.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.util.wmm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.opengis.geometry.DirectPosition;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Java port of the NOAA's geomagnetism library (see https://www.ngdc.noaa.gov/geomag/WMM/DoDWMM.shtml)
 * @author Hasdenteufel Eric (Geomatys)
 */
public final class WorldMagneticModel {

    private static final double MEAN_RADIUS = 6371.2;

    /**
     * load the model's coefficients from the resource file WMM.COF
     * @return the magnetic model
     * @throws IOException
     */
    public static MagneticModel readMagModel() throws IOException {
        final InputStream stream = WorldMagneticModel.class.getResourceAsStream("WMM.COF");
        if (stream == null) {
            throw new FileNotFoundException("WMM.COF");
        }

        final List<String> lines;
        try (final InputStream in = stream;
                final InputStreamReader inr = new InputStreamReader(in, StandardCharsets.UTF_8);
                final BufferedReader reader = new BufferedReader(inr)) {
            lines = reader.lines().collect(Collectors.toList());
        }

        return readMagModel(lines);
    }

    /**
     * load the model's coefficients from external resource
     * @param path the path to the model's coefficients
     * @return the magnetic model
     * @throws IOException
     */
    public static MagneticModel readMagModel(Path path) throws IOException {
        return readMagModel(Files.readAllLines(path));
    }

    private static MagneticModel readMagModel(List<String> coefficients) throws IOException {
        boolean stop = false;int l=0, nMax=0;
        while(!stop && l<coefficients.size()) {
            String line =coefficients.get(l++);
            if(!(stop = line.startsWith("99999"))) {
                String field = line.trim().split(" +")[0];
                try {
                    int n = Integer.parseInt(field);
                    nMax = Math.max(n, nMax);
                } catch(NumberFormatException e) {

                }
            }
        }
        MagneticModel model = new MagneticModel(nMax * ( nMax + 1 ) / 2 + nMax);
        model.nMax = nMax;
        model.nMaxSecVar = nMax;
        String[] headers = coefficients.get(0).trim().split(" +");
        model.epoch = Double.parseDouble(headers[0]);
        model.ModelName = headers[1];
        stop = false;l=1;

        while(!stop && l<coefficients.size()) {
            String line =coefficients.get(l++);
            if(!(stop = line.startsWith("99999"))) {
                String[] fields = line.trim().split(" +");
                int n = Integer.parseInt(fields[0]);
                int m = Integer.parseInt(fields[1]);
                double gnm = Double.parseDouble(fields[2]);
                double hnm = Double.parseDouble(fields[3]);
                double dgnm = Double.parseDouble(fields[4]);
                double dhnm = Double.parseDouble(fields[5]);
                if(m <= n) {
                    int index = (n * (n + 1) / 2 + m);
                    model.Main_Field_Coeff_G[index] = gnm;
                    model.Secular_Var_Coeff_G[index] = dgnm;
                    model.Main_Field_Coeff_H[index] = hnm;
                    model.Secular_Var_Coeff_H[index] = dhnm;
                }

            }
        }
        model.CoefficientFileEndDate = model.epoch+5;
        return model;
    }

    /**
     * Calculate the magnetic field elements for a single point
     * @param timedMagneticModel
     * @param coordGeodetic
     * @param ellip
     * @return
     */
    public static GeoMagneticElements computeGeoMagneticElements(MagneticModel timedMagneticModel, DirectPosition positionOfInterest) throws FactoryException, TransformException {
        DirectPosition coordGeodetic = to4326(positionOfInterest);
        final DirectPosition coordSpherical = toSpherical(coordGeodetic);
        SphericalHarmonicVariables SphVariables = ComputeSphericalHarmonicVariables(coordSpherical, timedMagneticModel.nMax);
        LegendreFunction LegendreFunction = computeLegendreFunction(coordSpherical, timedMagneticModel.nMax);
        MagneticResults MagneticResultsSph = summation(LegendreFunction, timedMagneticModel, SphVariables, coordSpherical); /* Accumulate the spherical harmonic coefficients*/
        MagneticResults MagneticResultsSphVar = secVarSummation(LegendreFunction, timedMagneticModel, SphVariables, coordSpherical); /*Sum the Secular Variation Coefficients  */
        MagneticResults MagneticResultsGeo = rotateMagneticVector(coordSpherical, coordGeodetic, MagneticResultsSph); /* Map the computed Magnetic fields to Geodeitic coordinates  */
        MagneticResults MagneticResultsGeoVar = rotateMagneticVector(coordSpherical, coordGeodetic, MagneticResultsSphVar); /* Map the secular variation field components to Geodetic coordinates*/
        GeoMagneticElements GeoMagneticElements = calculateGeoMagneticElements(MagneticResultsGeo); /* Calculate the Geomagnetic elements, Equation 19 , WMM Technical report */
        calculateSecularVariationElements(MagneticResultsGeoVar, GeoMagneticElements); /*Calculate the secular variation of each of the Geomagnetic elements*/
        calculateGridVariation(coordGeodetic,GeoMagneticElements);
        return GeoMagneticElements;
    }

    private static DirectPosition to4326(final DirectPosition source) throws FactoryException, TransformException {
        final CoordinateReferenceSystem sourceCrs = source.getCoordinateReferenceSystem();
        if (sourceCrs == null) {
            throw new IllegalArgumentException("Cannot identify coordinate system of given point. Please set the point CRS.");
        }

        final CoordinateOperation op = CRS.findOperation(sourceCrs, CommonCRS.WGS84.geographic3D(), null);
        final DirectPosition geo = new GeneralDirectPosition(CommonCRS.WGS84.geographic3D());
        return op.getMathTransform().transform(source, geo);
    }

    private static DirectPosition toSpherical(final DirectPosition source) throws FactoryException, TransformException {
        final CoordinateOperation toGeoCentric = CRS.findOperation(source.getCoordinateReferenceSystem(), CommonCRS.WGS84.geocentric(), null);
        final CoordinateOperation toSpheric = CRS.findOperation(CommonCRS.WGS84.geocentric(), CommonCRS.WGS84.spherical(), null);
        final DirectPosition spheric = new GeneralDirectPosition(CommonCRS.WGS84.spherical());
        return MathTransforms.concatenate(toGeoCentric.getMathTransform(), toSpheric.getMathTransform()).transform(source, spheric);
    }

    private static SphericalHarmonicVariables ComputeSphericalHarmonicVariables(DirectPosition coordSpherical, int nMax) {
        SphericalHarmonicVariables sphVariables = new SphericalHarmonicVariables(nMax);
        final double cos_lambda = Math.cos(Math.toRadians(coordSpherical.getCoordinate(1)));
        final double sin_lambda = Math.sin(Math.toRadians(coordSpherical.getCoordinate(1)));
        /* for n = 0 ... model_order, compute (Radius of Earth / Spherical radius r)^(n+2)
        for n  1..nMax-1 (this is much faster than calling pow MAX_N+1 times).      */
        final double radiusRatio = MEAN_RADIUS / (coordSpherical.getCoordinate(2) / 1000);
        sphVariables.RelativeRadiusPower[0] = Math.pow(radiusRatio, 2);
        for(int n = 1; n <= nMax; n++)
        {
            sphVariables.RelativeRadiusPower[n] = sphVariables.RelativeRadiusPower[n - 1] * (radiusRatio);
        }

        /*
         Compute cos(m*lambda), sin(m*lambda) for m = 0 ... nMax
               cos(a + b) = cos(a)*cos(b) - sin(a)*sin(b)
               sin(a + b) = cos(a)*sin(b) + sin(a)*cos(b)
         */
        sphVariables.cos_mlambda[0] = 1.0;
        sphVariables.sin_mlambda[0] = 0.0;

        sphVariables.cos_mlambda[1] = cos_lambda;
        sphVariables.sin_mlambda[1] = sin_lambda;
        for(int m = 2; m <= nMax; m++)
        {
            sphVariables.cos_mlambda[m] = sphVariables.cos_mlambda[m - 1] * cos_lambda - sphVariables.sin_mlambda[m - 1] * sin_lambda;
            sphVariables.sin_mlambda[m] = sphVariables.cos_mlambda[m - 1] * sin_lambda + sphVariables.sin_mlambda[m - 1] * cos_lambda;
        }
        return sphVariables;
    }

    /**
     * Computes  all of the Schmidt-semi normalized associated Legendre
     * functions up to degree nMax. If nMax <= 16, function computePcupLow is used.
     * Otherwise computePcupHigh is called.
     *
     * @param coordSpherical
     * @param nMax Maxumum degree of spherical harmonic secular model
     * @return Calculated Legendre variables in the data structure
     */
    private static LegendreFunction computeLegendreFunction(DirectPosition coordSpherical, int nMax) {
        final double sin_phi = Math.sin(Math.toRadians(coordSpherical.getCoordinate(0))); /* sin  (geocentric latitude) */
        return (nMax <= 16 || (1.0 - Math.abs(sin_phi)) < 1.0e-10)? computePcupLow(sin_phi,nMax) : computePcupHigh(sin_phi,nMax);
    }

    /**
     * This function evaluates all of the Schmidt-semi normalized associated Legendre
     * functions up to degree nMax.
     *
     *  Notes: Overflow may occur if nMax > 20 , especially for high-latitudes.
     *  Use computePcupHigh for large nMax.
     *
     * @param x     cos(colatitude) or sin(latitude).
     * @param nMax  Maximum spherical harmonic degree to compute.
     * @return associated Legendgre polynomials evaluated at x up to nMax
     */
    private static LegendreFunction computePcupLow(double x, int nMax) {

        final int NumTerms = ((nMax + 1) * (nMax + 2) / 2);
        LegendreFunction legendreFunction = new LegendreFunction(NumTerms);

        legendreFunction.Pcup[0] = 1.0;
        legendreFunction.dPcup[0] = 0.0;
        /*sin (geocentric latitude) - sin_phi */
        final double z = Math.sqrt((1.0 - x) * (1.0 + x));

        final double[] schmidtQuasiNorm = new double[NumTerms + 1];


        /* First, Compute the Gauss-normalized associated Legendre  functions*/
        for (int n = 1; n <= nMax; n++) {
            for (int m = 0; m <= n; m++) {
                final int index = (n * (n + 1) / 2 + m);
                if (n == m) {
                    final int index1 = (n - 1) * n / 2 + m - 1;
                    legendreFunction.Pcup[index] = z * legendreFunction.Pcup[index1];
                    legendreFunction.dPcup[index] = z * legendreFunction.dPcup[index1] + x * legendreFunction.Pcup[index1];
                } else if (n == 1 && m == 0) {
                    final int index1 = (n - 1) * n / 2 + m;
                    legendreFunction.Pcup[index] = x * legendreFunction.Pcup[index1];
                    legendreFunction.dPcup[index] = x * legendreFunction.dPcup[index1] - z * legendreFunction.Pcup[index1];
                } else if (n > 1 && n != m) {
                    final int index1 = (n - 2) * (n - 1) / 2 + m;
                    final int index2 = (n - 1) * n / 2 + m;
                    if (m > n - 2) {
                        legendreFunction.Pcup[index] = x * legendreFunction.Pcup[index2];
                        legendreFunction.dPcup[index] = x * legendreFunction.dPcup[index2] - z * legendreFunction.Pcup[index2];
                    } else {
                        final double k = (double) (((n - 1) * (n - 1)) - (m * m)) / (double) ((2 * n - 1) * (2 * n - 3));
                        legendreFunction.Pcup[index] = x * legendreFunction.Pcup[index2] - k * legendreFunction.Pcup[index1];
                        legendreFunction.dPcup[index] = x * legendreFunction.dPcup[index2] - z * legendreFunction.Pcup[index2] - k * legendreFunction.dPcup[index1];
                    }
                }
            }
        }
        /* Compute the ration between the the Schmidt quasi-normalized associated Legendre
         * functions and the Gauss-normalized version. */

        schmidtQuasiNorm[0] = 1.0;
        for (int n = 1; n <= nMax; n++) {
            int index = (n * (n + 1) / 2);
            int index1 = (n - 1) * n / 2;
            /* for m = 0 */
            schmidtQuasiNorm[index] = schmidtQuasiNorm[index1] * (double) (2 * n - 1) / (double) n;

            for (int m = 1; m <= n; m++) {
                index = (n * (n + 1) / 2 + m);
                index1 = (n * (n + 1) / 2 + m - 1);
                schmidtQuasiNorm[index] = schmidtQuasiNorm[index1] * Math.sqrt((double) ((n - m + 1) * (m == 1 ? 2 : 1)) / (double) (n + m));
            }

        }

        /* Converts the  Gauss-normalized associated Legendre
                  functions to the Schmidt quasi-normalized version using pre-computed
                  relation stored in the variable schmidtQuasiNorm */
        for (int n = 1; n <= nMax; n++) {
            for (int m = 0; m <= n; m++) {
                final int index = (n * (n + 1) / 2 + m);
                legendreFunction.Pcup[index] = legendreFunction.Pcup[index] * schmidtQuasiNorm[index];
                legendreFunction.dPcup[index] = -legendreFunction.dPcup[index] * schmidtQuasiNorm[index];
                /* The sign is changed since the new WMM routines use derivative with respect to latitude
                insted of co-latitude */
            }
        }

        return legendreFunction;
    }

   /**
     * This function evaluates all of the Schmidt-semi normalized associated Legendre
     * functions up to degree nMax. The functions are initially scaled by
     *  10^280 sin^m in order to minimize the effects of underflow at large m
     *  near the poles (see Holmes and Featherstone 2002, J. Geodesy, 76, 279-299).
     *  Note that this function performs the same operation as MAG_PcupLow.
     *  However this function also can be used for high degree (large nMax) models.
     *
     * @param x     cos(colatitude) or sin(latitude).
     * @param nMax  Maximum spherical harmonic degree to compute.
     * @return associated Legendgre polynomials evaluated at x up to nMax
     */
    private static LegendreFunction computePcupHigh(double x, int nMax) {

        if (Math.abs(x) == 1.0) {
            throw new IllegalArgumentException("Error in PcupHigh: derivative cannot be calculated at poles");
        }

        if (nMax == 0) {
            throw new IllegalArgumentException("Error in PcupHigh: nMax must be > 0");
        }

        final int NumTerms = ((nMax + 1) * (nMax + 2) / 2);
        LegendreFunction legendreFunction = new LegendreFunction(NumTerms);

        final double[] f1 = new double[NumTerms + 1], f2 = new double[NumTerms + 1], PreSqr = new double[NumTerms + 1];

        final double scalef = 1.0e-280;

        for (int n = 0; n <= 2 * nMax + 1; ++n) {
            PreSqr[n] = Math.sqrt((double) (n));
        }

        int k = 2;

        for (int n = 2; n <= nMax; n++) {
            k = k + 1;
            f1[k] = (double) (2 * n - 1) / (double) (n);
            f2[k] = (double) (n - 1) / (double) (n);
            for (int m = 1; m <= n - 2; m++) {
                k = k + 1;
                f1[k] = (double) (2 * n - 1) / PreSqr[n + m] / PreSqr[n - m];
                f2[k] = PreSqr[n - m - 1] * PreSqr[n + m - 1] / PreSqr[n + m] / PreSqr[n - m];
            }
            k = k + 2;
        }

        /*z = sin (geocentric latitude) */
        final double z = Math.sqrt((1.0 - x) * (1.0 + x));
        double pm2 = 1.0;
        legendreFunction.Pcup[0] = 1.0;
        legendreFunction.dPcup[0] = 0.0;

        double pm1 = x;
        legendreFunction.Pcup[1] = pm1;
        legendreFunction.dPcup[1] = z;
        k = 1;

        for (int n = 2; n <= nMax; n++) {
            k = k + n;
            double plm = f1[k] * x * pm1 - f2[k] * pm2;
            legendreFunction.Pcup[k] = plm;
            legendreFunction.dPcup[k] = (double) (n) * (pm1 - x * plm) / z;
            pm2 = pm1;
            pm1 = plm;
        }

        double pmm = PreSqr[2] * scalef;
        double rescalem = 1.0 / scalef;
        int kstart = 0;

        for (int m = 1; m <= nMax - 1; ++m) {
            rescalem = rescalem * z;

            /* Calculate legendreFunction.Pcup(m,m)*/
            kstart = kstart + m + 1;
            pmm = pmm * PreSqr[2 * m + 1] / PreSqr[2 * m];
            legendreFunction.Pcup[kstart] = pmm * rescalem / PreSqr[2 * m + 1];
            legendreFunction.dPcup[kstart] = -((double) (m) * x * legendreFunction.Pcup[kstart] / z);
            pm2 = pmm / PreSqr[2 * m + 1];
            /* Calculate legendreFunction.Pcup(m+1,m)*/
            k = kstart + m + 1;
            pm1 = x * PreSqr[2 * m + 1] * pm2;
            legendreFunction.Pcup[k] = pm1 * rescalem;
            legendreFunction.dPcup[k] = ((pm2 * rescalem) * PreSqr[2 * m + 1] - x * (double) (m + 1) * legendreFunction.Pcup[k]) / z;
            /* Calculate legendreFunction.Pcup(n,m)*/
            for (int n = m + 2; n <= nMax; ++n) {
                k = k + n;
                double plm = x * f1[k] * pm1 - f2[k] * pm2;
                legendreFunction.Pcup[k] = plm * rescalem;
                legendreFunction.dPcup[k] = (PreSqr[n + m] * PreSqr[n - m] * (pm1 * rescalem) - (double) (n) * x * legendreFunction.Pcup[k]) / z;
                pm2 = pm1;
                pm1 = plm;
            }
        }

        /* Calculate legendreFunction.Pcup(nMax,nMax)*/
        rescalem = rescalem * z;
        kstart = kstart + nMax + 1; /// hacks
        pmm = pmm / PreSqr[2 * nMax];
        legendreFunction.Pcup[kstart] = pmm * rescalem;
        legendreFunction.dPcup[kstart] = -(double) (nMax) * x * legendreFunction.Pcup[kstart] / z;

        return legendreFunction;
    }

    /**
     * Computes Geomagnetic Field Elements X, Y and Z in Spherical coordinate system using
     * spherical harmonic summation.
     *
     * @param legendreFunction
     * @param magneticModel
     * @param sphVariables
     * @param coordSpherical
     * @return
     */
    private static MagneticResults summation(LegendreFunction legendreFunction, MagneticModel magneticModel, SphericalHarmonicVariables sphVariables, DirectPosition coordSpherical) {
        MagneticResults magneticResults = new MagneticResults();

        for (int n = 1; n <= magneticModel.nMax; n++) {
            for (int m = 0; m <= n; m++) {
                final int index = (n * (n + 1) / 2 + m);
                /*          nMax      (n+2)       n     m            m           m
                        Bz =   -SUM (a/r)   (n+1) SUM  [g cos(m p) + h sin(m p)] P (sin(phi))
                                        n=1                m=0   n            n           n  */
                /* Equation 12 in the WMM Technical report.  Derivative with respect to radius.*/
                magneticResults.Bz -= sphVariables.RelativeRadiusPower[n]
                        * (magneticModel.Main_Field_Coeff_G[index] * sphVariables.cos_mlambda[m]
                        + magneticModel.Main_Field_Coeff_H[index] * sphVariables.sin_mlambda[m])
                        * (double) (n + 1) * legendreFunction.Pcup[index];
                /*        1 nMax  (n+2)    n     m            m           m
                        By =    SUM (a/r) (m)  SUM  [g cos(m p) + h sin(m p)] dP (sin(phi))
                                   n=1             m=0   n            n           n  */
                /* Equation 11 in the WMM Technical report. Derivative with respect to longitude, divided by radius. */
                magneticResults.By += sphVariables.RelativeRadiusPower[n]
                        * (magneticModel.Main_Field_Coeff_G[index] * sphVariables.sin_mlambda[m]
                        - magneticModel.Main_Field_Coeff_H[index] * sphVariables.cos_mlambda[m])
                        * (double) (m) * legendreFunction.Pcup[index];
                /*          nMax  (n+2) n     m            m           m
                        Bx = - SUM (a/r)   SUM  [g cos(m p) + h sin(m p)] dP (sin(phi))
                                   n=1         m=0   n            n           n  */
                /* Equation 10  in the WMM Technical report. Derivative with respect to latitude, divided by radius. */
                magneticResults.Bx -= sphVariables.RelativeRadiusPower[n]
                        * (magneticModel.Main_Field_Coeff_G[index] * sphVariables.cos_mlambda[m]
                        + magneticModel.Main_Field_Coeff_H[index] * sphVariables.sin_mlambda[m])
                        * legendreFunction.dPcup[index];

            }
        }

        final double cos_phi = Math.cos(Math.toRadians(coordSpherical.getCoordinate(0)));
        if (Math.abs(cos_phi) > 1.0e-10) {
            magneticResults.By = magneticResults.By / cos_phi;
        } else /* Special calculation for component - By - at Geographic poles.
             * If the user wants to avoid using this function,  please make sure that
             * the latitude is not exactly +/-90.
         */ {
            summationSpecial(magneticModel, sphVariables, coordSpherical, magneticResults);
        }
        return magneticResults;
    }

    /**
     * Special calculation for the component By at Geographic poles.
     * @param magneticModel
     * @param sphVariables
     * @param coordSpherical
     * @param magneticResults
     */
    private static void summationSpecial(MagneticModel magneticModel, SphericalHarmonicVariables sphVariables, DirectPosition coordSpherical, MagneticResults magneticResults) {

        double[] PcupS = new double[magneticModel.nMax + 1];

        PcupS[0] = 1;
        double schmidtQuasiNorm1 = 1.0;
        magneticResults.By = 0.0;
        final double sin_phi = Math.sin(Math.toRadians(coordSpherical.getCoordinate(0)));

        for (int n = 1; n <= magneticModel.nMax; n++) {

            /*  Compute the ration between the Gauss-normalized associated Legendre
                functions and the Schmidt quasi-normalized version. This is equivalent to
                sqrt((m==0?1:2)*(n-m)!/(n+m!))*(2n-1)!!/(n-m)!  */
            final int index = (n * (n + 1) / 2 + 1);
            double schmidtQuasiNorm2 = schmidtQuasiNorm1 * (double) (2 * n - 1) / (double) n;
            double schmidtQuasiNorm3 = schmidtQuasiNorm2 * Math.sqrt((double) (n * 2) / (double) (n + 1));
            schmidtQuasiNorm1 = schmidtQuasiNorm2;
            if (n == 1) {
                PcupS[n] = PcupS[n - 1];
            } else {
                final double k = (double) (((n - 1) * (n - 1)) - 1) / (double) ((2 * n - 1) * (2 * n - 3));
                PcupS[n] = sin_phi * PcupS[n - 1] - k * PcupS[n - 2];
            }
            /*          1 nMax  (n+2)    n     m            m           m
                By =    SUM (a/r) (m)  SUM  [g cos(m p) + h sin(m p)] dP (sin(phi))
                           n=1             m=0   n            n           n  */
            /* Equation 11 in the WMM Technical report. Derivative with respect to longitude, divided by radius. */
            magneticResults.By += sphVariables.RelativeRadiusPower[n]
                    * (magneticModel.Main_Field_Coeff_G[index] * sphVariables.sin_mlambda[1]
                    - magneticModel.Main_Field_Coeff_H[index] * sphVariables.cos_mlambda[1])
                    * PcupS[n] * schmidtQuasiNorm3;
        }


    }

    /**
     * This Function sums the secular variation coefficients to get the secular variation of the Magnetic vector
     * @param legendreFunction
     * @param magneticModel
     * @param sphVariables
     * @param coordSpherical
     * @return
     */
    private static MagneticResults secVarSummation(LegendreFunction legendreFunction, MagneticModel magneticModel, SphericalHarmonicVariables sphVariables, DirectPosition coordSpherical) {
        magneticModel.SecularVariationUsed = true;
        MagneticResults magneticResults = new MagneticResults();

        for (int n = 1; n <= magneticModel.nMaxSecVar; n++) {
            for (int m = 0; m <= n; m++) {
                final int index = (n * (n + 1) / 2 + m);
                /*            nMax      (n+2)       n     m            m           m
                        Bz =   -SUM (a/r)   (n+1) SUM  [g cos(m p) + h sin(m p)] P (sin(phi))
                                        n=1                m=0   n            n           n  */
                /*  Derivative with respect to radius.*/
                magneticResults.Bz -= sphVariables.RelativeRadiusPower[n]
                        * (magneticModel.Secular_Var_Coeff_G[index] * sphVariables.cos_mlambda[m]
                        + magneticModel.Secular_Var_Coeff_H[index] * sphVariables.sin_mlambda[m])
                        * (double) (n + 1) * legendreFunction.Pcup[index];
                /*          1 nMax  (n+2)    n     m            m           m
                        By =    SUM (a/r) (m)  SUM  [g cos(m p) + h sin(m p)] dP (sin(phi))
                                   n=1             m=0   n            n           n  */
                /* Derivative with respect to longitude, divided by radius. */
                magneticResults.By += sphVariables.RelativeRadiusPower[n]
                        * (magneticModel.Secular_Var_Coeff_G[index] * sphVariables.sin_mlambda[m]
                        - magneticModel.Secular_Var_Coeff_H[index] * sphVariables.cos_mlambda[m])
                        * (double) (m) * legendreFunction.Pcup[index];
                /*           nMax  (n+2) n     m            m           m
                        Bx = - SUM (a/r)   SUM  [g cos(m p) + h sin(m p)] dP (sin(phi))
                                   n=1         m=0   n            n           n  */
                /* Derivative with respect to latitude, divided by radius. */
                magneticResults.Bx -= sphVariables.RelativeRadiusPower[n]
                        * (magneticModel.Secular_Var_Coeff_G[index] * sphVariables.cos_mlambda[m]
                        + magneticModel.Secular_Var_Coeff_H[index] * sphVariables.sin_mlambda[m])
                        * legendreFunction.dPcup[index];
            }
        }
        final double cos_phi = Math.cos(Math.toRadians(coordSpherical.getCoordinate(0)));
        if (Math.abs(cos_phi) > 1.0e-10) {
            magneticResults.By = magneticResults.By / cos_phi;
        } else /* Special calculation for component By at Geographic poles */ {
            secVarSummationSpecial(magneticModel, sphVariables, coordSpherical, magneticResults);
        }
        return magneticResults;
    }

    /**
     * Special calculation for the secular variation summation at the poles
     * @param magneticModel
     * @param sphVariables
     * @param coordSpherical
     * @param magneticResults
     */
    private static void secVarSummationSpecial(MagneticModel magneticModel, SphericalHarmonicVariables sphVariables, DirectPosition coordSpherical, MagneticResults magneticResults) {

        double[] PcupS = new double[magneticModel.nMaxSecVar + 1];

        PcupS[0] = 1.0;
        double schmidtQuasiNorm1 = 1.0;
        magneticResults.By = 0.0;
        final double sin_phi = Math.sin(Math.toRadians(coordSpherical.getCoordinate(0)));

        for (int n = 1; n <= magneticModel.nMaxSecVar; n++) {
            final int index = (n * (n + 1) / 2 + 1);
            double schmidtQuasiNorm2 = schmidtQuasiNorm1 * (double) (2 * n - 1) / (double) n;
            double schmidtQuasiNorm3 = schmidtQuasiNorm2 * Math.sqrt((double) (n * 2) / (double) (n + 1));
            schmidtQuasiNorm1 = schmidtQuasiNorm2;
            if (n == 1) {
                PcupS[n] = PcupS[n - 1];
            } else {
               final double k = (double) (((n - 1) * (n - 1)) - 1) / (double) ((2 * n - 1) * (2 * n - 3));
                PcupS[n] = sin_phi * PcupS[n - 1] - k * PcupS[n - 2];
            }
            /*          1 nMax  (n+2)    n     m            m           m
                By =    SUM (a/r) (m)  SUM  [g cos(m p) + h sin(m p)] dP (sin(phi))
                           n=1             m=0   n            n           n  */
            /* Derivative with respect to longitude, divided by radius. */
            magneticResults.By += sphVariables.RelativeRadiusPower[n]
                    * (magneticModel.Secular_Var_Coeff_G[index] * sphVariables.sin_mlambda[1]
                    - magneticModel.Secular_Var_Coeff_H[index] * sphVariables.cos_mlambda[1])
                    * PcupS[n] * schmidtQuasiNorm3;
        }

    }

    /**
     * Rotate the Magnetic Vectors to Geodetic Coordinates
     * @param coordSpherical
     * @param coordGeodetic
     * @param magneticResultsSph
     * @return
     */
    private static MagneticResults rotateMagneticVector(DirectPosition coordSpherical, DirectPosition coordGeodetic, MagneticResults magneticResultsSph) {

        MagneticResults MagneticResultsGeo = new MagneticResults();

        /* Difference between the spherical and Geodetic latitudes */
        final double Psi = Math.toRadians(coordSpherical.getCoordinate(0) - coordGeodetic.getCoordinate(0));

        /* Rotate spherical field components to the Geodetic system */
        MagneticResultsGeo.Bz = magneticResultsSph.Bx * Math.sin(Psi) + magneticResultsSph.Bz * Math.cos(Psi);
        MagneticResultsGeo.Bx = magneticResultsSph.Bx * Math.cos(Psi) - magneticResultsSph.Bz * Math.sin(Psi);
        MagneticResultsGeo.By = magneticResultsSph.By;

        return MagneticResultsGeo;
    }

    /**
     * Calculate all the Geomagnetic elements from X,Y and Z components
     * @param magneticResultsGeo
     * @return
     */
    private static GeoMagneticElements calculateGeoMagneticElements(MagneticResults magneticResultsGeo) {
        GeoMagneticElements geoMagneticElements = new GeoMagneticElements();
        geoMagneticElements.X = magneticResultsGeo.Bx;
        geoMagneticElements.Y = magneticResultsGeo.By;
        geoMagneticElements.Z = magneticResultsGeo.Bz;

        geoMagneticElements.H = Math.sqrt(magneticResultsGeo.Bx * magneticResultsGeo.Bx + magneticResultsGeo.By * magneticResultsGeo.By);
        geoMagneticElements.F = Math.sqrt(geoMagneticElements.H * geoMagneticElements.H + magneticResultsGeo.Bz * magneticResultsGeo.Bz);
        geoMagneticElements.Decl = Math.toDegrees(Math.atan2(geoMagneticElements.Y, geoMagneticElements.X));
        geoMagneticElements.Incl = Math.toDegrees(Math.atan2(geoMagneticElements.Z, geoMagneticElements.H));
        return geoMagneticElements;
    }

    /**
     * Calculate the secular variation of each of the Geomagnetic elements.
     * @param magneticVariation
     * @param magneticElements
     */
    private static void calculateSecularVariationElements(MagneticResults magneticVariation, GeoMagneticElements magneticElements) {

        magneticElements.Xdot = magneticVariation.Bx;
        magneticElements.Ydot = magneticVariation.By;
        magneticElements.Zdot = magneticVariation.Bz;
        magneticElements.Hdot = (magneticElements.X * magneticElements.Xdot + magneticElements.Y * magneticElements.Ydot) / magneticElements.H; /* See equation 19 in the WMM technical report */
        magneticElements.Fdot = (magneticElements.X * magneticElements.Xdot + magneticElements.Y * magneticElements.Ydot + magneticElements.Z * magneticElements.Zdot) / magneticElements.F;
        magneticElements.Decldot = 180.0 / Math.PI * (magneticElements.X * magneticElements.Ydot - magneticElements.Y * magneticElements.Xdot) / (magneticElements.H * magneticElements.H);
        magneticElements.Incldot = 180.0 / Math.PI * (magneticElements.H * magneticElements.Zdot - magneticElements.Z * magneticElements.Hdot) / (magneticElements.F * magneticElements.F);
        magneticElements.GVdot = magneticElements.Decldot;

    }

    /**
     * Minimum Latitude for Polar Stereographic projection in degrees
     */
    private static final double MAG_PS_MIN_LAT_DEGREE = -55.0;

    /**
     * Maximum Latitude for Polar Stereographic projection in degrees
     */
    private static final double MAG_PS_MAX_LAT_DEGREE = 55.0;


    /**
     * Computes the grid variation for |latitudes| > MAG_MAX_LAT_DEGREE
     *
     * Grivation (or grid variation) is the angle between grid north and
     * magnetic north. This routine calculates Grivation for the Polar Stereographic
     * projection for polar locations (Latitude => |55| deg). Otherwise, it computes the grid
     * variation in UTM projection system. However, the UTM projection codes may be used to compute
     * the grid variation at any latitudes.
     *
     * @param location
     * @param elements
     */
    private static void calculateGridVariation(DirectPosition location, GeoMagneticElements elements) {

        if(location.getCoordinate(0) >= MAG_PS_MAX_LAT_DEGREE)
        {
            elements.GV = elements.Decl - location.getCoordinate(1);

        } else if(location.getCoordinate(0) <= MAG_PS_MIN_LAT_DEGREE)
        {
            elements.GV = elements.Decl + location.getCoordinate(1);

        } else
        {
            UTMParameters utmParameters = getTransverseMercator(location);
            elements.GV = elements.Decl - utmParameters.ConvergenceOfMeridians;
        }
    }

    /**
     * Gets the UTM Parameters for a given Latitude and Longitude
     * @param DirectPosition
     * @return
     */
    private static UTMParameters getTransverseMercator(DirectPosition DirectPosition) {

        final double Lambda = Math.toRadians(DirectPosition.getCoordinate(1));
        final double Phi = Math.toRadians(DirectPosition.getCoordinate(0));

        UTMParameters utmParameters = getUTMParameters(Phi, Lambda);
        final double K0 = 0.9996;
        double falseE = 500000, falseN=0;

        if (utmParameters.HemiSphere == 'n' || utmParameters.HemiSphere == 'N') {
            falseN = 0;
        }
        if (utmParameters.HemiSphere == 's' || utmParameters.HemiSphere == 'S') {
            falseN = 10000000;
        }

        /* WGS84 ellipsoid */

        final double Eps = 0.081819190842621494335;
        final double Epssq = 0.0066943799901413169961;
        final double K0R4 = 6367449.1458234153093;
        final double K0R4oa = 0.99832429843125277950;

        final double[] Acoeff = new double[] {
            8.37731820624469723600E-04,
            7.60852777357248641400E-07,
            1.19764550324249124400E-09,
            2.42917068039708917100E-12,
            5.71181837042801392800E-15,
            1.47999793137966169400E-17,
            4.10762410937071532000E-20,
            1.21078503892257704200E-22
        };

        /* WGS84 ellipsoid */


        /*   Execution of the forward T.M. algorithm  */

        final int XYonly = 0;
        TMfwd4(Eps, Epssq, K0R4, K0R4oa, Acoeff,
            Math.toRadians(utmParameters.CentralMeridian), K0, falseE, falseN,
            XYonly,
            Lambda, Phi,
            utmParameters);

        return utmParameters;
    }

    /**
     * Minimum Latitude for UTM projection in degrees
     */
    private static final double MAG_UTM_MIN_LAT_DEGREE = -80.5;

    /**
     * Maximum Latitude for UTM projection in degrees
     */
    private static final double MAG_UTM_MAX_LAT_DEGREE = 84.5;

    /**
     * converts geodetic (latitude and longitude) coordinates
     * to UTM projection parameters (zone, hemisphere and central meridian)
     *
     * @param latitude latitude in radians
     * @param longitude longitude in radians
     * @return Zone, Hemisphere & CentralMeridian UTM parameters
     */
    private static UTMParameters getUTMParameters(double latitude, double longitude) {

        if ((latitude < Math.toRadians(MAG_UTM_MIN_LAT_DEGREE)) || (latitude > Math.toRadians(MAG_UTM_MAX_LAT_DEGREE))) {
            throw new IndexOutOfBoundsException("Latitude out of range");
        }
        if ((longitude < -Math.PI) || (longitude > (2 * Math.PI))) {
            throw new IndexOutOfBoundsException("Longitude out of range");
        }

        if (longitude < 0) {
            longitude += (2 * Math.PI) + 1.0e-10;
        }
        final long Lat_Degrees = (long) (latitude * 180.0 / Math.PI);
        final long Long_Degrees = (long) (longitude * 180.0 / Math.PI);
        long temp_zone;
        if (longitude < Math.PI) {
            temp_zone = (long) (31 + ((longitude * 180.0 / Math.PI) / 6.0));
        } else {
            temp_zone = (long) (((longitude * 180.0 / Math.PI) / 6.0) - 29);
        }
        if (temp_zone > 60) {
            temp_zone = 1;
        }
        /* UTM special cases */
        if ((Lat_Degrees > 55) && (Lat_Degrees < 64) && (Long_Degrees > -1)
                && (Long_Degrees < 3)) {
            temp_zone = 31;
        }
        if ((Lat_Degrees > 55) && (Lat_Degrees < 64) && (Long_Degrees > 2)
                && (Long_Degrees < 12)) {
            temp_zone = 32;
        }
        if ((Lat_Degrees > 71) && (Long_Degrees > -1) && (Long_Degrees < 9)) {
            temp_zone = 31;
        }
        if ((Lat_Degrees > 71) && (Long_Degrees > 8) && (Long_Degrees < 21)) {
            temp_zone = 33;
        }
        if ((Lat_Degrees > 71) && (Long_Degrees > 20) && (Long_Degrees < 33)) {
            temp_zone = 35;
        }
        if ((Lat_Degrees > 71) && (Long_Degrees > 32) && (Long_Degrees < 42)) {
            temp_zone = 37;
        }

        UTMParameters utmParameters = new UTMParameters();

        if (temp_zone >= 31) {
            utmParameters.CentralMeridian = (6.0 * temp_zone - 183);
        } else {
            utmParameters.CentralMeridian = (6.0 * temp_zone + 177);
        }
        utmParameters.Zone = (int) temp_zone;
        if (latitude < 0) {
            utmParameters.HemiSphere = 'S';
        } else {
            utmParameters.HemiSphere = 'N';
        }

        return utmParameters;
    }

    /**
     * Transverse Mercator forward equations including point-scale and CoM
     * @param Eps           Eccentricity (epsilon) of the ellipsoid
     * @param Epssq         Eccentricity squared
     * @param K0R4          K0 times R4
     * @param K0R4oa        K0 times Ratio of R4 over semi-major axis
     * @param Acoeff        Trig series coefficients, omega as a function of chi
     * @param Lam0          Longitude of the central meridian in radians
     * @param K0            Central scale factor, for example, 0.9996 for UTM
     * @param falseE        False easting, for example, 500000 for UTM
     * @param falseN        False northing
     * @param XYonly        If one (1), then only X and Y will be properly computed
     * @param Lambda        Longitude (from Greenwich) in radians
     * @param Phi           Latitude in radians
     * @param utmParameters fill Easting, Northing, PointScale & ConvergenceOfMeridians UTM paramaters
     */
    private static void TMfwd4(
            double Eps, double Epssq, double K0R4, double K0R4oa,
            double Acoeff[], double Lam0, double K0, double falseE,
            double falseN, int XYonly, double Lambda, double Phi,
            UTMParameters utmParameters) {

        double Lam, CLam, SLam, CPhi, SPhi;
        double P, part1, part2, denom, CChi, SChi;
        double U, V;
        double T, Tsq, denom2;
        double c2u, s2u, c4u, s4u, c6u, s6u, c8u, s8u;
        double c2v, s2v, c4v, s4v, c6v, s6v, c8v, s8v;
        double Xstar, Ystar;
        double sig1, sig2, comroo;

        /*
           Ellipsoid to sphere
           --------- -- ------

           Convert longitude (Greenwhich) to longitude from the central meridian
           It is unnecessary to find the (-Pi, Pi] equivalent of the result.
           Compute its cosine and sine.
         */
        Lam = Lambda - Lam0;
        CLam = Math.cos(Lam);
        SLam = Math.sin(Lam);

        /*   Latitude  */
        CPhi = Math.cos(Phi);
        SPhi = Math.sin(Phi);

        /*   Convert geodetic latitude, Phi, to conformal latitude, Chi
             Only the cosine and sine of Chi are actually needed.        */
        P = Math.exp(Eps * ATanH(Eps * SPhi));
        part1 = (1 + SPhi) / P;
        part2 = (1 - SPhi) * P;
        denom = 1 / (part1 + part2);
        CChi = 2 * CPhi * denom;
        SChi = (part1 - part2) * denom;

        /*
           Sphere to first plane
           ------ -- ----- -----

           Apply spherical theory of transverse Mercator to get (u,v) coordinates
           Note the order of the arguments in Fortran's version of ArcTan, i.e.
                     atan2(y, x) = ATan(y/x)
           The two argument form of ArcTan is needed here.
         */
        T = CChi * SLam;
        U = ATanH(T);
        V = Math.atan2(SChi, CChi * CLam);


        /*
           Trigonometric multiple angles
           ------------- -------- ------

           Compute Cosh of even multiples of U
           Compute Sinh of even multiples of U
           Compute Cos  of even multiples of V
           Compute Sin  of even multiples of V
         */
        Tsq = T * T;
        denom2 = 1 / (1 - Tsq);
        c2u = (1 + Tsq) * denom2;
        s2u = 2 * T * denom2;
        c2v = (-1 + CChi * CChi * (1 + CLam * CLam)) * denom2;
        s2v = 2 * CLam * CChi * SChi * denom2;

        c4u = 1 + 2 * s2u * s2u;
        s4u = 2 * c2u * s2u;
        c4v = 1 - 2 * s2v * s2v;
        s4v = 2 * c2v * s2v;

        c6u = c4u * c2u + s4u * s2u;
        s6u = s4u * c2u + c4u * s2u;
        c6v = c4v * c2v - s4v * s2v;
        s6v = s4v * c2v + c4v * s2v;

        c8u = 1 + 2 * s4u * s4u;
        s8u = 2 * c4u * s4u;
        c8v = 1 - 2 * s4v * s4v;
        s8v = 2 * c4v * s4v;


        /*   First plane to second plane
             ----- ----- -- ------ -----

             Accumulate terms for X and Y
         */
        Xstar = Acoeff[3] * s8u * c8v;
        Xstar = Xstar + Acoeff[2] * s6u * c6v;
        Xstar = Xstar + Acoeff[1] * s4u * c4v;
        Xstar = Xstar + Acoeff[0] * s2u * c2v;
        Xstar = Xstar + U;

        Ystar = Acoeff[3] * c8u * s8v;
        Ystar = Ystar + Acoeff[2] * c6u * s6v;
        Ystar = Ystar + Acoeff[1] * c4u * s4v;
        Ystar = Ystar + Acoeff[0] * c2u * s2v;
        Ystar = Ystar + V;

        /*   Apply isoperimetric radius, scale adjustment, and offsets  */
         utmParameters.Easting = K0R4 * Xstar + falseE;
         utmParameters.Northing = K0R4 * Ystar + falseN;


        /*  Point-scale and CoM
            ----- ----- --- ---  */
        if (XYonly == 1) {
             utmParameters.PointScale = K0;
             utmParameters.ConvergenceOfMeridians = 0;
        } else {
            sig1 = 8 * Acoeff[3] * c8u * c8v;
            sig1 = sig1 + 6 * Acoeff[2] * c6u * c6v;
            sig1 = sig1 + 4 * Acoeff[1] * c4u * c4v;
            sig1 = sig1 + 2 * Acoeff[0] * c2u * c2v;
            sig1 = sig1 + 1;

            sig2 = 8 * Acoeff[3] * s8u * s8v;
            sig2 = sig2 + 6 * Acoeff[2] * s6u * s6v;
            sig2 = sig2 + 4 * Acoeff[1] * s4u * s4v;
            sig2 = sig2 + 2 * Acoeff[0] * s2u * s2v;

            /*    Combined square roots  */
            comroo = Math.sqrt((1 - Epssq * SPhi * SPhi) * denom2
                    * (sig1 * sig1 + sig2 * sig2));

             utmParameters.PointScale = K0R4oa * 2 * denom * comroo;
             utmParameters.ConvergenceOfMeridians = Math.atan2(SChi * SLam, CLam) + Math.atan2(sig2, sig1);
        }



    }

    private static double ATanH(double x) {
        return (0.5 * Math.log((1 + x) / (1 - x)));
    }


}
