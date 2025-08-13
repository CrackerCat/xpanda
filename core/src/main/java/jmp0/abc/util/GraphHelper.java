package jmp0.abc.util;

import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import jmp0.abc.Pair;
import jmp0.abc.decompiler.structure.Region;
import jmp0.abc.disasm.block.PandaIRBasicBlock;

import java.util.Iterator;
import java.util.Set;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class GraphHelper {
    public static <T,P> T getOnlyOneSuccessor(MutableValueGraph<T,P> graph,T node){
        if (graph.outDegree(node) == 0) return null;
        else return graph.successors(node).iterator().next();
    }

    public static <T,P> void replaceSuccessor(MutableValueGraph<T,P> graph,T node,T node2){
        for (T successor : graph.successors(node2)) {
            P value = graph.edgeValue(node2,successor).get();
            graph.putEdgeValue(node,successor,value);
        }
    }
    public static <T,P> void replacePredecessors(MutableValueGraph<T,P> graph,T node,T node2){
        for (T predecessors : graph.predecessors(node2)) {
            P value = graph.edgeValue(predecessors,node2).get();
            graph.putEdgeValue(predecessors,node,value);
        }
    }
}
