/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.openeo.process.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.apache.sis.parameter.DefaultParameterDescriptor;
import org.geotoolkit.atom.xml.Link;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.openeo.dto.CheckMessage;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.geotoolkit.process.ProcessDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import static org.geotoolkit.openeo.process.OpenEOUtils.examindProcessIdToOpenEOProcessId;
import static org.geotoolkit.openeo.process.OpenEOUtils.openEOProcessIdToExamindProcessId;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Process-Discovery">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Process.JSON_PROPERTY_ID,
        Process.JSON_PROPERTY_SUMMARY,
        Process.JSON_PROPERTY_DESCRIPTION,
        Process.JSON_PROPERTY_CATEGORIES,
        Process.JSON_PROPERTY_PARAMETERS,
        Process.JSON_PROPERTY_RETURNS,
        Process.JSON_PROPERTY_DEPRECATED,
        Process.JSON_PROPERTY_EXPERIMENTAL,
        Process.JSON_PROPERTY_EXCEPTIONS,
        Process.JSON_PROPERTY_EXAMPLES,
        Process.JSON_PROPERTY_LINKS,
        Process.JSON_PROPERTY_PROCESS_GRAPH
})
@XmlRootElement(name = "Process")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Process")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Process extends DataTransferObject {

    public Process() {}

    public Process(String id, String summary, String description, List<String> categories, List<ProcessParameter> parameters,
                   ProcessReturn returns, Boolean deprecated, Boolean experimental, Map<String, ProcessExceptionInformation> exceptions,
                   List<ProcessDescription> examples, List<Link> links) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.categories = categories;
        this.parameters = parameters;
        this.returns = returns;
        this.deprecated = deprecated;
        this.experimental = experimental;
        this.exceptions = exceptions;
        this.examples = examples;
        this.links = links;
    }

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_SUMMARY = "summary";
    @XmlElement(name = "summary")
    @jakarta.annotation.Nullable
    private String summary = "No summary specified";

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description = "No description specified";

    public static final String JSON_PROPERTY_CATEGORIES = "categories";
    @XmlElementWrapper(name = "categories")
    @XmlElement(name = "category")
    @JacksonXmlElementWrapper(localName = "categories", useWrapping = false)
    @JacksonXmlProperty(localName = "category")
    @jakarta.annotation.Nullable
    private List<String> categories = new ArrayList<>();

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    @JacksonXmlElementWrapper(localName = "parameters", useWrapping = false)
    @JacksonXmlProperty(localName = "parameter")
    @jakarta.annotation.Nullable
    private List<ProcessParameter> parameters = new ArrayList<>();

    public static final String JSON_PROPERTY_RETURNS = "returns";
    @XmlElement(name = "returns")
    @jakarta.annotation.Nullable
    private ProcessReturn returns = null;

    public static final String JSON_PROPERTY_DEPRECATED = "deprecated";
    @XmlElement(name = "deprecated")
    @jakarta.annotation.Nullable
    private Boolean deprecated = false;

    public static final String JSON_PROPERTY_EXPERIMENTAL = "experimental";
    @XmlElement(name = "experimental")
    @jakarta.annotation.Nullable
    private Boolean experimental = false;

    public static final String JSON_PROPERTY_EXCEPTIONS = "exceptions";
    @XmlTransient
    @jakarta.annotation.Nullable
    private Map<String, ProcessExceptionInformation> exceptions = new HashMap<>();

    public static final String JSON_PROPERTY_EXAMPLES = "examples";
    @XmlElementWrapper(name = "examples")
    @XmlElement(name = "example")
    @JacksonXmlElementWrapper(localName = "examples", useWrapping = false)
    @JacksonXmlProperty(localName = "example")
    @jakarta.annotation.Nullable
    private List<ProcessDescription> examples = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    // JAXB annotation for lists: element wrapper and element name
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    // Jackson XML annotation for lists
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_PROCESS_GRAPH = "process_graph";
    @XmlTransient
    @jakarta.annotation.Nonnull
    private Map<String, ProcessDescription> processGraph = new HashMap<>();

    /**
     * Get id
     *
     * @return id
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nonnull String id) {
        this.id = id;
    }

    /**
     * Get summary
     *
     * @return summary
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SUMMARY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty(JSON_PROPERTY_SUMMARY)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "summary")
    public void setSummary(@jakarta.annotation.Nullable String summary) {
        this.summary = summary;
    }

    /**
     * Get description
     *
     * @return description
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    /**
     * Get categories
     *
     * @return categories
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CATEGORIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CATEGORIES)
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty(JSON_PROPERTY_CATEGORIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CATEGORIES)
    public void setCategories(@jakarta.annotation.Nullable List<String> categories) {
        this.categories = categories;
    }

    /**
     * Get parameters
     *
     * @return parameters
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PARAMETERS)
    public List<ProcessParameter> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_PARAMETERS)
    public void setParameters(@jakarta.annotation.Nullable List<ProcessParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get returns
     *
     * @return returns
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RETURNS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returns")
    public ProcessReturn getReturns() {
        return returns;
    }

    @JsonProperty(JSON_PROPERTY_RETURNS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "returns")
    public void setReturns(@jakarta.annotation.Nullable ProcessReturn returns) {
        this.returns = returns;
    }

    /**
     * Get deprecated
     *
     * @return deprecated
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DEPRECATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "deprecated")
    public Boolean getDeprecated() {
        return deprecated;
    }

    @JsonProperty(JSON_PROPERTY_DEPRECATED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "deprecated")
    public void setDeprecated(@jakarta.annotation.Nullable Boolean deprecated) {
        this.deprecated = deprecated;
    }

    /**
     * Get experimental
     *
     * @return experimental
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXPERIMENTAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "experimental")
    public Boolean getExperimental() {
        return experimental;
    }

    @JsonProperty(JSON_PROPERTY_EXPERIMENTAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "experimental")
    public void setExperimental(@jakarta.annotation.Nullable Boolean experimental) {
        this.experimental = experimental;
    }

    /**
     * Get exceptions
     *
     * @return exceptions
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXCEPTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, ProcessExceptionInformation> getExceptions() {
        return exceptions;
    }

    @JsonProperty(JSON_PROPERTY_EXCEPTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setExceptions(@jakarta.annotation.Nullable Map<String, ProcessExceptionInformation> exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * Get examples
     *
     * @return examples
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXAMPLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_EXAMPLES)
    public List<ProcessDescription> getExamples() {
        return examples;
    }

    @JsonProperty(JSON_PROPERTY_EXAMPLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_EXAMPLES)
    public void setExamples(@jakarta.annotation.Nullable List<ProcessDescription> examples) {
        this.examples = examples;
    }

    /**
     * Get links
     *
     * @return links
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }

    /**
     * Get processGraph
     *
     * @return processGraph
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PROCESS_GRAPH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, ProcessDescription> getProcessGraph() {
        return processGraph;
    }

    @JsonProperty(JSON_PROPERTY_PROCESS_GRAPH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setProcessGraph(@jakarta.annotation.Nonnull Map<String, ProcessDescription> processGraph) {
        this.processGraph = processGraph;
    }

    public void sortProcessGraph() throws IllegalArgumentException {
        // Build adjacency list and in-degree map
        Map<String, Set<String>> dependencies = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        // Initialize maps
        for (String nodeId : processGraph.keySet()) {
            dependencies.put(nodeId, new HashSet<>());
            inDegree.put(nodeId, 0);
        }

        // Build dependency graph
        for (Map.Entry<String, ProcessDescription> entry : processGraph.entrySet()) {
            String currentNode = entry.getKey();
            ProcessDescription process = entry.getValue();

            for (ProcessDescriptionArgument argument : process.getArguments().values()) {
                if (argument.getType() == ProcessDescriptionArgument.ArgumentType.FROM_NODE) {
                    String fromNode = argument.getValue().toString();
                    if (!processGraph.containsKey(fromNode)) {
                        throw new IllegalStateException("Referenced node '" + fromNode + "' not present in graph.");
                    }
                    dependencies.get(fromNode).add(currentNode);
                    inDegree.put(currentNode, inDegree.get(currentNode) + 1);
                }
            }
        }

        // Queue of nodes with no incoming edges
        Queue<String> queue = new LinkedList<>();
        inDegree.forEach((node, degree) -> {
            if (degree == 0) queue.add(node);
        });

        // Resulting sorted graph
        LinkedHashMap<String, ProcessDescription> sortedGraph = new LinkedHashMap<>();

        while (!queue.isEmpty()) {
            String current = queue.poll();
            sortedGraph.put(current, processGraph.get(current));

            for (String neighbor : dependencies.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // Check if all nodes are processed (if not, cycle exists)
        if (sortedGraph.size() != processGraph.size()) {
            throw new IllegalStateException("Cyclic dependency detected in process graph.");
        }

        this.processGraph = sortedGraph;
    }

    public CheckMessage isProcessGraphValid(List<ProcessDescriptor> descriptors) {
        List<String> descriptorIds = descriptors.stream().map(d -> examindProcessIdToOpenEOProcessId(d.getIdentifier().getCode())).toList();

        for (Map.Entry<String, ProcessDescription> entry : processGraph.entrySet()) {
            ProcessDescription process = entry.getValue();

            //Process specified in the graph exist in the list of available process ?
            String[] processIdSplitted = process.getProcessId().split("\\.",2);
            String processId = processIdSplitted.length == 2 ? processIdSplitted[1] : processIdSplitted[0];
            if (!descriptorIds.contains(processId)) {
                return new CheckMessage(false, "No available process with this id : " + process.getProcessId());
            }

            //Check if the arguments are valid (arg type, links to an existing parameter / node, ...)
            for (Map.Entry<String, ProcessDescriptionArgument> argEntry : process.getArguments().entrySet()) {
                ProcessDescriptionArgument arg = argEntry.getValue();
                CheckMessage checkMessage = isProcessDescriptionArgumentValid(arg);
                if (!checkMessage.isValid()) {
                    return checkMessage;
                }
            }

            //Checks between "real" process, and given process
            ProcessDescriptor processDescriptor = descriptors.stream()
                    .filter(descriptor -> descriptor.getIdentifier().getCode().equals(openEOProcessIdToExamindProcessId(processId, false)))
                    .findFirst()
                    .orElse(null);

            if (processDescriptor != null) {
                if (processDescriptor.getInputDescriptor().descriptors().stream().filter(descriptor -> descriptor.getMinimumOccurs() > 0).count() > process.getArguments().size()) {
                    //The number of inputs arguments given is not the same of the number of arguments needed by the process
                    return new CheckMessage(false, "Number of arguments provided and needed by the process (" + process.getProcessId() +
                            ") are not the same (" + process.getArguments().size() + " provided) / (" + processDescriptor.getInputDescriptor().descriptors().size() + " needed)");
                }

                //Check (if possible) if the argument given is compatible with the argument needed
                for (int i=0; i<processDescriptor.getInputDescriptor().descriptors().size(); i++) {

                    GeneralParameterDescriptor inputDescriptor = processDescriptor.getInputDescriptor().descriptors().get(i);
                    ProcessDescriptionArgument processDescriptionArgument = process.getArguments().get(inputDescriptor.getName().getCode());

                    //Cannot find the needed argument in the list of passed arguments
                    if(processDescriptionArgument == null) {
                        if (inputDescriptor.getMinimumOccurs() <= 0) {
                            continue;
                        }
                        if (processId.equalsIgnoreCase("load_collection") &&
                                (inputDescriptor.getName().getCode().equalsIgnoreCase("serviceId")) ||
                                (inputDescriptor.getName().getCode().equalsIgnoreCase("external_stac_url")) ||
                                (inputDescriptor.getName().getCode().equalsIgnoreCase("external_stac_custom_process"))
                        ) {
                            //Special case for load_collection where the "serviceId" parameter is not mandatory in openEO, but is in examind
                            //Special case for load_collection where the "external_stac_url" parameter is not mandatory in openEO, but is in examind
                            //Special case for load_collection where the "external_stac_custom_process" parameter is not mandatory in openEO, but is in examind
                            continue;
                        } else if (processId.equalsIgnoreCase("load_stac") &&
                                (inputDescriptor.getName().getCode().equalsIgnoreCase("external_stac_custom_process"))
                        ) {
                            //Special case for load_stac where the "external_stac_custom_process" parameter is not mandatory in openEO, but is in examind
                            continue;
                        }

                        return new CheckMessage(false, "For the process : " + process.getProcessId() + ", no argument named " + inputDescriptor.getName().getCode() + " found");
                    }

                    if (inputDescriptor instanceof DefaultParameterDescriptor<?> inputDefaultParameterDescriptor) {
                        try {
                            if (inputDefaultParameterDescriptor.getValueType() != null) {
                                Class<?> type = inputDefaultParameterDescriptor.getValueClass();

                                switch (processDescriptionArgument.getType()) {
                                    case VALUE: //Check if the value specified is conform to the process input
                                        if (!checkClassAssignation(type, processDescriptionArgument.getValue().getClass())) {
                                            //Given input type and the needed input type are not the same
                                            return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the type specified for the argument : " +
                                                    inputDescriptor.getName().getCode() + " is not correct (" + type.toString() + " needed)" );
                                        } else {
                                            if (inputDefaultParameterDescriptor.getValidValues() != null && !inputDefaultParameterDescriptor.getValidValues().isEmpty()) {
                                                if (!inputDefaultParameterDescriptor.getValidValues().contains(processDescriptionArgument.getValue())) {
                                                    return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the type specified for the argument : " +
                                                            inputDescriptor.getName().getCode() + " is not is the list of accepted inputs (" +
                                                            inputDefaultParameterDescriptor.getValidValues().stream().map(Object::toString).toList() + ")");
                                                }
                                            }
                                        }
                                        break;

                                    case FROM_NODE: //Check if the node output value is conform to the process input
                                        String[] referencedProcessIdSplitted = processGraph.get((String) processDescriptionArgument.getValue()).getProcessId().split("\\.",2);
                                        String referencedProcessId = referencedProcessIdSplitted.length == 2 ? referencedProcessIdSplitted[1] : referencedProcessIdSplitted[0];

                                        ProcessDescriptor referencedProcess = descriptors.stream()
                                                .filter(descriptor -> descriptor.getIdentifier().getCode().equalsIgnoreCase(openEOProcessIdToExamindProcessId(referencedProcessId, false)))
                                                .findFirst()
                                                .orElse(null);

                                        if (referencedProcess == null) {
                                            return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the referenced process : " +
                                                    processDescriptionArgument.getValue() + " does not exist");
                                        }
                                        if (referencedProcess.getOutputDescriptor().descriptors().isEmpty()) {
                                            return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the referenced process : " +
                                                    processDescriptionArgument.getValue() + " has no available outputs");
                                        }

                                        //Get the first output as openEO works with processes with one output
                                        GeneralParameterDescriptor outputDescriptor = referencedProcess.getOutputDescriptor().descriptors().get(0);
                                        if (outputDescriptor instanceof DefaultParameterDescriptor<?> outputDefaultParameterDescriptor) {
                                            if (!checkClassAssignation(type, outputDefaultParameterDescriptor.getValueClass())) {
                                                return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the type of the output of the referenced process : " +
                                                        processDescriptionArgument.getValue() + " is not compatible with the type of the argument : " +
                                                        inputDescriptor.getName().getCode());
                                            }
                                        } else { //TODO: Check other type of parameters
                                            return new CheckMessage(false, "Parameter type is not a 'DefaultParameterDescriptor'");
                                        }

                                        break;

                                    case FROM_PARAMETER: //Check if the parameter value is conform to the process input
                                        ProcessParameter referencedParameter = this.getParameters().stream()
                                                .filter(parameter -> parameter.getName().equalsIgnoreCase((String) processDescriptionArgument.getValue()))
                                                .findFirst()
                                                .orElse(null);

                                        if (referencedParameter == null) {
                                            return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the referenced parameter : " +
                                                    processDescriptionArgument.getValue() + " does not exist");
                                        }
//                                        if (!checkClassAssignation(type, referencedParameter.getSchema().getType())) {
//                                            return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the type of the referenced parameter : " +
//                                                    processDescriptionArgument.getFromParameter() + " is not compatible with the type of the argument : " +
//                                                    inputDescriptor.getName().getCode());
//                                        }
                                        break;

                                    case ARRAY: //TODO: Be compatible when examind supports combination of outputs in an array
                                        if(type.isArray()) {
                                            for(var nestedProcess : (List<ProcessDescriptionArgument>) processDescriptionArgument.getValue()) {
                                                if (nestedProcess.getType() != ProcessDescriptionArgument.ArgumentType.VALUE) {
                                                    return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the content of the array for the argument : " +
                                                            inputDescriptor.getName().getCode() + " is not correct. For the moment, we only support arrays with constant values");
                                                }
                                            }

//                                            if (!checkClassAssignation(type, processDescriptionArgument.getValue().getClass())) {
//                                                return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the type specified for the argument : " +
//                                                        inputDescriptor.getName().getCode() + " is not correct (this argument doesn't accept the type of array specified)");
//                                            }
                                        } else {
                                            return new CheckMessage(false, "For the process : " + process.getProcessId() + ", the type specified for the argument : " +
                                                    inputDescriptor.getName().getCode() + " is not correct (this argument is not an array)");
                                        }
                                        break;

                                    case PROCESS_GRAPH: //TODO: Support sub-process graph
                                        return new CheckMessage(false, "Sub process graph is not supported for the moment by this implementation");
                                }
                            }

                        } catch (NullPointerException ex) {
                            //Do nothing, but I need to add this catch otherwise, sometimes, even if we check if it's null, this exception is raised
                        }
                    } else { //TODO: Check other type of parameters
                        return new CheckMessage(false, "Parameter type is not a 'DefaultParameterDescriptor'");
                    }
                }

                //TODO: Maybe, add a check for the outputs ?
            } else {
                return new CheckMessage(false, "No available process with this id : " + process.getProcessId());
            }
        }

        return new CheckMessage(true, null);
    }

    private CheckMessage isProcessDescriptionArgumentValid(ProcessDescriptionArgument arg) {

        if (arg.getType() == ProcessDescriptionArgument.ArgumentType.FROM_NODE) {
            String fromNode = arg.getValue().toString();
            if (processGraph.containsKey(fromNode)) {
                return new CheckMessage(true, null);
            } else {
                return new CheckMessage(false, "Argument 'from_node' (" + fromNode + ") is not present in the process graph (no process with this name)");
            }
        }

        if (arg.getType() == ProcessDescriptionArgument.ArgumentType.FROM_PARAMETER) {
            String fromParameter = arg.getValue().toString();
            boolean found = false;
            for (ProcessParameter param : parameters) {
                if (param.getName().equals(fromParameter)) {
                    found = true;
                    break;
                }
            }
            if (found){
                return new CheckMessage(true, null);
            } else {
                return new CheckMessage(false, "Argument 'from_parameter' (" + fromParameter + ") is not present in the parameters list (no parameter with this name)");
            }
        }

        if (arg.getType() == ProcessDescriptionArgument.ArgumentType.ARRAY) {
            List<ProcessDescriptionArgument> nestedArgs = (List<ProcessDescriptionArgument>) arg.getValue();
            for (ProcessDescriptionArgument nestedArg : nestedArgs) {
                CheckMessage checkMessage = isProcessDescriptionArgumentValid(nestedArg);
                if (!checkMessage.isValid()) {
                    return checkMessage;
                }
            }
            return new CheckMessage(true, null);
        }

        return new CheckMessage(true, null);
    }

    private boolean checkClassAssignation(Class<?> needed, Class<?> provided) {
        if (needed.getName().contains("Envelope") && provided.getName().contains("BoundingBox")) {
            return true;
        }
        if (needed == Double.class && provided == Integer.class) {
            return true;
        }
        if (needed == Float.class && provided == Integer.class) {
            return true;
        }

        return needed.isAssignableFrom(provided);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Process process = (Process) o;
        return Objects.equals(id, process.id) && Objects.equals(summary, process.summary) && Objects.equals(description, process.description)
                && Objects.equals(categories, process.categories) && Objects.equals(parameters, process.parameters)
                && Objects.equals(returns, process.returns) && Objects.equals(deprecated, process.deprecated)
                && Objects.equals(experimental, process.experimental) && Objects.equals(exceptions, process.exceptions)
                && Objects.equals(examples, process.examples) && Objects.equals(links, process.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, summary, description, categories, parameters, returns, deprecated, experimental, exceptions, examples, links);
    }
}
