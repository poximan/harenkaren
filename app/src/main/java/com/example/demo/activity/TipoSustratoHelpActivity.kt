package com.example.demo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.demo.Coordinadora
import com.example.demo.R
import com.example.demo.fragment.info.TipoSustratoHelpDescripFragment

class TipoSustratoHelpActivity : AppCompatActivity(), Coordinadora {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_sustrato_info)
    }

    override fun onChangeOpciones(index: Int) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_tpoSustratoHelpDescrip) as TipoSustratoHelpDescripFragment
        fragment.change(index)
    }
}