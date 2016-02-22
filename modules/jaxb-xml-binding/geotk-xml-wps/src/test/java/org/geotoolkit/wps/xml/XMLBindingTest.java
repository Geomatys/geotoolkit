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
package org.geotoolkit.wps.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.PolygonType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.geotoolkit.wps.xml.v100.DataInputsType;
import org.geotoolkit.wps.xml.v100.Execute;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.InputType;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal
 */
public class XMLBindingTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testUnmarshallingExecute() throws JAXBException, IOException, URISyntaxException {
        URL url = ClassLoader.getSystemResource("xml/executeRequest.xml");
        Unmarshaller u = WPSMarshallerPool.getInstance().acquireUnmarshaller();
        final Object obj = u.unmarshal(url);
        Assert.assertTrue(obj instanceof Execute);

        Execute request = (Execute) obj;
        Assert.assertEquals(2, request.getDataInputs().getInput().size());

        final InputType in1 = request.getDataInputs().getInput().get(0);

        Assert.assertNotNull(in1.getData().getComplexData());

        ComplexDataType complex = in1.getData().getComplexData();

        for (Object o : complex.getContent()) {
            if (!(o instanceof String)) {
                if (o instanceof JAXBElement) {
                    o = ((JAXBElement)o).getValue();
                }
                Assert.assertTrue("type was:" + o.getClass(), o instanceof CurveType);
            }
        }
    }

    @Test
    public void testUnmarshallingExecute2() throws JAXBException, IOException {
        URL url = ClassLoader.getSystemResource("xml/executeRequest2.xml");
        Unmarshaller u = WPSMarshallerPool.getInstance().acquireUnmarshaller();
        final Object obj = u.unmarshal(url);
        Assert.assertTrue(obj instanceof Execute);

        Execute request = (Execute) obj;
        Assert.assertEquals(2, request.getDataInputs().getInput().size());

        final InputType in1 = request.getDataInputs().getInput().get(0);

        Assert.assertNotNull(in1.getData().getComplexData());

        ComplexDataType complex = in1.getData().getComplexData();

        for (Object o : complex.getContent()) {
            if (!(o instanceof String)) {
                if (o instanceof JAXBElement) {
                    o = ((JAXBElement)o).getValue();
                }
                Assert.assertTrue("type was:" + o.getClass(), o instanceof LineStringType);
            }
        }
    }

    @Test
    public void testUnmarshallingExecute3() throws JAXBException, IOException {
        URL url = ClassLoader.getSystemResource("xml/executeRequest3.xml");
        Unmarshaller u = WPSMarshallerPool.getInstance().acquireUnmarshaller();
        final Object obj = u.unmarshal(url);
        Assert.assertTrue(obj instanceof Execute);

        Execute request = (Execute) obj;
        Assert.assertEquals(2, request.getDataInputs().getInput().size());

        final InputType in1 = request.getDataInputs().getInput().get(0);

        Assert.assertNotNull(in1.getData().getComplexData());

        ComplexDataType complex = in1.getData().getComplexData();

        for (Object o : complex.getContent()) {
            if (!(o instanceof String)) {
                if (o instanceof JAXBElement) {
                    o = ((JAXBElement)o).getValue();
                }
                Assert.assertTrue("type was:" + o.getClass(), o instanceof PolygonType);
            }
        }
    }

    @Test
    public void testUnmarshallingExecute4() throws JAXBException, IOException {
        URL url = ClassLoader.getSystemResource("xml/executeRequest4.xml");
        Unmarshaller u = WPSMarshallerPool.getInstance().acquireUnmarshaller();
        final Object obj = u.unmarshal(url);
        Assert.assertTrue(obj instanceof Execute);

        Execute request = (Execute) obj;
        Assert.assertEquals(1, request.getDataInputs().getInput().size());

        final InputType in1 = request.getDataInputs().getInput().get(0);

        Assert.assertNotNull(in1.getReference());

        InputReferenceType reference = in1.getReference();

        Assert.assertTrue("Bad type: " + reference.getBody().getClass(), reference.getBody() instanceof Execute);

    }

    @Test
    public void testMarshallingExecute4() throws JAXBException, IOException {
        final Execute executeBody = new Execute();
        executeBody.setIdentifier(new CodeType("integrated execute"));

        final Execute executeRoot = new Execute();
        DataInputsType dataInput = new DataInputsType();
        InputType input = new InputType();
        final InputReferenceType ref = new InputReferenceType();
        ref.setBody(executeBody);
        input.setReference(ref);
        dataInput.getInput().add(input);
        executeRoot.setDataInputs(dataInput);


        Marshaller m = WPSMarshallerPool.getInstance().acquireMarshaller();
        m.marshal(executeRoot, System.out);

    }

}
