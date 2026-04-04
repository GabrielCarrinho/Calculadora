package com.example.calculadora_prototype

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // ── Tela (Display) ───────────────────────────────────────────────────────
    private lateinit var tvDisplay: TextView

    // ── Variáveis que guardam o estado da calculadora ────────────────────────
    private var currentInput       = "0"    // número atual mostrado na tela
    private var firstOperand       = 0.0    // primeiro número da operação
    private var pendingOp          = ""     // operação escolhida (+, -, ×, ÷)
    private var shouldResetDisplay = false  // flag: limpar tela na próxima entrada

    // ════════════════════════════════════════════════════════════════════════
    //  INÍCIO DO APP
    // ════════════════════════════════════════════════════════════════════════
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        // ── ESTRUTURA DE REPETIÇÃO: registra os botões de dígitos 0 a 9 ─────
        // O "for" percorre todos os pares (botão, dígito) um por um
        val digitButtons = arrayOf(
            Pair(R.id.btn0, "0"),
            Pair(R.id.btn1, "1"),
            Pair(R.id.btn2, "2"),
            Pair(R.id.btn3, "3"),
            Pair(R.id.btn4, "4"),
            Pair(R.id.btn5, "5"),
            Pair(R.id.btn6, "6"),
            Pair(R.id.btn7, "7"),
            Pair(R.id.btn8, "8"),
            Pair(R.id.btn9, "9")
        )

        for (pair in digitButtons) {
            val btnId = pair.first   // ID do botão
            val digit = pair.second  // dígito correspondente
            findViewById<Button>(btnId).setOnClickListener { onDigit(digit) }
        }

        // ── Conecta os botões de operação às suas funções ────────────────────
        findViewById<Button>(R.id.btnAdd).setOnClickListener      { onOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { onOperator("−") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener   { onOperator("÷") }

        // ── Conecta os botões especiais ──────────────────────────────────────
        findViewById<Button>(R.id.btnEquals).setOnClickListener    { onEquals()    }
        findViewById<Button>(R.id.btnClear).setOnClickListener     { onClear()     }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { onPlusMinus() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener   { onPercent()   }
        findViewById<Button>(R.id.btnDot).setOnClickListener       { onDot()       }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ENTRADA DE NÚMEROS (digitação)
    // ════════════════════════════════════════════════════════════════════════
    private fun onDigit(digit: String) {

        // Se a tela deve ser resetada, começa um número novo
        if (shouldResetDisplay) {
            currentInput = digit
            shouldResetDisplay = false
        } else {
            // ESTRUTURA CONDICIONAL: controla o que aparece no display
            if (currentInput == "0") {
                currentInput = digit              // substitui o zero inicial
            } else if (currentInput.length < 12) {
                currentInput = currentInput + digit  // adiciona dígito ao número
            }
            // else: display cheio — ignora o dígito
        }

        updateDisplay()
    }

    // ── Ponto decimal ────────────────────────────────────────────────────────
    private fun onDot() {
        if (shouldResetDisplay) {
            currentInput = "0."
            shouldResetDisplay = false
            updateDisplay()
            return
        }
        // Só adiciona o ponto se ainda não tiver um
        if (!currentInput.contains(".")) {
            currentInput = currentInput + "."
            updateDisplay()
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SELEÇÃO DA OPERAÇÃO (+, -, ×, ÷)
    // ════════════════════════════════════════════════════════════════════════
    private fun onOperator(op: String) {

        // Se já havia uma operação pendente, calcula antes de registrar a nova
        if (pendingOp != "" && !shouldResetDisplay) {
            calculate()
        }

        firstOperand       = currentInput.toDoubleOrNull() ?: 0.0  // salva o primeiro número
        pendingOp          = op    // salva a operação escolhida
        shouldResetDisplay = true  // próximo dígito começa número novo
        highlightOperator(op)
    }

    // ════════════════════════════════════════════════════════════════════════
    //  BOTÃO IGUAL — executa o cálculo
    // ════════════════════════════════════════════════════════════════════════
    private fun onEquals() {
        if (pendingOp == "") return  // nada a calcular
        calculate()
        pendingOp = ""
        clearOperatorHighlights()
    }

    // ════════════════════════════════════════════════════════════════════════
    //  FUNÇÃO LIMPAR — obrigatória pelo projeto
    //  Zera tudo e permite iniciar um novo cálculo
    // ════════════════════════════════════════════════════════════════════════
    private fun onClear() {
        currentInput       = "0"   // limpa a tela
        firstOperand       = 0.0   // zera o primeiro número
        pendingOp          = ""    // remove a operação pendente
        shouldResetDisplay = false
        clearOperatorHighlights()
        updateDisplay()
    }

    // ── Inverte o sinal do número atual (+/-) ─────────────────────────────
    private fun onPlusMinus() {
        val value = currentInput.toDoubleOrNull() ?: return
        currentInput = formatResult(-value)
        updateDisplay()
    }

    // ── Calcula a porcentagem do número atual ─────────────────────────────
    private fun onPercent() {
        val value = currentInput.toDoubleOrNull() ?: return
        currentInput = formatResult(value / 100.0)
        updateDisplay()
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CÁLCULO — onde as operações matemáticas acontecem
    //  Usa estrutura condicional if/else para cada operação
    // ════════════════════════════════════════════════════════════════════════
    private fun calculate() {
        val secondOperand = currentInput.toDoubleOrNull() ?: return

        var result = 0.0

        // ── SOMA ─────────────────────────────────────────────────────────────
        if (pendingOp == "+") {
            result = firstOperand + secondOperand

            // ── SUBTRAÇÃO ────────────────────────────────────────────────────────
        } else if (pendingOp == "−") {
            result = firstOperand - secondOperand

            // ── MULTIPLICAÇÃO ────────────────────────────────────────────────────
        } else if (pendingOp == "×") {
            result = firstOperand * secondOperand

            // ── DIVISÃO ──────────────────────────────────────────────────────────
        } else if (pendingOp == "÷") {

            // !! TRATAMENTO DE ERRO: divisão por zero !!
            // Se o segundo número for zero, exibe mensagem de erro e para
            if (secondOperand == 0.0) {
                currentInput       = "Erro: div/0"
                shouldResetDisplay = true
                updateDisplay()
                return  // interrompe o cálculo
            }

            result = firstOperand / secondOperand
        }

        currentInput       = formatResult(result)
        firstOperand       = result
        shouldResetDisplay = true
        updateDisplay()
    }

    // ════════════════════════════════════════════════════════════════════════
    //  FORMATAÇÃO DO RESULTADO
    //  Se o resultado for inteiro, exibe sem vírgula (ex: 2.0 → "2")
    //  Se tiver decimal, exibe com até 6 casas (ex: 1.333333)
    // ════════════════════════════════════════════════════════════════════════
    private fun formatResult(value: Double): String {

        // Converte para inteiro e compara — se forem iguais, é número inteiro
        val valueAsInt = value.toInt()

        if (value == valueAsInt.toDouble()) {
            // Exibe sem casas decimais
            return valueAsInt.toString()
        } else {
            // Exibe com casas decimais limitadas a 6
            return "%.6f".format(value).trimEnd('0')
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ATUALIZA O DISPLAY
    // ════════════════════════════════════════════════════════════════════════
    private fun updateDisplay() {
        tvDisplay.text = currentInput

        // Alterna o botão entre "AC" (limpar tudo) e "C" (limpar entrada)
        val clearBtn = findViewById<Button>(R.id.btnClear)
        if (currentInput == "0" && pendingOp == "") {
            clearBtn.text = "AC"
        } else {
            clearBtn.text = "C"
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VISUAL: destaca o botão do operador ativo (fica branco com laranja)
    //  Usa estrutura de repetição WHILE
    // ════════════════════════════════════════════════════════════════════════
    private fun highlightOperator(activeOp: String) {
        val ids = arrayOf(R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide)
        val ops = arrayOf("+", "−", "×", "÷")

        var index = 0
        while (index < ids.size) {
            val btn = findViewById<Button>(ids[index])
            if (ops[index] == activeOp) {
                // Operador selecionado: fundo branco, texto laranja
                btn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(0xFFFFFFFF.toInt())
                btn.setTextColor(0xFFFF9F0A.toInt())
            } else {
                // Demais operadores: volta ao laranja padrão
                btn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(0xFFFF9F0A.toInt())
                btn.setTextColor(0xFFFFFFFF.toInt())
            }
            index++
        }
    }

    // ── Remove destaque de todos os operadores ────────────────────────────
    private fun clearOperatorHighlights() {
        val ids = arrayOf(R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide)

        // ESTRUTURA DE REPETIÇÃO FOR: restaura a cor laranja em todos
        for (id in ids) {
            val btn = findViewById<Button>(id)
            btn.backgroundTintList =
                android.content.res.ColorStateList.valueOf(0xFFFF9F0A.toInt())
            btn.setTextColor(0xFFFFFFFF.toInt())
        }
    }
}