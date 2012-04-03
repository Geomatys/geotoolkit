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
package org.geotoolkit.referencing.operation.provider;

import java.util.Map;
import java.util.HashMap;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.parameter.MatrixParameterDescriptors;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;
import org.geotoolkit.internal.referencing.MathTransformDecorator;
import static org.geotoolkit.parameter.MatrixParameterDescriptors.DEFAULT_MATRIX_SIZE;


/**
 * The provider for "<cite>Affine general parametric transformation</cite>" (EPSG:9624).
 * This is a special case of projective transforms. The OGC's name is {@code "Affine"}. The
 * default matrix size is
 * {@value org.geotoolkit.parameter.MatrixParameterDescriptors#DEFAULT_MATRIX_SIZE}&times;{@value
 * org.geotoolkit.parameter.MatrixParameterDescriptors#DEFAULT_MATRIX_SIZE}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see ProjectiveTransform
 * @see org.geotoolkit.referencing.operation.transform.AffineTransform2D
 * @see org.geotoolkit.referencing.operation.transform.LinearTransform1D
 *
 * @since 2.0
 * @module
 */
@Immutable
public class Affine extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 649555815622129472L;

    /**
     * The set of predefined providers.
     */
    private static final Affine[] methods = new Affine[8];

    /**
     * The parameters group.
     *
     * @todo We should register EPSG parameter identifiers (A0, A1, A2, B0, B1, B2) as well.
     */
    public static final ParameterDescriptorGroup PARAMETERS;
    static {
        final NamedIdentifier name = new NamedIdentifier(Citations.OGC, "Affine");
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY,        name);
        properties.put(IDENTIFIERS_KEY, new IdentifierCode(Citations.EPSG, 9624));
        properties.put(ALIAS_KEY, new ReferenceIdentifier[] {
            name,
            new NamedIdentifier(Citations.EPSG, "Affine parametric transformation"),
            new NamedIdentifier(Citations.GEOTOOLKIT,
                    Vocabulary.formatInternational(Vocabulary.Keys.AFFINE_TRANSFORM))
        });
        PARAMETERS = new MatrixParameterDescriptors(properties);
    }

    /**
     * Creates a provider for affine transform with a default matrix size.
     */
    public Affine() {
        this(DEFAULT_MATRIX_SIZE-1, DEFAULT_MATRIX_SIZE-1);
        methods[DEFAULT_MATRIX_SIZE-2] = this;
    }

    /**
     * Creates a provider for affine transform with the specified dimensions.
     */
    private Affine(final int sourceDimension, final int targetDimension) {
        super(sourceDimension, targetDimension, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Conversion> getOperationType() {
        return Conversion.class;
    }

    /**
     * Creates a projective transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final Matrix matrix = ((MatrixParameterDescriptors) getParameters()).getMatrix(values);
        final MathTransform transform = MathTransforms.linear(matrix);
        final Affine provider = getProvider(transform.getSourceDimensions(),
                                            transform.getTargetDimensions());
        return new MathTransformDecorator(transform, provider);
    }

    /**
     * Returns the operation method for the specified source and target dimensions.
     * This method provides different methods for different matrix sizes.
     *
     * @param sourceDimension The number of source dimensions.
     * @param targetDimension The number of target dimensions.
     * @return The provider for transforms of the given source and target dimensions.
     */
    public static Affine getProvider(final int sourceDimension, final int targetDimension) {
        if (sourceDimension == targetDimension) {
            final int i = sourceDimension - 1;
            if (i >= 0 && i < methods.length) {
                synchronized (Affine.class) {
                    Affine method = methods[i];
                    if (method == null) {
                        methods[i] = method = new Affine(sourceDimension, targetDimension);
                    }
                    return method;
                }
            }
        }
        return new Affine(sourceDimension, targetDimension);
    }
}
