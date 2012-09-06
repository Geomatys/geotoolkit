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

import org.geotoolkit.ncwms.v111.NcGetTimeseries111;
import org.geotoolkit.ncwms.v130.NcGetTimeseries130;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.junit.Test;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import static org.junit.Assert.*;

/**
 * Testing class for nc NcGetTimeseriesTest requests, in version 1.1.1 and 1.3.0.
 *
 * @author Fabien BERNARD (Geomatys)
 */
public class NcGetTimeseriesTest {
    public NcGetTimeseriesTest() {}

    /**
     * Ensures the {@link NcGetTimeseries111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testNcGetTimeseries111() throws NoSuchAuthorityCodeException, FactoryException {

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final NcGetTimeseries111 request = new NcGetTimeseries111("http://test.com",null);
        request.setDimension(new Dimension(800, 600));
        request.setFormat("image/png");
        request.setLayers("test");
        request.setStyles("");
        request.setEnvelope(env);
        request.setInfoFormat("gml");
        request.setQueryLayers("test");
        request.setColumnIndex(50);
        request.setRawIndex(40);

        request.setOpacity(65);
        request.dimensions().put("COLORSCALERANGE","-50,50");
        request.setNumColorBands(150);
        request.setLogScale(true);

        request.setDateBegin("2011-11-15T10:43:30Z");
        request.setDateEnd("2011-11-24T00:59:55.000Z");

        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }

        final String sUrl = url.toString();
        assertTrue("was:" + sUrl, sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue("was:" + sUrl, sUrl.contains("SRS=CRS%3A84"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=800"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=600"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("X=50"));
        assertTrue("was:" + sUrl, sUrl.contains("Y=40"));
        assertTrue("was:" + sUrl, sUrl.contains("OPACITY=65"));
        assertTrue("was:" + sUrl, sUrl.contains("COLORSCALERANGE=-50%2C50"));
        assertTrue("was:" + sUrl, sUrl.contains("NUMCOLORBANDS=150"));
        assertTrue("was:" + sUrl, sUrl.contains("LOGSCALE=true"));
        assertTrue("was:" + sUrl, sUrl.contains("TIME=2011-11-15T10%3A43%3A30Z%2F2011-11-24T00%3A59%3A55.000Z"));
    }

    /**
     * Ensures the {@link NcGetTimeseries130#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testNcGetTimeseries130() throws NoSuchAuthorityCodeException, FactoryException {

        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final NcGetTimeseries130 request = new NcGetTimeseries130("http://test.com",null);
        request.setDimension(new Dimension(800, 600));
        request.setFormat("image/png");
        request.setLayers("test");
        request.setStyles("");
        request.setEnvelope(env);
        request.setInfoFormat("gml");
        request.setQueryLayers("test");
        request.setColumnIndex(50);
        request.setRawIndex(40);

        request.setOpacity(65);
        request.dimensions().put("COLORSCALERANGE","-50,50");
        request.setNumColorBands(150);
        request.setLogScale(true);

        request.setDateBegin("2011-11-15T10:43:30Z");
        request.setDateEnd("2011-11-24T00:59:55.000Z");

        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }

        final String sUrl = url.toString();
        assertTrue("was:" + sUrl, sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue("was:" + sUrl, sUrl.contains("CRS=CRS%3A84"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=800"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=600"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("I=50"));
        assertTrue("was:" + sUrl, sUrl.contains("J=40"));
        assertTrue("was:" + sUrl, sUrl.contains("OPACITY=65"));
        assertTrue("was:" + sUrl, sUrl.contains("COLORSCALERANGE=-50%2C50"));
        assertTrue("was:" + sUrl, sUrl.contains("NUMCOLORBANDS=150"));
        assertTrue("was:" + sUrl, sUrl.contains("LOGSCALE=true"));
        assertTrue("was:" + sUrl, sUrl.contains("TIME=2011-11-15T10%3A43%3A30Z%2F2011-11-24T00%3A59%3A55.000Z"));
    }
}
