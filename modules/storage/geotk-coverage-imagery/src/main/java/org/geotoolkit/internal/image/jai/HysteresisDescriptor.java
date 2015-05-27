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

import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.registry.RenderedRegistryMode;


import org.geotoolkit.image.jai.Hysteresis;
import static org.geotoolkit.image.jai.Hysteresis.OPERATION_NAME;


/**
 * The descriptor for the {@link Hysteresis} operation.
 *
 * @author Lionel Flahaut (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public final class HysteresisDescriptor extends OperationDescriptorImpl {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5367676679515658447L;

    /**
     * Constructs the descriptor.
     */
    public HysteresisDescriptor() {
        super(new String[][]{{"GlobalName",  OPERATION_NAME},
                             {"LocalName",   OPERATION_NAME},
                             {"Vendor",      "org.geotoolkit"},
                             {"Description", "Thresholding by hysteresis"},
                             {"DocURL",      "http://www.geotoolkit.org/"}, // TODO: provides more accurate URL
                             {"Version",     "1.0"},
                             {"arg0Desc",    "The low threshold value, inclusive."},
                             {"arg1Desc",    "The high threshold value, inclusive."},
                             {"arg2Desc",    "The value to give to filtered pixel."}},
              new String[]   {RenderedRegistryMode.MODE_NAME}, 1,
              new String[]   {"low", "high", "padValue"}, // Argument names
              new Class<?>[] {Double.class, Double.class, Double.class},    // Argument classes
              new Object[]   {NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, 0.0},
              null // No restriction on valid parameter values.
       );
    }
}
