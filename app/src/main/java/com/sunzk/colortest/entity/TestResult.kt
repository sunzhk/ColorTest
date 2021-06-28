package com.sunzk.colortest.entity

class TestResult {
    var id = 0
    var date: String? = null
    var questionH = 0f
    var questionS = 0f
    var questionB = 0f
    var answerH = 0f
    var answerS = 0f
    var answerB = 0f

    constructor() {}
    constructor(
        id: Int,
        date: String?,
        questionH: Float,
        questionS: Float,
        questionB: Float,
        answerH: Float,
        answerS: Float,
        answerB: Float
    ) {
        this.id = id
        this.date = date
        this.questionH = questionH
        this.questionS = questionS
        this.questionB = questionB
        this.answerH = answerH
        this.answerS = answerS
        this.answerB = answerB
    }

    override fun toString(): String {
        val sb = StringBuilder("TestResult{")
        sb.append("id=").append(id)
        sb.append(", date='").append(date).append('\'')
        sb.append(", questionH=").append(questionH)
        sb.append(", questionS=").append(questionS)
        sb.append(", questionB=").append(questionB)
        sb.append(", answerH=").append(answerH)
        sb.append(", answerS=").append(answerS)
        sb.append(", answerB=").append(answerB)
        sb.append('}')
        return sb.toString()
    }
}