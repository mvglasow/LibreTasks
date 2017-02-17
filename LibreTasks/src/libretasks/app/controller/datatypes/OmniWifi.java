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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;
import libretasks.app.controller.util.DataTypeValidationException;

/**
 * Provides filtering capabilities for Wifi connections. 
 */
public class OmniWifi extends DataType {
	private String ssid;
	private String bssid = null;

	/* Tags for XML marshaling */
	private static final String omniWifiTag = "omniWifi";
	private static final String ssidTag = "ssid";
	private static final String bssidTag = "bssid";

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

	private static OmniWifi parseOmniWifi(String omniWifiString) throws DataTypeValidationException {
		final DataTypeValidationException validationFailed = new DataTypeValidationException(
				"String is not an OmniWifi.");
		
		String ssid = null;
		String bssid = null;
		
		XmlPullParser parser = Xml.newPullParser();
		StringReader reader = new StringReader(omniWifiString);
		Stack<String> tagStack = new Stack<String>();
		
		try {
			parser.setInput(reader);
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					// start tag found, store it
					tagStack.push(parser.getName());
				} else if (eventType == XmlPullParser.END_TAG) {
					// end tag found, pop from stack (exception if it doesn't match the top element)
					if (tagStack.empty() || !tagStack.pop().equals(parser.getName()))
						throw validationFailed;
				} else if (eventType == XmlPullParser.TEXT) {
					// text found, parse it
					if ((tagStack.size() < 2) || !tagStack.get(0).equals(omniWifiTag))
						throw validationFailed;
					if (tagStack.get(1).equals(ssidTag)) {
						if (tagStack.size() == 2)
							ssid = parser.getText();
					} else if (tagStack.get(1).equals(bssidTag)) {
						if (tagStack.size() == 2)
							bssid = parser.getText();
					}
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw validationFailed;
		} catch (IOException e) {
			e.printStackTrace();
			throw validationFailed;
		} finally {
			reader.close();
		}
		
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
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startTag(null, omniWifiTag);
			serializer.startTag(null, ssidTag);
			serializer.text(ssid);
			serializer.endTag(null, ssidTag);
			if (bssid != null) {
				serializer.startTag(null, bssidTag);
				serializer.text(bssid);
				serializer.endTag(null, bssid);
			}
			serializer.endTag(null, omniWifiTag);
			serializer.endDocument();
			return writer.toString();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
