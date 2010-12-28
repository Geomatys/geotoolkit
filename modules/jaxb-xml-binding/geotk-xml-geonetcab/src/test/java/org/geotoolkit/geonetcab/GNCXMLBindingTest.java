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
package org.geotoolkit.geonetcab;

import java.net.URISyntaxException;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import java.util.Date;
import org.geotoolkit.geotnetcab.GNC_UsersRestrictions;
import java.net.MalformedURLException;
import org.geotoolkit.geotnetcab.GNC_Product;
import org.geotoolkit.geotnetcab.GNC_Document;
import java.util.List;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.opengis.metadata.citation.OnlineResource;
import java.net.URI;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

//Junit dependencies
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.geotnetcab.GNC_Access;
import org.geotoolkit.geotnetcab.GNC_AccessConstraints;
import org.geotoolkit.geotnetcab.GNC_EOProduct;
import org.geotoolkit.geotnetcab.GNC_MaterialResource;
import org.geotoolkit.geotnetcab.GNC_OrganisationEntitie;
import org.geotoolkit.geotnetcab.GNC_OrganisationTypeCode;
import org.geotoolkit.geotnetcab.GNC_Reference;
import org.geotoolkit.geotnetcab.GNC_RelationNameCode;
import org.geotoolkit.geotnetcab.GNC_RelationType;
import org.geotoolkit.geotnetcab.GNC_Resource;
import org.geotoolkit.geotnetcab.GNC_Service;
import org.geotoolkit.geotnetcab.GNC_Software;
import org.geotoolkit.geotnetcab.GNC_ThematicTypeCode;
import org.geotoolkit.geotnetcab.GNC_Training;
import org.geotoolkit.geotnetcab.GNC_UseRestrictions;
import org.geotoolkit.geotnetcab.GNC_UserDefinedMetadata;
import org.geotoolkit.geotnetcab.ObjectFactory;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicDescription;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class GNCXMLBindingTest {

    private static MarshallerPool pool;

    @BeforeClass
    public static void setUpClass() throws Exception {
        pool = new MarshallerPool(DefaultMetadata.class, ObjectFactory.class);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws JAXBException {
    }

    @After
    public void tearDown() {
        
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void marshallingTest() throws JAXBException, MalformedURLException, URISyntaxException {

        Marshaller marshaller = pool.acquireMarshaller();
        DefaultMetadata meta = new DefaultMetadata();

        GNC_Resource identInfo = new GNC_Resource();
        OnlineResource resource = new DefaultOnlineResource(URI.create("http://something.com"));
        identInfo.setOnlineInformation(resource);

        meta.setIdentificationInfo(Arrays.asList(identInfo));
        StringWriter sw = new StringWriter();
        marshaller.marshal(meta, sw);

        String result = sw.toString();

        //System.out.println(result);
        
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlns
        result = StringUtilities.removeXmlns(result);
       

        String expResult = "<gmd:MD_Metadata >" +'\n' +
                           "    <gmd:identificationInfo>" +'\n' +
                           "        <ns8:GNC_Resource>" +'\n' +
                           "            <ns8:onlineInformation>" +'\n' +
                           "                <gmd:CI_OnlineResource>" +'\n'+
                           "                    <gmd:linkage>" +'\n' +
                           "                        <gmd:URL>http://something.com</gmd:URL>" +'\n' +
                           "                    </gmd:linkage>" +'\n' +
                           "                </gmd:CI_OnlineResource>" +'\n' +
                           "            </ns8:onlineInformation>" +'\n' +
                           "        </ns8:GNC_Resource>" +'\n' +
                           "    </gmd:identificationInfo>" +'\n' +
                           "</gmd:MD_Metadata>" + '\n' ;
        assertEquals(expResult, result);

        meta = new DefaultMetadata();

        GNC_OrganisationEntitie identInfo2 = new GNC_OrganisationEntitie();

        resource = new DefaultOnlineResource(URI.create("http://something.com"));
        identInfo2.setOnlineInformation(resource);

        identInfo2.setTypeOfOrganisation(GNC_OrganisationTypeCode.EXPERTS);

        List<GNC_Resource> references = new ArrayList<GNC_Resource>();

        GNC_MaterialResource materialResource = new GNC_MaterialResource();
        materialResource.setIsStillInProduction(Boolean.TRUE);
        GNC_Access access = new GNC_Access();
        access.setDescription("desc1");
        GNC_AccessConstraints ac = new GNC_AccessConstraints();
        ac.setDataAccessConditionPortal(new URI("http://danstonchat.com"));
        ac.setNameOfConditions("names");
        ac.setNonCommercialUse(Boolean.TRUE);
        ac.setThematicUsage(GNC_ThematicTypeCode.AGRICULTURE);
        GNC_UseRestrictions ur = new GNC_UseRestrictions();
        ur.setGeographicRestrictions(Boolean.FALSE);
        ur.setOtherConstraints("military");
        ur.setExtentOfRestrictions(new DefaultGeographicDescription());
        ac.setUseRestrictions(ur);
        GNC_UsersRestrictions urr = new GNC_UsersRestrictions();
        urr.setCategoryOfUsers(GNC_OrganisationTypeCode.EXPERTS);
        urr.setOtherConstraints("confidential");
        urr.setExtentOfRestrictions(new DefaultGeographicDescription());
        ac.setUsersRestrictions(urr);
        access.setDetailAccessConstraints(ac);
        materialResource.setAccess(access);
        GNC_UserDefinedMetadata umeta = new GNC_UserDefinedMetadata();
        umeta.setDateStamp(new Date(1454124));
        umeta.setFeedbackStatement("stmt");
        umeta.setUserContact(DefaultResponsibleParty.ORACLE);
        materialResource.setUserDefinedMetadata(umeta);
        GNC_RelationType relType = new GNC_RelationType();
        relType.setRelationName(GNC_RelationNameCode.USEDBY);
        materialResource.setRelationType(relType);
        references.add(materialResource);

        GNC_Reference reference = new GNC_Reference();
        references.add(reference);

        GNC_Document document = new GNC_Document();
        references.add(document);

        GNC_Product product = new GNC_Product();
        references.add(product);

        GNC_Service service = new GNC_Service();
        references.add(service);

        GNC_Software software = new GNC_Software();
        references.add(software);

        GNC_Training training = new GNC_Training();
        references.add(training);

        GNC_EOProduct eoproduct = new GNC_EOProduct();
        references.add(eoproduct);
        
        identInfo2.setReference(references);

        meta.setIdentificationInfo(Arrays.asList(identInfo2));
        sw = new StringWriter();
        marshaller.marshal(meta, sw);

        result = sw.toString();

        //System.out.print(result);

        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlns
        result = StringUtilities.removeXmlns(result);


        expResult =        "<gmd:MD_Metadata >" +'\n' +
                           "    <gmd:identificationInfo>" +'\n' +
                           "        <ns8:GNC_OrganisationEntitie>" +'\n' +
                           "            <ns8:onlineInformation>" +'\n' +
                           "                <gmd:CI_OnlineResource>" +'\n'+
                           "                    <gmd:linkage>" +'\n' +
                           "                        <gmd:URL>http://something.com</gmd:URL>" +'\n' +
                           "                    </gmd:linkage>" +'\n' +
                           "                </gmd:CI_OnlineResource>" +'\n' +
                           "            </ns8:onlineInformation>" +'\n' +
                           "            <ns8:reference>" +'\n' +
                           "                <ns8:GNC_MaterialResource>" +'\n' +
                           "                    <ns8:isStillInProduction>" +'\n' +
                           "                        <gco:Boolean>true</gco:Boolean>" +'\n' +
                           "                    </ns8:isStillInProduction>" +'\n' +
                           "                    <ns8:userDefinedMetadata>" +'\n' +
                           "                        <ns8:GNC_UserDefinedMetadata>" +'\n' +
                           "                            <ns8:dateStamp>1970-01-01T01:24:14.124+01:00</ns8:dateStamp>" +'\n' +
                           "                            <ns8:feedbackStatement>" +'\n' +
                           "                                <gco:CharacterString>stmt</gco:CharacterString>" +'\n' +
                           "                            </ns8:feedbackStatement>" +'\n' +
                           "                            <ns8:userContact>" +'\n' +
                           "                                <gmd:CI_ResponsibleParty>" +'\n' +
                           "                                    <gmd:organisationName>" +'\n' +
                           "                                        <gco:CharacterString>Oracle</gco:CharacterString>" +'\n' +
                           "                                    </gmd:organisationName>" +'\n' +
                           "                                    <gmd:contactInfo>" +'\n' +
                           "                                        <gmd:CI_Contact>" +'\n' +
                           "                                            <gmd:onlineResource>" +'\n' +
                           "                                                <gmd:CI_OnlineResource>" +'\n' +
                           "                                                    <gmd:linkage>" +'\n' +
                           "                                                        <gmd:URL>http://www.oracle.com</gmd:URL>" +'\n' +
                           "                                                    </gmd:linkage>" +'\n' +
                           "                                                    <gmd:function>" +'\n' +
                           "                                                        <gmd:CI_OnLineFunctionCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#information\" codeListValue=\"information\"/>" +'\n' +
                           "                                                    </gmd:function>" +'\n' +
                           "                                                </gmd:CI_OnlineResource>" +'\n' +
                           "                                            </gmd:onlineResource>" +'\n' +
                           "                                        </gmd:CI_Contact>" +'\n' +
                           "                                    </gmd:contactInfo>" +'\n' +
                           "                                    <gmd:role>" +'\n' +
                           "                                        <gmd:CI_RoleCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#owner\" codeListValue=\"owner\"/>" +'\n' +
                           "                                    </gmd:role>" +'\n' +
                           "                                </gmd:CI_ResponsibleParty>" +'\n' +
                           "                            </ns8:userContact>" +'\n' +
                           "                        </ns8:GNC_UserDefinedMetadata>" +'\n' +
                           "                    </ns8:userDefinedMetadata>" +'\n' +
                           "                    <ns8:access>" +'\n' +
                           "                        <ns8:GNC_Access>" +'\n' +
                           "                            <ns8:description>" +'\n' +
                           "                                <gco:CharacterString>desc1</gco:CharacterString>" +'\n' +
                           "                            </ns8:description>" +'\n' +
                           "                            <ns8:detailAccessConstraints>" +'\n' +
                           "                                <ns8:GNC_AccessConstraints>" +'\n' +
                           "                                    <ns8:dataAccessConditionPortal>" +'\n' +
                           "                                        <gmd:URL>http://danstonchat.com</gmd:URL>" +'\n' +
                           "                                    </ns8:dataAccessConditionPortal>" +'\n' +
                           "                                    <ns8:nameOfConditions>" +'\n' +
                           "                                        <gco:CharacterString>names</gco:CharacterString>" +'\n' +
                           "                                    </ns8:nameOfConditions>" +'\n' +
                           "                                    <ns8:nonCommercialUse>" +'\n' +
                           "                                        <gco:Boolean>true</gco:Boolean>" +'\n' +
                           "                                    </ns8:nonCommercialUse>" +'\n' +
                           "                                    <ns8:thematicUsage>" +'\n' +
                           "                                        <ns8:GNC_ThematicTypeCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#Agriculture\" codeListValue=\"Agriculture\"/>" +'\n' +
                           "                                    </ns8:thematicUsage>" +'\n' +
                           "                                    <ns8:useRestrictions>" +'\n' +
                           "                                        <ns8:GNC_UseRestrictions>" +'\n' +
                           "                                            <ns8:extentOfRestrictions>" +'\n' +
                           "                                                <gmd:EX_GeographicDescription/>" +'\n' +
                           "                                            </ns8:extentOfRestrictions>" +'\n' +
                           "                                            <ns8:geographicRestrictions>" +'\n' +
                           "                                                <gco:Boolean>false</gco:Boolean>" +'\n' +
                           "                                            </ns8:geographicRestrictions>" +'\n' +
                           "                                            <ns8:otherConstraints>" +'\n' +
                           "                                                <gco:CharacterString>military</gco:CharacterString>" +'\n' +
                           "                                            </ns8:otherConstraints>" +'\n' +
                           "                                        </ns8:GNC_UseRestrictions>" +'\n' +
                           "                                    </ns8:useRestrictions>" +'\n' +
                           "                                    <ns8:usersRestrictions>" +'\n' +
                           "                                        <ns8:GNC_UsersRestrictions>" +'\n' +
                           "                                            <ns8:categoryOfUsers>" +'\n' +
                           "                                                <ns8:GNC_OrganisationTypeCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#Experts\" codeListValue=\"Experts\"/>" +'\n' +
                           "                                            </ns8:categoryOfUsers>" +'\n' +
                           "                                            <ns8:extentOfRestrictions>" +'\n' +
                           "                                                <gmd:EX_GeographicDescription/>" +'\n' +
                           "                                            </ns8:extentOfRestrictions>" +'\n' +
                           "                                            <ns8:otherConstraints>" +'\n' +
                           "                                                <gco:CharacterString>confidential</gco:CharacterString>" +'\n' +
                           "                                            </ns8:otherConstraints>" +'\n' +
                           "                                        </ns8:GNC_UsersRestrictions>" +'\n' +
                           "                                    </ns8:usersRestrictions>" +'\n' +
                           "                                </ns8:GNC_AccessConstraints>" +'\n' +
                           "                            </ns8:detailAccessConstraints>" +'\n' +
                           "                        </ns8:GNC_Access>" +'\n' +
                           "                    </ns8:access>" +'\n' +
                           "                    <ns8:relationType>" +'\n' +
                           "                        <ns8:GNC_RelationType>" +'\n' +
                           "                            <ns8:relationName>" +'\n' +
                           "                                <ns8:GNC_RelationNameCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#Usedby\" codeListValue=\"Usedby\"/>" +'\n' +
                           "                            </ns8:relationName>" +'\n' +
                           "                        </ns8:GNC_RelationType>" +'\n' +
                           "                    </ns8:relationType>" +'\n' +
                           "                </ns8:GNC_MaterialResource>" +'\n' +
                           "            </ns8:reference>" +'\n' +
                           "            <ns8:reference>" +'\n' +
                           "                <ns8:GNC_Reference/>" +'\n' +
                           "            </ns8:reference>" +'\n' +
                           "            <ns8:reference>" +'\n' +
                           "                <ns8:GNC_Document/>" +'\n' +
                           "            </ns8:reference>" +'\n' +
                           "            <ns8:reference>" +'\n' +
                           "                <ns8:GNC_Product/>" +'\n' +
                           "            </ns8:reference>" +'\n' +
                           "            <ns8:reference>" +'\n' +
                           "                <ns8:GNC_Service/>" +'\n' +
                           "            </ns8:reference>" +'\n' +
                           "            <ns8:reference>" +'\n' +
                           "                <ns8:GNC_Software/>" +'\n' +
                           "            </ns8:reference>" +'\n' +
                           "            <ns8:reference>" +'\n' +
                           "                <ns8:GNC_Training/>" +'\n' +
                           "            </ns8:reference>" +'\n' +
                           "            <ns8:reference>" +'\n' +
                           "                <ns8:GNC_EOProduct/>" +'\n' +
                           "            </ns8:reference>" +'\n' +
                           "            <ns8:typeOfOrganisation>" +'\n' +
                           "                <ns8:GNC_OrganisationTypeCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#Experts\" codeListValue=\"Experts\"/>" +'\n' +
                           "            </ns8:typeOfOrganisation>" +'\n' +
                           "        </ns8:GNC_OrganisationEntitie>" +'\n' +
                           "    </gmd:identificationInfo>" +'\n' +
                           "</gmd:MD_Metadata>" + '\n' ;
        assertEquals(expResult, result);

        pool.release(marshaller);

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void unmarshallingTest() throws JAXBException, MalformedURLException, URISyntaxException {

        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        String xml = 
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n'
                + "<gmd:MD_Metadata xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:ns8=\"http://www.mdweb-project.org/files/xsd\">" + '\n'
                + "    <gmd:identificationInfo>" + '\n'
                + "        <ns8:GNC_Resource>" + '\n'
                + "            <ns8:onlineInformation>" + '\n'
                + "                <gmd:CI_OnlineResource>" + '\n'
                + "                    <gmd:linkage>" + '\n'
                + "                        <gmd:URL>http://something.com</gmd:URL>" + '\n'
                + "                    </gmd:linkage>" + '\n'
                + "                </gmd:CI_OnlineResource>" + '\n'
                + "            </ns8:onlineInformation>" + '\n'
                + "        </ns8:GNC_Resource>" + '\n'
                + "    </gmd:identificationInfo>" + '\n'
                + "</gmd:MD_Metadata>" + '\n';

        Object result = unmarshaller.unmarshal(new StringReader(xml));

        DefaultMetadata expResult = new DefaultMetadata();

        GNC_Resource identInfo = new GNC_Resource();
        OnlineResource resource = new DefaultOnlineResource(URI.create("http://something.com"));
        identInfo.setOnlineInformation(resource);

        expResult.setIdentificationInfo(Arrays.asList(identInfo));

        assertEquals(expResult, result);

        xml =   "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n'
                + "<gmd:MD_Metadata xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:ns8=\"http://www.mdweb-project.org/files/xsd\">" + '\n'
                + "    <gmd:identificationInfo>" + '\n'
                + "        <ns8:GNC_OrganisationEntitie>" + '\n'
                + "            <ns8:onlineInformation>" + '\n'
                + "                <gmd:CI_OnlineResource>" + '\n'
                + "                    <gmd:linkage>" + '\n'
                + "                        <gmd:URL>http://something.com</gmd:URL>" + '\n'
                + "                    </gmd:linkage>" + '\n'
                + "                </gmd:CI_OnlineResource>" + '\n'
                + "            </ns8:onlineInformation>" + '\n'
                + "            <ns8:reference>" + '\n'
                + "                <ns8:GNC_MaterialResource>" + '\n'
                + "                    <ns8:isStillInProduction>" + '\n'
                + "                        <gco:Boolean>true</gco:Boolean>" + '\n'
                + "                   </ns8:isStillInProduction>" + '\n'
                + "                    <ns8:access>" + '\n'
                + "                        <ns8:GNC_Access>" + '\n'
                + "                            <ns8:description>" + '\n'
                + "                                <gco:CharacterString>desc1</gco:CharacterString>" + '\n'
                + "                            </ns8:description>" + '\n'
                + "                            <ns8:detailAccessConstraints>" + '\n'
                + "                                <ns8:GNC_AccessConstraints>" + '\n'
                + "                                    <ns8:dataAccessConditionPortal>" + '\n'
                + "                                         <gmd:URL>http://danstonchat.com</gmd:URL>" + '\n'
                + "                                    </ns8:dataAccessConditionPortal>" + '\n'
                + "                                    <ns8:nameOfConditions>" + '\n'
                + "                                        <gco:CharacterString>names</gco:CharacterString>" + '\n'
                + "                                    </ns8:nameOfConditions>" + '\n'
                + "                                    <ns8:nonCommercialUse>" + '\n'
                + "                                        <gco:Boolean>true</gco:Boolean>"+ '\n'
                + "                                   </ns8:nonCommercialUse>" + '\n'
                + "                                    <ns8:thematicUsage>" + '\n'
                + "                                        <ns8:GNC_ThematicTypeCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#Agriculture\" codeListValue=\"Agriculture\"/>" + '\n'
                + "                                    </ns8:thematicUsage>" + '\n'
                + "                                    <ns8:useRestrictions>" + '\n'
                + "                                        <ns8:GNC_UseRestrictions>" + '\n'
                + "                                            <ns8:extentOfRestrictions>" + '\n'
                + "                                                <gmd:EX_GeographicDescription/>" + '\n'
                + "                                            </ns8:extentOfRestrictions>" + '\n'
                + "                                            <ns8:geographicRestrictions>" + '\n'
                + "                                                <gco:Boolean>false</gco:Boolean>" + '\n'
                + "                                            </ns8:geographicRestrictions>" + '\n'
                + "                                            <ns8:otherConstraints>" + '\n'
                + "                                                <gco:CharacterString>military</gco:CharacterString>" + '\n'
                + "                                            </ns8:otherConstraints>" + '\n'
                + "                                        </ns8:GNC_UseRestrictions>" + '\n'
                + "                                    </ns8:useRestrictions>" + '\n'
                + "                                    <ns8:usersRestrictions>" + '\n'
                + "                                        <ns8:GNC_UsersRestrictions>" + '\n'
                + "                                            <ns8:categoryOfUsers>" + '\n'
                + "                                                <ns8:GNC_OrganisationTypeCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#Experts\" codeListValue=\"Experts\"/>" + '\n'
                + "                                            </ns8:categoryOfUsers>" + '\n'
                + "                                            <ns8:extentOfRestrictions>" + '\n'
                + "                                                <gmd:EX_GeographicDescription/>" + '\n'
                + "                                            </ns8:extentOfRestrictions>" + '\n'
                + "                                            <ns8:otherConstraints>" + '\n'
                + "                                                <gco:CharacterString>confidential</gco:CharacterString>" + '\n'
                + "                                            </ns8:otherConstraints>" + '\n'
                + "                                        </ns8:GNC_UsersRestrictions>" + '\n'
                + "                                    </ns8:usersRestrictions>" + '\n'
                + "                                </ns8:GNC_AccessConstraints>" + '\n'
                + "                            </ns8:detailAccessConstraints>" + '\n'
                + "                        </ns8:GNC_Access>" + '\n'
                + "                    </ns8:access>" + '\n'
                + "                </ns8:GNC_MaterialResource>" + '\n'
                + "            </ns8:reference>" + '\n'
                + "            <ns8:reference>" + '\n'
                + "                <ns8:GNC_Reference/>" + '\n'
                + "            </ns8:reference>" + '\n'
                + "            <ns8:reference>" + '\n'
                + "                <ns8:GNC_Document/>" + '\n'
                + "            </ns8:reference>" + '\n'
                + "            <ns8:reference>" + '\n'
                + "                <ns8:GNC_Product/>" + '\n'
                + "            </ns8:reference>" + '\n'
                + "            <ns8:reference>" + '\n'
                + "                <ns8:GNC_Service/>" + '\n'
                + "            </ns8:reference>" + '\n'
                + "            <ns8:reference>" + '\n'
                + "                <ns8:GNC_Software/>" + '\n'
                + "            </ns8:reference>" + '\n'
                + "            <ns8:reference>" + '\n'
                + "                <ns8:GNC_Training/>" + '\n'
                + "            </ns8:reference>" + '\n'
                + "            <ns8:reference>" + '\n'
                + "                <ns8:GNC_EOProduct/>" + '\n'
                + "            </ns8:reference>" + '\n'
                + "            <ns8:typeOfOrganisation>" + '\n'
                + "                <ns8:GNC_OrganisationTypeCode codeList=\"http://www.tc211.org/ISO19139/resources/codeList.xml#Experts\" codeListValue=\"Experts\"/>" + '\n'
                + "            </ns8:typeOfOrganisation>" + '\n'
                + "        </ns8:GNC_OrganisationEntitie>" + '\n'
                + "    </gmd:identificationInfo>" + '\n'
                + "</gmd:MD_Metadata>" + '\n';

        result = unmarshaller.unmarshal(new StringReader(xml));

        expResult = new DefaultMetadata();

        GNC_OrganisationEntitie identInfo2 = new GNC_OrganisationEntitie();
        resource = new DefaultOnlineResource(URI.create("http://something.com"));
        identInfo2.setOnlineInformation(resource);

        List<GNC_Resource> references = new ArrayList<GNC_Resource>();

        GNC_MaterialResource materialResource = new GNC_MaterialResource();
        materialResource.setIsStillInProduction(Boolean.TRUE);
        GNC_Access access = new GNC_Access();
        access.setDescription("desc1");
        GNC_AccessConstraints ac = new GNC_AccessConstraints();
        ac.setDataAccessConditionPortal(new URI("http://danstonchat.com"));
        ac.setNameOfConditions("names");
        ac.setNonCommercialUse(Boolean.TRUE);
        ac.setThematicUsage(GNC_ThematicTypeCode.AGRICULTURE);
        GNC_UseRestrictions ur = new GNC_UseRestrictions();
        ur.setGeographicRestrictions(Boolean.FALSE);
        ur.setOtherConstraints("military");
        ur.setExtentOfRestrictions(new DefaultGeographicDescription());
        ac.setUseRestrictions(ur);
        GNC_UsersRestrictions urr = new GNC_UsersRestrictions();
        urr.setCategoryOfUsers(GNC_OrganisationTypeCode.EXPERTS);
        urr.setOtherConstraints("confidential");
        urr.setExtentOfRestrictions(new DefaultGeographicDescription());
        ac.setUsersRestrictions(urr);
        access.setDetailAccessConstraints(ac);
        materialResource.setAccess(access);
        references.add(materialResource);

        GNC_Reference reference = new GNC_Reference();
        references.add(reference);

        GNC_Document document = new GNC_Document();
        references.add(document);

        GNC_Product product = new GNC_Product();
        references.add(product);

        GNC_Service service = new GNC_Service();
        references.add(service);

        GNC_Software software = new GNC_Software();
        references.add(software);

        GNC_Training training = new GNC_Training();
        references.add(training);

        GNC_EOProduct eoproduct = new GNC_EOProduct();
        references.add(eoproduct);

        identInfo2.setReference(references);
        
        identInfo2.setTypeOfOrganisation(GNC_OrganisationTypeCode.EXPERTS);
        expResult.setIdentificationInfo(Arrays.asList(identInfo2));

        assertEquals(expResult, result);
        
        pool.release(unmarshaller);
    }
}
