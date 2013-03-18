package game;

import run.Gui;


public class MazeGenerator
{
	private static final int wallCode = 'W';
	private static final int emptyCode = '_';
	private static int rows;
	private static int columns;
	
	private static boolean started;
	
	public static int[][] generated_maze;
	
	public static boolean slow_process;
	public static int proc_speed = 10;

	synchronized public static void generateMaze(int _rows, int cols)
	{
		rows = _rows;
		columns = cols;
		new Thread(new Runnable(){
			public void run()
			{
				started = true;
				thread();
				Gui.g.pf.clearAll(true);
				Gui.g.pf.matrix = getNodeMatrix();
				Gui.g.pf.setStartAndEndFromMatrix();
				started = false;
			}
		}).start();
	}

	public static void thread()
	{
		generated_maze = makeMaze();
	}
	
	public static Node[][] getNodeMatrix()
	{
		Node[][] mat = new Node[rows][columns];
		for (int y = 0; y < columns; y++)
		{
			for (int x = 0; x < rows; x++)
			{
				mat[x][y] = new Node(generated_maze[x][y]);
				mat[x][y].x = x;
				mat[x][y].y = y;
			}
		}
		mat[1][1].value = 'S';
		mat[rows - 2][columns - 2].value = 'E';
		return mat;
	}

	private static int[][] makeMaze()
	{
		int[][] maze = new int[rows][columns];
		int i, j;
		int emptyCt = 0;
		int wallCt = 0;
		int[] wallrow = new int[(rows * columns) / 2];
		int[] wallcol = new int[(rows * columns) / 2];
		for (i = 0; i < rows; i++)
		{
			for (j = 0; j < columns; j++)
			{
				maze[i][j] = wallCode;
			}
		}
		for (i = 1; i < rows - 1; i += 2)
		{
			for (j = 1; j < columns - 1; j += 2)
			{
				emptyCt++;
				maze[i][j] = -emptyCt;
				if (i < rows - 2)
				{
					wallrow[wallCt] = i + 1;
					wallcol[wallCt] = j;
					wallCt++;
				}
				if (j < columns - 2)
				{
					wallrow[wallCt] = i;
					wallcol[wallCt] = j + 1;
					wallCt++;
				}
			}
		}
		int r = 0;
		for (i = wallCt - 1; i > 0; i--)
		{
			r = (int)(Math.random() * i);
			tearDown(wallrow[r], wallcol[r], maze);
			wallrow[r] = wallrow[i];
			wallcol[r] = wallcol[i];
		}
		for (i = 1; i < rows - 1; i++)
		{
			for (j = 1; j < columns - 1; j++)
			{
				if (maze[i][j] < 0)
					maze[i][j] = emptyCode;
			}
		}
		return maze;
	}

	synchronized static void tearDown(int row, int col, int[][] maze)
	{
		if (row % 2 == 1 && maze[row][col - 1] != maze[row][col + 1])
		{
			fill(row, col - 1, maze[row][col - 1], maze[row][col + 1], maze);
			maze[row][col] = maze[row][col + 1];
			if (slow_process && started)
			{
				try
				{
					generated_maze = maze;
					Gui.g.pf.matrix = getNodeMatrix();
					Thread.sleep(proc_speed);
				}
				catch (Exception e){}
			}
		}
		else if (row % 2 == 0 && maze[row - 1][col] != maze[row + 1][col])
		{
			if (slow_process && started)
			{
				try
				{
					generated_maze = maze;
					Gui.g.pf.matrix = getNodeMatrix();
					Thread.sleep(proc_speed);
				}
				catch (Exception e){e.printStackTrace();}
			}
			fill(row - 1, col, maze[row - 1][col], maze[row + 1][col], maze);
			maze[row][col] = maze[row + 1][col];
		}
	}

	private static void fill(int row, int col, int replace, int replaceWith, int[][] maze)
	{
		if (maze[row][col] == replace)
		{
			maze[row][col] = replaceWith;
			if (row < rows - 1)
				fill(row + 1, col, replace, replaceWith, maze);
			if (row > 0)
				fill(row - 1, col, replace, replaceWith, maze);
			if (col < columns - 1)
				fill(row, col + 1, replace, replaceWith, maze);
			if (col > 0)
				fill(row, col - 1, replace, replaceWith, maze);
		}
	}
}