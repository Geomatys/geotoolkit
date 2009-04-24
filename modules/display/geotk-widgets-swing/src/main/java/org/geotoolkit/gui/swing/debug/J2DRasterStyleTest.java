/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.gui.swing.debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.media.jai.KernelJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ConvolveDescriptor;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.processing.Operations;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
//import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotoolkit.util.NumberRange;
import org.opengis.coverage.grid.GridCoverage;

/**
 *
 * @author sorel
 */
public class J2DRasterStyleTest {
//
//    private static final File RASTER_3_BANDES = new File("/home/sorel/GIS_DATA/JEU_VILLE/ortho/1998-0897-1797-83.TIF");
//    private static final File RASTER_1_BANDE = new File("/home/sorel/GIS_DATA/1.tif");
//    private static final Operations RASTER_OPERATIONS = new Operations(null);
//
//
//    public static void main(String[] args){
//
//        testBandSelection_3();
//        testBandSelection_1();
//        testRecolor();
//        testContrastEnchance();
//        testShadedRelied();
//
//    }
//
//    private static void testBandSelection_3(){
//
//        GridCoverage2D coverage = createTiff3Band();
//
//        int[] indices = new int[3];
//        indices[0] = 0;
//        indices[0] = 1;
//        indices[0] = 2;
//
//        GridCoverage2D result = (GridCoverage2D) RASTER_OPERATIONS.selectSampleDimension(coverage, indices);
//
//        showImage( result );
//    }
//
//    private static void testBandSelection_1(){
//
//        GridCoverage2D coverage = createTiff1Band();
//
//        int[] indices = new int[1];
//        indices[0] = 0;
//
//        GridCoverage2D result = (GridCoverage2D) RASTER_OPERATIONS.selectSampleDimension(coverage, indices);
//
//        showImage( result );
//    }
//
//    private static void testShadedRelied(){
//
//        GridCoverage2D coverage = createTiff3Band();
//
//
//        float azimut = (float) Math.toRadians(330f);
//        float elevation = (float) Math.toRadians(15f);
//        float s1 = (float) (Math.sin(azimut) * Math.cos(elevation));
//        float s2 = (float) (Math.cos(azimut) * Math.cos(elevation));
//        System.out.println("si =" + s1 + " s2 = "+ s2);
//
//        int kernelSize = 3;
//        float[] kernelMatrix = new float[kernelSize * kernelSize];
//
//        kernelMatrix[0] = 0f;
//        kernelMatrix[1] = 0f;
//        kernelMatrix[2] = 0f;
//
//        kernelMatrix[3] = 0f;
//        kernelMatrix[4] = (-s1 + s2);
//        kernelMatrix[5] = (s1);
//
//        kernelMatrix[6] = 0f;
//        kernelMatrix[7] = (-s2);
//        kernelMatrix[8] = 0f;
//
//        for (int k = 0; k < kernelMatrix.length; k++) {
//            kernelMatrix[k] = 1.0f / (kernelSize * kernelSize);
//        }
//        KernelJAI kernel = new KernelJAI(kernelSize, kernelSize, kernelMatrix);
//
//        RenderedOp op = ConvolveDescriptor.create(coverage.getRenderedImage(), kernel, null);
//
//        //il faut combiner l'image obtenu avec le coverage d'origine
//
//
//        BufferedImage image = op.getAsBufferedImage();
//
//        showImage(image);
//
////        try {
////            ImageIO.write(op, "png", new File("shadedrelief.png"));
////        } catch (IOException ex) {
////            Logger.getLogger(J2DStyleTest.class.getName()).log(Level.SEVERE, null, ex);
////        }
//
//    }
//
//
//    private static void testRecolor(){
//
//        GridCoverage2D coverage = createTiff3Band();
//
//
//        Collection<org.geotoolkit.coverage.processing.ColorMap> maps = new ArrayList<org.geotoolkit.coverage.processing.ColorMap>();
//
//        org.geotoolkit.coverage.processing.ColorMap map = new org.geotoolkit.coverage.processing.ColorMap();
//        map.setRelativeRange("ext1", NumberRange.create(20,80));
//        map.setColor("ext1", Color.BLUE);
//
//
//        GridCoverage2D result = (GridCoverage2D) RASTER_OPERATIONS.recolor(coverage, maps.toArray( new org.geotoolkit.coverage.processing.ColorMap[0]));
//
//        showImage(result);
//
//    }
//
//    private static void testContrastEnchance(){
//
//        GridCoverage2D coverage = createTiff3Band();
//
//        // ???????????? comment faire ????????????????
//    }
//
//
////    private static GridCoverage2D createTiff1Band(){
////        GridCoverage cover = null;
////        try {
////            GeoTiffReader reader = new GeoTiffReader(RASTER_1_BANDE, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
////            cover = (GridCoverage2D) reader.read(null);
////        } catch (DataSourceException ex) {
////            cover = null;
////            ex.printStackTrace();
////        } catch (IOException ex) {
////            cover = null;
////            ex.printStackTrace();
////        }
////
////        return (GridCoverage2D) cover;
////    }
////
////    private static GridCoverage2D createTiff3Band(){
////        GridCoverage cover = null;
////        try {
////            GeoTiffReader reader = new GeoTiffReader(RASTER_3_BANDES, new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
////            cover = (GridCoverage2D) reader.read(null);
////        } catch (DataSourceException ex) {
////            cover = null;
////            ex.printStackTrace();
////        } catch (IOException ex) {
////            cover = null;
////            ex.printStackTrace();
////        }
////
////        return (GridCoverage2D) cover;
////    }
//
//
//    private static void showImage(final GridCoverage2D coverage){
//
//        JFrame frm = new JFrame();
//
//
//
//        JPanel panel = new JPanel(){
//
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//
//                g2.drawRenderedImage(coverage.getRenderedImage(), new AffineTransform() );
//
//            }
//
//        };
//        panel.setBackground(Color.BLACK);
//        panel.setSize(800,200);
//
//        JScrollPane jsp = new JScrollPane(panel);
//
//        frm.setContentPane(jsp);
//
//        frm.setSize(1200, 400);
//        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frm.setLocationRelativeTo(null);
//        frm.setVisible(true);
//
//    }
//
//    private static void showImage(final Image coverage){
//
//        JFrame frm = new JFrame();
//
//
//
//        JPanel panel = new JPanel(){
//
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//
//
//                g2.drawImage(coverage,null,null );
//
//            }
//
//        };
//        panel.setBackground(Color.BLACK);
//        panel.setSize(800,200);
//
//        JScrollPane jsp = new JScrollPane(panel);
//
//        frm.setContentPane(jsp);
//
//        frm.setSize(1200, 400);
//        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frm.setLocationRelativeTo(null);
//        frm.setVisible(true);
//
//    }


}
