package com.yulius.warasapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yulius.warasapp.data.model.UserPreference
import com.yulius.warasapp.ui.articles.ArticlesViewModel
import com.yulius.warasapp.ui.auth.login.LoginViewModel
import com.yulius.warasapp.ui.auth.register.RegisterViewModel
import com.yulius.warasapp.ui.diagnose.DiagnoseViewModel
import com.yulius.warasapp.ui.home.HomeViewModel
import com.yulius.warasapp.ui.main.MainViewModel
import com.yulius.warasapp.ui.profile.ProfileViewModel
import com.yulius.warasapp.ui.profile.change_password.ChangePasswordViewModel
import com.yulius.warasapp.ui.profile.contact_us.ContactUsViewModel

class ViewModelFactory(private val pref: UserPreference) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(pref) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(pref) as T
            }

            modelClass.isAssignableFrom(DiagnoseViewModel::class.java) -> {
                DiagnoseViewModel(pref) as T
            }

            modelClass.isAssignableFrom(ArticlesViewModel::class.java) -> {
                ArticlesViewModel(pref) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(pref) as T
            }

            modelClass.isAssignableFrom(ContactUsViewModel::class.java) -> {
                ContactUsViewModel(pref) as T
            }

            modelClass.isAssignableFrom(ChangePasswordViewModel::class.java) -> {
                ChangePasswordViewModel(pref) as T
            }


            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }


    }