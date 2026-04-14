package com.example.calculadora_prototype
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // ── Variáveis de estado da calculadora ──────────────────────────────────
    var numeroAtual: String = ""      // dígitos que o usuário está digitando
    var primeroNumero: Double = 0.0   // primeiro operando
    var operacao: String = ""         // operação escolhida (+, -, ×, ÷)
    var novaEntrada: Boolean = false  // sinaliza que o próximo dígito inicia novo número
    var temPonto: Boolean = false     // controla ponto decimal no número atual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ── Conectando XML ao Kotlin via findViewById ────────────────────────
        val tvDisplay    = findViewById<TextView>(R.id.tvDisplay)
        val tvExpressao  = findViewById<TextView>(R.id.tvExpressao)

        val btn0         = findViewById<Button>(R.id.btn0)
        val btn1         = findViewById<Button>(R.id.btn1)
        val btn2         = findViewById<Button>(R.id.btn2)
        val btn3         = findViewById<Button>(R.id.btn3)
        val btn4         = findViewById<Button>(R.id.btn4)
        val btn5         = findViewById<Button>(R.id.btn5)
        val btn6         = findViewById<Button>(R.id.btn6)
        val btn7         = findViewById<Button>(R.id.btn7)
        val btn8         = findViewById<Button>(R.id.btn8)
        val btn9         = findViewById<Button>(R.id.btn9)

        val btnSomar      = findViewById<Button>(R.id.btnSomar)
        val btnSubtrair   = findViewById<Button>(R.id.btnSubtrair)
        val btnMultiplicar= findViewById<Button>(R.id.btnMultiplicar)
        val btnDividir    = findViewById<Button>(R.id.btnDividir)
        val btnIgual      = findViewById<Button>(R.id.btnIgual)
        val btnLimpar     = findViewById<Button>(R.id.btnLimpar)
        val btnInverter   = findViewById<Button>(R.id.btnInverter)
        val btnPorcento   = findViewById<Button>(R.id.btnPorcento)
        val btnPonto      = findViewById<Button>(R.id.btnPonto)

        // ── Função local: digitar um número ─────────────────────────────────
        // Usa estrutura de repetição (while) para limitar o display a 9 dígitos
        fun digitarNumero(digito: String) {
            if (novaEntrada) {
                numeroAtual = ""
                novaEntrada = false
                temPonto = false
            }

            // Estrutura de repetição: garante que o display não ultrapasse 9 dígitos
            var tamanhoAtual = 0
            var i = 0
            while (i < numeroAtual.length) {
                if (numeroAtual[i] != '.') {
                    tamanhoAtual++
                }
                i++
            }

            if (tamanhoAtual >= 9) return

            numeroAtual += digito
            tvDisplay.text = numeroAtual
        }

        // ── Função local: selecionar operação ───────────────────────────────
        fun selecionarOperacao(op: String) {
            if (numeroAtual.isBlank()) return

            primeroNumero = numeroAtual.toDouble()
            operacao = op
            novaEntrada = true

            // Exibe a expressão parcial no display menor (ex: "42 +")
            val simbolo = when (op) {
                "+" -> "+"
                "-" -> "−"
                "×" -> "×"
                "÷" -> "÷"
                else -> op
            }
            tvExpressao.text = "${formatarResultado(primeroNumero)} $simbolo"
        }

        // ── Ação do botão IGUAL ──────────────────────────────────────────────
        btnIgual.setOnClickListener {
            if (operacao.isBlank() || numeroAtual.isBlank()) return@setOnClickListener

            val segundoNumero = numeroAtual.toDouble()
            var resultado = 0.0

            // Estrutura condicional: decide qual operação executar
            if (operacao == "+") {
                resultado = primeroNumero + segundoNumero
            } else if (operacao == "-") {
                resultado = primeroNumero - segundoNumero
            } else if (operacao == "×") {
                resultado = primeroNumero * segundoNumero
            } else if (operacao == "÷") {
                // Tratamento obrigatório: divisão por zero
                if (segundoNumero == 0.0) {
                    tvDisplay.text = "Erro"
                    tvExpressao.text = "Divisão por zero"
                    numeroAtual = ""
                    operacao = ""
                    novaEntrada = true
                    return@setOnClickListener
                }
                resultado = primeroNumero / segundoNumero
            }

            // Exibe a expressão completa acima do resultado
            val simbolo = when (operacao) {
                "+" -> "+"
                "-" -> "−"
                "×" -> "×"
                "÷" -> "÷"
                else -> operacao
            }
            tvExpressao.text = "${formatarResultado(primeroNumero)} $simbolo ${formatarResultado(segundoNumero)} ="

            val resultadoFormatado = formatarResultado(resultado)
            tvDisplay.text = resultadoFormatado
            numeroAtual = resultadoFormatado
            operacao = ""
            novaEntrada = true
        }

        // ── Ação do botão LIMPAR (AC) ────────────────────────────────────────
        btnLimpar.setOnClickListener {
            numeroAtual = ""
            primeroNumero = 0.0
            operacao = ""
            novaEntrada = false
            temPonto = false
            tvDisplay.text = "0"
            tvExpressao.text = ""
        }

        // ── Ação do botão +/- (inverter sinal) ──────────────────────────────
        btnInverter.setOnClickListener {
            if (numeroAtual.isBlank() || numeroAtual == "0") return@setOnClickListener
            val valor = numeroAtual.toDouble() * -1
            numeroAtual = formatarResultado(valor)
            tvDisplay.text = numeroAtual
        }

        // ── Ação do botão % (porcentagem) ────────────────────────────────────
        btnPorcento.setOnClickListener {
            if (numeroAtual.isBlank()) return@setOnClickListener
            val valor = numeroAtual.toDouble() / 100
            numeroAtual = formatarResultado(valor)
            tvDisplay.text = numeroAtual
        }

        // ── Ação do botão PONTO DECIMAL ──────────────────────────────────────
        btnPonto.setOnClickListener {
            if (novaEntrada) {
                numeroAtual = "0"
                novaEntrada = false
            }
            if (!temPonto) {
                if (numeroAtual.isBlank()) numeroAtual = "0"
                numeroAtual += "."
                temPonto = true
                tvDisplay.text = numeroAtual
            }
        }

        // ── Ações dos botões numéricos ───────────────────────────────────────
        btn0.setOnClickListener { digitarNumero("0") }
        btn1.setOnClickListener { digitarNumero("1") }
        btn2.setOnClickListener { digitarNumero("2") }
        btn3.setOnClickListener { digitarNumero("3") }
        btn4.setOnClickListener { digitarNumero("4") }
        btn5.setOnClickListener { digitarNumero("5") }
        btn6.setOnClickListener { digitarNumero("6") }
        btn7.setOnClickListener { digitarNumero("7") }
        btn8.setOnClickListener { digitarNumero("8") }
        btn9.setOnClickListener { digitarNumero("9") }

        // ── Ações dos botões de operação ─────────────────────────────────────
        btnSomar.setOnClickListener       { selecionarOperacao("+") }
        btnSubtrair.setOnClickListener    { selecionarOperacao("-") }
        btnMultiplicar.setOnClickListener { selecionarOperacao("×") }
        btnDividir.setOnClickListener     { selecionarOperacao("÷") }
    }
    fun formatarResultado(valor: Double): String {
        // Estrutura condicional: se for inteiro, remove a casa decimal
        return if (valor == valor.toLong().toDouble()) {
            valor.toLong().toString()
        } else {
            valor.toString()
        }
    }
}