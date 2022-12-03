package com.example.presentation.ui.view_models

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ApplicationViewModel  @Inject constructor(
    val application: Application
) : ViewModel()
