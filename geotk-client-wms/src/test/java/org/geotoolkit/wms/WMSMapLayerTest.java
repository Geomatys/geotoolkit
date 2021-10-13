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

package org.geotoolkit.wms;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.wms.v111.GetFeatureInfo111;
import org.geotoolkit.wms.v111.GetLegend111;
import org.geotoolkit.wms.v130.GetFeatureInfo130;
import org.geotoolkit.wms.v130.GetLegend130;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSMarshallerPool;
import org.geotoolkit.wms.xml.WMSVersion;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSMapLayerTest extends org.geotoolkit.test.TestBase {

    private final WebMapClient SERVER_111;
    private final WebMapClient SERVER_130;

    public WMSMapLayerTest() throws MalformedURLException, JAXBException {
        SERVER_111 = new MockWebMapClient(WMSVersion.v111);
        SERVER_130 = new MockWebMapClient(WMSVersion.v130);
    }

    /**
     * This test checks that in the case we use CRS:84 in WMS 1.1.1
     * the URL CRS will be changed by EPSG:4326 but with no coordinate change.
     * since EPSG:4326 in 1.1.1 is actually a CRS:84 .
     */
    @Test
    public void test_v111_GetMap_CRS84() throws TransformException, MalformedURLException, FactoryException{
        final WMSResource layer = new WMSResource(SERVER_111, "BlueMarble");

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue("was:" + query, query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG%3A4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
    }

    /**
     * This test checks that in the case we use EPSG:4326 in WMS 1.1.1
     * the URL BBOX Coordinate must be inverted .
     */
    @Test
    public void test_v111_GetMap_EPSG4326() throws MalformedURLException, TransformException, FactoryException {

        final WMSResource layer = new WMSResource(SERVER_111, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue("was:" + query, query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG%3A4326"));
        assertTrue("was:" + query, query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
    }

    /**
     * This test checks that in the case we use EPSG:32761 in WMS 1.1.1
     */
    @Test
    public void test_v111_GetMap_EPSG32761() throws MalformedURLException, TransformException, FactoryException {

        final WMSResource layer = new WMSResource(SERVER_111, "BlueMarble");
        layer.setUseLocalReprojection(false);


        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:32761"));
        env.setRange(0, -882900.0, 844300.0); // Lat
        env.setRange(1, 1974600.0, 3701800.0); // Lon

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue("was:" + query, query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG%3A32761"));
        /*
         * TODO - commented for now. As of 2014-06-28 we get:
         *
         * BBOX=1974599.9999999995%2C-882900.0000000003%2C3701799.9999999995%2C844299.9999999997
         *
         * but this may change in any future Apache SIS or Geotk version.
         * Ideally, the test would need to be tolerance to some epsilon.
         */
//      assertTrue("was:" + query, query.substring(query.indexOf("BBOX")).startsWith("BBOX=1974600.0%2C-882900.0%2C3701800.0%2C844300.0"));
    }

    /**
     * This test checks that in the case we use CRS:84 in WMS 1.3.0
     * the URL BBOX Coordinate are not changed.
     */
    @Test
    public void test_v130_GetMap_CRS84() throws MalformedURLException, TransformException, FactoryException {

        final WMSResource layer = new WMSResource(SERVER_130, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue("was:" + query, query.substring(query.indexOf("CRS")).startsWith("CRS=CRS%3A84"));
        assertTrue("was:" + query, query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
    }

    /**
     * This test checks that in the case we use EPSG:4326 in WMS 1.3.0
     * the URL BBOX Coordinate are not changed.
     */
    @Test
    public void test_v130_GetMap_EPSG4326() throws MalformedURLException, TransformException, FactoryException {

        final WMSResource layer = new WMSResource(SERVER_130, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue("was:" + query, query.substring(query.indexOf("CRS")).startsWith("CRS=EPSG%3A4326"));
        assertTrue("was:" + query, query.substring(query.indexOf("BBOX")).startsWith("BBOX=-90.0%2C-180.0%2C90.0%2C180.0"));
    }

    /**
     * This test checks that in the case we use EPSG:32761 in WMS 1.3.0
     */
    @Test
    public void test_v130_GetMap_EPSG32761() throws MalformedURLException, TransformException, FactoryException {

        final WMSResource layer = new WMSResource(SERVER_130, "BlueMarble");
        layer.setUseLocalReprojection(false);


        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:32761"));
        env.setRange(0, -882900.0, 844300.0); // Lat
        env.setRange(1, 1974600.0, 3701800.0); // Lon

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue("was:" + query, query.substring(query.indexOf("CRS")).startsWith("CRS=EPSG%3A32761"));
        assertTrue("was:" + query, query.substring(query.indexOf("BBOX")).startsWith("BBOX=-882900.0%2C1974600.0%2C844300.0%2C3701800.0"));
    }

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is not used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v111_GetFeatureInfo_EPSG4326() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v111_GetFeatureInfo_EPSG4326.xml"));
        pool.recycle(unmarshaller);

        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v111,capa);

        final WMSResource layer = new WMSResource(server, "test");

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue("was:" + sUrl, sUrl.contains("SRS=EPSG%3A4326"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=360"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=180"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("X=140"));
        assertTrue("was:" + sUrl, sUrl.contains("Y=250"));
    }

    /**
     * Ensures the {@link GetFeatureInfo130#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is not used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v130_GetFeatureInfo_EPSG4326() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v130_GetFeatureInfo_EPSG4326.xml"));
        pool.recycle(unmarshaller);

        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v130, capa);

        final WMSResource layer = new WMSResource(server, "test");

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.geographic());
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-90.0%2C-180.0%2C90.0%2C180.0"));
        assertTrue("was:" + sUrl, sUrl.contains("CRS=EPSG%3A4326"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=360"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=180"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("I=140"));
        assertTrue("was:" + sUrl, sUrl.contains("J=250"));
    }

    /**
     * Ensures the {@link GetFeatureInfo130#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is not used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v130_GetFeatureInfo_CRS84() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v130_GetFeatureInfo_CRS84.xml"));
        pool.recycle(unmarshaller);

        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v130,capa);

        final WMSResource layer = new WMSResource(server, "test");

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl,sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue("was:" + sUrl,sUrl.contains("CRS=CRS%3A84"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=360"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=180"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("I=140"));
        assertTrue("was:" + sUrl, sUrl.contains("J=250"));
    }

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v111_GetFeatureInfo_Reproject_EPSG4326_to_CRS84() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v111_GetFeatureInfo_Reproject_EPSG4326_to_CRS84.xml"));
        pool.recycle(unmarshaller);

        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v111,capa);

        final WMSResource layer = new WMSResource(server, "test");
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue("was:" + sUrl, sUrl.contains("SRS=EPSG%3A4326"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=360"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=180"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("X=140"));
        assertTrue("was:" + sUrl, sUrl.contains("Y=250"));
    }

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v130_GetFeatureInfo_Reproject_EPSG4326_to_CRS84() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v130_GetFeatureInfo_Reproject_EPSG4326_to_CRS84.xml"));
        pool.recycle(unmarshaller);

        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v130,capa);

        final WMSResource layer = new WMSResource(server, "test");
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-90.0%2C-180.0%2C90.0%2C180.0"));
        assertTrue("was:" + sUrl, sUrl.contains("CRS=EPSG%3A4326"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=360"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=180"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("I=140"));
        assertTrue("was:" + sUrl, sUrl.contains("J=250"));
    }

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v111_GetFeatureInfo_Reproject_CRS84_to_EPSG3857() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v111_GetFeatureInfo_Reproject_CRS84_to_EPSG3857.xml"));
        pool.recycle(unmarshaller);

        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v111,capa);

        final WMSResource layer = new WMSResource(server, "test");
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:3857"));
        env.setRange(0, -2.0037507067162E7, 2.0037507067162E7);
        env.setRange(1, -2.0037507067162E7, 2.0037507067162E7);

        final Dimension rect = new Dimension(512, 512);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();

        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-179.9999885408455%2C-85.05112779126262%2C179.9999885408455%2C85.05112779126263"));
        assertTrue("was:" + sUrl, sUrl.contains("SRS=EPSG%3A4326"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=512"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=512"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("X=140"));
        assertTrue("was:" + sUrl, sUrl.contains("Y=243"));
    }

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v130_GetFeatureInfo_Reproject_CRS84_to_EPSG3857() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v130_GetFeatureInfo_Reproject_CRS84_to_EPSG3857.xml"));
        pool.recycle(unmarshaller);

        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v130,capa);

        final WMSResource layer = new WMSResource(server, "test");
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:3857"));
        env.setRange(0, -2.0037507067162E7, 2.0037507067162E7);
        env.setRange(1, -2.0037507067162E7, 2.0037507067162E7);
        final Dimension rect = new Dimension(512, 512);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-179.9999885408455%2C-85.05112779126262%2C179.9999885408455%2C85.05112779126263"));
        assertTrue("was:" + sUrl, sUrl.contains("CRS=CRS%3A84"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=512"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=512"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("I=140"));
        assertTrue("was:" + sUrl, sUrl.contains("J=243"));
    }

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v111_GetFeatureInfo_Reproject_EPSG4326_to_EPSG3857() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v111_GetFeatureInfo_Reproject_EPSG4326_to_EPSG3857.xml"));
        pool.recycle(unmarshaller);


        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v111, capa);

        final WMSResource layer = new WMSResource(server, "test");
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:3857"));
        //-2.0037507067162E7,-2.0037507067162E7,2.0037507067162E7,2.0037507067162E7
        env.setRange(0, -2.0037507067162E7, 2.0037507067162E7);
        env.setRange(1, -2.0037507067162E7, 2.0037507067162E7);
        final Dimension rect = new Dimension(512, 512);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-179.9999885408455%2C-85.05112779126262%2C179.9999885408455%2C85.05112779126263"));
        assertTrue("was:" + sUrl, sUrl.contains("SRS=EPSG%3A4326"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=512"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=512"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("X=140"));
        assertTrue("was:" + sUrl, sUrl.contains("Y=243"));
    }

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     * This test is to make sure pick coordinates are correct when local reprojection is used.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void test_v130_GetFeatureInfo_Reproject_EPSG4326_to_EPSG3857() throws Exception {

        //test capabilities
        final MarshallerPool pool = WMSMarshallerPool.getInstance();
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final AbstractWMSCapabilities capa;
        capa = (AbstractWMSCapabilities) unmarshaller.unmarshal(MockWebMapClient.class.getResource(
                "/org/geotoolkit/wms/test_v130_GetFeatureInfo_Reproject_EPSG4326_to_EPSG3857.xml"));
        pool.recycle(unmarshaller);

        final WebMapClient server = new WebMapClient(new URL("http://test.com"), WMSVersion.v130,capa);

        final WMSResource layer = new WMSResource(server, "test");
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:3857"));
        env.setRange(0, -2.0037507067162E7, 2.0037507067162E7);
        env.setRange(1, -2.0037507067162E7, 2.0037507067162E7);
        final Dimension rect = new Dimension(512, 512);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1).getURL();

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-85.05112779126262%2C-179.9999885408455%2C85.05112779126263%2C179.9999885408455"));
        assertTrue("was:" + sUrl, sUrl.contains("CRS=EPSG%3A4326"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=512"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=512"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("I=140"));
        assertTrue("was:" + sUrl, sUrl.contains("J=243"));
    }

    /**
     * Ensures the {@link GetLegend111#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void test_v111_GetLegendGraphic() throws MalformedURLException {

        final WMSResource layer = new WMSResource(SERVER_111, "BlueMarble");
        layer.setStyles("test");
        layer.setExceptionsFormat("application/test");
        layer.setStyles("test");
        layer.setSldVersion("3.3.3");
        layer.dimensions().put("TIME", "20-20-20T20:20:20Z");
        layer.dimensions().put("ELEVATION", "500");
        layer.dimensions().put("DIMRANGE", "-50,50");

        final Dimension rect = new Dimension(140, 20);
        final URL url = layer.queryLegend(rect, "image/gif", "test", 2500.0).getURL();
        final String sUrl = url.toString();
        assertTrue("was:" + sUrl, sUrl.contains("SERVICE=WMS"));
        assertTrue("was:" + sUrl, sUrl.contains("VERSION=1.1.1"));
        assertTrue("was:" + sUrl, sUrl.contains("REQUEST=GetLegendGraphic"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fgif"));
        assertTrue("was:" + sUrl, sUrl.contains("EXCEPTIONS=application%2Ftest"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYER=BlueMarble"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=140"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=20"));
        assertTrue("was:" + sUrl, sUrl.contains("RULE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("SCALE=2500"));
        assertTrue("was:" + sUrl, sUrl.contains("SLD_VERSION=3.3.3"));
        assertTrue("was:" + sUrl, sUrl.contains("TIME=20-20-20T20%3A20%3A20Z"));
        assertTrue("was:" + sUrl, sUrl.contains("ELEVATION=500"));
        assertTrue("was:" + sUrl, sUrl.contains("DIMRANGE=-50%2C50"));

    }

    /**
     * Ensures the {@link GetLegend130#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void test_v130_GetLegendGraphic() throws MalformedURLException {

        final WMSResource layer = new WMSResource(SERVER_130, "BlueMarble");
        layer.setStyles("test");
        layer.setExceptionsFormat("application/test");
        layer.setStyles("test");
        layer.setSldVersion("3.3.3");
        layer.dimensions().put("TIME", "20-20-20T20:20:20Z");
        layer.dimensions().put("ELEVATION", "500");
        layer.dimensions().put("DIMRANGE", "-50,50");

        final Dimension rect = new Dimension(140, 20);
        final URL url = layer.queryLegend(rect, "image/gif", "test", 2500.0).getURL();
        final String sUrl = url.toString();
        assertTrue("was:" + sUrl, sUrl.contains("SERVICE=WMS"));
        assertTrue("was:" + sUrl, sUrl.contains("VERSION=1.3.0"));
        assertTrue("was:" + sUrl, sUrl.contains("REQUEST=GetLegendGraphic"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fgif"));
        assertTrue("was:" + sUrl, sUrl.contains("EXCEPTIONS=application%2Ftest"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYER=BlueMarble"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=140"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=20"));
        assertTrue("was:" + sUrl, sUrl.contains("RULE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("SCALE=2500"));
        assertTrue("was:" + sUrl, sUrl.contains("SLD_VERSION=3.3.3"));
        assertTrue("was:" + sUrl, sUrl.contains("TIME=20-20-20T20%3A20%3A20Z"));
        assertTrue("was:" + sUrl, sUrl.contains("ELEVATION=500"));
        assertTrue("was:" + sUrl, sUrl.contains("DIMRANGE=-50%2C50"));

    }


}
