/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.transform.MolodenskyTransform;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.internal.referencing.Identifiers.createDescriptorGroup;


/**
 * The provider for "<cite>Abridged Molodensky transformation</cite>" (EPSG:9605). This provider
 * constructs transforms from {@linkplain GeographicCRS geographic} to geographic coordinate
 * reference systems, without passing though {@linkplain GeocentricCRS geocentric} one.
 *
 * {@note The EPSG database does not use <code>src_semi_major</code>, <i>etc.</i>
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
@Immutable
public class AbridgedMolodensky extends Molodensky {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -3889456253400732280L;

    /**
     * The parameters group.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,  "Abridged_Molodenski"),
            new NamedIdentifier(Citations.EPSG, "Abridged Molodensky"),
            new IdentifierCode (Citations.EPSG,  9605),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.ABRIDGED_MOLODENSKY_TRANSFORM))
        }, new ParameterDescriptor<?>[] {
            DIM, SRC_DIM, TGT_DIM, DX, DY, DZ,
            SRC_SEMI_MAJOR, SRC_SEMI_MINOR,
            TGT_SEMI_MAJOR, TGT_SEMI_MINOR
        });

    /**
     * Constructs a provider.
     */
    public AbridgedMolodensky() {
        // Following constructors register themselves in the "complements" array.
        this(DEFAULT_DIMENSION, DEFAULT_DIMENSION, PARAMETERS, new AbridgedMolodensky[4]);
        new AbridgedMolodensky(DEFAULT_DIMENSION, 3, PARAMETERS, complements);
        new AbridgedMolodensky(3, DEFAULT_DIMENSION, PARAMETERS, complements);
        new AbridgedMolodensky(3, 3, PARAMETERS, complements);
    }

    /**
     * Constructs a provider from a set of parameters.
     *
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     * @param parameters      The set of parameters (never {@code null}).
     * @param complements     Providers for all combinations between 2D and 3D cases.
     */
    private AbridgedMolodensky(final int sourceDimension, final int targetDimension,
               final ParameterDescriptorGroup parameters, final Molodensky[] complements)
    {
        super(sourceDimension, targetDimension, parameters, complements);
    }

    /**
     * Returns {@code true} for the abridged formulas.
     */
    @Override
    boolean isAbridged() {
        return true;
    }
}
