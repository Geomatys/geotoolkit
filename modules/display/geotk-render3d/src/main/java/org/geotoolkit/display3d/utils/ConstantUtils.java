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

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

/**
 * @author Thomas Rouby (Geomatys)
 */
public final class ConstantUtils {

    public static final String SHADER_UNIFORM_MODE = "mode";                      // float
    public static final float SHADER_UNIFORM_MODE_TILES = 1.0f;
    public static final float SHADER_UNIFORM_MODE_LINES = 2.0f;

    public static final String SHADER_UNIFORM_CAMERA_MATRIX = "cameraMatrix";     // mat4[]

    public static final String SHADER_UNIFORM_CAMERA_EYE = "cameraEye";           // vec3
    public static final String SHADER_UNIFORM_CAMERA_CENTER = "cameraCenter";     // vec3
    public static final String SHADER_UNIFORM_CAMERA_UP = "cameraUp";             // vec3

    public static final String SHADER_UNIFORM_CAMERA_FOVY = "cameraFovy";         // float
    public static final String SHADER_UNIFORM_CAMERA_ASPECT = "cameraAspect";     // float
    public static final String SHADER_UNIFORM_CAMERA_NEAR = "cameraNear";         // float
    public static final String SHADER_UNIFORM_CAMERA_FAR = "cameraFar";           // float

    public static final String SHADER_UNIFORM_CAMERA_POSITION = "cameraPosition"; // vec3
    public static final String SHADER_UNIFORM_CAMERA_ROTATEX = "rotateX";         // float
    public static final String SHADER_UNIFORM_CAMERA_ROTATEY = "rotateY";         // float
    public static final String SHADER_UNIFORM_CAMERA_ROTATEZ = "rotateZ";         // float

    public static final String SHADER_UNIFORM_SCALE3D = "mult_vertices";          // vec3

    public static final String SHADER_UNIFORM_TEXTURE_0 = "texture0";             // sampler2D textureMNT
    public static final String SHADER_UNIFORM_TEXTURE_1 = "texture1";             // sampler2D textureImg

    public static final String SHADER_ATTRIBUTES_VERTICES = "vertices";           // vec3[]
    public static final String SHADER_ATTRIBUTES_UVS = "uVs";                     // vec2[]

    private ConstantUtils(){}

    public static ShaderProgram createShaderProgram(GL2 gl, ShaderCode vertex, ShaderCode fragment){
        final ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.add(gl, vertex, System.err);
        shaderProgram.add(gl, fragment, System.err);
        return shaderProgram;
    }

}
