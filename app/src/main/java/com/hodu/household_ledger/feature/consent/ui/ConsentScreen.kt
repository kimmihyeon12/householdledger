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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.hodu.household_ledger.core.ui.theme.AccentGradientWarm
import com.hodu.household_ledger.core.ui.theme.GoldPrimary

@Composable
fun ConsentScreen(
    onConsentComplete: () -> Unit
) {
    var termsAgreed by remember { mutableStateOf(false) }
    var privacyAgreed by remember { mutableStateOf(false) }
    var marketingAgreed by remember { mutableStateOf(false) }

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
                    color = GoldPrimary,
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
                    onCheckedChange = { termsAgreed = it }
                )
                ConsentItemRow(
                    tag = "필수",
                    isRequired = true,
                    label = "개인정보 수집·이용 동의",
                    checked = privacyAgreed,
                    onCheckedChange = { privacyAgreed = it }
                )
                ConsentItemRow(
                    tag = "선택",
                    isRequired = false,
                    label = "마케팅 정보 수신 동의",
                    checked = marketingAgreed,
                    onCheckedChange = { marketingAgreed = it }
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
    onCheckedChange: (Boolean) -> Unit
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
        Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = colors.onSurfaceVariant.copy(alpha = 0.3f)
        )
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
