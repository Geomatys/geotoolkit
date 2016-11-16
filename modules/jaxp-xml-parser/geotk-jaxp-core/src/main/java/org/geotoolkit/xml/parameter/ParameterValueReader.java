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
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.parameter.Parameter;
import org.geotoolkit.parameter.ParameterGroup;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.xml.StaxStreamReader;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.xml.parameter.ParameterConstants.*;
import org.opengis.parameter.ParameterNotFoundException;

/**
 * <p>This class provides a GeneralParameterValue reading method.</p>
 *
 * @author Samuel Andrés
 * @module
 */
public class ParameterValueReader extends StaxStreamReader {

    private final GeneralParameterDescriptor rootDesc ;
    private final Deque<GeneralParameterDescriptor> stack = new ArrayDeque<GeneralParameterDescriptor>();

    /**
     * <p>Constructs the value reader with a descriptor reader.</p>
     *
     * @param descriptorReader
     */
    public ParameterValueReader(final ParameterDescriptorReader descriptorReader)
            throws IOException, XMLStreamException, ClassNotFoundException{

        descriptorReader.read();
        rootDesc = descriptorReader.getDescriptorsRoot();
        descriptorReader.dispose();
    }

    /**
     * <p>Construct the value reader with its associated descriptor.</p>
     *
     * @param descriptor
     */
    public ParameterValueReader(final GeneralParameterDescriptor descriptor){
        rootDesc = descriptor;
    }

    /**
     * <p>This method reads a parameter document whose root is
     * a GeneralParameterValue</p>
     *
     * @return
     */
    public GeneralParameterValue read() throws XMLStreamException {

        while (reader.hasNext()) {

            switch (reader.next()) {

                case XMLStreamConstants.START_ELEMENT:
                final String eName = reader.getLocalName();
                final String eUri = reader.getNamespaceURI();

                if (URI_PARAMETER.equals(eUri)) {
                    return this.readValue(eName);
                }
                    break;
                }
            }
        return null;
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

        GeneralParameterDescriptor desc = stack.peekFirst();
        desc = getDescriptor(desc, eName);
        stack.addFirst(desc); //push

        final GeneralParameterValue result;

        if(desc instanceof ParameterDescriptor){
            Class targetClass = ((ParameterDescriptor) desc).getValueClass();
            Object converted = null;
            
            //HACK for Path support
            // we don't use ObjectConverters to convert Path from a String because
            // there is an already existing converter that doesn't use protocol (URI scheme)
            if (Path.class.isAssignableFrom(targetClass)) {
                final String text = reader.getElementText();
                if (!text.isEmpty()) {
                    URI uri = URI.create(text);
                    if (uri.getScheme() != null) {
                        //may target a path on another file system
                        converted = Paths.get(uri);
                    } else {
                        //assume path is on current file system (relative of absolute)
                        converted = Paths.get(text);
                    }
                }
            } else if (targetClass.isArray()) {
                List<Object> converteds = new ArrayList<>();
                boucle:
                while (reader.hasNext()) {

                    switch (reader.next()) {
                        case XMLStreamConstants.START_ELEMENT:

                            converteds.add(ObjectConverters.convert(reader.getElementText(), targetClass.getComponentType()));
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            if (desc.getName().getCode().equals(reader.getLocalName())
                                    && URI_PARAMETER.contains(reader.getNamespaceURI())) {
                                break boucle;
                            }
                            break;
                    }

                }

                converted = Array.newInstance(targetClass.getComponentType(), converteds.size());
                for (int i = 0 ; i < converteds.size(); i++) {
                    Array.set(converted, i, converteds.get(i));
                }

            } else {
                final String text = reader.getElementText();
                if (!text.isEmpty()) {
                    converted = ObjectConverters.convert(text, targetClass);
                }
            }
            if (converted != null) {
                result = new Parameter((ParameterDescriptor) desc,converted);
            } else {
                result = null;
            }
            
        } else if(desc instanceof ParameterDescriptorGroup){
            result = this.readValueGroup();
        } else {
            result = null;
        }

        stack.removeFirst(); //pop
        return result;
    }

    /**
     * <p>This method reads a value group</p>
     *
     * @param desc
     * @return
     * @throws XMLStreamException
     */
    private ParameterValueGroup readValueGroup()
            throws XMLStreamException{

        final ParameterDescriptorGroup desc = (ParameterDescriptorGroup) stack.peekFirst();

        final List<GeneralParameterValue> values = new ArrayList<GeneralParameterValue>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final GeneralParameterValue value = this.readValue(reader.getLocalName());
                    if (value != null) {
                        values.add(value);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (desc.getName().getCode().equals(reader.getLocalName())
                            && URI_PARAMETER.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        //try to fill missing parameters
        for(GeneralParameterDescriptor candidate : desc.descriptors()){
            final int minOcc = candidate.getMinimumOccurs();
            if(minOcc == 0) continue;

            int count = 0;
            for(GeneralParameterValue val : values){
                if(val.getDescriptor().equals(candidate)){
                    count++;
                }
            }

            //create missing values
            for(;count<minOcc;count++){
                values.add(candidate.createValue());
            }
        }

        return new ParameterGroup(
                desc, values.toArray(new GeneralParameterValue[values.size()]));
        }

    private GeneralParameterDescriptor getDescriptor(GeneralParameterDescriptor desc,
            String name) throws XMLStreamException{

        if(desc == null){
            if(!rootDesc.getName().getCode().equals(name) &&
               !rootDesc.getName().getCode().equals(name.replace('_', ' '))){
                throw new XMLStreamException("Descriptor for name : "+name+" not found.");
            }
            return rootDesc;

        }else{
            if(!(desc instanceof ParameterDescriptorGroup)){
                throw new XMLStreamException("Was expecting a descriptor group for name : " + name);
            }

            ParameterDescriptorGroup pdg = (ParameterDescriptorGroup) desc;
            GeneralParameterDescriptor candidate = null;
            try{
                return pdg.descriptor(name);
            }catch(ParameterNotFoundException ex){
                //second try
                name = name.replace('_', ' ');
            }

            try{
                return pdg.descriptor(name);
            }catch(ParameterNotFoundException ex){
                throw new XMLStreamException("Descriptor for name : "+name+" not found.");
            }

        }

    }

}
