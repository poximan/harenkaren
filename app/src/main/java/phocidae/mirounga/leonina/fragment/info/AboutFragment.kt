package phocidae.mirounga.leonina.fragment.info

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.acercade.text =
            Html.fromHtml(getString(R.string.abo_acercade), Html.FROM_HTML_MODE_COMPACT)

        val advertencia =
            getString(R.string.abo_legalTit) + "\n" + getString(R.string.app_name) + " " + getString(
                R.string.abo_legalMsj
            )
        binding.usodatos.text = Html.fromHtml(advertencia, Html.FROM_HTML_MODE_COMPACT)

        binding.build.text = "Build: ${getAppVersion().first}, ver.${getAppVersion().second}"
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.acercade.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            binding.usodatos.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            binding.build.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
    }

    fun getAppVersion(): Pair<String, Long> {
        val context = requireContext()

        val packageManager: PackageManager = context.packageManager
        val packageName: String = context.packageName
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)

        val versionName: String = packageInfo.versionName
        val versionCode: Long = packageInfo.longVersionCode

        return Pair(versionName, versionCode)
    }
}