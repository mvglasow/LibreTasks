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
package libretasks.app.view.simple.viewitem;

import java.util.Set;

import libretasks.app.R;
import libretasks.app.controller.datatypes.DataType;
import libretasks.app.controller.datatypes.OmniBluetoothDevice;
import libretasks.app.view.simple.model.ModelAttribute;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BluetoothDeviceViewItem extends AbstractViewItem {
	private final Activity activity;
	protected final ListView listView;
	protected ArrayAdapter<BluetoothDevice> listViewAdapter;

	/**
	 * Class Constructor.
	 * 
	 * @param id
	 *          the id used to uniquely identify this object.
	 * @param dataTypeDbID
	 *          the database id for {@link OmniBluetoothDevice}
	 * @param activity
	 *          the activity where this view item is to be built on
	 */
	public BluetoothDeviceViewItem(int id, long dataTypeDbID, Activity activity) {
		super(id, dataTypeDbID);

		this.activity = activity;
		listView = new ListView(activity);
		listView.setId(id);
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	/* (non-Javadoc)
	 * Create the UI for this object.
	 * 
	 * @param initData
	 *          data to be used for initializing the values in the {@link View} objects. Pass null for
	 *          no data.
	 * @return the {@link View} object representing the underlying {@link DataType}
	 */
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public View buildUI(DataType initData) {
		OmniBluetoothDevice selection = (initData instanceof OmniBluetoothDevice) ? (OmniBluetoothDevice) initData : null;
		BluetoothDevice selectedDevice = null;
		listViewAdapter = new ArrayAdapter<BluetoothDevice>(activity, R.layout.simple_list_item_2_single_choice, android.R.id.text1) {
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		        View view = super.getView(position, convertView, parent);
		        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
		        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

		        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
		        	text1.setText(getItem(position).getName());
		        	text2.setText(getItem(position).getAddress());
		        }
		        return view;
		    }
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
			Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
			// addAll() is not supported on APIs below 11
			for (BluetoothDevice device : devices) {
				listViewAdapter.add(device);
				if ((selection != null) && device.getAddress().equalsIgnoreCase(selection.getValue()))
					selectedDevice = device;
			}
		}
		/*
		 * This code will misbehave when editing a filter for which the device is no longer in the
		 * list (e.g. because it was unpaired): the device will not be shown and nothing will
		 * appear to be selected. The current implementation does not allow adding arbitrary
		 * devices to the list, as it operates directly on BluetoothDevice, which has no public
		 * constructor. 
		 */
		if (selectedDevice != null)
			listView.setItemChecked(listViewAdapter.getPosition(selectedDevice), true);
		listView.setAdapter(listViewAdapter);
		return listView;
	}

	/* (non-Javadoc)
	 * Get the data underlying the controls (that is, a subclass of DataType representing what the user entered/selected)
	 * 
	 * @return the data
	 * @throws Exception when data is invalid
	 */
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public DataType getData() throws Exception {
		BluetoothDevice item = listViewAdapter.getItem(listView.getCheckedItemPosition());
		if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) && (item != null)) {
			Log.d("BluetoothDeviceViewItem", "****ALL IS WELL. Returning OmniBluetoothDevice");
			return new OmniBluetoothDevice(((BluetoothDevice) item).getAddress());
		}
		else {
			Log.e("BluetoothDeviceViewItem", "****ERROR. Selection is null.");
			return null;
		}
	}

	/* (non-Javadoc)
	 * Save the state of this object into the {@code bundle} (basically, save all user input so it can be restored later) 
	 * 
	 * @param bundle
	 *          the {@link Bundle} object where the data will be saved
	 */
	@Override
	public void saveState(Bundle bundle) {
		BluetoothDevice item = listViewAdapter.getItem(listView.getCheckedItemPosition());
		if (item != null)
			bundle.putParcelable(String.valueOf(ID), item);
	}

	/* (non-Javadoc)
	 * Load the state of this object from the {@code bundle} (restore user input and others saved with saveState)
	 * 
	 * @param bundle
	 *          the {@link Bundle} object where the data will be extracted
	 * @throws Exception
	 */
	@Override
	public void loadState(Bundle bundle) throws Exception {
		String key = String.valueOf(ID);

		if (bundle.containsKey(key)) {
			Parcelable item = bundle.getParcelable(key);
			if (item instanceof BluetoothDevice)
				listView.setItemChecked(listViewAdapter.getPosition((BluetoothDevice) item), true);
		}
	}

	@Override
	public void insertAttribute(ModelAttribute attribute) {
		// Do nothing. This class does not support attributes.
	}
}
