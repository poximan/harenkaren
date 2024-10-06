package phocidae.mirounga.leonina.fragment.reporte

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Base64
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import phocidae.mirounga.leonina.R
import phocidae.mirounga.leonina.activity.HomeActivity
import phocidae.mirounga.leonina.dao.UnSocDAO
import phocidae.mirounga.leonina.database.HarenKarenRoomDatabase
import phocidae.mirounga.leonina.databinding.FragmentReportesBinding
import phocidae.mirounga.leonina.fragment.maps.SuperMapa
import phocidae.mirounga.leonina.model.UnidSocial
import java.io.ByteArrayOutputStream
import java.util.UUID

class ReportesFragment : Fragment(), OnImageCapturedListener {

    object DbConstants {
        const val PERMISSION_REQUEST_PICK_IMAGE1 = 4
        const val PERMISSION_REQUEST_PICK_IMAGE2 = 5
    }

    private lateinit var unSocDAO: UnSocDAO
    private var selectedRadioButton: RadioButton? = null
    private lateinit var mapota: SuperMapa

    private var _binding: FragmentReportesBinding? = null
    private val binding get() = _binding!!
    private val args: ReportesFragmentArgs by navArgs()

    private lateinit var scrollView: ScrollView
    private lateinit var webViewHeat: WebView

    private lateinit var logo2: ImageView

    private lateinit var webViewTorta: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        scrollView = binding.scrollReporte

        webViewHeat = binding.webViewRep
        webViewHeat.settings.javaScriptEnabled = true
        webViewHeat.addJavascriptInterface(JavaScriptInterface(this, requireActivity()), "Android")
        mapota = ReporteMapa(webViewHeat, requireContext())

        logo2 = binding.logo2

        binding.rangoFechas.text = ": " + args.rangoFechas

        unSocDAO = HarenKarenRoomDatabase
            .getDatabase(requireContext(), viewLifecycleOwner.lifecycleScope)
            .unSocDao()

        webViewTorta = binding.webViewTorta
        webViewTorta.settings.javaScriptEnabled = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Controla el scroll del ScrollView
        webViewHeat.setOnTouchListener { _, _ ->
            // Deshabilita el scroll del ScrollView mientras se está interactuando con el WebView
            scrollView.requestDisallowInterceptTouchEvent(true)
            false
        }

        scrollView.setOnTouchListener { _, _ ->
            // Habilita el scroll del ScrollView cuando no se está interactuando con el WebView
            scrollView.requestDisallowInterceptTouchEvent(false)
            false
        }

        binding.imageViewSelector.setOnClickListener { imgMembrete() }
        logo2.setOnClickListener { imgPortada() }
        binding.imprimirPdf.setOnClickListener { printPdf() }

        binding.fijarCuadro.setOnClickListener {
            activity?.runOnUiThread {
                webViewHeat.evaluateJavascript("fijar()", null)
            }
        }

        getInvolucrados(args.rangoFechas) {
            if (it.isNotEmpty()) {
                cambiarMenuLateral(it)
                mapota.resolverVisibilidad(it, selectedRadioButton!!.text.toString())
                contarCategoria(it)
                tabFilaHaren(it)
                tabFilaGpoHaren(it)
                tabFilaHarenSin(it)
                tabFilaPjaSolit(it)
                tabFilaIndivSolo(it)
                val reduccion = reducir(it)
                graficar(reduccion)
            } else {
                val mensaje = getString(R.string.rep_rangoVacio) + " " + args.rangoFechas
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navigationView: NavigationView = (activity as HomeActivity).navigationView
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.nav_drawer_menu)
        _binding = null
    }

    override fun onImageCaptured(bitmap: Bitmap) {

        // Convierte el Bitmap a un string Base64
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP)

        val htmlData = """
            <html>
            <body style="margin: 0; padding: 0;">
                <img src="data:image/png;base64,$encodedImage" style="width: 100%; height: 100%; object-fit: contain;" />
            </body>
            </html>
        """
        val layoutParams = webViewHeat.layoutParams

        layoutParams.height = 900
        webViewHeat.layoutParams = layoutParams

        webViewHeat.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_categorias, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_categorias -> {
                (activity as? HomeActivity)?.drawerLayout?.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cambiarMenuLateral(unSocList: List<UnidSocial>) {

        val navigationView: NavigationView = (activity as HomeActivity).navigationView
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.nav_map_categorias)

        val categorias = outerJoinCategorias(unSocList)

        for (i in categorias.indices) {
            val categoria = categorias[i]

            // Crear un nuevo MenuItem
            val menuItem = navigationView.menu.add(Menu.NONE, i, Menu.NONE, null)

            // Crear un RadioButton para este MenuItem
            val radioButton = layoutInflater.inflate(R.layout.item_categorias, null) as RadioButton
            radioButton.text = categoria

            // Escuchar clics en el RadioButton
            radioButton.setOnClickListener {
                selectedRadioButton?.isChecked = false
                selectedRadioButton = radioButton
                radioButton.isChecked = true
                mapota.resolverVisibilidad(unSocList, selectedRadioButton!!.text.toString())
                contarCategoria(unSocList)
            }
            // Asignar el RadioButton como actionView del MenuItem
            menuItem.actionView = radioButton
            if (i == 0) {
                radioButton.isChecked = true
                selectedRadioButton = radioButton
            }
        }

        // Notificar cambios en el menú para que se actualice
        navigationView.invalidate()
    }

    /**
     * de todos los contadores de categorias que tiene una unidad social, frecuentemente es necesario
     * operar sobre aquellos que no sean nulos. es decir si una unidad social tuviera los contadores
     * hembras=0, crias=1, machoPeriferico=3, interesara la lista [crias,machoPeriferico]
     * ahora bien, dada una lista esto se complejiza porque cada unidad social tendra su propio set
     * de categorias no nulas. haciendo una analogia con SQL, esta funcion realiza un OUTER JOIN de
     * categorias sobre todos las unidades sociales de una lista.
     *
     * @return la lista mas abarcativa de categorias no nulas (para al menos un caso)
     */
    private fun outerJoinCategorias(unSocList: List<UnidSocial>): List<String> {
        val combinedContadores = mutableSetOf<String>()
        for (unidSocial in unSocList) {
            combinedContadores.addAll(unidSocial.getContadoresNoNulos())
        }
        return combinedContadores.toList()
    }

    private fun contarCategoria(unSocList: List<UnidSocial>) {

        val atribString = selectedRadioButton!!.text.toString()

        val contCategoria = unSocList.sumOf { unSoc ->
            val field = unSoc::class.java.getDeclaredField(atribString)
            field.isAccessible = true
            field.get(unSoc) as Int
        }

        binding.contCategoria.text =
            requireContext().getString(R.string.rep_contCateg, atribString) + ": $contCategoria"
    }

    private fun tabFilaHaren(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_haren)
        }
        binding.tablePart.totHaren.text = total.toString()

        val promedio = unSocList.filter {
            it.ctxSocial == getString(R.string.ctx_haren)
        }.sumOf {
            it.vHembrasAd
        }
        binding.tablePart.promHaren.text = if (total != 0) {
            (promedio / total).toString()
        } else {
            "-"
        }
    }

    private fun tabFilaGpoHaren(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_gpoHarenes)
        }
        binding.tablePart.totGpoharen.text = total.toString()

        val promedio = unSocList.filter {
            it.ctxSocial == getString(R.string.ctx_gpoHarenes)
        }.sumOf {
            it.vHembrasAd
        }
        binding.tablePart.promGpoharen.text = if (total != 0) {
            (promedio / total).toString()
        } else {
            "-"
        }
    }

    private fun tabFilaHarenSin(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_harenSAlfa)
        }
        binding.tablePart.totHarensin.text = total.toString()

        val promedio = unSocList.filter {
            it.ctxSocial == getString(R.string.ctx_harenSAlfa)
        }.sumOf {
            it.vHembrasAd
        }
        binding.tablePart.promHarensin.text = if (total != 0) {
            (promedio / total).toString()
        } else {
            "-"
        }
    }

    private fun tabFilaPjaSolit(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_pjaSolitaria)
        }
        binding.tablePart.totPjasolit.text = total.toString()
    }

    private fun tabFilaIndivSolo(unSocList: List<UnidSocial>) {
        val total = unSocList.count {
            it.ctxSocial == getString(R.string.ctx_indivSolo)
        }
        binding.tablePart.totIndivsolo.text = total.toString()
    }

    private fun reducir(unSocList: List<UnidSocial>): UnidSocial {
        val randomQueTeTiro = UUID.randomUUID()
        return unSocList.reduce { acc, unidSocial ->
            UnidSocial(
                vAlfaS4Ad = acc.vAlfaS4Ad + unidSocial.vAlfaS4Ad,
                vAlfaSams = acc.vAlfaSams + unidSocial.vAlfaSams,
                vHembrasAd = acc.vHembrasAd + unidSocial.vHembrasAd,
                vCrias = acc.vCrias + unidSocial.vCrias,
                vDestetados = acc.vDestetados + unidSocial.vDestetados,
                vJuveniles = acc.vJuveniles + unidSocial.vJuveniles,
                vS4AdPerif = acc.vS4AdPerif + unidSocial.vS4AdPerif,
                vS4AdCerca = acc.vS4AdCerca + unidSocial.vS4AdCerca,
                vS4AdLejos = acc.vS4AdLejos + unidSocial.vS4AdLejos,
                vOtrosSamsPerif = acc.vOtrosSamsPerif + unidSocial.vOtrosSamsPerif,
                vOtrosSamsCerca = acc.vOtrosSamsCerca + unidSocial.vOtrosSamsCerca,
                vOtrosSamsLejos = acc.vOtrosSamsLejos + unidSocial.vOtrosSamsLejos,
                mAlfaS4Ad = acc.mAlfaS4Ad + unidSocial.mAlfaS4Ad,
                mAlfaSams = acc.mAlfaSams + unidSocial.mAlfaSams,
                mHembrasAd = acc.mHembrasAd + unidSocial.mHembrasAd,
                mCrias = acc.mCrias + unidSocial.mCrias,
                mDestetados = acc.mDestetados + unidSocial.mDestetados,
                mJuveniles = acc.mJuveniles + unidSocial.mJuveniles,
                mS4AdPerif = acc.mS4AdPerif + unidSocial.mS4AdPerif,
                mS4AdCerca = acc.mS4AdCerca + unidSocial.mS4AdCerca,
                mS4AdLejos = acc.mS4AdLejos + unidSocial.mS4AdLejos,
                mOtrosSamsPerif = acc.mOtrosSamsPerif + unidSocial.mOtrosSamsPerif,
                mOtrosSamsCerca = acc.mOtrosSamsCerca + unidSocial.mOtrosSamsCerca,
                mOtrosSamsLejos = acc.mOtrosSamsLejos + unidSocial.mOtrosSamsLejos,
                id = randomQueTeTiro, idRecorrido = randomQueTeTiro,
                ptoObsUnSoc = "", ctxSocial = "", tpoSustrato = "", timeStamp = "",
                latitud = 0.0, longitud = 0.0, comentario = ""
            )
        }
    }

    private fun graficar(unidSocial: UnidSocial) {
        val torta = ReporteTorta(webViewTorta)
        torta.mostrarMapaCalor(unidSocial)
    }

    private fun getInvolucrados(rangoFechas: String, callback: (List<UnidSocial>) -> Unit) {
        var unSocList: List<UnidSocial>

        val desde = rangoFechas.split(" ")[0] + " 00:00:00"
        val hasta = rangoFechas.split(" ")[1] + " 23:59:59"

        // ---------> HILO BACKGOUND
        CoroutineScope(Dispatchers.IO).launch {
            unSocList = unSocDAO.getEntreFechas(desde, hasta)

            // ---------> HILO PRINCIPAL
            withContext(Dispatchers.Main) {
                callback(unSocList)
            }
        }
    }

    private fun printPdf() {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = PdfPrintDocumentAdapter(requireContext(), scrollView, args.rangoFechas)
        val jobName =
            "${requireContext().applicationInfo.loadLabel(requireContext().packageManager)} Document"

        printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
    }

    private fun imgMembrete() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(
                Intent.EXTRA_ALLOW_MULTIPLE,
                true
            )  // Esto permite seleccionar múltiples imágenes
        }
        startActivityForResult(
            Intent.createChooser(intent, "Selecciona imágenes"),
            DbConstants.PERMISSION_REQUEST_PICK_IMAGE1
        )
    }

    private fun imgPortada() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(
                Intent.EXTRA_ALLOW_MULTIPLE,
                false
            )  // Esto permite seleccionar múltiples imágenes
        }
        startActivityForResult(
            Intent.createChooser(intent, "Selecciona imágenes"),
            DbConstants.PERMISSION_REQUEST_PICK_IMAGE2
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            when (requestCode) {
                DbConstants.PERMISSION_REQUEST_PICK_IMAGE1 -> {
                    data?.let {
                        if (it.clipData != null) {

                            binding.linearLayoutImages.removeAllViews()

                            for (i in 0 until it.clipData!!.itemCount) {
                                val imageUri = it.clipData!!.getItemAt(i).uri
                                addImageToLinearLayout(imageUri)
                            }
                        } else {
                            it.data?.let { uri -> addImageToLinearLayout(uri) }
                        }
                    }
                }

                DbConstants.PERMISSION_REQUEST_PICK_IMAGE2 -> {
                    data?.data?.let { uri ->
                        val resizedBitmap = getResizedBitmapForLogo(uri)
                        binding.logo2.setImageBitmap(resizedBitmap)
                    }
                }
            }
        }
    }

    private fun addImageToLinearLayout(imageUri: Uri) {
        val maxHeight = 80
        val resizedBitmap = getResizedBitmap(imageUri, maxHeight)

        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                maxHeight
            ).apply {
                marginEnd = 8 // Margen entre imágenes
            }
            adjustViewBounds = true // Mantiene la relación de aspecto
            scaleType = ImageView.ScaleType.FIT_CENTER // Ajuste de la imagen al centro
            setImageBitmap(resizedBitmap)
        }
        binding.linearLayoutImages.addView(imageView)
    }

    private fun getResizedBitmap(imageUri: Uri, maxHeight: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        // Primero, obtenemos las dimensiones del Bitmap original
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        // Calcular el factor de reducción (sample size)
        options.inSampleSize = calculateInSampleSize(options, maxHeight)
        options.inJustDecodeBounds = false

        // Decodificar el Bitmap redimensionado
        val resizedInputStream = requireContext().contentResolver.openInputStream(imageUri)
        val resizedBitmap = BitmapFactory.decodeStream(resizedInputStream, null, options)
        resizedInputStream?.close()

        return resizedBitmap
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, maxHeight: Int): Int {
        val height = options.outHeight
        var inSampleSize = 1

        if (height > maxHeight) {
            val halfHeight = height / 2
            while ((halfHeight / inSampleSize) >= maxHeight) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun getResizedBitmapForLogo(imageUri: Uri): Bitmap? {
        // Abrir el InputStream de la imagen.
        val inputStream = requireContext().contentResolver.openInputStream(imageUri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

        // Corregir la orientación EXIF si es necesario.
        val correctedBitmap = getCorrectedBitmap(originalBitmap, imageUri)

        // Redimensionar la imagen.
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val maxWidth = (screenWidth * 0.7).toInt()

        val aspectRatio = correctedBitmap.width.toFloat() / correctedBitmap.height.toFloat()
        val height = (maxWidth / aspectRatio).toInt()

        return Bitmap.createScaledBitmap(correctedBitmap, maxWidth, height, true)
    }

    private fun getCorrectedBitmap(bitmap: Bitmap, imageUri: Uri): Bitmap {
        val inputStream =
            requireContext().contentResolver.openInputStream(imageUri) ?: return bitmap
        val exif = ExifInterface(inputStream)

        return when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
