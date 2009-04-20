package org.geotoolkit.internal.jaxb.backend.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.internal.jaxb.backend.AbstractKeyword;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="vocabulary" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "Keyword")
public class Keyword implements AbstractKeyword {

    @XmlValue
    private String value;
    @XmlAttribute
    private String vocabulary;

   
     /**
     * An empty constructor used by JAXB.
     */
     Keyword() {
     }

     /**
     * Build a new Keyword object.
     */
    public Keyword(final String value) {
        this.value      = value;
    }
    
    /**
     * Build a new Keyword object.
     */
    public Keyword(final String value, final String vocabulary) {
        this.value      = value;
        this.vocabulary = vocabulary; 
    }
    
    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

   /**
    * Gets the value of the vocabulary property.
    */
    public String getVocabulary() {
        return vocabulary;
    }
}
