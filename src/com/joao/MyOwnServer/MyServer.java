package com.joao.MyOwnServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {

    private ServerSocket serverSocket;
    private Socket socket;
    private Boolean verbose = true;
    private int port = 8085;
    private String method;
    private File file;
    private String fileRequested;
    private PrintWriter out;
    private BufferedOutputStream outData;


    public static void main(String[] args){

        MyServer myServer = new MyServer();
        myServer.prepare();
        myServer.readRequest();
        myServer.sendResponse();
        myServer.stopService();

    }

    public void prepare(){
        try{
            serverSocket = new ServerSocket(port);

            socket = serverSocket.accept();
            if(verbose){
                System.out.println("Connection started on port " + port);
            }
        }
        catch(IOException e){
            System.out.println(e.getStackTrace().toString());
        }
    }

    public void readRequest(){
        try{
            BufferedReader newBR = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String[] page = newBR.readLine().split(" ");
            method = page[0];
            fileRequested = page[1].substring(1,page[1].length());

        }
        catch (IOException e){
            System.out.println(e.getStackTrace().toString());
        }

    }

    public void sendResponse(){
         File file = new File("/home/joao/IdeaProjects/MyOwnServer/src/com/joao/MyOwnServer/" + fileRequested);
         int fileLength = (int) file.length();
         byte[] fileData = new byte[fileLength];

         if(method.equals("GET")){
             try{
                 FileInputStream fileStream = new FileInputStream(file);
                 fileStream.read(fileData);
                 out = new PrintWriter(socket.getOutputStream());
                 outData = new BufferedOutputStream(socket.getOutputStream());
             }
             catch(Exception e){
                 e.printStackTrace();
             }


             out.println("HTTP/1.1 200 OK");
             out.println("Server: Java HTTP Server");
             out.println("Content-type: " + "text/html");
             out.println("Content-length: " + fileLength);
             out.println();
             out.flush();

             try {
                 outData.write(fileData,0,fileLength);
                 outData.flush();
             }
             catch(IOException e){
                 e.printStackTrace();
             }

         }
    }

    public void stopService(){
        try{
            socket.close();
            serverSocket.close();
            out.close();
            outData.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
}
