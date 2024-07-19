package phocidae.mirounga.leonina.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UnSocShareViewModel : ViewModel() {

    private val _lastSelectedValue = MutableLiveData<String>()
    val lastSelectedValue: LiveData<String>
        get() = _lastSelectedValue

    fun setLastSelectedValue(value: String) {
        _lastSelectedValue.value = value
    }
}