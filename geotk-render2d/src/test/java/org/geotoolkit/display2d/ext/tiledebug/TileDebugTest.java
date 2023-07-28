/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.ext.tiledebug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jakarta.xml.bind.JAXBException;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.storage.memory.InMemoryTiledGridCoverageResource;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.style.MutableStyle;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileDebugTest {

    /**
     * Sanity test, only ensure the rendering is successfull without errors not the final result.
     */
    @Test
    public void renderTileDebugTest() throws PortrayalException, FactoryException, DataStoreException{

        final CoordinateReferenceSystem crs = CRS.forCode("EPSG:2154");

        final TileDebugSymbolizer gs = new TileDebugSymbolizer();

        final MutableStyle style = GO2Utilities.STYLE_FACTORY.style(gs);


        final InMemoryTiledGridCoverageResource resource = new InMemoryTiledGridCoverageResource(Names.createLocalName(null, null, "test"));
        resource.createTileMatrixSet(TileMatrices.createWorldWGS84Template(12));


        final MapLayer layer = MapBuilder.createCoverageLayer(resource);
        layer.setStyle(style);
        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);

        final SceneDef sdef = new SceneDef(context);
        final CanvasDef cdef = new CanvasDef();
        cdef.setDimension(new Dimension(100, 100));
        cdef.setBackground(Color.darkGray);
        cdef.setEnvelope(CRS.getDomainOfValidity(crs));

        final RenderedImage img = DefaultPortrayalService.portray(cdef, sdef);
        Assert.assertNotNull(img);

    }

    /**
     * Test Jaxb xml support.
     */
    @Test
    public void testXml() throws JAXBException, IOException {
        final TileDebugSymbolizer gs = new TileDebugSymbolizer();
        final MutableStyle style = GO2Utilities.STYLE_FACTORY.style(gs);

        final Path path = Files.createTempFile("xml", ".xml");
        IOUtilities.deleteOnExit(path);
        new StyleXmlIO().writeStyle(path, style, Specification.StyledLayerDescriptor.V_1_1_0);
    }
}
