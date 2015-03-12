package com.example.transfer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends ActionBarActivity {

 EditText editTextAddress;
 Button buttonConnect;
 TextView textPort;

 static final int SocketServerPORT = 8080;

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  Log.e("Debug","Start");
  
  editTextAddress = (EditText) findViewById(R.id.address);
  textPort = (TextView) findViewById(R.id.port);
  textPort.setText("port: " + SocketServerPORT);
  buttonConnect = (Button) findViewById(R.id.connect);
  
  buttonConnect.setOnClickListener(new OnClickListener(){

   @Override
   public void onClick(View v) {
    ClientRxThread clientRxThread = 
     new ClientRxThread(
      editTextAddress.getText().toString(), 
      SocketServerPORT);
    
    clientRxThread.start();
   }});
 }

 private class ClientRxThread extends Thread {
  String dstAddress;
  int dstPort;
  ClientRxThread(String address, int port) {
   dstAddress = address;
   dstPort = port;
   Log.e("Debug",dstAddress);
  }

  @Override
  public void run() 
  {
	  Socket socket = null;
	   
	  try {
		  	Log.e("Debug","Before Connection");
		    socket = new Socket(dstAddress, dstPort);
		    Log.e("Debug","Connection made");
	 
   File file = new File(
     Environment.getExternalStorageDirectory(), 
     "test.txt");
   
   byte[] bytes = new byte[(int) file.length()];
   BufferedInputStream bis;
   try {
    bis = new BufferedInputStream(new FileInputStream(file));
    bis.read(bytes, 0, bytes.length);
    OutputStream os = socket.getOutputStream();
    os.write(bytes, 0, bytes.length);
    os.flush();
    socket.close();
    
    final String sentMsg = "File sent to: " + socket.getInetAddress();
    MainActivity.this.runOnUiThread(new Runnable() {

     @Override
     public void run() {
      Toast.makeText(MainActivity.this, 
        sentMsg, 
        Toast.LENGTH_LONG).show();
     }});
    
   } catch (FileNotFoundException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
	  }
	  catch (IOException e) {

		    e.printStackTrace();
		    
		    final String eMsg = "Something wrong: " + e.getMessage();
		    MainActivity.this.runOnUiThread(new Runnable() {

		     @Override
		     public void run() {
		      Toast.makeText(MainActivity.this, 
		        eMsg, 
		        Toast.LENGTH_LONG).show();
		     }});
		    
		   }
   
   
   finally {
    try {
     socket.close();
    } catch (IOException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
    }
   }
   
  }
 }

 
 
}