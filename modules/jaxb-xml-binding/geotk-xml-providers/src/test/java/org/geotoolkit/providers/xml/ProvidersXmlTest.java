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
import org.geotoolkit.se.xml.v110.DescriptionType;
import org.geotoolkit.se.xml.v110.FeatureTypeStyleType;
import org.geotoolkit.sld.xml.v110.StyledLayerDescriptor;
import org.geotoolkit.sld.xml.v110.UserLayer;
import org.geotoolkit.sld.xml.v110.UserStyle;
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
     * What should be the result of the marshalling process, without sld.
     */
    private static final String RESULT_MARSHALLING_WITHOUT_SLD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
            "<MapContext xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:se=\"http://www.opengis.net/se\">\n"+
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

    /**
     * What should be the result of the marshalling process, with sld.
     */
    private static final String RESULT_MARSHALLING_WITH_SLD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
            "<MapContext xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:se=\"http://www.opengis.net/se\">\n"+
            "    <MapItem>\n"+
            "        <MapItem>\n"+
            "            <MapLayer>\n"+
            "                <dataReference>postgis_test:my_otherlayer</dataReference>\n"+
            "                <style version=\"1.1.0\">\n"+
            "                    <sld:UserLayer>\n"+
            "                        <sld:UserStyle>\n"+
            "                            <se:Description>\n"+
            "                                <se:Title>test_sld</se:Title>\n"+
            "                            </se:Description>\n"+
            "                            <se:FeatureTypeStyle>\n"+
            "                                <se:Name>ft_test</se:Name>\n"+
            "                            </se:FeatureTypeStyle>\n"+
            "                        </sld:UserStyle>\n"+
            "                    </sld:UserLayer>\n"+
            "                </style>\n"+
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
        pool =   new MarshallerPool(MapContext.class, org.geotoolkit.internal.jaxb.geometry.ObjectFactory.class);
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
    }

    /**
     * Test for the marshalling process of a {@link MapContext}.
     *
     * @throws JAXBException
     */
    @Test
    public void testMarshallingWithoutSLD() throws JAXBException {
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
        assertEquals(RESULT_MARSHALLING_WITHOUT_SLD, result.trim());
    }

    /**
     * Test for the marshalling process of a {@link MapContext}.
     *
     * @throws JAXBException
     */
    @Test
    public void testMarshallingWithSLD() throws JAXBException {
        final List<MapItem> mapLayers2 = new ArrayList<MapItem>();
        final StyledLayerDescriptor sld = new StyledLayerDescriptor();
        final UserStyle us = new UserStyle();
        final DescriptionType title = new DescriptionType();
        title.setTitle("test_sld");
        us.setDescription(title);
        final FeatureTypeStyleType fts = new FeatureTypeStyleType();
        fts.setName("ft_test");
        us.getFeatureTypeStyleOrCoverageStyleOrOnlineResource().add(fts);
        final UserLayer ul = new UserLayer();
        ul.getUserStyle().add(us);
        sld.getNamedLayerOrUserLayer().add(ul);

        mapLayers2.add(new MapLayer(new DataReference("postgis_test:my_otherlayer"), sld));
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
        assertEquals(RESULT_MARSHALLING_WITH_SLD, result.trim());
    }

    /**
     * Test for the unmarshalling process of a string-representation of a {@link MapContext}.
     *
     * @throws JAXBException
     */
    @Test
    public void testUnmarshallingWithoutSLD() throws JAXBException {
        final StringReader sr = new StringReader(RESULT_MARSHALLING_WITHOUT_SLD);
        final Object result = unmarshaller.unmarshal(new StringReader(RESULT_MARSHALLING_WITHOUT_SLD));
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

    /**
     * Test for the unmarshalling process of a string-representation of a {@link MapContext}.
     *
     * @throws JAXBException
     */
    @Test
    public void testUnmarshallingWithSLD() throws JAXBException {
        final StringReader sr = new StringReader(RESULT_MARSHALLING_WITH_SLD);
        final Object result = unmarshaller.unmarshal(new StringReader(RESULT_MARSHALLING_WITH_SLD));
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
        assertTrue(ml0.getStyle().getNamedLayerOrUserLayer().get(0) instanceof UserLayer);
        final UserLayer ul  = (UserLayer) ml0.getStyle().getNamedLayerOrUserLayer().get(0);
        assertTrue(ul.getUserStyle().get(0).getFeatureTypeStyleOrCoverageStyleOrOnlineResource().get(0) instanceof FeatureTypeStyleType);
        final FeatureTypeStyleType fts = (FeatureTypeStyleType)ul.getUserStyle().get(0).getFeatureTypeStyleOrCoverageStyleOrOnlineResource().get(0);
        assertEquals("ft_test", fts.getName());

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
