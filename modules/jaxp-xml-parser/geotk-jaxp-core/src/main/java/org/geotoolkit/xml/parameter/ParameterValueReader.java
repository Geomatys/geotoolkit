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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.parameter.Parameter;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.util.Converters;
import org.geotoolkit.xml.StaxStreamReader;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.xml.parameter.ParameterConstants.*;

/**
 * <p>This class provides a GeneralParameterValue reading method.</p>
 *
 * @author Samuel Andrés
 * @module pending
 */
public class ParameterValueReader extends StaxStreamReader {

    private Map<String, GeneralParameterDescriptor> descriptors;

    /**
     * Private minimum constructor
     */
    private ParameterValueReader(){}

    /**
     * <p>Constructs the value reader with a descriptor reader.</p>
     *
     * @param descriptorReader
     */
    public ParameterValueReader(final ParameterDescriptorReader descriptorReader) 
            throws IOException, XMLStreamException, ClassNotFoundException{
        
        descriptorReader.read();
        this.descriptors = descriptorReader.getDescriptorsMap();
        descriptorReader.dispose();
    }

    /**
     * <p>Construct the value reader with its associated descriptor.</p>
     *
     * @param descriptor
     */
    public ParameterValueReader(final GeneralParameterDescriptor descriptor){
        this.descriptors = new HashMap<String, GeneralParameterDescriptor>();
        this.initDescriptors(descriptor);
    }

    /**
     * <p>Extracts descriptors.</p>
     *
     * @param descriptor
     */
    private void initDescriptors(final GeneralParameterDescriptor descriptor){
        this.descriptors.put(descriptor.getName().getCode(), descriptor);
        if(descriptor instanceof ParameterDescriptorGroup){
            for(GeneralParameterDescriptor d
                    : ((ParameterDescriptorGroup) descriptor).descriptors()){
                this.initDescriptors(d);
            }
        }
    }

    /**
     * <p>This method reads a parameter document whose root is
     * a GeneralParameterValue</p>
     *
     * @return
     */
    public GeneralParameterValue read() throws XMLStreamException {
        GeneralParameterValue root = null;

        while (reader.hasNext()) {

            switch (reader.next()) {

                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_PARAMETER.equals(eUri)) {
                        root = this.readValue(eName);
                    }
                    break;
            }
        }
        return root;
    }

    /**
     * <p>This method reads a general value.</p>
     *
     * @param eName
     * @return
     * @throws XMLStreamException
     */
    private GeneralParameterValue readValue(final String eName) 
            throws XMLStreamException{

        final GeneralParameterValue result;
        GeneralParameterDescriptor desc = this.descriptors.get(eName);

        if(desc == null){
            desc = this.descriptors.get(eName.replace('_', ' '));
        }

        if(desc == null){
            throw new NullPointerException("No descriptor found whose name code is "+eName);
        } else if(desc instanceof ParameterDescriptor){
            result = new Parameter(
                    (ParameterDescriptor) desc,
                    Converters.convert(
                    reader.getElementText(),((ParameterDescriptor) desc).getValueClass()));
        } else if(desc instanceof ParameterDescriptorGroup){
            result = this.readValueGroup((ParameterDescriptorGroup) desc);
        } else {
            result = null;
        }
        return result;
    }

    /**
     * <p>This method rads a value group</p>
     *
     * @param desc
     * @return
     * @throws XMLStreamException
     */
    private ParameterValueGroup readValueGroup(final ParameterDescriptorGroup desc)
            throws XMLStreamException{

        final List<GeneralParameterValue> values = new ArrayList<GeneralParameterValue>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    values.add(this.readValue(reader.getLocalName()));
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (desc.getName().getCode().equals(reader.getLocalName())
                            && URI_PARAMETER.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return  new ParameterGroup(
                desc, values.toArray(new GeneralParameterValue[values.size()]));
    }

}
