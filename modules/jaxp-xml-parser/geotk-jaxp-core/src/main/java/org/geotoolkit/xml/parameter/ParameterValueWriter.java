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

import java.lang.reflect.Array;
import javax.xml.stream.XMLStreamException;

import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

import java.nio.file.Path;

import static org.geotoolkit.xml.parameter.ParameterConstants.*;

/**
 * <p>This class provides a GeneralParameterValue writing method.</p>
 * 
 * @author Samuel Andrés
 * @module pending
 */
public class ParameterValueWriter extends StaxStreamWriter {

    /**
     * <p>This method writes an XML document containing a GeneralParameterValue</p>
     * Do not write the start/end, use when encapsulated in another writer.
     * 
     * @param generalParameterValue
     */
    public void writeForInsertion(final GeneralParameterValue generalParameterValue) throws XMLStreamException {
        this.writeGeneralParameterValue(generalParameterValue);
    }
    
    /**
     * <p>This method writes an XML document containing a GeneralParameterValue</p>
     *
     * @param generalParameterValue
     */
    public void write(final GeneralParameterValue generalParameterValue) throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.setDefaultNamespace(URI_PARAMETER);
        this.writeForInsertion(generalParameterValue);
        writer.writeEndDocument();
        writer.flush();
    }
    
    /**
     * <p>This method writes a GenaralParameterValue.</p>
     *
     * @param generalParameterValue
     * @throws XMLStreamException
     */
    private void writeGeneralParameterValue(
            final GeneralParameterValue generalParameterValue)
            throws XMLStreamException {

        writer.writeStartElement(URI_PARAMETER,
                generalParameterValue.getDescriptor().getName().getCode().replace(' ', '_'));

        if (generalParameterValue instanceof ParameterValueGroup) {
            this.writeParameterValueGroup((ParameterValueGroup) generalParameterValue);
        } else {
            ParameterValue param = (ParameterValue) generalParameterValue;
            final Class valueClass = param.getDescriptor().getValueClass();
            if (valueClass.isArray()) {
                final Object values = param.getValue();
                final int size = Array.getLength(values);
                for (int i = 0; i < size; i++) {
                    writer.writeStartElement(URI_PARAMETER, ENTRY_PARAMETER);
                    this.writeParameterValue(Array.get(values, i));
                    writer.writeEndElement();
                }
                
            } else {
                this.writeParameterValue(param.getValue());
            }
        }
        writer.writeEndElement();
    }

    /**
     * <p>This method writes a ParameterValue.</p>
     *
     * @param parameter
     * @throws XMLStreamException
     */
    private void writeParameterValue(final Object value)
            throws XMLStreamException {
        if(value != null){
            //HACK for Path support
            // we don't use ObjectConverters to convert Path into a String because
            // there is an already existing converter that doesn't return full path
            // with protocol (URI scheme)
            if (value instanceof Path) {
                Path path = (Path) value;
                writer.writeCharacters(path.toAbsolutePath().toUri().toString());
            } else {
                writer.writeCharacters(ObjectConverters.convert(value, String.class));
            }
        }
    }

    /**
     * <p>This method writes a ParameterValueGroup.</p>
     *
     * @param parameterGroup
     * @throws XMLStreamException
     */
    private void writeParameterValueGroup(final ParameterValueGroup parameterGroup)
            throws XMLStreamException {
        for (GeneralParameterValue value : parameterGroup.values()) {
            this.writeGeneralParameterValue(value);
        }
    }
}