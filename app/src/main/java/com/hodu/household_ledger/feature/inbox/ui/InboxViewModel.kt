package com.hodu.household_ledger.feature.inbox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            try {
                _candidates.value = candidateRepo.getCandidates()
            } catch (_: Exception) {}
            _isLoading.value = false
        }
    }

    fun confirmCandidate(id: Long, categoryId: Long) {
        viewModelScope.launch {
            try {
                candidateRepo.confirmCandidate(id, categoryId)
                _candidates.value = _candidates.value.filter { it.id != id }
            } catch (_: Exception) {}
        }
    }

    fun deleteCandidate(id: Long) {
        viewModelScope.launch {
            try {
                candidateRepo.deleteCandidate(id)
                _candidates.value = _candidates.value.filter { it.id != id }
            } catch (_: Exception) {}
        }
    }
}
