package com.airposted.bitoronbd_deliveryman.view.main.common

import androidx.fragment.app.Fragment

interface CommunicatorFragmentInterface {
    fun addContentFragment(fragment: Fragment?, addToBackStack: Boolean)
}