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

import libretasks.app.R;
import libretasks.app.controller.datatypes.DataType;
import libretasks.app.controller.datatypes.OmniDayOfWeek;
import libretasks.app.controller.util.DataTypeValidationException;
import libretasks.app.view.simple.model.ModelAttribute;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DayOfWeekViewItem extends AbstractViewItem {
	private final Activity activity;
	protected final ListView listView;
	protected ArrayAdapter<String> listViewAdapter;

	/**
	 * Class Constructor.
	 * 
	 * @param id
	 *          the id used to uniquely identify this object.
	 * @param dataTypeDbID
	 *          the database id for {@link OmniDayOfWeek}
	 * @param activity
	 *          the activity where this view item is to be built on
	 */
	public DayOfWeekViewItem(int id, long dataTypeDbID, Activity activity) {
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
	@Override
	public View buildUI(DataType initData) {
		listViewAdapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_list_item_single_choice,
				android.R.id.text1,
				new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}) {
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		        View view = super.getView(position, convertView, parent);
		        TextView text1 = (TextView) view.findViewById(android.R.id.text1);

		        text1.setText(getItem(position));
		        return view;
		    }
		};
		listView.setAdapter(listViewAdapter);
		if (initData instanceof OmniDayOfWeek) {
			int pos = -1;
			for (int i = 0; i < listViewAdapter.getCount(); i++)
				if (listViewAdapter.getItem(i).equalsIgnoreCase(initData.toString()))
					pos = i;
			Log.e(this.getClass().getSimpleName(), String.format("%s at position %d", initData.toString(), pos));
			try {
				listView.setItemChecked(pos, true);
			} catch (Exception e) {
				// NOP
			}
		}
		return listView;
	}

	/* (non-Javadoc)
	 * Get the data underlying the controls (that is, a subclass of DataType representing what the user entered/selected)
	 * 
	 * @return the data
	 * @throws Exception when data is invalid
	 */
	@Override
	public DataType getData() throws Exception {
		int index = listView.getCheckedItemPosition();
		String item = ((index >= 0) && (listView.getCount() > index)) ? listViewAdapter.getItem(index) : null;
		if (item != null) {
			return new OmniDayOfWeek(item);
		}
		else {
			throw new DataTypeValidationException(activity.getString(R.string.bad_list_selection));
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
		int index = listView.getCheckedItemPosition();
		if ((index >= 0) && (listView.getCount() > index))
			bundle.putString(String.valueOf(ID), listViewAdapter.getItem(index));
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
			String item = bundle.getString(key);
			int pos = -1;
			for (int i = 0; i < listViewAdapter.getCount(); i++)
				if (listViewAdapter.getItem(i).equalsIgnoreCase(item))
					pos = i;
			Log.e(this.getClass().getSimpleName(), String.format("%s at position %d", item, pos));
			try {
				listView.setItemChecked(pos, true);
			} catch (Exception e) {
				// NOP
			}
		}
	}

	@Override
	public void insertAttribute(ModelAttribute attribute) {
		// Do nothing. This class does not support attributes.
	}
}
