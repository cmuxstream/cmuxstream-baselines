// simple file handling class based on Roberts
// primitive error handling !!!

package stochastic;

import java.io.*;

public class FileIO
{
  // instance variables
  private String filename;
  private BufferedReader reader = null;
  private BufferedWriter writer = null;
  private boolean eof = false;

  // constructor with filename as parameter
  public FileIO(final String fname, char RorW)
  {
    filename = fname;

    switch (RorW)
    {
      case 'R':
          try
          {
            reader = new BufferedReader (new FileReader(filename));
          }
          catch(FileNotFoundException e)
          {
            error("can't open file: " + filename);
          }
          break;
      case 'W':
          try
          {
            writer = new BufferedWriter (new FileWriter(filename));
          }
          catch(IOException e)
          {
            error("can't open file: " + filename);
          }
          break;
      case 'A':
          try
          {
            writer = new BufferedWriter (new FileWriter(filename, true));
          }
          catch(IOException e)
          {
            error("can't open file: " + filename);
          }
          break;
        
        default :
          error("file action not specified");
    }
  } // end of constructor()

  // constructor with file handle as parameter
  public FileIO(final File file, char RorW)
  {
    filename = file.getName();
    switch (RorW)
    {
      case 'R':
          try
          {
            reader = new BufferedReader (new FileReader(file));
          }
          catch(FileNotFoundException e)
          {
            error("can't open file: " + filename);
          }
          break;
      case 'W':
          try
          {
            writer = new BufferedWriter (new FileWriter(filename));
          }
          catch(IOException e)
          {
            error("can't open file: " + filename);
          }
          break;
      default :
          error("file action not specified");
    }
  } // end of constructor()

  // close the file
  public final synchronized void close(char RorW)
  {
    switch (RorW)
    {
      case 'R':
          try
          {
            reader.close();
          }
          catch (IOException e)
          {
            error("Can't close file: " + filename);
          }
          break;
      case 'W':
          try
          {
            writer.close();
          }
          catch (IOException e)
          {
            error("Can't close file: " + filename);
          }
          break;
      case 'A':
          try
          {
            writer.close();
          }
          catch (IOException e)
          {
            error("Can't close file: " + filename);
          }
          break;          
          
      default :
          error("file action not specified");
    }
  } // end of close()


  // detect end of file
  public boolean eof()
  {
    return eof;
  } // end of eof()

  // read a string from opened file
  public final synchronized String readString()
  {
    String instring="";
    try
    {
      instring = reader.readLine();
    }
    catch (IOException e)
    {
      error ("readString failed for file: " + filename);
    }
    if (instring == null)
    {
      eof = true;
      instring = "";
    }
    return instring;
  } // end of readString()

  // write a string to opened file
  public final synchronized void writeString(final String outstring)
  {
    try
    {
      writer.write(outstring); 
    }
    catch (IOException e)
    {
      error ("writeString failed for file: " + filename);
    }
  } // end of writeString()

  public final synchronized void appendString(final String outstring)
  {
    try
    {
      writer.append(outstring); 
    }
    catch (IOException e)
    {
      error ("writeString failed for file: " + filename);
    }
  } // end of writeString()
  
  
  // write a new line character to opened file
  public final synchronized void writeNewLine()
  {
    try
    {
      writer.write('\n');
    }
    catch (IOException e)
    {
      error ("writeNewLine failed for file: " + filename);
    }
  } // end of writeNewLine()

    // write a new line character to opened file
  public final synchronized void appendNewLine()
  {
    try
    {
      writer.append('\n');
    }
    catch (IOException e)
    {
      error ("writeNewLine failed for file: " + filename);
    }
  } // end of writeNewLine()

  
  // display error messages
  public void error(String errormessage)
  {
    System.err.println(errormessage);
    System.err.println("Unable to continue - exiting program");
    System.exit(0);
  } // end of error()
}