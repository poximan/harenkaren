package phocidae.mirounga.leonina.fragment.login

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.FragmentRegisterBinding
import phocidae.mirounga.leonina.model.Usuario
import phocidae.mirounga.leonina.viewModel.UsuarioViewModel

class RegisterFragment : Fragment(), UsuarioCallback {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var usuarioViewModel: UsuarioViewModel
    private var visible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        _binding!!.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.main_fragment)
        }
        _binding!!.registerButton.setOnClickListener { checkSamePassword() }

        _binding!!.editTextPassword.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= _binding!!.editTextPassword.right - _binding!!.editTextPassword.compoundDrawables
                        .get(DRAWABLE_RIGHT).bounds.width()
                ) {
                    turnVisibility()
                    return@OnTouchListener true
                }
            }
            false
        })

        _binding!!.editTextPassword2.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= _binding!!.editTextPassword2.right - _binding!!.editTextPassword2.compoundDrawables
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

    private fun checkSamePassword() {
        val pass1 = _binding!!.editTextPassword.text.toString()
        val pass2 = _binding!!.editTextPassword2.text.toString()
        if (pass1 != pass2) {
            showErrorMsg()
        } else {
            createNewAccount()
        }
    }

    private fun showErrorMsg() {
        _binding?.loginFailedTextView?.visibility = View.VISIBLE

        var fadeOutAnimationObject = AlphaAnimation(1f, 0f)
        fadeOutAnimationObject.duration = 2000
        Handler(Looper.getMainLooper()).postDelayed({
            _binding?.loginFailedTextView?.startAnimation(fadeOutAnimationObject)
            _binding?.loginFailedTextView?.visibility = View.INVISIBLE
        }, 1000)

    }

    private fun turnVisibility() {
        if (!visible) {
            _binding!!.editTextPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            _binding!!.editTextPassword2.transformationMethod =
                PasswordTransformationMethod.getInstance()

            _binding!!.editTextPassword.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.visible_on,
                0
            )
            _binding!!.editTextPassword2.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.visible_on,
                0
            )
            visible = true
        } else {
            _binding!!.editTextPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            _binding!!.editTextPassword2.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

            _binding!!.editTextPassword.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.visible_off,
                0
            )
            _binding!!.editTextPassword2.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.visible_off,
                0
            )
            visible = false
        }
    }

    private fun createNewAccount() {

        val email = _binding!!.editTextEmail.text.toString()
        val password = _binding!!.editTextPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                // this@LoginFragment almacena la referencia antes de cambiar de contexto
                usuarioViewModel.crearConEmailPass(email, password, this@RegisterFragment)
            }
        } else {
            val context = requireContext()
            Toast.makeText(context, context.getString(R.string.reg_newAccount), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onLoginSuccess(usuario: Usuario?) {
        CoroutineScope(Dispatchers.Main).launch {
            val context = requireContext()
            Toast.makeText(
                context,
                context.getString(R.string.reg_loginSuccess),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.main_fragment)
        }
    }

    override fun onLoginFailure(errorMessage: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val context = requireContext()
            Toast.makeText(
                context,
                context.getString(R.string.reg_loginFailure),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}







