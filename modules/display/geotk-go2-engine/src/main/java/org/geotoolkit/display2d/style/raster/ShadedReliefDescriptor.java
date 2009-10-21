/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.style.raster;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.registry.RenderedRegistryMode;

import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Errors;

/**
 * Description of the shading operation.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShadedReliefDescriptor extends OperationDescriptorImpl {
    
    /**
     * The operation name, which is {@value}.
     */
    public static final String OPERATION_NAME = "ShadedRelief";

    /**
     * Constructs the descriptor.
     */
    public ShadedReliefDescriptor() {
        super(new String[][]{{"GlobalName",  OPERATION_NAME},
                             {"LocalName",   OPERATION_NAME},
                             {"Vendor",      "org.geotidy"},
                             {"Description", "Make a hill shading image"},
                             {"DocURL",      "http://geotidy.geomatys.fr"},
                             {"Version",     "1.0"},
                             {"arg0Desc",    "The azimuth value, this is the direction of lighting in deg (default 315)"},
                             {"arg1Desc",    "The altitude value, this is the altitude of the lighting source in degrees above horizontal (default 45)"},
                             {"arg2Desc",    "The zfactor value, this is the DEM altitude scaling z-factor (default 1)"}
                            },
              new String[]   {RenderedRegistryMode.MODE_NAME}, 0,        // Supported modes
              new String[]   {"azimuth", "altitude","zfactor"},          // Parameter names
              new Class []   {double.class, double.class, double.class}, // Parameter classes
              new Object[]   {null, null,null},                          // Default value
              null                                                       // Valid parameter values
        );
    }

    /**
     * Returns {@code true} if this operation supports the specified mode, and
     * is capable of handling the given input source(s) for the specified mode.
     *
     * @param modeName The mode name (usually "Rendered").
     * @param param The parameter block for the operation to performs.
     * @param message A buffer for formatting an error message if any.
     */
    @Override
    protected boolean validateSources(final String      modeName,
                                      final ParameterBlock param,
                                      final StringBuffer message)
    {
        if (super.validateSources(modeName, param, message)) {
            if(param.getNumSources() != 1) return false;

            final Object source = param.getSource(0);
            if (!(source instanceof RenderedImage)) {
                message.append(Errors.format(Errors.Keys.BAD_PARAMETER_TYPE_$2,
                               "source"+0, Classes.getClass(source)));
                return false;
            }
            
            return true;
        }
        return false;
    }

    /**
     * Returns {@code true} if the parameters are valids.
     *
     * @param modeName The mode name (usually "Rendered").
     * @param param The parameter block for the operation to performs.
     * @param message A buffer for formatting an error message if any.
     */
    @Override
    protected boolean validateParameters(final String      modeName,
                                         final ParameterBlock param,
                                         final StringBuffer message)
    {
        if (!super.validateParameters(modeName, param, message))  {
            return false;
        }
        return true;
    }
}
