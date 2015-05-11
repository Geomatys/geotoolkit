/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import java.net.URL;
import java.io.File;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.parameter.Parameters;

import static org.geotoolkit.referencing.operation.provider.NADCON.*;


/**
 * The NADCON transform (EPSG code 9613). See
 * <a href="http://www.ngs.noaa.gov/TOOLS/Nadcon/Nadcon.html">North American Datum Conversion Utility</a>
 * for an overview. See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.NADCON}</li>
 * </ul>
 *
 *
 * {@section Description}
 *
 * NADCON is a two dimensional datum shift method, created by the National Geodetic Survey (NGS),
 * that uses interpolated values from two grid shift files. This method is used to transform NAD27
 * (EPSG:4267) datum coordinates (and some others) to NAD83 (EPSG:4269) within the United States.
 * <p>
 * There are two set of grid shift files: NADCON and High Accuracy Reference Networks (HARN).
 * NADCON shifts from NAD27 (and some others) to NAD83 while HARN shifts from the NADCON NAD83
 * to an improved NAD83. Both sets of grid shift files may be downloaded from
 * <a href="http://www.ngs.noaa.gov/PC_PROD/NADCON/">www.ngs.noaa.gov/PC_PROD/NADCON/</a>.
 * The <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module
 * can download the data and install them in a directory where they can be find automatically.
 * <p>
 * Some of the NADCON grids, their areas of use, and source datums are shown
 * in the following table. The accuracy column is in metres at 67% confidence.
 * <p>
 * <table border="1">
 *   <tr><th>File Name</th><th>Area</th><th>Source Datum</th><th>Accuracy</th></tr>
 *   <tr><td>CONUS</td><td>Conterminous U S (lower 48 states)</td><td>NAD27</td><td>0.15</td></tr>
 *   <tr><td>ALASKA</td><td>Alaska, incl. Aleutian Islands</td><td>NAD27</td><td>0.5</td></tr>
 *   <tr><td>HAWAII</td><td>Hawaiian Islands</td><td>Old Hawaiian (4135)</td><td>0.2</td></tr>
 *   <tr><td>STLRNC</td><td>St. Lawrence Is., AK</td><td>St. Lawrence Island (4136)</td><td>-</td></tr>
 *   <tr><td>STPAUL </td><td>St. Paul Is., AK</td><td>St. Paul Island (4137)</td><td>-</td></tr>
 *   <tr><td>STGEORGE</td><td>St. George Is., AK</td><td>St. George Island (4138)</td><td>-</td></tr>
 *   <tr><td>PRVI</td><td>Puerto Rico and the Virgin Islands</td><td>Puerto Rico (4139)</td><td>0.05</td></tr>
 * </table>
 * <p>
 * The grid names to use for transforming are parameters of this {@code MathTransform}. Those
 * parameters may be the full name and path to the grids or just the name of the grids if the
 * default location of the grids was set as a preference. This preference may be set with the
 * above-cited <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module.
 * <p>
 * Transformations here have been tested to be within 0.00001 seconds of values given by the
 * <cite>NGS ndcon210</cite> program for NADCON grids. American Samoa and HARN shifts have not
 * yet been tested.
 *
 *
 * {@section References}
 *
 * <ul>
 *   <li><a href="http://www.ngs.noaa.gov/PC_PROD/NADCON/Readme.htm">NADCON Readme</a></li>
 *   <li>American Samoa Grids for NADCON - Samoa Readme.txt</li>
 *   <li><a href="http://www.ngs.noaa.gov/PUBS_LIB/NGS50.pdf">NADCON - The
 *       Application of Minimum-Curvature-Derived Surfaces in the Transformation of
 *       Positional Data From the North American Datum of 1927 to the North
 *       American Datum of 1983</a> - NOAA TM.</li>
 *   <li>{@code ndcon210.for} - NGS Fortran source code for NADCON conversions. See the
 *       following subroutines: TRANSF, TO83, FGRID, INTRP, COEFF and SURF</li>
 *   <li>{@code nadgrd.for} - NGS Fortran source code to export/import binary and text grid
 *       formats</li>
 *   <li>EPSG Geodesy Parameters database</li>
 * </ul>
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
public class NadconTransform extends GridTransform2D {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4707304160205218546L;

    /**
     * Latitude grid shift file names. This is saved for formatting parameters in WKT.
     */
    private final String latitudeGridFile;

    /**
     * Longitude grid shift file names. This is saved for formatting parameters in WKT.
     */
    private final String longitudeGridFile;

    /**
     * Constructs a grid from the specified shift files. The arguments may be file paths and names,
     * or just file names. They are resolved as below:
     * <p>
     * <ul>
     *   <li>If an argument is a {@linkplain URL} or {@linkplain File#isAbsolute absolute file},
     *       then it is used directly.</li>
     *
     *   <li>Otherwise if an argument is a single filename with no
     *       {@linkplain File#getParent parent directory}, and if the file is found in a
     *       {@code org/geotoolkit/referencing/operation/transform/NADCON} directory on the
     *       classpath, then that later file is used.</li>
     *
     *   <li>Otherwise (i.e. the file is relative and not presents on the classpath), prepend
     *       the grid location set by the user, if any. This location can be set by the
     *       <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module,
     *       which can also download and install the data.</li>
     * </ul>
     *
     * @param  longitudeGridFile Path (optional) and name to the longitude difference file.
     *         This will have a {@code ".los"} or {@code ".loa"} file extention.
     * @param  latitudeGridFile Path (optional) and name to the latitude difference file.
     *         This will have a {@code ".las"} or {@code ".laa"} file extention.
     * @throws FactoryException If there is an error reading the grid files.
     */
    public NadconTransform(String longitudeGridFile, String latitudeGridFile) throws FactoryException {
        this(NadconLoader.loadIfAbsent(longitudeGridFile, latitudeGridFile));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private NadconTransform(final NadconLoader loader) {
        super(GridType.NADCON, loader.getDataBuffer(), loader.getSize(), loader.getArea());
        latitudeGridFile  = (String) loader.latitudeGridFile;
        longitudeGridFile = (String) loader.longitudeGridFile;
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return PARAMETERS;
    }

    /**
     * Returns the parameter values for this math transform.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        final ParameterValueGroup parameters = getParameterDescriptors().createValue();
        Parameters.getOrCreate(LAT_DIFF_FILE,  parameters).setValue(latitudeGridFile);
        Parameters.getOrCreate(LONG_DIFF_FILE, parameters).setValue(longitudeGridFile);
        return parameters;
    }
}
