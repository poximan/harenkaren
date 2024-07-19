package phocidae.mirounga.leonina.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.fragment.info.Coordinadora
import phocidae.mirounga.leonina.fragment.info.CtxSocialHelpDescripFragment

class CtxSocialHelpActivity : AppCompatActivity(), Coordinadora {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ctx_social_info)
    }

    override fun onChangeOpciones(index: Int) {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_ctxSocialHelpDescrip) as CtxSocialHelpDescripFragment
        fragment.change(index)
    }
}