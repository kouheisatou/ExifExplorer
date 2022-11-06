import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.swing.JFileChooser

private var state = mutableStateOf<WindowState>(WindowState.Initialized)

@Composable
@Preview
fun App() {
    MaterialTheme {
        when (state.value) {
            is WindowState.Initialized -> {
                Text("Press ⌘+O to open directory")
            }

            is WindowState.Loading -> {
                LinearProgressIndicator((state.value as WindowState.Loading).loadingProgress.value)
            }

            is WindowState.Displaying -> {
                val displayingState = state.value as WindowState.Displaying
                Column {
                    if (displayingState.loadingProgress.value != 1f) {
                        Row {
                            Text(displayingState.processingContentText.value)
                            LinearProgressIndicator(displayingState.loadingProgress.value)
                        }
                    }
                    ImageListComponent(displayingState.imageItemListModel)
                }
            }
        }
    }
}

sealed class WindowState {
    object Initialized : WindowState()
    class Loading(val imageItemListModel: ImageItemListModel) : WindowState() {
        val loadingProgress = mutableStateOf(0f)
    }

    class Displaying(val imageItemListModel: ImageItemListModel) : WindowState() {
        var loadingProgress = mutableStateOf(0f)
        val processingContentText = mutableStateOf("")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MenuBar {
            Menu("File", mnemonic = 'F') {
                Item(
                    "Open directory",
                    onClick = {
                        val fileChooser = JFileChooser().apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY }
                        val selected = fileChooser.showOpenDialog(window)
                        if (selected == JFileChooser.APPROVE_OPTION) {
                            val imageItemListModel = ImageItemListModel(fileChooser.selectedFile)

                            state.value = WindowState.Loading(imageItemListModel)

                            imageItemListModel.loadFiles(
                                onProgressChanged = {
                                    (state.value as WindowState.Loading).loadingProgress.value = it
                                },
                                onLoadFinished = {
                                    state.value = WindowState.Displaying(it)
                                    imageItemListModel.genThumbnails(
                                        onProgressChanged = { percentage, imageItem ->
                                            (state.value as WindowState.Displaying).loadingProgress.value = percentage
                                            (state.value as WindowState.Displaying).processingContentText.value =
                                                "Generating thumbnail of ${imageItem.imageFile.name}"
                                        },
                                    )
                                }
                            )
                        }
                    },
                    shortcut = KeyShortcut(Key.O, meta = true),
                )
            }
        }
        App()
    }
}
