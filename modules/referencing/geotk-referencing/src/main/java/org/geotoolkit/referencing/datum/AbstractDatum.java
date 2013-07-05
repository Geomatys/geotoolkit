/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.datum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.jcip.annotations.Immutable;

import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.util.InternationalString;

import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.io.wkt.Formatter;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.internal.jaxb.gco.DateAsLongAdapter;

import static org.apache.sis.util.Utilities.deepEquals;


/**
 * Specifies the relationship of a coordinate system to the earth, thus creating a
 * {@linkplain org.geotoolkit.referencing.crs.AbstractCRS coordinate reference system}.
 * A datum uses a parameter or set of parameters that determine the location of the
 * origin of the coordinate reference system. Each datum subtype can be associated with
 * only specific types of {@linkplain org.geotoolkit.referencing.cs.AbstractCS coordinate systems}.
 * <p>
 * A datum can be defined as a set of real points on the earth that have coordinates.
 * The definition of the datum may also include the temporal behavior (such as the
 * rate of change of the orientation of the coordinate axes).
 * <p>
 * This class is conceptually <cite>abstract</cite>, even if it is technically possible to
 * instantiate it. Typical applications should create instances of the most specific subclass with
 * {@code Default} prefix instead. An exception to this rule may occurs when it is not possible to
 * identify the exact type.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @see org.geotoolkit.referencing.cs.AbstractCS
 * @see org.geotoolkit.referencing.crs.AbstractCRS
 *
 * @since 1.2
 * @module
 */
@Immutable
@XmlType(name="AbstractDatumType")
public class AbstractDatum extends AbstractIdentifiedObject implements Datum {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4894180465652474930L;

    /**
     * List of localizable properties. To be given to
     * {@link AbstractIdentifiedObject} constructor.
     */
    private static final String[] LOCALIZABLES = {ANCHOR_POINT_KEY, SCOPE_KEY};

    /**
     * Description, possibly including coordinates, of the point or points used to anchor the datum
     * to the Earth. Also known as the "origin", especially for Engineering and Image Datums.
     */
    @XmlElement
    private final InternationalString anchorPoint;

    /**
     * The time after which this datum definition is valid. This time may be precise
     * (e.g. 1997 for IRTF97) or merely a year (e.g. 1983 for NAD83). If the time is
     * not defined, then the value is {@link Long#MIN_VALUE}.
     */
    @XmlElement
    @XmlJavaTypeAdapter(value=DateAsLongAdapter.class, type=long.class)
    private final long realizationEpoch;

    /**
     * Area or region in which this datum object is valid.
     */
    @XmlElement(name = "validArea")
    private final Extent domainOfValidity;

    /**
     * Description of domain of usage, or limitations of usage, for which this
     * datum object is valid.
     */
    @XmlElement
    private final InternationalString scope;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private AbstractDatum() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new datum with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param datum The datum to copy.
     *
     * @since 2.2
     */
    public AbstractDatum(final Datum datum) {
        super(datum);
        final Date epoch = datum.getRealizationEpoch();
        realizationEpoch = (epoch!=null) ? epoch.getTime() : Long.MIN_VALUE;
        domainOfValidity = datum.getDomainOfValidity();
        scope            = datum.getScope();
        anchorPoint      = datum.getAnchorPoint();
    }

    /**
     * Constructs a datum from a set of properties. The properties given in argument follow
     * the same rules than for the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}. Additionally, the following properties are understood by this
     * constructor:
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.datum.Datum#ANCHOR_POINT_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link InternationalString} or {@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getAnchorPoint}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.datum.Datum#REALIZATION_EPOCH_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link Date}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getRealizationEpoch}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.datum.Datum#DOMAIN_OF_VALIDITY_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link Extent}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getDomainOfValidity}</td>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.datum.Datum#SCOPE_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link InternationalString} or {@link String}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getScope}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to the identified object.
     */
    public AbstractDatum(final Map<String,?> properties) {
        this(properties, new HashMap<String,Object>());
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private AbstractDatum(final Map<String,?> properties, final Map<String,Object> subProperties) {
        super(properties, subProperties, LOCALIZABLES);
        final Date realizationEpoch;
        anchorPoint      = (InternationalString) subProperties.get(ANCHOR_POINT_KEY      );
        realizationEpoch = (Date)                subProperties.get(REALIZATION_EPOCH_KEY );
        domainOfValidity = (Extent)              subProperties.get(DOMAIN_OF_VALIDITY_KEY);
        scope            = (InternationalString) subProperties.get(SCOPE_KEY             );
        this.realizationEpoch = (realizationEpoch != null) ?
                                 realizationEpoch.getTime() : Long.MIN_VALUE;
    }

    /**
     * Same convenience method than {@link org.geotoolkit.cs.AbstractCS#name} except that we get
     * the unlocalized name (usually in English locale), because the name is part of the elements
     * compared by the {@link #equals} method.
     */
    static Map<String,Object> name(final int key) {
        final Map<String,Object> properties = new HashMap<>(4);
        final InternationalString name = Vocabulary.formatInternational(key);
        properties.put(NAME_KEY,  name.toString(null)); // "null" required for unlocalized version.
        properties.put(ALIAS_KEY, name);
        return properties;
    }

    /**
     * Description, possibly including coordinates, of the point or points used to anchor the datum
     * to the Earth. Also known as the "origin", especially for Engineering and Image Datums.
     * <p>
     * <ul>
     *   <li>For a geodetic datum, this point is also known as the fundamental point, which is
     *       traditionally the point where the relationship between geoid and ellipsoid is defined.
     *       In some cases, the "fundamental point" may consist of a number of points. In those
     *       cases, the parameters defining the geoid/ellipsoid relationship have then been averaged
     *       for these points, and the averages adopted as the datum definition.</li>
     *
     *   <li>For an engineering datum, the anchor point may be a physical point, or it may be a
     *       point with defined coordinates in another CRS.</li>
     *
     *   <li>For an image datum, the anchor point is usually either the centre of the image or the
     *       corner of the image.</li>
     *
     *   <li>For a temporal datum, this attribute is not defined. Instead of the anchor point,
     *       a temporal datum carries a separate time origin of type {@link Date}.</li>
     * </ul>
     */
    @Override
    public InternationalString getAnchorPoint() {
        return anchorPoint;
    }

    /**
     * The time after which this datum definition is valid. This time may be precise (e.g. 1997
     * for IRTF97) or merely a year (e.g. 1983 for NAD83). In the latter case, the epoch usually
     * refers to the year in which a major recalculation of the geodetic control network, underlying
     * the datum, was executed or initiated. An old datum can remain valid after a new datum is
     * defined. Alternatively, a datum may be superseded by a later datum, in which case the
     * realization epoch for the new datum defines the upper limit for the validity of the
     * superseded datum.
     */
    @Override
    public Date getRealizationEpoch() {
        return (realizationEpoch != Long.MIN_VALUE) ? new Date(realizationEpoch) : null;
    }

    /**
     * Area or region or timeframe in which this datum is valid.
     *
     * @since 2.4
     */
    @Override
    public Extent getDomainOfValidity() {
        return domainOfValidity;
    }

    /**
     * Description of domain of usage, or limitations of usage, for which this
     * datum object is valid.
     */
    @Override
    public InternationalString getScope() {
        return scope;
    }

    /**
     * Gets the type of the datum as an enumerated code. Datum type was provided
     * for all kind of datum in the legacy OGC 01-009 specification. In the new
     * OGC 03-73 (ISO 19111) specification, datum type is provided only for
     * vertical datum. Nevertheless, we keep this method around since it is
     * needed for WKT formatting. Note that we returns the datum type ordinal
     * value, not the code list object.
     */
    int getLegacyDatumType() {
        return 0;
    }

    /**
     * Compares the specified object with this datum for equality.
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
                    final AbstractDatum that = (AbstractDatum) object;
                    return this.realizationEpoch == that.realizationEpoch &&
                           Objects.equals(this.domainOfValidity, that.domainOfValidity) &&
                           Objects.equals(this.anchorPoint,      that.anchorPoint) &&
                           Objects.equals(this.scope,            that.scope);
                }
                case BY_CONTRACT: {
                    final Datum that = (Datum) object;
                    return deepEquals(getRealizationEpoch(), that.getRealizationEpoch(), mode) &&
                           deepEquals(getDomainOfValidity(), that.getDomainOfValidity(), mode) &&
                           deepEquals(getAnchorPoint(),      that.getAnchorPoint(),      mode) &&
                           deepEquals(getScope(),            that.getScope(),            mode);
                }
                default: {
                    /*
                     * Tests for name, since datum with different name have completely
                     * different meaning. We don't perform this comparison if the user
                     * asked for metadata comparison, because in such case the names
                     * have already been compared by the subclass.
                     */
                    final IdentifiedObject that = (IdentifiedObject) object;
                    return nameMatches(that. getName().getCode()) ||
                           IdentifiedObjects.nameMatches(that, getName().getCode());
                }
            }
        }
        return false;
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * {@note All subclasses will override this method, but only <code>DefaultGeodeticDatum</code>
     *        will <strong>not</strong> invoke this parent method, because horizontal datum do not
     *        write the datum type.}
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name.
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        formatter.append(getLegacyDatumType());
        return Classes.getShortClassName(this);
    }
}
