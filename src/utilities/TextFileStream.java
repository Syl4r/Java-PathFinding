package utilities;
import java.io.*;
import java.util.Vector;


public class TextFileStream
{
	private File f;
	private FileReader fr;
	private FileWriter fw;
	private BufferedReader br;
	private BufferedWriter bw;
	private String path;
	private TextLines lines;
	private int current;
	
	
	public TextFileStream(String path)
	{
		f = new File(path);
		if (exists())
		{
			this.path = path;
			loadLines();
			current = 0;
		}
	}
	
	private boolean loadReader()
	{
		try
		{
			if (bw != null)
				bw.close();
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			return true;
		}
		catch (Exception e){}
		return false;
	}
	
	private boolean loadWriter()
	{
		try
		{
			if (br != null)
				br.close();
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			return true;
		}
		catch (Exception e){}
		return false;
	}
	
	private boolean closeAll()
	{
		try
		{
			if (br != null)
				br.close();
			if (bw != null)
				bw.close();
			return true;
		}
		catch (Exception e){}
		return false;
	}
	
	private boolean loadLines()
	{
		if (loadReader() && exists())
		{
			lines = new TextLines();
			String line = "";
			try
			{
				while ((line = br.readLine()) != null)
				{
					lines.addLine(line);
				}
				closeAll();
				return true;
			}
			catch (Exception e){}
		}
		return false;
	}
	
	private boolean find(String line)
	{
		return (exists() ? lines.contains(line) : false);
	}
	
	private boolean isValidIndex(int i)
	{
		return (exists() ? (i < lines.length() && i >= 0) : false);
	}
	
	private void writeLines()
	{
		if (exists())
		{
			for (int i = 0; i < lines.length(); i++)
			{
				try
				{
					bw.write(lines.getLine(i) + "\n");
				}
				catch (Exception e){break;}
			}
		}
	}
	
	private int getIndex(String line)
	{
		String tmp = "";
		int i = 0;
		if (exists())
		{
			while ((tmp = lines.getLine(i)) != null && !tmp.equals(line))
			{
				i++;
			}
		}
		return (tmp != null && !tmp.equals("") ? i : -1);
	}
	
	public boolean addLine(int i, String line)
	{
		return (exists() ? lines.addLine(i, line) : false);
	}
	
	public void removeLine(int i)
	{
		if (exists())
			lines.removeLine(i);
	}
	
	public boolean removeLine(String line)
	{
		return (exists() ? lines.removeLine(line) : false);
	}
	
	public void addLine(String line)
	{
		if (exists())
			lines.addLine(line);
	}
	
	public boolean editLine(int i, String newString)
	{
		if (isValidIndex(i) && exists())
		{
			editString(i, newString);
			return true;
		}
		return false;
	}
	
	public boolean editLine(String line, String newString)
	{
		if (exists() && find(line))
		{
			int i = getIndex(line);
			editString(i, newString);
			return true;
		}
		return false;
	}
	
	public String getLine(int i)
	{
		return (exists() ? lines.getLine(i) : null);
	}
	
	public int indexOf(String line)
	{
		if (exists())
		{
			return (find(line) ? getIndex(line) : -1);
		}
		return -1;
	}
	
	private void editString(int index, String newValue)
	{
		if (isValidIndex(index) && exists())
		{
			if (exists())
			{
				lines.removeLine(index);
				lines.addLine(index, newValue);
			}
		}
	}
	
	public String nextLine()
	{
		if (exists())
		{
			if (current < length())
			{
				current++;
				return getLine(current - 1);
			}
		}
		return null;
	}
	
	public boolean hasMoreLines()
	{
		return (current < length());
	}
	
	public void resetLineCount()
	{
		current = 0;
	}
	
	public int getCurrentLineCount()
	{
		return current;
	}
	
	public String getLines()
	{
		return lines.getLines(true);
	}
	
	public void trimAllLines()
	{
		if (exists())
		{
			for (int i = 0; i < lines.length(); i++)
			{
				String line = lines.getLine(i);
				editString(i, line.trim());
			}
		}
	}
	
	public void allLinesToLowerCase()
	{
		if (exists())
		{
			for (int i = 0; i < lines.length(); i++)
			{
				String line = lines.getLine(i);
				editString(i, line.toLowerCase());
			}
		}
	}
	
	public void clear()
	{
		if (exists())
		{
			lines.clear();
		}
	}
	
	public int length()
	{
		return lines.length();
	}
	
	public String getPath()
	{
		return path;
	}

	public boolean setPath(String path)
	{
		if (closeAll())
		{
			f = new File(path);
			if (exists())
			{
				loadLines();
				closeAll();
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteFile()
	{
		if (exists())
		{
			closeAll();
			f.delete();
			lines.clear();
			lines = null;
			current = 0;
			return true;
		}
		return false;
	}
	
	public boolean createFile()
	{
		if (!exists())
		{
			closeAll();
			try
			{
				f.createNewFile();
				loadReader();
				try
				{
					lines.clear();
				}
				catch (Exception e){}
				loadLines();
				closeAll();
				current = 0;
			}
			catch (Exception e){}
			return true;
		}
		return false;
	}
	
	public boolean exists()
	{
		return (f != null) ? f.exists() : null;
	}
	
	public boolean reload()
	{
		if (exists())
		{
			closeAll();
			loadLines();
			closeAll();
			current = 0;
			return true;
		}
		return false;
	}
	
	public boolean apply()
	{
		if (exists())
		{
			try
			{
				f.delete();
				f.createNewFile();
			}
			catch (Exception e){}
			closeAll();
			loadWriter();
			writeLines();
			closeAll();
			return true;
		}
		return false;
	}
	
	
	private class TextLines
 	{
 		private Vector<String> lines;
 		
 		public TextLines()
 		{
 			lines = new Vector<String>(1, 1);
 		}
 		
 		public void addLine(String line)
 		{
 			lines.add(line);
 		}
 		
 		public boolean addLine(int index, String line)
 		{
 			try
 			{
 				lines.add(index, line);
 				return true;
 			}
 			catch (Exception e){return false;}
 		}

 		public void removeLine(int index)
 		{
 			try
			{
 				lines.remove(index);
			}
			catch (Exception e){}
 		}
 		
 		public boolean removeLine(String line)
 		{
 			return lines.remove(line);
 		}
 		
 		public void clear()
 		{
 			lines.clear();
 		}

 		public String getLine(int index)
 		{
 			try
 			{
 				return lines.get(index);
 			}
 			catch (Exception e){return null;}
 		}
 		
 		public String getLines(boolean newLineChar)
 		{
 			String nl = (newLineChar) ? "\n" : "";
 			String allLines = "";
 			String tmp = "";
 			int i = 0;
 			while ((tmp = getLine(i)) != null)
 			{
 				allLines += tmp + nl;
 				i++;
 			}
 			return allLines;
 		}
 		
 		public boolean contains(String line)
 		{
 			for (String s : lines)
 			{
 				if (s.equals(line))
 					return true;
 			}
 			return false;
 		}
 		
 		public int length()
 		{
 			return lines.size();
 		}
 	}
}