/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import javax.measure.unit.SI;
import javax.measure.unit.NonSI;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;

import org.geotoolkit.measure.Units;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.GeocentricTransform;
import org.geotoolkit.referencing.operation.transform.ConcatenatedTransform;
import org.geotoolkit.referencing.operation.transform.GeocentricAffineTransform;
import org.geotoolkit.internal.referencing.Identifiers;

import static java.util.Collections.singletonMap;
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptor;
import static org.geotoolkit.internal.referencing.Identifiers.createOptionalDescriptor;


/**
 * The provider for "<cite>Position Vector 7-parameters transformation</cite>" (EPSG:9606).
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public class PositionVector7Param extends MathTransformProvider {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -6398226638364450229L;

    /**
     * The default value for geographic source and target dimensions, which is 2.
     *
     * {@note If this default value is modified, then the handling of the 3D
     *        cases in <code>MolodenskyTransform</code> must be adjusted too.}
     */
    static final int DEFAULT_DIMENSION = 2;

    /**
     * The maximal value for a rotation, in arc-second.
     */
    private static final double MAX_ROTATION = 180*60*60;

    /**
     * The operation parameter descriptor for the number of source geographic dimension (2 or 3).
     * This is a Geotk-specific argument. If presents, an {@code "Ellipsoid_To_Geocentric"}
     * transform will be concatenated before the geocentric translation.
     */
    public static final ParameterDescriptor<Integer> SRC_DIM = DefaultParameterDescriptor.create(
            singletonMap(NAME_KEY, new NamedIdentifier(Citations.GEOTOOLKIT, "src_dim")),
            DEFAULT_DIMENSION, 2, 3, false);

    /**
     * The operation parameter descriptor for the number of target geographic dimension (2 or 3).
     * This is a Geotk-specific argument. If presents, a {@code "Geocentric_To_Ellipsoid"}
     * transform will be concatenated after the geocentric translation.
     */
    public static final ParameterDescriptor<Integer> TGT_DIM = DefaultParameterDescriptor.create(
            singletonMap(NAME_KEY, new NamedIdentifier(Citations.GEOTOOLKIT, "tgt_dim")),
            DEFAULT_DIMENSION, 2, 3, false);

    /**
     * The operation parameter descriptor for the {@code "src_semi_major"} optional parameter value.
     * This is a Geotk-specific argument. If presents, an {@code "Ellipsoid_To_Geocentric"}
     * transform will be concatenated before the geocentric translation.
     * <p>
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> SRC_SEMI_MAJOR = createOptionalDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC, "src_semi_major")
            },
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the {@code "src_semi_minor"} optional parameter value.
     * This is a Geotk-specific argument. If presents, an {@code "Ellipsoid_To_Geocentric"}
     * transform will be concatenated before the geocentric translation.
     * <p>
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
     public static final ParameterDescriptor<Double> SRC_SEMI_MINOR = createOptionalDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC, "src_semi_minor"),
            },
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the {@code "tgt_semi_major"} optional parameter value.
     * This is a Geotk-specific argument. If presents, a {@code "Geocentric_To_Ellipsoid"}
     * transform will be concatenated after the geocentric translation.
     * <p>
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> TGT_SEMI_MAJOR = createOptionalDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC, "tgt_semi_major")
            },
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the {@code "tgt_semi_minor"} optional parameter value.
     * This is a Geotk-specific argument. If presents, a {@code "Geocentric_To_Ellipsoid"}
     * transform will be concatenated after the geocentric translation.
     * <p>
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> TGT_SEMI_MINOR = createOptionalDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC, "tgt_semi_minor")
            },
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the <cite>X-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dx dx}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> DX = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "dx"),
                new NamedIdentifier(Citations.EPSG, "X-axis translation")
            },
            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the <cite>Y-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dy dy}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> DY = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "dy"),
                new NamedIdentifier(Citations.EPSG, "Y-axis translation")
            },
            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the <cite>Z-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dz dz}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     */
    public static final ParameterDescriptor<Double> DZ = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "dz"),
                new NamedIdentifier(Citations.EPSG, "Z-axis translation")
            },
            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE);

    /**
     * The operation parameter descriptor for the <cite>X-axis rotation</cite>
     * ({@linkplain BursaWolfParameters#ex ex}) parameter value. Units are
     * {@linkplain NonSI#SECOND_ANGLE arc-seconds}.
     */
    public static final ParameterDescriptor<Double> EX = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "ex"),
                new NamedIdentifier(Citations.EPSG, "X-axis rotation")
            },
            0.0, -MAX_ROTATION, MAX_ROTATION, NonSI.SECOND_ANGLE);

    /**
     * The operation parameter descriptor for the <cite>Y-axis rotation</cite>
     * ({@linkplain BursaWolfParameters#ey ey}) parameter value. Units are
     * {@linkplain NonSI#SECOND_ANGLE arc-seconds}.
     */
    public static final ParameterDescriptor<Double> EY = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "ey"),
                new NamedIdentifier(Citations.EPSG, "Y-axis rotation")
            },
            0.0, -MAX_ROTATION, MAX_ROTATION, NonSI.SECOND_ANGLE);

    /**
     * The operation parameter descriptor for the <cite>Z-axis rotation</cite>
     * ({@linkplain BursaWolfParameters#ez ez}) parameter value. Units are
     * {@linkplain NonSI#SECOND_ANGLE arc-seconds}.
     */
    public static final ParameterDescriptor<Double> EZ = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "ez"),
                new NamedIdentifier(Citations.EPSG, "Z-axis rotation")
            },
            0.0, -MAX_ROTATION, MAX_ROTATION, NonSI.SECOND_ANGLE);

    /**
     * The operation parameter descriptor for the <cite>Scale difference</cite>
     * ({@linkplain BursaWolfParameters#ppm ppm}) parameter value. Valid values
     * range from negative to positive infinity. Units are
     * {@linkplain Units#PPM parts per million}.
     */
    public static final ParameterDescriptor<Double> PPM = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "ppm"),
                new NamedIdentifier(Citations.EPSG, "Scale difference")
            },
            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Units.PPM);

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS =
            createDescriptorGroup("Position Vector 7-param. transformation", 9606);

    /**
     * Creates a parameters group using the 7 parameters.
     */
    static ParameterDescriptorGroup createDescriptorGroup(final String name, final int code) {
        return Identifiers.createDescriptorGroup(new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.EPSG, name),
            new NamedIdentifier(Citations.EPSG, "Bursa-Wolf"),
            new IdentifierCode (Citations.EPSG, code)
        }, new ParameterDescriptor<?>[] {
            DX, DY, DZ, EX, EY, EZ, PPM,
            SRC_SEMI_MAJOR, SRC_SEMI_MINOR,
            TGT_SEMI_MAJOR, TGT_SEMI_MINOR,
            SRC_DIM, TGT_DIM
        });
    }

    /**
     * Constructs the provider.
     */
    public PositionVector7Param() {
        this(PARAMETERS);
    }

    /**
     * Constructs a provider with the specified parameters.
     */
    PositionVector7Param(final ParameterDescriptorGroup parameters) {
        super(3, 3, parameters);
    }

    /**
     * Returns the operation type, which is a transformation.
     */
    @Override
    public Class<Transformation> getOperationType() {
        return Transformation.class;
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
        final BursaWolfParameters parameters = new BursaWolfParameters(null);
        fill(parameters, values);
        return concatenate(concatenate(new GeocentricAffineTransform(parameters, getParameters()),
                values, SRC_SEMI_MAJOR, SRC_SEMI_MINOR, SRC_DIM),
                values, TGT_SEMI_MAJOR, TGT_SEMI_MINOR, TGT_DIM);
    }

    /**
     * Fills the given Bursa-Wolf parameters according the specified values.
     * This method is invoked automatically by {@link #createMathTransform}.
     *
     * @param parameters The Bursa-Wold parameters to set.
     * @param values The parameter values to read. Those parameters will not be modified.
     */
    void fill(final BursaWolfParameters parameters, final ParameterValueGroup values) {
        parameters.dx  = Parameters.doubleValue(DX, values);
        parameters.dy  = Parameters.doubleValue(DY, values);
        parameters.dz  = Parameters.doubleValue(DZ, values);
        parameters.ex  = Parameters.doubleValue(EX, values);
        parameters.ey  = Parameters.doubleValue(EY, values);
        parameters.ez  = Parameters.doubleValue(EZ, values);
        parameters.ppm = Parameters.doubleValue(PPM, values);
    }

    /**
     * Concatenates the supplied transform with an "ellipsoid to geocentric" or a
     * "geocentric to ellipsod" step, if needed.
     */
    private static MathTransform concatenate(final MathTransform transform,
            final ParameterValueGroup values,
            final ParameterDescriptor<Double> major,
            final ParameterDescriptor<Double> minor,
            final ParameterDescriptor<Integer> dim)
    {
        double  semiMajor = Parameters.doubleValue(major, values);
        double  semiMinor = Parameters.doubleValue(minor, values);
        Integer dimension = Parameters.integerValue(dim,  values);
        boolean hasHeight = false;
        if (dimension == null) {
            if (Double.isNaN(semiMajor) && Double.isNaN(semiMinor)) {
                return transform;
            }
        } else {
            switch (dimension) {
                case DEFAULT_DIMENSION: break;
                case 3: hasHeight = true; break;
                default: {
                    final String name = dim.getName().getCode();
                    throw new InvalidParameterValueException(Errors.format(
                            Errors.Keys.ILLEGAL_ARGUMENT_$2, name, dimension), name, dimension);
                }
            }
        }
        ensureValid(major, semiMajor);
        ensureValid(minor, semiMinor);
        final GeocentricTransform step;
        step = new GeocentricTransform(semiMajor, semiMinor, SI.METRE, hasHeight);
        // Note: dimension may be 0 if not user-provided, which is treated as 2.
        if (dim == SRC_DIM) {
            return ConcatenatedTransform.create(step, transform);
        } else {
            return ConcatenatedTransform.create(transform, step.inverse());
        }
    }

    /**
     * Ensures the the specified parameter is valid.
     */
    private static void ensureValid(final ParameterDescriptor<?> param, double value) {
        if (!(value > 0)) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.MISSING_PARAMETER_$1, param.getName().getCode()));
        }
    }
}
