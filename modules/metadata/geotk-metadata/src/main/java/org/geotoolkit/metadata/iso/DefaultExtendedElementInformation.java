/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso;

import java.util.Collection;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Datatype;
import org.opengis.metadata.Obligation;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.ExtendedElementInformation;
import org.opengis.util.InternationalString;

import org.geotoolkit.lang.ValueRange;


/**
 * New metadata element, not found in ISO 19115, which is required to describe geographic data.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MD_ExtendedElementInformation_Type", propOrder={
    "name",
    "shortName",
    "domainCode",
    "definition",
    "obligation",
    "condition",
    "dataType",
    "maximumOccurrence",
    "domainValue",
    "parentEntity",
    "rule",
    "rationales",
    "sources"
})
@XmlRootElement(name = "MD_ExtendedElementInformation")
public class DefaultExtendedElementInformation extends MetadataEntity
        implements ExtendedElementInformation
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -935396252908733907L;

    /**
     * Name of the extended metadata element.
     */
    private String name;

    /**
     * Short form suitable for use in an implementation method such as XML or SGML.
     */
    private String shortName;

    /**
     * Three digit code assigned to the extended element.
     * Non-null only if the {@linkplain #getDataType data type}
     * is {@linkplain Datatype#CODE_LIST_ELEMENT code list element}.
     */
    private Integer domainCode;

    /**
     * Definition of the extended element.
     */
    private InternationalString definition;

    /**
     * Obligation of the extended element.
     */
    private Obligation obligation;

    /**
     * Condition under which the extended element is mandatory.
     * Non-null value only if the {@linkplain #getObligation obligation}
     * is {@linkplain Obligation#CONDITIONAL conditional}.
     */
    private InternationalString condition;

    /**
     * Code which identifies the kind of value provided in the extended element.
     */
    private Datatype dataType;

    /**
     * Maximum occurrence of the extended element.
     * Returns {@code null} if it doesn't apply, for example if the
     * {@linkplain #getDataType data type} is {@linkplain Datatype#ENUMERATION enumeration},
     * {@linkplain Datatype#CODE_LIST code list} or {@linkplain Datatype#CODE_LIST_ELEMENT
     * code list element}.
     */
    private Integer maximumOccurrence;

    /**
     * Valid values that can be assigned to the extended element.
     * Returns {@code null} if it doesn't apply, for example if the
     * {@linkplain #getDataType data type} is {@linkplain Datatype#ENUMERATION enumeration},
     * {@linkplain Datatype#CODE_LIST code list} or {@linkplain Datatype#CODE_LIST_ELEMENT
     * code list element}.
     */
    private InternationalString domainValue;

    /**
     * Name of the metadata entity(s) under which this extended metadata element may appear.
     * The name(s) may be standard metadata element(s) or other extended metadata element(s).
     */
    private Collection<String> parentEntity;

    /**
     * Specifies how the extended element relates to other existing elements and entities.
     */
    private InternationalString rule;

    /**
     * Reason for creating the extended element.
     */
    private Collection<InternationalString> rationales;

    /**
     * Name of the person or organization creating the extended element.
     */
    private Collection<ResponsibleParty> sources;

    /**
     * Construct an initially empty extended element information.
     */
    public DefaultExtendedElementInformation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultExtendedElementInformation(final ExtendedElementInformation source) {
        super(source);
    }

    /**
     * Create an extended element information initialized to the given values.
     *
     * @param name          The name of the extended metadata element.
     * @param definition    The definition of the extended element.
     * @param condition     The condition under which the extended element is mandatory.
     * @param dataType      The code which identifies the kind of value provided in the extended element.
     * @param parentEntity  The name of the metadata entity(s) under which this extended metadata element may appear.
     * @param rule          How the extended element relates to other existing elements and entities.
     * @param sources       The name of the person or organization creating the extended element.
     */
    public DefaultExtendedElementInformation(final String              name,
                                             final InternationalString definition,
                                             final InternationalString condition,
                                             final Datatype            dataType,
                                             final Collection<String>  parentEntity,
                                             final InternationalString rule,
                                             final Collection<? extends ResponsibleParty> sources)
    {
        setName        (name);
        setDefinition  (definition);
        setCondition   (condition);
        setDataType    (dataType);
        setParentEntity(parentEntity);
        setRule        (rule);
        setSources     (sources);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultExtendedElementInformation castOrCopy(final ExtendedElementInformation object) {
        return (object == null) || (object instanceof DefaultExtendedElementInformation)
                ? (DefaultExtendedElementInformation) object : new DefaultExtendedElementInformation(object);
    }

    /**
     * Name of the extended metadata element.
     */
    @Override
    @XmlElement(name = "name", required = true)
    public synchronized String getName() {
        return name;
    }

    /**
     * Sets the name of the extended metadata element.
     *
     * @param newValue The new name.
     */
    public synchronized void setName(final String newValue) {
        checkWritePermission();
        name = newValue;
    }

    /**
     * Short form suitable for use in an implementation method such as XML or SGML.
     * NOTE: other methods may be used.
     * Returns {@code null} if the {@linkplain #getDataType data type}
     * is {@linkplain Datatype#CODE_LIST_ELEMENT code list element}.
     */
    @Override
    @XmlElement(name = "shortName")
    public synchronized String getShortName()  {
        return shortName;
    }

    /**
     * Sets a short form suitable for use in an implementation method such as XML or SGML.
     *
     * @param newValue The new short name.
     */
    public synchronized void setShortName(final String newValue)  {
        checkWritePermission();
        shortName = newValue;
    }

    /**
     * Three digit code assigned to the extended element.
     * Returns a non-null value only if the {@linkplain #getDataType data type}
     * is {@linkplain Datatype#CODE_LIST_ELEMENT code list element}.
     */
    @Override
    @XmlElement(name = "domainCode")
    public synchronized Integer getDomainCode() {
        return domainCode;
    }

    /**
     * Sets a three digit code assigned to the extended element.
     *
     * @param newValue The new domain code.
     */
    public synchronized void setDomainCode(final Integer newValue) {
        checkWritePermission();
        domainCode = newValue;
    }

    /**
     * Definition of the extended element.
     */
    @Override
    @XmlElement(name = "definition", required = true)
    public synchronized InternationalString getDefinition()  {
        return definition;
    }

    /**
     * Sets the definition of the extended element.
     *
     * @param newValue The new definition.
     */
    public synchronized void setDefinition(final InternationalString newValue)  {
        checkWritePermission();
        definition = newValue;
    }

    /**
     * Obligation of the extended element.
     */
    @Override
    @XmlElement(name = "obligation")
    public synchronized Obligation getObligation()  {
        return obligation;
    }

    /**
     * Sets the obligation of the extended element.
     *
     * @param newValue The new obligation.
     */
    public synchronized void setObligation(final Obligation newValue)  {
        checkWritePermission();
        obligation = newValue;
    }

    /**
     * Condition under which the extended element is mandatory.
     * Returns a non-null value only if the {@linkplain #getObligation obligation}
     * is {@linkplain Obligation#CONDITIONAL conditional}.
     */
    @Override
    @XmlElement(name = "condition")
    public synchronized InternationalString getCondition() {
        return condition;
    }

    /**
     * Sets the condition under which the extended element is mandatory.
     *
     * @param newValue The new condition.
     */
    public synchronized void setCondition(final InternationalString newValue) {
        checkWritePermission();
        condition = newValue;
    }

    /**
     * Code which identifies the kind of value provided in the extended element.
     */
    @Override
    @XmlElement(name = "dataType", required = true)
    public synchronized Datatype getDataType() {
        return dataType;
    }

    /**
     * Sets the code which identifies the kind of value provided in the extended element.
     *
     * @param newValue The new data type.
     */
    public synchronized void setDataType(final Datatype newValue) {
        checkWritePermission();
        dataType = newValue;
    }

    /**
     * Maximum occurrence of the extended element.
     * Returns {@code null} if it doesn't apply, for example if the
     * {@linkplain #getDataType data type} is {@linkplain Datatype#ENUMERATION enumeration},
     * {@linkplain Datatype#CODE_LIST code list} or {@linkplain Datatype#CODE_LIST_ELEMENT
     * code list element}.
     */
    @Override
    @ValueRange(minimum=0)
    @XmlElement(name = "maximumOccurrence")
    public synchronized Integer getMaximumOccurrence() {
        return maximumOccurrence;
    }

    /**
     * Sets the maximum occurrence of the extended element.
     *
     * @param newValue The new maximum occurrence.
     */
    public synchronized void setMaximumOccurrence(final Integer newValue) {
        checkWritePermission();
        maximumOccurrence = newValue;
    }

    /**
     * Valid values that can be assigned to the extended element.
     * Returns {@code null} if it doesn't apply, for example if the
     * {@linkplain #getDataType data type} is {@linkplain Datatype#ENUMERATION enumeration},
     * {@linkplain Datatype#CODE_LIST code list} or {@linkplain Datatype#CODE_LIST_ELEMENT
     * code list element}.
     */
    @Override
    @XmlElement(name = "domainValue")
    public synchronized InternationalString getDomainValue() {
        return domainValue;
    }

    /**
     * Sets the valid values that can be assigned to the extended element.
     *
     * @param newValue The new domain value.
     */
    public synchronized void setDomainValue(final InternationalString newValue) {
        checkWritePermission();
        domainValue = newValue;
    }

    /**
     * Name of the metadata entity(s) under which this extended metadata element may appear.
     * The name(s) may be standard metadata element(s) or other extended metadata element(s).
     */
    @Override
    @XmlElement(name = "parentEntity", required = true)
    public synchronized Collection<String> getParentEntity() {
        return parentEntity = nonNullCollection(parentEntity, String.class);
    }

    /**
     * Sets the name of the metadata entity(s) under which this extended metadata element may appear.
     *
     * @param newValues The new parent entity.
     */
    public synchronized void setParentEntity(final Collection<? extends String> newValues) {
        parentEntity = copyCollection(newValues, parentEntity, String.class);
    }

    /**
     * Specifies how the extended element relates to other existing elements and entities.
     */
    @Override
    @XmlElement(name = "rule", required = true)
    public synchronized InternationalString getRule() {
        return rule;
    }

    /**
     * Sets how the extended element relates to other existing elements and entities.
     *
     * @param newValue The new rule.
     */
    public synchronized void setRule(final InternationalString newValue) {
        checkWritePermission();
        rule = newValue;
    }

    /**
     * Reason for creating the extended element.
     */
    @Override
    @XmlElement(name = "rationale")
    public synchronized Collection<InternationalString> getRationales() {
        return rationales = nonNullCollection(rationales, InternationalString.class);
    }

    /**
     * Sets the reason for creating the extended element.
     *
     * @param newValues The new rationales.
     */
    public synchronized void setRationales(final Collection<? extends InternationalString> newValues) {
        rationales = copyCollection(newValues, rationales, InternationalString.class);
    }

    /**
     * Name of the person or organization creating the extended element.
     */
    @Override
    @XmlElement(name = "source", required = true)
    public synchronized Collection<ResponsibleParty> getSources() {
        return sources = nonNullCollection(sources, ResponsibleParty.class);
    }

    /**
     * Sets the name of the person or organization creating the extended element.
     *
     * @param newValues The new sources.
     */
    public synchronized void setSources(final Collection<? extends ResponsibleParty> newValues) {
        sources = copyCollection(newValues, sources, ResponsibleParty.class);
    }
}
