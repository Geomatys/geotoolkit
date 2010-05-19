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
package org.geotoolkit.sml.xml;


import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.sml.xml.v100.AbstractProcessType;
import org.geotoolkit.sml.xml.v100.Capabilities;
import org.geotoolkit.sml.xml.v100.Characteristics;
import org.geotoolkit.sml.xml.v100.Classification;
import org.geotoolkit.sml.xml.v100.ComponentType;
import org.geotoolkit.sml.xml.v100.Contact;
import org.geotoolkit.sml.xml.v100.DataSourceType;
import org.geotoolkit.sml.xml.v100.Documentation;
import org.geotoolkit.sml.xml.v100.History;
import org.geotoolkit.sml.xml.v100.Identification;
import org.geotoolkit.sml.xml.v100.Keywords;
import org.geotoolkit.sml.xml.v100.LegalConstraint;
import org.geotoolkit.sml.xml.v100.Member;
import org.geotoolkit.sml.xml.v100.ProcessChainType;
import org.geotoolkit.sml.xml.v100.ProcessModelType;
import org.geotoolkit.sml.xml.v100.SystemType;
import org.geotoolkit.swe.xml.Position;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.swe.xml.SimpleDataRecord;

/**
 * An object factory allowing to create SensorML object from different version.
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class SmlFactory {

    /**
     * The current SensorML version of the factory.
     */
    private String version;

    /**
     * build a new factory to build SensorML object from the specified version.
     *
     * @param version The sensorML version.
     */
    public SmlFactory(String version) {
        this.version = version;
    }

    /**
     * Build a IoComponent in the factory version.
     *
     * @param name
     * @param quantity
     * @return
     */
    public IoComponent createIoComponent(String name, Quantity quantity) {

        if ("1.0.0".equals(version)) {
            if (quantity != null && !(quantity instanceof org.geotoolkit.swe.xml.v100.QuantityType)) {
                throw new IllegalArgumentException("Unexpected SWE version for quantity object.");
            }
            return new org.geotoolkit.sml.xml.v100.IoComponentPropertyType(name,
                                                                (org.geotoolkit.swe.xml.v100.QuantityType)quantity);

        } else if ("1.0.1".equals(version)) {
            if (quantity != null && !(quantity instanceof org.geotoolkit.swe.xml.v101.QuantityType)) {
                throw new IllegalArgumentException("Unexpected SWE version for quantity object.");
            }
            return new org.geotoolkit.sml.xml.v101.IoComponentPropertyType(name,
                                                                (org.geotoolkit.swe.xml.v101.QuantityType)quantity);
        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    /**
     * Build a IoComponent in the factory version.
     *
     * @param name
     * @param quantity
     * @return
     */
    public IoComponent createIoComponent(String name, SimpleDataRecord record) {

        if ("1.0.0".equals(version)) {
            if (record != null && !(record instanceof org.geotoolkit.swe.xml.v100.SimpleDataRecordType)) {
                throw new IllegalArgumentException("Unexpected SWE version for record object.");
            }
            return new org.geotoolkit.sml.xml.v100.IoComponentPropertyType(name,
                                                                (org.geotoolkit.swe.xml.v100.SimpleDataRecordType)record);

        } else if ("1.0.1".equals(version)) {
            if (record != null && !(record instanceof org.geotoolkit.swe.xml.v101.SimpleDataRecordEntry)) {
                throw new IllegalArgumentException("Unexpected SWE version for record object.");
            }
            return new org.geotoolkit.sml.xml.v101.IoComponentPropertyType(name,
                                                                (org.geotoolkit.swe.xml.v101.SimpleDataRecordEntry)record);
        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    /**
     * Build a Inputs in the factory version.
     *
     * @param name
     * @param quantity
     * @return
     */
    public AbstractInputs createInputs(List<? extends IoComponent> inputList) {

        if ("1.0.0".equals(version)) {
            if (inputList == null) {
                throw new IllegalArgumentException("Unexpected SWE version for inputList object.");
            }
            return new org.geotoolkit.sml.xml.v100.Inputs((List<org.geotoolkit.sml.xml.v100.IoComponentPropertyType>)inputList);

        } else if ("1.0.1".equals(version)) {
            if (inputList == null) {
                throw new IllegalArgumentException("Unexpected SWE version for imputList object.");
            }
            return new org.geotoolkit.sml.xml.v101.Inputs((List<org.geotoolkit.sml.xml.v101.IoComponentPropertyType>)inputList);
        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    /**
     * Build a Inputs in the factory version.
     *
     * @param name
     * @param quantity
     * @return
     */
    public AbstractOutputs createOutputs(List<? extends IoComponent> outputList) {

        if ("1.0.0".equals(version)) {
            if (outputList == null) {
                throw new IllegalArgumentException("Unexpected SWE version for outputList object.");
            }
            return new org.geotoolkit.sml.xml.v100.Outputs((List<org.geotoolkit.sml.xml.v100.IoComponentPropertyType>)outputList);

        } else if ("1.0.1".equals(version)) {
            if (outputList == null) {
                throw new IllegalArgumentException("Unexpected SWE version for outputList object.");
            }
            return new org.geotoolkit.sml.xml.v101.Outputs((List<org.geotoolkit.sml.xml.v101.IoComponentPropertyType>)outputList);
        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    /**
     * Build a Inputs in the factory version.
     *
     * @param name
     * @param quantity
     * @return
     */
    public AbstractPosition createPosition(String name, Position position) {

        if ("1.0.0".equals(version)) {
            if (position != null && !(position instanceof org.geotoolkit.swe.xml.v100.PositionType)) {
                throw new IllegalArgumentException("Unexpected SWE version for position object.");
            }
            return new org.geotoolkit.sml.xml.v100.Position(name, (org.geotoolkit.swe.xml.v100.PositionType) position);

        } else if ("1.0.1".equals(version)) {
            if (position != null && !(position instanceof org.geotoolkit.swe.xml.v101.PositionType)) {
                throw new IllegalArgumentException("Unexpected SWE version for position object.");
            }
            return new org.geotoolkit.sml.xml.v101.Position(name, (org.geotoolkit.swe.xml.v101.PositionType) position);
        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    /**
     * Build a Inputs in the factory version.
     *
     * @param name
     * @param quantity
     * @return
     */
    public AbstractPositions createPositions(String id , List<? extends AbstractPosition> positions) {

        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sml.xml.v100.Positions(id, (List<org.geotoolkit.sml.xml.v100.Position>) positions);

        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.sml.xml.v101.Positions(id, (List<org.geotoolkit.sml.xml.v101.Position>) positions);

        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    /**
     * Build a ValidTime in the factory version.
     *
     */
    public AbstractValidTime createValidTime(String begin, String end) {

        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sml.xml.v100.ValidTime(begin, end);

        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.sml.xml.v101.ValidTime(begin, end);

        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    /**
     * Build a ValidTime in the factory version.
     *
     */
    public ComponentProperty createComponentProperty(String href) {

        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sml.xml.v100.ComponentPropertyType(href);

        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.sml.xml.v101.ComponentPropertyType(href);

        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    /**
     * Build a Inputs in the factory version.
     *
     * @param name
     * @param quantity
     * @return
     */
    public AbstractComponents createComponents(List<? extends ComponentProperty> components) {

        if ("1.0.0".equals(version)) {
            return new org.geotoolkit.sml.xml.v100.Components((List<org.geotoolkit.sml.xml.v100.ComponentPropertyType>) components);

        } else if ("1.0.1".equals(version)) {
            return new org.geotoolkit.sml.xml.v101.Components((List<org.geotoolkit.sml.xml.v101.ComponentPropertyType>) components);

        } else {
            throw new IllegalArgumentException("Unexpected SML version:" + version);
        }
    }

    public static org.geotoolkit.sml.xml.v101.SensorML convertTo101(org.geotoolkit.sml.xml.v100.SensorML sensor) {
        List<org.geotoolkit.sml.xml.v101.SensorML.Member> newMembers = new ArrayList<org.geotoolkit.sml.xml.v101.SensorML.Member>();

        for (Member oldMember : sensor.getMember()) {
            final org.geotoolkit.sml.xml.v101.AbstractProcessType newProcess;

            if (oldMember.getRealProcess() instanceof System) {
                newProcess = new org.geotoolkit.sml.xml.v101.SystemType();

            } else if (oldMember.getRealProcess() instanceof Component) {
                newProcess = new org.geotoolkit.sml.xml.v101.ComponentType();

            } else if (oldMember.getRealProcess() instanceof AbstractDataSource) {
                newProcess = new org.geotoolkit.sml.xml.v101.DataSourceType();
                
            } else if (oldMember.getRealProcess() instanceof AbstractProcessModel) {
                newProcess = new org.geotoolkit.sml.xml.v101.ProcessModelType();

            } else if (oldMember.getRealProcess() instanceof ComponentArray) {
                newProcess = new org.geotoolkit.sml.xml.v101.ComponentArrayType();

            } else {
                throw new IllegalArgumentException("Other sensor type than system, component, processModel, processChain, componentArray or datasource are not yet convertible");
            }

            AbstractProcessType oldProcess = (AbstractProcessType) oldMember.getRealProcess();

            // id
            newProcess.setId(oldProcess.getId());

            // name
            newProcess.setName(oldProcess.getName());

            // srsName
            newProcess.setSrsName(oldProcess.getSrsName());

            // description
            newProcess.setDescription(oldProcess.getDescription());

            //boundedBy
            newProcess.setBoundedBy(oldProcess.getBoundedBy());

            //capabilities
            List<org.geotoolkit.sml.xml.v101.Capabilities> newCapabilities = new ArrayList<org.geotoolkit.sml.xml.v101.Capabilities>();
            for (Capabilities oldCapa : oldProcess.getCapabilities()) {
                newCapabilities.add(new org.geotoolkit.sml.xml.v101.Capabilities(oldCapa));
            }
            newProcess.setCapabilities(newCapabilities);

            // characteristics
            List<org.geotoolkit.sml.xml.v101.Characteristics> newCharacteristics = new ArrayList<org.geotoolkit.sml.xml.v101.Characteristics>();
            for (Characteristics oldChar : oldProcess.getCharacteristics()) {
                newCharacteristics.add(new org.geotoolkit.sml.xml.v101.Characteristics(oldChar));
            }
            newProcess.setCharacteristics(newCharacteristics);

            // Classification
            List<org.geotoolkit.sml.xml.v101.Classification> newClassification = new ArrayList<org.geotoolkit.sml.xml.v101.Classification>();
            for (Classification oldClass : oldProcess.getClassification()) {
                newClassification.add(new org.geotoolkit.sml.xml.v101.Classification(oldClass));
            }
            newProcess.setClassification(newClassification);

            // Contact
            List<org.geotoolkit.sml.xml.v101.Contact> newContact = new ArrayList<org.geotoolkit.sml.xml.v101.Contact>();
            for (Contact oldContact : oldProcess.getContact()) {
                newContact.add(new org.geotoolkit.sml.xml.v101.Contact(oldContact));
            }
            newProcess.setContact(newContact);

            // Contact
            List<org.geotoolkit.sml.xml.v101.Documentation> newDocumentation = new ArrayList<org.geotoolkit.sml.xml.v101.Documentation>();
            for (Documentation oldDoc : oldProcess.getDocumentation()) {
                newDocumentation.add(new org.geotoolkit.sml.xml.v101.Documentation(oldDoc));
            }
            newProcess.setDocumentation(newDocumentation);

            // History
            List<org.geotoolkit.sml.xml.v101.History> newHistory = new ArrayList<org.geotoolkit.sml.xml.v101.History>();
            for (History oldhist : oldProcess.getHistory()) {
                newHistory.add(new org.geotoolkit.sml.xml.v101.History(oldhist));
            }
            newProcess.setHistory(newHistory);

            // Identification
            List<org.geotoolkit.sml.xml.v101.Identification> newIdentification = new ArrayList<org.geotoolkit.sml.xml.v101.Identification>();
            for (Identification oldIdent : oldProcess.getIdentification()) {
                newIdentification.add(new org.geotoolkit.sml.xml.v101.Identification(oldIdent));
            }
            newProcess.setIdentification(newIdentification);


            // keywords
            List<org.geotoolkit.sml.xml.v101.Keywords> newKeywords = new ArrayList<org.geotoolkit.sml.xml.v101.Keywords>();
            for (Keywords oldKeyw : oldProcess.getKeywords()) {
                newKeywords.add(new org.geotoolkit.sml.xml.v101.Keywords(oldKeyw));
            }
            newProcess.setKeywords(newKeywords);

            // legal constraint
            List<org.geotoolkit.sml.xml.v101.LegalConstraint> newLegalConstraints = new ArrayList<org.geotoolkit.sml.xml.v101.LegalConstraint>();
            for (LegalConstraint oldcons : oldProcess.getLegalConstraint()) {
                newLegalConstraints.add(new org.geotoolkit.sml.xml.v101.LegalConstraint(oldcons));
            }
            newProcess.setLegalConstraint(newLegalConstraints);

            // security constraint
            if (oldProcess.getSecurityConstraint() != null) {
                newProcess.setSecurityConstraint(new org.geotoolkit.sml.xml.v101.SecurityConstraint(oldProcess.getSecurityConstraint()));
            }

            // validTime
            if (oldProcess.getValidTime() != null) {
                newProcess.setValidTime(oldProcess.getValidTime());
            }

            if (oldProcess instanceof AbstractComponent) {
                AbstractComponent newAbsComponent = (AbstractComponent) newProcess;
                AbstractComponent oldAbsComponent = (AbstractComponent) oldProcess;

                //Inputs
                if (oldAbsComponent.getInputs() != null) {
                    newAbsComponent.setInputs(oldAbsComponent.getInputs());
                }

                // outputs
                if (oldAbsComponent.getOutputs() != null) {
                    newAbsComponent.setOutputs(oldAbsComponent.getOutputs());
                }

                // parameters
                if (oldAbsComponent.getParameters() != null) {
                    newAbsComponent.setParameters(oldAbsComponent.getParameters());
                }
            }

            if (oldProcess instanceof AbstractDerivableComponent) {
                org.geotoolkit.sml.xml.v101.AbstractDerivableComponentType newDerComponent =  (org.geotoolkit.sml.xml.v101.AbstractDerivableComponentType) newProcess;
                AbstractDerivableComponent oldDerComponent = (AbstractDerivableComponent) oldProcess;

                // Position
                if (oldDerComponent.getPosition() != null) {
                    newDerComponent.setPosition(oldDerComponent.getPosition());
                }

                if (oldDerComponent.getSMLLocation() != null) {
                    newDerComponent.setSMLLocation(oldDerComponent.getSMLLocation());
                }

                if (oldDerComponent.getInterfaces() != null) {
                    newDerComponent.setInterfaces(new org.geotoolkit.sml.xml.v101.Interfaces(oldDerComponent.getInterfaces()));
                }

                if (oldDerComponent.getSpatialReferenceFrame() != null) {
                    newDerComponent.setSpatialReferenceFrame(new org.geotoolkit.sml.xml.v101.SpatialReferenceFrame(oldDerComponent.getSpatialReferenceFrame()));
                }

                if (oldDerComponent.getTemporalReferenceFrame() != null) {
                    newDerComponent.setTemporalReferenceFrame(new org.geotoolkit.sml.xml.v101.TemporalReferenceFrame(oldDerComponent.getTemporalReferenceFrame()));
                }

                if (oldDerComponent.getTimePosition() != null) {
                    newDerComponent.setTimePosition(new org.geotoolkit.sml.xml.v101.TimePosition(oldDerComponent.getTimePosition()));
                }
            }

            if (oldProcess instanceof AbstractPureProcess) {
                org.geotoolkit.sml.xml.v101.AbstractPureProcessType newAbsPuProc = (org.geotoolkit.sml.xml.v101.AbstractPureProcessType) newProcess;
                AbstractPureProcess oldAbsPuProc = (AbstractPureProcess) oldProcess;

                //Inputs
                if (oldAbsPuProc.getInputs() != null) {
                    newAbsPuProc.setInputs(new org.geotoolkit.sml.xml.v101.Inputs(oldAbsPuProc.getInputs()));
                }

                // outputs
                if (oldAbsPuProc.getOutputs() != null) {
                    newAbsPuProc.setOutputs(new org.geotoolkit.sml.xml.v101.Outputs(oldAbsPuProc.getOutputs()));
                }

                // parameters
                if (oldAbsPuProc.getParameters() != null) {
                    newAbsPuProc.setParameters(new org.geotoolkit.sml.xml.v101.Parameters(oldAbsPuProc.getParameters()));
                }
            }
            
            if (oldMember.getRealProcess() instanceof System) {
                SystemType oldSystem = (SystemType) oldMember.getRealProcess();
                org.geotoolkit.sml.xml.v101.SystemType newSystem = (org.geotoolkit.sml.xml.v101.SystemType) newProcess;
                
                // components
                if (oldSystem.getComponents() != null) {
                    newSystem.setComponents(new org.geotoolkit.sml.xml.v101.Components(oldSystem.getComponents()));
                }

                // positions
                if (oldSystem.getPositions() != null) {
                    newSystem.setPositions(new org.geotoolkit.sml.xml.v101.Positions(oldSystem.getPositions()));
                }

                // connections
                if (oldSystem.getConnections() != null) {
                    newSystem.setConnections(new org.geotoolkit.sml.xml.v101.Connections(oldSystem.getConnections()));
                }

            } else if (oldMember.getRealProcess() instanceof Component) {
                ComponentType oldComponent = (ComponentType) oldMember.getRealProcess();
                org.geotoolkit.sml.xml.v101.ComponentType newCompo = (org.geotoolkit.sml.xml.v101.ComponentType) newProcess;

                // method
                if (oldComponent.getMethod() != null) {
                    newCompo.setMethod(new org.geotoolkit.sml.xml.v101.MethodPropertyType(oldComponent.getMethod()));
                }

            } else if (oldMember.getRealProcess() instanceof AbstractDataSource) {
                DataSourceType oldDataSource = (DataSourceType) oldMember.getRealProcess();
                org.geotoolkit.sml.xml.v101.DataSourceType newDataSource = (org.geotoolkit.sml.xml.v101.DataSourceType) newProcess;

                if (oldDataSource.getDataDefinition() != null) {
                    newDataSource.setDataDefinition(new org.geotoolkit.sml.xml.v101.DataDefinition(oldDataSource.getDataDefinition()));
                }
                if (oldDataSource.getValues() != null) {
                    newDataSource.setValues(new org.geotoolkit.sml.xml.v101.Values(oldDataSource.getValues()));
                }
                if (oldDataSource.getObservationReference() != null) {
                    newDataSource.setObservationReference(new org.geotoolkit.sml.xml.v101.ObservationReference(oldDataSource.getObservationReference()));
                }

            } else if (oldMember.getRealProcess() instanceof AbstractProcessModel) {
                ProcessModelType oldProcessModel = (ProcessModelType) oldMember.getRealProcess();
                org.geotoolkit.sml.xml.v101.ProcessModelType newProcessModel = (org.geotoolkit.sml.xml.v101.ProcessModelType) newProcess;

                if (oldProcessModel.getMethod() != null) {
                    newProcessModel.setMethod(new org.geotoolkit.sml.xml.v101.MethodPropertyType(oldProcessModel.getMethod()));
                }
                

            } else if (oldMember.getRealProcess() instanceof AbstractProcessChain) {
                ProcessChainType oldProcessChain = (ProcessChainType) oldMember.getRealProcess();
                org.geotoolkit.sml.xml.v101.ProcessChainType newProcessChain = (org.geotoolkit.sml.xml.v101.ProcessChainType) newProcess;

                 // components
                if (oldProcessChain.getComponents() != null) {
                    newProcessChain.setComponents(new org.geotoolkit.sml.xml.v101.Components(oldProcessChain.getComponents()));
                }

                // connections
                if (oldProcessChain.getConnections() != null) {
                    newProcessChain.setConnections(new org.geotoolkit.sml.xml.v101.Connections(oldProcessChain.getConnections()));
                }

            } else if (oldMember.getRealProcess() instanceof ComponentArray) {
                // nothing to do
            } else {
                throw new IllegalArgumentException("Other sensor type than system ,component, processModel, processChain, componentArray or datasource are not yet convertible");
            }
            newMembers.add(new org.geotoolkit.sml.xml.v101.SensorML.Member(newProcess));
        }
        org.geotoolkit.sml.xml.v101.SensorML result = new org.geotoolkit.sml.xml.v101.SensorML("1.0.1", newMembers);
        return result;
    }
}
