/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.metadata.fra;

import java.util.Collection;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.DataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;


/**
 * AFNOR extension to ISO {@link DataIdentification}.
 * The following schema fragment specifies the expected content contained within this class.
 *
 * {@preformat xml
 *   <complexType name="FRA_DataIdentification_Type">
 *     <complexContent>
 *       <extension base="{http://www.isotc211.org/2005/gmd}MD_DataIdentification_Type">
 *         <sequence>
 *           <element name="relatedCitation" type="{http://www.isotc211.org/2005/gmd}CI_Citation_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         </sequence>
 *       </extension>
 *     </complexContent>
 *   </complexType>
 * }
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@XmlType(name = "FRA_DataIdentification_Type")
@XmlRootElement(name ="FRA_DataIdentification")
public class FRA_DataIdentification extends DefaultDataIdentification {
    /**
     * For serialization purpose.
     */
    private static final long serialVersionUID = 2491310165988749063L;

    /**
     * The documents at the origin of the creation of the identified resources.
     */
    private Collection<Citation> relatedCitations;

    /**
     * Constructs an initially empty data identification.
     */
    public FRA_DataIdentification() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public FRA_DataIdentification(final DataIdentification source) {
        super(source);
    }

    /**
     * Returns the documents at the origin of the creation of the identified resources.
     *
     * @return Citations to the current documents.
     */
    @XmlElement(name = "relatedCitation")
    public synchronized Collection<Citation> getRelatedCitations() {
        return xmlOptional(relatedCitations = nonNullCollection(relatedCitations, Citation.class));
    }

    /**
     * Sets the documents at the origin of the creation of the identified resources.
     *
     * @param newValues Citation to the new documents.
     */
    public synchronized void setRelatedCitations(final Collection<? extends Citation> newValues) {
        relatedCitations = copyCollection(newValues, relatedCitations, Citation.class);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code true}, since the marshalling
     * process is going to be done. This method is automatically called by JAXB
     * when the marshalling begins.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
        xmlMarshalling(true);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code false}, since the marshalling
     * process is finished. This method is automatically called by JAXB when the
     * marshalling ends.
     *
     * @param marshaller Not used in this implementation
     */
    @SuppressWarnings("unused")
    private void afterMarshal(Marshaller marshaller) {
        xmlMarshalling(false);
    }
}
