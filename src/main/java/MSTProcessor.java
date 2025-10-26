package main.java;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class MSTProcessor {
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java MSTProcessor <input_file> <output_file>");
            System.exit(1);
        }
        
        String inputFile = args[0];
        String outputFile = args[1];
        
        try {
            processGraphs(inputFile, outputFile);
            System.out.println("Processing completed successfully!");
            System.out.println("Output written to: " + outputFile);
        } catch (Exception e) {
            System.err.println("Error processing graphs: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void processGraphs(String inputFile, String outputFile) throws IOException {
        // Read input JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        InputData inputData = gson.fromJson(new FileReader(inputFile), InputData.class);
        
        JsonObject output = new JsonObject();
        JsonArray results = new JsonArray();
        
        for (GraphData graphData : inputData.getGraphs()) {
            // Convert to Graph object
            List<Edge> edges = new ArrayList<>();
            for (GraphData.EdgeData edgeData : graphData.getEdges()) {
                edges.add(new Edge(edgeData.getFrom(), edgeData.getTo(), edgeData.getWeight()));
            }
            Graph graph = new Graph(graphData.getNodes(), edges);
            
            // Process with both algorithms
            JsonObject result = new JsonObject();
            result.addProperty("graph_id", graphData.getId());
            
            // Input stats
            JsonObject inputStats = new JsonObject();
            inputStats.addProperty("vertices", graphData.getNodes().size());
            inputStats.addProperty("edges", graphData.getEdges().size());
            result.add("input_stats", inputStats);
            
            // Run Prim's algorithm
            PrimAlgorithm prim = new PrimAlgorithm();
            MSTResult primResult = prim.findMST(graph);
            result.add("prim", createAlgorithmResult(primResult));
            
            // Run Kruskal's algorithm
            KruskalAlgorithm kruskal = new KruskalAlgorithm();
            MSTResult kruskalResult = kruskal.findMST(graph);
            result.add("kruskal", createAlgorithmResult(kruskalResult));
            
            results.add(result);
        }
        
        output.add("results", results);
        
        // Write output JSON
        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(output, writer);
        }
    }
    
    private static JsonObject createAlgorithmResult(MSTResult result) {
        JsonObject obj = new JsonObject();
        
        JsonArray mstEdges = new JsonArray();
        for (Edge edge : result.getMstEdges()) {
            JsonObject edgeObj = new JsonObject();
            edgeObj.addProperty("from", edge.getFrom());
            edgeObj.addProperty("to", edge.getTo());
            edgeObj.addProperty("weight", edge.getWeight());
            mstEdges.add(edgeObj);
        }
        
        obj.add("mst_edges", mstEdges);
        obj.addProperty("total_cost", result.getTotalCost());
        obj.addProperty("operations_count", result.getOperationsCount());
        obj.addProperty("execution_time_ms", Math.round(result.getExecutionTimeMs() * 100.0) / 100.0);
        
        return obj;
    }
}
