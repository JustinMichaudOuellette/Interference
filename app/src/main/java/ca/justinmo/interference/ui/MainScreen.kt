package ca.justinmo.interference.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.NumberPicker
import ca.justinmo.interference.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onAboutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onAboutClick) {
                        Icon(Icons.Outlined.Info, contentDescription = "About")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WaveSelectionSection(
                selectedWave = viewModel.selectedWave,
                onWaveSelected = viewModel::onWaveSelected
            )

            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Controls",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    ToggleRow(
                        isXAxis = viewModel.isXAxis,
                        invert = viewModel.invert,
                        stairs = viewModel.stairs,
                        onTogglesChanged = viewModel::onTogglesChanged
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Frequency Range",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FrequencyPickers(
                        frequencies = viewModel.frequencies,
                        minIdx = viewModel.minFrequencyIndex,
                        maxIdx = viewModel.maxFrequencyIndex,
                        onMinChanged = viewModel::onMinFrequencyChanged,
                        onMaxChanged = viewModel::onMaxFrequencyChanged
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Adjustments",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    AdjustmentSliders(
                        distortion = viewModel.distortion,
                        exponential = viewModel.exponential,
                        onDistortionChanged = viewModel::onDistortionChanged,
                        onExponentialChanged = viewModel::onExponentialChanged
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaveSelectionSection(
    selectedWave: Int,
    onWaveSelected: (Int) -> Unit
) {
    val waves = listOf(
        R.drawable.ic_stop_24_white,
        R.drawable.ic_square_wave_24_white,
        R.drawable.ic_sawtooth_wave_24_white,
        R.drawable.ic_triangle_wave_24_white,
        R.drawable.ic_sine_wave_24_white,
        R.drawable.ic_noise_wave_24_white,
        R.drawable.ic_radioactive_24_white
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Waveform",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            waves.forEachIndexed { index, iconRes ->
                SegmentedButton(
                    selected = selectedWave == index,
                    onClick = { onWaveSelected(index) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = waves.size),
                    icon = {
                        // Custom icon handling if needed, but here we just use the painter
                    }
                ) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ToggleRow(
    isXAxis: Boolean,
    invert: Boolean,
    stairs: Boolean,
    onTogglesChanged: (Boolean, Boolean, Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val modifier = Modifier.weight(1f)
        
        FilterChip(
            modifier = modifier,
            selected = isXAxis,
            onClick = { onTogglesChanged(!isXAxis, invert, stairs) },
            label = { 
                Text(
                    text = if (isXAxis) stringResource(R.string.x) else stringResource(R.string.y),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                ) 
            }
        )
        FilterChip(
            modifier = modifier,
            selected = invert,
            onClick = { onTogglesChanged(isXAxis, !invert, stairs) },
            label = { 
                Text(
                    text = if (invert) stringResource(R.string.minus) else stringResource(R.string.plus),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                ) 
            }
        )
        FilterChip(
            modifier = modifier,
            selected = stairs,
            onClick = { onTogglesChanged(isXAxis, invert, !stairs) },
            label = { 
                Text(
                    text = if (stairs) stringResource(R.string.stairs_on) else stringResource(R.string.stairs_off),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                ) 
            }
        )
    }
}

@Composable
fun FrequencyPickers(
    frequencies: Array<String>,
    minIdx: Int,
    maxIdx: Int,
    onMinChanged: (Int) -> Unit,
    onMaxChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val size = frequencies.size
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.min_hz), style = MaterialTheme.typography.labelLarge)
            ComposeNumberPicker(
                values = frequencies,
                value = minIdx,
                onValueChange = onMinChanged,
                min = 0,
                max = size - 2
            )
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.max_hz), style = MaterialTheme.typography.labelLarge)
            ComposeNumberPicker(
                values = frequencies,
                value = maxIdx,
                onValueChange = onMaxChanged,
                min = 1,
                max = size - 1
            )
        }
    }
}

@Composable
fun ComposeNumberPicker(
    values: Array<String>,
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int
) {
    AndroidView(
        modifier = Modifier.height(160.dp),
        factory = { context ->
            NumberPicker(context).apply {
                minValue = min
                maxValue = max
                displayedValues = values.sliceArray(min..max)
                wrapSelectorWheel = false
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            }
        },
        update = { picker ->
            picker.setOnValueChangedListener { _, _, newVal ->
                onValueChange(newVal)
            }
            if (picker.value != value) {
                picker.value = value
            }
        }
    )
}

@Composable
fun AdjustmentSliders(
    distortion: Float,
    exponential: Float,
    onDistortionChanged: (Float) -> Unit,
    onExponentialChanged: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Text(stringResource(R.string.distortion), style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                Slider(
                    value = distortion,
                    onValueChange = onDistortionChanged,
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
        
        Column {
            Text(stringResource(R.string.exponential), style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                Slider(
                    value = (exponential - 1f) / 10f, // Map 1.0..11.0 to 0.0..1.0
                    onValueChange = { onExponentialChanged(1f + it * 10f) },
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}
