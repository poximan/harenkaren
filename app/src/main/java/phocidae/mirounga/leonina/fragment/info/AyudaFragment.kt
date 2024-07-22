package phocidae.mirounga.leonina.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.adapter.ExpandListAdapter
import phocidae.mirounga.leonina.databinding.FragmentAyudaBinding

class AyudaFragment : Fragment() {

    private var _binding: FragmentAyudaBinding? = null
    private val binding get() = _binding!!

    private lateinit var expanList: ExpandableListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAyudaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expanList = binding.expanList
        val expandableListData = initData()

        val expandListAdapter = ExpandListAdapter(requireContext(), expandableListData)
        expanList.setAdapter(expandListAdapter)
        expanList.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            // Lógica cuando se hace clic en un elemento hijo
            true
        }
    }

    private fun initData(): LinkedHashMap<String, List<Pair<String, Int?>>> {
        val expandableListData = LinkedHashMap<String, List<Pair<String, Int?>>>()

        val firstItem = listOf(
            Pair(getString(R.string.ayu_detCenso1, getString(R.string.home_censos)), null),
            Pair(
                getString(R.string.ayu_detalle1, getString(R.string.home_censos)),
                R.drawable.ayucensos_home
            ),
            Pair(getString(R.string.ayu_detCenso3), R.drawable.ayucensos_anio),
            Pair(getString(R.string.ayu_detCenso4), R.drawable.ayucensos_aniolista)
        )
        val secondItem = listOf(
            Pair(getString(R.string.ayu_detRecorr1, getString(R.string.home_mapa)), null),
            Pair(
                getString(R.string.ayu_detalle1, getString(R.string.home_mapa)),
                R.drawable.ayurecorridos_home
            ),
            Pair(getString(R.string.ayu_detRecorr2), R.drawable.ayurecorridos),
            Pair(getString(R.string.ayu_detRecorr3), R.drawable.ayurecorridos_calor)
        )
        val thirdItem = listOf(
            Pair(
                getString(
                    R.string.ayu_detImportExp1,
                    getString(R.string.ayu_importexp),
                    getString(R.string.mnu_importar),
                    getString(R.string.mnu_exportar)
                ), null
            ),
            Pair(getString(R.string.ayu_detImportExp2), R.drawable.ayuimportarexp_home),
            Pair(getString(R.string.ayu_detImportExp3), R.drawable.ayuimportarexp_menu)
        )

        expandableListData[getString(R.string.home_censos)] = firstItem
        expandableListData[getString(R.string.home_mapa)] = secondItem
        expandableListData[getString(R.string.ayu_importexp)] = thirdItem

        return expandableListData
    }
}