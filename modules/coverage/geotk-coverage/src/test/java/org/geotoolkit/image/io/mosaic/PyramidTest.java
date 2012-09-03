/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.io.mosaic;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.junit.Test;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 * Try catch nothing just about temporary tests.
 *
 * @author rmarech
 */
public class PyramidTest {

    PyramidBuilder pyramid;
    RenderedImage ri;
    double[] coeffX, coeffY;

    final JFrame frm = new JFrame();
    final PanelTest jp = new PanelTest();

    public PyramidTest() throws IOException, NoninvertibleTransformException, TransformException {

        try{
            ri = ImageIO.read(new File("../../../../MF.jpg"));
            File directory = new File("../../../../tuile_pour_Martin");

            FilenameFormatter filename = new FilenameFormatter();
            filename.ensurePrefixSet("tuile");

            coeffX = new double[]{1, 0.5, 0.25};
            coeffY = new double[]{1, 0.5, 0.25};

            pyramid = new PyramidBuilder(ri, coeffX, coeffY, null, "tiff",InterpolationCase.BICUBIC, 2, new double[3]);
            pyramid.setTileDirectory(directory);
            pyramid.createPyramid();

            frm.add(jp);
        }catch(Exception e){

        }

    }

    @Test
    public void etage0Test() throws NoninvertibleTransformException, TransformException, IOException, InterruptedException {

        try{
            int etage = 0;
            long l1 = System.currentTimeMillis();
            RenderedImage image = pyramid.getImage(new Rectangle((int)(ri.getMinX()*coeffX[etage]),(int)(ri.getMinY()*coeffY[etage]),(int) (ri.getWidth()*coeffX[etage]),(int) (ri.getHeight()*coeffY[etage])), etage);
            System.out.println("time = "+(System.currentTimeMillis()-l1)+" ms");

            frm.setTitle("test etage 0 tuile 256x256");
            frm.setSize(image.getWidth(), image.getHeight());
            frm.setLocationRelativeTo(null);
            jp.setImage(image);
            jp.setGraphicValues(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
            frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frm.setVisible(true);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(200, 300, 500, 500), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(700, 50, 500, 300), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(50, 600, 900, 300), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);
            frm.setVisible(false);
        }catch(Exception e){

        }
    }

    @Test
    public void etage1Test() throws NoninvertibleTransformException, TransformException, IOException, InterruptedException {

        try{
            int etage = 1;
            long l1 = System.currentTimeMillis();
            RenderedImage image = pyramid.getImage(new Rectangle((int)(ri.getMinX()*coeffX[etage]),(int)(ri.getMinY()*coeffY[etage]),(int) (ri.getWidth()*coeffX[etage]),(int) (ri.getHeight()*coeffY[etage])), etage);
            System.out.println("time = "+(System.currentTimeMillis()-l1)+" ms");

            frm.setTitle("test etage 1 tuile 256x256");
            frm.setSize(image.getWidth(), image.getHeight());
            frm.setLocationRelativeTo(null);
            jp.setImage(image);
            jp.setGraphicValues(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
            frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frm.setVisible(true);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(100, 150, 250, 250), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(350, 25, 250, 150), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(25, 300, 450, 150), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);
            frm.setVisible(false);
        }catch(Exception e){

        }
    }


    @Test
    public void etage2Test() throws NoninvertibleTransformException, TransformException, IOException, InterruptedException {

        try{
            int etage = 2;
            long l1 = System.currentTimeMillis();
            RenderedImage image = pyramid.getImage(new Rectangle((int)(ri.getMinX()*coeffX[etage]),(int)(ri.getMinY()*coeffY[etage]),(int) (ri.getWidth()*coeffX[etage]),(int) (ri.getHeight()*coeffY[etage])), etage);
            System.out.println("time = "+(System.currentTimeMillis()-l1)+" ms");

            frm.setTitle("test etage 2 tuile 256x256");
            frm.setSize(image.getWidth(), image.getHeight());
            frm.setLocationRelativeTo(null);
            jp.setImage(image);
            jp.setGraphicValues(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
            frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frm.setVisible(true);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(50, 75, 125, 125), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(175, 12, 125, 125), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);

            image = pyramid.getImage(new Rectangle(12, 150, 225, 75), etage);
            jp.setImage(image);
            frm.repaint();
            Thread.sleep(3000);
            frm.setVisible(false);
        }catch(Exception e){

        }
    }
}
