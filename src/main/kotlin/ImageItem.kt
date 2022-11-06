import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private const val THUMBNAIL_SIZE = 128

class ImageItem(val imageFile: File) {
    val exif: TiffImageMetadata
    var thumbnail = mutableStateOf<BufferedImage?>(null)

    init {
        val imageMetadata = Imaging.getMetadata(imageFile)
        val jpegMetadata = imageMetadata as JpegImageMetadata
        exif = jpegMetadata.exif
    }

    fun genThumbnail() {
        val jpeg = ImageIO.read(imageFile)
        val width = if(jpeg.width > jpeg.height) {THUMBNAIL_SIZE}else{THUMBNAIL_SIZE * jpeg.height / jpeg.width}
        val height = if(jpeg.width > jpeg.height){THUMBNAIL_SIZE * jpeg.height / jpeg.width}else{THUMBNAIL_SIZE}
        thumbnail.value = BufferedImage(width, height, jpeg.type)
        thumbnail.value!!.graphics.drawImage(jpeg.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING),0, 0, width, height, null)
    }
}


@Composable
fun ImageItemComponent(imageItem: ImageItem) {

    Row {
        if (imageItem.thumbnail.value != null) {
            Image(
                bitmap = imageItem.thumbnail.value!!.toComposeImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(THUMBNAIL_SIZE.dp, THUMBNAIL_SIZE.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(THUMBNAIL_SIZE.dp)
                    .clip(RectangleShape)
                    .background(Color.LightGray)
            ){
                CircularProgressIndicator()
            }
        }
        Text(imageItem.exif.toString())
    }
}