

package org.geotoolkit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.Interface;
import org.opengis.service.Operation;
import org.opengis.service.Port;
import org.opengis.util.TypeName;


/**
 * <p>Java class for SV_Interface_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_Interface_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="typeName" type="{http://www.isotc211.org/2005/gco}TypeName_PropertyType"/>
 *         &lt;element name="theSV_Port" type="{http://www.isotc211.org/2005/srv}SV_Port_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="operation" type="{http://www.isotc211.org/2005/srv}SV_Operation_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlType(name = "SV_Interface_Type", propOrder = {
    "typeName",
    "theSVPort",
    "operation"
})
@XmlRootElement(name="SV_Interface")
public class InterfaceImpl implements Interface {

    private TypeName typeName;
    private Collection<Port> theSVPort;
    private Operation operation;

    /**
     * An empty constructor used by JAXB
     */
    public InterfaceImpl() {
        
    }
    
    /**
     * Clone an interface
     */
    public InterfaceImpl(final Interface interfac) {
        this.operation = interfac.getOperation();
        this.theSVPort = interfac.getTheSVPort();
        this.typeName  = interfac.getTypeName();
    }
    
    /**
     * Gets the value of the typeName property.
     */
    @XmlElement(required = true)
    public TypeName getTypeName() {
        return typeName;
    }

    /**
     * Sets the value of the typeName property.
     * 
    */
    public void setTypeName(final TypeName value) {
        this.typeName = value;
    }

    /**
     * Gets the value of the theSVPort property.
     * 
     */
    @XmlElement(name = "theSV_Port")
    public Collection<Port> getTheSVPort() {
        if (theSVPort == null) {
            theSVPort = new ArrayList<Port>();
        }
        return this.theSVPort;
    }
    
    public void setTheSVPort(final Collection<Port> theSVPort) {
         this.theSVPort = theSVPort;
     }
    
    public void setTheSVPort(final Port theSVPort) {
        if (this.theSVPort == null) {
            this.theSVPort = new ArrayList<Port>();
        } 
        this.theSVPort.add(theSVPort);
     }

    /**
     * Gets the value of the operation property.
     * 
     */
     @XmlElement(required = true)
    public Operation getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     */
    public void setOperation(final Operation value) {
        this.operation = value;
    }

}
