/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.processing.chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.processing.chain.model.Chain;
import org.geotoolkit.processing.chain.model.Element;
import org.geotoolkit.processing.chain.model.ElementProcess;
import org.geotoolkit.processing.chain.model.FlowLink;

/**
 * Utility methods to manipulate process chains.
 *
 * @author Johann Sorel (Geomatys)
 */
final class Flow {

    private Flow() {}

    /**
     * Analyze the model and create a Node graph.
     * Node objects can be :
     * - Collection<Parameter> inputs
     * - Collection<Parameter> outputs
     * - Constant
     * - Descriptor
     *
     * Links are translated with the getChildren method.
     */
    static Collection<FlowNode> createFlow(final Chain chain){

        final FlowNode inputNode = new FlowNode(ElementProcess.BEGIN,true,false);
        final FlowNode outputNode = new FlowNode(ElementProcess.END,false,true);

        final FlowNode inputParamNode  = new FlowNode(chain.getInputs(),true,false);
        final FlowNode outputParamNode = new FlowNode(chain.getOutputs(),false,true);

        final Map<Object,FlowNode> nodes = new HashMap<Object,FlowNode>();
        nodes.put(inputNode,inputNode);
        nodes.put(outputNode,outputNode);
        nodes.put(inputParamNode,inputParamNode);
        nodes.put(outputParamNode,outputParamNode);

        for(Object obj : chain.getElements()) {nodes.put(obj,new FlowNode(obj,false,false));}

        for(FlowLink link : chain.getFlowLinks()){
            Object source = link.getSource(chain);
            Object target = link.getTarget(chain);

            if(source.equals(ElementProcess.BEGIN)){
                source = inputNode;
            }
            if(target.equals(ElementProcess.END)){
                target = outputNode;
            }

            final FlowNode sourceNode = nodes.get(source);
            final FlowNode targetNode = nodes.get(target);

            sourceNode.children.add(targetNode);
            sourceNode.links.add(link);
        }

        return nodes.values();
    }

    /**
     * Sort Nodes by execution order.
     * Each rank is a list of nodes which are necessarily
     * executed after the previous rank and before the next.
     *
     * @param nodes
     * @return List
     */
    static List<List<FlowNode>> sortByRank(Collection<FlowNode> nodes){

        nodes = new ArrayList<FlowNode>(nodes);

        final List<List<FlowNode>> ranked = new ArrayList<List<FlowNode>>();

        //extract in/out/begin/end nodes
        FlowNode inNode = null;
        FlowNode outNode = null;
        FlowNode beginNode = null;
        FlowNode endNode = null;
        for(FlowNode n : nodes){
            if (n.isInput) {
                if (n.object instanceof Element) {
                    beginNode = n;
                } else {
                    inNode = n;
                }
            } else if (n.isOutput) {
                if (n.object instanceof Element) {
                    endNode = n;
                } else {
                    outNode = n;
                }
            }
        }
        nodes.remove(beginNode);
        nodes.remove(inNode);
        nodes.remove(endNode);
        nodes.remove(outNode);

        while(!nodes.isEmpty()){
            final List<FlowNode> step = new ArrayList<FlowNode>();
            ranked.add(step);

            loop:
            for(FlowNode candidate : nodes){
                //check amought remaining nodes if one of them is a parent of this node
                for(FlowNode parent : nodes){
                    if(parent.children.contains(candidate)){
                        continue loop;
                    }
                }

                step.add(candidate);
            }

            nodes.removeAll(step);
        }

        //always place input and output parameters alone in first and last position
        ranked.add(0, Arrays.asList(inNode, beginNode));
        ranked.add(Arrays.asList(outNode, endNode));

        return ranked;
    }

}
