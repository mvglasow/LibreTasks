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
 * Provides data type for storing a check box input.
 */
public class OmniCheckBoxInput extends DataType {
  private Boolean value;

  /* data type name to be stored in db */
  public static final String DB_NAME = "CheckBoxInput";

  public enum Filter implements DataType.Filter {
    IS_TRUE("is true"), IS_FALSE("is false");
    
    public final String displayName;
    
    Filter(String displayName) {
      this.displayName = displayName;
    }
  }

  public OmniCheckBoxInput(Boolean value) {
    this.value = value;
  
  }

  public OmniCheckBoxInput(String value) {
    this.value = Boolean.parseBoolean(value);
  }

  @Override
  public String getValue() {
    return value.toString();
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
    switch ((Filter) filter) {
    case IS_TRUE:
      return value;
    case IS_FALSE:
      return !value;
    default:
      return false;
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }

  /**
   * @return the value
   */
  public boolean isChecked() {
    return value;
  }

}
