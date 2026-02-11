package com.example.household_ledger.data.mock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.household_ledger.model.*
import com.example.household_ledger.ui.theme.*

object MockData {

    val categories = listOf(
        Category(1, "식비", TransactionType.EXPENSE, Icons.Default.Restaurant, CategoryFood),
        Category(2, "교통", TransactionType.EXPENSE, Icons.Default.DirectionsCar, CategoryTransport),
        Category(3, "쇼핑", TransactionType.EXPENSE, Icons.Default.ShoppingBag, CategoryShopping),
        Category(4, "여가", TransactionType.EXPENSE, Icons.Default.SportsEsports, CategoryEntertainment),
        Category(5, "건강", TransactionType.EXPENSE, Icons.Default.LocalHospital, CategoryHealth),
        Category(6, "교육", TransactionType.EXPENSE, Icons.Default.School, CategoryEducation),
        Category(7, "생활", TransactionType.EXPENSE, Icons.Default.Home, CategoryLiving),
        Category(8, "기타", TransactionType.EXPENSE, Icons.Default.MoreHoriz, CategoryOther),
        Category(9, "급여", TransactionType.INCOME, Icons.Default.AccountBalance, IncomeBlue),
        Category(10, "용돈", TransactionType.INCOME, Icons.Default.CardGiftcard, IncomeBlue)
    )

    private val baseTime = 1738368000000L // 2025-02-01 00:00 KST

    val transactions = listOf(
        // 2월 1일
        Transaction(1, TransactionType.EXPENSE, 12500, "스타벅스 강남점", 1, baseTime + 3600000 * 9, "아메리카노 외", TransactionSource.AUTO),
        Transaction(2, TransactionType.EXPENSE, 35000, "쿠팡", 3, baseTime + 3600000 * 14, "생필품", TransactionSource.AUTO),
        Transaction(3, TransactionType.EXPENSE, 4500, "서울교통공사", 2, baseTime + 3600000 * 8, "", TransactionSource.AUTO),
        // 1월 31일
        Transaction(4, TransactionType.INCOME, 3200000, "주식회사 테크", 9, baseTime - 86400000 * 1 + 3600000 * 10, "1월 급여", TransactionSource.MANUAL),
        // 1월 30일
        Transaction(5, TransactionType.EXPENSE, 89000, "올리브영", 5, baseTime - 86400000 * 2 + 3600000 * 15, "비타민", TransactionSource.AUTO),
        Transaction(6, TransactionType.EXPENSE, 15000, "배달의민족", 1, baseTime - 86400000 * 2 + 3600000 * 19, "치킨", TransactionSource.AUTO),
        // 1월 29일
        Transaction(7, TransactionType.EXPENSE, 52000, "CGV 용산", 4, baseTime - 86400000 * 3 + 3600000 * 18, "영화+팝콘", TransactionSource.MANUAL),
        Transaction(8, TransactionType.EXPENSE, 1200, "CU편의점", 1, baseTime - 86400000 * 3 + 3600000 * 22, "음료", TransactionSource.AUTO),
        // 1월 28일
        Transaction(9, TransactionType.EXPENSE, 45000, "다이소", 7, baseTime - 86400000 * 4 + 3600000 * 11, "수납용품", TransactionSource.AUTO),
        // 1월 27일
        Transaction(10, TransactionType.INCOME, 500000, "김철수", 10, baseTime - 86400000 * 5 + 3600000 * 13, "설날 용돈", TransactionSource.MANUAL),
        // 2월 추가 거래 (캘린더에 풍부하게 표시)
        Transaction(11, TransactionType.EXPENSE, 8200, "맥도날드", 1, baseTime + 86400000 * 2 + 3600000 * 12, "빅맥세트", TransactionSource.AUTO),
        Transaction(12, TransactionType.EXPENSE, 3400, "버스", 2, baseTime + 86400000 * 2 + 3600000 * 8, "", TransactionSource.AUTO),
        Transaction(13, TransactionType.EXPENSE, 67000, "자라", 3, baseTime + 86400000 * 4 + 3600000 * 16, "니트", TransactionSource.MANUAL),
        Transaction(14, TransactionType.EXPENSE, 9800, "GS25", 1, baseTime + 86400000 * 5 + 3600000 * 23, "야식", TransactionSource.AUTO),
        Transaction(15, TransactionType.EXPENSE, 25000, "넷플릭스", 4, baseTime + 86400000 * 6 + 3600000 * 10, "월 구독", TransactionSource.AUTO),
        Transaction(16, TransactionType.EXPENSE, 142000, "병원", 5, baseTime + 86400000 * 8 + 3600000 * 14, "감기 진료", TransactionSource.MANUAL),
        Transaction(17, TransactionType.EXPENSE, 5500, "지하철", 2, baseTime + 86400000 * 10 + 3600000 * 9, "", TransactionSource.AUTO),
        Transaction(18, TransactionType.EXPENSE, 31000, "교보문고", 6, baseTime + 86400000 * 10 + 3600000 * 15, "책 구매", TransactionSource.MANUAL),
        Transaction(19, TransactionType.INCOME, 200000, "부모님", 10, baseTime + 86400000 * 12 + 3600000 * 11, "생활비", TransactionSource.MANUAL),
        Transaction(20, TransactionType.EXPENSE, 78000, "이마트", 7, baseTime + 86400000 * 14 + 3600000 * 17, "장보기", TransactionSource.AUTO),
        Transaction(21, TransactionType.EXPENSE, 18500, "스타벅스", 1, baseTime + 86400000 * 14 + 3600000 * 10, "프라푸치노", TransactionSource.AUTO),
        Transaction(22, TransactionType.EXPENSE, 6000, "택시", 2, baseTime + 86400000 * 16 + 3600000 * 23, "야간 택시", TransactionSource.AUTO),
        Transaction(23, TransactionType.EXPENSE, 55000, "삼겹살집", 1, baseTime + 86400000 * 18 + 3600000 * 19, "회식", TransactionSource.MANUAL),
        Transaction(24, TransactionType.EXPENSE, 12000, "약국", 5, baseTime + 86400000 * 20 + 3600000 * 13, "감기약", TransactionSource.AUTO),
        Transaction(25, TransactionType.EXPENSE, 95000, "주유소", 2, baseTime + 86400000 * 22 + 3600000 * 11, "주유", TransactionSource.AUTO)
    )

    val candidates = listOf(
        Candidate(1, 101, TransactionType.EXPENSE, 8900, "이디야커피", baseTime + 3600000, "삼성카드", CandidateStatus.CANDIDATE, 0.95f),
        Candidate(2, 102, TransactionType.EXPENSE, 23000, "GS25", baseTime + 7200000, "현대카드", CandidateStatus.CANDIDATE, 0.88f),
        Candidate(3, 103, TransactionType.EXPENSE, 156000, "무신사", baseTime + 10800000, "신한카드", CandidateStatus.CANDIDATE, 0.92f),
        Candidate(4, 104, TransactionType.EXPENSE, 35000, "쿠팡", baseTime + 14400000, "삼성카드", CandidateStatus.CANCEL_CANDIDATE, 0.75f),
        Candidate(5, 105, TransactionType.EXPENSE, 6500, "지하철", baseTime + 18000000, "카카오페이", CandidateStatus.CANDIDATE, 0.80f)
    )

    val pointLedgerEntries = listOf(
        PointLedger(1, 2, "거래 확정", baseTime - 3600000 * 1, 1),
        PointLedger(2, 2, "거래 확정", baseTime - 3600000 * 2, 2),
        PointLedger(3, 2, "거래 확정", baseTime - 3600000 * 3, 3),
        PointLedger(4, 10, "하루 정산 보너스", baseTime - 86400000 * 1),
        PointLedger(5, 2, "거래 확정", baseTime - 86400000 * 2, 5),
        PointLedger(6, 2, "거래 확정", baseTime - 86400000 * 2, 6),
        PointLedger(7, 20, "주간 리포트 확인", baseTime - 86400000 * 7),
        PointLedger(8, 100, "월 마감 보너스", baseTime - 86400000 * 30)
    )

    val totalPoints: Int get() = pointLedgerEntries.sumOf { it.delta }

    val shopItems = listOf(
        ShopItem(1, "봄 꽃무늬 셔츠", ItemRarity.COMMON, 300, ItemType.CLOTH, "화사한 봄 느낌의 셔츠"),
        ShopItem(2, "청바지", ItemRarity.COMMON, 400, ItemType.CLOTH, "클래식한 데님 청바지"),
        ShopItem(3, "선글라스", ItemRarity.COMMON, 500, ItemType.ACCESSORY, "시크한 선글라스"),
        ShopItem(4, "모자", ItemRarity.COMMON, 350, ItemType.ACCESSORY, "귀여운 버킷햇"),
        ShopItem(5, "벚꽃 배경", ItemRarity.COMMON, 600, ItemType.BACKGROUND, "분홍빛 벚꽃 배경"),
        ShopItem(6, "정장 세트", ItemRarity.RARE, 2000, ItemType.CLOTH, "격식 있는 정장"),
        ShopItem(7, "왕관", ItemRarity.RARE, 2500, ItemType.ACCESSORY, "빛나는 황금 왕관"),
        ShopItem(8, "우주 배경", ItemRarity.RARE, 3000, ItemType.BACKGROUND, "은하수가 흐르는 우주"),
        ShopItem(9, "드래곤 갑옷", ItemRarity.EPIC, 5000, ItemType.CLOTH, "전설의 드래곤 갑옷"),
        ShopItem(10, "날개", ItemRarity.EPIC, 4500, ItemType.ACCESSORY, "천사의 날개")
    )

    val characterState = CharacterState(
        equippedCloth = shopItems[0].copy(owned = true),
        equippedAccessory = shopItems[3].copy(owned = true),
        equippedBackground = null
    )

    // Summary helpers
    val monthlyExpense: Long get() = transactions
        .filter { it.type == TransactionType.EXPENSE && it.state == TransactionState.NORMAL }
        .sumOf { it.amount }

    val monthlyIncome: Long get() = transactions
        .filter { it.type == TransactionType.INCOME && it.state == TransactionState.NORMAL }
        .sumOf { it.amount }

    val balance: Long get() = monthlyIncome - monthlyExpense

    fun getCategoryById(id: Long): Category? = categories.find { it.id == id }

    fun getTransactionById(id: Long): Transaction? = transactions.find { it.id == id }

    fun getExpenseByCategory(): Map<Category, Long> {
        return transactions
            .filter { it.type == TransactionType.EXPENSE && it.state == TransactionState.NORMAL }
            .groupBy { getCategoryById(it.categoryId) ?: categories.last() }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }
    }

    fun getTransactionsByDay(year: Int, month: Int, day: Int): List<Transaction> {
        val cal = java.util.Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val dayStart = cal.timeInMillis
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
        val dayEnd = cal.timeInMillis
        return transactions.filter { it.occurredAt in dayStart until dayEnd }
    }

    fun getDailyTotals(year: Int, month: Int): Map<Int, Pair<Long, Long>> {
        val cal = java.util.Calendar.getInstance()
        val result = mutableMapOf<Int, Pair<Long, Long>>()
        transactions.forEach { tx ->
            cal.timeInMillis = tx.occurredAt
            if (cal.get(java.util.Calendar.YEAR) == year && cal.get(java.util.Calendar.MONTH) == month - 1) {
                val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
                val (income, expense) = result.getOrDefault(day, 0L to 0L)
                if (tx.type == TransactionType.INCOME) {
                    result[day] = (income + tx.amount) to expense
                } else {
                    result[day] = income to (expense + tx.amount)
                }
            }
        }
        return result
    }
}
