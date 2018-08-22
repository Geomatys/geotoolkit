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

/**
 * Java port of the NOAA's MAGtype_MagneticModel structure
 * @author Hasdenteufel Eric (Geomatys)
 */
public class MagneticModel {

    double EditionDate;
    double epoch; /*Base time of Geomagnetic model epoch (yrs)*/
    String ModelName;
    double[] Main_Field_Coeff_G; /* C - Gauss coefficients of main geomagnetic model (nT) Index is (n * (n + 1) / 2 + m) */
    double[] Main_Field_Coeff_H; /* C - Gauss coefficients of main geomagnetic model (nT) */
    double[] Secular_Var_Coeff_G; /* CD - Gauss coefficients of secular geomagnetic model (nT/yr) */
    double[] Secular_Var_Coeff_H; /* CD - Gauss coefficients of secular geomagnetic model (nT/yr) */
    int nMax; /* Maximum degree of spherical harmonic model */
    int nMaxSecVar; /* Maximum degree of spherical harmonic secular model */
    boolean SecularVariationUsed; /* Whether or not the magnetic secular variation vector will be needed by program*/
    double CoefficientFileEndDate;

    /**
     *
     * @param NumTerms Total number of spherical harmonic coefficients in the model
     */
    MagneticModel(int NumTerms) {

        this.CoefficientFileEndDate = 0;
        this.EditionDate = 0;
        this.ModelName = "";
        this.SecularVariationUsed = false;
        this.epoch = 0;
        this.nMax = 0;
        this.nMaxSecVar = 0;
        this.Main_Field_Coeff_G = new double[NumTerms+1];
        this.Main_Field_Coeff_H = new double[NumTerms+1];
        this.Secular_Var_Coeff_G = new double[NumTerms+1];
        this.Secular_Var_Coeff_H = new double[NumTerms+1];

        for (int i = 0; i < NumTerms; i++) {
            this.Main_Field_Coeff_G[i] = 0;
            this.Main_Field_Coeff_H[i] = 0;
            this.Secular_Var_Coeff_G[i] = 0;
            this.Secular_Var_Coeff_H[i] = 0;
        }

    }

    /**
     * Time change the Model coefficients from the base year of the model using secular variation coefficients.
     * @param userDate
     * @return the Magnetic Model adjusted.
     */
    MagneticModel timelyModify(MagneticDate userDate) {
        MagneticModel timedMagneticModel = new MagneticModel(Main_Field_Coeff_G.length-1);
        timedMagneticModel.EditionDate = this.EditionDate;
        timedMagneticModel.epoch = this.epoch;
        timedMagneticModel.nMax = this.nMax;
        timedMagneticModel.nMaxSecVar = this.nMaxSecVar;
        final int a = timedMagneticModel.nMaxSecVar;
        final int b = (a * (a + 1) / 2 + a);
        timedMagneticModel.ModelName = this.ModelName;
        for(int n = 1; n <= this.nMax; n++)
        {
            for(int m = 0; m <= n; m++)
            {
                final int index = (n * (n + 1) / 2 + m);
                if(index <= b)
                {
                    timedMagneticModel.Main_Field_Coeff_H[index] = this.Main_Field_Coeff_H[index] + (userDate.DecimalYear - this.epoch) * this.Secular_Var_Coeff_H[index];
                    timedMagneticModel.Main_Field_Coeff_G[index] = this.Main_Field_Coeff_G[index] + (userDate.DecimalYear - this.epoch) * this.Secular_Var_Coeff_G[index];
                    timedMagneticModel.Secular_Var_Coeff_H[index] = this.Secular_Var_Coeff_H[index]; /* We need a copy of the secular var coef to calculate secular change */
                    timedMagneticModel.Secular_Var_Coeff_G[index] = this.Secular_Var_Coeff_G[index];
                } else
                {
                    timedMagneticModel.Main_Field_Coeff_H[index] = this.Main_Field_Coeff_H[index];
                    timedMagneticModel.Main_Field_Coeff_G[index] = this.Main_Field_Coeff_G[index];
                }
            }
        }
        return timedMagneticModel;
    }




}
