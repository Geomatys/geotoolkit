/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.providers.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Tests on the marshalling/unmarshalling process for a map context.
 *
 * @author Cédric Briançon
 */
public class ProvidersXmlTest {
    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller marshaller;

    /**
     * What should be the result of the marshalling process.
     */
    private static final String RESULT_MARSHALLING =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
            "<MapContext>\n"+
            "    <MapItem>\n"+
            "        <MapItem>\n"+
            "            <MapLayer>\n"+
            "                <providerReference>postgis_test:my_otherlayer</providerReference>\n"+
            "                <styleReference>my_otherstyle</styleReference>\n"+
            "            </MapLayer>\n"+
            "            <MapLayer>\n"+
            "                <providerReference>coverage:my_thirdlayer</providerReference>\n"+
            "                <styleReference>my_newstyle</styleReference>\n"+
            "            </MapLayer>\n"+
            "        </MapItem>\n"+
            "        <MapItem/>\n"+
            "        <MapLayer>\n"+
            "            <providerReference>postgis_test:my_layer</providerReference>\n"+
            "            <styleReference>my_style</styleReference>\n"+
            "        </MapLayer>\n"+
            "    </MapItem>\n"+
            "</MapContext>";

    @Before
    public void setUp() throws JAXBException {
        pool =   new MarshallerPool(MapContext.class);
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
    }

    /**
     * Test for the marshalling process of a {@link MapContext}.
     *
     * @throws JAXBException
     */
    @Test
    public void testMarshalling() throws JAXBException {
        final List<MapLayer> mapLayers2 = Arrays.asList(
                new MapLayer(new ProviderReference("postgis_test:my_otherlayer"), new StyleReference("my_otherstyle")),
                new MapLayer(new ProviderReference("coverage:my_thirdlayer"), new StyleReference("my_newstyle"))
        );
        final List<MapItem> mapItems = Arrays.asList(new MapItem(null, mapLayers2), new MapItem(null, null));

        final List<MapLayer> mapLayers = Arrays.asList(
                new MapLayer(new ProviderReference("postgis_test:my_layer"), new StyleReference("my_style")));
        final MapItem mapItem = new MapItem(mapItems, mapLayers);
        final MapContext mapContext = new MapContext(mapItem);

        final StringWriter sw = new StringWriter();
        marshaller.marshal(mapContext, sw);

        final String result = sw.toString();
        try {
            sw.close();
        } catch (IOException e) {
            fail("Unable to close the writer");
        }
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(RESULT_MARSHALLING, result.trim());
    }

    /**
     * Test for the unmarshalling process of a string-representation of a {@link MapContext}.
     *
     * @throws JAXBException
     */
    @Test
    public void testUnmarshalling() throws JAXBException {
        final StringReader sr = new StringReader(RESULT_MARSHALLING);
        final Object result = unmarshaller.unmarshal(new StringReader(RESULT_MARSHALLING));
        sr.close();
        assertNotNull(result);
        assertTrue(result instanceof MapContext);

        final MapContext mc = (MapContext)result;
        final MapLayer ml0 = mc.getMapItem().getMapItem().get(0).getMapLayer().get(0);
        assertEquals("postgis_test:my_otherlayer", ml0.getProviderReference().getValue());
        assertEquals("my_otherstyle", ml0.getStyleReference().getValue());

        final MapLayer ml1 = mc.getMapItem().getMapItem().get(0).getMapLayer().get(1);
        assertEquals("coverage:my_thirdlayer", ml1.getProviderReference().getValue());
        assertEquals("my_newstyle", ml1.getStyleReference().getValue());

        final List<MapLayer> mapLayers = mc.getMapItem().getMapLayer();
        assertNotNull(mapLayers);
        assertFalse(mapLayers.isEmpty());
        assertEquals(1, mapLayers.size());
        assertEquals("postgis_test:my_layer", mapLayers.get(0).getProviderReference().getValue());
        assertEquals("my_style", mapLayers.get(0).getStyleReference().getValue());
    }

    @After
    public void tearDown() {
        if (unmarshaller != null) {
            pool.release(unmarshaller);
        }
        if (marshaller != null) {
            pool.release(marshaller);
        }
    }
}
