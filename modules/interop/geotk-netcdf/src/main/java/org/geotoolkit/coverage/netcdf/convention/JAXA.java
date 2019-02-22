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

import java.util.Map;
import org.apache.sis.internal.netcdf.Convention;
import org.apache.sis.internal.netcdf.Decoder;
import org.apache.sis.internal.netcdf.Variable;
import org.apache.sis.referencing.operation.transform.TransferFunction;
import org.apache.sis.measure.NumberRange;

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

    private static final String[] NO_DATA = {
        "Error_DN",
        "Land_DN",
        "Cloud_error_DN",
        "Retrieval_error_DN"
    };

    private static final String SUFFIX = "_DN";

    @Override
    protected boolean isApplicableTo(final Decoder decoder) {
        final String[] path = decoder.getSearchPath();
        decoder.setSearchPath(getSearchPath());
        final boolean r = SATELLITE_VAL.equals(decoder.stringValue(SATELLITE_KEY));
        decoder.setSearchPath(path);
        return r;
    }

    @Override
    public String[] getSearchPath() {
        return new String[] {"Global_attributes", "Level_1_attributes", "Processing_attributes"};
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
        if (n == null) {
            if ("QA_flag".equals(dataOrAxis.getName())) {
                switch (index) {
                    case 0: n = "Line grids";  break;
                    case 1: n = "Pixel grids"; break;
                }
            }
        } else if ("Piexl grids".equals(n)) {
            n = "Pixel grids";
        }
        return n;
    }

    @Override
    public NumberRange<?> validRange(final Variable data) {
        NumberRange<?> range = super.validRange(data);
        if (range == null) {
            final double min = data.getAttributeAsNumber("Minimum_valid_DN");
            final double max = data.getAttributeAsNumber("Maximum_valid_DN");
            if (Double.isFinite(min) && Double.isFinite(max)) {
                range = NumberRange.createBestFit(min, true, max, true);
            }
        }
        return range;
    }

    @Override
    public Map<Number,Object> nodataValues(final Variable data) {
        final Map<Number, Object> pads = super.nodataValues(data);
        for (String name : NO_DATA) {
            final double value = data.getAttributeAsNumber(name);
            if (Double.isFinite(value)) {
                if (name.endsWith(SUFFIX)) {
                    name = name.substring(0, name.length() - SUFFIX.length());
                }
                pads.put(value, name.replace('_', ' '));
            }
        }
        return pads;
    }

    @Override
    public TransferFunction transferFunction(final Variable source) {
        final TransferFunction tr = super.transferFunction(source);
        if (tr.isIdentity()) {
            final double slope  = source.getAttributeAsNumber("Slope");
            final double offset = source.getAttributeAsNumber("Offset");
            if (Double.isFinite(slope))  tr.setScale (slope);
            if (Double.isFinite(offset)) tr.setOffset(offset);
        }
        return tr;
    }
}
