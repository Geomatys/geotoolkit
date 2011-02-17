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

import java.io.StringReader;
import java.util.List;
import org.geotoolkit.inspire.xml.vs.LanguageType;
import org.opengis.metadata.citation.ResponsibleParty;
import org.geotoolkit.metadata.iso.citation.Citations;
import java.util.Arrays;
import java.util.Date;
import java.net.URI;
import org.opengis.util.NameFactory;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.inspire.xml.vs.ExtendedCapabilitiesType;
import org.geotoolkit.inspire.xml.vs.LanguagesType;
import org.geotoolkit.inspire.xml.vs.ObjectFactory;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultContact;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.extent.DefaultTemporalExtent;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.naming.DefaultNameFactory;
import org.geotoolkit.service.ServiceTypeImpl;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.wms.xml.v130.Capability;

import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.PresentationForm;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.maintenance.ScopeCode;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class WmsXmlBindingTest {

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool =   new MarshallerPool("org.geotoolkit.wms.xml.v111:" +
                                    "org.geotoolkit.wms.xml.v130:" +
                                    "org.geotoolkit.inspire.xml.vs:" +
                                    "org.geotoolkit.internal.jaxb.geometry");
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
    }

    @After
    public void tearDown() {
        if (unmarshaller != null) {
            pool.release(unmarshaller);
        }
        if (marshaller != null) {
            pool.release(marshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void unmarshallingTest() throws JAXBException {

    
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void inpsireExtensionmarshallingTest() throws Exception {


        ExtendedCapabilitiesType ext = new ExtendedCapabilitiesType();
        NameFactory nameFactory = new DefaultNameFactory();
        ObjectFactory factory = new ObjectFactory();

        ext.setResourceType(ScopeCode.SERVICE);

        ext.setSpatialDataService(new ServiceTypeImpl(nameFactory.createLocalName(null, "view")));

        ext.setResourcelocator(new DefaultOnlineResource(URI.create("http://javacestpasdlamenthealeau.com")));

        ext.setMetadataUrl(new DefaultOnlineResource(URI.create("http://javacestdurocknroll.com")));

        DefaultExtent extent = new DefaultExtent();
        DefaultTemporalExtent tempExt = new DefaultTemporalExtent();
        DefaultPeriod period = new DefaultPeriod();
        period.setBegining(new Date(120000000));
        period.setEnding(new Date(120000001));
        tempExt.setExtent(period);
        extent.setTemporalElements(Arrays.asList(tempExt));
        ext.setTemporalRefererence(extent);

        DefaultConformanceResult cresult =  new DefaultConformanceResult(Citations.EPSG, new DefaultInternationalString("see the referenced specification"), true);
        ext.setConformity(cresult);

        ResponsibleParty party = DefaultResponsibleParty.EPSG;
        ext.setMetadataPointOfContact(party);

        ext.setMetadataDate(new Date(82800000));

        DefaultKeywords key = new DefaultKeywords(Arrays.asList(new SimpleInternationalString("something")));
        ext.setInpireKeywords(key);

        List<LanguageType> langs = new ArrayList<LanguageType>();
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

        //System.out.println("RESULT:" + result);
        
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = StringUtilities.removeXmlns(result);


        String expResult =
        "<wms:Capability >" + '\n' +
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
        "            <gmd:MD_ScopeCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_ScopeCode\" codeListValue=\"service\"/>" + '\n' +
        "        </inspire_vs:ResourceType>" + '\n' +
        "        <inspire_vs:TemporalRefererence>" + '\n' +
        "            <gmd:EX_Extent>" + '\n' +
        "                <gmd:temporalElement>" + '\n' +
        "                    <gmd:EX_TemporalExtent>" + '\n' +
        "                        <gmd:extent>" + '\n' +
        "                            <gml:TimePeriod gml:id=\"extent\">" + '\n' +
        "                                <gml:beginPosition>1970-01-02T10:20:00.000+01:00</gml:beginPosition>" + '\n' +
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
        "                                                    <gmd:URL>http://www.epsg.org</gmd:URL>" + '\n' +
        "                                                </gmd:linkage>" + '\n' +
        "                                                <gmd:function>" + '\n' +
        "                                                    <gmd:CI_OnLineFunctionCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_OnLineFunctionCode\" codeListValue=\"information\"/>" + '\n' +
        "                                                </gmd:function>" + '\n' +
        "                                            </gmd:CI_OnlineResource>" + '\n' +
        "                                        </gmd:onlineResource>" + '\n' +
        "                                    </gmd:CI_Contact>" + '\n' +
        "                                </gmd:contactInfo>" + '\n' +
        "                                <gmd:role>" + '\n' +
        "                                    <gmd:CI_RoleCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\"/>" + '\n' +
        "                                </gmd:role>" + '\n' +
        "                            </gmd:CI_ResponsibleParty>" + '\n' +
        "                        </gmd:citedResponsibleParty>" + '\n' +
        "                        <gmd:presentationForm>" + '\n' +
        "                            <gmd:CI_PresentationFormCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_PresentationFormCode\" codeListValue=\"tableDigital\"/>" + '\n' +
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
        "                                    <gmd:URL>http://www.epsg.org</gmd:URL>" + '\n' +
        "                                </gmd:linkage>" + '\n' +
        "                                <gmd:function>" + '\n' +
        "                                    <gmd:CI_OnLineFunctionCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_OnLineFunctionCode\" codeListValue=\"information\"/>" + '\n' +
        "                                </gmd:function>" + '\n' +
        "                            </gmd:CI_OnlineResource>" + '\n' +
        "                        </gmd:onlineResource>" + '\n' +
        "                    </gmd:CI_Contact>" + '\n' +
        "                </gmd:contactInfo>" + '\n' +
        "                <gmd:role>" + '\n' +
        "                    <gmd:CI_RoleCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\"/>" + '\n' +
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

        assertEquals(expResult, result);

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void inpsireExtensionUnmarshallingTest() throws Exception {


        String xml =
        "<wms:Capability xmlns:wms=\"http://www.opengis.net/wms\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:srv=\"http://www.isotc211.org/2005/srv\" xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:sld=\"http://www.opengis.net/sld\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:inspire_vs=\"http://inspira.europa.eu/networkservice/view/1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + '\n' +
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
        "            <gmd:MD_ScopeCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#MD_ScopeCode\" codeListValue=\"service\"/>" + '\n' +
        "        </inspire_vs:ResourceType>" + '\n' +
        "        <inspire_vs:TemporalRefererence>" + '\n' +
        "            <gmd:EX_Extent>" + '\n' +
        "                <gmd:temporalElement>" + '\n' +
        "                    <gmd:EX_TemporalExtent>" + '\n' +
        "                        <gmd:extent>" + '\n' +
        "                            <gml:TimePeriod gml:id=\"extent\">" + '\n' +
        "                                <gml:beginPosition>1970-01-02T10:20:00.000+01:00</gml:beginPosition>" + '\n' +
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
        "                                                    <gmd:URL>http://www.epsg.org</gmd:URL>" + '\n' +
        "                                                </gmd:linkage>" + '\n' +
        "                                                <gmd:function>" + '\n' +
        "                                                    <gmd:CI_OnLineFunctionCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_OnLineFunctionCode\" codeListValue=\"information\"/>" + '\n' +
        "                                                </gmd:function>" + '\n' +
        "                                            </gmd:CI_OnlineResource>" + '\n' +
        "                                        </gmd:onlineResource>" + '\n' +
        "                                    </gmd:CI_Contact>" + '\n' +
        "                                </gmd:contactInfo>" + '\n' +
        "                                <gmd:role>" + '\n' +
        "                                    <gmd:CI_RoleCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\"/>" + '\n' +
        "                                </gmd:role>" + '\n' +
        "                            </gmd:CI_ResponsibleParty>" + '\n' +
        "                        </gmd:citedResponsibleParty>" + '\n' +
        "                        <gmd:presentationForm>" + '\n' +
        "                            <gmd:CI_PresentationFormCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_PresentationFormCode\" codeListValue=\"tableDigital\"/>" + '\n' +
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
        "                                    <gmd:URL>http://www.epsg.org</gmd:URL>" + '\n' +
        "                                </gmd:linkage>" + '\n' +
        "                                <gmd:function>" + '\n' +
        "                                    <gmd:CI_OnLineFunctionCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_OnLineFunctionCode\" codeListValue=\"information\"/>" + '\n' +
        "                                </gmd:function>" + '\n' +
        "                            </gmd:CI_OnlineResource>" + '\n' +
        "                        </gmd:onlineResource>" + '\n' +
        "                    </gmd:CI_Contact>" + '\n' +
        "                </gmd:contactInfo>" + '\n' +
        "                <gmd:role>" + '\n' +
        "                    <gmd:CI_RoleCode codeList=\"http://schemas.opengis.net/iso/19139/20070417/resources/Codelist/gmxCodelists.xml#CI_RoleCode\" codeListValue=\"principalInvestigator\"/>" + '\n' +
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

        ext.setSpatialDataService(new ServiceTypeImpl(nameFactory.createLocalName(null, "view")));

        ext.setResourcelocator(new DefaultOnlineResource(URI.create("http://javacestpasdlamenthealeau.com")));

        ext.setMetadataUrl(new DefaultOnlineResource(URI.create("http://javacestdurocknroll.com")));

        DefaultExtent extent = new DefaultExtent();
        DefaultTemporalExtent tempExt = new DefaultTemporalExtent();
        DefaultPeriod period = new DefaultPeriod();
        period.setBegining(new Date(120000000));
        period.setEnding(new Date(120000001));
        tempExt.setExtent(period);
        extent.setTemporalElements(Arrays.asList(tempExt));
        ext.setTemporalRefererence(extent);

        DefaultResponsibleParty rp = new DefaultResponsibleParty(Role.PRINCIPAL_INVESTIGATOR);
        rp.setOrganisationName(new SimpleInternationalString("European Petroleum Survey Group"));
        DefaultOnlineResource or = new DefaultOnlineResource(URI.create("http://www.epsg.org"));
        or.setFunction(OnLineFunction.INFORMATION);
        DefaultContact ct = new DefaultContact(or);
        rp.setContactInfo(ct);
        DefaultCitation citation = new DefaultCitation(rp);
        citation.setAlternateTitles(Arrays.asList(new SimpleInternationalString("EPSG")));
        citation.setIdentifiers(Arrays.asList(new DefaultIdentifier("EPSG")));
        citation.setPresentationForms(Arrays.asList(PresentationForm.TABLE_DIGITAL));
        DefaultConformanceResult cresult =  new DefaultConformanceResult(citation, new DefaultInternationalString("see the referenced specification"), true);
        ext.setConformity(cresult);

        ResponsibleParty party = DefaultResponsibleParty.EPSG;
        ext.setMetadataPointOfContact(party);

        ext.setMetadataDate(new Date(82800000));

        DefaultKeywords key = new DefaultKeywords(Arrays.asList(new SimpleInternationalString("something")));
        ext.setInpireKeywords(key);

        List<LanguageType> langs = new ArrayList<LanguageType>();
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

        assertEquals(expResult.getInspireExtendedCapabilities().getConformity().getExplanation(), result.getInspireExtendedCapabilities().getConformity().getExplanation());
        assertEquals(expResult.getInspireExtendedCapabilities().getConformity().getSpecification().getCollectiveTitle(), result.getInspireExtendedCapabilities().getConformity().getSpecification().getCollectiveTitle());
        assertEquals(expResult.getInspireExtendedCapabilities().getConformity().getSpecification().getCitedResponsibleParties(), result.getInspireExtendedCapabilities().getConformity().getSpecification().getCitedResponsibleParties());
        assertEquals(expResult.getInspireExtendedCapabilities().getConformity().getSpecification().getAlternateTitles(), result.getInspireExtendedCapabilities().getConformity().getSpecification().getAlternateTitles());
        assertEquals(expResult.getInspireExtendedCapabilities().getConformity().getSpecification(), result.getInspireExtendedCapabilities().getConformity().getSpecification());
        assertEquals(expResult.getInspireExtendedCapabilities().getConformity(), result.getInspireExtendedCapabilities().getConformity());
        assertEquals(expResult.getInspireExtendedCapabilities().getResourceType(), result.getInspireExtendedCapabilities().getResourceType());
        assertEquals(expResult.getInspireExtendedCapabilities(), result.getInspireExtendedCapabilities());
        assertEquals(expResult, result);

    }
}
