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
package org.geotoolkit.processing.vector;

import java.util.Collections;

import org.geotoolkit.processing.vector.centroid.CentroidDescriptor;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;

import org.geotoolkit.processing.AbstractProcessingRegistry;
import org.geotoolkit.processing.vector.affinetransform.AffineTransformDescriptor;
import org.geotoolkit.processing.vector.buffer.BufferDescriptor;
import org.geotoolkit.processing.vector.clip.ClipDescriptor;
import org.geotoolkit.processing.vector.clipgeometry.ClipGeometryDescriptor;
import org.geotoolkit.processing.vector.convexhull.ConvexHullDescriptor;
import org.geotoolkit.processing.vector.difference.DifferenceDescriptor;
import org.geotoolkit.processing.vector.differencegeometry.DifferenceGeometryDescriptor;
import org.geotoolkit.processing.vector.douglaspeucker.DouglasPeuckerDescriptor;
import org.geotoolkit.processing.vector.filter.FilterDescriptor;
import org.geotoolkit.processing.vector.intersect.IntersectDescriptor;
import org.geotoolkit.processing.vector.intersection.IntersectionDescriptor;
import org.geotoolkit.processing.vector.maxlimit.MaxLimitDescriptor;
import org.geotoolkit.processing.vector.merge.MergeDescriptor;
import org.geotoolkit.processing.vector.nearest.NearestDescriptor;
import org.geotoolkit.processing.vector.regroup.RegroupDescriptor;
import org.geotoolkit.processing.vector.reproject.ReprojectDescriptor;
import org.geotoolkit.processing.vector.retype.RetypeDescriptor;
import org.geotoolkit.processing.vector.sort.SortByDescriptor;
import org.geotoolkit.processing.vector.spatialjoin.SpatialJoinDescriptor;
import org.geotoolkit.processing.vector.startoffset.StartOffsetDescriptor;
import org.geotoolkit.processing.vector.union.UnionDescriptor;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * Factory for vector process
 * Factory name : "vector"
 * @author Quentin Boileau
 * @module pending
 */
public class VectorProcessingRegistry extends AbstractProcessingRegistry {

    /**Factory name*/
    public static final String NAME = "vector";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    /**
     * Default constructor 
     */
    public VectorProcessingRegistry() {
        super(  CentroidDescriptor.INSTANCE,            ClipGeometryDescriptor.INSTANCE,    ClipDescriptor.INSTANCE,
                DouglasPeuckerDescriptor.INSTANCE,      BufferDescriptor.INSTANCE,          IntersectDescriptor.INSTANCE,
                NearestDescriptor.INSTANCE,             SpatialJoinDescriptor.INSTANCE,     DifferenceDescriptor.INSTANCE,
                DifferenceGeometryDescriptor.INSTANCE,  IntersectionDescriptor.INSTANCE,    ConvexHullDescriptor.INSTANCE,
                RegroupDescriptor.INSTANCE,             AffineTransformDescriptor.INSTANCE, ReprojectDescriptor.INSTANCE,
                MergeDescriptor.INSTANCE,               UnionDescriptor.INSTANCE,           RetypeDescriptor.INSTANCE,
                FilterDescriptor.INSTANCE,              MaxLimitDescriptor.INSTANCE,        
                SortByDescriptor.INSTANCE,              StartOffsetDescriptor.INSTANCE);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
}
