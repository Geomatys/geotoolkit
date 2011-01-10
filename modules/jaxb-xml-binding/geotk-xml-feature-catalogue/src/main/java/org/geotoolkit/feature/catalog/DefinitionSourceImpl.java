

package org.geotoolkit.feature.catalog;

import java.util.Iterator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;
import org.opengis.feature.catalog.DefinitionSource;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.ResponsibleParty;



/**
 * Class that specifies the source of a definition.
 * 
 * <p>Java class for FC_DefinitionSource_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC_DefinitionSource_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="source" type="{http://www.isotc211.org/2005/gmd}CI_Citation_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC_DefinitionSource_Type", propOrder = {
    "source"
})
public class DefinitionSourceImpl implements DefinitionSource, Referenceable {

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;
    
    @XmlElement(required = true)
    private Citation source;
    
    @XmlTransient
    private boolean isReference = false;

     /**
     * An empty constructor used by JAXB
     */
    public DefinitionSourceImpl() {
        
    }
    
    /**
     * Clone a DefinitionSource
     */
    public DefinitionSourceImpl(final DefinitionSource feature) {
        if (feature != null) {
            this.source = feature.getSource();
            this.id     = feature.getId();
        }
        
    }
    
     /**
     * build a new definition source
     */
    public DefinitionSourceImpl(final String id, final Citation source) {
        this.id     = id;    
        this.source = source;
    }
    
    /**
     * Gets the value of the source property.
    */
    public Citation getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     */
    public void setSource(final Citation value) {
        this.source = value;
    }
    
    public void setReference(final boolean isReference) {
        this.isReference = isReference;
    }
    
    public boolean isReference() {
        return isReference;
    }
    
    public DefinitionSourceImpl getReference() {
        DefinitionSourceImpl result = new DefinitionSourceImpl(this);
        result.setReference(true);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "[DefinitionSource]: id:" + id + '\n' + "source: " + source;  
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefinitionSourceImpl) {
            final DefinitionSourceImpl that = (DefinitionSourceImpl) object;
           
            
            
            //we redefine the Equals method of Citation
            boolean sourceb = Utilities.equals(this.source.getCollectiveTitle(),         that.source.getCollectiveTitle())         &&
                              Utilities.equals(this.source.getEdition(),                 that.source.getEdition())                 &&
                              Utilities.equals(this.source.getEditionDate(),             that.source.getEditionDate())             &&
                              Utilities.equals(this.source.getISBN(),                    that.source.getISBN())                    &&
                              Utilities.equals(this.source.getISSN(),                    that.source.getISSN())                    &&
                              Utilities.equals(this.source.getIdentifiers(),             that.source.getIdentifiers())             &&
                              Utilities.equals(this.source.getOtherCitationDetails(),    that.source.getOtherCitationDetails())    &&
                              Utilities.equals(this.source.getSeries(),                  that.source.getSeries())                  &&
                              Utilities.equals(this.source.getTitle(),                   that.source.getTitle());
            if (Utilities.equals(this.source.getDates().size(), that.source.getDates().size())) {
                Iterator<? extends CitationDate> thisIT = this.source.getDates().iterator();
                Iterator<? extends CitationDate> thatIT = that.source.getDates().iterator();
                
                while (thisIT.hasNext() && thatIT.hasNext()) {
                    if (!Utilities.equals(thisIT.next(), thatIT.next())) {
                        sourceb = false;
                    }
                }
            } else {
                sourceb = false;
            }
            
            if (Utilities.equals(this.source.getCitedResponsibleParties().size(), that.source.getCitedResponsibleParties().size())) {
                Iterator<? extends ResponsibleParty> thisIT = this.source.getCitedResponsibleParties().iterator();
                Iterator<? extends ResponsibleParty> thatIT = that.source.getCitedResponsibleParties().iterator();
                
                while (thisIT.hasNext() && thatIT.hasNext()) {
                    if (!Utilities.equals(thisIT.next(), thatIT.next())) {
                        sourceb = false;
                    }
                }
            } else {
                sourceb = false;
            }
            return Utilities.equals(this.id, that.id) && 
                   sourceb;
            
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.id     != null ? this.id.hashCode()     : 0);
        hash = 31 * hash + (this.source != null ? this.source.hashCode() : 0);
        return hash;
    }

}
