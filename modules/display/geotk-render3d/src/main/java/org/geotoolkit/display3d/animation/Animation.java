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
package org.geotoolkit.display3d.animation;

import com.jogamp.opengl.GL;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.math.XMath;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.phase.Phase;
import org.geotoolkit.display3d.scene.ContextContainer3D;
import org.geotoolkit.display3d.scene.Terrain;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;
import org.geotoolkit.display3d.utils.BezierCurve;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public class Animation implements Phase {

    private Map3D map3d;

    private final List<List<Vector3d>> lineControlPoints = new ArrayList<>();
    private final List<AnimationPhase> phases = new ArrayList<>();

    private List<Vector3d> pathPoints;
    private List<Double> pathLength = new ArrayList<>();

    private double totalDistance = 0.0;
    private double high;
    private int precision = 10;
    private double speed = 1.0;
    private double scale;

    private boolean isRun = false;
    private boolean isValid = false;

    private long curTime = 0l;
    private long duration = 0l;

    public Animation(Map3D map3d){
        this(map3d,new ArrayList<Vector3d>(),0,0,10,1.0);
    }

    public Animation(Map3D map3d, List<Vector3d> pathPoints, double high, double scale, int precision){
        this(map3d, pathPoints, high, scale, precision, 1.0);
    }

    public Animation(Map3D map3d, List<Vector3d> pathPoints, double high, double scale, double speed){
        this(map3d, pathPoints, high, scale, 10, speed);
    }

    public Animation(Map3D map3d, List<Vector3d> pathPoints, double high, double scale){
        this(map3d, pathPoints, high, scale, 10, 1.0);
    }

    public Animation(Map3D map3d, List<Vector3d> pathPoints, double high, double scale, int precision, double speed){
        this.map3d = map3d;
        this.pathPoints = new ArrayList<>(pathPoints);
        this.high = high;
        this.scale = scale;
        this.precision = precision;
        this.speed = speed;
    }

    public void addPhase(AnimationPhase phase){
        this.phases.add(phase);
    }

    public void removePhase(AnimationPhase phase){
        this.phases.remove(phase);
    }

    public List<AnimationPhase> getPhases(){
        return phases;
    }

    public final double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
        this.isValid = false;
    }

    public final double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        this.isValid = false;
    }

    public final double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        this.isValid = false;
    }

    public void setPathPoints(List<Coordinate> pathPoints) {
        if (pathPoints != null){
            this.pathPoints = new ArrayList<>();
            for (Coordinate point : pathPoints){
                this.pathPoints.add(new Vector3d(point.x, point.y, point.z));
            }
        }
        this.isValid = false;
    }

    public boolean isRun() {
        return this.isRun;
    }

    public boolean isValid(){
        return this.isValid;
    }

    public boolean validate() {
        try {
            this.computeAll();
        } catch (Exception ex) {
            Map3D.LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            this.isValid = false;
        }
        return this.isValid;
    }

    public double approximateSpeed(long duration){
        double length = 0.0;

        for (int i=0; i<this.pathPoints.size()-2; i++){
            Vector3d tmp1 = this.pathPoints.get(i);
            Vector3d tmp2 = this.pathPoints.get(i+1);

            Vector3d len = new Vector3d(0.0, 0.0, 0.0);
            len.sub(tmp2, tmp1);
            length += len.length();
        }

        return length / duration;
    }

    public void computeAll() throws AnimationNotValidException {
        if (this.isRun){
            return;
        }
        this.isValid = false;
        if (this.pathPoints.size()<3) return;
        this.computeAltitude();
//        this.computeCollision();
        this.computeControlPoints();
        this.computeDistance();
        this.isValid = true;
    }

    private void computeAltitude() throws AnimationNotValidException {
        try {
            for (Vector3d pathPoint : this.pathPoints){
                pathPoint.z = this.high;
            }
        } catch (Exception ex) {
            throw new AnimationNotValidException(ex);
        }
    }

//    private void computeCollision() throws AnimationNotValidException {
//        try {
//            final Terrain terrain = this.map3d.getTerrain();
//            if (terrain != null){
//                final TrackBallCamera camera = this.map3d.getCamera();
//                final double scale = camera.getViewScale(camera.getLength());
//                int pathSize = this.pathPoints.size();
//
//                for (int i=0; i<pathSize-1; i++){
//                    final Vector3d P0 = this.pathPoints.get(i);
//                    final Vector3d P1 = this.pathPoints.get(i+1);
//
//                    final Vector3d P0P1 = new Vector3d();
//                    P0P1.sub(P1, P0);
//
//                    for (int t=1; t<=this.precision; t++){
//                        final double pos = (double)t/(this.precision +1);
//
//                        final Vector3d testPoint = new Vector3d(P0.x+pos*P0P1.x, P0.y+pos*P0P1.y, P0.z+pos*P0P1.z);
//                        final double altitude = terrain.getAltitudeOf(testPoint.x, testPoint.y, this.scale);
//
//                        if (testPoint.z < altitude){
//                            testPoint.z = altitude+this.high;
//                            this.pathPoints.add(i+1, testPoint);
//                            pathSize += 1;
//                            break;
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            throw new AnimationNotValidException(ex);
//        }
//    }

    private static Vector3d tangeante3Points(Vector3d P1, Vector3d P2, Vector3d P3){
        final Vector3d P2P3 = new Vector3d();
        P2P3.sub(P3,P2);
        P2P3.normalize();

        final Vector3d P2P1 = new Vector3d();
        P2P1.sub(P1, P2);
        P2P1.normalize();

        final Vector3d T1 = new Vector3d();
        T1.add(P2, P2P1);

        final Vector3d T2 = new Vector3d();
        T2.add(P2, P2P3);

        final Vector3d T1T2 = new Vector3d();
        T1T2.sub(T2, T1);

        return T1T2;
    }

    private void computeControlPoints() throws AnimationNotValidException {

        this.lineControlPoints.clear();
        for (int i=0; i<this.pathPoints.size()-1; i++){
            List<Vector3d> controlPoints = new ArrayList<>();

            Vector3d P1 = this.pathPoints.get(i);
            Vector3d P2 = this.pathPoints.get(i+1);

            Vector3d P1P2 = new Vector3d();
            P1P2.sub(P2,P1);

            if (this.pathPoints.size() > 2){
                try {

                Vector3d tan1;
                Vector3d tan2;

                if (i == 0){
                    Vector3d P3 = this.pathPoints.get(i+2);

                    tan1 = new Vector3d(P1P2);
                    tan2 = tangeante3Points(P1, P2, P3);
                } else if (i == this.pathPoints.size()-2){
                    Vector3d P0 = this.pathPoints.get(i-1);

                    tan1 = tangeante3Points(P0, P1, P2);
                    tan2 = new Vector3d(P1P2);
                } else {
                    Vector3d P0 = this.pathPoints.get(i-1);
                    Vector3d P3 = this.pathPoints.get(i+2);

                    tan1 = tangeante3Points(P0, P1, P2);
                    tan2 = tangeante3Points(P1, P2, P3);
                }
                tan1.normalize();
                tan2.normalize();

                Vector3d M1 = new Vector3d(P1P2);
                M1.scale(1.0/5.0);
                M1.add(P1, M1);

                Vector3d M2 = new Vector3d(P1P2);
                M2.scale(4.0/5.0);
                M2.add(P1, M2);

                double a1 = P2.x*tan1.x - P1.x*tan1.x +
                        P2.y*tan1.y - P1.y*tan1.y +
                        P2.z*tan1.z - P1.z*tan1.z;

                double b1 = P2.x*P1.x - P2.x*M1.x - P1.x*P1.x + P1.x*M1.x +
                        P2.y*P1.y - P2.y*M1.y - P1.y*P1.y + P1.y*M1.y +
                        P2.z*P1.z - P2.z*M1.z - P1.z*P1.z + P1.z*M1.z;

                double t1 = -b1/a1; // GeometryUtils.clamp(-b1 / a1, -this.speed, this.speed);

                final double xo1 = P1.x + t1*tan1.x;
                final double yo1 = P1.y + t1*tan1.y;
                final double zo1 = P1.z + t1*tan1.z;

                Vector3d C1 = new Vector3d(xo1, yo1, zo1);

//                if (terrain != null){
//                    double altitude1 = terrain.getAltitudeOf(C1.x, C1.y, this.scale);
//                    while (C1.z <= altitude1){
//                        t1 /= 2;
//                        C1.x = P1.x + t1*tan1.x;
//                        C1.y = P1.y + t1*tan1.y;
//                        C1.z = P1.z + t1*tan1.z;
//                        altitude1 = terrain.getAltitudeOf(C1.x, C1.y, this.scale);
//                    }
//                }

                double a2 = P2.x*tan2.x - P1.x*tan2.x +
                        P2.y*tan2.y - P1.y*tan2.y +
                        P2.z*tan2.x - P1.z*tan2.z;

                double b2 = P2.x*P2.x - P2.x*M2.x - P1.x*P2.x + P1.x*M2.x +
                        P2.y*P2.y - P2.y*M2.y - P1.y*P2.y + P1.y*M2.y +
                        P2.z*P2.z - P2.z*M2.z - P1.z*P2.z + P1.z*M2.z;

                double t2 = -b2 / a2; // GeometryUtils.clamp(-b2 / a2, -this.speed, this.speed);

                final double xo2 = P2.x + t2*tan2.x;
                final double yo2 = P2.y + t2*tan2.y;
                final double zo2 = P2.z + t2*tan2.z;

                Vector3d C2 = new Vector3d(xo2, yo2, zo2);

//                if (terrain != null){
//                    double altitude2 = terrain.getAltitudeOf(C2.x, C2.y, this.scale);
//                    while (C2.z <= altitude2){
//                        t2 /= 2;
//                        C2.x = P2.x + t2*tan2.x;
//                        C2.y = P2.y + t2*tan2.y;
//                        C2.z = P2.z + t2*tan2.z;
//                        altitude2 = terrain.getAltitudeOf(C2.x, C2.y, this.scale);
//                    }
//                }

                controlPoints.add(C1);
                controlPoints.add(C2);
                } catch (Exception ex) {
                    throw new AnimationNotValidException(ex);
                }
            }

            this.lineControlPoints.add(controlPoints);
        }
    }

    private void computeDistance(){
        this.totalDistance = 0.0;
        this.pathLength = new ArrayList<Double>();

        for (int i=0; i<this.pathPoints.size()-1; i++){

            final List<Vector3d> bezierPath = new ArrayList<Vector3d>();
            final List<Vector3d> controlPoints = this.lineControlPoints.get(i);
            final Vector3d P0 = this.pathPoints.get(i);
            final Vector3d P1 = this.pathPoints.get(i+1);

            bezierPath.add(P0);
            for (int j=1; j<=this.precision; j++){
                final double t = (double)j/(double)(this.precision+1);
                final List<Vector3d> Pi = new ArrayList<>();
                Pi.add(P0);
                Pi.add(controlPoints.get(0));
                Pi.add(controlPoints.get(1));
                Pi.add(P1);

                final Vector3d curve = BezierCurve.bezierCurve(Pi, t);
                bezierPath.add(curve);
            }
            bezierPath.add(P1);

            double length = 0.0;
            for (int b=0; b<bezierPath.size()-1; b++){
                final Vector3d V0 = bezierPath.get(b);
                final Vector3d V1 = bezierPath.get(b+1);
                final Vector3d V0V1 = new Vector3d();
                V0V1.sub(V1, V0);

                length += V0V1.length();
            }
            this.totalDistance += length;
            this.pathLength.add(length);
        }
    }

    private double distanceAt(double time, double totalTime){
        return this.totalDistance*XMath.clamp(time/totalTime, 0.0, 1.0);
    }

    public Vector3d accelAt(double time, double totalTime) throws AnimationNotValidException {
        return this.accelAt(this.distanceAt(time, totalTime));
    }

    public Vector3d accelAt(double distance) throws AnimationNotValidException {

        this.assertIsValid();

        int index = -1;
        double t = 0.0;
        Vector3d P1 = new Vector3d();
        Vector3d P2 = new Vector3d();
        Vector3d P1P2 = new Vector3d();

        double compDist = 0.0;
        for (int i=0; i<this.pathLength.size(); i++){
            P1 = this.pathPoints.get(i);
            P2 = this.pathPoints.get(i+1);

            P1P2.sub(P2, P1);

            index = i;
            t = XMath.clamp((distance - compDist) / this.pathLength.get(i), 0.0, 1.0);

            if (compDist+this.pathLength.get(i) > distance){
                break;
            }

            compDist += this.pathLength.get(i);
        }

        final List<Vector3d> controlPoints = this.lineControlPoints.get(index);

        if (controlPoints.size() == 2){
            final List<Vector3d> Pi = new ArrayList<>();
            Pi.add(P1);
            Pi.add(controlPoints.get(0));
            Pi.add(controlPoints.get(1));
            Pi.add(P2);

            return BezierCurve.bezierSecondCurve(Pi, t);
        }
        return null;
    }


    public Vector3d speedAt(double time, double totalTime) throws AnimationNotValidException {
        return this.speedAt(this.distanceAt(time, totalTime));
    }

    public Vector3d speedAt(double distance) throws AnimationNotValidException {

        this.assertIsValid();

        int index = -1;
        double t = 0.0;
        Vector3d P1 = new Vector3d();
        Vector3d P2 = new Vector3d();
        Vector3d P1P2 = new Vector3d();

        double compDist = 0.0;
        for (int i=0; i<this.pathLength.size(); i++){
            P1 = this.pathPoints.get(i);
            P2 = this.pathPoints.get(i+1);

            P1P2.sub(P2, P1);

            index = i;
            t = XMath.clamp((distance-compDist)/this.pathLength.get(i), 0.0, 1.0);

            if (compDist+this.pathLength.get(i) > distance){
                break;
            }

            compDist += this.pathLength.get(i);
        }

        final List<Vector3d> controlPoints = this.lineControlPoints.get(index);

        if (controlPoints.size() == 2){
            final List<Vector3d> Pi = new ArrayList<>();
            Pi.add(P1);
            Pi.add(controlPoints.get(0));
            Pi.add(controlPoints.get(1));
            Pi.add(P2);

            return BezierCurve.bezierDerivativeCurve(Pi, t);
        }
        return null;
    }

    public Vector3d positionAt(double time, double totalTime) throws AnimationNotValidException {
        return this.positionAt(this.distanceAt(time, totalTime));
    }

    public Vector3d positionAt(double distance) throws AnimationNotValidException {

        this.assertIsValid();

        int index = -1;
        double t = 0.0;
        Vector3d P1 = new Vector3d();
        Vector3d P2 = new Vector3d();
        Vector3d P1P2 = new Vector3d();

        double compDist = 0.0;
        for (int i=0; i<this.pathLength.size(); i++){
            P1 = this.pathPoints.get(i);
            P2 = this.pathPoints.get(i+1);

            P1P2.sub(P2, P1);

            index = i;
            t = XMath.clamp((distance-compDist)/this.pathLength.get(i), 0.0, 1.0);

            if (compDist+this.pathLength.get(i) > distance){
                break;
            }

            compDist += this.pathLength.get(i);
        }

        final List<Vector3d> controlPoints = this.lineControlPoints.get(index);

        if (controlPoints.size() == 2){
            final List<Vector3d> Pi = new ArrayList<>();
            Pi.add(P1);
            Pi.add(controlPoints.get(0));
            Pi.add(controlPoints.get(1));
            Pi.add(P2);

            return BezierCurve.bezierCurve(Pi, t);
        }
        return null;
    }

    public List<Vector3d> getPathPoints() {

        final List<Vector3d> tmpPathPoints = new ArrayList<Vector3d>();
        for (Vector3d point : this.pathPoints){
            tmpPathPoints.add(new Vector3d(point.x, point.y, point.z));
        }

        return tmpPathPoints;
    }

    public List<Vector3d> getControlPoints() {

        final List<Vector3d> controls = new ArrayList<Vector3d>();
        for (int i=0; i<this.pathPoints.size(); i++){
            controls.add(new Vector3d(this.pathPoints.get(i)));
            if (i < this.lineControlPoints.size()){
                final List<Vector3d> controlPoints = this.lineControlPoints.get(i);
                for (int j=0; j<controlPoints.size(); j++){
                    controls.add(new Vector3d(controlPoints.get(j).x, controlPoints.get(j).y, controlPoints.get(j).z));
                }
            }
        }
        return controls;
    }

    public void assertIsValid() throws AnimationNotValidException {
        if (!this.isValid){
            throw new AnimationNotValidException();
        }
    }

    public void initializeAnimationPosition() throws AnimationNotValidException {

        if (!this.isValid){
            throw new AnimationNotValidException();
        }
        final Terrain terrain = ((ContextContainer3D)map3d.getContainer()).getTerrain();
        if (terrain != null){
            try {
                final Vector3d position = this.positionAt(0.0);

                GeneralDirectPosition pos = new GeneralDirectPosition(terrain.getEnvelope().getCoordinateReferenceSystem());
                pos.setOrdinate(0, position.x);
                pos.setOrdinate(1, position.y);

                final double alti = terrain.getAltitudeSmoothOf(pos, terrain.getMaxScale());
                position.z += alti;

                final Vector3d direction = this.speedAt(0.0);
                direction.z = 0.0;
                direction.normalize();

                double angle =  180.0 + Math.toDegrees(Math.acos(-direction.x));
                if (direction.y != 0.0){
                    angle = 180.0f + Math.signum(direction.y)*Math.toDegrees(Math.acos(-direction.x));
                }

                this.map3d.getCamera().setRotateZ((float) angle + 90.0f);

                this.map3d.getCamera().setCenter(new Vector3f(position));
            } catch (Exception ex) {
                throw new AnimationNotValidException(ex);
            }
        }
    }

    public void startAnimation(long duration) throws AnimationNotValidException {
        this.isRun = true;
        this.duration = duration;

        for (AnimationPhase phase : phases){
            phase.beginPhase(this);
        }

        map3d.addPhase(this);
    }

    public void stopAnimation() {
        map3d.removePhase(this);
        this.isRun = false;

        for (AnimationPhase phase : phases){
            phase.endPhase(this);
        }
    }

    public long getDuration() {
        return duration;
    }

    public long getCurTime(){
        return curTime;
    }

    public void setCurTime(long position){
        this.curTime = XMath.clamp(position, 0l, duration);
    }

    @Override
    public void setMap(Map3D map) {
        this.map3d = map3d;
    }

    @Override
    public Map3D getMap() {
        return this.map3d;
    }

    @Override
    public void update(GL gl) {
        if (this.isRun) {
            try{

                final TrackBallCamera camera = this.map3d.getCamera();
                final Terrain terrain = ((ContextContainer3D)map3d.getContainer()).getTerrain();
                final Vector3d position = this.positionAt(curTime, duration);

                GeneralDirectPosition pos = new GeneralDirectPosition(terrain.getEnvelope().getCoordinateReferenceSystem());
                pos.setOrdinate(0, position.x);
                pos.setOrdinate(1, position.y);

                final double alti = terrain.getAltitudeSmoothOf(pos, terrain.getMaxScale());
                position.z += alti;

                final Vector3d direction = this.speedAt(curTime, duration);
                direction.z = 0.0;
                direction.normalize();

                final double angle;
                if (Math.signum(direction.y) != 0.0){
                    angle = 180.0f + Math.signum(direction.y)*Math.toDegrees(Math.acos(-direction.x));
                } else {
                    angle =  180.0 + Math.toDegrees(Math.acos(-direction.x));
                }

                camera.setRotateZ((float) angle + 90.0f);
                camera.setCenter(new Vector3f(position));
            } catch (Exception ex) {
                this.map3d.getMonitor().exceptionOccured(ex, Level.WARNING);
            }

            for (AnimationPhase phase : phases){
                phase.currentPhase(gl, this);
            }

            if (curTime >= duration) {
                this.stopAnimation();
            }
        }
    }

    public interface AnimationPhase {
        public void beginPhase(Animation animation);
        public void currentPhase(GL gl, Animation animation);
        public void endPhase(Animation animation);
    }
}
