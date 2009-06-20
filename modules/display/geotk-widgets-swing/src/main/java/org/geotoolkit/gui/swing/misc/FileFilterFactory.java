/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
import javax.swing.filechooser.FileFilter;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.util.InternationalString;

/**
 * file filter factory
 *  
 * @author Johann Sorel
 */
public class FileFilterFactory {

    public static FileFilter FolderFilter = new FileFilter(){
    
    @Override
    public boolean accept(File fichier) {        
        return fichier.isDirectory();
    }
    
    @Override
    public String getDescription() {
        return MessageBundle.getString("filter_folder");
    }
    
};
    
    public static enum FORMAT {

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
        JOINT_PHOTOGRAPHIC_EXPERTS_GROUP("filter_jpg", "jpg", "jpeg","gif"),
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
        final InternationalString desc;
        final String[] ends;

        FORMAT(String i18n, String... ends) {
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
    }

    
    public static FileFilter createFileFilter(final FORMAT format) {

        if (format == null) {
            throw new NullPointerException();
        }

        FileFilter ff = new FileFilter() {

            private String desc;

            {
                StringBuffer buff = new StringBuffer();
                
                buff.append(format.getDescription().toString());
                buff.append(" (");
                
                String[] ends = format.getFileEnds();
                
                buff.append('*');
                buff.append(ends[0]);
                
                for(int i=1;i<ends.length;i++){
                    String end = ends[i];
                    buff.append(',');
                    buff.append('*');
                    buff.append(end);
                }
                buff.append(')');
                
                desc = buff.toString();
            }

            @Override
            public boolean accept(File pathname) {
                String[] ends = format.getFileEnds();

                String nom = pathname.getName();

                if (pathname.isDirectory()) {
                    return true;
                }

                for (int i = 0,  n = ends.length; i < n; i++) {
                    if (nom.toLowerCase().endsWith(ends[i])) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public String getDescription() {
                return desc;
            }
        };


        return ff;
    }
}
