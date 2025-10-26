package main.java;

import java.util.*;

public class GraphGenerator {
    private final Random random;
    
    public GraphGenerator(long seed) {
        this.random = new Random(seed);
    }
    
    public GraphGenerator() {
        this(System.currentTimeMillis());
    }
    
    /**
     * Generate a connected graph with specified vertices and edges
     * @param vertices Number of vertices
     * @param edges Number of edges (must be >= vertices - 1)
     * @param maxWeight Maximum edge weight
     * @return Generated graph
     */
    public Graph generateGraph(int vertices, int edges, int maxWeight) {
        if (edges < vertices - 1) {
            throw new IllegalArgumentException("Number of edges must be at least vertices - 1 to ensure connectivity");
        }
        
        if (edges > vertices * (vertices - 1) / 2) {
            throw new IllegalArgumentException("Too many edges for the number of vertices");
        }
        
        // Generate vertex names
        List<String> nodeNames = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            nodeNames.add("V" + i);
        }
        
        // Generate spanning tree first to ensure connectivity
        List<Edge> edgeList = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        visited.add(nodeNames.get(0));
        
        // Build a random spanning tree
        for (int i = 1; i < vertices; i++) {
            String from = nodeNames.get(random.nextInt(i));
            String to = nodeNames.get(i);
            int weight = random.nextInt(maxWeight) + 1;
            edgeList.add(new Edge(from, to, weight));
            visited.add(to);
        }
        
        // Add remaining edges randomly
        Set<String> existingEdges = new HashSet<>();
        for (Edge edge : edgeList) {
            existingEdges.add(getEdgeKey(edge.getFrom(), edge.getTo()));
        }
        
        int remainingEdges = edges - (vertices - 1);
        int attempts = 0;
        int maxAttempts = remainingEdges * 10;
        
        while (edgeList.size() < edges && attempts < maxAttempts) {
            int v1 = random.nextInt(vertices);
            int v2 = random.nextInt(vertices);
            
            if (v1 != v2) {
                String from = nodeNames.get(v1);
                String to = nodeNames.get(v2);
                String edgeKey = getEdgeKey(from, to);
                
                if (!existingEdges.contains(edgeKey)) {
                    int weight = random.nextInt(maxWeight) + 1;
                    edgeList.add(new Edge(from, to, weight));
                    existingEdges.add(edgeKey);
                }
            }
            attempts++;
        }
        
        return new Graph(nodeNames, edgeList);
    }
    
    private String getEdgeKey(String v1, String v2) {
        // Ensure consistent key regardless of order
        return v1.compareTo(v2) < 0 ? v1 + "-" + v2 : v2 + "-" + v1;
    }
    
    /**
     * Generate test graphs according to specification:
     * - small: 10 graphs with < 30 vertices
     * - medium: 10 graphs with < 300 vertices  
     * - large: 10 graphs with < 1000 vertices
     * - extra: 5 graphs with < 300 vertices
     */
    public static List<TestGraphSpec> generateTestSpecification() {
        List<TestGraphSpec> specs = new ArrayList<>();
        
        // Small graphs (10 graphs, < 30 vertices)
        for (int i = 0; i < 10; i++) {
            int vertices = 5 + i * 2; // 5, 7, 9, ..., 23
            int edges = Math.min(vertices * 2, vertices * (vertices - 1) / 2);
            specs.add(new TestGraphSpec("small_" + (i + 1), vertices, edges, 100));
        }
        
        // Medium graphs (10 graphs, < 300 vertices)
        for (int i = 0; i < 10; i++) {
            int vertices = 50 + i * 25; // 50, 75, 100, ..., 275
            int edges = Math.min(vertices * 3, vertices * (vertices - 1) / 2);
            specs.add(new TestGraphSpec("medium_" + (i + 1), vertices, edges, 1000));
        }
        
        // Large graphs (10 graphs, < 1000 vertices)
        for (int i = 0; i < 10; i++) {
            int vertices = 100 + i * 90; // 100, 190, 280, ..., 910
            int edges = Math.min(vertices * 4, vertices * (vertices - 1) / 2);
            specs.add(new TestGraphSpec("large_" + (i + 1), vertices, edges, 10000));
        }
        
        // Extra large graphs (5 graphs, < 300 vertices)
        for (int i = 0; i < 5; i++) {
            int vertices = 100 + i * 40; // 100, 140, 180, 220, 260
            int edges = Math.min(vertices * 4, vertices * (vertices - 1) / 2);
            specs.add(new TestGraphSpec("extra_" + (i + 1), vertices, edges, 1000));
        }
        
        return specs;
    }
    
    public static class TestGraphSpec {
        private final String name;
        private final int vertices;
        private final int edges;
        private final int maxWeight;
        
        public TestGraphSpec(String name, int vertices, int edges, int maxWeight) {
            this.name = name;
            this.vertices = vertices;
            this.edges = edges;
            this.maxWeight = maxWeight;
        }
        
        public String getName() {
            return name;
        }
        
        public int getVertices() {
            return vertices;
        }
        
        public int getEdges() {
            return edges;
        }
        
        public int getMaxWeight() {
            return maxWeight;
        }
        
        @Override
        public String toString() {
            return String.format("%s: V=%d, E=%d, MaxWeight=%d", name, vertices, edges, maxWeight);
        }
    }
}
