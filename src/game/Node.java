package game;

import java.awt.Color;
import java.awt.Graphics;

public class Node
{
	public int g;
	public float h;
	public Node parent;
	public int x;
	public int y;
	public int value;
    
    public Node(int v)
    {
        g = 0;
        h = 0;
        x = 0;
        y = 0;
        value = v;
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Node))
            return false;
        Node n = (Node)obj;
        return this.x == n.x && this.y == n.y;
    }

    private Color getCol(int c)
    {
        if (c == '_')
            return Color.black;
        if (c == 'S')
            return Color.green.darker();
        if (c == 'E')
            return Color.red.darker();
        if (c == 'W')
            return Color.blue.darker();
        if (c == 'P')
            return Color.orange;
        if (c == 'A')
            return new Color(20, 20, 20);
        if (c == 'V')
        	return new Color(20, 20, 20);
        if (c == 'N')
        	return Color.red;
        return Color.black;
    }
    
    public boolean isWalkable()
    {
    	return value != 'W';
    }

    public void render(Graphics g, int q)
    {
        Color c = getCol(value);
        g.setColor(c);
        g.fillRect(x * q, y * q, q, q);
        g.setColor(new Color(50, 50, 50));
        g.drawRect(x * q, y * q, q, q);
    }

	public float fscore()
	{
		return g + h;
	}
}
