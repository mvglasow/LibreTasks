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
 * Copyright 2009, 2010 Omnidroid - http://code.google.com/p/omnidroid 
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

import libretasks.app.controller.Event;
import android.content.Intent;

/**
 * This class encapsulates an GpsFixLost event. It wraps the intent that triggered this event
 * and provides access to any attribute data associated with it.
 * 
 * This event fires whenever the GPS loses a previously held fix but remains enabled. It does not
 * fire when the GPS starts up, as there is no previous fix. Also, it does not fire when the last
 * app stops requesting updates and the GPS shuts down. Consequently, acquisition and loss of GPS
 * fix typically do not occur in pairs.
 */
public class GpsFixLostEvent extends Event {
  /** Event name (to match record in database) */
  public static final String APPLICATION_NAME = "GPS";
  public static final String EVENT_NAME = "GPS Fix Lost";
  public static final String ACTION_NAME = "GPS_FIX_LOST";

  /**
   * Constructs a new GpsFixLost object that holds an GpsFixLost event fired intent. This
   * intent holds the data needed to check the event against user defined rules.
   * 
   * @param intent
   *          the intent received when the GpsFixLost event was fired
   */
  public GpsFixLostEvent(Intent intent) {
    super(APPLICATION_NAME, EVENT_NAME, intent);
  }
}
