

package org.geotoolkit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.service.ServiceIdentification;
import org.opengis.service.ServiceProvider;

/**
 *
 * @author Guilhem Legal
 */
@XmlType(name = "SV_ServiceProvider_Type", propOrder = {
    "serviceContact",
    "providerName",
    "services"
})
@Deprecated
public class ServiceProviderImpl implements ServiceProvider {

    private Collection<ResponsibleParty> serviceContact;
    private String providerName;
    private Collection<ServiceIdentification> services;
    
    /**
     * An empty constrcutor used by JAXB 
     */
    public ServiceProviderImpl() {
        
    }
    
    /**
     * Clone a ServiceProvider. 
     */
    public ServiceProviderImpl(ServiceProvider provider) {
        this.providerName   = provider.getProviderName();
        this.serviceContact = provider.getServiceContact();
        this.services       = provider.getServices();
        
    }
    
    @XmlElement(required = true)
    public Collection<ResponsibleParty> getServiceContact() {
        if (serviceContact == null) {
            serviceContact = new ArrayList<ResponsibleParty>();
        }
        return serviceContact;
    }
    
    public void setServiceContact(Collection<ResponsibleParty> serviceContact) {
         this.serviceContact = serviceContact;
    }
    
    public void setServiceContact(ResponsibleParty serviceContact) {
        if (this.serviceContact == null) {
            this.serviceContact = new ArrayList<ResponsibleParty>();
        }
        this.serviceContact.add(serviceContact);
    }

    @XmlElement(required = true) 
    public String getProviderName() {
       return providerName;
    }

    @XmlElement
    public Collection<ServiceIdentification> getServices() {
        if (services == null) {
            services = new ArrayList<ServiceIdentification>();
        }
        return services;
    }
    
    public void setServices(Collection<ServiceIdentification> services) {
         this.services = services;
    }
    
    public void setServices(ServiceIdentification services) {
        if (this.services == null) {
            this.services = new ArrayList<ServiceIdentification>();
        }
        this.services.add(services);
    }

}
