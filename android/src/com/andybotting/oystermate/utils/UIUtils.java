/*  
 * Copyright 2010 Andy Botting <andy@andybotting.com>  
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This file is distributed in the hope that it will be useful, but  
 * WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 * General Public License for more details.  
 *  
 * You should have received a copy of the GNU General Public License  
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:
 * 
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andybotting.oystermate.utils;

import com.andybotting.oystermate.R;
import com.andybotting.oystermate.activity.WebLaunch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;
import android.widget.Toast;

public class UIUtils {

	/**
	 * Helper for creating toasts. You could call it a toaster.
	 */
	public static void popToast(Context context, CharSequence text) {
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * Show a dialog message
	 */
	public static void showMessage(Context context, String title, String message) {
		final SpannableString s = new SpannableString(message);
		Linkify.addLinks(s, Linkify.ALL);

		final AlertDialog d = new AlertDialog.Builder(context).setTitle(title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		}).setIcon(R.drawable.ic_dialog_alert).setMessage(s).create();
		d.show();
		((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * Launch the web view with the given URL
	 * 
	 * @param url
	 */
	public static void launchWebView(Context context, String url) {
		Bundle bundle = new Bundle();
		bundle.putString(WebLaunch.INTENT_URL, url);
		Intent intent = new Intent(context, WebLaunch.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

}
