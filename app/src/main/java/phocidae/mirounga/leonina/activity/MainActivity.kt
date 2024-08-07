package phocidae.mirounga.leonina.activity

import android.content.ContentResolver
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var resolver: ContentResolver
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    private val mapaParesOrigenDestino: MutableMap<Int, Int> = mutableMapOf()

    private var doubleBackParaSalir = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resolver = applicationContext.contentResolver

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        drawerLayout = binding.drawerLayout

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostMain) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.navView, navController)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        setContentView(view)

        agregarParOrigenDestino(R.id.home_fragment, R.id.login_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {

        val navController = this.findNavController(R.id.navHostMain)
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
        val navController = findNavController(R.id.navHostMain)
        val currentFragment = navController.currentDestination?.id

        // Verifica si estás en el fragmento inicial de activity_main.xml
        if (currentFragment == R.id.main_fragment) {
            if (doubleBackParaSalir) {
                super.onBackPressed()
                return
            }
            this.doubleBackParaSalir = true
            Toast.makeText(this, getString(R.string.nav_main_fragment), Toast.LENGTH_SHORT).show()
            handler.postDelayed({ doubleBackParaSalir = false }, 1000)
        } else {
            onSupportNavigateUp()
        }
    }
}