package data.structure;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Graph {
    private boolean directed;
    // private boolean weighted;
    private ListLinked<Vertex> vertexList;
    private Vertex[] vertexs;
    private int numVertexs;
    private Stack<Integer> pila = new Stack<Integer>();

    private boolean isConnected;
    private int componentConnected;

    public Graph(boolean directed) {
        this.directed = directed;
        isConnected = false;
        componentConnected = 0;
        vertexList = new ListLinked<>();
    }

    public Graph(boolean directed, int numVertexs) {
        this.directed = directed;
        isConnected = false;
        componentConnected = 0;
        vertexs = new Vertex[numVertexs];
    }

    public boolean isConnected() {
        BFS();
        return isConnected;
    }

    public int getComponentConnected() {
        return componentConnected;
    }

    public boolean getDirected() {
        return directed;
    }

    public ListLinked<Vertex> getVertexList() {
        return vertexList;
    }

    public Vertex[] getVertexs() {
        return vertexs;
    }

    public int getNumVertexs() {
        return numVertexs;
    }

    public void addVertex(Vertex vertex) {
        vertexList.add(vertex);
    }

    public void addEdge(Vertex v1, Vertex v2, double weight) {
        Edge edge = new Edge(v1, v2, weight);
        v1.addEdge(edge);
        if (!directed) {
            Edge edge2 = new Edge(v2, v1, weight);
            v2.addEdge(edge2);
        }
    }

    public void DFS(Vertex vertex) {
        ListLinked<Vertex> travelBFS = new ListLinked<>();
        // Queue<Vertex> queue = new LinkedList<>();// estructura de datos cola
        Stack<Vertex> stack = new Stack<>();
        stack.push(vertex);
        vertex.setStatus(State.VISITADO);
        travelBFS.add(vertex);
        while (!stack.isEmpty()) {
            vertex = stack.pop();// vertice padre
            ListLinked<Edge> lEdges = vertex.getEdges();
            Node<Edge> node = lEdges.getHead();
            while (node != null) {
                Vertex opposite = node.getData().getV2();// vertices hijos
                if (opposite.getState() == State.NO_VISITADO) {
                    stack.add(opposite);
                    opposite.setStatus(State.VISITADO);

                    opposite.setJumps(vertex.getJumps() + 1);
                    travelBFS.add(opposite);
                }
                node = node.getLink();
            }
            vertex.setStatus(State.PROCESADO);
        }
        Node<Vertex> temp = travelBFS.getHead();
        while (temp != null) {
            System.out.print(temp.getData().getLabel() + "{" + temp.getData().getJumps() + "}\t");
            temp = temp.getLink();
        }
    }

    public void BFS(Vertex vertex) {
        ListLinked<Vertex> travelBFS = new ListLinked<>();
        Queue<Vertex> queue = new LinkedList<>();// estructura de datos cola
        queue.add(vertex);
        vertex.setStatus(State.VISITADO);
        travelBFS.add(vertex);
        while (!queue.isEmpty()) {
            vertex = queue.poll();// vertice padre
            ListLinked<Edge> lEdges = vertex.getEdges();
            Node<Edge> node = lEdges.getHead();
            while (node != null) {
                Vertex opposite = node.getData().getV2();// vertices hijos
                if (opposite.getState() == State.NO_VISITADO) {
                    queue.add(opposite);
                    opposite.setStatus(State.VISITADO);
                    opposite.setParent(vertex);
                    opposite.setJumps(vertex.getJumps() + 1);
                    travelBFS.add(opposite);
                }
                node = node.getLink();
            }
            vertex.setStatus(State.PROCESADO);
        }
        /*
         * Node<Vertex> temp = travelBFS.getHead(); while (temp != null) {
         * System.out.print(temp.getData().getLabel() + "{" + temp.getData().getJumps()
         * + "}\t"); temp = temp.getLink(); }
         */
    }

    public void BFS() {
        Node<Vertex> iterator = vertexList.getHead();
        isConnected = false;
        while (iterator != null) {
            Vertex vertex = iterator.getData();
            if (vertex.getState().compareTo(State.NO_VISITADO) == 0) {
                BFS(vertex);
                componentConnected++;
                isConnected = componentConnected == 1;
            }
            iterator = iterator.getLink();
        }
    }

    public void shortesPaths() {
        BFS(vertexList.getHead().getData());
        System.out.println("Caminos cortos");
        for (int i = 0; i < vertexs.length; i++) {
            String route = printShortPath(getShortPath(vertexs[i]));
            System.out.println(vertexs[i].getLabel() + "(" + vertexs[i].getJumps() + "): " + route);
        }
    }

    public void shortPath(Vertex start, Vertex finish) {
        BFS(start);
        Stack<Vertex> shortPath = getShortPath(finish);
        printShortPath(shortPath);
    }

    public String printShortPath(Stack<Vertex> shortPath) {
        String output = "";
        if (!shortPath.isEmpty()) {
            while (!shortPath.isEmpty()) {
                output += shortPath.pop().getLabel();
                if (shortPath.size() >= 1) {
                    output += " -> ";
                }
            }
        } else {
            output += "Sin caminos cortos, vertice aislado";
        }
        // output += "\n";
        return output;
    }

    public Stack<Vertex> getShortPath(Vertex vertex) {
        Stack<Vertex> shortPath = new Stack<>();
        Vertex parent = vertex.getParent();
        if (parent != null) {
            shortPath.push(vertex);
            while (parent != null) {
                shortPath.push(parent);
                parent = parent.getParent();
            }
        }
        return shortPath;
    }

    public void printGraph() {
        ListLinked<Edge> edges;
        String output = "";
        for (int i = 0; i < vertexs.length; i++) {
            Vertex vertex = vertexs[i];
            output = output + vertex.getLabel();
            edges = vertex.getEdges();
            output = output + "(" + edges.size() + "){d=" + vertex.getD() + ", f=" + vertex.getF() + "} -> ";
            // output = output + "(" + edges.size() + ") -> ";
            Node<Edge> temp = edges.getHead();
            while (temp != null) {
                output = output + "{" + temp.getData().getV2().getLabel() + "} ";
                temp = temp.getLink();
            }
            output = output + "\n";
        }
        System.out.println(output);
    }

    public void DFS(Vertex inicio, int d) {
        inicio.setStatus(State.VISITADO);
        inicio.setD(d);
        pila.push(d);
        // System.out.println("d = "+d);
        Node<Edge> node = inicio.getEdges().getHead();
        while (node != null) {
            if (node.getData().getV2().getState() == State.NO_VISITADO) {
                pila.pop();
                DFS(node.getData().getV2(), d + 1);
                d = pila.pop();
            }
            node = node.getLink();
        }
        inicio.setStatus(State.PROCESADO);
        inicio.setF(d + 1);
        pila.push(d + 1);
    }

    public void readFileInput(String filename) {
        String path = System.getProperty("user.dir") + "\\input\\" + filename;
        try {

            System.out.println(path);
            File file = new File(path);
            Scanner scanner = new Scanner(file);

            String line = "";
            line = scanner.next();
            Pattern pattern;
            Matcher matcher;

            pattern = Pattern.compile("size\\s*=\\s*(\\d+)");
            matcher = pattern.matcher(line);
            matcher.find();
            String strSize = matcher.group(1);
            System.out.println(strSize);

            vertexs = new Vertex[Integer.parseInt(strSize)];
            // Obteniendo las lineas de informacion de vertices
            while (!(line = scanner.nextLine()).equals(";")) {

                pattern = Pattern.compile("(\\d+)\\s*=\\s*(.+)");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    Vertex vertex = new Vertex(matcher.group(2));
                    addVertex(vertex);
                    vertexs[Integer.parseInt(matcher.group(1))] = vertex;
                }
                // System.out.println(line);
            }

            // Obteniendo las lineas de informacion de aristas
            while (!(line = scanner.nextLine()).equals(";")) {
                pattern = Pattern.compile("\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int posV1 = Integer.parseInt(matcher.group(1));
                    int posV2 = Integer.parseInt(matcher.group(2));
                    Vertex v1 = vertexs[posV1];
                    Vertex v2 = vertexs[posV2];
                    double weight = Double.parseDouble(matcher.group(3));
                    addEdge(v1, v2, weight);
                }
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void Repositor() {
        Node<Vertex> node = vertexList.getHead();
        while (node != null) {
            node.getData().setStatus(State.NO_VISITADO);
            node = node.getLink();
        }
    }

    private boolean Articulado() {
        Node<Vertex> node = vertexList.getHead();
        int aux = 0;
        while (node != null) {
            Vertex vertex = node.getData();
            if (vertex.getState().compareTo(State.NO_VISITADO) == 0) {
                BFS(vertex);
                aux++;            
            }
            node = node.getLink();
        }
        if (aux == 1)
            return false;
        else
            return true;
    }

    public ListLinked<Vertex> VerticesDeArticulacion() {
        Node<Vertex> node = vertexList.getHead();
        Vertex vertex = null;
        Boolean articulado = false;
        ListLinked<Vertex> list = new ListLinked<>();
        while(node!=null) {
            vertex = node.getData();
            vertex.setStatus(State.ELIMINADO);            
            articulado = Articulado();
            node = node.getLink();            
            Repositor();
            if(articulado)
                list.add(vertex);
        }
        return list;
    }

    public void MostrarVerticesdeArticulacion(Vertex vertex) {
        Node<Edge> node = vertex.getEdges().getHead();
        System.out.print("Vertices de articulacion :");
        while (node != null) {
            System.out.print("[" + node.getData().getV2().getLabel() + "] ");
            node = node.getLink();
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph(false);

        graph.readFileInput("grafo6.txt");
        // graph.printGraph();
        graph.MostrarVerticesdeArticulacion(graph.vertexs[0]);
    }
}
