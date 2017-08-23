/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.processing;

import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeotkProcessingRegistry extends AbstractProcessingRegistry {

    public static final String NAME = "geotoolkit";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        Identifier id = new DefaultIdentifier(NAME);
        DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public GeotkProcessingRegistry(){
        super(
                //coverage
                org.geotoolkit.processing.coverage.bandcombine.BandCombineDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.bandselect.BandSelectDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.compose.ComposeDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.copy.CopyCoverageStoreDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.coveragetofeatures.CoverageToFeaturesDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.coveragetovector.CoverageToVectorDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.isoline.IsolineDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.isoline2.IsolineDescriptor2.INSTANCE,
                org.geotoolkit.processing.coverage.kriging.KrigingDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.mathcalc.MathCalcDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.merge.MergeDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.metadataextractor.ExtractionDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.pyramid.PyramidDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.reducetodomain.ReduceToDomainDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.reformat.ReformatDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.resample.IOResampleDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.resample.ResampleDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.shadedrelief.ShadedReliefDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.statistics.StatisticsDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.straighten.StraightenDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.tiling.TilingDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.volume.ComputeVolumeDescriptor.INSTANCE,
                org.geotoolkit.processing.coverage.kriging.KrigingDescriptor.INSTANCE,
                //datastore
                org.geotoolkit.processing.datastore.copy.CopyDescriptor.INSTANCE,
                //groovy
                org.geotoolkit.processing.groovy.GroovyDescriptor.INSTANCE,
                //image
                org.geotoolkit.processing.image.bandselect.BandSelectDescriptor.INSTANCE,
                org.geotoolkit.processing.image.bandcombine.BandCombineDescriptor.INSTANCE,
                org.geotoolkit.processing.image.reformat.ReformatDescriptor.INSTANCE,
                org.geotoolkit.processing.image.replace.ReplaceDescriptor.INSTANCE,
                org.geotoolkit.processing.image.dynamicrange.DynamicRangeStretchDescriptor.INSTANCE,
                org.geotoolkit.processing.image.statistics.ImageStatisticsDescriptor.INSTANCE,
                //io
                org.geotoolkit.processing.io.createtempfile.CreateTempFileDescriptor.INSTANCE,
                org.geotoolkit.processing.io.createtempfolder.CreateTempFolderDescriptor.INSTANCE,
                org.geotoolkit.processing.io.delete.DeleteDescriptor.INSTANCE,
                org.geotoolkit.processing.io.unpackfile.UnpackFileDescriptor.INSTANCE,
                org.geotoolkit.processing.io.packfile.PackFileDescriptor.INSTANCE,
                //jts
                org.geotoolkit.processing.jts.buffer.BufferDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.area.AreaDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.boundary.BoundaryDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.centroid.CentroidDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.contain.ContainDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.convexhull.ConvexHullDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.coveredby.CoveredByDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.covers.CoversDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.crosses.CrossesDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.difference.DifferenceDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.envelope.EnvelopeDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.equalsexact.EqualsExactDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.intersection.IntersectionDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.intersection.IntersectionSurfaceDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.intersects.IntersectsDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.isempty.IsEmptyDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.lenght.LenghtDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.overlaps.OverlapsDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.touches.TouchesDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.union.UnionDescriptor.INSTANCE,
                org.geotoolkit.processing.jts.within.WithinDescriptor.INSTANCE,
                //math
                org.geotoolkit.processing.math.add.AddDescriptor.INSTANCE,
                org.geotoolkit.processing.math.substract.SubstractDescriptor.INSTANCE,
                org.geotoolkit.processing.math.divide.DivideDescriptor.INSTANCE,
                org.geotoolkit.processing.math.multiply.MultiplyDescriptor.INSTANCE,
                org.geotoolkit.processing.math.power.PowerDescriptor.INSTANCE,
                org.geotoolkit.processing.math.absolute.AbsoluteDescriptor.INSTANCE,
                org.geotoolkit.processing.math.acos.AcosDescriptor.INSTANCE,
                org.geotoolkit.processing.math.asin.AsinDescriptor.INSTANCE,
                org.geotoolkit.processing.math.atan.AtanDescriptor.INSTANCE,
                org.geotoolkit.processing.math.atan2.Atan2Descriptor.INSTANCE,
                org.geotoolkit.processing.math.cos.CosDescriptor.INSTANCE,
                org.geotoolkit.processing.math.sin.SinDescriptor.INSTANCE,
                org.geotoolkit.processing.math.tan.TanDescriptor.INSTANCE,
                org.geotoolkit.processing.math.round.RoundDescriptor.INSTANCE,
                org.geotoolkit.processing.math.ceil.CeilDescriptor.INSTANCE,
                org.geotoolkit.processing.math.floor.FloorDescriptor.INSTANCE,
                org.geotoolkit.processing.math.log.LogDescriptor.INSTANCE,
                org.geotoolkit.processing.math.todegree.ToDegreeDescriptor.INSTANCE,
                org.geotoolkit.processing.math.toradian.ToRadianDescriptor.INSTANCE,
                org.geotoolkit.processing.math.min.MinDescriptor.INSTANCE,
                org.geotoolkit.processing.math.max.MaxDescriptor.INSTANCE,
                org.geotoolkit.processing.math.sum.SumDescriptor.INSTANCE,
                org.geotoolkit.processing.math.avg.AvgDescriptor.INSTANCE,
                org.geotoolkit.processing.math.median.MedianDescriptor.INSTANCE,
                //metadata
                org.geotoolkit.processing.metadata.merge.MergeDescriptor.INSTANCE,
                //referencing
                org.geotoolkit.processing.referencing.createdb.CreateDBDescriptor.INSTANCE,
                //string
                org.geotoolkit.processing.string.ConcatDescriptor.INSTANCE,
                //vector
                org.geotoolkit.processing.vector.centroid.CentroidDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.clipgeometry.ClipGeometryDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.clip.ClipDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.douglaspeucker.DouglasPeuckerDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.buffer.BufferDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.intersect.IntersectDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.nearest.NearestDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.spatialjoin.SpatialJoinDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.difference.DifferenceDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.differencegeometry.DifferenceGeometryDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.intersection.IntersectionDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.convexhull.ConvexHullDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.regroup.RegroupDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.affinetransform.AffineTransformDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.reproject.ReprojectDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.merge.MergeDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.union.UnionDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.retype.RetypeDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.filter.FilterDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.maxlimit.MaxLimitDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.sort.SortByDescriptor.INSTANCE,
                org.geotoolkit.processing.vector.startoffset.StartOffsetDescriptor.INSTANCE
              );
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

}

