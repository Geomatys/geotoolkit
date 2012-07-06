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
import java.util.ArrayList;
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
            "                <dataReference>postgis_test:my_otherlayer</dataReference>\n"+
            "                <styleReference>my_otherstyle</styleReference>\n"+
            "            </MapLayer>\n"+
            "            <MapLayer>\n"+
            "                <dataReference>coverage:my_thirdlayer</dataReference>\n"+
            "                <styleReference>my_newstyle</styleReference>\n"+
            "            </MapLayer>\n"+
            "        </MapItem>\n"+
            "        <MapItem/>\n"+
            "        <MapLayer>\n"+
            "            <dataReference>postgis_test:my_layer</dataReference>\n"+
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
        final List<MapItem> mapLayers2 = new ArrayList<MapItem>();
        mapLayers2.add(new MapLayer(new DataReference("postgis_test:my_otherlayer"), new StyleReference("my_otherstyle")));
        mapLayers2.add(new MapLayer(new DataReference("coverage:my_thirdlayer"), new StyleReference("my_newstyle")));

        final List<MapItem> mapItems = new ArrayList<MapItem>();
        mapItems.add(new MapItem(mapLayers2));

        final MapLayer ml = new MapLayer(new DataReference("postgis_test:my_layer"), new StyleReference("my_style"));
        mapItems.add(new MapItem());
        mapItems.add(ml);
        final MapItem mapItem = new MapItem(mapItems);
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
        final List<MapItem> mapItems = mc.getMapItem().getMapItems();
        assertNotNull(mapItems);
        assertFalse(mapItems.isEmpty());
        assertEquals(3, mapItems.size());

        final MapLayer ml0 = (MapLayer) mapItems.get(0).getMapItems().get(0);
        assertEquals("postgis_test:my_otherlayer", ml0.getDataReference().getValue());
        assertEquals("my_otherstyle", ml0.getStyleReference().getValue());

        final MapLayer ml1 = (MapLayer) mapItems.get(0).getMapItems().get(1);
        assertEquals("coverage:my_thirdlayer", ml1.getDataReference().getValue());
        assertEquals("my_newstyle", ml1.getStyleReference().getValue());

        assertEquals("postgis_test:my_layer", ((MapLayer)mapItems.get(2)).getDataReference().getValue());
        assertEquals("my_style", ((MapLayer)mapItems.get(2)).getStyleReference().getValue());
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
