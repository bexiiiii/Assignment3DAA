import json
import csv
import os

# Ensure results directory exists
os.makedirs('results', exist_ok=True)

# Read output.json from results directory
with open('results/output.json', 'r') as f:
    data = json.load(f)

# Create CSV file in results directory
with open('results/results.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['Graph', 'Category', 'V', 'E', 'Cost', 'P_Ops', 'P_Time', 'K_Ops', 'K_Time', 'Winner', 'Speedup'])
    
    for result in data['results']:
        graph_id = result['graph_id']
        name = f"graph_{graph_id}"
        vertices = result['input_stats']['vertices']
        edges = result['input_stats']['edges']
        mst_cost = result['prim']['total_cost']
        
        # Determine category based on vertices
        if vertices < 30:
            category = 'small'
        elif vertices < 300:
            category = 'medium'
        elif vertices < 1000:
            category = 'large'
        else:
            category = 'extra'
        
        prim = result['prim']
        kruskal = result['kruskal']
        
        # Determine faster algorithm and speedup
        if prim['execution_time_ms'] < kruskal['execution_time_ms']:
            faster = 'Prim'
            speedup = f"{kruskal['execution_time_ms'] / prim['execution_time_ms']:.2f}x"
        else:
            faster = 'Kruskal'
            speedup = f"{prim['execution_time_ms'] / kruskal['execution_time_ms']:.2f}x"
        
        writer.writerow([
            name,
            category,
            vertices,
            edges,
            mst_cost,
            prim['operations_count'],
            f"{prim['execution_time_ms']:.2f}",
            kruskal['operations_count'],
            f"{kruskal['execution_time_ms']:.2f}",
            faster,
            speedup
        ])

print("✅ results/results.csv created successfully!")
