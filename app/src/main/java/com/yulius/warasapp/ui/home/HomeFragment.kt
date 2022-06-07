package com.yulius.warasapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.yulius.warasapp.R
import com.yulius.warasapp.adapter.DetailHistoryAdapter
import com.yulius.warasapp.data.model.History
import com.yulius.warasapp.data.model.UserPreference
import com.yulius.warasapp.databinding.FragmentHomeBinding
import com.yulius.warasapp.ui.auth.login.LoginActivity
import com.yulius.warasapp.ui.diagnose.recommendation.RecommendationActivity
import com.yulius.warasapp.ui.main.MainActivity
import com.yulius.warasapp.ui.profile.change_password.ChangePasswordActivity
import com.yulius.warasapp.ui.profile.editprofile.EditProfileActivity
import com.yulius.warasapp.util.*
import java.util.ArrayList
import kotlin.random.Random

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var adapter = DetailHistoryAdapter()
    private lateinit var viewModel: HomeViewModel
    private lateinit var history: History

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.actionBar?.hide()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupViewModel()
        setupAction()
        setupRecycleView()
    }

    private fun setupRecycleView() {
        with(binding) {
            rvRecommendation.layoutManager = LinearLayoutManager(context)
            rvRecommendation.setHasFixedSize(true)
            rvRecommendation.adapter = adapter
        }

        adapter.setData(listRecommendation)
    }

    private fun setupView() {
        history = History(0,"","","","","",0,0)
        binding.ivAvatar.setImageResource(R.drawable.avatar)
    }

    private val listRecommendation: ArrayList<String>
        get() {
            val dataRecommendation = resources.getStringArray(R.array.data_recommendation).toList()
            val listData = ArrayList<String>()
            for (i in 0..2) {
                val random = Random.nextInt(dataRecommendation.size)
                listData.add(dataRecommendation[random])
            }
            return listData
        }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(
                    UserPreference.getInstance(
                        requireContext().dataStore
                    )
                )
            )[HomeViewModel::class.java]

        viewModel.getUser().observe(viewLifecycleOwner){
            if(it.isLogin){
                binding.tvUsername.text = it.full_name
                viewModel.setLastHistory(it.id)
            } else {
                val i = Intent(activity, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
        }

        viewModel.getLastHistory().observe(viewLifecycleOwner){
            Log.d("TAG", "setupViewModel: $it")
            if(it.data != null){
                binding.cvDailyReport.visibility = View.VISIBLE
                viewModel.setData(it.data)
                history = it.data
            }
        }


        viewModel.listReports.observe(viewLifecycleOwner){ listData ->
            for(i in listData.indices){
                if(changeFormatTime(listData[i].created_date) == todayDate()){

                    if(listData[i].daily_report != null){
                        binding.dailyReportText.text = listData[i].daily_report
                        binding.btnAddReport.visibility = View.GONE
                        binding.btnAddReport.isEnabled = false

                    } else {
                        binding.dailyText1.visibility = View.GONE
                        binding.dailyReportText.visibility = View.GONE
                        binding.btnAddReport.setOnClickListener {
                            showReportDialog(history)
                        }
                    }
                    binding.dailyDate.text = changeTimeFormatCreatedAt(listData[i].created_date!!)
                }
            }
        }



        val random = Random.nextInt(800)
        viewModel.setQuote(random)
        viewModel.getQuotes().observe(viewLifecycleOwner){
            if(it.results != null){
                binding.tvQuote.text = it.results[0].quote
                binding.tvQuoteAuthor.text = it.results[0].author
            }
        }
    }

    private fun setupAction() {
        binding.btnCheck.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_diagnose)
        }

        binding.tvAllRecommendation.setOnClickListener {
            startActivity(Intent(context,RecommendationActivity::class.java))
        }

        binding.ivAvatar.setOnClickListener{
            val popupMenu = PopupMenu(context,binding.ivAvatar)
            popupMenu.inflate(R.menu.popup_profile_menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.logout -> {
                        showLogoutDialog()
                    }
                    R.id.edit_profile -> {
                        startActivity(Intent(activity, EditProfileActivity::class.java))
                    }
                    R.id.change_password -> {
                        startActivity(Intent(activity, ChangePasswordActivity::class.java))
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun showLogoutDialog() {
        val builder =
            AlertDialog.Builder(requireContext(), 0).create()
        val view =
            layoutInflater.inflate(R.layout.dialog_logout, null)
        val btnConfirm = view.findViewById<Button>(R.id.btn_confirm)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        builder.setView(view)

        btnConfirm.setOnClickListener {
            viewModel.logout()
            val i = Intent(activity, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
        btnCancel.setOnClickListener {
            builder.dismiss()
        }
        builder.show()
    }

    private fun showReportDialog(historyData: History) {
        val builder =
            AlertDialog.Builder(requireContext(), 0).create()
        val view =
            layoutInflater.inflate(R.layout.dialog_report, null)
        val title = view.findViewById<TextView>(R.id.tv_report_title)
        title.text = getString(R.string.daily_report_title, changeTimeFormat(todayDate()))
        val etReport = view.findViewById<TextInputLayout>(R.id.et_reports)
        val btnConfirm = view.findViewById<Button>(R.id.btn_confirm)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        builder.setView(view)

        btnConfirm.setOnClickListener {
            val report = etReport.editText?.text.toString()
            viewModel.sendDailyReport(report,historyData,object : ResponseCallback {
                override fun getCallback(msg: String, status: Boolean) {
                    builder.dismiss()
                    showDialogs(msg,status,historyData)
                }
            })
        }
        btnCancel.setOnClickListener {
            builder.dismiss()
        }

        builder.show()
    }

    private fun showDialogs(msg: String, status: Boolean, historyData: History) {
        if (status) {
            AlertDialog.Builder(requireContext(),0).apply {
                setTitle("Yay !")
                setMessage(msg)
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("historyData", historyData )
                    startActivity(intent)
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(requireContext(),0).apply {
                setTitle("Oops")
                setMessage(msg)
                setNegativeButton(getString(R.string.repeat)) { dialog, _ ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
    }

}