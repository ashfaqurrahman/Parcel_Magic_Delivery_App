package com.airposted.bitoronbd_deliveryman.ui.main

import androidx.fragment.app.Fragment

interface CommunicatorFragmentInterface {
    fun addContentFragment(fragment: Fragment?, addToBackStack: Boolean)
}