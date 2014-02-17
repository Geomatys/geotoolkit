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
import com.jogamp.opengl.util.texture.TextureData;
import javax.media.opengl.GL;

import org.geotoolkit.math.XMath;
import org.opengis.geometry.Envelope;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;
import java.awt.*;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.utils.TextureManager;

/**
 * @author Thomas Rouby (Geomatys)
 */
public class Tile3D extends Mesh3D {

    public static final int INDEX_MNT = 9;
    public static final int INDEX_IMAGE = 0;

    private final Envelope envelope;
    private final Point3i position;
    private final int axis0Number, axis1Number;
    private final int axis0Pts, axis1Pts;

    public static final float borderZTranslate = -2000.0f;

    public Tile3D(Map3D map, Envelope env, Point3i position, int axis0, int axis1){
        super(map);

        this.envelope = env;
        this.position = position;

        this.axis0Number = axis0;
        this.axis1Number = axis1;

        this.axis0Pts = axis0+2;
        this.axis1Pts = axis1+2;

        final float[]   vertices = new float[(axis0Pts*axis1Pts)*3];
        final float[]   normals = new float[(axis0Pts*axis1Pts)*3];
        final int[]     indices  = new int[((axis0Pts-1)*(axis1Pts-1)) * 3 * 2];
        final float[]   uvs      = new float[(axis0Pts*axis1Pts)*2];

        final double axis0Step = envelope.getSpan(0) / (axis0Number-1);
        final double axis1Step = envelope.getSpan(1) / (axis1Number-1);

        for (int j=0; j<axis1Pts; j++){
            for (int i=0; i<axis0Pts; i++){
                final int col = XMath.clamp(i-1, 0, axis0Number-1);
                final int row = XMath.clamp(j-1, 0, axis1Number-1);

                final int coord = i + j * axis0Pts;

                vertices[coord *3    ] = (float)(envelope.getMinimum(0) + col*axis0Step);
                vertices[coord *3 + 1] = (float)(envelope.getMaximum(1) - row*axis1Step);
                vertices[coord *3 + 2] = (inBorder(coord))?(borderZTranslate):(0.0f);

                normals[coord *3    ] = 0.0f;
                normals[coord *3 + 1] = 0.0f;
                normals[coord *3 + 2] = 1.0f;

                uvs[coord *2    ] = (float)col/((float)axis0Number-1.0f);
                uvs[coord *2 + 1] = (float)row/((float)axis1Number-1.0f);

                if (j < axis1Pts-1 && i < axis0Pts-1){
                    final int coordInd = i + j * (axis0Pts - 1);

                    indices[coordInd *6    ] = coord;
                    indices[coordInd *6 + 1] = i+j*axis0Pts + axis0Pts + 1;
                    indices[coordInd *6 + 2] = i+j*axis0Pts + 1;
                    indices[coordInd *6 + 3] = coord;
                    indices[coordInd *6 + 4] = i+j*axis0Pts + axis0Pts;
                    indices[coordInd *6 + 5] = i+j*axis0Pts + axis0Pts + 1;
                }
            }
        }

        this.verticesb = Buffers.newDirectFloatBuffer(vertices);
        this.normalb = Buffers.newDirectFloatBuffer(normals);
        this.indicesb = Buffers.newDirectIntBuffer(indices);
        this.uvsb = Buffers.newDirectFloatBuffer(uvs);
    }

    public double getScale() {
        TextureManager textImg = getTexture(INDEX_IMAGE);
        if (textImg != null){
            return this.envelope.getSpan(0) / textImg.getTextureData().getWidth();
        } else {
            return this.envelope.getSpan(0) / 256.0;
        }
    }

    public final Point3i getPosition() {
        return this.position;
    }

    public final Envelope getEnvelope() {
        return this.envelope;
    }

    public void setMNT(float[] vertices) {
        setVertices(vertices);

        computeNormals();
    }

    public final TextureManager getTextureImg() {
        return this.getTexture(INDEX_IMAGE);
    }

    public void setTextureImg(TextureManager textureImg) {
        this.setTexture(INDEX_IMAGE, textureImg);
    }

    public void setTextureDataImg(TextureData textureImg) {
        this.setTextureData(INDEX_IMAGE, textureImg);
    }

    @Override
    public void checkTexture(GLAutoDrawable glAutoDrawable, int index){
        final Map3D canvas = this.getCanvas();
        if (index < 0 || index >= this.textures.length) return;

        if (canvas.doAction()) {
            if (this.futurText[index] != null) {
                if (this.futurText[index].updateTexture(glAutoDrawable)) {
                    computeUVs();
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

    public void computeUVs() {
        final float[]   uvs      = new float[(axis0Pts*axis1Pts)*2];

        for (int j=0; j<axis1Pts; j++){
            for (int i=0; i<axis0Pts; i++){
                int col = XMath.clamp(i - 1, 0, axis0Number - 1);
                int row = XMath.clamp(j-1, 0, axis1Number-1);

                uvs[(i+j*axis0Pts)*2    ] = (float)col/((float)axis0Number-1.0f);
                uvs[(i+j*axis0Pts)*2 + 1] = (float)row/((float)axis1Number-1.0f);
            }
        }

        this.uvsb = Buffers.newDirectFloatBuffer(uvs);
        minU = minV = 0.0f;
        maxU = maxV = 1.0f;
    }

    private boolean inBorder(int ind){
        final int mod = ind%axis0Pts;
        return ind<axis0Pts || ind >= axis0Pts*(axis1Pts-1) || mod <= 0 || mod >= axis0Pts-1;
    }

    public void computeNormals() {
        final Vector3f[] nls = new Vector3f[axis0Pts*axis1Pts];
        final int[] indices = getIndicesAsArray();
        final float[] vertices = getVerticesAsArray();

        for (int i=0; i<indices.length; i+=3) {

            if (inBorder(indices[i]) || inBorder(indices[i+1]) || inBorder(indices[i+2])){
                continue;
            }

            int p1ind = indices[i]*3;
            final Vector3f p1 = new Vector3f(vertices[p1ind], vertices[p1ind+1],vertices[p1ind+2]);

            int p2ind = indices[i+1]*3;
            final Vector3f p2 = new Vector3f(vertices[p2ind], vertices[p2ind+1],vertices[p2ind+2]);

            int p3ind = indices[i+2]*3;
            final Vector3f p3 = new Vector3f(vertices[p3ind], vertices[p3ind+1],vertices[p3ind+2]);

            final Vector3f v1 = new Vector3f();
            v1.sub(p2, p1);

            final Vector3f v2 = new Vector3f();
            v2.sub(p3, p1);

            final Vector3f normal = new Vector3f();
            normal.cross(v1, v2);

            if (nls[indices[i  ]] == null){
                nls[indices[i  ]] = new Vector3f(normal);
            } else {
                nls[indices[i  ]].add(normal);
            }
            if (nls[indices[i+1]] == null){
                nls[indices[i+1]] = new Vector3f(normal);
            } else {
                nls[indices[i+1]].add(normal);
            }
            if (nls[indices[i+2]] == null){
                nls[indices[i+2]] = new Vector3f(normal);
            } else {
                nls[indices[i+2]].add(normal);
            }
        }

        final float[] nlsa = new float[nls.length*3];
        for (int i=0; i<nls.length; i++){
            if (nls[i] == null){
                nls[i] = new Vector3f(0.0f, 0.0f, 1.0f);
            } else {
                nls[i].normalize();
            }
            nlsa[i*3  ] = nls[i].x;
            nlsa[i*3+1] = nls[i].y;
            nlsa[i*3+2] = nls[i].z;
        }

        this.normalb = Buffers.newDirectFloatBuffer(nlsa);
    }

    public Dimension getPtsNumber(){
        return new Dimension(this.axis0Pts, this.axis1Pts);
    }

    public Dimension getAxisNumber(){
        return new Dimension(axis0Number, axis1Number);
    }

    public double getZValue(double axis0, double axis1){
        final double axis0Pos = axis0Number*XMath.clamp(axis0, 0.0, 1.0) + 1.0;
        final double axis1Pos = axis1Number*XMath.clamp(axis1, 0.0, 1.0) + 1.0;

        /**
         *   C -- D
         *   |    |
         *   A -- B
         */

        final double zValueA = this.verticesb.get(((int)Math.floor(axis0Pos)+(int)Math.floor(axis1Pos)*axis0Pts)*3 + 2);
        final double zValueB = this.verticesb.get(((int)Math.ceil(axis0Pos)+(int)Math.floor(axis1Pos)*axis0Pts)*3 + 2);
        final double zValueC = this.verticesb.get(((int)Math.floor(axis0Pos)+(int)Math.ceil(axis1Pos)*axis0Pts)*3 + 2);
        final double zValueD = this.verticesb.get(((int)Math.ceil(axis0Pos)+(int)Math.ceil(axis1Pos)*axis0Pts)*3 + 2);

        final double axis0Coef = axis0Pos - Math.floor(axis0Pos);
        final double axis1Coef = axis1Pos - Math.floor(axis1Pos);

        final double AB = axis0Coef*zValueA + (1.0-axis0Coef)*zValueB;
        final double CD = axis0Coef*zValueC + (1.0-axis0Coef)*zValueD;

        return axis1Coef*AB + (1.0-axis1Coef)*CD;
    }

    @Override
    protected void drawInternal(GLAutoDrawable glDrawable) throws GLException {

        final GL gl = glDrawable.getGL();

        if (gl.isGL2()) {

            final GL2 gl2 = gl.getGL2();

            if (this.wireframe){
                gl2.glDisable(GL.GL_CULL_FACE);
                gl2.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
            } else {
                gl2.glEnable(GL.GL_CULL_FACE);
                gl2.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
            }

            this.checkTexture(glDrawable, INDEX_IMAGE);

            final TextureManager image = getTextureImg();
            if(image != null && image.isUpdate()){
                gl2.glEnable(GL.GL_TEXTURE_2D);
                image.bind(gl2);
                image.enable(gl2);
            }else{
                gl2.glColor4f(0.4f, 0.4f, 0.4f, 0.4f);
            }

            if (!gl2.glIsEnabled(GL.GL_TEXTURE_2D)){
                gl2.glColor4f(0.4f, 0.4f, 0.4f, 0.4f);
            }

            gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
            gl2.glVertexPointer(3, GL.GL_FLOAT, 0, this.verticesb.rewind());

            if (this.normalb != null){
                gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
                gl2.glNormalPointer(GL.GL_FLOAT, 0, this.normalb.rewind());
            }

            gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
            gl2.glTexCoordPointer(2, GL.GL_FLOAT, 0, this.uvsb.rewind());
            gl2.glDrawElements(GL2.GL_TRIANGLES, this.getNumIndices(), GL2.GL_UNSIGNED_INT, this.indicesb.rewind());

            if(image != null){
                gl2.glDisable(GL.GL_TEXTURE_2D);
            }else{
                gl2.glColor4f(1.0f, 1.0f,1.0f, 1.0f);
            }

            if (this.wireframe){
                gl2.glEnable(GL.GL_CULL_FACE);
            }
        }
    }

    @Override
    public void dispose(GLAutoDrawable glDrawable) {
        super.dispose(glDrawable);
    }

}
