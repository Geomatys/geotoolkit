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
package org.geotoolkit.pending.demo.coverage;

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.postgresql.PGCoverageStore;
import org.geotoolkit.coverage.postgresql.PGCoverageStoreFactory;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Cédric Briançon (Geomatys)
 */
public class PGCoverageDemo {
    /**
     * Change me !
     */
    private static final String LAYER_NAME = "nww3.t00z.grib.Wind_speed_surface";

    /**
     * @param args the command line arguments
     *
     */
    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        final ParameterDescriptorGroup desc = PGCoverageStoreFactory.PARAMETERS_DESCRIPTOR;
        final ParameterValueGroup params = desc.createValue();
        Parameters.getOrCreate(PGCoverageStoreFactory.DATABASE, params).setValue("*****");
        Parameters.getOrCreate(PGCoverageStoreFactory.HOST, params).setValue("localhost");
        Parameters.getOrCreate(PGCoverageStoreFactory.PORT, params).setValue(5432);
        Parameters.getOrCreate(PGCoverageStoreFactory.USER, params).setValue("*****");
        Parameters.getOrCreate(PGCoverageStoreFactory.PASSWORD, params).setValue("*****");
        Parameters.getOrCreate(PGCoverageStoreFactory.NAMESPACE, params).setValue("no namespace");

        final CoverageStore store = CoverageStoreFinder.open(params);
        if (!(store instanceof PGCoverageStore)) {
            throw new DataStoreException("Wrong parameters");
        }

        final CoverageReference ref = store.getCoverageReference(new DefaultName(LAYER_NAME));
        final CoverageMapLayer layer = MapBuilder.createCoverageLayer(ref,
                new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "layer");

        final MapContext ctx = MapBuilder.createContext();
        ctx.layers().add(layer);

        JMap2DFrame.show(ctx);
    }
}
