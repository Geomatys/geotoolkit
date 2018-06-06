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
package org.geotoolkit.wps.client;

import org.geotoolkit.wps.client.WPSVersion;
import org.geotoolkit.wps.client.WebProcessingClient;
import org.geotoolkit.wps.client.ExecuteRequest;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import javax.xml.bind.Marshaller;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.junit.Test;

import static org.apache.sis.test.Assert.*;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.io.WPSSchema;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInput;
import org.geotoolkit.wps.xml.v200.Execute;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.LiteralValue;
import org.geotoolkit.wps.xml.v200.OutputDefinition;
import org.geotoolkit.wps.xml.v200.Reference;
import org.opengis.referencing.crs.GeographicCRS;


/**
 * Testing class for GetCapabilities requests of WPS client, in version 1.0.0.
 *
 * @author Quentin Boileau
 */
public class ExecuteTest extends org.geotoolkit.test.TestBase {

    private static String EPSG_VERSION;

    public ExecuteTest() {
        EPSG_VERSION = org.geotoolkit.referencing.CRS.getVersion("EPSG").toString();
    }

    @Test
    public void testRequestAndMarshall() throws Exception {
        final WebProcessingClient client = new WebProcessingClient(new URL("http://test.com"), null, WPSVersion.v100);
        final ExecuteRequest request = client.createExecute();
        final Execute execute = request.getContent();

        final GeographicCRS epsg4326 = CommonCRS.WGS84.geographic();
        final GeneralEnvelope env = new GeneralEnvelope(epsg4326);
        env.setRange(0, 10, 10);
        env.setRange(1, 10, 10);

        execute.setIdentifier("identifier");
        final List<DataInput> inputs = execute.getInput();
        inputs.add(new DataInput("literal", new Data(new LiteralValue("10", null, null))));
        inputs.add(new DataInput("bbox", new Data(new BoundingBoxType(env))));
        inputs.add(new DataInput("complex", new Data(
                new Format("UTF-8", WPSMimeType.APP_GML.val(), WPSSchema.OGC_GML_3_1_1.getValue(), null),
                new PointType(new DirectPosition2D(epsg4326, 0, 0))
        )));
        inputs.add(new DataInput("reference", new Reference("http://link.to/reference/", null, null)));

        execute.getOutput().add(new OutputDefinition("output", false));

        assertEquals("WPS", execute.getService());
        assertEquals("1.0.0", execute.getVersion().toString());
        assertEquals(execute.getIdentifier().getValue(), "identifier");

        final StringWriter stringWriter = new StringWriter();
        final Marshaller marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
        marshaller.marshal(execute, stringWriter);

        String result = stringWriter.toString();
        try (final InputStream expected = expectedRequest()) {
            assertXmlEquals(expected, result, "xmlns:*");
        }

        WPSMarshallerPool.getInstance().recycle(marshaller);
    }

    private static InputStream expectedRequest() {
        return ExecuteTest.class.getResourceAsStream("execute.xml");
    }
}
