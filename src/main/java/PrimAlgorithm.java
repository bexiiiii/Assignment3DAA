package main.java;

import java.util.*;

public class PrimAlgorithm {
    private int operationsCount;
    
    public MSTResult findMST(Graph graph) {
        operationsCount = 0;
        long startTime = System.nanoTime();
        
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;
        int vertices = graph.getVertices();
        
        // Build adjacency list
        Map<String, List<EdgeWithNode>> adjacencyList = new HashMap<>();
        for (String node : graph.getNodeNames()) {
            adjacencyList.put(node, new ArrayList<>());
            operationsCount++; // initialization
        }
        
        for (Edge edge : graph.getEdges()) {
            adjacencyList.get(edge.getFrom()).add(new EdgeWithNode(edge.getTo(), edge.getWeight()));
            adjacencyList.get(edge.getTo()).add(new EdgeWithNode(edge.getFrom(), edge.getWeight()));
            operationsCount += 2; // adding edges to adjacency list
        }
        
        // Prim's algorithm
        Set<String> visited = new HashSet<>();
        PriorityQueue<EdgeWithNode> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        
        // Start from the first node
        String startNode = graph.getNodeNames().get(0);
        visited.add(startNode);
        operationsCount++; // adding start node to visited
        
        for (EdgeWithNode edge : adjacencyList.get(startNode)) {
            pq.offer(edge);
            operationsCount++; // adding to priority queue
        }
        
        while (!pq.isEmpty() && visited.size() < vertices) {
            EdgeWithNode current = pq.poll();
            operationsCount++; // polling from priority queue
            
            if (visited.contains(current.node)) {
                operationsCount++; // checking if visited
                continue;
            }
            
            // Find the edge that connects to this node from visited set
            String fromNode = null;
            for (String visitedNode : visited) {
                for (EdgeWithNode edge : adjacencyList.get(visitedNode)) {
                    if (edge.node.equals(current.node) && edge.weight == current.weight) {
                        fromNode = visitedNode;
                        break;
                    }
                }
                if (fromNode != null) break;
            }
            
            if (fromNode != null) {
                mstEdges.add(new Edge(fromNode, current.node, current.weight));
                totalCost += current.weight;
                operationsCount++; // adding edge to MST
            }
            
            visited.add(current.node);
            operationsCount++; // adding to visited
            
            for (EdgeWithNode edge : adjacencyList.get(current.node)) {
                if (!visited.contains(edge.node)) {
                    pq.offer(edge);
                    operationsCount++; // adding to priority queue
                }
                operationsCount++; // checking if visited
            }
        }
        
        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        
        return new MSTResult(mstEdges, totalCost, operationsCount, executionTimeMs);
    }
    
    private static class EdgeWithNode {
        String node;
        int weight;
        
        EdgeWithNode(String node, int weight) {
            this.node = node;
            this.weight = weight;
        }
    }
}
