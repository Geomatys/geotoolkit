/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.gui.swing.debug;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.geotools.geometry.jts.ReferencedEnvelope;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display.canvas.control.FailOnErrorMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.GO2Hints;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.map.MapContext;

/**
 *
 * @author sorel
 */
public class BenchProfiler {

    public static void main(String[] args) throws IOException {
        MapContext context = ContextBuilder.buildBigRoadContext();

//        ReferencedEnvelope env = new ReferencedEnvelope(-180,180,-90,90,DefaultGeographicCRS.WGS84);
//        ReferencedEnvelope env = new ReferencedEnvelope(-60,180,-30,90,DefaultGeographicCRS.WGS84);
        ReferencedEnvelope env = context.getBounds();

        Hints hints = new Hints();
//        hints.put(GO2Hints.KEY_GENERALIZE, GO2Hints.GENERALIZE_ON);

        final long before = System.currentTimeMillis();

        for(int i=0;i<3;i++){
            System.out.println("pass "+ i);
            try {
                BufferedImage img = DefaultPortrayalService.portray(
                        context,
                        env,
                        null,
                        null,
                        new Dimension(2000, 1000),
                        true,
                        0,
                        new FailOnErrorMonitor(),
                        null,
                        hints);
//                show(img);
            } catch (PortrayalException ex) {
                Logger.getLogger(BenchProfiler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        final long after = System.currentTimeMillis();

        System.out.println(after-before);
    }

    private static void show(Image image){
        JFrame frm = new JFrame();
        frm.setContentPane(new JScrollPane(new JLabel(new ImageIcon(image))));

        frm.setSize(800, 600);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }

}
