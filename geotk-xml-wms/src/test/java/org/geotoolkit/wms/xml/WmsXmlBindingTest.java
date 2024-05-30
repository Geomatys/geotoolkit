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
package org.geotoolkit.wms.xml;

import java.awt.Dimension;
import org.geotoolkit.wms.xml.v111.VendorSpecificCapabilities;
import java.io.StringReader;
import java.util.List;
import org.geotoolkit.inspire.xml.vs.LanguageType;
import org.opengis.metadata.citation.ResponsibleParty;
import java.util.Arrays;
import java.util.Date;
import java.net.URI;
import org.opengis.util.NameFactory;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import jakarta.xml.bind.JAXBContext;
import org.opengis.metadata.citation.Citation;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.time.Instant;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.privy.Constants;
import org.apache.sis.util.privy.URLs;
import org.apache.sis.xml.privy.LegacyNamespaces;
import org.apache.sis.util.DefaultInternationalString;

import org.geotoolkit.inspire.xml.vs.ExtendedCapabilitiesType;
import org.geotoolkit.inspire.xml.vs.LanguagesType;
import org.geotoolkit.inspire.xml.vs.ObjectFactory;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.citation.DefaultContact;
import org.apache.sis.metadata.iso.citation.DefaultOnlineResource;
import org.apache.sis.metadata.iso.citation.DefaultOrganisation;
import org.apache.sis.metadata.iso.citation.DefaultResponsibility;
import org.apache.sis.metadata.iso.citation.DefaultResponsibleParty;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultTemporalExtent;
import org.apache.sis.metadata.iso.identification.DefaultKeywords;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.iso.DefaultNameFactory;
import org.geotoolkit.service.ServiceType;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.InstantWrapper;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.wms.xml.v111.BoundingBox;
import org.geotoolkit.wms.xml.v130.Capability;
import org.geotoolkit.wmsc.xml.v111.TileSet;

import org.apache.sis.xml.MarshallerPool;
import org.junit.*;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.temporal.Period;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.xml.XML;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.opengis.referencing.IdentifiedObject;

import static org.junit.Assert.*;
import static org.geotoolkit.test.Assertions.assertXmlEquals;
import static org.geotoolkit.test.TestUtilities.getSingleton;
import org.apache.sis.util.Version;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class WmsXmlBindingTest {
    private static final DefaultCitation EPSG;
    static {
        final DefaultOnlineResource r = new DefaultOnlineResource(URI.create(URLs.EPSG));
        r.setFunction(OnLineFunction.INFORMATION);

        final DefaultResponsibility p = new DefaultResponsibility(Role.PRINCIPAL_INVESTIGATOR, null,
                new DefaultOrganisation("International Association of Oil & Gas Producers", null, null, new DefaultContact(r)));

        final DefaultCitation c = new DefaultCitation("EPSG Geodetic Parameter Dataset");
        c.getPresentationForms().add(PresentationForm.TABLE_DIGITAL);
        c.getIdentifiers().add(new DefaultIdentifier(Constants.EPSG));
        c.getCitedResponsibleParties().add(p);
        c.transitionTo(DefaultCitation.State.FINAL);
        EPSG = c;
    }

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(XML.METADATA_VERSION, LegacyNamespaces.VERSION_2007);
        properties.put(XML.TIMEZONE, TimeZone.getTimeZone("CET"));
        pool = new MarshallerPool(JAXBContext.newInstance(
                "org.geotoolkit.wms.xml.v111:" +
                "org.geotoolkit.wms.xml.v130:" +
                "org.geotoolkit.inspire.xml.vs:" +
                "org.apache.sis.xml.bind.metadata.geometry"), properties);
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
    }

    @After
    public void tearDown() {
        if (unmarshaller != null) {
            pool.recycle(unmarshaller);
        }
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
    }

    @Test
    public void WMSCUnmarshallingTest() throws JAXBException {
        String xml =
        "<Capability>" + '\n' +
        "    <VendorSpecificCapabilities>" + '\n' +
        "     <TileSet>" + '\n' +
        "        <SRS>EPSG:310024802</SRS>" + '\n' +
        "        <BoundingBox SRS=\"EPSG:310024802\" minx=\"-1048576\" miny=\"3670016\" maxx=\"2621440\" maxy=\"8388608\" />" + '\n' +
        "        <Resolutions>0.5 1 2 4 8</Resolutions>" + '\n' +
        "        <Width>256</Width>" + '\n' +
        "        <Height>256</Height>" + '\n' +
        "        <Format>image/png</Format>" + '\n' +
        "        <Layers>ADMINISTRATIVEUNITS.BOUNDARIES</Layers>" + '\n' +
        "    </TileSet>" + '\n' +
        "    <TileSet>" + '\n' +
        "        <SRS>EPSG:310915814</SRS>" + '\n' +
        "        <BoundingBox SRS=\"EPSG:310915814\" minx=\"-6791168\" miny=\"1761280\" maxx=\"-6553600\" maxy=\"2023424\" />" + '\n' +
        "        <Resolutions>0.5 1 2 4</Resolutions>" + '\n' +
        "        <Width>256</Width>" + '\n' +
        "        <Height>256</Height>" + '\n' +
        "        <Format>image/png</Format>" + '\n' +
        "        <Layers>ADMINISTRATIVEUNITS.BOUNDARIES</Layers>" + '\n' +
        "    </TileSet>" + '\n' +
        "    </VendorSpecificCapabilities>" + '\n' +
        "    </Capability>" + '\n';

        final Object unmarshalled = unmarshaller.unmarshal(new StringReader(xml));

        assertNotNull(unmarshalled);
        assertTrue(unmarshalled instanceof org.geotoolkit.wms.xml.v111.Capability);

        org.geotoolkit.wms.xml.v111.Capability result = (org.geotoolkit.wms.xml.v111.Capability) unmarshalled;

        org.geotoolkit.wms.xml.v111.Capability expResult = new org.geotoolkit.wms.xml.v111.Capability(null, null,null,null);
        VendorSpecificCapabilities spec = new VendorSpecificCapabilities();

        BoundingBox bb1 = new BoundingBox("EPSG:310024802", -1048576, 3670016, 2621440, 8388608, null, null);
        List<Double> res = new ArrayList<>();
        res.add(0.5);
        res.add(1.0);
        res.add(2.0);
        res.add(4.0);
        res.add(8.0);

        TileSet ts = new TileSet("EPSG:310024802", bb1, res, 256, 256, "image/png", Arrays.asList("ADMINISTRATIVEUNITS.BOUNDARIES"));
        spec.getTileSet().add(ts);

        BoundingBox bb2 = new BoundingBox("EPSG:310915814", -6791168, 1761280, -6553600, 2023424, null, null);
        List<Double> res2 = new ArrayList<>();
        res2.add(0.5);
        res2.add(1.0);
        res2.add(2.0);
        res2.add(4.0);

        TileSet ts2 = new TileSet("EPSG:310915814", bb2, res2, 256, 256, "image/png", Arrays.asList("ADMINISTRATIVEUNITS.BOUNDARIES"));
        spec.getTileSet().add(ts2);
        expResult.setVendorSpecificCapabilities(spec);

        assertEquals(expResult, result);

    }

    @Test
    public void WMSCMarshallingTest() throws Exception {
        String expResult =
        "<Capability >" + '\n' +
        "    <VendorSpecificCapabilities>" + '\n' +
        "        <TileSet>" + '\n' +
        "            <SRS>EPSG:310024802</SRS>" + '\n' +
        "            <BoundingBox maxy=\"8388608.0\" maxx=\"2621440.0\" miny=\"3670016.0\" minx=\"-1048576.0\" SRS=\"EPSG:310024802\"/>" + '\n' +
        "            <Resolutions>0.5 1.0 2.0 4.0 8.0</Resolutions>" + '\n' +
        "            <Width>256</Width>" + '\n' +
        "            <Height>256</Height>" + '\n' +
        "            <Format>image/png</Format>" + '\n' +
        "            <Layers>ADMINISTRATIVEUNITS.BOUNDARIES</Layers>" + '\n' +
        "        </TileSet>" + '\n' +
        "        <TileSet>" + '\n' +
        "            <SRS>EPSG:310915814</SRS>" + '\n' +
        "            <BoundingBox maxy=\"2023424.0\" maxx=\"-6553600.0\" miny=\"1761280.0\" minx=\"-6791168.0\" SRS=\"EPSG:310915814\"/>" + '\n' +
        "            <Resolutions>0.5 1.0 2.0 4.0</Resolutions>" + '\n' +
        "            <Width>256</Width>" + '\n' +
        "            <Height>256</Height>" + '\n' +
        "            <Format>image/png</Format>" + '\n' +
        "            <Layers>ADMINISTRATIVEUNITS.BOUNDARIES</Layers>" + '\n' +
        "        </TileSet>" + '\n' +
        "    </VendorSpecificCapabilities>" + '\n' +
        "</Capability>" + '\n';

        org.geotoolkit.wms.xml.v111.Capability capa = new org.geotoolkit.wms.xml.v111.Capability(null, null,null,null);
        VendorSpecificCapabilities spec = new VendorSpecificCapabilities();

        BoundingBox bb1 = new BoundingBox("EPSG:310024802", -1048576, 3670016, 2621440, 8388608, null, null);
        List<Double> res = new ArrayList<>();
        res.add(0.5);
        res.add(1.0);
        res.add(2.0);
        res.add(4.0);
        res.add(8.0);

        TileSet ts = new TileSet("EPSG:310024802", bb1, res, 256, 256, "image/png", Arrays.asList("ADMINISTRATIVEUNITS.BOUNDARIES"));
        spec.getTileSet().add(ts);

        BoundingBox bb2 = new BoundingBox("EPSG:310915814", -6791168, 1761280, -6553600, 2023424, null, null);
        List<Double> res2 = new ArrayList<>();
        res2.add(0.5);
        res2.add(1.0);
        res2.add(2.0);
        res2.add(4.0);

        TileSet ts2 = new TileSet("EPSG:310915814", bb2, res2, 256, 256, "image/png", Arrays.asList("ADMINISTRATIVEUNITS.BOUNDARIES"));
        spec.getTileSet().add(ts2);
        capa.setVendorSpecificCapabilities(spec);

        StringWriter sw = new StringWriter();
        marshaller.marshal(capa, sw);
        String result = sw.toString();

        assertXmlEquals(expResult, result, "xmlns:*");

    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void inpsireExtensionmarshallingTest() throws Exception {


        ExtendedCapabilitiesType ext = new ExtendedCapabilitiesType();
        NameFactory nameFactory = new DefaultNameFactory();
        ObjectFactory factory = new ObjectFactory();

        ext.setResourceType(ScopeCode.SERVICE);

        ext.setSpatialDataService(new ServiceType(nameFactory.createLocalName(null, "view")));

        ext.setResourcelocator(new DefaultOnlineResource(URI.create("http://javacestpasdlamenthealeau.com")));

        ext.setMetadataUrl(new DefaultOnlineResource(URI.create("http://javacestdurocknroll.com")));

        DefaultExtent extent = new DefaultExtent();
        DefaultTemporalExtent tempExt = new DefaultTemporalExtent();

        NamedIdentifier periodName = new NamedIdentifier(null, "period");
        final Map<String, Object> periodProp = new HashMap<>();
        periodProp.put(IdentifiedObject.NAME_KEY, periodName);

        NamedIdentifier name = new NamedIdentifier(null, "period instant");
        final Map<String, Object> properties = new HashMap<>();
        properties.put(IdentifiedObject.NAME_KEY, name);

        DefaultPeriod period = new DefaultPeriod(periodProp,
                new DefaultInstant(properties, Instant.ofEpochMilli(120000000)),
                new DefaultInstant(properties, Instant.ofEpochMilli(120000001)));
//        period.setBegining(new DefaultInstant(properties, new DefaultPosition(new Date(120000000))));
//        period.setEnding(new DefaultInstant(properties, new DefaultPosition(new Date(120000001))));

//      org.apache.sis.xml.bind.gml.GMLAdapter.IDs.setUUID(period, "extent");
        tempExt.setExtent(period);
        extent.setTemporalElements(Arrays.asList(tempExt));
        ext.setTemporalRefererence(extent);

        DefaultConformanceResult cresult = new DefaultConformanceResult(EPSG, new DefaultInternationalString("see the referenced specification"), true);
        ext.setConformity(cresult);

        ResponsibleParty party = DefaultResponsibleParty.castOrCopy(EPSG.getCitedResponsibleParties().iterator().next());
        ext.setMetadataPointOfContact(party);

        ext.setMetadataDate(new Date(82800000));

        DefaultKeywords key = new DefaultKeywords(new SimpleInternationalString("something"));
        ext.setInpireKeywords(key);

        List<LanguageType> langs = new ArrayList<>();
        langs.add(new LanguageType("FR"));
        langs.add(new LanguageType("EN", true));
        LanguagesType languages = new LanguagesType(langs);
        ext.setLanguages(languages);

        ext.setCurrentLanguage("FR");

        JAXBElement<ExtendedCapabilitiesType> jbExtendedCap = factory.createExtendedCapabilities(ext);
        Capability capability = new Capability(null, null, null, jbExtendedCap);

        StringWriter sw = new StringWriter();
        marshaller.marshal(capability, sw);
        String result = sw.toString();

        String expResult =
        "<wms:Capability xmlns:wms=\"http://www.opengis.net/wms\""
                + " xmlns:gmd=\"http://www.isotc211.org/2005/gmd\""
                + " xmlns:gco=\"http://www.isotc211.org/2005/gco\""
                + " xmlns:srv=\"http://www.isotc211.org/2005/srv\""
                + " xmlns:inspire_vs=\"http://inspira.europa.eu/networkservice/view/1.0\""
                + " xmlns:gml=\"http://www.opengis.net/gml/3.2\">" + '\n' +
        "    <inspire_vs:ExtendedCapabilities>" + '\n' +
        "        <inspire_vs:Resourcelocator>" + '\n' +
        "            <gmd:linkage>" + '\n' +
        "                <gmd:URL>http://javacestpasdlamenthealeau.com</gmd:URL>" + '\n' +
        "            </gmd:linkage>" + '\n' +
        "        </inspire_vs:Resourcelocator>" + '\n' +
        "        <inspire_vs:MetadataUrl>" + '\n' +
        "            <gmd:linkage>" + '\n' +
        "                <gmd:URL>http://javacestdurocknroll.com</gmd:URL>" + '\n' +
        "            </gmd:linkage>" + '\n' +
        "        </inspire_vs:MetadataUrl>" + '\n' +
        "        <inspire_vs:ResourceType>" + '\n' +
        "            <gmd:MD_ScopeCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#MD_ScopeCode\" codeListValue=\"service\">Service</gmd:MD_ScopeCode>" + '\n' +
        "        </inspire_vs:ResourceType>" + '\n' +
        "        <inspire_vs:TemporalRefererence>" + '\n' +
        "            <gmd:EX_Extent>" + '\n' +
        "                <gmd:temporalElement>" + '\n' +
        "                    <gmd:EX_TemporalExtent>" + '\n' +
        "                        <gmd:extent>" + '\n' +
        "                            <gml:TimePeriod>" + '\n' +
        "                                <gml:beginPosition>1970-01-02T10:20:00+01:00</gml:beginPosition>" + '\n' +
        "                                <gml:endPosition>1970-01-02T10:20:00.001+01:00</gml:endPosition>" + '\n' +
        "                            </gml:TimePeriod>" + '\n' +
        "                        </gmd:extent>" + '\n' +
        "                    </gmd:EX_TemporalExtent>" + '\n' +
        "                </gmd:temporalElement>" + '\n' +
        "            </gmd:EX_Extent>" + '\n' +
        "        </inspire_vs:TemporalRefererence>" + '\n' +
        "        <inspire_vs:Conformity>" + '\n' +
        "            <gmd:DQ_ConformanceResult>" + '\n' +
        "                <gmd:specification>" + '\n' +
        "                    <gmd:CI_Citation>" + '\n' +
        "                        <gmd:title>" + '\n' +
        "                            <gco:CharacterString>EPSG Geodetic Parameter Dataset</gco:CharacterString>" + '\n' +
        "                        </gmd:title>" + '\n' +
        "                        <gmd:identifier>" + '\n' +
        "                            <gmd:MD_Identifier>" + '\n' +
        "                                <gmd:code>" + '\n' +
        "                                    <gco:CharacterString>EPSG</gco:CharacterString>" + '\n' +
        "                                </gmd:code>" + '\n' +
        "                            </gmd:MD_Identifier>" + '\n' +
        "                        </gmd:identifier>" + '\n' +
        "                        <gmd:citedResponsibleParty>" + '\n' +
        "                            <gmd:CI_ResponsibleParty>" + '\n' +
        "                                <gmd:organisationName>" + '\n' +
        "                                    <gco:CharacterString>International Association of Oil &amp; Gas Producers</gco:CharacterString>" + '\n' +
        "                                </gmd:organisationName>" + '\n' +
        "                                <gmd:contactInfo>\n" +
        "                                    <gmd:CI_Contact>\n" +
        "                                        <gmd:onlineResource>\n" +
        "                                            <gmd:CI_OnlineResource>\n" +
        "                                                <gmd:linkage>\n" +
        "                                                    <gmd:URL>https://epsg.org/</gmd:URL>\n" +
        "                                                </gmd:linkage>\n" +
        "                                                <gmd:function>\n" +
        "                                                    <gmd:CI_OnLineFunctionCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_OnLineFunctionCode\" codeListValue=\"information\">Information</gmd:CI_OnLineFunctionCode>\n" +
        "                                                </gmd:function>\n" +
        "                                            </gmd:CI_OnlineResource>\n" +
        "                                        </gmd:onlineResource>\n" +
        "                                    </gmd:CI_Contact>\n" +
        "                                </gmd:contactInfo>\n" +
        "                                <gmd:role>" + '\n' +
        "                                    <gmd:CI_RoleCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\">Principal investigator</gmd:CI_RoleCode>" + '\n' +
        "                                </gmd:role>" + '\n' +
        "                            </gmd:CI_ResponsibleParty>" + '\n' +
        "                        </gmd:citedResponsibleParty>" + '\n' +
        "                        <gmd:presentationForm>" + '\n' +
        "                            <gmd:CI_PresentationFormCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_PresentationFormCode\" codeListValue=\"tableDigital\">Table digital</gmd:CI_PresentationFormCode>" + '\n' +
        "                        </gmd:presentationForm>" + '\n' +
        "                    </gmd:CI_Citation>" + '\n' +
        "                </gmd:specification>" + '\n' +
        "                <gmd:explanation>" + '\n' +
        "                    <gco:CharacterString>see the referenced specification</gco:CharacterString>" + '\n' +
        "                </gmd:explanation>" + '\n' +
        "                <gmd:pass>" + '\n' +
        "                    <gco:Boolean>true</gco:Boolean>" + '\n' +
        "                </gmd:pass>" + '\n' +
        "            </gmd:DQ_ConformanceResult>" + '\n' +
        "        </inspire_vs:Conformity>" + '\n' +
        "        <inspire_vs:MetadataPointOfContact>" + '\n' +
        "            <gmd:CI_ResponsibleParty>" + '\n' +
        "                <gmd:organisationName>" + '\n' +
        "                    <gco:CharacterString>International Association of Oil &amp; Gas Producers</gco:CharacterString>" + '\n' +
        "                </gmd:organisationName>" + '\n' +
        "                <gmd:contactInfo>\n" +
        "                    <gmd:CI_Contact>\n" +
        "                        <gmd:onlineResource>\n" +
        "                            <gmd:CI_OnlineResource>\n" +
        "                                <gmd:linkage>\n" +
        "                                    <gmd:URL>https://epsg.org/</gmd:URL>\n" +
        "                                </gmd:linkage>\n" +
        "                                <gmd:function>\n" +
        "                                    <gmd:CI_OnLineFunctionCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_OnLineFunctionCode\" codeListValue=\"information\">Information</gmd:CI_OnLineFunctionCode>\n" +
        "                                </gmd:function>\n" +
        "                            </gmd:CI_OnlineResource>\n" +
        "                        </gmd:onlineResource>\n" +
        "                    </gmd:CI_Contact>\n" +
        "                </gmd:contactInfo>\n" +
        "                <gmd:role>" + '\n' +
        "                    <gmd:CI_RoleCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\">Principal investigator</gmd:CI_RoleCode>" + '\n' +
        "                </gmd:role>" + '\n' +
        "            </gmd:CI_ResponsibleParty>" + '\n' +
        "        </inspire_vs:MetadataPointOfContact>" + '\n' +
        "        <inspire_vs:MetadataDate>" + '\n' +
        "            <gco:Date>1970-01-02</gco:Date>" + '\n' +
        "        </inspire_vs:MetadataDate>" + '\n' +
        "        <inspire_vs:SpatialDataService>" + '\n' +
        "            <srv:serviceType>" + '\n' +
        "                <gco:LocalName>view</gco:LocalName>" + '\n' +
        "            </srv:serviceType>" + '\n' +
        "        </inspire_vs:SpatialDataService>" + '\n' +
        "        <inspire_vs:InpireKeywords>" + '\n' +
        "            <gmd:keyword>" + '\n' +
        "                <gco:CharacterString>something</gco:CharacterString>" + '\n' +
        "            </gmd:keyword>" + '\n' +
        "        </inspire_vs:InpireKeywords>" + '\n' +
        "        <inspire_vs:Languages>" + '\n' +
        "            <inspire_vs:Language>FR</inspire_vs:Language>" + '\n' +
        "            <inspire_vs:Language default=\"true\">EN</inspire_vs:Language>" + '\n' +
        "        </inspire_vs:Languages>" + '\n' +
        "        <inspire_vs:currentLanguage>FR</inspire_vs:currentLanguage>" + '\n' +
        "    </inspire_vs:ExtendedCapabilities>" + '\n' +
        "</wms:Capability>" + '\n';

//      org.apache.sis.xml.bind.gml.GMLAdapter.IDs.removeUUID(period);

        assertXmlEquals(expResult, result, "http://www.w3.org/2000/xmlns:*");
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void inpsireExtensionUnmarshallingTest() throws Exception {
        String xml =
        "<wms:Capability xmlns:wms=\"http://www.opengis.net/wms\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:srv=\"http://www.isotc211.org/2005/srv\" xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:inspire_vs=\"http://inspira.europa.eu/networkservice/view/1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + '\n' +
        "    <inspire_vs:ExtendedCapabilities>" + '\n' +
        "        <inspire_vs:Resourcelocator>" + '\n' +
        "            <gmd:linkage>" + '\n' +
        "                <gmd:URL>http://javacestpasdlamenthealeau.com</gmd:URL>" + '\n' +
        "            </gmd:linkage>" + '\n' +
        "        </inspire_vs:Resourcelocator>" + '\n' +
        "        <inspire_vs:MetadataUrl>" + '\n' +
        "            <gmd:linkage>" + '\n' +
        "                <gmd:URL>http://javacestdurocknroll.com</gmd:URL>" + '\n' +
        "            </gmd:linkage>" + '\n' +
        "        </inspire_vs:MetadataUrl>" + '\n' +
        "        <inspire_vs:ResourceType>" + '\n' +
        "            <gmd:MD_ScopeCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#MD_ScopeCode\" codeListValue=\"service\"/>" + '\n' +
        "        </inspire_vs:ResourceType>" + '\n' +
        "        <inspire_vs:TemporalRefererence>" + '\n' +
        "            <gmd:EX_Extent>" + '\n' +
        "                <gmd:temporalElement>" + '\n' +
        "                    <gmd:EX_TemporalExtent>" + '\n' +
        "                        <gmd:extent>" + '\n' +
        "                            <gml:TimePeriod>" + '\n' +
        "                                <gml:beginPosition>1970-01-02T10:20:00+01:00</gml:beginPosition>" + '\n' +
        "                                <gml:endPosition>1970-01-02T10:20:01.000+01:00</gml:endPosition>" + '\n' +
        "                            </gml:TimePeriod>" + '\n' +
        "                        </gmd:extent>" + '\n' +
        "                    </gmd:EX_TemporalExtent>" + '\n' +
        "                </gmd:temporalElement>" + '\n' +
        "            </gmd:EX_Extent>" + '\n' +
        "        </inspire_vs:TemporalRefererence>" + '\n' +
        "        <inspire_vs:Conformity>" + '\n' +
        "            <gmd:DQ_ConformanceResult>" + '\n' +
        "                <gmd:specification>" + '\n' +
        "                    <gmd:CI_Citation>" + '\n' +
        "                        <gmd:title>" + '\n' +
        "                            <gco:CharacterString>European Petroleum Survey Group</gco:CharacterString>" + '\n' +
        "                        </gmd:title>" + '\n' +
        "                        <gmd:alternateTitle>" + '\n' +
        "                            <gco:CharacterString>EPSG</gco:CharacterString>" + '\n' +
        "                        </gmd:alternateTitle>" + '\n' +
        "                        <gmd:identifier>" + '\n' +
        "                            <gmd:MD_Identifier>" + '\n' +
        "                                <gmd:code>" + '\n' +
        "                                    <gco:CharacterString>EPSG</gco:CharacterString>" + '\n' +
        "                                </gmd:code>" + '\n' +
        "                            </gmd:MD_Identifier>" + '\n' +
        "                        </gmd:identifier>" + '\n' +
        "                        <gmd:citedResponsibleParty>" + '\n' +
        "                            <gmd:CI_ResponsibleParty>" + '\n' +
        "                                <gmd:organisationName>" + '\n' +
        "                                    <gco:CharacterString>European Petroleum Survey Group</gco:CharacterString>" + '\n' +
        "                                </gmd:organisationName>" + '\n' +
        "                                <gmd:contactInfo>" + '\n' +
        "                                    <gmd:CI_Contact>" + '\n' +
        "                                        <gmd:onlineResource>" + '\n' +
        "                                            <gmd:CI_OnlineResource>" + '\n' +
        "                                                <gmd:linkage>" + '\n' +
        "                                                    <gmd:URL>https://epsg.org/</gmd:URL>" + '\n' +
        "                                                </gmd:linkage>" + '\n' +
        "                                                <gmd:function>" + '\n' +
        "                                                    <gmd:CI_OnLineFunctionCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_OnLineFunctionCode\" codeListValue=\"information\"/>" + '\n' +
        "                                                </gmd:function>" + '\n' +
        "                                            </gmd:CI_OnlineResource>" + '\n' +
        "                                        </gmd:onlineResource>" + '\n' +
        "                                    </gmd:CI_Contact>" + '\n' +
        "                                </gmd:contactInfo>" + '\n' +
        "                                <gmd:role>" + '\n' +
        "                                    <gmd:CI_RoleCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\"/>" + '\n' +
        "                                </gmd:role>" + '\n' +
        "                            </gmd:CI_ResponsibleParty>" + '\n' +
        "                        </gmd:citedResponsibleParty>" + '\n' +
        "                        <gmd:presentationForm>" + '\n' +
        "                            <gmd:CI_PresentationFormCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_PresentationFormCode\" codeListValue=\"tableDigital\"/>" + '\n' +
        "                        </gmd:presentationForm>" + '\n' +
        "                    </gmd:CI_Citation>" + '\n' +
        "                </gmd:specification>" + '\n' +
        "                <gmd:explanation>" + '\n' +
        "                    <gco:CharacterString>see the referenced specification</gco:CharacterString>" + '\n' +
        "                </gmd:explanation>" + '\n' +
        "                <gmd:pass>" + '\n' +
        "                    <gco:Boolean>true</gco:Boolean>" + '\n' +
        "                </gmd:pass>" + '\n' +
        "            </gmd:DQ_ConformanceResult>" + '\n' +
        "        </inspire_vs:Conformity>" + '\n' +
        "        <inspire_vs:MetadataPointOfContact>" + '\n' +
        "            <gmd:CI_ResponsibleParty>" + '\n' +
        "                <gmd:organisationName>" + '\n' +
        "                    <gco:CharacterString>European Petroleum Survey Group</gco:CharacterString>" + '\n' +
        "                </gmd:organisationName>" + '\n' +
        "                <gmd:contactInfo>" + '\n' +
        "                    <gmd:CI_Contact>" + '\n' +
        "                        <gmd:onlineResource>" + '\n' +
        "                            <gmd:CI_OnlineResource>" + '\n' +
        "                                <gmd:linkage>" + '\n' +
        "                                    <gmd:URL>https://epsg.org/</gmd:URL>" + '\n' +
        "                                </gmd:linkage>" + '\n' +
        "                                <gmd:function>" + '\n' +
        "                                    <gmd:CI_OnLineFunctionCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_OnLineFunctionCode\" codeListValue=\"information\"/>" + '\n' +
        "                                </gmd:function>" + '\n' +
        "                            </gmd:CI_OnlineResource>" + '\n' +
        "                        </gmd:onlineResource>" + '\n' +
        "                    </gmd:CI_Contact>" + '\n' +
        "                </gmd:contactInfo>" + '\n' +
        "                <gmd:role>" + '\n' +
        "                    <gmd:CI_RoleCode codeList=\"http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\"/>" + '\n' +
        "                </gmd:role>" + '\n' +
        "            </gmd:CI_ResponsibleParty>" + '\n' +
        "        </inspire_vs:MetadataPointOfContact>" + '\n' +
        "        <inspire_vs:MetadataDate>" + '\n' +
        "            <gco:Date>1970-01-02</gco:Date>" + '\n' +
        "        </inspire_vs:MetadataDate>" + '\n' +
        "        <inspire_vs:SpatialDataService>" + '\n' +
        "            <srv:serviceType>" + '\n' +
        "                <gco:LocalName>view</gco:LocalName>" + '\n' +
        "            </srv:serviceType>" + '\n' +
        "        </inspire_vs:SpatialDataService>" + '\n' +
        "        <inspire_vs:InpireKeywords>" + '\n' +
        "            <gmd:keyword>" + '\n' +
        "                <gco:CharacterString>something</gco:CharacterString>" + '\n' +
        "            </gmd:keyword>" + '\n' +
        "        </inspire_vs:InpireKeywords>" + '\n' +
        "        <inspire_vs:Languages>" + '\n' +
        "            <inspire_vs:Language>FR</inspire_vs:Language>" + '\n' +
        "            <inspire_vs:Language default=\"true\">EN</inspire_vs:Language>" + '\n' +
        "        </inspire_vs:Languages>" + '\n' +
        "        <inspire_vs:currentLanguage>FR</inspire_vs:currentLanguage>" + '\n' +
        "    </inspire_vs:ExtendedCapabilities>" + '\n' +
        "</wms:Capability>" + '\n';

        ExtendedCapabilitiesType ext = new ExtendedCapabilitiesType();
        NameFactory nameFactory = new DefaultNameFactory();
        ObjectFactory factory = new ObjectFactory();

        ext.setResourceType(ScopeCode.SERVICE);

        ext.setSpatialDataService(new ServiceType(nameFactory.createLocalName(null, "view")));

        ext.setResourcelocator(new DefaultOnlineResource(URI.create("http://javacestpasdlamenthealeau.com")));

        ext.setMetadataUrl(new DefaultOnlineResource(URI.create("http://javacestdurocknroll.com")));

        DefaultExtent extent = new DefaultExtent();
        DefaultTemporalExtent tempExt = new DefaultTemporalExtent();


        NamedIdentifier periodName = new NamedIdentifier(null, "period");
        final Map<String, Object> periodProp = new HashMap<>();
        periodProp.put(IdentifiedObject.NAME_KEY, periodName);
        NamedIdentifier instantName = new NamedIdentifier(null, "period instant");
        final Map<String, Object> properties = new HashMap<>();
        properties.put(IdentifiedObject.NAME_KEY, instantName);

        DefaultPeriod period = new DefaultPeriod(periodProp,
                new DefaultInstant(properties, Instant.ofEpochMilli(120000000)),
                new DefaultInstant(properties, Instant.ofEpochMilli(120001000)));

        tempExt.setExtent(period);
        extent.setTemporalElements(Arrays.asList(tempExt));
        ext.setTemporalRefererence(extent);

        DefaultResponsibleParty rp = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        rp.setOrganisationName(new SimpleInternationalString("European Petroleum Survey Group"));
        DefaultOnlineResource or = new DefaultOnlineResource(URI.create("https://epsg.org/"));
        or.setFunction(OnLineFunction.INFORMATION);
        DefaultContact ct = new DefaultContact(or);
        rp.setContactInfo(ct);
        DefaultCitation citation = new DefaultCitation();
        citation.setCitedResponsibleParties(Arrays.asList(rp));
        citation.setTitle(rp.getOrganisationName());
        citation.setAlternateTitles(Arrays.asList(new SimpleInternationalString("EPSG")));
        citation.setIdentifiers(Arrays.asList(new DefaultIdentifier("EPSG")));
        citation.setPresentationForms(Arrays.asList(PresentationForm.TABLE_DIGITAL));
        DefaultConformanceResult cresult =  new DefaultConformanceResult(citation, new DefaultInternationalString("see the referenced specification"), true);
        ext.setConformity(cresult);

        ResponsibleParty party = DefaultResponsibleParty.castOrCopy(EPSG.getCitedResponsibleParties().iterator().next());
        ext.setMetadataPointOfContact(party);

        ext.setMetadataDate(new Date(82800000));

        DefaultKeywords key = new DefaultKeywords(new SimpleInternationalString("something"));
        ext.setInpireKeywords(key);

        List<LanguageType> langs = new ArrayList<>();
        langs.add(new LanguageType("FR"));
        langs.add(new LanguageType("EN", true));
        LanguagesType languages = new LanguagesType(langs);
        ext.setLanguages(languages);

        ext.setCurrentLanguage("FR");

        JAXBElement<ExtendedCapabilitiesType> jbExtendedCap = factory.createExtendedCapabilities(ext);
        Capability expResult = new Capability(null, null, null, jbExtendedCap);

        final Object unmarshalled = unmarshaller.unmarshal(new StringReader(xml));

        assertNotNull(unmarshalled);

        assertTrue(unmarshalled instanceof Capability);

        Capability result = (Capability) unmarshalled;

        // The Unmarshaller replaced automatically the DefaultInternationalString by a
        // SimpleInternationalString because it detected that there is only one locale.
        // Perform the same change in our expected result in order to allow comparison.
        cresult.setExplanation(new SimpleInternationalString(cresult.getExplanation().toString()));

        final ExtendedCapabilitiesType expCapabilities =       expResult.getInspireExtendedCapabilities();
        final ExtendedCapabilitiesType    capabilities =          result.getInspireExtendedCapabilities();
        final ConformanceResult          expConformity = expCapabilities.getConformity();
        final ConformanceResult             conformity =    capabilities.getConformity();
        final Citation                expSpecification =   expConformity.getSpecification();
        final Citation                   specification =      conformity.getSpecification();
        final Extent                       expTemporal = expCapabilities.getTemporalRefererence();
        final Extent                          temporal =    capabilities.getTemporalRefererence();
        final TemporalExtent         expTemporalExtent = getSingleton(expTemporal.getTemporalElements());
        final TemporalExtent            temporalExtent = getSingleton(temporal.getTemporalElements());
        final Period                   expExtentPeriod = (Period) expTemporalExtent.getExtent();
        final Period                      extentPeriod = (Period) temporalExtent.getExtent();

        assertEquals(expConformity.getExplanation().toString(),              conformity.getExplanation().toString());
        assertEquals(expSpecification.getCollectiveTitle(),                  specification.getCollectiveTitle());
        assertEquals(expSpecification.getCitedResponsibleParties(),          specification.getCitedResponsibleParties());
        assertEquals(expSpecification.getAlternateTitles(),                  specification.getAlternateTitles());
        assertEquals(expSpecification,                                       specification);
        assertEquals(expConformity,                                          conformity);
        assertEquals(expCapabilities.getResourceType(),                      capabilities.getResourceType());
        assertEquals(expTemporal.getDescription(),                           temporal.getDescription());
        assertEquals(expExtentPeriod.getBeginning(), InstantWrapper.toInstant(extentPeriod.getBeginning()));
        assertEquals(expExtentPeriod.getEnding(),    InstantWrapper.toInstant(extentPeriod.getEnding()));
        if (expExtentPeriod.getClass() == extentPeriod.getClass()) {
            /*
             * The time period created by this test case is an instance of org.geotoolkit.temporal.object.DefaultPeriod
             * while the unmarshalled period is an instance of org.geotoolkit.gml.xml.v311.TimePeriodType. This is okay
             * since DefaultPeriod does not have JAXB annotation. But it prevents us to compare the objects further.
             */
            assertEquals(expExtentPeriod,   extentPeriod);
            assertEquals(expTemporalExtent, temporalExtent);
            assertEquals(expTemporal,       temporal);
            assertEquals(expCapabilities,   capabilities);
            assertEquals(expResult,         result);
        }
    }

    @Test
    public void requestTest() throws Exception {
        GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, -180.0, 180.0);
        env.setRange(0, -90.0, 90.0);
        List<String> layers = Arrays.asList("layer1", "layer2");
        List<String> styles = Arrays.asList("style1", "style2");

        GetMap gm = new GetMap(env, new Version(WMSVersion.v130.getCode()), "image/png", layers, styles, new Dimension(500, 500), Collections.EMPTY_MAP);

        assertEquals(layers, gm.getLayers());

        List<String> queryLayers = layers;
        GetFeatureInfo gfi = new GetFeatureInfo(gm, 4, 5, queryLayers, "application/json", 10);
        assertEquals(queryLayers, gfi.getQueryLayers());
    }
}
