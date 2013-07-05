/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.xml;

import java.util.Collection;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;


/**
 * The interface for all Geotk objects having identifiers. Identifiers are {@link String} in
 * a namespace identified by a {@link Citation}. The namespace can be some organization like
 * <a href="http://www.epsg.org">EPSG</a> for Coordinate Reference System objects, or a
 * well-known acronym like ISBN for <cite>International Standard Book Number</cite>.
 * <p>
 * When an identified object is marshalled in a ISO 19139 compliant XML document, some identifiers
 * are handled in a special way: they appear as {@code gml:id}, {@code gco:uuid} or {@code xlink:href}
 * attributes of the XML element. Those identifiers can be specified using the {@link IdentifierSpace}
 * enum values as below:
 *
 * {@preformat java
 *     IdentifiedObject object = ...;
 *     object.getIdentifierMap().put(IdentifierSpace.ID, "myID");
 * }
 *
 * {@section Relationship with GeoAPI}
 * Identifiers exist also in some (not all) GeoAPI objects. Some GeoAPI objects
 * ({@link org.opengis.metadata.acquisition.Instrument}, {@link org.opengis.metadata.acquisition.Platform},
 * {@link org.opengis.metadata.acquisition.Operation}, {@link org.opengis.metadata.lineage.Processing},
 * <i>etc.</i>) have an explicit single identifier attribute, while other GeoAPI objects
 * ({@link org.opengis.metadata.citation.Citation}, {@link org.opengis.metadata.acquisition.Objective},
 * referencing {@link org.opengis.referencing.IdentifiedObject}, <i>etc.</i>) allow an arbitrary
 * number of identifiers. However GeoAPI does not define explicit methods for handling the {@code id},
 * {@code uuid} or {@code href} attributes, since they are specific to XML marshalling (they do not
 * appear in OGC/ISO abstract specifications). This {@code IdentifiedObject} interface provides a
 * way to handle those identifiers.
 * <p>
 * Note that GeoAPI defines a similar interface, also named {@link org.opengis.referencing.IdentifiedObject}.
 * However that GeoAPI interface is not of general use, since it contains methods like
 * {@link org.opengis.referencing.IdentifiedObject#toWKT() toWKT()} that are specific to referencing
 * or geometric objects. In addition, the GeoAPI interface defines some attributes
 * ({@linkplain org.opengis.referencing.IdentifiedObject#getName() name},
 * {@linkplain org.opengis.referencing.IdentifiedObject#getAlias() alias},
 * {@linkplain org.opengis.referencing.IdentifiedObject#getRemarks() remarks}) that are not needed
 * for the purpose of handling XML {@code id}, {@code uuid} or {@code href} attributes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see IdentifierSpace
 * @see org.geotoolkit.metadata.iso.MetadataEntity
 * @see ObjectLinker#newIdentifiedObject(Class, Identifier[])
 *
 * @since 3.18
 * @module
 *
 * @deprecated Moved to SIS as {@link org.apache.sis.xml.IdentifiedObject}.
 */
@Deprecated
public interface IdentifiedObject {
    /**
     * Returns all identifiers associated to this object. Each {@linkplain Identifier#getCode()
     * identifier code} shall be unique in the {@linkplain Identifier#getAuthority() identifier
     * authority} name space. Examples of namespace are:
     * <p>
     * <ul>
     *   <li>{@linkplain org.geotoolkit.metadata.iso.citation.Citations#EPSG EPSG} codes</li>
     *   <li><cite>Universal Product Code</cite> (UPC)</li>
     *   <li><cite>National Stock Number</cite> (NSN)</li>
     *   <li><cite>International Standard Book Number</cite>
     *       ({@linkplain org.geotoolkit.metadata.iso.citation.Citations#ISBN ISBN})</li>
     *   <li><cite>International Standard Serial Number</cite>
     *       ({@linkplain org.geotoolkit.metadata.iso.citation.Citations#ISSN ISSN})</li>
     *   <li><cite>Universally Unique Identifier</cite> ({@linkplain java.util.UUID})</li>
     *   <li>XML {@linkplain IdentifierSpace#ID ID} attribute</li>
     *   <li>{@linkplain XLink} ({@code href}, {@code role}, {@code arcrole}, {@code title},
     *       {@code show} and {@code actuate} attributes)</li>
     * </ul>
     * <p>
     * Note that XML ID attribute are actually unique only in the scope of the XML document
     * being processed.
     *
     * @return All identifiers associated to this object, or an empty collection if none.
     *
     * @see org.geotoolkit.metadata.iso.citation.DefaultCitation#getIdentifiers()
     * @see org.geotoolkit.metadata.iso.acquisition.DefaultObjective#getIdentifiers()
     * @see org.geotoolkit.referencing.AbstractIdentifiedObject#getIdentifiers()
     *
     * @since 3.19
     */
    Collection<? extends Identifier> getIdentifiers();

    /**
     * A map view of {@linkplain #getIdentifiers() identifiers}.
     * Each {@linkplain java.util.Map.Entry map entry} is associated to an element from the
     * identifier collection in which the {@linkplain java.util.Map.Entry#getKey() key} is
     * the {@linkplain Identifier#getAuthority() identifier authority} and the
     * {@linkplain java.util.Map.Entry#getValue() value} is the
     * {@linkplain Identifier#getCode() identifier code}.
     * <p>
     * There is usually a one-to-one relationship between the map entries and the identifier
     * elements, but not always:
     * <ul>
     *   <li><p>The map view may contain less entries, because the map interface allows only one
     *   entry per authority. If the {@linkplain #getIdentifiers() identifier collection} contains
     *   many identifiers for the same authority, then only the first occurrence is visible through
     *   this {@code Map} view.</p></li>
     *
     *   <li><p>The map view may also contain more entries than the {@linkplain #getIdentifiers()
     *   identifier collection}. For example the {@link org.opengis.metadata.citation.Citation}
     *   interface defines separated attributes for ISBN, ISSN and other identifiers. This map
     *   view may choose to unify all those attributes in a single view.</p></li>
     * </ul>
     * <p>
     * The map supports {@link IdentifierMap#put put} operations if and only if this
     * {@code IdentifiedObject} is modifiable.
     *
     * @return The identifiers as a map of (<var>authority</var>, <var>code</var>) entries,
     *         or an empty map if none.
     *
     * @since 3.19
     */
    IdentifierMap getIdentifierMap();
}
