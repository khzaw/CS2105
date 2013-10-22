import java.io.*;
import java.net.*;
import java.util.*;

class proxy {
  
  public static Map<String, String> fs = new HashMap<String, String>();
  public static Map<String, String> fr = new HashMap<String, String>();
  
  public static void main(String[] args) throws IOException {

    int port = Integer.parseInt(args[0]);
    String fileSub = args[1];
    String fileRedirect = args[2];

    createFS(fileSub); createFR(fileRedirect);

    ServerSocket serverSocket = new ServerSocket(port); 
    while(true) {
      proxyFunc(serverSocket);
      //proxyFunc(serverSocket.accept());
    }
  }

  public static void proxyFunc(ServerSocket serverSocket) throws IOException {

    Socket clientSocket = null, server = null;

    try {

      clientSocket = serverSocket.accept();
    
      
      OutputStream toClient = clientSocket.getOutputStream();
      InputStream fromClient = clientSocket.getInputStream();

      // STEP 1 :: Get the HTTP request header from firefox
      ByteArrayOutputStream clientBuffer = new ByteArrayOutputStream();
      int bytesRead;
      byte[] request = new byte[10240];
      while((bytesRead = fromClient.read(request, 0, request.length)) != -1) {
        clientBuffer.write(request, 0, bytesRead);    // get the body message
        if(endOfRequest(request)) break;
      }
      request = clientBuffer.toByteArray();
      clientBuffer.flush();

      InputStream inputStream = new ByteArrayInputStream(request);
      BufferedReader readerFromClient = new BufferedReader(new InputStreamReader(inputStream));

      String[] temp;
      String line, hostname = null, contentType = null;
      while((line = readerFromClient.readLine()) != null) {
        temp = line.split(" ");
        if(temp[0].equals("Host:"))
          hostname = temp[1];
      }

      hostname = checkRedirect(hostname);
        //System.out.println(hostname);

      // STEP 2 :: Forward the HTTP Requests to server host
      server = new Socket(hostname, 80);
      OutputStream toServer = server.getOutputStream();
      toServer.write(request);


      // STEP 3 :: get the repsonse data from server
      InputStream tempInputStream = server.getInputStream();
      BufferedReader in = new BufferedReader(new InputStreamReader(tempInputStream));      
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        temp = inputLine.split(" ");
        if(temp[0].equals("Content-Type:"))
          contentType = temp[1];

      }
      in.close();
      DataInputStream fromServer = new DataInputStream(tempInputStream);
      
      byte[] data = new byte[40960];
      int i;
      while((i = fromServer.read(data, 0, data.length)) != -1) {
        // STEP 4 :: finally, forward the data back to firefox
        toClient.write(data, 0, i);
        toClient.flush();
      }
      toClient.close();
    } catch(IOException e) {
      //System.err.println(e);
    } finally {
      try {
        if(clientSocket != null) clientSocket.close();
        if(server != null) server.close();
      } catch(IOException e) {}
    }
  }

  // helper method to detect the blank line, should have written a more elegant
  // one
  public static boolean endOfRequest(byte[] request) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request)));
    String line;
    while( (line=br.readLine()) != null) {
      if (line.equals(""))
        return true;
    }
    return false;
  }

  public static String checkRedirect(String hostname) {
    if(fr.containsKey(hostname)) {
      String answer = fr.get(hostname);
      return answer.substring(7);
    }
    return hostname;
  }

  public static void createFS(String fileSub) {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(fileSub));
      String [] temp;
      String str;
      while((str = in.readLine()) != null) {
        temp = str.split(" ");
        fs.put(temp[0], temp[1]);
      }
    } catch(IOException e) {
      System.err.println("IOException");
    } finally {
      try{
      if(in != null) in.close();
      } catch(IOException e) {};
    }
  
  }

  public static void createFR(String fileRedirect) {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(fileRedirect));
      String str;
      String [] temp;
      while((str = in.readLine()) != null) {
        temp = str.split(" ");
        fr.put(temp[0], temp[1]);
      }
    } catch(IOException e) {
      System.err.println("IOException");
    } finally {
      try{
      if(in != null) in.close();
      } catch(IOException e) {};
    }
  
  }

}
