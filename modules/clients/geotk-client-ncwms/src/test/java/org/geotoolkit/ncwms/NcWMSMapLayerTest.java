/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.ncwms;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.Dimension;
import java.net.MalformedURLException;
import javax.xml.bind.JAXBException;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.ncwms.map.NcWMSMapLayer;
import org.geotoolkit.wms.xml.WMSVersion;

import org.junit.Test;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;

/**
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public class NcWMSMapLayerTest {

    private final NcWebMapClient SERVER_111;
    private final NcWebMapClient SERVER_130;

    public NcWMSMapLayerTest() throws MalformedURLException, JAXBException {
        SERVER_111 = new MockWebMapClient(WMSVersion.v111);
        SERVER_130 = new MockWebMapClient(WMSVersion.v130);
    }

    /**
     * This test checks if the ncWMS parameters are not added to the get map
     * request when they are null
     */
    @Test
    public void test_GetMapWithNullValues() throws TransformException,
            MalformedURLException, FactoryException{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");

        final String query = layer.getCoverageReference().query(env, new Dimension(800, 600)).toString();
        assertFalse(query.contains("OPACITY="));
        assertFalse(query.contains("COLORSCALERANGE="));
        assertFalse(query.contains("NUMCOLORBANDS="));
        assertFalse(query.contains("LOGSCALE="));
    }

    /**
     * This test chacks if the ncWMS parameters are added to the get map request
     */
    @Test
    public void test_GetMap() throws TransformException, MalformedURLException,
            FactoryException{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");
        layer.getCoverageReference().setOpacity(new Integer(60));
        layer.getCoverageReference().dimensions().put("COLORSCALERANGE","auto");
        layer.getCoverageReference().setNumColorBands(125);
        layer.getCoverageReference().setLogScale(true);

        final String query = layer.getCoverageReference().query(env, new Dimension(800, 600)).toString();
        assertTrue("was:" + query, query.contains("OPACITY=60"));
        assertTrue("was:" + query, query.contains("COLORSCALERANGE=auto"));
        assertTrue("was:" + query, query.contains("NUMCOLORBANDS=125"));
        assertTrue("was:" + query, query.contains("LOGSCALE=true"));
    }

    /**
     * This test checks if the ncWMS parameters are not added to the get feature
     * info request when they are null
     */
    @Test
    public void test_GetFeatureInfoWithNullValues() throws TransformException,
            MalformedURLException, FactoryException{

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");

        final String query = layer.getCoverageReference().queryFeatureInfo(env, new Dimension(360, 180),
                140, 250, new String[]{"BlueMarble"}, "gml", 1).getURL().toString();
        assertTrue("was:" + query, query.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + query, query.contains("QUERY_LAYERS=BlueMarble"));
        assertTrue("was:" + query, query.contains("X=140"));
        assertTrue("was:" + query, query.contains("Y=250"));
        assertFalse(query.contains("OPACITY=60"));
        assertFalse(query.contains("COLORSCALERANGE=auto"));
        assertFalse(query.contains("NUMCOLORBANDS=125"));
        assertFalse(query.contains("LOGSCALE=true"));
    }

    /**
     * This test checks if the ncWMS parameters are added to the get feature
     * info request
     */
    @Test
    public void test_GetFeatureInfo() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException,
            NoninvertibleTransformException {

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");
        layer.getCoverageReference().setOpacity(new Integer(60));
        layer.getCoverageReference().dimensions().put("COLORSCALERANGE","auto");
        layer.getCoverageReference().setNumColorBands(125);
        layer.getCoverageReference().setLogScale(true);

        final String query = layer.getCoverageReference().queryFeatureInfo(env, new Dimension(360, 180),
                140, 250, new String[]{"BlueMarble"}, "gml", 1).getURL().toString();
        assertTrue("was:" + query, query.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + query, query.contains("QUERY_LAYERS=BlueMarble"));
        assertTrue("was:" + query, query.contains("X=140"));
        assertTrue("was:" + query, query.contains("Y=250"));
        assertTrue("was:" + query, query.contains("OPACITY=60"));
        assertTrue("was:" + query, query.contains("COLORSCALERANGE=auto"));
        assertTrue("was:" + query, query.contains("NUMCOLORBANDS=125"));
        assertTrue("was:" + query, query.contains("LOGSCALE=true"));
    }

    /**
     * This test checks if the ncWMS parameters are added to the GetLegendGraphic
     * request
     */
    @Test
    public void test_GetLegend() throws MalformedURLException {

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");
        layer.getCoverageReference().dimensions().put("COLORSCALERANGE","auto");
        layer.getCoverageReference().setNumColorBands(125);
        layer.getCoverageReference().setLogScale(true);
        layer.getCoverageReference().setStyles("style_name/palette_name");

        final String query = layer.getCoverageReference().queryLegend(new Dimension(360, 180),
                "image/png", "test", 100.0).getURL().toString();
        assertTrue("was:" + query, query.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + query, query.contains("WIDTH=360"));
        assertTrue("was:" + query, query.contains("HEIGHT=180"));
        assertTrue("was:" + query, query.contains("RULE=test"));
        assertTrue("was:" + query, query.contains("SCALE=100"));
        assertTrue("was:" + query, query.contains("PALETTE=palette_name"));
        assertTrue("was:" + query, query.contains("COLORSCALERANGE=auto"));
        assertTrue("was:" + query, query.contains("NUMCOLORBANDS=125"));
        assertTrue("was:" + query, query.contains("LOGSCALE=true"));
    }

    /**
     * This test checks if the ncWMS parameters are added to the get metadata
     * request
     */
    @Test
    public void test_GetMetadataLayerDetails() throws MalformedURLException {

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");

        // Test mandatory values
        String query = layer.getCoverageReference().queryMetadataLayerDetails().toString();
        assertTrue("was:" + query, query.contains("request=GetMetadata"));
        assertTrue("was:" + query, query.contains("item=layerDetails"));
        assertTrue("was:" + query, query.contains("layerName=BlueMarble"));


        // Test all values
        layer.getCoverageReference().dimensions().put("TIME", "10-10-10T01:00:00Z");
        query = layer.getCoverageReference().queryMetadataLayerDetails().toString();
        assertTrue("was:" + query, query.contains("item=layerDetails"));
        assertTrue("was:" + query, query.contains("layerName=BlueMarble"));
        assertTrue("was:" + query, query.contains("time=10-10-10T01%3A00%3A00Z"));
    }

    /**
     * This test checks if the ncWMS parameters are added to the get metadata
     * request
     */
    @Test
    public void test_GetMetadataAnimationTimesteps() throws MalformedURLException {

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");

        // Test mandatory values
        String query = layer.getCoverageReference().queryMetadataAnimationTimesteps("10-10-10T01:00:00Z",
                "11-10-10T01:00:00Z").toString();
        assertTrue("was:" + query, query.contains("request=GetMetadata"));
        assertTrue("was:" + query, query.contains("item=animationTimesteps"));
        assertTrue("was:" + query, query.contains("layerName=BlueMarble"));
        assertTrue("was:" + query, query.contains("start=10-10-10T01%3A00%3A00Z"));
        assertTrue("was:" + query, query.contains("end=11-10-10T01%3A00%3A00Z"));

    }

    /**
     * This test checks if the ncWMS parameters are added to the get metadata
     * request
     */
    @Test
    public void test_GetMetadataTimesteps() throws MalformedURLException {

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");
        layer.getCoverageReference().dimensions().put("TIME", "10-10-10T01:00:00Z");

        // Test mandatory values
        String query = layer.getCoverageReference().queryMetadataTimesteps().toString();
        assertTrue("was:" + query, query.contains("request=GetMetadata"));
        assertTrue("was:" + query, query.contains("item=timesteps"));
        assertTrue("was:" + query, query.contains("layerName=BlueMarble"));
        assertTrue("was:" + query, query.contains("day=10-10-10T01%3A00%3A00Z"));
    }

    /**
     * This test checks if the ncWMS parameters are added to the get metadata
     * request
     */
    @Test
    public void test_GetMetadataMinmax() throws MalformedURLException {

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");

        // Test mandatory values
        String query = layer.getCoverageReference().queryMetadataMinmax("epsg:4326","-2,-1,1,2","300","400").toString();
        assertTrue("was:" + query, query.contains("request=GetMetadata"));
        assertTrue("was:" + query, query.contains("item=minmax"));
        assertTrue("was:" + query, query.contains("layers=BlueMarble"));
        assertTrue("was:" + query, query.contains("crs=epsg%3A4326"));
        assertTrue("was:" + query, query.contains("bbox=-2%2C-1%2C1%2C2"));
        assertTrue("was:" + query, query.contains("width=300"));
        assertTrue("was:" + query, query.contains("height=400"));

    }

    /**
     * This test checks if the ncWMS parameters are added to the get metadata
     * request
     */
    @Test
    public void test_GetTransect() throws MalformedURLException {

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");

        // Test mandatory values
        String query = layer.getCoverageReference().queryTransect("CRS:84", "1 2,3 4", "image/png")
                .toString();
        assertTrue("was:" + query, query.contains("REQUEST=GetTransect"));
        assertTrue("was:" + query, query.contains("LAYER=BlueMarble"));
        assertTrue("was:" + query, query.contains("CRS=CRS%3A84"));
        assertTrue("was:" + query, query.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + query, query.contains("LINESTRING=1+2%2C3+4"));

        // Test optional values
        layer.getCoverageReference().dimensions().put("TIME", "10-10-10T10:00:00Z");
        layer.getCoverageReference().dimensions().put("ELEVATION", "550");
        query = layer.getCoverageReference().queryTransect("CRS:84", "1 2,3 4", "image/png").toString();
        assertTrue("was:" + query, query.contains("TIME=10-10-10T10%3A00%3A00Z"));
        assertTrue("was:" + query, query.contains("ELEVATION=550"));
    }

    /**
     * This test checks if the ncWMS parameters are added to the get vertical profile
     * request
     */
    @Test
    public void test_GetVerticalProfile() throws MalformedURLException {

        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");

        // Test mandatory values
        String query = layer.getCoverageReference().queryVerticalProfile("CRS:84", 1, 2,  "image/png")
                .toString();
        System.out.println("query = " + query);
        assertTrue("was:" + query, query.contains("REQUEST=GetVerticalProfile"));
        assertTrue("was:" + query, query.contains("LAYER=BlueMarble"));
        assertTrue("was:" + query, query.contains("CRS=CRS%3A84"));
        assertTrue("was:" + query, query.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + query, query.contains("POINT=1.0+2.0"));

        // Test optional values
        layer.getCoverageReference().dimensions().put("TIME", "10-10-10T10:00:00Z");
        query = layer.getCoverageReference().queryVerticalProfile("CRS:84", 1, 2,  "image/png").toString();
        assertTrue("was:" + query, query.contains("TIME=10-10-10T10%3A00%3A00Z"));

    }
}
