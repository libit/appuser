/*
 * Libit保留所有版权，如有疑问联系QQ：308062035
 * Copyright (c) 2018.
 */
package cn.lrapps.android.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.widget.Toast;

import com.lrcall.appuser.R;
import cn.lrapps.utils.LogcatTools;

import java.util.List;

public class ActivityPreferenceSettings extends PreferenceActivity
{
	private static final String TAG = ActivityPreferenceSettings.class.getName();
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(Preference preference, Object value)
		{
			String stringValue = value.toString();
			if (preference instanceof ListPreference)
			{
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);
				// Set the summary to reflect the new value.
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
			}
			else if (preference instanceof RingtonePreference)
			{
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue))
				{
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);
				}
				else
				{
					Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
					if (ringtone == null)
					{
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					}
					else
					{
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}
			}
			else if (preference instanceof EditTextPreference)
			{
				preference.setSummary(MyApplication.getContext().getString(R.string.pref_summary_current_value, stringValue));
			}
			else
			{
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};
	private static Preference.OnPreferenceClickListener sBindPreferenceClickListener = new Preference.OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			LogcatTools.debug(TAG + " OnPreferenceClickListener", preference.getKey() + " click");
			Toast.makeText(MyApplication.getContext(), preference.getKey() + " click", Toast.LENGTH_LONG).show();
			if (preference.hasKey() && preference.getKey().equals("preference_general"))
			{
				//                ActivityPreferenceSettings.GeneralPreferenceFragment.instantiate(MyApplication.getContext(), GeneralPreferenceFragment.class.getName());
			}
			return true;
		}
	};

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context)
	{
		return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
	}

	private static void bindPreferenceSummaryToValue(Preference preference)
	{
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
	}

	private static void bindPreferenceClick(Preference preference)
	{
		preference.setOnPreferenceClickListener(sBindPreferenceClickListener);
		sBindPreferenceClickListener.onPreferenceClick(preference);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen()
	{
		if (!isSimplePreferences(this))
		{
			return;
		}
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.
		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);
		bindPreferenceSummaryToValue(findPreference("app_column_count_portrait"));
		bindPreferenceSummaryToValue(findPreference("app_column_count_landscape"));
		bindPreferenceSummaryToValue(findPreference("app_font_size"));
		//        PreferenceCategory fakeHeader = new PreferenceCategory(this);
		//        fakeHeader.setTitle(R.string.pref_header_notifications);
		//        getPreferenceScreen().addPreference(fakeHeader);
		//        addPreferencesFromResource(R.xml.pref_all);
		//        bindPreferenceClick(findPreference("preference_general"));
		//        bindPreferenceClick(findPreference("preference_backup_and_restore"));
		// Add 'notifications' preferences, and a corresponding header.
		//		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		//		fakeHeader.setTitle(R.string.pref_header_notifications);
		//		getPreferenceScreen().addPreference(fakeHeader);
		//		addPreferencesFromResource(R.xml.pref_notification);
		//
		//		// Add 'data and sync' preferences, and a corresponding header.
		//		fakeHeader = new PreferenceCategory(this);
		//		fakeHeader.setTitle(R.string.pref_header_data_sync);
		//		getPreferenceScreen().addPreference(fakeHeader);
		//		addPreferencesFromResource(R.xml.pref_data_sync);
		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		//		bindPreferenceSummaryToValue(findPreference("sync_frequency"));
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane()
	{
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target)
	{
		if (!isSimplePreferences(this))
		{
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	@Override
	protected boolean isValidFragment(String fragmentName)
	{
		if (fragmentName.equals(GeneralPreferenceFragment.class.getName()))
		{
			return true;
		}
		else if (fragmentName.equals(BackupPreferenceFragment.class.getName()))
		{
			return true;
		}
		return super.isValidFragment(fragmentName);
	}

	/**
	 * This fragment shows general preferences only. It is used when the
	 * context is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			bindPreferenceSummaryToValue(findPreference("app_column_count_portrait"));
			bindPreferenceSummaryToValue(findPreference("app_column_count_landscape"));
			bindPreferenceSummaryToValue(findPreference("app_font_size"));
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class BackupPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_backup);
			bindPreferenceClick(findPreference("preference_backup_and_restore"));
		}
	}

	/**
	 * This fragment shows notification preferences only. It is used when the
	 * context is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);
			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		}
	}

	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * context is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DataSyncPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_data_sync);
			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("sync_frequency"));
		}
	}
}
