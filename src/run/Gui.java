package run;

import game.Game;
import game.MapFile;
import game.MazeGenerator;
import game.PathFinding.DISTANCE;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class Gui extends JFrame
{
	public Gui instance;
	
	public static Game g;
	
	private JButton start;
	private JButton clear;
	private JButton clear_s;
	private JButton save;
	private JButton open;
	private JButton maze;
	
	public static JRadioButton rb_start;
	public static JRadioButton rb_end;
	public static JRadioButton rb_wall;
	public static JRadioButton rb_clear;
	
	private JLabel lbl_draw;
	private JLabel lbl_speed;
	private JLabel lbl_heur;
	private JLabel lbl_size;
	
	public static JLabel lbl_loops;
	public static JLabel lbl_time;
	public static JLabel lbl_info;
	
	private JCheckBox chk_diag;
	private JSlider speed;
	private JCheckBox chk_slow_maze;
	private JSlider maze_speed;
	
	private JComboBox<String> heuristics;
	private JComboBox<String> size;
	
	private ButtonGroup group;
	private JFileChooser dialog;

	public Gui(String titolo, int w, int h, boolean visible)
	{
		//-----------------------------------------------------------------------
		super(titolo);
		instance = this;
		setSize(w, h);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		//-----------------------------------------------------------------------

		g = new Game();
		g.setBounds(0, 0, g.width * g.q, g.height * g.q);
		setBounds(10, 10, g.getWidth() + 200, g.getHeight() + 28);
		add(g);

		start = new JButton("Start");
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				clear_message();
				g.start();
			}
		});
		add(start);
		
		clear_s = new JButton("Clear search");
		clear_s.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				clear_message();
				g.clear_search();
			}
		});
		add(clear_s);
		
		clear = new JButton("Clear");
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				clear_message();
				g.clear();
			}
		});
		add(clear);
		
		lbl_draw = new JLabel("Draw");
		add(lbl_draw);
		
		rb_start = new JRadioButton("Start");
		add(rb_start);
		
		rb_end = new JRadioButton("End");
		add(rb_end);
		
		rb_wall = new JRadioButton("Wall");
		add(rb_wall);
		
		rb_clear = new JRadioButton("Erase");
		add(rb_clear);
		
		group = new ButtonGroup();
		
		group.add(rb_clear);
		group.add(rb_end);
		group.add(rb_start);
		group.add(rb_wall);
		
		rb_start.setSelected(true);
		
		chk_diag = new JCheckBox("Allow diagonal");
		chk_diag.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				g.pf.setDiagonal(chk_diag.isSelected());
			}
		});
		add(chk_diag);
		
		lbl_speed = new JLabel("Speed");
		add(lbl_speed);
		
		speed = new JSlider();
		speed.setMaximum(-1);
		speed.setMinimum(-100);
		speed.setValue(-10);
		speed.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				g.pf.setDelay(speed.getValue() * -1);
			}
		});
		add(speed);
		
		save = new JButton("Save map");
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				int r = dialog.showSaveDialog(instance);
				if (r == JFileChooser.APPROVE_OPTION)
					MapFile.saveToFile(g.pf.matrix, dialog.getSelectedFile().getPath());
			}
		});
		add(save);
		
		open = new JButton("Open map");
		open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				int r = dialog.showOpenDialog(instance);
				if (r == JFileChooser.APPROVE_OPTION)
				{
					g.pf.loadFromFile(dialog.getSelectedFile().getPath());
					clear_message();
				}
			}
		});
		add(open);
		
		lbl_heur = new JLabel("Heuristics:");
		add(lbl_heur);
		
		
		lbl_loops = new JLabel("Loops: ");
		lbl_loops.setFont(new Font("Arial", Font.BOLD, 16));
		add(lbl_loops);
		
		lbl_time = new JLabel("Time: ");
		lbl_time.setFont(new Font("Arial", Font.BOLD, 16));
		add(lbl_time);
		
		heuristics = new JComboBox<String>();
		heuristics.addItem("Manhattan Distance");
		heuristics.addItem("Euclidean Distance");
		heuristics.addItem("Diagonal Distance");
		heuristics.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				DISTANCE d = DISTANCE.MANHATTAN;
				String i = (String)heuristics.getSelectedItem();
				if (i.trim().equalsIgnoreCase("manhattan distance"))
					d = DISTANCE.MANHATTAN;
				else if (i.trim().equalsIgnoreCase("euclidean distance"))
					d = DISTANCE.EUCLIDEAN;
				else if (i.trim().equalsIgnoreCase("diagonal distance"))
					d = DISTANCE.DIAGONAL;
				g.pf.setHeuristicDistanceType(d);
			}
		});
		add(heuristics);
		
		lbl_size = new JLabel("Size:");
		add(lbl_size);
		
		size = new JComboBox<String>();
		size.addItem("25x21");
		size.addItem("51x41");
		size.addItem("101x81");
		size.addItem("201x161");
		size.setSelectedIndex(1);
		size.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				g.pf.clearAll(false);
				if (size.getSelectedIndex() == 0)
				{
					g.changeSize(25, 21, 40);
					instance.setSize(instance.getWidth(), (25 * 40) + 28);
				}
				else if (size.getSelectedIndex() == 1)
				{
					g.changeSize(51, 41, 20);
					instance.setSize(instance.getWidth(), (51 * 20) + 28);
				}
				else if (size.getSelectedIndex() == 2)
				{
					g.changeSize(101, 81, 10);
					instance.setSize(instance.getWidth(), (101 * 10) + 28);
				}
				else if (size.getSelectedIndex() == 3)
				{
					g.changeSize(201, 161, 5);
				}
				g.setBounds(0, 0, g.width * g.q, g.height * g.q);
				setBounds(10, 10, g.getWidth() + 200, g.getHeight() + 28);
				setSizes();
				heuristics.setSelectedIndex(0);
				clear_message();
			}
		});
		add(size);
		
		maze = new JButton("Generate maze");
		maze.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				clear_message();
				MazeGenerator.generateMaze(g.width, g.height);
			}
		});
		add(maze);
		
		chk_slow_maze = new JCheckBox("Slow process");
		chk_slow_maze.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				maze_speed.setVisible(chk_slow_maze.isSelected());
				MazeGenerator.slow_process = chk_slow_maze.isSelected();
			}
		});
		add(chk_slow_maze);
		
		maze_speed = new JSlider();
		maze_speed.setMaximum(-1);
		maze_speed.setMinimum(-100);
		maze_speed.setValue(-10);
		maze_speed.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				MazeGenerator.proc_speed = maze_speed.getValue() * -1;
			}
		});
		add(maze_speed);
		
		lbl_info = new JLabel("");
		lbl_info.setFont(new Font("Arial", Font.BOLD, 16));
		add(lbl_info);
		
		
		chk_slow_maze.setSelected(true);
		chk_slow_maze.setSelected(false);
		dialog = new JFileChooser();
		setSizes();


		//-----------------------------------------------------------------------
		setVisible(visible);
	}
	
	public static void no_path_message()
	{
		lbl_info.setForeground(Color.red.darker());
		lbl_info.setText("No path found!");
	}
	
	public static void distance_message(int d)
	{
		lbl_info.setForeground(Color.green.darker());
		lbl_info.setText("Path length: " + d);
	}
	
	public static void clear_message()
	{
		lbl_info.setText("");
	}
	
	public void setSizes()
	{
		start.setBounds(g.getWidth() + 10, 5, getWidth() - (g.getWidth() + 20), 30);
		clear_s.setBounds(start.getX(), 40, getWidth() - (g.getWidth() + 20), 30);
		clear.setBounds(start.getX(), 70, getWidth() - (g.getWidth() + 20), 30);
		lbl_draw.setBounds(clear.getX() + 10, 110, 100, 15);
		rb_start.setBounds(clear.getX() + 20, 125, 100, 20);
		rb_end.setBounds(clear.getX() + 20, 145, 100, 20);
		rb_wall.setBounds(clear.getX() + 20, 165, 100, 20);
		rb_clear.setBounds(clear.getX() + 20, 185, 100, 20);
		chk_diag.setBounds(clear.getX(), 210, 200, 30);
		lbl_speed.setBounds(clear.getX() + 5, 250, 100, 20);
		speed.setBounds(clear.getX(), 270, getWidth() - (g.getWidth() + 30), 30);
		save.setBounds(clear.getX(), 320, clear.getWidth(), 30);
		open.setBounds(clear.getX(), 350, clear.getWidth(), 30);
		lbl_heur.setBounds(clear.getX(), 390, 100, 20);
		lbl_loops.setBounds(clear.getX(), getHeight() - 60, 400, 30);
		lbl_time.setBounds(lbl_loops.getX(), lbl_loops.getY() - 30, 400, 30);
		heuristics.setBounds(clear.getX(), 410, clear.getWidth(), 30);
		lbl_size.setBounds(clear.getX(), 450, 200, 20);
		size.setBounds(clear.getX(), 470, clear.getWidth(), 30);
		maze.setBounds(clear.getX(), 530, clear.getWidth(), 30);
		chk_slow_maze.setBounds(clear.getX(), 560, 200, 30);
		maze_speed.setBounds(clear.getX(), 590, getWidth() - (g.getWidth() + 30), 30);
		lbl_info.setBounds(clear.getX(), lbl_time.getY() - 40, getWidth() - (g.getWidth() + 30), 30);
	}


}
