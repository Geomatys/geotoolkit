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
package org.geotoolkit.display3d.scene.component;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.TextureData;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.utils.TextureManager;

/**
 * @author Thomas Rouby (Geomatys)
 */
public abstract class TexturedObject3D extends Object3D {

    public final static int MAX_TEXTURE = 10;

    protected float minU = 0.0f, minV = 0.0f, maxU = 1.0f, maxV = 1.0f;
    protected FloatBuffer uvsb;
    protected FloatBuffer normalb = null;

    protected final TextureManager[] futurText = new TextureManager[TexturedObject3D.MAX_TEXTURE];
    protected final TextureManager[] textures = new TextureManager[TexturedObject3D.MAX_TEXTURE];

    protected final List<TextureManager> oldText = new ArrayList<>();

    protected TexturedObject3D(Map3D map){
        super(map);
    }

    public void setUVsRange(float minU, float minV, float maxU, float maxV){
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
    }

    public float[] getUVsRange() {
        return new float[]{minU, minV, maxU, maxV};
    }

    public void setUVs(float[] uvs){
        this.uvsb = Buffers.newDirectFloatBuffer(uvs);
    }

    public void setNormals(float[] normals){
        this.normalb = Buffers.newDirectFloatBuffer(normals);
    }

    public FloatBuffer getUVs(){
        return this.uvsb;
    }

    public float[] getUVsAsArray(){
        if (this.uvsb.hasArray()){
            return this.uvsb.array();
        } else {
            final float[] uvs = new float[this.uvsb.limit()];
            this.uvsb.rewind();
            this.uvsb.get(uvs);
            return uvs;
        }
    }

    public void setTexture(int index, TextureManager texture) {
        if( index >= MAX_TEXTURE && index < 0) return;
        if (textures[index] != null) {
            textures[index].removeUser(this);
            oldText.add(textures[index]);
        }
        texture.addUser(this);
        textures[index] = texture;
    }

    public void setTextureData(int index, TextureData texture){
        if( index >= MAX_TEXTURE && index < 0) return;
        TextureManager textM = new TextureManager(index, texture);
        textM.addUser(this);
        futurText[index] = textM;
    }

    public final TextureManager getTexture(int index){
        if (index < 0 || index >= this.textures.length) return null;
        return this.textures[index];
    }

    public final TextureManager getFuturText(int index){
        if (index < 0 || index >= this.futurText.length) return null;
        return this.futurText[index];
    }

    public void checkTexture(GLAutoDrawable glAutoDrawable, int index){
        final Map3D canvas = this.getCanvas();
        if (index < 0 || index >= this.textures.length) return;

        if (canvas.doAction()) {
            if (this.futurText[index] != null) {
                if (this.futurText[index].updateTexture(glAutoDrawable)) {
                    setTexture(index, this.futurText[index]);
                    this.futurText[index] = null;
                    canvas.addAction();
                }
            } else if (this.textures[index] != null) {
                if (this.textures[index].updateTexture(glAutoDrawable)) {
                    canvas.addAction();
                }
            }
        }
    }

    public static int getTextureAddress(int index){
        if (index >= TexturedObject3D.MAX_TEXTURE) return -1;
        //GL texture id constants are incremental.
        return GL2.GL_TEXTURE0 + index;
    }

    @Override
    public void dispose(GLAutoDrawable glDrawable) {
        super.dispose(glDrawable);
        for(int i=0; i<MAX_TEXTURE; i++){
            TextureManager texture = textures[i];
            TextureManager futur = futurText[i];
            if(texture != null){
                texture.removeUser(this);
                if (!texture.isUsed()) {
                    texture.destroy(glDrawable.getGL());
                }
            }
            if(futur != null){
                futur.removeUser(this);
                if (!futur.isUsed()) {
                    futur.destroy(glDrawable.getGL());
                }
            }
        }
        for (TextureManager old : oldText) {
            if (!old.isUsed()){
                old.destroy(glDrawable.getGL());
            }
        }
        oldText.clear();
    }

    @Override
    public void dispose() {
        super.dispose();
        for(int i=0; i<MAX_TEXTURE; i++){
            TextureManager texture = textures[i];
            TextureManager futur = futurText[i];
            if (texture != null) {
                texture.removeUser(this);
            }
            if (futur != null) {
                futur.removeUser(this);
            }
        }
    }
}
