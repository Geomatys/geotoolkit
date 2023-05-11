/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2019, Geomatys
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

package org.geotoolkit.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.LogManager;
import javax.measure.Unit;
import org.apache.sis.internal.util.X364;
import static org.apache.sis.internal.util.X364.*;
import org.apache.sis.util.Classes;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.collection.Containers;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.process.*;
import org.geotoolkit.processing.util.converter.StringToAffineTransformConverter;
import org.geotoolkit.processing.util.converter.StringToFeatureSetConverter;
import org.geotoolkit.processing.util.converter.StringToGeometryConverter;
import org.geotoolkit.processing.util.converter.StringToMapConverter;
import org.geotoolkit.processing.util.converter.StringToSortByConverter;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.util.StringUtilities;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;
import org.opengis.util.NoSuchIdentifierException;


/**
 * Runnable class which dynamically run processes available in the registry.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class ProcessConsole {

    private static final boolean X364_SUPPORTED = X364.isAnsiSupported();
    private static final List LIST_CONVERTERS = Containers.unmodifiableList(StringToFeatureSetConverter.getInstance(),
                StringToGeometryConverter.getInstance(),
                StringToAffineTransformConverter.getInstance(),
                StringToSortByConverter.getInstance(),
                StringToMapConverter.getInstance());


    public static void main(String[] args) {
        new ProcessConsole().execute(args);
    }

    private boolean failed = false;

    private final ProcessListener consoleAdapter = new ProcessListener() {

        private long startTimeMs;

        @Override
        public void started(final ProcessEvent event) {
            printEvent(event, FOREGROUND_DEFAULT.sequence());
            startTimeMs = System.currentTimeMillis();
        }
        @Override
        public void progressing(final ProcessEvent event) {
            printEvent(event, FOREGROUND_DEFAULT.sequence());
        }

        public void dismissed(final ProcessEvent event) {
            failed = true;
            printEvent(event, FOREGROUND_RED.sequence());
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
        @Override
        public void paused(final ProcessEvent event) {
            printEvent(event, FOREGROUND_DEFAULT.sequence());
        }
        @Override
        public void resumed(final ProcessEvent event) {
            printEvent(event, FOREGROUND_DEFAULT.sequence());
        }

        private void printEvent(final ProcessEvent event, final String color) {

            final long currentTimeMs = System.currentTimeMillis();
            final long etd = currentTimeMs - startTimeMs;
            final long eta = (long) (((100.0 * etd) / event.getProgress()) - etd);

            final StringBuilder sb = new StringBuilder();
            sb.append(color);
            sb.append(BOLD.sequence());
            sb.append(event.getProgress());
            sb.append("%\t");
            sb.append(RESET.sequence());
            sb.append(color);
            if (eta > 0 && etd > 0) {
                sb.append("Elapsed: ");
                sb.append(TemporalUtilities.durationToString(etd));
                sb.append("\t");
                sb.append("Remaining: ~");
                sb.append(TemporalUtilities.durationToString(eta));
                sb.append("\t");
            }


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

    private final List<ProcessingRegistry> registries = new ArrayList<>();
    private final List<String> examples = new ArrayList<>();
    private String header = "    ____            _              _ _    _ _   \n" +
                            "  / ___| ___  ___ | |_ ___   ___ | | | _(_) |_ \n" +
                            " | |  _ / _ \\/ _ \\| __/ _ \\ / _ \\| | |/ / | __|\n" +
                            " | |_| |  __/ (_) | || (_) | (_) | |   <| | |_ \n" +
                            "  \\____|\\___|\\___/ \\__\\___/ \\___/|_|_|\\_\\_|\\__|\n" +
                            "                                               \n" +
                            "";

    public ProcessConsole() {

        // search for a logging configuration
        Path p = Paths.get("logging.properties");
        if (Files.exists(p)) {
            System.out.println("Loading logging.properties");
            try (InputStream is = Files.newInputStream(p)) {
                LogManager.getLogManager().readConfiguration(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Setup.initialize(null);
        //set default processing registries
        Iterator<ProcessingRegistry> ite = ProcessFinder.getProcessFactories();
        while (ite.hasNext()) {
            registries.add(ite.next());
        }
        //set default examples
        examples.add("-help math:add");
        examples.add("-help geotoolkit.math:add");
        examples.add("math:add -first 7 -second 5");
    }

    /**
     * Modifiable list of ProcessingRegistry available.
     * @return
     */
    public List<ProcessingRegistry> getRegistries() {
        return registries;
    }

    /**
     * Modifiable list of execution examples.
     * @return
     */
    public List<String> getExamples() {
        return examples;
    }

    public String getHeader() {
        return header;
    }

    /**
     * Set header, printed at start of the help message.
     * @param header
     */
    public void setHeader(String header) {
        this.header = header;
    }

    public void execute(String[] args) {

        if (args.length < 1) {
            globalHelp();
        }

        boolean displayHelp = false;
        boolean silent = false;
        String firstArg = args[0];
        if (args.length == 1 && "-ui".equalsIgnoreCase(firstArg)) {
            try {
                Class<?> clazzfx = Class.forName("javafx.embed.swing.JFXPanel");
                //check it can be created
                Object inst = clazzfx.newInstance();
                Class<?> clazz = Class.forName("org.geotoolkit.gui.javafx.process.FXExecutionPane");
                Method fct = clazz.getMethod("show", Collection.class);
                fct.invoke(null, registries);
                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            return;
        } else if ("-list".equalsIgnoreCase(firstArg) || "-l".equalsIgnoreCase(firstArg)) {
            printList();
            return;
        } else if("-help".equalsIgnoreCase(firstArg) || "-h".equalsIgnoreCase(firstArg)) {
            displayHelp = true;
            args = Arrays.copyOfRange(args, 1, args.length);
        } else if("-silent".equalsIgnoreCase(firstArg) || "-s".equalsIgnoreCase(firstArg)) {
            silent = true;
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        if (args.length < 1) {
            globalHelp();
        }

        //first argument must be the tool name
        //can be written 'processName' or 'authority'.'processName'
        firstArg = args[0];
        final String authorityCode;
        final String processCode;
        final int index = firstArg.indexOf('.');
        if (index != -1) {
            authorityCode = firstArg.substring(0, index);
            processCode = firstArg.substring(index+1);
        } else {
            authorityCode = null;
            processCode = firstArg;
        }

        final ProcessDescriptor desc;
        try {
            desc = getProcessDescriptor(authorityCode, processCode);
        } catch(NoSuchIdentifierException ex) {
            print(FOREGROUND_RED,"Could not find tool for name : ",firstArg,FOREGROUND_DEFAULT,"\n");
            return;
        }

        if (displayHelp) {
            printHelp(desc);
            return;
        }

        //parse parameters
        args = Arrays.copyOfRange(args, 1, args.length); //remove the tool name parameter

        if (args.length > 0 && ("-help".equalsIgnoreCase(args[0]) || "-h".equalsIgnoreCase(args[0]))) {
            printHelp(desc);
            return;
        }

        final ParameterValueGroup params;
        try {
            params = parseParameters(args, desc.getInputDescriptor());
        } catch (Exception ex) {
            print(FOREGROUND_RED,ex.getLocalizedMessage(),FOREGROUND_DEFAULT,"\n");
            System.exit(1);
            return;
        }

        //execute process
        final org.geotoolkit.process.Process process = desc.createProcess(params);
        process.addListener(consoleAdapter);
        final ParameterValueGroup result;
        try {
            result = process.call();
        } catch (ProcessException ex) {
            print(FOREGROUND_RED,ex.getLocalizedMessage(),FOREGROUND_DEFAULT,"\n");
            System.exit(1);
            return;
        }

        //show result only if in non-silent mode and result have values
        if (!silent && !desc.getOutputDescriptor().descriptors().isEmpty()) {
            System.out.println(result);
        }

        Setup.shutdown();

        print(FOREGROUND_YELLOW,"Process ended, exiting",FOREGROUND_DEFAULT,"\n");

        if (failed) {
            System.exit(1);
        } else {
            //we force exit, this is necessary because some dependencies
            //holds thread pools and other kind of structures which prevent exiting.
            System.exit(0);
        }

    }

    /**
     * Search for a Process descriptor in the given authority and the given name.
     *
     * @param authority registry name
     * @param processName process descriptor name
     * @return ProcessDescriptor
     * @throws IllegalArgumentException if description could not be found
     */
    private ProcessDescriptor getProcessDescriptor(String authority, final String processName) throws NoSuchIdentifierException {

        if (authority != null && authority.trim().isEmpty()) {
            authority = null;
        }

        if (authority != null) {
            final ProcessingRegistry factory = ProcessFinder.getProcessFactory(registries.iterator(), authority);
            if (factory != null) {
                return factory.getDescriptor(processName);
            } else {
                throw new NoSuchIdentifierException("No processing registry for given code.", authority);
            }
        }

        //try all factories
        for (ProcessingRegistry factory : registries) {
            try {
                return factory.getDescriptor(processName);
            } catch (NoSuchIdentifierException ex) {
            }
        }

        throw new NoSuchIdentifierException("No process for given code.", processName);
    }

    /**
     * Print a list of all tools available.
     */
    private void printList() {
        for (ProcessingRegistry registry : registries) {
            for (final Identifier id : registry.getIdentification().getCitation().getIdentifiers()) {
                print(BOLD,id.getCode()," ",RESET);
            }
            List<String> names = new ArrayList<>(registry.getNames());
            Collections.sort(names);
            print(StringUtilities.toStringTree("",names));
            print("\n");
        }
    }

    /**
     * Print a detailed description of the tool description.
     */
    private static void printHelp(final ProcessDescriptor desc) {

        final InternationalString abs = desc.getProcedureDescription();
        if (abs != null) {
            print("\n",BOLD,"DESCRIPTION",RESET,"\n",abs,"\n");
        }

        printDescriptor(desc.getInputDescriptor(),true);
        printDescriptor(desc.getOutputDescriptor(),false);
        print("\n");
    }

    /**
     * Print list of available parameters in the descriptor.
     */
    private static void printDescriptor(final ParameterDescriptorGroup params, final boolean input) {
        if (params == null) {
            return;
        }

        print("\n");

        if (input) {
            print(BOLD,FOREGROUND_GREEN,"=== INPUTS ===",RESET,"\n");
        } else {
            print(BOLD,FOREGROUND_YELLOW,"=== OUTPUTS ===",RESET,"\n");
        }

        if (params.descriptors().isEmpty()) {
            print(FAINT," none ",RESET,"\n");
            return;
        }


        for (final GeneralParameterDescriptor pdesc : params.descriptors()) {
            final Identifier id = pdesc.getName();
            final String code = id.getCode();
            final int minOcc = pdesc.getMinimumOccurs();
            final int maxOcc = pdesc.getMaximumOccurs();
            final InternationalString remark = pdesc.getRemarks();

            print(BOLD,(input)?"-":"",code,RESET,"\t");
            print("(",minOcc,",",maxOcc,")\t");

            if (pdesc instanceof ParameterDescriptor) {
                final ParameterDescriptor d = (ParameterDescriptor) pdesc;
                final Class clazz = d.getValueClass();
                final Set validValues = d.getValidValues();
                final Unit unit = d.getUnit();
                final Comparable minVal = d.getMinimumValue();
                final Comparable maxVal = d.getMaximumValue();

                print(Classes.getShortName(clazz));
                if (unit != null) {
                    print(" ",unit);
                }
                print("\t");

                if (validValues != null) {
                    print("{");
                    for (final Object obj : validValues) {
                        print(obj," ");
                    }
                    print("}\t");
                }
                if (minVal != null || maxVal != null) {
                    String from = "";
                    String to = "";

                    if (minVal != null) {
                        from = minVal.toString();
                    }
                    if (maxVal != null) {
                        to = maxVal.toString();
                    }

                    print("[ ",from," ... ",to," ]\t");
                }

            }

            if (remark != null) {
                print("\n\t",FAINT,remark,RESET);
            }

            print("\n");
        }
    }

    /**
     * Parse, convert and set parameter values from the command line arguments.
     */
    private static ParameterValueGroup parseParameters(final String[] args, final ParameterDescriptorGroup desc)
            throws UnconvertibleObjectException, IllegalArgumentException {
        final ParameterValueGroup group = desc.createValue();

        //regroup value for each parameter
        final List<Entry<String,List<String>>> groups = new ArrayList<Entry<String,List<String>>>();
        Entry<String,List<String>> current = null;
        for (String str : args) {
            if (str.startsWith("-")) {
                //start a new parameter
                if (current != null) {
                    groups.add(current);
                }
                current = new SimpleEntry<String, List<String>>(str.substring(1), new ArrayList<String>());
                continue;
            }

            //append to current parameter
            if (current == null) {
                throw new IllegalArgumentException("value : "+str+" is not linked to any parameter.");
            }
            current.getValue().add(str);
        }
        if (current != null) {
            groups.add(current);
        }


        //set parameter values
        for (final Entry<String,List<String>> entry : groups) {
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
    private static <T> Object toValue(final List<String> values, final Class<T> binding) throws UnconvertibleObjectException {
        final int size = values.size();

        Class baseBinding = binding;
        if (binding.isArray()) {
            baseBinding = binding.getComponentType();
        }

        ObjectConverter<? super String, ? extends T> converter = null;
        try {
            converter = ObjectConverters.find(String.class, baseBinding);
        } catch(UnconvertibleObjectException ex) {
            for (ObjectConverter conv : (List<ObjectConverter>)LIST_CONVERTERS) {
                if (conv.getTargetClass().equals(baseBinding)) {
                    converter = conv;
                }
            }

            if (converter == null) {
                throw ex;
            }
        }

        if (size == 0 && baseBinding == Boolean.class) {
            //if there is no values after this parameter, it means we set it to true
            return true;
        } else if(size == 0) {
            return null;
        } else if(size == 1) {
            //return a single value converted
            return converter.apply(values.get(0));
        } else {
            //convert to array of binding class
            final T[] array = (T[])Array.newInstance(baseBinding,size);
            for (int i=0;i<size;i++) {
                array[i] = converter.apply(values.get(i));
            }
            return array;
        }
    }

    private void globalHelp() {
        if (header != null) print(header,"\n");
        print(BOLD,"This tool works using three configurations blocks.\n",RESET);
        print(BOLD,FOREGROUND_MAGENTA,"[global parameters]",RESET);
        print(BOLD,FOREGROUND_DEFAULT," [tool name] ",RESET);
        print(BOLD,FOREGROUND_GREEN,"[tool parameters]",RESET,"\n");
        print(FOREGROUND_MAGENTA,"-ui ",RESET,"        : Display graphical interface (if Geotoolkit JavaFX module is available).\n");
        print(FOREGROUND_MAGENTA,"-list -l ",RESET,"   : Display list of available tools.\n");
        print(FOREGROUND_MAGENTA,"-help -h ",RESET,"   : Display help for a tool, must be followed by the tool name.\n");
        print(FOREGROUND_MAGENTA,"-silent -s ",RESET," : Silently execute tool (will not show the result).\n");
        print(FOREGROUND_DEFAULT,"Tool name ",RESET,"  : can be authority.name or name alone if unique.\n");
        print(FOREGROUND_GREEN,"Tool params ",RESET,": can be found using -help for a given tool.\n");
        print("\n");

        if (!examples.isEmpty()) {
            print(BOLD,"Examples",RESET,"\n");
            for (String str : examples) {
                print(str,"\n");
            }
            print("\n");
        }

        System.exit(0);
    }

    /**
     * Print in the console the given objects.
     * X364 object are automatically removed if the console does not handle them.
     */
    private static void print(final Object ... texts) {
        final String text;
        if (texts.length == 1) {
            text = String.valueOf(texts[0]);
        } else {
            final StringBuilder sb = new StringBuilder();
            for (Object obj : texts) {
                if (obj instanceof X364) {
                    if (X364_SUPPORTED) {
                        sb.append( ((X364)obj).sequence() );
                    }
                } else {
                    sb.append(obj);
                }
            }
            text = sb.toString();
        }
        System.out.print(text);
    }

}
