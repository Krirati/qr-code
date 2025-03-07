package com.kstudio.qrcode.ui.component.navigation

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kstudio.qrcode.ui.theme.QrCodeTheme

@Composable
fun NavigationBar(
    tabs: List<String>,
    selectedOption: Int = 0,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    tabColor: Color = Color.White,
    selectedOptionColor: Color = Color(0xFF7980FF),
    containerCornerRadius: Dp = 16.dp,
    tabCornerRadius: Dp = 12.dp,
    selectorHeight: Dp = 48.dp,
    tabHeight: Dp = 40.dp,
    spacing: Dp = 4.dp,
    textStyle: TextStyle = TextStyle(
        color = Color(0xFF31394F).copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    selectedTabTextStyle: TextStyle = TextStyle(
        color = selectedOptionColor,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    onTabSelected: (selectedIndex: Int) -> Unit = {}
) {
    if (tabs.size !in 2..4) {
        throw IllegalArgumentException("NavigationBar must have between 2 and 4")
    }

    BoxWithConstraints(
        modifier = Modifier
            .clip(RoundedCornerShape(containerCornerRadius))
            .height(selectorHeight)
            .background(containerColor)
    ) {
        val segmentWidth = maxWidth / tabs.size
        val boxWidth = segmentWidth - spacing * 2
        val positions = tabs.indices.map { index ->
            segmentWidth * index + (segmentWidth - boxWidth) / 2
        }

        val animatedOffsetX by animateDpAsState(targetValue = positions[selectedOption])
        val containerHeight = maxHeight
        val verticalOffset = (containerHeight - tabHeight) / 2

        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, text ->
                Text(
                    text = text,
                    style = textStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(segmentWidth)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onTabSelected(index)
                        }
                )
            }
        }
        Box(
            modifier = Modifier
                .offset(x = animatedOffsetX, y = verticalOffset)
                .clip(RoundedCornerShape(tabCornerRadius))
                .width(boxWidth) // Updated box width
                .height(tabHeight)
                .background(tabColor)
        ) {
            Text(
                text = tabs[selectedOption],
                modifier = Modifier.align(Alignment.Center),
                style = selectedTabTextStyle
            )
        }
    }
}

@Preview
@Composable
private fun NavigationBarPreview() {
    QrCodeTheme {
        val selectedOption = remember { mutableIntStateOf(0) }
        val optionTexts = listOf("Tab 1", "Tab 2", "Tab 3")
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .background(Color.Gray)
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            NavigationBar(tabs = optionTexts, selectedOption = selectedOption.intValue) {
                Toast.makeText(context, "Selected tab: ${it + 1}", Toast.LENGTH_SHORT).show()
                selectedOption.intValue = it
            }
        }
    }
}