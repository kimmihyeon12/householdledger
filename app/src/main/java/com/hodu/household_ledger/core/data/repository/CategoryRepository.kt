package com.hodu.household_ledger.core.data.repository

import com.hodu.household_ledger.core.data.mapper.CategoryMapper
import com.hodu.household_ledger.core.domain.model.Category
import com.hodu.household_ledger.core.network.ApiClient

class CategoryRepository {

    private val api = ApiClient.categoryApi

    suspend fun getCategories(): List<Category> {
        return CategoryMapper.toDomainList(api.getCategories())
    }

    suspend fun getCategory(id: Long): Category {
        return CategoryMapper.toDomain(api.getCategory(id))
    }
}
