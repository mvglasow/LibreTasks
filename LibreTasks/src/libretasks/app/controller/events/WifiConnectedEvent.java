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
package libretasks.app.controller.events;

import android.content.Intent;
import libretasks.app.controller.Event;
import libretasks.app.controller.datatypes.OmniWifi;
import libretasks.app.controller.util.DataTypeValidationException;

/**
 * This class encapsulates a WifiConnected event. It wraps the intent that triggered this event
 * and provides access to any attribute data associated with it.
 */
public class WifiConnectedEvent extends Event {
	/** Event name (to match record in database) */
	public static final String APPLICATION_NAME = "WiFi";
	public static final String EVENT_NAME = "WiFi Connected";
	public static final String ACTION_NAME = "WIFI_CONNECTED";

	public static final String ATTRIBUTE_WIFI = "WiFi";
	public static final String ATTRIBUTE_WIFI_SSID = "SSID";
	// BSSID is for internal use only and not exposed through the UI
	public static final String ATTRIBUTE_WIFI_BSSID = "BSSID";

	/** Cache any values that are requested because it is likely they will be asked for again */
	protected String bssid;
	protected String ssid;

	public WifiConnectedEvent(Intent intent) {
		super(APPLICATION_NAME, EVENT_NAME, intent);
		ssid = intent.getStringExtra(WifiConnectedEvent.ATTRIBUTE_WIFI_SSID);
		bssid = intent.getStringExtra(WifiConnectedEvent.ATTRIBUTE_WIFI_BSSID);
	}

	@Override
	public String getAttribute(String attributeName) throws IllegalArgumentException {
		if (attributeName.equals(WifiConnectedEvent.ATTRIBUTE_WIFI)) {
			try {
				return new OmniWifi(ssid, bssid).toString();
			} catch (DataTypeValidationException e) {
				e.printStackTrace();
				return null;
			}
		} else if (attributeName.equals(WifiConnectedEvent.ATTRIBUTE_WIFI_SSID)) {
			return ssid;
		} else {
			return super.getAttribute(attributeName);
		}
	}
}
