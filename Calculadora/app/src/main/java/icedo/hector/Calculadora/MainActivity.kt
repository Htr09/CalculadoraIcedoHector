package icedo.hector.Calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



class MainActivity : AppCompatActivity() {

    var num1:Float=0.0f
    var num2:Float=0.0f
    var operador: String = ""
    lateinit var pantalla: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pantalla = findViewById(R.id.txtPantalla)

        val ac: Button=findViewById(R.id.btnAC)
        val sumar: Button=findViewById(R.id.btnSumar)
        val restar:Button=findViewById(R.id.btnRestar)
        val multiplicar:Button=findViewById(R.id.btnMultiplicar)
        val dividir:Button=findViewById(R.id.btnDividir)
        val punto:Button=findViewById(R.id.btnPunto)
        val total:Button=findViewById(R.id.btnTotal)
        val borrar:Button=findViewById(R.id.btnBorrar)
        val uno:Button=findViewById(R.id.btnUno)
        val dos:Button=findViewById(R.id.btnDos)
        val tres:Button=findViewById(R.id.btnTres)
        val cuatro:Button=findViewById(R.id.btnCuatro)
        val cinco:Button=findViewById(R.id.btnCinco)
        val seis:Button=findViewById(R.id.btnSeis)
        val siete:Button=findViewById(R.id.btnSiete)
        val ocho:Button=findViewById(R.id.btnOcho)
        val nueve:Button=findViewById(R.id.btnNueve)
        val cero:Button=findViewById(R.id.btnCero)

        val botonesNumericos = listOf(uno, dos, tres, cuatro, cinco, seis, siete, ocho, nueve, cero)

        botonesNumericos.forEach { boton ->
            boton.setOnClickListener {
                pantalla.append(boton.text)
            }
        }

        val botonesOperadores = mapOf(
            sumar to "+",
            restar to "-",
            multiplicar to "×",
            dividir to "÷"
        )


        botonesOperadores.forEach { (boton, operador) ->
            boton.setOnClickListener {
                if (pantalla.text.isNotEmpty() && !pantalla.text.endsWith(operador)) {
                    pantalla.append(operador)
                }
            }
        }

        punto.setOnClickListener {
            val texto = pantalla.text.toString()
            if (texto.isNotEmpty() && texto.last().isDigit() && !texto.contains(".")) {
                pantalla.append(".")
            }
        }

        total.setOnClickListener { calcularResultado() }

        ac.setOnClickListener {
            pantalla.text = ""
        }

        borrar.setOnClickListener {
            if (pantalla.text.isNotEmpty()) {
                pantalla.text = pantalla.text.substring(0, pantalla.text.length - 1)
            }
        }
    }

    private fun calcularResultado() {
        val expresion = pantalla.text.toString()
        if (expresion.isNotEmpty()) {
            try {
                val resultado = evaluarExpresion(expresion)
                pantalla.text = if (resultado % 1 == 0.0) resultado.toInt().toString() else resultado.toString()
            } catch (e: Exception) {
                pantalla.text = "Error"
            }
        }
    }

    private fun evaluarExpresion(expresion: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expresion.length) expresion[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expresion.length) throw RuntimeException("Carácter inesperado: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('×'.code) -> x *= parseFactor()
                        eat('÷'.code) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = expresion.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Carácter inesperado: " + ch.toChar())
                }

                return x
            }
        }.parse()
    }
}