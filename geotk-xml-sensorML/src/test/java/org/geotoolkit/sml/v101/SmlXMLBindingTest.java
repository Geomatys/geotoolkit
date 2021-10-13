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

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//constellation
import org.geotoolkit.sml.xml.v101.Classifier;
import org.geotoolkit.sml.xml.v101.DataSourceType;
import org.geotoolkit.sml.xml.v101.DataDefinition;
import org.geotoolkit.sml.xml.v101.ObjectFactory;
import org.geotoolkit.sml.xml.v101.IdentifierList;
import org.geotoolkit.sml.xml.v101.Identifier;
import org.geotoolkit.swe.xml.v101.TextBlockType;
import org.geotoolkit.swe.xml.v101.DataBlockDefinitionType;
import org.geotoolkit.swe.xml.v101.CodeSpacePropertyType;
import org.geotoolkit.swe.xml.v101.DataComponentPropertyType;
import org.geotoolkit.swe.xml.v101.DataRecordType;
import org.geotoolkit.swe.xml.v101.ObservableProperty;
import org.geotoolkit.swe.xml.v101.QuantityType;
import org.geotoolkit.swe.xml.v101.UomPropertyType;

// JAXB dependencies
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.test.xml.DocumentComparator;

//Junit dependencies
import org.geotoolkit.gml.xml.v311.CodeType;
import org.geotoolkit.sml.xml.SensorMLMarshallerPool;
import org.geotoolkit.sml.xml.v101.Classification;
import org.geotoolkit.sml.xml.v101.ClassifierList;
import org.geotoolkit.sml.xml.v101.ComponentPropertyType;
import org.geotoolkit.sml.xml.v101.ComponentType;
import org.geotoolkit.sml.xml.v101.Components;
import org.geotoolkit.sml.xml.v101.ComponentList;
import org.geotoolkit.sml.xml.v101.Contact;
import org.geotoolkit.sml.xml.v101.ContactInfo;
import org.geotoolkit.sml.xml.v101.Address;
import org.geotoolkit.sml.xml.v101.Phone;
import org.geotoolkit.sml.xml.v101.Identification;
import org.geotoolkit.sml.xml.v101.Inputs;
import org.geotoolkit.sml.xml.v101.Interface;
import org.geotoolkit.sml.xml.v101.Interfaces;
import org.geotoolkit.sml.xml.v101.InterfaceList;
import org.geotoolkit.sml.xml.v101.IoComponentPropertyType;
import org.geotoolkit.sml.xml.v101.Keywords;
import org.geotoolkit.sml.xml.v101.KeywordList;
import org.geotoolkit.sml.xml.v101.MethodPropertyType;
import org.geotoolkit.sml.xml.v101.OnlineResource;
import org.geotoolkit.sml.xml.v101.Outputs;
import org.geotoolkit.sml.xml.v101.Parameters;
import org.geotoolkit.sml.xml.v101.ParameterList;
import org.geotoolkit.sml.xml.v101.Position;
import org.geotoolkit.sml.xml.v101.ResponsibleParty;
import org.geotoolkit.sml.xml.v101.SensorML;
import org.geotoolkit.sml.xml.v101.SystemType;
import org.geotoolkit.sml.xml.v101.Term;
import org.geotoolkit.swe.xml.v101.QuantityRange;
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;


/**
 *
 * @author guilhem
 * @module
 */
public class SmlXMLBindingTest extends org.geotoolkit.test.TestBase {

    private ObjectFactory sml101Factory = new ObjectFactory();
    private org.geotoolkit.swe.xml.v101.ObjectFactory swe101Factory = new org.geotoolkit.swe.xml.v101.ObjectFactory();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
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
    public void SystemUnmarshallMarshalingTest() throws Exception {

        Unmarshaller unmarshaller = SensorMLMarshallerPool.getInstance().acquireUnmarshaller();

        InputStream is = SmlXMLBindingTest.class.getResourceAsStream("/org/geotoolkit/sml/system101.xml");
        Object unmarshalled = unmarshaller.unmarshal(is);
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        assertTrue(unmarshalled instanceof SensorML);

        SensorML result = (SensorML) unmarshalled;

        SensorML.Member member = new SensorML.Member();

        SystemType system = new SystemType();
        system.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-09-CTD-1");

        List<String> kw = new ArrayList<String>();
        kw.add("OCEANS");
        kw.add("OCEANS:OCEAN TEMPERATURE");
        kw.add("OCEANS:OCEAN PRESSURE");
        kw.add("OCEANS:SALINITY/DENSITY");
        kw.add("Instruments/Sensors:In Situ/Laboratory Instruments:Conductivity Sensors");
        Keywords keywords = new Keywords(new KeywordList(URI.create("urn:x-nasa:def:gcmd:keywords"), kw));
        system.setKeywords(keywords);

        CodeSpacePropertyType cs = new CodeSpacePropertyType("urn:x-ogc:dictionary::sensorTypes");
        Classifier cl2 = new Classifier("sensorType", new Term(cs, "CTD", "urn:x-ogc:def:classifier:OGC:sensorType"));

        List<Classifier> cls = new ArrayList<Classifier>();
        cls.add(cl2);

        ClassifierList claList = new ClassifierList(null, cls);
        Classification classification = new Classification(claList);
        system.setClassification(classification);

        List<Identifier> identifiers = new ArrayList<Identifier>();


        Identifier id1 = new Identifier("uniqueID", new Term("urn:ogc:object:feature:Sensor:IFREMER:13471-09-CTD-1", "urn:ogc:def:identifierType:OGC:uniqueID"));
        Identifier id2 = new Identifier("shortName", new Term("Microcat_CT_SBE37", "urn:x-ogc:def:identifier:OGC:shortName"));

        cs = new CodeSpacePropertyType("urn:x-ogc:def:identifier:SBE:modelNumber");
        Identifier id3 = new Identifier("modelNumber", new Term(cs, "", "urn:x-ogc:def:identifier:OGC:modelNumber"));

        cs = new CodeSpacePropertyType("urn:x-ogc:def:identifier:SBE:serialNumber");
        Identifier id4 = new Identifier("serialNumber", new Term(cs, "", "urn:x-ogc:def:identifier:OGC:serialNumber"));

        identifiers.add(id1);
        identifiers.add(id2);
        identifiers.add(id3);
        identifiers.add(id4);
        IdentifierList identifierList = new IdentifierList(null, identifiers);
        Identification identification = new Identification(identifierList);
        system.setIdentification(identification);

        Address address1 = new Address("1808 136th Place NE", "Bellevue", "Washington", "98005", "USA", null);
        Phone phone1     = new Phone("+1 (425) 643-9866", "+1 (425) 643-9954");
        ContactInfo contactInfo1 = new ContactInfo(phone1, address1);
        contactInfo1.setOnlineResource(new OnlineResource("http://www.seabird.com"));

        ResponsibleParty resp1 = new ResponsibleParty(null, "Sea-Bird Electronics, Inc.", null, contactInfo1);
        Contact contact1 = new Contact(null, resp1);
        contact1.setArcrole("urn:x-ogc:def:classifiers:OGC:contactType:manufacturer");

        system.setContact(Arrays.asList(contact1));

        List<ComponentPropertyType> compos = new ArrayList<ComponentPropertyType>();
        ComponentType compo1 = new ComponentType();
        compo1.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-1017-PSAL-2.0");
        List<IoComponentPropertyType> ios1 = new ArrayList<IoComponentPropertyType>();
        ios1.add(new IoComponentPropertyType("CNDC", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:CNDC")));
        ios1.add(new IoComponentPropertyType("TEMP", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:TEMP")));
        ios1.add(new IoComponentPropertyType("PRES", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:PRES")));
        Inputs inputs1 = new Inputs(ios1);
        compo1.setInputs(inputs1);

        QuantityType q = new QuantityType("urn:x-ogc:def:phenomenon:OGC:PSAL", new UomPropertyType("P.S.U", null), null);
        q.setParameterName(new CodeType("#sea_water_electrical_conductivity", "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/11/standard-name-table"));
        IoComponentPropertyType io1 = new IoComponentPropertyType("computedPSAL",q);
        Outputs outputs1 = new Outputs(Arrays.asList(io1));
        compo1.setOutputs(outputs1);

        compos.add(new ComponentPropertyType("IFREMER-13471-1017-PSAL-2.0", sml101Factory.createComponent(compo1)));

        ComponentType compo2 = new ComponentType();
        compo2.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-1017-CNDC-2.0");

        List<IoComponentPropertyType> ios2 = new ArrayList<IoComponentPropertyType>();
        ios2.add(new IoComponentPropertyType("CNDC", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:CNDC")));
        Inputs inputs2 = new Inputs(ios2);
        compo2.setInputs(inputs2);

        QuantityType q2 = new QuantityType("urn:x-ogc:def:phenomenon:OGC:CNDC", new UomPropertyType("mhos/m", null), null);
        q2.setParameterName(new CodeType("#sea_water_electrical_conductivity", "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/11/standard-name-table"));
        IoComponentPropertyType io2 = new IoComponentPropertyType("measuredCNDC",q2);
        Outputs outputs2 = new Outputs(Arrays.asList(io2));
        compo2.setOutputs(outputs2);

        compos.add(new ComponentPropertyType("IFREMER-13471-1017-CNDC-2.0", sml101Factory.createComponent(compo2)));

        ComponentType compo3 = new ComponentType();
        compo3.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-1017-PRES-2.0");
        compo3.setDescription("Conductivity detector connected to the SBE37SMP Recorder");

        List<IoComponentPropertyType> ios3 = new ArrayList<IoComponentPropertyType>();
        ios3.add(new IoComponentPropertyType("PRES", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:PRES")));
        Inputs inputs3 = new Inputs(ios3);
        compo3.setInputs(inputs3);

        UomPropertyType uom3 = new UomPropertyType("dBar", null);
        uom3.setTitle("decibar=10000 pascals");
        QuantityType q3 = new QuantityType("urn:x-ogc:def:phenomenon:OGC:PRES", uom3, null);
        q3.setParameterName(new CodeType("#sea_water_pressure", "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/11/standard-name-table"));
        IoComponentPropertyType io3 = new IoComponentPropertyType("measuredPRES",q3);
        Outputs outputs3 = new Outputs(Arrays.asList(io3));
        compo3.setOutputs(outputs3);

        compos.add(new ComponentPropertyType("IFREMER-13471-1017-PRES-2.0", sml101Factory.createComponent(compo3)));

        ComponentType compo4 = new ComponentType();
        compo4.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-1017-TEMP-2.0");
        compo4.setDescription(" Temperature detector connected to the SBE37SMP Recorder");

        List<IoComponentPropertyType> ios4 = new ArrayList<IoComponentPropertyType>();
        ios4.add(new IoComponentPropertyType("TEMP", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:TEMP")));
        Inputs inputs4 = new Inputs(ios4);
        compo4.setInputs(inputs4);

        UomPropertyType uom4 = new UomPropertyType("Cel", null);
        uom4.setTitle("Celsius degree");
        QuantityType q4 = new QuantityType("urn:x-ogc:def:phenomenon:OGC:TEMP", uom4, null);
        q4.setParameterName(new CodeType("#sea_water_temperature", "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/11/standard-name-table"));
        IoComponentPropertyType io4 = new IoComponentPropertyType("measuredTEMP",q4);
        Outputs outputs4 = new Outputs(Arrays.asList(io4));
        compo4.setOutputs(outputs4);

        List<DataComponentPropertyType> params4 = new ArrayList<DataComponentPropertyType>();
        List<DataComponentPropertyType> fields4 = new ArrayList<DataComponentPropertyType>();
        QuantityRange qr = new QuantityRange(new UomPropertyType("Cel", null), Arrays.asList(-5.0,35.0));
        qr.setDefinition("urn:x-ogc:def:sensor:dynamicRange");
        fields4.add(new DataComponentPropertyType("dynamicRange", null, qr));
        QuantityType qr2 = new QuantityType("urn:x-ogc:def:sensor:gain", null, 1.0);
        fields4.add(new DataComponentPropertyType("gain", null, qr2));
        QuantityType qr3 = new QuantityType("urn:x-ogc:def:sensor:offset", null, 0.0);
        fields4.add(new DataComponentPropertyType("offset", null, qr3));

        DataRecordType record = new DataRecordType("urn:x-ogc:def:sensor:linearCalibration", fields4);
        DataComponentPropertyType recordProp = new DataComponentPropertyType(record,"calibration");
        recordProp.setRole("urn:x-ogc:def:sensor:steadyState");
        params4.add(recordProp);

        params4.add(new DataComponentPropertyType("accuracy", "urn:x-ogc:def:sensor:OGC:accuracy", new QuantityType("urn:x-ogc:def:sensor:OGC:absoluteAccuracy", new UomPropertyType("Cel", null), 0.0020)));
        ParameterList parameterList4 = new ParameterList(params4);
        Parameters parameters4 = new Parameters(parameterList4);
        compo4.setParameters(parameters4);

        compo4.setMethod(new MethodPropertyType("urn:x-ogc:def:process:1.0:detector"));
        compos.add(new ComponentPropertyType("IFREMER-13471-1017-TEMP-2.0", sml101Factory.createComponent(compo4)));

        ComponentList componentList = new ComponentList(compos);
        Components components = new Components(componentList);
        system.setComponents(components);

        Interface i1 = new Interface("RS232", null);
        List<Interface> interfaceL = new ArrayList<Interface>();
        interfaceL.add(i1);
        InterfaceList interfaceList = new InterfaceList(null, interfaceL);
        Interfaces interfaces = new Interfaces(interfaceList);
        system.setInterfaces(interfaces);

        system.setDescription("The SBE 37-SMP MicroCAT is a high-accuracy conductivity and temperature (pressure optional) recorder with internal battery and memory, serial communication or Inductive Modem and pump (optional). Designed for moorings or other long duration, fixed-site deployments, the MicroCAT includes a standard serial interface and nonvolatile FLASH memory. Construction is of titanium and other non-corroding materials to ensure long life with minimum maintenance, and depth capability is 7000 meters (23,000 feet).");

        member.setProcess(sml101Factory.createSystem(system));
        SensorML expectedResult = new SensorML("1.0.1", Arrays.asList(member));

        assertEquals(result.getMember().size(), 1);
        assertTrue(result.getMember().get(0).getProcess() != null);
        assertTrue(result.getMember().get(0).getProcess().getValue() instanceof SystemType);

        SystemType resultProcess = (SystemType) result.getMember().get(0).getProcess().getValue();


        assertTrue(resultProcess.getContact().size() == 1);
        assertEquals(resultProcess.getContact().get(0).getContactList(), system.getContact().get(0).getContactList());
        assertEquals(resultProcess.getContact().get(0).getResponsibleParty().getContactInfo(), system.getContact().get(0).getResponsibleParty().getContactInfo());
        assertEquals(resultProcess.getContact().get(0).getResponsibleParty().getOrganizationName(), system.getContact().get(0).getResponsibleParty().getOrganizationName());
        assertEquals(resultProcess.getContact().get(0).getResponsibleParty(), system.getContact().get(0).getResponsibleParty());
        assertEquals(resultProcess.getContact().get(0), system.getContact().get(0));
        assertEquals(resultProcess.getContact(), system.getContact());

        assertTrue(resultProcess.getClassification().size() == 1);
        assertTrue(resultProcess.getClassification().get(0).getClassifierList().getClassifier().size() == 1);
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm().getCodeSpace(), system.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm().getCodeSpace());
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm().getDefinition(), system.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm().getDefinition());
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm().getValue(), system.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm().getValue());
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm(), system.getClassification().get(0).getClassifierList().getClassifier().get(0).getTerm());
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier().get(0), system.getClassification().get(0).getClassifierList().getClassifier().get(0));
        assertEquals(resultProcess.getClassification().get(0).getClassifierList().getClassifier(), system.getClassification().get(0).getClassifierList().getClassifier());
        assertEquals(resultProcess.getClassification().get(0).getClassifierList(), system.getClassification().get(0).getClassifierList());
        assertEquals(resultProcess.getClassification().get(0), system.getClassification().get(0));
        assertEquals(resultProcess.getClassification(), system.getClassification());

        assertEquals(resultProcess.getIdentification().size(), system.getIdentification().size());
        assertEquals(resultProcess.getIdentification().get(0).getIdentifierList().getIdentifier().size(), system.getIdentification().get(0).getIdentifierList().getIdentifier().size());
        assertEquals(resultProcess.getIdentification().get(0).getIdentifierList().getIdentifier(), system.getIdentification().get(0).getIdentifierList().getIdentifier());
        assertEquals(resultProcess.getIdentification().get(0).getIdentifierList(), system.getIdentification().get(0).getIdentifierList());
        assertEquals(resultProcess.getIdentification().get(0), system.getIdentification().get(0));
        assertEquals(resultProcess.getIdentification(), system.getIdentification());

        assertEquals(resultProcess.getValidTime(), system.getValidTime());

        assertEquals(resultProcess.getParameters(), system.getParameters());

        //assertEquals(resultProcess.getInputs().getInputList().getInput(), system.getInputs().getInputList().getInput());
        //assertEquals(resultProcess.getInputs().getInputList(), system.getInputs().getInputList());
        //assertEquals(resultProcess.getInputs(), system.getInputs());

        assertEquals(resultProcess.getOutputs(), system.getOutputs());

        assertEquals(resultProcess.getSMLLocation(), system.getSMLLocation());

        assertEquals(resultProcess.getPosition(), system.getPosition());

        assertEquals(resultProcess.getSpatialReferenceFrame(), system.getSpatialReferenceFrame());

        assertEquals(resultProcess.getDocumentation(), system.getDocumentation());

        assertEquals(resultProcess.getCharacteristics(), system.getCharacteristics());

        assertEquals(resultProcess.getComponents().getComponentList().getComponent().size(), system.getComponents().getComponentList().getComponent().size());
        for (int i = 0 ; i < system.getComponents().getComponentList().getComponent().size(); i++) {
            ComponentPropertyType expCP = system.getComponents().getComponentList().getComponent().get(i);
            ComponentPropertyType resCP = resultProcess.getComponents().getComponentList().getComponent().get(i);
            ComponentType expCPprocess = (ComponentType) expCP.getAbstractProcess();
            ComponentType resCPprocess = (ComponentType) resCP.getAbstractProcess();
            assertEquals(expCPprocess.getBoundedBy(), resCPprocess.getBoundedBy());
            assertEquals(expCPprocess.getCapabilities(), resCPprocess.getCapabilities());
            assertEquals(expCPprocess.getCharacteristics(), resCPprocess.getCharacteristics());
            assertEquals(expCPprocess.getClassification(), resCPprocess.getClassification());
            assertEquals(expCPprocess.getContact(), resCPprocess.getContact());
            assertEquals(expCPprocess.getDescription(), resCPprocess.getDescription());
            assertEquals(expCPprocess.getDescriptionReference(), resCPprocess.getDescriptionReference());
            assertEquals(expCPprocess.getDocumentation(), resCPprocess.getDocumentation());
            assertEquals(expCPprocess.getHistory(), resCPprocess.getHistory());
            assertEquals(expCPprocess.getId(), resCPprocess.getId());
            assertEquals(expCPprocess.getIdentification(), resCPprocess.getIdentification());
            assertEquals(expCPprocess.getIdentifier(), resCPprocess.getIdentifier());
            assertEquals(expCPprocess.getInputs(), resCPprocess.getInputs());
            assertEquals(expCPprocess.getInterfaces(), resCPprocess.getInterfaces());
            assertEquals(expCPprocess.getKeywords(), resCPprocess.getKeywords());
            assertEquals(expCPprocess.getLegalConstraint(), resCPprocess.getLegalConstraint());
            assertEquals(expCPprocess.getLocation(), resCPprocess.getLocation());
            assertEquals(expCPprocess.getName(), resCPprocess.getName());
            assertEquals(expCPprocess.getOutputs(), resCPprocess.getOutputs());
            if (expCPprocess.getParameters() != null) {
                for (int j = 0; j< expCPprocess.getParameters().getParameterList().getParameter().size(); j++) {
                    final DataComponentPropertyType expParam = expCPprocess.getParameters().getParameterList().getParameter().get(j);
                    final DataComponentPropertyType resParam = resCPprocess.getParameters().getParameterList().getParameter().get(j);
                    if (expParam.getAbstractRecord() instanceof DataRecordType) {
                        DataRecordType expRecord = (DataRecordType) expParam.getAbstractRecord();
                        DataRecordType resRecord = (DataRecordType) resParam.getAbstractRecord();
                        for (int k = 0; k< expRecord.getField().size(); k++) {
                             DataComponentPropertyType expField = expRecord.getField().get(k);
                             DataComponentPropertyType resField = resRecord.getField().get(k);
                             assertEquals(expField.getQuantityRange(), resField.getQuantityRange());
                             assertEquals(expField, resField);
                        }
                        assertEquals(expRecord.getField(), resRecord.getField());
                        assertEquals(expRecord, resRecord);
                    }
                    assertEquals(expParam.getAbstractRecord(), resParam.getAbstractRecord());
                    assertEquals(expParam, resParam);

                }
                assertEquals(expCPprocess.getParameters().getParameterList().getParameter(), resCPprocess.getParameters().getParameterList().getParameter());
                assertEquals(expCPprocess.getParameters().getParameterList(), resCPprocess.getParameters().getParameterList());
            }
            assertEquals(expCPprocess.getParameters(), resCPprocess.getParameters());
            assertEquals(expCPprocess.getParameterName(), resCPprocess.getParameterName());
            assertEquals(expCPprocess.getPosition(), resCPprocess.getPosition());
            assertEquals(expCPprocess.getSMLLocation(), resCPprocess.getSMLLocation());
            assertEquals(expCPprocess.getSecurityConstraint(), resCPprocess.getSecurityConstraint());
            assertEquals(expCPprocess.getSrsName(), resCPprocess.getSrsName());
            assertEquals(expCPprocess.getValidTime(), resCPprocess.getValidTime());
            assertEquals(expCPprocess.getMethod(), resCPprocess.getMethod());
            assertEquals(expCPprocess.getTemporalReferenceFrame(), resCPprocess.getTemporalReferenceFrame());
            assertEquals(expCPprocess.getTimePosition(), resCPprocess.getTimePosition());

            assertEquals(expCPprocess, resCPprocess);
            assertEquals(expCP, resCP);
        }
        assertEquals(resultProcess.getComponents().getComponentList().getComponent(), system.getComponents().getComponentList().getComponent());
        assertEquals(resultProcess.getComponents().getComponentList(), system.getComponents().getComponentList());
        assertEquals(resultProcess.getComponents(), system.getComponents());

        assertEquals(resultProcess.getPositions(), system.getPositions());

        assertEquals(resultProcess.getTemporalReferenceFrame(), system.getTemporalReferenceFrame());

        assertEquals(resultProcess.getConnections(), system.getConnections());

        assertEquals(resultProcess.getInterfaces(), system.getInterfaces());

        assertEquals(resultProcess.getLegalConstraint(), system.getLegalConstraint());

        assertEquals(resultProcess, system);
        assertEquals(expectedResult.getMember().get(0), result.getMember().get(0));
        assertEquals(expectedResult.getMember(), result.getMember());
        assertEquals(expectedResult, result);

        SensorMLMarshallerPool.getInstance().recycle(unmarshaller);
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void SystemMarshalingTest() throws Exception {

        SensorML.Member member = new SensorML.Member();

        SystemType system = new SystemType();
        system.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-09-CTD-1");

        List<String> kw = new ArrayList<String>();
        kw.add("OCEANS");
        kw.add("OCEANS:OCEAN TEMPERATURE");
        kw.add("OCEANS:OCEAN PRESSURE");
        kw.add("OCEANS:SALINITY/DENSITY");
        kw.add("Instruments/Sensors:In Situ/Laboratory Instruments:Conductivity Sensors");
        Keywords keywords = new Keywords(new KeywordList(URI.create("urn:x-nasa:def:gcmd:keywords"), kw));
        system.setKeywords(keywords);

        CodeSpacePropertyType cs = new CodeSpacePropertyType("urn:x-ogc:dictionary::sensorTypes");
        Classifier cl2 = new Classifier("sensorType", new Term(cs, "CTD", "urn:x-ogc:def:classifier:OGC:sensorType"));

        List<Classifier> cls = new ArrayList<Classifier>();
        cls.add(cl2);

        ClassifierList claList = new ClassifierList(null, cls);
        Classification classification = new Classification(claList);
        system.setClassification(classification);

        List<Identifier> identifiers = new ArrayList<Identifier>();


        Identifier id1 = new Identifier("uniqueID", new Term("urn:ogc:object:feature:Sensor:IFREMER:13471-09-CTD-1", "urn:ogc:def:identifierType:OGC:uniqueID"));
        Identifier id2 = new Identifier("shortName", new Term("Microcat_CT_SBE37", "urn:x-ogc:def:identifier:OGC:shortName"));

        cs = new CodeSpacePropertyType("urn:x-ogc:def:identifier:SBE:modelNumber");
        Identifier id3 = new Identifier("modelNumber", new Term(cs, "", "urn:x-ogc:def:identifier:OGC:modelNumber"));

        cs = new CodeSpacePropertyType("urn:x-ogc:def:identifier:SBE:serialNumber");
        Identifier id4 = new Identifier("serialNumber", new Term(cs, "", "urn:x-ogc:def:identifier:OGC:serialNumber"));

        identifiers.add(id1);
        identifiers.add(id2);
        identifiers.add(id3);
        identifiers.add(id4);
        IdentifierList identifierList = new IdentifierList(null, identifiers);
        Identification identification = new Identification(identifierList);
        system.setIdentification(identification);

        Address address1 = new Address("1808 136th Place NE", "Bellevue", "Washington", "98005", "USA", null);
        Phone phone1     = new Phone("+1 (425) 643-9866", "+1 (425) 643-9954");
        ContactInfo contactInfo1 = new ContactInfo(phone1, address1);
        contactInfo1.setOnlineResource(new OnlineResource("http://www.seabird.com"));

        ResponsibleParty resp1 = new ResponsibleParty(null, "Sea-Bird Electronics, Inc.", null, contactInfo1);
        Contact contact1 = new Contact(null, resp1);
        contact1.setArcrole("urn:x-ogc:def:classifiers:OGC:contactType:manufacturer");

        system.setContact(Arrays.asList(contact1));

        List<ComponentPropertyType> compos = new ArrayList<ComponentPropertyType>();
        ComponentType compo1 = new ComponentType();
        compo1.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-1017-PSAL-2.0");
        List<IoComponentPropertyType> ios1 = new ArrayList<IoComponentPropertyType>();
        ios1.add(new IoComponentPropertyType("CNDC", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:CNDC")));
        ios1.add(new IoComponentPropertyType("TEMP", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:TEMP")));
        ios1.add(new IoComponentPropertyType("PRES", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:PRES")));
        Inputs inputs1 = new Inputs(ios1);
        compo1.setInputs(inputs1);

        QuantityType q = new QuantityType("urn:x-ogc:def:phenomenon:OGC:PSAL", new UomPropertyType("P.S.U", null), null);
        q.setParameterName(new CodeType("#sea_water_electrical_conductivity", "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/11/standard-name-table"));
        IoComponentPropertyType io1 = new IoComponentPropertyType("computedPSAL",q);
        Outputs outputs1 = new Outputs(Arrays.asList(io1));
        compo1.setOutputs(outputs1);

        compos.add(new ComponentPropertyType("IFREMER-13471-1017-PSAL-2.0", sml101Factory.createComponent(compo1)));

        ComponentType compo2 = new ComponentType();
        compo2.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-1017-CNDC-2.0");

        List<IoComponentPropertyType> ios2 = new ArrayList<IoComponentPropertyType>();
        ios2.add(new IoComponentPropertyType("CNDC", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:CNDC")));
        Inputs inputs2 = new Inputs(ios2);
        compo2.setInputs(inputs2);

        QuantityType q2 = new QuantityType("urn:x-ogc:def:phenomenon:OGC:CNDC", new UomPropertyType("mhos/m", null), null);
        q2.setParameterName(new CodeType("#sea_water_electrical_conductivity", "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/11/standard-name-table"));
        IoComponentPropertyType io2 = new IoComponentPropertyType("measuredCNDC",q2);
        Outputs outputs2 = new Outputs(Arrays.asList(io2));
        compo2.setOutputs(outputs2);

        compos.add(new ComponentPropertyType("IFREMER-13471-1017-CNDC-2.0", sml101Factory.createComponent(compo2)));

        ComponentType compo3 = new ComponentType();
        compo3.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-1017-PRES-2.0");
        compo3.setDescription("Conductivity detector connected to the SBE37SMP Recorder");

        List<IoComponentPropertyType> ios3 = new ArrayList<IoComponentPropertyType>();
        ios3.add(new IoComponentPropertyType("PRES", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:PRES")));
        Inputs inputs3 = new Inputs(ios3);
        compo3.setInputs(inputs3);

        UomPropertyType uom3 = new UomPropertyType("dBar", null);
        uom3.setTitle("decibar=10000 pascals");
        QuantityType q3 = new QuantityType("urn:x-ogc:def:phenomenon:OGC:PRES", uom3, null);
        q3.setParameterName(new CodeType("#sea_water_pressure", "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/11/standard-name-table"));
        IoComponentPropertyType io3 = new IoComponentPropertyType("measuredPRES",q3);
        Outputs outputs3 = new Outputs(Arrays.asList(io3));
        compo3.setOutputs(outputs3);

        compos.add(new ComponentPropertyType("IFREMER-13471-1017-PRES-2.0", sml101Factory.createComponent(compo3)));

        ComponentType compo4 = new ComponentType();
        compo4.setId("urn-ogc-object-feature-Sensor-IFREMER-13471-1017-TEMP-2.0");
        compo4.setDescription(" Temperature detector connected to the SBE37SMP Recorder");

        List<IoComponentPropertyType> ios4 = new ArrayList<IoComponentPropertyType>();
        ios4.add(new IoComponentPropertyType("TEMP", new ObservableProperty("urn:x-ogc:def:phenomenon:OGC:TEMP")));
        Inputs inputs4 = new Inputs(ios4);
        compo4.setInputs(inputs4);

        UomPropertyType uom4 = new UomPropertyType("Cel", null);
        uom4.setTitle("Celsius degree");
        QuantityType q4 = new QuantityType("urn:x-ogc:def:phenomenon:OGC:TEMP", uom4, null);
        q4.setParameterName(new CodeType("#sea_water_temperature", "http://cf-pcmdi.llnl.gov/documents/cf-standard-names/standard-name-table/11/standard-name-table"));
        IoComponentPropertyType io4 = new IoComponentPropertyType("measuredTEMP",q4);
        Outputs outputs4 = new Outputs(Arrays.asList(io4));
        compo4.setOutputs(outputs4);

        List<DataComponentPropertyType> params4 = new ArrayList<DataComponentPropertyType>();
        List<DataComponentPropertyType> fields4 = new ArrayList<DataComponentPropertyType>();
        QuantityRange qr = new QuantityRange(new UomPropertyType("Cel", null), Arrays.asList(-5.0,35.0));
        qr.setDefinition("urn:x-ogc:def:sensor:dynamicRange");
        fields4.add(new DataComponentPropertyType("dynamicRange", null, qr));
        QuantityType qr2 = new QuantityType("urn:x-ogc:def:sensor:gain", null, 1.0);
        fields4.add(new DataComponentPropertyType("gain", null, qr2));
        QuantityType qr3 = new QuantityType("urn:x-ogc:def:sensor:offset", null, 0.0);
        fields4.add(new DataComponentPropertyType("offset", null, qr3));

        DataRecordType record = new DataRecordType("urn:x-ogc:def:sensor:linearCalibration", fields4);
        DataComponentPropertyType recordProp = new DataComponentPropertyType(record,"calibration");
        recordProp.setRole("urn:x-ogc:def:sensor:steadyState");
        params4.add(recordProp);

        params4.add(new DataComponentPropertyType("accuracy", "urn:x-ogc:def:sensor:OGC:accuracy", new QuantityType("urn:x-ogc:def:sensor:OGC:absoluteAccuracy", new UomPropertyType("Cel", null), 0.0020)));
        ParameterList parameterList4 = new ParameterList(params4);
        Parameters parameters4 = new Parameters(parameterList4);
        compo4.setParameters(parameters4);

        compo4.setMethod(new MethodPropertyType("urn:x-ogc:def:process:1.0:detector"));
        compos.add(new ComponentPropertyType("IFREMER-13471-1017-TEMP-2.0", sml101Factory.createComponent(compo4)));

        ComponentList componentList = new ComponentList(compos);
        Components components = new Components(componentList);
        system.setComponents(components);

        Interface i1 = new Interface("RS232", null);
        List<Interface> interfaceL = new ArrayList<Interface>();
        interfaceL.add(i1);
        InterfaceList interfaceList = new InterfaceList(null, interfaceL);
        Interfaces interfaces = new Interfaces(interfaceList);
        system.setInterfaces(interfaces);

        system.setDescription("The SBE 37-SMP MicroCAT is a high-accuracy conductivity and temperature (pressure optional) recorder with internal battery and memory, serial communication or Inductive Modem and pump (optional). Designed for moorings or other long duration, fixed-site deployments, the MicroCAT includes a standard serial interface and nonvolatile FLASH memory. Construction is of titanium and other non-corroding materials to ensure long life with minimum maintenance, and depth capability is 7000 meters (23,000 feet).");

        member.setProcess(sml101Factory.createSystem(system));
        SensorML sensor = new SensorML("1.0.1", Arrays.asList(member));

        Marshaller m = SensorMLMarshallerPool.getInstance().acquireMarshaller();

        StringWriter sw = new StringWriter();
        m.marshal(sensor, sw);

        String result = sw.toString();

        InputStream in = SmlXMLBindingTest.class.getResourceAsStream("/org/geotoolkit/sml/system101.xml");
        StringWriter out = new StringWriter();
        byte[] buffer = new byte[1024];
        int size;

        while ((size = in.read(buffer, 0, 1024)) > 0) {
            out.write(new String(buffer, 0, size));
        }

        String expResult = out.toString();

        final DocumentComparator comparator = new DocumentComparator(expResult, result){
            @Override
            protected strictfp void compareAttributeNode(Attr expected, Node actual) {
                super.compareAttributeNode(expected, actual);
            }
        };
        comparator.ignoredAttributes.add("http://www.w3.org/2000/xmlns:*");
        comparator.ignoredAttributes.add("http://www.w3.org/2001/XMLSchema-instance:schemaLocation");
        comparator.compare();

        SensorMLMarshallerPool.getInstance().recycle(m);
    }

    @Test
    public void DataSourceMarshalingTest() throws Exception {
        final SystemType system = new SystemType();
        final List<DataComponentPropertyType> fields = new ArrayList<>();
        fields.add(DataComponentPropertyType.LATITUDE_FIELD);
        fields.add(DataComponentPropertyType.LONGITUDE_FIELD);
        fields.add(DataComponentPropertyType.TIME_FIELD);
        final DataRecordType posRecord = new DataRecordType(null, fields);
        final DataBlockDefinitionType definition = new DataBlockDefinitionType(null, Arrays.asList(posRecord), TextBlockType.DEFAULT_ENCODING);
        final DataDefinition dataDefinition = new DataDefinition(definition);
        final org.geotoolkit.sml.xml.v101.Values trajValues = new org.geotoolkit.sml.xml.v101.Values();
        trajValues.setAny("test");
        final DataSourceType datasource = new DataSourceType(dataDefinition, trajValues, null);
        final Position pos  = new Position(null, datasource);

        system.setPosition(pos);
        DataSourceType expds = (DataSourceType) pos.getAbstractProcess();
        DataSourceType resds = (DataSourceType) system.getPosition().getAbstractProcess();
        assertEquals(expds.getDataDefinition(), resds.getDataDefinition());
        assertEquals(expds, resds);
        assertEquals(pos.getAbstractProcess(), system.getPosition().getAbstractProcess());
        assertEquals(pos.getPosition(), system.getPosition().getPosition());
        assertEquals(pos, system.getPosition());
        final SensorML sml =  new SensorML("1.0.1", Arrays.asList(new SensorML.Member(system)));
        Marshaller m = SensorMLMarshallerPool.getInstance().acquireMarshaller();
        ObjectFactory factory = new ObjectFactory();
        //m.marshal(factory.createPosition(pos), System.out);
        //m.marshal(factory.createSystem(system), System.out);
        //m.marshal(sml, System.out);
        SensorMLMarshallerPool.getInstance().recycle(m);
    }
}
