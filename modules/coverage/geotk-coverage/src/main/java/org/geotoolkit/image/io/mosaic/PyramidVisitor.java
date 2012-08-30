/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.mosaic;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.io.TreeVisitor;
import org.geotoolkit.index.tree.io.TreeVisitorResult;
import org.opengis.geometry.Envelope;
import static java.lang.Math.*;

/**
 *
 * @author rmarech
 */
public class PyramidVisitor implements TreeVisitor {

    private Rectangle intersection;

    private final int bx;
    private final int by;
    private final int bw;
    private final int bh;
    private final int bxmax;
    private final int bymax;

    PixelIterator destPix;

    private final int nb;

    public PyramidVisitor(WritableRenderedImage image, Rectangle boundary) {
        destPix = PixelIteratorFactory.createRowMajorWriteableIterator(image, image);
        this.nb = image.getSampleModel().getNumBands();
        this.intersection = new Rectangle();
        this.bx    = boundary.x;
        this.by    = boundary.y;
        this.bw    = boundary.width;
        this.bh    = boundary.height;
        this.bxmax = bx + bw;
        this.bymax = by + bh;
    }


    @Override
    public TreeVisitorResult filter(Node node) {
        return TreeVisitorResult.CONTINUE;
    }

    @Override
    public TreeVisitorResult visit(Envelope element) {
        //recup le chemin
        if (!(element instanceof PyramidTile))
            throw new IllegalStateException("element is not pyramidTile type");

        final int eltminx = (int) element.getMinimum(0);
        final int eltminy = (int) element.getMinimum(1);

        RenderedImage img = null;
        try {
            img = ImageIO.read(((PyramidTile)element).getPath());//////
        } catch (IOException ex) {
            Logger.getLogger(PyramidVisitor.class.getName()).log(Level.SEVERE, null, ex);
        }

        intersection.setBounds(bx - eltminx, by - eltminy, bw, bh);

        final PixelIterator pix = PixelIteratorFactory.createRowMajorIterator(img, intersection);

        int ydeb = max(by, eltminy);
        int ymax = min(bymax,(int) element.getMaximum(1));
        int xdeb = max(bx, eltminx);
        int xmax = xdeb + (min(bxmax,(int) element.getMaximum(0)) - xdeb) * nb;

        for (int y = ydeb ; y < ymax; y++) {
            destPix.moveTo(xdeb, y, 0);
            for (int x = xdeb; x < xmax; x++) {//ya une modif genre xmax x nb
                pix.next();
                destPix.setSample(pix.getSample());
                destPix.next();
            }
        }
        return TreeVisitorResult.CONTINUE;
    }

}
