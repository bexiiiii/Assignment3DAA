package main.java;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class BenchmarkRunner {
    
    public static void main(String[] args) {
        String outputFile = args.length > 0 ? args[0] : "benchmark_results.json";
        
        System.out.println("=".repeat(60));
        System.out.println("MST Algorithms Benchmark");
        System.out.println("=".repeat(60));
        System.out.println();
        
        GraphGenerator generator = new GraphGenerator(42); // Fixed seed for reproducibility
        List<GraphGenerator.TestGraphSpec> specs = GraphGenerator.generateTestSpecification();
        
        System.out.println("Test specification:");
        System.out.println("- Small graphs:  10 graphs with < 30 vertices");
        System.out.println("- Medium graphs: 10 graphs with < 300 vertices");
        System.out.println("- Large graphs:  10 graphs with < 1000 vertices");
        System.out.println("- Extra graphs:  5 graphs with < 3000 vertices");
        System.out.println("Total: " + specs.size() + " test graphs");
        System.out.println();
        
        List<BenchmarkResult> results = new ArrayList<>();
        
        for (int i = 0; i < specs.size(); i++) {
            GraphGenerator.TestGraphSpec spec = specs.get(i);
            System.out.printf("[%2d/%2d] Testing %s...", i + 1, specs.size(), spec.getName());
            
            try {
                Graph graph = generator.generateGraph(spec.getVertices(), spec.getEdges(), spec.getMaxWeight());
                
                // Run Prim's algorithm
                PrimAlgorithm prim = new PrimAlgorithm();
                MSTResult primResult = prim.findMST(graph);
                
                // Run Kruskal's algorithm
                KruskalAlgorithm kruskal = new KruskalAlgorithm();
                MSTResult kruskalResult = kruskal.findMST(graph);
                
                // Verify results match
                if (primResult.getTotalCost() != kruskalResult.getTotalCost()) {
                    System.out.println(" ✗ MISMATCH!");
                    System.err.println("Error: Prim and Kruskal produced different costs!");
                    continue;
                }
                
                results.add(new BenchmarkResult(
                    spec.getName(),
                    spec.getVertices(),
                    spec.getEdges(),
                    primResult,
                    kruskalResult
                ));
                
                System.out.printf(" ✓ (Cost: %d)\n", primResult.getTotalCost());
                
            } catch (Exception e) {
                System.out.println(" ✗ ERROR: " + e.getMessage());
            }
        }
        
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("Benchmark completed!");
        System.out.println("=".repeat(60));
        
        // Save results
        saveResults(results, outputFile);
        
        // Print summary
        printSummary(results);
    }
    
    private static void saveResults(List<BenchmarkResult> results, String outputFile) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject output = new JsonObject();
        
        JsonArray resultsArray = new JsonArray();
        for (BenchmarkResult result : results) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", result.getName());
            obj.addProperty("vertices", result.getVertices());
            obj.addProperty("edges", result.getEdges());
            obj.addProperty("mst_cost", result.getPrimResult().getTotalCost());
            
            JsonObject prim = new JsonObject();
            prim.addProperty("operations", result.getPrimResult().getOperationsCount());
            prim.addProperty("time_ms", result.getPrimResult().getExecutionTimeMs());
            obj.add("prim", prim);
            
            JsonObject kruskal = new JsonObject();
            kruskal.addProperty("operations", result.getKruskalResult().getOperationsCount());
            kruskal.addProperty("time_ms", result.getKruskalResult().getExecutionTimeMs());
            obj.add("kruskal", kruskal);
            
            resultsArray.add(obj);
        }
        
        output.add("results", resultsArray);
        
        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(output, writer);
            System.out.println("\nResults saved to: " + outputFile);
        } catch (IOException e) {
            System.err.println("Error saving results: " + e.getMessage());
        }
    }
    
    private static void printSummary(List<BenchmarkResult> results) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PERFORMANCE SUMMARY");
        System.out.println("=".repeat(80));
        
        Map<String, List<BenchmarkResult>> categories = new HashMap<>();
        categories.put("Small", new ArrayList<>());
        categories.put("Medium", new ArrayList<>());
        categories.put("Large", new ArrayList<>());
        categories.put("Extra", new ArrayList<>());
        
        for (BenchmarkResult result : results) {
            if (result.getName().startsWith("small")) {
                categories.get("Small").add(result);
            } else if (result.getName().startsWith("medium")) {
                categories.get("Medium").add(result);
            } else if (result.getName().startsWith("large")) {
                categories.get("Large").add(result);
            } else if (result.getName().startsWith("extra")) {
                categories.get("Extra").add(result);
            }
        }
        
        for (String category : Arrays.asList("Small", "Medium", "Large", "Extra")) {
            List<BenchmarkResult> categoryResults = categories.get(category);
            if (categoryResults.isEmpty()) continue;
            
            System.out.println("\n" + category + " Graphs:");
            System.out.println("-".repeat(80));
            System.out.printf("%-15s %8s %8s | %12s %12s | %12s %12s\n",
                "Name", "Vertices", "Edges", 
                "Prim Ops", "Prim Time", 
                "Kruskal Ops", "Kruskal Time");
            System.out.println("-".repeat(80));
            
            double totalPrimTime = 0;
            double totalKruskalTime = 0;
            int primWins = 0;
            int kruskalWins = 0;
            
            for (BenchmarkResult result : categoryResults) {
                System.out.printf("%-15s %8d %8d | %12d %10.2fms | %12d %10.2fms",
                    result.getName(),
                    result.getVertices(),
                    result.getEdges(),
                    result.getPrimResult().getOperationsCount(),
                    result.getPrimResult().getExecutionTimeMs(),
                    result.getKruskalResult().getOperationsCount(),
                    result.getKruskalResult().getExecutionTimeMs()
                );
                
                if (result.getPrimResult().getExecutionTimeMs() < result.getKruskalResult().getExecutionTimeMs()) {
                    System.out.println(" ← Prim");
                    primWins++;
                } else {
                    System.out.println(" ← Kruskal");
                    kruskalWins++;
                }
                
                totalPrimTime += result.getPrimResult().getExecutionTimeMs();
                totalKruskalTime += result.getKruskalResult().getExecutionTimeMs();
            }
            
            System.out.println("-".repeat(80));
            System.out.printf("Average time: Prim = %.2fms, Kruskal = %.2fms\n",
                totalPrimTime / categoryResults.size(),
                totalKruskalTime / categoryResults.size());
            System.out.printf("Faster: Prim = %d times, Kruskal = %d times\n", primWins, kruskalWins);
        }
        
        System.out.println("\n" + "=".repeat(80));
    }
    
    private static class BenchmarkResult {
        private final String name;
        private final int vertices;
        private final int edges;
        private final MSTResult primResult;
        private final MSTResult kruskalResult;
        
        public BenchmarkResult(String name, int vertices, int edges, MSTResult primResult, MSTResult kruskalResult) {
            this.name = name;
            this.vertices = vertices;
            this.edges = edges;
            this.primResult = primResult;
            this.kruskalResult = kruskalResult;
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
        
        public MSTResult getPrimResult() {
            return primResult;
        }
        
        public MSTResult getKruskalResult() {
            return kruskalResult;
        }
    }
}
