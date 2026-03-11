package com.hodu.household_ledger.feature.inbox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hodu.household_ledger.core.common.AppState
import com.hodu.household_ledger.core.data.repository.CandidateRepository
import com.hodu.household_ledger.core.domain.model.Candidate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InboxViewModel : ViewModel() {

    private val candidateRepo = CandidateRepository()

    private val _candidates = MutableStateFlow<List<Candidate>>(emptyList())
    val candidates: StateFlow<List<Candidate>> = _candidates

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCandidates()
    }

    fun loadCandidates() {
        viewModelScope.launch {
            _isLoading.value = true
            AppState.startLoading()
            try {
                _candidates.value = candidateRepo.getCandidates()
            } catch (e: Exception) {
                AppState.showError(message = "후보 목록을 불러오지 못했습니다", retry = { loadCandidates() })
            }
            _isLoading.value = false
            AppState.stopLoading()
        }
    }

    fun confirmCandidate(id: Long, categoryId: Long) {
        viewModelScope.launch {
            try {
                candidateRepo.confirmCandidate(id, categoryId)
                _candidates.value = _candidates.value.filter { it.id != id }
            } catch (e: Exception) {
                AppState.showError(message = "후보 확정에 실패했습니다")
            }
        }
    }

    fun deleteCandidate(id: Long) {
        viewModelScope.launch {
            try {
                candidateRepo.deleteCandidate(id)
                _candidates.value = _candidates.value.filter { it.id != id }
            } catch (e: Exception) {
                AppState.showError(message = "후보 삭제에 실패했습니다")
            }
        }
    }
}
