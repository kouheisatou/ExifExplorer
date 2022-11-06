import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

private const val AVAILABLE_THREAD = 4

class ImageItemListModel(private val directory: File) {
    val imageList = mutableStateListOf<ImageItem>()

    fun loadFiles(onProgressChanged: (percentage: Float) -> Unit, onLoadFinished: (ImageItemListModel) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileList = directory.listFiles { pathname: File ->
                pathname.extension.matches(
                    Regex("JPG|JPEG|jpg|jpeg")
                )
            }
            for (file in fileList.withIndex()) {
                imageList.add(ImageItem(file.value))
                onProgressChanged((file.index + 1).toFloat() / fileList.size)
                println(file.value.name)
            }

            onLoadFinished(this@ImageItemListModel)
        }
    }

    fun genThumbnails(onProgressChanged: (percentage: Float, processingImage: ImageItem) -> Unit) {
        var completed = 0
        CoroutineScope(Dispatchers.IO).launch {
            for (imageItem in imageList.withIndex()) {
                imageItem.value.genThumbnail()
                onProgressChanged(completed / imageList.size.toFloat(), imageItem.value)
                completed++
            }
        }
    }

    fun sort() {
    }
}

@Composable
fun ImageListComponent(imageItemListModel: ImageItemListModel, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(imageItemListModel.imageList.size) {
            ImageItemComponent(imageItemListModel.imageList[it])
            Divider(
                modifier = Modifier.background(color = Color.Black)
            )
        }
    }
}