/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import org.geotoolkit.ncwms.v130.NcGetLegend130;
import org.geotoolkit.ncwms.v111.NcGetLegend111;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for ncWMS GetMetadata requests, in version 1.1.1 and 1.3.0.
 *
 * @author Olivier Terral (Geomatys)
 */
public class NcGetMetadataTest {
    public NcGetMetadataTest() {}

    /**
     * Ensures the {@link NcGetMetadata#getURL()} method returns a well-built url,
     * with the item parameter set to "layerDetails".
     */
    @Test
    public void testNcGetMetadataLayerDetails() {
        final NcGetMetadata request = new NcGetMetadata("http://test.com");
        request.setItem("layerDetails");        
        request.setLayerName("test");
        request.setTime("01-01-01T01:00:00Z");        
        request.setDay("01-01-01T01:00:00Z");
        request.setStart("01-01-01T01:00:00Z");
        request.setEnd("01-01-01T01:00:00Z");
        
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.contains("request=GetMetadata"));
        assertTrue(sUrl.contains("item=layerDetails"));
        assertTrue(sUrl.contains("layerName=test"));
        assertTrue(sUrl.contains("time=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("day=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("start=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("end=01-01-01T01:00:00Z"));
    }

    /**
     * Ensures the {@link NcGetMetadata#getURL()} method returns a well-built url,
     * with the item parameter set to "animationTimesteps".
     */
    @Test
    public void testNcGetMetadataAnimationTimesteps() {
        final NcGetMetadata request = new NcGetMetadata("http://test.com");
        request.setItem("animationTimesteps");        
        request.setLayerName("test");
        request.setTime("01-01-01T01:00:00Z");        
        request.setDay("01-01-01T01:00:00Z");
        request.setStart("01-01-01T01:00:00Z");
        request.setEnd("01-01-01T01:00:00Z");
        
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.contains("request=GetMetadata"));
        assertTrue(sUrl.contains("item=animationTimesteps"));
        assertTrue(sUrl.contains("layerName=test"));
        assertFalse(sUrl.contains("time=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("day=01-01-01T01:00:00Z"));
        assertTrue(sUrl.contains("start=01-01-01T01:00:00Z"));
        assertTrue(sUrl.contains("end=01-01-01T01:00:00Z"));
    }
    
    /**
     * Ensures the {@link NcGetMetadata#getURL()} method returns a well-built url,
     * with the item parameter set to "timesteps".
     */
    @Test
    public void testNcGetMetadataTimesteps() {
        final NcGetMetadata request = new NcGetMetadata("http://test.com");
        request.setItem("timesteps");        
        request.setLayerName("test");
        request.setTime("01-01-01T01:00:00Z");        
        request.setDay("01-01-01T01:00:00Z");
        request.setStart("01-01-01T01:00:00Z");
        request.setEnd("01-01-01T01:00:00Z");
        
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.contains("request=GetMetadata"));
        assertTrue(sUrl.contains("item=timesteps"));
        assertTrue(sUrl.contains("layerName=test"));
        assertFalse(sUrl.contains("time=01-01-01T01:00:00Z"));
        assertTrue(sUrl.contains("day=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("start=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("end=01-01-01T01:00:00Z"));
    }
    
    /**
     * Ensures the {@link NcGetMetadata#getURL()} method returns a well-built url,
     * with the item parameter set to "menu".
     */
    @Test
    public void testNcGetMetadataMenu() {
        final NcGetMetadata request = new NcGetMetadata("http://test.com");
        request.setItem("menu");        
        request.setLayerName("test");
        request.setTime("01-01-01T01:00:00Z");        
        request.setDay("01-01-01T01:00:00Z");
        request.setStart("01-01-01T01:00:00Z");
        request.setEnd("01-01-01T01:00:00Z");
        
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.contains("request=GetMetadata"));
        assertTrue(sUrl.contains("item=menu"));
        assertFalse(sUrl.contains("layerName=test"));
        assertFalse(sUrl.contains("time=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("day=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("start=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("end=01-01-01T01:00:00Z"));
    }
    
    /**
     * Ensures the {@link NcGetMetadata#getURL()} method returns a well-built url,
     * with the item parameter set to "minmax".
     */
    @Test
    public void testNcGetMetadataMinmax() {
        final NcGetMetadataMinMax request = new NcGetMetadataMinMax("http://test.com");
        request.setItem("minmax");        
        request.setLayerName("test");
        request.setTime("01-01-01T01:00:00Z");   
        request.setElevation("5"); 
        request.setCrs("epsg:4326"); 
        request.setBbox("-2,-1,2,1"); 
        request.setWidth("400");   
        request.setHeight("300");         
        request.setDay("01-01-01T01:00:00Z");
        request.setStart("01-01-01T01:00:00Z");
        request.setEnd("01-01-01T01:00:00Z");
        
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.contains("request=GetMetadata"));
        assertTrue(sUrl.contains("item=minmax"));
        assertTrue(sUrl.contains("layers=test"));
        assertTrue(sUrl.contains("time=01-01-01T01:00:00Z"));
        assertTrue(sUrl.contains("elevation=5"));
        assertTrue(sUrl.contains("crs=epsg:4326"));
        assertTrue(sUrl.contains("bbox=-2,-1,2,1"));
        assertTrue(sUrl.contains("width=400"));
        assertTrue(sUrl.contains("height=300"));
        assertFalse(sUrl.contains("day=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("start=01-01-01T01:00:00Z"));
        assertFalse(sUrl.contains("end=01-01-01T01:00:00Z"));
    }
}
