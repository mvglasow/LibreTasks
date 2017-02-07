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
 * Copyright 2010 OmniDroid - http://code.google.com/p/omnidroid
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
package libretasks.app.controller.external.actions;

import libretasks.app.controller.actions.SpeechAction;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.DONUT)
public class SpeechService extends Service implements TextToSpeech.OnInitListener {
	private String message;
	
	private TextToSpeech tts;

	@Override
	public IBinder onBind(Intent intent) {
		// return null because client can't bind to this service
		return null;
	}
	
	@Override
	public void onDestroy() {
		if (tts != null)
			tts.shutdown();
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
        	// Start speaking only after initialization has finished successfully.
            speak();
        } else if (status == TextToSpeech.ERROR) {
            Log.w(this.getClass().getSimpleName(), "TTS initialization failed");
        }
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
			message = intent.getStringExtra(SpeechAction.PARAM_MESSAGE);
			tts = new TextToSpeech(this.getBaseContext(), this);
		} else {
			tts = null;
		}
	}

	/**
	 * Speaks a message
	 * 
	 */
	private void speak() {
		if (tts != null) {
			tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
		}
	}
}
