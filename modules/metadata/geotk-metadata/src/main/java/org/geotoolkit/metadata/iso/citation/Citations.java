/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.citation;

import java.net.URI;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.ResponsibleParty;
import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.xml.IdentifierSpace;

import static java.util.Arrays.asList;
import java.util.Collections;
import static java.util.Collections.singleton;


/**
 * A set of pre-defined constants and static methods working on {@linkplain Citation citations}.
 * Pre-defined metadata constants are usually declared in implementation classes like
 * {@link DefaultResponsibleParty}. But citations are an exception since they are extensively
 * referenced in the Geotk library, and handling citations requires some convenience methods.
 * They are factored out in this {@code Citations} class for clarity.
 * <p>
 * The citation constants declared in this class are for:
 * <p>
 * <ul>
 *   <li><cite>Organizations</cite> (e.g. {@linkplain #OPEN_GIS OpenGIS})</li>
 *   <li><cite>Specifications</cite> (e.g. {@linkplain #WMS})</li>
 *   <li><cite>Authorities</cite> that maintain definitions of codes (e.g. {@linkplain #EPSG})</li>
 * </ul>
 * <p>
 * In the later case, the citations are actually of kind {@link IdentifierSpace}. The namespaces of
 * codes maintained by the authority are given by the {@linkplain Citation#getIdentifiers() citation
 * identifiers}. For example EPSG codes look like {@code "EPSG:4326"}, so the identifier for the
 * {@link #EPSG} citation is defined as {@code "EPSG"}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.20
 *
 * @since 2.2
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
public final class Citations extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    private Citations() {
    }

    /**
     * Constructs a citation with the specified title.
     *
     * @param title The title, as a {@link String} or an {@link InternationalString} object.
     * @param name  The field name in the {@link Citations} class.
     * @param identifier The identifier, or {@code null} if none.
     */
    private static DefaultCitation create(final CharSequence title, final String name, final String identifier) {
        final DefaultCitation citation = new DefaultCitation(title);
        setIdentifier(citation, identifier);
        return citation;
    }

    /**
     * Constructs a citation with the specified responsible party.
     *
     * @param party The name for an organization that is responsible for the resource.
     * @param name  The field name in the {@link Citations} class.
     * @param identifier The identifier, or {@code null} if none.
     */
    private static DefaultCitation create(final ResponsibleParty party, final String name, final String identifier) {
        final DefaultCitation citation = new DefaultCitation(party);
        setIdentifier(citation, identifier);
        return citation;
    }

    /**
     * Sets the alternative title.
     */
    private static void setAlternateTitle(final DefaultCitation citation, final String title) {
        assert !title.equals(citation.getTitle().toString(null)) : title;
        citation.setAlternateTitles(Collections.singleton(new SimpleInternationalString(title)));
    }

    /**
     * Sets the identifier. This is used as a convenience method for the creation of constants.
     */
    private static void setIdentifier(final DefaultCitation citation, final String identifier) {
        if (identifier != null) {
            citation.setIdentifiers(Collections.singleton(new DefaultIdentifier(identifier)));
        }
    }

    /**
     * Sets the presentation form to the given value. Any previous values are overwritten.
     */
    private static void setPresentationForm(final DefaultCitation citation, final PresentationForm form) {
        citation.setPresentationForms(Collections.singleton(form));
    }




    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////               O R G A N I S A T I O N S               ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    /**
     * Map Info is a spatial data software which defines its proper codes for CRS projection and datum codes.
     */
    public static final Citation MAP_INFO;
    static {
        final DefaultCitation c = create("MapInfo", "MAP_INFO", "MapInfo");
        c.freeze();
        MAP_INFO = c;
    }

    /**
     * The <A HREF="http://www.iso.org/">International Organization for Standardization</A>
     * organisation. An {@linkplain Citation#getAlternateTitles alternate title} for this
     * citation is "ISO" (according ISO 19115, alternate titles often contain abbreviations).
     *
     * @since 3.19
     */
    public static final Citation ISO;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.ISO, "ISO", "ISO");
        setAlternateTitle(c, "ISO");
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        ISO = c;
    }

    /**
     * The <A HREF="http://www.opengeospatial.org">Open Geospatial consortium</A> organisation.
     * "Open Geospatial consortium" is the new name for "OpenGIS consortium".
     * An {@linkplain Citation#getAlternateTitles alternate title} for this citation is "OGC"
     * (according ISO 19115, alternate titles often contain abbreviations).
     *
     * @see #OPEN_GIS
     * @see org.geotoolkit.io.wkt.Convention#OGC
     * @category Organisation
     */
    public static final Citation OGC;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.OGC, "OGC", "OGC");
        setAlternateTitle(c, "OGC");
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        OGC = c;
        // NOTE: Most OGC properties will be copied into OPEN_GIS as well.
    }

    /**
     * The <A HREF="http://www.opengis.org">OpenGIS consortium</A> organisation.
     * "OpenGIS consortium" is the old name for "Open Geospatial consortium".
     * {@linkplain Citation#getAlternateTitles Alternate titles} for this citation are
     * "OpenGIS" and "OGC" (according ISO 19115, alternate titles often contain abbreviations).
     *
     * @see #OGC
     * @category Organisation
     */
    public static final Citation OPEN_GIS;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.OPEN_GIS, "OPEN_GIS", null);
        c.setPresentationForms(OGC.getPresentationForms());
        c.setAlternateTitles  (OGC.getAlternateTitles());
        c.setIdentifiers      (OGC.getIdentifiers());
        c.getIdentifiers().add(new DefaultIdentifier("OpenGIS"));
        c.freeze();
        OPEN_GIS = c;
    }

    /**
     * The <A HREF="http://www.iho.int">International hydrographic organization</A>.
     *
     * @see #S57
     * @category Organisation
     *
     * @since 3.22
     */
    public static final Citation IHO;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.IHO, "IHO", "IHO");
        c.freeze();
        IHO = c;
    }

    /**
     * The <A HREF="http://www.esri.com">ESRI</A> organisation.
     * This company defines many Coordinate Reference Systems in addition to the {@linkplain #EPSG}
     * ones.
     *
     * @see org.geotoolkit.io.wkt.Convention#ESRI
     * @category Organisation
     */
    public static final Citation ESRI;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.ESRI, "ESRI", "ESRI");
        c.freeze();
        ESRI = c;
    }

    /**
     * The <A HREF="http://www.oracle.com">Oracle</A> organisation.
     *
     * @see org.geotoolkit.io.wkt.Convention#ORACLE
     * @category Organisation
     */
    public static final Citation ORACLE;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.ORACLE, "ORACLE", "Oracle");
        c.freeze();
        ORACLE = c;
    }

    /**
     * The <A HREF="http://postgis.refractions.net">PostGIS</A> project.
     *
     * @category Organisation
     *
     * @since 2.4
     */
    public static final Citation POSTGIS;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.POSTGIS, "POSTGIS", "PostGIS");
        c.freeze();
        POSTGIS = c;
    }

    /**
     * The <A HREF="http://www.geotoolkit.org">Geotoolkit.org</A> project.
     *
     * @category Organisation
     */
    public static final Citation GEOTOOLKIT;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.GEOTOOLKIT, "GEOTOOLKIT", "Geotk");
        c.freeze();
        GEOTOOLKIT = c;
    }

    /**
     * The <A HREF="http://www.geotools.org">GeoTools</A> project.
     *
     * @category Organisation
     */
    public static final Citation GEOTOOLS;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.GEOTOOLS, "GEOTOOLS", "GeoTools");
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
     * version numbers may be upgraded in future Geotk versions.
     *
     * @see <A HREF="http://www.opengeospatial.org/">Open Geospatial Consortium</A>
     * @see <A HREF="http://www.opengis.org/docs/01-068r3.pdf">WMS 1.1.1 specification</A>
     * @see <A HREF="http://portal.opengis.org/files/?artifact_id=5316">WMS 1.3.0 specification</A>
     * @category Specification
     */
    public static final Citation WMS;
    static {
        final DefaultCitation c = create("Web Map Service", "WMS", "WMS");
        c.setAlternateTitles(asList(
                new SimpleInternationalString("WMS 1.3.0"),
                new SimpleInternationalString("OGC 04-024"),
                new SimpleInternationalString("ISO 19128")));

        c.setCitedResponsibleParties(asList(
                DefaultResponsibleParty.OGC,
                DefaultResponsibleParty.OGC(Role.PUBLISHER, DefaultOnlineResource.WMS)));
        /*
         * The WMS specification is a model in a programming point of view, but this is not
         * the purpose of ISO 19115 PresentationForm.MODEL_DIGITAL in my understanding. The
         * later rather looks like the output of a numerical model (e.g. meteorological model).
         * The WMS specification is distributed as a PDF document.
         */
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        WMS = c;
    }

    /**
     * The <A HREF="http://www.unidata.ucar.edu/software/netcdf-java">NetCDF</A> specification.
     *
     * @see org.geotoolkit.io.wkt.Convention#NETCDF
     * @category Specification
     *
     * @since 3.08
     */
    public static final Citation NETCDF;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.NETCDF, "NETCDF", "NetCDF");
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        NETCDF = c;
    }

    /**
     * The <A HREF="http://cf-pcmdi.llnl.gov/">NetCDF Climate and Forecast (CF) Metadata Convention</A> specification.
     *
     * @category Specification
     *
     * @since 3.21
     */
    public static final Citation NETCDF_CF;
    static {
        // TODO: Needs its own responsibly party.
        final DefaultCitation c = create(DefaultResponsibleParty.NETCDF, "NETCDF_CF", "NetCDF-CF");
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.setTitle(new SimpleInternationalString("NetCDF Climate and Forecast (CF) Metadata Convention"));
        c.freeze();
        NETCDF_CF = c;
    }

    /**
     * The <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> specification.
     *
     * @see org.geotoolkit.io.wkt.Convention#GEOTIFF
     * @category Specification
     */
    public static final Citation GEOTIFF;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.GEOTIFF, "GEOTIFF", "GeoTIFF");
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        GEOTIFF = c;
    }

    /**
     * The <A HREF="http://www.iho.int/iho_pubs/standard/S-57Ed3.1/31Main.pdf">IHO transfer
     * standard for digital hydrographic data</A> specification.
     *
     * @see #IHO
     * @category Specification
     *
     * @since 3.22
     */
    public static final Citation S57;
    static {
        final DefaultCitation c = create(DefaultResponsibleParty.IHO, "S57", "S57");
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        S57 = c;
    }

    /**
     * The <A HREF="http://java.sun.com/products/java-media/jai">Java Advanced Imaging</A> library.
     * An {@linkplain Citation#getAlternateTitles alternate title} for this citation is "JAI"
     * (according ISO 19115, alternate titles often contain abbreviations).
     *
     * @category Specification
     */
    public static final Citation JAI;
    static {
        final DefaultCitation c = create("Java Advanced Imaging", "JAI", "JAI");
        setAlternateTitle(c, "JAI");
        c.setCitedResponsibleParties(singleton(DefaultResponsibleParty.SUN_MICROSYSTEMS));
        c.freeze();
        JAI = c;
    }




    ///////////////////////////////////////////////////////////////////////
    ////////                                                       ////////
    ////////                 A U T H O R I T I E S                 ////////
    ////////                                                       ////////
    ///////////////////////////////////////////////////////////////////////

    /**
     * The <A HREF="http://www.epsg.org">European Petroleum Survey Group</A> authority.
     * An {@linkplain Citation#getAlternateTitles alternate title} for this citation is
     * "EPSG" (according ISO 19115, alternate titles often contain abbreviations). In
     * addition, this citation contains the "EPSG" {@linkplain Citation#getIdentifiers identifier}.
     * <p>
     * This citation is used as an authority for
     * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory CRS
     * authority factory} on EPSG data, Geotk compares the {@code "EPSG"} string against the
     * {@linkplain Citation#getIdentifiers identifiers} (or against the {@linkplain Citation#getTitle
     * title} and {@linkplain Citation#getAlternateTitles alternate titles} if there is no identifier)
     * using the {@link #identifierMatches(Citation,String) identifierMatches} method.
     *
     * @see #AUTO
     * @see #AUTO2
     * @see #CRS
     * @see org.geotoolkit.io.wkt.Convention#EPSG
     * @category Code space
     */
    public static final IdentifierSpace<Integer> EPSG;
    static {
        final CitationConstant.Authority<Integer> c = new CitationConstant.Authority<>(
                DefaultResponsibleParty.EPSG, "EPSG", "EPSG");
        setAlternateTitle(c, "EPSG");
        setPresentationForm(c, PresentationForm.TABLE_DIGITAL);
        c.freeze();
        EPSG = c;
    }

    /**
     * The French mapping agency (<A HREF="http://www.ign.fr">Institut Géographique National</A>).
     * This agency defines many Coordinate Reference Systems in addition to the {@linkplain #EPSG}
     * ones.
     *
     * @category Code space
     *
     * @since 3.20
     */
    public static final IdentifierSpace<Integer> IGNF;
    static {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(Role.RESOURCE_PROVIDER);
        r.setOrganisationName(new SimpleInternationalString("Institut Géographique National"));
        r.setContactInfo(new DefaultContact(new DefaultOnlineResource(URI.create("http://www.ign.fr"))));
        final CitationConstant.Authority<Integer> c = new CitationConstant.Authority<>(r, "IGNF", "IGNF");
        c.getPresentationForms().add(PresentationForm.TABLE_DIGITAL);
        c.freeze();
        IGNF = c;
    }

    /**
     * The <A HREF="http://www.opengis.org/docs/01-068r3.pdf">WMS 1.1.1</A> "Automatic Projections"
     * authority. An {@linkplain Citation#getAlternateTitles alternate title} for this citation is
     * "AUTO" (according ISO 19115, alternate titles often contain abbreviations). In addition, this
     * citation contains the "AUTO" {@linkplain Citation#getIdentifiers identifier}.
     * <p>
     * <strong>Warning:</strong> {@code AUTO} is different from {@link #AUTO2} used for WMS 1.3.0.
     * <p>
     * This citation is used as an authority for
     * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory CRS
     * authority factory} on AUTO data, Geotk compares the {@code "AUTO"} string against the
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
    public static final IdentifierSpace<String> AUTO;
    static { // Sanity check ensure that all @see tags are actually available in the metadata
        final CitationConstant.Authority<String> c = new CitationConstant.Authority<>(
                "Automatic Projections", "AUTO", "AUTO");
        /*
         * Do not put "WMS 1.1.1" and "OGC 01-068r3" as alternative titles. They are alternative
         * titles for the WMS specification (see the WMS constant in this class), not for the
         * AUTO authority name.
         */
        c.setCitedResponsibleParties(asList(
                DefaultResponsibleParty.OGC,
                DefaultResponsibleParty.OGC(Role.PUBLISHER, OnLineFunction.DOWNLOAD, "http://www.opengis.org/docs/01-068r3.pdf")));
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL); // See comment in WMS.
        c.freeze();
        AUTO = c;
    }

    // Do not put the ...files/?artifact... link in the head sentence: it break javadoc formatting.
    /**
     * The WMS 1.3.0 "Automatic Projections" authority. An {@linkplain Citation#getAlternateTitles
     * alternate title} for this citation is "AUTO2" (according ISO 19115, alternate titles often
     * contain abbreviations). In addition, this citation contains the "AUTO2"
     * {@linkplain Citation#getIdentifiers identifier}.
     * <p>
     * <strong>Warning:</strong> {@code AUTO2} is different from {@link #AUTO} used for WMS 1.1.1
     * and earlier.
     * <p>
     * This citation is used as an authority for
     * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
     * identifiers. When searching an {@linkplain org.opengis.referencing.crs.CRSAuthorityFactory CRS
     * authority factory} on AUTO2 data, Geotk compares the {@code "AUTO2"} string against the
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
    public static final IdentifierSpace<String> AUTO2;
    static {
        final CitationConstant.Authority<String> c = new CitationConstant.Authority<>(
                "Automatic Projections", "AUTO2", "AUTO2");
        /*
         * Do not put "WMS 1.3.0" and "OGC 04-024" as alternative titles. They are alternative
         * titles for the WMS specification (see the WMS constant in this class), not for the
         * AUTO2 authority name.
         */
        c.setCitedResponsibleParties(asList(
                DefaultResponsibleParty.OGC,
                DefaultResponsibleParty.OGC(Role.PUBLISHER, DefaultOnlineResource.WMS)));
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL); // See comment in WMS.
        c.freeze();
        AUTO2 = c;
    }

    // Do not put the ...files/?artifact... link in the head sentence: it break javadoc formatting.
    /**
     * The WMS 1.3.0 "CRS" authority. This is defined in the same document than {@link #AUTO2}.
     * This citation declares both {@code "CRS"} and {@code "OGC"} identifiers, because we see
     * both {@code "CRS:84"} and {@code "OGC:CRS84"} in practice.
     *
     * @see #WMS
     * @see #AUTO
     * @see #AUTO2
     * @see #CRS
     * @see #EPSG
     * @category Code space
     */
    public static final IdentifierSpace<String> CRS;
    static {
        final CitationConstant.Authority<String> c = new CitationConstant.Authority<>(
                "Web Map Service CRS", "CRS", "CRS");
        c.getIdentifiers().add(new DefaultIdentifier("OGC"));
        c.setCitedResponsibleParties(AUTO2.getCitedResponsibleParties());
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL); // See comment in WMS.
        c.freeze();
        CRS = c;
    }

    /**
     * URN in the OGC namespace. This citation contains the {@code "urn:ogc:def"} and
     * {@code "urn:x-ogc:def"} {@linkplain Citation#getIdentifiers() identifiers}.
     *
     * @category Code space
     * @since 2.4
     */
    public static final IdentifierSpace<URI> URN_OGC;
    static {
        final CitationConstant.Authority<URI> c = new CitationConstant.Authority<>(
                "URN in OGC namespace", "URN_OGC", "urn:ogc:def");
        c.getIdentifiers().add(new DefaultIdentifier("urn:x-ogc:def"));
        c.setCitedResponsibleParties(singleton(DefaultResponsibleParty.OGC));
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        URN_OGC = c;
    }

    /**
     * URL in the OGC namespace. This citation contains the {@code "http://www.opengis.net"}
     * {@linkplain Citation#getIdentifiers() identifiers}.
     *
     * @category Code space
     * @since 2.4
     */
    public static final IdentifierSpace<URI> HTTP_OGC;
    static {
        final CitationConstant.Authority<URI> c = new CitationConstant.Authority<>(
                "URL in OGC namespace", "HTTP_OGC", "http://www.opengis.net");
        c.setCitedResponsibleParties(singleton(DefaultResponsibleParty.OGC));
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        HTTP_OGC = c;
    }

    /**
     * The <A HREF="http://trac.osgeo.org/proj/">Proj.4</A> project.
     *
     * @see org.geotoolkit.io.wkt.Convention#PROJ4
     * @category Specification
     *
     * @since 3.20
     */
    public static final IdentifierSpace<String> PROJ4;
    static {
        final CitationConstant.Authority<String> c = new CitationConstant.Authority<>(
                DefaultResponsibleParty.PROJ4, "PROJ4", "PROJ4");
        setPresentationForm(c, PresentationForm.DOCUMENT_DIGITAL);
        c.freeze();
        PROJ4 = c;
    }

    /**
     * <cite>International Standard Book Number</cite> (ISBN) defined by
     * {@linkplain DefaultResponsibleParty#ISO ISO}-2108. The ISO-19115 metadata standard
     * defines a specific attribute for this information, but the Geotk library handles it
     * like any other identifier.
     *
     * @see Citation#getISBN()
     *
     * @category Code space
     * @since 3.19
     */
    public static final IdentifierSpace<String> ISBN = org.apache.sis.metadata.iso.citation.Citations.ISBN;

    /**
     * <cite>International Standard Serial Number</cite> (ISSN) defined by
     * {@linkplain DefaultResponsibleParty#ISO ISO}-3297. The ISO-19115 metadata standard
     * defines a specific attribute for this information, but the Geotk library handles it
     * like any other identifier.
     *
     * @see Citation#getISSN()
     *
     * @category Code space
     * @since 3.19
     */
    public static final IdentifierSpace<String> ISSN = org.apache.sis.metadata.iso.citation.Citations.ISSN;

    /**
     * Unknown authority, vendor or specification.
     *
     * @since 3.05
     */
    public static final Citation UNKNOWN;
    static {
        final DefaultCitation c = create(
                Vocabulary.formatInternational(Vocabulary.Keys.UNKNOWN), "UNKNOWN", null);
        c.freeze();
        UNKNOWN = c;
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
        OGC, OPEN_GIS, ESRI, ORACLE, POSTGIS, GEOTOOLKIT, MAP_INFO, GEOTOOLS, WMS, NETCDF, GEOTIFF, JAI,
        EPSG, AUTO, AUTO2, CRS, URN_OGC, HTTP_OGC, UNKNOWN
    };

    /**
     * Returns a citation of the given name. The method makes the following choice:
     * <p>
     * <ul>
     *   <li>If the given title is {@code null} or empty (ignoring spaces), then this method
     *       returns {@code null}.</li>
     *   <li>Otherwise if the given name matches a {@linkplain Citation#getTitle title} or an
     *       {@linkplain Citation#getAlternateTitles alternate titles} of one of the pre-defined
     *       constants ({@link #EPSG}, {@link #GEOTIFF}, <i>etc.</i>), then that constant
     *       is returned.</li>
     *   <li>Otherwise, a new citation is created with the specified name as the title.</li>
     * </ul>
     *
     * @param  title The citation title (or alternate title), or {@code null}.
     * @return A citation using the specified name, or {@code null} if the given title is null
     *         or empty.
     */
    public static Citation fromName(String title) {
        if (title == null || ((title = title.trim()).isEmpty())) {
            return null;
        }
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
        ArgumentChecks.ensureNonNull("c1", c1);
        ArgumentChecks.ensureNonNull("c2", c2);
        return org.apache.sis.internal.util.Citations.titleMatches(c1, c2);
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
        ArgumentChecks.ensureNonNull("citation", citation);
        ArgumentChecks.ensureNonNull("title", title);
        return org.apache.sis.internal.util.Citations.titleMatches(citation, title);
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
    public static boolean identifierMatches(final Citation c1, final Citation c2) {
        ArgumentChecks.ensureNonNull("c1", c1);
        ArgumentChecks.ensureNonNull("c2", c2);
        return org.apache.sis.internal.util.Citations.identifierMatches(c1, c2);
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
    public static boolean identifierMatches(final Citation citation, final String identifier) {
        ArgumentChecks.ensureNonNull("citation", citation);
        ArgumentChecks.ensureNonNull("identifier", identifier);
        return org.apache.sis.internal.util.Citations.identifierMatches(citation, identifier);
    }

    /**
     * Returns the shortest identifier for the specified citation, or the title if there is
     * no identifier. This method is useful for extracting the namespace from an authority,
     * for example {@code "EPSG"}.
     *
     * @param  citation The citation for which to get the identifier, or {@code null}.
     * @return The shortest identifier of the given citation, or {@code null} if the
     *         given citation was null or doesn't declare any identifier or title.
     *
     * @since 2.4
     */
    public static String getIdentifier(final Citation citation) {
        return org.apache.sis.internal.util.Citations.getIdentifier(citation);
    }
}
