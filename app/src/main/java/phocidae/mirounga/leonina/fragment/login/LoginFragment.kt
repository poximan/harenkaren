package phocidae.mirounga.leonina.fragment.login

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.activity.HomeActivity
import phocidae.mirounga.leonina.databinding.FragmentLoginBinding
import phocidae.mirounga.leonina.model.Usuario
import phocidae.mirounga.leonina.viewModel.UsuarioViewModel

class LoginFragment : Fragment(), UsuarioCallback {

    object DbConstants {
        const val PERMISSION_REQUEST_GET_ACCOUNTS = 1
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var usuarioViewModel: UsuarioViewModel
    private var visible: Boolean = false

    private lateinit var viewlonga: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        usuarioViewModel = UsuarioViewModel(requireActivity().application)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewlonga = view

        binding.loginButton.setOnClickListener { checkLogin() }
        binding.cancelButton.setOnClickListener { goBack() }
        binding.huella.setOnClickListener { usarHuella() }

        // visibilidad de la contraseña
        binding.pass.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.pass.right - binding.pass.compoundDrawables
                        .get(DRAWABLE_RIGHT).bounds.width()
                ) {
                    turnVisibility()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun usarHuella() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.GET_ACCOUNTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.GET_ACCOUNTS),
                DbConstants.PERMISSION_REQUEST_GET_ACCOUNTS
            )
        } else {
            checkLoginHuella()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == DbConstants.PERMISSION_REQUEST_GET_ACCOUNTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLoginHuella()
            } else {
                // Permiso denegado, manejar la situación
            }
        }
    }

    private fun checkLoginHuella() {
        val biometricLoginManager = BiometricLoginManager(requireContext())

        biometricLoginManager.authenticate(object :
            BiometricLoginManager.BiometricAuthenticationCallback {
            override fun onAuthenticationSuccess() {
                val account = getPrimaryAccount()
                val usuario = if (account != null) {
                    Usuario(1, account.name, account.name, true)
                } else {
                    null
                }
                onLoginSuccess(usuario)
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
                snack(viewlonga, requireContext().getString(R.string.log_checkHuella1))
            }

            override fun onAuthenticationFailed() {
                snack(viewlonga, requireContext().getString(R.string.log_checkHuella2))
            }
        })
    }

    fun getPrimaryAccount(): Account? {
        val accountManager = AccountManager.get(requireContext())
        val accounts = accountManager.accounts
        // Aquí filtramos por el tipo de cuenta (por ejemplo, cuentas de Google)
        return accounts.firstOrNull { it.type == "com.google" }
    }

    private fun turnVisibility() {
        if (!visible) {
            binding.pass.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.pass.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.visible_on,
                0
            )
            visible = true
        } else {
            binding.pass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.pass.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.visible_off,
                0
            )
            visible = false
        }
    }

    private fun checkLogin() {
        val email = binding.email.text.toString()
        val password = binding.pass.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showErrorMsg(requireContext().getString(R.string.log_checkLogin))
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                // this@LoginFragment almacena la referencia antes de cambiar de contexto
                usuarioViewModel.loginConEmailPass(email, password, this@LoginFragment)
            }
        }
    }

    override fun onLoginSuccess(usuario: Usuario?) {
        CoroutineScope(Dispatchers.Main).launch {
            val intent = Intent(activity, HomeActivity::class.java).apply {
                putExtra("usuario", usuario)
            }
            startActivity(intent)
        }
    }

    override fun onLoginFailure(errorMessage: String) {
        showErrorMsg(errorMessage)
    }

    private fun goBack() {
        findNavController().navigate(R.id.main_fragment)
    }

    private fun showErrorMsg(message: String) {

        CoroutineScope(Dispatchers.Main).launch {
            binding.loginFailedTextView.text = message
            binding.loginFailedTextView.visibility = View.VISIBLE

            val fadeOutAnimationObject = AlphaAnimation(1f, 0f)
            fadeOutAnimationObject.duration = 5000
            Handler(Looper.getMainLooper()).postDelayed({
                _binding?.loginFailedTextView?.startAnimation(fadeOutAnimationObject)
                _binding?.loginFailedTextView?.visibility = View.INVISIBLE
            }, 1000)
        }
    }

    private fun snack(view: View, text: String) {
        val viewLocal: View = view.findViewById(R.id.vista_login)
        val snackbar = Snackbar.make(viewLocal, text, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}