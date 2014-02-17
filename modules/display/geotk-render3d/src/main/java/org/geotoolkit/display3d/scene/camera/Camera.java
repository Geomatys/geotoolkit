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
package org.geotoolkit.display3d.scene.camera;

import com.jogamp.opengl.util.PMVMatrix;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;
import org.geotoolkit.display.DisplayElement;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.utils.ConstantUtils;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public abstract class Camera extends DisplayElement {

    /**
     * Property used to notify listeners when a camera parameters has changed.
     * We should have different properties for each parameter but we don't need
     * this level of details in events.
     */
    public static final String PROP_CONFIG = "config";
    /**
     * Eye property name.
     */
    public static final String PROP_EYE = ConstantUtils.SHADER_UNIFORM_CAMERA_EYE;
    /**
     * Center property name.
     */
    public static final String PROP_CENTER = ConstantUtils.SHADER_UNIFORM_CAMERA_CENTER;

    private final Map3D map;

    protected Vector3f eye;       // The camera position
    protected Vector3f center;    // The camera direction point
    protected Vector3f up;        // The camera upper vector
    protected int width = 300;      // width in pixel of view
    protected int height = 300;     // height in pixel of view
    protected float near = 1.0f;
    protected float far = Float.MAX_VALUE/2.0f;
    protected float fovy = 45.0f;

    protected float minLength = -1.0f;
    protected float maxLength = -1.0f;

    protected final PMVMatrix pmvMatrix = new PMVMatrix();

    protected Camera(Map3D map, Vector3f eye, Vector3f center, Vector3f up){
        this.map = map;
        this.eye = eye;
        this.center = center;
        this.up = up;
        this.up.normalize();
    }

    protected Camera(Camera orig) {
        this.map = orig.map;
        this.eye = new Vector3f(orig.eye);
        this.center = new Vector3f(orig.center);
        this.up = new Vector3f(orig.up);
        this.width = orig.width;
        this.height = orig.height;
        this.near = orig.near;
        this.far = orig.far;
        this.fovy = orig.fovy;
        this.minLength = orig.minLength;
        this.maxLength = orig.maxLength;
    }

    public Map3D getMap() {
        return map;
    }

    public Vector3f getEye(){
        return eye;
    }

    public float[] getEyeAsArray(){
        return new float[]{eye.x, eye.y, eye.z};
    }

    public void setEye(Vector3f eye){
        if(this.eye.equals(eye)) return;
        this.eye.set(eye);
        fireConfigChanged(PROP_EYE);
    }

    public final void setEye(float x, float y, float z){
        setEye(new Vector3f(x, y, z));
    }

    public Vector3f getCenter(){
        return center;
    }

    public float[] getCenterAsArray(){
        return new float[]{center.x, center.y, center.z};
    }

    public void setCenter(Vector3f center){
        if(this.center.equals(center)) return;
        this.center.set(center);
        fireConfigChanged(PROP_CENTER);
    }

    public final void setCenter(float x, float y, float z){
        setCenter(new Vector3f(x, y, z));
    }

    public Vector3f getUp(){
        return up;
    }

    public float[] getUpAsArray(){
        return new float[]{up.x, up.y, up.z};
    }

    public void setUp(Vector3f up){
        if(this.up.equals(up)) return;
        this.up.set(up);
        this.up.normalize();
        fireConfigChanged(PROP_CONFIG);
    }

    public final void setUp(float x, float y, float z){
        setUp(new Vector3f(x, y, z));
    }

    public float getAspect(){
        return (float)(width)/(float)(height);
    }

    public void setWidth(int width){
        if(this.width == width) return;
        this.width = width;
        fireConfigChanged(PROP_CONFIG);
    }

    public int getWidth(){
        return this.width;
    }

    public void setHeight(int height){
        if(this.height == height) return;
        this.height = height;
        fireConfigChanged(PROP_CONFIG);
    }

    public int getHeight(){
        return this.height;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        if(this.near == near) return;
        this.near = near;
        fireConfigChanged(PROP_CONFIG);
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        if(this.far == far) return;
        this.far = far;
        fireConfigChanged(PROP_CONFIG);
    }

    public float getFovy(){
        return fovy;
    }

    public void setFovy(float fovy) {
        if(this.fovy == fovy) return;
        this.fovy = fovy;
        fireConfigChanged(PROP_CONFIG);
    }

    public float getLength(){
        final Vector3f length = new Vector3f();
        length.sub(this.getCenter(), this.getEye());
        return length.length();
    }

    public float getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(float maxLength) {
        if(this.maxLength == maxLength) return;
        this.maxLength = maxLength;
        fireConfigChanged(PROP_CONFIG);
    }

    public float getMinLength() {
        return minLength;
    }

    public void setMinLength(float minLength) {
        if(this.minLength == minLength) return;
        this.minLength = minLength;
        fireConfigChanged(PROP_CONFIG);
    }

    public abstract void moveFront(final float move);
    public abstract void moveBack(final float move);
    public abstract void moveLeft(final float move);
    public abstract void moveRight(final float move);
    public abstract void moveUp(final float move);
    public abstract void moveDown(final float move);

    public abstract void rotateLeft(final float move);
    public abstract void rotateRight(final float move);
    public abstract void rotateUp(final float move);
    public abstract void rotateDown(final float move);

    public abstract void zoomMore(final float move);
    public abstract void zoomLess(final float move);
    public abstract void zoomTo(final float distance);

    public abstract FloatBuffer generatePMvMatrix();

    protected void fireConfigChanged(String propertyName){
        firePropertyChange(propertyName, false, true);
    }

}
