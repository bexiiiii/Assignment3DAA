#!/usr/bin/env python3
"""
MST Algorithms Performance Analysis and Visualization
"""

import json
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib.gridspec import GridSpec
import os

# Ensure results directory exists
os.makedirs('results', exist_ok=True)

# Load data from results directory
with open('results/output.json', 'r') as f:
    data = json.load(f)

# Prepare data
results = []
for r in data['results']:
    name = r.get('name', f"graph_{r['graph_id']}")
    vertices = r['input_stats']['vertices']
    
    # Determine category based on vertices
    if vertices < 30:
        category = 'small'
    elif vertices < 300:
        category = 'medium'
    elif vertices < 1000:
        category = 'large'
    else:
        category = 'extra'
    
    results.append({
        'name': name,
        'category': category,
        'vertices': vertices,
        'edges': r['input_stats']['edges'],
        'cost': r['prim']['total_cost'],
        'prim_ops': r['prim']['operations_count'],
        'prim_time': r['prim']['execution_time_ms'],
        'kruskal_ops': r['kruskal']['operations_count'],
        'kruskal_time': r['kruskal']['execution_time_ms']
    })

df = pd.DataFrame(results)

# Determine winner
df['faster'] = df.apply(lambda row: 'Prim' if row['prim_time'] < row['kruskal_time'] else 'Kruskal', axis=1)
df['speedup'] = df.apply(lambda row: row['kruskal_time'] / row['prim_time'] if row['faster'] == 'Kruskal' 
                         else row['prim_time'] / row['kruskal_time'], axis=1)

# Categories
categories = {'small': [], 'medium': [], 'large': [], 'extra': []}
for idx, row in df.iterrows():
    categories[row['category']].append(idx)

print("=" * 80)
print("MST ALGORITHMS PERFORMANCE ANALYSIS")
print("=" * 80)
print()

# Statistics by category
print("STATISTICS BY CATEGORY:")
print("-" * 80)
for cat_name in ['small', 'medium', 'large', 'extra']:
    indices = categories[cat_name]
    if not indices:
        continue
    
    cat_df = df.iloc[indices]
    prim_wins = (cat_df['faster'] == 'Prim').sum()
    kruskal_wins = (cat_df['faster'] == 'Kruskal').sum()
    avg_speedup = cat_df[cat_df['faster'] == 'Kruskal']['speedup'].mean()
    
    print(f"\n{cat_name.upper()} ({len(indices)} graphs):")
    print(f"  Vertices: {cat_df['vertices'].min()}-{cat_df['vertices'].max()}")
    print(f"  Edges: {cat_df['edges'].min()}-{cat_df['edges'].max()}")
    print(f"  Prim wins: {prim_wins}")
    print(f"  Kruskal wins: {kruskal_wins}")
    print(f"  Avg Kruskal speedup: {avg_speedup:.2f}x")
    print(f"  Avg Prim time: {cat_df['prim_time'].mean():.2f} ms")
    print(f"  Avg Kruskal time: {cat_df['kruskal_time'].mean():.2f} ms")

# Overall statistics
print("\n" + "=" * 80)
print("OVERALL STATISTICS:")
print("-" * 80)
prim_total = (df['faster'] == 'Prim').sum()
kruskal_total = (df['faster'] == 'Kruskal').sum()
print(f"Total graphs: {len(df)}")
print(f"Prim faster: {prim_total} ({prim_total/len(df)*100:.1f}%)")
print(f"Kruskal faster: {kruskal_total} ({kruskal_total/len(df)*100:.1f}%)")
print(f"Average Kruskal speedup: {df[df['faster'] == 'Kruskal']['speedup'].mean():.2f}x")
print(f"Maximum Kruskal speedup: {df[df['faster'] == 'Kruskal']['speedup'].max():.2f}x")

# GRAPHS
print("\n" + "=" * 80)
print("Creating visualizations...")
print("=" * 80)

# Style settings
plt.style.use('seaborn-v0_8-darkgrid')
colors = {'small': '#2ecc71', 'medium': '#3498db', 'large': '#e74c3c', 'extra': '#9b59b6'}

# Create figure with multiple subplots
fig = plt.figure(figsize=(20, 12))
gs = GridSpec(3, 3, figure=fig, hspace=0.3, wspace=0.3)

# 1. Execution Time vs Number of Vertices
ax1 = fig.add_subplot(gs[0, :2])
for cat_name, indices in categories.items():
    if not indices:
        continue
    cat_df = df.iloc[indices]
    ax1.scatter(cat_df['vertices'], cat_df['prim_time'], 
               label=f'Prim ({cat_name})', marker='o', s=100, alpha=0.6, color=colors[cat_name])
    ax1.scatter(cat_df['vertices'], cat_df['kruskal_time'], 
               label=f'Kruskal ({cat_name})', marker='s', s=100, alpha=0.6, 
               edgecolors=colors[cat_name], facecolors='none', linewidths=2)

ax1.set_xlabel('Number of Vertices (V)', fontsize=12, fontweight='bold')
ax1.set_ylabel('Execution Time (ms)', fontsize=12, fontweight='bold')
ax1.set_title('Execution Time: Prim vs Kruskal', fontsize=14, fontweight='bold')
ax1.legend(fontsize=9, ncol=2)
ax1.set_yscale('log')
ax1.grid(True, alpha=0.3)

# 2. Operations Count vs Number of Vertices
ax2 = fig.add_subplot(gs[0, 2])
ax2.scatter(df['vertices'], df['prim_ops'], label='Prim', marker='o', s=80, alpha=0.6, color='#e74c3c')
ax2.scatter(df['vertices'], df['kruskal_ops'], label='Kruskal', marker='s', s=80, alpha=0.6, color='#3498db')
ax2.set_xlabel('Number of Vertices (V)', fontsize=11, fontweight='bold')
ax2.set_ylabel('Operations Count', fontsize=11, fontweight='bold')
ax2.set_title('Operations: Prim vs Kruskal', fontsize=12, fontweight='bold')
ax2.legend(fontsize=9)
ax2.grid(True, alpha=0.3)

# 3. Speedup by Category
ax3 = fig.add_subplot(gs[1, 0])
category_names = []
speedups = []
for cat_name in ['small', 'medium', 'large', 'extra']:
    indices = categories[cat_name]
    if indices:
        cat_df = df.iloc[indices]
        kruskal_df = cat_df[cat_df['faster'] == 'Kruskal']
        if len(kruskal_df) > 0:
            category_names.append(cat_name.capitalize())
            speedups.append(kruskal_df['speedup'].mean())

bars = ax3.bar(category_names, speedups, color=[colors[c.lower()] for c in category_names], alpha=0.7)
ax3.set_ylabel('Average Speedup (x times)', fontsize=11, fontweight='bold')
ax3.set_title('Average Kruskal Speedup', fontsize=12, fontweight='bold')
ax3.grid(True, alpha=0.3, axis='y')
for bar in bars:
    height = bar.get_height()
    ax3.text(bar.get_x() + bar.get_width()/2., height,
            f'{height:.1f}x', ha='center', va='bottom', fontweight='bold')

# 4. Win Distribution
ax4 = fig.add_subplot(gs[1, 1])
win_data = []
labels = []
for cat_name in ['small', 'medium', 'large', 'extra']:
    indices = categories[cat_name]
    if indices:
        cat_df = df.iloc[indices]
        kruskal_wins = (cat_df['faster'] == 'Kruskal').sum()
        win_data.append(kruskal_wins)
        labels.append(f"{cat_name.capitalize()}\n({len(indices)} graphs)")

bars = ax4.bar(labels, win_data, color=[colors[c.split('\n')[0].lower()] for c in labels], alpha=0.7)
ax4.set_ylabel('Kruskal Wins Count', fontsize=11, fontweight='bold')
ax4.set_title('Kruskal Wins by Category', fontsize=12, fontweight='bold')
ax4.grid(True, alpha=0.3, axis='y')
for bar in bars:
    height = bar.get_height()
    ax4.text(bar.get_x() + bar.get_width()/2., height,
            f'{int(height)}', ha='center', va='bottom', fontweight='bold')

# 5. Time vs Number of Edges
ax5 = fig.add_subplot(gs[1, 2])
ax5.scatter(df['edges'], df['prim_time'], label='Prim', marker='o', s=80, alpha=0.6, color='#e74c3c')
ax5.scatter(df['edges'], df['kruskal_time'], label='Kruskal', marker='s', s=80, alpha=0.6, color='#3498db')
ax5.set_xlabel('Number of Edges (E)', fontsize=11, fontweight='bold')
ax5.set_ylabel('Execution Time (ms)', fontsize=11, fontweight='bold')
ax5.set_title('Time vs Edges', fontsize=12, fontweight='bold')
ax5.legend(fontsize=9)
ax5.set_yscale('log')
ax5.grid(True, alpha=0.3)

# 6. Operations Comparison by Category
ax6 = fig.add_subplot(gs[2, :])
x = np.arange(len(df))
width = 0.35
bars1 = ax6.bar(x - width/2, df['prim_ops'], width, label='Prim', alpha=0.7, color='#e74c3c')
bars2 = ax6.bar(x + width/2, df['kruskal_ops'], width, label='Kruskal', alpha=0.7, color='#3498db')

ax6.set_xlabel('Graph Number', fontsize=12, fontweight='bold')
ax6.set_ylabel('Operations Count', fontsize=12, fontweight='bold')
ax6.set_title('Operations Comparison for All 30 Graphs', fontsize=14, fontweight='bold')
ax6.legend(fontsize=10)
ax6.grid(True, alpha=0.3, axis='y')

# Category separators
for i, (cat_name, indices) in enumerate(categories.items()):
    if i > 0 and indices:
        ax6.axvline(x=indices[0]-0.5, color='gray', linestyle='--', alpha=0.5, linewidth=2)
        ax6.text(indices[0], ax6.get_ylim()[1]*0.95, cat_name.upper(), 
                fontsize=9, fontweight='bold', ha='left')

plt.suptitle('MST Algorithms: Comprehensive Performance Analysis', 
             fontsize=16, fontweight='bold', y=0.995)

plt.savefig('results/performance_analysis.png', dpi=300, bbox_inches='tight')
print(" Saved: results/performance_analysis.png")

# Additional graph: detailed time comparison
fig2, axes = plt.subplots(2, 2, figsize=(16, 12))
fig2.suptitle('Detailed Execution Time Analysis by Category', 
              fontsize=16, fontweight='bold')

for idx, cat_name in enumerate(['small', 'medium', 'large', 'extra']):
    ax = axes[idx // 2, idx % 2]
    indices = categories[cat_name]
    if not indices:
        continue
    
    cat_df = df.iloc[indices]
    x = np.arange(len(cat_df))
    
    ax.plot(x, cat_df['prim_time'], 'o-', label='Prim', linewidth=2, 
            markersize=8, color='#e74c3c', alpha=0.7)
    ax.plot(x, cat_df['kruskal_time'], 's-', label='Kruskal', linewidth=2, 
            markersize=8, color='#3498db', alpha=0.7)
    
    ax.set_xlabel(f'Graph Index in {cat_name} Category', fontsize=11, fontweight='bold')
    ax.set_ylabel('Execution Time (ms)', fontsize=11, fontweight='bold')
    ax.set_title(f'{cat_name.upper()}: V={cat_df["vertices"].min()}-{cat_df["vertices"].max()}', 
                fontsize=12, fontweight='bold')
    ax.legend(fontsize=10)
    ax.grid(True, alpha=0.3)
    
    # Add annotations for extreme values
    max_speedup_idx = cat_df['speedup'].idxmax()
    max_speedup_local = cat_df.index.get_loc(max_speedup_idx)
    ax.annotate(f'Max speedup:\n{cat_df.loc[max_speedup_idx, "speedup"]:.1f}x', 
                xy=(max_speedup_local, cat_df.loc[max_speedup_idx, 'kruskal_time']),
                xytext=(10, 20), textcoords='offset points',
                bbox=dict(boxstyle='round,pad=0.5', fc='yellow', alpha=0.7),
                arrowprops=dict(arrowstyle='->', connectionstyle='arc3,rad=0'))

plt.tight_layout()
plt.savefig('results/detailed_time_analysis.png', dpi=300, bbox_inches='tight')
print(" Saved: results/detailed_time_analysis.png")

print("\n" + "=" * 80)
print(" Analysis completed!")
print("=" * 80)
print("\nGenerated files:")
print("  • performance_analysis.png - comprehensive analysis")
print("  • detailed_time_analysis.png - detailed analysis by category")
print("  • results.csv - tabular data")
