import java.io.*;
import java.net.*;

class TCPServer {
  public static void main(String[] args) throws IOException {
    String clientSentence;
    String capitalizedSentence;

    // create a welcoming socket at port 6789
    ServerSocket welcomeSocket = new ServerSocket(6789);

    while(true) {
      // wait, on welcoming socket accept() method for client contact create,
      // new socket return
      Socket connectionSocket = welcomeSocket.accept();

      // create input stream, attached to socket
      BufferedReader inFromClient =
        new BufferedReader(
            new InputStreamReader(connectionSocket.getInputStream()));

      // create output stream, attached to socket
      DataOutputStream outToClient =
        new DataOutputStream(connectionSocket.getOutputStream());

      // read in line from socket
      clientSentence = inFromClient.readLine();

      capitalizedSentence = clientSentence.toUpperCase() + "\n";

      // write out line to socket 
      outToClient.writeBytes(capitalizedSentence);
    }
  }
}
