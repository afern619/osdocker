import java.util.*; 
import java.io.*; 
import java.io.File; 
import java.util.Scanner; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.w3c.dom.Node;
import java.io.FileNotFoundException; 
import java.util.Random;

public class ReadingFromFile extends JFrame
{ 
    int width;
    int height;

    ArrayList<Node> nodes;
    ArrayList<edge> edges;

    public ReadingFromFile() { 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	nodes = new ArrayList<Node>();
	edges = new ArrayList<edge>();
	width = 30;
	height = 30;
    }

    public ReadingFromFile(String name) { 
	this.setTitle(name);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	nodes = new ArrayList<Node>();
	edges = new ArrayList<edge>();
	width = 30;
	height = 30;
    }
    class Node {
	int x, y;
	String name;
	
	public Node(String myName, int myX, int myY) {
	    x = myX;
	    y = myY;
	    name = myName;
	}
    }

    class edge {
    Node i,j;
    String name;
    int weight;
	
	public edge(Node ii, Node jj, int weighted) {
	    i = ii;
        j = jj;	    
        name = "From " + ii.name + " to " + jj.name;
        weight = weighted;
	}
    }

    public ReadingFromFile.Node addNode(String name, int x, int y) { 
    ReadingFromFile.Node n = new ReadingFromFile.Node(name, x, y);
    String nName = n.name;
    for(ReadingFromFile.Node no : nodes){
        if(nName.equals(no.name)){
            return no;
        }
	else if(no.x == x || no.y == y){
		x += 100;
		y += 100;
	}
    }
	
    nodes.add(n);
    this.repaint();
    return n;
    }

    public void addEdge(Node i, Node j, int weight) {
    boolean equals = false;
    String tempName = "From " + i.name + " to " + j.name;
    for(edge e: edges){
        String nameE = e.name;
        if(nameE.equals(tempName)){
            equals = true;
	e.weight += weight;
            break;
        }      
    }
    if(!equals){
        edges.add(new edge(i,j, weight));
        this.repaint();
    }
    }
    
    public void paint(Graphics g) {
	FontMetrics f = g.getFontMetrics();
	int nodeHeight = Math.max(height, f.getHeight());

	g.setColor(Color.black);
	for (edge e : edges) {
        g.drawLine(e.i.x, e.i.y, e.j.x, e.j.y);
        String weighted = Integer.toString(e.weight);
        int middleX = (e.i.x + e.j.x)/2;
        int middleY = (e.i.y + e.j.y)/2;
        g.drawString(weighted, middleX, middleY);
	}

	for (Node n : nodes) {
	    int nodeWidth = Math.max(width, f.stringWidth(n.name)+width/2);
	    g.setColor(Color.white);
	    g.fillOval(n.x-nodeWidth/2, n.y-nodeHeight/2, 
		       nodeWidth, nodeHeight);
	    g.setColor(Color.black);
	    g.drawOval(n.x-nodeWidth/2, n.y-nodeHeight/2, 
		       nodeWidth, nodeHeight);
	    
	    g.drawString(n.name, n.x-f.stringWidth(n.name)/2,
			 n.y+f.getHeight()/2);
	}
    }

    public static void main(String[] args) throws Exception 
    { 
      File file =  new File("/home/ubuntu/file.csv"); 
      Scanner sc = new Scanner(file); 
      ReadingFromFile frame = new ReadingFromFile("IP's Address");
        frame.setSize(800,700);
        frame.setVisible(true);
     
	int y = 50;
	int x = 100;
	
      while(sc.hasNextLine())
      {
       	int y2, x2;
        ReadingFromFile.Node firstNode;
        ReadingFromFile.Node middleNode;
        String[] tokens = sc.nextLine().split("[- >\n,]+");

        String last = tokens[tokens.length - 1];
        String first = tokens[tokens.length- 4];
        String mid = tokens[tokens.length - 3];

        firstNode = frame.addNode(first, x, y);
     y += 150 ;
    x += 100;
	y2 = y + 150;
	x2 = x + 100;
        
	if(y2 >= 600 || y >= 600){
		y = 50;
		y2 = y + 100;
	}
	if( x2 >= 700 || x >= 700){
		x = 100;
		x2 = x + 50;
	}

        middleNode = frame.addNode(mid, x2, y2);
        int lastI = Integer.parseInt(last);
        if(lastI >= 1 ){
            frame.addEdge(firstNode, middleNode, lastI);
        }
      }
	sc.close();
    }
}
