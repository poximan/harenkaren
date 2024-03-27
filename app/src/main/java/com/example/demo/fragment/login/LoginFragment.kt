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
        _binding!!.loginButton.setOnClickListener { checkLogin() }
        _binding!!.cancelButton.setOnClickListener { goBack() }

        // visibilidad de la contraseña
        _binding!!.pass.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= _binding!!.pass.right - _binding!!.pass.compoundDrawables
                        .get(DRAWABLE_RIGHT).bounds.width()
                ) {
                    turnVisibility()
                    return@OnTouchListener true
                }
            }
            false
        })

        usuarioViewModel = UsuarioViewModel(requireActivity().application)
        return binding.root
    }

    private fun turnVisibility() {
        if (!visible) {
            _binding!!.pass.transformationMethod = PasswordTransformationMethod.getInstance()
            _binding!!.pass.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.visible_on,
                0
            )
            visible = true
        } else {
            _binding!!.pass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            _binding!!.pass.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.visible_off,
                0
            )
            visible = false
        }
    }

    private fun checkLogin() {

        val email = _binding!!.email.text.toString()
        val password = _binding!!.pass.text.toString()

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
}