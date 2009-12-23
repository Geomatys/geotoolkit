/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.shptest.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;


/**
 * Test support for test cases which require an "online" resource, such as an
 * external server or database.
 * <p>
 * Online tests work off of a "fixture". A fixture is a properties file which
 * defines connection parameters for some remote service. Each online test case
 * must define the id of the fixture is uses with {@link #getFixtureId()}.
 * </p>
 * <p>
 * Fixtures are stored under the users home directory, under the "{@code .geotoolkit}"
 * directory. In the event that a fixture does not exist, the test case is
 * aborted.
 * </p>
 * <p>
 * Online tests connect to remote / online resources. Test cases should do all
 * connection / disconnection in the {@link #connect} and {@link #disconnect()}
 * methods.
 * </p>
 *
 * <p>
 * The default behaviour of this class is that if {@link #connect()} throws an exception, the test
 * suite is disabled, causing each test to pass without being run. In addition, exceptions thrown by
 * {@link #disconnect()} are ignored. This behaviour allows tests to be robust against transient
 * outages of online resources, but also means that local software failures in {@link #connect()} or
 * {@link #disconnect()} will be silent.
 * </p>
 * 
 * <p>
 * To have exceptions thrown by {@link #connect()} and {@link #disconnect()} cause tests to fail,
 * set <code>skip.on.failure=false</code> in the fixture property file. This restores the
 * traditional behaviour of unit tests, that is, that exceptions cause unit tests to fail.
 * </p>
 *
 * @module pending
 * @since 2.4
 * @version $Id$
 * @author Justin Deoliveira, The Open Planning Project
 */
public abstract class OnlineTestCase extends TestCase {

    /**
     * The key in the test fixture property file used to set the behaviour of the online test if
     * {@link #connect()} fails.
     */
    public static final String SKIP_ON_FAILURE_KEY = "skip.on.failure";

    /**
     * The default value used for {@link #SKIP_ON_FAILURE_KEY} if it is not present.
     */
    public static final String SKIP_ON_FAILURE_DEFAULT = "true";

    /**
     * The test fixture, {@code null} if the fixture is not available.
     */
    protected Properties fixture;

    /**
     * Flag that determines effect of exceptions in connect/disconnect. If true (the default),
     * exceptions in connect cause the the test to be disabled, and exceptions in disconnect to be
     * ignored. If false, exceptions will be rethrown, and cause the test to fail.
     */
    protected boolean skipOnFailure = true;

    /**
     * Loads the test fixture for the test case.
     * <p>
     * The fixture id is obtained via {@link #getFixtureId()}.
     * </p>
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // load the fixture
        File base = new File(System.getProperty("user.home") + File.separator + ".geotoolkit");
        String fixtureId = getFixtureId();
        if (fixtureId == null) {
            fixture = null; // not available (turn test off)            
            return;
        }
        File fixtureFile = new File(base, fixtureId.replace('.',
                File.separatorChar).concat(".properties"));

        if (fixtureFile.exists()) {
            InputStream input = new BufferedInputStream(new FileInputStream(fixtureFile));
            try {
                fixture = new Properties();
                fixture.load(input);
            } finally {
                input.close();
            }
            skipOnFailure = Boolean.parseBoolean(fixture.getProperty(SKIP_ON_FAILURE_KEY,
                    SKIP_ON_FAILURE_DEFAULT));
            // call the setUp template method
            try {
                connect();
            } catch (Exception e) {
                if (skipOnFailure) {
                    // disable the test
                    fixture = null;
                    // leave some trace of the swallowed exception
                    e.printStackTrace();
                } else {
                    // do not swallow the exception
                    throw e;
                }
            }

        }
    }

    /**
     * Tear down method for test, calls through to {@link #disconnect()} if the
     * test is active.
     */
    @Override
    protected void tearDown() throws Exception {
        if (fixture != null) {
            try {
                disconnect();
            } catch (Exception e) {
                if (skipOnFailure) {
                    // do nothing
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * Connection method, called from {@link #setUp()}.
     * <p>
     * Subclasses should do all initialization / connection here. In the event
     * of a connection not being available, this method should throw an
     * exception to abort the test case.
     * </p>
     * 
     * @throws Exception if the connection failed.
     */
    protected void connect() throws Exception {
    }

    /**
     * Disconnection method, called from {@link #tearDown()}.
     * <p>
     * Subclasses should do all cleanup here.
     * </p>
     * 
     * @throws Exception if the disconnection failed.
     */
    protected void disconnect() throws Exception {
    }

    /**
     * Override which checks if the fixture is available. If not the test is not
     * executed.
     */
    @Override
    protected void runTest() throws Throwable {
        // if the fixture was loaded, run
        if (fixture != null) {
            super.runTest();
        }
        // otherwise do nothing
    }

    /**
     * The fixture id for the test case.
     * <p>
     * This name is hierarchical, similar to a java package name. Example:
     * {@code "postgis.demo_bc"}.
     * </p>
     * 
     * @return The fixture id.
     */
    protected abstract String getFixtureId();
}
