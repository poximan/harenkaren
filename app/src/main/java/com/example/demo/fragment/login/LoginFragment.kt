package com.example.demo.fragment.login

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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demo.R
import com.example.demo.databinding.FragmentLoginBinding
import com.example.demo.viewModel.UsuarioViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment(), UsuarioCallback {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var usuarioViewModel: UsuarioViewModel
    private var visible: Boolean = false

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

        binding.loginButton.setOnClickListener { checkLogin() }
        binding.cancelButton.setOnClickListener { goBack() }
        binding.huella.setOnClickListener { checkLoginHuella(view) }

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

    private fun checkLoginHuella(view: View) {
        val biometricLoginManager = BiometricLoginManager(requireContext())

        biometricLoginManager.authenticate(object :
            BiometricLoginManager.BiometricAuthenticationCallback {
            override fun onAuthenticationSuccess() {
                onLoginSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
                snack(view, "No se encontro hardware detector de huella")
            }

            override fun onAuthenticationFailed() {
                snack(view, "No se pudo verificar usuario")
            }
        })
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
            showErrorMsg("Complete los campos")
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                // this@LoginFragment almacena la referencia antes de cambiar de contexto
                usuarioViewModel.loginConEmailPass(email, password, this@LoginFragment)
            }
        }
    }

    override fun onLoginSuccess() {
        CoroutineScope(Dispatchers.Main).launch {
            findNavController().navigate(R.id.goToHomeFragment)
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

            var fadeOutAnimationObject = AlphaAnimation(1f, 0f)
            fadeOutAnimationObject.duration = 3000
            Handler(Looper.getMainLooper()).postDelayed({
                _binding?.loginFailedTextView?.startAnimation(fadeOutAnimationObject)
                _binding?.loginFailedTextView?.visibility = View.INVISIBLE
            }, 1000)
        }
    }

    private fun snack(view: View, text: String) {
        val view: View = view.findViewById(R.id.vista_login)
        val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}