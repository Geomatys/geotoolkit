/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sml.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.sensorml._2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AbstractSettings_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "AbstractSettings");
    private final static QName _AbstractProcess_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "AbstractProcess");
    private final static QName _InputList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "InputList");
    private final static QName _ConnectionList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "ConnectionList");
    private final static QName _SimpleProcess_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "SimpleProcess");
    private final static QName _EventList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "EventList");
    private final static QName _Event_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "Event");
    private final static QName _AbstractModes_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "AbstractModes");
    private final static QName _ComponentList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "ComponentList");
    private final static QName _FeatureList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "FeatureList");
    private final static QName _Mode_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "Mode");
    private final static QName _ModeChoice_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "ModeChoice");
    private final static QName _AbstractMetadataList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "AbstractMetadataList");
    private final static QName _OutputList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "OutputList");
    private final static QName _ProcessMethod_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "ProcessMethod");
    private final static QName _Link_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "Link");
    private final static QName _DescribedObject_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "DescribedObject");
    private final static QName _AbstractPhysicalProcess_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "AbstractPhysicalProcess");
    private final static QName _CharacteristicList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "CharacteristicList");
    private final static QName _SpatialFrame_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "SpatialFrame");
    private final static QName _KeywordList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "KeywordList");
    private final static QName _TemporalFrame_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "TemporalFrame");
    private final static QName _PhysicalComponent_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "PhysicalComponent");
    private final static QName _AggregateProcess_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "AggregateProcess");
    private final static QName _ObservableProperty_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "ObservableProperty");
    private final static QName _DataInterface_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "DataInterface");
    private final static QName _ClassifierList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "ClassifierList");
    private final static QName _ContactList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "ContactList");
    private final static QName _DocumentList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "DocumentList");
    private final static QName _AbstractAlgorithm_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "AbstractAlgorithm");
    private final static QName _Term_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "Term");
    private final static QName _IdentifierList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "IdentifierList");
    private final static QName _Settings_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "Settings");
    private final static QName _CapabilityList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "CapabilityList");
    private final static QName _PhysicalSystem_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "PhysicalSystem");
    private final static QName _ParameterList_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "ParameterList");
    private final static QName _IdentifierListTypeIdentifier_QNAME = new QName("http://www.opengis.net/sensorml/2.0", "identifier");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.sensorml._2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArraySettingPropertyType }
     * 
     */
    public ArraySettingPropertyType createArraySettingPropertyType() {
        return new ArraySettingPropertyType();
    }

    /**
     * Create an instance of {@link ArraySettingPropertyType.ArrayValues }
     * 
     */
    public ArraySettingPropertyType.ArrayValues createArraySettingPropertyTypeArrayValues() {
        return new ArraySettingPropertyType.ArrayValues();
    }

    /**
     * Create an instance of {@link ClassifierListType }
     * 
     */
    public ClassifierListType createClassifierListType() {
        return new ClassifierListType();
    }

    /**
     * Create an instance of {@link ParameterListType }
     * 
     */
    public ParameterListType createParameterListType() {
        return new ParameterListType();
    }

    /**
     * Create an instance of {@link CapabilityListType }
     * 
     */
    public CapabilityListType createCapabilityListType() {
        return new CapabilityListType();
    }

    /**
     * Create an instance of {@link ConnectionListType }
     * 
     */
    public ConnectionListType createConnectionListType() {
        return new ConnectionListType();
    }

    /**
     * Create an instance of {@link CharacteristicListType }
     * 
     */
    public CharacteristicListType createCharacteristicListType() {
        return new CharacteristicListType();
    }

    /**
     * Create an instance of {@link EventType }
     * 
     */
    public EventType createEventType() {
        return new EventType();
    }

    /**
     * Create an instance of {@link SpatialFrameType }
     * 
     */
    public SpatialFrameType createSpatialFrameType() {
        return new SpatialFrameType();
    }

    /**
     * Create an instance of {@link IdentifierListType }
     * 
     */
    public IdentifierListType createIdentifierListType() {
        return new IdentifierListType();
    }

    /**
     * Create an instance of {@link OutputListType }
     * 
     */
    public OutputListType createOutputListType() {
        return new OutputListType();
    }

    /**
     * Create an instance of {@link ProcessMethodType }
     * 
     */
    public ProcessMethodType createProcessMethodType() {
        return new ProcessMethodType();
    }

    /**
     * Create an instance of {@link ComponentListType }
     * 
     */
    public ComponentListType createComponentListType() {
        return new ComponentListType();
    }

    /**
     * Create an instance of {@link InputListType }
     * 
     */
    public InputListType createInputListType() {
        return new InputListType();
    }

    /**
     * Create an instance of {@link ObservablePropertyType }
     * 
     */
    public ObservablePropertyType createObservablePropertyType() {
        return new ObservablePropertyType();
    }

    /**
     * Create an instance of {@link FeatureListType }
     * 
     */
    public FeatureListType createFeatureListType() {
        return new FeatureListType();
    }

    /**
     * Create an instance of {@link AbstractMetadataListType }
     * 
     */
    public AbstractMetadataListType createAbstractMetadataListType() {
        return new AbstractMetadataListType();
    }

    /**
     * Create an instance of {@link AggregateProcessType }
     * 
     */
    public AggregateProcessType createAggregateProcessType() {
        return new AggregateProcessType();
    }

    /**
     * Create an instance of {@link DocumentListType }
     * 
     */
    public DocumentListType createDocumentListType() {
        return new DocumentListType();
    }

    /**
     * Create an instance of {@link AbstractSettingsType }
     * 
     */
    public AbstractSettingsType createAbstractSettingsType() {
        return new AbstractSettingsType();
    }

    /**
     * Create an instance of {@link KeywordListType }
     * 
     */
    public KeywordListType createKeywordListType() {
        return new KeywordListType();
    }

    /**
     * Create an instance of {@link AbstractModesType }
     * 
     */
    public AbstractModesType createAbstractModesType() {
        return new AbstractModesType();
    }

    /**
     * Create an instance of {@link PhysicalComponentType }
     * 
     */
    public PhysicalComponentType createPhysicalComponentType() {
        return new PhysicalComponentType();
    }

    /**
     * Create an instance of {@link TemporalFrameType }
     * 
     */
    public TemporalFrameType createTemporalFrameType() {
        return new TemporalFrameType();
    }

    /**
     * Create an instance of {@link SimpleProcessType }
     * 
     */
    public SimpleProcessType createSimpleProcessType() {
        return new SimpleProcessType();
    }

    /**
     * Create an instance of {@link SettingsType }
     * 
     */
    public SettingsType createSettingsType() {
        return new SettingsType();
    }

    /**
     * Create an instance of {@link PhysicalSystemType }
     * 
     */
    public PhysicalSystemType createPhysicalSystemType() {
        return new PhysicalSystemType();
    }

    /**
     * Create an instance of {@link DataInterfaceType }
     * 
     */
    public DataInterfaceType createDataInterfaceType() {
        return new DataInterfaceType();
    }

    /**
     * Create an instance of {@link ModeType }
     * 
     */
    public ModeType createModeType() {
        return new ModeType();
    }

    /**
     * Create an instance of {@link TermType }
     * 
     */
    public TermType createTermType() {
        return new TermType();
    }

    /**
     * Create an instance of {@link ContactListType }
     * 
     */
    public ContactListType createContactListType() {
        return new ContactListType();
    }

    /**
     * Create an instance of {@link LinkType }
     * 
     */
    public LinkType createLinkType() {
        return new LinkType();
    }

    /**
     * Create an instance of {@link EventListType }
     * 
     */
    public EventListType createEventListType() {
        return new EventListType();
    }

    /**
     * Create an instance of {@link ModeChoiceType }
     * 
     */
    public ModeChoiceType createModeChoiceType() {
        return new ModeChoiceType();
    }

    /**
     * Create an instance of {@link ClassifierListPropertyType }
     * 
     */
    public ClassifierListPropertyType createClassifierListPropertyType() {
        return new ClassifierListPropertyType();
    }

    /**
     * Create an instance of {@link AbstractAlgorithmPropertyType }
     * 
     */
    public AbstractAlgorithmPropertyType createAbstractAlgorithmPropertyType() {
        return new AbstractAlgorithmPropertyType();
    }

    /**
     * Create an instance of {@link InputListPropertyType }
     * 
     */
    public InputListPropertyType createInputListPropertyType() {
        return new InputListPropertyType();
    }

    /**
     * Create an instance of {@link ConnectionListPropertyType }
     * 
     */
    public ConnectionListPropertyType createConnectionListPropertyType() {
        return new ConnectionListPropertyType();
    }

    /**
     * Create an instance of {@link TemporalFramePropertyType }
     * 
     */
    public TemporalFramePropertyType createTemporalFramePropertyType() {
        return new TemporalFramePropertyType();
    }

    /**
     * Create an instance of {@link ParameterListPropertyType }
     * 
     */
    public ParameterListPropertyType createParameterListPropertyType() {
        return new ParameterListPropertyType();
    }

    /**
     * Create an instance of {@link FeatureListPropertyType }
     * 
     */
    public FeatureListPropertyType createFeatureListPropertyType() {
        return new FeatureListPropertyType();
    }

    /**
     * Create an instance of {@link PhysicalComponentPropertyType }
     * 
     */
    public PhysicalComponentPropertyType createPhysicalComponentPropertyType() {
        return new PhysicalComponentPropertyType();
    }

    /**
     * Create an instance of {@link ProcessMethodPropertyType }
     * 
     */
    public ProcessMethodPropertyType createProcessMethodPropertyType() {
        return new ProcessMethodPropertyType();
    }

    /**
     * Create an instance of {@link IdentifierListPropertyType }
     * 
     */
    public IdentifierListPropertyType createIdentifierListPropertyType() {
        return new IdentifierListPropertyType();
    }

    /**
     * Create an instance of {@link KeywordListPropertyType }
     * 
     */
    public KeywordListPropertyType createKeywordListPropertyType() {
        return new KeywordListPropertyType();
    }

    /**
     * Create an instance of {@link AbstractSettingsPropertyType }
     * 
     */
    public AbstractSettingsPropertyType createAbstractSettingsPropertyType() {
        return new AbstractSettingsPropertyType();
    }

    /**
     * Create an instance of {@link StatusSettingPropertyType }
     * 
     */
    public StatusSettingPropertyType createStatusSettingPropertyType() {
        return new StatusSettingPropertyType();
    }

    /**
     * Create an instance of {@link OutputListPropertyType }
     * 
     */
    public OutputListPropertyType createOutputListPropertyType() {
        return new OutputListPropertyType();
    }

    /**
     * Create an instance of {@link ObservablePropertyPropertyType }
     * 
     */
    public ObservablePropertyPropertyType createObservablePropertyPropertyType() {
        return new ObservablePropertyPropertyType();
    }

    /**
     * Create an instance of {@link ConstraintPropertyType }
     * 
     */
    public ConstraintPropertyType createConstraintPropertyType() {
        return new ConstraintPropertyType();
    }

    /**
     * Create an instance of {@link DataComponentRefPropertyType }
     * 
     */
    public DataComponentRefPropertyType createDataComponentRefPropertyType() {
        return new DataComponentRefPropertyType();
    }

    /**
     * Create an instance of {@link AggregateProcessPropertyType }
     * 
     */
    public AggregateProcessPropertyType createAggregateProcessPropertyType() {
        return new AggregateProcessPropertyType();
    }

    /**
     * Create an instance of {@link CharacteristicListPropertyType }
     * 
     */
    public CharacteristicListPropertyType createCharacteristicListPropertyType() {
        return new CharacteristicListPropertyType();
    }

    /**
     * Create an instance of {@link EventPropertyType }
     * 
     */
    public EventPropertyType createEventPropertyType() {
        return new EventPropertyType();
    }

    /**
     * Create an instance of {@link PositionUnionPropertyType }
     * 
     */
    public PositionUnionPropertyType createPositionUnionPropertyType() {
        return new PositionUnionPropertyType();
    }

    /**
     * Create an instance of {@link SimpleProcessPropertyType }
     * 
     */
    public SimpleProcessPropertyType createSimpleProcessPropertyType() {
        return new SimpleProcessPropertyType();
    }

    /**
     * Create an instance of {@link AbstractProcessPropertyType }
     * 
     */
    public AbstractProcessPropertyType createAbstractProcessPropertyType() {
        return new AbstractProcessPropertyType();
    }

    /**
     * Create an instance of {@link ContactListPropertyType }
     * 
     */
    public ContactListPropertyType createContactListPropertyType() {
        return new ContactListPropertyType();
    }

    /**
     * Create an instance of {@link DataComponentOrObservablePropertyType }
     * 
     */
    public DataComponentOrObservablePropertyType createDataComponentOrObservablePropertyType() {
        return new DataComponentOrObservablePropertyType();
    }

    /**
     * Create an instance of {@link AbstractPhysicalProcessPropertyType }
     * 
     */
    public AbstractPhysicalProcessPropertyType createAbstractPhysicalProcessPropertyType() {
        return new AbstractPhysicalProcessPropertyType();
    }

    /**
     * Create an instance of {@link EventListPropertyType }
     * 
     */
    public EventListPropertyType createEventListPropertyType() {
        return new EventListPropertyType();
    }

    /**
     * Create an instance of {@link ConstraintSettingPropertyType }
     * 
     */
    public ConstraintSettingPropertyType createConstraintSettingPropertyType() {
        return new ConstraintSettingPropertyType();
    }

    /**
     * Create an instance of {@link ModeSettingPropertyType }
     * 
     */
    public ModeSettingPropertyType createModeSettingPropertyType() {
        return new ModeSettingPropertyType();
    }

    /**
     * Create an instance of {@link SettingsPropertyType }
     * 
     */
    public SettingsPropertyType createSettingsPropertyType() {
        return new SettingsPropertyType();
    }

    /**
     * Create an instance of {@link DataInterfacePropertyType }
     * 
     */
    public DataInterfacePropertyType createDataInterfacePropertyType() {
        return new DataInterfacePropertyType();
    }

    /**
     * Create an instance of {@link CapabilityListPropertyType }
     * 
     */
    public CapabilityListPropertyType createCapabilityListPropertyType() {
        return new CapabilityListPropertyType();
    }

    /**
     * Create an instance of {@link TermPropertyType }
     * 
     */
    public TermPropertyType createTermPropertyType() {
        return new TermPropertyType();
    }

    /**
     * Create an instance of {@link ModeChoicePropertyType }
     * 
     */
    public ModeChoicePropertyType createModeChoicePropertyType() {
        return new ModeChoicePropertyType();
    }

    /**
     * Create an instance of {@link AbstractMetadataListPropertyType }
     * 
     */
    public AbstractMetadataListPropertyType createAbstractMetadataListPropertyType() {
        return new AbstractMetadataListPropertyType();
    }

    /**
     * Create an instance of {@link LinkPropertyType }
     * 
     */
    public LinkPropertyType createLinkPropertyType() {
        return new LinkPropertyType();
    }

    /**
     * Create an instance of {@link DocumentListPropertyType }
     * 
     */
    public DocumentListPropertyType createDocumentListPropertyType() {
        return new DocumentListPropertyType();
    }

    /**
     * Create an instance of {@link TimeInstantOrPeriodPropertyType }
     * 
     */
    public TimeInstantOrPeriodPropertyType createTimeInstantOrPeriodPropertyType() {
        return new TimeInstantOrPeriodPropertyType();
    }

    /**
     * Create an instance of {@link ComponentListPropertyType }
     * 
     */
    public ComponentListPropertyType createComponentListPropertyType() {
        return new ComponentListPropertyType();
    }

    /**
     * Create an instance of {@link AbstractModesPropertyType }
     * 
     */
    public AbstractModesPropertyType createAbstractModesPropertyType() {
        return new AbstractModesPropertyType();
    }

    /**
     * Create an instance of {@link DescribedObjectPropertyType }
     * 
     */
    public DescribedObjectPropertyType createDescribedObjectPropertyType() {
        return new DescribedObjectPropertyType();
    }

    /**
     * Create an instance of {@link ValueSettingPropertyType }
     * 
     */
    public ValueSettingPropertyType createValueSettingPropertyType() {
        return new ValueSettingPropertyType();
    }

    /**
     * Create an instance of {@link PhysicalSystemPropertyType }
     * 
     */
    public PhysicalSystemPropertyType createPhysicalSystemPropertyType() {
        return new PhysicalSystemPropertyType();
    }

    /**
     * Create an instance of {@link ModePropertyType }
     * 
     */
    public ModePropertyType createModePropertyType() {
        return new ModePropertyType();
    }

    /**
     * Create an instance of {@link SpatialFramePropertyType }
     * 
     */
    public SpatialFramePropertyType createSpatialFramePropertyType() {
        return new SpatialFramePropertyType();
    }

    /**
     * Create an instance of {@link ArraySettingPropertyType.ArrayValues.Encoding }
     * 
     */
    public ArraySettingPropertyType.ArrayValues.Encoding createArraySettingPropertyTypeArrayValuesEncoding() {
        return new ArraySettingPropertyType.ArrayValues.Encoding();
    }

    /**
     * Create an instance of {@link ClassifierListType.Classifier }
     * 
     */
    public ClassifierListType.Classifier createClassifierListTypeClassifier() {
        return new ClassifierListType.Classifier();
    }

    /**
     * Create an instance of {@link ParameterListType.Parameter }
     * 
     */
    public ParameterListType.Parameter createParameterListTypeParameter() {
        return new ParameterListType.Parameter();
    }

    /**
     * Create an instance of {@link CapabilityListType.Capability }
     * 
     */
    public CapabilityListType.Capability createCapabilityListTypeCapability() {
        return new CapabilityListType.Capability();
    }

    /**
     * Create an instance of {@link ConnectionListType.Connection }
     * 
     */
    public ConnectionListType.Connection createConnectionListTypeConnection() {
        return new ConnectionListType.Connection();
    }

    /**
     * Create an instance of {@link CharacteristicListType.Characteristic }
     * 
     */
    public CharacteristicListType.Characteristic createCharacteristicListTypeCharacteristic() {
        return new CharacteristicListType.Characteristic();
    }

    /**
     * Create an instance of {@link DescribedObjectType.ValidTime }
     * 
     */
    public DescribedObjectType.ValidTime createDescribedObjectTypeValidTime() {
        return new DescribedObjectType.ValidTime();
    }

    /**
     * Create an instance of {@link DescribedObjectType.Characteristics }
     * 
     */
    public DescribedObjectType.Characteristics createDescribedObjectTypeCharacteristics() {
        return new DescribedObjectType.Characteristics();
    }

    /**
     * Create an instance of {@link DescribedObjectType.Capabilities }
     * 
     */
    public DescribedObjectType.Capabilities createDescribedObjectTypeCapabilities() {
        return new DescribedObjectType.Capabilities();
    }

    /**
     * Create an instance of {@link EventType.Time }
     * 
     */
    public EventType.Time createEventTypeTime() {
        return new EventType.Time();
    }

    /**
     * Create an instance of {@link EventType.Configuration }
     * 
     */
    public EventType.Configuration createEventTypeConfiguration() {
        return new EventType.Configuration();
    }

    /**
     * Create an instance of {@link AbstractProcessType.Configuration }
     * 
     */
    public AbstractProcessType.Configuration createAbstractProcessTypeConfiguration() {
        return new AbstractProcessType.Configuration();
    }

    /**
     * Create an instance of {@link AbstractProcessType.FeaturesOfInterest }
     * 
     */
    public AbstractProcessType.FeaturesOfInterest createAbstractProcessTypeFeaturesOfInterest() {
        return new AbstractProcessType.FeaturesOfInterest();
    }

    /**
     * Create an instance of {@link AbstractProcessType.Inputs }
     * 
     */
    public AbstractProcessType.Inputs createAbstractProcessTypeInputs() {
        return new AbstractProcessType.Inputs();
    }

    /**
     * Create an instance of {@link AbstractProcessType.Outputs }
     * 
     */
    public AbstractProcessType.Outputs createAbstractProcessTypeOutputs() {
        return new AbstractProcessType.Outputs();
    }

    /**
     * Create an instance of {@link AbstractProcessType.Parameters }
     * 
     */
    public AbstractProcessType.Parameters createAbstractProcessTypeParameters() {
        return new AbstractProcessType.Parameters();
    }

    /**
     * Create an instance of {@link AbstractProcessType.Modes }
     * 
     */
    public AbstractProcessType.Modes createAbstractProcessTypeModes() {
        return new AbstractProcessType.Modes();
    }

    /**
     * Create an instance of {@link AbstractPhysicalProcessType.LocalReferenceFrame }
     * 
     */
    public AbstractPhysicalProcessType.LocalReferenceFrame createAbstractPhysicalProcessTypeLocalReferenceFrame() {
        return new AbstractPhysicalProcessType.LocalReferenceFrame();
    }

    /**
     * Create an instance of {@link AbstractPhysicalProcessType.LocalTimeFrame }
     * 
     */
    public AbstractPhysicalProcessType.LocalTimeFrame createAbstractPhysicalProcessTypeLocalTimeFrame() {
        return new AbstractPhysicalProcessType.LocalTimeFrame();
    }

    /**
     * Create an instance of {@link SpatialFrameType.Axis }
     * 
     */
    public SpatialFrameType.Axis createSpatialFrameTypeAxis() {
        return new SpatialFrameType.Axis();
    }

    /**
     * Create an instance of {@link IdentifierListType.Identifier }
     * 
     */
    public IdentifierListType.Identifier createIdentifierListTypeIdentifier() {
        return new IdentifierListType.Identifier();
    }

    /**
     * Create an instance of {@link OutputListType.Output }
     * 
     */
    public OutputListType.Output createOutputListTypeOutput() {
        return new OutputListType.Output();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Algorithm }
     * 
     */
    public ProcessMethodType.Algorithm createProcessMethodTypeAlgorithm() {
        return new ProcessMethodType.Algorithm();
    }

    /**
     * Create an instance of {@link ComponentListType.Component }
     * 
     */
    public ComponentListType.Component createComponentListTypeComponent() {
        return new ComponentListType.Component();
    }

    /**
     * Create an instance of {@link InputListType.Input }
     * 
     */
    public InputListType.Input createInputListTypeInput() {
        return new InputListType.Input();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSettingsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "AbstractSettings", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<AbstractSettingsType> createAbstractSettings(AbstractSettingsType value) {
        return new JAXBElement<AbstractSettingsType>(_AbstractSettings_QNAME, AbstractSettingsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "AbstractProcess", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "DescribedObject")
    public JAXBElement<AbstractProcessType> createAbstractProcess(AbstractProcessType value) {
        return new JAXBElement<AbstractProcessType>(_AbstractProcess_QNAME, AbstractProcessType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InputListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "InputList", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<InputListType> createInputList(InputListType value) {
        return new JAXBElement<InputListType>(_InputList_QNAME, InputListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConnectionListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "ConnectionList", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<ConnectionListType> createConnectionList(ConnectionListType value) {
        return new JAXBElement<ConnectionListType>(_ConnectionList_QNAME, ConnectionListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleProcessType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "SimpleProcess", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractProcess")
    public JAXBElement<SimpleProcessType> createSimpleProcess(SimpleProcessType value) {
        return new JAXBElement<SimpleProcessType>(_SimpleProcess_QNAME, SimpleProcessType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "EventList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<EventListType> createEventList(EventListType value) {
        return new JAXBElement<EventListType>(_EventList_QNAME, EventListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "Event", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<EventType> createEvent(EventType value) {
        return new JAXBElement<EventType>(_Event_QNAME, EventType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractModesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "AbstractModes", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<AbstractModesType> createAbstractModes(AbstractModesType value) {
        return new JAXBElement<AbstractModesType>(_AbstractModes_QNAME, AbstractModesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "ComponentList", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<ComponentListType> createComponentList(ComponentListType value) {
        return new JAXBElement<ComponentListType>(_ComponentList_QNAME, ComponentListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "FeatureList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<FeatureListType> createFeatureList(FeatureListType value) {
        return new JAXBElement<FeatureListType>(_FeatureList_QNAME, FeatureListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "Mode", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "DescribedObject")
    public JAXBElement<ModeType> createMode(ModeType value) {
        return new JAXBElement<ModeType>(_Mode_QNAME, ModeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModeChoiceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "ModeChoice", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractModes")
    public JAXBElement<ModeChoiceType> createModeChoice(ModeChoiceType value) {
        return new JAXBElement<ModeChoiceType>(_ModeChoice_QNAME, ModeChoiceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractMetadataListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "AbstractMetadataList", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<AbstractMetadataListType> createAbstractMetadataList(AbstractMetadataListType value) {
        return new JAXBElement<AbstractMetadataListType>(_AbstractMetadataList_QNAME, AbstractMetadataListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OutputListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "OutputList", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<OutputListType> createOutputList(OutputListType value) {
        return new JAXBElement<OutputListType>(_OutputList_QNAME, OutputListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "ProcessMethod", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<ProcessMethodType> createProcessMethod(ProcessMethodType value) {
        return new JAXBElement<ProcessMethodType>(_ProcessMethod_QNAME, ProcessMethodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LinkType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "Link", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<LinkType> createLink(LinkType value) {
        return new JAXBElement<LinkType>(_Link_QNAME, LinkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribedObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "DescribedObject", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<DescribedObjectType> createDescribedObject(DescribedObjectType value) {
        return new JAXBElement<DescribedObjectType>(_DescribedObject_QNAME, DescribedObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractPhysicalProcessType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "AbstractPhysicalProcess", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractProcess")
    public JAXBElement<AbstractPhysicalProcessType> createAbstractPhysicalProcess(AbstractPhysicalProcessType value) {
        return new JAXBElement<AbstractPhysicalProcessType>(_AbstractPhysicalProcess_QNAME, AbstractPhysicalProcessType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CharacteristicListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "CharacteristicList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<CharacteristicListType> createCharacteristicList(CharacteristicListType value) {
        return new JAXBElement<CharacteristicListType>(_CharacteristicList_QNAME, CharacteristicListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatialFrameType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "SpatialFrame", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<SpatialFrameType> createSpatialFrame(SpatialFrameType value) {
        return new JAXBElement<SpatialFrameType>(_SpatialFrame_QNAME, SpatialFrameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeywordListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "KeywordList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<KeywordListType> createKeywordList(KeywordListType value) {
        return new JAXBElement<KeywordListType>(_KeywordList_QNAME, KeywordListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalFrameType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "TemporalFrame", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<TemporalFrameType> createTemporalFrame(TemporalFrameType value) {
        return new JAXBElement<TemporalFrameType>(_TemporalFrame_QNAME, TemporalFrameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PhysicalComponentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "PhysicalComponent", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractPhysicalProcess")
    public JAXBElement<PhysicalComponentType> createPhysicalComponent(PhysicalComponentType value) {
        return new JAXBElement<PhysicalComponentType>(_PhysicalComponent_QNAME, PhysicalComponentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AggregateProcessType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "AggregateProcess", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractProcess")
    public JAXBElement<AggregateProcessType> createAggregateProcess(AggregateProcessType value) {
        return new JAXBElement<AggregateProcessType>(_AggregateProcess_QNAME, AggregateProcessType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservablePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "ObservableProperty", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<ObservablePropertyType> createObservableProperty(ObservablePropertyType value) {
        return new JAXBElement<ObservablePropertyType>(_ObservableProperty_QNAME, ObservablePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataInterfaceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "DataInterface", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<DataInterfaceType> createDataInterface(DataInterfaceType value) {
        return new JAXBElement<DataInterfaceType>(_DataInterface_QNAME, DataInterfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassifierListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "ClassifierList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<ClassifierListType> createClassifierList(ClassifierListType value) {
        return new JAXBElement<ClassifierListType>(_ClassifierList_QNAME, ClassifierListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "ContactList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<ContactListType> createContactList(ContactListType value) {
        return new JAXBElement<ContactListType>(_ContactList_QNAME, ContactListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "DocumentList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<DocumentListType> createDocumentList(DocumentListType value) {
        return new JAXBElement<DocumentListType>(_DocumentList_QNAME, DocumentListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractAlgorithmType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "AbstractAlgorithm", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<AbstractAlgorithmType> createAbstractAlgorithm(AbstractAlgorithmType value) {
        return new JAXBElement<AbstractAlgorithmType>(_AbstractAlgorithm_QNAME, AbstractAlgorithmType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TermType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "Term", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<TermType> createTerm(TermType value) {
        return new JAXBElement<TermType>(_Term_QNAME, TermType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "IdentifierList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<IdentifierListType> createIdentifierList(IdentifierListType value) {
        return new JAXBElement<IdentifierListType>(_IdentifierList_QNAME, IdentifierListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SettingsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "Settings", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractSettings")
    public JAXBElement<SettingsType> createSettings(SettingsType value) {
        return new JAXBElement<SettingsType>(_Settings_QNAME, SettingsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CapabilityListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "CapabilityList", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractMetadataList")
    public JAXBElement<CapabilityListType> createCapabilityList(CapabilityListType value) {
        return new JAXBElement<CapabilityListType>(_CapabilityList_QNAME, CapabilityListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PhysicalSystemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "PhysicalSystem", substitutionHeadNamespace = "http://www.opengis.net/sensorml/2.0", substitutionHeadName = "AbstractPhysicalProcess")
    public JAXBElement<PhysicalSystemType> createPhysicalSystem(PhysicalSystemType value) {
        return new JAXBElement<PhysicalSystemType>(_PhysicalSystem_QNAME, PhysicalSystemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "ParameterList", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<ParameterListType> createParameterList(ParameterListType value) {
        return new JAXBElement<ParameterListType>(_ParameterList_QNAME, ParameterListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IdentifierListType.Identifier }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorml/2.0", name = "identifier", scope = IdentifierListType.class)
    public JAXBElement<IdentifierListType.Identifier> createIdentifierListTypeIdentifier(IdentifierListType.Identifier value) {
        return new JAXBElement<IdentifierListType.Identifier>(_IdentifierListTypeIdentifier_QNAME, IdentifierListType.Identifier.class, IdentifierListType.class, value);
    }

}
