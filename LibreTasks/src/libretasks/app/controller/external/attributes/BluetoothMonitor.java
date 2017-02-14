/*  
 * Copyright (c) 2017  LibreTasks - https://github.com/biotinker/LibreTasks  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 3 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 /*******************************************************************************
 * Copyright 2010 Omnidroid - http://code.google.com/p/omnidroid 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *******************************************************************************/
package libretasks.app.controller.external.attributes;

import libretasks.app.controller.events.BluetoothConnectedEvent;
import libretasks.app.controller.events.BluetoothDisconnectedEvent;
import libretasks.app.controller.events.TimeTickEvent;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

/**
 * Monitors Bluetooth state and broadcasts Bluetooth Connect/Disconnect Intent when devices connect or disconnect.
 */
public class BluetoothMonitor extends BroadcastReceiver implements SystemServiceEventMonitor {
	private static final String SYSTEM_SERVICE_NAME = "BLUETOOTH_SERVICE";
	private static final String MONITOR_NAME = BluetoothMonitor.class.getSimpleName();

	private Context context;  

	public BluetoothMonitor(Context context) {
		this.context = context;
	}

	@Override
	public String getSystemServiceName() {
		return SYSTEM_SERVICE_NAME;
	}

	@Override
	public void init() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		context.registerReceiver(this, intentFilter);
	}

	@Override
	public void stop() {
		context.unregisterReceiver(this);
	}

	@Override
	public String getMonitorName() {
		return MONITOR_NAME;
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public void onReceive(Context context, Intent intent) {
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if (device == null)
			return;
		// initalize to prevent Eclipse from nagging
		String address = "";
		String name = "";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			address = device.getAddress();
			name = device.getName();
		}
		if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
			Log.d(MONITOR_NAME, "ACL_CONNECTED intent received");
			Intent outIntent = new Intent(BluetoothConnectedEvent.ACTION_NAME);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
				outIntent.putExtra(BluetoothConnectedEvent.ATTRIBUTE_BLUETOOTH_DEVICE, address);
				outIntent.putExtra(BluetoothConnectedEvent.ATTRIBUTE_BLUETOOTH_DEVICE_NAME, name);
			}
			context.sendBroadcast(outIntent);
		} else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
			Log.d(MONITOR_NAME, "ACL_DISCONNECTED intent received");
			Intent outIntent = new Intent(BluetoothDisconnectedEvent.ACTION_NAME);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
				outIntent.putExtra(BluetoothDisconnectedEvent.ATTRIBUTE_BLUETOOTH_DEVICE, address);
				outIntent.putExtra(BluetoothDisconnectedEvent.ATTRIBUTE_BLUETOOTH_DEVICE_NAME, name);
			}
			context.sendBroadcast(outIntent);
		}
	}
}
