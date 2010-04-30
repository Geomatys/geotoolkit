/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.metadata.iso.quality;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.ConformanceResult;

import org.geotoolkit.lang.ThreadSafe;


/**
 * Information about the outcome of evaluating the obtained value (or set of values) against
 * a specified acceptable conformance quality level.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Guilhem Legal (Geomatys)
 * @version 3.11
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "specification",
    "explanation",
    "pass"
})
@XmlRootElement(name = "DQ_ConformanceResult")
public class DefaultConformanceResult extends AbstractResult implements ConformanceResult {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -8746956498487963352L;

    /**
     * Citation of product specification or user requirement against which data is being evaluated.
     */
    private Citation specification;

    /**
     * Explanation of the meaning of conformance for this result.
     */
    private InternationalString explanation;

    /**
     * Indication of the conformance result.
     * <p>
     * The field is directly annotated here, because the getter method is called {@link #pass()},
     * and JAXB does not recognize it. The method should have been called getPass() or isPass().
     */
    @XmlElement(name = "pass", required = true)
    private Boolean pass;

    /**
     * Constructs an initially empty conformance result.
     */
    public DefaultConformanceResult() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultConformanceResult(final ConformanceResult source) {
        super(source);
    }

    /**
     * Creates a conformance result initialized to the given values.
     *
     * @param specification Specification or requirement against which data is being evaluated.
     * @param explanation The meaning of conformance for this result.
     * @param pass Indication of the conformance result.
     */
    public DefaultConformanceResult(final Citation specification,
                                    final InternationalString explanation,
                                    final boolean pass)
    {
        setSpecification(specification);
        setExplanation(explanation);
        setPass(pass);
    }

    /**
     * Returns the citation of product specification or user
     * requirement against which data is being evaluated.
     */
    @Override
    @XmlElement(name = "specification", required = true)
    public synchronized Citation getSpecification() {
        return specification;
    }

    /**
     * Sets the citation of product specification or user requirement against which data
     * is being evaluated.
     *
     * @param newValue The new specification.
     */
    public synchronized void setSpecification(final Citation newValue) {
        checkWritePermission();
        specification = newValue;
    }

    /**
     * Returns the explanation of the meaning of conformance for this result.
     */
    @Override
    @XmlElement(name = "explanation", required = true)
    public synchronized InternationalString getExplanation() {
        return explanation;
    }

    /**
     * Sets the explanation of the meaning of conformance for this result.
     *
     * @param newValue The new explanation.
     */
    public synchronized void setExplanation(final InternationalString newValue) {
        checkWritePermission();
        explanation = newValue;
    }

    /**
     * Returns an indication of the conformance result.
     */
    @Override
    public synchronized boolean pass() {
        return (pass != null) && pass;
    }

    /**
     * Sets the indication of the conformance result.
     *
     * @param newValue {@code true} if the test pass.
     */
    public synchronized void setPass(final boolean newValue) {
        checkWritePermission();
        pass = newValue;
    }
}
