package com.example.demo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demo.Cordinadora
import com.example.demo.R
import com.example.demo.fragment.info.PtoObsHelpDescripFragment

class PtoObsHelpActivity : AppCompatActivity(), Cordinadora {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pto_obs_censo)
    }

    override fun onChangeOpciones(index: Int) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_ptoObsHelpDescrip) as PtoObsHelpDescripFragment
        fragment.change(index)
    }
}