package dev.michelolvera.shoppingclever.ui.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Listas de compras"
    }
    val text: LiveData<String> = _text
}