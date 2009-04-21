/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.constraint;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.util.InternationalString;
import org.opengis.metadata.constraint.Classification;
import org.opengis.metadata.constraint.SecurityConstraints;


/**
 * Handling restrictions imposed on the resource for national security or similar security concerns.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 2.1
 * @module
 */
@XmlType(name = "MD_SecurityConstraints", propOrder={
    "classification",
    "userNote",
    "classificationSystem",
    "handlingDescription"
})
@XmlRootElement(name = "MD_SecurityConstraints")
public class DefaultSecurityConstraints extends DefaultConstraints implements SecurityConstraints {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 6412833018607679734L;;

    /**
     * Name of the handling restrictions on the resource.
     */
    private Classification classification;

    /**
     * Explanation of the application of the legal constraints or other restrictions and legal
     * prerequisites for obtaining and using the resource.
     */
    private InternationalString userNote;

    /**
     * Name of the classification system.
     */
    private InternationalString classificationSystem;

    /**
     * Additional information about the restrictions on handling the resource.
     */
    private InternationalString handlingDescription;

    /**
     * Creates an initially empty security constraints.
     */
    public DefaultSecurityConstraints() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultSecurityConstraints(final SecurityConstraints source) {
        super(source);
    }

    /**
     * Creates a security constraints initialized with the specified classification.
     *
     * @param classification The nname of the handling restrictions on the resource.
     */
    public DefaultSecurityConstraints(final Classification classification) {
        setClassification(classification);
    }

    /**
     * Returns the name of the handling restrictions on the resource.
     */
    @Override
    @XmlElement(name = "classification", required = true)
    public Classification getClassification() {
        return classification;
    }

    /**
     * Sets the name of the handling restrictions on the resource.
     *
     * @param newValue The new classification.
     */
    public synchronized void setClassification(final Classification newValue) {
        checkWritePermission();
        classification = newValue;
    }

    /**
     * Returns the explanation of the application of the legal constraints or other restrictions and legal
     * prerequisites for obtaining and using the resource.
     */
    @Override
    @XmlElement(name = "userNote")
    public InternationalString getUserNote() {
        return userNote;
    }

    /**
     * Sets the explanation of the application of the legal constraints or other restrictions and legal
     * prerequisites for obtaining and using the resource.
     *
     * @param newValue The new user note.
     */
    public synchronized void setUserNote(final InternationalString newValue) {
        checkWritePermission();
        userNote = newValue;
    }

    /**
     * Returns the name of the classification system.
     */
    @Override
    @XmlElement(name = "classificationSystem")
    public InternationalString getClassificationSystem() {
        return classificationSystem;
    }

    /**
     * Sets the name of the classification system.
     *
     * @param newValue The new classification system.
     */
    public synchronized void setClassificationSystem(final InternationalString newValue) {
        checkWritePermission();
        classificationSystem = newValue;
    }

    /**
     * Returns the additional information about the restrictions on handling the resource.
     */
    @Override
    @XmlElement(name = "handlingDescription")
    public InternationalString getHandlingDescription() {
        return handlingDescription;
    }

    /**
     * Sets the additional information about the restrictions on handling the resource.
     *
     * @param newValue The new handling description.
     */
    public synchronized void setHandlingDescription(final InternationalString newValue) {
        checkWritePermission();
        handlingDescription = newValue;
    }
}
