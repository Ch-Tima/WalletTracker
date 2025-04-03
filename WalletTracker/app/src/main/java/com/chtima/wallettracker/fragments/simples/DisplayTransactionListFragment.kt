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
import com.chtima.wallettracker.models.Category
import com.chtima.wallettracker.models.CategoryWithTransactions
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.viewModels.CategoryViewModel
import java.util.Date


/**
 * Fragment для вывода спика транзакций с возможностью влияния на выводимую информацию через @
 *
 * **/
class DisplayTransactionListFragment : Fragment() {

    //ViewModels
    private lateinit var categoryVM: CategoryViewModel

    //other
    private lateinit var adapter: TransactionAdapter
    private var categoryWithTransactions: List<CategoryWithTransactions> = emptyList()
    private lateinit var f: TransactionFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_display_transaction_list, container, false)

        // адаптер для визуализации
        adapter = TransactionAdapter(requireContext(), toTransactionList().toMutableList())
        val recyclerView: RecyclerView = v.findViewById(R.id.list_transaction)//need only one time
        recyclerView.adapter = adapter

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //содания VM и зпрос данных
        categoryVM = ViewModelProvider(this)[CategoryViewModel::class]
        categoryVM.getCategoriesWithTransactionsByUser().observe(viewLifecycleOwner){
            categoryWithTransactions = it.toList()
            f = TransactionFilter(categoryWithTransactions, {
                adapter.updateList(toTransactionList(it))
            })
            //f.byTitle("c").apply()//TEST
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DisplayTransactionListFragment().apply {}
    }

    public fun filter() = f

    class TransactionFilter (private val categoryWithTransactions: List<CategoryWithTransactions>,
               private val callback: (List<CategoryWithTransactions>) -> Unit){

        private var filtered: List<CategoryWithTransactions> = categoryWithTransactions.toList()

        fun byTitle(text: String): TransactionFilter {
            filtered = filtered.map { cwt ->
                cwt.copy(
                    transactions = cwt.transactions.filter { it.title.contains(text, ignoreCase = true) }
                )
            }.filter { it.transactions.isNotEmpty() }
            return this
        }

        fun byNote(note: String): TransactionFilter {
            filtered = filtered.map { categoryWithTransactions ->
                categoryWithTransactions.copy(
                    transactions = categoryWithTransactions.transactions.filter { it.note?.contains(note, ignoreCase = true) == true }
                )
            }.filter { it.transactions.isNotEmpty() }
            return this
        }
        fun bySum(min: Double, max: Double): TransactionFilter {
            filtered = filtered.map { categoryWithTransactions ->
                categoryWithTransactions.copy(
                    transactions = categoryWithTransactions.transactions.filter { it.sum in min..max }
                )
            }.filter { it.transactions.isNotEmpty() }
            return this
        }


        public fun apply(){
            callback(filtered)
        }

    }
    /**
     * @return all Transaction from List&lt;CategoryWithTransactions&gt;
     * */
    private fun toTransactionList():List<Transaction>{
        return toTransactionList(this.categoryWithTransactions)
    }
    private fun toTransactionList(l: List<CategoryWithTransactions>):List<Transaction>{
        return l.flatMap { it.transactions }
    }

    class SortingCriteria{
        var title: String = ""
        var note: String = ""
        var categories: List<Category> = ArrayList()
        var date: Date? = null
        var transactionType : Transaction.TransactionType? = null
    }

}