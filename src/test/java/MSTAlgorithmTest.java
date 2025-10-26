package test.java;

import main.java.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class MSTAlgorithmTest {
    
    @Test
    public void testPrimAlgorithmGraph1() {
        // Graph 1: A-B-C-D-E
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges = Arrays.asList(
            new Edge("A", "B", 4),
            new Edge("A", "C", 3),
            new Edge("B", "C", 2),
            new Edge("B", "D", 5),
            new Edge("C", "D", 7),
            new Edge("C", "E", 8),
            new Edge("D", "E", 6)
        );
        
        Graph graph = new Graph(nodes, edges);
        PrimAlgorithm prim = new PrimAlgorithm();
        MSTResult result = prim.findMST(graph);
        
        assertEquals(16, result.getTotalCost());
        assertEquals(4, result.getMstEdges().size()); // V-1 edges
        assertTrue(result.getOperationsCount() > 0);
    }
    
    @Test
    public void testKruskalAlgorithmGraph1() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        List<Edge> edges = Arrays.asList(
            new Edge("A", "B", 4),
            new Edge("A", "C", 3),
            new Edge("B", "C", 2),
            new Edge("B", "D", 5),
            new Edge("C", "D", 7),
            new Edge("C", "E", 8),
            new Edge("D", "E", 6)
        );
        
        Graph graph = new Graph(nodes, edges);
        KruskalAlgorithm kruskal = new KruskalAlgorithm();
        MSTResult result = kruskal.findMST(graph);
        
        assertEquals(16, result.getTotalCost());
        assertEquals(4, result.getMstEdges().size());
        assertTrue(result.getOperationsCount() > 0);
    }
    
    @Test
    public void testPrimAlgorithmGraph2() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
            new Edge("A", "B", 1),
            new Edge("A", "C", 4),
            new Edge("B", "C", 2),
            new Edge("C", "D", 3),
            new Edge("B", "D", 5)
        );
        
        Graph graph = new Graph(nodes, edges);
        PrimAlgorithm prim = new PrimAlgorithm();
        MSTResult result = prim.findMST(graph);
        
        assertEquals(6, result.getTotalCost());
        assertEquals(3, result.getMstEdges().size()); // V-1 edges
    }
    
    @Test
    public void testKruskalAlgorithmGraph2() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
            new Edge("A", "B", 1),
            new Edge("A", "C", 4),
            new Edge("B", "C", 2),
            new Edge("C", "D", 3),
            new Edge("B", "D", 5)
        );
        
        Graph graph = new Graph(nodes, edges);
        KruskalAlgorithm kruskal = new KruskalAlgorithm();
        MSTResult result = kruskal.findMST(graph);
        
        assertEquals(6, result.getTotalCost());
        assertEquals(3, result.getMstEdges().size());
    }
    
    @Test
    public void testBothAlgorithmsProduceSameCost() {
        List<String> nodes = Arrays.asList("A", "B", "C", "D");
        List<Edge> edges = Arrays.asList(
            new Edge("A", "B", 1),
            new Edge("A", "C", 4),
            new Edge("B", "C", 2),
            new Edge("C", "D", 3),
            new Edge("B", "D", 5)
        );
        
        Graph graph = new Graph(nodes, edges);
        
        PrimAlgorithm prim = new PrimAlgorithm();
        MSTResult primResult = prim.findMST(graph);
        
        KruskalAlgorithm kruskal = new KruskalAlgorithm();
        MSTResult kruskalResult = kruskal.findMST(graph);
        
        // Both algorithms should produce the same total cost
        assertEquals(primResult.getTotalCost(), kruskalResult.getTotalCost());
        assertEquals(primResult.getMstEdges().size(), kruskalResult.getMstEdges().size());
    }
    
    @Test
    public void testSimpleTriangle() {
        List<String> nodes = Arrays.asList("A", "B", "C");
        List<Edge> edges = Arrays.asList(
            new Edge("A", "B", 1),
            new Edge("B", "C", 2),
            new Edge("A", "C", 3)
        );
        
        Graph graph = new Graph(nodes, edges);
        
        PrimAlgorithm prim = new PrimAlgorithm();
        MSTResult primResult = prim.findMST(graph);
        
        assertEquals(3, primResult.getTotalCost()); // Should pick edges 1 and 2
        assertEquals(2, primResult.getMstEdges().size());
    }
}
