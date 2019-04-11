package org.geotoolkit.coverage.memory;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.extent.VerticalExtent;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class MemoryStoreTest {

    private static MemoryCoverageStore create() throws DataStoreException {
        final MemoryCoverageStore mcs = new MemoryCoverageStore();
        final GridCoverageResource ref = mcs.add(new DefiningCoverageResource(Names.createLocalName("test", ":", "mock")));

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setRenderedImage(new BufferedImage(13, 13, BufferedImage.TYPE_BYTE_GRAY));
        gcb.setCoordinateReferenceSystem(CommonCRS.defaultGeographic());
        gcb.setEnvelope(new GeneralEnvelope(new DefaultGeographicBoundingBox(-7, 7, -7, 7)));

        ref.acquireWriter().write(gcb.build(), null);
        return mcs;
    }

    @Test
    public void testMetadata() throws Exception {
        final MemoryCoverageStore store = create();

        final Metadata md = store.getMetadata();

        final GridCoverageResource[] refs = DataStores.flatten(store,true).stream()
                .filter(GridCoverageResource.class::isInstance)
                .map(GridCoverageResource.class::cast)
                .toArray(size -> new GridCoverageResource[size]);

        final DefaultExtent expectedExtent = new DefaultExtent();
        final Set<CoordinateReferenceSystem> crss = new HashSet<>();
        for (final GridCoverageResource ref : refs) {
            final GridGeometry gg = ref.getGridGeometry();
            expectedExtent.addElements(gg.getEnvelope());
            crss.add(gg.getCoordinateReferenceSystem());
        }

        final Set<GeographicExtent> geoBoxes = new HashSet<>();
        final Set<TemporalExtent> timeBoxes = new HashSet<>();
        final Set<VerticalExtent> vertBoxes = new HashSet<>();
        md.getIdentificationInfo().stream()
                .filter(info -> info instanceof DataIdentification)
                .flatMap(info -> ((DataIdentification)info).getExtents().stream())
                .sequential()
                .forEach(extent -> {
                    geoBoxes.addAll(extent.getGeographicElements());
                    timeBoxes.addAll(extent.getTemporalElements());
                    vertBoxes.addAll(extent.getVerticalElements());
                });

        Assert.assertTrue("Metadata should contain data reference system.", md.getReferenceSystemInfo().containsAll(crss));
        Assert.assertTrue("Metadata geographic extents should be aligned with data store envelopes", sameElements(geoBoxes, expectedExtent.getGeographicElements()));
        Assert.assertTrue("Metadata temporal extents should be aligned with data store envelopes", sameElements(timeBoxes, expectedExtent.getTemporalElements()));
        Assert.assertTrue("Metadata vertical extents should be aligned with data store envelopes", sameElements(vertBoxes, expectedExtent.getVerticalElements()));
    }

    /**
     * Check that the two given collections contain the same objects.
     * @param first
     * @param second
     * @return True if input collections have the same content (order is not checked).
     * False otherwise.
     */
    private static boolean sameElements(Collection first, Collection second) {
        return first.size() == second.size() && first.containsAll(second);
    }
}
