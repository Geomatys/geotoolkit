/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2011, Johann Sorel
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
package org.geotoolkit.gui.swing.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * IconBundle, manage icons and avoid double loading
 * 
 * @author Johann Sorel
 * @module pending
 */
public final class IconBundle {

    public static final ImageIcon EMPTY_ICON = new ImageIcon(IconBundle.class.getResource("/org/geotoolkit/gui/swing/resource/icon/blanc.png"));
    public static final ImageIcon EMPTY_ICON_16 = new ImageIcon(IconBundle.class.getResource("/org/geotoolkit/gui/swing/resource/icon/blanc16.png"));

    //TODO not thread safe, fix that
    private static final List<ResourceBundle> BUNDLES = new ArrayList<ResourceBundle>();
    private static final WeakHashMap<String, ImageIcon> CACHE = new WeakHashMap<String, ImageIcon>();

    static {
        BUNDLES.add( ResourceBundle.getBundle("org/geotoolkit/gui/swing/resource/IconBundle") );
    }

    private IconBundle() {}

    private static URL getURL(String adress, Class base) {

        if(base == null){
            base = IconBundle.class;
        }

        if (adress.startsWith("/")) {
            adress = adress.substring(1);
        }

        URL url = base.getClassLoader().getResource(adress);

        if (url == null) {
            url = base.getClassLoader().getResource("/" + adress);
        }

        if (url == null) {
            url = base.getResource(adress);
        }

        if (url == null) {
            url = base.getResource("/" + adress);
        }

        return url;
    }

    private static ImageIcon getIcon(final URL url) {
        if (url == null) {
            return EMPTY_ICON;
        } else {
            return new ImageIcon(url);
        }
    }

    public static BufferedImage getBuffer(final String key) throws IOException{
       return ImageIO.read(getURL(getValue(key), IconBundle.class));
    }

    /**
     * get the ImageIcon matching the key String.
     * return a blanc imageIcon size 1x1 if the key isn't found.
     * @param key
     * @return ImageIcon
     */
    public static ImageIcon getIcon(final String key) {
        return getIcon(key, IconBundle.class);
    }

    public static ImageIcon getIcon(final String key, final Class base) {

        if (key == null) {
            return EMPTY_ICON;
        }
        ImageIcon icon = null;

        if (CACHE.containsKey(key)) {
            icon = CACHE.get(key);
        } else {
            String adress = getValue(key);

            if (adress != null) {
                icon = getIcon(getURL(adress, base));
                CACHE.put(key, icon);
            }
        }

        if (icon == null) {
            icon = EMPTY_ICON;
        }

        return icon;
    }

    private static String getValue(final String key) {

        for(int i = BUNDLES.size()-1; i>=0; i--){
            ResourceBundle bundle = BUNDLES.get(i);
            
            if (existe(bundle, key)) {
                String adress = bundle.getString(key);
                if (adress.startsWith("$")) {
                    return getValue(adress.substring(1));
                } else {
                    return adress;
                }
            }
        }

        return null;
    }

    private static boolean existe(final ResourceBundle bundle, final String key) {

        Enumeration<String> keys = bundle.getKeys();

        while (keys.hasMoreElements()) {
            if (key.equals(keys.nextElement())) {
                return true;
            }
        }

        return false;
    }

    /**
     * use your own icon bundle, property file should look like defaultset.properties.
     * if icon is missing the defautltset icon will be used
     * @param bundle ResourceBundle
     */
    public static void addBundle(final ResourceBundle bundle) {
        BUNDLES.add(bundle);
    }

}
