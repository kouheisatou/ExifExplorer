import com.drew.imaging.ImageMetadataReader
import java.io.File

fun main() {
    try {
        val sampleFile = File("src/main/resources/sample.JPG")
        val metadata = ImageMetadataReader.readMetadata(sampleFile)

        for(directory in metadata.directories){
            println("==============${directory.name}==============")
            for(tag in directory.tags){
                println("${tag.tagType}:${tag.tagName}=${tag.description}")
            }
        }
    }catch (e: Exception){
        e.printStackTrace()
    }
}