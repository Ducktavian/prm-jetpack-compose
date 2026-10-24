package com.example.prm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// For tests of ViewModels that collect PersonRepository in viewModelScope:
// - Unit tests have no Android main thread, so main-dispatcher work runs immediately instead.
// - Every ViewModel made with create() is cleared after the test. That cancels its
//   viewModelScope, so it stops listening to the shared repository and can't interfere
//   with later tests.
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTestRule : TestWatcher() {
    // Public (with nextKey) only because create() is inline
    val store = ViewModelStore()
    private var count = 0

    override fun starting(description: Description) {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    override fun finished(description: Description) {
        store.clear()
        Dispatchers.resetMain()
    }

    // e.g. rule.create { PersonListViewModel() }
    inline fun <reified VM : ViewModel> create(noinline build: () -> VM): VM {
        val factory = viewModelFactory { initializer { build() } }
        // A new key each time, so a test can create several of the same class
        return ViewModelProvider(store, factory)["viewModel${nextKey()}", VM::class.java]
    }

    fun nextKey(): Int = count++
}
