/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive;

import org.apache.sis.referencing.CommonCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.primitive.Point;

import junit.framework.TestCase;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.JTSPositionFactory;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;

/**
 * @author gdavis
 *
 * @module pending
 */
public class PointImplTest extends TestCase {

	public void testNewEmptyPoint() {
		Point point = new JTSPoint();
		assertNotNull(point.getCoordinateReferenceSystem());
		DirectPosition position = point.getDirectPosition();
		assertNotNull(position);
	}

	public void testNewPointHere() {
		DirectPosition here = new GeneralDirectPosition(CommonCRS.WGS84.normalizedGeographic());
		here.setOrdinate(0, 48.44);
		here.setOrdinate(1, -123.37); // 48.44,-123.37

		Point point = new JTSPoint(here);
		assertNotNull(point.getCoordinateReferenceSystem());
		assertEquals(here.getCoordinateReferenceSystem(), point
				.getCoordinateReferenceSystem());
		assertEquals(here, point.getDirectPosition());
		assertEquals(here.hashCode(), point.getDirectPosition().hashCode());
	}

	public void testNewFactoryPointHere() {
		PositionFactory gFact = new JTSPositionFactory(
				CommonCRS.WGS84.normalizedGeographic());
		double[] ords = { 48.44, -123.37 };
		DirectPosition here = gFact.createDirectPosition(ords);

		Point point = new JTSPoint(here);
		assertNotNull(point.getCoordinateReferenceSystem());
		assertEquals(here.getCoordinateReferenceSystem(), point
				.getCoordinateReferenceSystem());
		assertEquals(here, point.getDirectPosition());
		assertEquals(here.hashCode(), point.getDirectPosition().hashCode());
	}

	public void testPicoStuff() {
//		DefaultPicoContainer container = new DefaultPicoContainer(); // parent
//
//		// Teach Container about Factory Implementations we want to use
//		container.registerComponentImplementation(PositionFactoryImpl.class);
//		container.registerComponentImplementation(PrimitiveFactoryImpl.class);
//		container.registerComponentImplementation(GeometryFactoryImpl.class);
//
//		// Confirm Container cannot create anything yet
//		assertNull(container
//				.getComponentInstanceOfType(CoordinateReferenceSystem.class));
//		try {
//			container.getComponentInstanceOfType(PositionFactory.class);
//			//fail("We should not be able to make a position factory yet - we do not have a CRS");
//			// we need to work with out a crs on the grounds that FactorySPI
//			// has to be able to find our class :-(
//		} catch (Exception expected) {
//		}
//		// let's provide a CRS now and confirm everything works
//		container.registerComponentInstance(AbstractCRS.castOrCopy(CommonCRS.WGS84.geographic3D()).forConvention(AxesConvention.RIGHT_HANDED));
//
//		PositionFactory positionFactory =
//			(PositionFactory) container.getComponentInstanceOfType(PositionFactory.class);
//
//		assertSame(AbstractCRS.castOrCopy(CommonCRS.WGS84.geographic3D()).forConvention(AxesConvention.RIGHT_HANDED), positionFactory.getCoordinateReferenceSystem());
	}

//	/**
//	 * Now that we understand containers let's start testing stuff ...
//	 * @param crs
//	 * @return container
//	 */
//	protected PicoContainer container( CoordinateReferenceSystem crs ){
//		DefaultPicoContainer container = new DefaultPicoContainer(); // parent
//
//		container.registerComponentImplementation(PositionFactoryImpl.class);
//		container.registerComponentImplementation(PrimitiveFactoryImpl.class);
//		container.registerComponentImplementation(GeometryFactoryImpl.class);
//		container.registerComponentInstance( crs );
//
//		return container;
//	}

//	public void testWSG84Point(){
//		PicoContainer c = container( CommonCRS.WGS84.normalizedGeographic() );
//
//		// Do actually test stuff
//
//		double[] ords = { 48.44, -123.37 };
//		PositionFactory factory = (PositionFactory) c.getComponentInstanceOfType( PositionFactory.class );
//
//		assertNotNull(factory);
//		DirectPosition here = factory.createDirectPosition(ords);
//		Point point = new PointImpl(here);
//		assertNotNull(point.getCoordinateReferenceSystem());
//		assertEquals(here.getCoordinateReferenceSystem(), point
//				.getCoordinateReferenceSystem());
//		assertEquals(here, point.getPosition());
//		assertEquals(here.hashCode(), point.getPosition().hashCode());
//	}

	public void testWSG843DPoint(){

		// Do actually test stuff
		double[] ords = { 48.44, -123.37, 0.0 };
		PositionFactory factory = new JTSPositionFactory(AbstractCRS.castOrCopy(CommonCRS.WGS84.geographic3D()).forConvention(AxesConvention.RIGHT_HANDED));

		assertNotNull(factory);
		DirectPosition here = factory.createDirectPosition(ords);
		Point point = new JTSPoint(here);
		assertNotNull(point.getCoordinateReferenceSystem());
		assertEquals(here.getCoordinateReferenceSystem(), point
				.getCoordinateReferenceSystem());
		assertEquals(here, point.getDirectPosition());
		assertEquals(here.hashCode(), point.getDirectPosition().hashCode());
	}

}
