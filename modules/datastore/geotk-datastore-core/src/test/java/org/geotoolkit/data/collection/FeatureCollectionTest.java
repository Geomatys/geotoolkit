/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
 *    
 *    Created on July 21, 2003, 5:58 PM
 */

package org.geotoolkit.data.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.geotoolkit.data.FeatureCollectionUtilities;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

/**
 *
 * @author  en
 * @module pending
 */
public class FeatureCollectionTest extends TestCase {
  
  FeatureCollection<SimpleFeatureType, SimpleFeature> features;
  
  public FeatureCollectionTest(String testName){
    super(testName);
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(FeatureCollectionTest.class);
    return suite;
  }
  
  protected void setUp() throws Exception {
    features = FeatureCollectionUtilities.createCollection();
    SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
    tb.setName( "Dummy" );
    
    SimpleFeatureBuilder b = new SimpleFeatureBuilder(tb.buildFeatureType());
    
    for (int i = 0; i < 100; i++) {
      features.add(b.buildFeature(null)); 
    }
  }
  
  public Collection randomPiece(Collection original) {
    LinkedList next = new LinkedList();
    Iterator og = original.iterator();
    while (og.hasNext()) {
      if (Math.random() > .5) {
        next.add(og.next());
      } else {
        og.next();
      }
    }
    return next;
  }
  public Collection randomPiece(FeatureCollection original) {
      LinkedList next = new LinkedList();
      Iterator og = original.iterator();
      try {
          while (og.hasNext()) {
            if (Math.random() > .5) {
              next.add(og.next());
            } else {
              og.next();
            }
          }
          return next;
      }
      finally {
          original.close( og );
      }
    }  
  public void testBounds() throws Exception {
    PrecisionModel pm = new PrecisionModel();
    Geometry[] g = new Geometry[4];
    GeometryFactory gf = new GeometryFactory();
    
    g[0] = gf.createPoint( new Coordinate(0,0) );
    g[1] = gf.createPoint( new Coordinate(0,10));
    g[2] = gf.createPoint( new Coordinate(10,0));
    g[3] = gf.createPoint( new Coordinate(10,10));

    GeometryCollection gc = gf.createGeometryCollection( g );
    
    SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
    tb.setName("bounds");
    tb.add( "p1", Point.class, DefaultGeographicCRS.WGS84 );
    
    SimpleFeatureType t = tb.buildFeatureType();
    
    FeatureCollection<SimpleFeatureType, SimpleFeature> fc = FeatureCollectionUtilities.createCollection();
    SimpleFeatureBuilder b = new SimpleFeatureBuilder(t);
    
    for (int i = 0; i < g.length; i++) {
        b.add( g[i]);
        fc.add( b.buildFeature(null) );
    } 
    assertEquals(gc.getEnvelopeInternal(),fc.getBounds());
  }
  
  public void testSetAbilities() {
    int size = features.size();
    features.addAll(randomPiece(features));
    assertEquals(features.size(),size);
  }
  
  public void testAddRemoveAllAbilities() throws Exception {
    Collection half = randomPiece(features);
    Collection otherHalf = FeatureCollectionUtilities.list(features);
    otherHalf.removeAll(half);
    features.removeAll(half);
    assertTrue(features.containsAll(otherHalf));
    assertTrue(!features.containsAll(half));
    features.removeAll(otherHalf);
    assertTrue(features.size() == 0);
    features.addAll(half);
    assertTrue(features.containsAll(half));
    features.addAll(otherHalf);
    assertTrue(features.containsAll(otherHalf));
    features.retainAll(otherHalf);
    assertTrue(features.containsAll(otherHalf));
    assertTrue(!features.containsAll(half));
    features.addAll(otherHalf);
    Iterator i = features.iterator();
    while (i.hasNext()) {
      i.next();
      i.remove();
    }
    assertEquals(features.size(),0);
    
    SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
    tb.setName( "XXX" );
    SimpleFeatureBuilder b = new SimpleFeatureBuilder(tb.buildFeatureType());
    
    assertTrue(! features.remove(b.buildFeature(null)));
  }
  
  public void testAssorted() {
    FeatureCollection<SimpleFeatureType, SimpleFeature> copy = FeatureCollectionUtilities.createCollection();
    copy.addAll(features);
    copy.clear();
    assertTrue(copy.isEmpty());
    copy.addAll(features);
    assertTrue(!copy.isEmpty());
    
    List<SimpleFeature> list = FeatureCollectionUtilities.list(features);
    SimpleFeature[] f1 = (SimpleFeature[]) list.toArray(new SimpleFeature[list.size()]);
    SimpleFeature[] f2 = (SimpleFeature[]) features.toArray(new SimpleFeature[list.size()]);
    assertEquals(f1.length,f2.length);
    for (int i = 0; i < f1.length; i++) {
      assertSame(f1[i], f2[i]);
    }
    FeatureIterator<SimpleFeature> copyIterator = copy.features();
    FeatureIterator<SimpleFeature> featuresIterator = features.features();
    while (copyIterator.hasNext() && featuresIterator.hasNext()) {
      assertEquals(copyIterator.next(),featuresIterator.next());
    }
    
    FeatureCollection<SimpleFeatureType, SimpleFeature> listen = FeatureCollectionUtilities.createCollection();
    ListenerProxy counter = new ListenerProxy();
    listen.addListener(counter);
    listen.addAll(features);
    assertEquals(1,counter.changeEvents);
    listen.removeListener(counter);
    listen.removeAll(FeatureCollectionUtilities.list(features));
    assertEquals(1,counter.changeEvents);
  }
  
  static class ListenerProxy implements CollectionListener {
    int changeEvents = 0;
    
    public void collectionChanged(CollectionEvent tce) {
      changeEvents++;
    }
    
  }
}
