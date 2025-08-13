package jmp0.abc.decompiler.structure;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.Traverser;
import jmp0.abc.decompiler.PandaDecompileException;
import lombok.SneakyThrows;

import java.util.*;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DominatorGraph {
    private final HashMap<Region,Region> idoms;
    private HashMap<Region, List<Region>> domFrontier;
    private HashMap<Region, Integer> reversePostOrder;
    public DominatorGraph(MutableValueGraph<Region,Boolean> graph, Region entry){
        this.idoms = new HashMap<>();
        build(graph, entry);
        this.idoms.put(entry,null);
        this.domFrontier = buildDominanceFrontiers(graph, idoms);
    }


    private HashMap<Region, List<Region>> buildDominanceFrontiers(MutableValueGraph<Region,Boolean> graph, Map<Region, Region> idoms) {
        HashMap<Region, List<Region>> fronts = new HashMap<>();
        for (Region node : graph.nodes()) {
            fronts.put(node, new ArrayList<>());
        }

        for (Region bb : graph.nodes()) {
            Set<Region> pred = graph.predecessors(bb);
            if (pred.size() < 2) {
                continue;
            }
            for (Region p : pred) {
                Region r = p;
                while (r != null && r != idoms.get(bb)) {
                    // Add bb to the dominance frontier of r.
                    if (!fronts.get(r).contains(bb)) {
                        fronts.get(r).add(bb);
                    }
                    r = idoms.get(r);
                }
            }
        }
        return fronts;
    }

    private void build(MutableValueGraph<Region,Boolean> graph,Region entryNode){
        idoms.put(entryNode,entryNode);
        reversePostOrder = reversePostorderNumbering(graph,entryNode);
        HashMap<Integer,Region> nodes = new HashMap<>();
        for (Map.Entry<Region, Integer> regionIntegerEntry : reversePostOrder.entrySet()) {
            nodes.put(regionIntegerEntry.getValue(),regionIntegerEntry.getKey());
        }
        boolean changed;
        do
        {
            changed = false;
            for (Region b : nodes.values()) {
                if (b == entryNode)
                    continue;
                Region newIdom = null;
                for (Region p : graph.predecessors(b)) {
                    if (idoms.containsKey(p))
                    {
                        if (newIdom == null)
                            newIdom = p;
                        else if (idoms.containsKey(p))
                        {
                            newIdom = Intersect(idoms, p, newIdom);
                        }
                    }
                }
                Region oldIdom = idoms.get(b);

                if ((oldIdom == null || oldIdom != newIdom) && newIdom != null)
                {
                    idoms.put(b,newIdom);
                    changed = true;
                }
            }
        } while (changed);
    }

    @SneakyThrows
    private Region Intersect(HashMap<Region,Region> postdoms, Region b1, Region b2)
    {
        Region i1 = b1;
        Region i2 = b2;
        int c = 0;
        while (i1 != i2)
        {
            while (reversePostOrder.get(i1) > reversePostOrder.get(i2))
            {
                ++c;
                if (c > 100000)
                    throw new PandaDecompileException("Dominator graph calculation timed out.");
                i1 = postdoms.get(i1);
            }
            while (reversePostOrder.get(i2) > reversePostOrder.get(i1))
            {
                ++c;
                if (c > 100000)
                    throw new PandaDecompileException("Dominator graph calculation timed out.");
                i2 = postdoms.get(i2);
            }
        }
        return i1;
    }

    private HashMap<Region,Integer> reversePostorderNumbering(MutableValueGraph<Region,Boolean> graph,Region entryNode){
        HashMap<Region, Integer> ret = new HashMap<>();
        for (Region region : Traverser.forGraph(graph).depthFirstPostOrder(entryNode)) {
            ret.put(region,graph.nodes().size() - ret.size() -1);
        }
        return ret;
    }

    public boolean dominatesStrictly(Region dominator, Region d) {
        while (idoms.containsKey(d) && idoms.get(d) != null) {
            Region iDom = idoms.get(d);
            if (iDom.equals(dominator)) {
                return true;
            }
            d = iDom;
        }
        return false;
    }


}
