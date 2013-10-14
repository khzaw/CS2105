import java.io.*;
import java.net.*;

class DownloadURL {

  public static void main(String[] args) throws IOException {

    String outputFile, code, url;
    outputFile = code = url = null;

    // get the command arguments
    if(args.length == 2) {
      url = args[0];
      outputFile = args[1];
    } else {
      throw new IllegalArgumentException("Insufficient Arguments");
    }

    saveUrl(url, outputFile);
  }

  /*
   * Method to save the content to a filename of a given URL
   */
  public static void saveUrl(String urlString, String filename) throws IOException {

    BufferedInputStream in = null;
    FileOutputStream fout = null;
    int responseCode = -1;
    HttpURLConnection connection = null;

    try {
      URL url = new URL("http://" + urlString);
      connection = (HttpURLConnection)url.openConnection();
      connection.setInstanceFollowRedirects(true);  // follow redirects 
      connection.connect();
      responseCode = connection.getResponseCode();
      in = new BufferedInputStream(connection.getInputStream());
    }
    catch(Exception e) {
      if(responseCode != -1) {    // for http connection errors, write the http code and exit
        fout = new FileOutputStream(filename);
        fout.write(String.valueOf(responseCode).getBytes());
        System.exit(1);
      }
      else {                      // for other errors, exit gracefully
        System.out.println("Error :" + e.toString());
        System.exit(1);
      }
    }

    try {
      fout = new FileOutputStream(filename);

      byte data[] = new byte[1024];   // initial buffer
      int count;
      while((count = in.read(data, 0, 1024)) != -1) {
        fout.write(data, 0, count);
      }
    } catch(Exception e) {        // IOException errors
      System.out.println("Error :" + e.toString());
    } finally {
      if(in != null) in.close();
      if(fout != null) fout.close();
    }
  }
}
