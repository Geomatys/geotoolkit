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
package org.geotoolkit.gui.swing.util;

import java.io.File;
import java.lang.ref.WeakReference;
import javax.swing.filechooser.FileFilter;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.util.InternationalString;

/**
 * File filter enumeration.
 *
 * @author Johann Sorel
 * @module
 */
public enum FileFormat {

    ACCESS_DATABASE(MessageBundle.Keys.filter_access, "mdb"),
    AUTOCAD_DWG(MessageBundle.Keys.filter_dwg, "dwg"),
    COMMA_SEPARATED_VALUES(MessageBundle.Keys.filter_csv, "csv"),
    DIMAP(MessageBundle.Keys.filter_dimap, "tif", "tiff"),
    ERDAS_IMAGE(MessageBundle.Keys.filter_erdas_img, "img"),
    ERMAPPER_COMPRESSED_WAVELETS(MessageBundle.Keys.filter_ecw, "ecw"),
    ESRI_SHAPEFILE(MessageBundle.Keys.filter_shapefile, "shp"),
    ESRI_ASCII_GRID(MessageBundle.Keys.filter_asc, "asc"),
    GEOTIFF(MessageBundle.Keys.filter_geotiff, "tif", "tiff"),
    GEOGRAPHY_MARKUP_LANGUAGE(MessageBundle.Keys.filter_gml, "gml"),
    KEYHOLE_MARKUP_LANGUAGE(MessageBundle.Keys.filter_kml, "kml"),
    KEYHOLE_MARKUP_LANGUAGE_ZIPPED(MessageBundle.Keys.filter_kmz, "kmz"),
    JOINT_PHOTOGRAPHIC_EXPERTS_GROUP(MessageBundle.Keys.filter_jpg, "jpg", "jpeg", "gif"),
    JPEG_2000(MessageBundle.Keys.filter_jpg2, "jp2", "j2k"),
    MAPINFO_EXCHANGE(MessageBundle.Keys.filter_mif, "mif"),
    MAPINFO_TAB(MessageBundle.Keys.filter_tab, "tab"),
    MICROSTATION_DGN(MessageBundle.Keys.filter_microstation_dgn, "dgn"),
    NETCDF_GRIB(MessageBundle.Keys.filter_netcdfgrib, "nc", "ncml", "cdf", "grib","grib1","grib2","grb","grb1","grb2","grd"),
    NMEA(MessageBundle.Keys.filter_nmea, "txt", "log"),
    PDF(MessageBundle.Keys.filter_pdf, "pdf"),
    PORTABLE_NETWORK_GRAPHICS(MessageBundle.Keys.filter_png, "png"),
    SCALABLE_VECTOR_GRAPHICS(MessageBundle.Keys.filter_svg, "svg"),
    STYLE_LAYER_DESCRIPTOR(MessageBundle.Keys.filter_sld, "sld"),
    TIFF(MessageBundle.Keys.filter_tiff, "tif", "tiff"),
    VISUALDEM(MessageBundle.Keys.filter_vdem, "dem"),
    WEBMAPCONTEXT(MessageBundle.Keys.filter_wmc, "wmc"),
    S57(MessageBundle.Keys.filter_s57, "000"),
    WORLD_IMAGE(MessageBundle.Keys.filter_world_image, "jpg", "jpeg", "bmp", "png");

    private final InternationalString desc;
    private final String[] ends;
    private WeakReference<FileFilter> ref;

    FileFormat(final short i18n, final String... ends) {
        this.desc = MessageBundle.formatInternational(i18n);
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
            ref = new WeakReference<>(ff);
        }

        return ff;
    }

    private static final class SimpleFileFilter extends FileFilter {

        private final FileFormat format;
        private final String desc;

        private SimpleFileFilter(final FileFormat format) {
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
        public boolean accept(final File pathname) {
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
