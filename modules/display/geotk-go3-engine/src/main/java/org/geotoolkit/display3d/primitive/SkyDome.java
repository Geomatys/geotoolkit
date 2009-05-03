/*
 * SkyDome.java
 *
 */

package org.geotoolkit.display3d.primitive;

import com.ardor3d.light.DirectionalLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Dome;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.util.Timer;
import com.ardor3d.util.geom.BufferUtils;
import java.nio.FloatBuffer;

/**
 * sky gradient based on "A practical analytic model for daylight"
 * by A. J. Preetham, Peter Shirley, Brian Smits (University of Utah)
 * @author Highnik
 */
public class SkyDome extends Node {

    public static final float INFINITY = 3.3e+38f;
    public static final float EPSILON = 0.000001f;
    private Dome dome;
    private Vector3 cameraPos = new Vector3();
    // shading parameters
    private float thetaSun;
    private float phiSun;
    private float turbidity = 2.0f;
    private boolean isLinearExpControl;
    private float exposure = 18.0f;
    private float overcast;
    private float gammaCorrection = 2.5f;
    // time parameters
    private float timeOfDay = 0.0f;
    private float julianDay = 0.0f;
    private float latitude = 0.0f;
    private float longitude = 0.0f;
    private float stdMeridian = 0.0f;
    private float sunnyTime = 12.0f;
    private float solarDeclination = 0.0f;
    private float latitudeInRadian = 0.0f;
    private boolean isNight = false;
    // timer control.
    private Timer timer;
    private float currentTime;
    private float updateTime = 0.0f;
    private float timeWarp = 180.0f;
    private boolean renderRequired = true;
    // used at update color
    private float chi;
    private float zenithLuminance;
    private float zenithX;
    private float zenithY;
    private float[] perezLuminance;
    private float[] perezX;
    private float[] perezY;
    private Vector3 sunDirection = new Vector3();
    private Vector3 sunPosition = new Vector3();
    private FloatBuffer colorBuf;
    private FloatBuffer normalBuf;
    private Vector3 vertex = new Vector3();
    private float gamma;
    private float cosTheta;
    private float cosGamma2;
    private float x_value;
    private float y_value;
    private float yClear;
    private float yOver;
    private float _Y;
    private float _X;
    private float _Z;
    private DirectionalLight dr;
    private Node sun;
//    private LensFlare flare;
    private boolean sunEnabled = true;
    /** Distribution coefficients for the luminance(Y) distribution function */
    private float distributionLuminance[][] = { // Perez distributions
        {0.17872f, -1.46303f}, // a = darkening or brightening of the horizon
        {-0.35540f, 0.42749f}, // b = luminance gradient near the horizon,
        {-0.02266f, 5.32505f}, // c = relative intensity of the circumsolar region
        {0.12064f, -2.57705f}, // d = width of the circumsolar region
        {-0.06696f, 0.37027f}};   // e = relative backscattered light
    /** Distribution coefficients for the x distribution function */
    private float distributionXcomp[][] = {
        {-0.01925f, -0.25922f},
        {-0.06651f, 0.00081f},
        {-0.00041f, 0.21247f},
        {-0.06409f, -0.89887f},
        {-0.00325f, 0.04517f}
    };
    /** Distribution coefficients for the y distribution function */
    private float distributionYcomp[][] = {
        {-0.01669f, -0.26078f},
        {-0.09495f, 0.00921f},
        {-0.00792f, 0.21023f},
        {-0.04405f, -1.65369f},
        {-0.01092f, 0.05291f}
    };
    /** Zenith x value */
    private float zenithXmatrix[][] = {
        {0.00165f, -0.00375f, 0.00209f, 0.00000f},
        {-0.02903f, 0.06377f, -0.03202f, 0.00394f},
        {0.11693f, -0.21196f, 0.06052f, 0.25886f}
    };
    /** Zenith y value */
    private float zenithYmatrix[][] = {
        {0.00275f, -0.00610f, 0.00317f, 0.00000f},
        {-0.04214f, 0.08970f, -0.04153f, 0.00516f},
        {0.15346f, -0.26756f, 0.06670f, 0.26688f}
    };

    private LightState lightState;

    /** Creates a new instance of SkyDome */
    public SkyDome() {
        this("SkyDome", 11, 18, 100f);
    }

    public SkyDome(String name) {
        this(name, 11, 18, 100f);
    }

    public SkyDome(String name, int planes, int radialSamples, float radius) {
        this(name, new Vector3(0, 0, 0), planes, radialSamples, radius);
    }

    public SkyDome(String name, Vector3 center, int planes, int radialSamples, float radius) {
        dome = new Dome(name, center, planes, radialSamples, radius, true);
//        dome = new Sphere(name, center, planes, radialSamples, radius);
//        dome.setIsCollidable(false);
        dome.setSolidColor(ColorRGBA.BLUE);
        attachChild(dome);
        timer = new Timer();
        currentTime = (float) timer.getTimeInSeconds();

        solarDeclination = calc_solar_declination(julianDay);
        sunnyTime = calc_sunny_time(latitude, solarDeclination);

        // create a lens flare effects
        setupLensFlare();

        ZBufferState zbuff = new ZBufferState();//DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
        zbuff.setWritable(false);
        zbuff.setEnabled(true);
        zbuff.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        setRenderState(zbuff);

        setCullHint(CullHint.Never);
        setLightCombineMode(Spatial.LightCombineMode.Off);
        setTextureCombineMode(Spatial.TextureCombineMode.Replace);

        CullState cs = new CullState(); //DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        cs.setEnabled(true);
        cs.setCullFace(CullState.Face.None);
        dome.setRenderState(cs);
        dome.setCullHint(CullHint.Never);
        dome.setLightCombineMode(LightCombineMode.Off);
        dome.setTextureCombineMode(Spatial.TextureCombineMode.Replace);
    }

    /**
     * Set Sun's positon
     */
    public void setSunPosition(Vector3 sunPos) {
        Vector3 pos = new Vector3();
        pos = FastMath2.cartesianToSpherical(sunPos, pos);
        thetaSun = pos.getZf();
        phiSun = pos.getYf();
    }

    /**
     * Return Sun's position
     */
    public Vector3 getSunPosition() {
        return sunPosition;
    }

    /**
     * Convert time to sun position
     * @param time
     *              Sets a time of day between 0 to 24 (6,25 = 6:15 hs)
     */
    public void setSunPosition(float time) {
        float solarTime, solarAltitude, opp, adj, solarAzimuth, cosSolarDeclination, sinSolarDeclination, sinLatitude, cosLatitude;
        this.timeOfDay = time;

        sinLatitude = FastMath2.sin(latitudeInRadian);
        cosLatitude = FastMath2.cos(latitudeInRadian);
        sinSolarDeclination = FastMath2.sin(solarDeclination);
        cosSolarDeclination = FastMath2.cos(solarDeclination);

        // real time
        solarTime = time + (0.170f * FastMath2.sin(4f * FastMath2.PI * (julianDay - 80f) / 373f) -
                0.129f * FastMath2.sin(FastMath2.TWO_PI * (julianDay - 8f) / 355f)) +
                (stdMeridian - longitude) / 15;

        solarAltitude = FastMath2.asin(sinLatitude * sinSolarDeclination -
                cosLatitude * cosSolarDeclination *
                FastMath2.cos(FastMath2.PI * solarTime / sunnyTime));

        opp = -cosSolarDeclination * FastMath2.sin(FastMath2.PI * solarTime / sunnyTime);

        adj = (cosLatitude * sinSolarDeclination + sinLatitude * cosSolarDeclination *
                FastMath2.cos(FastMath2.PI * solarTime / sunnyTime));

        solarAzimuth = FastMath2.atan2(opp, adj);

        if (solarAltitude > 0.0f) {

            isNight = false;
            if ((opp < 0.0f && solarAzimuth < 0.0f) || (opp > 0.0f && solarAzimuth > 0.0f)) {
                solarAzimuth = FastMath2.HALF_PI + solarAzimuth;
            } else {
                solarAzimuth = FastMath2.HALF_PI - solarAzimuth;
            }
            phiSun = FastMath2.TWO_PI - solarAzimuth;
            thetaSun = FastMath2.HALF_PI - solarAltitude;

            sunDirection.setX( dome.getRadius() );
            sunDirection.setY( phiSun );
            sunDirection.setZ( solarAltitude );
            sunPosition = FastMath2.sphericalToCartesian(sunDirection, sunPosition);

            if (this.isSunEnabled()) {
                sun.setTranslation(sunPosition);
            }

        } else {
            isNight = true;
        }

    }

    /**
     * Return if now is night
     */
    public boolean isNight() {
        return isNight;
    }

    /**
     * Set Day of year between 0 to 364
     */
    public void setDay(float julianDay) {
        this.julianDay = clamp(julianDay, 0.0f, 365.0f);
        // Solar declination
        solarDeclination = calc_solar_declination(julianDay);
        sunnyTime = calc_sunny_time(latitudeInRadian, solarDeclination);
    }

    /**
     * Get Day of year
     */
    public float getDay() {
        return julianDay;
    }

    /**
     * Set latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = clamp(latitude, -90.0f, 90.0f);
        latitudeInRadian = FastMath2.DEG_TO_RAD * latitude;
        sunnyTime = calc_sunny_time(latitudeInRadian, solarDeclination);
    }

    /**
     * Get latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Set longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     * Get longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Set standar meridian
     * @param stdMeridian
     *                      TimeZone * 15
     */
    public void setStandardMeridian(float stdMeridian) {
        this.stdMeridian = stdMeridian;
    }

    /**
     * Get standar meridian
     */
    public float getStandardMeridian() {
        return stdMeridian;
    }

    public void setTurbidity(float turbidity) {
        this.turbidity = clamp(turbidity, 1.0f, 512.0f);
    }

    /**
     * Set Exposure factor
     */
    public void setExposure(boolean isLinearExpControl, float exposure) {
        this.isLinearExpControl = isLinearExpControl;
        this.exposure = 1.0f / clamp(exposure, 1.0f, INFINITY);
    }

    /**
     * Set Over Cast factor
     */
    public void setOvercastFactor(float overcast) {
        this.overcast = clamp(overcast, 0.0f, 1.0f);
    }

    /**
     * Set gamma correction factor
     */
    public void setGammaCorrection(float gamma) {
        this.gammaCorrection = 1.0f / clamp(gamma, EPSILON, INFINITY);
    }

    /**
     * Seconds to update
     */
    public void setUpdateTime(float seconds) {
        this.updateTime = seconds;
    }

    public float getUpdateTime() {
        return updateTime;
    }

    /**
     * if updateTime = 1 and timeWarp = 1, every seconds will be updated
     */
    public void setTimeWarp(float timeWarp) {
        this.timeWarp = timeWarp;
    }

    public float getTimeWarp() {
        return timeWarp;
    }

    public void update(Camera camera) {
        if (updateTime > 0.0f) {
            if ((timer.getTimeInSeconds() - currentTime) >= updateTime) {
                currentTime = (float) timer.getTimeInSeconds();
                timeOfDay += updateTime * timeWarp / 3600f;
                setSunPosition(timeOfDay);
                renderRequired = true;
            }
        }
//        cameraPos = DisplaySystem.getDisplaySystem().getRenderer().getCamera().getLocation();
        dome.setTranslation(camera.getLocation());
        render();
    }

    /**
     * update Sky color
     */
    public void render() {

        if (!renderRequired) {
            return;
        }

        if (isNight) {
            dome.setSolidColor(ColorRGBA.BLACK);
            return;
        }

        // get zenith luminance
        chi = ((4.0f / 9.0f) - (turbidity / 120.0f)) * (FastMath2.PI - (2.0f * thetaSun));
        zenithLuminance = ((4.0453f * turbidity) - 4.9710f) * FastMath2.tan(chi) - (0.2155f * turbidity) + 2.4192f;
        if (zenithLuminance < 0.0f) {
            zenithLuminance = -zenithLuminance;
        }

        // get x / y zenith
        zenithX = getZenith(zenithXmatrix, thetaSun, turbidity);
        zenithY = getZenith(zenithYmatrix, thetaSun, turbidity);

        // get perez function parameters
        perezLuminance = getPerez(distributionLuminance, turbidity);
        perezX = getPerez(distributionXcomp, turbidity);
        perezY = getPerez(distributionYcomp, turbidity);

        // make some precalculation
        zenithX = perezFunctionO1(perezX, thetaSun, zenithX);
        zenithY = perezFunctionO1(perezY, thetaSun, zenithY);
        zenithLuminance = perezFunctionO1(perezLuminance, thetaSun, zenithLuminance);

        // build sun direction vector
        sunDirection.setX( FastMath2.cos(FastMath2.HALF_PI - thetaSun) * FastMath2.cos(phiSun) );
        sunDirection.setY( FastMath2.sin(FastMath2.HALF_PI - thetaSun) );
        sunDirection.setZ( FastMath2.cos(FastMath2.HALF_PI - thetaSun) * FastMath2.sin(phiSun) );
        sunDirection.normalizeLocal();

        // trough all vertices
        normalBuf = dome.getMeshData().getNormalBuffer();
        colorBuf = dome.getMeshData().getColorBuffer();

        for (int i = 0; i < (normalBuf.limit() / 3); i++) {

            BufferUtils.populateFromBuffer(vertex, normalBuf, i);

            // angle between sun and vertex
            gamma = FastMath2.acos((float)vertex.dot(sunDirection));

            if (vertex.getY() < 0.05f) {
                vertex.setY( 0.05f );
            }

            cosTheta = 1.0f / vertex.getXf();
            cosGamma2 = FastMath2.sqr(FastMath2.cos(gamma));

            // Compute x,y values
            x_value = perezFunctionO2(perezX, cosTheta, gamma, cosGamma2, zenithX);
            y_value = perezFunctionO2(perezY, cosTheta, gamma, cosGamma2, zenithY);

            // luminance(Y) for clear & overcast sky
            yClear = perezFunctionO2(perezLuminance, cosTheta, gamma, cosGamma2, zenithLuminance);
            yOver = (1.0f + 2.0f * vertex.getYf()) / 3.0f;

            _Y = FastMath2.LERP(overcast, yClear, yOver);
            _X = (x_value / y_value) * _Y;
            _Z = ((1.0f - x_value - y_value) / y_value) * _Y;

            ColorXYZ color = new ColorXYZ(_X, _Y, _Z);
            color.convertXYZtoRGB();
            color.convertRGBtoHSV();

            if (isLinearExpControl) {                                       // linear scale
                color.setValue(color.getValue() * exposure);
            } else {                                                        // exp scale
                color.setValue(1.0f - FastMath2.exp(-exposure * color.getValue()));
            }
            color.convertHSVtoRGB();

            // gamma control
            color.setGammaCorrection(gammaCorrection);

            // clamp rgb between 0.0 - 1.0
            color.clamp();

            // change the color
            BufferUtils.setInBuffer(color.getRGBA(), colorBuf, i);
        }
        renderRequired = false;
    }

    /**
     * Returns a LightNode that represents the Sun
     */
    public Node getSun() {
        return sun;
    }

//    /**
//     * Set the rootNode to flare
//     */
//    public void setRootNode(Node value) {
//        if (flare != null) {
//            flare.setRootNode(value);
//        }
//    }

    /**
     * Set a intensity to Flare
     */
    public void setIntensity(float value) {
//        if (flare != null) {
//            flare.setIntensity(value);
//        }
    }

    /**
     * Attach the lightstate to scene
     */
    public void setTarget(Spatial node) {
        node.setRenderState(lightState);
    }

    public void setSunEnabled(boolean enable) {
        this.sunEnabled = enable;
//        sun.getLight().setEnabled(enable);
    }

    public boolean isSunEnabled() {
        return sunEnabled;
    }

    private float calc_solar_declination(float jDay) {
        return (0.4093f * FastMath2.sin(FastMath2.TWO_PI * (284f + jDay) / 365f));
    }

    private float calc_sunny_time(float lat, float solarDeclin) {
        // Time of hours over horizon
        sunnyTime = (2.0f * FastMath2.acos(-FastMath2.tan(lat) * FastMath2.tan(solarDeclin)));
        sunnyTime = (sunnyTime * FastMath2.RAD_TO_DEG) / 15;
        return sunnyTime;
    }

    /**
     * Create Lens flare effect
     */
    private void setupLensFlare() {

        dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(ColorRGBA.WHITE);
        dr.setAmbient(ColorRGBA.GRAY);
        dr.setDirection(new Vector3(0.0f, 0.0f, 0.0f));

        lightState = new LightState();
        lightState.setEnabled(true);
        lightState.attach(dr);

        sun = new Node("SunNode");
        sun.setRenderState(lightState);

//        Vector3f min2 = new Vector3f(-0.1f, -0.1f, -0.1f);
//        Vector3f max2 = new Vector3f(0.1f, 0.1f, 0.1f);
//        Box lightBox = new Box("lightbox", min2, max2);
//        lightBox.setModelBound(new BoundingBox());
//        lightBox.updateModelBound();
//        sun.attachChild(lightBox);
//        lightBox.setLightCombineMode(Spatial.LightCombineMode.Off);
//
//        // Setup the lensflare textures.
//        TextureState[] tex = new TextureState[4];
//        tex[0] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
//        tex[0].setTexture(
//                TextureManager.loadTexture(
//                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "flare1.png"),
//                Texture.MinificationFilter.Trilinear,
//                Texture.MagnificationFilter.Bilinear,
//                Image.Format.RGBA8,
//                1.0f,
//                true));
//        tex[0].setEnabled(true);
//
//        tex[1] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
//        tex[1].setTexture(
//                TextureManager.loadTexture(
//                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "flare2.png"),
//                Texture.MinificationFilter.Trilinear,
//                Texture.MagnificationFilter.Bilinear));
//        tex[1].setEnabled(true);
//
//        tex[2] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
//        tex[2].setTexture(
//                TextureManager.loadTexture(
//                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "flare3.png"),
//                Texture.MinificationFilter.Trilinear,
//                Texture.MagnificationFilter.Bilinear));
//        tex[2].setEnabled(true);
//
//        tex[3] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
//        tex[3].setTexture(
//                TextureManager.loadTexture(
//                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "flare4.png"),
//                Texture.MinificationFilter.Trilinear,
//                Texture.MagnificationFilter.Bilinear));
//        tex[3].setEnabled(true);
//
//        flare = LensFlareFactory.createBasicLensFlare("flare", tex);
//
//        flare.setIntensity(0.5f);
//
//        flare.setModelBound(new BoundingBox());
//        flare.setCullHint(CullHint.Never);
//        flare.updateModelBound();
//
//        flare.setTriangleAccurateOcclusion(true);
//
//        // notice that it comes at the end
//        sun.attachChild(flare);
        attachChild(sun);
    }

    private float[] getPerez(float[][] distribution, float turbidity) {
        float[] perez = new float[5];
        perez[0] = distribution[0][0] * turbidity + distribution[0][1];
        perez[1] = distribution[1][0] * turbidity + distribution[1][1];
        perez[2] = distribution[2][0] * turbidity + distribution[2][1];
        perez[3] = distribution[3][0] * turbidity + distribution[3][1];
        perez[4] = distribution[4][0] * turbidity + distribution[4][1];
        return perez;
    }

    private float getZenith(float[][] zenithMatrix, float theta, float turbidity) {
        float theta2 = theta * theta;
        float theta3 = theta * theta2;

        return (zenithMatrix[0][0] * theta3 + zenithMatrix[0][1] * theta2 + zenithMatrix[0][2] * theta + zenithMatrix[0][3]) * turbidity * turbidity +
                (zenithMatrix[1][0] * theta3 + zenithMatrix[1][1] * theta2 + zenithMatrix[1][2] * theta + zenithMatrix[1][3]) * turbidity +
                (zenithMatrix[2][0] * theta3 + zenithMatrix[2][1] * theta2 + zenithMatrix[2][2] * theta + zenithMatrix[2][3]);
    }

    private float perezFunctionO1(float[] perezCoeffs, float thetaSun, float zenithValue) {
        float val = (1.0f + perezCoeffs[0] * FastMath2.exp(perezCoeffs[1])) *
                (1.0f + perezCoeffs[2] * FastMath2.exp(perezCoeffs[3] * thetaSun) + perezCoeffs[4] * FastMath2.sqr(FastMath2.cos(thetaSun)));
        return zenithValue / val;
    }

    private float perezFunctionO2(float[] perezCoeffs, float cosTheta, float gamma, float cosGamma2, float zenithValue) {
        return zenithValue * (1.0f + perezCoeffs[0] * FastMath2.exp(perezCoeffs[1] * cosTheta)) *
                (1.0f + perezCoeffs[2] * FastMath2.exp(perezCoeffs[3] * gamma) + perezCoeffs[4] * cosGamma2);
    }

    /**
     * clamp the value between min and max values
     */
    private float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }

    class ColorXYZ {

        private float x = 0.0f;
        private float y = 0.0f;
        private float z = 0.0f;
        private float r = 0.0f;
        private float g = 0.0f;
        private float b = 0.0f;
        private float a = 1.0f;
        private float hue = 0.0f;
        private float saturation = 0.0f;
        private float value = 0.0f;

        public ColorXYZ(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public float getValue() {
            return this.value;
        }

        public void clamp() {
            if (r < 0) {
                r = 0;
            }
            if (g < 0) {
                g = 0;
            }
            if (b < 0) {
                b = 0;
            }
            if (r > 1) {
                r = 1;
            }
            if (g > 1) {
                g = 1;
            }
            if (b > 1) {
                b = 1;
            }
        }

        public void setGammaCorrection(float gammaCorrection) {
            r = FastMath2.pow(r, gammaCorrection);
            g = FastMath2.pow(g, gammaCorrection);
            b = FastMath2.pow(b, gammaCorrection);
        }

        /**
         * Retorna o RGBA color
         */
        public ColorRGBA getRGBA() {
            return new ColorRGBA(r, g, b, a);
        }

        /**
         * Converte XYZ to RGB color
         */
        public void convertXYZtoRGB() {
            this.r = 3.240479f * x - 1.537150f * y - 0.498530f * z;
            this.g = -0.969256f * x + 1.875991f * y + 0.041556f * z;
            this.b = 0.055648f * x - 0.204043f * y + 1.057311f * z;
        }

        /**
         * Converte RGB to HSV
         */
        public void convertRGBtoHSV() {
            float minColor = Math.min(Math.min(r, g), b);
            float maxColor = Math.max(Math.max(r, g), b);
            float delta = maxColor - minColor;

            this.value = maxColor;                                              // Value
            if (!(FastMath2.abs(maxColor) < EPSILON)) {
                this.saturation = delta / maxColor;                             // Saturation
            } else {                                                            // r = g = b = 0
                this.saturation = 0.0f;                                         // Saturation = 0
                this.hue = -1;                                                  // Hue = undefined
                return;
            }

            if (FastMath2.abs(r - maxColor) < EPSILON) {
                this.hue = (g - b) / delta;
            } // between yellow & magenta
            else if (FastMath2.abs(g - maxColor) < EPSILON) {
                this.hue = 2.0f + (b - r) / delta;
            } // between cyan & yellow
            else {
                this.hue = 4.0f + (r - g) / delta;
            }                                // between magenta & cyan

            this.hue *= 60.0f;                                                  // degrees

            if (this.hue < 0.0f) {
                this.hue += 360.0f;
            }                                             // positive
        }

        /**
         * Converte HSV to RGB
         */
        public ColorXYZ convertHSVtoRGB() {
            if (FastMath2.abs(saturation) < EPSILON) {                           // achromatic (grey)
                this.r = value;
                this.g = value;
                this.b = value;
                this.a = value;
            }

            hue /= 60.0f;             // sector 0 to 5
            int sector = (int) FastMath2.floor(hue);

            float f = hue - sector;                                             // factorial part of hue
            float p = value * (1.0f - saturation);
            float q = value * (1.0f - saturation * f);
            float t = value * (1.0f - saturation * (1.0f - f));
            switch (sector) {
                case 0:
                    this.r = value;
                    this.g = t;
                    this.b = p;
                    break;
                case 1:
                    this.r = q;
                    this.g = value;
                    this.b = p;
                    break;
                case 2:
                    this.r = p;
                    this.g = value;
                    this.b = t;
                    break;
                case 3:
                    this.r = p;
                    this.g = q;
                    this.b = value;
                    break;
                case 4:
                    this.r = t;
                    this.g = p;
                    this.b = value;
                    break;
                default:                                                        // case 5:
                    this.r = value;
                    this.g = p;
                    this.b = q;
                    break;
            }
            return this;
        }
    }
}