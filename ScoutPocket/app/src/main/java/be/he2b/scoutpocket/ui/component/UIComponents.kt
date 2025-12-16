package be.he2b.scoutpocket.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.database.entity.Presence
import be.he2b.scoutpocket.model.PresenceStatus
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.model.backgroundColor
import be.he2b.scoutpocket.model.contentColor
import be.he2b.scoutpocket.model.textColor
import be.he2b.scoutpocket.ui.theme.ScoutPocketTheme
import com.composables.icons.lucide.CheckCheck
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composables.icons.lucide.X

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
            .height(48.dp)
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

@Composable
fun MemberCard(
    modifier: Modifier = Modifier,
    member: Member,
    presence: Presence?,
    onPresenceClick: (() -> Unit)? = null,
) {
    Surface(
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
            ),
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = member.section.backgroundColor(),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${member.firstName.first()}${member.lastName.first()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = member.section.textColor()
                )
            }

            Text(
                modifier = Modifier.weight(1f),
                text = "${member.firstName} ${member.lastName.uppercase()}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            if (presence != null && onPresenceClick != null) {
                PresenceButton(
                    status = presence.status,
                    onClick = onPresenceClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemberCardPreview() {
    val sampleMember = Member(
        lastName = "Dupont",
        firstName = "Lucas",
        section = Section.BALADINS
    )

    ScoutPocketTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MemberCard(
                member = sampleMember,
                presence = Presence(
                    eventId = 0,
                    memberId = 0,
                    status = PresenceStatus.DEFAULT,
                )
            )
        }
    }
}

@Composable
fun PresenceButton(
    modifier: Modifier = Modifier,
    status: PresenceStatus,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .background(
                color = status.backgroundColor(),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .size(22.dp),
            imageVector = when (status) {
                PresenceStatus.DEFAULT -> Lucide.Minus
                PresenceStatus.PRESENT -> Lucide.CheckCheck
                PresenceStatus.ABSENT -> Lucide.X
            },
            contentDescription = "Statut",
            tint = status.contentColor(),
        )
    }
}

@Preview
@Composable
fun PresenceButtonPreview() {
    ScoutPocketTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PresenceButton(
                status = PresenceStatus.DEFAULT,
                onClick = { }
            )
            PresenceButton(
                status = PresenceStatus.PRESENT,
                onClick = { }
            )
            PresenceButton(
                status = PresenceStatus.ABSENT,
                onClick = { }
            )
        }
    }
}
