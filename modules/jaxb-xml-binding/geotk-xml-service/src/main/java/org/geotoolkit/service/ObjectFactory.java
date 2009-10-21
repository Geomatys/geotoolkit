

package org.geotoolkit.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.ogc package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlRegistry
public class ObjectFactory {
    
    private final static QName _ServiceIdentification_QNAME = new QName("http://www.isotc211.org/2005/srv", "SV_ServiceIdentification");
    
    /**
     * Create an instance of {@link ExistenceCapabilitiesType }
     * 
     */
    public ServiceIdentificationImpl creatServiceIdentificationImpl() {
        return new ServiceIdentificationImpl();
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnaryLogicOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.isotc211.org/2005/srv", name = "SV_ServiceIdentification", substitutionHeadNamespace = "http://www.isotc211.org/2005/srv")
    public JAXBElement<ServiceIdentificationImpl> createServiceIdentification(ServiceIdentificationImpl value) {
        return new JAXBElement<ServiceIdentificationImpl>(_ServiceIdentification_QNAME, ServiceIdentificationImpl.class, null, value);
    }

}
