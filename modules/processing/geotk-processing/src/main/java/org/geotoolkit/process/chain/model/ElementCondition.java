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
package org.geotoolkit.process.chain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ElementCondition extends Element implements Parameterized{

    @XmlElement(name = "syntax")
    private String syntax;

    @XmlElement(name = "expression")
    private String expression;

    @XmlElement(name="inputs")
    private List<Parameter> inputs;

    @XmlElement(name="outputs")
    private List<Parameter> outputs;

    @XmlElement(name="success")
    private List<FlowLink> success;

    @XmlElement(name="fail")
    private List<FlowLink> failed;

    public ElementCondition() {

    }

    public ElementCondition(final ElementCondition condition) {
        super(condition.id);
        this.expression = condition.getExpression();
        for (Parameter cdt : condition.getInputs()) {
            getInputs().add(new Parameter(cdt));
        }
        for (FlowLink cdt : condition.getSuccess()) {
            getSuccess().add(new FlowLink(cdt));
        }
        for (FlowLink cdt : condition.getFailed()) {
            getFailed().add(new FlowLink(cdt));
        }
    }

    public ElementCondition(Integer id) {
        super(id);
    }

    public ElementCondition(final Integer id, final List<Parameter> inputs, final List<FlowLink> success,
            final List<FlowLink> fail, final String expression, final int x, final int y) {
        super(id,x, y);
        this.inputs = inputs;
        this.success = success;
        this.failed = fail;
        this.expression = expression;
    }

    public final List<Parameter> getInputs() {
        if (inputs == null) {
            inputs = new ArrayList<Parameter>();
        }
        return inputs;
    }

    public void setInputs(List<Parameter> inputs) {
        this.inputs = inputs;
    }

    public final List<FlowLink> getSuccess() {
        if (success == null) {
            success = new ArrayList<FlowLink>();
        }
        return success;
    }

    public void setSuccess(List<FlowLink> success) {
        this.success = success;
    }

    public final List<FlowLink> getFailed() {
        if (failed == null) {
            failed = new ArrayList<FlowLink>();
        }
        return failed;
    }

    public void setFailed(List<FlowLink> fail) {
        this.failed = fail;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ElementCondition) { // no looking for position
            final ElementCondition that = (ElementCondition) obj;
            return Objects.equals(this.id,        that.id)
                && Objects.equals(this.inputs,    that.inputs)
                && Objects.equals(this.outputs,    that.outputs)
                && Objects.equals(this.success,   that.success)
                && Objects.equals(this.failed,      that.failed)
                && Objects.equals(this.expression,that.expression);
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("id:").append(id).append('\n');
        if (inputs != null) {
            sb.append("inputs:").append(inputs).append('\n');
        }
        if (outputs != null) {
            sb.append("outputs:").append(outputs).append('\n');
        }
        if (success != null) {
            sb.append("success:").append(success).append('\n');
        }
        if (failed != null) {
            sb.append("fail:").append(failed).append('\n');
        }
        if (expression != null) {
            sb.append("expression:").append(expression).append('\n');
        }
        return sb.toString();
    }

    @Override
    public Element copy() {
        return new ElementCondition(this);
    }

    @Override
    public List<Parameter> getOutputs() {
        return Collections.emptyList();
    }

    @Override
    public void setOutputs(List<Parameter> outputs) {
    }

}
