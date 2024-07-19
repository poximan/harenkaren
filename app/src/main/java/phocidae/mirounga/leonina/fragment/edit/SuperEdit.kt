package phocidae.mirounga.leonina.fragment.edit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import phocidae.mirounga.leonina.model.UnidSocial
import phocidae.mirounga.leonina.model.UnidadSociable
import kotlin.reflect.KFunction2

abstract class SuperEdit : Fragment(), UnidadSociable {

    protected lateinit var colectar: (Int, Map<String, Any>) -> Unit
    protected val map: MutableMap<String, Any> = mutableMapOf()

    protected lateinit var unSocEditable: UnidSocial

    fun editInstance(
        colectarFunc: KFunction2<Int, Map<String, Any>, Unit>,
        unSoc: UnidSocial
    ): UnidadSociable {
        colectar = colectarFunc
        unSocEditable = unSoc
        return this
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        try {
            outState.putParcelable("unSocEditable", unSocEditable)
        } catch (e: UninitializedPropertyAccessException) {
            Log.i(
                "estadoRotacion",
                "falso positivo para UninitializedPropertyAccessException en ${toString()}." +
                        " por rotacion de pantalla + criterio de anticipacion TabLayout/ViewPager que pretende salvar datos" +
                        " antes que entre en RUN el fragmento contenedor"
            )
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            try {
                unSocEditable = it.getParcelable("unSocEditable")!!
            } catch (e: NullPointerException) {
                Log.i(
                    "estadoRotacion", "falso positivo para NullPointerException en ${toString()}." +
                            " por rotacion de pantalla + criterio de anticipacion TabLayout/ViewPager que pretende recuperar datos" +
                            " antes que entre en RUN el fragmento contenedor"
                )
            }
        }
    }
}
