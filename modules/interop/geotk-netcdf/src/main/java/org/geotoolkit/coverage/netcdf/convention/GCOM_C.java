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
import java.util.HashMap;
import org.apache.sis.storage.netcdf.AttributeNames;
import org.apache.sis.internal.netcdf.Convention;
import org.apache.sis.internal.netcdf.Decoder;
import org.apache.sis.internal.netcdf.Variable;
import org.apache.sis.referencing.operation.transform.TransferFunction;
import org.apache.sis.measure.NumberRange;


/**
 * Customization of Apache SIS netCDF reader for conventions used in GCOM-C files produced by JAXA.
 *
 * @author Alexis Manin (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 */
public final class GCOM_C extends Convention {
    /**
     * Name of the attribute used as a sentinel value.
     */
    private static final String SATELLITE_KEY = "Satellite";

    /**
     * Value of the {@value #SATELLITE_KEY} attribute for considering a netCDF file
     * as a product of this convention.
     */
    private static final String SATELLITE_VAL = "Global Change Observation Mission - Climate (GCOM-C)";

    /**
     * Mapping from ACDD or CF-Convention attribute names to names of attributes used by this convention.
     */
    private static final Map<String,String> ATTRIBUTES;
    static {
        final Map<String,String> m = new HashMap<>();
        m.put(AttributeNames.TITLE,               "Product_name");             // identification­Info / citation / title
        m.put(AttributeNames.PRODUCT_VERSION,     "Product_version");          // identification­Info / citation / edition
        m.put(AttributeNames.IDENTIFIER.TEXT,     "Product_file_name");        // identification­Info / citation / identifier / code
        m.put(AttributeNames.DATE_CREATED,        "Processing_UT");            // identification­Info / citation / date
        m.put(AttributeNames.CREATOR.INSTITUTION, "Processing_organization");  // identification­Info / citation / citedResponsibleParty
        m.put(AttributeNames.SUMMARY,             "Dataset_description");      // identification­Info / abstract
        m.put(AttributeNames.PLATFORM.TEXT,       "Satellite");                // acquisition­Information / platform / identifier
        m.put(AttributeNames.INSTRUMENT.TEXT,     "Sensor");                   // acquisition­Information / platform / instrument / identifier
        m.put(AttributeNames.PROCESSING_LEVEL,    "Product_level");            // content­Info / processing­Level­Code
        m.put(AttributeNames.SOURCE,              "Input_files");              // data­Quality­Info / lineage / source / description
        m.put(AttributeNames.TIME.MINIMUM,        "Scene_start_time");         // identification­Info / extent / temporal­Element / extent
        m.put(AttributeNames.TIME.MAXIMUM,        "Scene_end_time");           // identification­Info / extent / temporal­Element / extent
        ATTRIBUTES = m;
    }

    /**
     * Names of attributes for sample values having "no-data" meaning.
     */
    private static final String[] NO_DATA = {
        "Error_DN",
        "Land_DN",
        "Cloud_error_DN",
        "Retrieval_error_DN"
    };

    /**
     * Suffix of all attribute names enumerated in {@link #NO_DATA}.
     */
    private static final String SUFFIX = "_DN";

    /**
     * Creates a new instance of Apache SIS netCDF reader customization for JAXA files.
     */
    public void JAXA() {
    }

    /**
     * Detects if this set of conventions applies to the given netCDF file.
     * This method shall not change the state of the given {@link Decoder}.
     *
     * @param  decoder  the netCDF file to test.
     * @return {@code true} if this set of conventions can apply.
     */
    @Override
    protected boolean isApplicableTo(final Decoder decoder) {
        final String[] path = decoder.getSearchPath();
        decoder.setSearchPath("Global_attributes");
        final boolean r = SATELLITE_VAL.equals(decoder.stringValue(SATELLITE_KEY));
        decoder.setSearchPath(path);
        return r;
    }

    /**
     * Specifies a list of groups where to search for named attributes, in preference order.
     * The {@code null} name stands for the root group.
     *
     * @return  name of groups where to search in for global attributes, in preference order.
     */
    @Override
    public String[] getSearchPath() {
        return new String[] {"Global_attributes", null, "Processing_attributes"};
    }

    /**
     * Returns the name of an attribute in this convention which is equivalent to the attribute of given name in CF-convention.
     * The given parameter is a name from <cite>CF conventions</cite> or from <cite>Attribute Convention for Dataset Discovery
     * (ACDD)</cite>. Some of those attribute names are listed in the {@link org.apache.sis.storage.netcdf.AttributeNames} class.
     *
     * @param  name  an attribute name from CF or ACDD convention.
     * @return the attribute name expected to be found in a netCDF file structured according this {@code Convention}.
     *         If this convention does not know about attribute of the given name, then {@code name} is returned unchanged.
     */
    @Override
    public String mapAttributeName(final String name) {
        return ATTRIBUTES.getOrDefault(name, name);
    }

    /**
     * Returns the attribute-specified name of the dimension at the given index, or {@code null} if unspecified.
     * See {@link Convention#nameOfDimension(Variable, int)} for a more detailed explanation of this information.
     * The implementation in this class fixes a typo found in some {@code ":Dim1"} attribute values and generates
     * the values in a variable where they are missing.
     *
     * @param  dataOrAxis  the variable for which to get the attribute-specified name of the dimension.
     * @param  index       zero-based index of the dimension for which to get the name.
     * @return dimension name as specified by attributes, or {@code null} if none.
     */
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

    /**
     * Returns the range of valid values, or {@code null} if unknown.
     *
     * @param  data  the variable to get valid range of values for.
     *               This is usually a variable containing raster data.
     * @return the range of valid values, or {@code null} if unknown.
     */
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

    /**
     * Returns all no-data values declared for the given variable, or an empty map if none.
     * The map keys are the no-data values (pad sample values or missing sample values).
     * The map values are {@link String} instances containing the description of the no-data value.
     *
     * @param  data  the variable for which to get no-data values.
     * @return no-data values with textual descriptions.
     */
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

    /**
     * Builds the function converting values from their packed formats in the variable to "real" values.
     * This method is invoked only if {@link #validRange(Variable)} returned a non-null value.
     *
     * @param  data  the variable from which to determine the transfer function.
     * @return a transfer function built from the attributes defined in the given variable.
     */
    @Override
    public TransferFunction transferFunction(final Variable data) {
        final TransferFunction tr = super.transferFunction(data);
        if (tr.isIdentity()) {
            final double slope  = data.getAttributeAsNumber("Slope");
            final double offset = data.getAttributeAsNumber("Offset");
            if (Double.isFinite(slope))  tr.setScale (slope);
            if (Double.isFinite(offset)) tr.setOffset(offset);
        }
        return tr;
    }
}
