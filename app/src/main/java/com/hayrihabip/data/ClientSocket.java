package com.hayrihabip.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Application;
import android.util.Log;

import com.hayrihabip.R;
import com.hayrihabip.items.Functions;

public class ClientSocket {
	public int CLIENTPORT;
	public String SERVERIP;
	
	public Application mApp;	
	
	private Socket socket;	
	public PrintWriter pWriter;
	public BufferedReader bReader;
	public OutputStreamWriter oSWriter;
	
	private Runnable updateUIThread;
	
	public ClientSocket(Application mApp) throws IOException{
		this(mApp, mApp.getResources().getInteger(R.integer.CLIENTPORT), mApp.getResources().getString(R.string.SERVERIP));	
	}
	
	public ClientSocket(Application mApp, int CLIENTPORT, String SERVERIP) throws IOException{
		this.mApp = mApp;
		this.updateUIThread = new GetMessages();
		this.CLIENTPORT = CLIENTPORT;
		this.SERVERIP = SERVERIP;

		new Thread(new ClientThread()).start();
	}

	class ClientThread implements Runnable {
		@Override
		public void run() {			
			try {
				socket = new Socket(InetAddress.getByName(SERVERIP), CLIENTPORT);
				
				oSWriter = new OutputStreamWriter(socket.getOutputStream());
				pWriter = new PrintWriter(new BufferedWriter(oSWriter), true);
				bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				new Thread(updateUIThread).start();
			} catch(Exception e) {
				Log.v("ClientThread", "ClientSocket - Error " + e.getMessage());			
			}
		}
	}
	
	class GetMessages implements Runnable{
		@Override
		public void run() {
			boolean control = true;
			
			while(control){
				try{
					String data = "";	
					char item = (char)bReader.read();
					
					if(item == '<'){
						while((item = (char)bReader.read()) != '>')
							data += item;
					}
					
					if(!Functions.IsNullOrEmpty(data))
						Log.v("GetMessages", "GetMessages data : " + data);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}		
	}
}