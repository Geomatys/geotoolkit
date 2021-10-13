/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.image.jai.Mask;
import static org.geotoolkit.image.jai.Mask.OPERATION_NAME;


/**
 * The descriptor for the {@link Mask} operation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class MaskDescriptor extends OperationDescriptorImpl {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2672710758846673474L;

    /**
     * Constructs the descriptor.
     */
    public MaskDescriptor() {
        super(new String[][]{{"GlobalName",  OPERATION_NAME},
                             {"LocalName",   OPERATION_NAME},
                             {"Vendor",      "org.geotoolkit"},
                             {"Description", "Applies a mask on an image."},
                             {"DocURL",      "http://www.geotoolkit.org/"}, // TODO: provides more accurate URL
                             {"Version",     "1.0"},
                             {"arg0Desc",    "The values to copy to the destination images for every " +
                                             "masked pixels,or null for using the mask values."}},
              new String[]   {RenderedRegistryMode.MODE_NAME}, 1,
              new String[]   {"newValues"},      // Argument names
              new Class<?>[] {double[].class},   // Argument classes
              new Object[]   {null},             // Default values for parameters
              null                               // Valid range for parameters
        );
    }
}
