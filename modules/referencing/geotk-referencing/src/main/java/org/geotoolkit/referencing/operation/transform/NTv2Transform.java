/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.parameter.Parameters;
import static org.geotoolkit.referencing.operation.provider.NTv2.*;


/**
 * Transform based on a NTv2 grid (EPSG code 9615).
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.NTv2}</li>
 * </ul>
 *
 *
 * {@section Description}
 *
 * This is a geodetic transformation simular to {@link NadconTransform}, operating on geographic
 * coordinate differences by bi-linear interpolation.
 *
 * {@note The original NTv2 grids expect longitudes to be positive west. However this class
 *        performs the sign reversal by itself, so inputs given to <code>NTv2Transform</code>
 *        have longitudes positive east.}
 *
 * @author Simon Reynard (Geomatys)
 * @version 3.12
 *
 * @see <a href="http://www.killetsoft.de/p_trdn_e.htm">List of some NTv2 files</a>
 *
 * @since 3.12
 * @module
 */
public class NTv2Transform extends GridTransform2D {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -1351957989631930381L;

    /**
     * The file name of RGF93 grid, which is {@value}. This grid can be downloaded from
     * <a href="http://lambert93.ign.fr/index.php?id=30">http://lambert93.ign.fr/</a>.
     */
    public static final String RGF93 = "ntf_r93.gsb";

    /**
     * Latitude/longitude grid shift file name.
     */
    private final String gridFile;

    /**
     * Constructs a grid from the specified shift file. The argument may be a file path and name,
     * or just the file name. It is resolved as below:
     * <p>
     * <ul>
     *   <li>If the argument is a {@linkplain URL} or {@linkplain File#isAbsolute absolute file},
     *       then it is used directly.</li>
     *
     *   <li>Otherwise if the argument is a single filename with no
     *       {@linkplain File#getParent parent directory}, and if the file is found in a
     *       {@code org/geotoolkit/referencing/operation/transform/NTv2} directory on the
     *       classpath, then that later file is used.</li>
     *
     *   <li>Otherwise (i.e. the file is relative and not presents on the classpath), prepend
     *       the grid location set by the user, if any. This location can be set by the
     *       <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module,
     *       which can also download and install the data.</li>
     * </ul>
     *
     * @param  gridFile Path (optional) and name to the longitude difference file.
     *         This will typically have a {@code ".gsb"} file extention.
     * @throws FactoryException If there is an error reading the grid file.
     */
    public NTv2Transform(final String gridFile) throws FactoryException {
        this(NTv2Loader.loadIfAbsent(gridFile, false));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private NTv2Transform(final NTv2Loader loader) throws FactoryException {
        super(GridType.NTv2, loader.getDataBuffer(), loader.getSize(), loader.getArea());
        final String units = loader.getString("GS_TYPE");
        if (!"SECONDS".equals(units)) {
            throw new FactoryException(Errors.format(Errors.Keys.ILLEGAL_PARAMETER_VALUE_2, "GS_TYPE", units));
        }
        gridFile  = (String) loader.latitudeGridFile;
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
        Parameters.getOrCreate(DIFFERENCE_FILE, parameters).setValue(gridFile);
        return parameters;
    }
}
