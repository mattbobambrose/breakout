import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import kotlin.math.cos
import kotlin.math.sin

data class Ball(
    val program: Program,
    val paddle: Paddle,
    val radius: Double,
    val speed: Double,
    val color: ColorRGBa,
    var x: Double = radius,
    var y: Double = radius,
    var angle: Double = 45.0,
) {

    fun update() {
        var ph = paddle.height / 2
        if (x in (paddle.x..paddle.x + paddle.width)
            && y in ((paddle.y - ph - (radius / 2))..(paddle.y + ph + radius))
            && angle < 180
        ) {
            angle = normalizeTo360Range(360 - angle)
        }
        if (x <= radius || x >= program.width - radius) {
            angle = normalizeTo360Range(180 - angle)
        }
        if (y <= radius || y >= program.height - radius) {
            angle = 360 - angle
        }
        x += speed * cos(Math.toRadians(angle))
        y += speed * sin(Math.toRadians(angle))

        when {
            x < radius -> x = radius
            x > program.width - radius -> x = program.width - radius
            y < radius -> y = radius
            y > program.height - radius -> y = program.height - radius
        }
    }

    fun draw() {
        update()
        program.drawer.apply {
            fill = color
            circle(x, y, radius)
        }
    }
}

data class Paddle(
    val program: Program,
    val width: Double,
    val height: Double,
    var speed: Double,
    val color: ColorRGBa,
    var x: Double = 0.0,
    var y: Double = 0.0,
) {
    fun moveLeft() {
        x -= speed
    }

    fun moveRight() {
        x += speed
    }

    fun draw() {
        program.drawer.apply {
            fill = color
            stroke = null
            rectangle(x, y, this@Paddle.width, this@Paddle.height)
        }
    }

}

fun normalizeTo360Range(a: Double) = (a + 360) % 360

fun main() {
    application {
        configure {}
        program {
            var paddleWidth = width / 4.0
            var paddleHeight = height / 64.0

            val paddle =
                Paddle(this, paddleWidth, paddleHeight, 20.0, ColorRGBa.WHITE, width / 2.0, height - height / 7.0)
            val ball = Ball(this, paddle, width / 64.0, 3.0, ColorRGBa.WHITE)

            keyboard.keyDown.listen {
                if (it.name == "arrow-left") {
                    paddle.moveLeft()
                }
                if (it.name == "arrow-right") {
                    paddle.moveRight()
                }
            }
            extend {
                drawer.clear(ColorRGBa.PINK)
                paddle.draw()
                ball.draw()
            }
        }
    }
}