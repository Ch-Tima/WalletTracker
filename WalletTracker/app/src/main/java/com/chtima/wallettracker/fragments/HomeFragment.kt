package com.chtima.wallettracker.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.chtima.wallettracker.R
import com.chtima.wallettracker.adapters.TransactionAdapter
import com.chtima.wallettracker.components.SwitchTransactionView
import com.chtima.wallettracker.fragments.dialogs.AddTransactionDialogFragment
import com.chtima.wallettracker.models.CategoryWithTransactions
import com.chtima.wallettracker.models.DialogObserver
import com.chtima.wallettracker.models.Transaction
import com.chtima.wallettracker.models.Transaction.TransactionType
import com.chtima.wallettracker.utils.CurrencyUtils
import com.chtima.wallettracker.viewModels.CategoryViewModel
import com.chtima.wallettracker.viewModels.TransactionViewModel
import com.chtima.wallettracker.viewModels.UserViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.AbstractMap
import java.util.Calendar
import java.util.Date;
import java.util.Locale

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;
import com.chtima.wallettracker.db.AppDatabase
import com.chtima.wallettracker.models.User


class HomeFragment private constructor(): Fragment() {

    //ViewModels
    private lateinit var userVM: UserViewModel
    private lateinit var categoryVM: CategoryViewModel
    private lateinit var transactionVM: TransactionViewModel

    private lateinit var user: User

    //UI
    private lateinit var btnBalance: Button
    private lateinit var sliderChartFragment: SliderChartFragment;

    private var transactionType : TransactionType = TransactionType.EXPENSE;
    private val categoryWithTransactions: MutableList<CategoryWithTransactions> = ArrayList()
    private lateinit var transactionAdapter: TransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val v : View = inflater.inflate(R.layout.fragment_home, container, false)

        Log.d("SimpleDateFormatNOW", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(System.currentTimeMillis())))
        Log.d("SimpleDateFormatNOW-LONG", System.currentTimeMillis().toString())


        //UI
        v.findViewById<ImageButton>(R.id.btn_add).setOnClickListener{ _ ->
            val dialog = AddTransactionDialogFragment.newInstance()
            dialog.addDialogObserver(object : DialogObserver<Transaction>{
                @SuppressLint("CheckResult")
                override fun onSuccess(result: Transaction) {
                    transactionVM.insert(result)
                        .subscribe({ id ->
                            result.id = id;
                            if (TransactionType.EXPENSE == result.type)
                                user.deductFromBalance(result.sum)
                            else
                                user.addToBalance(result.sum)

                            userVM.update(user).subscribe({
                                Log.i("WW-INFO-C", "OK")
                            }, {
                                e -> Log.e("WW-INFO-E", e.toString())
                            })

                        }, { er ->
                            val b = AppDatabase.isExist(requireContext())
                            Log.e("ERR", er.toString())
                        })

                }

                override fun onCancel() {}
            })
            dialog.show(childFragmentManager, AddTransactionDialogFragment::class.java.name)
        }

        //recyclerView
        transactionAdapter = TransactionAdapter(requireContext(), toTransactionList().toMutableList())
        val recyclerView: RecyclerView = v.findViewById(R.id.transaction_recycle)
        recyclerView.adapter = transactionAdapter

        sliderChartFragment = SliderChartFragment.newInstance()
        childFragmentManager.beginTransaction()
                .replace(R.id.slide_chart_fragment, sliderChartFragment, SliderChartFragment.Companion::class.java.name)
                .commit()

        //SwitchTransactionView
        val switch = v.findViewById<SwitchTransactionView>(R.id.swicher_transaction_type);
        switch.addSwitchTransactionListener(object : SwitchTransactionView.SwitchTransactionListener{
            override fun onChangedSelection(type: TransactionType) {
                transactionType = type
                filterTransactionWithUpdateUI()
            }
        })

        //buttons
        btnBalance = v.findViewById<Button>(R.id.balance_btn);//set user balance to "balance_btn"

        return v
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userVM = ViewModelProvider(this)[UserViewModel::class]
        categoryVM = ViewModelProvider(this)[CategoryViewModel::class]
        transactionVM = ViewModelProvider(this)[TransactionViewModel::class]

        userVM.getUser().observe(viewLifecycleOwner) { u ->
            user = u
            btnBalance.text = "${user.balance} ${CurrencyUtils.getCurrencyChar(user.currency?:"", requireContext())}"
        }

        //как обновить UI
        categoryVM.getCategoriesWithTransactionsByUser().observe(viewLifecycleOwner){
            categoryWithTransactions.clear()
            categoryWithTransactions.addAll(it)
            filterTransactionWithUpdateUI()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment().apply {}
    }

    //Method to filter transactions based on the transaction type and update the UI
    private fun filterTransactionWithUpdateUI(){
        if(categoryWithTransactions.isEmpty()) return

        val transactionsFilterType = categoryWithTransactions
            .flatMap { it.transactions }
            .filter { it.type == transactionType }
            .sortedByDescending { it.dateTime }

        transactionAdapter.updateList(transactionsFilterType)

        if(sliderChartFragment.isResumed){
            updateSliderChartFragment()
        }else{
            sliderChartFragment.lifecycle.addObserver(object: DefaultLifecycleObserver{
                override fun onResume(owner: LifecycleOwner) {
                    super.onResume(owner)
                    updateSliderChartFragment()
                }
            })
        }

    }

    private fun updateSliderChartFragment(){
        //setPieChart TODAY
        val pieChartToday: MutableList<PieEntry> = ArrayList()

        categoryWithTransactions.map { item ->
            val sum = item.transactions.stream().filter {
                it.type == transactionType && it.dateTime.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate().equals(
                        Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    ) }?.mapToDouble { it.sum }?.sum()
            AbstractMap.SimpleEntry<CategoryWithTransactions, Double>(item, sum)
        }.filter { it.value > 0 }.sortedBy { it.value }.take(4)
            .forEach {
                pieChartToday.add(PieEntry(it.value.toFloat(), it.key.category.title))
            }

        sliderChartFragment.setPieChartToday(pieChartToday)

        //LAST_WEEK
        val pieBarChartLastWeek = ArrayList<BarEntry>()
        getSumTransactionLastWeek().forEach {
            pieBarChartLastWeek.add(BarEntry(LocalDate.parse(it.key).dayOfWeek.value.toFloat(), it.value.toFloat()))
        }
        this.sliderChartFragment.setBarChartLastWeek(pieBarChartLastWeek);

        //setBarChartThisWeek
        val pieBarChartThisWeek = ArrayList<BarEntry>()
        getSumTransactionThisWeek().forEach {
            pieBarChartThisWeek.add(BarEntry(LocalDate.parse(it.key).dayOfWeek.value.toFloat(), it.value.toFloat()))
        }
        this.sliderChartFragment.setBarChartThisWeek(pieBarChartThisWeek);
    }

    /**
     * @return SimpleEntry, where KEY is the date and VALUE is the sum of all completed transactions. Over the past week.
     * */
    private fun getSumTransactionLastWeek(): List<AbstractMap.SimpleEntry<String, Double>>{
        val startLastWeek = Calendar.getInstance();//last week 13 - 19
        startLastWeek.setTime(Date(System.currentTimeMillis()));
        startLastWeek.add(Calendar.DAY_OF_WEEK, -startLastWeek.get(Calendar.DAY_OF_WEEK) + 1);
        startLastWeek.add(Calendar.DAY_OF_MONTH, -7);

        val endLastWeek = startLastWeek.clone() as Calendar
        endLastWeek.add(Calendar.DATE, 7);

        //search for all transactions for the LAST week
        return getSumTransactionForPeriod(startLastWeek.time, endLastWeek.time);
    }

    /**
     * @return SimpleEntry, where KEY is the date and VALUE is the sum of all completed transactions. Over the this week.
     * */
    private fun getSumTransactionThisWeek(): List<AbstractMap.SimpleEntry<String, Double>>{
        val startThisWeek = Calendar.getInstance()
        startThisWeek.setTime(Date(System.currentTimeMillis()))
        startThisWeek.add(Calendar.DAY_OF_WEEK, -startThisWeek.get(Calendar.DAY_OF_WEEK) + 1)

        val endThisWeek = startThisWeek.clone() as Calendar
        endThisWeek.add(Calendar.DATE, 7)

        //search for all transactions for the THIS week
        return getSumTransactionForPeriod(startThisWeek.time, endThisWeek.time);
    }

    /**
     * @param start filtering start date.
     * @param end filtering end date.
     * @return SimpleEntry, where KEY is the date and VALUE is the sum of all completed transactions. For the period specified in the date parameters.
     * */
    private fun getSumTransactionForPeriod(start:Date, end: Date) :  List<AbstractMap.SimpleEntry<String, Double>>{
        return toTransactionList().filter { it.type == transactionType && (it.dateTime.before(end) && it.dateTime.after(start)) }
            .groupBy { it.getDate() }
            .map { AbstractMap.SimpleEntry<String, Double>(it.key, it.value.sumOf { transaction: Transaction -> transaction.sum }) }
    }

    /**
     * @return all Transaction from List&lt;CategoryWithTransactions&gt;
     * @see HomeFragment#categoryWithTransactions */
    private fun toTransactionList():List<Transaction>{
        return categoryWithTransactions.flatMap { it.transactions }

    }

}