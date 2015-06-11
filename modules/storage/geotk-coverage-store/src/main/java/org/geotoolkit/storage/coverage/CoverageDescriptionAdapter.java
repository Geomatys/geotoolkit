package org.geotoolkit.storage.coverage;

import org.apache.sis.metadata.iso.ImmutableIdentifier;
import org.apache.sis.metadata.iso.content.DefaultAttributeGroup;
import org.apache.sis.metadata.iso.content.DefaultCoverageDescription;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.metadata.DefaultSampleDimensionExt;
import org.geotoolkit.metadata.ImageStatistics;

/**
 * Simple Adapter that fill a {@link DefaultCoverageDescription} with information form
 * a {@link org.geotoolkit.metadata.ImageStatistics} object.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class CoverageDescriptionAdapter extends DefaultCoverageDescription {

    public CoverageDescriptionAdapter(ImageStatistics statistics) {

        final DefaultAttributeGroup attg = new DefaultAttributeGroup();
        final ImageStatistics.Band[] bands = statistics.getBands();
        for(int i=0;i<bands.length;i++){
            final ImageStatistics.Band band = bands[i];
            final DefaultSampleDimensionExt dim = new DefaultSampleDimensionExt();
            dim.setMinValue(band.getMin());
            dim.setMaxValue(band.getMax());
            dim.setMeanValue(band.getMean());
            dim.setStandardDeviation(band.getStd());
            dim.setHistogram(band.getHistogram());
            dim.setHistogramMin(band.getMin());
            dim.setHistogramMax(band.getMax());

            dim.setSequenceIdentifier(Names.createMemberName(null, "/", "" + i, Integer.class));
            dim.getIdentifiers().add(new ImmutableIdentifier(null, null, band.getName()));
            dim.getNames().add(new ImmutableIdentifier(null, null, band.getName()));

            attg.getAttributes().add(dim);
        }

        this.getAttributeGroups().add(attg);
    }
}
