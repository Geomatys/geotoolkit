/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.xml.StaxStreamReader;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.util.InternationalString;
import static org.geotoolkit.xml.parameter.ParameterConstants.*;

/**
 * <p>This class provides a GeneralParameterValue reading method.</p>
 *
 * @author Samuel Andrés
 * @module pending
 */
public class ParameterDescriptorReader extends StaxStreamReader {

    private Map<String, GeneralParameterDescriptor> descriptors = new HashMap<String, GeneralParameterDescriptor>();
    private GeneralParameterDescriptor root;

    /**
     * <p>This method reads a parameter document whose root is
     * a GeneralParameterValue</p>
     *
     * @return
     */
    public void read() throws XMLStreamException, ClassNotFoundException {
        while (reader.hasNext()) {

            switch (reader.next()) {

                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XSD.equals(eUri)) {
                        if (TAG_XSD_ELEMENT.equals(eName)) {
                            String descriptorCodeName = reader.getAttributeValue(null, ATT_XSD_NAME).replace('_', ' ');

                            int maxOccurs = (reader.getAttributeValue(null, ATT_XSD_MAX_OCCURS) == null)
                                    ? 1 : this.readOccurrences(reader.getAttributeValue(null, ATT_XSD_MAX_OCCURS));
                            int minOccurs = (reader.getAttributeValue(null, ATT_XSD_MIN_OCCURS) == null)
                                    ? 1 : this.readOccurrences(reader.getAttributeValue(null, ATT_XSD_MIN_OCCURS));

                            String defaultValue = reader.getAttributeValue(null, ATT_XSD_DEFAULT);
                            this.root = this.readGeneralParameterDescriptor(
                                    descriptorCodeName, minOccurs, maxOccurs, defaultValue);
                        }
                    }
                    break;
            }
        }
    }

    /**
     *
     * @return General Parameter Descriptor root.
     */
    public GeneralParameterDescriptor getDescriptorsRoot() {
        return this.root;
    }

    /**
     *
     * @return Map of all contained descriptors.
     */
    public Map<String, GeneralParameterDescriptor> getDescriptorsMap() {
        return this.descriptors;
    }

    /**
     * <p>Reads an occurrence value.</p>
     *
     * @param occurrence
     * @return
     */
    private int readOccurrences(String occurrence) {
        if (VAL_XSD_UNBOUNDED.equals(occurrence)) {
            return Integer.MAX_VALUE;
        } else {
            return Integer.parseInt(occurrence);
        }
    }

    /**
     * <p>Reads a general parameter descriptor.</p>
     *
     * @param descriptorCodeName
     * @param minOcc
     * @param maxOcc
     * @param defaultValue
     * @return
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     */
    private GeneralParameterDescriptor readGeneralParameterDescriptor(
            String descriptorCodeName, int minOcc, int maxOcc, String defaultValue)
            throws XMLStreamException, ClassNotFoundException {

        GeneralParameterDescriptor descriptor = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {

                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XSD.equals(eUri)) {
                        if (TAG_XSD_SIMPLE_TYPE.equals(eName)) {
                            descriptor = this.readParameterDescriptor(
                                    descriptorCodeName, minOcc, defaultValue);
                        } else if (TAG_XSD_SEQUENCE.equals(eName)) {
                            descriptor = this.readParameterDescriptorGroup(
                                    descriptorCodeName, minOcc, maxOcc);
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_XSD_ELEMENT.equals(reader.getLocalName())
                            && URI_XSD.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }
        }

        this.descriptors.put(descriptorCodeName, descriptor);
        return descriptor;
    }

    /**
     * <p>This method reads a ParameterDescriptor</p>
     *
     * @param descriptorCodeName
     * @param minOcc
     * @param maxOcc
     * @param defaultValue
     * @return
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     */
    private ParameterDescriptor readParameterDescriptor(
            String descriptorCodeName, int minOcc, Object defaultValue)
            throws XMLStreamException, ClassNotFoundException {

        Class c = null;
        ReferenceIdentifier name = null;
        SimpleEntry<Object, ValueType>[] values = null;
        ValuesTopology topology = null;
        CharSequence remarks = null;
        Citation authority = null;
        Comparable minimum = null, maximum = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XSD.equals(eUri)) {
                        if (TAG_XSD_RESTRICTION.equals(eName)) {
                            SimpleEntry<ValuesTopology, SimpleEntry<Object, ValueType>[]> result = this.readValues(c);
                            values = result.getValue();
                            topology = result.getKey();
                        } else if (TAG_XSD_DOCUMENTATION.equals(eName)) {
                            remarks = reader.getElementText();
                        } else if (TAG_XSD_APP_INFO.equals(eName)) {
                            String[] appInfoLine = reader.getElementText().split(":");

                            if ("valueClass".equals(appInfoLine[0])) {
                                c = Class.forName(appInfoLine[1]);
                            }
//                            if("codeSpace".equals(appInfoLine[0])){
//                                codeSpace = appInfoLine[1];
//                            }
//                            if("version".equals(appInfoLine[0])){
//                                version = appInfoLine[1];
//                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_XSD_SIMPLE_TYPE.equals(reader.getLocalName())
                            && URI_XSD.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        // MIN and/or MAX values
        final Object[] valuesTab = new Object[values.length];
        for (int i = 0, length = values.length; i < length; i++) {
            valuesTab[i] = values[i].getKey();
            if (values[i].getValue() == ValueType.MIN) {
                minimum = (Comparable) valuesTab[i];
            } else if (values[i].getValue() == ValueType.MAX) {
                maximum = (Comparable) valuesTab[i];
            }
        }

        // DEFAULT value
        if (defaultValue != null) {
            defaultValue = Converters.convert(defaultValue, c);
        }

        // Are values in INTERVAL or DISCRETE range ?
        if (topology == ValuesTopology.INTERVAL) {
            // Is there minimum and/or maximum boundarie ?
            if (minimum == null && maximum == null) {
                return new DefaultParameterDescriptor(
                        descriptorCodeName, remarks, c, defaultValue, minOcc != 0);
            } else {
                return new DefaultParameterDescriptor(
                        authority, descriptorCodeName, c, null, defaultValue, minimum, maximum, null, minOcc != 0);
            }
        } else {
            return new DefaultParameterDescriptor(
                    authority, descriptorCodeName, c, valuesTab, defaultValue, minimum, maximum, null, minOcc != 0);
        }
    }

    /**
     * <p>This method reads a ParameterDescriptorGroup</p>
     *
     * @param descriptorCodeName
     * @param minimumOccurs
     * @param maximumOccurs
     * @return
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     */
    private ParameterDescriptorGroup readParameterDescriptorGroup(
            String descriptorCodeName, int minimumOccurs, int maximumOccurs)
            throws XMLStreamException, ClassNotFoundException {

        final List<GeneralParameterDescriptor> descs = new ArrayList<GeneralParameterDescriptor>();
        final Map<String, Object> properties = new HashMap<String, Object>();
        String codeSpace = null, version = null;
        InternationalString remarks = null;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XSD.equals(eUri)) {
                        if (TAG_XSD_ELEMENT.equals(eName)) {
                            String dcn = reader.getAttributeValue(null, ATT_XSD_NAME).replace('_', ' ');
                            int maxO = (reader.getAttributeValue(null, ATT_XSD_MAX_OCCURS) == null)
                                    ? 1 : this.readOccurrences(reader.getAttributeValue(null, ATT_XSD_MAX_OCCURS));
                            int minO = (reader.getAttributeValue(null, ATT_XSD_MIN_OCCURS) == null)
                                    ? 1 : this.readOccurrences(reader.getAttributeValue(null, ATT_XSD_MIN_OCCURS));
                            String defaultValue = reader.getAttributeValue(null, ATT_XSD_DEFAULT);
                            descs.add(this.readGeneralParameterDescriptor(dcn, minO, maxO, defaultValue));
                        } else if (TAG_XSD_DOCUMENTATION.equals(eName)) {
                            remarks = new DefaultInternationalString(reader.getElementText());
                        } else if (TAG_XSD_APP_INFO.equals(eName)) {
//                            String[] appInfoLine = reader.getElementText().split(":");
//
//                            if("codeSpace".equals(appInfoLine[0])){
//                                codeSpace = appInfoLine[1];
//                            }
//                            if("version".equals(appInfoLine[0])){
//                                version = appInfoLine[1];
//                            }
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_XSD_SEQUENCE.equals(reader.getLocalName())
                            && URI_XSD.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        properties.put("name", this.readName(descriptorCodeName, codeSpace, version));
        properties.put("remarks", remarks);

        return new DefaultParameterDescriptorGroup(properties, minimumOccurs, maximumOccurs,
                descs.toArray(new GeneralParameterDescriptor[descs.size()]));
//        return new DefaultParameterDescriptorGroup(
//                name.getCode(),
//                descriptors.toArray(new GeneralParameterDescriptor[descriptors.size()]));
    }

    /**
     * <p>This method reads a descriptor name.</p>
     *
     * @param code
     * @param codeSpace
     * @param version
     * @return
     */
    private ReferenceIdentifier readName(
            String code, String codeSpace, String version) {

//        Citation authority = null;
        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("code", code);

        // UNSUPPORTED PROPERTIES
//        properties.put("authority", authority);
//        properties.put("codespace", codeSpace);
//        properties.put("version", version);

        return new NamedIdentifier(properties);
        //return new NamedIdentifier(authority, code, version);
        //return new DefaultReferenceIdentifier(authority, codeSpace, code, version, remarks);
    }

    /**
     * <p>This method reads values with them type (default, maximum...).
     * If a value has not particular type, ValueType is set to null.</p>
     *
     * @param c
     * @return
     * @throws XMLStreamException
     * @throws ClassNotFoundException
     */
    private SimpleEntry<ValuesTopology, SimpleEntry<Object, ValueType>[]> readValues(Class c)
            throws XMLStreamException, ClassNotFoundException {

        final List<SimpleEntry<Object, ValueType>> values = new ArrayList<SimpleEntry<Object, ValueType>>();
        String minString = null, maxString = null;
        ValuesTopology topology = ValuesTopology.INTERVAL;

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_XSD.equals(eUri)) {

                        // INTERVAL TOPOLOGY
                        if (TAG_XSD_MAX_INCLUSIVE.equals(eName)) {
                            ValueType tv = ValueType.MAX;
                            Object ov = Converters.convert(
                                    reader.getAttributeValue(null, ATT_XSD_VALUE), c);
                            values.add(new SimpleEntry<Object, ValueType>(ov, tv));
                        } else if (TAG_XSD_MIN_INCLUSIVE.equals(eName)) {
                            ValueType tv = ValueType.MIN;
                            Object ov = Converters.convert(
                                    reader.getAttributeValue(null, ATT_XSD_VALUE), c);
                            values.add(new SimpleEntry<Object, ValueType>(ov, tv));
                        } else if (TAG_XSD_PATTERN.equals(eName)) {

                            if (Boolean.class.equals(c)) {
                                ValueType tv = null;
                                Boolean ov = (Boolean) Converters.convert(
                                        reader.getAttributeValue(null, ATT_XSD_VALUE), c);
                                tv = (ov) ? ValueType.MAX : ValueType.MIN;
                                values.add(new SimpleEntry<Object, ValueType>(ov, tv));
                            } else {
                                String tempString = reader.getAttributeValue(null, ATT_XSD_VALUE);

                                // Index of the beginning of variable pattern part
                                int idx = tempString.indexOf('[');

                                if (idx == -1) {
                                    idx = tempString.length();
                                }

                                // Update of min and max Strings
                                if (idx > 0) {
                                    tempString = tempString.substring(0, idx);
                                    if (minString == null) {
                                        minString = tempString;
                                        maxString = minString;
                                    } else if (tempString.compareTo(minString) < 0) {
                                        minString = tempString;
                                    } else if (tempString.compareTo(maxString) > 0) {
                                        maxString = tempString;
                                    }
                                }
                            }
                        } // DISCRETE TOPOLOGY
                        else if (TAG_XSD_ENUMERATION.equals(eName)) {
                            topology = ValuesTopology.DISCRETE;
                            ValueType tv = null;
                            Object ov = Converters.convert(
                                    reader.getAttributeValue(null, ATT_XSD_VALUE), c);
                            values.add(new SimpleEntry<Object, ValueType>(ov, tv));
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_XSD_RESTRICTION.equals(reader.getLocalName())
                            && URI_XSD.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        // If String interval
        if (minString != null) {
            values.add(new SimpleEntry<Object, ValueType>(
                    Converters.convert(minString, c),
                    ValueType.MIN));
            values.add(new SimpleEntry<Object, ValueType>(
                    Converters.convert(maxString, c),
                    ValueType.MAX));
        }

        return new SimpleEntry<ValuesTopology, SimpleEntry<Object, ValueType>[]>(
                topology, values.toArray(new SimpleEntry[values.size()]));
    }
}
