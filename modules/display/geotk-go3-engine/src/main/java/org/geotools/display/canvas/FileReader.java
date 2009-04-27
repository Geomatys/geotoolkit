/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.display.canvas;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.data.Query;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotoolkit.factory.Hints;
import org.geotools.feature.FeatureCollection;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;

/**
 *
 * @author axel
 */
public class FileReader {

    private final URL url;

private static final String GEOTIFF = ".tif";


    public FileReader(URL urlpath) {
        this.url = urlpath;
    }

    /**
     *
     * @return
     * @throws java.io.IOException
     */
    public FeatureCollection shapeReader() throws IOException {
        FeatureSource source = null;
        FeatureCollection features;

        try {
            System.out.println("Valeur de l'url =>" + this.url.getFile());
            ShapefileDataStore store = new ShapefileDataStore(this.url);
            String name = store.getTypeNames()[0];
            source = store.getFeatureSource(name);
            System.out.println(name);

            System.out.println(source.getCount(Query.ALL));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        features = source.getFeatures();
        Envelope env = source.getBounds();
        double xmin = env.getMinimum(0);
        double ymin = env.getMinimum(1);
        double xmax = env.getMaximum(0);
        double ymax = env.getMaximum(1);
        System.out.println("Xmin " + xmin + " ymin " + ymin + " xmax " + xmax + " ymax " + ymax);
      
        return features;
    }

    /**
     *
     * @return
     * @throws java.net.URISyntaxException
     */
    public GridCoverage rasterReader()throws URISyntaxException {
        GeoTiffReader reader;
        GridCoverage cover = null;
        if (this.url != null) {
            String name = this.url.getFile().toLowerCase();
            File file = null;
                file = new File(this.url.getFile());
           
            if (file != null) {
                    try {
                         System.out.println("File path => "+file.getName());
                        reader = new GeoTiffReader(file,new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
                        cover = (GridCoverage2D) reader.read(null);
                    } catch (DataSourceException ex) {
                        cover = null;
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        cover = null;
                        ex.printStackTrace();
                    }
                }
            }
        
        return cover;
    }
    /**
     *
     * @return the Path value for a URL
     */
    public String getFilePath(){
        return this.url.getPath();
    }
    /**
     *
     * @return the name value for a URL 
     */
    public String getFileName(){
        return this.url.getFile();
    }

}
