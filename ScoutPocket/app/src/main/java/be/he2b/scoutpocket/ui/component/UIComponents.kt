package be.he2b.scoutpocket.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.model.backgroundColor
import be.he2b.scoutpocket.model.textColor
import be.he2b.scoutpocket.ui.theme.ScoutPocketTheme

@Composable
fun SectionPill(
    modifier: Modifier = Modifier,
    section: Section,
) {
    Box(
        modifier = modifier
            .background(
                color = section.backgroundColor(),
                shape = CircleShape,
            )
            .padding(12.dp, 4.dp),
    ) {
        Text(
            text = section.label,
            style = MaterialTheme.typography.bodySmall,
            color = section.textColor(),
        )
    }
}

@Composable
fun SwitchButton(
    modifier: Modifier = Modifier,
    buttons: List<Pair<String, Boolean>>,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape,
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape,
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        buttons.forEach { (label, isSelected) ->
            val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .clickable(onClick = onClick)
                    .padding(12.dp, 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
            }
        }
    }
}

@Preview
@Composable
fun SwitchButtonPreview() {
    val buttons = listOf("Button 1" to true, "Button 2" to false)

    ScoutPocketTheme {
        SwitchButton(
            buttons = buttons,
            onClick = { },
        )
    }
}