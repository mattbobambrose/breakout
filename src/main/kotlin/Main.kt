import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import kotlin.math.cos
import kotlin.math.sin

data class Ball(
    val program: Program,
    val paddle: Paddle,
    val radius: Double,
    val speed: Double,
    val color: ColorRGBa,
    var x: Double = paddle.x,
    var y: Double = paddle.y - paddle.height / 2 - radius,
    var angle: Double = 180.0,
) {

    private fun update() {
        val ph = paddle.height / 2
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
            angle = normalizeTo360Range(360 - angle)
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

data class Brick(
    val program: Program,
    val ball: Ball,
    val width: Double,
    val height: Double,
    val color: ColorRGBa,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var isDead: Boolean = false,
) {
    fun draw() {
        program.drawer.apply {
            fill = color
            stroke = null
            if (!isDead) {
                rectangle(x, y, this@Brick.width, this@Brick.height)
            }
            if (isBottomHit()) {
                isDead = true
                ball.angle = normalizeTo360Range(360 - ball.angle)
            }
            if (isTopHit()) {
                isDead = true
                ball.angle = normalizeTo360Range(360 - ball.angle)
            }
            if (isLeftHit()) {
                isDead = true
                ball.angle = normalizeTo360Range(180 - ball.angle)
            }
            if (isRightHit()) {
                isDead = true
                ball.angle = normalizeTo360Range(180 - ball.angle)
            }
        }
    }

    private fun isBottomHit(): Boolean {
        return !isDead && ball.x in (x..x + width) && ball.y in (y + height..y + height + ball.radius) && ball.angle > 180
    }

    private fun isTopHit(): Boolean {
        return !isDead && ball.x in (x..x + width) && ball.y in (y - ball.radius..y) && ball.angle < 180
    }

    private fun isLeftHit(): Boolean {
        return !isDead && ball.x in (x - ball.radius..x) && ball.y in (y..y + height) && (ball.angle < 90 || ball.angle > 270)
    }

    private fun isRightHit(): Boolean {
        return !isDead && ball.x in (x + width..x + width + ball.radius) && ball.y in (y..y + height) && ball.angle in 90.0..270.0
    }
}

data class Scoreboard(val program: Program, val bricks: List<Brick>) {
    fun draw() {
        program.drawer.apply {
            fill = ColorRGBa.BLACK
            text("Score: $score", 10.0, 10.0)
        }
    }

    private val score: Int get() = bricks.count { it.isDead }
}

fun normalizeTo360Range(a: Double) = (a + 360) % 360

fun main() {
    application {
        configure {}
        program {
            val paddleWidth = width / 4.0
            val paddleHeight = height / 64.0

            val paddle =
                Paddle(
                    this,
                    paddleWidth,
                    paddleHeight,
                    20.0,
                    ColorRGBa.BLACK,
                    width / 2.0 - paddleWidth / 2.0,
                    height - height / 7.0
                )
            val ball = Ball(this, paddle, width / 64.0, 3.0, ColorRGBa.BLACK)
            val bricks = mutableListOf<Brick>()
            val scoreboard = Scoreboard(this, bricks)

//            Normal
//            for (i in 0 until 40) {
//                bricks.add(
//                    Brick(
//                        this,
//                        ball,
//                        width / 11.0,
//                        height / 20.0,
//                        ColorRGBa.BLACK,
//                        (i % 10) * width / 10.0 + (width / 220.0),
//                        ((i / 10) * height / 15.0) + 50.0
//                    )
//                )
//            }

//            Blackpink
            for (i in 0 until 200) {
                bricks.add(
                    Brick(
                        this,
                        ball,
                        width / 20.0,
                        height / 15.0,
                        ColorRGBa.BLACK,
                        (i % 20) * width / 20.0,
                        ((i / 20) * height / 15.0) + 30.0
                    )
                )
            }

            for (i in 0 until 200) {
                if (i >= 100) {
                    bricks[i].y += 20
                }
            }

            for (i in bricks.filterIndexed() { index, _ ->
                ((index % 20 == 3 || index % 20 == 7 || index % 20 == 12 || index % 20 == 16) && index < 100)
                        || index == 2
                        || index in 5..6
                        || index == 18
                        || index == 21
                        || index in 25..26
                        || index in 29..30
                        || index in 34..35
                        || index == 39
                        || index == 42
                        || index in 45..46
                        || index in 54..55
                        || index in 58..59
                        || index == 61
                        || index in 65..66
                        || index in 69..70
                        || index in 74..75
                        || index == 79
                        || index == 82
                        || index in 89..90
                        || index == 98

                        || ((index % 20 in 0..1 || index % 20 in 18..19
                        || index % 20 == 5 || index % 20 == 9 || index % 20 == 14) && index >= 100)
                        || index == 104
                        || index in 111..112
                        || index == 116
                        || index == 123
                        || index == 132
                        || index == 137
                        || ((index % 20 == 6 || index % 20 == 8) && index in 120..180 && index >= 100)
                        || index == 144
                        || index in 156..157
                        || index in 163..164
                        || index == 171
                        || index == 177
                        || index in 183..184
                        || index in 191..192
                        || index == 196
            }) {
                i.isDead = true
            }

            keyboard.keyDown.listen {
                if (it.name == "arrow-left") {
                    paddle.moveLeft()
                }
                if (it.name == "arrow-right") {
                    paddle.moveRight()
                }
            }
            val font = loadFont("data/fonts/default.otf", 48.0)
            extend {
                drawer.clear(ColorRGBa.PINK)
                paddle.draw()
                ball.draw()
                for (i in bricks) {
                    i.draw()
                }
                scoreboard.draw()
            }
        }
    }
}