import java.io.*;
import java.net.*;

class TCPClient {
  public static void main(String argv[]) throws Exception {
    final String hostname = "www.comp.nus.edu.sg/~chanmc/p1/text1";
    final int port = 80;

    String sentence;
    String modifiedSentence;

    // create input stream
    BufferedReader inFromUser =
      new BufferedReader(new InputStreamReader(System.in));

    // create clientSocket object of type Socket, connect to server
    Socket clientSocket = new Socket(hostname, port);

    // create output stream attached to socket
    DataOutputStream outToServer = 
      new DataOutputStream(clientSocket.getOutputStream());

    // create input stream attached to socket
    BufferedReader inFromServer =
      new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    sentence = inFromUser.readLine();

    // send line to server
    outToServer.writeBytes(sentence + "\n");

    // read line from server
    modifiedSentence = inFromServer.readLine();

    System.out.println("FROM SERVER:" + modifiedSentence);

    // close socket
    clientSocket.close();
  }
}

