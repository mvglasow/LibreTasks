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

import libretasks.app.controller.datatypes.OmniBatteryLevel;
import libretasks.app.controller.events.BatteryLevelEvent;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

public class BatteryMonitor extends BroadcastReceiver implements
		SystemServiceEventMonitor {
	private static final String SYSTEM_SERVICE_NAME = "BATTERY_SERVICE";
	private static final String MONITOR_NAME = BatteryMonitor.class.getSimpleName();

	private Context context;
	
	/*
	 * Cached state.
	 */
	private int batteryLevel = -1;

	public BatteryMonitor(Context context) {
		this.context = context;
	}

	@Override
	public String getSystemServiceName() {
		return SYSTEM_SERVICE_NAME;
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public void init() {
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR)
			return;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = context.registerReceiver(this, intentFilter);
		
		// Being a sticky intent, we get the last instance upon registering
		int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);

		batteryLevel = rawLevel * 100 / scale;
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
		int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);

		int newLevelPct = rawLevel * 100 / scale;
		
		if ((batteryLevel >= 0) && (rawLevel >= 0) && (newLevelPct != batteryLevel)) {
			/*
			 * Ignore the initial battery level (when the last level is -1), as well as invalid
			 * battery levels (raw level is -1) and events on which the percentage did not actually
			 * change.
			 */
			OmniBatteryLevel newLevel = new OmniBatteryLevel(newLevelPct, batteryLevel);
			Intent outIntent = new Intent(BatteryLevelEvent.ACTION_NAME);
			String temp = newLevel.toString();
			outIntent.putExtra(BatteryLevelEvent.ATTRIBUTE_BATTERY_LEVEL, temp);
			context.sendBroadcast(outIntent);

			batteryLevel = newLevelPct;
		}
	}
}
