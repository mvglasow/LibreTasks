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

import libretasks.app.controller.util.DataTypeValidationException;

/**
 * Provides filtering capabilities for Wifi connections. 
 */
public class OmniWifi extends DataType {
	private String ssid;
	private String bssid = null;

	/* Tags for XML marshaling */
	private static final String omniWifiOpenTag = "<omniWifi>";
	private static final String omniWifiCloseTag = "</omniWifi>";
	private static final String ssidOpenTag = "<ssid>";
	private static final String ssidCloseTag = "</ssid>";
	private static final String bssidOpenTag = "<bssid>";
	private static final String bssidCloseTag = "</bssid>";

	/* data type name to be stored in db */
	public static final String DB_NAME = "WifiNetwork";

	public enum Filter implements DataType.Filter {
		EQUALS("equals"), NOTEQUALS("not equals");

		public final String displayName;

		Filter(String displayName) {
			this.displayName = displayName;
		}
	}

	/**
	 * @brief Creates a new {@code OmniWifi} from XML.
	 * 
	 * This is primarily used to recreate an OmniWifi from a string (created by calling
	 * {@link #toString()}.
	 * 
	 * @param json
	 */
	public OmniWifi(String omniWifiString) throws DataTypeValidationException {
		OmniWifi wifi = parseOmniWifi(omniWifiString);

		init(wifi);
	}

	/**
	 * @brief Creates a new OmniWifi from a literal SSID and (optionally) BSSID.
	 * 
	 * @param ssid The SSID of the WiFi network
	 * @param bssid The BSSID of the WiFI network, can be null
	 * 
	 * @throws DataTypeValidationException
	 *           if {@code ssid} is null.
	 */
	// TODO validate BSSID
	public OmniWifi(String ssid, String bssid) throws DataTypeValidationException {
		if (ssid == null)
			throw new DataTypeValidationException("SSID cannot be null");
		this.ssid = ssid;
		this.bssid = bssid;
	}

	private void init(OmniWifi wifi) {
		this.ssid = wifi.ssid;
		this.bssid = wifi.bssid;
	}

	/*
	 * TODO the parser methods were copied from OmniArea. Unless we switch to standard XML parsing
	 * methods, we might want to move these methods somewhere central (e.g. DataType) so all
	 * DataType subclasses can use them.
	 */

	/**
	 * Parses out the text located between first occurrences of the open and closed tags.
	 * 
	 * @param parseString
	 *          string to parse
	 * @param openTag
	 *          the opening tag
	 * @param closeTag
	 *          the closing tag
	 * @return text located between first occurrences of the first open and closed tags, or null if
	 *         proper tags are not found.
	 */
	private static String parseTagValue(String parseString, String openTag, String closeTag) {
		// TODO (dvo203): Replace by standard XML parsing methods.
		int beg, end;

		beg = parseString.indexOf(openTag);
		end = parseString.indexOf(closeTag);
		if (beg < 0 || end < 0) {
			return null;
		}
		if (beg > end) {
			return null;
		}
		if (beg + openTag.length() == end) {
			return "";
		}

		return parseString.substring(beg + openTag.length(), end);
	}

	/**
	 * Parses out text located between first occurrences of the open and closed tags.
	 * 
	 * @param parseString
	 *          string to parse
	 * @param openTag
	 *          the opening tag
	 * @param closeTag
	 *          the closing tag
	 * @param exception
	 *          exception to throw in case of conversion error.
	 * @return text located between first occurrences of the open and closed tags.
	 * @throws DataTypeValidationException
	 *           if proper tags are not found.
	 */
	private static String parseStringValue(String parseString, String openTag, String closeTag,
			DataTypeValidationException exception) throws DataTypeValidationException {
		// Temporary variable that holds the text of the parsed tag
		String tagValue;
		tagValue = parseTagValue(parseString, openTag, closeTag);
		if (tagValue == null) {
			throw exception;
		}
		return tagValue;
	}

	private static OmniWifi parseOmniWifi(String omniWifiString) throws DataTypeValidationException {
		final DataTypeValidationException validationFailed = new DataTypeValidationException(
				"String is not an OmniWifi.");

		// Parse OmniWifi
		String omniWifiBody = parseStringValue(omniWifiString, omniWifiOpenTag, omniWifiCloseTag,
				validationFailed);

		// Parse SSID
		String ssid = parseStringValue(omniWifiBody, ssidOpenTag, ssidCloseTag,
				validationFailed);
		// TODO unescape SSID

		// Parse BSSID
		String bssid = parseTagValue(omniWifiBody, bssidOpenTag, bssidCloseTag);
		// TODO flag invalid BSSIDs

		// create and return OmniWifi object
		return new OmniWifi(ssid, bssid);
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

	/*
	 * @see libretasks.app.controller.datatypes.DataType#matchFilter(DataType.Filter, DataType)
	 */
	@Override
	public boolean matchFilter(DataType.Filter filter, DataType userDefinedValue)
			throws IllegalArgumentException {
		if (!(filter instanceof Filter)) {
			throw new IllegalArgumentException("Invalid filter type '" + filter.toString()
					+ "' provided.");
		}
		if (userDefinedValue instanceof OmniWifi) {
			return matchFilter((Filter) filter, (OmniWifi) userDefinedValue);
		} else {
			throw new IllegalArgumentException("Matching filter not found for the datatype "
					+ userDefinedValue.getClass().toString() + ". ");
		}
	}

	public boolean matchFilter(Filter filter, OmniWifi comparisonValue) {
		switch (filter) {
		case EQUALS:
			return (ssid.equals(comparisonValue.ssid)
					&& ((bssid == null) || (comparisonValue.bssid == null) || bssid.equalsIgnoreCase(comparisonValue.bssid)));
		case NOTEQUALS:
			return !(ssid.equals(comparisonValue.ssid)
					&& ((bssid == null) || (comparisonValue.bssid == null) || bssid.equalsIgnoreCase(comparisonValue.bssid)));
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
		return this.ssid;
	}

	/**
	 * @brief Provides the string representation of the {@code OmniWifi} for storage and later recreation.
	 * 
	 * @return The contents of this {@code OmniWifi} in XML notation.
	 */
	@Override
	public String toString() {
		// FIXME properly escape characters in the SSID
		String omniWifiBody = "";
		if (bssid != null)
			omniWifiBody = omniWifiBody + bssidOpenTag + bssid + bssidCloseTag;
		omniWifiBody = omniWifiBody + ssidOpenTag + ssid + ssidCloseTag;
		return omniWifiOpenTag + omniWifiBody + omniWifiCloseTag;
	}
}
