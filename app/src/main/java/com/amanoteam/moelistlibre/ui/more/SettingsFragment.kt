package com.amanoteam.moelistlibre.ui.more

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.amanoteam.moelistlibre.R
import com.amanoteam.moelistlibre.ui.main.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showToolbar(false)
        (activity as? MainActivity)?.showBottomBar(false)
        (parentFragment as? MoreFragment)?.showToolbar()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

    }
}