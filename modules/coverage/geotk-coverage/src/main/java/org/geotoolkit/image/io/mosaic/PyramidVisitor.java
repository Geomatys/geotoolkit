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
import java.awt.image.WritableRenderedImage;
import java.io.IOException;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.io.TreeVisitor;
import org.geotoolkit.index.tree.io.TreeVisitorResult;
import org.opengis.geometry.Envelope;

/**
 * Copy all pixels tile(s) found in RTree, in a destination {@code WritableRenderedImage}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class PyramidVisitor implements TreeVisitor {

    /**
     * Area iterate.
     */
    private Rectangle intersection;

    /**
     * Area iterate min x.
     */
    private final int bx;

    /**
     * Area iterate min y.
     */
    private final int by;

    /**
     * Area iterate width.
     */
    private final int bw;

    /**
     * Area iterate height.
     */
    private final int bh;

    /**
     * Area iterate max x.
     */
    private final int bxmax;

    /**
     * Area iterate max y.
     */
    private final int bymax;

    /**
     * Iterator to write in result {@code RenderedImage}.
     */
    PixelIterator destPix;

    /**
     * Result image band number.
     */
    private final int nb;

    /**
     * Copy and write in a result {@code RenderedImage} from RTree tile found.
     *
     * @param image result image within write.
     * @param boundary area iterate write.
     */
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

    /**
     * {@inheritDoc }.
     */
    @Override
    public TreeVisitorResult filter(Node node) {
        return TreeVisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc }.
     *
     * Copy found tile pixels values in result {@code RenderedImage}.
     *
     * @param element found RTree tile.
     * @return {@link TreeVisitorResult}.
     */
    @Override
    public TreeVisitorResult visit(Envelope element) {
        //recup le chemin
        if (!(element instanceof RTreeTile))
            throw new IllegalStateException("element is not pyramidTile type");

        final int eltminx = (int) element.getMinimum(0);
        final int eltminy = (int) element.getMinimum(1);

        RenderedImage img = null;
        try {
            img = ImageIO.read(((RTreeTile)element).getPath());
        } catch (IOException ex) {
            Logger.getLogger(PyramidVisitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (img.getSampleModel().getNumBands() != nb)
            throw new IllegalStateException("tile band number not equals to destination image band number = "+nb);

        intersection.setBounds(bx - eltminx, by - eltminy, bw, bh);

        final PixelIterator pix = PixelIteratorFactory.createRowMajorIterator(img, intersection);

        final int ydeb = max(by, eltminy);
        final int ymax = min(bymax, (int) element.getMaximum(1));
        final int xdeb = max(bx, eltminx);
        final int xmax = xdeb + (min(bxmax,(int) element.getMaximum(0)) - xdeb) * nb;

        for (int y = ydeb ; y < ymax; y++) {
            destPix.moveTo(xdeb, y, 0);
            for (int x = xdeb; x < xmax; x++) {
                pix.next();
                destPix.setSample(pix.getSample());
                destPix.next();
            }
        }
        return TreeVisitorResult.CONTINUE;
    }
}
