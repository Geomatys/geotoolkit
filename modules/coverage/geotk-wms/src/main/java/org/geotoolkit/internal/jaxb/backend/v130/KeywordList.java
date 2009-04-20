package org.geotoolkit.internal.jaxb.backend.v130;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractKeywordList;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wms}Keyword" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "keyword"
})
@XmlRootElement(name = "KeywordList")
public class KeywordList implements AbstractKeywordList {

    @XmlElement(name = "Keyword")
    private List<Keyword> keyword = new ArrayList<Keyword>();

     /**
     * An empty constructor used by JAXB.
     */
     KeywordList() {
     }

    /**
     * Build a new Keyword list.
     */
    public KeywordList(final List<Keyword> keyword) {
        this.keyword = keyword;
        
    }
    
    /**
     * Build a new Contact person primary object.
     */
    public KeywordList(final Keyword... keywords) {
        for (final Keyword element : keywords) {
            this.keyword.add(element);
        }
    }
    
    /**
     * Gets the value of the keyword property.
     */
    public List<Keyword> getKeyword() {
       return Collections.unmodifiableList(keyword);
    }

}
