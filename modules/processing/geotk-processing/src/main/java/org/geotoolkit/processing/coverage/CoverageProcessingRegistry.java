/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 - 2012, Geomatys
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

package org.geotoolkit.processing.coverage;

import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.processing.AbstractProcessingRegistry;
import org.geotoolkit.processing.coverage.bandcombine.BandCombineDescriptor;
import org.geotoolkit.processing.coverage.bandselect.BandSelectDescriptor;
import org.geotoolkit.processing.coverage.copy.CopyCoverageStoreDescriptor;
import org.geotoolkit.processing.coverage.coveragetofeatures.CoverageToFeaturesDescriptor;
import org.geotoolkit.processing.coverage.coveragetovector.CoverageToVectorDescriptor;
import org.geotoolkit.processing.coverage.isoline.IsolineDescriptor;
import org.geotoolkit.processing.coverage.isoline2.IsolineDescriptor2;
import org.geotoolkit.processing.coverage.kriging.KrigingDescriptor;
import org.geotoolkit.processing.coverage.mathcalc.MathCalcDescriptor;
import org.geotoolkit.processing.coverage.merge.MergeDescriptor;
import org.geotoolkit.processing.coverage.metadataextractor.ExtractionDescriptor;
import org.geotoolkit.processing.coverage.pyramid.PyramidDescriptor;
import org.geotoolkit.processing.coverage.reducetodomain.ReduceToDomainDescriptor;
import org.geotoolkit.processing.coverage.reformat.ReformatDescriptor;
import org.geotoolkit.processing.coverage.resample.IOResampleDescriptor;
import org.geotoolkit.processing.coverage.resample.ResampleDescriptor;
import org.geotoolkit.processing.coverage.shadedrelief.ShadedReliefDescriptor;
import org.geotoolkit.processing.coverage.statistics.StatisticsDescriptor;
import org.geotoolkit.processing.coverage.straighten.StraightenDescriptor;
import org.geotoolkit.processing.coverage.tiling.TilingDescriptor;
import org.geotoolkit.processing.coverage.volume.ComputeVolumeDescriptor;
import org.geotoolkit.processing.image.statistics.ImageStatisticsDescriptor;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

import java.util.Collections;

/**
 * Declare loading of coverage processes.
 *
 * @author Johann sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class CoverageProcessingRegistry extends AbstractProcessingRegistry {

    public static final String NAME = "coverage";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        Identifier id = new DefaultIdentifier(NAME);
        DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public CoverageProcessingRegistry(){
        super(CoverageToVectorDescriptor.INSTANCE,
              CoverageToFeaturesDescriptor.INSTANCE,
              TilingDescriptor.INSTANCE,
              KrigingDescriptor.INSTANCE,
              ExtractionDescriptor.INSTANCE,
              IsolineDescriptor.INSTANCE,
              IsolineDescriptor2.INSTANCE,
              ResampleDescriptor.INSTANCE,
              CopyCoverageStoreDescriptor.INSTANCE,
              StraightenDescriptor.INSTANCE,
              ReduceToDomainDescriptor.INSTANCE,
              BandSelectDescriptor.INSTANCE,
              BandCombineDescriptor.INSTANCE,
              ReformatDescriptor.INSTANCE,
              MergeDescriptor.INSTANCE,
              PyramidDescriptor.INSTANCE,
              ImageStatisticsDescriptor.INSTANCE,
              ComputeVolumeDescriptor.INSTANCE,
              IOResampleDescriptor.INSTANCE,
              ShadedReliefDescriptor.INSTANCE,
              MathCalcDescriptor.INSTANCE,
              StatisticsDescriptor.INSTANCE);
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

}
