package com.chtima.wallettracker.fragments.simples

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.adapters.TransactionAdapter
import com.chtima.wallettracker.fragments.HomeFragment
import com.chtima.wallettracker.models.CategoryWithTransactions
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.viewModels.CategoryViewModel

class DisplayTransactionListFragment : Fragment() {

    //ViewModels
    private lateinit var categoryVM: CategoryViewModel

    //other
    private lateinit var adapter: TransactionAdapter
    private val categoryWithTransactions: MutableList<CategoryWithTransactions> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_display_transaction_list, container, false)

        adapter = TransactionAdapter(requireContext(), toTransactionList().toMutableList())
        val recyclerView: RecyclerView = v.findViewById(R.id.list_transaction)//need only one time
        recyclerView.adapter = adapter

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryVM = ViewModelProvider(this)[CategoryViewModel::class]
        categoryVM.getCategoriesWithTransactionsByUser().observe(viewLifecycleOwner){
            categoryWithTransactions.clear()
            categoryWithTransactions.addAll(it)
            adapter.updateList(toTransactionList())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DisplayTransactionListFragment().apply {}
    }

    /**
     * @return all Transaction from List&lt;CategoryWithTransactions&gt;
     * @see HomeFragment#categoryWithTransactions */
    private fun toTransactionList():List<Transaction>{
        return categoryWithTransactions.flatMap { it.transactions }
    }
}