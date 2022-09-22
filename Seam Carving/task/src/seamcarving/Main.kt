package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val image: BufferedImage = ImageIO.read(File(args[1]))
    for (i in 0 until image.height) {
        for (j in 0 until image.width) {
            val color  =  Color(image.getRGB(j, i))
            val newColor = Color(255-color.red, 255-color.green, 255-color.blue)
            image.setRGB(j, i, newColor.rgb)
        }
    }
    ImageIO.write(image, "png", File(args[3]))
}
