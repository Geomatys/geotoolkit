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

import java.awt.geom.NoninvertibleTransformException;
import java.net.URL;
import java.awt.Dimension;
import java.net.MalformedURLException;
import javax.xml.bind.JAXBException;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wms.xml.WMSVersion;
import org.geotoolkit.wms.v111.GetFeatureInfo111;

import org.junit.Test;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSMapLayerTest {

    private final WebMapServer SERVER_111;
    private final WebMapServer SERVER_130;

    public WMSMapLayerTest() throws MalformedURLException, JAXBException {
        SERVER_111 = new MockWebMapServer(WMSVersion.v111);
        SERVER_130 = new MockWebMapServer(WMSVersion.v130);
    }

    /**
     * This test checks that in the case we use CRS:84 in WMS 1.1.1
     * the URL CRS will be changed by EPSG:4326 but with no coordinate change.
     * since EPSG:4326 in 1.1.1 is actually a CRS:84 .
     */
    @Test
    public void test_v111_GetMap_CRS84() throws TransformException, MalformedURLException, FactoryException{
        final WMSMapLayer layer = new WMSMapLayer(SERVER_111, "BlueMarble");

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG:4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0,-90.0,180.0,90.0"));
    }

    /**
     * This test checks that in the case we use EPSG:4326 in WMS 1.1.1
     * the URL BBOX Coordinate must be inverted .
     */
    @Test
    public void test_v111_GetMap_EPSG4326() throws MalformedURLException, TransformException, FactoryException {

        final WMSMapLayer layer = new WMSMapLayer(SERVER_111, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG:4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0,-90.0,180.0,90.0"));
    }
        
    /**
     * This test checks that in the case we use EPSG:32761 in WMS 1.1.1
     */
    @Test
    public void test_v111_GetMap_EPSG32761() throws MalformedURLException, TransformException, FactoryException {

        final WMSMapLayer layer = new WMSMapLayer(SERVER_111, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:32761"));
        env.setRange(0, -882900.0, 844300.0); // Lat
        env.setRange(1, 1974600.0, 3701800.0); // Lon

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG:32761"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=1974600.0,-882900.0,3701800.0,844300.0"));
    }
    
    /**
     * This test checks that in the case we use CRS:84 in WMS 1.3.0
     * the URL BBOX Coordinate are not changed.
     */
    @Test
    public void test_v130_GetMap_CRS84() throws MalformedURLException, TransformException, FactoryException {

        final WMSMapLayer layer = new WMSMapLayer(SERVER_130, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("CRS")).startsWith("CRS=CRS:84"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0,-90.0,180.0,90.0"));
    }
    
    /**
     * This test checks that in the case we use EPSG:4326 in WMS 1.3.0
     * the URL BBOX Coordinate are not changed.
     */
    @Test
    public void test_v130_GetMap_EPSG4326() throws MalformedURLException, TransformException, FactoryException {

        final WMSMapLayer layer = new WMSMapLayer(SERVER_130, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("CRS")).startsWith("CRS=EPSG:4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-90.0,-180.0,90.0,180.0"));
    }
    
    /**
     * This test checks that in the case we use EPSG:32761 in WMS 1.3.0
     */
    @Test
    public void test_v130_GetMap_EPSG32761() throws MalformedURLException, TransformException, FactoryException {

        final WMSMapLayer layer = new WMSMapLayer(SERVER_130, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:32761"));
        env.setRange(0, -882900.0, 844300.0); // Lat
        env.setRange(1, 1974600.0, 3701800.0); // Lon

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("CRS")).startsWith("CRS=EPSG:32761"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-882900.0,1974600.0,844300.0,3701800.0"));
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
    public void test_v111_GetFeatureInfo_EPSG4326() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v111);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return true;
            }
        };

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0,-90.0,180.0,90.0"));
        assertTrue(sUrl.contains("SRS=EPSG:4326"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=360"));
        assertTrue(sUrl.contains("HEIGHT=180"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("X=140"));
        assertTrue(sUrl.contains("Y=250"));
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
    public void test_v130_GetFeatureInfo_EPSG4326() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v130);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return true;
            }
        };

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-90.0,-180.0,90.0,180.0"));
        assertTrue(sUrl.contains("CRS=EPSG:4326"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=360"));
        assertTrue(sUrl.contains("HEIGHT=180"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("I=140"));
        assertTrue(sUrl.contains("J=250"));
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
    public void test_v130_GetFeatureInfo_CRS84() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v130);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return true;
            }
        };

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0,-90.0,180.0,90.0"));
        assertTrue(sUrl.contains("CRS=CRS:84"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=360"));
        assertTrue(sUrl.contains("HEIGHT=180"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("I=140"));
        assertTrue(sUrl.contains("J=250"));
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
    public void test_v111_GetFeatureInfo_Reproject_EPSG4326_to_CRS84() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v111);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return false;
            }
            @Override
            protected CoordinateReferenceSystem findOriginalCRS() throws FactoryException {
                return CRS.decode("EPSG:4326");
            }
        };
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0,-90.0,180.0,90.0"));
        assertTrue(sUrl.contains("SRS=EPSG:4326"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=360"));
        assertTrue(sUrl.contains("HEIGHT=180"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("X=140"));
        assertTrue(sUrl.contains("Y=250"));
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
    public void test_v130_GetFeatureInfo_Reproject_EPSG4326_to_CRS84() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v130);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return false;
            }
            @Override
            protected CoordinateReferenceSystem findOriginalCRS() throws FactoryException {
                return CRS.decode("EPSG:4326");
            }
        };
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final Dimension rect = new Dimension(360, 180);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-90.0,-180.0,90.0,180.0"));
        assertTrue(sUrl.contains("CRS=EPSG:4326"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=360"));
        assertTrue(sUrl.contains("HEIGHT=180"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("I=140"));
        assertTrue(sUrl.contains("J=250"));
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
    public void test_v111_GetFeatureInfo_Reproject_CRS84_to_EPSG3857() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v111);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return false;
            }
            @Override
            protected CoordinateReferenceSystem findOriginalCRS() throws FactoryException {
                return CRS.decode("CRS:84");
            }
        };
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:3857"));
        env.setRange(0, -2.0037507067162E7, 2.0037507067162E7);
        env.setRange(1, -2.0037507067162E7, 2.0037507067162E7);
        
        final Dimension rect = new Dimension(512, 512);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();
        
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-179.9999885408455,-85.05112779126263,179.9999885408455,85.05112779126262")); 
        assertTrue(sUrl.contains("SRS=EPSG:4326"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=512"));
        assertTrue(sUrl.contains("HEIGHT=512"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("X=140"));
        assertTrue(sUrl.contains("Y=243"));
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
    public void test_v130_GetFeatureInfo_Reproject_CRS84_to_EPSG3857() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v130);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return false;
            }
            @Override
            protected CoordinateReferenceSystem findOriginalCRS() throws FactoryException {
                return CRS.decode("CRS:84");
            }
        };
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:3857"));
        env.setRange(0, -2.0037507067162E7, 2.0037507067162E7);
        env.setRange(1, -2.0037507067162E7, 2.0037507067162E7);
        final Dimension rect = new Dimension(512, 512);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();        
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-179.9999885408455,-85.05112779126263,179.9999885408455,85.05112779126262")); 
        assertTrue(sUrl.contains("CRS=CRS:84"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=512"));
        assertTrue(sUrl.contains("HEIGHT=512"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("I=140"));
        assertTrue(sUrl.contains("J=243"));
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
    public void test_v111_GetFeatureInfo_Reproject_EPSG4326_to_EPSG3857() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v111);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return false;
            }
            @Override
            protected CoordinateReferenceSystem findOriginalCRS() throws FactoryException {
                return CRS.decode("EPSG:4326");
            }
        };
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:3857"));
        //-2.0037507067162E7,-2.0037507067162E7,2.0037507067162E7,2.0037507067162E7
        env.setRange(0, -2.0037507067162E7, 2.0037507067162E7);
        env.setRange(1, -2.0037507067162E7, 2.0037507067162E7);
        final Dimension rect = new Dimension(512, 512);

        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-179.9999885408455,-85.05112779126263,179.9999885408455,85.05112779126262")); 
        assertTrue(sUrl.contains("SRS=EPSG:4326"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=512"));
        assertTrue(sUrl.contains("HEIGHT=512"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("X=140"));
        assertTrue(sUrl.contains("Y=243"));
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
    public void test_v130_GetFeatureInfo_Reproject_EPSG4326_to_EPSG3857() throws NoSuchAuthorityCodeException,
            FactoryException, MalformedURLException, TransformException, NoninvertibleTransformException {

        final WebMapServer server = new WebMapServer(new URL("http://test.com"), WMSVersion.v130);

        final WMSMapLayer layer = new WMSMapLayer(server, "test"){
            @Override
            protected boolean supportCRS(CoordinateReferenceSystem crs) throws FactoryException {
                return false;
            }
            @Override
            protected CoordinateReferenceSystem findOriginalCRS() throws FactoryException {
                return CRS.decode("EPSG:4326");
            }
        };
        layer.setUseLocalReprojection(true);

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:3857"));
        env.setRange(0, -2.0037507067162E7, 2.0037507067162E7);
        env.setRange(1, -2.0037507067162E7, 2.0037507067162E7);
        final Dimension rect = new Dimension(512, 512);
        
        final URL url = layer.queryFeatureInfo(env, rect, 140, 250, new String[]{"test"}, "gml", 1);

        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-85.05112779126263,-179.9999885408455,85.05112779126262,179.9999885408455")); 
        assertTrue(sUrl.contains("CRS=EPSG:4326"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=512"));
        assertTrue(sUrl.contains("HEIGHT=512"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("I=140"));
        assertTrue(sUrl.contains("J=243"));
    }

}
