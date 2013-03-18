package game;

import java.util.StringTokenizer;
import utilities.TextFileStream;
import utilities.std;

public class MapFile
{
	public static void saveToFile(Node[][] mat, String file_path)
	{
		TextFileStream file = new TextFileStream(file_path);
		if (file.exists())
			file.deleteFile();
		file.createFile();
		String f = "";
		for (int y = 0; y < mat[0].length; y++)
		{
			for (int x = 0; x < mat.length; x++)
			{
				f += std.str(mat[x][y].value) + " ";
			}
			f = f.trim();
			f += "\n";
		}
		file.addLine(f);
		file.apply();
	}
	
	public static Node[][] loadFromFile(String file_path, int w, int h)
	{
		TextFileStream file = new TextFileStream(file_path);
		if (!file.exists())
			return null;
		Node[][] mat = new Node[w][h];
		String f = file.getLines();
		StringTokenizer st = new StringTokenizer(f, "\n");
		int x = 0, y = 0;
		while (st.hasMoreTokens())
		{
			String t = st.nextToken().trim();
			if (t.equals(""))
				continue;
			StringTokenizer st2 = new StringTokenizer(t, " ");
			x = 0;
			while (st2.hasMoreTokens())
			{
				String tt = st2.nextToken().trim();
				if (tt.equals(""))
					continue;
				mat[x][y] = new Node(std.toint(tt));
				mat[x][y].x = x;
				mat[x][y].y = y;
				if (x < w - 1)
					x++;
			}
			if (y < h - 1)
				y++;
		}
		return mat;
	}
}
