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
 * Copyright 2009 Omnidroid - http://code.google.com/p/omnidroid
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
package libretasks.app.controller.datatypes;

/**
 * Provides filtering capabilities for Bluetooth devices. 
 */
public class OmniBluetoothDevice extends DataType {
	private String value;

	/* data type name to be stored in db */
	public static final String DB_NAME = "BluetoothDevice";

	public enum Filter implements DataType.Filter {
		EQUALS("equals"), NOTEQUALS("not equals");

		public final String displayName;

		Filter(String displayName) {
			this.displayName = displayName;
		}
	}

	public OmniBluetoothDevice(String str) {
		this.value = str;
	}

	/**
	 * 
	 * @param str
	 *          the filter name.
	 * @return Filter
	 * @throws IllegalArgumentException
	 *           when the filter with the given name does not exist.
	 */
	public static Filter getFilterFromString(String str) throws IllegalArgumentException {
		return Filter.valueOf(str.toUpperCase());
	}

	/* TODO implement
	 * @see libretasks.app.controller.datatypes.DataType#matchFilter(DataType.Filter, DataType)
	 */
	@Override
	public boolean matchFilter(DataType.Filter filter, DataType userDefinedValue)
			throws IllegalArgumentException {
		if (!(filter instanceof Filter)) {
			throw new IllegalArgumentException("Invalid filter type '" + filter.toString()
					+ "' provided.");
		}
		if (userDefinedValue instanceof OmniBluetoothDevice) {
			return matchFilter((Filter) filter, (OmniBluetoothDevice) userDefinedValue);
		} else {
			throw new IllegalArgumentException("Matching filter not found for the datatype "
					+ userDefinedValue.getClass().toString() + ". ");
		}
	}

	public boolean matchFilter(Filter filter, OmniBluetoothDevice comparisonValue) {
		switch (filter) {
		case EQUALS:
			return value.equalsIgnoreCase(comparisonValue.value);
		case NOTEQUALS:
			return !value.equalsIgnoreCase(comparisonValue.value);
		default:
			return false;
		}
	}

	/**
	 * Indicates whether or not the given filter is supported by the data type.
	 * 
	 * @param filter
	 * @return true if the filter is supported, false otherwise.
	 */
	public static boolean isValidFilter(String filter) {
		try {
			getFilterFromString(filter);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
