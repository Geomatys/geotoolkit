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
package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractCapabilities;
import org.geotoolkit.sml.xml.AbstractCharacteristics;
import org.geotoolkit.sml.xml.AbstractClassification;
import org.geotoolkit.sml.xml.AbstractContact;
import org.geotoolkit.sml.xml.AbstractDocumentation;
import org.geotoolkit.sml.xml.AbstractHistory;
import org.geotoolkit.sml.xml.AbstractIdentification;
import org.geotoolkit.sml.xml.AbstractKeywords;
import org.geotoolkit.sml.xml.AbstractLegalConstraint;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractValidTime;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractProcessType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0.1}AbstractSMLType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}metadataGroup" minOccurs="0"/>
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
    "securityConstraint",
    "legalConstraint",
    "characteristics",
    "capabilities",
    "contact",
    "documentation",
    "history"
})
@XmlSeeAlso({
    DataSourceType.class,
    AbstractRestrictedProcessType.class,
    AbstractDerivableProcessType.class,
    AbstractDerivableComponentType.class
})
public abstract class AbstractProcessType extends AbstractSMLType implements AbstractProcess {

    private List<Keywords> keywords;
    private List<Identification> identification;
    private List<Classification> classification;
    private ValidTime validTime;
    private SecurityConstraint securityConstraint;
    private List<LegalConstraint> legalConstraint;
    private List<Characteristics> characteristics;
    private List<Capabilities> capabilities;
    private List<Contact> contact;
    private List<Documentation> documentation;
    private List<History> history;

    public AbstractProcessType() {

    }

    public AbstractProcessType(final AbstractProcess pr) {
        super(pr);
        if (pr != null) {

            //capabilities
            this.capabilities = new ArrayList<Capabilities>();
            for (AbstractCapabilities oldCapa : pr.getCapabilities()) {
                this.capabilities.add(new Capabilities(oldCapa));
            }

            // characteristics
            this.characteristics = new ArrayList<Characteristics>();
            for (AbstractCharacteristics oldChar : pr.getCharacteristics()) {
                this.characteristics.add(new Characteristics(oldChar));
            }

            // Classification
            this.classification = new ArrayList<Classification>();
            for (AbstractClassification oldClass : pr.getClassification()) {
                this.classification.add(new Classification(oldClass));
            }

            // Contact
            this.contact = new ArrayList<Contact>();
            for (AbstractContact oldContact : pr.getContact()) {
                this.contact.add(new Contact(oldContact));
            }

            // Contact
            this.documentation = new ArrayList<Documentation>();
            for (AbstractDocumentation oldDoc : pr.getDocumentation()) {
                this.documentation.add(new Documentation(oldDoc));
            }

            // History
            this.history = new ArrayList<History>();
            for (AbstractHistory oldhist : pr.getHistory()) {
                this.history.add(new History(oldhist));
            }

            // Identification
            this.identification = new ArrayList<Identification>();
            for (AbstractIdentification oldIdent : pr.getIdentification()) {
                this.identification.add(new Identification(oldIdent));
            }


            // keywords
            this.keywords = new ArrayList<Keywords>();
            for (AbstractKeywords oldKeyw : pr.getKeywords()) {
                this.keywords.add(new Keywords(oldKeyw));
            }

            // legal constraint
            this.legalConstraint = new ArrayList<LegalConstraint>();
            for (AbstractLegalConstraint oldcons : pr.getLegalConstraint()) {
                this.legalConstraint.add(new LegalConstraint(oldcons));
            }

            // security constraint
            if (pr.getSecurityConstraint() != null) {
                this.securityConstraint = new SecurityConstraint(pr.getSecurityConstraint());
            }
            
            // validTime
            if (pr.getValidTime() != null) {
                this.validTime = new ValidTime(pr.getValidTime());
            }
        }

    }
    /**
     * Gets the value of the keywords property.
     * 
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
    public void setKeywords(final List<Keywords> keywords) {
        this.keywords = keywords;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setKeywords(final Keywords keywords) {
        if (keywords != null) {
            if (this.keywords == null) {
                this.keywords = new ArrayList<Keywords>();
            }
            this.keywords.add(keywords);
        }
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
    public void setIdentification(final List<Identification> identification) {
        this.identification = identification;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setIdentification(final Identification identification) {
        if (identification != null) {
            if (this.identification == null) {
                this.identification = new ArrayList<Identification>();
            }
            this.identification.add(identification);
        }
    }

    /**
     * Gets the value of the classification property.
     * 
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
    public void setClassification(final List<Classification> classification) {
        this.classification = classification;
    }

    /**
     * Sets the value of the keywords property.
     *
     */
    public void setClassification(final Classification classification) {
        if (classification != null) {
            if (this.classification == null) {
                this.classification = new ArrayList<Classification>();
            }
            this.classification.add(classification);
        }
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
     * 
     */
    public void setValidTime(final AbstractValidTime value) {
        if (value != null) {
            this.validTime = new ValidTime(value);
        }
    }

    /**
     * Gets the value of the securityConstraint property.
     * 
     */
    public SecurityConstraint getSecurityConstraint() {
        return securityConstraint;
    }

    /**
     * Sets the value of the securityConstraint property.
     * 
     */
    public void setSecurityConstraint(final SecurityConstraint value) {
        this.securityConstraint = value;
    }

    /**
     * Gets the value of the legalConstraint property.
     *
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
    public void setLegalConstraint(final LegalConstraint legalConstraint) {
        if (legalConstraint != null) {
            if (this.legalConstraint == null) {
                this.legalConstraint = new ArrayList<LegalConstraint>();
            }
            this.legalConstraint.add(legalConstraint);
        }
    }

    /**
     * Gets the value of the legalConstraint property.
     *
     */
    public void setLegalConstraint(final List<LegalConstraint> legalConstraint) {
        this.legalConstraint = legalConstraint;
    }
    
    /**
     * Gets the value of the characteristics property.
     * 
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
    public void setCharacteristics(final List<Characteristics> characteristics) {
        this.characteristics = characteristics;
    }

    /**
     * Sets the value of the characteristics property.
     *
     */
    public void setCharacteristics(final Characteristics characteristics) {
        if (characteristics != null) {
            if (this.characteristics == null) {
                this.characteristics = new ArrayList<Characteristics>();
            }
            this.characteristics.add(characteristics);
        }
    }
    
    /**
     * Gets the value of the capabilities property.
     * 
     */
    public List<Capabilities> getCapabilities() {
        if (capabilities == null) {
            capabilities = new ArrayList<Capabilities>();
        }
        return this.capabilities;
    }

    /**
     * Gets the value of the capabilities property.
     *
     */
    public void setCapabilities(final List<Capabilities> capabilities) {
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
    public void SetContact(final Contact contact) {
        if (contact != null) {
            if (this.contact == null) {
                this.contact = new ArrayList<Contact>();
            }
            this.contact.add(contact);
        }
    }

    /**
     * sets the value of the contact property.
     *
     */
    public void setContact(final List<Contact> contact) {
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
    public void setDocumention(final Documentation documentation) {
        if (documentation != null) {
            if (this.documentation == null) {
                this.documentation = new ArrayList<Documentation>();
            }
            this.documentation.add(documentation);
        }
    }

    /**
     * sets the value of the contact property.
     *
     */
    public void setDocumentation(final List<Documentation> documentation) {
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

    /**
     * Sets the value of the history property.
     */
    public void setHistory(final List<History> history) {
        this.history = history;
    }

    /**
     * Sets the value of the history property.
     */
    public void setHistory(final History history) {
        if (history != null) {
            if (this.history == null) {
                this.history = new ArrayList<History>();
            }
            this.history.add(history);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
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
            for (Capabilities k : capabilities) {
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
                   Utilities.equals(this.securityConstraint, that.securityConstraint)    &&
                   Utilities.equals(this.validTime,       that.validTime)          &&
                   Utilities.equals(this.history,         that.history);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        hash = 17 * hash + (this.identification != null ? this.identification.hashCode() : 0);
        hash = 17 * hash + (this.classification != null ? this.classification.hashCode() : 0);
        hash = 17 * hash + (this.validTime != null ? this.validTime.hashCode() : 0);
        hash = 17 * hash + (this.securityConstraint != null ? this.securityConstraint.hashCode() : 0);
        hash = 17 * hash + (this.legalConstraint != null ? this.legalConstraint.hashCode() : 0);
        hash = 17 * hash + (this.characteristics != null ? this.characteristics.hashCode() : 0);
        hash = 17 * hash + (this.capabilities != null ? this.capabilities.hashCode() : 0);
        hash = 17 * hash + (this.contact != null ? this.contact.hashCode() : 0);
        hash = 17 * hash + (this.documentation != null ? this.documentation.hashCode() : 0);
        hash = 17 * hash + (this.history != null ? this.history.hashCode() : 0);
        return hash;
    }
}
