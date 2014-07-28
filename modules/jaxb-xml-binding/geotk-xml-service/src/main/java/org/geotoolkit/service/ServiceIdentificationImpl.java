/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.service;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.iso.identification.AbstractIdentification;
import org.apache.sis.internal.jaxb.code.SV_CouplingType;
import org.apache.sis.internal.jaxb.gco.StringAdapter;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.distribution.StandardOrderProcess;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.service.CouplingType;
import org.opengis.metadata.service.OperationMetadata;
import org.opengis.service.CoupledResource;
import org.opengis.service.ServiceIdentification;
import org.opengis.service.ServiceProvider;
import org.opengis.util.GenericName;


/**
 * <p>Java class for SV_ServiceIdentification_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SV_ServiceIdentification_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gmd}AbstractMD_Identification_Type">
 *       &lt;sequence>
 *         &lt;element name="serviceType" type="{http://www.isotc211.org/2005/gco}GenericName_PropertyType"/>
 *         &lt;element name="serviceTypeVersion" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="accessProperties" type="{http://www.isotc211.org/2005/gmd}MD_StandardOrderProcess_PropertyType" minOccurs="0"/>
 *         &lt;element name="restrictions" type="{http://www.isotc211.org/2005/gmd}MD_Constraints_PropertyType" minOccurs="0"/>
 *         &lt;element name="keywords" type="{http://www.isotc211.org/2005/gmd}MD_Keywords_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="extent" type="{http://www.isotc211.org/2005/gmd}EX_Extent_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="coupledResource" type="{http://www.isotc211.org/2005/srv}SV_CoupledResource_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="couplingType" type="{http://www.isotc211.org/2005/srv}SV_CouplingType_PropertyType"/>
 *         &lt;element name="containsOperations" type="{http://www.isotc211.org/2005/srv}SV_OperationMetadata_PropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="operatesOn" type="{http://www.isotc211.org/2005/gmd}MD_DataIdentification_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlType(propOrder = {
    "serviceType",
    "serviceTypeVersion",
    "accessProperties",
    "restrictions",
    "extents",
    "coupledResource",
    "couplingType",
    "containsOperations",
    "operatesOn",
    "provider"
})
@XmlRootElement(name = "SV_ServiceIdentification")
public class ServiceIdentificationImpl extends AbstractIdentification implements ServiceIdentification {

    private GenericName serviceType;
    private Collection<String> serviceTypeVersion;
    private StandardOrderProcess accessProperties;
    private Constraints restrictions;
    private Collection<CoupledResource> coupledResource;
    private CouplingType couplingType;
    private Collection<OperationMetadata> containsOperations;
    private Collection<DataIdentification> operatesOn;
    private Collection<ServiceProvider> provider;

    /**
     * An empty constructor used by JAXB
     */
    public ServiceIdentificationImpl() {

    }

    /**
     * Clone a ServiceIdentification
     */
    public ServiceIdentificationImpl(final ServiceIdentification service) {
        this.containsOperations = service.getContainsOperations();
        this.serviceTypeVersion = service.getServiceTypeVersion();
        this.accessProperties   = service.getAccessProperties();
        this.coupledResource    = service.getCoupledResource();
        this.couplingType       = service.getCouplingType();
        this.operatesOn         = service.getOperatesOn();
        this.provider           = service.getProvider();
        this.restrictions       = service.getRestrictions();
        this.serviceType        = service.getServiceType();
    }

    /**
     * Build a new Service identification
     */
    public ServiceIdentificationImpl(final Collection<OperationMetadata> operations, final GenericName serviceType, final CouplingType couplingType) {
        this.containsOperations = operations;
        this.serviceType        = serviceType;
        this.couplingType       = couplingType;
    }

    /**
     * Gets the value of the serviceType property.
     *
     */
    @XmlElement(required = true, namespace= "http://www.isotc211.org/2005/srv")
    public synchronized GenericName getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     *
     */
    public synchronized void setServiceType(final GenericName value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the serviceTypeVersion property.
     *
    */
    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement
    public synchronized Collection<String> getServiceTypeVersion() {
        return serviceTypeVersion = nonNullCollection(serviceTypeVersion, String.class);
    }

     public synchronized void setServiceTypeVersion(final Collection<? extends String> serviceTypeVersion) {
         this.serviceTypeVersion = writeCollection(serviceTypeVersion, this.serviceTypeVersion, String.class);
     }
    /**
     * Gets the value of the accessProperties property.
     *
     */
    @XmlElement
    public synchronized StandardOrderProcess getAccessProperties() {
        return accessProperties;
    }

    /**
     * Sets the value of the accessProperties property.
    */
    public synchronized void setAccessProperties(final StandardOrderProcess value) {
        this.accessProperties = value;
    }

    /**
     * Gets the value of the restrictions property.
     *
     */
    @XmlElement
    public synchronized Constraints getRestrictions() {
        return restrictions;
    }

    /**
     * Sets the value of the restrictions property.
     *
    */
    public synchronized void setRestrictions(final Constraints value) {
        this.restrictions = value;
    }

    /**
     * Gets the value of the extent property.
     *
     */
    @XmlElement(name = "extent")
    public synchronized Collection<Extent> getExtents() {
        return super.getExtents();
    }

    public synchronized void setExtents(Collection<? extends Extent> extent) {
        super.setExtents(extent);
    }

    /**
     * Gets the value of the coupledResource property.
     *
     */
    @XmlElement
    public synchronized Collection<CoupledResource> getCoupledResource() {
        return coupledResource = nonNullCollection(coupledResource, CoupledResource.class);
    }

    public synchronized void setCoupledResource(final Collection<? extends CoupledResource> coupledResource) {
        this.coupledResource = writeCollection(coupledResource, this.coupledResource, CoupledResource.class);
    }

    /**
     * Gets the value of the couplingType property.
     *
     */
    @XmlJavaTypeAdapter(SV_CouplingType.class)
    @XmlElement(required = true)
    public synchronized CouplingType getCouplingType() {
        return couplingType;
    }

    /**
     * Sets the value of the couplingType property.
     *
    */
    public synchronized void setCouplingType(final CouplingType value) {
        this.couplingType = value;
    }

    /**
     * Gets the value of the containsOperations property.
     *
     */
    @XmlElement(required = true)
    public synchronized Collection<OperationMetadata> getContainsOperations() {
        return containsOperations = nonNullCollection(containsOperations, OperationMetadata.class);
    }

    public synchronized void setContainsOperations(final Collection<? extends OperationMetadata> containsOperations) {
        this.containsOperations = writeCollection(containsOperations, this.containsOperations, OperationMetadata.class);
    }


    /**
     * Gets the value of the operatesOn property.
     *
     */
    @XmlElement
    public synchronized Collection<DataIdentification> getOperatesOn() {
        return operatesOn = nonNullCollection(operatesOn, DataIdentification.class);
    }

    public void setOperatesOn(final Collection<? extends DataIdentification> operatesOn) {
        this.operatesOn = writeCollection(operatesOn, this.operatesOn, DataIdentification.class);
    }


    @Deprecated
    @XmlElement
    public synchronized Collection<ServiceProvider> getProvider() {
        return provider = nonNullCollection(provider, ServiceProvider.class);
    }

    @Deprecated
    public synchronized void setProvider(final Collection<? extends ServiceProvider> provider) {
        this.provider = writeCollection(provider, this.provider, ServiceProvider.class);
    }

    @Override
    public synchronized MetadataStandard getStandard() {
        return MetadataStandard.ISO_19119;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append("SV ServiceIdentification:").append('\n');
        String tab = "\t";
        s.append(tab).append("ServiceType:").append(serviceType).append('\n');
        if (serviceTypeVersion != null) {
            s.append(tab).append("ServiceTypeVersion:").append('\n');
            tab += '\t';
            for (String version: serviceTypeVersion) {
                s.append(tab).append(version).append('\n');
            }
            tab = tab.substring(0, tab.length() - 1);

        }
        if (accessProperties != null) {
            s.append(tab).append("accessProperties: ").append(accessProperties).append('\n');
        }
        if (restrictions != null) {
            s.append(tab).append("restrictions: ").append(restrictions).append('\n');
        }
        final Collection<Extent> extent = super.getExtents();
        if (extent != null) {
            s.append(tab).append("extent: ").append('\n').append(extent).append('\n');
        }
        if (coupledResource != null) {
            s.append(tab).append("coupledResource:").append('\n');
            tab += '\t';
            for (CoupledResource res: coupledResource) {
                s.append(tab).append(res).append('\n');
            }
            tab = tab.substring(0, tab.length() - 1);
        }
        s.append(tab).append("couplingType: ").append(couplingType).append('\n');
        if (containsOperations != null) {
            s.append(tab).append("containsOperation:").append('\n');
            tab += '\t';
            for (OperationMetadata om: containsOperations) {
                s.append(tab).append(om).append('\n');
            }
            tab = tab.substring(0, tab.length() - 1);
        }
        if (operatesOn != null) {
            s.append(tab).append("operatesOn:").append('\n');
            tab += '\t';
            for (DataIdentification di: operatesOn) {
                s.append(tab).append(di).append('\n');
            }
            tab = tab.substring(0, tab.length() - 1);
        }
        return s.toString();
    }

}
