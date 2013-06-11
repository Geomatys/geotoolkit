/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swes.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.swes._2 package. 
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

    private final static QName _NotificationProducerMetadata_QNAME = new QName("http://www.opengis.net/swes/2.0", "NotificationProducerMetadata");
    private final static QName _InsertionMetadata_QNAME = new QName("http://www.opengis.net/swes/2.0", "InsertionMetadata");
    private final static QName _SensorChanged_QNAME = new QName("http://www.opengis.net/swes/2.0", "SensorChanged");
    private final static QName _NotificationBrokerMetadata_QNAME = new QName("http://www.opengis.net/swes/2.0", "NotificationBrokerMetadata");
    private final static QName _SensorDescription_QNAME = new QName("http://www.opengis.net/swes/2.0", "SensorDescription");
    private final static QName _ExtensibleResponse_QNAME = new QName("http://www.opengis.net/swes/2.0", "ExtensibleResponse");
    private final static QName _FeatureRelationship_QNAME = new QName("http://www.opengis.net/swes/2.0", "FeatureRelationship");
    private final static QName _AbstractContents_QNAME = new QName("http://www.opengis.net/swes/2.0", "AbstractContents");
    private final static QName _ExtensibleRequest_QNAME = new QName("http://www.opengis.net/swes/2.0", "ExtensibleRequest");
    private final static QName _UpdateSensorDescriptionResponse_QNAME = new QName("http://www.opengis.net/swes/2.0", "UpdateSensorDescriptionResponse");
    private final static QName _InsertSensorResponse_QNAME = new QName("http://www.opengis.net/swes/2.0", "InsertSensorResponse");
    private final static QName _DeleteSensorResponse_QNAME = new QName("http://www.opengis.net/swes/2.0", "DeleteSensorResponse");
    private final static QName _InsertSensor_QNAME = new QName("http://www.opengis.net/swes/2.0", "InsertSensor");
    private final static QName _OfferingChanged_QNAME = new QName("http://www.opengis.net/swes/2.0", "OfferingChanged");
    private final static QName _DeleteSensor_QNAME = new QName("http://www.opengis.net/swes/2.0", "DeleteSensor");
    private final static QName _AbstractSWES_QNAME = new QName("http://www.opengis.net/swes/2.0", "AbstractSWES");
    private final static QName _FilterDialectMetadata_QNAME = new QName("http://www.opengis.net/swes/2.0", "FilterDialectMetadata");
    private final static QName _UpdateSensorDescription_QNAME = new QName("http://www.opengis.net/swes/2.0", "UpdateSensorDescription");
    private final static QName _DescribeSensorResponse_QNAME = new QName("http://www.opengis.net/swes/2.0", "DescribeSensorResponse");
    private final static QName _SWESEvent_QNAME = new QName("http://www.opengis.net/swes/2.0", "SWESEvent");
    private final static QName _SensorDescriptionUpdated_QNAME = new QName("http://www.opengis.net/swes/2.0", "SensorDescriptionUpdated");
    private final static QName _DescribeSensor_QNAME = new QName("http://www.opengis.net/swes/2.0", "DescribeSensor");
    private final static QName _AbstractOffering_QNAME = new QName("http://www.opengis.net/swes/2.0", "AbstractOffering");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.swes._2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdateSensorDescriptionType }
     * 
     */
    public UpdateSensorDescriptionType createUpdateSensorDescriptionType() {
        return new UpdateSensorDescriptionType();
    }

    /**
     * Create an instance of {@link SensorChangedPropertyType }
     * 
     */
    public SensorChangedPropertyType createSensorChangedPropertyType() {
        return new SensorChangedPropertyType();
    }

    /**
     * Create an instance of {@link FeatureRelationshipPropertyType }
     * 
     */
    public FeatureRelationshipPropertyType createFeatureRelationshipPropertyType() {
        return new FeatureRelationshipPropertyType();
    }

    /**
     * Create an instance of {@link DeleteSensorResponsePropertyType }
     * 
     */
    public DeleteSensorResponsePropertyType createDeleteSensorResponsePropertyType() {
        return new DeleteSensorResponsePropertyType();
    }

    /**
     * Create an instance of {@link SensorDescriptionUpdatedType.ValidTime }
     * 
     */
    public SensorDescriptionUpdatedType.ValidTime createSensorDescriptionUpdatedTypeValidTime() {
        return new SensorDescriptionUpdatedType.ValidTime();
    }

    /**
     * Create an instance of {@link ExtensibleRequestPropertyType }
     * 
     */
    public ExtensibleRequestPropertyType createExtensibleRequestPropertyType() {
        return new ExtensibleRequestPropertyType();
    }

    /**
     * Create an instance of {@link AbstractContentsPropertyType }
     * 
     */
    public AbstractContentsPropertyType createAbstractContentsPropertyType() {
        return new AbstractContentsPropertyType();
    }

    /**
     * Create an instance of {@link FeatureRelationshipType }
     * 
     */
    public FeatureRelationshipType createFeatureRelationshipType() {
        return new FeatureRelationshipType();
    }

    /**
     * Create an instance of {@link AbstractContentsType.RelatedFeature }
     * 
     */
    public AbstractContentsType.RelatedFeature createAbstractContentsTypeRelatedFeature() {
        return new AbstractContentsType.RelatedFeature();
    }

    /**
     * Create an instance of {@link DescribeSensorType.ValidTime }
     * 
     */
    public DescribeSensorType.ValidTime createDescribeSensorTypeValidTime() {
        return new DescribeSensorType.ValidTime();
    }

    /**
     * Create an instance of {@link InsertSensorResponsePropertyType }
     * 
     */
    public InsertSensorResponsePropertyType createInsertSensorResponsePropertyType() {
        return new InsertSensorResponsePropertyType();
    }

    /**
     * Create an instance of {@link ExtensibleResponsePropertyType }
     * 
     */
    public ExtensibleResponsePropertyType createExtensibleResponsePropertyType() {
        return new ExtensibleResponsePropertyType();
    }

    /**
     * Create an instance of {@link InsertSensorType }
     * 
     */
    public InsertSensorType createInsertSensorType() {
        return new InsertSensorType();
    }

    /**
     * Create an instance of {@link AbstractContentsType.Offering }
     * 
     */
    public AbstractContentsType.Offering createAbstractContentsTypeOffering() {
        return new AbstractContentsType.Offering();
    }

    /**
     * Create an instance of {@link NotificationProducerMetadataType.ProducerEndpoint }
     * 
     */
    public NotificationProducerMetadataType.ProducerEndpoint createNotificationProducerMetadataTypeProducerEndpoint() {
        return new NotificationProducerMetadataType.ProducerEndpoint();
    }

    /**
     * Create an instance of {@link SensorDescriptionType }
     * 
     */
    public SensorDescriptionType createSensorDescriptionType() {
        return new SensorDescriptionType();
    }

    /**
     * Create an instance of {@link AbstractOfferingPropertyType }
     * 
     */
    public AbstractOfferingPropertyType createAbstractOfferingPropertyType() {
        return new AbstractOfferingPropertyType();
    }

    /**
     * Create an instance of {@link SWESEventPropertyType }
     * 
     */
    public SWESEventPropertyType createSWESEventPropertyType() {
        return new SWESEventPropertyType();
    }

    /**
     * Create an instance of {@link DescribeSensorResponseType }
     * 
     */
    public DescribeSensorResponseType createDescribeSensorResponseType() {
        return new DescribeSensorResponseType();
    }

    /**
     * Create an instance of {@link NotificationBrokerMetadataType }
     * 
     */
    public NotificationBrokerMetadataType createNotificationBrokerMetadataType() {
        return new NotificationBrokerMetadataType();
    }

    /**
     * Create an instance of {@link SensorDescriptionType.Data }
     * 
     */
    public SensorDescriptionType.Data createSensorDescriptionTypeData() {
        return new SensorDescriptionType.Data();
    }

    /**
     * Create an instance of {@link AbstractOfferingType.RelatedFeature }
     * 
     */
    public AbstractOfferingType.RelatedFeature createAbstractOfferingTypeRelatedFeature() {
        return new AbstractOfferingType.RelatedFeature();
    }

    /**
     * Create an instance of {@link AbstractSWESPropertyType }
     * 
     */
    public AbstractSWESPropertyType createAbstractSWESPropertyType() {
        return new AbstractSWESPropertyType();
    }

    /**
     * Create an instance of {@link DescribeSensorResponsePropertyType }
     * 
     */
    public DescribeSensorResponsePropertyType createDescribeSensorResponsePropertyType() {
        return new DescribeSensorResponsePropertyType();
    }

    /**
     * Create an instance of {@link SensorChangedType }
     * 
     */
    public SensorChangedType createSensorChangedType() {
        return new SensorChangedType();
    }

    /**
     * Create an instance of {@link UpdateSensorDescriptionPropertyType }
     * 
     */
    public UpdateSensorDescriptionPropertyType createUpdateSensorDescriptionPropertyType() {
        return new UpdateSensorDescriptionPropertyType();
    }

    /**
     * Create an instance of {@link SensorDescriptionType.ValidTime }
     * 
     */
    public SensorDescriptionType.ValidTime createSensorDescriptionTypeValidTime() {
        return new SensorDescriptionType.ValidTime();
    }

    /**
     * Create an instance of {@link OfferingChangedPropertyType }
     * 
     */
    public OfferingChangedPropertyType createOfferingChangedPropertyType() {
        return new OfferingChangedPropertyType();
    }

    /**
     * Create an instance of {@link NotificationProducerMetadataType.ServedTopics }
     * 
     */
    public NotificationProducerMetadataType.ServedTopics createNotificationProducerMetadataTypeServedTopics() {
        return new NotificationProducerMetadataType.ServedTopics();
    }

    /**
     * Create an instance of {@link DeleteSensorPropertyType }
     * 
     */
    public DeleteSensorPropertyType createDeleteSensorPropertyType() {
        return new DeleteSensorPropertyType();
    }

    /**
     * Create an instance of {@link OfferingChangedType }
     * 
     */
    public OfferingChangedType createOfferingChangedType() {
        return new OfferingChangedType();
    }

    /**
     * Create an instance of {@link SensorDescriptionUpdatedType }
     * 
     */
    public SensorDescriptionUpdatedType createSensorDescriptionUpdatedType() {
        return new SensorDescriptionUpdatedType();
    }

    /**
     * Create an instance of {@link SWESEventType.Service }
     * 
     */
    public SWESEventType.Service createSWESEventTypeService() {
        return new SWESEventType.Service();
    }

    /**
     * Create an instance of {@link DescribeSensorType }
     * 
     */
    public DescribeSensorType createDescribeSensorType() {
        return new DescribeSensorType();
    }

    /**
     * Create an instance of {@link DeleteSensorResponseType }
     * 
     */
    public DeleteSensorResponseType createDeleteSensorResponseType() {
        return new DeleteSensorResponseType();
    }

    /**
     * Create an instance of {@link InsertSensorPropertyType }
     * 
     */
    public InsertSensorPropertyType createInsertSensorPropertyType() {
        return new InsertSensorPropertyType();
    }

    /**
     * Create an instance of {@link FilterDialectMetadataType }
     * 
     */
    public FilterDialectMetadataType createFilterDialectMetadataType() {
        return new FilterDialectMetadataType();
    }

    /**
     * Create an instance of {@link FilterDialectMetadataPropertyType }
     * 
     */
    public FilterDialectMetadataPropertyType createFilterDialectMetadataPropertyType() {
        return new FilterDialectMetadataPropertyType();
    }

    /**
     * Create an instance of {@link InsertSensorType.ProcedureDescription }
     * 
     */
    public ProcedureDescription createProcedureDescription() {
        return new ProcedureDescription();
    }

    /**
     * Create an instance of {@link UpdateSensorDescriptionResponseType }
     * 
     */
    public UpdateSensorDescriptionResponseType createUpdateSensorDescriptionResponseType() {
        return new UpdateSensorDescriptionResponseType();
    }

    /**
     * Create an instance of {@link UpdateSensorDescriptionResponsePropertyType }
     * 
     */
    public UpdateSensorDescriptionResponsePropertyType createUpdateSensorDescriptionResponsePropertyType() {
        return new UpdateSensorDescriptionResponsePropertyType();
    }

    /**
     * Create an instance of {@link DeleteSensorType }
     * 
     */
    public DeleteSensorType createDeleteSensorType() {
        return new DeleteSensorType();
    }

    /**
     * Create an instance of {@link InsertionMetadataPropertyType }
     * 
     */
    public InsertionMetadataPropertyType createInsertionMetadataPropertyType() {
        return new InsertionMetadataPropertyType();
    }

    /**
     * Create an instance of {@link UpdateSensorDescriptionType.Description }
     * 
     */
    public UpdateSensorDescriptionType.Description createUpdateSensorDescriptionTypeDescription() {
        return new UpdateSensorDescriptionType.Description();
    }

    /**
     * Create an instance of {@link SWESEventType }
     * 
     */
    public SWESEventType createSWESEventType() {
        return new SWESEventType();
    }

    /**
     * Create an instance of {@link SensorDescriptionPropertyType }
     * 
     */
    public SensorDescriptionPropertyType createSensorDescriptionPropertyType() {
        return new SensorDescriptionPropertyType();
    }

    /**
     * Create an instance of {@link NotificationProducerMetadataPropertyType }
     * 
     */
    public NotificationProducerMetadataPropertyType createNotificationProducerMetadataPropertyType() {
        return new NotificationProducerMetadataPropertyType();
    }

    /**
     * Create an instance of {@link DescribeSensorPropertyType }
     * 
     */
    public DescribeSensorPropertyType createDescribeSensorPropertyType() {
        return new DescribeSensorPropertyType();
    }

    /**
     * Create an instance of {@link DescribeSensorResponseType.Description }
     * 
     */
    public DescribeSensorResponseType.Description createDescribeSensorResponseTypeDescription() {
        return new DescribeSensorResponseType.Description();
    }

    /**
     * Create an instance of {@link InsertSensorResponseType }
     * 
     */
    public InsertSensorResponseType createInsertSensorResponseType() {
        return new InsertSensorResponseType();
    }

    /**
     * Create an instance of {@link NotificationProducerMetadataType }
     * 
     */
    public NotificationProducerMetadataType createNotificationProducerMetadataType() {
        return new NotificationProducerMetadataType();
    }

    /**
     * Create an instance of {@link SensorDescriptionUpdatedPropertyType }
     * 
     */
    public SensorDescriptionUpdatedPropertyType createSensorDescriptionUpdatedPropertyType() {
        return new SensorDescriptionUpdatedPropertyType();
    }

    /**
     * Create an instance of {@link InsertSensorType.RelatedFeature }
     * 
     */
    public InsertSensorType.RelatedFeature createInsertSensorTypeRelatedFeature() {
        return new InsertSensorType.RelatedFeature();
    }

    /**
     * Create an instance of {@link InsertSensorType.Metadata }
     * 
     */
    public InsertSensorType.Metadata createInsertSensorTypeMetadata() {
        return new InsertSensorType.Metadata();
    }

    /**
     * Create an instance of {@link NotificationBrokerMetadataPropertyType }
     * 
     */
    public NotificationBrokerMetadataPropertyType createNotificationBrokerMetadataPropertyType() {
        return new NotificationBrokerMetadataPropertyType();
    }

    /**
     * Create an instance of {@link NotificationProducerMetadataType.SupportedDialects }
     * 
     */
    public NotificationProducerMetadataType.SupportedDialects createNotificationProducerMetadataTypeSupportedDialects() {
        return new NotificationProducerMetadataType.SupportedDialects();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificationProducerMetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "NotificationProducerMetadata", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "AbstractSWES")
    public JAXBElement<NotificationProducerMetadataType> createNotificationProducerMetadata(NotificationProducerMetadataType value) {
        return new JAXBElement<NotificationProducerMetadataType>(_NotificationProducerMetadata_QNAME, NotificationProducerMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertionMetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "InsertionMetadata")
    public JAXBElement<InsertionMetadataType> createInsertionMetadata(InsertionMetadataType value) {
        return new JAXBElement<InsertionMetadataType>(_InsertionMetadata_QNAME, InsertionMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SensorChangedType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "SensorChanged", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "SWESEvent")
    public JAXBElement<SensorChangedType> createSensorChanged(SensorChangedType value) {
        return new JAXBElement<SensorChangedType>(_SensorChanged_QNAME, SensorChangedType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificationBrokerMetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "NotificationBrokerMetadata", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "NotificationProducerMetadata")
    public JAXBElement<NotificationBrokerMetadataType> createNotificationBrokerMetadata(NotificationBrokerMetadataType value) {
        return new JAXBElement<NotificationBrokerMetadataType>(_NotificationBrokerMetadata_QNAME, NotificationBrokerMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SensorDescriptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "SensorDescription")
    public JAXBElement<SensorDescriptionType> createSensorDescription(SensorDescriptionType value) {
        return new JAXBElement<SensorDescriptionType>(_SensorDescription_QNAME, SensorDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensibleResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "ExtensibleResponse")
    public JAXBElement<ExtensibleResponseType> createExtensibleResponse(ExtensibleResponseType value) {
        return new JAXBElement<ExtensibleResponseType>(_ExtensibleResponse_QNAME, ExtensibleResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureRelationshipType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "FeatureRelationship")
    public JAXBElement<FeatureRelationshipType> createFeatureRelationship(FeatureRelationshipType value) {
        return new JAXBElement<FeatureRelationshipType>(_FeatureRelationship_QNAME, FeatureRelationshipType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractContentsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "AbstractContents", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "AbstractSWES")
    public JAXBElement<AbstractContentsType> createAbstractContents(AbstractContentsType value) {
        return new JAXBElement<AbstractContentsType>(_AbstractContents_QNAME, AbstractContentsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensibleRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "ExtensibleRequest")
    public JAXBElement<ExtensibleRequestType> createExtensibleRequest(ExtensibleRequestType value) {
        return new JAXBElement<ExtensibleRequestType>(_ExtensibleRequest_QNAME, ExtensibleRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateSensorDescriptionResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "UpdateSensorDescriptionResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<UpdateSensorDescriptionResponseType> createUpdateSensorDescriptionResponse(UpdateSensorDescriptionResponseType value) {
        return new JAXBElement<UpdateSensorDescriptionResponseType>(_UpdateSensorDescriptionResponse_QNAME, UpdateSensorDescriptionResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertSensorResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "InsertSensorResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<InsertSensorResponseType> createInsertSensorResponse(InsertSensorResponseType value) {
        return new JAXBElement<InsertSensorResponseType>(_InsertSensorResponse_QNAME, InsertSensorResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteSensorResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "DeleteSensorResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<DeleteSensorResponseType> createDeleteSensorResponse(DeleteSensorResponseType value) {
        return new JAXBElement<DeleteSensorResponseType>(_DeleteSensorResponse_QNAME, DeleteSensorResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertSensorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "InsertSensor", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<InsertSensorType> createInsertSensor(InsertSensorType value) {
        return new JAXBElement<InsertSensorType>(_InsertSensor_QNAME, InsertSensorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OfferingChangedType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "OfferingChanged", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "SWESEvent")
    public JAXBElement<OfferingChangedType> createOfferingChanged(OfferingChangedType value) {
        return new JAXBElement<OfferingChangedType>(_OfferingChanged_QNAME, OfferingChangedType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteSensorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "DeleteSensor", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<DeleteSensorType> createDeleteSensor(DeleteSensorType value) {
        return new JAXBElement<DeleteSensorType>(_DeleteSensor_QNAME, DeleteSensorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSWESType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "AbstractSWES")
    public JAXBElement<AbstractSWESType> createAbstractSWES(AbstractSWESType value) {
        return new JAXBElement<AbstractSWESType>(_AbstractSWES_QNAME, AbstractSWESType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FilterDialectMetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "FilterDialectMetadata", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "AbstractSWES")
    public JAXBElement<FilterDialectMetadataType> createFilterDialectMetadata(FilterDialectMetadataType value) {
        return new JAXBElement<FilterDialectMetadataType>(_FilterDialectMetadata_QNAME, FilterDialectMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateSensorDescriptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "UpdateSensorDescription", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<UpdateSensorDescriptionType> createUpdateSensorDescription(UpdateSensorDescriptionType value) {
        return new JAXBElement<UpdateSensorDescriptionType>(_UpdateSensorDescription_QNAME, UpdateSensorDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeSensorResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "DescribeSensorResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<DescribeSensorResponseType> createDescribeSensorResponse(DescribeSensorResponseType value) {
        return new JAXBElement<DescribeSensorResponseType>(_DescribeSensorResponse_QNAME, DescribeSensorResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SWESEventType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "SWESEvent", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "AbstractSWES")
    public JAXBElement<SWESEventType> createSWESEvent(SWESEventType value) {
        return new JAXBElement<SWESEventType>(_SWESEvent_QNAME, SWESEventType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SensorDescriptionUpdatedType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "SensorDescriptionUpdated", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "SensorChanged")
    public JAXBElement<SensorDescriptionUpdatedType> createSensorDescriptionUpdated(SensorDescriptionUpdatedType value) {
        return new JAXBElement<SensorDescriptionUpdatedType>(_SensorDescriptionUpdated_QNAME, SensorDescriptionUpdatedType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeSensorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "DescribeSensor", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<DescribeSensorType> createDescribeSensor(DescribeSensorType value) {
        return new JAXBElement<DescribeSensorType>(_DescribeSensor_QNAME, DescribeSensorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractOfferingType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swes/2.0", name = "AbstractOffering", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "AbstractSWES")
    public JAXBElement<AbstractOfferingType> createAbstractOffering(AbstractOfferingType value) {
        return new JAXBElement<AbstractOfferingType>(_AbstractOffering_QNAME, AbstractOfferingType.class, null, value);
    }

}
