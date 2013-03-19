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
package org.geotoolkit.process.chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.process.chain.model.Chain;
import org.geotoolkit.process.chain.model.ChainElement;

/**
 * Utility methods to manipulate process chains.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ChainFlow {

    static final class ExecutionNode{

        private final Object object;
        private final List<ExecutionNode> children = new ArrayList<ExecutionNode>();
        private final List<Chain.ExecutionLinkDto> links = new ArrayList<Chain.ExecutionLinkDto>();
        private final boolean isInput;
        private final boolean isOutput;

        private ExecutionNode(Object object, boolean isInput, boolean isOutput) {
            this.object = object;
            this.isInput = isInput;
            this.isOutput = isOutput;
        }

        public Object getObject() {
            return object;
        }

        public List<ExecutionNode> getChildren() {
            return children;
        }

        public List<Chain.ExecutionLinkDto> getLinks() {
            return links;
        }

        public boolean isInput() {
            return isInput;
        }

        public boolean isOutput() {
            return isOutput;
        }

    }

    private ChainFlow() {}

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
    static Collection<ExecutionNode> createExecutionFlow(final Chain chain){

        final ExecutionNode inputNode = new ExecutionNode(ChainElement.BEGIN,true,false);
        final ExecutionNode outputNode = new ExecutionNode(ChainElement.END,false,true);

        final ExecutionNode inputParamNode  = new ExecutionNode(chain.getInputs(),true,false);
        final ExecutionNode outputParamNode = new ExecutionNode(chain.getOutputs(),false,true);

        final Map<Object,ExecutionNode> nodes = new HashMap<Object,ExecutionNode>();
        nodes.put(inputNode,inputNode);
        nodes.put(outputNode,outputNode);
        nodes.put(inputParamNode,inputParamNode);
        nodes.put(outputParamNode,outputParamNode);

        for(Object obj : chain.getChainElements()) {nodes.put(obj,new ExecutionNode(obj,false,false));}

        for(Chain.ExecutionLinkDto link : chain.getExecutionLinks()){
            Object source = link.getSource(chain);
            Object target = link.getTarget(chain);

            if(source.equals(ChainElement.BEGIN)){
                source = inputNode;
            }
            if(target.equals(ChainElement.END)){
                target = outputNode;
            }

            final ExecutionNode sourceNode = nodes.get(source);
            final ExecutionNode targetNode = nodes.get(target);

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
    static List<List<ExecutionNode>> sortByRankExec(Collection<ExecutionNode> nodes){

        nodes = new ArrayList<ExecutionNode>(nodes);

        final List<List<ExecutionNode>> ranked = new ArrayList<List<ExecutionNode>>();

        //extract in/out/begin/end nodes
        ExecutionNode inNode = null;
        ExecutionNode outNode = null;
        ExecutionNode beginNode = null;
        ExecutionNode endNode = null;
        for(ExecutionNode n : nodes){
            if (n.isInput) {
                if (n.object instanceof ChainElement) {
                    beginNode = n;
                } else {
                    inNode = n;
                }
            } else if (n.isOutput) {
                if (n.object instanceof ChainElement) {
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
            final List<ExecutionNode> step = new ArrayList<ExecutionNode>();
            ranked.add(step);

            loop:
            for(ExecutionNode candidate : nodes){
                //check amought remaining nodes if one of them is a parent of this node
                for(ExecutionNode parent : nodes){
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
