/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.renderer.style;

import org.geotoolkit.font.IconBuilder;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lookup and caches font definitions for faster retrieval
 *
 * @author Andrea Aime - TOPP
 *
 * @module
 */
public class FontCache {

    /** The logger for the rendering module. */
    static final Logger LOGGER = Logger.getLogger("org.geotoolkit.renderer.style");

    private static FontCache defaultInstance;

    /** Set containing the font families known of this machine */
    private Set<String> fontFamilies;

    /** Fonts already loaded */
    private final Map<String, Font> loadedFonts = new ConcurrentHashMap<>();

    /**
     * @return the default, system wide font cache
     */
    public static synchronized FontCache getDefaultInsance() {
        if (defaultInstance == null) {
            defaultInstance = new FontCache();
            IconBuilder.FONT.ifPresent(font -> {
                // HACK for JMDA-361 : Font awesome object naming differs from received one. TODO: find a better strategy
                defaultInstance.loadedFonts.put(font.getFamily(), font);
                defaultInstance.loadedFonts.put(font.getPSName(), font);
                defaultInstance.loadedFonts.put(font.getName(), font);
                defaultInstance.loadedFonts.put("FontAwesome", font);
                defaultInstance.loadedFonts.put("Font Awesome", font);
            });
        }
        return defaultInstance;
    }

    public Font getFont(final String requestedFont) {
        // see if the font has already been loaded
        LOGGER.log(Level.FINEST, "trying to load {0}", requestedFont);

        Font result = loadedFonts.get(requestedFont);
        if (result != null) {
            return loadedFonts.get(requestedFont);
        }

        LOGGER.finest("not already loaded");

        // if not, try to load from the java runtime or as an URL. We start by checking system font families are loaded.
        synchronized (this) {
            if (fontFamilies == null) {
                final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                fontFamilies = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ge.getAvailableFontFamilyNames())));

                LOGGER.log(Level.FINEST, "there are {0} fonts available", fontFamilies.size());
            }
        }

        if (fontFamilies.contains(requestedFont)) {
            result = new Font(requestedFont, Font.PLAIN, 12);
        } else {
            LOGGER.finest("not a system font");
            result = loadFromUrl(requestedFont);
        }

        // log the result and exit
        if(result == null) {
            LOGGER.log(Level.FINE, "Could not load font {0}", requestedFont);
        } else {
            loadedFonts.put(requestedFont, result);
        }
        return result;
    }

    /**
     * Tries to load the specified font name as a URL
     */
    Font loadFromUrl(final String fontUrl) {
        // may be its a file or url
        InputStream is = null;

        if (fontUrl.startsWith("http") || fontUrl.startsWith("file:") || fontUrl.startsWith("jar:")) {
            try {
                URL url = new URL(fontUrl);
                is = url.openStream();
            } catch (MalformedURLException mue) {
                // this may be ok - but we should mention it
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Bad url in SLDStyleFactory " + fontUrl + "\n" + mue);
                }
            } catch (IOException ioe) {
                // we'll ignore this for the moment
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("IO error in SLDStyleFactory " + fontUrl + "\n" + ioe);
                }
            }
        } else {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("not a URL");
            }

            File file = new File(fontUrl);

            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException fne) {
                // this may be ok - but we should mention it
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Bad file name in SLDStyleFactory" + fontUrl + "\n" + fne);
                }
            }
        }

        // make sure we have anything to load
        if (is == null) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("null input stream, could not load the font");
            }
            return null;
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("about to load");
        }

        try {
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException ffe) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Font format error in SLDStyleFactory " + fontUrl + "\n" + ffe);
            }
            return null;
        } catch (IOException ioe) {
            // we'll ignore this for the moment
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("IO error in SLDStyleFactory " + fontUrl + "\n" + ioe);
            }
            return null;
        }
    }

}
