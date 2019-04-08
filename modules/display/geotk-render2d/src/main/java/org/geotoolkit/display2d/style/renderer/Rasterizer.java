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

    private static final double TOLERANCE = 0.001;

    WritablePixelIterator img;
    double[] pixel;
    int width;
    int height;

    final static class Vertice {
        double x;
        double y;

        public Vertice(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Vertice() {
        }

        @Override
        public String toString() {
            return x+" "+y;
        }

    }

    void drawTriangle(Vertice v1, Vertice v2, Vertice v3) {
        int minx = (int) Math.min(v1.x, Math.min(v2.x, v3.x)) -1;
        int maxx = (int) (Math.max(v1.x, Math.max(v2.x, v3.x))) +1;
        int miny = (int) Math.min(v1.y, Math.min(v2.y, v3.y)) -1;
        int maxy = (int) (Math.max(v1.y, Math.max(v2.y, v3.y))) +1;
        minx = clamp(minx, 0, width);
        maxx = clamp(maxx, 0, width);
        miny = clamp(miny, 0, height);
        maxy = clamp(maxy, 0, height);

        Vertice p = new Vertice();
        for (int x=minx;x<maxx;x++) {
            for (int y=miny;y<maxy;y++) {
                p.x = x;
                p.y = y;
                if (inTriangle2D(v1, v2, v3, p)) {
                    img.moveTo(x, y);
                    img.setPixel(pixel);
                }
            }
        }

    }

    void drawTriangle2(Vertice v1, Vertice v2, Vertice v3) {
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

    private static double clamp(double v, double s, double e) {
        if (s < e) {
            return Math.max(Math.min(e, v), s);
        } else {
            return Math.max(Math.min(s, v), e);
        }
    }

    private static int clamp(int v, int s, int e) {
        return Math.max(Math.min(e, v), s);
    }

    private static boolean inTriangle2D(Vertice a, Vertice b, Vertice c, Vertice p){
        final double[] bary = getBarycentricValue2D(a, b, c, p);
        return bary[1] >= 0.0 && bary[2] >= 0.0 && (bary[1] + bary[2]) <= (1.0+TOLERANCE);
    }

    private static double[] getBarycentricValue2D(Vertice a, Vertice b, Vertice c, Vertice p){
        final Vertice v0 = new Vertice(b.x-a.x, b.y-a.y);
        final Vertice v1 = new Vertice(c.x-a.x, c.y-a.y);
        final Vertice v2 = new Vertice(p.x-a.x, p.y-a.y);
        final double d00 = dot2D(v0,v0);
        final double d01 = dot2D(v0,v1);
        final double d11 = dot2D(v1,v1);
        final double d20 = dot2D(v2,v0);
        final double d21 = dot2D(v2,v1);
        final double denom = d00 * d11 - d01 * d01;
        final double v = (d11 * d20 - d01 * d21) / denom;
        final double w = (d00 * d21 - d01 * d20) / denom;
        final double u = 1.0f - v - w;
        return new double[]{u, v, w};
    }

    private static double dot2D(final Vertice v, final Vertice o){
        return v.x * o.x + v.y * o.y;
    }
}
