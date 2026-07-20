package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTheme
import com.example.ui.theme.getColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeIndex: Int,
    onThemeSelect: (Int) -> Unit,
    onLogout: () -> Unit,
    onNavigateAbout: () -> Unit,
    onNavigateEmulator: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showThemeMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("SETTINGS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // Visual Identity Section
            item {
                SettingsHeader("Visual Identity", Icons.Default.Palette)
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showThemeMenu = !showThemeMenu },
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val currentTheme = AppTheme.values().getOrElse(themeIndex) { AppTheme.ORIGINAL }
                            val colors = getColorScheme(currentTheme)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary)
                                    .border(2.dp, colors.background, CircleShape)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Current Combination", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                Text("Combination ${themeIndex + 1}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                        Icon(
                            if (showThemeMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                }
            }

            if (showThemeMenu) {
                itemsIndexed(AppTheme.values()) { index, theme ->
                    val colors = getColorScheme(theme)
                    val isSelected = themeIndex == index
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickable { onThemeSelect(index) },
                        shape = MaterialTheme.shapes.small,
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(30.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            ) {
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(colors.background))
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(colors.primary))
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(colors.secondary))
                            }
                            Spacer(Modifier.width(16.dp))
                            Text(
                                "Combination ${index + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Information Section
            item {
                SettingsHeader("Resources", Icons.Default.Info)
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column {
                        SettingsClickableItem(
                            label = "Supported Emulators",
                            subLabel = "RA Compatible cores & standalone emulators",
                            icon = Icons.Default.Computer,
                            onClick = onNavigateEmulator
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp).alpha(0.1f))
                        SettingsClickableItem(
                            label = "About Canopus T6",
                            subLabel = "Application information and legal",
                            painter = painterResource(id = com.example.R.drawable.ic_launcher_foreground),
                            onClick = onNavigateAbout
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.alpha(0.1f))
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("LOG OUT OF SYSTEM", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SettingsClickableItem(label: String, subLabel: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, painter: androidx.compose.ui.graphics.painter.Painter? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (painter != null) {
            Icon(painter, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Unspecified, modifier = Modifier.size(24.dp))
        } else if (icon != null) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(subLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
    }
}



