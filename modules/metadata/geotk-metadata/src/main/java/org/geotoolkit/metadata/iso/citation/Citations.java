/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.citation;

import java.util.Collection;
import java.util.Iterator;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Role;
import org.opengis.util.InternationalString;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * A set of pre-defined constants and static methods working on {@linkplain Citation citations}.
 * Pre-defined metadata constants are usually declared in implementation classes like
 * {@link DefaultResponsibleParty}. But citations are an exception since they are extensively
 * referenced in the Geotoolkit library, and handling citations requires some convenience methods.
 * They are factored out in this {@code Citations} class for clarity.
 * <p>
 * Citations may be about an <cite>organisation</cite> (e.g. {@linkplain #OPEN_GIS OpenGIS}),
 * a <cite>specification</cite> (e.g. {@linkplain #WMS}) or an <cite>authority</cite> that
 * maintains definitions of codes (e.g. {@linkplain #EPSG}). In the later case, the citation
 * contains an {@linkplain Citation#getIdentifiers identifier} which is the namespace of the
 * codes maintained by the authority. For example the identifier for the {@link #EPSG} citation
 * is {@code "EPSG"}, and EPSG codes look like {@code "EPSG:4326"}.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett (Refractions)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public final class Citations {
    /**
     * Do not allows instantiation of this class.
     */
    private Citations() {
    }




    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////               O R G A N I S A T I O N S               ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    /**
     * The <A HREF="http://www.opengeospatial.org">Open Geospatial consortium</A> organisation.
     * "Open Geospatial consortium" is the new name for "OpenGIS consortium".
     * An {@linkplain Citation#getAlternateTitles alternate title} for this citation is "OGC"
     * (according ISO 19115, alternate titles often contain abreviations).
     *
     * @see DefaultResponsibleParty#OGC
     * @see #OPEN_GIS
     * @category Organisation
     */
    public static final Citation OGC;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.OGC, "OGC");
        c.addAuthority("OGC", false);
        c.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        OGC = c;
        // NOTE: Most OGC properties will be copied into OPEN_GIS as well.
    }

    /**
     * The <A HREF="http://www.opengis.org">OpenGIS consortium</A> organisation.
     * "OpenGIS consortium" is the old name for "Open Geospatial consortium".
     * {@linkplain Citation#getAlternateTitles Alternate titles} for this citation are
     * "OpenGIS" and "OGC" (according ISO 19115, alternate titles often contain abreviations).
     *
     * @see DefaultResponsibleParty#OPEN_GIS
     * @see #OGC
     * @category Organisation
     */
    public static final Citation OPEN_GIS;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.OPEN_GIS, "OPEN_GIS");
        c.addAuthority("OpenGIS", false);
        c.getAlternateTitles()  .addAll(OGC.getAlternateTitles());
        c.getIdentifiers()      .addAll(OGC.getIdentifiers());
        c.getPresentationForms().addAll(OGC.getPresentationForms());
        c.freeze();
        OPEN_GIS = c;
    }

    /**
     * The <A HREF="http://www.esri.com">ESRI</A> organisation.
     *
     * @see DefaultResponsibleParty#ESRI
     * @category Organisation
     */
    public static final Citation ESRI;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.ESRI, "ESRI");
        c.addAuthority("ESRI", false);
        c.freeze();
        ESRI = c;
    }

    /**
     * The <A HREF="http://www.oracle.com">Oracle</A> organisation.
     *
     * @see DefaultResponsibleParty#ORACLE
     * @category Organisation
     */
    public static final Citation ORACLE;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.ORACLE, "ORACLE");
        c.addAuthority("Oracle", false);
        c.freeze();
        ORACLE = c;
    }

    /**
     * The <A HREF="http://postgis.refractions.net">PostGIS</A> project.
     *
     * @see DefaultResponsibleParty#POSTGIS
     * @category Organisation
     *
     * @since 2.4
     */
    public static final Citation POSTGIS;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.POSTGIS, "POSTGIS");
        c.addAuthority("PostGIS", false);
        c.freeze();
        POSTGIS = c;
    }

    /**
     * The <A HREF="http://www.geotoolkit.org">Geotoolkit.org</A> project.
     *
     * @see DefaultResponsibleParty#GEOTOOLKIT
     * @category Organisation
     */
    public static final Citation GEOTOOLKIT;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.GEOTOOLKIT, "GEOTOOLKIT");
        c.addAuthority("Geotoolkit.org", false);
        c.freeze();
        GEOTOOLKIT = c;
    }

    /**
     * The <A HREF="http://www.geotools.org">GeoTools</A> project.
     *
     * @see DefaultResponsibleParty#GEOTOOLS
     * @category Organisation
     */
    public static final Citation GEOTOOLS;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.GEOTOOLS, "GEOTOOLS");
        c.addAuthority("GeoTools", false);
        c.freeze();
        GEOTOOLS = c;
    }




    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////              S P E C I F I C A T I O N S              ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    // Do not put the ...files/?artifact... link in the head sentence: it break javadoc formatting.
    /**
     * The Web Map Service specification. {@linkplain Citation#getAlternateTitles Alternate titles}
     * for this citation are "WMS", "WMS 1.3.0", "OGC 04-024" and "ISO 19128". Note that the
     * version numbers may be upgrated in future Geotoolkit versions.
     *
     * @see <A HREF="http://www.opengeospatial.org/">Open Geospatial Consortium</A>
     * @see <A HREF="http://www.opengis.org/docs/01-068r3.pdf">WMS 1.1.1 specification</A>
     * @see <A HREF="http://portal.opengis.org/files/?artifact_id=5316">WMS 1.3.0 specification</A>
     * @see DefaultResponsibleParty#OGC
     * @see DefaultOnLineResource#WMS
     * @category Specification
     */
    public static final Citation WMS;
    static {
        final DefaultCitation c = new CitationConstant("Web Map Service", "WMS");
        c.addAuthority("WMS", false);
        final Collection<InternationalString> titles = c.getAlternateTitles();
        titles.add(new SimpleInternationalString("WMS 1.3.0"));
        titles.add(new SimpleInternationalString("OGC 04-024"));
        titles.add(new SimpleInternationalString("ISO 19128"));

        final Collection<ResponsibleParty> parties = c.getCitedResponsibleParties();
        parties.add(DefaultResponsibleParty.OGC);
        parties.add(DefaultResponsibleParty.OGC(Role.PUBLISHER, DefaultOnLineResource.WMS));
        /*
         * The WMS specification is a model in a programming point of view, but this is not
         * the purpose of ISO 19115 PresentationForm.MODEL_DIGITAL in my understanding. The
         * later rather looks like the output of a numerical model (e.g. meteorological model).
         * The WMS specification is distributed as a PDF document.
         */
        c.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        WMS = c;
    }

    /**
     * The <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> specification.
     *
     * @see DefaultResponsibleParty#GEOTIFF
     * @category Specification
     */
    public static final Citation GEOTIFF;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.GEOTIFF, "GEOTIFF");
        c.addAuthority("GeoTIFF", false);
        c.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        GEOTIFF = c;
    }

    /**
     * The <A HREF="http://java.sun.com/products/java-media/jai">Java Advanced Imaging</A> library.
     * An {@linkplain Citation#getAlternateTitles alternate title} for this citation is "JAI"
     * (according ISO 19115, alternate titles often contain abreviations).
     *
     * @see DefaultResponsibleParty#SUN_MICROSYSTEMS
     * @category Specification
     */
    public static final Citation JAI;
    static {
        final DefaultCitation c = new CitationConstant("Java Advanced Imaging", "JAI");
        c.addAuthority("JAI", true);
        c.getCitedResponsibleParties().add(DefaultResponsibleParty.SUN_MICROSYSTEMS);
        c.freeze();
        JAI = c;
    }




    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////             C R S   A U T H O R I T I E S             ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    /**
     * The <A HREF="http://www.epsg.org">European Petroleum Survey Group</A> authority.
     * An {@linkplain Citation#getAlternateTitles alternate title} for this citation is
     * "EPSG" (according ISO 19115, alternate titles often contain abreviations). In
     * addition, this citation contains the "EPSG" {@linkplain Citation#getIdentifiers identifier}.
     * <p>
     * This citation is used as an authority for
     * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory CRS
     * authority factory} on EPSG data, Geotoolkit compares the {@code "EPSG"} string against the
     * {@linkplain Citation#getIdentifiers identifiers} (or against the {@linkplain Citation#getTitle
     * title} and {@linkplain Citation#getAlternateTitles alternate titles} if there is no identifier)
     * using the {@link #identifierMatches(Citation,String) identifierMatches} method.
     *
     * @see DefaultResponsibleParty#EPSG
     * @see #AUTO
     * @see #AUTO2
     * @see #CRS
     * @category Code space
     */
    public static final Citation EPSG;
    static {
        final DefaultCitation c = new CitationConstant(DefaultResponsibleParty.EPSG, "EPSG");
        c.addAuthority("EPSG", true);
        c.getPresentationForms().add(PresentationForm.TABLE_DIGITAL);
        c.freeze();
        EPSG = c;
    }

    /**
     * The <A HREF="http://www.opengis.org/docs/01-068r3.pdf">WMS 1.1.1</A> "Automatic Projections"
     * authority. An {@linkplain Citation#getAlternateTitles alternate title} for this citation is
     * "AUTO" (according ISO 19115, alternate titles often contain abreviations). In addition, this
     * citation contains the "AUTO" {@linkplain Citation#getIdentifiers identifier}.
     * <p>
     * <strong>Warning:</strong> {@code AUTO} is different from {@link #AUTO2} used for WMS 1.3.0.
     * <p>
     * This citation is used as an authority for
     * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory CRS
     * authority factory} on AUTO data, Geotoolkit compares the {@code "AUTO"} string against the
     * {@linkplain Citation#getIdentifiers identifiers} (or against the {@linkplain Citation#getTitle
     * title} and {@linkplain Citation#getAlternateTitles alternate titles} if there is no identifier)
     * using the {@link #identifierMatches(Citation,String) identifierMatches} method.
     *
     * @see <A HREF="http://www.opengeospatial.org/">Open Geospatial Consortium</A>
     * @see <A HREF="http://www.opengis.org/docs/01-068r3.pdf">WMS 1.1.1 specification</A>
     * @see #WMS
     * @see #AUTO2
     * @see #CRS
     * @see #EPSG
     * @category Code space
     */
    public static final Citation AUTO;
    static { // Sanity check ensure that all @see tags are actually available in the metadata
        final DefaultCitation c = new CitationConstant("Automatic Projections", "AUTO");
        c.addAuthority("AUTO", false);
        /*
         * Do not put "WMS 1.1.1" and "OGC 01-068r3" as alternative titles. They are alternative
         * titles for the WMS specification (see the WMS constant in this class), not for the
         * AUTO authority name.
         */
        final Collection<ResponsibleParty> parties = c.getCitedResponsibleParties();
        parties.add(DefaultResponsibleParty.OGC);
        parties.add(DefaultResponsibleParty.OGC(Role.PUBLISHER, OnLineFunction.DOWNLOAD,
                                             "http://www.opengis.org/docs/01-068r3.pdf"));
        c.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL); // See comment in WMS.
        c.freeze();
        AUTO = c;
    }

    // Do not put the ...files/?artifact... link in the head sentence: it break javadoc formatting.
    /**
     * The WMS 1.3.0 "Automatic Projections" authority. An {@linkplain Citation#getAlternateTitles
     * alternate title} for this citation is "AUTO2" (according ISO 19115, alternate titles often
     * contain abreviations). In addition, this citation contains the "AUTO2"
     * {@linkplain Citation#getIdentifiers identifier}.
     * <p>
     * <strong>Warning:</strong> {@code AUTO2} is different from {@link #AUTO} used for WMS 1.1.1
     * and earlier.
     * <p>
     * This citation is used as an authority for
     * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory CRS
     * authority factory} on AUTO2 data, Geotoolkit compares the {@code "AUTO2"} string against the
     * {@linkplain Citation#getIdentifiers identifiers} (or against the {@linkplain Citation#getTitle
     * title} and {@linkplain Citation#getAlternateTitles alternate titles} if there is no identifier)
     * using the {@link #identifierMatches(Citation,String) identifierMatches} method.
     *
     * @see <A HREF="http://www.opengeospatial.org/">Open Geospatial Consortium</A>
     * @see <A HREF="http://portal.opengis.org/files/?artifact_id=5316">WMS 1.3.0 specification</A>
     * @see #WMS
     * @see #AUTO
     * @see #CRS
     * @see #EPSG
     * @category Code space
     */
    public static final Citation AUTO2;
    static {
        final DefaultCitation c = new CitationConstant("Automatic Projections", "AUTO2");
        c.addAuthority("AUTO2", false);
        /*
         * Do not put "WMS 1.3.0" and "OGC 04-024" as alternative titles. They are alternative
         * titles for the WMS specification (see the WMS constant in this class), not for the
         * AUTO2 authority name.
         */
        final Collection<ResponsibleParty> parties = c.getCitedResponsibleParties();
        parties.add(DefaultResponsibleParty.OGC);
        parties.add(DefaultResponsibleParty.OGC(Role.PUBLISHER, DefaultOnLineResource.WMS));
        c.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL); // See comment in WMS.
        c.freeze();
        AUTO2 = c;
    }

    // Do not put the ...files/?artifact... link in the head sentence: it break javadoc formatting.
    /**
     * The WMS 1.3.0 "CRS" authority. This is defined in the same document than {@link #AUTO2}.
     *
     * @see #WMS
     * @see #AUTO
     * @see #AUTO2
     * @see #CRS
     * @see #EPSG
     * @category Code space
     */
    public static final Citation CRS;
    static {
        final DefaultCitation c = new CitationConstant("Web Map Service CRS", "CRS");
        c.addAuthority("CRS", false);
        c.getCitedResponsibleParties().addAll(AUTO2.getCitedResponsibleParties());
        c.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL); // See comment in WMS.
        c.freeze();
        CRS = c;
    }

    /**
     * URN in the OGC namespace. This citation contains the {@code "urn:ogc:def"} and
     * {@code "urn:x-ogc:def"} {@linkplain Citation#getIdentifiers identifiers}.
     *
     * @category Code space
     * @since 2.4
     */
    public static final Citation URN_OGC;
    static {
        final DefaultCitation c = new CitationConstant("URN in OGC namespace", "URN_OGC");
        c.addAuthority("urn:ogc:def", false);
        c.addAuthority("urn:x-ogc:def", false);
        c.getCitedResponsibleParties().add(DefaultResponsibleParty.OGC);
        c.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        URN_OGC = c;
    }

    /**
     * URL in the OGC namespace. This citation contains the
     * {@code "http://www.opengis.net"} {@linkplain Citation#getIdentifiers identifiers}.
     *
     * @category Code space
     * @since 2.4
     */
    public static final Citation HTTP_OGC;
    static {
        final DefaultCitation c = new CitationConstant("URL in OGC namespace", "HTTP_OGC");
        c.addAuthority("http://www.opengis.net", false);
        c.getCitedResponsibleParties().add(DefaultResponsibleParty.OGC);
        c.getPresentationForms().add(PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        HTTP_OGC = c;
    }




    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////             End of constants declarations             ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    /**
     * List of citations declared in this class.
     */
    private static final Citation[] AUTHORITIES = {
        OGC, OPEN_GIS, ESRI, ORACLE, GEOTOOLKIT, WMS, GEOTIFF, JAI, EPSG, AUTO, AUTO2, CRS
    };

    /**
     * Returns a citation of the given name. If the given name matches a {@linkplain
     * Citation#getTitle title} or an {@linkplain Citation#getAlternateTitles alternate titles}
     * of one of the pre-defined constants ({@link #EPSG}, {@link #GEOTIFF}, <cite>etc.</cite>),
     * then this constant is returned. Otherwise, a new citation is created with the specified
     * name as the title.
     *
     * @param  title The citation title (or alternate title).
     * @return A citation using the specified name
     */
    public static Citation fromName(final String title) {
        for (int i=0; i<AUTHORITIES.length; i++) {
            final Citation citation = AUTHORITIES[i];
            if (titleMatches(citation, title)) {
                return citation;
            }
        }
        return new DefaultCitation(title);
    }

    /**
     * Returns {@code true} if at least one {@linkplain Citation#getTitle title} or
     * {@linkplain Citation#getAlternateTitles alternate title} in {@code c1} is equal to a title
     * or alternate title in {@code c2}. The comparison is case-insensitive and ignores leading
     * and trailing spaces. The titles ordering is not significant.
     *
     * @param  c1 The first citation to compare.
     * @param  c2 the second citation to compare.
     * @return {@code true} if at least one title or alternate title matches.
     */
    public static boolean titleMatches(final Citation c1, final Citation c2) {
        InternationalString candidate = c2.getTitle();
        Iterator<? extends InternationalString> iterator = null;
        do {
            // The "null" locale argument is required for getting the unlocalized version.
            final String asString = candidate.toString(null);
            if (titleMatches(c1, asString)) {
                return true;
            }
            final String asLocalized = candidate.toString();
            if (asLocalized!=asString && titleMatches(c1, asLocalized)) {
                return true;
            }
            if (iterator == null) {
                final Collection<? extends InternationalString> titles = c2.getAlternateTitles();
                if (titles == null) {
                    break;
                }
                iterator = titles.iterator();
            }
            if (!iterator.hasNext()) {
                break;
            }
            candidate = iterator.next();
        } while (true);
        return false;
    }

    /**
     * Returns {@code true} if the {@linkplain Citation#getTitle title} or any
     * {@linkplain Citation#getAlternateTitles alternate title} in the given citation
     * matches the given string. The comparison is case-insensitive and ignores leading
     * and trailing spaces.
     *
     * @param  citation The citation to check for.
     * @param  title The title or alternate title to compare.
     * @return {@code true} if the title or alternate title matches the given string.
     */
    public static boolean titleMatches(final Citation citation, String title) {
        title = title.trim();
        InternationalString candidate = citation.getTitle();
        Iterator<? extends InternationalString> iterator = null;
        do {
            // The "null" locale argument is required for getting the unlocalized version.
            final String asString = candidate.toString(null);
            if (asString.trim().equalsIgnoreCase(title)) {
                return true;
            }
            final String asLocalized = candidate.toString();
            if (asLocalized!=asString && asLocalized.trim().equalsIgnoreCase(title)) {
                return true;
            }
            if (iterator == null) {
                final Collection<? extends InternationalString> titles = citation.getAlternateTitles();
                if (titles == null) {
                    break;
                }
                iterator = titles.iterator();
            }
            if (!iterator.hasNext()) {
                break;
            }
            candidate = iterator.next();
        } while (true);
        return false;
    }

    /**
     * Returns {@code true} if at least one {@linkplain Citation#getIdentifiers identifier} in
     * {@code c1} is equal to an identifier in {@code c2}. The comparison is case-insensitive
     * and ignores leading and trailing spaces. The identifier ordering is not significant.
     * <p>
     * If (and <em>only</em> if) the citations do not contains any identifier, then this method
     * fallback on titles comparison using the {@link #titleMatches(Citation,Citation)
     * titleMatches} method. This fallback exists for compatibility with client codes using
     * citation {@linkplain Citation#getTitle titles} without identifiers.
     *
     * @param  c1 The first citation to compare.
     * @param  c2 the second citation to compare.
     * @return {@code true} if at least one identifier, title or alternate title matches.
     */
    public static boolean identifierMatches(Citation c1, Citation c2) {
        if (c1 == c2) {
            return true; // Optimisation for a common case.
        }
        /*
         * If there is no identifier in both citations, fallback on title comparisons. If there is
         * identifiers in only one citation, make sure that this citation is the second one (c2) in
         * order to allow at least one call to 'identifierMatches(c1, String)'.
         */
        Iterator<? extends Identifier> iterator = c2.getIdentifiers().iterator();
        if (!iterator.hasNext()) {
            iterator = c1.getIdentifiers().iterator();
            if (!iterator.hasNext()) {
                return titleMatches(c1, c2);
            }
            c1 = c2;
            c2 = null; // Just for make sure that we don't use it by accident.
        }
        do {
            final String id = iterator.next().getCode().trim();
            if (identifierMatches(c1, id)) {
                return true;
            }
        } while (iterator.hasNext());
        return false;
    }

    /**
     * Returns {@code true} if any {@linkplain Citation#getIdentifiers identifiers} in the given
     * citation matches the given string. The comparison is case-insensitive and ignores leading
     * and trailing spaces. If (and <em>only</em> if) the citation do not contains any identifier,
     * then this method fallback on titles comparison using the {@link #titleMatches(Citation,
     * String) titleMatches} method. This fallback exists for compatibility with client codes using
     * citation {@linkplain Citation#getTitle titles} without identifiers.
     *
     * @param  citation The citation to check for.
     * @param  identifier The identifier to compare.
     * @return {@code true} if the title or alternate title matches the given string.
     */
    public static boolean identifierMatches(final Citation citation, String identifier) {
        identifier = identifier.trim();
        final Collection<? extends Identifier> identifiers = citation.getIdentifiers();
        for (final Identifier id : identifiers) {
            final String code = id.getCode().trim();
            if (identifier.equalsIgnoreCase(code)) {
                return true;
            }
        }
        if (identifiers.isEmpty()) {
            return titleMatches(citation, identifier);
        } else {
            return false;
        }
    }

    /**
     * Returns the shortest identifier for the specified citation, or the title if there is
     * no identifier. This method is useful for extracting the namespace from an authority,
     * for example {@code "EPSG"}.
     *
     * @param  citation The citation for which to get the identifier, or {@code null}.
     * @return The shortest identifier of the given citation, or {@code null} if the
     *         given citation was null.
     *
     * @since 2.4
     */
    public static String getIdentifier(final Citation citation) {
        String identifier = null;
        if (citation != null) {
            for (final Identifier id : citation.getIdentifiers()) {
                final String candidate = id.getCode().trim();
                final int length = candidate.length();
                if (length != 0) {
                    if (identifier == null || length < identifier.length()) {
                        identifier = candidate;
                    }
                }
            }
            if (identifier == null) {
                identifier = String.valueOf(citation.getTitle());
            }
        }
        return identifier;
    }
}
