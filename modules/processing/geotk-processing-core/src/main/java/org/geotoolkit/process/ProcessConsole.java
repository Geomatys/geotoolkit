/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Geomatys
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

package org.geotoolkit.process;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.Iterator;
import javax.measure.unit.Unit;

import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.io.X364;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.process.converters.StringToAffineTransformConverter;
import org.geotoolkit.process.converters.StringToCRSConverter;
import org.geotoolkit.process.converters.StringToFeatureCollectionConverter;
import org.geotoolkit.process.converters.StringToFeatureTypeConverter;
import org.geotoolkit.process.converters.StringToFilterConverter;
import org.geotoolkit.process.converters.StringToGeometryConverter;
import org.geotoolkit.process.converters.StringToSortByConverter;
import org.geotoolkit.process.converters.StringToUnitConverter;
import org.geotoolkit.process.converters.StringToMapConverter;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.converter.ConverterRegistry;
import org.geotoolkit.util.converter.ObjectConverter;

import static org.geotoolkit.io.X364.*;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.util.InternationalString;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.metadata.Identifier;
import org.opengis.util.NoSuchIdentifierException;


/**
 * Runnable class which dynamically run processes available in the registry.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class ProcessConsole {

    private static final boolean X364_SUPPORTED = X364.isSupported();
    private static final List LIST_CONVERTERS = UnmodifiableArrayList.wrap(
                StringToFeatureCollectionConverter.getInstance(),
                StringToUnitConverter.getInstance(),
                StringToGeometryConverter.getInstance(),
                StringToCRSConverter.getInstance(),
                StringToFeatureTypeConverter.getInstance(),
                StringToAffineTransformConverter.getInstance(),
                StringToFilterConverter.getInstance(),
                StringToSortByConverter.getInstance(),
                StringToMapConverter.getInstance());
    
    private static boolean failed = false;
        
    private static final ProcessListener CONSOLE_ADAPTER = new ProcessListener() {
        
        @Override
        public void started(final ProcessEvent event) {
            printEvent(event, FOREGROUND_DEFAULT.sequence());
        }
        @Override
        public void progressing(final ProcessEvent event) {
            printEvent(event, FOREGROUND_DEFAULT.sequence());
        }
        @Override
        public void completed(final ProcessEvent event) {
            printEvent(event, BOLD.sequence()+FOREGROUND_GREEN.sequence());
        }
        @Override
        public void failed(final ProcessEvent event) {
            failed = true;
            printEvent(event, FOREGROUND_RED.sequence());
        }
        
        private void printEvent(final ProcessEvent event, final String color) {
            final StringBuilder sb = new StringBuilder();
            sb.append(color);
            sb.append(BOLD.sequence());
            sb.append(event.getProgress());
            sb.append("%\t");
            sb.append(RESET.sequence());
            sb.append(color);
            
            final InternationalString message = event.getTask();
            if(message != null){
                sb.append(message.toString());
            }
            
            final Throwable ex = event.getException();
            if(ex != null && message == null){
                sb.append(FOREGROUND_RED.sequence());
                sb.append(ex.getMessage());
                sb.append(FOREGROUND_DEFAULT.sequence());
            }
            if(ex != null){
                final StringWriter buffer = new StringWriter();
                final PrintWriter writer = new PrintWriter(buffer);
                ex.printStackTrace(writer);
                writer.flush();
                buffer.flush();
                final String str = buffer.toString();
                sb.append("\n");
                sb.append(FOREGROUND_RED.sequence());
                sb.append(str);
                sb.append(FOREGROUND_DEFAULT.sequence());
            }
            
            sb.append(RESET.sequence());
            sb.append("\n");
            
            print(sb.toString());
        }
        
    };

    public static void main(String ... args) {

        Setup.initialize(null);

        if(args.length < 1){
            globalHelp();
        }

        boolean displayHelp = false;
        boolean silent = false;
        String firstArg = args[0];
        if("-list".equalsIgnoreCase(firstArg) || "-l".equalsIgnoreCase(firstArg)){
            printList();
            return;
        }else if("-help".equalsIgnoreCase(firstArg) || "-h".equalsIgnoreCase(firstArg)){
            displayHelp = true;
            args = Arrays.copyOfRange(args, 1, args.length);
        }else if("-silent".equalsIgnoreCase(firstArg) || "-s".equalsIgnoreCase(firstArg)){
            silent = true;
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        if(args.length < 1){
            globalHelp();
        }

        //first argument must be the tool name
        //can be written 'processName' or 'authority'.'processName'
        firstArg = args[0];
        final String authorityCode;
        final String processCode;
        final int index = firstArg.indexOf('.');
        if(index != -1){
            authorityCode = firstArg.substring(0, index);
            processCode = firstArg.substring(index+1);
        }else{
            authorityCode = null;
            processCode = firstArg;
        }

        final ProcessDescriptor desc;
        try{
            desc = ProcessFinder.getProcessDescriptor(authorityCode, processCode);
        }catch(NoSuchIdentifierException ex){
            print(FOREGROUND_RED,"Could not find tool for name : ",firstArg,FOREGROUND_DEFAULT,"\n");
            return;
        }

        if(displayHelp){
            printHelp(desc);
            return;
        }

        //parse parameters
        args = Arrays.copyOfRange(args, 1, args.length); //remove the tool name parameter
        final ParameterValueGroup params;
        try {
            params = parseParameters(args, desc.getInputDescriptor());
        } catch (Exception ex) {
            print(FOREGROUND_RED,ex.getLocalizedMessage(),FOREGROUND_DEFAULT,"\n");
            System.exit(1);
            return;
        }
        
        //execute process
        final Process process = desc.createProcess(params);
        process.addListener(CONSOLE_ADAPTER);
        final ParameterValueGroup result;
        try {
            result = process.call();
        } catch (ProcessException ex) {
            print(FOREGROUND_RED,ex.getLocalizedMessage(),FOREGROUND_DEFAULT,"\n");
            System.exit(1);
            return;
        }

        //show result only if in non-silent mode and result have values
        if(!silent && !desc.getOutputDescriptor().descriptors().isEmpty()){
            System.out.println(result);
        }

        Setup.shutdown();
        
        if(failed){
            System.exit(1);
        }
        
    }

    /**
     * Print a list of all tools available.
     */
    private static void printList(){
        final Iterator<ProcessingRegistry> ite = ProcessFinder.getProcessFactories();
        while(ite.hasNext()){
            final ProcessingRegistry registry = ite.next();
            for(final Identifier id : registry.getIdentification().getCitation().getIdentifiers()){
                print(BOLD,id.getCode()," ",RESET);
            }
            print(StringUtilities.toStringTree(Arrays.asList(registry.getNames())));
            print("\n");
        }
    }

    /**
     * Print a detailed description of the tool description.
     */
    private static void printHelp(final ProcessDescriptor desc){
        
        final InternationalString abs = desc.getProcedureDescription();
        if(abs != null){
            print("\n",BOLD,"DESCRIPTION",RESET,"\n",abs,"\n");
        }

        printDescriptor(desc.getInputDescriptor(),true);
        printDescriptor(desc.getOutputDescriptor(),false);
    }

    /**
     * Print list of available parameters in the descriptor.
     */
    private static void printDescriptor(final ParameterDescriptorGroup params, final boolean input){
        if(params == null){
            return;
        }

        print("\n");

        if(input){
            print(BOLD,FOREGROUND_GREEN,">>> INPUT",RESET,"\n");
        }else{
            print(BOLD,FOREGROUND_YELLOW,"<<< OUTPUT",RESET,"\n");
        }

        for(final GeneralParameterDescriptor pdesc : params.descriptors()){
            final ReferenceIdentifier id = pdesc.getName();
            final String code = id.getCode();
            final int minOcc = pdesc.getMinimumOccurs();
            final int maxOcc = pdesc.getMaximumOccurs();
            final InternationalString remark = pdesc.getRemarks();

            print(BOLD,(input)?"-":"",code,RESET,"\t");
            print("(",minOcc,",",maxOcc,")\t");

            if(pdesc instanceof ParameterDescriptor){
                final ParameterDescriptor d = (ParameterDescriptor) pdesc;
                final Class clazz = d.getValueClass();
                final Set validValues = d.getValidValues();
                final Unit unit = d.getUnit();
                final Comparable minVal = d.getMinimumValue();
                final Comparable maxVal = d.getMaximumValue();

                print(Classes.getShortName(clazz));
                if(unit != null){
                    print(" ",unit);
                }
                print("\t");

                if(validValues != null){
                    print("{");
                    for(final Object obj : validValues){
                        print(obj," ");
                    }
                    print("}\t");
                }
                if(minVal != null || maxVal != null){
                    String from = "";
                    String to = "";

                    if(minVal != null){
                        from = minVal.toString();
                    }
                    if(maxVal != null){
                        to = maxVal.toString();
                    }

                    print("[ ",from," ... ",to," ]\t");
                }

            }

            if(remark!=null){
                print("\n\t",FAINT,remark,RESET);
            }

            print("\n");
        }
    }

    /**
     * Parse, convert and set parameter values from the command line arguments.
     */
    private static ParameterValueGroup parseParameters(final String[] args, final ParameterDescriptorGroup desc) 
            throws NonconvertibleObjectException, IllegalArgumentException{
        final ParameterValueGroup group = desc.createValue();

        //regroup value for each parameter
        final List<Entry<String,List<String>>> groups = new ArrayList<Entry<String,List<String>>>();
        Entry<String,List<String>> current = null;
        for(String str : args){
            if (str.startsWith("-")) {
                //start a new parameter
                if(current != null){
                    groups.add(current);
                }
                current = new SimpleEntry<String, List<String>>(str.substring(1), new ArrayList<String>());
                continue;
            }

            //append to current parameter
            if(current == null){
                throw new IllegalArgumentException("value : "+str+" is not linked to any parameter.");
            }
            current.getValue().add(str);
        }
        if(current != null){
            groups.add(current);
        }


        //set parameter values
        for(final Entry<String,List<String>> entry : groups){
            final ParameterValue parameter = group.parameter(entry.getKey());
            final Class clazz = parameter.getDescriptor().getValueClass();
            final List<String> values = entry.getValue();
            final Object converted = toValue(values, clazz);
            parameter.setValue(converted);
        }

        return group;
    }

    /**
     * Convert a List of string values in the appropriate Class.
     * Possibly an Array or a single value.
     */
    private static <T> Object toValue(final List<String> values, final Class<T> binding) throws NonconvertibleObjectException{
        final int size = values.size();

        ObjectConverter<String,T> converter = null;
        try{
            converter = ConverterRegistry.system().converter(String.class, binding);
        }catch(NonconvertibleObjectException ex){
            for(ObjectConverter conv : (List<ObjectConverter>)LIST_CONVERTERS){
                if(conv.getTargetClass().equals(binding)){
                    converter = conv;
                }
            }

            if(converter == null){
                throw ex;
            }
        }

        if(size == 0 && binding == Boolean.class){
            //if there is no values after this parameter, it means we set it to true
            return true;
        }else if(size == 0){
            return null;
        }else if(size == 1){
            //return a single value converted
            return converter.convert(values.get(0));
        }else{
            //convert to array of binding class
            final T[] array = (T[])Array.newInstance(binding,size);
            for(int i=0;i<size;i++){
                array[i] = converter.convert(values.get(i));
            }
            return array;
        }
    }

    private static void globalHelp(){
        print(BOLD,"This tool works using three configurations blocks.\n",RESET);
        print(BOLD,FOREGROUND_MAGENTA,"[global parameters]",RESET);
        print(BOLD,FOREGROUND_DEFAULT," [tool name] ",RESET);
        print(BOLD,FOREGROUND_GREEN,"[tool parameters]",RESET,"\n");
        print(FOREGROUND_MAGENTA,"-list -l ",RESET,"   : Display list of available tools.\n");
        print(FOREGROUND_MAGENTA,"-help -h ",RESET,"   : Display help for a tool, must be followed by the tool name.\n");
        print(FOREGROUND_MAGENTA,"-silent -s ",RESET," : Silently execute tool (will not show the result).\n");
        print(FOREGROUND_DEFAULT,"Tool name ",RESET,"  : can be authority.name or name alone if unique.\n");
        print(FOREGROUND_GREEN,"Tool params ",RESET,": can be found using -help for a given tool.\n");

        System.exit(0);
    }

    /**
     * Print in the console the given objects.
     * X364 object are automaticly removed if the console does not handle them.
     */
    private static void print(final Object ... texts){
        final String text;
        if(texts.length == 1){
            text = String.valueOf(texts[0]);
        }else{
            final StringBuilder sb = new StringBuilder();
            for(Object obj : texts){
                if(obj instanceof X364){
                    if(X364_SUPPORTED){
                        sb.append( ((X364)obj).sequence() );
                    }
                }else{
                    sb.append(obj);
                }
            }
            text = sb.toString();
        }
        System.out.print(text);
    }

}
