import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

const val THUMBNAIL_SIZE = 128

class ImageItem(val imageFile: File) {
    val exifMap: Map<String, String>
    var thumbnail = mutableStateOf<BufferedImage?>(null)


    init {
        val imageMetadata = Imaging.getMetadata(imageFile)
        val jpegMetadata = imageMetadata as JpegImageMetadata

        val exifMap = mutableMapOf<String, String>()
        jpegMetadata.exif.allFields.forEach {
            exifMap[it.tagName] = it.value.toString()
        }
        this.exifMap = exifMap
    }

    fun genThumbnail() {
        val jpeg = ImageIO.read(imageFile)
        val width = THUMBNAIL_SIZE
        val height = THUMBNAIL_SIZE * jpeg.height / jpeg.width
        thumbnail.value = BufferedImage(width, height, jpeg.type)
        thumbnail.value!!.graphics.drawImage(
            jpeg.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING),
            0,
            0,
            width,
            height,
            null
        )
    }
}


@Composable
fun ImageItemComponent(imageItem: ImageItem, exifColumn: SnapshotStateList<String>, exifColumnWidth: SnapshotStateList<Int>, exifColumnScrollState: State<ScrollState>) {

    Row {
        Column {
            val modifier = Modifier
                .clickable {
                    println("todo : open new image window")
                    println(imageItem.exifMap.toString())
                }

            if (imageItem.thumbnail.value != null) {
                Image(
                    bitmap = imageItem.thumbnail.value!!.toComposeImageBitmap(),
                    contentDescription = null,
                    modifier = modifier.size(imageItem.thumbnail.value!!.width.dp, imageItem.thumbnail.value!!.height.dp)
                )
            }else{
                Box(
                    modifier = modifier
                        .size(THUMBNAIL_SIZE.dp)
                        .clip(RectangleShape)
                        .background(Color.LightGray)
                )
            }
            Text(imageItem.imageFile.name)
        }

        Row(modifier = Modifier.horizontalScroll(exifColumnScrollState.value)) {
            for (i in 0 until exifColumn.size) {
                Box(modifier = Modifier.width(exifColumnWidth[i].dp)){
                    Text(imageItem.exifMap[exifColumn[i]].toString())
                }
            }
        }
    }
}