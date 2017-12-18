package com.example.lxt.ujb_project.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.lxt.ujb_project.fragment.HomeFragment;


/**
 * �ֻ��˵� ����� ��Ҫȥ�����ƶ�������   ����������
 * @author Joney
 *
 */
public class BluetoothCommService {

	// Debugging
	private static final String TAG = "BluetoothComm";

	private static final boolean D = true;

	private static final String NAME = "123";
	// SPPЭ��UUID
	private static final UUID SPP_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// Member fields
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private int mState;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;

	public static final int STATE_NONE = 0; // �����κ���
	public static final int STATE_LISTEN = 1; // ��������
	public static final int STATE_CONNECTING = 2; // ������������
	public static final int STATE_CONNECTED = 3; // ��������Զ���豸

	public BluetoothCommService(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandler = handler;
		mState = STATE_NONE;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;
		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(HomeFragment.MESSAGE_STATE_CHANGE, state, -1)
				.sendToTarget();
	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the service. Specifically start AcceptThread to begin a session in
	 * listening (server) mode. Called by the Activity onResume() ���������߳�
	 */
	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to listen on a BluetoothServerSocket
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();// ���������߳�
		}
		setState(STATE_LISTEN);
	}

	/**
	 * ����һ���������� ���ȵ�������������ӵĶ���
	 * 
	 * @param device
	 */
	public synchronized void connect(BluetoothDevice device) {

		if (mState == STATE_CONNECTING) {// ��������״̬
			if (mConnectThread != null) {// ��ConnectThread������
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {// �д��������е��̣߳�����
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// ��ʼ����һ���߳�ȥ����
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();// �����µ����������߳�

		setState(STATE_CONNECTING);
	}

	/**
	 * �����߳� Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) {
		if (D)
			Log.d(TAG, "connected");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();// �Ϳͻ��˿�ʼͨ��

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler
				.obtainMessage(HomeFragment.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(HomeFragment.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		setState(STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;// �õ������߳�
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * ����ʧ�� Indicate that the connection attempt failed and notify the UI
	 * Activity.
	 */
	private void connectionFailed() {
		setState(STATE_LISTEN);

		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(HomeFragment.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(HomeFragment.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	/**
	 * ���Ӷ�ʧ Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		setState(STATE_LISTEN);

		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(HomeFragment.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(HomeFragment.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	/**
	 * �����߳� ����������ģ��� һֱ�ȴ� �û�������
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			// Create a new listening server socket
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME,
						SPP_UUID);
			} catch (IOException e) {
				Log.e("��ȡͨ��","���ٶ�ȡͨ��ʧ��");
			}
			// �õ�BluetoothServerSocket����
			mmServerSocket = tmp;
		}

		public void run() {
			setName("AcceptThread");// set the name of thread
			BluetoothSocket socket = null;// ����һ��������

			// Listen to the server socket if we're not connected
			Log.e("��ȡͨ��", "��ȡ��ǰ��״̬�Ա�"+mState+"&"+STATE_CONNECTED);
			while (mState != STATE_CONNECTED) {// ��û�����ӣ�һֱִ��
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					// ��һ�������̣߳���˲�Ҫ����Activity��
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothCommService.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice());
							break;
							//TODO
						case STATE_NONE:
							
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate
							// new socket.
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
			if (D)
				Log.i(TAG, "END mAcceptThread");
		}

		public void cancel() {
			if (D)
				Log.d(TAG, "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of server failed", e);
			}
		}
	}

	
//////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * 
	 * �ͻ��� Ҳ�����ֻ��� �������� ����
	 * 
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;// Զ���豸

		public ConnectThread(BluetoothDevice device) {

			mmDevice = device;
			BluetoothSocket tmp = null;

			try {
				// ���һ��BluetoothSocket����
				tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
			} catch (IOException e) {
				Log.e(TAG, "create() failed");
			}
			mmSocket = tmp;
		}

		public void run() {
			setName("ConnectThread");

			// ȡ������
			mAdapter.cancelDiscovery();

			try {
				mmSocket.connect();
			} catch (IOException e) {
				connectionFailed();
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "�����������޷��ر�socket");
				}
				BluetoothCommService.this.start();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothCommService.this) {
				mConnectThread = null;
			}

			Log.e(TAG, "�Ѿ�ʵ������");
			// Start the connected thread���������ϣ���������
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "�ر�socketʧ��");
			}
		}
	}

	/**
	 * ����һ���Ѿ������˵� �߳� ���ڼ������е��������� This thread runs during a connection with a
	 * remote device. It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);

					// Send the obtained bytes to the UI Activity
					mHandler.obtainMessage(HomeFragment.MESSAGE_READ, bytes,
							-1, buffer).sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				// Share the sent message back to the UI Activity
				mHandler.obtainMessage(HomeFragment.MESSAGE_WRITE, -1, -1,
						buffer).sendToTarget();
			
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
}
