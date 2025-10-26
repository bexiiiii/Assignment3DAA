package main.java;

import java.util.*;

public class KruskalAlgorithm {
    private int operationsCount;
    
    public MSTResult findMST(Graph graph) {
        operationsCount = 0;
        long startTime = System.nanoTime();
        
        List<Edge> mstEdges = new ArrayList<>();
        int totalCost = 0;
        
        // Sort edges by weight
        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        Collections.sort(sortedEdges);
        operationsCount += sortedEdges.size(); // counting sort operations
        
        // Initialize Union-Find
        UnionFind uf = new UnionFind(graph.getNodeNames());
        
        for (Edge edge : sortedEdges) {
            operationsCount++; // iterating through edges
            
            if (!uf.isConnected(edge.getFrom(), edge.getTo())) {
                operationsCount++; // find operation
                mstEdges.add(edge);
                totalCost += edge.getWeight();
                uf.union(edge.getFrom(), edge.getTo());
                operationsCount++; // union operation
                
                if (mstEdges.size() == graph.getVertices() - 1) {
                    break;
                }
            } else {
                operationsCount++; // find operation
            }
        }
        
        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        
        return new MSTResult(mstEdges, totalCost, operationsCount, executionTimeMs);
    }
    
    private class UnionFind {
        private final Map<String, String> parent;
        private final Map<String, Integer> rank;
        
        public UnionFind(List<String> nodes) {
            parent = new HashMap<>();
            rank = new HashMap<>();
            for (String node : nodes) {
                parent.put(node, node);
                rank.put(node, 0);
                operationsCount++; // initialization
            }
        }
        
        public String find(String node) {
            if (!parent.get(node).equals(node)) {
                parent.put(node, find(parent.get(node))); // path compression
                operationsCount++; // find operation
            }
            operationsCount++; // find operation
            return parent.get(node);
        }
        
        public void union(String node1, String node2) {
            String root1 = find(node1);
            String root2 = find(node2);
            
            if (!root1.equals(root2)) {
                if (rank.get(root1) < rank.get(root2)) {
                    parent.put(root1, root2);
                } else if (rank.get(root1) > rank.get(root2)) {
                    parent.put(root2, root1);
                } else {
                    parent.put(root2, root1);
                    rank.put(root1, rank.get(root1) + 1);
                }
                operationsCount++; // union operation
            }
        }
        
        public boolean isConnected(String node1, String node2) {
            return find(node1).equals(find(node2));
        }
    }
}
