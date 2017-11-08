import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;

public class MultiThreadChatServer {


  private static ServerSocket serverSocket = null;
 
  private static Socket clientSocket = null;

  private static final int maxClientsCount = 10;
  private static final clientThread[] threads = new clientThread[maxClientsCount];
  private static final String[] groups =  new String[maxClientsCount];
  public static void main(String args[]) {


    int portNumber = 8081;
    if (args.length < 1) {
      System.out
          .println("Usage: java MultiThreadChatServer <portNumber>\n"
              + "Now using port number=" + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }

   
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;
        for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientSocket, threads,groups)).start();
            break;
          }
        }
        if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}


class clientThread extends Thread {
  private int GroupID=-1;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private String[] groups;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads,String[] groups) {
    this.clientSocket = clientSocket;
    this.groups = groups;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
  	String[] groups=this.groups;
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {
     
       Scanner sc=new Scanner(System.in);
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      os.println("Enter your name.");
      String name = is.readLine().trim();
      System.out.println(name);
      os.println("Enter your PWD.");
      String pwd = is.readLine().trim();
      if(!pwd.equals("Shah"))
      { os.println("invalid credentials");
      	}
      	os.println("1 to join ,2 to make a group");
      	int d=Integer.parseInt(is.readLine().trim());
      	// os.println(d);
      	if(d==1){
      	for(int i=0;i<groups.length;i++){
      		if(groups[i]!=null)
      		      os.println(i+": "+groups[i]);
      		       
      		      
      		}
      		os.println("Enter Your Choice");
      		       GroupID=Integer.parseInt(is.readLine().trim());
      	}else if (d==2){
      		os.println("Enter a group name");
      		 String grpname = is.readLine().trim();
      		 int h=0;
      		      while(groups[h]!=null)
      		      h++;
      		      
      		      groups[h]=grpname;
      		      GroupID=h;
      		     
      		}
      		else{	os.println("invalid choice");}
      		
      		
      		
      System.out.println("A new user "+name+"wants to enter chat, give access ? 1=yes/0=no ");
      int reply=sc.nextInt();
      if(!(reply==0))
      {
      os.println("Hello " + name
          + " to our chat room.\nTo leave enter /quit in a new line");
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("*** A new user " + name
              + " entered the chat room !!! ***");
        }
      }
      while (true) {
        String line = is.readLine();
        if (line.startsWith("/quit")) {
          break;
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null&&	threads[i].GroupID==GroupID) {
            threads[i].os.println("<" + name + "&gr; " + line);
          }
        }
      }
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] != null && threads[i] != this) {
          threads[i].os.println("*** The user " + name
              + " is leaving the chat room !!! ***");
        }
      }
      os.println("*** Bye " + name + " ***");

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
      for (int i = 0; i < maxClientsCount; i++) {
        if (threads[i] == this) {
          threads[i] = null;
        }
      }

      /*
       * Close the output stream, close the input stream, close the socket.
       */
      is.close();
      os.close();
      clientSocket.close();
    }
    else{
    	 os.println("Admin has denied your request");
    	}
    } catch (IOException e) {
    
    }	//ifblock
	

  }
}