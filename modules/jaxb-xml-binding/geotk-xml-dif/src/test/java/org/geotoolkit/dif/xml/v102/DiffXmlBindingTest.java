/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.dif.xml.v102;

import java.io.InputStream;
import java.io.StringWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.test.xml.DocumentComparator;
import org.apache.sis.xml.MarshallerPool;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author guilhem
 */
public class DiffXmlBindingTest extends org.geotoolkit.test.TestBase {

    private static MarshallerPool pool;
    private Marshaller   marshaller;
    private Unmarshaller unmarshaller;
    private static ObjectFactory FACTORY = new ObjectFactory();

    @BeforeClass
    public static void setUpClass() throws Exception {
        pool = new MarshallerPool(JAXBContext.newInstance(ObjectFactory.class), new HashMap<>());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws JAXBException {
        marshaller = pool.acquireMarshaller();
        unmarshaller = pool.acquireUnmarshaller();
    }

    @After
    public void tearDown() {
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
        if (unmarshaller != null) {
            pool.recycle(unmarshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void marshallingTest() throws Exception {
        DIF dif = new DIF();
        dif.setEntryID(new EntryIDType("id-1", null));
        dif.setEntryTitle("title");
        dif.setSummary(new SummaryType("some abstract", "some purpose"));
        dif.getMetadataAssociation().add(new MetadataAssociationType(new EntryIDType("id-1", null), MetadataAssociationTypeEnum.PARENT, null));

        PersonnelType author = new PersonnelType();
        author.getRole().add(PersonnelRoleEnum.METADATA_AUTHOR);
        AddressType addr = new AddressType("12 rue blip", "Montpellier", "Occitanie", "34000", "France");
        PhoneType phone = new PhoneType("0466441122", PhoneTypeEnum.DIRECT_LINE);
        author.getContactGroup().add(new ContactGroupType("Geomatys", addr, phone, "contact@geomatys.com"));
        dif.getPersonnel().add(author);

        PersonnelType author2 = new PersonnelType();
        author2.getRole().add(PersonnelRoleEnum.METADATA_AUTHOR);
        AddressType addr2 = new AddressType("12 rue blip", "Montpellier", "Occitanie", "34000", "France");
        PhoneType phone2 = new PhoneType("0466441122", PhoneTypeEnum.DIRECT_LINE);
        author2.getContactPerson().add(new ContactPersonType("987-887", "jean", "pierre", "imbert", addr2, phone2, "contact@geomatys.com"));
        dif.getPersonnel().add(author2);

        PersonnelType tech = new PersonnelType();
        tech.getRole().add(PersonnelRoleEnum.TECHNICAL_CONTACT);
        AddressType addrTech = new AddressType("12 rue blip", "Montpellier", "Occitanie", "34000", "France");
        PhoneType phoneTech = new PhoneType("0466441122", PhoneTypeEnum.DIRECT_LINE);
        tech.getContactPerson().add(new ContactPersonType("1244110-777", "michel","henry", "bel", addrTech, phoneTech, "tech-contact@geomatys.com"));
        dif.getPersonnel().add(tech);

        BoundingRectangleType rec = new BoundingRectangleType();
        rec.setEasternmostLongitude("5.0");
        rec.setWesternmostLongitude("5.0");
        rec.setNorthernmostLatitude("5.0");
        rec.setSouthernmostLatitude("5.0");
        rec.setMaximumAltitude("10");
        rec.setMinimumAltitude("3");
        final Geometry geom = new Geometry(rec);
        dif.setSpatialCoverage(new SpatialCoverageType(geom));

        dif.setMetadataDates(new MetadataDatesType("2010-12-01T12:00Z"));

        ContactPersonType distributor = new ContactPersonType("666-777", "pedro","juan", "lopez", addrTech, phoneTech, "distributor@geomatys.com");
        OrgPersonnelType distPers = new OrgPersonnelType(OrganizationPersonnelRoleEnum.DATA_CENTER_CONTACT, distributor, null);

        dif.getOrganization().add(new OrganizationType(OrganizationTypeEnum.DISTRIBUTOR, new OrganizationNameType("Geom", "Geomatys"), "http://www.geomatys.com", distPers));

        dif.getDatasetLanguage().add(DatasetLanguageEnum.FRENCH);

        dif.setDatasetProgress(DatasetProgressEnum.COMPLETE);

        dif.getLocation().add(new LocationType("GARD"));

        dif.getISOTopicCategory().add(new ISOTopicCategoryType("economy"));

        dif.getScienceKeywords().add(new ScienceKeywordsType("science category", "science topic", "science term"));

        dif.getDataResolution().add(new DataResolutionType());

        dif.getTemporalCoverage().add(new TemporalCoverageType(new RangeDateTimeType("2010-12-01T12:00Z", "2010-12-02T23:00Z")));

        dif.getMultimediaSample().add(new MultimediaSampleType("http://localhost:8080/quicklook/SCENE_5_083-342_09_04_16_09_38_39_1_J.png", "quicklook", "image/png"));

        dif.getAncillaryKeyword().add("anc kw1");
        dif.getAncillaryKeyword().add("anc kw2");

        dif.setAccessConstraints("some acces constraint");

        dif.setUseConstraints(new UseConstraintsType("use csts"));

        dif.getDistribution().add(new DistributionType("http://link.com", "3.12", "binary", "free"));
        dif.getDistribution().add(new DistributionType("http://link/img", "6", "image/png", "10$"));

        dif.getRelatedURL().add(new RelatedURLType(new URLContentType("GET DATA"), "http", Arrays.asList("https://gportal.jaxa.jp/"), "jaxa", new DisplayableTextType(" desc blma"), null));

        StringWriter sw = new StringWriter();

        marshaller.marshal(dif, sw);
        System.out.println(sw.toString());

        final DocumentComparator comparator = new DocumentComparator(sw.toString(), getResourceAsStream("org/geotoolkit/dif/xml/v102/dif.xml"));
        comparator.ignoredAttributes.add("http://www.w3.org/2000/xmlns:*");
        comparator.ignoredAttributes.add("http://www.w3.org/2001/XMLSchema-instance:schemaLocation");
        comparator.ignoreComments = true;
        comparator.compare();
    }

    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }

    /**
     * Return an input stream of the specified resource.
     */
    public static InputStream getResourceAsStream(final String url) {
        final ClassLoader cl = getContextClassLoader();
        return cl.getResourceAsStream(url);
    }
}
