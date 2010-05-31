


package org.geotoolkit.service;

import org.opengis.service.DCPList;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.text.StringAdapter;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.service.OperationMetadata;
import org.opengis.service.Parameter;


/**
 * <p>Java class for SV_OperationMetadata_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_OperationMetadata_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="operationName" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="DCP" type="{http://www.isotc211.org/2005/srv}DCPList_PropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="operationDescription" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="invocationName" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="parameters" type="{http://www.isotc211.org/2005/srv}SV_Parameter_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="connectPoint" type="{http://www.isotc211.org/2005/gmd}CI_OnlineResource_PropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="dependsOn" type="{http://www.isotc211.org/2005/srv}SV_OperationMetadata_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
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
    "operationName",
    "DCP",
    "operationDescription",
    "invocationName",
    "parameters",
    "connectPoint",
    "dependsOn"
})
public class OperationMetadataImpl implements OperationMetadata {

   
    private String operationName;
    private Collection<DCPList> dcp;
    private String operationDescription;
    private String invocationName;
    private Collection<Parameter> parameters;
    private Collection<OnlineResource> connectPoint;
    private Collection<OperationMetadata> dependsOn;

    private boolean isUuidref;
    
    /**
     * An empty constrcutor used by JAXB
     */
    public OperationMetadataImpl() {
    }
    
    /**
     * Clone a OperationMetadata
     */
    public OperationMetadataImpl(OperationMetadata operation) {
        this.connectPoint         = operation.getConnectPoint();
        this.dcp                  = operation.getDCP();
        this.dependsOn            = operation.getDependsOn();
        this.invocationName       = operation.getInvocationName();
        this.operationDescription = operation.getOperationDescription();
        this.operationName        = operation.getOperationName();
        this.parameters           = operation.getParameters();
    }
    
    /**
     * Build a new Operation metadata
     */
    public OperationMetadataImpl(String operationName) {
        this.operationName = operationName;
    }
    
    /**
     * Gets the value of the operationName property.
     * 
     */
    @XmlJavaTypeAdapter(StringAdapter.class)
    @XmlElement(required = true)
    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets the value of the operationName property.
     * 
     */
    public void setOperationName(String value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the dcp property.
     * 
     * 
     */
    @XmlElement(name = "DCP", required = true)
    public Collection<DCPList> getDCP() {
        if (dcp == null) {
            dcp = new ArrayList<DCPList>();
        }
        return this.dcp;
    }
    
    public void setDCP(Collection<DCPList> dcp) {
         this.dcp = dcp;
    }
    
    public void setDCP(DCPList dcp) {
        if (this.dcp == null) {
            this.dcp = new ArrayList<DCPList>();
        } 
        this.dcp.add(dcp);
     }

    /**
     * Gets the value of the operationDescription property.
     * 
     */
    @XmlElement
    public String getOperationDescription() {
        return operationDescription;
    }

    /**
     * Sets the value of the operationDescription property.
     * 
     */
    public void setOperationDescription(String value) {
        this.operationDescription = value;
    }

    /**
     * Gets the value of the invocationName property.
     * 
     */
    @XmlElement
    public String getInvocationName() {
        return invocationName;
    }

    /**
     * Sets the value of the invocationName property.
     * 
     */
    public void setInvocationName(String value) {
        this.invocationName = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     */
    @XmlElement
    public Collection<Parameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<Parameter>();
        }
        return this.parameters;
    }
    
    public void setParameters(Collection<Parameter> parameters) {
         this.parameters = parameters;
    }
    
    public void setParameters(Parameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<Parameter>();
        }
         this.parameters.add(parameter);
     }

    /**
     * Gets the value of the connectPoint property.
     * 
     */
    @XmlElement(required = true)
    public Collection<OnlineResource> getConnectPoint() {
        if (connectPoint == null) {
            connectPoint = new ArrayList<OnlineResource>();
        }
        return this.connectPoint;
    }
    
    public void setConnectPoint(Collection<OnlineResource> connectPoint) {
         this.connectPoint = connectPoint;
    }
    
    public void setConnectPoint(OnlineResource connectPoint) {
        if (this.connectPoint == null) {
            this.connectPoint = new ArrayList<OnlineResource>();
        }
        this.connectPoint.add(connectPoint);
    }

    /**
     * Gets the value of the dependsOn property.
     */
    @XmlElement
    public Collection<OperationMetadata> getDependsOn() {
        if (dependsOn == null) {
            dependsOn = new ArrayList<OperationMetadata>();
        }
        return this.dependsOn;
    }
    
    public void setDependsOn(Collection<OperationMetadata> dependsOn) {
         this.dependsOn = dependsOn;
    }
    
    public void setDependsOn(OperationMetadata dependsOn) {
        if (this.dependsOn == null) {
            this.dependsOn = new ArrayList<OperationMetadata>();
        }
        this.dependsOn.add(dependsOn);
    }

    /**
     * @return the isUuidref
     */
    public boolean isUuidref() {
        return isUuidref;
    }

    /**
     * @param isUuidref the isUuidref to set
     */
    public void setIsUuidref(boolean isUuidref) {
        this.isUuidref = isUuidref;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[OperationMetadata]").append('\n');
        s.append("operationName: ").append(operationName);
        if (operationDescription != null)
            s.append("operationDescription: ").append(operationDescription).append('\n');
        if (invocationName != null)
            s.append("invocationName: ").append(invocationName).append('\n');
        if (dcp != null) {
            s.append("DCP:").append('\n');
            for (DCPList d:dcp) {
                s.append('\t').append(d).append('\n');
            }
        }
        if (dependsOn != null) {
            s.append("dependsOn:").append('\n');
            for (OperationMetadata d:dependsOn) {
                s.append('\t').append(d).append('\n');
            } 
        }
        if (connectPoint != null) {
            s.append("connectPoint:").append('\n');
            for (OnlineResource d:connectPoint) {
                s.append('\t').append(d).append('\n');
            } 
        }
        if (parameters != null) {
            s.append("parameters:").append('\n');
            for (Parameter d:parameters) {
                s.append('\t').append(d).append('\n');
            } 
        }
        return s.toString();
    }

}
