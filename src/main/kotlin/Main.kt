import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.io.FileFilter
import javax.swing.JFileChooser

var directorySelected = mutableStateOf<Int?>(null)
var fileChooser = JFileChooser().apply { fileSelectionMode = JFileChooser.DIRECTORIES_ONLY }

@Composable
@Preview
fun App() {
    MaterialTheme {
        if (directorySelected.value == JFileChooser.APPROVE_OPTION && fileChooser.selectedFile != null) {
            val listModel = ImageItemListModel(fileChooser.selectedFile)

            ImageListComponent(listModel)
        }
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
                        directorySelected.value = fileChooser.showOpenDialog(window)
                    },
                    shortcut = KeyShortcut(Key.O, meta = true),
                )
            }
        }
        App()
    }
}
