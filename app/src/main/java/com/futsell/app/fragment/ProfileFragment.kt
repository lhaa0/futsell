package com.futsell.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.futsell.app.R
import com.futsell.app.util.PrefsHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.profil_act.*

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profil_act, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity!!.finish()
        }
        txtName.text = PrefsHelper(activity!!).getPref(PrefsHelper.FULLNAME)
        txtEmail.text = PrefsHelper(activity!!).getPref(PrefsHelper.EMAIL)
    }
}