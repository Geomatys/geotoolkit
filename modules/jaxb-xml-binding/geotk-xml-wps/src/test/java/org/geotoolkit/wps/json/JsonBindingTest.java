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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.geotoolkit.gml.xml.v321.CoordinatesType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.xml.v200.DataTransmissionMode;
import org.geotoolkit.wps.xml.v200.JobControlOptions;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

        Assert.assertNotNull(in1.getData());

        assertEquals(org.geotoolkit.wps.xml.v200.Execute.Mode.sync, request.getMode());
        assertEquals(org.geotoolkit.wps.xml.v200.Execute.Response.raw, request.getResponse());

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

        Assert.assertNotNull(in1.getData());

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

        Assert.assertNotNull(in1.getData());

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

        Assert.assertNotNull(in1.getData());

    }

    @Test
    public void testMarshallingExecute4() throws JAXBException, IOException, URISyntaxException {

        final Execute executeRoot = new Execute();
        Input input = new Input();
        input.setId("inout-1");
        input.setData("somevalue");
        executeRoot.setInputs(Arrays.asList(input));

        Output output = new Output();
        output.setId("out-1");
        output.setTransmissionMode(DataTransmissionMode.VALUE);
        executeRoot.setOutputs(Arrays.asList(output));
        executeRoot.setMode(org.geotoolkit.wps.xml.v200.Execute.Mode.sync);
        executeRoot.setResponse(org.geotoolkit.wps.xml.v200.Execute.Response.raw);


        ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        StringWriter sw = new StringWriter();
        m.writeValue(sw, executeRoot);
        String expResult = IOUtilities.toString(IOUtilities.getResourceAsPath("json/executeRequest5.json"));
        expResult = expResult.replace(" ", "");
        expResult = expResult.replace("\n", "");
        String result = sw.toString().replace(" ", "");

        assertEquals(expResult, result);

    }

    @Test
    public void testMarshallingExecuteV2() throws JAXBException, IOException, URISyntaxException {


        PointType pt = new PointType(new CoordinatesType());
        Format format = new Format().encoding("UTF8").mimeType("text/xml").schema("http://kk.com");

        final Execute executeRoot = new Execute();
        Input dataInput = (Input) new Input().id("input1").format(format);
        dataInput.setData("not a point");
        executeRoot.addInputsItem(dataInput);

        Output output = new Output();
        output.setId("out-1");
        output.setTransmissionMode(DataTransmissionMode.REFERENCE);
        executeRoot.setOutputs(Arrays.asList(output));
        executeRoot.setMode(org.geotoolkit.wps.xml.v200.Execute.Mode.async);
        executeRoot.setResponse(org.geotoolkit.wps.xml.v200.Execute.Response.document);

        ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        StringWriter sw = new StringWriter();
        m.writeValue(sw, executeRoot);
        String expResult = IOUtilities.toString(IOUtilities.getResourceAsPath("json/executeRequest6.json"));

        expResult = expResult.replace(" ", "");
        expResult = expResult.replace("\n", "");
        String result = sw.toString().replace(" ", "");

        assertEquals(expResult, result);

    }

    @Test
    public void testMarshallingStatusInfo() throws JAXBException, IOException, URISyntaxException {

        final StatusInfo status = new StatusInfo();
        status.setMessage("some msg");
        status.setProgress(50);
        status.setStatus(StatusInfo.StatusEnum.started);

        ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        StringWriter sw = new StringWriter();
        m.writeValue(sw, status);

        String expResult = IOUtilities.toString(IOUtilities.getResourceAsPath("json/status1.json"));
        expResult = expResult.replace(" ", "");
        expResult = expResult.replace("\n", "");
        String result = sw.toString().replace(" ", "");
        assertEquals(expResult, result);
    }

    @Test
    public void testUnmarshallingStatus() throws JAXBException, IOException, URISyntaxException {

        String json = IOUtilities.toString(IOUtilities.getResourceAsPath("json/status1.json"));
        ObjectMapper m = new ObjectMapper();
        StatusInfo result = m.readValue(json, StatusInfo.class);

        final StatusInfo status = new StatusInfo();
        status.setMessage("some msg");
        status.setProgress(50);
        status.setStatus(StatusInfo.StatusEnum.started);

        assertEquals(status, result);

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
        summary2.setAbstract("some process 2");
        summary2.setId("urn:process:2");
        summary2.setKeywords(Arrays.asList("kw4", "kw3"));
        summary2.setProcessDescriptionURL("http://process/2");
        summary2.setTitle("title p2");
        summary2.setVersion("1.0");
        collec.addProcessesItem(summary2);


        ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        StringWriter sw = new StringWriter();
        m.writeValue(sw, collec);
        String expResult = "{\"processes\":["
                + "{"
                + "\"id\":\"urn:process:1\","
                + "\"title\":\"title p1\","
                + "\"keywords\":[\"kw1\",\"kw2\"],"
                + "\"version\":\"1.0\","
                + "\"jobControlOptions\":[\"sync-execute\"],"
                + "\"processDescriptionURL\":\"http://process/1\","
                + "\"abstract\":\"some process 1\"},"
                + "{"
                + "\"id\":\"urn:process:2\","
                + "\"title\":\"title p2\","
                + "\"keywords\":[\"kw4\",\"kw3\"],"
                + "\"version\":\"1.0\","
                + "\"jobControlOptions\":[\"async-execute\"],"
                + "\"processDescriptionURL\":\"http://process/2\","
                + "\"abstract\":\"some process 2\"}"
                + "]}";
        assertEquals(expResult, sw.toString());

    }

    @Test
    public void testMarshallingProcesseOffering() throws JAXBException, IOException, URISyntaxException {

        ProcessOffering collec = new ProcessOffering();


        Process summary = new Process();
        summary.setAbstract("some process 1");
        summary.setId("urn:process:1");
        summary.setKeywords(Arrays.asList("kw1", "kw2"));
        summary.setExecuteEndpoint("http://process/1");
        summary.setTitle("title p1");

        collec.setJobControlOptions(Arrays.asList(JobControlOptions.SYNC_EXECUTE));
        collec.setProcessVersion("1.0");
        collec.setOutputTransmission(Arrays.asList(DataTransmissionMode.REFERENCE));

        // inputs
        FormatDescription format = (FormatDescription) new FormatDescription()._default(true).encoding("UTF8").mimeType("text/xml").schema("http://kk.com");
        InputType input1 = new InputType();
        input1.setAbstract("some input 1");
        input1.setFormats(Arrays.asList(format));
        input1.setKeywords(Arrays.asList("in1"));
        input1.setLiteralDataDomain(new LiteralDataDomain(new LiteralDataDomainTypeDataType("String", null)));
        input1.setMinOccurs("0");
        input1.setMaxOccurs("unbounded");
        input1.setTitle("in put 1 title");
        input1.setId("id1");
        summary.addInputsItem(input1);

        InputType input2 = new InputType();
        input2.setAbstract("some input 2");
        input2.setFormats(Arrays.asList(format));
        input2.setKeywords(Arrays.asList("in2"));
        input2.setFormats(Arrays.asList(format));
        input2.setMinOccurs("0");
        input2.setMaxOccurs("1");
        input2.setTitle("in put 2 title");
        input2.setId("id2");
        input2.setAdditionalParameters(Arrays.asList(new AdditionalParameters(null, Arrays.asList(new AdditionalParameter("EOImage", Arrays.asList("true"))))));
        summary.addInputsItem(input2);

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
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        StringWriter sw = new StringWriter();
        m.writeValue(sw, collec);
        String expResult = IOUtilities.toString(IOUtilities.getResourceAsPath("json/processOffering.json"));
        expResult = expResult.replace(" ", "");
        expResult = expResult.replace("\n", "");
        String result = sw.toString().replace(" ", "");
        assertEquals(expResult, result);

    }

    @Test
    public void testUnmarshallingProcesseOffering() throws JAXBException, IOException, URISyntaxException {

        String json = IOUtilities.toString(IOUtilities.getResourceAsPath("json/processOffering.json"));
        ObjectMapper m = new ObjectMapper();
        ProcessOffering result = m.readValue(json, ProcessOffering.class);

        Assert.assertNotNull(result.getProcess());
        Assert.assertNotNull(result.getProcess().getInputs());
        Assert.assertEquals(2, result.getProcess().getInputs().size());

        Assert.assertTrue(result.getProcess().getInputs().get(0).getLiteralDataDomain() != null);

        Assert.assertTrue(result.getProcess().getInputs().get(1).getLiteralDataDomain() == null);
        Assert.assertTrue(result.getProcess().getInputs().get(1).getSupportedCRS()== null);

        InputType lit = result.getProcess().getInputs().get(0);
        InputType comp = result.getProcess().getInputs().get(1);

        assertNotNull(comp.getAdditionalParameters());
        assertTrue(comp.getAdditionalParameters().size() == 1);
        assertEquals(comp.getAdditionalParameters().get(0).getParameters().get(0).getName(), "EOImage");
        assertEquals(comp.getAdditionalParameters().get(0).getParameters().get(0).getValues(), Arrays.asList("true"));

    }

     @Test
    public void testMarshallingDeploy() throws JAXBException, IOException, URISyntaxException {

        Process summary = new Process();
        summary.setAbstract("some process 1");
        summary.setId("urn:process:1");
        summary.setKeywords(Arrays.asList("kw1", "kw2"));
        summary.setExecuteEndpoint("http://process/1");
        summary.setTitle("title p1");


        // inputs
        FormatDescription format = (FormatDescription) new FormatDescription()._default(true).encoding("UTF8").mimeType("text/xml").schema("http://kk.com");
        InputType input1 = new InputType();
        input1.setAbstract("some input 1");
        input1.setFormats(Arrays.asList(format));
        input1.setKeywords(Arrays.asList("in1"));
        input1.setLiteralDataDomain(new LiteralDataDomain(new LiteralDataDomainTypeDataType("String", "http://www.w3.org/TR/xmlschema-2/#String")));
        input1.setMinOccurs("0");
        input1.setMaxOccurs("unbounded");
        input1.setTitle("in put 1 title");
        input1.setId("ID1");
        input1.setAdditionalParameters(Arrays.asList(new AdditionalParameters(null, Arrays.asList(new AdditionalParameter("EOImage", Arrays.asList("true"))))));
        summary.addInputsItem(input1);

        InputType input2 = new InputType();
        input2.setAbstract("some input 2");
        input2.setFormats(Arrays.asList(format));
        input2.setKeywords(Arrays.asList("in2"));
        input2.setFormats(Arrays.asList(format));
        input2.setMinOccurs("0");
        input2.setMaxOccurs("1");
        input2.setTitle("in put 2 title");
        input2.setId("ID2");
        summary.addInputsItem(input2);

        // outputs
        OutputDescription output = new OutputDescription();
        output.setAbstract("some output");
        output.setFormats(Arrays.asList(format));
        output.setId("out-1");
        output.setKeywords(Arrays.asList("kw-out"));
        output.setTitle("out title");
        summary.addOutputsItem(output);

        ProcessDescriptionChoiceType process = new ProcessDescriptionChoiceType(summary, "1.0", Arrays.asList(JobControlOptions.SYNC_EXECUTE), Arrays.asList(DataTransmissionMode.REFERENCE));
        ExecutionUnit unit = new ExecutionUnit("http://test.cwl");
        Deploy deploy = new Deploy(process, Arrays.asList(unit), "deploy 1", true);

        ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        StringWriter sw = new StringWriter();
        m.writeValue(sw, deploy);
        //System.out.println(sw.toString());
        String expResult = IOUtilities.toString(IOUtilities.getResourceAsPath("json/deploy.json"));
        expResult = expResult.replace(" ", "");
        expResult = expResult.replace("\n", "");
        String result = sw.toString().replace(" ", "");

        assertEquals(expResult, result);

    }

    @Test
    public void testMarshallingResult() throws JAXBException, IOException {
        final Result result = new Result();
        List<OutputInfo> outputs = new ArrayList<>();
        outputs.add(new OutputInfo("out", "someValue"));
        result.setOutputs(outputs);

        result.setLinks(Arrays.asList(new JsonLink("http://localhost:9001/wps/default/bills/3b490bb5", "Bill", "application/json", "Associated Bill", null)));

        ObjectMapper m = new ObjectMapper();
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        StringWriter sw = new StringWriter();
        m.writeValue(sw, result);
        System.out.println(sw.toString());
    }

}
