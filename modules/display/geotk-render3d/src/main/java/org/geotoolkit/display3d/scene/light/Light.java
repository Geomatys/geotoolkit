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

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;

/**
 * @author Thomas Rouby (Geomatys))
 */
public class Light {

    // GL_LIGHT0 <= light < GL_MAX_LIGHTS
    private final int light;
    // GL_AMBIENT params value
    private FloatBuffer ambient;
    // GL_DIFFUSE params value
    private FloatBuffer diffuse;
    // GL_SPECULAR params value
    private FloatBuffer specular;
    // GL_POSITION params value
    private FloatBuffer position;
    // GL_SPOT_DIRECTION params value
    private FloatBuffer spot_direction;
    // GL_SPOT_EXPONENT params value
    private float spot_exponent = Float.NaN;
    // GL_SPOT_CUTOFF params value
    private float spot_cutoff = Float.NaN;
    // GL_CONSTANT_ATTENUATION params value
    private float constant_attenuation = Float.NaN;
    // GL_LINEAR_ATTENUATION params value
    private float linear_attenuation = Float.NaN;
    // GL_QUADRATIC_ATTENUATION params value
    private float quadratic_attenuation = Float.NaN;

    /**
     * @param light Specifies a light.
     *              The number of lights depends on the implementation, but at least eight lights are supported.
     *              They are identified by symbolic names of the form GL_LIGHT i, where i ranges from 0 to the value of GL_MAX_LIGHTS - 1.
     * @link http://www.opengl.org/sdk/docs/man2/xhtml/glLight.xml
     */
    public Light(int light) {
        this.light = light;
    }

    /**
     * GL_AMBIENT params value
     *
     * @param ambient contains floating-point values that specify the ambient RGBA intensity of the light.
     *                The initial opengl ambient light intensity is (0, 0, 0, 1).
     */
    public void setAmbient(float[] ambient) {
        this.ambient = Buffers.newDirectFloatBuffer(ambient);
    }

    /**
     * GL_DIFFUSE params value
     *
     * @param diffuse contains floating-point values that specify the diffuse RGBA intensity of the light.
     *                The initial value for GL_LIGHT0 is (1, 1, 1, 1), for other lights, the initial value is (0, 0, 0, 1).
     */
    public void setDiffuse(float[] diffuse) {
        this.diffuse = Buffers.newDirectFloatBuffer(diffuse);
    }

    /**
     * GL_SPECULAR params value
     *
     * @param specular contains four floating-point values that specify the specular RGBA intensity of the light.
     *                 The initial value for GL_LIGHT0 is (1, 1, 1, 1), for other lights, the initial value is (0, 0, 0, 1).
     */
    public void setSpecular(float[] specular) {
        this.specular = Buffers.newDirectFloatBuffer(specular);
    }

    /**
     * GL_POSITION params value
     *
     * @param position     contains three floating-point values that specify the position of the light in homogeneous object coordinates.
     *                     <p/>
     *                     The position is transformed by the modelview matrix when glLight is called (just as if it were a point), and it is stored in eye coordinates.
     *                     If the w component of the position is 0, the light is treated as a directional source.
     *                     Diffuse and specular lighting calculations take the light's direction, but not its actual position, into account, and attenuation is disabled.
     *                     Otherwise, diffuse and specular lighting calculations are based on the actual location of the light in eye coordinates, and attenuation is enabled.
     *                     The initial position is (0, 0, 1, 0);
     *                     thus, the initial light source is directional, parallel to, and in the direction of the -z axis.
     * @param directional if true, the light is directional, else positional
     */
    public void setPosition(float[] position, boolean directional) {
        this.position = Buffers.newDirectFloatBuffer(new float[]{position[0], position[1], position[2], (directional) ? (0.0f) : (1.0f)});
    }

    /**
     * GL_POSITION params value
     *
     * normalize position before set to buffer.
     *
     * @param position
     * @param directional
     */
    public void setPosition(Vector3f position, boolean directional) {
        final Vector3f pos = new Vector3f();
        pos.normalize(position);
        this.setPosition(new float[]{pos.x, pos.y, pos.z}, directional);
    }

    /**
     * GL_SPOT_DIRECTION params value
     *
     * @param spot_direction contains three floating-point values that specify the direction of the light in homogeneous object coordinates.
     *                       <p/>
     *                       The spot direction is transformed by the upper 3x3 of the modelview matrix
     *                       when glLight is called, and it is stored in eye coordinates.
     *                       It is significant only when GL_SPOT_CUTOFF is not 180, which it is initially.
     *                       The initial direction is (0,0,-1).
     */
    public void setSpotDirection(float[] spot_direction) {
        this.spot_direction = Buffers.newDirectFloatBuffer(spot_direction);
    }

    /**
     * GL_SPOT_EXPONENT params value
     *
     * @param spot_exponent is a single floating-point value that specifies the intensity distribution of the light.
     *                      Only values in the range [0,128] are accepted.
     *                      <p/>
     *                      Effective light intensity is attenuated by the cosine of the angle between
     *                      the direction of the light and the direction from the light to the vertex
     *                      being lighted, raised to the power of the spot exponent.
     *                      Thus, higher spot exponents result in a more focused light source,
     *                      regardless of the spot cutoff angle (see GL_SPOT_CUTOFF, next paragraph).
     *                      The initial spot exponent is 0, resulting in uniform light distribution.
     */
    public void setSpotExponent(float spot_exponent) {
        this.spot_exponent = spot_exponent;
    }

    /**
     * GL_SPOT_CUTOFF params value
     *
     * @param spot_cutoff is a single floating-point value that specifies the maximum spread angle of a light source.
     *                    Only values in the range [0,90] and the special value 180 are accepted.
     *                    If the angle between the direction of the light and the direction from the
     *                    light to the vertex being lighted is greater than the spot cutoff angle,
     *                    the light is completely masked.
     *                    Otherwise, its intensity is controlled by the spot exponent and the attenuation factors.
     *                    The initial spot cutoff is 180, resulting in uniform light distribution.
     */
    public void setSpotCutoff(float spot_cutoff) {
        this.spot_cutoff = spot_cutoff;
    }

    /**
     * GL_CONSTANT_ATTENUATION params value
     *
     * @param constant_attenuation is a single floating-point value that specifies one of the three light attenuation factors.
     *                             Only nonnegative values are accepted.
     *                             If the light is positional, rather than directional, its intensity is attenuated by the reciprocal
     *                             of the sum of the constant factor, the linear factor times the distance between the light
     *                             and the vertex being lighted, and the quadratic factor times the square of the same distance.
     *                             The initial attenuation factors are (1, 0, 0), resulting in no attenuation.
     */
    public void setConstantAttenuation(float constant_attenuation) {
        this.constant_attenuation = constant_attenuation;
    }

    /**
     * GL_LINEAR_ATTENUATION params value
     *
     * @param linear_attenuation is a single floating-point value that specifies one of the three light attenuation factors.
     *                           Only nonnegative values are accepted.
     *                           If the light is positional, rather than directional, its intensity is attenuated by the reciprocal
     *                           of the sum of the constant factor, the linear factor times the distance between the light
     *                           and the vertex being lighted, and the quadratic factor times the square of the same distance.
     *                           The initial attenuation factors are (1, 0, 0), resulting in no attenuation.
     */
    public void setLinearAttenuation(float linear_attenuation) {
        this.linear_attenuation = linear_attenuation;
    }

    /**
     * GL_QUADRATIC_ATTENUATION params value
     *
     * @param quadratic_attenuation is a single floating-point value that specifies one of the three light attenuation factors.
     *                              Only nonnegative values are accepted.
     *                              If the light is positional, rather than directional, its intensity is attenuated by the reciprocal
     *                              of the sum of the constant factor, the linear factor times the distance between the light
     *                              and the vertex being lighted, and the quadratic factor times the square of the same distance.
     *                              The initial attenuation factors are (1, 0, 0), resulting in no attenuation.
     */
    public void setQuadraticAttenuation(float quadratic_attenuation) {
        this.quadratic_attenuation = quadratic_attenuation;
    }

    /**
     * Call of glLight on set values
     *
     * @param glDrawable the glDrawable content the gl where to call glLight methods
     */
    public void update(GLAutoDrawable glDrawable) {
        final GL gl = glDrawable.getGL();
        if (gl instanceof GL2) {
            final GL2 gl2 = gl.getGL2();

            if (this.ambient != null)
                gl2.glLightfv(this.light, GL2.GL_AMBIENT, this.ambient);

            if (this.diffuse != null)
                gl2.glLightfv(this.light, GL2.GL_DIFFUSE, this.diffuse);

            if (this.specular != null)
                gl2.glLightfv(this.light, GL2.GL_SPECULAR, this.specular);

            if (this.position != null)
                gl2.glLightfv(this.light, GL2.GL_POSITION, this.position);

            if (this.spot_direction != null)
                gl2.glLightfv(this.light, GL2.GL_SPOT_DIRECTION, this.spot_direction);

            if (!Float.isNaN(this.spot_exponent))
                gl2.glLightf(this.light, GL2.GL_SPOT_EXPONENT, this.spot_exponent);

            if (!Float.isNaN(this.spot_cutoff))
                gl2.glLightf(this.light, GL2.GL_SPOT_CUTOFF, this.spot_cutoff);

            if (!Float.isNaN(this.constant_attenuation))
                gl2.glLightf(this.light, GL2.GL_CONSTANT_ATTENUATION, this.constant_attenuation);

            if (!Float.isNaN(this.linear_attenuation))
                gl2.glLightf(this.light, GL2.GL_LINEAR_ATTENUATION, this.linear_attenuation);

            if (!Float.isNaN(this.quadratic_attenuation))
                gl2.glLightf(this.light, GL2.GL_QUADRATIC_ATTENUATION, this.quadratic_attenuation);
        }
    }
}
