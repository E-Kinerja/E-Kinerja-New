package com.arya.e_kinerja.ui.laporan_aktivitas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arya.e_kinerja.data.Repository
import com.arya.e_kinerja.data.Result
import com.arya.e_kinerja.data.local.datastore.SessionDataStore
import com.arya.e_kinerja.data.local.entity.SessionEntity
import com.arya.e_kinerja.data.remote.response.TugasAktivitasResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaporanAktivitasViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val repository: Repository
): ViewModel() {

    private val _sessionEntity = MutableLiveData<SessionEntity>()
    val sessionEntity: LiveData<SessionEntity>
        get() = _sessionEntity

    fun retrieveSession() {
        viewModelScope.launch {
            sessionDataStore.getSession().collect {
                _sessionEntity.value = it
            }
        }
    }

    fun getTugasAktivitas(bulan: String, tahun: String): LiveData<Result<List<TugasAktivitasResponse>>> {
        return repository.getTugasAktivitas(bulan, tahun)
    }
}