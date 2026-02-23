package com.hodu.household_ledger

import android.app.Application
import com.hodu.household_ledger.core.data.local.ConsentManager
import com.hodu.household_ledger.core.data.local.TokenManager
import com.hodu.household_ledger.core.data.local.UserManager
import com.kakao.sdk.common.KakaoSdk

class HouseholdLedgerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        TokenManager.init(this)
        UserManager.init(this)
        ConsentManager.init(this)
    }
}
