package de.hsma.gentool.gui.editor.display;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.border.MatteBorder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.TreeMultimap;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import de.hsma.gentool.gui.editor.NucleicDisplay;
import de.hsma.gentool.gui.editor.NucleicEditor;
import de.hsma.gentool.gui.editor.NucleicListener;
import de.hsma.gentool.nucleic.Tuple;

public class GraphDisplay extends mxGraphComponent implements NucleicDisplay {
	private static final long serialVersionUID = 1;

	private mxGraph graph;
	private Object parent;
	private mxIGraphModel model;

	private Map<Tuple, Object> vertices;
	private Map<Entry<Tuple,Tuple>,Object> edges;

	public GraphDisplay(NucleicEditor editor) {
		super(new mxGraph());
		
		graph = getGraph();
		parent = graph.getDefaultParent();
		model = graph.getModel();
		
		vertices = new HashMap<>();
		edges = new HashMap<>();
		
		setBorder(new MatteBorder(0,2,0,0,Color.LIGHT_GRAY));
		getViewport().setOpaque(true); getViewport().setBackground(Color.WHITE);
		setEnabled(false);
	}

	@Override public String getLabel() { return "Graph"; }
	
	@Override public boolean hasPreferredSize() { return false; }
	@Override public void setPreferredSize() { /* nothing to do here */ }

	@Override public void tuplesInsert(NucleicListener.NucleicEvent event) {
		HashSet<Tuple> vertices = new HashSet<Tuple>();
		HashMultimap<Tuple, Tuple> edges = HashMultimap.create();
		TreeMultimap<Integer,Tuple> verticesByLength = TreeMultimap.create(Comparator.reverseOrder(), Comparator.naturalOrder());

		Tuple tupleFrom,tupleTo;
		for(Tuple tuple:event.getTuples()) {
			String string = tuple.toString();
			if(string.length()<2) continue;

			vertices.add(tupleFrom=new Tuple(string.substring(0,1)));
			vertices.add(tupleTo=new Tuple(string.substring(1)));
			edges.put(tupleFrom,tupleTo);

			vertices.add(tupleFrom=new Tuple(string.substring(0,string.length()-1)));
			vertices.add(tupleTo=new Tuple(string.substring(string.length()-1)));
			edges.put(tupleFrom,tupleTo);
			
			if(string.length()%2==0) {
				vertices.add(tupleFrom=new Tuple(string.substring(0,string.length()/2)));
				vertices.add(tupleTo=new Tuple(string.substring(string.length()/2)));
				edges.put(tupleFrom,tupleTo);
			}
		}
		for(Tuple vertex:vertices)
			verticesByLength.put(vertex.getBases().length,vertex);
		
		model.beginUpdate();
		try {
			for(Entry<Tuple,Tuple> edge:edges.entries())
				this.addEdge(edge); //and vertices
			for(Tuple vertex:new HashSet<Tuple>(this.vertices.keySet()))
				if(!vertices.contains(vertex)) this.removeVertex(vertex);
			for(Entry<Tuple,Tuple> edge:new HashSet<Entry<Tuple,Tuple>>(this.edges.keySet()))
				if(!edges.containsEntry(edge.getKey(),edge.getValue())) this.removeEdge(edge);
		} finally { model.endUpdate(); }
		if(vertices.isEmpty()) return;
		
		model.beginUpdate();
		try {
			LinkedList<Integer> lengths = new LinkedList<Integer>(verticesByLength.keySet());
			int width = this.getWidth(),last = lengths.getLast(),columns = Math.max(2,lengths.size()*2-1),
				columnGap = width/(columns+1),rowGap = 80,left = -columnGap/2+10, topInset = -50;
			for(Integer length:lengths) {
				int top;
				ArrayList<Tuple> verticesWithLength = new ArrayList<Tuple>(verticesByLength.get(length));
				if(columns==2||length!=last) {
					int half = (int)Math.ceil((double)verticesWithLength.size()/2.);
					left += columnGap; top = topInset;
					for(Tuple vertex : verticesWithLength.subList(0,half))
						this.positionVertexAt(vertex,left,top+=rowGap);
					top = topInset;
					for(Tuple vertex:verticesWithLength.subList(half,verticesWithLength.size()))
						this.positionVertexAt(vertex,width-left,top+=rowGap);
				} else {
					left = width / 2; top = topInset;
					for(Tuple vertex:verticesWithLength)
						this.positionVertexAt(vertex,left,top+=rowGap);
				}
			}
		} finally { model.endUpdate(); }
	}
	@Override public void tuplesRemoved(NucleicListener.NucleicEvent event) { this.tuplesInsert(event); }
	@Override public void tuplesChanged(NucleicListener.NucleicEvent event) { /* nothing to do here */ }
	
	private Object insertVertex(Tuple tuple) {
		Object vertex = vertices.get(tuple);
		if(vertex==null) {
			vertex = this.graph.insertVertex(this.parent,null,tuple,.0,.0,.0,.0);
			graph.updateCellSize(vertex);
			vertices.put(tuple,vertex);
		}
		return vertex;
	}
	private Object removeVertex(Tuple tuple) {
		Object vertex = vertices.get(tuple);
		if(vertex!=null) {
			this.vertices.remove(tuple);
			this.edges.keySet().removeIf(edge->((Tuple)edge.getKey()).equals(tuple)||((Tuple)edge.getValue()).equals(tuple));
			this.model.remove(vertex);
		}
		return vertex;
	}

	@SuppressWarnings("unused") private Object addEdge(Tuple tupleFrom,Tuple tupleTo) {
		return addEdge(new AbstractMap.SimpleEntry<Tuple,Tuple>(tupleFrom,tupleTo));
	}
	private Object addEdge(Map.Entry<Tuple, Tuple> tupleEdge) {
		Object edge = edges.get(tupleEdge);
		if(edge==null) {
			edge = graph.insertEdge(this.parent,null,"",insertVertex(tupleEdge.getKey()),insertVertex(tupleEdge.getValue()));
			edges.put(tupleEdge,edge);
		}
		return edge;
	}

	@SuppressWarnings("unused") private Object removeEdge(Tuple tupleFrom,Tuple tupleTo) {
		return removeEdge(new AbstractMap.SimpleEntry<Tuple, Tuple>(tupleFrom, tupleTo));
	}
	private Object removeEdge(Map.Entry<Tuple, Tuple> tupleEdge) {
		Object edge = edges.remove(tupleEdge);
		if(edge!=null) model.remove(edge);
		return edge;
	}

	private void positionVertexAt(Tuple tuple, int x, int y) {
		Object vertex = this.vertices.get(tuple);
		mxRectangle bounds = this.graph.getCellBounds(vertex);
		graph.resizeCell(vertex,new mxRectangle((double)x-bounds.getWidth()/2.,y,bounds.getWidth(),bounds.getHeight()));
	}
}