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
    private PrintWriter out_img;
    private BufferedOutputStream outData_img;


    public static void main(String[] args){

        MyServer myServer = new MyServer();
        while (true){
            myServer.prepare();
            myServer.readRequest();
            myServer.sendResponse();
            myServer.stopService();
        }

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
         File file = new File("www/" + fileRequested);
         int fileLength = (int) file.length();
         byte[] fileData = new byte[fileLength];

         //image file
        File fileImg = new File("www/lin.png");
        int fileImgLength = (int) fileImg.length();
        byte[] fileImgData = new byte[fileImgLength];

         if(method.equals("GET")){
             try{
                 FileInputStream fileStream = new FileInputStream(file);
                 fileStream.read(fileData);
                 out = new PrintWriter(socket.getOutputStream());
                 outData = new BufferedOutputStream(socket.getOutputStream());

                 //image content
                 FileInputStream fileImgStr = new FileInputStream(fileImg);
                 fileImgStr.read(fileImgData);
                 out_img = new PrintWriter(socket.getOutputStream());
                 outData_img = new BufferedOutputStream(socket.getOutputStream());

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

             out_img.println("HTTP/1.1 200 OK");
             out_img.println("Server: Java HTTP Server");
             out_img.println("Content-type: " + "image/png");
             out_img.println("Content-length: " + fileImgLength);
             out_img.println();
             out_img.flush();

             try {
                 outData_img.write(fileImgData,0,fileImgLength);
                 outData_img.flush();
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
