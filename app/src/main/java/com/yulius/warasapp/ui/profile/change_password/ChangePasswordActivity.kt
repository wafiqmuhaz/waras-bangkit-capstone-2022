package com.yulius.warasapp.ui.profile.change_password

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.yulius.warasapp.R
import com.yulius.warasapp.data.model.User
import com.yulius.warasapp.data.model.UserPreference
import com.yulius.warasapp.databinding.ActivityChangePasswordBinding
import com.yulius.warasapp.ui.auth.login.LoginActivity
import com.yulius.warasapp.ui.main.MainActivity
import com.yulius.warasapp.util.ResponseCallback
import com.yulius.warasapp.util.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChangePasswordBinding
    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[ChangePasswordViewModel::class.java]

        viewModel.getUser().observe(this){
            if (!it.isLogin){
                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            } else {
                user = it
            }
        }
    }

    private fun setupAction() {
        showLoading()
        binding.apply {
            ivAvatar.setOnClickListener{
                onBackPressed()
            }

            btnSubmit.setOnClickListener {
                var isError = false
                if(TextUtils.isEmpty(etCurrPass.text)){
                    isError = true
                    etCurrPass.error = getString(R.string.enter_current_pass)
                }
                if(TextUtils.isEmpty(etNewPass.text)){
                    isError = true
                    etCurrPass.error = getString(R.string.enter_new_pass)
                }
                if(TextUtils.isEmpty(etNewPass.text)){
                    isError = true
                    etCurrPass.error = getString(R.string.enter_repeat_pass)
                }

                if(etCurrPass.text.toString() == etNewPass.text.toString()){
                    isError = true
                    etNewPass.error = getString(R.string.check_new_password)
                } else if(etRepeatPass.text.toString() != etNewPass.text.toString()){
                    isError = true
                    etRepeatPass.error = getString(R.string.valid_equal_password)
                }

                if(!isError){
                    viewModel.checkPassword(user.username, etCurrPass.text.toString(),object : ResponseCallback{
                        override fun getCallback(msg: String, status: Boolean) {
                            if (status){
                                viewModel.changePassword(user,etNewPass.text.toString(),object: ResponseCallback{
                                    override fun getCallback(msg: String, status: Boolean) {
                                        showDialogs(msg,status)
                                    }
                                })
                            } else {
                                showDialogs(msg, status)
                            }
                        }
                    })
                }
            }
        }
    }

    private fun showDialogs(msg: String, status: Boolean) {
        if (status) {
            AlertDialog.Builder(this).apply {
                setTitle("Yay !")
                val message = getString(R.string.change_password_success)
                setMessage(message)
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    val intent = Intent(this@ChangePasswordActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this) {
            binding.apply {
                when {
                    it -> progressBar.visibility = View.VISIBLE
                    else -> progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }
}