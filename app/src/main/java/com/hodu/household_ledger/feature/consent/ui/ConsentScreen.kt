package com.hodu.household_ledger.feature.consent.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hodu.household_ledger.core.data.local.ConsentManager
import com.hodu.household_ledger.core.ui.theme.AccentGradientEnd
import com.hodu.household_ledger.core.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentScreen(
    onConsentComplete: () -> Unit
) {
    var termsAgreed by remember { mutableStateOf(false) }
    var privacyAgreed by remember { mutableStateOf(false) }
    var marketingAgreed by remember { mutableStateOf(false) }
    var sheetContent by remember { mutableStateOf<ConsentDetailType?>(null) }

    val allRequired = termsAgreed && privacyAgreed
    val allChecked = termsAgreed && privacyAgreed && marketingAgreed

    val headerAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        headerAlpha.animateTo(1f, tween(500))
        contentAlpha.animateTo(1f, tween(400, delayMillis = 100))
        buttonAlpha.animateTo(1f, tween(400, delayMillis = 200))
    }

    val colors = MaterialTheme.colorScheme

    // 약관 상세 BottomSheet
    if (sheetContent != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { sheetContent = null },
            sheetState = sheetState,
            containerColor = colors.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ConsentDetailContent(
                type = sheetContent!!,
                onDismiss = { sheetContent = null }
            )
        }
    }

    Scaffold(
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // Header
            Column(modifier = Modifier.alpha(headerAlpha.value)) {
                Text(
                    text = "콩돈",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.onBackground,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "서비스 이용을 위해\n약관에 동의해 주세요",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.onSurfaceVariant,
                    lineHeight = 26.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 전체 동의
            Column(modifier = Modifier.alpha(contentAlpha.value)) {
                ConsentAllRow(
                    checked = allChecked,
                    onCheckedChange = { checked ->
                        termsAgreed = checked
                        privacyAgreed = checked
                        marketingAgreed = checked
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    color = colors.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 개별 항목
                ConsentItemRow(
                    tag = "필수",
                    isRequired = true,
                    label = "서비스 이용약관 동의",
                    checked = termsAgreed,
                    onCheckedChange = { termsAgreed = it },
                    onDetailClick = { sheetContent = ConsentDetailType.TERMS }
                )
                ConsentItemRow(
                    tag = "필수",
                    isRequired = true,
                    label = "개인정보 수집·이용 동의",
                    checked = privacyAgreed,
                    onCheckedChange = { privacyAgreed = it },
                    onDetailClick = { sheetContent = ConsentDetailType.PRIVACY }
                )
                ConsentItemRow(
                    tag = "선택",
                    isRequired = false,
                    label = "마케팅 정보 수신 동의",
                    checked = marketingAgreed,
                    onCheckedChange = { marketingAgreed = it },
                    onDetailClick = { sheetContent = ConsentDetailType.MARKETING }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 동의 버튼
            Column(modifier = Modifier.alpha(buttonAlpha.value)) {
                ConsentButton(
                    enabled = allRequired,
                    onClick = {
                        ConsentManager.saveConsent(marketingAgreed)
                        onConsentComplete()
                    }
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

private enum class ConsentDetailType(val title: String) {
    TERMS("서비스 이용약관"),
    PRIVACY("개인정보 수집·이용 동의"),
    MARKETING("마케팅 정보 수신 동의")
}

@Composable
private fun ConsentDetailContent(
    type: ConsentDetailType,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp)
    ) {
        Text(
            text = type.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )
        Spacer(modifier = Modifier.height(20.dp))

        val content = when (type) {
            ConsentDetailType.TERMS -> """
제1조 (목적)
본 약관은 콩돈(이하 "서비스")의 이용과 관련하여 서비스 제공자와 이용자 간의 권리, 의무 및 책임 사항을 규정함을 목적으로 합니다.

제2조 (서비스의 내용)
① 서비스는 사용자의 금융 거래 내역을 자동으로 수집·분류하여 가계부를 관리할 수 있도록 지원합니다.
② 서비스는 알림 접근 권한을 통해 거래 알림을 읽고, 이를 가공하여 거래 내역으로 등록합니다.

제3조 (이용자의 의무)
① 이용자는 서비스를 불법적인 목적으로 사용해서는 안 됩니다.
② 이용자는 타인의 정보를 도용하거나, 서비스의 정상적인 운영을 방해해서는 안 됩니다.

제4조 (서비스의 변경 및 중단)
① 서비스 제공자는 운영상, 기술상의 이유로 서비스를 변경하거나 중단할 수 있습니다.
② 서비스 변경 또는 중단 시 이용자에게 사전 공지합니다.

제5조 (면책)
① 서비스 제공자는 천재지변, 불가항력으로 인한 서비스 제공 불가에 대해 책임지지 않습니다.
② 이용자의 귀책 사유로 인한 서비스 이용 장애에 대해서는 책임지지 않습니다.
            """.trimIndent()

            ConsentDetailType.PRIVACY -> """
1. 수집하는 개인정보 항목
- 소셜 로그인 정보: 닉네임, 프로필 이미지, 소셜 계정 고유 ID
- 거래 정보: 금액, 가맹점명, 거래 일시, 거래 유형

2. 개인정보의 수집·이용 목적
- 회원 식별 및 서비스 제공
- 거래 내역 자동 수집 및 가계부 관리
- 통계 분석 및 지출 리포트 제공

3. 개인정보의 보유 및 이용 기간
- 회원 탈퇴 시까지 보유하며, 탈퇴 시 즉시 파기합니다.
- 관계 법령에 따라 보존할 필요가 있는 경우 해당 기간 동안 보관합니다.

4. 개인정보의 파기 절차 및 방법
- 이용 목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다.
- 전자적 파일 형태의 정보는 복구할 수 없는 방법으로 영구 삭제합니다.

5. 이용자의 권리
- 이용자는 언제든지 자신의 개인정보에 대한 열람, 수정, 삭제를 요청할 수 있습니다.
- 회원 탈퇴를 통해 개인정보 처리 정지를 요청할 수 있습니다.
            """.trimIndent()

            ConsentDetailType.MARKETING -> """
1. 마케팅 정보 수신 항목
- 서비스 업데이트 및 새로운 기능 안내
- 이벤트 및 프로모션 정보
- 맞춤형 지출 분석 리포트 및 절약 팁

2. 수신 방법
- 앱 푸시 알림

3. 동의 철회
- 설정 메뉴에서 언제든지 마케팅 수신 동의를 철회할 수 있습니다.
- 동의 철회 시 마케팅 관련 알림이 즉시 중단됩니다.

※ 마케팅 정보 수신에 동의하지 않아도 서비스 이용에 제한이 없습니다.
            """.trimIndent()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = content,
                fontSize = 13.sp,
                color = colors.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.onBackground,
                contentColor = colors.background
            )
        ) {
            Text("확인", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ConsentButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "btnScale"
    )

    val colors = MaterialTheme.colorScheme
    val disabledBg = colors.surfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (enabled) Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = GoldPrimary.copy(alpha = 0.3f),
                    spotColor = GoldPrimary.copy(alpha = 0.25f)
                ) else Modifier
            )
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = if (enabled) Brush.horizontalGradient(
                    colors = listOf(GoldPrimary, AccentGradientEnd)
                ) else Brush.horizontalGradient(
                    colors = listOf(disabledBg, disabledBg)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "동의하고 시작하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else colors.onSurfaceVariant.copy(alpha = 0.5f)
            )
            if (enabled) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun ConsentAllRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val bgColor by animateColorAsState(
        targetValue = if (checked) GoldPrimary.copy(alpha = 0.08f) else colors.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(250),
        label = "allBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) GoldPrimary.copy(alpha = 0.25f) else colors.outline.copy(alpha = 0.15f),
        animationSpec = tween(250),
        label = "allBorder"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleCheck(checked = checked, size = 28)
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "전체 동의합니다",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
        }
    }
}

@Composable
private fun ConsentItemRow(
    tag: String,
    isRequired: Boolean,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDetailClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(horizontal = 8.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleCheck(checked = checked, size = 22)
        Spacer(modifier = Modifier.width(12.dp))

        // 뱃지 스타일 태그
        val tagBgColor = if (isRequired) GoldPrimary.copy(alpha = 0.1f) else colors.surfaceVariant
        val tagTextColor = if (isRequired) GoldPrimary else colors.onSurfaceVariant

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(tagBgColor)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = tag,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = tagTextColor,
                letterSpacing = 0.3.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = colors.onSurface.copy(alpha = 0.85f)
        )

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onDetailClick,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = "상세 보기",
                modifier = Modifier.size(18.dp),
                tint = colors.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun CircleCheck(checked: Boolean, size: Int) {
    val bgColor by animateColorAsState(
        targetValue = if (checked) GoldPrimary else Color(0xFFD4D4D8),
        animationSpec = tween(200),
        label = "checkBg"
    )
    val checkScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkScale"
    )

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Rounded.Check,
            contentDescription = null,
            modifier = Modifier
                .size((size * 0.55f).dp)
                .scale(checkScale),
            tint = Color.White
        )
    }
}
