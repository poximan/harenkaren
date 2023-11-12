package com.example.demo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demo.Cordinadora
import com.example.demo.R
import com.example.demo.fragment.info.CtxSocialHelpDescripFragment

class CtxSocialHelpActivity : AppCompatActivity(), Cordinadora {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ctx_social_info)
    }

    override fun onChangeOpciones(index: Int) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_ctxSocialHelpDescrip) as CtxSocialHelpDescripFragment
        fragment.changeFishingType(index)
    }
}