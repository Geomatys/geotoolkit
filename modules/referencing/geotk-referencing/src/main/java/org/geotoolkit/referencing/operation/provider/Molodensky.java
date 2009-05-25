/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Collection;
import javax.measure.unit.SI;

import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;

import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.MolodenskyTransform;
import org.geotoolkit.internal.referencing.MathTransformDecorator;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;

import static java.util.Collections.singletonMap;
import static org.geotoolkit.parameter.Parameters.doubleValue;
import static org.geotoolkit.parameter.Parameters.integerValue;
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptor;
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptorGroup;


/**
 * The provider for "<cite>Molodensky transformation</cite>" (EPSG:9604). This provider constructs
 * transforms from {@linkplain GeographicCRS geographic} to geographic coordinate reference systems,
 * without passing though {@linkplain GeocentricCRS geocentric} one.
 *
 * {@note The EPSG database does not use <code>src_semi_major</code>, <cite>etc.</cite>
 *        parameters and instead uses "<cite>Semi-major axis length difference</cite>"
 *        and "<cite>Flattening difference</cite>".}
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see MolodenskyTransform
 *
 * @since 2.1
 * @module
 */
public class Molodensky extends MathTransformProvider {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 8126525068450868912L;

    /**
     * The default value for source and target geographic dimensions, which is {@value}.
     *
     * {@note If this default value is modified, then the handling of the 3D cases must
     *        be adjusted.}
     */
    static final int DEFAULT_DIMENSION = PositionVector7Param.DEFAULT_DIMENSION;

    /**
     * The operation parameter descriptor for the number of geographic dimension (2 or 3).
     * This argument applies on both the source and the target dimension. The default value
     * is 2.
     */
    public static final ParameterDescriptor<Integer> DIM = DefaultParameterDescriptor.create(
            singletonMap(NAME_KEY, new NamedIdentifier(Citations.OGC, "dim")),
            DEFAULT_DIMENSION, 2, 3, false);

    /**
     * The operation parameter descriptor for the number of source geographic dimension (2 or 3).
     * This is a Geotoolkit-specific argument. The standard parameter is {@link #DIM}, which set
     * both the source and target dimension.
     */
    public static final ParameterDescriptor<Integer> SRC_DIM = PositionVector7Param.SRC_DIM;

    /**
     * The operation parameter descriptor for the number of target geographic dimension (2 or 3).
     * This is a Geotoolkit-specific argument. The standard parameter is {@link #DIM}, which set
     * both the source and target dimension.
     */
    public static final ParameterDescriptor<Integer> TGT_DIM = PositionVector7Param.TGT_DIM;

    /**
     * The operation parameter descriptor for the <cite>X-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dx dx}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> DX = PositionVector7Param.DX;

    /**
     * The operation parameter descriptor for the <cite>Y-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dy dy}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> DY = PositionVector7Param.DY;

    /**
     * The operation parameter descriptor for the <cite>Z-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dz dz}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> DZ = PositionVector7Param.DZ;

    /**
     * The operation parameter descriptor for the {@code "src_semi_major"} parameter value.
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> SRC_SEMI_MAJOR = createDescriptor(
            identifiers(PositionVector7Param.SRC_SEMI_MAJOR),
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the {@code "src_semi_minor"} parameter value.
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> SRC_SEMI_MINOR = createDescriptor(
            identifiers(PositionVector7Param.SRC_SEMI_MINOR),
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the "tgt_semi_major" parameter value.
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> TGT_SEMI_MAJOR = createDescriptor(
            identifiers(PositionVector7Param.TGT_SEMI_MAJOR),
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the "tgt_semi_minor" parameter value.
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> TGT_SEMI_MINOR = createDescriptor(
            identifiers(PositionVector7Param.TGT_SEMI_MINOR),
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * Helper method for parameter descriptor creation.
     */
    private static final NamedIdentifier[] identifiers(final ParameterDescriptor<Double> parameter) {
        final Collection<GenericName> id = parameter.getAlias();
        return id.toArray(new NamedIdentifier[id.size()]);
    }

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,      "Molodenski"),
                new NamedIdentifier(Citations.EPSG,     "Molodensky"),
                new NamedIdentifier(Citations.EPSG,     "9604"),
                new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                    Vocabulary.Keys.MOLODENSKY_TRANSFORM))
            }, new ParameterDescriptor[] {
                DIM, SRC_DIM, TGT_DIM, DX, DY, DZ,
                SRC_SEMI_MAJOR, SRC_SEMI_MINOR,
                TGT_SEMI_MAJOR, TGT_SEMI_MINOR
            });

    /**
     * The providers for all combinaisons between 2D and 3D cases. Array length is 4.
     * Index is build with following rule:
     * <ul>
     *   <li>Bit 1: dimension of source coordinates (0 for 2D, 1 for 3D).</li>
     *   <li>Bit 0: dimension of target coordinates (0 for 2D, 1 for 3D).</li>
     * </ul>
     */
    final Molodensky[] complements;

    /**
     * Returns the index in the {@link #complements} array for the given source and target
     * dimensions, which must be either 2 or 3. This method assumes that the arguments have
     * already been checked for validity.
     */
    private static int index(final int sourceDimension, final int targetDimension) {
        return ((sourceDimension & 1) << 1) | (targetDimension & 1);
    }

    /**
     * Constructs a provider.
     */
    public Molodensky() {
        // Following constructors register themself in the "complements" array.
        this(DEFAULT_DIMENSION, DEFAULT_DIMENSION, PARAMETERS, new Molodensky[4]);
        new Molodensky(DEFAULT_DIMENSION, 3, PARAMETERS, complements);
        new Molodensky(3, DEFAULT_DIMENSION, PARAMETERS, complements);
        new Molodensky(3, 3, PARAMETERS, complements);
    }

    /**
     * Constructs a provider from a set of parameters.
     *
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     * @param parameters      The set of parameters (never {@code null}).
     * @param complements     Providers for all combinaisons between 2D and 3D cases.
     */
    Molodensky(final int sourceDimension, final int targetDimension,
               final ParameterDescriptorGroup parameters, final Molodensky[] complements)
    {
        super(sourceDimension, targetDimension, parameters);
        this.complements = complements;
        final int index = index(sourceDimension, targetDimension);
        if (complements[index] != null) {
            throw new AssertionError(index);
        }
        complements[index] = this;
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Returns the dimension declared in the given parameter value, or 0 if none. If
     * this method returns a non-zero value, then it is garanteed to be either 2 or 3.
     *
     * @param  descriptor The descriptor of the dimension to get.
     * @param  values The values from which to get the dimension.
     * @return The dimension, or 0 if none.
     * @throws InvalidParameterValueException if the dimension parameter has an invalid value.
     */
    private static int dimension(final ParameterDescriptor<Integer> descriptor, final ParameterValueGroup values)
            throws InvalidParameterValueException
    {
        final Integer value = integerValue(descriptor, values);
        if (value == null) {
            return 0;
        }
        final int dimension = value; // Unboxing.
        if (dimension != 2 && dimension != 3) {
            final String name = descriptor.getName().getCode();
            throw new InvalidParameterValueException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, name, dimension), name, dimension);
        }
        return dimension;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        int srcDim = sourceDimension;
        int tgtDim = targetDimension;
        int dimension = dimension(DIM, values);
        if (dimension != 0) {
            srcDim = tgtDim = dimension;
        }
        dimension = dimension(SRC_DIM, values);
        if (dimension != 0) {
            srcDim = dimension;
        }
        dimension = dimension(TGT_DIM, values);
        if (dimension != 0) {
            tgtDim = dimension;
        }
        final double  a = doubleValue(SRC_SEMI_MAJOR, values);
        final double  b = doubleValue(SRC_SEMI_MINOR, values);
        final double ta = doubleValue(TGT_SEMI_MAJOR, values);
        final double tb = doubleValue(TGT_SEMI_MINOR, values);
        final double dx = doubleValue(DX,             values);
        final double dy = doubleValue(DY,             values);
        final double dz = doubleValue(DZ,             values);
        MathTransform transform = MolodenskyTransform.create(isAbridged(),
                a, b, srcDim == 3, ta, tb, tgtDim == 3, dx, dy, dz);
        final Molodensky provider = complements[index(srcDim, tgtDim)];
        if (provider != this) {
            transform = new MathTransformDecorator(transform, provider);
        }
        return transform;
    }

    /**
     * Returns {@code true} for the abridged formulas.
     * This method is overridden by {@link AbridgedMolodensky}.
     */
    boolean isAbridged() {
        return false;
    }
}
