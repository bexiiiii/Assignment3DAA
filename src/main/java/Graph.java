package main.java;

import java.util.*;

public class Graph {
    private final int vertices;
    private final Map<String, Integer> nodeIndexMap;
    private final List<String> nodeNames;
    private final List<Edge> edges;
    
    public Graph(List<String> nodes, List<Edge> edges) {
        this.vertices = nodes.size();
        this.nodeNames = new ArrayList<>(nodes);
        this.nodeIndexMap = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeIndexMap.put(nodes.get(i), i);
        }
        this.edges = new ArrayList<>(edges);
    }
    
    public int getVertices() {
        return vertices;
    }
    
    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }
    
    public String getNodeName(int index) {
        return nodeNames.get(index);
    }
    
    public int getNodeIndex(String name) {
        return nodeIndexMap.get(name);
    }
    
    public List<String> getNodeNames() {
        return new ArrayList<>(nodeNames);
    }
}
