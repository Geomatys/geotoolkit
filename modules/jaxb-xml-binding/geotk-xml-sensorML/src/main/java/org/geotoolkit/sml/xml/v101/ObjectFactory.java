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
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.sensorml._1_0 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @module pending
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _Process_QNAME        = new QName("http://www.opengis.net/sensorML/1.0.1", "AbstractProcess");
    private static final QName _RuleLanguage_QNAME   = new QName("http://www.opengis.net/sensorML/1.0.1", "ruleLanguage");
    private static final QName _ProcessMethod_QNAME  = new QName("http://www.opengis.net/sensorML/1.0.1", "ProcessMethod");
    private static final QName _ComponentArray_QNAME = new QName("http://www.opengis.net/sensorML/1.0.1", "ComponentArray");
    private static final QName _ProcessChain_QNAME   = new QName("http://www.opengis.net/sensorML/1.0.1", "ProcessChain");
    private static final QName _Method_QNAME         = new QName("http://www.opengis.net/sensorML/1.0.1", "method");
    private static final QName _ProcessModel_QNAME   = new QName("http://www.opengis.net/sensorML/1.0.1", "ProcessModel");
    private static final QName _RelaxNG_QNAME        = new QName("http://www.opengis.net/sensorML/1.0.1", "relaxNG");
    private static final QName _System_QNAME         = new QName("http://www.opengis.net/sensorML/1.0.1", "System");
    private static final QName _DataSource_QNAME     = new QName("http://www.opengis.net/sensorML/1.0.1", "DataSource");
    private static final QName _Component_QNAME      = new QName("http://www.opengis.net/sensorML/1.0.1", "Component");
    private static final QName _Schematron_QNAME     = new QName("http://www.opengis.net/sensorML/1.0.1", "schematron");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.sensorml._1_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessMethodType.Rules }
     * 
     */
    public ProcessMethodType.Rules createProcessMethodTypeRules() {
        return new ProcessMethodType.Rules();
    }

    /**
     * Create an instance of {@link ResponsibleParty }
     * 
     */
    public ResponsibleParty createResponsibleParty() {
        return new ResponsibleParty();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Algorithm.AlgorithmDefinition.MathML }
     * 
     */
    public ProcessMethodType.Algorithm.AlgorithmDefinition.MathML createProcessMethodTypeAlgorithmAlgorithmDefinitionMathML() {
        return new ProcessMethodType.Algorithm.AlgorithmDefinition.MathML();
    }

    /**
     * Create an instance of {@link MethodPropertyType }
     * 
     */
    public MethodPropertyType createMethodPropertyType() {
        return new MethodPropertyType();
    }

    /**
     * Create an instance of {@link Outputs.OutputList }
     * 
     */
    public OutputList createOutputList() {
        return new OutputList();
    }

    /**
     * Create an instance of {@link IoComponentPropertyType }
     * 
     */
    public IoComponentPropertyType createIoComponentPropertyType() {
        return new IoComponentPropertyType();
    }

    /**
     * Create an instance of {@link Characteristics }
     * 
     */
    public Characteristics createCharacteristics() {
        return new Characteristics();
    }

    /**
     * Create an instance of {@link Positions }
     * 
     */
    public Positions createPositions() {
        return new Positions();
    }

    /**
     * Create an instance of {@link Components.ComponentList }
     * 
     */
    public ComponentList createComponentList() {
        return new ComponentList();
    }

    /**
     * Create an instance of {@link Interfaces.InterfaceList }
     * 
     */
    public InterfaceList createInterfaceList() {
        return new InterfaceList();
    }

    /**
     * Create an instance of {@link PresentationLayerPropertyType }
     * 
     */
    public PresentationLayerPropertyType createPresentationLayerPropertyType() {
        return new PresentationLayerPropertyType();
    }

    /**
     * Create an instance of {@link Parameters.ParameterList }
     * 
     */
    public ParameterList createParameterList() {
        return new ParameterList();
    }

    /**
     * Create an instance of {@link DataSourceType.DataDefinition }
     * 
     */
    public DataDefinition createDataDefinition() {
        return new DataDefinition();
    }

    /**
     * Create an instance of {@link Connection }
     * 
     */
    public Connection createConnection() {
        return new Connection();
    }

    /**
     * Create an instance of {@link SensorML }
     * 
     */
    public SensorML createSensorML() {
        return new SensorML();
    }

    /**
     * Create an instance of {@link SecurityConstraint }
     * 
     */
    public SecurityConstraint createSecurityConstraint() {
        return new SecurityConstraint();
    }

    /**
     * Create an instance of {@link Classification.ClassifierList.Classifier }
     * 
     */
    public Classifier createClassifier() {
        return new Classifier();
    }

    /**
     * Create an instance of {@link History }
     * 
     */
    public History createHistory() {
        return new History();
    }

    /**
     * Create an instance of {@link ValidTime }
     * 
     */
    public ValidTime createValidTime() {
        return new ValidTime();
    }

    /**
     * Create an instance of {@link DocumentList.Member }
     * 
     */
    public DocumentList.Member createDocumentListMember() {
        return new DocumentList.Member();
    }

    /**
     * Create an instance of {@link Keywords.KeywordList }
     * 
     */
    public KeywordList createKeywordList() {
        return new KeywordList();
    }

    /**
     * Create an instance of {@link Security }
     * 
     */
    public Security createSecurity() {
        return new Security();
    }

    /**
     * Create an instance of {@link TimePosition }
     * 
     */
    public TimePosition createTimePosition() {
        return new TimePosition();
    }

    /**
     * Create an instance of {@link RuleLanguageType }
     * 
     */
    public RuleLanguageType createRuleLanguageType() {
        return new RuleLanguageType();
    }

    /**
     * Create an instance of {@link Keywords }
     * 
     */
    public Keywords createKeywords() {
        return new Keywords();
    }

    /**
     * Create an instance of {@link Parameters }
     * 
     */
    public Parameters createParameters() {
        return new Parameters();
    }

    /**
     * Create an instance of {@link EventList.Member }
     * 
     */
    public EventListMember createEventListMember() {
        return new EventListMember();
    }

    /**
     * Create an instance of {@link DataSourceType }
     * 
     */
    public DataSourceType createDataSourceType() {
        return new DataSourceType();
    }

    /**
     * Create an instance of {@link DataSourceType.Values }
     * 
     */
    public Values createValues() {
        return new Values();
    }

    /**
     * Create an instance of {@link Positions.PositionList }
     * 
     */
    public PositionList createPositionList() {
        return new PositionList();
    }

    /**
     * Create an instance of {@link Identification.IdentifierList.Identifier }
     * 
     */
    public Identifier createIdentifier() {
        return new Identifier();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Implementation.ImplementationCode }
     * 
     */
    public ProcessMethodType.Implementation.ImplementationCode createProcessMethodTypeImplementationImplementationCode() {
        return new ProcessMethodType.Implementation.ImplementationCode();
    }

    /**
     * Create an instance of {@link ContactList.Member }
     * 
     */
    public ContactList.Member createContactListMember() {
        return new ContactList.Member();
    }

    /**
     * Create an instance of {@link ParametersPropertyType }
     * 
     */
    public ParametersPropertyType createParametersPropertyType() {
        return new ParametersPropertyType();
    }

    /**
     * Create an instance of {@link Interface }
     * 
     */
    public Interface createInterface() {
        return new Interface();
    }

    /**
     * Create an instance of {@link Capabilities }
     * 
     */
    public Capabilities createCapabilities() {
        return new Capabilities();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Implementation.ImplementationCode.BinaryRef }
     * 
     */
    public ProcessMethodType.Implementation.ImplementationCode.BinaryRef createProcessMethodTypeImplementationImplementationCodeBinaryRef() {
        return new ProcessMethodType.Implementation.ImplementationCode.BinaryRef();
    }

    /**
     * Create an instance of {@link Term }
     * 
     */
    public Term createTerm() {
        return new Term();
    }

    /**
     * Create an instance of {@link ConnectionsPropertyType }
     * 
     */
    public ConnectionsPropertyType createConnectionsPropertyType() {
        return new ConnectionsPropertyType();
    }

    /**
     * Create an instance of {@link Position }
     * 
     */
    public Position createPosition() {
        return new Position();
    }

    /**
     * Create an instance of {@link SensorML.Member }
     * 
     */
    public SensorML.Member createSensorMLMember() {
        return new SensorML.Member();
    }

    /**
     * Create an instance of {@link InterfaceDefinition }
     * 
     */
    public InterfaceDefinition createInterfaceDefinition() {
        return new InterfaceDefinition();
    }

    /**
     * Create an instance of {@link ContactInfo.Phone }
     * 
     */
    public Phone createPhone() {
        return new Phone();
    }

    /**
     * Create an instance of {@link SpatialReferenceFrame }
     * 
     */
    public SpatialReferenceFrame createSpatialReferenceFrame() {
        return new SpatialReferenceFrame();
    }

    /**
     * Create an instance of {@link ProcessMethodType }
     * 
     */
    public ProcessMethodType createProcessMethodType() {
        return new ProcessMethodType();
    }

    /**
     * Create an instance of {@link SystemType }
     * 
     */
    public SystemType createSystemType() {
        return new SystemType();
    }

    /**
     * Create an instance of {@link Inputs }
     * 
     */
    public Inputs createInputs() {
        return new Inputs();
    }

    /**
     * Create an instance of {@link Location }
     * 
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link LayerPropertyType }
     * 
     */
    public LayerPropertyType createLayerPropertyType() {
        return new LayerPropertyType();
    }

    /**
     * Create an instance of {@link Classification.ClassifierList }
     * 
     */
    public ClassifierList createClassifierList() {
        return new ClassifierList();
    }

    /**
     * Create an instance of {@link AbstractListType }
     * 
     */
    public AbstractListType createAbstractListType() {
        return new AbstractListType();
    }

    /**
     * Create an instance of {@link InputsPropertyType }
     * 
     */
    public InputsPropertyType createInputsPropertyType() {
        return new InputsPropertyType();
    }

    /**
     * Create an instance of {@link LegalConstraint }
     * 
     */
    public LegalConstraint createLegalConstraint() {
        return new LegalConstraint();
    }

    /**
     * Create an instance of {@link Connections.ConnectionList }
     * 
     */
    public ConnectionList createConnectionList() {
        return new ConnectionList();
    }

    /**
     * Create an instance of {@link DataSourcesPropertyType }
     * 
     */
    public DataSourcesPropertyType createDataSourcesPropertyType() {
        return new DataSourcesPropertyType();
    }

    /**
     * Create an instance of {@link Rights }
     * 
     */
    public Rights createRights() {
        return new Rights();
    }

    /**
     * Create an instance of {@link Outputs }
     * 
     */
    public Outputs createOutputs() {
        return new Outputs();
    }

    /**
     * Create an instance of {@link DataSourceType.ObservationReference }
     * 
     */
    public ObservationReference createDataSourceTypeObservationReference() {
        return new ObservationReference();
    }

    /**
     * Create an instance of {@link ContactList }
     * 
     */
    public ContactList createContactList() {
        return new ContactList();
    }

    /**
     * Create an instance of {@link Inputs.InputList }
     * 
     */
    public InputList createInputList() {
        return new InputList();
    }

    /**
     * Create an instance of {@link ProcessModelType }
     * 
     */
    public ProcessModelType createProcessModelType() {
        return new ProcessModelType();
    }

    /**
     * Create an instance of {@link ArrayLink }
     * 
     */
    public ArrayLink createArrayLink() {
        return new ArrayLink();
    }

    /**
     * Create an instance of {@link Connections }
     * 
     */
    public Connections createConnections() {
        return new Connections();
    }

    /**
     * Create an instance of {@link Identification }
     * 
     */
    public Identification createIdentification() {
        return new Identification();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Rules.RulesDefinition }
     * 
     */
    public ProcessMethodType.Rules.RulesDefinition createProcessMethodTypeRulesRulesDefinition() {
        return new ProcessMethodType.Rules.RulesDefinition();
    }

    /**
     * Create an instance of {@link EventList }
     * 
     */
    public EventList createEventList() {
        return new EventList();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Implementation }
     * 
     */
    public ProcessMethodType.Implementation createProcessMethodTypeImplementation() {
        return new ProcessMethodType.Implementation();
    }

    /**
     * Create an instance of {@link Documentation }
     * 
     */
    public Documentation createDocumentation() {
        return new Documentation();
    }

    /**
     * Create an instance of {@link Document }
     * 
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link Contact }
     * 
     */
    public Contact createContact() {
        return new Contact();
    }

    /**
     * Create an instance of {@link Person }
     * 
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link ComponentType }
     * 
     */
    public ComponentType createComponentType() {
        return new ComponentType();
    }

    /**
     * Create an instance of {@link ComponentArrayType }
     * 
     */
    public ComponentArrayType createComponentArrayType() {
        return new ComponentArrayType();
    }

    /**
     * Create an instance of {@link RelaxNG }
     * 
     */
    public RelaxNG createRelaxNG() {
        return new RelaxNG();
    }

    /**
     * Create an instance of {@link Schematron }
     * 
     */
    public Schematron createSchematron() {
        return new Schematron();
    }

    /**
     * Create an instance of {@link Event }
     * 
     */
    public Event createEvent() {
        return new Event();
    }

    /**
     * Create an instance of {@link Interfaces }
     * 
     */
    public Interfaces createInterfaces() {
        return new Interfaces();
    }

    /**
     * Create an instance of {@link TemporalReferenceFrame }
     * 
     */
    public TemporalReferenceFrame createTemporalReferenceFrame() {
        return new TemporalReferenceFrame();
    }

    /**
     * Create an instance of {@link Link.Source }
     * 
     */
    public LinkRef createLinkRef() {
        return new LinkRef();
    }

    /**
     * Create an instance of {@link Identification.IdentifierList }
     * 
     */
    public IdentifierList createIdentifierList() {
        return new IdentifierList();
    }

    /**
     * Create an instance of {@link Classification }
     * 
     */
    public Classification createClassification() {
        return new Classification();
    }

    /**
     * Create an instance of {@link Components.ComponentList.Component }
     * 
     */
    public ComponentPropertyType createComponentPropertyType() {
        return new ComponentPropertyType();
    }

    /**
     * Create an instance of {@link ContactInfo.Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link ContactInfo }
     * 
     */
    public ContactInfo createContactInfo() {
        return new ContactInfo();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Algorithm }
     * 
     */
    public ProcessMethodType.Algorithm createProcessMethodTypeAlgorithm() {
        return new ProcessMethodType.Algorithm();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Implementation.ImplementationCode.SourceRef }
     * 
     */
    public ProcessMethodType.Implementation.ImplementationCode.SourceRef createProcessMethodTypeImplementationImplementationCodeSourceRef() {
        return new ProcessMethodType.Implementation.ImplementationCode.SourceRef();
    }

    /**
     * Create an instance of {@link OutputsPropertyType }
     * 
     */
    public OutputsPropertyType createOutputsPropertyType() {
        return new OutputsPropertyType();
    }

    /**
     * Create an instance of {@link OnlineResource }
     * 
     */
    public OnlineResource createOnlineResource() {
        return new OnlineResource();
    }

    /**
     * Create an instance of {@link ProcessChainType }
     * 
     */
    public ProcessChainType createProcessChainType() {
        return new ProcessChainType();
    }

    /**
     * Create an instance of {@link ProcessMethodType.Algorithm.AlgorithmDefinition }
     * 
     */
    public ProcessMethodType.Algorithm.AlgorithmDefinition createProcessMethodTypeAlgorithmAlgorithmDefinition() {
        return new ProcessMethodType.Algorithm.AlgorithmDefinition();
    }

    /**
     * Create an instance of {@link ComponentsPropertyType }
     * 
     */
    public ComponentsPropertyType createComponentsPropertyType() {
        return new ComponentsPropertyType();
    }

    /**
     * Create an instance of {@link DocumentList }
     * 
     */
    public DocumentList createDocumentList() {
        return new DocumentList();
    }

    /**
     * Create an instance of {@link Link }
     *
     */
    public Link createLink() {
        return new Link();
    }

    /**
     * Create an instance of {@link Components }
     * 
     */
    public Components createComponents() {
        return new Components();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "AbstractProcess")
    public JAXBElement<AbstractProcessType> createProcess(final AbstractProcessType value) {
        return new JAXBElement<AbstractProcessType>(_Process_QNAME, AbstractProcessType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RuleLanguageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "ruleLanguage")
    public JAXBElement<RuleLanguageType> createRuleLanguage(final RuleLanguageType value) {
        return new JAXBElement<RuleLanguageType>(_RuleLanguage_QNAME, RuleLanguageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "ProcessMethod")
    public JAXBElement<ProcessMethodType> createProcessMethod(final ProcessMethodType value) {
        return new JAXBElement<ProcessMethodType>(_ProcessMethod_QNAME, ProcessMethodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentArrayType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "ComponentArray", substitutionHeadNamespace = "http://www.opengis.net/sensorML/1.0.1", substitutionHeadName = "AbstractProcess")
    public JAXBElement<ComponentArrayType> createComponentArray(final ComponentArrayType value) {
        return new JAXBElement<ComponentArrayType>(_ComponentArray_QNAME, ComponentArrayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessChainType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "ProcessChain", substitutionHeadNamespace = "http://www.opengis.net/sensorML/1.0.1", substitutionHeadName = "AbstractProcess")
    public JAXBElement<ProcessChainType> createProcessChain(final ProcessChainType value) {
        return new JAXBElement<ProcessChainType>(_ProcessChain_QNAME, ProcessChainType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MethodPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "method")
    public JAXBElement<MethodPropertyType> createMethod(final MethodPropertyType value) {
        return new JAXBElement<MethodPropertyType>(_Method_QNAME, MethodPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessModelType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "ProcessModel", substitutionHeadNamespace = "http://www.opengis.net/sensorML/1.0.1", substitutionHeadName = "AbstractProcess")
    public JAXBElement<ProcessModelType> createProcessModel(final ProcessModelType value) {
        return new JAXBElement<ProcessModelType>(_ProcessModel_QNAME, ProcessModelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelaxNG }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "relaxNG", substitutionHeadNamespace = "http://www.opengis.net/sensorML/1.0.1", substitutionHeadName = "ruleLanguage")
    public JAXBElement<RelaxNG> createRelaxNG(final RelaxNG value) {
        return new JAXBElement<RelaxNG>(_RelaxNG_QNAME, RelaxNG.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SystemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "System", substitutionHeadNamespace = "http://www.opengis.net/sensorML/1.0.1", substitutionHeadName = "AbstractProcess")
    public JAXBElement<SystemType> createSystem(final SystemType value) {
        return new JAXBElement<SystemType>(_System_QNAME, SystemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataSourceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "DataSource", substitutionHeadNamespace = "http://www.opengis.net/sensorML/1.0.1", substitutionHeadName = "AbstractProcess")
    public JAXBElement<DataSourceType> createDataSource(final DataSourceType value) {
        return new JAXBElement<DataSourceType>(_DataSource_QNAME, DataSourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "Component", substitutionHeadNamespace = "http://www.opengis.net/sensorML/1.0.1", substitutionHeadName = "AbstractProcess")
    public JAXBElement<ComponentType> createComponent(final ComponentType value) {
        return new JAXBElement<ComponentType>(_Component_QNAME, ComponentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Schematron }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sensorML/1.0.1", name = "schematron", substitutionHeadNamespace = "http://www.opengis.net/sensorML/1.0.1", substitutionHeadName = "ruleLanguage")
    public JAXBElement<Schematron> createSchematron(final Schematron value) {
        return new JAXBElement<Schematron>(_Schematron_QNAME, Schematron.class, null, value);
    }

}
