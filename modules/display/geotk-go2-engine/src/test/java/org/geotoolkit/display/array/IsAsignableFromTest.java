/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.display.array;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author sorel
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
