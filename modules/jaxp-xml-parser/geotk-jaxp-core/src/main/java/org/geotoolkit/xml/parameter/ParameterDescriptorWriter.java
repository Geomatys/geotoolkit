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

import javax.xml.stream.XMLStreamException;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import static org.geotoolkit.xml.parameter.ParameterConstants.*;
import org.opengis.util.InternationalString;

/**
 * <p>This class provides a GeneralParameterValue writing method.</p>
 * 
 * @author Samuel Andrés
 * @module pending
 */
public class ParameterDescriptorWriter extends StaxStreamWriter {

    private static final String ANY_STRING = "[0-9a-zA-Z]*";

    /**
     * <p>Writes an XML Schema mapping parameterDescriptor.</p>
     *
     * @param generalParameterDescriptor
     */
    public void write(final GeneralParameterDescriptor generalParameterDescriptor) throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.setDefaultNamespace(URI_XSD);
        writer.writeStartElement(URI_XSD, TAG_XSD_SCHEMA);
        writer.writeNamespace(PREFIX_PARAMETER, URI_PARAMETER);
        writer.writeAttribute("targetNamespace", URI_PARAMETER);
        writer.writeAttribute("elementFormDefault", "qualified");
        this.writeGeneralParameterDescriptor(generalParameterDescriptor);
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
    }

    /**
     * <p>This method writes a ParameterDescriptor.</p>
     *
     * @param descriptor
     * @throws XMLStreamException
     */
    private void writeParameterDescriptor(final ParameterDescriptor descriptor)
            throws XMLStreamException {

        if (descriptor.getDefaultValue() != null) {
            writer.writeAttribute(ATT_XSD_DEFAULT, descriptor.getDefaultValue().toString());
        }

        /*
         * ParameterDescriptor (simple descriptor) is mapped as a simple type
         * with specified base type and restrictions.
         */
        writer.writeStartElement(URI_XSD, TAG_XSD_SIMPLE_TYPE);

        /*
         * Annotations contains
         * - Remarks (description tag)
         * - other values used by descriptor reader, as Java mapping class canonical name.
         */
        this.writeAnnotations(descriptor);
        writer.writeStartElement(URI_XSD, TAG_XSD_RESTRICTION);

        /*
         * Writting base of restriction XSD type. Default : xsd:string
         */
        if (Integer.class.equals(descriptor.getValueClass())) {
            writer.writeAttribute(ATT_XSD_BASE, TYPE_XSD_INT);
        } else if (Long.class.equals(descriptor.getValueClass())) {
            writer.writeAttribute(ATT_XSD_BASE, TYPE_XSD_LONG);
        } else if (Float.class.equals(descriptor.getValueClass())) {
            writer.writeAttribute(ATT_XSD_BASE, TYPE_XSD_FLOAT);
        } else if (Double.class.equals(descriptor.getValueClass())) {
            writer.writeAttribute(ATT_XSD_BASE, TYPE_XSD_DOUBLE);
        } else if (Boolean.class.equals(descriptor.getValueClass())) {
            writer.writeAttribute(ATT_XSD_BASE, TYPE_XSD_BOOLEAN);
        } else {
            writer.writeAttribute(ATT_XSD_BASE, TYPE_XSD_STRING);
        }

        /*
         * -------------- WRITTING RESTRICTIONS --------------------------------
         */

        /*
         * Discrete topology : values enumeration (numbers, Strings, Booleans)
         */
        if (descriptor.getValidValues() != null) {
            for (Object value : descriptor.getValidValues()) {
                writer.writeStartElement(URI_XSD, TAG_XSD_ENUMERATION);
                writer.writeAttribute(ATT_XSD_VALUE, value.toString());
                writer.writeEndElement();
            }
        }
        /*
         * Interval topology
         */
        else {

            // CASE of String
            if (String.class.equals(descriptor.getValueClass())) {
                String max = (String) descriptor.getMaximumValue();
                String min = (String) descriptor.getMinimumValue();
                if (min != null && max != null) {

                    int idx = this.writeIntervalPatterns(min, max);
                    this.writeBottomPatterns(idx, min);
                    this.writeTopPatterns(idx, max);

                } else if (min != null) {
                    this.writeBottomPatterns(-1, min);
                } else if (max != null) {
                    this.writeTopPatterns(-1, max);
                }
            } // CASE of Boolean interval
            else if (Boolean.class.equals(descriptor.getValueClass())) {
                if (descriptor.getMinimumValue() != null) {
                    writer.writeStartElement(URI_XSD, TAG_XSD_PATTERN);
                    writer.writeAttribute(ATT_XSD_VALUE, descriptor.getMinimumValue().toString());
                    writer.writeEndElement();
                }
                if (descriptor.getMaximumValue() != null) {
                    writer.writeStartElement(URI_XSD, TAG_XSD_PATTERN);
                    writer.writeAttribute(ATT_XSD_VALUE, descriptor.getMaximumValue().toString());
                    writer.writeEndElement();
                }
            } // CASE of numeric values
            else {
                if (descriptor.getMinimumValue() != null) {
                    writer.writeStartElement(URI_XSD, TAG_XSD_MIN_INCLUSIVE);
                    writer.writeAttribute(ATT_XSD_VALUE, descriptor.getMinimumValue().toString());
                    writer.writeEndElement();
                }
                if (descriptor.getMaximumValue() != null) {
                    writer.writeStartElement(URI_XSD, TAG_XSD_MAX_INCLUSIVE);
                    writer.writeAttribute(ATT_XSD_VALUE, descriptor.getMaximumValue().toString());
                    writer.writeEndElement();
                }
            }
        }

        /*
         * --------------- END OF WRITTING RESTRICTIONS ------------------------
         */

        writer.writeEndElement();
        writer.writeEndElement();
    }

    /**
     * <p>This method writes generic pattern between two Strings.</p>
     *
     * @param min
     * @param max
     * @return index of first different character
     * @throws XMLStreamException
     */
    private int writeIntervalPatterns(final String min, final String max)
            throws XMLStreamException {

        final StringBuilder sb = new StringBuilder();
        final int l = Math.min(min.length(), max.length());
        int idx = 0;

        for (int i = 0; i < l; i++) {
            idx = i;

            // When characters are different
            if (min.charAt(i) < max.charAt(i)
                    && Character.isLetterOrDigit((min.charAt(i) + 1))
                    && Character.isLetterOrDigit((max.charAt(i) - 1))) {

                sb.append('[');
                sb.append((char) (min.charAt(i) + 1));
                sb.append('-');
                sb.append((char) (max.charAt(i) - 1));
                sb.append(']');
                break;
            } // While characters are equal
            else if (min.charAt(i) == max.charAt(i)) {
                sb.append(min.charAt(i));
            } // Last character
            else if (i == l - 1) {
            } else {
                throw new UnsupportedOperationException("Maximal String value"
                        + "has to be greater than minimal value");
            }
        }

        writer.writeStartElement(URI_XSD, TAG_XSD_PATTERN);
        writer.writeAttribute(ATT_XSD_VALUE, sb.toString() + ANY_STRING);
        writer.writeEndElement();

        return idx;
    }

    /**
     * <p>This method writes patterns like maximum String</p>
     *
     * @param idx starting index
     * @param max maximum String
     * @throws XMLStreamException
     */
    private void writeTopPatterns(final int idx, final String max)
            throws XMLStreamException {

        for (int i = idx + 1; i < max.length() - 1; i++) {
            if (Character.isLetterOrDigit(max.charAt(i) - 1)) {
                StringBuilder sb2 = new StringBuilder(max.substring(0, i));
                sb2.append((char) (max.charAt(i) - 1));
                writer.writeStartElement(URI_XSD, TAG_XSD_PATTERN);
                writer.writeAttribute(ATT_XSD_VALUE, sb2.toString() + ANY_STRING);
                writer.writeEndElement();
            }
        }
        writer.writeStartElement(URI_XSD, TAG_XSD_PATTERN);
        writer.writeAttribute(ATT_XSD_VALUE, max);
        writer.writeEndElement();
    }

    /**
     * <p>This methods writes patterns like minimum String.</p>
     *
     * @param idx starting index
     * @param min minimum String
     * @throws XMLStreamException
     */
    private void writeBottomPatterns(final int idx, final String min)
            throws XMLStreamException {

        for (int i = idx + 1; i < min.length() - 1; i++) {
            if (Character.isLetterOrDigit(min.charAt(i) + 1)) {
                StringBuilder sb2 = new StringBuilder(min.substring(0, i));
                sb2.append((char) (min.charAt(i) + 1));
                writer.writeStartElement(URI_XSD, TAG_XSD_PATTERN);
                writer.writeAttribute(ATT_XSD_VALUE, sb2.toString() + ANY_STRING);
                writer.writeEndElement();
            }
        }
        writer.writeStartElement(URI_XSD, TAG_XSD_PATTERN);
        writer.writeAttribute(ATT_XSD_VALUE, min + ANY_STRING);
        writer.writeEndElement();
    }

    /**
     * <p>This method writes a ParameterDescriptorGroup</p>
     *
     * @param descriptor
     * @throws XMLStreamException
     */
    private void writeParameterDescriptorGroup(final ParameterDescriptorGroup descriptor)
            throws XMLStreamException {

        writer.writeStartElement(URI_XSD, TAG_XSD_COMPLEX_TYPE);
        writer.writeStartElement(URI_XSD, TAG_XSD_SEQUENCE);
        this.writeAnnotations(descriptor);

        for (GeneralParameterDescriptor d : descriptor.descriptors()) {
            this.writeGeneralParameterDescriptor(d);
        }

        writer.writeEndElement();
        writer.writeEndElement();
    }

    /**
     * <p>This method writes a general parameter descriptor</p>
     * 
     * @param parameterDescriptor
     * @throws XMLStreamException
     */
    private void writeGeneralParameterDescriptor(final GeneralParameterDescriptor parameterDescriptor)
            throws XMLStreamException {

        /*
         * Each descriptor is mapped as an element.
         */
        writer.writeStartElement(URI_XSD, TAG_XSD_ELEMENT);
        writer.writeAttribute(ATT_XSD_NAME,
                parameterDescriptor.getName().getCode().replace(' ', '_'));

        /*
         * Writting occurrences
         */
        if (parameterDescriptor.getMinimumOccurs() != 1) {
            writer.writeAttribute(ATT_XSD_MIN_OCCURS,
                    String.valueOf(parameterDescriptor.getMinimumOccurs()));
        }
        if (parameterDescriptor.getMaximumOccurs() != 1) {
            if (parameterDescriptor.getMaximumOccurs() == Integer.MAX_VALUE) {
                writer.writeAttribute(ATT_XSD_MAX_OCCURS, VAL_XSD_UNBOUNDED);
            } else {
                writer.writeAttribute(ATT_XSD_MAX_OCCURS,
                        String.valueOf(parameterDescriptor.getMaximumOccurs()));
            }
        }

        /*
         * ParameterDescriptor or ParameterDescriptorGroup ?
         */
        if (parameterDescriptor instanceof ParameterDescriptor) {
            this.writeParameterDescriptor((ParameterDescriptor) parameterDescriptor);
        } else if (parameterDescriptor instanceof ParameterDescriptorGroup) {
            this.writeParameterDescriptorGroup((ParameterDescriptorGroup) parameterDescriptor);
        }

        writer.writeEndElement();
    }

    /**
     * <p>Wtites annotations (remarks and app infos)</p>
     *
     * @param parameterDescriptor
     * @throws XMLStreamException
     */
    private void writeAnnotations(final GeneralParameterDescriptor parameterDescriptor)
            throws XMLStreamException {

        if (parameterDescriptor.getRemarks() != null
                || parameterDescriptor.getName().getCodeSpace() != null
                || parameterDescriptor.getName().getVersion() != null
                || parameterDescriptor instanceof ParameterDescriptor) {
            writer.writeStartElement(URI_XSD, TAG_XSD_ANNOTATION);
            // Java mapping class canonical name
            if (parameterDescriptor instanceof ParameterDescriptor) {
                this.writeAppInfo("valueClass",
                        ((ParameterDescriptor) parameterDescriptor).getValueClass().getCanonicalName());
            }
//            if(parameterDescriptor.getName().getCodeSpace() != null){
//                this.writeAppInfo("codeSpace",parameterDescriptor.getName().getCodeSpace());
//            }
//            if(parameterDescriptor.getName().getVersion() != null){
//                this.writeAppInfo("version",parameterDescriptor.getName().getVersion());
//            }
            // Remarks are written as documentation.
            if (parameterDescriptor.getRemarks() != null) {
                this.writeRemarks(parameterDescriptor.getRemarks());
            }
            writer.writeEndElement();
        }
    }

    /**
     * <p>This method writes remarks.</p>
     *
     * @param remarks
     * @throws XMLStreamException
     */
    private void writeRemarks(final InternationalString remarks)
            throws XMLStreamException {

        writer.writeStartElement(URI_XSD, TAG_XSD_DOCUMENTATION);
        writer.writeCharacters(remarks.toString());
        writer.writeEndElement();
    }

    /**
     * <p>writes app infos.</p>
     *
     * @param pseudoTag
     * @param info
     * @throws XMLStreamException
     */
    private void writeAppInfo(final String pseudoTag, final String info)
            throws XMLStreamException {

        writer.writeStartElement(URI_XSD, TAG_XSD_APP_INFO);
        writer.writeCharacters(pseudoTag + ":" + info);
        writer.writeEndElement();
    }
}
