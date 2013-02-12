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
package org.geotoolkit.referencing;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import net.jcip.annotations.Immutable;

import org.opengis.metadata.extent.Extent;
import org.opengis.util.InternationalString;
import org.opengis.referencing.ReferenceSystem;

import org.apache.sis.util.ComparisonMode;

import static org.geotoolkit.util.Utilities.deepEquals;


/**
 * Description of a spatial and temporal reference system used by a dataset.
 * <p>
 * This class is conceptually <cite>abstract</cite>, even if it is technically possible to
 * instantiate it. Typical applications should create instances of the most specific subclass with
 * {@code Default} prefix instead. An exception to this rule may occurs when it is not possible to
 * identify the exact type.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.1
 * @module
 */
@Immutable
public class AbstractReferenceSystem extends AbstractIdentifiedObject implements ReferenceSystem {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3337659819553899435L;

    /**
     * List of localizable properties. To be given to {@link AbstractIdentifiedObject} constructor.
     */
    private static final String[] LOCALIZABLES = {SCOPE_KEY};

    /**
     * Area for which the (coordinate) reference system is valid.
     */
    private final Extent domainOfValidity;

    /**
     * Description of domain of usage, or limitations of usage, for which this
     * (coordinate) reference system object is valid.
     */
    @XmlElement(required = true)
    private final InternationalString scope;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private AbstractReferenceSystem() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new reference system with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param object The reference system to copy.
     *
     * @since 2.2
     */
    public AbstractReferenceSystem(final ReferenceSystem object) {
        super(object);
        domainOfValidity = object.getDomainOfValidity();
        scope            = object.getScope();
    }

    /**
     * Constructs a reference system from a set of properties.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     * Additionally, the following properties are understood by this construtor:
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceSystem#DOMAIN_OF_VALIDITY_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link Extent}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getDomainOfValidity}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.ReferenceSystem#SCOPE_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link String} or {@link InternationalString}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getScope}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to this object.
     */
    public AbstractReferenceSystem(final Map<String,?> properties) {
        this(properties, new HashMap<String,Object>());
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private AbstractReferenceSystem(final Map<String,?> properties, final Map<String,Object> subProperties) {
        super(properties, subProperties, LOCALIZABLES);
        domainOfValidity = (Extent)   subProperties.get(DOMAIN_OF_VALIDITY_KEY);
        scope = (InternationalString) subProperties.get(SCOPE_KEY);
    }

    /**
     * Area or region or timeframe in which this (coordinate) reference system is valid.
     * Returns {@code null} if not available.
     *
     * @since 2.4
     */
    @Override
    public Extent getDomainOfValidity() {
        return domainOfValidity;
    }

    /**
     * Description of domain of usage, or limitations of usage, for which this
     * (coordinate) reference system object is valid.
     * Returns {@code null} if not available.
     */
    @Override
    public InternationalString getScope() {
        return scope;
    }

    /**
     * Compares this reference system with the specified object for equality.
     * If the {@code mode} argument value is {@link ComparisonMode#STRICT STRICT} or
     * {@link ComparisonMode#BY_CONTRACT BY_CONTRACT}, then all available properties are
     * compared including the {@linkplain #getDomainOfValidity() domain of validity} and
     * the {@linkplain #getScope() scope}.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final AbstractReferenceSystem that = (AbstractReferenceSystem) object;
                    return Objects.equals(domainOfValidity, that.domainOfValidity) &&
                           Objects.equals(scope,            that.scope);
                }
                case BY_CONTRACT: {
                    final ReferenceSystem that = (ReferenceSystem) object;
                    return deepEquals(getDomainOfValidity(), that.getDomainOfValidity(), mode) &&
                           deepEquals(getScope(),            that.getScope(), mode);
                }
                default: {
                    // Domain of validity and scope are metadata, so they can be ignored.
                    return true;
                }
            }
        }
        return false;
    }
}
