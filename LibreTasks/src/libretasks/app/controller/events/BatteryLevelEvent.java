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
import libretasks.app.controller.datatypes.OmniBatteryLevel;
import libretasks.app.controller.util.DataTypeValidationException;

/**
 * This class encapsulates a BatteryLevel event. It wraps the intent that triggered this event
 * and provides access to any attribute data associated with it.
 */
public class BatteryLevelEvent extends Event {
	/** Event name (to match record in database) */
	public static final String APPLICATION_NAME = "Battery";
	public static final String EVENT_NAME = "Battery Level Changed";
	public static final String ACTION_NAME = "BATTERY_LEVEL_CHANGED";

	public static final String ATTRIBUTE_BATTERY_LEVEL = "Battery level";

	/** Cache any values that are requested because it is likely they will be asked for again */
	protected String level;

	public BatteryLevelEvent(Intent intent) {
		super(APPLICATION_NAME, EVENT_NAME, intent);
		level = intent.getStringExtra(BatteryLevelEvent.ATTRIBUTE_BATTERY_LEVEL);
	}

	@Override
	public String getAttribute(String attributeName) throws IllegalArgumentException {
		if (attributeName.equals(BatteryLevelEvent.ATTRIBUTE_BATTERY_LEVEL)) {
			try {
				return new OmniBatteryLevel(level).toString(); // FIXME won't work
			} catch (DataTypeValidationException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return super.getAttribute(attributeName);
		}
	}
}
