/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2018, Geomatys
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
package org.geotoolkit.xml.parameter;

import org.opengis.parameter.ParameterDescriptor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.geotoolkit.parameter.ParameterGroup;
import org.apache.sis.referencing.NamedIdentifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;
import org.apache.sis.parameter.DefaultParameterValue;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.DefaultInternationalString;
import org.geotoolkit.parameter.Parameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>Provids a general tests for different values class, topologies etc.</p>
 *
 * @author Samuel Andrés
 */
public class ParameterTest extends org.geotoolkit.test.TestBase {

    private static final String PARAMETER_FILE =
            "src/test/resources/org/geotoolkit/xml/parameter/parameterWriting.xml";
    private static final String WRITING_ALIAS_FILE =
            "src/test/resources/org/geotoolkit/xml/parameter/parameterWritingAlias.xml";
    private static final String SCHEMA_FILE =
            "src/test/resources/org/geotoolkit/xml/parameter/parameterSchema.xml";

    private static File WRITING_FILE;
    private static File WRITING_SCHEMA;
    private static File WRITING_AFTER_READING_FILE;
    private static File WRITING_AFTER_READING_SCHEMA;

    private static ParameterValueGroup INITIAL;
    private static ParameterDescriptorGroup INITIAL_DESC;
    private static ParameterDescriptorGroup ALIAS_DESC;
    private static ParameterValueGroup FINAL;
    private static ParameterDescriptorGroup FINAL_DESC;

    public ParameterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException, XMLStreamException, ClassNotFoundException {
        INITIAL = generateGeneralParameter();
        INITIAL_DESC = INITIAL.getDescriptor();
        writing_Operation(INITIAL);
        FINAL = reading_reWriting_Operations();
        FINAL_DESC = FINAL.getDescriptor();

        final ParameterBuilder pb = new ParameterBuilder(INITIAL_DESC);
        pb.addName("aliasGD");
        ALIAS_DESC = pb.createGroup(INITIAL_DESC.descriptors().toArray(new GeneralParameterDescriptor[0]));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void compareTest() throws IOException, XMLStreamException{
        // level 1 : structure of initial reading
        assertParameterRead(INITIAL);

        // level 2 : structure of final reading
        assertParameterRead(FINAL);

        // level 3 : equality of descriptors
       // assertTrue(FINAL_DESC.equals(INITIAL_DESC));
//        assertTrue(((DefaultParameterDescriptorGroup)FINAL_DESC).equals(INITIAL_DESC, ComparisonMode.IGNORE_METADATA));
        assertEquals(FINAL_DESC, INITIAL_DESC);

        // level 4 : equality of values
//        Assert.assertParameterEquals((ParameterValueGroup)FINAL, (ParameterValueGroup)INITIAL, 0);
        assertEquals(FINAL, INITIAL);
    }

    /**
     * <p>This method check reading values.</p>
     *
     * @param gpv
     * @throws IOException
     * @throws XMLStreamException
     */
    public static void assertParameterRead(final GeneralParameterValue gpv) throws IOException, XMLStreamException {
        assertTrue(gpv instanceof ParameterValueGroup);
        final ParameterValueGroup globalValueGroup = (ParameterValueGroup) gpv;
        final ParameterDescriptorGroup globalDescritorGroup = globalValueGroup.getDescriptor();
        assertEquals(1, globalDescritorGroup.descriptors().size());
        assertEquals("nameGD",globalDescritorGroup.getName().getCode());

        assertTrue(globalDescritorGroup.descriptors().get(0) instanceof ParameterDescriptorGroup);
        final ParameterDescriptorGroup descriptorGroup = (ParameterDescriptorGroup) globalDescritorGroup.descriptors().get(0);
        assertEquals("nameDG",descriptorGroup.getName().getCode());
        assertEquals("yokoyoko",descriptorGroup.getRemarks().toString());

        assertEquals(8, descriptorGroup.descriptors().size());

        assertTrue(descriptorGroup.descriptors().get(0) instanceof ParameterDescriptor);
        final ParameterDescriptor descriptor1 = (ParameterDescriptor) descriptorGroup.descriptors().get(0);
        final Set<String> valid1 = new TreeSet<>();
        valid1.add("salut");
        valid1.add("defaut");
        assertEquals(java.util.Collections.unmodifiableSet(valid1), descriptor1.getValidValues());
        assertEquals("defaut", descriptor1.getDefaultValue());
        assertEquals(String.class, descriptor1.getValueClass());
        assertEquals(new NamedIdentifier(null, "nameD1"), descriptor1.getName());

        assertTrue(descriptorGroup.descriptors().get(1) instanceof ParameterDescriptor);
        final ParameterDescriptor descriptor2 = (ParameterDescriptor) descriptorGroup.descriptors().get(1);
        assertEquals("remarks", descriptor2.getRemarks().toString());
        assertEquals(null, descriptor2.getValidValues());
        assertEquals("sauce", descriptor2.getDefaultValue());
        assertEquals(String.class, descriptor2.getValueClass());
        assertEquals(new NamedIdentifier(null, "nameD2"), descriptor2.getName());

        assertTrue(descriptorGroup.descriptors().get(2) instanceof ParameterDescriptor);
        final ParameterDescriptor descriptor3 = (ParameterDescriptor) descriptorGroup.descriptors().get(2);
        final Set<String> valid3 = new TreeSet<>();
        valid3.add("autruche");
        valid3.add("oiseau");
        valid3.add("poulet");
        assertEquals(java.util.Collections.unmodifiableSet(valid3), descriptor3.getValidValues());
        assertEquals("autruche", descriptor3.getDefaultValue());
        assertEquals(String.class, descriptor3.getValueClass());
        assertEquals(new NamedIdentifier(null, "nameD3"), descriptor3.getName());

        assertTrue(descriptorGroup.descriptors().get(3) instanceof ParameterDescriptor);
        final ParameterDescriptor descriptor4 = (ParameterDescriptor) descriptorGroup.descriptors().get(3);
        final Set<String> valid4 = new TreeSet<>();
        valid4.add("train");
        valid4.add("transport");
        valid4.add("voiture");
        assertEquals(java.util.Collections.unmodifiableSet(valid4), descriptor4.getValidValues());
        assertEquals("voiture", descriptor4.getDefaultValue());
        assertEquals(String.class, descriptor4.getValueClass());
        assertNull(descriptor4.getName().getAuthority());
        //assertEquals(new SimpleInternationalString("myTitle"),descriptor4.getName().getAuthority().getTitle());
        assertEquals("nameD4",descriptor4.getName().getCode());

        assertTrue(descriptorGroup.descriptors().get(4) instanceof ParameterDescriptor);
        final ParameterDescriptor descriptor5 = (ParameterDescriptor) descriptorGroup.descriptors().get(4);
        assertEquals(4.1, descriptor5.getMinimumValue());
        assertEquals(7.2, descriptor5.getMaximumValue());
        assertEquals(4.3, descriptor5.getDefaultValue());
        assertEquals(Double.class, descriptor5.getValueClass());
        assertNull(descriptor5.getName().getAuthority());
        //assertEquals(new SimpleInternationalString("myTitle"),descriptor5.getName().getAuthority().getTitle());
        assertEquals("nameD5",descriptor5.getName().getCode());

        assertTrue(descriptorGroup.descriptors().get(5) instanceof ParameterDescriptor);
        final ParameterDescriptor descriptor6 = (ParameterDescriptor) descriptorGroup.descriptors().get(5);
        assertEquals(false, descriptor6.getMinimumValue());
        assertEquals(true, descriptor6.getMaximumValue());
        assertEquals(false, descriptor6.getDefaultValue());
        assertEquals(Boolean.class, descriptor6.getValueClass());
        assertNull(descriptor6.getName().getAuthority());
        //assertEquals(new SimpleInternationalString("myTitle"),descriptor6.getName().getAuthority().getTitle());
        assertEquals("nameD6",descriptor6.getName().getCode());

        assertTrue(descriptorGroup.descriptors().get(6) instanceof ParameterDescriptor);
        final ParameterDescriptor descriptor7 = (ParameterDescriptor) descriptorGroup.descriptors().get(6);
        assertEquals("Alphabet", descriptor7.getMinimumValue());
        assertEquals("zanzibar", descriptor7.getMaximumValue());
        assertEquals("chocobo", descriptor7.getDefaultValue());
        assertEquals(String.class, descriptor7.getValueClass());
        assertNull(descriptor7.getName().getAuthority());
        //assertEquals(new SimpleInternationalString("myTitle"),descriptor7.getName().getAuthority().getTitle());
        assertEquals("nameD7",descriptor7.getName().getCode());

        assertTrue(descriptorGroup.descriptors().get(7) instanceof ParameterDescriptor);
        final ParameterDescriptor descriptor8 = (ParameterDescriptor) descriptorGroup.descriptors().get(7);
        assertEquals("remarks", descriptor8.getRemarks().toString());
        assertEquals(null, descriptor8.getValidValues());
        assertEquals(null, descriptor8.getDefaultValue());
        assertEquals(String.class, descriptor8.getValueClass());
        assertEquals(new NamedIdentifier(null, "name D8"), descriptor8.getName());
    }

    /**
     * <p>This method builds a parameter (value and descriptor)</p>
     *
     * @return
     */
    public static ParameterValueGroup generateGeneralParameter() {
        // PARAMETER VALUE 1
        final String value1 = "salut";
        final String def1 = "defaut";
        final String[] valid1 = new String[]{"defaut", "salut"};
        final ParameterDescriptor descriptor1 = new ParameterBuilder()
                .addName("nameD1")
                .createEnumerated(String.class, valid1, def1);
        final DefaultParameterValue parameter1 = new DefaultParameterValue(descriptor1);
        parameter1.setValue(value1);

        // PARAMETER VALUE 2
        final String value2 = "cuisine";
        final String def2 = "sauce";
        final CharSequence remarks = "remarks";
        final ParameterDescriptor descriptor2 = new ParameterBuilder()
                .addName("nameD2")
                .setRemarks(remarks)
                .setRequired(true)
                .create(String.class, def2);
        final DefaultParameterValue parameter2 = new DefaultParameterValue(descriptor2);
        parameter2.setValue(value2);

        // PARAMETER VALUE 3
        final String value3 = "oiseau";
        final String def3 = "autruche";
        final String[] valid3 = new String[]{"autruche", "oiseau", "poulet"};
        final ParameterDescriptor descriptor3 = new ParameterBuilder()
                .addName("nameD3")
                .setRequired(true)
                .createEnumerated(String.class, valid3, def3);
        final DefaultParameterValue parameter3 = new DefaultParameterValue(descriptor3);
        parameter3.setValue(value3);

        // PARAMETER VALUE 4
        final String value4 = "transport";
        final String def4 = "voiture";
        final String[] valid4 = new String[]{"train", "transport", "voiture"};
        final Citation authority4 = null;//new DefaultCitation("myTitle");
        final ParameterDescriptor descriptor4 = new ParameterBuilder()
                .addName(authority4, "nameD4")
                .setRequired(true)
                .createEnumerated(String.class, valid4, def4);
        final DefaultParameterValue parameter4 = new DefaultParameterValue(descriptor4);
        parameter4.setValue(value4);

        // PARAMETER VALUE 5
        final Double value5 = 5.2;
        final Double def5 = 4.3;
        //ResponsibleParty party = new DefaultResponsibleParty(Role.USER);
        final Citation authority5 = null;// new DefaultCitation("myTitle");//new DefaultCitation(party);
        final ParameterDescriptor descriptor5 = new ParameterBuilder()
                .addName(authority5, "nameD5")
                .setRequired(true)
                .createBounded(Double.class, 4.1, 7.2, def5);
        final DefaultParameterValue parameter5 = new DefaultParameterValue(descriptor5);
        parameter5.setValue(value5);

        // PARAMETER VALUE 6
        final Boolean value6 = true;
        final Boolean def6 = false;
        //ResponsibleParty party = new DefaultResponsibleParty(Role.USER);
        final Citation authority6 = null;//new DefaultCitation("myTitle");//new DefaultCitation(party);
        final ParameterDescriptor descriptor6 = new ParameterBuilder()
                .addName(authority6, "nameD6")
                .setRequired(true)
                .createBounded(Boolean.class, false,true,def6);
        final DefaultParameterValue parameter6 = new DefaultParameterValue(descriptor6);
        parameter6.setValue(value6);

        // PARAMETER VALUE 7
        final String value7 = "bigoudi";
        final String def7 = "chocobo";
        //ResponsibleParty party = new DefaultResponsibleParty(Role.USER);
        final Citation authority7 = null; //new DefaultCitation("myTitle");//new DefaultCitation(party);
        final ParameterDescriptor descriptor7 = new ParameterBuilder()
                .addName(authority7, "nameD7")
                .setRequired(true)
                .createBounded(String.class, "Alphabet", "zanzibar", def7);
        final DefaultParameterValue parameter7 = new DefaultParameterValue(descriptor7);
        parameter7.setValue(value7);

         // PARAMETER VALUE 8
        final String value8 = "lapis lazulis";
        final ParameterDescriptor descriptor8 = new ParameterBuilder()
                .addName("name D8")
                .setRemarks(remarks)
                .setRequired(true)
                .create(String.class, null);
        final DefaultParameterValue parameter8 = new DefaultParameterValue(descriptor8);
        parameter8.setValue(value8);

        // PARAMETER VALUE GROUP
        final GeneralParameterDescriptor[] descriptors = new GeneralParameterDescriptor[]{
            descriptor1, descriptor2, descriptor3, descriptor4, descriptor5, descriptor6, descriptor7, descriptor8};
        final Map<String, Object> properties = new HashMap<>();
        properties.put("name", "nameDG");
        properties.put("remarks", new DefaultInternationalString("yokoyoko"));
        final ParameterDescriptorGroup descriptorGroup = new DefaultParameterDescriptorGroup(
                properties, 1, 2, descriptors);
        final GeneralParameterValue[] valueGroup = new GeneralParameterValue[]{
            parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8};
        final ParameterValueGroup parameterGroup = new ParameterGroup(descriptorGroup, valueGroup);

        // GLOBAL PARAMETER VALUE GROUP
        final GeneralParameterDescriptor[] globalDescriptors = new GeneralParameterDescriptor[]{
            descriptorGroup};
        final Map<String, Object> globalProperties = new HashMap<>();
        globalProperties.put("name", "nameGD");
        final ParameterDescriptorGroup globalDescriptorGroup = new DefaultParameterDescriptorGroup(
                globalProperties, 1, 1, globalDescriptors);
        final GeneralParameterValue[] globalValues = new GeneralParameterValue[]{
            parameterGroup, parameterGroup};
        final ParameterValueGroup globalParameter = new ParameterGroup(
                globalDescriptorGroup, globalValues);

        return globalParameter;
    }

    /**
     * <p>This method writes a value into an XML file and its descriptor into a schema file.</p>
     *
     * @param globalParameter
     * @throws IOException
     * @throws XMLStreamException
     */
    public static void writing_Operation(final GeneralParameterValue globalParameter)
            throws IOException, XMLStreamException {

        WRITING_FILE = File.createTempFile("parameter", "xml");
        final ParameterValueWriter writer = new ParameterValueWriter();
        writer.setOutput(WRITING_FILE);
        writer.write(globalParameter);
        writer.dispose();

        WRITING_SCHEMA = File.createTempFile("schema", "xml");
        final ParameterDescriptorWriter schemaWriter = new ParameterDescriptorWriter();
        schemaWriter.setOutput(WRITING_SCHEMA);
        schemaWriter.write(globalParameter.getDescriptor());
        schemaWriter.dispose();

    }

    /**
     * <p>This method tests reading file value with a descriptor reader.</p>
     *
     * <p>Then, it writes an XML file for the value, and an associated schema for
     * the descriptor.</p>
     *
     * @return
     * @throws IOException
     * @throws XMLStreamException
     */
    public static ParameterValueGroup reading_reWriting_Operations()
            throws IOException, XMLStreamException, ClassNotFoundException {

        final ParameterDescriptorReader descriptorReader = new ParameterDescriptorReader();
        descriptorReader.setInput(new File(SCHEMA_FILE));

        final ParameterValueReader valueReader = new ParameterValueReader(descriptorReader);
        valueReader.setInput(new File(PARAMETER_FILE));

        final GeneralParameterValue parameterValue = valueReader.read();
        valueReader.dispose();
        descriptorReader.dispose();

        final ParameterValueWriter valueWriter = new ParameterValueWriter();
        WRITING_AFTER_READING_FILE = File.createTempFile("parameter-after-read", "xml");
        valueWriter.setOutput(WRITING_AFTER_READING_FILE);
        valueWriter.write(parameterValue);
        valueWriter.dispose();

        final ParameterDescriptorWriter descriptorWriter = new ParameterDescriptorWriter();
        WRITING_AFTER_READING_SCHEMA = File.createTempFile("schema-after-read", "xml");
        descriptorWriter.setOutput(WRITING_AFTER_READING_SCHEMA);
        descriptorWriter.write(parameterValue.getDescriptor());
        descriptorWriter.dispose();
        return (ParameterValueGroup) parameterValue;
    }

    /**
     * <p>This method tests reading file value with a descriptor instance.</p>
     *
     * @throws IOException
     * @throws XMLStreamException
     */
    @Test
    public void simpleReadingValueXMLTest()
            throws IOException, XMLStreamException {

        final ParameterValueReader valueReader = new ParameterValueReader(INITIAL_DESC);
        valueReader.setInput(new File(PARAMETER_FILE));
        final GeneralParameterValue parameterValue = valueReader.read();
        valueReader.dispose();
        assertEquals(INITIAL, parameterValue);
    }

    /**
     * <p>This method tests reading file value with a descriptor instance.</p>
     *
     * @throws IOException
     * @throws XMLStreamException
     */
    @Test
    public void aliasReadingValueXMLTest()
            throws IOException, XMLStreamException {

        final ParameterValueReader valueReader = new ParameterValueReader(ALIAS_DESC);
        valueReader.setInput(new File(WRITING_ALIAS_FILE));
        final ParameterValueGroup parameterValue = (ParameterValueGroup) valueReader.read();
        valueReader.dispose();
        assertEquals(Parameters.toMap(INITIAL), Parameters.toMap(parameterValue));
    }

    /**
     * <p>This method tests reading file descriptor.</p>
     *
     * @throws IOException
     * @throws XMLStreamException
     */
    @Test
    public void simpleReadingDescriptorXSDTest()
            throws IOException, XMLStreamException, ClassNotFoundException {

        final ParameterDescriptorReader descriptorReader = new ParameterDescriptorReader();
        descriptorReader.setInput(new File(SCHEMA_FILE));
        descriptorReader.read();
        final GeneralParameterDescriptor parameterDescriptor = descriptorReader.getDescriptorsRoot();
        descriptorReader.dispose();
        assertEquals(INITIAL_DESC, parameterDescriptor);
    }

}
