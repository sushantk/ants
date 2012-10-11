package ants;

import org.codehaus.jackson.JsonNode;

/**
 * Wrapper over jackson node class
 */
public final class ObjectTree {

    private JsonNode node;

    public ObjectTree(JsonNode node) {
        this.node = node;
    }

    public JsonNode getNode() {
        return this.node;
    }

    public String toString() {
        return this.node.toString();
    }
}
