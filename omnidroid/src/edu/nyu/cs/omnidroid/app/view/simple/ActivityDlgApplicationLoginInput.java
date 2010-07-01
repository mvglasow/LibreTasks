/*******************************************************************************
 * Copyright 2009, 2010 OmniDroid - http://code.google.com/p/omnidroid
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
package edu.nyu.cs.omnidroid.app.view.simple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import edu.nyu.cs.omnidroid.app.R;
import edu.nyu.cs.omnidroid.app.view.simple.factoryui.FactoryActions;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelApplication;

/**
 * This dialog is a shell to contain UI elements specific to creating a login UI. Given an
 * application, we can construct the inner UI elements.
 */
public class ActivityDlgApplicationLoginInput extends Activity {

  /** Layout dynamically generated on our action type by FactoryActions. */
  private LinearLayout llContent;

  /** Main layout to which we append the dynamically generated layout. */
  private LinearLayout llDynamic;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initializeUI();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  private void initializeUI() {
    setContentView(R.layout.activity_dlg_action_input);

    Button btnOk = (Button) findViewById(R.id.activity_dlg_action_input_btnOk);
    btnOk.setOnClickListener(listenerBtnClickOk);

    Button btnHelp = (Button) findViewById(R.id.activity_dlg_action_input_btnHelp);
    btnHelp.setOnClickListener(listenerBtnClickInfo);

    llContent = (LinearLayout) findViewById(R.id.activity_dlg_action_input_llDynamicContent);

    // Add dynamic content from the application
    ModelApplication modelApp = UIDbHelperStore.instance().db().getApplication(
        RuleBuilder.instance().getChosenApplication().getDatabaseId());

    llDynamic = FactoryActions.buildLoginUI(modelApp, this);

    llContent.addView(llDynamic);
  }

  private View.OnClickListener listenerBtnClickOk = new View.OnClickListener() {
    public void onClick(View v) {
      ModelApplication application = RuleBuilder.instance().getChosenApplication();
      try {
        FactoryActions.buildApplicationFromLoginUI(application, llDynamic);
        UIDbHelperStore.instance().db().updateApplicationLoginInfo(application);
      } catch (Exception e) {
        UtilUI.showAlert(v.getContext(), "", e.toString());
        return;
      }

      finish();
    }
  };

  private View.OnClickListener listenerBtnClickInfo = new View.OnClickListener() {
    public void onClick(View v) {
      UtilUI.showAlert(v.getContext(), getString(R.string.login_info_title),
          getString(R.string.login_info_details));
    }
  };
}