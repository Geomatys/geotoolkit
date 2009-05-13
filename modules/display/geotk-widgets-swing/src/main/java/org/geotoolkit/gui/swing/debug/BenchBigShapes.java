

package org.geotoolkit.gui.swing.debug;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.MapContext;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel
 */
public class BenchBigShapes {


    public BenchBigShapes(){

        FilterFactory ff = FactoryFinder.getFilterFactory(null);
        System.out.println(ff.getClass());


//        MapContext context = BenchContextBuilder.buildBigRoadContext();
        MapContext context = ContextBuilder.buildRealCityContext();

//        MapLayer[] layers = context.getLayers();
//
//        long before = System.nanoTime();
//
//        for(MapLayer layer : layers){
//
//            FeatureSource<? extends FeatureType, ? extends Feature> fts = layer.getFeatureSource();
//            FeatureCollection<? extends FeatureType, ? extends Feature> fc = null;
//            try {
//                fc = fts.getFeatures();
//            } catch (IOException ex) {
//                Logger.getLogger(BenchBigShapes.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            FeatureIterator<? extends Feature> ite = fc.features();
//            while(ite.hasNext()){
//                ite.next().getDefaultGeometryProperty().getValue();
//            }
//
//        }
//
//        long after = System.nanoTime();
//
//        System.out.println( (after-before)/1000000000f  +" sec");


        //make 3 pass to load stuffs
        testBasicService(context,null,"imageGO2.png");
        testBasicService(context,null,"imageGO2.png");
        testBasicService(context,null,"imageGO2.png");

        int NBPASS = 10;

        float[] times = new float[NBPASS];


        //test with generalisation
        for(int i=0; i<NBPASS;i++){
            System.out.println("pass "+i);
            times[i] = testBasicService(context,null,"imageGO2.png");
        }


        //summary---------------------------------------------------------------

        float sum = 0;
        for(int i=0;i<NBPASS;i++){
            float f = times[i];
            System.out.println("GO2 :time to render = " + f +"sec.");
            sum += f;
        }
        float averageNO = sum/NBPASS;
        System.out.println("GO2 : Average = " + averageNO +"sec.");

    }


    private static float testBasicService(MapContext context,Hints hints,String filename){

        Dimension canvasDimension = new Dimension(2400,1200);
        Envelope dataEnvelope = null;
        try {
            dataEnvelope = context.getBounds();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        GeneralEnvelope env = new GeneralEnvelope(dataEnvelope.getCoordinateReferenceSystem());
        double spanX = dataEnvelope.getSpan(0);
        double spanY = dataEnvelope.getSpan(1);
        env.setRange(0, dataEnvelope.getMinimum(0)+ 0.25f*spanX, dataEnvelope.getMaximum(0)-0.25f*spanX);
        env.setRange(1, dataEnvelope.getMinimum(1)+ 0.25f*spanY, dataEnvelope.getMaximum(1)-0.25f*spanY);
        dataEnvelope = env;

        File output = new File(filename);
        String mime = "image/png";

        BufferedImage image = null;
        
        
        // GO GO GO GO GO GO ---------------------------------------------------
        long before = System.nanoTime();
        try {
            image = DefaultPortrayalService.portray(context, dataEnvelope, canvasDimension, false);
        } catch (PortrayalException ex) {
            Logger.getLogger(BenchBigShapes.class.getName()).log(Level.SEVERE, null, ex);
        }
        long after = System.nanoTime();
        // finished ------------------------------------------------------------
        
        
        
        ImageOutputStream stream = null;
        try {
            stream = new FileImageOutputStream(output);
            writeImage(image, mime, stream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BenchBigShapes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BenchBigShapes.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        return (float) ((after-before)/1000000000f );
    }


    public static void main(String[] args) {
        new BenchBigShapes();
    }

    protected static synchronized void writeImage(final BufferedImage image,
            final String mime, Object output) throws IOException {
        if (image == null) {
            throw new NullPointerException("Image can not be null");
        }
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mime);
        while (writers.hasNext()) {
            final ImageWriter writer = writers.next();
            final ImageWriterSpi spi = writer.getOriginatingProvider();
            if (spi.canEncodeImage(image)) {
                ImageOutputStream stream = null;
                if (!isValidType(spi.getOutputTypes(), output)) {
                    stream = ImageIO.createImageOutputStream(output);
                    output = stream;
                }
                writer.setOutput(output);
                writer.write(image);
                writer.dispose();
                if (stream != null) {
                    stream.close();
                }

                return;
            }
        }

        throw new IOException("Unknowed image type");
    }

    /**
     * Check if the provided object is an instance of one of the given classes.
     */
    private static synchronized boolean isValidType(final Class<?>[] validTypes, final Object type) {
        for (final Class<?> t : validTypes) {
            if (t.isInstance(type)) {
                return true;
            }
        }
        return false;
    }

}
