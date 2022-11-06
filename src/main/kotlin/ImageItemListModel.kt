import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

class ImageItemListModel(private val directory: File) {
    val imageList = mutableStateListOf<ImageItem>()
    var thumbnailGenerateProcess: Job? = null
    var exifColumns = mutableStateListOf<String>()
    var exifColumnWidth = mutableStateListOf<Int>()
    var exifColumnScrollState = mutableStateOf(ScrollState(0))

    fun loadFiles(onProgressChanged: (percentage: Float) -> Unit, onLoadFinished: (ImageItemListModel) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileList = directory.listFiles { pathname: File ->
                pathname.extension.matches(
                    Regex("JPG|JPEG|jpg|jpeg")
                )
            }
            for (file in fileList.withIndex()) {
                val imageItem = ImageItem(file.value)
                imageItem.exifMap.forEach{
                    if(!exifColumns.contains(it.key)){
                        exifColumns.add(it.key)
                        exifColumnWidth.add(100)
                    }
                }
                imageList.add(imageItem)
                onProgressChanged((file.index + 1).toFloat() / fileList.size)
            }

            onLoadFinished(this@ImageItemListModel)
        }
    }

    fun genThumbnails(onProgressChanged: (percentage: Float, processingImage: ImageItem) -> Unit) {
        var completed = 1
        thumbnailGenerateProcess = CoroutineScope(Dispatchers.IO).launch {
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
    Row(
        modifier = Modifier
            .padding(start = THUMBNAIL_SIZE.dp)
            .horizontalScroll(imageItemListModel.exifColumnScrollState.value)
    ){
        for (i in 0 until imageItemListModel.exifColumns.size) {
            Box(modifier = Modifier.width(imageItemListModel.exifColumnWidth[i].dp)){
                Text(imageItemListModel.exifColumns[i])
            }
        }
    }

    Divider(color = Color.Black)
    LazyColumn(modifier = modifier) {
        items(imageItemListModel.imageList.size) {
            ImageItemComponent(imageItemListModel.imageList[it], imageItemListModel.exifColumns, imageItemListModel.exifColumnWidth, imageItemListModel.exifColumnScrollState)
            Divider(
                modifier = Modifier.background(color = Color.Black)
            )
        }
    }
}