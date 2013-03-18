package game;

import game.PathFinding.DISTANCE;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import run.Gui;

@SuppressWarnings("serial")
public class Game extends JPanel implements Runnable
{
	public int width;
	public int height;
	public int q;
	
	private MouseHandler mh;
	
	public PathFinding pf;
	
	public Game()
	{
		mh = new MouseHandler();
		setDoubleBuffered(true);
		q = 20;
		width = 51;
		height = 41;
		MazeGenerator.slow_process = false;
		pf = new PathFinding(width, height);
		pf.setDelay(15);
		pf.setStart(1, 1, false);
		pf.setEnd(width - 2, height - 2, false);
		pf.setHeuristicDistanceType(DISTANCE.MANHATTAN);
		addMouseListener(mh);
		addMouseMotionListener(mh);
		new Thread(this).start();
	}
	
	public void changeSize(int w, int h, int q)
	{
		this.width = w;
		this.height = h;
		this.q = q;
		pf = new PathFinding(width, height);
		pf.setDelay(15);
		pf.setStart(1, 1, false);
		pf.setEnd(width - 2, height - 2, false);
		pf.setHeuristicDistanceType(DISTANCE.MANHATTAN);
	}
	
	public void run()
	{
		while (true)
		{
			repaint();
			try
			{
				Thread.sleep(1);
			}
			catch (Exception e){}
		}
	}
	
	public void start()
	{
		pf.clearPath();
		pf.multithread_find();
	}
	
	public void clear()
	{
		pf.clear();
	}
	
	public void clear_search()
	{
		pf.clearPath();
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		pf.render(g, q);
	}
	
	public class MouseHandler extends MouseAdapter
	{		
		public void mouseDragged(MouseEvent e)
		{
			action(e);
		}
		
		public void mouseMoved(MouseEvent e)
		{
			
		}
		
		public void mousePressed(MouseEvent e)
		{
			int x = e.getX() / q;
			int y = e.getY() / q;
			if (Gui.rb_start.isSelected())
			{
				pf.setStart(x, y, true);
			}
			else if (Gui.rb_end.isSelected())
			{
				pf.setEnd(x, y, true);
			}
			else if (Gui.rb_wall.isSelected())
			{
				pf.setWall(x, y);
			}
			else if (Gui.rb_clear.isSelected())
			{
				pf.clearAt(x, y);
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
			
		}
		
		private void action(MouseEvent e)
		{
			int x = e.getX() / q;
			int y = e.getY() / q;
			if (Gui.rb_wall.isSelected())
			{
				pf.setWall(x, y);
			}
			else if (Gui.rb_clear.isSelected())
			{
				pf.clearAt(x, y);
			}
		}
	}
}
