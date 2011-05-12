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

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.ncwms.map.NcWMSMapLayer;
import org.geotoolkit.wms.WebMapServer;
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

    private final WebMapServer SERVER_111;
    private final WebMapServer SERVER_130;

    public NcWMSMapLayerTest() throws MalformedURLException, JAXBException {
        SERVER_111 = new MockWebMapServer(WMSVersion.v111);
        SERVER_130 = new MockWebMapServer(WMSVersion.v130);
    }
    
    /**
     * This test checks if the ncWMS parameters are not added to the get map 
     * request when they are null
     */
    @Test
    public void test_GetMapWithNullValues() throws TransformException, 
            MalformedURLException, FactoryException{        

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);               
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "BlueMarble");
        
        final String query = layer.query(env, new Dimension(800, 600)).toString();        
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

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        layer.setOpacity(new Integer(60));
        layer.dimensions().put("COLORSCALERANGE","auto");
        layer.setNumColorBands(125);
        layer.setLogScale(true);
        
        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue(query.contains("OPACITY=60"));
        assertTrue(query.contains("COLORSCALERANGE=auto"));
        assertTrue(query.contains("NUMCOLORBANDS=125"));
        assertTrue(query.contains("LOGSCALE=true"));
    }
    
    /**
     * This test checks if the ncWMS parameters are not added to the get feature 
     * info request when they are null
     */
    @Test
    public void test_GetFeatureInfoWithNullValues() throws TransformException, 
            MalformedURLException, FactoryException{

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        
        final String query = layer.queryFeatureInfo(env, new Dimension(360, 180), 
                140, 250, new String[]{"test"}, "gml", 1).toString();
        assertTrue(query.contains("INFO_FORMAT=gml"));
        assertTrue(query.contains("QUERY_LAYERS=test"));
        assertTrue(query.contains("X=140"));
        assertTrue(query.contains("Y=250"));
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

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        layer.setOpacity(new Integer(60));
        layer.dimensions().put("COLORSCALERANGE","auto");
        layer.setNumColorBands(125);
        layer.setLogScale(true);        
        
        final String query = layer.queryFeatureInfo(env, new Dimension(360, 180),
                140, 250, new String[]{"test"}, "gml", 1).toString();
        assertTrue(query.contains("INFO_FORMAT=gml"));
        assertTrue(query.contains("QUERY_LAYERS=test"));
        assertTrue(query.contains("X=140"));
        assertTrue(query.contains("Y=250"));
        assertTrue(query.contains("OPACITY=60"));
        assertTrue(query.contains("COLORSCALERANGE=auto"));
        assertTrue(query.contains("NUMCOLORBANDS=125"));
        assertTrue(query.contains("LOGSCALE=true"));
    }
    
    /**
     * This test checks if the ncWMS parameters are added to the GetLegendGraphic 
     * request
     */
    @Test
    public void test_GetLegend() throws MalformedURLException {
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        layer.dimensions().put("COLORSCALERANGE","auto");
        layer.setNumColorBands(125);
        layer.setLogScale(true);    
        layer.setStyles("style_name/palette_name");         
        
        final String query = layer.queryLegend(new Dimension(360, 180),
                "image/png", "test", 100.0).toString();
        assertTrue(query.contains("FORMAT=image/png"));
        assertTrue(query.contains("WIDTH=360"));
        assertTrue(query.contains("HEIGHT=180"));
        assertTrue(query.contains("RULE=test"));
        assertTrue(query.contains("SCALE=100"));
        assertTrue(query.contains("PALETTE=palette_name"));
        assertTrue(query.contains("COLORSCALERANGE=auto"));
        assertTrue(query.contains("NUMCOLORBANDS=125"));
        assertTrue(query.contains("LOGSCALE=true"));
    }
    
    /**
     * This test checks if the ncWMS parameters are added to the get metadata 
     * request
     */
    @Test
    public void test_GetMetadataLayerDetails() throws MalformedURLException {
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        
        // Test mandatory values
        String query = layer.queryMetadataLayerDetails().toString(); 
        assertTrue(query.contains("request=GetMetadata"));         
        assertTrue(query.contains("item=layerDetails"));
        assertTrue(query.contains("layerName=test"));
        
        
        // Test all values
        layer.dimensions().put("TIME", "10-10-10T01:00:00Z");        
        query = layer.queryMetadataLayerDetails().toString();        
        assertTrue(query.contains("item=layerDetails"));
        assertTrue(query.contains("layerName=test"));
        assertTrue(query.contains("time=10-10-10T01:00:00Z"));
    }
    
    /**
     * This test checks if the ncWMS parameters are added to the get metadata 
     * request
     */
    @Test
    public void test_GetMetadataAnimationTimesteps() throws MalformedURLException {
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        
        // Test mandatory values
        String query = layer.queryMetadataAnimationTimesteps("10-10-10T01:00:00Z",
                "11-10-10T01:00:00Z").toString();        
        assertTrue(query.contains("request=GetMetadata"));  
        assertTrue(query.contains("item=animationTimesteps"));
        assertTrue(query.contains("layerName=test"));
        assertTrue(query.contains("start=10-10-10T01:00:00Z"));
        assertTrue(query.contains("end=11-10-10T01:00:00Z"));
        
    }
    
    /**
     * This test checks if the ncWMS parameters are added to the get metadata 
     * request
     */
    @Test
    public void test_GetMetadataTimesteps() throws MalformedURLException {
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        layer.dimensions().put("TIME", "10-10-10T01:00:00Z"); 
        
        // Test mandatory values
        String query = layer.queryMetadataTimesteps().toString();        
        assertTrue(query.contains("request=GetMetadata"));   
        assertTrue(query.contains("item=timesteps"));
        assertTrue(query.contains("layerName=test"));
        assertTrue(query.contains("day=10-10-10T01:00:00Z"));        
    }    
    
    /**
     * This test checks if the ncWMS parameters are added to the get metadata 
     * request
     */
    @Test
    public void test_GetMetadataMinmax() throws MalformedURLException {
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        
        // Test mandatory values
        String query = layer.queryMetadataMinmax().toString();        
        assertTrue(query.contains("request=GetMetadata"));   
        assertTrue(query.contains("item=minmax"));
        assertTrue(query.contains("layerName=test"));
        
    }
    
    /**
     * This test checks if the ncWMS parameters are added to the get metadata 
     * request
     */
    @Test
    public void test_GetTransect() throws MalformedURLException {
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        
        // Test mandatory values
        String query = layer.queryTransect("CRS:84", "1%2,3%4", "image/png")
                .toString();        
        assertTrue(query.contains("REQUEST=GetTransect"));   
        assertTrue(query.contains("LAYER=test"));
        assertTrue(query.contains("CRS=CRS:84"));
        assertTrue(query.contains("FORMAT=image/png"));
        assertTrue(query.contains("LINESTRING=1%2,3%4"));
        
        // Test optional values
        layer.dimensions().put("TIME", "10-10-10T10:00:00Z");
        layer.dimensions().put("ELEVATION", "550");
        query = layer.queryTransect("CRS:84", "1%2,3%4", "image/png").toString();  
        assertTrue(query.contains("TIME=10-10-10T10:00:00Z"));
        assertTrue(query.contains("ELEVATION=550"));
    }
    
    /**
     * This test checks if the ncWMS parameters are added to the get metadata 
     * request
     */
    @Test
    public void test_GetVerticalProfile() throws MalformedURLException {
        
        final NcWMSMapLayer layer = new NcWMSMapLayer(SERVER_111, "test");
        
        // Test mandatory values
        String query = layer.queryVerticalProfile("CRS:84", 1, 2,  "image/png")
                .toString();        
        System.out.println("query = " + query);
        assertTrue(query.contains("REQUEST=GetVerticalProfile"));   
        assertTrue(query.contains("LAYER=test"));  
        assertTrue(query.contains("CRS=CRS:84"));
        assertTrue(query.contains("FORMAT=image/png")); 
        assertTrue(query.contains("POINT=1.0%2.0")); 
        
        // Test optional values
        layer.dimensions().put("TIME", "10-10-10T10:00:00Z");
        query = layer.queryVerticalProfile("CRS:84", 1, 2,  "image/png").toString();  
        assertTrue(query.contains("TIME=10-10-10T10:00:00Z"));
        
    }
}
