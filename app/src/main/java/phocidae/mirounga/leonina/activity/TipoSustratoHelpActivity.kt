package phocidae.mirounga.leonina.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.fragment.info.Coordinadora
import phocidae.mirounga.leonina.fragment.info.TipoSustratoHelpDescripFragment

class TipoSustratoHelpActivity : AppCompatActivity(), Coordinadora {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tpo_sustrato_info)
    }

    override fun onChangeOpciones(index: Int) {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_tpoSustratoHelpDescrip) as TipoSustratoHelpDescripFragment
        fragment.change(index)
    }
}