package jmp0.abc.decompiler.structure;

import com.google.common.graph.MutableValueGraph;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class LoopFinder {
    private enum NodeColor{
        Gray,
        Black
    }
    private final MutableValueGraph<Region,Boolean> graph;
    private final Region entry;
    private final HashMap<Region,NodeColor> nodeColorHashMap = new HashMap<>();
    @Getter private final HashSet<Region> loopNodes = new HashSet<>();

    public LoopFinder(MutableValueGraph<Region,Boolean> graph,Region entry,DominatorGraph doms){
        this.graph = graph;
        this.entry = entry;
        for (Region predecessor : graph.predecessors(entry)) {
            if (doms.dominatesStrictly(entry,predecessor)){
                if (!nodeColorHashMap.containsKey(predecessor)){
                    backwardVisit(predecessor);
                }
            }else if (predecessor == entry){
                loopNodes.add(predecessor);
            }
        }
        NodeColor color = nodeColorHashMap.get(entry);
        if (color == NodeColor.Gray){
            forwardVisit(entry);
        }
    }

    private void backwardVisit(Region node){
        this.nodeColorHashMap.put(node,NodeColor.Gray);
        if (node == entry) return;
        for (Region predecessor : graph.predecessors(node)) {
            if (!nodeColorHashMap.containsKey(predecessor))
                backwardVisit(predecessor);
        }
    }

    private void forwardVisit(Region node){
        nodeColorHashMap.put(node,NodeColor.Black);
        loopNodes.add(node);
        for (Region successor : graph.successors(node)) {
            if (nodeColorHashMap.get(successor) == NodeColor.Gray){
                forwardVisit(successor);
            }
        }
    }

}
