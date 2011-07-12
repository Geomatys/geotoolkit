/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.metadata.iso.citation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Series;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.UnmodifiableMetadataException;
import org.geotoolkit.internal.jaxb.IdentifierMapAdapter;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.xml.IdentifierMap;

import static org.geotoolkit.internal.jaxb.MarshalContext.filterIdentifiers;


/**
 * Standardized resource reference.
 *
 * {@section Unified identifiers view}
 * The ISO 19115 model provides specific attributes for the {@linkplain #getISBN() ISBN} and
 * {@linkplain #getISSN() ISSN} codes. However from an application point of view, it is sometime
 * convenient to handle those codes like any other identifiers. The {@linkplain #getIdentifierMap()
 * identifier map} view includes those ISBN and ISSN codes.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "CI_Citation_Type", propOrder={
    "title",
    "alternateTitles",
    "dates",
    "edition",
    "editionDate",
    "identifiers",
    "citedResponsibleParties",
    "presentationForms",
    "series",
    "otherCitationDetails",
    "collectiveTitle",
    "ISBN",
    "ISSN"
})
@XmlRootElement(name = "CI_Citation")
public class DefaultCitation extends MetadataEntity implements Citation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 490722440306018256L;

    /**
     * Name by which the cited resource is known.
     */
    private InternationalString title;

    /**
     * Short name or other language name by which the cited information is known.
     * Example: "DCW" as an alternative title for "Digital Chart of the World.
     */
    private Collection<InternationalString> alternateTitles;

    /**
     * Reference date for the cited resource.
     */
    private Collection<CitationDate> dates;

    /**
     * Version of the cited resource.
     */
    private InternationalString edition;

    /**
     * Date of the edition in milliseconds elapsed sine January 1st, 1970,
     * or {@link Long#MIN_VALUE} if none.
     */
    private long editionDate;

    /**
     * Name and position information for an individual or organization that is responsible
     * for the resource. Returns an empty string if there is none.
     */
    private Collection<ResponsibleParty> citedResponsibleParties;

    /**
     * Mode in which the resource is represented, or an empty string if none.
     */
    private Collection<PresentationForm> presentationForms;

    /**
     * Information about the series, or aggregate dataset, of which the dataset is a part.
     * May be {@code null} if none.
     */
    private Series series;

    /**
     * Other information required to complete the citation that is not recorded elsewhere.
     * May be {@code null} if none.
     */
    private InternationalString otherCitationDetails;

    /**
     * Common title with holdings note. Note: title identifies elements of a series
     * collectively, combined with information about what volumes are available at the
     * source cited. May be {@code null} if there is no title.
     */
    private InternationalString collectiveTitle;

    /**
     * International Standard Book Number, or {@code null} if none.
     */
    private String ISBN;

    /**
     * International Standard Serial Number, or {@code null} if none.
     */
    private String ISSN;

    /**
     * Constructs an initially empty citation.
     */
    public DefaultCitation() {
        editionDate = Long.MIN_VALUE;
    }

    /**
     * Constructs a new citation initialized to the values specified by the given object.
     * This constructor performs a shallow copy (i.e. each source attributes are reused
     * without copying them).
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultCitation(final Citation source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date value (GEOTK-170).
            if (editionDate == 0 && source.getEditionDate() == null) {
                editionDate = Long.MIN_VALUE;
            }
        }
    }

    /**
     * Constructs a citation with the specified title.
     *
     * @param title The title, as a {@link String} or an {@link InternationalString} object.
     */
    public DefaultCitation(final CharSequence title) {
        this(); // Initialize the date field.
        final InternationalString t;
        if (title instanceof InternationalString) {
            t = (InternationalString) title;
        } else {
            t = new SimpleInternationalString(title.toString());
        }
        setTitle(t);
    }

    /**
     * Constructs a citation with the specified responsible party. This convenience constructor
     * initialize the citation title to the first non-null of the following properties:
     * {@linkplain ResponsibleParty#getOrganisationName organisation name},
     * {@linkplain ResponsibleParty#getPositionName position name} or
     * {@linkplain ResponsibleParty#getIndividualName individual name}.
     *
     * @param party The name and position information for an individual or organization that is
     *              responsible for the resource.
     * @since 2.2
     */
    public DefaultCitation(final ResponsibleParty party) {
        this(); // Initialize the date field.
        InternationalString title = party.getOrganisationName();
        if (title == null) {
            title = party.getPositionName();
            if (title == null) {
                String name = party.getIndividualName();
                if (name != null) {
                    title = new SimpleInternationalString(name);
                }
            }
        }
        setTitle(title);
        setCitedResponsibleParties(Collections.singleton(party));
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
    public static DefaultCitation castOrCopy(final Citation object) {
        return (object == null) || (object instanceof DefaultCitation)
                ? (DefaultCitation) object : new DefaultCitation(object);
    }

    /**
     * @deprecated Renamed {@link #castOrCopy castOrCopy}.
     * @param object The object to get as a Geotk implementation, or {@code null} if none.
     * @return The given object as a Geotk implementation.
     */
    @Deprecated
    public static DefaultCitation wrap(final Citation object) {
        return castOrCopy(object);
    }

    /**
     * Returns the name by which the cited resource is known.
     */
    @Override
    @XmlElement(name = "title", required = true)
    public synchronized InternationalString getTitle() {
        return title;
    }

    /**
     * Sets the name by which the cited resource is known.
     *
     * @param newValue The new title.
     */
    public synchronized void setTitle(final InternationalString newValue) {
        checkWritePermission();
        title = newValue;
    }

    /**
     * Returns the short name or other language name by which the cited information is known.
     * Example: "DCW" as an alternative title for "Digital Chart of the World".
     */
    @Override
    @XmlElement(name = "alternateTitle")
    public synchronized Collection<InternationalString> getAlternateTitles() {
        return alternateTitles = nonNullCollection(alternateTitles, InternationalString.class);
    }

    /**
     * Sets the short name or other language name by which the cited information is known.
     *
     * @param newValues The new alternate titles.
     */
    public synchronized void setAlternateTitles(final Collection<? extends InternationalString> newValues) {
        alternateTitles = copyCollection(newValues, alternateTitles, InternationalString.class);
    }

    /**
     * Returns the reference date for the cited resource.
     */
    @Override
    @XmlElement(name = "date", required = true)
    public synchronized Collection<CitationDate> getDates() {
        return dates = nonNullCollection(dates, CitationDate.class);
    }

    /**
     * Sets the reference date for the cited resource.
     *
     * @param newValues The new dates.
     */
    public synchronized void setDates(final Collection<? extends CitationDate> newValues) {
        dates = copyCollection(newValues, dates, CitationDate.class);
    }

    /**
     * Returns the version of the cited resource.
     */
    @Override
    @XmlElement(name = "edition")
    public synchronized InternationalString getEdition() {
        return edition;
    }

    /**
     * Sets the version of the cited resource.
     *
     * @param newValue The new edition.
     */
    public synchronized void setEdition(final InternationalString newValue) {
        checkWritePermission();
        edition = newValue;
    }

    /**
     * Returns the date of the edition, or {@code null} if none.
     */
    @Override
    @XmlElement(name = "editionDate")
    public synchronized Date getEditionDate() {
        return (editionDate != Long.MIN_VALUE) ? new Date(editionDate) : null;
    }

    /**
     * Sets the date of the edition, or {@code null} if none.
     *
     * @param newValue The new edition date.
     *
     * @todo Use an unmodifiable {@link Date} here.
     */
    public synchronized void setEditionDate(final Date newValue) {
        checkWritePermission();
        editionDate = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the unique identifier for the resource. Example: Universal Product Code (UPC),
     * National Stock Number (NSN).
     */
    @Override
    @XmlElement(name = "identifier")
    public Collection<Identifier> getIdentifiers() {
        return filterIdentifiers(super.getIdentifiers(), false);
    }

    /**
     * Returns a map view of the {@linkplain #getIdentifiers() identifiers}. If the
     * {@linkplain #getISBN() ISBN} or {@linkplain #getISSN() ISSN} properties are
     * defined, then they will be included in the map view.
     * <p>
     * The map returns by this method is <cite>live</cite>: changes in this
     * {@code Citation} object will be reflected in the map, and conversely.
     *
     * @since 3.19
     */
    @Override
    public synchronized IdentifierMap getIdentifierMap() {
        if (identifierMap == null) {
            final Collection<Identifier> identifiers = getIdentifiers();
            if (identifiers == null) {
                return IdentifierMapAdapter.EMPTY;
            }
            identifierMap = IdentifierMapAdapter.create(Identifier.class,
                    new IdentifierList((List<Identifier>) identifiers));
        }
        return identifierMap;
    }

    /**
     * Sets the unique identifier for the resource. Example: Universal Product Code (UPC),
     * National Stock Number (NSN).
     *
     * @param newValues The new identifiers.
     */
    public synchronized void setIdentifiers(final Collection<? extends Identifier> newValues) {
        identifiers = copyCollection(newValues, identifiers, Identifier.class);
    }

    /**
     * Returns the name and position information for an individual or organization that is
     * responsible for the resource. Returns an empty string if there is none.
     */
    @Override
    @XmlElement(name = "citedResponsibleParty")
    public synchronized Collection<ResponsibleParty> getCitedResponsibleParties() {
        return citedResponsibleParties = nonNullCollection(citedResponsibleParties, ResponsibleParty.class);
    }

    /**
     * Sets the name and position information for an individual or organization that is responsible
     * for the resource. Returns an empty string if there is none.
     *
     * @param newValues The new cited responsible parties.
     */
    public synchronized void setCitedResponsibleParties(final Collection<? extends ResponsibleParty> newValues) {
        citedResponsibleParties = copyCollection(newValues, citedResponsibleParties, ResponsibleParty.class);
    }

    /**
     * Returns the mode in which the resource is represented, or an empty string if none.
     */
    @Override
    @XmlElement(name = "presentationForm")
    public synchronized Collection<PresentationForm> getPresentationForms() {
        return presentationForms = nonNullCollection(presentationForms, PresentationForm.class);
    }

    /**
     * Sets the mode in which the resource is represented, or an empty string if none.
     *
     * @param newValues The new presentation form.
     */
    public synchronized void setPresentationForms(final Collection<? extends PresentationForm> newValues) {
        presentationForms = copyCollection(newValues, presentationForms, PresentationForm.class);
    }

    /**
     * Returns the information about the series, or aggregate dataset, of which the dataset is
     * a part. Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "series")
    public synchronized Series getSeries() {
        return series;
    }

    /**
     * Sets the information about the series, or aggregate dataset, of which the dataset is
     * a part. Set to {@code null} if none.
     *
     * @param newValue The new series.
     */
    public synchronized void setSeries(final Series newValue) {
        checkWritePermission();
        series = newValue;
    }

    /**
     * Returns other information required to complete the citation that is not recorded elsewhere.
     * Returns {@code null} if none.
     */
    @Override
    @XmlElement(name = "otherCitationDetails")
    public synchronized InternationalString getOtherCitationDetails() {
        return otherCitationDetails;
    }

    /**
     * Sets other information required to complete the citation that is not recorded elsewhere.
     * Sets to {@code null} if none.
     *
     * @param newValue Other citations details.
     */
    public synchronized void setOtherCitationDetails(final InternationalString newValue) {
        checkWritePermission();
        otherCitationDetails = newValue;
    }

    /**
     * Returns the common title with holdings note. Note: title identifies elements of a series
     * collectively, combined with information about what volumes are available at the
     * source cited. Returns {@code null} if there is no title.
     */
    @Override
    @XmlElement(name = "collectiveTitle")
    public synchronized InternationalString getCollectiveTitle() {
        return collectiveTitle;
    }

    /**
     * Sets the common title with holdings note. Note: title identifies elements of a series
     * collectively, combined with information about what volumes are available at the
     * source cited. Set to {@code null} if there is no title.
     *
     * @param newValue The new collective title.
     */
    public synchronized void setCollectiveTitle(final InternationalString newValue) {
        checkWritePermission();
        collectiveTitle = newValue;
    }

    /**
     * Returns the International Standard Book Number, or {@code null} if none.
     *
     * @see Citations#ISBN
     */
    @Override
    @XmlElement(name = "ISBN")
    public synchronized String getISBN() {
        return ISBN;
    }

    /**
     * Sets the International Standard Book Number, or {@code null} if none.
     *
     * @param newValue The new ISBN.
     */
    public synchronized void setISBN(final String newValue) {
        checkWritePermission();
        ISBN = newValue;
    }

    /**
     * Returns the International Standard Serial Number, or {@code null} if none.
     *
     * @see Citations#ISSN
     */
    @Override
    @XmlElement(name = "ISSN")
    public synchronized String getISSN() {
        return ISSN;
    }

    /**
     * Sets the International Standard Serial Number, or {@code null} if none.
     *
     * @param newValue The new ISSN.
     */
    public synchronized void setISSN(final String newValue) {
        checkWritePermission();
        ISSN = newValue;
    }

    /**
     * The list of identifiers, which also include the ISBN and ISSN codes. This class wraps
     * an existing identifier list and appends the ISBN and ISSN codes to it, if those codes
     * are present.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.19
     *
     * @since 3.19
     * @module
     */
    @ThreadSafe
    private final class IdentifierList extends AbstractList<Identifier> implements Serializable {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -4827958271256100238L;

        /**
         * The original list of identifiers, without ISBN or ISSN codes.
         */
        private final List<Identifier> raw;

        /**
         * The ISBN and ISSN strings wrapped in {@link Identifier} objects when first needed.
         */
        private transient Identifier ISBN, ISSN;

        /**
         * Creates a new list of identifiers backed by the given "raw" (without ISBN or ISSN
         * codes) list.
         */
        IdentifierList(final List<Identifier> raw) {
            this.raw = raw;
        }

        /**
         * Returns the length of this list.
         */
        @Override
        public int size() {
            synchronized (DefaultCitation.this) {
                int size = raw.size();
                if (getISBN() != null) size++;
                if (getISSN() != null) size++;
                return size;
            }
        }

        /**
         * Returns the identifier for the given code, or {@code null} if the given code is null.
         *
         * @param  index 0 for ISBN or 1 for ISSN.
         * @param  code The code, or {@code null}
         * @return The identifier for the given code, or {@code null}.
         */
        private Identifier setCode(final int index, final String code) {
            if (code == null) {
                return null;
            }
            final Identifier old = (index == 0) ? ISBN : ISSN;
            final ISN_Identifier id;
            if (old instanceof ISN_Identifier) {
                id = (ISN_Identifier) old;
            } else {
                id = new ISN_Identifier(index == 0 ? Citations.ISBN : Citations.ISSN);
                switch (index) {
                    case 0: ISBN = id; break;
                    case 1: ISSN = id; break;
                }
            }
            id.setCode(code);
            return id;
        }

        /**
         * Returns the identifier at the given index. If ISBN and ISSN codes are present,
         * then they are the two last elements in this list.
         */
        @Override
        @SuppressWarnings("fallthrough")
        public Identifier get(final int index) {
            synchronized (DefaultCitation.this) {
                String code;
                switch (index - raw.size()) {
                    /*
                     * Strictly speaking we should also ensure that 'index < size()' since
                     * get(size+1) should fail if ISBN code is null.  Currently, we return
                     * the ISSN code anyway. We assume that it doesn't hurt so much, so we
                     * keep the implementation simple for now...
                     */
                    case 0: if ((code = getISBN()) != null) return setCode(0, code); // Fallthrough
                    case 1: if ((code = getISSN()) != null) return setCode(1, code); // Fallthrough
                }
                return raw.get(index);
            }
        }

        /**
         * Sets the identifier at the given index.
         */
        @Override
        @SuppressWarnings("fallthrough")
        public Identifier set(final int index, final Identifier identifier) {
            ArgumentChecks.ensureNonNull("identifier", identifier);
            synchronized (DefaultCitation.this) {
                Identifier old;
                switch (index - raw.size()) {
                    // The same comment than in the getter method apply also here.
                    case 0: if ((old=setCode(0, getISBN())) != null) {ISBN=identifier; return old;} // Fallthrough
                    case 1: if ((old=setCode(1, getISSN())) != null) {ISSN=identifier; return old;} // Fallthrough
                }
                return raw.set(index, identifier);
            }
        }

        /**
         * Removes the identifier at the given index.
         */
        @Override
        @SuppressWarnings("fallthrough")
        public Identifier remove(final int index) {
            synchronized (DefaultCitation.this) {
                Identifier old;
                switch (index - raw.size()) {
                    // The same comment than in the getter method apply also here.
                    case 0: if ((old=setCode(0, getISBN())) != null) {ISBN=null; return old;} // Fallthrough
                    case 1: if ((old=setCode(1, getISSN())) != null) {ISSN=null; return old;} // Fallthrough
                }
                return raw.remove(index);
            }
        }

        /**
         * Adds the given identifier. This method is slightly inconsistent with the standard
         * {@link List} contract in that the element may not be added at the end of the list.
         * However since the public API declares a {@link Collection} rather than a list, we
         * did not promised to add the element at the end of the list.
         */
        @Override
        public boolean add(final Identifier identifier) {
            ArgumentChecks.ensureNonNull("identifier", identifier);
            return raw.add(identifier); // Already synchronized.
        }
    }

    /**
     * An identifier with unmodifiable authority, used for ISBN or ISSN identifiers only.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.19
     *
     * @since 3.19
     * @module
     */
    private static final class ISN_Identifier extends DefaultIdentifier {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = -6356207807115132812L;

        /** Creates a new identifier for the given ISBN or ISSN authority. */
        ISN_Identifier(final Citation authority) {
            super.setAuthority(authority);
        }

        /** Do not allow modification of the authority. */
        @Override public void setAuthority(final Citation newValue) {
            throw new UnmodifiableMetadataException(Errors.format(Errors.Keys.UNMODIFIABLE_METADATA));
        }
    }
}
