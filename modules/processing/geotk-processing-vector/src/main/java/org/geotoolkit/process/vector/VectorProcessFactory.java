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
package org.geotoolkit.process.vector;

import java.util.Collections;

import org.geotoolkit.process.vector.centroid.CentroidDescriptor;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.process.AbstractProcessFactory;
import org.geotoolkit.process.vector.buffer.BufferDescriptor;
import org.geotoolkit.process.vector.clip.ClipDescriptor;
import org.geotoolkit.process.vector.clipgeometry.ClipGeometryDescriptor;
import org.geotoolkit.process.vector.douglaspeucker.DouglasPeuckerDescriptor;
import org.geotoolkit.process.vector.intersect.IntersectDescriptor;
import org.geotoolkit.process.vector.nearest.NearestDescriptor;
import org.geotoolkit.process.vector.spacialjoin.SpacialJoinDescriptor;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Factory for vector process
 * Factory name : "vector"
 * @author Quentin Boileau
 * @module pending
 */
public class VectorProcessFactory extends AbstractProcessFactory {

    /**Factory name*/
    public static final String NAME = "vector";
    static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        Identifier id = new DefaultIdentifier(NAME);
        DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    /**
     * Default constructor 
     */
    public VectorProcessFactory() {
        super(CentroidDescriptor.INSTANCE, ClipGeometryDescriptor.INSTANCE, ClipDescriptor.INSTANCE,
                DouglasPeuckerDescriptor.INSTANCE, BufferDescriptor.INSTANCE, IntersectDescriptor.INSTANCE,
                NearestDescriptor.INSTANCE, SpacialJoinDescriptor.INSTANCE);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
}
