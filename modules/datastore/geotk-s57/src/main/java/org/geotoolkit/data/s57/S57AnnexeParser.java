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
package org.geotoolkit.data.s57;

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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.gui.swing.tree.Trees;
import org.opengis.feature.type.FeatureType;

/**
 * Used only to rebuild types from S-57 annexes.
 * S-57_AppendixA_Chapter1_31ApAch1.pdf
 * S-57_AppendixA_Chapter2_31ApAch2.pdf
 * 
 * @author Johann Sorel (Geomatys)
 */
final class S57AnnexeParser {
    
    public static class S57FeatureType implements Serializable{
        String acronym;
        int code;
        String fullName;
        String description;
        String remarks;
        String reference;
        List<String> attA = new ArrayList<String>();
        List<String> attB = new ArrayList<String>();
        List<String> attC = new ArrayList<String>();

        @Override
        public String toString() {
            final List lst = new ArrayList();
            lst.add("CODE="+code);
            lst.add("NAME="+fullName);
            lst.add("DESC="+description);
            lst.add("REMARK="+remarks);
            lst.add("REFS="+reference);
            lst.add(Trees.toString("AttA", attA));
            lst.add(Trees.toString("AttB", attB));
            lst.add(Trees.toString("AttC", attC));            
            return Trees.toString(acronym, lst);
        }        
    }
    
    public static class S57PropertyType implements Serializable{
        String acronym;
        int code;
        String fullName;
        String type;
        List<String> expecteds = new ArrayList<String>();
        String definition;
        String references;
        Double minimum;
        Double maximum;
        String remarks;
        String indication;
        String format;
        String exemple;
        
        @Override
        public String toString() {
            final List lst = new ArrayList();
            lst.add("CODE="+code);
            lst.add("NAME="+fullName);
            lst.add("TYPE="+type);
            lst.add("definition="+definition);
            lst.add("references="+references);
            lst.add("minimum="+minimum);
            lst.add("maximum="+maximum);
            lst.add("remarks="+remarks);
            lst.add("indication="+indication);
            lst.add("format="+format);
            lst.add("exemple="+exemple);
            return Trees.toString(acronym, lst);
        }        
    }

    private final Map<String,S57FeatureType> s57ftypes;
    private final Map<String,S57PropertyType> s57ptypes;
    private final Map<String, FeatureType> featureTypes = new HashMap<String, FeatureType>();
    
    public static void main(String[] args) throws IOException {
        
        args = new String[]{
            "/home/jsorel/TRAVAIL/1_Specification/IHO/S-57_AppendixA_Chapter1_31ApAch1.pdf",
            "/home/jsorel/TRAVAIL/1_Specification/IHO/S-57_AppendixA_Chapter2_31ApAch2.pdf"
        };
        S57AnnexeParser parser = new S57AnnexeParser(args[0], args[1]);
        
        
        
    }
    
    public S57AnnexeParser(String an1, String an2) throws IOException {
        s57ftypes = parseFeatureTypes(an1);
        s57ptypes = parsePropertyTypes(an2);
        
        //rebuild types
        for(S57FeatureType sft : s57ftypes.values()){
            System.out.println(getFeatureType(sft.acronym));
        }
        System.out.println(s57ftypes.size());
    }
    
    private FeatureType getFeatureType(String name){
        FeatureType ft = featureTypes.get(name);
        if(ft!=null) return ft;
        
        final S57FeatureType sft = s57ftypes.get(name);
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(sft.acronym);
        final List<String> allAtts = new ArrayList<String>();
        allAtts.addAll(sft.attA);
        allAtts.addAll(sft.attB);
        allAtts.addAll(sft.attC);

        for(String att : allAtts){
            final S57PropertyType pt = s57ptypes.get(att);
            if(pt==null){
                System.out.println(">>>>"+att);
            }
            ftb.add(pt.acronym, Object.class);
        }

        ft = ftb.buildFeatureType();
        featureTypes.put(ft.getName().getLocalPart(), ft);
        
        return ft;
    }
    
    private static Map<String,S57FeatureType> parseFeatureTypes(String path) throws IOException {
        final Map<String,S57FeatureType> featureTypes = new HashMap<String,S57FeatureType>();
        
        final PdfReader reader = new PdfReader(path);        
        final int nbPages = reader.getNumberOfPages();
        
        for(int i=0;i<nbPages+1;i++){
            if(i<17)continue;
            
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
    
    private static Map<String,S57PropertyType> parsePropertyTypes(String path) throws IOException {
        final Map<String,S57PropertyType> propertyTypes = new HashMap<String,S57PropertyType>();
        
        final PdfReader reader = new PdfReader(path);        
        final int nbPages = reader.getNumberOfPages();
        
        for(int i=0;i<nbPages+1;i++){            
            if(i<10)continue;            
            
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
            System.out.println(str);
            
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
            final String expinput = "expected input:";
            final String definition = "definition:";
            final String references = "references:";
            final String indication = "indication:";
            final String minval = "minimum value:";
            final String maxval = "maximum value:";
            final String format = "format:";
            final String exemple = "exemple:";
            final String remarks = "remarks:";
            final String attribute = "attribute:";
            
            final TreeMap<Integer,String> map = new TreeMap();
            map.put(lstr.indexOf(attType), attType);
            map.put(lstr.indexOf(expinput), expinput);
            map.put(lstr.indexOf(definition), definition);
            map.put(lstr.indexOf(references), references);
            map.put(lstr.indexOf(indication), indication);
            map.put(lstr.indexOf(minval), minval);
            map.put(lstr.indexOf(maxval), maxval);
            map.put(lstr.indexOf(format), format);
            map.put(lstr.indexOf(exemple), exemple);
            map.put(lstr.indexOf(remarks), remarks);
            map.put(lstr.indexOf(attribute), attribute);

            for(Map.Entry<Integer,String> e : map.entrySet()){
                final Integer index = e.getKey();
                final String val = e.getValue();
                if(index<0) continue;

                Integer end = map.ceilingKey(index+1);
                final String clip;
                if(end==null){
                    clip = str.substring(index+val.length()).trim();
                }else{
                    clip = str.substring(index+val.length(),end).trim();
                }
                
                if(val == attType){
                    type.type = clip;
                }else if(val == expinput){
                    type.expecteds.add(clip);//TODO
                }else if(val == definition){
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
