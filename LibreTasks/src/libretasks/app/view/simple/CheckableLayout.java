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
package libretasks.app.view.simple;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class CheckableLayout extends LinearLayout implements Checkable {
	private RadioButton checkbox;

	public CheckableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        // find checkable view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i)
        {
            View v = getChildAt(i);
            if (v instanceof RadioButton)
            {
                checkbox = (RadioButton) v;
            }
        }
    }

	@Override
	public void setChecked(boolean checked) {
		if (checkbox != null)
        {
            checkbox.setChecked(checked);
        }
	}

	@Override
	public boolean isChecked() {
		return checkbox != null ? checkbox.isChecked() : false;
	}

	@Override
	public void toggle() {
		if (checkbox != null)
        {
            checkbox.toggle();
        }
	}

}
