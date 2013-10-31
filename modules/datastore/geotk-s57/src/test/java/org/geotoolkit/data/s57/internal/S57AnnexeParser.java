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
package org.geotoolkit.data.s57.internal;

import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.geotoolkit.data.s57.S57Constants;
import org.geotoolkit.data.s57.S57FeatureStore;
import org.geotoolkit.data.s57.TypeBanks;
import org.geotoolkit.data.s57.annexe.S57FeatureType;
import org.geotoolkit.data.s57.annexe.S57PropertyType;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;

/**
 * Used only to rebuild types from S-57 annexes.
 * S-57_AppendixA_Chapter1_31ApAch1.pdf
 * S-57_AppendixA_Chapter2_31ApAch2.pdf
 *
 * @author Johann Sorel (Geomatys)
 */
public final class S57AnnexeParser {

    public final Map<String,S57FeatureType> s57ftypes;
    public final Map<String,S57PropertyType> s57ptypes;
    public final Map<String, FeatureType> featureTypes = new HashMap<>();

    public static void main(String[] args) throws IOException {

        args = new String[]{
            "/home/jsorel/TRAVAIL/1_Specification/IHO/S-57_AppendixA_Chapter1_31ApAch1.pdf",
            "/home/jsorel/TRAVAIL/1_Specification/IHO/S-57_AppendixA_Chapter2_31ApAch2.pdf"
        };
        S57AnnexeParser parser = new S57AnnexeParser(args[0], args[1]);

        System.out.println(parser.s57ftypes.size());
        System.out.println(parser.s57ptypes.size());

        for(S57FeatureType sft : parser.s57ftypes.values()){
            System.out.println(sft.toFormattedString());
            sft.fromFormattedString(sft.toFormattedString());
        }

        System.out.println("---------------------------------");
        for(S57PropertyType sft : parser.s57ptypes.values()){
            System.out.println(sft.toFormattedString());
            sft.fromFormattedString(sft.toFormattedString());
        }

    }

    public S57AnnexeParser() throws IOException {
        this(
            "/home/jsorel/TRAVAIL/1_Specification/IHO/S-57_AppendixA_Chapter1_31ApAch1.pdf",
            "/home/jsorel/TRAVAIL/1_Specification/IHO/S-57_AppendixA_Chapter2_31ApAch2.pdf");
    }

    public S57AnnexeParser(String an1, String an2) throws IOException {
        this(an1,17,-1,
             an2,10,-1);

    }

    public S57AnnexeParser(String an1, int startPage1, int endPage1, String an2, int startPage2, int endPage2) throws IOException {
        s57ftypes = parseFeatureTypes(an1,startPage1,endPage1);
        s57ptypes = parsePropertyTypes(an2,startPage2,endPage2);

    }

    public FeatureType getFeatureType(String name){
        FeatureType ft = featureTypes.get(name);
        if(ft!=null) return ft;

        final S57FeatureType sft = s57ftypes.get(name);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(sft.acronym);
        //add a geometry type
        ftb.add(S57Constants.PROPERTY_GEOMETRY, Geometry.class, DefaultGeographicCRS.WGS84);
        //vector properties
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        adb.setName(S57Constants.PROPERTY_VECTORS);
        adb.setType(TypeBanks.getVectorType(DefaultGeographicCRS.WGS84));
        adb.setMinOccurs(0);
        adb.setMaxOccurs(Integer.MAX_VALUE);
        ftb.add(adb.buildDescriptor());

        final List<String> allAtts = new ArrayList<String>();
        allAtts.addAll(sft.attA);
        allAtts.addAll(sft.attB);
        allAtts.addAll(sft.attC);

        for(String att : allAtts){
            final S57PropertyType pt = s57ptypes.get(att);
            Class binding;
            if("E".equalsIgnoreCase(pt.type)){
                //enumeration type
                binding = String.class;
            }else if("L".equalsIgnoreCase(pt.type)){
                //enumaration list
                binding = String.class;
            }else if("F".equalsIgnoreCase(pt.type)){
                //float
                binding = Double.class;
            }else if("I".equalsIgnoreCase(pt.type)){
                //integer
                binding = Integer.class;
            }else if("A".equalsIgnoreCase(pt.type)){
                //code string
                binding = String.class;
            }else if("S".equalsIgnoreCase(pt.type)){
                // free text
                binding = String.class;
            }else{
                throw new RuntimeException("unknowned property type : "+pt.type);
            }
            AttributeDescriptor desc = ftb.add(pt.acronym, binding);
            desc.getUserData().put(S57FeatureStore.S57TYPECODE, pt.code);
        }

        ft = ftb.buildFeatureType();
        featureTypes.put(ft.getName().getLocalPart(), ft);

        return ft;
    }

    public FeatureType getFeatureType(int code){
        for(S57FeatureType sft : s57ftypes.values()){
            if(sft.code == code){
                return getFeatureType(sft.acronym);
            }
        }
        return null;
    }


    private static Map<String,S57FeatureType> parseFeatureTypes(String path, int startPage, int endPage) throws IOException {
        final Map<String,S57FeatureType> featureTypes = new HashMap<>();

        final PdfReader reader = new PdfReader(path);
        final int nbPages = reader.getNumberOfPages();

        if(startPage<0) startPage = 0;
        if(endPage<0) endPage = nbPages;

        for(int i=startPage;i<endPage+1;i++){

            final byte[] streamBytes = reader.getPageContent(i);
            final PRTokeniser tokenizer = new PRTokeniser(new RandomAccessFileOrArray(streamBytes));

            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            while (tokenizer.nextToken()) {
                if(tokenizer.getTokenType() == PRTokeniser.TK_STRING){
                    sb.append(tokenizer.getStringValue());
                }
            }

            final S57FeatureType type = new S57FeatureType();

            final String str = sb.toString();

            if(str.contains("DELETED - DO NOT USE")) continue;
            if(!str.contains("Acronym: "))continue;

            int index1 = str.indexOf("Acronym:")+8;
            int index2 = str.indexOf("Code:");
            type.acronym = str.substring(index1, index2).trim();
            index2 += 5;

            //find the end of the code
            for(index1=index2;;index1++){
                char c = str.charAt(index1);
                if(c == ' ')continue;
                if(!Character.isDigit(c)) break;
            }
            String codestr = str.substring(index2, index1);
            type.code = Integer.valueOf(codestr.trim());
            int size = codestr.length();
            index1 = str.indexOf(type.acronym,index2);
            if(index1>0){
                type.fullName = str.substring(index2+size, index1).trim();
            }

            index1 = str.indexOf("Set Attribute_A:")+16;
            index2 = str.indexOf("Set Attribute_B:");
            String[] parts = str.substring(index1, index2).split(";|,");
            for(String part : parts){
                part = part.trim();
                if(part.isEmpty()) continue;
                type.attA.add(part);
            }

            index1 = str.indexOf("Set Attribute_B:")+16;
            index2 = str.indexOf("Set Attribute_C:");
            parts = str.substring(index1, index2).split(";|,");
            for(String part : parts){
                part = part.trim();
                if(part.isEmpty()) continue;
                type.attB.add(part);
            }

            index1 = str.indexOf("Set Attribute_C:")+16;
            index2 = str.indexOf("Definition:");
            parts = str.substring(index1, index2).split(";|,");
            for(String part : parts){
                part = part.trim();
                if(part.isEmpty()) continue;
                type.attC.add(part);
            }

            index2+=11;
            index1 = str.indexOf("Reference:");
            int dsize = 10;
            if(index1<0){
                index1 = str.indexOf("References:");
                dsize = 11;
            }
            //references may not be present
            boolean noref = false;
            if(index1<0){
                noref = true;
                index1 = str.indexOf("Remarks:");
                dsize = 11;
            }
            type.description = str.substring(index2, index1).trim();

            if(noref){
                index1+=8;
                index2 = str.indexOf("Object Class:");
                type.remarks = str.substring(index1, index2).trim();
            }else{
                index1+=dsize;
                index2 = str.indexOf("Remarks:");
                type.reference = str.substring(index1, index2).trim();

                index2+=8;
                index1 = str.indexOf("Object Class:");
                type.remarks = str.substring(index2, index1).trim();
            }

            index1 = str.indexOf("Object Class:")+13;
            type.fullName = str.substring(index1).trim();

            featureTypes.put(type.acronym,type);
        }

        return featureTypes;
    }

    private static Map<String,S57PropertyType> parsePropertyTypes(String path, int startPage, int endPage) throws IOException {
        final Map<String,S57PropertyType> propertyTypes = new HashMap<>();

        final PdfReader reader = new PdfReader(path);
        final int nbPages = reader.getNumberOfPages();

        if(startPage<0) startPage = 0;
        if(endPage<0) endPage = nbPages;

        for(int i=startPage;i<endPage+1;i++){

            final byte[] streamBytes = reader.getPageContent(i);
            final PRTokeniser tokenizer = new PRTokeniser(new RandomAccessFileOrArray(streamBytes));

            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            while (tokenizer.nextToken()) {
                if(tokenizer.getTokenType() == PRTokeniser.TK_STRING){
                    sb.append(tokenizer.getStringValue());
                }
            }

            final S57PropertyType type = new S57PropertyType();
            String str = sb.toString();
            String lstr = str.toLowerCase();
            //System.out.println(str);

            if(str.contains("DELETED - DO NOT USE")) continue;
            if(!lstr.contains("acronym: "))continue;

            int index1 = lstr.indexOf("acronym:")+8;
            int index2 = lstr.indexOf("code:");
            type.acronym = str.substring(index1, index2).trim();
            index2 += 5;

            //find the end of the code
            for(index1=index2;;index1++){
                char c = str.charAt(index1);
                if(c == ' ')continue;
                if(!Character.isDigit(c)) break;
            }
            String codestr = str.substring(index2, index1);
            type.code = Integer.valueOf(codestr.trim());
            str = str.substring(index1);
            lstr = str.toLowerCase();

            final String attType = "attribute type:";
            final String att2Type = "input type:";
            final String expinput = "expected input:";
            final String definition = "definition:";
            final String definitions = "definitions:";
            final String references = "references:";
            final String indication = "indication:";
            final String minval = "minimum value:";
            final String maxval = "maximum value:";
            final String format = "format:";
            final String exemple = "exemple:";
            final String remarks = "remarks:";
            final String attribute = "attribute:";
            final String int1ref = "int 1 reference:";
            final String chartref = "chart specification:";



            final TreeMap<Integer,String> map = new TreeMap();
            map.put(lstr.indexOf(attType), attType);
            map.put(lstr.indexOf(att2Type), att2Type);
            map.put(lstr.indexOf(expinput), expinput);
            map.put(lstr.indexOf(definition), definition);
            map.put(lstr.indexOf(definitions), definitions);
            map.put(lstr.indexOf(references), references);
            map.put(lstr.indexOf(indication), indication);
            map.put(lstr.indexOf(minval), minval);
            map.put(lstr.indexOf(maxval), maxval);
            map.put(lstr.indexOf(format), format);
            map.put(lstr.indexOf(exemple), exemple);
            map.put(lstr.indexOf(remarks), remarks);
            map.put(lstr.indexOf(attribute), attribute);
            map.put(lstr.indexOf(int1ref), int1ref); //TODO
            map.put(lstr.indexOf(chartref), chartref); //TODO

            for(Map.Entry<Integer,String> e : map.entrySet()){
                final Integer index = e.getKey();
                final String val = e.getValue();
                if(index<0) continue;

                Integer end = map.ceilingKey(index+1);
                String clip;
                if(end==null){
                    clip = str.substring(index+val.length()).trim();
                }else{
                    clip = str.substring(index+val.length(),end).trim();
                }

                if(val == attType){
                    type.type = clip;
                }else if(val == att2Type){
                    type.type = clip;
                }else if(val == expinput){
                    clip = clip.substring(clip.indexOf("Meaning")+7);
                    if(clip.indexOf("INT 1") >= 0){
                        clip = clip.substring(clip.indexOf("INT 1")+5).trim();
                    }
                    if(clip.indexOf("M-4") >= 0){
                        clip = clip.substring(clip.indexOf("M-4")+3).trim();
                    }
                    int previous = 0;
                    final String[] parts = clip.split("\\d+:");
                    for(int k=1;k<parts.length;k++){
                        String desc = parts[k];
                        int desci = clip.indexOf(desc,previous)-1; //remove the ':'
                        final String ecodestr = clip.substring(
                                previous,desci).trim();
                        final int code = Integer.valueOf(ecodestr);
                        previous = desci+desc.length()+1;
                        type.expecteds.add(ecodestr);
                    }

                }else if(val == definition){
                    type.definition = clip;
                }else if(val == definitions){
                    type.definition = clip;
                }else if(val == references){
                    type.references = clip;
                }else if(val == indication){
                    type.indication = clip;
                }else if(val == minval){
                    type.minimum = Double.valueOf(clip);
                }else if(val == maxval){
                    type.maximum = Double.valueOf(clip);
                }else if(val == format){
                    type.format = clip;
                }else if(val == exemple){
                    type.exemple = clip;
                }else if(val == remarks){
                    type.remarks = clip;
                }else if(val == attribute){
                    type.fullName = clip;
                }
            }

            propertyTypes.put(type.acronym,type);
        }

        return propertyTypes;
    }

    private static String toString(String name, PdfObject candidate, final int depth){

        if(candidate instanceof PdfName){
            return name+" "+((PdfName)candidate).toString();
        }else if(candidate instanceof PdfNumber){
            return name+" "+((PdfNumber)candidate).doubleValue();
        }else if(candidate instanceof PdfString){
            return name+" "+((PdfString)candidate).toString();
        }else if(candidate instanceof PdfBoolean){
            return name+" "+((PdfBoolean)candidate).booleanValue();
        }

        if(depth == 0){
            return name +" ...";
        }

        final List<String> values = new ArrayList<String>();

        if(candidate instanceof PdfArray){
            final PdfArray array = (PdfArray) candidate;
            for(int i=0,n=array.size();i<n;i++){
                final PdfObject obj = array.getDirectObject(i);
                values.add(toString("["+i+"]",obj,depth-1));
            }
        }else if(candidate instanceof PdfDictionary){
            final PdfDictionary dico = (PdfDictionary) candidate;
            final Set<PdfName> names = dico.getKeys();
            for(PdfName key : names){
                PdfObject obj = dico.getDirectObject(key);
                values.add(toString(String.valueOf(key),obj,depth-1));
            }
        }
        return Trees.toString(name, values);
    }

}
