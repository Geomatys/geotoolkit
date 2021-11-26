/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db.postgres;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.session.Session;
import static org.geotoolkit.db.postgres.PostgresProvider.*;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.ResourceId;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresVersioningTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final FeatureType FTYPE_SIMPLE;

    static{
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.addAttribute(String.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(Boolean.class).setName("boolean");
        ftb.addAttribute(Integer.class).setName("integer");
        ftb.addAttribute(Point.class).setName("point").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName("string");
        FTYPE_SIMPLE = ftb.build();
    }

    private PostgresStore store;

    public PostgresVersioningTest(){
    }

    private static Parameters params;

    /**
     * Find JDBC connection parameters in specified file at
     * "/home/.geotoolkit.org/test-pgfeature.properties".
     * If properties file doesn't find all tests are skipped.
     *
     * <p>To lunch tests user should create file with this architecture<br/>
     * for example : <br/>
     * database   = junit    (table name)<br/>
     * port       = 5432     (port number)<br/>
     * schema     = public   (schema name)<br/>
     * user       = postgres (user login)<br/>
     * password   = postgres (user password)<br/>
     * simpletype = false <br/>
     * namespace  = no namespace</p>
     * @throws IOException
     */
    @BeforeClass
    public static void beforeClass() throws IOException {
        String path = System.getProperty("user.home");
        path += "/.geotoolkit.org/test-pgfeature.properties";
        final File f = new File(path);
        Assume.assumeTrue(f.exists());
        final Properties properties = new Properties();
        properties.load(new FileInputStream(f));
        params = Parameters.castOrWrap(org.geotoolkit.parameter.Parameters.toParameter((Map)properties, PARAMETERS_DESCRIPTOR, false));
    }

    private void reload(boolean simpleType) throws DataStoreException, VersioningException {
        if(store != null){
            store.close();
        }

        //open in complex type to delete all types
        params.getOrCreate(PostgresProvider.SIMPLETYPE).setValue(false);
        store = (PostgresStore) DataStores.open(params);
        for(GenericName n : store.getNames()){
            VersionControl vc = store.getVersioning(n.toString());
            vc.dropVersioning();
            store.deleteFeatureType(n.toString());
        }
        assertTrue(store.getNames().isEmpty());
        store.close();

        //reopen the way it was asked
        params.getOrCreate(PostgresProvider.SIMPLETYPE).setValue(simpleType);
        store = (PostgresStore) DataStores.open(params);
        assertTrue(store.getNames().isEmpty());

        //delete historisation functions, he must create them himself
        store.dropHSFunctions();

    }

    @Test
    public void testSimpleTypeVersioning() throws DataStoreException, VersioningException {
        reload(true);
        List<Version> versions;
        Version version;
        Feature feature;
        ResourceId fid;
        Version v1;
        Version v2;
        Version v3;
        FeatureIterator ite;
        Query qb = new Query();

        //create table
        final FeatureType refType = FTYPE_SIMPLE;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        assertNotNull(store.getQueryCapabilities());
        assertTrue(store.getQueryCapabilities().handleVersioning());


        //get version control
        final VersionControl vc = store.getVersioning(refType.getName().toString());
        assertNotNull(vc);
        assertTrue(vc.isEditable());
        assertFalse(vc.isVersioned());

        ////////////////////////////////////////////////////////////////////////
        //start versioning /////////////////////////////////////////////////////
        vc.startVersioning();
        assertTrue(vc.isVersioned());
        versions = vc.list();
        assertTrue(versions.isEmpty());

        //check the version table is not visible in the feature types
        store.refreshMetaModel();
        final Set<GenericName> names = store.getNames();
        assertEquals(1, names.size());

        ////////////////////////////////////////////////////////////////////////
        //make an insert ///////////////////////////////////////////////////////
        final Point firstPoint = GF.createPoint(new Coordinate(56, 45));
        feature = refType.newInstance();
        feature.setPropertyValue("id","0");
        feature.setPropertyValue("boolean",Boolean.TRUE);
        feature.setPropertyValue("integer",14);
        feature.setPropertyValue("point",firstPoint);
        feature.setPropertyValue("string","someteststring");
        store.addFeatures(refType.getName().toString(), Collections.singleton(feature));

        //we should have one version
        versions = vc.list();
        assertEquals(1, versions.size());
        version = versions.get(0);
        Date date = version.getDate();

        //ensure normal reading is correct without version----------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getPropertyValue("boolean"));
            assertEquals(14,                feature.getPropertyValue("integer"));
            assertEquals(firstPoint,        feature.getPropertyValue("point"));
            assertEquals("someteststring",  feature.getPropertyValue("string"));
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        //ensure normal reading is correct with version-------------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(version.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getPropertyValue("boolean"));
            assertEquals(14,                feature.getPropertyValue("integer"));
            assertEquals(firstPoint,        feature.getPropertyValue("point"));
            assertEquals("someteststring",  feature.getPropertyValue("string"));
        }finally{
            ite.close();
        }

        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }


        ////////////////////////////////////////////////////////////////////////
        //make an update ///////////////////////////////////////////////////////
        final Point secondPoint = GF.createPoint(new Coordinate(-12, 21));
        final Map<String,Object> updates = new HashMap<>();
        updates.put("boolean", Boolean.FALSE);
        updates.put("integer", -3);
        updates.put("point", secondPoint);
        updates.put("string", "anothertextupdated");

        store.updateFeatures(refType.getName().toString(), fid, updates);

        //we should have two versions
        versions = vc.list();
        assertEquals(2, versions.size());
        v1 = versions.get(0);
        v2 = versions.get(1);
        //should be ordered starting from the oldest
        assertTrue(v1.getDate().compareTo(v2.getDate()) < 0);

        //ensure normal reading is correct without version----------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.FALSE,       feature.getPropertyValue("boolean"));
            assertEquals(-3,                  feature.getPropertyValue("integer"));
            assertEquals(secondPoint,         feature.getPropertyValue("point"));
            assertEquals("anothertextupdated",feature.getPropertyValue("string"));
        }finally{
            ite.close();
        }

        //ensure normal reading is correct with version-------------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v2.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.FALSE,       feature.getPropertyValue("boolean"));
            assertEquals(-3,                  feature.getPropertyValue("integer"));
            assertEquals(secondPoint,         feature.getPropertyValue("point"));
            assertEquals("anothertextupdated",feature.getPropertyValue("string"));
        }finally{
            ite.close();
        }

        //ensure reading a previous version works ------------------------------
        qb = new Query();;
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v1.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getPropertyValue("boolean"));
            assertEquals(14,                feature.getPropertyValue("integer"));
            assertEquals(firstPoint,        feature.getPropertyValue("point"));
            assertEquals("someteststring",  feature.getPropertyValue("string"));
        }finally{
            ite.close();
        }

        //ensure reading a previous version using not exact date----------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionDate(new Date(v1.getDate().getTime()+400));
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getPropertyValue("boolean"));
            assertEquals(14,                feature.getPropertyValue("integer"));
            assertEquals(firstPoint,        feature.getPropertyValue("point"));
            assertEquals("someteststring",  feature.getPropertyValue("string"));
        }finally{
            ite.close();
        }

        ////////////////////////////////////////////////////////////////////////
        //delete record ////////////////////////////////////////////////////////

        store.removeFeatures(refType.getName().toString(), fid);
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertEquals(0, store.getCount(qb));

        //we should have three versions
        versions = vc.list();
        assertEquals(3, versions.size());
        v1 = versions.get(0);
        v2 = versions.get(1);
        v3 = versions.get(2);
        //should be ordered starting from the oldest
        assertTrue(v2.getDate().compareTo(v1.getDate()) > 0);
        assertTrue(v3.getDate().compareTo(v2.getDate()) > 0);

        //ensure we have nothing if no version set -----------------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertTrue(store.createSession(true).getFeatureCollection(qb).isEmpty());

        //ensure we have nothing if latest version set -------------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v3.getLabel());
        assertTrue(store.createSession(true).getFeatureCollection(qb).isEmpty());

        //ensure we have nothing with date after deletion ----------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionDate(new Date(v3.getDate().getTime()+400));
        assertTrue(store.createSession(true).getFeatureCollection(qb).isEmpty());

        //ensure reading version 1 works ---------------------------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v1.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
            assertEquals(14,                feature.getProperty("integer").getValue());
            assertEquals(firstPoint,        feature.getProperty("point").getValue());
            assertEquals("someteststring",  feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }

        //ensure reading version 2 works ---------------------------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v2.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.FALSE,      feature.getProperty("boolean").getValue());
            assertEquals(-3,                 feature.getProperty("integer").getValue());
            assertEquals(secondPoint,        feature.getProperty("point").getValue());
            assertEquals("anothertextupdated",feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }

        ////////////////////////////////////////////////////////////////////////
        //drop versioning //////////////////////////////////////////////////////

        vc.dropVersioning();
        assertTrue(vc.isEditable());
        assertFalse(vc.isVersioned());
        versions = vc.list();
        assertTrue(versions.isEmpty());

        //ensure we have no record----------------------------------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertTrue(store.createSession(true).getFeatureCollection(qb).isEmpty());
    }

    /**
     * Check versions are created on each call on the session.
     */
    @Test
    public void testVersioningSynchrone() throws DataStoreException, VersioningException{
        reload(true);
        List<Version> versions;
        Version version;
        Feature feature;
        ResourceId fid;
        FeatureIterator ite;
        Query qb = new Query();

        final FeatureType refType = FTYPE_SIMPLE;
        store.createFeatureType(refType);
        final VersionControl vc = store.getVersioning(refType.getName().toString());

        ////////////////////////////////////////////////////////////////////////
        //start versioning /////////////////////////////////////////////////////
        vc.startVersioning();
        versions = vc.list();
        assertTrue(versions.isEmpty());

        final Session session = store.createSession(false);

        ////////////////////////////////////////////////////////////////////////
        //make an insert ///////////////////////////////////////////////////////
        final Point firstPoint = GF.createPoint(new Coordinate(56, 45));
        feature = refType.newInstance();
        feature.setPropertyValue("id","0");
        feature.setPropertyValue("boolean",Boolean.TRUE);
        feature.setPropertyValue("integer",14);
        feature.setPropertyValue("point",firstPoint);
        feature.setPropertyValue("string","someteststring");
        session.addFeatures(refType.getName().toString(), Collections.singleton(feature));

        //we should have one version
        versions = vc.list();
        assertEquals(1, versions.size());
        version = versions.get(0);
        Date date = version.getDate();

        //ensure normal reading is correct without version----------------------
        qb = new Query();
        qb.setTypeName(refType.getName().toString());
        ite = session.getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }

        ////////////////////////////////////////////////////////////////////////
        //make an update 1 /////////////////////////////////////////////////////
        final Point secondPoint = GF.createPoint(new Coordinate(-12, 21));
        Map<String,Object> updates = new HashMap<>();
        updates.put("boolean", Boolean.FALSE);
        updates.put("integer", -3);
        updates.put("point", secondPoint);
        updates.put("string", "anothertextupdated");
        session.updateFeatures(refType.getName().toString(), fid, updates);

        //we should have two versions
        versions = vc.list();
        assertEquals(2, versions.size());

        ////////////////////////////////////////////////////////////////////////
        //make an update 2 /////////////////////////////////////////////////////
        final Point thirdPoint = GF.createPoint(new Coordinate(48, -51));
        updates = new HashMap<>();
        updates.put("boolean", Boolean.TRUE);
        updates.put("integer", -89);
        updates.put("point", thirdPoint);
        updates.put("string", "thridupdatetext");
        session.updateFeatures(refType.getName().toString(), fid, updates);

        //we should have three versions
        versions = vc.list();
        assertEquals(3, versions.size());

        ////////////////////////////////////////////////////////////////////////
        //delete record ////////////////////////////////////////////////////////

        session.removeFeatures(refType.getName().toString(), fid);
        qb = new Query();
        qb.setTypeName(refType.getName().toString());
        assertEquals(0, session.getCount(qb));

        //we should have four versions
        versions = vc.list();
        assertEquals(4, versions.size());

        ////////////////////////////////////////////////////////////////////////
        //make an insert ///////////////////////////////////////////////////////
        Point fourthPoint = GF.createPoint(new Coordinate(66, 11));
        feature = refType.newInstance();
        feature.setPropertyValue("id","0");
        feature.setPropertyValue("boolean",Boolean.FALSE);
        feature.setPropertyValue("integer",22);
        feature.setPropertyValue("point",fourthPoint);
        feature.setPropertyValue("string","fourthupdateString");
        session.addFeatures(refType.getName().toString(), Collections.singleton(feature));

        //we should have five versions
        versions = vc.list();
        assertEquals(5, versions.size());
    }

    /**
     * Check versions are created only on session commit calls.
     */
    @Test
    public void testVersioningASynchrone() throws DataStoreException, VersioningException{
        reload(true);
        List<Version> versions;
        Version version;
        Feature feature;
        ResourceId fid;
        FeatureIterator ite;
        Query qb = new Query();

        final FeatureType refType = FTYPE_SIMPLE;
        store.createFeatureType(refType);
        final VersionControl vc = store.getVersioning(refType.getName().toString());

        ////////////////////////////////////////////////////////////////////////
        //start versioning /////////////////////////////////////////////////////
        vc.startVersioning();
        versions = vc.list();
        assertTrue(versions.isEmpty());

        final Session session = store.createSession(true);

        ////////////////////////////////////////////////////////////////////////
        //make an insert ///////////////////////////////////////////////////////
        final Point firstPoint = GF.createPoint(new Coordinate(56, 45));
        feature = refType.newInstance();
        feature.setPropertyValue("id","0");
        feature.setPropertyValue("boolean",Boolean.TRUE);
        feature.setPropertyValue("integer",14);
        feature.setPropertyValue("point",firstPoint);
        feature.setPropertyValue("string","someteststring");
        session.addFeatures(refType.getName().toString(), Collections.singleton(feature));

        //we should have 0 version
        versions = vc.list();
        assertEquals(0, versions.size());

        session.commit(); // <-- creates a version

        //we should have 1 version
        versions = vc.list();
        assertEquals(1, versions.size());
        version = versions.get(0);
        Date date = version.getDate();

        //ensure normal reading is correct without version----------------------
        qb = new Query();
        qb.setTypeName(refType.getName().toString());
        ite = session.getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }

        ////////////////////////////////////////////////////////////////////////
        //make 2 updates at the time ///////////////////////////////////////////
        final Point secondPoint = GF.createPoint(new Coordinate(-12, 21));
        Map<String,Object> updates = new HashMap<>();
        updates.put("boolean", Boolean.FALSE);
        updates.put("integer", -3);
        updates.put("point", secondPoint);
        updates.put("string", "anothertextupdated");
        session.updateFeatures(refType.getName().toString(), fid, updates);

        //we should have 1 version
        versions = vc.list();
        assertEquals(1, versions.size());

        final Point thirdPoint = GF.createPoint(new Coordinate(48, -51));
        updates = new HashMap<>();
        updates.put("boolean", Boolean.TRUE);
        updates.put("integer", -89);
        updates.put("point", thirdPoint);
        updates.put("string", "thridupdatetext");
        session.updateFeatures(refType.getName().toString(), fid, updates);

        //we should have 1 version
        versions = vc.list();
        assertEquals(1, versions.size());

        session.commit();  // <-- creates a version

        //we should have two versions
        versions = vc.list();
        assertEquals(2, versions.size());

        //ensure we read the latest --------------------------------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.TRUE,      feature.getProperty("boolean").getValue());
            assertEquals(-89,               feature.getProperty("integer").getValue());
            assertEquals(thirdPoint,        feature.getProperty("point").getValue());
            assertEquals("thridupdatetext", feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }

        ////////////////////////////////////////////////////////////////////////
        // make delete + insert at the same time ///////////////////////////////

        session.removeFeatures(refType.getName().toString(), fid);
        qb = new Query();
        qb.setTypeName(refType.getName().toString());
        assertEquals(0, session.getCount(qb));

        //we should have two versions
        versions = vc.list();
        assertEquals(2, versions.size());

        ////////////////////////////////////////////////////////////////////////
        //delete record ////////////////////////////////////////////////////////

        session.removeFeatures(refType.getName().toString(), fid);
        qb = new Query();
        qb.setTypeName(refType.getName().toString());
        assertEquals(0, session.getCount(qb));

        //we should have two versions
        versions = vc.list();
        assertEquals(2, versions.size());

        Point fourthPoint = GF.createPoint(new Coordinate(66, 11));
        feature = refType.newInstance();
        feature.setPropertyValue("id","0");
        feature.setPropertyValue("boolean",Boolean.FALSE);
        feature.setPropertyValue("integer",22);
        feature.setPropertyValue("point",fourthPoint);
        feature.setPropertyValue("string","fourthupdateString");
        session.addFeatures(refType.getName().toString(), Collections.singleton(feature));

        //we should have two versions
        versions = vc.list();
        assertEquals(2, versions.size());

        session.commit();  // <-- creates a version

        //we should have three versions
        versions = vc.list();
        assertEquals(3, versions.size());

        //ensure we read the latest --------------------------------------------
        qb = new Query();
        qb.setTypeName(refType.getName().toString());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            assertEquals(Boolean.FALSE,        feature.getProperty("boolean").getValue());
            assertEquals(22,                   feature.getProperty("integer").getValue());
            assertEquals(fourthPoint,          feature.getProperty("point").getValue());
            assertEquals("fourthupdateString", feature.getProperty("string").getValue());
        }finally{
            ite.close();
        }
    }

    @Test
    public void testTrimVersioning() throws DataStoreException, VersioningException {
        reload(true);
        List<Version> versions;
        Feature feature;
        ResourceId fid;
        Version v0;
        Version v1;
        Version v2;
        FeatureIterator ite;
        Query qb = new Query();

        //create table
        final FeatureType refType = FTYPE_SIMPLE;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        //get version control
        final VersionControl vc = store.getVersioning(refType.getName().toString());
        assertNotNull(vc);
        assertTrue(vc.isEditable());
        assertFalse(vc.isVersioned());

        //start versioning /////////////////////////////////////////////////////
        vc.startVersioning();
        assertTrue(vc.isVersioned());
        versions = vc.list();
        assertTrue(versions.isEmpty());

        //make an insert ///////////////////////////////////////////////////////
        final Point firstPoint = GF.createPoint(new Coordinate(56, 45));
        feature = refType.newInstance();
        feature.setPropertyValue("id","0");
        feature.setPropertyValue("boolean",Boolean.TRUE);
        feature.setPropertyValue("integer",14);
        feature.setPropertyValue("point",firstPoint);
        feature.setPropertyValue("string","someteststring");
        store.addFeatures(refType.getName().toString(), Collections.singleton(feature));

        //we should have one version
        versions = vc.list();
        assertEquals(1, versions.size());

        // get identifier
        //ensure normal reading is correct without version----------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }

        //make an update ///////////////////////////////////////////////////////
        final Point secondPoint = GF.createPoint(new Coordinate(-12, 21));
        final Map<String,Object> updates = new HashMap<>();
        updates.put("boolean", Boolean.FALSE);
        updates.put("integer", -3);
        updates.put("point", secondPoint);
        updates.put("string", "anothertextupdated");

        store.updateFeatures(refType.getName().toString(), fid, updates);

        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }

        //make a 2nd update ///////////////////////////////////////////////////////
        final Point thirdPoint = GF.createPoint(new Coordinate(145, -221));
        final Map<String,Object> updates2 = new HashMap<>();
        updates2.put("boolean", Boolean.FALSE);
        updates2.put("integer", 150);
        updates2.put("point", thirdPoint);
        updates2.put("string", "secondtextupdated");

        store.updateFeatures(refType.getName().toString(), fid, updates2);

        //get all versions organized in increase dates order.
        versions = vc.list();
        assertEquals(3, versions.size());
        v0 = versions.get(0);
        v1 = versions.get(1);
        v2 = versions.get(2);

        /* first trim between v1 date and v2 date (named middle date) to verify
         * deletion of first version and update v1 date at trim date.*/
        final Date middle = new Date((v1.getDate().getTime() + v2.getDate().getTime()) >> 1);
        vc.trim(middle);

        versions = vc.list();
        assertEquals(2, versions.size());
        //ensure version 0 does not exist
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v0.getLabel());
        try {
            store.createSession(true).getFeatureCollection(qb).isEmpty();
            fail("should not find version");
        } catch(FeatureStoreRuntimeException ex) {
            //ok
        }

        //ensure version v1 begin at middle date.
        assertEquals(vc.list().get(0).getDate().getTime(), middle.getTime());

        /* second trim at exactely the begining of the third version to verify,
         * deletion of second version and third version existence.*/
        vc.trim(v2);
        versions = vc.list();
        assertEquals(1, versions.size());
        //ensure version 1 does not exist
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v1.getLabel());
        try {
            store.createSession(true).getFeatureCollection(qb).isEmpty();
            fail("should not find version");
        } catch(FeatureStoreRuntimeException ex) {
            //ok
        }
        //ensure version v2 begin time doesn't change.
        assertEquals(vc.list().get(0).getDate().getTime(), v2.getDate().getTime());

        /* third trim just after v3 version date, to verify that v3
         * version date become trim date */
        final long lastDate = v2.getDate().getTime()+400;
        vc.trim(new Date(lastDate));
        versions = vc.list();
        assertEquals(1, versions.size());
        //ensure version v2 begin time become lastDate.
        assertEquals(vc.list().get(0).getDate().getTime(), lastDate);
    }

    @Test
    public void testRevertVersioning() throws DataStoreException, VersioningException {
        reload(true);
        List<Version> versions;
        Feature feature;
        ResourceId fid;
        Version v0;
        Version v1;
        Version v2;
        FeatureIterator ite;
        Query qb = new Query();

        //create table
        final FeatureType refType = FTYPE_SIMPLE;
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());

        //get version control
        final VersionControl vc = store.getVersioning(refType.getName().toString());
        assertNotNull(vc);
        assertTrue(vc.isEditable());
        assertFalse(vc.isVersioned());

        //start versioning /////////////////////////////////////////////////////
        vc.startVersioning();
        assertTrue(vc.isVersioned());
        versions = vc.list();
        assertTrue(versions.isEmpty());

        //make an insert ///////////////////////////////////////////////////////
        final Point firstPoint = GF.createPoint(new Coordinate(56, 45));
        feature = refType.newInstance();
        feature.setPropertyValue("id","0");
        feature.setPropertyValue("boolean",Boolean.TRUE);
        feature.setPropertyValue("integer",14);
        feature.setPropertyValue("point",firstPoint);
        feature.setPropertyValue("string","someteststring");
        store.addFeatures(refType.getName().toString(), Collections.singleton(feature));

        //we should have one version
        versions = vc.list();
        assertEquals(1, versions.size());

        // get identifier
        //ensure normal reading is correct without version----------------------
        qb = new Query();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }

        //make an update ///////////////////////////////////////////////////////
        final Point secondPoint = GF.createPoint(new Coordinate(-12, 21));
        final Map<String,Object> updates = new HashMap<>();
        updates.put("boolean", Boolean.FALSE);
        updates.put("integer", -3);
        updates.put("point", secondPoint);
        updates.put("string", "anothertextupdated");

        store.updateFeatures(refType.getName().toString(), fid, updates);

        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }

        //make a remove ///////////////////////////////////////////////////////
        store.removeFeatures(refType.getName().toString(), fid);

        //ensure test table is empty
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertTrue(store.createSession(true).getFeatureCollection(qb).isEmpty());

        //get all versions organized in increase dates order.
        versions = vc.list();
        assertEquals(3, versions.size());
        v0 = versions.get(0);
        v1 = versions.get(1);
        v2 = versions.get(2);

        /* first revert between v1 date and v2 date (named middle date) to verify
         * re - insertion of feature in the original base and update v2 ending date become null.*/
        final Date middle = new Date((v1.getDate().getTime() + v2.getDate().getTime()) >> 1);// =/2
        vc.revert(middle);
        versions = vc.list();
        assertEquals(2, versions.size());

        //ensure version v2 does not exist
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v2.getLabel());
        try {
            store.createSession(true).getFeatureCollection(qb).isEmpty();
            fail("should not find version");
        } catch(FeatureStoreRuntimeException ex) {
            //ok
        }

        //ensure test table contain feature from version 1
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertFalse(store.createSession(true).getFeatureCollection(qb).isEmpty());

        Feature featV1;
        Feature feat;
        // feature from test base result.
        qb = new Query();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feat = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        // feature from version v1.
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v1.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            featV1 = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        assertTrue(feat.getProperty("boolean").equals(featV1.getProperty("boolean")));
        assertTrue(feat.getProperty("integer").equals(featV1.getProperty("integer")));
        assertTrue(feat.getProperty("point").equals(featV1.getProperty("point")));
        assertTrue(feat.getProperty("string").equals(featV1.getProperty("string")));

        /* second revert at v0 begin date to verify update roll back, and verify
         * feature update from history table into original base.*/
        vc.revert(v0.getDate());
        versions = vc.list();
        assertEquals(1, versions.size());

        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v1.getLabel());
        try {
            store.createSession(true).getFeatureCollection(qb).isEmpty();
            fail("should not find version");
        } catch(FeatureStoreRuntimeException ex) {
            //ok
        }

        //ensure test table contain feature from version 1
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertFalse(store.createSession(true).getFeatureCollection(qb).isEmpty());

        // feature from test base result.
        qb = new Query();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feat = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        // feature from version v1.
        qb = new Query();
        qb.setTypeName(refType.getName());
        qb.setVersionLabel(v0.getLabel());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            featV1 = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        assertTrue(feat.getProperty("boolean").equals(featV1.getProperty("boolean")));
        assertTrue(feat.getProperty("integer").equals(featV1.getProperty("integer")));
        assertTrue(feat.getProperty("point").equals(featV1.getProperty("point")));
        assertTrue(feat.getProperty("string").equals(featV1.getProperty("string")));
    }

    @Test
    public void testDistinctSchema() throws DataStoreException, VersioningException, FileNotFoundException, IOException {
        reload(true);
        List<Version> versions;
        Feature feature;
        ResourceId fid;
        Version v0;
        Version v1;
        Version v2;
        FeatureIterator ite;
        Query qb = new Query();

        final FeatureType refType = FTYPE_SIMPLE;

        // ------------------- initialize public2 schema --------------------
        /// creation 2eme table
        PostgresStore store2;
        final ParameterValueGroup params2  = params.clone();
        params2.parameter("schema").setValue("public2");
        store2 = (PostgresStore) DataStores.open(params2);

        //-------------- create schema in public2 schema --------------------
        try {
            ((DataStoreFactory)store2.getProvider()).create(params2);
        } catch (Exception ex) {
            //schema public2 already exist
        }

        for(GenericName n : store2.getNames()) {
            VersionControl vc = store2.getVersioning(n.toString());
            vc.dropVersioning();
            store2.deleteFeatureType(n.toString());
        }
        assertTrue(store2.getNames().isEmpty());

        //delete historisation functions, he must create them himself
        store2.dropHSFunctions();

        //-------------- create table in public schema --------------------
        store.createFeatureType(refType);
        assertEquals(1, store.getNames().size());
        assertTrue(store2.getNames().isEmpty());

        //get version control
        final VersionControl vcP1 = store.getVersioning(refType.getName().toString());
        assertNotNull(vcP1);
        assertTrue(vcP1.isEditable());
        assertFalse(vcP1.isVersioned());

        //-------------------- start versioning in public schema ---------------
        vcP1.startVersioning();
        assertTrue(vcP1.isVersioned());
        versions = vcP1.list();
        assertTrue(versions.isEmpty());

        //--------------------- table creation in public2 schema ---------------
        store2.createFeatureType(refType);
        assertEquals(1, store2.getNames().size());

        //get version control
        final VersionControl vcP2 = store2.getVersioning(refType.getName().toString());
        assertNotNull(vcP2);
        assertTrue(vcP2.isEditable());
        assertFalse(vcP2.isVersioned());

        //-------------------- start versioning in public schema ---------------
        vcP2.startVersioning();
        assertTrue(vcP2.isVersioned());
        versions = vcP2.list();
        assertTrue(versions.isEmpty());

        /* insert, update and delete some elements in public schema and verify
         * public2 schema stay empty, to verify the 2th schema are actions distincts.*/

        //make an insert ///////////////////////////////////////////////////////
        final Point firstPoint = GF.createPoint(new Coordinate(56, 45));
        feature = refType.newInstance();
        feature.setPropertyValue("id","0");
        feature.setPropertyValue("boolean",Boolean.TRUE);
        feature.setPropertyValue("integer",14);
        feature.setPropertyValue("point",firstPoint);
        feature.setPropertyValue("string","someteststring");
        store.addFeatures(refType.getName().toString(), Collections.singleton(feature));

        // ensure test table in public2 schema is empty
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertTrue(store2.createSession(true).getFeatureCollection(qb).isEmpty());

        // ensure history test table in public2 schema is empty
        assertTrue(vcP2.list().isEmpty());

        //make an update ///////////////////////////////////////////////////////

        // get feature to update
        // get identifier
        qb = new Query();
        qb.setTypeName(refType.getName());
        ite = store.createSession(true).getFeatureCollection(qb).iterator();
        try{
            feature = ite.next();
            fid = FeatureExt.getId(feature);
        }finally{
            ite.close();
        }

        try {
            //wait a bit just to have some space between version dates
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }

        final Point secondPoint = GF.createPoint(new Coordinate(-12, 21));
        final Map<String,Object> updates = new HashMap<>();
        updates.put("boolean", Boolean.FALSE);
        updates.put("integer", -3);
        updates.put("point", secondPoint);
        updates.put("string", "anothertextupdated");
        store.updateFeatures(refType.getName().toString(), fid, updates);

        // ensure test table in public2 schema is empty
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertTrue(store2.createSession(true).getFeatureCollection(qb).isEmpty());

        // ensure history test table in public2 schema is empty
        assertTrue(vcP2.list().isEmpty());

        //make a remove ///////////////////////////////////////////////////////
        store.removeFeatures(refType.getName().toString(), fid);

        // ensure test table in public2 schema is empty
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertTrue(store2.createSession(true).getFeatureCollection(qb).isEmpty());

        // ensure history test table in public2 schema is empty
        assertTrue(vcP2.list().isEmpty());

        //get all versions organized in increase dates order.
        versions = vcP1.list();
        assertEquals(3, versions.size());
        v0 = versions.get(0);
        v1 = versions.get(1);
        v2 = versions.get(2);

        vcP1.revert(v1.getDate());
        // ensure test table in public2 schema is empty
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertTrue(store2.createSession(true).getFeatureCollection(qb).isEmpty());

        // ensure history test table in public2 schema is empty
        assertTrue(vcP2.list().isEmpty());

        vcP1.trim(v1.getDate());
        // ensure test table in public2 schema is empty
        qb = new Query();
        qb.setTypeName(refType.getName());
        assertTrue(store2.createSession(true).getFeatureCollection(qb).isEmpty());

        // ensure history test table in public2 schema is empty
        assertTrue(vcP2.list().isEmpty());
    }
}
