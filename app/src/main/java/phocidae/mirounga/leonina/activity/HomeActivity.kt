package phocidae.mirounga.leonina.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    private val mapaParesOrigenDestino: MutableMap<Int, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.resolver = applicationContext.contentResolver

        binding = ActivityHomeBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostHome) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.navView, navController)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        setContentView(binding.root)

        definicionBotonesBack()
    }

    private fun definicionBotonesBack() {
        agregarParOrigenDestino(R.id.censo_fragment, R.id.home_fragment)
        agregarParOrigenDestino(R.id.dia_list_fragment, R.id.censo_fragment)
        agregarParOrigenDestino(R.id.recorr_list_fragment, R.id.dia_list_fragment)
        agregarParOrigenDestino(R.id.unsoc_list_fragment, R.id.recorr_detail_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {

        val navController = this.findNavController(R.id.navHostHome)
        var fragActual = navController.currentDestination?.id

        /*
         Verifica si el fragmento actual tiene un destino particular
         si no lo tiene, sale por else y ejecuta comportamiento default
         */
        val fragDestino = mapaParesOrigenDestino[fragActual]

        return if (fragDestino != null) {
            // Desapila hasta que alcances el fragmento de destino
            while (fragActual != fragDestino) {
                navController.popBackStack()

                val nuevoFragActual = navController.currentDestination?.id
                if (nuevoFragActual == fragActual) break // Salir si no se ha movido a un nuevo fragmento

                fragActual = nuevoFragActual
            }
            true
        } else {
            NavigationUI.navigateUp(navController, drawerLayout)
        }
    }

    private fun agregarParOrigenDestino(idOrigen: Int, idDestino: Int) {
        mapaParesOrigenDestino[idOrigen] = idDestino
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.navHostHome)
        val currentFragment = navController.currentDestination?.id

        // Verifica si estás en el fragmento inicial de activity_main.xml
        if (currentFragment == R.id.home_fragment) {
            super.onBackPressed()
            return
        } else {
            onSupportNavigateUp()
        }
    }
}