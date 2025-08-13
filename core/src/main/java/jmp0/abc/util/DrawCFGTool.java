package jmp0.abc.util;

import com.google.common.base.Function;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ParallelEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.renderers.CachingEdgeRenderer;
import edu.uci.ics.jung.visualization.renderers.CenterEdgeArrowRenderingSupport;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import jmp0.abc.decompiler.structure.Region;
import jmp0.abc.disasm.block.PandaIRBasicBlock;
import jmp0.abc.disasm.block.PandaIRCFG;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
public final class DrawCFGTool {
    public static void draw(MutableValueGraph<PandaIRBasicBlock, Boolean> origin){
        DirectedGraph<PandaIRBasicBlock, String> graph = new DirectedSparseGraph<PandaIRBasicBlock, String>();
        for (PandaIRBasicBlock node : origin.nodes()) {
            graph.addVertex(node);
        }
        for (EndpointPair<PandaIRBasicBlock> edge : origin.edges()) {
            Boolean value = origin.edgeValue(edge.nodeU(),edge.nodeV()).get();
            if (value) graph.addEdge("#$graph_cfg##_true" + edge.nodeU().getName() + edge.nodeV().getName(),edge.nodeU(),edge.nodeV());
            else graph.addEdge("?"+edge.nodeU().getName() + edge.nodeV().getName(),edge.nodeU(),edge.nodeV());
        }
        JFrame jf = new JFrame();
        //fixme layout
        KKLayout<PandaIRBasicBlock, String> frLayout = new KKLayout<>(graph);
        VisualizationViewer<PandaIRBasicBlock, String> vv = new VisualizationViewer<PandaIRBasicBlock, String>(frLayout);
        DefaultModalGraphMouse<PandaIRBasicBlock, String> graphMouse = new DefaultModalGraphMouse<PandaIRBasicBlock, String>();
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(graphMouse);
        vv.getRenderContext().setVertexShapeTransformer(input -> {
            int length = input.toString().split("\n").length;
            return new Rectangle2D.Float(-90, 0, 180, length*15);
        });
        vv.getRenderContext().setVertexFillPaintTransformer(input ->{
            if (input.getName().equals("entry")){
                return Color.GREEN;
            }
            if (input.getTerminator().getOpCode().isReturnIns()){
                return Color.PINK;
            }
            return Color.WHITE;
        });

        vv.getRenderContext().setVertexLabelTransformer(input -> "<html>" + input.toString().replace("\n","<br>").replace("\t","&ensp;&ensp;&ensp;&ensp;") + "</html>");

        Function<String, Paint> arrowEdgeColor = input -> {
            if (input.startsWith("#$graph_cfg##_true")) return Color.RED;
            else return Color.BLUE;
        };
        vv.getRenderContext().setEdgeDrawPaintTransformer(arrowEdgeColor);
        vv.getRenderer().setEdgeRenderer(new BasicEdgeRenderer<PandaIRBasicBlock,String>(){
            @Override
            protected Shape prepareFinalEdgeShape(RenderContext<PandaIRBasicBlock, String> rc, Layout<PandaIRBasicBlock, String> layout, String e, int[] coords, boolean[] loop) {
                Graph<PandaIRBasicBlock, String> graph = layout.getGraph();
                Pair<PandaIRBasicBlock> endpoints = graph.getEndpoints(e);
                PandaIRBasicBlock v1 = endpoints.getFirst();
                PandaIRBasicBlock v2 = endpoints.getSecond();
                Point2D p1 = (Point2D)layout.apply(v1);
                Point2D p2 = (Point2D)layout.apply(v2);
                p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
                p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
                Shape s1 = (Shape)rc.getVertexShapeTransformer().apply(v1);
                float x1 = (float)p1.getX();
                float y1 = (float)p1.getY() + s1.getBounds().height;
                float x2 = (float)p2.getX();
                float y2 = (float)p2.getY();
                coords[0] = (int)x1;
                coords[1] = (int)y1;
                coords[2] = (int)x2;
                coords[3] = (int)y2;
                boolean isLoop = loop[0] = v1.equals(v2);
                Shape s2 = (Shape)rc.getVertexShapeTransformer().apply(v2);
                Shape edgeShape = (Shape)rc.getEdgeShapeTransformer().apply(e);
                AffineTransform xform = AffineTransform.getTranslateInstance((double)x1, (double)y1);
                if (isLoop) {
                    Rectangle2D s2Bounds = s2.getBounds2D();
                    xform.scale(s2Bounds.getWidth(), s2Bounds.getHeight());
                    xform.translate(0.0, -((Shape)edgeShape).getBounds2D().getWidth() / 2.0);
                }else {
                    float dy;
                    float dx;
                    dx = x2 - x1;
                    dy = y2 - y1;
                    float thetaRadians = (float)Math.atan2((double)dy, (double)dx);
                    xform.rotate((double)thetaRadians);
                    float dist = (float)Math.sqrt((double)(dx * dx + dy * dy));
                    xform.scale((double)dist, 1.0);
                }
                return xform.createTransformedShape((Shape)edgeShape);
            }
        });
        vv.getRenderContext().setArrowFillPaintTransformer(arrowEdgeColor);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        jf.getContentPane().add(vv);
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);
    }

    public static void drawRegionGraph(MutableValueGraph<Region, Boolean> origin){
        DirectedGraph<Region, String> graph = new DirectedSparseGraph<Region, String>();
        for (Region node : origin.nodes()) {
            graph.addVertex(node);
        }
        for (EndpointPair<Region> edge : origin.edges()) {
            Boolean value = origin.edgeValue(edge.nodeU(),edge.nodeV()).get();
            if (value) graph.addEdge("#$graph_cfg##_true" + edge.nodeU().getName() + edge.nodeV().getName(),edge.nodeU(),edge.nodeV());
            else graph.addEdge("?"+edge.nodeU().getName() + edge.nodeV().getName(),edge.nodeU(),edge.nodeV());
        }
        JFrame jf = new JFrame();
        //fixme layout
        KKLayout<Region, String> frLayout = new KKLayout<>(graph);
        VisualizationViewer<Region, String> vv = new VisualizationViewer<Region, String>(frLayout);
        DefaultModalGraphMouse<Region, String> graphMouse = new DefaultModalGraphMouse<Region, String>();
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        vv.setGraphMouse(graphMouse);
        vv.getRenderContext().setVertexShapeTransformer(input -> {
            int length = input.toGraphString().split("\n").length;
            return new Rectangle2D.Float(-90, 0, 180, length*15);
        });
        vv.getRenderContext().setVertexFillPaintTransformer(input ->{
            if (input.getName().equals("entry")){
                return Color.GREEN;
            }
            if (input.getType() == Region.RegionType.Tail){
                return Color.PINK;
            }
            return Color.WHITE;
        });

        vv.getRenderContext().setVertexLabelTransformer(input -> "<html>" + input.toGraphString().replace("\n","<br>").replace("\t","&ensp;&ensp;&ensp;&ensp;") + "</html>");

        Function<String, Paint> arrowEdgeColor = input -> {
            if (input.startsWith("#$graph_cfg##_true")) return Color.RED;
            else return Color.BLUE;
        };
        vv.getRenderContext().setEdgeDrawPaintTransformer(arrowEdgeColor);
        vv.getRenderer().setEdgeRenderer(new BasicEdgeRenderer<Region,String>(){
            @Override
            protected Shape prepareFinalEdgeShape(RenderContext<Region, String> rc, Layout<Region, String> layout, String e, int[] coords, boolean[] loop) {
                Graph<Region, String> graph = layout.getGraph();
                Pair<Region> endpoints = graph.getEndpoints(e);
                Region v1 = endpoints.getFirst();
                Region v2 = endpoints.getSecond();
                Point2D p1 = (Point2D)layout.apply(v1);
                Point2D p2 = (Point2D)layout.apply(v2);
                p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
                p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
                Shape s1 = (Shape)rc.getVertexShapeTransformer().apply(v1);
                float x1 = (float)p1.getX();
                float y1 = (float)p1.getY() + s1.getBounds().height;
                float x2 = (float)p2.getX();
                float y2 = (float)p2.getY();
                coords[0] = (int)x1;
                coords[1] = (int)y1;
                coords[2] = (int)x2;
                coords[3] = (int)y2;
                boolean isLoop = loop[0] = v1.equals(v2);
                Shape s2 = (Shape)rc.getVertexShapeTransformer().apply(v2);
                Shape edgeShape = (Shape)rc.getEdgeShapeTransformer().apply(e);
                AffineTransform xform = AffineTransform.getTranslateInstance((double)x1, (double)y1);
                if (isLoop) {
                    Rectangle2D s2Bounds = s2.getBounds2D();
                    xform.scale(s2Bounds.getWidth(), s2Bounds.getHeight());
                    xform.translate(0.0, -((Shape)edgeShape).getBounds2D().getWidth() / 2.0);
                }else {
                    float dy;
                    float dx;
                    dx = x2 - x1;
                    dy = y2 - y1;
                    float thetaRadians = (float)Math.atan2((double)dy, (double)dx);
                    xform.rotate((double)thetaRadians);
                    float dist = (float)Math.sqrt((double)(dx * dx + dy * dy));
                    xform.scale((double)dist, 1.0);
                }
                return xform.createTransformedShape((Shape)edgeShape);
            }
        });
        vv.getRenderContext().setArrowFillPaintTransformer(arrowEdgeColor);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        jf.getContentPane().add(vv);
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);
    }
}
