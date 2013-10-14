import java.io.*;
import java.net.*;

class DownloadURL {

  public static void main(String[] args)throws IOException {

    String hostname, path, outputFile, code;
    hostname = path = outputFile = code = null;

    int firstIndex = 0;
    final int port = 80;

    Socket clientSocket = new Socket();

    PrintWriter output = null;
    BufferedReader input = null;

    if(args.length > 0) {
      String[] temp = args[0].split("/", 2);
      hostname = temp[0];
      path = "/" + temp[1];
      outputFile = args[1];
    }

    try {
      clientSocket.connect(new InetSocketAddress(hostname, port));

      // writer for socket
      output = new PrintWriter(clientSocket.getOutputStream(), true);
      // reader for socket
      input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch(UnknownHostException e) {
      e.printStackTrace();
    }

    // Send message to server
    String message = String.format("GET %s HTTP/1.1\r\nHost: %s:%d \r\n",
                            path, hostname, port);
    output.println(message);

    // Get response from server
    String response;
    Boolean flag = false;
    FileOutputStream out = new FileOutputStream(outputFile);
    while((response = input.readLine()) != null)
    {
      System.out.print(response + "\t");
      System.out.println(response.getBytes());
      System.out.println();
      if(response.startsWith("HTTP/1.1")) {
        String[] temp = response.split(" ", 3);
        code = temp[1];
      }

      if(code.equals("200") && response.isEmpty() && !(flag)) {
        flag = true;
        continue;
      }

      if(code.equals("200") && flag) {
        if(!(response.equals("\n") || response.equals("\r\n") || response.equals("\r"))) {
          out.write(response.getBytes());
          out.write(System.getProperty("line.separator").getBytes());
        }
      }

      if(!(code.equals("200")) && flag==false) {
        out.write(code.getBytes());
        break;
      }
    }

    out.flush(); out.close();
    output.flush(); output.close();

    input.close();
    clientSocket.close();
  }
}
