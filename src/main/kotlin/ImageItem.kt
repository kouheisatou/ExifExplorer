import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata
import java.io.File

class ImageItem(val imageFile: File) {

    val exif: TiffImageMetadata

    init {
        val imageMetadata = Imaging.getMetadata(imageFile.readBytes())
        val jpegMetadata = imageMetadata as JpegImageMetadata
        exif = jpegMetadata.exif
    }
}

@Composable
fun ImageItemComponent(imageItem: ImageItem){

    Row {
        Image(
            bitmap = loadImageBitmap(imageItem.imageFile.inputStream()),
            contentDescription = null,
            modifier = Modifier.size(120.dp, 120.dp)
        )
        Text(imageItem.exif.toString())
    }
}