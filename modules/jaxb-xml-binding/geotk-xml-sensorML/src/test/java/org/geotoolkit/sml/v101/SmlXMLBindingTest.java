/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sml.v101;

import org.geotoolkit.sml.xml.v101.ComponentType;
import org.geotoolkit.sml.xml.v101.ObjectFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

//constellation
import org.geotoolkit.sml.xml.v101.Classification.ClassifierList.Classifier;
import org.geotoolkit.gml.xml.v311modified.TimePeriodType;
import org.geotoolkit.gml.xml.v311modified.TimePositionType;

import org.geotoolkit.sml.xml.v101.Identification.IdentifierList;
import org.geotoolkit.sml.xml.v101.Identification.IdentifierList.Identifier;
import org.geotoolkit.sml.xml.v101.Inputs.InputList;
import org.geotoolkit.sml.xml.v101.Outputs.OutputList;
import org.geotoolkit.sml.xml.v101.Parameters.ParameterList;
import org.geotoolkit.swe.xml.v101.CodeSpacePropertyType;
import org.geotoolkit.swe.xml.v101.DataComponentPropertyType;
import org.geotoolkit.swe.xml.v101.DataRecordType;
import org.geotoolkit.swe.xml.v101.ObservableProperty;
import org.geotoolkit.swe.xml.v101.QuantityRange;
import org.geotoolkit.swe.xml.v101.QuantityType;
import org.geotoolkit.swe.xml.v101.TimeRange;
import org.geotoolkit.swe.xml.v101.UomPropertyType;

// JAXB dependencies
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//Junit dependencies
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author guilhem
 */
public class SmlXMLBindingTest {

    private Logger       logger = Logger.getLogger("org.geotoolkit.sml");
    private MarshallerPool  pool;
    private ObjectFactory sml100Factory = new ObjectFactory();
    private org.geotoolkit.swe.xml.v101.ObjectFactory swe100Factory = new org.geotoolkit.swe.xml.v101.ObjectFactory();


    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        pool = new MarshallerPool("org.geotoolkit.sml.xml.v101");
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void ComponentMarshalingTest() throws Exception {

        ComponentType compo = new ComponentType();

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void ComponentUnmarshallMarshalingTest() throws Exception {
        /*
        InputStream is = Util.getResourceAsStream("org/constellation/sml/component.xml");
        Object unmarshalled = unmarshaller.unmarshal(is);
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        assertTrue(unmarshalled instanceof SensorML);

        SensorML result = (SensorML) unmarshalled;

        SensorML.Member member = new SensorML.Member();
        member.setRole("urn:x-ogx:def:sensor:OGC:detector");

        ComponentType component = new ComponentType();

        List<String> kw = new ArrayList<String>();
        kw.add("piezometer");
        kw.add("geosciences");
        kw.add("point d'eau");
        Keywords keywords = new Keywords(new Keywords.KeywordList("urn:x-brgm:def:gcmd:keywords", kw));
        component.setKeywords(keywords);

        Classifier cl1 = new Classification.ClassifierList.Classifier("intendedApplication", new Term("eaux souterraines", "urn:x-ogc:def:classifier:OGC:application"));
        CodeSpacePropertyType cs = new CodeSpacePropertyType("urn:x-brgm:def:GeoPoint:bss");
        Classifier cl2 = new Classification.ClassifierList.Classifier("sensorType", new Term(cs, "Profondeur", "urn:sensor:classifier:sensorType"));
        List<Classifier> cls = new ArrayList<Classifier>();
        cls.add(cl1);
        cls.add(cl2);
        Classification.ClassifierList claList = new Classification.ClassifierList(null, cls);
        Classification classification = new Classification(claList);
        component.setClassification(classification);

        List<Identifier> identifiers = new ArrayList<Identifier>();
        cs = new CodeSpacePropertyType("urn:x-brgm:def:sensorSystem:hydras");
        Identifier id1 = new Identifier("supervisorCode", new Term(cs, "00ARGLELES_2000", "urn:x-ogc:def:identifier:OGC:modelNumber"));
        Identifier id2 = new Identifier("longName", new Term("Madofil II", "urn:x-ogc:def:identifier:OGC:longname"));
        identifiers.add(id1);
        identifiers.add(id2);
        IdentifierList identifierList = new IdentifierList(null, identifiers);
        Identification identification = new Identification(identifierList);
        component.setIdentification(identification);

        TimePeriodType period = new TimePeriodType(new TimePositionType("2004-06-01"));
        ValidTime vTime = new ValidTime(period);
        component.setValidTime(vTime);

       
           TODO
        Capabilities capabilities = new Capabilities();
        TimeRange timeRange = new TimeRange(Arrays.asList("1987-04-23", "now"));
        DataComponentPropertyType field = new DataComponentPropertyType("periodOfData", "urn:x-brgm:def:property:periodOfData", timeRange);
        DataRecordType record = new DataRecordType("urn:x-brgm:def:property:periodOfData", Arrays.asList(field));
        JAXBElement<? extends AbstractDataRecordType> jbRecord = swe100Factory.createDataRecord(record);
        capabilities.setAbstractDataRecord(jbRecord);
        component.setCapabilities(capabilities);

        Contact contact = new Contact("urn:x-ogc:def:role:manufacturer", new ResponsibleParty("IRIS"));
        component.SetContact(contact);

        Position position = new Position("conductivitePosition", "piezometer#piezoPosition");
        component.getRest().add(position);

        IoComponentPropertyType io = new IoComponentPropertyType("level", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:level"));
        InputList inputList = new InputList(Arrays.asList(io));
        Inputs inputs = new Inputs(inputList);
        component.setInputs(inputs);

        IoComponentPropertyType io2 = new IoComponentPropertyType("depth", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:depth"));
        OutputList outputList = new OutputList(Arrays.asList(io2));
        Outputs outputs = new Outputs(outputList);
        component.setOutputs(outputs);

        List<DataComponentPropertyType> params = new ArrayList<DataComponentPropertyType>();
        UomPropertyType uom = new UomPropertyType(null, "urn:ogc:unit:minuts");
        QuantityType quantity1 = new QuantityType("urn:x-ogc:def:property:frequency", uom, 60.0);
//TODO  DataComponentPropertyType p1 = new DataComponentPropertyType("frequency", "urn:x-ogc:def:property:frequency", quantity1);
//TODO  params.add(p1);
        UomPropertyType uom2 = new UomPropertyType("m", null);
        QuantityType quantity2 = new QuantityType("urn:x-ogc:def:property:precision", uom2, 0.05);
//TODO  DataComponentPropertyType p2 = new DataComponentPropertyType("precision", "urn:x-ogc:def:property:precision", quantity2);
//TODO  params.add(p2);
        QuantityRange quantityRange = new QuantityRange(uom2, Arrays.asList(0.0, 10.0));
//TODO  DataComponentPropertyType p3 = new DataComponentPropertyType("validity", "urn:x-ogc:def:property:validity", quantityRange);
//TODO  params.add(p3);
        ParameterList paramList = new ParameterList(params);
        Parameters parameters = new Parameters(paramList);
        component.setParameters(parameters);

        member.setProcess(sml100Factory.createComponent(component));
        SensorML expectedResult = new SensorML("1.0", Arrays.asList(member));

        assertEquals(result.getMember().size(), 1);
        assertTrue(result.getMember().get(0).getProcess() != null);
        assertTrue(result.getMember().get(0).getProcess().getValue() instanceof ComponentType);

        ComponentType resultProcess = (ComponentType) result.getMember().get(0).getProcess().getValue();

        assertEquals(resultProcess.getCapabilities(), component.getCapabilities());
        
        assertTrue(resultProcess.getContact().size() == 1);
        assertEquals(resultProcess.getContact().get(0).getContactList(), component.getContact().get(0).getContactList());
        assertEquals(resultProcess.getContact().get(0).getResponsibleParty().getContactInfo(), component.getContact().get(0).getResponsibleParty().getContactInfo());
        assertEquals(resultProcess.getContact().get(0).getResponsibleParty().getOrganizationName(), component.getContact().get(0).getResponsibleParty().getOrganizationName());
        assertEquals(resultProcess.getContact().get(0).getResponsibleParty(), component.getContact().get(0).getResponsibleParty());
        assertEquals(resultProcess.getContact().get(0), component.getContact().get(0));
        assertEquals(resultProcess.getContact(), component.getContact());

        assertTrue(resultProcess.getClassification().size() == 1);
        assertTrue(resultProcess.getClassification().get(0).getClassifierList().getClassifier().size() == 2);
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm(), component.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm());
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier().get(0), component.getClassification().get(0).getClassifierList().getClassifier().get(0));
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier(), component.getClassification().get(0).getClassifierList().getClassifier());
        assertEquals(resultProcess.getClassification().get(0).getClassifierList(), component.getClassification().get(0).getClassifierList());
        assertEquals(resultProcess.getClassification().get(0), component.getClassification().get(0));
        assertEquals(resultProcess.getClassification(), component.getClassification());

        assertEquals(resultProcess.getIdentification(), component.getIdentification());

        assertEquals(resultProcess.getValidTime(), component.getValidTime());

        assertEquals(resultProcess.getParameters(), component.getParameters());

        assertEquals(resultProcess.getInputs().getInputList().getInput(), component.getInputs().getInputList().getInput());
        assertEquals(resultProcess.getInputs().getInputList(), component.getInputs().getInputList());
        assertEquals(resultProcess.getInputs(), component.getInputs());

        assertEquals(resultProcess.getOutputs(), component.getOutputs());

        assertEquals(resultProcess, component);

        assertEquals(expectedResult.getMember().get(0), result.getMember().get(0));
        assertEquals(expectedResult.getMember(), result.getMember());
        assertEquals(expectedResult, result);*/
    }

}
