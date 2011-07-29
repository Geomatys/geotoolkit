/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.jts;

import java.util.Collections;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.process.AbstractProcessFactory;
import org.geotoolkit.process.jts.area.AreaDescriptor;
import org.geotoolkit.process.jts.boundary.BoundaryDescriptor;
import org.geotoolkit.process.jts.buffer.BufferDescriptor;
import org.geotoolkit.process.jts.centroid.CentroidDescriptor;
import org.geotoolkit.process.jts.contain.ContainDescriptor;
import org.geotoolkit.process.jts.convexhull.ConvexHullDescriptor;
import org.geotoolkit.process.jts.coveredby.CoveredByDescriptor;
import org.geotoolkit.process.jts.covers.CoversDescriptor;
import org.geotoolkit.process.jts.crosses.CrossesDescriptor;
import org.geotoolkit.process.jts.difference.DifferenceDescriptor;
import org.geotoolkit.process.jts.envelope.EnvelopeDescriptor;
import org.geotoolkit.process.jts.equalsexact.EqualsExactDescriptor;
import org.geotoolkit.process.jts.intersection.IntersectionDescriptor;
import org.geotoolkit.process.jts.intersects.IntersectsDescriptor;
import org.geotoolkit.process.jts.isempty.IsEmptyDescriptor;
import org.geotoolkit.process.jts.lenght.LenghtDescriptor;
import org.geotoolkit.process.jts.overlaps.OverlapsDescriptor;
import org.geotoolkit.process.jts.touches.TouchesDescriptor;
import org.geotoolkit.process.jts.union.UnionDescriptor;
import org.geotoolkit.process.jts.within.WithinDescriptor;


import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Provide a set of JTS operations as processes.
 * those are not very useful on their own, but are necessary when chaining
 * processes.
 * 
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class JTSProcessFactory extends AbstractProcessFactory{
    
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

    public JTSProcessFactory() {
        super(BufferDescriptor.INSTANCE, AreaDescriptor.INSTANCE, BoundaryDescriptor.INSTANCE, CentroidDescriptor.INSTANCE,
                ContainDescriptor.INSTANCE, ConvexHullDescriptor.INSTANCE, CoveredByDescriptor.INSTANCE, CoversDescriptor.INSTANCE,
                CrossesDescriptor.INSTANCE, DifferenceDescriptor.INSTANCE, EnvelopeDescriptor.INSTANCE, EqualsExactDescriptor.INSTANCE,
                IntersectionDescriptor.INSTANCE, IntersectsDescriptor.INSTANCE, IsEmptyDescriptor.INSTANCE, LenghtDescriptor.INSTANCE,
                OverlapsDescriptor.INSTANCE, TouchesDescriptor.INSTANCE, UnionDescriptor.INSTANCE, WithinDescriptor.INSTANCE);
    }
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
}
