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

import libretasks.app.controller.events.WifiConnectedEvent;
import libretasks.app.controller.events.WifiDisconnectedEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ConnectivityMonitor extends BroadcastReceiver implements
		SystemServiceEventMonitor {
	private static final String SYSTEM_SERVICE_NAME = "CONNECTIVITY_SERVICE";
	private static final String MONITOR_NAME = ConnectivityMonitor.class.getSimpleName();

	private Context context;
	private ConnectivityManager connectivityManager;
	private WifiManager wifiManager;
	
	/*
	 * Cached state. Ensure that wifiInfo is always null when networkInfo.getType() returns
	 * something other than TYPE_WIFI, because the rest of the code skips examining
	 * networkInfo.getType() and instead simply checks whether wifiInfo is null. Doing so will also
	 * catch situations in which WifiManager.getConnectionInfo() unexpectedly returns null (docs
	 * are unclear as to whether this may happen).
	 */
	private NetworkInfo networkInfo;
	private WifiInfo wifiInfo;

	public ConnectivityMonitor(Context context) {
		this.context = context;
	}

	@Override
	public String getSystemServiceName() {
		return SYSTEM_SERVICE_NAME;
	}

	@Override
	public void init() {
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
		if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI))
			wifiInfo = wifiManager.getConnectionInfo();
		else
			wifiInfo = null;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
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

	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkInfo oldNetworkInfo = networkInfo;
		networkInfo = connectivityManager.getActiveNetworkInfo();
		WifiInfo oldWifiInfo = wifiInfo;
		if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI))
			wifiInfo = wifiManager.getConnectionInfo();
		else
			wifiInfo = null;
		
		// WiFi disconnected
		if (hasWifiChanged(oldWifiInfo, wifiInfo) || 
				((oldWifiInfo != null)
					&& (oldNetworkInfo != null)
					&& oldNetworkInfo.isConnected()
					&& ((wifiInfo == null) 
							|| (networkInfo == null)
							|| (!networkInfo.isConnected())))) {
			Intent outIntent = new Intent(WifiDisconnectedEvent.ACTION_NAME);
			outIntent.putExtra(WifiDisconnectedEvent.ATTRIBUTE_WIFI_SSID, oldWifiInfo.getSSID());
			outIntent.putExtra(WifiDisconnectedEvent.ATTRIBUTE_WIFI_BSSID, oldWifiInfo.getBSSID());
			context.sendBroadcast(outIntent);
		}
		
		// WiFi connected
		if (hasWifiChanged(oldWifiInfo, wifiInfo) || 
				(((oldNetworkInfo == null) || (oldNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI) || (!oldNetworkInfo.isConnected()))
					&& (networkInfo != null)
					&& (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
					&& networkInfo.isConnected())) {
			Intent outIntent = new Intent(WifiConnectedEvent.ACTION_NAME);
			outIntent.putExtra(WifiConnectedEvent.ATTRIBUTE_WIFI_SSID, wifiInfo.getSSID());
			outIntent.putExtra(WifiConnectedEvent.ATTRIBUTE_WIFI_BSSID, wifiInfo.getBSSID());
			context.sendBroadcast(outIntent);
		}
	}
	
	/**
	 * @brief Determines if we have moved to a different wifi without losing connectivity.
	 * 
	 * Currently this only detects SSID changes. If the BSSID changes but the SSID does not, we
	 * currently assume the device has switched between two access points of the same wifi. This is
	 * not always correct: we may have different wifis configured with the same name, distinguished
	 * only by the BSSID, but unless Android returns a BSSID for configured wifis, we cannot tell
	 * for sure when we have switched to a different wifi with an identical SSID.
	 * 
	 * @return True if the wifi has changed since the last event, false in all other cases.
	 * Pure connect or disconnect events (where either the old or new state is disconnected, i.e.
	 * one of the two arguments are null) are also reported as false.
	 */
	private boolean hasWifiChanged(WifiInfo oldInfo, WifiInfo newInfo) {
		if ((oldInfo == null) || (newInfo == null))
			return false;
		return !(oldInfo.getSSID().equals(newInfo.getSSID()));
	}
}
