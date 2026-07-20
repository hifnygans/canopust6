package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class EmulatorInfo(val platform: String, val emulators: List<String>)

val emulatorData = listOf(
    EmulatorInfo("NES", listOf("FCEUmm", "Mesen", "Nestopia")),
    EmulatorInfo("SNES", listOf("Snes9x", "Mesen-S", "bsnes")),
    EmulatorInfo("N64", listOf("Mupen64Plus-Next", "Parallel N64")),
    EmulatorInfo("Game Boy / Color", listOf("Gambatte", "SameBoy", "Gearboy")),
    EmulatorInfo("GBA", listOf("mGBA", "VBA-M", "VBA Next")),
    EmulatorInfo("Genesis / Mega Drive", listOf("Genesis Plus GX", "BlastEm", "PicoDrive")),
    EmulatorInfo("PS1", listOf("DuckStation", "Beetle PSX HW", "PCSX ReARMed")),
    EmulatorInfo("PSP", listOf("PPSSPP")),
    EmulatorInfo("Nintendo DS", listOf("melonDS", "DeSmuME")),
    EmulatorInfo("Nintendo 3DS", listOf("Citra")),
    EmulatorInfo("GameCube / Wii", listOf("Dolphin")),
    EmulatorInfo("Dreamcast", listOf("Flycast")),
    EmulatorInfo("Arcade", listOf("FinalBurn Neo", "MAME"))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmulatorScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("COMPATIBLE EMULATORS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black) },
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
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "RetroAchievements requires specific emulators or Libretro cores to track achievements. Below is a list of recommended options.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(emulatorData) { info ->
                EmulatorPlatformCard(info)
            }
        }
    }
}

@Composable
fun EmulatorPlatformCard(info: EmulatorInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Computer, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(info.platform, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            info.emulators.forEach { emu ->
                Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape))
                    Spacer(Modifier.width(12.dp))
                    Text(emu, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
