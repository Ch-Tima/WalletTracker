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
import com.chtima.wallettracker.viewModels.TransactionReportViewModel
import java.util.Date


/**
 * Fragment для вывода спика транзакций с возможностью влияния на выводимую информацию через @
 *
 * **/
class DisplayTransactionListFragment : Fragment() {

    //ViewModels
    private lateinit var categoryVM: CategoryViewModel
    private lateinit var transactionReportVM : TransactionReportViewModel

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
        transactionReportVM = ViewModelProvider(requireActivity())[TransactionReportViewModel::class]
        categoryVM = ViewModelProvider(this)[CategoryViewModel::class]
        categoryVM.getCategoriesWithTransactionsByUser().observe(viewLifecycleOwner){
            categoryWithTransactions = it.toList()
            f = TransactionFilter(categoryWithTransactions) { l ->
                adapter.updateList(toTransactionList(l))
            }
            f.apply()
            filter()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DisplayTransactionListFragment().apply {}
    }

    fun filter(){
        val filterParams = this.transactionReportVM.filterParams.value ?: return
        f.clear().apply()
        filterParams.let {
            filterParams.getListOfCategory().isNotEmpty().let { f.byCategory(filterParams.getListOfCategory()) }
            filterParams.getTransactionType()?.let { t -> f.byType(t) }
            if(filterParams.getDateStart()!=null && filterParams.getDateEnd()!=null)
                f.byDate(filterParams.getDateStart()!!, filterParams.getDateEnd()!!)
            f.apply()
        }?:run {
            f.clear().apply()
        }
    }
    fun getFilter() = f

    class TransactionFilter constructor(
        private val categoryWithTransactions: List<CategoryWithTransactions>,
        private val callback: (List<CategoryWithTransactions>) -> Unit){

        private var filtered: List<CategoryWithTransactions> = categoryWithTransactions.toList()

        fun byText(text: String): TransactionFilter {
            filtered = filtered.map { cwt ->
                cwt.copy(transactions = cwt.transactions.filter {
                    val title = it.title.lowercase()
                    val note = it.note.orEmpty().take(50).lowercase()
                    val words = ("$title $note").split("\\s+".toRegex())
                    words.any { word -> word.contains(text.lowercase()) }
                })
            }
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
        fun byCategory(list: List<Category>): TransactionFilter{
            if(list.isNotEmpty())
                filtered = filtered.filter { list.any{ l -> l.id == it.category.id } }
            return this
        }
        fun byDate(dS: Date, dE: Date): TransactionFilter{
            filtered = filtered.map { cwt ->
                cwt.copy(transactions = cwt.transactions.filter {
                    it.dateTime.after(dS) && it.dateTime.before(dE)
                })
            }
            return this
        }
        fun byType(type: Transaction.TransactionType): TransactionFilter{
            filtered = filtered.map { cwt ->
                cwt.copy(transactions = cwt.transactions.filter {
                    it.type == type
                })
            }
            return this
        }
        fun clear(): TransactionFilter{
            filtered = categoryWithTransactions
            return this
        }
        fun apply(){
            filtered = filtered.filter { it.transactions.isNotEmpty() }
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


}