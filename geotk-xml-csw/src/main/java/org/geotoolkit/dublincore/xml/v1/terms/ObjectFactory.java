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
package org.geotoolkit.dublincore.xml.v1.terms;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.dublincore.xml.v1.elements.SimpleLiteral;


/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the org.constellation.dublincore.v1.terms package.
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding of schema
 * type definitions, element declarations and model groups.
 * Factory methods for each of these are provided in this class.
 *
 * @module
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _IsPartOf_QNAME              = new QName("http://www.purl.org/dc/terms/", "isPartOf");
    private static final QName _Replaces_QNAME              = new QName("http://www.purl.org/dc/terms/", "replaces");
    private static final QName _Issued_QNAME                = new QName("http://www.purl.org/dc/terms/", "issued");
    private static final QName _Alternative_QNAME           = new QName("http://www.purl.org/dc/terms/", "alternative");
    private static final QName _Modified_QNAME              = new QName("http://www.purl.org/dc/terms/", "modified");
    private static final QName _Requires_QNAME              = new QName("http://www.purl.org/dc/terms/", "requires");
    private static final QName _HasFormat_QNAME             = new QName("http://www.purl.org/dc/terms/", "hasFormat");
    private static final QName _EducationLevel_QNAME        = new QName("http://www.purl.org/dc/terms/", "educationLevel");
    private static final QName _IsReplacedBy_QNAME          = new QName("http://www.purl.org/dc/terms/", "isReplacedBy");
    private static final QName _AccessRights_QNAME          = new QName("http://www.purl.org/dc/terms/", "accessRights");
    private static final QName _Temporal_QNAME              = new QName("http://www.purl.org/dc/terms/", "temporal");
    private static final QName _IsVersionOf_QNAME           = new QName("http://www.purl.org/dc/terms/", "isVersionOf");
    private static final QName _IsReferencedBy_QNAME        = new QName("http://www.purl.org/dc/terms/", "isReferencedBy");
    private static final QName _Abstract_QNAME              = new QName("http://www.purl.org/dc/terms/", "abstract");
    private static final QName _Audience_QNAME              = new QName("http://www.purl.org/dc/terms/", "audience");
    private static final QName _HasPart_QNAME               = new QName("http://www.purl.org/dc/terms/", "hasPart");
    private static final QName _Extent_QNAME                = new QName("http://www.purl.org/dc/terms/", "extent");
    private static final QName _BibliographicCitation_QNAME = new QName("http://www.purl.org/dc/terms/", "bibliographicCitation");
    private static final QName _Valid_QNAME                 = new QName("http://www.purl.org/dc/terms/", "valid");
    private static final QName _IsFormatOf_QNAME            = new QName("http://www.purl.org/dc/terms/", "isFormatOf");
    private static final QName _DateAccepted_QNAME          = new QName("http://www.purl.org/dc/terms/", "dateAccepted");
    private static final QName _Mediator_QNAME              = new QName("http://www.purl.org/dc/terms/", "mediator");
    private static final QName _HasVersion_QNAME            = new QName("http://www.purl.org/dc/terms/", "hasVersion");
    private static final QName _Available_QNAME             = new QName("http://www.purl.org/dc/terms/", "available");
    private static final QName _IsRequiredBy_QNAME          = new QName("http://www.purl.org/dc/terms/", "isRequiredBy");
    private static final QName _References_QNAME            = new QName("http://www.purl.org/dc/terms/", "references");
    private static final QName _Created_QNAME               = new QName("http://www.purl.org/dc/terms/", "created");
    private static final QName _DateCopyrighted_QNAME       = new QName("http://www.purl.org/dc/terms/", "dateCopyrighted");
    private static final QName _Spatial_QNAME               = new QName("http://www.purl.org/dc/terms/", "spatial");
    private static final QName _DateSubmitted_QNAME         = new QName("http://www.purl.org/dc/terms/", "dateSubmitted");
    private static final QName _Medium_QNAME                = new QName("http://www.purl.org/dc/terms/", "medium");
    private static final QName _ConformsTo_QNAME            = new QName("http://www.purl.org/dc/terms/", "conformsTo");
    private static final QName _TableOfContents_QNAME       = new QName("http://www.purl.org/dc/terms/", "tableOfContents");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.constellation.dublincore.v1.terms
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "isPartOf", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createIsPartOf(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_IsPartOf_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "replaces", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createReplaces(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Replaces_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "issued", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "date")
    public JAXBElement<SimpleLiteral> createIssued(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Issued_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "alternative", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "title")
    public JAXBElement<SimpleLiteral> createAlternative(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Alternative_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "modified", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "date")
    public JAXBElement<SimpleLiteral> createModified(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Modified_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "requires", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createRequires(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Requires_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "hasFormat", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createHasFormat(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_HasFormat_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "educationLevel", substitutionHeadNamespace = "http://www.purl.org/dc/terms/", substitutionHeadName = "audience")
    public JAXBElement<SimpleLiteral> createEducationLevel(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_EducationLevel_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "isReplacedBy", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createIsReplacedBy(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_IsReplacedBy_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "accessRights", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "rights")
    public JAXBElement<SimpleLiteral> createAccessRights(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_AccessRights_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "temporal", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "coverage")
    public JAXBElement<SimpleLiteral> createTemporal(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Temporal_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "isVersionOf", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createIsVersionOf(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_IsVersionOf_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "isReferencedBy", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createIsReferencedBy(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_IsReferencedBy_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "abstract", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "description")
    public JAXBElement<SimpleLiteral> createAbstract(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Abstract_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "audience", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "DC-element")
    public JAXBElement<SimpleLiteral> createAudience(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Audience_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "hasPart", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createHasPart(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_HasPart_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "extent", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "format")
    public JAXBElement<SimpleLiteral> createExtent(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Extent_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "bibliographicCitation", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "identifier")
    public JAXBElement<SimpleLiteral> createBibliographicCitation(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_BibliographicCitation_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "valid", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "date")
    public JAXBElement<SimpleLiteral> createValid(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Valid_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "isFormatOf", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createIsFormatOf(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_IsFormatOf_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "dateAccepted", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "date")
    public JAXBElement<SimpleLiteral> createDateAccepted(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_DateAccepted_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "mediator", substitutionHeadNamespace = "http://www.purl.org/dc/terms/", substitutionHeadName = "audience")
    public JAXBElement<SimpleLiteral> createMediator(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Mediator_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "hasVersion", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createHasVersion(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_HasVersion_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "available", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "date")
    public JAXBElement<SimpleLiteral> createAvailable(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Available_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "isRequiredBy", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createIsRequiredBy(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_IsRequiredBy_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "references", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createReferences(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_References_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "created", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "date")
    public JAXBElement<SimpleLiteral> createCreated(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Created_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "dateCopyrighted", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "date")
    public JAXBElement<SimpleLiteral> createDateCopyrighted(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_DateCopyrighted_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "spatial", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "coverage")
    public JAXBElement<SimpleLiteral> createSpatial(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Spatial_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "dateSubmitted", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "date")
    public JAXBElement<SimpleLiteral> createDateSubmitted(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_DateSubmitted_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "medium", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "format")
    public JAXBElement<SimpleLiteral> createMedium(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_Medium_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "conformsTo", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "relation")
    public JAXBElement<SimpleLiteral> createConformsTo(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_ConformsTo_QNAME, SimpleLiteral.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleLiteral }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.purl.org/dc/terms/", name = "tableOfContents", substitutionHeadNamespace = "http://www.purl.org/dc/elements/1.1/", substitutionHeadName = "description")
    public JAXBElement<SimpleLiteral> createTableOfContents(final SimpleLiteral value) {
        return new JAXBElement<SimpleLiteral>(_TableOfContents_QNAME, SimpleLiteral.class, null, value);
    }

}
