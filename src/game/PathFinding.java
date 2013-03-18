package game;

import java.awt.Graphics;
import java.util.ArrayList;
import run.Gui;
import utilities.std;

public class PathFinding implements Runnable
{
	public enum DISTANCE{MANHATTAN, EUCLIDEAN, DIAGONAL};
	
	public Node[][] matrix;
	private Node start;
	private Node end;
	private int _start_x;
	private int _start_y;
	private int _end_x;
	private int _end_y;
	private ArrayList<Node> openList;
	private ArrayList<Node> closedList;
	
	private int width;
	private int height;
	
	private boolean diagonal;
	
	private boolean slow;
	public boolean slow_analize;
	
	private int delay;
	
	public DISTANCE distance_type;
	
	public boolean no_path;
	
	

	public PathFinding(int w, int h)
	{
		width = w;
		height = h;
		matrix = new Node[width][height];
		clear();
		openList = new ArrayList<Node>();
		closedList = new ArrayList<Node>();
		start = null;
		end = null;
		distance_type = DISTANCE.MANHATTAN;
	}
	
	public void loadFromFile(String path)
	{
		clear();
		matrix = MapFile.loadFromFile(path, width, height);
		setStartAndEndFromMatrix();
	}
	
	public void setHeuristicDistanceType(DISTANCE d)
	{
		distance_type = d;
	}
	
	public void setStartAndEndFromMatrix()
	{
		for (int j = 0; j < height; j++)
		{
			for (int i = 0; i < width; i++)
			{
				if (matrix[i][j].value == 'S')
					setStart(i, j, false);
				else if (matrix[i][j].value == 'E')
					setEnd(i, j, false);
			}
		}
	}
	
	public void multithread_find()
	{
		new Thread(this).start();
	}
	
	public void run()
	{
		find();
	}
	
	public void setDelay(int d)
	{
		if (d == 0)
			slow = false;
		else
			slow = true;
		delay = d;
	}
	
	public void setDiagonal(boolean d)
	{
		diagonal = d;
	}

	public void setStart(int x, int y, boolean over)
	{
		if (over && (managedAccess(x, y).value == 'S' || managedAccess(x, y).value == 'E'))
			return;
		if (!managedAccess(x, y, 'S'))
			return;
		managedAccess(_start_x, _start_y, '_');
		start = new Node('S');
		start.x = x;
		start.y = y;
		_start_x = x;
		_start_y = y;
	}

	public void setEnd(int x, int y, boolean over)
	{
		if (over && (managedAccess(x, y).value == 'S' || managedAccess(x, y).value == 'E'))
			return;
		
		if (!managedAccess(x, y, 'E'))
			return;
		managedAccess(_end_x, _end_y, '_');
		end = new Node('E');
		end.x = x;
		end.y = y;
		_end_x = x;
		_end_y = y;
	}

	public void setWall(int x, int y)
	{
		if (managedAccess(x, y).value == 'E' || managedAccess(x, y).value == 'S')
			return;
		managedAccess(x, y, 'W');
	}
	
	public void clearAt(int x, int y)
	{
		if (matrix[x][y].value == 'S' || matrix[x][y].value == 'E')
			return;
		managedAccess(x, y, '_');
	}

	public void clear()
	{
		for (int j = 0; j < height; j++)
		{
			for (int i = 0; i < width; i++)
			{
				if (matrix[i][j] != null && (matrix[i][j].value == 'S' || matrix[i][j].value == 'E'))
					continue;
				matrix[i][j] = new Node('_');
				matrix[i][j].x = i;
				matrix[i][j].y = j;
			}
		}
	}
	
	public void clearAll(boolean delete)
	{
		if (delete)
		{
			start = null;
			end = null;
			_start_x = _start_y = _end_x = _end_y = -1;
		}
		for (int j = 0; j < height; j++)
		{
			for (int i = 0; i < width; i++)
			{
				matrix[i][j] = new Node('_');
				matrix[i][j].x = i;
				matrix[i][j].y = j;
			}
		}
	}
	
	public void clearPath()
	{
		for (int j = 0; j < height; j++)
		{
			for (int i = 0; i < width; i++)
			{
				matrix[i][j].g = 0;
				matrix[i][j].h = 0;
				if (matrix[i][j].value != 'W' && matrix[i][j].value != 'S' && matrix[i][j].value != 'E')
					matrix[i][j].value = '_';
			}
		}
		openList.clear();
		closedList.clear();
	}

	private boolean managedAccess(int x, int y, int c)
	{
		if (x >= 0 && x < width && y >= 0 && y < height)
		{
			matrix[x][y].value = c;
			return true;
		}
		return false;
	}

	public void find()
	{
		if (start == null || end == null)
			return;
		pathfinding(diagonal);
	}

	private void pathfinding(boolean diag)
	{
		no_path = false;
		std.startBenchmark();
		int cyc = 0;
		Node current = start;
		closedList.add(current);
		while (!current.equals(end))
		{
			if (slow_analize)
			{
				try
				{
					Thread.sleep(delay);
				}
				catch (Exception e){}
			}
			cyc++;
			Gui.lbl_loops.setText("Loops: " + cyc);
			Node temp = null;
			for (int x = -1; x < 2; x++)
			{
				for (int y = -1; y < 2; y++)
				{
					if (!diag && ((x != 0 && y != 0) || x == y))
						continue;
					temp = managedAccess(current.x + x, current.y + y);
					if (temp != null && (!temp.isWalkable() || closedList.contains(temp)))
						continue;
					if (temp != null)
					{
						if (!openList.contains(temp))
						{
							temp.g = current.g + 1;
							temp.h = getDistance(temp, end);
							temp.parent = current;
							openList.add(temp);
						}
						else
						{
							if (temp.g < current.g)
							{
								temp.parent = current;
								temp.g = current.g + 1;
								temp.h = getDistance(temp, end);
							}
						}
					}
				}
			}
			if (openList.size() == 0)
				break;
			Node next = openList.get(0);
			for (Node n : openList)
			{
				if (n.fscore() < next.fscore())
					next = n;
			}
			
			if (!next.equals(end))
				next.value = 'A';
			openList.remove(next);
			closedList.add(next);
			current = next;
		}
		std.stopBenchmark(false);
		double time = (double)std.time / 1000 / 1000;
		String u = " ms";
		if (time > 1000)
		{
			time /= 1000;
			u = " s";
		}
		
		if (time / (int)time == 0)
			time = (int)time;
		
		String tim = std.str(time);
		if (tim.indexOf(".") > 0)
			tim = tim.substring(0, tim.indexOf(".") + 3);
		
		Gui.lbl_time.setText("Time: " + tim + u);
		finalPath();
	}
	
	private float getDistance(Node n1, Node n2)
	{
		if (distance_type == DISTANCE.MANHATTAN)
			return manhattanDistance(n1, n2);
		if (distance_type == DISTANCE.EUCLIDEAN)
			return euclideanDistance(n1, n2);
		if (distance_type == DISTANCE.DIAGONAL)
			return diagonalDistance(n1, n2);
		return manhattanDistance(n1, n2);
	}
	
	private void finalPath()
	{
		if (!closedList.get(closedList.size() - 1).equals(end))
		{
			no_path = true;
		}
		Node current = closedList.get(closedList.size() - 1);
		ArrayList<Node> path = new ArrayList<Node>();
		while (!current.equals(start))
		{
			if (!current.equals(end))
				path.add(current);
			current = current.parent;
		}
		for (int i = path.size() - 1; i >= 0; i--)
		{
			Node n = path.get(i);
			if (slow)
			{
				try
				{
					Thread.sleep(delay);
				}
				catch (Exception e){}
			}
			n.value = 'P';
		}
		if (no_path)
		{
			Gui.no_path_message();
			for (int j = 0; j < height; j++)
			{
				for (int i = 0; i < width; i++)
				{
					if (matrix[i][j].value == 'P')
						matrix[i][j].value = 'N';
				}
			}
		}
		else
			Gui.distance_message(path.size());
	}

	private int manhattanDistance(Node n1, Node n2)
	{
		return Math.abs(n1.x - n2.x) + Math.abs(n1.y - n2.y);
	}
	
	private float euclideanDistance(Node n1, Node n2)
	{
		return (float)Math.sqrt(Math.pow((n1.x - n2.x), 2) + Math.pow((n1.y - n2.y), 2));
	}
	
	private float diagonalDistance(Node n1, Node n2)
	{
		return Math.max(Math.abs(n1.x-n2.x), Math.abs(n1.y-n2.y));
	}

	private Node managedAccess(int x, int y)
	{
		if (x >= 0 && x < width && y >= 0 && y < height)
			return matrix[x][y];
		return null;
	}

	public void render(Graphics g, int q)
	{
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				matrix[x][y].render(g, q);
			}
		}
	}
}
