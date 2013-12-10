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
package org.geotoolkit.display3d.scene.light;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Rouby (Geomatys))
 */
public class LightsManager {

    private final static int[] MATERIAL_ORDER = new int[]{
        GL2.GL_AMBIENT,
        GL2.GL_DIFFUSE,
        GL2.GL_SPECULAR,
        GL2.GL_EMISSION,
        GL2.GL_SHININESS,
        GL2.GL_AMBIENT_AND_DIFFUSE,
        GL2.GL_COLOR_INDEXES};

    private final Map<Integer, Light> lights = new HashMap<>();
    private final Map<Integer, Material> materials = new HashMap<>();

    private boolean toDisplay = false;

    public LightsManager() {}

    /**
     * @param lightNum
     * @param position
     * @return the {@link Light} object added, null if lightNum is wrong
     */
    public Light addLight(int lightNum, float[] position) {
        final int lightInd = GL2.GL_LIGHT0+lightNum;
        if (lightInd >= GL2.GL_LIGHT0+GL2.GL_MAX_LIGHTS) {
            return null;
        }

        final Light light = new Light(lightInd);

        lights.put(lightInd, light);

        if(position != null && position.length >= 3) {
            if(position.length == 3) {
                light.setPosition(position, true);
            } else {
                light.setPosition(position, (position[3] == 0.0f));
            }
        }

        return light;
    }

    public Light getLight(int lightNum) {
        final int lightInd = GL2.GL_LIGHT0+lightNum;
        if (lightInd >= GL2.GL_LIGHT0+GL2.GL_MAX_LIGHTS) {
            return null;
        }
        return lights.get(lightInd);
    }

    /**
     * @param face
     * @param type
     * @param color
     * @return
     */
    public Material addMaterial(int face, int type, Color color) {
        return materials.put(type, new Material(face, type, color));
    }

    public boolean isEnable(){
        return toDisplay;
    }

    public void setEnable(boolean state){
        toDisplay = state;
    }

    public boolean isEmpty(){
        return materials.isEmpty() && lights.isEmpty();
    }

    public void updateMaterial(GLAutoDrawable glDrawable) {
        if (!toDisplay) return;

        final GL gl = glDrawable.getGL();
        if (gl instanceof GL2) {
            final GL2 gl2 = (GL2) gl.getGL2();

            for (Integer material : MATERIAL_ORDER) {
                if (materials.containsKey(material)) {
                    materials.get(material).update(glDrawable);
                }
            }
        }
    }

    public void updateLight(GLAutoDrawable glDrawable) {
        if (!toDisplay) return;

        final GL gl = glDrawable.getGL();
        if (gl instanceof GL2) {
            final GL2 gl2 = (GL2) gl.getGL2();

            if (!lights.isEmpty()) {
                final int maxLight = GL2.GL_LIGHT0 + (GL2.GL_MAX_LIGHTS);
                for (int light=GL2.GL_LIGHT0; light<maxLight; light++){
                    if (lights.containsKey(light)){
                        gl2.glEnable(light);
                        lights.get(light).update(glDrawable);
                    }
                }
            }
        }
    }
}
