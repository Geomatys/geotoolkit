/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011 - 2012, Geomatys
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
package org.geotoolkit.processing.jts;

import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.processing.AbstractProcessingRegistry;
import org.geotoolkit.processing.jts.area.AreaDescriptor;
import org.geotoolkit.processing.jts.boundary.BoundaryDescriptor;
import org.geotoolkit.processing.jts.buffer.BufferDescriptor;
import org.geotoolkit.processing.jts.centroid.CentroidDescriptor;
import org.geotoolkit.processing.jts.contain.ContainDescriptor;
import org.geotoolkit.processing.jts.convexhull.ConvexHullDescriptor;
import org.geotoolkit.processing.jts.coveredby.CoveredByDescriptor;
import org.geotoolkit.processing.jts.covers.CoversDescriptor;
import org.geotoolkit.processing.jts.crosses.CrossesDescriptor;
import org.geotoolkit.processing.jts.difference.DifferenceDescriptor;
import org.geotoolkit.processing.jts.envelope.EnvelopeDescriptor;
import org.geotoolkit.processing.jts.equalsexact.EqualsExactDescriptor;
import org.geotoolkit.processing.jts.intersection.IntersectionDescriptor;
import org.geotoolkit.processing.jts.intersection.IntersectionSurfaceDescriptor;
import org.geotoolkit.processing.jts.intersects.IntersectsDescriptor;
import org.geotoolkit.processing.jts.isempty.IsEmptyDescriptor;
import org.geotoolkit.processing.jts.lenght.LenghtDescriptor;
import org.geotoolkit.processing.jts.overlaps.OverlapsDescriptor;
import org.geotoolkit.processing.jts.touches.TouchesDescriptor;
import org.geotoolkit.processing.jts.union.UnionDescriptor;
import org.geotoolkit.processing.jts.within.WithinDescriptor;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Provide a set of JTS operations as processes.
 * those are not very useful on their own, but are necessary when chaining
 * processes.
 *
 * @author Quentin Boileau (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class JTSProcessingRegistry extends AbstractProcessingRegistry{

    /** factory name **/
    public static final String NAME = "jts";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public JTSProcessingRegistry() {
        super(BufferDescriptor.INSTANCE, AreaDescriptor.INSTANCE, BoundaryDescriptor.INSTANCE, CentroidDescriptor.INSTANCE,
                ContainDescriptor.INSTANCE, ConvexHullDescriptor.INSTANCE, CoveredByDescriptor.INSTANCE, CoversDescriptor.INSTANCE,
                CrossesDescriptor.INSTANCE, DifferenceDescriptor.INSTANCE, EnvelopeDescriptor.INSTANCE, EqualsExactDescriptor.INSTANCE,
                IntersectionDescriptor.INSTANCE, IntersectionSurfaceDescriptor.INSTANCE, IntersectsDescriptor.INSTANCE,
                IsEmptyDescriptor.INSTANCE, LenghtDescriptor.INSTANCE, OverlapsDescriptor.INSTANCE, TouchesDescriptor.INSTANCE,
                UnionDescriptor.INSTANCE, WithinDescriptor.INSTANCE);
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

}
