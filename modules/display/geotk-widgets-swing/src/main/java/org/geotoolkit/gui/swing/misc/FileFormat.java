/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Johann Sorel
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
package org.geotoolkit.gui.swing.misc;

import java.io.File;
import java.lang.ref.WeakReference;
import javax.swing.filechooser.FileFilter;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.util.InternationalString;

/**
 * File filter enumeration.
 *  
 * @author Johann Sorel
 * @module pending
 */
public enum FileFormat {

    ACCESS_DATABASE("filter_access", "mdb"),
    AUTOCAD_DWG("filter_dwg", "dwg"),
    COMMA_SEPARATED_VALUES("filter_csv", "csv"),
    ERDAS_IMAGE("filter_erdas_img", "img"),
    ERMAPPER_COMPRESSED_WAVELETS("filter_ecw", "ecw"),
    ESRI_SHAPEFILE("filter_shapefile", "shp"),
    ESRI_ASCII_GRID("filter_asc", "asc"),
    GEOTIFF("filter_geotiff", "tif", "tiff"),
    GEOGRAPHY_MARKUP_LANGUAGE("filter_gml", "gml"),
    KEYHOLE_MARKUP_LANGUAGE("filter_kml", "kml"),
    KEYHOLE_MARKUP_LANGUAGE_ZIPPED("filter_kmz", "kmz"),
    JOINT_PHOTOGRAPHIC_EXPERTS_GROUP("filter_jpg", "jpg", "jpeg", "gif"),
    JPEG_2000("filter_jpg2", "jp2", "j2k"),
    MAPINFO_EXCHANGE("filter_mif", "mif"),
    MAPINFO_TAB("filter_tab", "tab"),
    MICROSTATION_DGN("filter_microstation_dgn", "dgn"),
    PORTABLE_NETWORK_GRAPHICS("filter_png", "png"),
    SCALABLE_VECTOR_GRAPHICS("filter_svg", "svg"),
    STYLE_LAYER_DESCRIPTOR("filter_sld", "sld"),
    TIFF("filter_tiff", "tif", "tiff"),
    VISUALDEM("filter_vdem", "dem"),
    WEBMAPCONTEXT("filter_wmc", "wmc"),
    WORLD_IMAGE("filter_world_image", "jpg", "jpeg", "bmp", "png");
    private final InternationalString desc;
    private final String[] ends;
    private WeakReference<FileFilter> ref;

    FileFormat(String i18n, String... ends) {
        this.desc = MessageBundle.getI18NString(i18n);
        this.ends = new String[ends.length];
        String dot = ".";
        for (int i = 0; i < ends.length; i++) {
            this.ends[i] = dot + ends[i];
        }
    }

    public InternationalString getDescription() {
        return desc;
    }

    public String[] getFileEnds() {
        return ends.clone();
    }

    public synchronized FileFilter getFileFilter() {
        FileFilter ff = null;
        if (ref != null) {
            ff = ref.get();
        }

        if (ff == null) {
            ff = new SimpleFileFilter(this);
            ref = new WeakReference<FileFilter>(ff);
        }

        return ff;
    }

    private static final class SimpleFileFilter extends FileFilter {

        private final FileFormat format;
        private final String desc;

        private SimpleFileFilter(FileFormat format) {
            this.format = format;

            final StringBuilder buff = new StringBuilder();
            buff.append(format.getDescription().toString());
            buff.append(" (");
            final String[] ends = format.getFileEnds();
            buff.append('*').append(ends[0]);
            for (int i = 1; i < ends.length; i++) {
                buff.append(",*").append(ends[i]);
            }
            buff.append(')');
            desc = buff.toString();
        }

        @Override
        public String getDescription() {
            return desc;
        }

        @Override
        public boolean accept(File pathname) {
            final String[] ends = format.getFileEnds();
            final String nom = pathname.getName();

            if (pathname.isDirectory()) {
                return true;
            }

            for (int i = 0, n = ends.length; i < n; i++) {
                if (nom.toLowerCase().endsWith(ends[i])) {
                    return true;
                }
            }

            return false;
        }
    }
}
