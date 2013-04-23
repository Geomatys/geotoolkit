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

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.registry.RenderedRegistryMode;
import net.jcip.annotations.Immutable;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;
import org.geotoolkit.image.jai.Combine;

import static org.geotoolkit.image.jai.Combine.Transform;
import static org.geotoolkit.image.jai.Combine.OPERATION_NAME;


/**
 * The operation descriptor for the {@link Combine} operation. While this descriptor declares
 * to support 0 {@link RenderedImage} sources, an arbitrary amount of sources can really be
 * specified. The "0" should be understood as the <em>minimal</em> number of sources required.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Immutable
public final class CombineDescriptor extends OperationDescriptorImpl {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 617861534162611411L;

    /**
     * Constructs the descriptor.
     */
    public CombineDescriptor() {
        super(new String[][]{{"GlobalName",  OPERATION_NAME},
                             {"LocalName",   OPERATION_NAME},
                             {"Vendor",      "org.geotoolkit"},
                             {"Description", "Combine rendered images using a linear relation."},
                             {"DocURL",      "http://www.geotoolkit.org/"}, // TODO: provides more accurate URL
                             {"Version",     "1.0"},
                             {"arg0Desc",    "The coefficients for linear combination as a matrix."},
                             {"arg1Desc",    "An optional transform to apply on sample values "+
                                             "before the linear combination."}},
              new String[]   {RenderedRegistryMode.MODE_NAME}, 0,    // Supported modes
              new String[]   {"matrix", "transform"},                // Parameter names
              new Class<?>[] {double[][].class, Transform.class},    // Parameter classes
              new Object[]   {NO_PARAMETER_DEFAULT, null},           // Default value
              null                                                   // Valid parameter values
        );
    }

    /**
     * Returns {@code true} if this operation supports the specified mode, and
     * is capable of handling the given input source(s) for the specified mode.
     *
     * @param modeName The mode name (usually "Rendered").
     * @param param The parameter block for the operation to performs.
     * @param message A buffer for formatting an error message if any.
     * @return {@code true} if this operation can handle the given input sources.
     */
    @Override
    protected boolean validateSources(final String      modeName,
                                      final ParameterBlock param,
                                      final StringBuffer message)
    {
        if (super.validateSources(modeName, param, message)) {
            for (int i=param.getNumSources(); --i>=0;) {
                final Object source = param.getSource(i);
                if (!(source instanceof RenderedImage)) {
                    message.append(Errors.format(Errors.Keys.ILLEGAL_PARAMETER_TYPE_2,
                            "source"+i, Classes.getClass(source)));
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns {@code true} if the parameters are valids. This implementation checks
     * that the number of bands in the source src1 is equal to the number of bands of
     * source src2.
     *
     * @param modeName The mode name (usually "Rendered").
     * @param param The parameter block for the operation to performs.
     * @param message A buffer for formatting an error message if any.
     * @return {@code true} if the parameters are valid.
     */
    @Override
    protected boolean validateParameters(final String      modeName,
                                         final ParameterBlock param,
                                         final StringBuffer message)
    {
        if (!super.validateParameters(modeName, param, message))  {
            return false;
        }
        final double[][] matrix = (double[][]) param.getObjectParameter(0);
        int numSamples = 1; // Begin at '1' for the offset value.
        for (int i=param.getNumSources(); --i>=0;) {
            numSamples += ((RenderedImage) param.getSource(i)).getSampleModel().getNumBands();
        }
        for (int i=0; i<matrix.length; i++) {
            if (matrix[i].length != numSamples) {
                message.append(Errors.format(Errors.Keys.UNEXPECTED_ROW_LENGTH_3,
                        i, matrix[i].length, numSamples));
                return false;
            }
        }
        return true;
    }
}
