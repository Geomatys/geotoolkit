/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.referencing.factory.web;

import java.util.Collection;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.referencing.operation.projection.TransverseMercator;
import org.geotoolkit.referencing.operation.projection.Orthographic;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.Commons.*;


/**
 * Tests {@link AutoCRSFactory}.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @author Andrea Aime (OpenGeo)
 * @version 3.16
 *
 * @since 2.2
 */
public final strictfp class AutoCRSFactoryTest extends ReferencingTestBase {
    /**
     * The factory to test.
     */
    private CRSAuthorityFactory factory;

    /**
     * Initializes the factory to test.
     */
    @Before
    public void setUp() {
        factory = new AutoCRSFactory();
    }

    /**
     * Tests the registration in {@link AuthorityFactoryFinder}.
     */
    @Test
    public void testFactoryFinder() {
        final Collection<String> authorities = AuthorityFactoryFinder.getAuthorityNames();
        assertTrue(authorities.contains("AUTO"));
        assertTrue(authorities.contains("AUTO2"));
        factory = AuthorityFactoryFinder.getCRSAuthorityFactory("AUTO", null);
        assertTrue(factory instanceof AutoCRSFactory);
        assertSame(factory, AuthorityFactoryFinder.getCRSAuthorityFactory("AUTO2", null));
    }

    /**
     * Checks the authority names.
     */
    @Test
    public void testAuthority() {
        final Citation authority = factory.getAuthority();
        assertTrue (Citations.identifierMatches(authority, "AUTO"));
        assertTrue (Citations.identifierMatches(authority, "AUTO2"));
        assertFalse(Citations.identifierMatches(authority, "EPSG"));
        assertFalse(Citations.identifierMatches(authority, "CRS"));
    }

    /**
     * Tests {@link CRSAuthorityFactory#getDescriptionText(String)}.
     *
     * @throws FactoryException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testDescription() throws FactoryException {
        assertEquals("WGS 84 / Auto UTM", factory.getDescriptionText("AUTO:42001,0,0").toString());
        assertEquals("WGS 84 / Auto UTM", factory.getDescriptionText("AUTO:42001").toString());
    }

    /**
     * Tests that using an incomplete code throws an exception.
     *
     * @throws FactoryException Should never happen if not of kind {@link NoSuchAuthorityCodeException}.
     *
     * @since 3.16
     */
    @Test(expected=NoSuchAuthorityCodeException.class)
    public void testIncompleteCode() throws FactoryException {
        factory.createObject("AUTO:42001");
    }

    /**
     * UDIG requires this to work.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void test42001() throws FactoryException {
        final ProjectedCRS proj = factory.createProjectedCRS("AUTO:42001,0.0,0.0");
        assertNotNull("auto-utm", proj);
        assertSame   (proj, factory.createObject("AUTO :42001, 0,0"));
        assertSame   (proj, factory.createObject("AUTO2:42001, 0,0"));
        assertSame   (proj, factory.createObject(      "42001, 0,0"));
        assertSame   (proj, factory.createObject(      "42001 ,0,0"));
        assertNotSame(proj, factory.createObject("AUTO :42001,30,0"));
        assertEquals ("Transverse_Mercator", proj.getConversionFromBase().getMethod().getName().getCode());
    }

    /**
     * Same tests than {@link #test42001}, but with units.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void test42001Units() throws FactoryException {
        final ProjectedCRS proj = factory.createProjectedCRS("AUTO:42001,9001,0.0,0.0");
        assertNotNull("auto-utm", proj);
        assertSame   (SI.METRE, proj.getCoordinateSystem().getAxis(0).getUnit());
        assertSame   (proj, factory.createObject("AUTO :42001,  0,0"));
        assertSame   (proj, factory.createObject("AUTO :42001, ,0,0"));
        assertSame   (proj, factory.createObject("AUTO :42001, 9001,0,0"));
        assertSame   (proj, factory.createObject("AUTO2:42001, 9001,0,0"));
        assertSame   (proj, factory.createObject(      "42001, 9001,0,0"));
        assertNotSame(proj, factory.createObject("AUTO :42001, 9001,30,0"));
        assertEquals ("Transverse_Mercator", proj.getConversionFromBase().getMethod().getName().getCode());
        assertEquals (TransverseMercator.class, getProjectionClass(proj));
        /*
         * Use an other units.
         */
        final ProjectedCRS projUS = factory.createProjectedCRS("AUTO:42001,9002,0.0,0.0");
        assertSame(NonSI.FOOT, projUS.getCoordinateSystem().getAxis(0).getUnit());
    }

    /**
     * Tests the polar, equatorial and oblique cases.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void test42003() throws FactoryException {
        ProjectedCRS proj = factory.createProjectedCRS("AUTO:42003,9001,0.0,0");
        assertEquals ("Orthographic", proj.getConversionFromBase().getMethod().getName().getCode());
        assertEquals (Orthographic.class, getProjectionClass(proj));

        proj = factory.createProjectedCRS("AUTO:42003,9001,0.0,90");
        assertEquals ("Orthographic", proj.getConversionFromBase().getMethod().getName().getCode());
        assertEquals (Orthographic.class, getProjectionClass(proj));

        proj = factory.createProjectedCRS("AUTO:42003,9001,0.0,45");
        assertEquals ("Orthographic", proj.getConversionFromBase().getMethod().getName().getCode());
        assertEquals (Orthographic.class, getProjectionClass(proj));
    }

    /**
     * Tests a case which should have been optimized as an affine transform.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void test42004() throws FactoryException {
        final ProjectedCRS proj = factory.createProjectedCRS("AUTO:42004,9001,0.0,35");
        assertEquals ("Equidistant Cylindrical (Spherical)", proj.getConversionFromBase().getMethod().getName().getCode());
        assertNull   ("Should have been optimized to an AffineTransform.", getProjectionClass(proj));
        final double stdParallel1 = proj.getConversionFromBase().getParameterValues().parameter("standard_parallel").doubleValue();
        assertEquals("The parameter should still available, even if the projection " +
                "has been optimized to an AffineTransform.", 35.0, stdParallel1, 1E-9);
    }

    /**
     * Tests the affine transform case with different units.
     *
     * @throws FactoryException Should never happen.
     * @throws NoninvertibleTransformException Should never happen.
     */
    @Test
    public void testUnits() throws FactoryException, NoninvertibleTransformException {
        AffineTransform tr1, tr2;
        tr1 = (AffineTransform) factory.createProjectedCRS("42004,9001,0,35").getConversionFromBase().getMathTransform();
        tr2 = (AffineTransform) factory.createProjectedCRS("42004,9002,0,35").getConversionFromBase().getMathTransform();
        tr2 = tr2.createInverse();
        tr2.concatenate(tr1);
        assertEquals("Expected any kind of scale.", 0, tr2.getType() & ~AffineTransform.TYPE_MASK_SCALE);
        assertEquals("Expected the conversion factor from foot to metre.", 0.3048, tr2.getScaleX(), 1E-9);
        assertEquals("Expected the conversion factor from foot to metre.", 0.3048, tr2.getScaleY(), 1E-9);
    }
}
