/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.pending.demo.coverage;

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.sql.CoverageSQLStoreFactory;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author guilhem
 */
public class CoverageSQLDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();
        
        ParameterDescriptorGroup desc = CoverageSQLStoreFactory.PARAMETERS;
        System.out.println("desc:" + desc);

        ParameterValueGroup params = desc.createValue();
        params.parameter("URL").setValue("jdbc:postgresql://db.geomatys.com:5432/coverages-tmp");
        params.parameter("rootDirectory").setValue("/home/guilhem/data/PostGRID");
        params.parameter("user").setValue("username");
        params.parameter("password").setValue("******");

        CoverageStore store = CoverageStoreFinder.open(params);
        CoverageReference ref = store.getCoverageReference(new DefaultName("Levitus"));

        CoverageMapLayer layer = MapBuilder.createCoverageLayer(ref);

        MapContext ctx = MapBuilder.createContext();
        ctx.layers().add(layer);

        JMap2DFrame.show(ctx);
    }


}
