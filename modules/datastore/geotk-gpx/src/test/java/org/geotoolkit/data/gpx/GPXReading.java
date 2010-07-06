/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.gpx;

import com.vividsolutions.jts.geom.GeometryFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotoolkit.data.AbstractReadingTests;
import org.geotoolkit.data.AbstractReadingTests.ExpectedResult;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.gpx.model.GPXModelConstants;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.type.Name;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GPXReading {//extends AbstractReadingTests{

    private final GPXDataStore store;
    private final Set<Name> names = new HashSet<Name>();
    private final List<ExpectedResult> expecteds = new ArrayList<ExpectedResult>();

    public GPXReading() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, IOException{

        final File file = File.createTempFile("temp", "gpx");
        file.delete();
        //file.deleteOnExit();
        store = new GPXDataStore(file);

        final GeometryFactory gf = new GeometryFactory();

        GeneralEnvelope env = null; //we don't expect any envelope

        //Complex type not supported
//        names.add(GPXModelConstants.TYPE_GPX_ENTITY.getName());
//        expecteds.add(new ExpectedResult(GPXModelConstants.TYPE_GPX_ENTITY.getName(),GPXModelConstants.TYPE_GPX_ENTITY,0,env));

        names.add(GPXModelConstants.TYPE_WAYPOINT.getName());
        expecteds.add(new ExpectedResult(GPXModelConstants.TYPE_WAYPOINT.getName(),GPXModelConstants.TYPE_WAYPOINT,0,env));

        //Complex type not supported
//        names.add(GPXModelConstants.TYPE_ROUTE.getName());
//        expecteds.add(new ExpectedResult(GPXModelConstants.TYPE_ROUTE.getName(),GPXModelConstants.TYPE_ROUTE,0,env));
//
//        names.add(GPXModelConstants.TYPE_TRACK.getName());
//        expecteds.add(new ExpectedResult(GPXModelConstants.TYPE_TRACK.getName(),GPXModelConstants.TYPE_TRACK,0,env));

        
        //@todo ------------- test way points ----------------------------------------
//        FeatureWriter writer = store.getFeatureWriterAppend(GPXModelConstants.TYPE_WAYPOINT.getName());
//        try{
//            Feature f = writer.next();
//
//            final Collection<Property> props = new ArrayList<Property>();
//            for(PropertyDescriptor desc : f.getType().getDescriptors()){
//                if(desc instanceof AttributeDescriptor && desc)
//                props.add(new DefaultProperty(FileU, desc));
//            }
//
//            f.setAttribute("geometry", gf.createPoint(new Coordinate(10, 11)));
//            f.setAttribute("stringProp", "hop1");
//            f.setAttribute("intProp", 15);
//            f.setAttribute("doubleProp", 32.2);
//            writer.write();
//        }finally{
//            writer.close();
//        }
//
//        GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
//        env.setRange(0, -5, 10);
//        env.setRange(1, -1, 11);
//
//        names.add(GPXModelConstants.TYPE_WAYPOINT.getName());
//        expecteds.add(new ExpectedResult(GPXModelConstants.TYPE_WAYPOINT.getName(),GPXModelConstants.TYPE_WAYPOINT,2,env));

        //@todo test routes : waiting for refractoring of feature
        //@todo test tracks : waiting for refractoring of feature

    }

//    @Override
//    protected synchronized DataStore getDataStore() {
//        return store;
//    }
//
//    @Override
//    protected Set<Name> getExpectedNames() {
//        return names;
//    }
//
//    @Override
//    protected List<ExpectedResult> getReaderTests() {
//        return expecteds;
//    }

}
