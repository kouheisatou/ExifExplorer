import org.apache.commons.imaging.ImageReadException
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata
import java.io.File
import java.io.IOException


fun main() {
    println(readExifMetadata(File("src/main/resources/sample.JPG")))
}

    private fun readExifMetadata(imageFile: File): TiffImageMetadata? {
        val imageMetadata = Imaging.getMetadata(imageFile.readBytes()) ?: return null
        val jpegMetadata = imageMetadata as JpegImageMetadata
        return jpegMetadata.exif ?: return null
}
