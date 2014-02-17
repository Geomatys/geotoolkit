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

import org.apache.sis.geometry.GeneralDirectPosition;

import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector3d;
import java.nio.FloatBuffer;
import java.util.logging.Level;

import org.geotoolkit.math.XMath;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.scene.ContextContainer3D;
import org.geotoolkit.display3d.scene.Terrain;

/**
 * @author Thomas Rouby (Geomatys)
 */
public class TrackBallCamera extends Camera {

    private final Vector3d scale3d;

    private float rotateX = 0.0f;
    private float rotateY = 0.0f;
    private float rotateZ = 0.0f;

    public TrackBallCamera(Map3D map){
        this(map,new Vector3f(0.0f,0.0f,0.0f),new Vector3f(1.0f,0.0f,0.0f),new Vector3f(0.0f,0.0f,1.0f));
    }

    public TrackBallCamera(Map3D map, Vector3f eye, Vector3f center, Vector3f up){
        super(map, eye, center, up);
        scale3d = new Vector3d(1.0, 1.0, 1.0);
    }

    public TrackBallCamera(TrackBallCamera orig) {
        super(orig);
        this.rotateX = orig.rotateX;
        this.rotateY = orig.rotateY;
        this.rotateZ = orig.rotateZ;
        this.scale3d = new Vector3d(orig.scale3d);
    }

    public TrackBallCamera(Map3D map,
            float eyeX, float eyeY, float eyeZ,
            float centerX, float centerY, float centerZ,
            float upX, float upY, float upZ){
        this(map, new Vector3f(eyeX, eyeY, eyeZ), new Vector3f(centerX, centerY, centerZ), new Vector3f(upX, upY, upZ));
    }

    /** Global scene scale */
    public Vector3d getScale3d() {
        return scale3d;
    }

    public double getProjectionLength(double length){
        final double fov = getFovy()*getAspect();
        final double angle = Math.toRadians(fov)/2.0;
        return (((Math.sin(angle)*length)/Math.cos(angle)) * 2.0) / (this.scale3d.x/100.0);
    }

    public double getViewScale(double length){
        final double distX = this.getProjectionLength(length);
        return distX/getWidth();
    }

    public void updateCameraElevation() {
        final Terrain terrain = ((ContextContainer3D)getMap().getContainer()).getTerrain();
        if(terrain==null) return;

        GeneralDirectPosition pos = new GeneralDirectPosition(terrain.getEnvelope().getCoordinateReferenceSystem());
        pos.setOrdinate(0, center.x);
        pos.setOrdinate(1, center.y);

        try {
            final double alti = terrain.getAltitudeSmoothOf(pos, terrain.getMaxScale());
            setCenter(center.x, center.y, (float)alti);
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "", ex);
        }

    }

    public void translate(float x, float y, float z){
        this.translate(new Vector3f(x, y, z));
    }

    public void translate(Vector3f translate){
        if (Float.isNaN(translate.x) || Float.isNaN(translate.y) || Float.isNaN(translate.z)){
            return;
        }
        this.center.add(translate);
        fireConfigChanged(PROP_CENTER);
    }

    public Vector3f getDirection(){
        final float radAngle = (float)Math.toRadians(this.rotateZ);
        Vector3f direction = new Vector3f(0.0f,1.0f,0.0f);
        direction = new Vector3f(   direction.x*(float)Math.cos(-radAngle) + direction.y*-(float)Math.sin(-radAngle),
                                    direction.x*(float)Math.sin(-radAngle) + direction.y*(float)Math.cos(-radAngle),
                                    0.0f);
        direction.normalize();
        return direction;
    }

    public Vector3f getLeft(){
        Vector3f direction = this.getDirection();
        return new Vector3f(-direction.y, direction.x, 0.0f);
    }

    @Override
    public float getLength() {
        return this.getEye().length();
    }

    @Override
    public void moveFront(final float move){
        Vector3f viewDir = this.getDirection();
        viewDir.normalize();
        viewDir.scale(move);

        this.translate(viewDir);
    }

    @Override
    public void moveBack(final float move){
        this.moveFront(-move);
    }

    @Override
    public void moveLeft(final float move) {
        Vector3f viewLeft = this.getLeft();
        viewLeft.normalize();
        viewLeft.scale(move);

        this.translate(viewLeft);
    }

    @Override
    public void moveRight(final float move) {
        this.moveLeft(-move);
    }

    @Override
    public void moveUp(final float move) {
        this.rotateUp(move);
    }

    @Override
    public void moveDown(final float move) {
        this.rotateDown(move);
    }

    @Override
    public void rotateLeft(final float move) {
        this.rotateZ = (float)((this.rotateZ+move)%360.0);
        fireConfigChanged(PROP_CONFIG);
    }

    @Override
    public void rotateRight(final float move) {
        this.rotateLeft(-move);
    }

    @Override
    public void rotateUp(final float move) {
        this.rotateX = XMath.clamp(this.rotateX + move, -90.0f, 0.0f);
        fireConfigChanged(PROP_CONFIG);
    }

    @Override
    public void rotateDown(final float move) {
        this.rotateUp(-move);
    }

    @Override
    public void zoomMore(final float move){
        final Vector3f tmpEye = new Vector3f(this.eye);

        final float distance = tmpEye.length();
        if (distance - move <= 0.0f){
            return;
        }
        tmpEye.normalize();

        float length = distance-move;
        if (this.minLength >= 0.0f){
            length = Math.max(this.minLength, distance-move);
        }
        if (this.maxLength >= 0.0f){
            length = Math.min(this.maxLength, distance-move);
        }

        tmpEye.scale(length);

        this.setEye(tmpEye);
    }

    @Override
    public void zoomLess(final float move){
        this.zoomMore(-move);
    }

    @Override
    public void zoomTo(float distance) {
        if (this.minLength >= 0.0f){
            distance = Math.max(this.minLength, distance);
        }
        if (this.maxLength >= 0.0f){
            distance = Math.min(this.maxLength, distance);
        }

        final Vector3f tmpEye = new Vector3f(this.eye);
        tmpEye.normalize();
        tmpEye.scale(distance);

        this.setEye(tmpEye);
    }

    @Override
    public FloatBuffer generatePMvMatrix(){
        this.pmvMatrix.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        this.pmvMatrix.glLoadIdentity();
        this.pmvMatrix.gluPerspective(this.getFovy(), this.getAspect(), this.getNear(), this.getFar());
        this.pmvMatrix.gluLookAt(this.getEye().x, this.getEye().y, this.getEye().z,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);
        this.pmvMatrix.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        this.pmvMatrix.glLoadIdentity();
        this.pmvMatrix.update();

        return pmvMatrix.glGetPMvMatrixf();
    }

    public float getRotateX() {
        return rotateX;
    }

    public float getRotateY() {
        return rotateY;
    }

    public float getRotateZ() {
        return rotateZ;
    }

    public void setRotateX(float rotateX) {
        if(this.rotateX == rotateX) return;
        this.rotateX = rotateX;
        fireConfigChanged(PROP_CONFIG);
    }

    public void setRotateY(float rotateY) {
        if(this.rotateY == rotateY) return;
        this.rotateY = rotateY;
        fireConfigChanged(PROP_CONFIG);
    }

    public void setRotateZ(float rotateZ) {
        if(this.rotateZ == rotateZ) return;
        this.rotateZ = rotateZ;
        fireConfigChanged(PROP_CONFIG);
    }
}
