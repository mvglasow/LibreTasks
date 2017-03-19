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

import libretasks.app.controller.util.DataTypeValidationException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * Provides a data type for storing battery level and filtering by it.
 * 
 * For battery levels reported by the device, this data type encapsulates both the current level
 * and the last reported level. This allows its two filters, {@code RISEN_TO} and
 * {@code DROPPED_TO}, to catch instances in which the target value for the filter was skipped: For
 * example, if the filter is of type {@code DROPPED_TO} with a target value of 7%, and the battery
 * level changes from 8% to 6%, the event would match the filter.
 */
public class OmniBatteryLevel extends DataType {
	private int value;
	private Integer oldValue = null;
	
	/* Tags for XML marshaling */
	private static final String omniBatteryLevelTag = "omniBatteryLevel";
	private static final String levelTag = "level";
	private static final String oldLevelTag = "oldLevel";

	/* data type name to be stored in db */
	public static final String DB_NAME = "BatteryLevel";

	public enum Filter implements DataType.Filter {
		RISEN_TO("has risen to"), DROPPED_TO("has dropped to");

		public final String displayName;

		Filter(String displayName) {
			this.displayName = displayName;
		}
	}

	/**
	 * Creates a new OmniBatteryLevel from integer values.
	 * 
	 * The {@code oldValue} argument is used only if this {@code OmniBatteryLevel} represents the
	 * currently reported battery level. When using a {@code OmniBatteryLevel} for comparison, this
	 * argument can be null.
	 * 
	 * @param value The battery level in percent
	 * @param oldValue The previous battery level
	 */
	public OmniBatteryLevel(int value, Integer oldValue) {
		this.value = value;
		this.oldValue = oldValue;
	}

	/**
	 * @brief Creates a new {@code OmniBatteryLevel} from XML.
	 * 
	 * This is primarily used to recreate an OmniBatteryLevel from a string (created by calling
	 * {@link #toString()}.
	 * 
	 * @param xmlString A valid XML string, as produced by the {@link #toString()} method.
	 */
	public OmniBatteryLevel(String xmlString) throws DataTypeValidationException {
		OmniBatteryLevel obl = parseOmniBatteryLevel(xmlString);
		init(obl);
	}
	
	private void init(OmniBatteryLevel obl) {
		this.value = obl.value;
		this.oldValue = obl.oldValue;
	}

	@Override
	public String getValue() {
		return ((Integer) value).toString();
	}

	private static OmniBatteryLevel parseOmniBatteryLevel(String omniBatteryLevelString) throws DataTypeValidationException {
		final DataTypeValidationException validationFailed = new DataTypeValidationException(
				"String is not an OmniBatteryLevel.");
		
		Integer level = null;
		Integer oldLevel = null;
		
		XmlPullParser parser = Xml.newPullParser();
		StringReader reader = new StringReader(omniBatteryLevelString);
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
					if ((tagStack.size() < 2) || !tagStack.get(0).equals(omniBatteryLevelTag))
						throw validationFailed;
					if (tagStack.get(1).equals(levelTag)) {
						if (tagStack.size() == 2)
							level = Integer.valueOf(parser.getText());
					} else if (tagStack.get(1).equals(oldLevelTag)) {
						if (tagStack.size() == 2)
							oldLevel = Integer.valueOf(parser.getText());
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
		
		if (level == null)
			throw new DataTypeValidationException("level must be supplied.");
		
		// create and return OmniBatteryLevel object
		return new OmniBatteryLevel(level, oldLevel);
	}

	/**
	 * Returns Filter represented by filterName.
	 * 
	 * @param filterName
	 *          the filter name.
	 * @return Filter represented by filterName
	 * @throws IllegalArgumentException
	 *           when the filter with the given name does not exist.
	 */
	public static Filter getFilterFromString(String filterName) throws IllegalArgumentException {
		return Filter.valueOf(filterName.toUpperCase());
	}

	@Override
	public boolean matchFilter(DataType.Filter filter, DataType userDefinedValue)
			throws IllegalArgumentException {
		if (filter == null || !(filter instanceof Filter)){
			throw new IllegalArgumentException("Invalid filter "+filter+" provided.");
		}
		if (userDefinedValue instanceof OmniBatteryLevel) {
			return matchFilter((Filter) filter, (OmniBatteryLevel) userDefinedValue);
		} else {
			throw new IllegalArgumentException("Matching filter not found for the datatype "
					+ userDefinedValue.getClass().toString() + ". ");
		}
	}

	public boolean matchFilter(Filter filter, OmniBatteryLevel comparisonValue) {
		switch (filter) {
		case RISEN_TO:
			return ((oldValue != null) && (oldValue < comparisonValue.value) && (value >= comparisonValue.value));
		case DROPPED_TO:
			return ((oldValue != null) && (oldValue > comparisonValue.value) && (value <= comparisonValue.value));
		default:
			return false;
		}
	}

	/**
	 * @brief Provides the string representation of the {@code OmniBatteryLevel} for storage and later recreation.
	 * 
	 * @return The contents of this {@code OmniBatteryLevel} in XML notation.
	 */
	@Override
	public String toString() {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startTag(null, omniBatteryLevelTag);
			serializer.startTag(null, levelTag);
			serializer.text(((Integer) value).toString());
			serializer.endTag(null, levelTag);
			if (oldValue != null) {
				serializer.startTag(null, oldLevelTag);
				serializer.text(oldValue.toString());
				serializer.endTag(null, oldLevelTag);
			}
			serializer.endTag(null, omniBatteryLevelTag);
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
