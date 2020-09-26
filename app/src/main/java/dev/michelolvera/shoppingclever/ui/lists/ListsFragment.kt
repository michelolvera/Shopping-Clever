package dev.michelolvera.shoppingclever.ui.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.michelolvera.shoppingclever.R
import dev.michelolvera.shoppingclever.adapter.ListAdapter
import dev.michelolvera.shoppingclever.model.ListModel
import kotlinx.android.synthetic.main.fragment_lists.*

class ListsFragment : Fragment() {

    private lateinit var listsViewModel: ListsViewModel

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listsViewModel = ViewModelProvider(this).get(ListsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lists, container, false)
        val textView: TextView = root.findViewById(R.id.text_title_list)
        listsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(context)
        // TODO: Eliminar listas ejemplo
        viewAdapter = ListAdapter(arrayOf(ListModel("Ejemplo1", 100.00), ListModel("Ejemplo2", 100.00)))

        list_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}