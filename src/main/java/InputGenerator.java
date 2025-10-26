package main.java;

import com.google.gson.*;
import java.io.*;
import java.util.*;

/**
 * Generates input.json file with graphs of all categories:
 * - Small (5): < 30 vertices
 * - Medium (10): < 300 vertices
 * - Large (10): < 1000 vertices
 * - Extra (5): < 3000 vertices
 */
public class InputGenerator {
    
    public static void main(String[] args) {
        String outputFile = args.length > 0 ? args[0] : "input.json";
        
        System.out.println("=".repeat(60));
        System.out.println("Generating input.json with all graph categories");
        System.out.println("=".repeat(60));
        System.out.println();
        
        GraphGenerator generator = new GraphGenerator(42); // Fixed seed
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        JsonObject root = new JsonObject();
        JsonArray graphs = new JsonArray();
        
        int graphId = 1;
        
        // Small graphs (5 graphs, < 30 vertices)
        System.out.println("Generating Small graphs (5)...");
        for (int i = 0; i < 5; i++) {
            int vertices = 8 + i * 4; // 8, 12, 16, 20, 24
            int edges = Math.min(vertices * 2, vertices * (vertices - 1) / 2);
            
            Graph graph = generator.generateGraph(vertices, edges, 100);
            JsonObject graphObj = graphToJson(graph, graphId++, "small_" + (i + 1));
            graphs.add(graphObj);
            
            System.out.printf("  ✓ small_%d: V=%d, E=%d\n", i + 1, vertices, edges);
        }
        System.out.println();
        
        // Medium graphs (10 graphs, < 300 vertices)
        System.out.println("Generating Medium graphs (10)...");
        for (int i = 0; i < 10; i++) {
            int vertices = 50 + i * 25; // 50, 75, 100, ..., 275
            int edges = Math.min(vertices * 3, vertices * (vertices - 1) / 2);
            
            Graph graph = generator.generateGraph(vertices, edges, 1000);
            JsonObject graphObj = graphToJson(graph, graphId++, "medium_" + (i + 1));
            graphs.add(graphObj);
            
            System.out.printf("  ✓ medium_%d: V=%d, E=%d\n", i + 1, vertices, edges);
        }
        System.out.println();
        
        // Large graphs (10 graphs, < 1000 vertices)
        System.out.println("Generating Large graphs (10)...");
        for (int i = 0; i < 10; i++) {
            int vertices = 100 + i * 90; // 100, 190, 280, ..., 910
            int edges = Math.min(vertices * 4, vertices * (vertices - 1) / 2);
            
            Graph graph = generator.generateGraph(vertices, edges, 10000);
            JsonObject graphObj = graphToJson(graph, graphId++, "large_" + (i + 1));
            graphs.add(graphObj);
            
            System.out.printf("  ✓ large_%d: V=%d, E=%d\n", i + 1, vertices, edges);
        }
        System.out.println();
        
        // Extra large graphs (5 graphs, < 3000 vertices)
        System.out.println("Generating Extra graphs (5)...");
        for (int i = 0; i < 5; i++) {
            int vertices = 500 + i * 500; // 500, 1000, 1500, 2000, 2500
            int edges = Math.min(vertices * 5, vertices * (vertices - 1) / 2);
            
            Graph graph = generator.generateGraph(vertices, edges, 50000);
            JsonObject graphObj = graphToJson(graph, graphId++, "extra_" + (i + 1));
            graphs.add(graphObj);
            
            System.out.printf("  ✓ extra_%d: V=%d, E=%d\n", i + 1, vertices, edges);
        }
        System.out.println();
        
        root.add("graphs", graphs);
        
        // Write to file
        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(root, writer);
            System.out.println("=".repeat(60));
            System.out.println("✅ Successfully generated: " + outputFile);
            System.out.println("=".repeat(60));
            System.out.println();
            System.out.println("Summary:");
            System.out.println("  • Total graphs: 30");
            System.out.println("  • Small (5):    < 30 vertices");
            System.out.println("  • Medium (10):  < 300 vertices");
            System.out.println("  • Large (10):   < 1000 vertices");
            System.out.println("  • Extra (5):    < 3000 vertices");
            System.out.println();
            
            File file = new File(outputFile);
            System.out.printf("File size: %.2f KB\n", file.length() / 1024.0);
            
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static JsonObject graphToJson(Graph graph, int id, String name) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("name", name);
        
        JsonArray nodes = new JsonArray();
        for (String node : graph.getNodeNames()) {
            nodes.add(node);
        }
        obj.add("nodes", nodes);
        
        JsonArray edges = new JsonArray();
        for (Edge edge : graph.getEdges()) {
            JsonObject edgeObj = new JsonObject();
            edgeObj.addProperty("from", edge.getFrom());
            edgeObj.addProperty("to", edge.getTo());
            edgeObj.addProperty("weight", edge.getWeight());
            edges.add(edgeObj);
        }
        obj.add("edges", edges);
        
        return obj;
    }
}
