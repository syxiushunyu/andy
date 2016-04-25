package blue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bluetooth.R;

public class MainActivity extends Activity implements OnClickListener{

	   
	private Button connectService;
	private Button openBluetooth;
	private Button requestConnect;
	private Button send;
	private EditText msg;
	private InputStream inputStream;
	private OutputStream outputStream;
	public BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice bluetoothDevice;
	private BluetoothServerSocket bluetoothServerSocket;
	private MyHandler myhandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}
	
	private void init(){
		connectService = (Button) findViewById(R.id.connectService);
		openBluetooth = (Button) findViewById(R.id.openBluetooth);
		requestConnect = (Button) findViewById(R.id.requestConnect);
		send = (Button) findViewById(R.id.send);
		msg = (EditText) findViewById(R.id.msg);
		connectService.setOnClickListener(this);
		openBluetooth.setOnClickListener(this);
		requestConnect.setOnClickListener(this);
		send.setOnClickListener(this);
	}
	
	//开启蓝牙
	private void openBlueTh(){
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter!=null){
			if(!bluetoothAdapter.isEnabled()){
				Intent open = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(open);
				Intent visbile = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				visbile.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				startActivity(visbile);
			}else{
				Toast.makeText(MainActivity.this,"蓝牙已经开启",Toast.LENGTH_SHORT).show();	
			}
		}else{
			Toast.makeText(MainActivity.this,"没有蓝牙设备",Toast.LENGTH_SHORT).show();
		}
	}
	
	//服务器端应答服务器请求
	private void connectService(){
		
	}
	
	//处理客户端发送的socket请求handler
	class MyHandler extends Handler{
		
		public MyHandler(){
			
		}
		
		public MyHandler(Looper l){
			super(l);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			byte[] buffer = new byte[1024];
			buffer = (byte[]) msg.obj;
			String str = new String(buffer,0,msg.arg1);
			
		}
		
	}
	
	//开启线程 服务器端注册BluetoothServerSocket来监听client端的BluetoothSocket请求
	public class RequestThread extends Thread{
		
		private BluetoothServerSocket m_bluetoothServerSocket = null;
		
		public RequestThread() {
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			try {
				m_bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("bluetooth",uuid);//服务器端注册一个具有名称和唯一标识的BluetoothServerSocket
			} catch (IOException e) {
				e.printStackTrace();
			}
			bluetoothServerSocket = m_bluetoothServerSocket;
			Toast.makeText(MainActivity.this,"服务已经开启",Toast.LENGTH_SHORT).show();
		}

		@Override
		public void run() {
			super.run();
			BluetoothSocket socket = null;
			while (true) {
				try {
					socket = bluetoothServerSocket.accept();//接受client的BluetoothSocket请求
					if(socket!=null){
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(socket!=null){
				Toast.makeText(MainActivity.this,"连接已建立",Toast.LENGTH_SHORT).show();
			}
		}
		
		//取消监听
		public void cancel(){
			try {
				m_bluetoothServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//处理socket
	public class ConnectThread extends Thread{
		
		private final BluetoothSocket bluetoothSocket;
		private Message msg = myhandler.obtainMessage();
		
		public ConnectThread(BluetoothSocket bluetoothSocket){
			this.bluetoothSocket = bluetoothSocket;
			InputStream is = null;
			OutputStream os = null;
			try {
				is = bluetoothSocket.getInputStream();
				os = bluetoothSocket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inputStream = is;
			outputStream = os;
		}
		
		@Override
		public void run() {
			byte[] data = new byte[1024];
			int bytes;
			while (true) {
				try {
					bytes = inputStream.read(data);
					msg.obj = data;
					msg.arg1 = bytes;
					myhandler.sendMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void write(byte[] bytes) {    
            try {    
                outputStream.write(bytes);  
            } catch (IOException e) { }    
        }  
		
		public void cancle(){
			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.connectService://连接服务端
			connectService();
			break;
		case R.id.openBluetooth://打开蓝牙
			openBlueTh();
			break;
		case R.id.requestConnect://
	
			break;
		case R.id.send://发送
	
			break;
		default:
			break;
		}
	}
	

}
