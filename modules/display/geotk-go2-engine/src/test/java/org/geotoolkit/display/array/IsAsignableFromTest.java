/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display.array;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class IsAsignableFromTest extends TestCase{

    /**
     * Returns the test suite.
     */
    public static Test suite() {
        return new TestSuite(IsAsignableFromTest.class);
    }

    /**
     * Constructs a test case with the given name.
     */
    public IsAsignableFromTest(final String name) {
        super(name);
    }

    public void testAssignable(){
        //TODO
    }

    /**
     * V�rifie le bon fonctionnement de cette classe. Cette m�thode peut �tre appel�e
     * sans argument.  Les assertions doivent �tre activ�es (option <code>-ea</code>)
     * pour que la v�rification soit effective. Cette m�thode peut aussi �tre ex�cut�e
     * sans les assertions pour tester les performances.
     */
    public static void main(final String[] args) {
        final IsAsignableFromTest test = new IsAsignableFromTest(null);
        test.testAssignable();
    }

}
