import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo
import java.io.File
import java.io.FileFilter

class ImageItemListModel(directory: File) {
    val imageList = mutableStateListOf<ImageItem>()

    init {

        directory.listFiles { pathname: File ->
            pathname.extension.matches(
                Regex("JPG|JPEG|jpg|jpeg")
            )
        }?.forEach {
            imageList.add(ImageItem(it))
        }
    }

    fun sort(){
    }
}

@Composable
fun ImageListComponent(imageItemListModel: ImageItemListModel) {
    LazyColumn {
        items(imageItemListModel.imageList.size) {
            ImageItemComponent(imageItemListModel.imageList[it])
        }
    }
}