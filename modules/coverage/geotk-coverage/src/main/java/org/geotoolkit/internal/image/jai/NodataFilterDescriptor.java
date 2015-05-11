/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.jai;

import javax.media.jai.util.Range;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.registry.RenderedRegistryMode;

import org.geotoolkit.image.jai.NodataFilter;
import static org.geotoolkit.image.jai.NodataFilter.OPERATION_NAME;


/**
 * The descriptor for the {@link NodataFilter} operation.
 *
 * @author Lionel Flahaut (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public final class NodataFilterDescriptor extends OperationDescriptorImpl {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7468804845301237899L;

    /**
     * The range of valid parameter values.
     */
    private static final Range ARGUMENT_RANGE = new Range(Integer.class, Integer.valueOf(0), null);

    /**
     * Constructs the descriptor.
     */
    public NodataFilterDescriptor() {
        super(new String[][]{{"GlobalName",  OPERATION_NAME},
                             {"LocalName",   OPERATION_NAME},
                             {"Vendor",      "org.geotoolkit"},
                             {"Description", "Replace NaN values by a weighted average of neighbor values."},
                             {"DocURL",      "http://www.geotoolkit.org/"}, // TODO: provides more accurate URL
                             {"Version",     "1.0"},
                             {"arg0Desc",    "The number of pixel above, below, to the left and " +
                                             "to the right of central NaN pixel."},
                             {"arg1Desc",    "The minimal number of valid neighbors required " +
                                             "in order to consider the average as valid."}},
              new String[]   {RenderedRegistryMode.MODE_NAME}, 1,
              new String[]   {"padding", "validityThreshold"},         // Argument names
              new Class<?>[] {Integer.class,      Integer.class},      // Argument classes
              new Object[]   {Integer.valueOf(1), Integer.valueOf(4)}, // Default values for parameters
              new Range[]    {ARGUMENT_RANGE, ARGUMENT_RANGE}          // Valid range for parameters
        );
    }
}
