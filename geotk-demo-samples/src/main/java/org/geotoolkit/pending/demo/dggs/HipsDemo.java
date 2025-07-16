
package org.geotoolkit.pending.demo.dggs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.service.GraphicsPortrayer;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.hips.HIPSProvider;
import org.geotoolkit.hips.HIPSStore;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

public class HipsDemo {

    private static final MutableStyleFactory SF = GO2Utilities.STYLE_FACTORY;

    public static void main(String[] args) throws Exception {

        try (HIPSStore store = new HIPSStore(HIPSProvider.provider(), new StorageConnector(new URI("https://data.camras.nl/hips/hipslist")))) {

            for (Resource r : store.components()) {
                System.out.println(r.getIdentifier().get());

                if (r instanceof DiscreteGlobalGridResource dgr) {

                    final GridGeometry domain = new GridGeometry(new GridExtent(720,360), CRS.getDomainOfValidity(CommonCRS.WGS84.normalizedGeographic()), GridOrientation.REFLECTION_Y);

                    final MutableStyle styleRaster = SF.style(SF.rasterSymbolizer());
                    final MapLayer layerRaster = new MapLayer();
                    layerRaster.setData(dgr);
                    layerRaster.setStyle(styleRaster);

                    GraphicsPortrayer p = new GraphicsPortrayer();
                    p.setDomain(domain);
                    p.portray(layerRaster);
                    BufferedImage image = p.getImage();

                    ImageIO.write(image, "png", new File("hips.png"));

                    break;
                }
            }
        }

    }

}
