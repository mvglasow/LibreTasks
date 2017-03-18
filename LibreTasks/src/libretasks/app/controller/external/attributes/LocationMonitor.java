/*  
 * Copyright (c) 2016  LibreTasks - https://github.com/biotinker/LibreTasks  
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
package libretasks.app.controller.external.attributes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import libretasks.app.controller.Event;
import libretasks.app.controller.datatypes.OmniArea;
import libretasks.app.controller.events.GpsFixAcquiredEvent;
import libretasks.app.controller.events.GpsFixLostEvent;
import libretasks.app.controller.events.LocationChangedEvent;
import libretasks.app.controller.util.DataTypeValidationException;

/**
 * The class is responsible for communication with the Location Service. It provides access to
 * Location Services External Attribute, as well as Initiates Location Change Intents.
 */
public class LocationMonitor extends BroadcastReceiver implements SystemServiceEventMonitor {
  private static final String SYSTEM_SERVICE_NAME = "LOCATION_SERVICE";
  private static final String MONITOR_NAME = "LocationMonitor";
  private static OmniArea lastLocation;
  /** Minimum frequency in updates(in milliseconds). Default value is 300000 (5 minutes). */
  private static final long MIN_PROVIDER_UPDATE_INTERVAL = 300000;
  /** Minimum change in location(in meters). Default value is 50 meters. */
  private static final float MIN_PROVIDER_UPDATE_DISTANCE = 50;
  private static final String PROVIDER = LocationManager.GPS_PROVIDER;
  
  // Constants defined in com.android.internal.location.GpsLocationProvider
  /**
   * Broadcast intent action indicating that the GPS has either been
   * enabled or disabled. An intent extra provides this state as a boolean,
   * where {@code true} means enabled.
   * @see #EXTRA_ENABLED
   */
  public static final String GPS_ENABLED_CHANGE_ACTION = "android.location.GPS_ENABLED_CHANGE";
  
  /**
   * Broadcast intent action indicating that the GPS has either started or
   * stopped receiving GPS fixes. An intent extra provides this state as a
   * boolean, where {@code true} means that the GPS is actively receiving fixes.
   * @see #EXTRA_ENABLED
   */
  public static final String GPS_FIX_CHANGE_ACTION = "android.location.GPS_FIX_CHANGE";
  
  /**
   * The lookup key for a boolean that indicates whether GPS is enabled or
   * disabled. {@code true} means GPS is enabled. Retrieve it with
   * {@link android.content.Intent#getBooleanExtra(String,boolean)}.
   */
  public static final String EXTRA_ENABLED = "enabled";

  private Context context;
  
  /** Whether we currently have a fix. */
  private boolean hasFix = false;
  
  public LocationMonitor(Context context) {
    this.context = context;
  }
  
  public void init() {
    lastLocation = null;
    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    if (lm == null) {
      Log.i("LocationService", "Could not obtain LOCATION_SERVICE from the system.");
      return;
    }
    lm.requestLocationUpdates(PROVIDER, MIN_PROVIDER_UPDATE_INTERVAL, MIN_PROVIDER_UPDATE_DISTANCE,
        locationListener);
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(GPS_ENABLED_CHANGE_ACTION);
    intentFilter.addAction(GPS_FIX_CHANGE_ACTION);
    context.registerReceiver(this, intentFilter);
  }

  public void stop() {
    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    lm.removeUpdates(locationListener);
    context.unregisterReceiver(this);
  }

  private final LocationListener locationListener = new LocationListener() {
    public void onLocationChanged(Location location) {
      OmniArea newLocation;
      try {
        newLocation = new OmniArea(null, location.getLatitude(), location.getLongitude(), location
            .getAccuracy());
      } catch (DataTypeValidationException e) {
        newLocation = null;

      }

      if (newLocation != null && lastLocation != newLocation) {
        // Create intent
        Intent intent = new Intent(LocationChangedEvent.ACTION_NAME);
        String temp = newLocation.toString();
        intent.putExtra(Event.ATTRIBUTE_LOCATION, temp);
        context.sendBroadcast(intent);
      }
    }

    /** Required to implement. Do nothing. */
    public void onProviderDisabled(String provider) {
    }

    /** Required to implement. Do nothing. */
    public void onProviderEnabled(String provider) {
    }

    /** Required to implement. Do nothing. */
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
  };

  /**
   * Returns current location.
   * 
   * @return OmniArea object that has longitude, latitude, and accuracy (proximityDistance)
   * @throws ExternalAttributeAccessException
   *           throws an exception if attribute is unavailable.
   */
  public OmniArea getAttributeValue() throws ExternalAttributeAccessException {
    if (lastLocation == null) {
      Log.i("LocationService", "Could not obtain Current Location from the system.");
      throw new ExternalAttributeAccessException("Location Service is not available.");
    }
    return lastLocation;
  }

  public String getMonitorName() {
    return MONITOR_NAME;
  }

  public String getSystemServiceName() {
    return SYSTEM_SERVICE_NAME;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (GPS_ENABLED_CHANGE_ACTION.equals(intent.getAction()) &&
        intent.hasExtra(EXTRA_ENABLED) &&
        !intent.getBooleanExtra(EXTRA_ENABLED, false)) {
      // reset fix when GPS is released
      hasFix = false;
    } else if (GPS_FIX_CHANGE_ACTION.equals(intent.getAction())) {
      boolean newFix = (intent.getBooleanExtra(EXTRA_ENABLED, hasFix));
      if (newFix && !hasFix) {
        // fix acquired
        Intent outIntent = new Intent(GpsFixAcquiredEvent.ACTION_NAME);
        context.sendBroadcast(outIntent);    
      } else if (!newFix && hasFix) {
        // fix lost
        Intent outIntent = new Intent(GpsFixLostEvent.ACTION_NAME);
        context.sendBroadcast(outIntent);    
      }
      hasFix = newFix;
    }
  }
}
