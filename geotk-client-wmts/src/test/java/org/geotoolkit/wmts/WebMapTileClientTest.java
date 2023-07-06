/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.wmts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import jakarta.xml.bind.JAXBException;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.wmts.xml.WMTSBindingUtilities;
import org.geotoolkit.wmts.xml.WMTSVersion;
import org.geotoolkit.wmts.xml.v100.Capabilities;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WebMapTileClientTest {

    /**
     * Test update sequence updates child resources.
     */
    @Test
    public void testUpdateSequence() throws MalformedURLException, JAXBException, DataStoreException, FactoryException {

        Capabilities capa1 = WMTSBindingUtilities.unmarshall(WebMapTileClientTest.class.getResource("/org/geotoolkit/wmts/UpdateSequence1.xml"), WMTSVersion.v100);
        Capabilities capa2 = WMTSBindingUtilities.unmarshall(WebMapTileClientTest.class.getResource("/org/geotoolkit/wmts/UpdateSequence2.xml"), WMTSVersion.v100);

        final WebMapTileClient wmts = new WebMapTileClient(new URL("http://localhost:8080/wmts"), null, WMTSVersion.v100, capa1, true);

        final GridCoverageResource resource = (GridCoverageResource) wmts.findResource("PROFONDEUR_RGB_pyramid");
        final AtomicBoolean updated = new AtomicBoolean(false);
        resource.addListener(StoreEvent.class, new StoreListener<StoreEvent>() {
            @Override
            public void eventOccured(StoreEvent event) {
                updated.set(true);
            }
        });

        Assert.assertEquals(CommonCRS.WGS84.normalizedGeographic(), resource.getGridGeometry().getCoordinateReferenceSystem());
        Assert.assertEquals(false, updated.get());

        wmts.checkForUpdates(capa2);

        Assert.assertEquals(true, updated.get());
        Assert.assertEquals(CommonCRS.NAD83.normalizedGeographic(), resource.getGridGeometry().getCoordinateReferenceSystem());

    }

}
