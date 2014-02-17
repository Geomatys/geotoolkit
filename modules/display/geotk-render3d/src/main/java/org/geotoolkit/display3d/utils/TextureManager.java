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

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureData;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLProfile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Rouby (Geomatys))
 */
public class TextureManager {

//    private int index = 0;
//    private int counter = 0;

    private boolean isUpdate = false;

    private final List<Object> users = new ArrayList<>();

    private final TextureData textureData;
    private final Texture texture;

    public static TextureManager create(int index, GLProfile profile, File file, boolean bool, String string) throws IOException {
        final TextureData textureData = TextureIO.newTextureData(profile, file, bool, string);
        if (textureData == null) {
            return null;
        } else {
            return new TextureManager(index, textureData);
        }
    }

    public static TextureManager create(int index, GLProfile profile, InputStream stream, boolean bool, String string) throws IOException {
        final TextureData textureData = TextureIO.newTextureData(profile, stream, bool, string);
        if (textureData == null) {
            return null;
        } else {
            return new TextureManager(index, textureData);
        }
    }

    public static TextureManager create(int index, GLProfile profile, URL url, boolean bool, String string) throws IOException {
        final TextureData textureData = TextureIO.newTextureData(profile, url, bool, string);
        if (textureData == null) {
            return null;
        } else {
            return new TextureManager(index, textureData);
        }
    }

    public static TextureManager create(int index, GLProfile profile, File file, int internalFormat, int pixelFormat, boolean bool, String string) throws IOException {
        final TextureData textureData = TextureIO.newTextureData(profile, file, internalFormat, pixelFormat, bool, string);
        if (textureData == null) {
            return null;
        } else {
            return new TextureManager(index, textureData);
        }
    }

    public static TextureManager create(int index, GLProfile profile, InputStream stream, int internalFormat, int pixelFormat, boolean bool, String string) throws IOException {
        final TextureData textureData = TextureIO.newTextureData(profile, stream, internalFormat, pixelFormat, bool, string);
        if (textureData == null) {
            return null;
        } else {
            return new TextureManager(index, textureData);
        }
    }

    public static TextureManager create(int index, GLProfile profile, URL url, int internalFormat, int pixelFormat, boolean bool, String string) throws IOException {
        final TextureData textureData = TextureIO.newTextureData(profile, url, internalFormat, pixelFormat, bool, string);
        if (textureData == null) {
            return null;
        } else {
            return new TextureManager(index, textureData);
        }
    }

    public static TextureManager create(int index, GLProfile glp, int internalFormat, int pixelFormat, boolean mipmap, BufferedImage image) {
        final TextureData textureData = new AWTTextureData(glp, internalFormat, pixelFormat, mipmap, image);
        return new TextureManager(index,textureData);
    }

    public TextureManager(int index, TextureData textureData) {
        this.textureData = textureData;
        this.texture = TextureIO.newTexture(index);
    }

    public boolean updateTexture(GLAutoDrawable glAutoDrawable) {
        return updateTexture(glAutoDrawable, false);
    }

    public boolean updateTexture(GLAutoDrawable glAutoDrawable, boolean forced) {
        final GL gl = glAutoDrawable.getGL();

        if (textureData != null && (!isUpdate || forced)) {
            texture.updateImage(gl, textureData);
            isUpdate = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void enable(GL gl){
        texture.enable(gl);
    }

    public void bind(GL gl) {
        texture.bind(gl);
    }

    public void disable(GL gl) {
        texture.disable(gl);
    }

    public void destroy(GL gl) {
        texture.destroy(gl);
    }

    public Texture getTexture() {
        return this.texture;
    }

    public int getTextureIndex(){
        return this.texture.getTarget();
    }

    public TextureData getTextureData() {
        return this.textureData;
    }

    public boolean addUser(Object user){
        return this.users.add(user);
    }

    public boolean removeUser(Object user) {
        return this.users.remove(user);
    }

    public int countUser(){
        return this.users.size();
    }

    public boolean isUsed(){
        return this.users.size() > 0;
    }
}
