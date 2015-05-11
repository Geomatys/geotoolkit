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

import org.geotoolkit.image.jai.SilhouetteMask;
import static org.geotoolkit.image.jai.SilhouetteMask.OPERATION_NAME;


/**
 * The descriptor for the {@link SilhouetteMask} operation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class SilhouetteMaskDescriptor extends OperationDescriptorImpl {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1198620343701127505L;

    /**
     * Constructs the descriptor.
     */
    public SilhouetteMaskDescriptor() {
        super(new String[][]{{"GlobalName",  OPERATION_NAME},
                             {"LocalName",   OPERATION_NAME},
                             {"Vendor",      "org.geotoolkit"},
                             {"Description", "Creates the silhouette of an image that has been rotated."},
                             {"DocURL",      "http://www.geotoolkit.org/"}, // TODO: provides more accurate URL
                             {"Version",     "1.0"},
                             {"arg0Desc",    "The sample values that were used as the " +
                                             "background of the source image."}},
              new String[]   {RenderedRegistryMode.MODE_NAME}, 1,
              new String[]   {"background"},     // Argument names
              new Class<?>[] {double[][].class}, // Argument classes
              new Object[]   {new double[1][1]}, // Default values for parameters
              null                               // Valid range for parameters
        );
    }
}
