package com.airposted.bitoronbd_deliveryman.view.auth

import androidx.fragment.app.Fragment

interface AuthCommunicatorFragmentInterface {
    fun addContentFragment(fragment: Fragment?, addToBackStack: Boolean)
}