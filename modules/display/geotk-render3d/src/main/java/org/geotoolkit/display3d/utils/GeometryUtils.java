/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display3d.utils;

import javax.media.opengl.GL2;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

/**
 * @author Thomas Rouby (Geomatys)
 */
public final class GeometryUtils {

    private GeometryUtils(){}

    public static void drawAxisHelper(GL2 gl){
        drawAxisHelper(gl, 1.0f);
    }

    public static void drawAxisHelper(GL2 gl, float length){
        gl.glBegin(GL2.GL_LINES);
            gl.glColor3f(length,0.0f,0.0f);
            gl.glVertex3f(0.0f,0.0f,0.0f);
            gl.glVertex3f(1.0f,0.0f,0.0f);

            gl.glColor3f(0.0f,length,0.0f);
            gl.glVertex3f(0.0f,0.0f,0.0f);
            gl.glVertex3f(0.0f,1.0f,0.0f);

            gl.glColor3f(0.0f,0.0f,length);
            gl.glVertex3f(0.0f,0.0f,0.0f);
            gl.glVertex3f(0.0f,0.0f,1.0f);
        gl.glEnd();
    }

    public static float[] getPixels(int x, int y, int w, int h, int numBandsIn, int numBandsOut, Raster data) {

        if (numBandsIn < numBandsOut){
            numBandsOut = numBandsIn;
        }

        int Offset = 0;
        int x1 = x + w;
        int y1 = y + h;

        float pixels[] = new float[numBandsOut * w * h];

        for (int i=y; i<y1; i++) {
            for(int j=x; j<x1; j++) {
                for(int k=0; k<numBandsIn; k++) {
                    if (k < numBandsOut){
                        pixels[Offset++] = (float)data.getSample(j, i, k)/255.0f;
                    }
                }
            }
        }

        return pixels;
    }

    public static float[] getPixels (int x, int y, int w, int h, ColorModel cm, Raster data){

        int Offset = 0;
        int x1 = x + w;
        int y1 = y + h;

        float pixels[] = new float[3 * w * h];

        for (int i=y; i<y1; i++) {
            for(int j=x; j<x1; j++) {
                int pixel = data.getSample(j, i, 0);

                pixels[Offset++] = (float)cm.getRed( pixel )/255.0f;
                pixels[Offset++] = (float)cm.getGreen(pixel)/255.0f;
                pixels[Offset++] = (float)cm.getBlue(pixel)/255.0f;
            }
        }

        return pixels;
    }

}
