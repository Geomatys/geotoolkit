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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractValidTime;
import org.geotoolkit.swe.xml.v100.DataRecordType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractProcessType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0}AbstractSMLType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0}metadataGroup" minOccurs="0"/>
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
@XmlType(name = "AbstractProcessType", propOrder = {
    "keywords",
    "identification",
    "classification",
    "validTime",
    //"securityConstraint",
    "legalConstraint",
    "characteristics",
    "capabilities",
    "contact",
    "documentation",
    "history"
})
@XmlSeeAlso({AbstractDerivableComponentType.class, DataSourceType.class, AbstractRestrictedProcessType.class}) 
public abstract class AbstractProcessType extends AbstractSMLType implements AbstractProcess {

    private List<Keywords> keywords;
    private List<Identification> identification;
    private List<Classification> classification;
    private ValidTime validTime;
    private List<LegalConstraint> legalConstraint;
    private List<Characteristics> characteristics;
    private List<CapabilitiesSML> capabilities;
    private List<Contact> contact;
    private List<Documentation> documentation;
    private List<History> history;

    /**
     * Gets the value of the keywords property.
     */
    public List<Keywords> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<Keywords>();
        }
        return this.keywords;
    }

     /**
     * Sets the value of the keywords property.
     *
     */
    public void setKeywords(List<Keywords> keywords) {
        if (keywords == null) {
            this.keywords = keywords;
        }
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setKeywords(Keywords keywords) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<Keywords>();
        }
        this.keywords.add(keywords);
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setKeywords(KeywordList keywords) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<Keywords>();
        }
        this.keywords.add(new Keywords(keywords));
    }

    /**
     * Gets the value of the identification property.
     * 
     */
    public List<Identification> getIdentification() {
        if (identification == null) {
            identification = new ArrayList<Identification>();
        }
        return this.identification;
    }

     /**
     * Sets the value of the keywords property.
     *
     */
    public void setIdentification(List<Identification> identification) {
        if (identification == null) {
            this.identification = identification;
        }
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setIdentification(Identification identification) {
        if (this.identification == null) {
            this.identification = new ArrayList<Identification>();
        }
        this.identification.add(identification);
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setIdentification(IdentifierList identification) {
        if (this.identification == null) {
            this.identification = new ArrayList<Identification>();
        }
        this.identification.add(new Identification(identification));
    }


    /**
     * Gets the value of the classification property.
     */
    public List<Classification> getClassification() {
        if (classification == null) {
            classification = new ArrayList<Classification>();
        }
        return this.classification;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setClassification(List<Classification> classification) {
        if (classification == null) {
            this.classification = classification;
        }
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setClassification(Classification classification) {
        if (this.classification == null) {
            this.classification = new ArrayList<Classification>();
        }
        this.classification.add(classification);
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setClassification(ClassifierList classification) {
        if (this.classification == null) {
            this.classification = new ArrayList<Classification>();
        }
        this.classification.add(new Classification(classification));
    }

    /**
     * Gets the value of the validTime property.
     * 
     */
    public ValidTime getValidTime() {
        return validTime;
    }

    /**
     * Sets the value of the validTime property.
     */
    public void setValidTime(AbstractValidTime value) {
        this.validTime = new ValidTime(value);
    }

    /**
     * Gets the value of the legalConstraint property.
     * 
     */
    public List<LegalConstraint> getLegalConstraint() {
        if (legalConstraint == null) {
            legalConstraint = new ArrayList<LegalConstraint>();
        }
        return this.legalConstraint;
    }

    /**
     * Gets the value of the legalConstraint property.
     *
     */
    public void setLegalConstraint(LegalConstraint legalConstraint) {
        if (this.legalConstraint == null) {
            this.legalConstraint = new ArrayList<LegalConstraint>();
        }
        this.legalConstraint.add(legalConstraint);
    }

    /**
     * Gets the value of the legalConstraint property.
     *
     */
    public void setLegalConstraint(Rights legalConstraint) {
        if (this.legalConstraint == null) {
            this.legalConstraint = new ArrayList<LegalConstraint>();
        }
        this.legalConstraint.add(new LegalConstraint(legalConstraint));
    }

    /**
     * Gets the value of the legalConstraint property.
     *
     */
    public void setLegalConstraint(List<LegalConstraint> legalConstraint) {
        this.legalConstraint = legalConstraint;
    }

    /**
     * Gets the value of the characteristics property.
     * 
     */
    public List<Characteristics> getCharacteristics() {
        if (characteristics == null) {
            characteristics = new ArrayList<Characteristics>();
        }
        return this.characteristics;
    }

    /**
     * Sets the value of the characteristics property.
     *
     */
    public void setCharacteristics(List<Characteristics> characteristics) {
        this.characteristics = characteristics;
    }

    /**
     * Sets the value of the characteristics property.
     *
     */
    public void setCharacteristics(Characteristics characteristics) {
        if (this.characteristics == null) {
            this.characteristics = new ArrayList<Characteristics>();
        }
        this.characteristics.add(characteristics);
    }

    /**
     * Sets the value of the characteristics property.
     *
     */
    public void setCharacteristics(DataRecordType characteristics) {
        if (this.characteristics == null) {
            this.characteristics = new ArrayList<Characteristics>();
        }
        this.characteristics.add(new Characteristics(characteristics));
    }

    /**
     * Gets the value of the capabilities property.
     * 
     */
    public List<CapabilitiesSML> getCapabilities() {
        if (capabilities == null) {
            capabilities = new ArrayList<CapabilitiesSML>();
        }
        return this.capabilities;
    }

    /**
     * Sets the value of the capabilities property.
     *
     */
    public void setCapabilities(CapabilitiesSML capabilties) {
        if (this.capabilities == null) {
            this.capabilities = new ArrayList<CapabilitiesSML>();
        }
        this.capabilities.add(capabilties);
    }

    /**
     * Sets the value of the capabilities property.
     *
     */
    public void setCapabilities(DataRecordType capabilties) {
        if (this.capabilities == null) {
            this.capabilities = new ArrayList<CapabilitiesSML>();
        }
        this.capabilities.add(new CapabilitiesSML(capabilties));
    }

    /**
     * Sets the value of the capabilities property.
     *
     */
    public void setCapabilities(List<CapabilitiesSML> capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * Gets the value of the contact property.
     * 
     */
    public List<Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Contact>();
        }
        return this.contact;
    }

    /**
     * Sets the value of the contact property.
     *
     */
    public void setContact(Contact contact) {
        if (this.contact == null) {
            this.contact = new ArrayList<Contact>();
        }
        this.contact.add(contact);
    }

    /**
     * Sets the value of the contact property.
     *
     */
    public void setContact(ResponsibleParty contact) {
        if (this.contact == null) {
            this.contact = new ArrayList<Contact>();
        }
        this.contact.add(new Contact(contact));
    }

    /**
     * sets the value of the contact property.
     *
     */
    public void setContact(List<Contact> contact) {
        this.contact = contact;
    }

    /**
     * Gets the value of the documentation property.
     * 
     */
    public List<Documentation> getDocumentation() {
        if (documentation == null) {
            documentation = new ArrayList<Documentation>();
        }
        return this.documentation;
    }

    /**
     * Sets the value of the contact property.
     *
     */
    public void setDocumentation(Documentation documentation) {
        if (this.documentation == null) {
            this.documentation = new ArrayList<Documentation>();
        }
        this.documentation.add(documentation);
    }

    /**
     * Sets the value of the contact property.
     *
     */
    public void setDocumentation(Document documentation) {
        if (this.documentation == null) {
            this.documentation = new ArrayList<Documentation>();
        }
        this.documentation.add(new Documentation(documentation));
    }

    /**
     * sets the value of the contact property.
     *
     */
    public void setDocumentation(List<Documentation> documentation) {
        this.documentation = documentation;
    }

    /**
     * Gets the value of the history property.
     * 
     */
    public List<History> getHistory() {
        if (history == null) {
            history = new ArrayList<History>();
        }
        return this.history;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[SensorML]").append("\n");
        if (keywords != null) {
            sb.append("Keywords:").append('\n');
            for (Keywords k : keywords) {
                sb.append(k).append('\n');
            }
        }
        if (identification != null) {
            sb.append("Identification:").append('\n');
            for (Identification k : identification) {
                sb.append(k).append('\n');
            }
        }
        if (classification != null) {
            sb.append("Identification:").append('\n');
            for (Classification k : classification) {
                sb.append(k).append('\n');
            }
        }
        if (validTime != null) {
            sb.append("validTime:").append(validTime).append('\n');
        }
        if (legalConstraint != null) {
            sb.append("legalConstraint:").append('\n');
            for (LegalConstraint k : legalConstraint) {
                sb.append(k).append('\n');
            }
        }
        if (characteristics != null) {
            sb.append("characteristics:").append('\n');
            for (Characteristics k : characteristics) {
                sb.append(k).append('\n');
            }
        }
        if (capabilities != null) {
            sb.append("capabilities:").append('\n');
            for (CapabilitiesSML k : capabilities) {
                sb.append(k).append('\n');
            }
        }
        if (contact != null) {
            sb.append("contact:").append('\n');
            for (Contact k : contact) {
                sb.append(k).append('\n');
            }
        }
        if (documentation != null) {
            sb.append("documentation:").append('\n');
            for (Documentation k : documentation) {
                sb.append(k).append('\n');
            }
        }
        if (history != null) {
            sb.append("history:").append('\n');
            for (History k : history) {
                sb.append(k).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractProcessType && super.equals(object)) {
            final AbstractProcessType that = (AbstractProcessType) object;
            return Utilities.equals(this.capabilities,    that.capabilities)       &&
                   Utilities.equals(this.characteristics, that.characteristics)    &&
                   Utilities.equals(this.classification,  that.classification)     &&
                   Utilities.equals(this.contact,         that.contact)            &&
                   Utilities.equals(this.documentation,   that.documentation)      &&
                   Utilities.equals(this.identification,  that.identification)     &&
                   Utilities.equals(this.keywords,        that.keywords)           &&
                   Utilities.equals(this.legalConstraint, that.legalConstraint)    &&
                   Utilities.equals(this.validTime,       that.validTime)          &&
                   Utilities.equals(this.history,         that.history);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        hash = 59 * hash + (this.identification != null ? this.identification.hashCode() : 0);
        hash = 59 * hash + (this.classification != null ? this.classification.hashCode() : 0);
        hash = 59 * hash + (this.validTime != null ? this.validTime.hashCode() : 0);
        hash = 59 * hash + (this.legalConstraint != null ? this.legalConstraint.hashCode() : 0);
        hash = 59 * hash + (this.characteristics != null ? this.characteristics.hashCode() : 0);
        hash = 59 * hash + (this.capabilities != null ? this.capabilities.hashCode() : 0);
        hash = 59 * hash + (this.contact != null ? this.contact.hashCode() : 0);
        hash = 59 * hash + (this.documentation != null ? this.documentation.hashCode() : 0);
        hash = 59 * hash + (this.history != null ? this.history.hashCode() : 0);
        return hash;
    }

}
