/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.wps.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import javax.xml.bind.JAXBException;
import org.geotoolkit.gml.xml.v321.CoordinatesType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.wps.xml.v200.JobControlOptions;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal
 */
public class JsonBindingTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testUnmarshallingExecute() throws JAXBException, IOException, URISyntaxException {
        URL url = ClassLoader.getSystemResource("json/executeRequest.json");
        ObjectMapper m = new ObjectMapper();
        final Object obj = m.readValue(url, Execute.class);
        Assert.assertTrue(obj instanceof Execute);

        Execute request = (Execute) obj;
        Assert.assertEquals(2, request.getInputs().size());

        final Input in1 = request.getInputs().get(0);

        Assert.assertNotNull(in1.getValue());

    }

    @Test
    public void testUnmarshallingExecute2() throws JAXBException, IOException {
        URL url = ClassLoader.getSystemResource("json/executeRequest2.json");
        ObjectMapper m = new ObjectMapper();
        final Object obj = m.readValue(url, Execute.class);
        Assert.assertTrue(obj instanceof Execute);

        Execute request = (Execute) obj;
        Assert.assertEquals(2, request.getInputs().size());

        final Input in1 = request.getInputs().get(0);

        Assert.assertNotNull(in1.getValue());

    }

    @Test
    public void testUnmarshallingExecute3() throws JAXBException, IOException {
        URL url = ClassLoader.getSystemResource("json/executeRequest3.json");
        ObjectMapper m = new ObjectMapper();
        final Object obj = m.readValue(url, Execute.class);
        Assert.assertTrue(obj instanceof Execute);

        Execute request = (Execute) obj;
        Assert.assertEquals(2, request.getInputs().size());

        final Input in1 = request.getInputs().get(0);

        Assert.assertNotNull(in1.getValue());

    }

    @Test
    public void testUnmarshallingExecute4() throws JAXBException, IOException {
        URL url = ClassLoader.getSystemResource("json/executeRequest4.json");
        ObjectMapper m = new ObjectMapper();
        final Object obj = m.readValue(url, Execute.class);
        Assert.assertTrue(obj instanceof Execute);

        Execute request = (Execute) obj;
        Assert.assertEquals(2, request.getInputs().size());

        final Input in1 = request.getInputs().get(0);

        Assert.assertNotNull(in1.getValue());

    }

    @Test
    public void testMarshallingExecute4() throws JAXBException, IOException {

        final Execute executeRoot = new Execute();
        Input input = new Input();
        input.setId("inout-1");
        input.setValue("somevalue");
        executeRoot.setInputs(Arrays.asList(input));

        Output output = new Output();
        output.setId("out-1");
        output.setTransmissionMode(TransmissionMode.VALUE);
        executeRoot.setOutputs(Arrays.asList(output));


        ObjectMapper m = new ObjectMapper();
        StringWriter sw = new StringWriter();
        m.writeValue(sw, executeRoot);
        String expResult = "{\"inputs\":[{\"id\":\"inout-1\",\"format\":null,\"value\":\"somevalue\"}],\"outputs\":[{\"id\":\"out-1\",\"format\":null,\"transmissionMode\":\"VALUE\"}]}";
        assertEquals(expResult, sw.toString());

    }

    @Test
    public void testMarshallingExecuteV2() throws JAXBException, IOException {


        PointType pt = new PointType(new CoordinatesType());
        Format format = new Format().encoding("UTF8").mimeType("text/xml").schema("http://kk.com");

        final Execute executeRoot = new Execute();
        Input dataInput = (Input) new Input().value("not a point").id("input1").format(format);
        executeRoot.addInputsItem(dataInput);

        Output output = new Output();
        output.setId("out-1");
        output.setTransmissionMode(TransmissionMode.REFERENCE);
        executeRoot.setOutputs(Arrays.asList(output));

        ObjectMapper m = new ObjectMapper();
        StringWriter sw = new StringWriter();
        m.writeValue(sw, executeRoot);
        String expResult = "{\"inputs\":[{\"id\":\"input1\",\"format\":{\"mimeType\":\"text/xml\",\"schema\":\"http://kk.com\",\"encoding\":\"UTF8\"},\"value\":\"not a point\"}],\"outputs\":[{\"id\":\"out-1\",\"format\":null,\"transmissionMode\":\"REFERENCE\"}]}";
        assertEquals(expResult, sw.toString());

    }

    @Test
    public void testMarshallingProcesseCollectionV2() throws JAXBException, IOException {

        ProcessCollection collec = new ProcessCollection();


        ProcessSummary summary = new ProcessSummary().addJobControlOptionsItem(JobControlOptions.SYNC_EXECUTE);
        summary.setAbstract("some process 1");
        summary.setId("urn:process:1");
        summary.setKeywords(Arrays.asList("kw1", "kw2"));
        summary.setProcessDescriptionURL("http://process/1");
        summary.setTitle("title p1");
        summary.setVersion("1.0");
        collec.addProcessesItem(summary);


        ProcessSummary summary2 = new ProcessSummary().addJobControlOptionsItem(JobControlOptions.ASYNC_EXECUTE);
        summary.setAbstract("some process 2");
        summary.setId("urn:process:2");
        summary.setKeywords(Arrays.asList("kw4", "kw3"));
        summary.setProcessDescriptionURL("http://process/2");
        summary.setTitle("title p2");
        summary.setVersion("1.0");
        collec.addProcessesItem(summary2);


        ObjectMapper m = new ObjectMapper();
        StringWriter sw = new StringWriter();
        m.writeValue(sw, collec);
        String expResult = "{\"processes\":[{\"id\":\"urn:process:2\",\"title\":\"title p2\",\"keywords\":[\"kw4\",\"kw3\"],\"metadata\":null,\"version\":\"1.0\",\"jobControlOptions\":[\"SYNC_EXECUTE\"],\"processDescriptionURL\":\"http://process/2\",\"abstract\":\"some process 2\"},{\"id\":null,\"title\":null,\"keywords\":null,\"metadata\":null,\"version\":null,\"jobControlOptions\":[\"ASYNC_EXECUTE\"],\"processDescriptionURL\":null,\"abstract\":null}]}";
        assertEquals(expResult, sw.toString());

    }

    @Test
    public void testMarshallingProcesseOffering() throws JAXBException, IOException {

        ProcessOffering collec = new ProcessOffering();


        Process summary = new Process().addJobControlOptionsItem(JobControlOptions.SYNC_EXECUTE);
        summary.setAbstract("some process 1");
        summary.setId("urn:process:1");
        summary.setKeywords(Arrays.asList("kw1", "kw2"));
        summary.setExecuteEndpoint("http://process/1");
        summary.setTitle("title p1");
        summary.setVersion("1.0");
        summary.setOutputTransmission(Arrays.asList(TransmissionMode.REFERENCE));

        // inputs
        FormatDescription format = (FormatDescription) new FormatDescription()._default(true).encoding("UTF8").mimeType("text/xml").schema("http://kk.com");
        LiteralInputType input1 = new LiteralInputType();
        input1.setAbstract("some input 1");
        input1.setFormats(Arrays.asList(format));
        input1.setKeywords(Arrays.asList("in1"));
        input1.setLiteralDataDomain(format);
        input1.setMinOccurs(0);
        input1.setMaxOccurs(Integer.MAX_VALUE);
        input1.setTitle("in put 1 title");
        summary.addInputsItem(input1);

        // outputs
        OutputDescription output = new OutputDescription();
        output.setAbstract("some output");
        output.setFormats(Arrays.asList(format));
        output.setId("out-1");
        output.setKeywords(Arrays.asList("kw-out"));
        output.setTitle("out title");
        summary.addOutputsItem(output);

        collec.setProcess(summary);

        ObjectMapper m = new ObjectMapper();
        StringWriter sw = new StringWriter();
        m.writeValue(sw, collec);
        String expResult = "{\"process\":{\"id\":\"urn:process:1\",\"title\":\"title p1\",\"keywords\":[\"kw1\",\"kw2\"],\"metadata\":null,\"inputs\":[{\"id\":null,\"title\":\"in put 1 title\",\"keywords\":[\"in1\"],\"metadata\":null,\"formats\":[{\"mimeType\":\"text/xml\",\"schema\":\"http://kk.com\",\"encoding\":\"UTF8\",\"maximumMegabytes\":null,\"default\":true}],\"minOccurs\":0,\"maxOccurs\":2147483647,\"abstract\":\"some input 1\",\"LiteralDataDomain\":{\"mimeType\":\"text/xml\",\"schema\":\"http://kk.com\",\"encoding\":\"UTF8\",\"maximumMegabytes\":null,\"default\":true}}],\"outputs\":[{\"id\":\"out-1\",\"title\":\"out title\",\"keywords\":[\"kw-out\"],\"metadata\":null,\"formats\":[{\"mimeType\":\"text/xml\",\"schema\":\"http://kk.com\",\"encoding\":\"UTF8\",\"maximumMegabytes\":null,\"default\":true}],\"abstract\":\"some output\"}],\"version\":\"1.0\",\"jobControlOptions\":[\"SYNC_EXECUTE\"],\"outputTransmission\":[\"REFERENCE\"],\"executeEndpoint\":\"http://process/1\",\"abstract\":\"some process 1\"}}";
        assertEquals(expResult, sw.toString());

    }

}
