/*
 * Copyright (C) 2013 zzl09
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

package com.example.zioc_for_android;

import java.lang.reflect.Field;

import org.stackx.debug.ZLog;

import android.app.Activity;

public class ZActivity extends Activity {
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initChildView();
	}

	private void initChildView() {
		Field[] fields = getClass().getDeclaredFields();
		if (fields != null && fields.length > 0)
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					if (field.get(this) != null)
						continue;
					setChildViewHolderField(this,field);
					setViewField(this, field);
				} catch (Exception e) {
					ZLog.e(e);
				}
			}
	}

	private void setChildViewHolderField(Object instant,Field field)
			throws Exception {
		AViewHolder viewHolderInject = field.getAnnotation(AViewHolder.class);
		if (viewHolderInject != null) {
			Class<?> holderClass = field.getType();
			Object holder = holderClass.getDeclaredConstructors()[0].newInstance(instant);
			ZLog.e(holderClass.getDeclaredConstructors());
			for (Field holderField : holderClass.getDeclaredFields()) {
				holderField.setAccessible(true);
				if (holderField.get(holder) != null)
					continue;
				setViewField(holder, holderField);
			}
			field.set(this, holder);
		}
	}

	private void setViewField(Object instant, Field field)
			throws Exception {
		ViewInject viewInject = field.getAnnotation(ViewInject.class);
		if (viewInject != null) {
			int viewId = viewInject.id();
			field.set(instant, findViewById(viewId));
		}
	}

}
