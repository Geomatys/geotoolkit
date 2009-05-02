/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.display.canvas;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotools.display.canvas.J3DPanel;
import org.geotools.display.geom.FeatureGraphicJ3D;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;

/**
 *
 * @author axel
 */
public class test extends JFrame implements ActionListener{

    private J3DPanel j3dp = new J3DPanel();
    private JButton openButton = new JButton();
    private JButton shapeFileView = new JButton();
    private JButton rasterFileView = new JButton();
    private JPanel panneau = new JPanel(new BorderLayout());
    static int WIDTH = 800, HEIGHT = 600;

    public test() {


        openButton.setText("Open File");
        shapeFileView.setText("View Shape File");
        rasterFileView.setText("View Raster File");
        openButton.addActionListener(this);
        shapeFileView.addActionListener(this);
        rasterFileView.addActionListener(this);

        panneau.add(BorderLayout.EAST, shapeFileView);
        panneau.add(BorderLayout.WEST, rasterFileView);
        panneau.add(BorderLayout.NORTH, openButton);

        panneau.add(BorderLayout.CENTER, j3dp);
        setContentPane(panneau);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setVisible(true);

    }


     /**
     *
     * @param e get the Event
     */
    public void actionPerformed(ActionEvent e) {
        FileReader file;


        if (e.getSource().equals(openButton)) {
            try {
                System.out.println("ouvrir");
                JFileChooser chooser = new JFileChooser();
                OpenFileFilter filter = new OpenFileFilter();
                filter.addExtension("shp");
                filter.addExtension("tif");
                filter.setDescription("fichier shape & tif");


                chooser.setFileFilter(filter);
                chooser.setApproveButtonText("Choix du fichier...");
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("Opening - Path:" + chooser.getSelectedFile().getAbsolutePath());
                    URL url = new URL("file:///" + chooser.getSelectedFile().getAbsolutePath());
                    file = new FileReader(url);

                    OpenFileFilter filterFileShape = new OpenFileFilter();
                    OpenFileFilter filterFileRaster = new OpenFileFilter();
                    filterFileShape.addExtension("shp");
                    filterFileRaster.addExtension("tif");

                    if (filterFileShape.accept(new File(url.toURI()))) {
                        System.out.println("Test Open => " + file.shapeReader().toString());
                    } else if (filterFileRaster.accept(new File(url.toURI()))) {
                        //System.out.println("Test Open => " + file.rasterReader().toString());
//                        GridCoverage gridCoverageFile = file.rasterReader();
//                        System.out.println("CRS du Coverage => " + gridCoverageFile.getCoordinateReferenceSystem());
//                        Envelope gridEnvelope = gridCoverageFile.getEnvelope();
//                        double[] LowerCorner = gridEnvelope.getLowerCorner().getCoordinate();
//                        double[] UpperCorner = gridEnvelope.getUpperCorner().getCoordinate();
//
//                        System.out.println("Valeur de l'envelope de coverage LowerCorner =>" + LowerCorner[0]);
//                        System.out.println("Valeur de l'envelope de coverage UpperCorner =>" + UpperCorner[0]);
//
//                        GridCoverage2D coverage2D = (GridCoverage2D) gridCoverageFile;
//                        RenderedImage renderedImage = coverage2D.getRenderedImage();
//
//
//
//                        // coverage2D.show("tyutu");
//                        Raster data = renderedImage.getData();
//                        //DataBuffer buffer = data.getDataBuffer();
//                        //System.out.println(buffer.getSize());
//                        // ou => Copie en moins data.getSampleFloat(X,Y, 0);
//                        double[] samples = data.getSamples(data.getMinX(), data.getMinY(), data.getWidth(), data.getHeight(), 0, (double[]) null);
//                        int SampleModelTranslateX = data.getSampleModelTranslateX();
//                        int SampleModelTranslateY = data.getSampleModelTranslateY();
                       // System.out.println("valeur de Translate X => " + SampleModelTranslateX);
                       // System.out.println("valeur de Translate Y => " + SampleModelTranslateY);

//                        for (int i = 0; i < samples.length; i++) {
//                            double val = samples[i];
//                            System.out.println(val);
//
//                        }
                    }

                }
            } catch (Exception f) {
                JOptionPane.showMessageDialog(null, "Erreur", "WARNING MESSAGE", JOptionPane.WARNING_MESSAGE);
                f.printStackTrace();
            }
        }
       if (e.getSource().equals(shapeFileView)){

            j3dp.getContainer3d().add(new FeatureGraphicJ3D(j3dp.getRenderer()));
            
       }
        if (e.getSource().equals(rasterFileView)){

          
       }
    }



    public static void main(String[] args) {

        new test();

    }
}
