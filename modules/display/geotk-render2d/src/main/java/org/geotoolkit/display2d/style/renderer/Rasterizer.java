/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import org.apache.sis.image.WritablePixelIterator;

/**
 * Simple rasterizer based on :
 * http://www.sunshine2k.de/coding/java/TriangleRasterization/TriangleRasterization.html#algo2
 *
 * @author Johann Sorel (Geomatys)
 */
final class Rasterizer {
    WritablePixelIterator img;
    double[] pixel;
    int width;
    int height;

    final static class Vertice {
        double x;
        double y;
    }

    void drawTriangle(Vertice v1, Vertice v2, Vertice v3) {
        /* at first sort the three vertices by y-coordinate ascending so v1 is the topmost vertice */
        if (v3.y < v2.y) {
            Vertice v = v3;
            v3 = v2;
            v2 = v;
        }
        if (v2.y < v1.y) {
            Vertice v = v1;
            v1 = v2;
            v2 = v;
        }
        if (v3.y < v2.y) {
            Vertice v = v3;
            v3 = v2;
            v2 = v;
        }

        /* here we know that v1.y <= v2.y <= v3.y */
        if (v2.y == v3.y) { /* check for trivial case of bottom-flat triangle */
            fillBottomFlatTriangle(v1, v2, v3);
        } else if (v1.y == v2.y) { /* check for trivial case of top-flat triangle */
            fillTopFlatTriangle(v1, v2, v3);
        } else {
            /* general case - split the triangle in a topflat and bottom-flat one */
            Vertice v4 = new Vertice();
            v4.x = (v1.x + ((v2.y - v1.y) / (v3.y - v1.y)) * (v3.x - v1.x));
            v4.y = v2.y;
            fillBottomFlatTriangle(v1, v2, v4);
            fillTopFlatTriangle(v2, v4, v3);
        }
    }

    void fillBottomFlatTriangle(Vertice v1, Vertice v2, Vertice v3) {
        double invslope1 = (v2.x - v1.x) / (v2.y - v1.y);
        double invslope2 = (v3.x - v1.x) / (v3.y - v1.y);

        double curx1 = v1.x;
        double curx2 = v1.x;

        final int sy = Math.max( (int) v1.y, 0);
        final double ey = Math.min(v2.y, height-1);

        for (int scanlineY = sy; scanlineY <= ey; scanlineY++) {
            drawLine(curx1, curx2, scanlineY);
            curx1 += invslope1;
            curx2 += invslope2;
        }
    }

    void fillTopFlatTriangle(Vertice v1, Vertice v2, Vertice v3) {
        double invslope1 = (v3.x - v1.x) / (v3.y - v1.y);
        double invslope2 = (v3.x - v2.x) / (v3.y - v2.y);

        double curx1 = v3.x;
        double curx2 = v3.x;

        final int sy = Math.min( (int) v3.y, height-1);
        final double ey = Math.max(v1.y, 0);

        for (int scanlineY = sy; scanlineY > ey; scanlineY--) {
            drawLine(curx1, curx2, scanlineY);
            curx1 -= invslope1;
            curx2 -= invslope2;
        }
    }

    void drawLine(double x1, double x2, int y) {
        if (x1 > x2) {
            double x = x1;
            x1 = x2;
            x2 = x;
        }
        int ix1 = (int) Math.max(x1-0.5, 0);
        int ix2 = (int) Math.min(x2+0.5, width);
        for (int x=ix1;x<ix2;x++) {
            img.moveTo(x, y);
            img.setPixel(pixel);
        }
    }


}
