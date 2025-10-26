package main.java;

import java.util.List;

public class GraphData {
    private int id;
    private List<String> nodes;
    private List<EdgeData> edges;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public List<String> getNodes() {
        return nodes;
    }
    
    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
    
    public List<EdgeData> getEdges() {
        return edges;
    }
    
    public void setEdges(List<EdgeData> edges) {
        this.edges = edges;
    }
    
    public static class EdgeData {
        private String from;
        private String to;
        private int weight;
        
        public String getFrom() {
            return from;
        }
        
        public void setFrom(String from) {
            this.from = from;
        }
        
        public String getTo() {
            return to;
        }
        
        public void setTo(String to) {
            this.to = to;
        }
        
        public int getWeight() {
            return weight;
        }
        
        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}
