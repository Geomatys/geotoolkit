
package org.geotoolkit.gui.swing.debug;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotoolkit.gui.swing.crschooser.JCRSChooser;

import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display2d.canvas.GO2Hints;
import org.geotoolkit.display.canvas.control.NeverFailMonitor;
import org.geotoolkit.display2d.canvas.web.WebLayeredCanvas2D;
import org.geotoolkit.display2d.canvas.web.WebLayeredCanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.container.DefaultContextContainer2D;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.factory.Hints;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author sorel
 */
public class PortrayalServiceTest {
        
    private static MapContext context = ContextBuilder.buildRealCityContext();
    
    public static void main(String[] args){

//        testReadThreadSeparation();

//       testBasic();
//        for(int i=0;i<10;i++){
            testWebLayeredCanvas(0);
//        }
//        testWMSLayerCRS();
//        testGeneralisation();
//        testMultithread();

//        testPostGridService();
    }

    private static void testBasic(){
        
        
        Dimension canvasDimension = new Dimension(1200,800);
        
        Rectangle2D rect = new Rectangle2D.Double(-180d, 90, 360d, -180d);
        CoordinateReferenceSystem crs = null;
        
//        try {
//           crs = CRS.decode("EPSG:4326");
//        } catch (NoSuchAuthorityCodeException ex) {
//            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (FactoryException ex) {
//            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
        crs = DefaultGeographicCRS.WGS84;
        
        ReferencedEnvelope dataEnvelope = null;
        dataEnvelope = new ReferencedEnvelope(rect, crs);
        
//        try {
//            dataEnvelope = context.getBounds();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        
        File output = new File("WMSLayerCRS.png");
        String mime = "image/png";

        ImageOutputStream stream = null;
        try {
            stream = new FileImageOutputStream(output);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            DefaultPortrayalService.portray(context, dataEnvelope,null, stream, mime, canvasDimension, null,false);
        } catch (PortrayalException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    private static void testWebLayeredCanvas(int nu){
        WebLayeredCanvas2D canvas = new WebLayeredCanvas2D(DefaultGeographicCRS.WGS84,new Dimension(800, 600));
        ContextContainer2D renderer = new DefaultContextContainer2D(canvas, false);
        canvas.setContainer(renderer);
                
        canvas.setMonitor(new webMonitor(nu));
        
        try{
            renderer.setContext(context);
            canvas.getController().setVisibleArea(context.getBounds());

            canvas.repaint();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    private static void testWMSLayerCRS() {

        MapContext context = ContextBuilder.buildBigRoadContext();

        Dimension canvasDimension = new Dimension(2400,1200);
        ReferencedEnvelope dataEnvelope = null;
        try {
            dataEnvelope = context.getBounds();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        double n = 20037507.067161847;
        Rectangle2D rect = new Rectangle2D.Double(-n, n, 2*n, -2*n);
        Envelope env = null;
        try {
            env = new Envelope2D(CRS.decode("EPSG:3395"), rect);
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//
//        System.out.println("dataEnveloppe =" + dataEnvelope);
//        System.out.println("CRS data =" + dataEnvelope.getCoordinateReferenceSystem());
//
        JCRSChooser chooser = new JCRSChooser(null, true);
        chooser.showDialog();
        CoordinateReferenceSystem crs = chooser.getCRS();
//
//        System.out.println("new CRS ="+ crs);
        
//        try {
////            MathTransform trs = CRS.findMathTransform(dataEnvelope.getCoordinateReferenceSystem(), crs);
////            env = CRS.transform(trs, env);
//            env = CRS.transform(dataEnvelope, crs);
//        } catch (TransformException ex) {
//            ex.printStackTrace();
//            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
//        } 
////        catch (FactoryException ex) {
////            ex.printStackTrace();
////            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
////        }

        System.out.println("Enveloppe =" + env);
        
        dataEnvelope = new ReferencedEnvelope(env);

        System.out.println("dataEnveloppe 2 =" + dataEnvelope);
        
        File output = new File("WMSLayerCRS");
        String mime = "image/png";

        ImageOutputStream stream = null;
        try {
            stream = new FileImageOutputStream(output);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            DefaultPortrayalService.portray(context, dataEnvelope, null,stream, mime, canvasDimension, null,false);
        } catch (PortrayalException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private static void testReadThreadSeparation(){

        MapContext context = ContextBuilder.buildBigRoadContext();
        Map<Key,Object> maps = new HashMap();
        Hints hints = new Hints(maps);
        int NBPASS = 3;
        float[] times = new float[2*NBPASS];



        long before = System.nanoTime();
        long nb = 0;

        for(MapLayer layer : context.layers()){
            
            if(layer instanceof FeatureMapLayer){
                FeatureMapLayer featureLayer = (FeatureMapLayer) layer;
                FeatureCollection<? extends FeatureType,? extends Feature> fc = null;
                try {
                    fc = featureLayer.getFeatureSource().getFeatures();
                } catch (IOException ex) {
                    Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                FeatureIterator ite = fc.features();


                while(ite.hasNext()){
                    nb++;
                    Feature obj = ite.next();
                    Geometry geom = (Geometry) obj.getDefaultGeometryProperty().getValue();
                }
                ite.close();
            }
        }
        long after = System.nanoTime();

        System.out.println("Reference time (go through all features "+nb+") : " + ((after-before)/1000000000f ) );


        //test without generalisation
        maps.put(GO2Hints.KEY_GENERALIZE,GO2Hints.GENERALIZE_OFF);
        hints = new Hints(maps);
        for(int i=0; i<NBPASS;i++){
            System.out.println("pass "+i);
            times[i] = testBasicService(context,hints,"imageNOGEN.png");
        }

        maps = new HashMap();
        maps.put(GO2Hints.KEY_GENERALIZE,GO2Hints.GENERALIZE_ON);
        hints = new Hints(maps);
        //test with generalisation
        for(int i=0; i<NBPASS;i++){
            System.out.println("pass "+i);
            times[NBPASS+i] = testBasicService(context,hints,"imageGEN.png");
        }


        //summary---------------------------------------------------------------

        float sum = 0;
        for(int i=0;i<NBPASS;i++){
            float f = times[i];
            System.out.println("NOGEN : time to render = " + f +"sec.");
            sum += f;
        }
        float averageNO = sum/NBPASS;
        System.out.println("NOGEN : Average = " + averageNO +"sec.");

        sum = 0;
        for(int i=0;i<NBPASS;i++){
            float f = times[NBPASS+i];
            System.out.println("GEN : time to render = " + f +"sec.");
            sum += f;
        }
        float averageO = sum/NBPASS;
        System.out.println("GEN : Average = " + averageO +"sec.");
        System.out.println("Optimisation benefit = " + ((averageNO-averageO)*100)/averageNO  +"%");

    }

    private static void testGeneralisation(){

        MapContext context = ContextBuilder.buildBigRoadContext();
        Map<Key,Object> maps = new HashMap();
        Hints hints = new Hints(maps);
        int NBPASS = 2;
        float[] times = new float[2*NBPASS];



        long before = System.nanoTime();
        long nb = 0;

        for(MapLayer layer : context.layers()){
            if(layer instanceof FeatureMapLayer){
                FeatureMapLayer featureLayer = (FeatureMapLayer) layer;
                FeatureCollection<? extends FeatureType,? extends Feature> fc = null;
                try {
                    fc = featureLayer.getFeatureSource().getFeatures();
                } catch (IOException ex) {
                    Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                Iterator<? extends Feature> ite = fc.iterator();


                while(ite.hasNext()){
                    nb++;
                    Feature obj = ite.next();
                    Geometry geom = (Geometry) obj.getDefaultGeometryProperty().getValue();
                }
            }
            
        }
        long after = System.nanoTime();

        System.out.println("Reference time (go through all features "+nb+") : " + ((after-before)/1000000000f ) );


        //test without generalisation
        maps.put(GO2Hints.KEY_GENERALIZE,GO2Hints.GENERALIZE_OFF);
        hints = new Hints(maps);
        for(int i=0; i<NBPASS;i++){
            System.out.println("pass "+i);
            times[i] = testBasicService(context,hints,"imageNOGEN.png");
        }

        maps = new HashMap();
        maps.put(GO2Hints.KEY_GENERALIZE,GO2Hints.GENERALIZE_ON);
        hints = new Hints(maps);
        //test with generalisation
        for(int i=0; i<NBPASS;i++){
            System.out.println("pass "+i);
            times[NBPASS+i] = testBasicService(context,hints,"imageGEN.png");
        }


        //summary---------------------------------------------------------------

        float sum = 0;
        for(int i=0;i<NBPASS;i++){
            float f = times[i];
            System.out.println("NOGEN : time to render = " + f +"sec.");
            sum += f;
        }
        float averageNO = sum/NBPASS;
        System.out.println("NOGEN : Average = " + averageNO +"sec.");

        sum = 0;
        for(int i=0;i<NBPASS;i++){
            float f = times[NBPASS+i];
            System.out.println("GEN : time to render = " + f +"sec.");
            sum += f;
        }
        float averageO = sum/NBPASS;
        System.out.println("GEN : Average = " + averageO +"sec.");
        System.out.println("Optimisation benefit = " + ((averageNO-averageO)*100)/averageNO  +"%");

    }

    private static void testMultithread(){

        MapContext context = ContextBuilder.buildMixedContext();
        Map<Key,Object> maps = new HashMap();
        Hints hints = new Hints(maps);
        int NBPASS = 3;
        float[] times = new float[2*NBPASS];


        //test without multithread
        maps.put(GO2Hints.KEY_MULTI_THREAD,false);
        hints = new Hints(maps);
        for(int i=0; i<NBPASS;i++){
            times[i] = testBasicService(context,hints,"imageMONO.png");
        }

        maps = new HashMap();
        hints = new Hints(maps);
        //test with multithread
        for(int i=0; i<NBPASS;i++){
            times[NBPASS+i] = testBasicService(context,hints,"imageMULTI.png");
        }


        //summary---------------------------------------------------------------

        float sum = 0;
        for(int i=0;i<NBPASS;i++){
            float f = times[i];
            System.out.println("MONO : time to render = " + f +"sec.");
            sum += f;
        }
        float averageNO = sum/NBPASS;
        System.out.println("MONO : Average = " + averageNO +"sec.");

        sum = 0;
        for(int i=0;i<NBPASS;i++){
            float f = times[NBPASS+i];
            System.out.println("MULTI : time to render = " + f +"sec.");
            sum += f;
        }
        float averageO = sum/NBPASS;
        System.out.println("MULTI : Average = " + averageO +"sec.");
        System.out.println("Optimisation benefit = " + ((averageNO-averageO)*100)/averageNO  +"%");


    }

    private static float testBasicService(MapContext context,Hints hints,String filename){

        Dimension canvasDimension = new Dimension(2400,1200);
        ReferencedEnvelope dataEnvelope = null;
        try {
            dataEnvelope = context.getBounds();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        File output = new File(filename);
        String mime = "image/png";

        long before = System.nanoTime();

        ImageOutputStream stream = null;
        try {
            stream = new FileImageOutputStream(output);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            DefaultPortrayalService.portray(context, dataEnvelope, null,stream, mime, canvasDimension, hints,false);
        } catch (PortrayalException ex) {
            Logger.getLogger(PortrayalServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         

        long after = System.nanoTime();
        
        return (float) ((after-before)/1000000000f );
    }

//    private static void testPostGridService(){
//        try {
//            GridCoverage2D coverage = (GridCoverage2D) ContextBuilder.createPostGridLayer();
//
//            System.out.println(coverage);
//
//            BufferedImage image = service.portray(coverage, coverage.getEnvelope2D(), new Dimension(800, 800));
//
//
//            Collection<Image> images = new ArrayList<Image>();
//            images .add(image);
//            showImage(images);
//
//            System.out.println(image);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } catch (CatalogException ex) {
//            ex.printStackTrace();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//
//    }


    public static void showImage(final Image image){
        showImage(Collections.singleton(image));
    }

    private static void showImage(final Collection<Image> imgs){

        JFrame frm = new JFrame();



        JPanel panel = new JPanel(){

            int X = 0;
            int Y = 0;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                X=10;
                Y=10;

                for(Image image : imgs){
                    if(image != null){
                        g2.setColor(Color.RED);
                        g2.fillRect(X, Y, image.getWidth(this), image.getHeight(this));
                        g2.drawImage(image, X,Y, this);
                        X += image.getWidth(this) +5;
                    }
                }


            }

        };
        panel.setBackground(Color.BLACK);
        panel.setSize(800,200);

        JScrollPane jsp = new JScrollPane(panel);

        frm.setContentPane(jsp);

        frm.setSize(1200, 400);
        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);

    }
    
    static class webMonitor extends NeverFailMonitor implements WebLayeredCanvasMonitor{

        int numero;
        
        webMonitor(int nu){
            this.numero = nu;
        }
        
        @Override
        public void imageCreated(RenderedImage buffer, double zIndex){
            System.out.println("Generated > " + zIndex);
            try {
                ImageIO.write(buffer, "png", new File(numero +"_"+ zIndex + ".png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    
}
