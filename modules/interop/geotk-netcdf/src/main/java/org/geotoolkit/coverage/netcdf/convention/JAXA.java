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
package org.geotoolkit.coverage.netcdf.convention;

import org.apache.sis.internal.netcdf.Convention;
import org.apache.sis.internal.netcdf.Decoder;
import org.apache.sis.internal.netcdf.Variable;
import org.apache.sis.referencing.operation.transform.TransferFunction;

import ucar.nc2.constants.ACDD;


/**
 * Conventions for GCOM-C files produced by JAXA.
 *
 * @author Alexis Manin (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 */
public final class JAXA extends Convention {
    private static final String SATELLITE_KEY = "Satellite";
    private static final String SATELLITE_VAL = "Global Change Observation Mission - Climate (GCOM-C)";

    @Override
    protected boolean isApplicableTo(Decoder decoder) {
        return SATELLITE_VAL.equals(decoder.stringValue(SATELLITE_KEY));
    }

    @Override
    public String[] getSearchPath() {
        return new String[]{"Global_attributes", "Level_1_attributes", "Processing_attributes"};
    }

    @Override
    public String mapAttributeName(final String name) {
        switch (name) {
            case ACDD.creator_name:     return "Algorithm_developer";
            case ACDD.TIME_START:       return "Scene_start_time";
            case ACDD.TIME_END:         return "Scene_end_time";
            case ACDD.summary:          return "Dataset_description";
            case ACDD.title:            return "Product_name";
            case ACDD.processing_level: return "Product_level";
            case "instrument":          return "Sensor";
        }
        return name;
    }

    @Override
    public String nameOfDimension(final Variable dataOrAxis, final int index) {
        String n = super.nameOfDimension(dataOrAxis, index);
        if ("Piexl grids".equals(n)) {
            n = "Pixel grids";
        }
        return n;
    }

    @Override
    public TransferFunction transferFunction(final Variable source) {
        final TransferFunction tr = super.transferFunction(source);
        final double slope  = source.getAttributeAsNumber("Slope");
        final double offset = source.getAttributeAsNumber("Offset");
        if (Double.isFinite(slope))  tr.setOffset(slope);
        if (Double.isFinite(offset)) tr.setOffset(offset);
        return tr;
    }
}
